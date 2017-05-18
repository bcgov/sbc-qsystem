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
package ru.apertum.qsystem.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import ru.apertum.qsystem.client.forms.FBoardConfig;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 * Старт редактора конфигурации клиентского табло
 *
 * @author Evgeniy Egorov
 */
public class TabloRedactor {

    private static File file;
    private static Element root;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        QLog.initial(args, 17);
        Locale.setDefault(Locales.getInstance().getLangCurrent());

        // проверить есть ли файл /config/clientboard.xml и если есть отправить его на редактирование
        if (args.length < 2) {
            throw new ServerException("No param '-tcfg' file for context.");
        }
        file = new File(QConfig.cfg().getTabloBoardCfgFile());
        if (!file.exists()) {
            throw new ServerException("File context \"" + QConfig.cfg().getTabloBoardCfgFile() + "\" not exist.");
        }
        QLog.l().logger().info("Load file: " + file.getAbsolutePath());
        final SAXReader reader = new SAXReader(false);
        try {
            root = reader.read(file).getRootElement();
        } catch (DocumentException ex) {
            throw new ServerException("Wrong xml file. " + ex.getMessage());
        }
        /*
         java.awt.EventQueue.invokeLater(new Runnable() {

         @Override
         public void run() {*/
        final FBoardConfig bc = new FBoardConfigImpl(null, false);
        bc.setTitle(bc.getTitle() + " " + file.getAbsolutePath());
        bc.setParams(root);
        Uses.setLocation(bc);
        bc.setVisible(true);
        /*         }
         });*/
    }

    static class FBoardConfigImpl extends FBoardConfig {

        public FBoardConfigImpl(JFrame parent, boolean modal) {
            super(parent, modal);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        @Override
        public void saveResult() throws IOException {
            saveForm();
            saveToFile();
            JOptionPane.showMessageDialog(this, "Сохранение завершено успешно.", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
        }

        @Override
        public void hideRedactor() {
            super.hideRedactor();
            System.exit(0);
        }
    }

    /**
     *
     */
    public static void saveToFile() {
        final long start = System.currentTimeMillis();
        QLog.l().logger().info("abc");
        // в файл
        final FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            throw new ServerException("Не возможно создать файл настроек табло. " + ex.getMessage());
        }
        try {
            fos.write(root.asXML().getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            throw new ServerException("Не возможно сохранить изменения в поток." + ex.getMessage());
        }
        QLog.l().logger().info("Состояние сохранено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.");
    }
}
