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
package ru.apertum.qsystem.server;

import java.util.HashMap;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.model.QUser;

/**
 * Сессии пользователей. Т.е. те, кто уже в сервере сидит.
 *
 * @author Evgeniy Egorov
 */
public class QSession {

    private static final long LIVE_TIME = 65000;//65 sec.
    private final HashMap<String, Object> data = new HashMap<>();
    private String ipAdress;
    private byte[] IP;
    private QUser user;
    private long time = 0;

    public QSession(QUser user, String ipAdress, byte[] IP) {
        this.user = user;
        this.ipAdress = ipAdress;
        this.IP = IP;
        time = System.currentTimeMillis();
        QLog.l().logger().trace("Session for new user '" + user.getName() + "' ip=" + ipAdress);
    }

    public QSession(QUser user) {
        this.user = user;
        time = System.currentTimeMillis();
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }

    public byte[] getIP() {
        return IP;
    }

    public void setIP(byte[] IP) {
        this.IP = IP;
    }

    public QUser getUser() {
        return user;
    }

    public void setUser(QUser user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void update() {
        setTime(System.currentTimeMillis());
    }

    public Object getAttribute(String name) {
        return data.get(name);
    }

    public void setAttribute(String name, Object attr) {
        data.put(name, attr);
    }

    public void removeAttribute(String name) {
        data.remove(name);
    }

    public void removeAllAttributes() {
        data.clear();
    }

    boolean isValid() {
        return (System.currentTimeMillis() - time) < LIVE_TIME;
    }

}
