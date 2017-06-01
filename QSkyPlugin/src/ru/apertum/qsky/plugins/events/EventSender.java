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
package ru.apertum.qsky.plugins.events;

import ru.apertum.qsky.plugins.IQSkyPluginUID;
import ru.apertum.qsky.plugins.ping.PingResult;
import ru.apertum.qsky.plugins.ws.WorkThreads;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IChangeCustomerStateEvent;

/**
 * Плагин во время смены статуса клиенту отсылает статистику в облако через вебсервис
 * @author egorov
 */
public class EventSender implements IChangeCustomerStateEvent, IQSkyPluginUID {

    @Override
    public void change(QCustomer qc, CustomerState cs, Long newServiceId) {
        if (!PingResult.getInstance().isReady()) {
            QLog.l().logger().error("Версия плагина \"QSkySenderPlugin\" не сообветствует версии облака.");
            return;
        }
        // Создаем событие
        // Отсылаем событие
        WorkThreads.getInstance().addEvent(new EventToSky(qc, cs, newServiceId));
    }

    @Override
    public String getDescription() {
        return "Плагин \"QSkySenderPlugin\" во время смены статуса клиенту отсылает статистику в облако через вебсервис";
    }

    @Override
    public long getUID() {
        return UID;
    }

    @Override
    public void change(String userPoint, String customerPrefix, int customerNumber, CustomerState cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
