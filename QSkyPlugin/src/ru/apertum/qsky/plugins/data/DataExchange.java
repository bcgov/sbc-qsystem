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
package ru.apertum.qsky.plugins.data;

import ru.apertum.qsky.plugins.IQSkyPluginUID;
import ru.apertum.qsky.plugins.ping.PingResult;
import ru.apertum.qsky.plugins.ws.SkyService;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.extra.IDataExchange;

/**
 *
 * @author egorov
 */
public class DataExchange implements IDataExchange, IQSkyPluginUID {

    @Override
    public void sendServiceName(Long branchId, Long serviceId, String name) {
        if (!PingResult.getInstance().isReady()) {
            QLog.l().logger().error("Версия плагина \"QSkySenderPlugin\" не сообветствует версии облака.");
            return;
        }
        System.out.println("Service to Sky: " + name + "(" + serviceId + "/" + branchId + ")");
        SkyService.getInstance().getQsky().sendServiceName(branchId, serviceId, name);
    }

    @Override
    public void sendUserName(Long branchId, Long userId, String name) {
        if (!PingResult.getInstance().isReady()) {
            QLog.l().logger().error("Версия плагина \"QSkySenderPlugin\" не сообветствует версии облака.");
            return;
        }
        System.out.println("User to Sky: " + name + "(" + userId + "/" + branchId + ")");
        SkyService.getInstance().getQsky().sendUserName(branchId, userId, name);
    }

    @Override
    public String getDescription() {
        return "Плагин \"QSkySenderPlugin\". Обмен данными с облаком.";
    }

    @Override
    public long getUID() {
        return UID;
    }

}
