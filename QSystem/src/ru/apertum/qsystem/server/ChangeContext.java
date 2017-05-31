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
package ru.apertum.qsystem.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Scanner;
import org.dom4j.DocumentException;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.hibernate.SqlServers;
import ru.apertum.qsystem.hibernate.SqlServers.SqlServer;

/**
 * Утилита изменения Spring-контекста в инсталлированном приложении Класс изменения Spring-контекста в инсталлированном приложении. Консольный классик простого
 * редактирования XML-файла
 *
 * @author Evgeniy Egorov
 */
public class ChangeContext {

    static public final String filePath = "config/asfb.dat";

    public static void main(String args[]) throws DocumentException, IOException {
        final File conff = new File(filePath);
        final LinkedList<SqlServer> servs;
        if (conff.exists()) {
            String str = "";
            try (FileInputStream fis = new FileInputStream(conff); Scanner s = new Scanner(new InputStreamReader(fis, "UTF-8"))) {
                while (s.hasNextLine()) {
                    final String line = s.nextLine().trim();
                    str += line;
                }
            } catch (IOException ex) {
                System.err.println(ex);
                throw new RuntimeException(ex);
            }
            Gson gson = GsonPool.getInstance().borrowGson();
            try {
                servs = gson.fromJson(str, SqlServers.class).getServers();
                if (servs == null) {
                    throw new RuntimeException("File error.");
                }
            } catch (JsonSyntaxException ex) {
                throw new RuntimeException("Data error. " + ex.toString());
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }
        } else {
            servs = new LinkedList<>();
        }

        String str = "asd";
        while (!"".equals(str)) {
            System.out.println();
            System.out.println();

            if (servs.size() == 0) {
                System.out.println("No servers.");
            } else {
                System.out.println("Servers:");
                int i = 0;
                for (SqlServer ser : servs) {
                    System.out.println((++i) + " " + ser);
                }
                System.out.println("");
            }

            if (servs.size() != 0) {

                System.out.println("Press '+' for create DB server");
                System.out.println("Press '-' for remove DB server");
                System.out.println("Press 'm' for select main DB server");
                System.out.println("Press 'c' for select current DB server");
                System.out.print("Press number for edit DB server(enter - exit):");
                str = read();

                if ("+".equals(str)) {
                    servs.add(new SqlServer("serverDB_" + servs.size(), "root", "root", "jdbc:mysql://127.0.0.1/qsystem?autoReconnect=true&amp;characterEncoding=UTF-8", false, false));
                }
                if ("-".equals(str)) {
                    System.out.println("");
                    System.out.println("Choose number for removing('enter' for cancel): ");
                    int i = 0;
                    for (SqlServer ser : servs) {
                        System.out.println("  " + (++i) + "  " + ser);
                    }
                    System.out.print("Removing server:");
                    str = read();
                    if (!"".equals(str) && str.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= servs.size()) {
                        servs.remove(Integer.parseInt(str) - 1);
                    }
                    str = "-";
                }

                if ("m".equals(str)) {
                    System.out.println("");
                    System.out.println("Choose number for main('enter' for cancel): ");
                    int i = 0;
                    for (SqlServer ser : servs) {
                        System.out.println("  " + (++i) + "  " + ser);
                    }
                    System.out.print("Main server:");
                    str = read();
                    if (!"".equals(str) && str.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= servs.size()) {
                        servs.stream().forEach((ser) -> {
                            ser.setMain(Boolean.FALSE);
                        });
                        servs.get(Integer.parseInt(str) - 1).setMain(Boolean.TRUE);
                    }
                    str = "m";
                }
                if ("c".equals(str)) {
                    System.out.println("");
                    System.out.println("Choose number for current('enter' for cancel): ");
                    int i = 0;
                    for (SqlServer ser : servs) {
                        System.out.println("  " + (++i) + "  " + ser);
                    }
                    System.out.print("Current server:");
                    str = read();
                    if (!"".equals(str) && str.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= servs.size()) {
                        servs.stream().forEach((ser) -> {
                            ser.setCurrent(Boolean.FALSE);
                        });
                        servs.get(Integer.parseInt(str) - 1).setCurrent(Boolean.TRUE);
                    }
                    str = "c";
                }
                if (!"".equals(str) && str.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= servs.size()) {
                    final SqlServer ser = servs.get(Integer.parseInt(str) - 1);
                    String a = null;
                    if (ser.getUrl().contains("//") && ser.getUrl().indexOf("/", ser.getUrl().indexOf("//")) > -1) {
                        a = ser.getUrl().substring(ser.getUrl().indexOf("//") + 2, ser.getUrl().indexOf("/", ser.getUrl().indexOf("//") + 2));
                    }
                    System.out.println("");
                    System.out.println("");
                    System.out.println("Choose number for edit('enter' for cancel): ");
                    System.out.println("  1 user=" + ser.getUser());
                    System.out.println("  2 password=" + ser.getPassword());
                    System.out.println("  3 url=" + ser.getUrl());
                    System.out.println("  4 driver=" + ser.getDriver());
                    if (a != null) {
                        System.out.println("  5 adress=" + a);
                    }
                    System.out.print("Parameter for edit:");
                    str = read();
                    if (!"".equals(str) && str.matches("-?\\d+(\\.\\d+)?") && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 4) {
                        switch (Integer.parseInt(str)) {
                            case 1:
                                System.out.println("User");
                                System.out.println("Old value: " + ser.getUser());
                                System.out.print("New value: ");
                                ser.setUser(read());
                                break;
                            case 2:
                                System.out.println("Password");
                                System.out.println("Old value: " + ser.getPassword());
                                System.out.print("New value: ");
                                ser.setPassword(read());
                                break;
                            case 3:
                                System.out.println("URL");
                                System.out.println("Old value: " + ser.getUrl());
                                System.out.print("New value: ");
                                ser.setUrl(read());
                                break;
                            case 4:
                                System.out.println("Driver");
                                System.out.println("Old value: " + ser.getDriver());
                                System.out.print("New value: ");
                                ser.setDriver(read());
                                break;    
                            case 5:
                                if (a != null) {
                                    System.out.println("Adress");
                                    System.out.println("Old value: " + a);
                                    System.out.print("New value: ");
                                    ser.setUrl(ser.getUrl().replace(a, read()));
                                }
                                break;
                        }
                    }
                    str = "c";
                }

            } else {
                System.out.print("Press '+' for create first DB server(enter - exit):");
                str = read();
                if ("+".equals(str)) {
                    System.out.print("Enter a name of the new DB server:");
                    str = read();
                    servs.add(new SqlServer(str, "root", "root", "jdbc:mysql://127.0.0.1/qsystem?autoReconnect=true&amp;characterEncoding=UTF-8", false, false));
                }
            }
        }

        System.out.println();
        System.out.print("Save context(1 - yes, any key - no): ");
        if ("1".equals(read())) {
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                final String message;
                Gson gson = GsonPool.getInstance().borrowGson();
                try {
                    message = gson.toJson(new SqlServers(servs));
                } finally {
                    GsonPool.getInstance().returnGson(gson);
                }
                fos.write(message.getBytes("UTF-8"));
                fos.flush();
            }
            System.out.println("Save and Exit");
        } else {
            System.out.println("Exit without save");
        }
    }

    /**
     * Читаем введеную строку в консоли
     *
     * @return введеная строка
     */
    private static String read() {
        Scanner d = new Scanner(new InputStreamReader(System.in));
        return d.nextLine();
    }
}
