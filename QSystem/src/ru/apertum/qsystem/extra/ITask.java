/*
 * Copyright (C) 2015 Evgeniy Egorov
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

import ru.apertum.qsystem.common.cmd.AJsonRPC20;
import ru.apertum.qsystem.common.cmd.CmdParams;
import ru.apertum.qsystem.common.model.QCustomer;

/**
 * Если надо добавить команду, исполняемую сервером
 *
 * @author Evgeniy Egorov
 */
public interface ITask extends IExtra {

    /**
     * Выполнение команды :: Executing the command
     *
     * @param cmdParams входные параметры :: input parameters
     * @param ipAdress источник команды :: Team source
     * @param IP источник команды :: Team Source
     * @return результат выполнения команды, соблюдать протокол jsonRPC2.0 :: Result of the command,
     * follow the protocol jsonRPC2.0
     */
    public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP);

    /**
     * Выполнение команды :: Executing the command
     *
     * @param cmdParams входные параметры :: input parameters
     * @param ipAdress источник команды :: Team source
     * @param IP источник команды :: Team Source
     * @param customer Customer for whicch we need to start the service
     * @return результат выполнения команды, соблюдать протокол jsonRPC2.0 :: Result of the command,
     * follow the protocol jsonRPC2.0
     */
    public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP, QCustomer customer);

    /**
     * Уникальное имя команды, по которому ищется исполнитель The unique name of the team that the
     * artist is looking for
     */
    public String getName();
}
