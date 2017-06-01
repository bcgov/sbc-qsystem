/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.plugins.positions;

import ru.apertum.qsky.plugins.IQSkyPluginUID;
import ru.apertum.qsky.plugins.ping.PingResult;
import ru.apertum.qsky.plugins.ws.WorkThreads;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.ICustomerChangePosition;

/**
 * Этот класс создастся как плагин и методы его будут вызываться из главной программы как методы плагина
 * @author egorov
 */
public class PositionChanger implements ICustomerChangePosition, IQSkyPluginUID {

    @Override
    public void insert(QCustomer customer, QCustomer before, QCustomer after) {
        if (!PingResult.getInstance().isReady()) {
            QLog.l().logger().error("Версия плагина \"QSkySenderPlugin\" не сообветствует версии облака.");
            return;
        }
        // Создаем событие
        // Отсылаем событие
        WorkThreads.getInstance().moveClient(new PositionToSky(PositionToSky.Mode.INSERT, customer, before, after));
    }

    @Override
    public void remove(QCustomer customer) {
        if (!PingResult.getInstance().isReady()) {
            QLog.l().logger().error("Версия плагина \"QSkySenderPlugin\" не сообветствует версии облака.");
            return;
        }
        // Создаем событие
        // Отсылаем событие
        WorkThreads.getInstance().moveClient(new PositionToSky(PositionToSky.Mode.REMOVE, customer, null, null));
    }

    @Override
    public String getDescription() {
        return "Плагин \"QSkySenderPlugin\" отправляет в облако позицию клиента в очереди";
    }

    @Override
    public long getUID() {
        return UID;
    }
}
