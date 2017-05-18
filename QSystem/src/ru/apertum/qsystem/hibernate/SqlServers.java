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
package ru.apertum.qsystem.hibernate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.LinkedList;

/**
 *
 * @author Evgeniy Egorov
 */
public class SqlServers {

    @Expose
    @SerializedName("servers")
    private LinkedList<SqlServer> servers = new LinkedList<>();

    public LinkedList<SqlServer> getServers() {
        return servers;
    }

    public void setServers(LinkedList<SqlServer> servers) {
        this.servers = servers;
    }

    public SqlServers() {
    }

    public SqlServers(LinkedList<SqlServer> servers) {
        this.servers = servers;
    }

    public static class SqlServer {

        @Expose
        @SerializedName("driver")
        private String driver = "com.mysql.jdbc.Driver";
        @Expose
        @SerializedName("url")
        private String url = "jdbc:mysql://127.0.0.1/qsystem?autoReconnect=true&amp;characterEncoding=UTF-8";
        @Expose
        @SerializedName("user")
        private String user = "root";
        @Expose
        @SerializedName("password")
        private String password = "root";
        @Expose
        @SerializedName("main")
        private Boolean main = false;
        @Expose
        @SerializedName("current")
        private Boolean current = false;

        @Expose
        @SerializedName("name")
        private String name = "";

        public SqlServer() {
        }

        public SqlServer(String name, String user, String password, String url, boolean main, boolean current) {
            this.current = current;
            this.main = main;
            this.password = password;
            this.url = url;
            this.user = user;
            this.name = name;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean isCurrent() {
            return current;
        }

        public void setCurrent(Boolean current) {
            this.current = current;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean isMain() {
            return main;
        }

        public void setMain(Boolean main) {
            this.main = main;
        }

        @Override
        public String toString() {
            return name + "   " + (isCurrent() ? "C" : "_") + "   " + (isMain() ? "M" : "_") + "   " + getUrl();
        }
    }
}
