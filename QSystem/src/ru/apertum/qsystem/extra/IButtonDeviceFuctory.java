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
package ru.apertum.qsystem.extra;

import ru.apertum.qsystem.common.cmd.RpcGetServerState;
import ru.apertum.qsystem.server.model.QUser;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Evgeniy Egorov
 */
public abstract interface IButtonDeviceFuctory extends IExtra {

    public static interface IButtonDevice {

        /**
         * Приняли от устройства и что-то делаем с этим
         *
         * @param b
         */
        public void doAction(byte[] b);

        /**
         * Опросить устройство
         */
        public void getFeedback();

        /**
         * Сменить адрес устройству
         */
        public void changeAdress();

        /**
         * Маякнуть
         */
        public void check();

        public QUser getUser();

        public void setUser(QUser user);

        public String getId();

    }

    /**
     * Message from device turn into ID from qub.adr
     *
     * @param bytes data from hardware device for pressing a button
     * @param users
     * @return Some class which ready to do something after receive data from device with ID - look out to qub.adr
     */
    public IButtonDevice getButtonDevice(byte[] bytes, List<QUser> users);

    public LinkedList<IButtonDevice> getButtonDevices(List<QUser> users);

    public AbstractTableModel getDeviceTable();

    public void refreshDeviceTable(LinkedList<QUser> users, LinkedList<RpcGetServerState.ServiceInfo> servs);

}
