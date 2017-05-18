/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.htmlboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import ru.apertum.qsystem.common.QLog;

/**
 *
 * @author Evgeniy Egorov
 */
public class HtmlBoardProps {

    final private HashMap<String, String> addrs = new HashMap<>();
    final private HashMap<String, String> ids = new HashMap<>();

    public HashMap<String, String> getAddrs() {
        return addrs;
    }
    final static private File ADDR_FILE = new File("config/html_main_board/links.adr");
    final static private String MAIN_PROPS_FILE = "config/html_main_board/mainboard.properties";
    final Properties settings = new Properties();

    ;

    private HtmlBoardProps() {
        try (FileInputStream fis = new FileInputStream(ADDR_FILE); Scanner s = new Scanner(fis)) {
            while (s.hasNextLine()) {
                final String line = s.nextLine().trim();
                if (!line.startsWith("#")) {
                    final String[] ss = line.split("=");
                    addrs.put(ss[0], ss[1]);
                    ids.put(ss[1], ss[0]);
                    System.out.println(ss[0] + " " + ss[1]);
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }

        File f = new File(MAIN_PROPS_FILE);
        if (f.exists()) {
            final FileInputStream inStream;
            try {
                inStream = new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            try {
                settings.load(new InputStreamReader(inStream, "UTF-8"));
            } catch (IOException ex) {
                throw new RuntimeException("Cant read version. " + ex);
            }

            topSize = Integer.parseInt(settings.getProperty("top.size", "0"));
            topUrl = settings.getProperty("top.url");
            leftSize = Integer.parseInt(settings.getProperty("left.size", "0"));
            leftUrl = settings.getProperty("left.url");
            rightSize = Integer.parseInt(settings.getProperty("right.size", "0"));
            rightUrl = settings.getProperty("right.url");
            bottomSize = Integer.parseInt(settings.getProperty("bottom.size", "0"));
            bottomUrl = settings.getProperty("bottom.url");
            needReload = "1".equals(settings.getProperty("need_reload", "1")) || "true".equals(settings.getProperty("need_reload", "true"));
        } else {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                QLog.l().logger().error("Properties file wasnt crated.", ex);
            }
        }
    }

    public void saveProps() {
        settings.setProperty("top.size", "" + topSize);
        settings.setProperty("top.url", topUrl);
        settings.setProperty("left.size", "" + leftSize);
        settings.setProperty("left.url", leftUrl);
        settings.setProperty("right.size", "" + rightSize);
        settings.setProperty("right.url", rightUrl);
        settings.setProperty("bottom.size", "" + bottomSize);
        settings.setProperty("bottom.url", bottomUrl);
        settings.setProperty("need_reload", needReload ? "1" : "0");
        try {
            settings.store(new FileOutputStream(MAIN_PROPS_FILE), "в пикселах / in pixel");
        } catch (IOException ex) {
            QLog.l().logger().error("Properties warent saved.", ex);
        }
    }

    public static HtmlBoardProps getInstance() {
        return AddrPropHolder.INSTANCE;
    }

    private static class AddrPropHolder {

        private static final HtmlBoardProps INSTANCE = new HtmlBoardProps();
    }

    public String getId(String adr) {
        return ids.get(adr);
    }

    public String getAddr(String id) {
        return addrs.get(id);
    }

    public static void main(String[] ss) {
        String s = "[123|names]asd [123|name] asd [321|ext] asd asd [222|discription] asd [555|blink] asd[2222|blink]";
        System.out.println(s);
        System.out.println("");
        s = s.replaceAll("\\[\\d+\\|(name|discription|point|blink|ext)\\]", "#");
        System.out.println(s);
        /*
         ArrayList<String> allMatches = new ArrayList<>();
         Matcher m = Pattern.compile("\\[\\d+\\|(name|discription|point|blink|ext)\\]").matcher(s);
         while (m.find()) {
         allMatches.add(m.group());
         }
         int i = 0;
         for (String string : allMatches) {
         System.out.println(string);
         s = s.replaceAll(string.replace("[", "\\[").replace("]", "\\]").replace("|", "\\|"), "b_" + i++);
         }
         System.out.println(s);
         /*
         allMatches = new ArrayList<>();
         m = Pattern.compile("\\[\\d+\\|\\d+\\]").matcher(s);
         while (m.find()) {
         allMatches.add(m.group());
         }
         for (String string : allMatches) {
         System.out.println(string);
         s = s.replaceAll(string.replace("[", "\\[").replace("]", "\\]").replace("|", "\\|"), "A123");
         }
         System.out.println(s);
         /*
         System.out.println("addrs:");
         for (Long l : getInstance().addrs.keySet()) {
         System.out.println(l + "=" + getInstance().getAddr(l).getName());

         }
         */
    }

    int topSize = 0;
    String topUrl = "";
    int leftSize = 0;
    String leftUrl = "";
    int rightSize = 0;
    String rightUrl = "";
    int bottomSize = 0;
    String bottomUrl = "";
    boolean needReload = true;

    public boolean isNeedReload() {
        return needReload;
    }

    public void setNeedReload(boolean needReload) {
        this.needReload = needReload;
    }

    public int getTopSize() {
        return topSize;
    }

    public String getTopUrl() {
        return topUrl;
    }

    public int getLeftSize() {
        return leftSize;
    }

    public String getLeftUrl() {
        return leftUrl;
    }

    public int getRightSize() {
        return rightSize;
    }

    public int getBottomSize() {
        return bottomSize;
    }

    public String getBottomUrl() {
        return bottomUrl;
    }

    public String getRightUrl() {
        return rightUrl;
    }

}
