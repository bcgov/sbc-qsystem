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

import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.ATreeModel;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;

/**
 * Интерфейс для плагинов печати талонов. Нужен для добавления плагинов Реализовываем этот интерфейс, оформляем jar как сервисный, кладем в папку plugins.
 *
 * @author Evgeniy Egorov
 */
public interface IPrintTicket extends IExtra {

    /**
     * Печать талона.
     *
     * @param customer Для него печатаем
     * @param caption Название конторы при печати из админки
     * @return true - Если все нормально напечатали.
     */
    public boolean printTicket(QCustomer customer, String caption);

    /**
     * Печать талона для предварительной записи
     *
     * @param advCustomer Для него печатаем
     * @param caption Название конторы при печати из админки
     * @return true - Если все нормально напечатали.
     */
    public boolean printTicketAdvance(final QAdvanceCustomer advCustomer, String caption);

    /**
     * Печать талона для предварительной записи
     *
     * @param customer Для него печатаем
     * @param tm это деоево услуг чтоб в нем искать услуги по ID
     * @return true - Если все нормально напечатали.
     */
    public boolean printTicketComplex(QCustomer customer, ATreeModel tm);
}
