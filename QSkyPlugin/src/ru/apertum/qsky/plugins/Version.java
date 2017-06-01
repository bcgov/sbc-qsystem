/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Evgeniy Egorov
 */
public class Version {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/qskyplugin.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new RuntimeException("Cant read version. " + ex);
        }
        System.out.println("***    QSkyAPIplugin    ***");
        System.out.println("     version " + settings.getProperty("version"));
        System.out.println("     date " + settings.getProperty("date"));
        System.out.println("     UID " + settings.getProperty("UID"));
        System.out.println("*** QMS Apertum-QSystem ***");
    }
    
}
