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
package ru.apertum.qsystem.reports.model;

import org.apache.http.HttpRequest;
import ru.apertum.qsystem.reports.common.Response;

/**
 * Интерфейс герератора отчетов.
 * По сути генератор генерирует документ определенного формата(html, rtf, pdf) по готовому источнику данных
 * У каждого генератора есть свой формироватор, которым он сформирует данные для отчета.
 * При регистрации в СУБД отчетов в таблице reports указываются параметры генераторов, среди которых и класс его формироватора.
 * @author Evgeniy Egorov
 */
public interface IGenerator {

    /**
     * Получение отчета.
     * @param request заврос, который прислал блоузер на сервер.
     * @return данные для возврата клиенту
     */
    public Response process(HttpRequest request);
    
    /**
     * Ссылка на генератор. Уникально в системе. всегда идентифацирует генератор.
     * @return String - ссылка на генератора.
     */
    public String getHref();
}
