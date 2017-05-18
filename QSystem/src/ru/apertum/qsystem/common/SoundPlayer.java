/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.model.QService;

/**
 * Класс проигрывания звуковых ресурсов и файлов. Создает отдельный поток для каждого проигрыша, но игоает синхронизированно. По этому все ресурсы проиграются
 * друг за другом и это не будет тормозить основной поток. Воспроизведение кучи мелких файлов глючит, накладываются др. на др.
 *
 * @author Evgeniy Egorov
 */
public class SoundPlayer implements Runnable {

    public SoundPlayer(LinkedList<String> resourceList) {
        this.resourceList = resourceList;
    }
    /**
     * Тут храним имя ресурса для загрузки
     */
    private final LinkedList<String> resourceList;

    /**
     * Проиграть звуковой ресурс
     *
     * @param resourceName имя проигрываемого ресурса
     */
    public static void play(String resourceName) {
        final LinkedList<String> resourceList = new LinkedList<>();
        resourceList.add(resourceName);
        play(resourceList);
    }

    /**
     * Проиграть набор звуковых ресурсов
     *
     * @param resourceList список имен проигрываемых ресурсов
     */
    public static void play(LinkedList<String> resourceList) {
        // и запускаем новый вычислительный поток (см. ф-ю run())
        final Thread playThread = new Thread(new SoundPlayer(resourceList));
        //playThread.setDaemon(true);
        playThread.setPriority(Thread.NORM_PRIORITY);
        playThread.start();
    }

    public static void printAudioFormatInfo(AudioFormat audioformat) {
        System.out.println("*****************************************");
        System.out.println("Format: " + audioformat.toString());
        System.out.println("Encoding: " + audioformat.getEncoding());
        System.out.println("SampleRate:" + audioformat.getSampleRate());
        System.out.println("SampleSizeInBits: " + audioformat.getSampleSizeInBits());
        System.out.println("Channels: " + audioformat.getChannels());
        System.out.println("FrameSize: " + audioformat.getFrameSize());
        System.out.println("FrameRate: " + audioformat.getFrameRate());
        System.out.println("BigEndian: " + audioformat.isBigEndian());
        System.out.println("*****************************************\n");
    }

    /**
     * Asks the user to select a file to play.
     *
     * @return
     */
    public File getFileToPlay() {
        File file = null;
        JFrame frame = new JFrame();
        JFileChooser chooser = new JFileChooser(".");
        int returnvalue = chooser.showDialog(frame, "Select File to Play");
        if (returnvalue == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }
        return file;
    }

    @Override
    public void run() {
        doSounds(this, resourceList);
    }
    /**
     * Листенер, срабатываюшщий при начале проигрывания семплов
     */
    private static ActionListener startListener = null;

    public static ActionListener getStartListener() {
        return startListener;
    }

    public static void setStartListener(ActionListener startListener) {
        SoundPlayer.startListener = startListener;
    }
    /**
     * Событие завершения проигрывания семплов
     */
    private static ActionListener finishListener = null;

    public static ActionListener getFinishListener() {
        return finishListener;
    }

    public static void setFinishListener(ActionListener finishListener) {
        SoundPlayer.finishListener = finishListener;
    }

    synchronized private static void doSounds(Object o, LinkedList<String> resourceList) {
        if (startListener != null) {
            startListener.actionPerformed(new ActionEvent(o, 1, "start do sounds"));
        }
        resourceList.stream().forEach((res) -> {
            doSound(o, res);
        });
        if (finishListener != null) {
            finishListener.actionPerformed(new ActionEvent(o, 1, "finish do sounds"));
        }
    }

    synchronized private static void doSound(Object o, String resourceName) {
        QLog.l().logger().debug("Try to play sound \"" + resourceName + "\"");
        AudioInputStream ais = null;
        try {
            ais = AudioSystem.getAudioInputStream(Object.class.getResource(resourceName));
            //get the AudioFormat for the AudioInputStream 
            AudioFormat audioformat = ais.getFormat();
            //printAudioFormatInfo(audioformat);
            //ULAW & ALAW format to PCM format conversion 
            if ((audioformat.getEncoding() == AudioFormat.Encoding.ULAW)
                    || (audioformat.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat newformat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        audioformat.getSampleRate(),
                        audioformat.getSampleSizeInBits() * 2,
                        audioformat.getChannels(),
                        audioformat.getFrameSize() * 2,
                        audioformat.getFrameRate(),
                        true);
                ais = AudioSystem.getAudioInputStream(newformat, ais);
                audioformat = newformat;
                //printAudioFormatInfo(audioformat);
            }
            //checking for a supported output line 
            DataLine.Info datalineinfo = new DataLine.Info(SourceDataLine.class, audioformat);
            if (!AudioSystem.isLineSupported(datalineinfo)) {
                System.out.println("Line matching " + datalineinfo + " is not supported.");
            } else {
                byte[] sounddata;
                try (SourceDataLine sourcedataline = (SourceDataLine) AudioSystem.getLine(datalineinfo)) {
                    sourcedataline.open(audioformat);
                    sourcedataline.start();
                    int framesizeinbytes = audioformat.getFrameSize();
                    int bufferlengthinframes = sourcedataline.getBufferSize() / 8;
                    int bufferlengthinbytes = bufferlengthinframes * framesizeinbytes;
                    sounddata = new byte[bufferlengthinbytes];
                    int numberofbytesread;
                    while ((numberofbytesread = ais.read(sounddata)) != -1) {
                        sourcedataline.write(sounddata, 0, numberofbytesread);
                    }
                    int frPos = -1;
                    while (frPos != sourcedataline.getFramePosition()) {
                        frPos = sourcedataline.getFramePosition();
                        Thread.sleep(100);
                    }
                }
            }

            //printAudioFormatInfo(audioformat);
        } catch (InterruptedException ex) {
            QLog.l().logger().error("InterruptedException: " + ex);
        } catch (LineUnavailableException lue) {
            QLog.l().logger().error("LineUnavailableException: " + lue.toString());
        } catch (UnsupportedAudioFileException uafe) {
            QLog.l().logger().error("UnsupportedAudioFileException: " + uafe.toString());
        } catch (IOException ioe) {
            QLog.l().logger().error("IOException: " + ioe.toString());
        } finally {
            try {
                if (ais != null) {
                    ais.close();
                }
            } catch (IOException ex) {
                QLog.l().logger().error("IOException при освобождении входного потока медиаресурса: " + ex);
            }
        }
    }

    /**
     * Разбить фразу на звуки и сформировать набор файлов для воспроизведения.
     *
     * @param path путь, где лежать звуковые ресурсы, это могут быть файлы на диске или ресурсы в jar
     * @param phrase фраза для разбора
     * @return список файлов для воспроизведения фразы
     */
    private static LinkedList<String> toSound(String path, String phrase) {
        final LinkedList<String> res = new LinkedList<>();
        for (int i = 0; i < phrase.length(); i++) {

            String elem = phrase.substring(i, i + 1);
            if (isNum(phrase.charAt(i))) {
                String ss = elem;
                if (i != 0 && isNum(phrase.charAt(i - 1))) {
                    ss = "_" + ss;
                }
                int n = i + 1;
                boolean suff = false;
                while (n < phrase.length() && isNum(phrase.charAt(n))) {
                    ss = ss + "0";
                    if ('0' != phrase.charAt(n)) {
                        suff = true;
                    }
                    n++;
                }
                if (suff) {
                    ss = ss + "_";
                } else {
                    i = n - 1;
                }
                elem = ss;
                if (elem.contains("_0") && elem.contains("0_")) {
                    continue;
                }
                if (isZero(elem)) {
                    elem = "0";
                }
                if (elem.endsWith("10_")) {
                    char[] ch = new char[1];
                    ch[0] = phrase.charAt(i + 1);
                    elem = elem.replaceFirst("10_", "1" + new String(ch));
                    i++;
                }
            }

            final String file = path + elem.toLowerCase() + ".wav";
            //System.out.println(nom.substring(i, i + 1) + " - " + file);
            res.add(file);

        }
        return res;
    }

    /**
     * Разбить фразу на звуки и сформировать набор файлов для воспроизведения. Упрощенный вариант.
     *
     * @param path путь, где лежать звуковые ресурсы, это могут быть файлы на диске или ресурсы в jar
     * @param phrase фраза для разбора
     * @return список файлов для воспроизведения фразы
     */
    public static LinkedList<String> toSoundSimple(String path, String phrase) {
        final LinkedList<String> res = new LinkedList<>();
        for (int i = 0; i < phrase.length(); i++) {

            String elem = phrase.substring(i, i + 1);
            if (isNum(phrase.charAt(i))) {

                if (!isZero(elem)) {
                    int j = i + 1;
                    while (j < phrase.length() && isNum(phrase.charAt(j))) {
                        elem = elem + "0";
                        j++;
                    }
                }
                if ("10".equals(elem)) {
                    elem = phrase.substring(i, i + 2);
                    i++;
                }

            }
            if (!isZero(elem)) {
                final String fileName = reRus(elem.toLowerCase());
                if (fileName != null) {
                    final String file = path + fileName.toLowerCase() + ".wav";
                    //System.out.println(elem + " - " + file);
                    res.add(file);
                }
            }

        }
        return res;
    }

    /**
     * Разбить фразу на звуки и сформировать набор файлов для воспроизведения. Упрощенный вариант с поиском существующих семплов.
     *
     * @param path путь, где лежать звуковые ресурсы, это могут быть файлы на диске или ресурсы в jar
     * @param phrase фраза для разбора
     * @return список файлов для воспроизведения фразы
     */
    public static LinkedList<String> toSoundSimple2(String path, String phrase) {
        final LinkedList<String> res = new LinkedList<>();

        // Разделим на буквы и цыфры
        final Matcher m = Pattern.compile("\\d").matcher(phrase);

        // Добавим буквы если они есть во фразе и в ресурсах
        if (m.find()) {
            for (int i = 0; i < m.start(); i++) {
                String elem = phrase.substring(i, i + 1);
                final String fileName = reRus(elem.toLowerCase());
                final String file = path + fileName.toLowerCase() + ".wav";
                //System.out.println(elem);
                // сэмплы букав должны быть в ресурсах
                if (file.getClass().getResourceAsStream(file) != null) {
                    //System.out.println(elem + " + " + file);
                    res.add(file);
                }
            }
            phrase = phrase.substring(m.start());
        }

        // ну теперь расщепим цифры, найдеи под них ресурсы и сложим в список для воспроизведения
        String lastAdded = "";
        for (int i = 0; i < phrase.length(); i++) {

            final String elem = phrase.substring(i).toLowerCase();
            //System.out.println("-" + elem + "_" + isNum(elem));
            String file = path + elem + ".wav";
            if (file.getClass().getResourceAsStream(file) != null) {
                // тут иногда перед произнесением очередных цыфр надо добавить типа препозицию, типа "тридцать и пять"
                if (lastAdded.length() == 2 && elem.length() == 1) {
                    final String fileAnd = path + "and.wav";
                    if (fileAnd.getClass().getResourceAsStream(fileAnd) != null) {
                        //System.out.println("and.wav" + " + " + fileAnd);
                        res.add(fileAnd);
                    }
                }
                //System.out.println(elem + " + " + file);
                res.add(file);
                break;
            } else {
                String elemZer = (elem.substring(0, 1) + "00000000000000000000000000").substring(0, elem.length());
                file = path + elemZer + ".wav";
                if (file.getClass().getResourceAsStream(file) != null) {
                    //System.out.println(elemZer + " + " + file);
                    lastAdded = elemZer;
                    res.add(file);
                }
            }

        }
        return res;
    }

    public static void main(String[] args) {

        //Pattern pattern = Pattern.compile("[0-9]+\\|[^\\^]+$");
        Pattern pattern = Pattern.compile("[0-9]+\\|[^\\^]+$");
        System.out.println(pattern.matcher("123|qwe1|||||23/nasd#$#$%#$%%#$%123").matches());
        
        
        /*
        Pattern pattern = Pattern.compile("(\\d+\\^)(.+?\\^)(\\d+\\^){4}\\d+"); //42301810232338017977^phiz-gate-codB^770151^1101^77^3^151
        System.out.println(pattern.matcher("42301810232338017977^phiz-gate-codB^770151^1101^77^3^151").matches());
        System.out.println(pattern.matcher("42301840289153231510^systemX^770151^1425^77^3^151").matches());
        System.out.println(pattern.matcher("40820978005570404150^phiz-gate-codB^770151^1103^77^3^151").matches());
        System.out.println(pattern.matcher("42301978861227387568^systemX^770017^2462^77^294^17").matches());
        System.out.println(pattern.matcher("42301978861227387568^123123123^770017^2462^77^294^17").matches());
        */

        if (true) {
            return;
        }

        Uses.loadPlugins("./plugins/");
        toSoundSimple2("/ru/apertum/qsystem/server/sound/", "A354");
        //new Thread(new Bulb("F irst")).start();
        //new Thread(new Bulb("S econds")).start();

    }

    // private static boolean isRus(String elem) {
    //     return "йцукенгшщзхъфывапролджэячсмитьбю".indexOf(elem.toLowerCase()) != -1;
    // }
    private static HashMap<String, String> latters = null;
    private static String preffix = "";

    private static String reRus(String elem) {
        if (latters == null) {
            latters = new HashMap<>();
            try {
                final InputStream ris = elem.getClass().getResourceAsStream("/ru/apertum/qsystem/server/sound/latters.properties");
                if (ris == null) {
                    return null;
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(ris, Charset.forName("utf8")))) {
                    String line;
                    boolean f = true;
                    while ((line = br.readLine()) != null) {
                        line = (f ? line.substring(1) : line);
                        f = false;
                        //System.out.println(line);
                        String[] ss = line.split("=");
                        if (ss[0].startsWith("pref")) {
                            preffix = ss[1];
                        } else {
                            latters.put(ss[0], ss[1]);
                        }

                    }
                }
            } catch (IOException ex) {
                QLog.l().logger().error("Не найден зкуковой ресурс или что-то в этом роде. " + ex);
                return null;
            }
        }

        //final String is__ru = "й ц у к е н г ш щ з х ъ ф ы в а п р о л д ж э я ч с м и т ь б ю ё ";
        //final String not_ru = "iic u k e n g shghz x zzf yyv a p r o l d jzeeiachs m i t ccb iuio";
        //int pos = is__ru.indexOf(elem.toLowerCase());
        final String ns = latters.get(elem) == null ? elem : preffix + latters.get(elem); //not_ru.substring(pos, pos + 2).trim().toLowerCase();
        return ns;
    }

    private static boolean isNum(char elem) {
        return '1' == elem || '2' == elem || '3' == elem || '4' == elem || '5' == elem || '6' == elem || '7' == elem || '8' == elem || '9' == elem || '0' == elem;
    }

    private static boolean isNum(String elem) {
        return elem.matches("\\d+");
    }

    private static boolean isZero(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!('0' == str.charAt(i) || '_' == str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проговорить вызов клиента голосом
     *
     * @param clientNumber номер вызываемого клиента
     * @param pointNumber номер кабинета, куда вызвали
     * @param isFirst
     */
    public static void inviteClient(String clientNumber, String pointNumber, boolean isFirst) {
        inviteClient(clientNumber, pointNumber, isFirst, null, null, null);
    }

    /**
     * Проговорить вызов клиента голосом
     *
     * @param clientNumber номер вызываемого клиента
     * @param pointNumber номер кабинета, куда вызвали
     * @param isFirst
     * @param inviteType Для сервера всегда null. Для всего остального смотреть в настройках сервера и передавать
     * @param voiceType Для сервера всегда null. Для всего остального смотреть в настройках сервера и передавать
     * @param pointType Для сервера всегда null. Для всего остального смотреть в настройках сервера и передавать 0 - кабинет, 1 - окно, 2 - стойка
     */
    public static void inviteClient(String clientNumber, String pointNumber, boolean isFirst, Integer inviteType, Integer voiceType, Integer pointType) {
        final int ivt = inviteType == null ? ServerProps.getInstance().getProps().getSound() : inviteType;
        final int voc = voiceType == null ? ServerProps.getInstance().getProps().getVoice() : voiceType;
        final int pnt = pointType == null ? ServerProps.getInstance().getProps().getPoint() : pointType;
        if (ivt == 0) {
            return;
        }
        final LinkedList<String> res = new LinkedList<>();
        // путь к звуковым файлам
        final String path = "/ru/apertum/qsystem/server/sound/";
        if ((isFirst && ivt == 2) || ivt == 1 || ivt == 3) {
            res.add(path + "ding.wav");
        }

        if (ivt == 3 || (!isFirst && ivt == 2)) {
            res.add(path + "client.wav");

            res.addAll(toSoundSimple2(path, clientNumber));

            switch (pnt) {
                case 0:
                    res.add(path + "tocabinet.wav");
                    break;
                case 1:
                    res.add(path + "towindow.wav");
                    break;
                case 2:
                    res.add(path + "tostoika.wav");
                    break;
                case 3:
                    res.add(path + "totable.wav");
                default:
                    res.add(path + "towindow.wav");
            }

            res.addAll(toSoundSimple2(path, pointNumber));
        }
        SoundPlayer.play(res);
    }

    /**
     * Проговорить вызов клиента голосом
     *
     * @param service
     * @param clientNumber номер вызываемого клиента
     * @param pointNumber номер кабинета, куда вызвали
     * @param isFirst
     */
    public static void inviteClient(QService service, String clientNumber, String pointNumber, boolean isFirst) {
        // Для начала найдем шаблон
        QService tempServ = service;
        while ((tempServ.getSoundTemplate() == null || tempServ.getSoundTemplate().startsWith("0")) && tempServ.getParent() != null) {
            tempServ = tempServ.getParent();
        }
        if (tempServ.getSoundTemplate() == null || tempServ.getSoundTemplate().startsWith("0")) {
            return;
        }

        int gong = 1;
        if (tempServ.getSoundTemplate().length() > 1) {
            switch (tempServ.getSoundTemplate().substring(1, 2)) {
                case "1":
                    gong = 1;
                    break;
                case "2":
                    gong = 2;
                    break;
                case "3":
                    gong = 3;
                    break;
                default:
                    gong = 1;
            }
        }
        boolean client = false;
        if (tempServ.getSoundTemplate().length() > 2) {
            client = "1".equals(tempServ.getSoundTemplate().substring(2, 3));
        }
        boolean cl_num = false;
        if (tempServ.getSoundTemplate().length() > 3) {
            cl_num = "1".equals(tempServ.getSoundTemplate().substring(3, 4));
        }
        int go_to = 5;
        if (tempServ.getSoundTemplate().length() > 4) {
            switch (tempServ.getSoundTemplate().substring(4, 5)) {
                case "1":
                    go_to = 1;
                    break;
                case "2":
                    go_to = 2;
                    break;
                case "3":
                    go_to = 3;
                    break;
                case "4":
                    go_to = 4;
                    break;
                case "5":
                    go_to = 5;
                    break;
                default:
                    go_to = 5;
            }
        }
        boolean go_num = false;
        if (tempServ.getSoundTemplate().length() > 5) {
            go_num = tempServ.getSoundTemplate().endsWith("1");
        }

        final LinkedList<String> res = new LinkedList<>();
        // путь к звуковым файлам
        final String path = "/ru/apertum/qsystem/server/sound/";
        if ((isFirst && gong == 3) || gong == 2) {
            res.add(path + "ding.wav");
        }

        if (!(isFirst && gong == 3)) {
            if (client) {
                res.add(path + "client.wav");
            }
            if (cl_num) {
                res.addAll(toSoundSimple2(path, clientNumber));
            }
            switch (go_to) {
                case 1:
                    res.add(path + "tocabinet.wav");
                    break;
                case 2:
                    res.add(path + "towindow.wav");
                    break;
                case 3:
                    res.add(path + "tostoika.wav");
                    break;
                case 4:
                    res.add(path + "totable.wav");
                    break;

            }
            if (go_num) {
                res.addAll(toSoundSimple2(path, pointNumber));
            }
        }
        SoundPlayer.play(res);
    }
}
