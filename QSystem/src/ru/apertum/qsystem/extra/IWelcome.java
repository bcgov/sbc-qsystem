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

import java.awt.Frame;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QService;

/**
 *
 * @author Evgeniy Egorov
 */
public interface IWelcome extends IExtra {

    public void start(IClientNetProperty netProperty, RpcGetAllServices.ServicesForWelcome srvs);

    /**
     * Mетод который показывает модально диалог с информацией для клиентов.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param netProperty свойства работы с сервером
     * @param htmlText текст для прочтения
     * @param printText текст для печати
     * @param modal модальный диалог или нет
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @return продолжат сравить кастомера в очередь или нет, типа если true - все одобрено, false - что-то не понравилось клиенту и он не будет стоять
     */
    public boolean showPreInfoDialog(Frame parent, INetProperty netProperty, String htmlText, String printText, boolean modal, boolean fullscreen, int delay);

    /**
     * Статический метод который показывает модально диалог ввода строки.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param modal модальный диалог или нет
     * @param netProperty свойства работы с сервером
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @param caption название на нужном языке
     * @param service услуга, в которую встает
     * @return XML-описание результата предварительной записи, по сути это номерок. если null, то отказались от предварительной записи
     */
    public String showInputDialog(Frame parent, boolean modal, INetProperty netProperty, boolean fullscreen, int delay, String caption, QService service);

    /**
     * Событие нажатия кнопки на таче.
     *
     * @param servece Кнопку соотв этой услуге нажали.
     */
    public void buttonPressed(QService servece);

    /**
     * Событие - Встал в очередь
     *
     * @param customer этот встал в очередь.
     * @param service
     */
    public void readyNewCustomer(QCustomer customer, QService service);

    /**
     * Событие - записался предварительно
     *
     * @param advCustomer этот и записался
     * @param service
     */
    public void readyNewAdvCustomer(QAdvanceCustomer advCustomer, QService service);

}
