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
package ru.apertum.qsystem.hibernate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.Action;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.server.ChangeContext;

/**
 * Класс - не фабрика сессий не для Hibernate.
 *
 * @author Evgeniy Egorov
 */
/**
 * Класс - не фабрика сессий не для Hibernate. Добавлено не поле для не регистрации не аннорированных классов.
 *
 * @author Evgeniy Egorov
 */
public class AnnotationSessionFactoryBean implements Action {

    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://127.0.0.1/qsystem?autoReconnect=true&amp;characterEncoding=UTF-8";
       private String user = "root";
       private String password = "root";
       private String name = "";
       private boolean main = false;
    private boolean current = false;
    
//    private String driver = "org.h2.Driver";
//    private String url = "jdbc:h2:./db/QSystemDB;AUTO_SERVER\u003dTRUE";
//    private String user = "root";
//    private String password = "root";
//    private String name = "H2-embedded";
//    private boolean main = true;
//    private boolean current = true;
    private LinkedList<SqlServers.SqlServer> servers;

    public LinkedList<SqlServers.SqlServer> getServers() {
        if (servers == null) {
            load(ChangeContext.filePath);
        }
        return servers;
    }

    public void makeCurrent(SqlServers.SqlServer server) {
        servers.stream().forEach((ser) -> {
            ser.setCurrent(false);
        });
        server.setCurrent(true);
    }

    public void saveServers() {
        try {
            try (FileOutputStream fos = new FileOutputStream(ChangeContext.filePath)) {
                final String message;
                Gson gson = GsonPool.getInstance().borrowGson();
                try {
                    message = gson.toJson(new SqlServers(getServers()));
                } finally {
                    GsonPool.getInstance().returnGson(gson);
                }
                fos.write(message.getBytes("UTF-8"));
                fos.flush();
            }
        } catch (IOException ex) {
            throw new ClientException(ex);
        }
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void putValue(String key, Object value) {
    }

    @Override
    public void setEnabled(boolean b) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (SqlServers.SqlServer ser : servers) {
            if (ser.getName().equals(e.getActionCommand())) {
                makeCurrent(ser);
                saveServers();
                break;
            }
        }
        final String[] params = new String[1];
        final File f = new File("StartAdmin.bat");
        if (f.exists()) {
            params[0] = "StartAdmin.bat";
        } else {
            params[0] = "admin.sh";
        }
        try {
            Runtime.getRuntime().exec(params);
        } catch (IOException ex) {
            QLog.l().logger().error("Не стартануло. ", ex);
        }
        System.exit(0);
    }
    private boolean flag = true;

    synchronized private void load(String filePath) {
        if (flag) {
            final File conff = new File(filePath);
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
                    servers = gson.fromJson(str, SqlServers.class).getServers();
                    if (servers == null) {
                        throw new RuntimeException("File error.");
                    }
                } catch (JsonSyntaxException ex) {
                    throw new RuntimeException("Data error. " + ex.toString());
                } finally {
                    GsonPool.getInstance().returnGson(gson);
                }
            } else {
                servers = new LinkedList<>();
            }
            if (servers.size() != 0) {
                SqlServers.SqlServer cur = servers.get(0);
                for (SqlServers.SqlServer ser : servers) {
                    if (ser.isCurrent()) {
                        cur = ser;
                        break;
                    }
                }
                driver = cur.getDriver();
                url = cur.getUrl();
                user = cur.getUser();
                password = cur.getPassword();
                name = cur.getName();
            }
            else
            {
                driver = "com.mysql.jdbc.Driver";
                name = System.getenv ("MYSQL_DATABASE");
                url = "jdbc:mysql://" + System.getenv ("MYSQL_SERVICE") + "/" + name ;
                user = System.getenv ("MYSQL_USER");
                password = System.getenv ("MYSQL_PASSWORD");
                name = System.getenv ("MYSQL_DATABASE");
            }
            flag = false;
            QLog.l().logger().warn("DB server '" + name + " driver=" + driver + "' url=" + url);
        }
    }

    public boolean isMain() {
        load(ChangeContext.filePath);
        return main;
    }

    public void setMain(boolean main) {
        load(ChangeContext.filePath);
        this.main = main;
    }

    public boolean isCurrent() {
        load(ChangeContext.filePath);
        return current;
    }

    public void setCurrent(boolean current) {
        load(ChangeContext.filePath);
        this.current = current;
    }

    public String getDriver() {
        load(ChangeContext.filePath);
        return driver;
    }

    public void setDriver(String driver) {
        load(ChangeContext.filePath);
        this.driver = driver;
    }



    public String getUrl() {
        load(ChangeContext.filePath);
        return url;
    }

    public void setUrl(String url) {
        load(ChangeContext.filePath);
        this.url = url;
    }

    public String getUser() {
        load(ChangeContext.filePath);
        return user;
    }

    public void setUser(String user) {
        load(ChangeContext.filePath);
        this.user = user;
    }

    public String getPassword() {
        load(ChangeContext.filePath);
        return password;
    }

    public void setPassword(String password) {
        load(ChangeContext.filePath);
        this.password = password;
    }

    public String getName() {
        return name;
    }
}
