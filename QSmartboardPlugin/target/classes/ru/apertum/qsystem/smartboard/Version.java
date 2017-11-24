/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.smartboard;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Evgeniy Egorov
 */
public class Version {

    public static String version;
    public static String date;
    public static long UID;
    public static String _UID;
    public static String description;

    static {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass()
            .getResourceAsStream("/smartboard.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new RuntimeException("Cant read version. " + ex);
        }

        version = settings.getProperty("version");
        date = settings.getProperty("date");
        _UID = settings.getProperty("UID");
        description = String
            .format(settings.getProperty("description"), settings.getProperty("version"),
                settings.getProperty("date"), settings.getProperty("UID"));
        try {
            UID = Long.decode(settings.getProperty("UID"));
        } catch (NumberFormatException ex) {
            UID = -1;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println();
        System.out.println("***    QSmartboardPlugin    ***");
        System.out.println("     " + description);
        System.out.println("     version " + version);
        System.out.println("     date " + date);
        System.out.println("     UID " + _UID);
        System.out.println("***   QMS Apertum-QSystem   ***");
        System.out.println();

        if ("123%".matches("^-?\\d+(%|px)$")) {
            //  System.out.println("good");
        } else {
            //   System.out.println("bad");
        }
        //System.out.println("00px%".replaceAll("0|%|(px)", ""));
    }

}
