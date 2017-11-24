/*
 * Copyright (C) 2014 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem;

import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.exceptions.ServerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Evgeniy Egorov
 */
public class About {

    public static String ver = "";
    public static String date = "";
    public static String db = "";

    public static void load() {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/ru/apertum/qsystem/common/version.properties");

        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }
        ver = settings.getProperty(FAbout.VERSION);
        date = settings.getProperty(FAbout.DATE);
        db = settings.getProperty(FAbout.VERSION_DB);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        load();
        System.out.println();
        System.out.println();

        printdef();

        System.out.println("");
        printver();
        System.out.println("");
        System.out.println("");
        System.out.println("*** QMS Apertum-QSystem ***");
        System.out.println("   version " + ver);
        System.out.println("   date " + date);
        System.out.println("   DB " + db);
        System.out.println("*** *** *** *** *** *** ***");
    }

    static public void printdef() {
        System.out.println();
        System.out.println("############################################################");
        System.out.println("#### SERVICE BC - CUSTOMER FLOW MANAGMENT SYSTEM (CFMS) ####");
        System.out.println("############################################################");
        System.out.println("");
    }

    static public void printver() {
        System.out.println("17.0");
    }

}
