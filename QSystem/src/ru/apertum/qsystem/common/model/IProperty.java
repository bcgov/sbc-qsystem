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
package ru.apertum.qsystem.common.model;

import org.dom4j.Element;

/**
 * Некаое именовоное свойство
 * @author Evgeniy Egorov
 *
 */
public interface IProperty {

    /**
     * Наименование
     * @return 
     */
    public String getName();

    /**
     * Некое значение.
     * Этод метод возвращает в зависимости от места использования:
     * 1. Коэффициент участия юзера в обработке услуги
     * 2. Да и все пока. почти ненужная весч.
     * @return
     */
    public Object getValue();

    /**
     * Описание параметра в виде XML.
     * @return XML-элемент корень параметра
     * @deprecated
     */
    @Deprecated
    public Element getXML();

    /**
     * Все свойства они описыпают какой-то конкретный объект.
     * Вот этот объект - носитель свойств и должен возвращать этод метод.
     * Если вдруг окажется что такого объекта нету, то возвращает null.
     * Этот метод появился в связи с тем что hibernate ваозвращает не просто поля,
     * а уже готовые классы, и у них можно взять свойства.
     * @return Объект - обладатель свойств.
     * @deprecated
     */
    @Deprecated
    public Object getInstance();
    
    /**
     * Нужно для Формирования списка наименований.
     * Вроде как этот медод везде есть, но сдесь добавлен для того чтоб помнить о том что его надо перекрыть.
     * @return Наименование.
     */
    @Override
    public String toString();
    
    /**
     * Уникальный идентификатор.
     * Например как Идентификатор услуги.
     * Нужен для построения дерева услуг при чтении из базы.
     * @return
     */
    public Long getId();
}
