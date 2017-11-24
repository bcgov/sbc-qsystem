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
package ru.apertum.qsystem.client.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.IClientNetProperty;

/**
 * Класс интерфейса INetPropertyImpl, для обработки командной строки клиентских модулей
 *
 * @author Evgeniy Egorov
 */
public class ClientNetProperty implements IClientNetProperty {

    private Integer portServer = -1; // Порт сервера
    private Integer portClient = -1; // Порт клиента
    private String adress; // Адрес сервера

    public ClientNetProperty(String[] args) {
        for (Integer i = 0; i < args.length; i++) {
            if ("-sport".equalsIgnoreCase(args[i])) {
                portServer = Integer.parseInt(args[i + 1]);
            }
            if ("-cport".equalsIgnoreCase(args[i])) {
                portClient = Integer.parseInt(args[i + 1]);
            }
            if ("-s".equalsIgnoreCase(args[i])) {
                adress = args[i + 1];
            }
        }
    }

    @Override
    public Integer getPort() {
        return portServer;
    }

    @Override
    public Integer getClientPort() {
        return portClient;
    }

    @Override
    public InetAddress getAddress() {
        InetAddress adr = null;
        try {
            adr = InetAddress.getByName(adress);
        } catch (UnknownHostException ex) {
            QLog.l().logger().error("Error!", ex);
        }
        return adr;
    }
}
