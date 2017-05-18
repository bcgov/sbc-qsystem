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
package ru.apertum.qsystem.server.controller;

import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IExtra;
import ru.apertum.qsystem.server.model.QUser;

/**
 * Интерфейс событий вывода информации: зазываем, обрабатываем, освобождаем Имеет методы для конфигурирования.
 *
 * @author Evgeniy Egorov
 */
public interface IIndicatorBoard extends IExtra {

    public static final String SECTION = "mainboard";
    public static final String PARAMETER = "type";
    public static final String CLASSIC = "classic";
    public static final String HTML = "html";

    /**
     * На табло по определенному адресу должен замигать номер вызываемого клиента. При этом он должен подняться на верх.
     *
     * @param user пользователь, который начал работать с клиентом.
     * @param customer Клиент, который был вызван
     */
    public void inviteCustomer(QUser user, QCustomer customer);

    /**
     * На табло оператора долженн перестать мигать номер вызываемого клиента
     *
     * @param user пользователь, который начал работать с клиентом.
     */
    public void workCustomer(QUser user);

    /**
     * На табло по определенному адресу должно отчистиццо табло
     *
     * @param user пользователь, который удалил клиента.
     */
    public void killCustomer(QUser user);

    /**
     * Выключить информационное табло.
     */
    public void close();

    /**
     * Перегрузить информационное табло.
     */
    public void refresh();

    /**
     * Включить информационное табло.
     */
    public void showBoard();

    /**
     * Включить информационное табло.
     */
    public void clear();

    /**
     * Получить некую информацию о табло
     *
     * @return XML-конфигурация
     */
    public Element getConfig();

    /**
     * Сохранить конфигурацию
     *
     * @param element XML-конфигурация для сохранения
     */
    public void saveConfig(Element element);

    /**
     * получить форму редактора для табло.
     *
     * @return фрейм редактора.
     */
    public AFBoardRedactor getRedactor();

    /**
     * получить форму самого табло.
     *
     * @return Некая десктопная форма.
     */
    public Object getBoardForm();

    /**
     * Событие для главного табло в моменте встатия клиента в очередь. Появилось при необходимости обносить таблицу ближайших в момент получения талона.
     * Объявлен как дефолтный что бы старые плагины не отвалились. Надеюсь, не отвалятся.
     *
     * @param customer Клиент, который был вызван
     */
    public default void customerStandIn(QCustomer customer) {

    }
}
