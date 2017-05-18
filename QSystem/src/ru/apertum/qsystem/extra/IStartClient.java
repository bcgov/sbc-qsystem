/*
 * Copyright (C) 2013 Evgeniy Egorov
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

import java.awt.event.ActionEvent;
import ru.apertum.qsystem.client.forms.FClient;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.server.model.QUser;

/**
 * Интерфейс плагинов, которые просто стартуют при старте программы оператора
 *
 * @author Evgeniy Egorov
 */
public interface IStartClient extends IExtra {

    /**
     * Метод выполнится после создания формы приложения оператора
     *
     * @param form форма приложения оператора
     */
    public void start(FClient form);

    public void pressButton(QUser user, INetProperty netProperty, RpcGetSelfSituation.SelfSituation situation, ActionEvent evt, int keyId);
}
