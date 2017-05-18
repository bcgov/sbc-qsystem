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

/**
 * Если надо добавить команду, исполняемую сервером
 *
 * @author Evgeniy Egorov
 */
public interface ITask extends IExtra {

    /**
     * Выполнение команды
     * @param cmdParams входные параметры
     * @param ipAdress источник команды
     * @param IP источник команды
     * @return результат выполнения команды, соблюдать протокол jsonRPC2.0
     */
    public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP);

    /**
     * Уникальное имя команды, по которому ищется исполнитель
     *
     * @return
     */
    public String getName();
}
