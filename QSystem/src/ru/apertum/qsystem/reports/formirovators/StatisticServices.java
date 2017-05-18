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
package ru.apertum.qsystem.reports.formirovators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.common.Uses;import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.Response;

/**
 * Статистический отчет в разрезе услуг за период.
 * @author Evgeniy Egorov
 */
public class StatisticServices extends AFormirovator {

    /**
     * Метод формирования параметров для отчета.
     * В отчет нужно передать некие параметры. Они упаковываются в Мар.
     * Если параметры не нужны, то сформировать пустой Мар.
     * @param driverClassName
     * @param url
     * @param username
     * @param password
     * @param request
     * @return
     */
    @Override
    public Map getParameters(String driverClassName, String url, String username, String password, HttpRequest request) {
        return paramMap;
    }
    /**
     * Для параметров
     */
    final private HashMap<String, Date> paramMap = new HashMap<>();

    /**
     * Метод получения коннекта к базе если отчет строится через коннект.
     * Если отчет строится не через коннект, а формироватором, то выдать null.
     * @param driverClassName
     * @param url
     * @param username
     * @param password
     * @param request
     * @return коннект соединения к базе или null.
     */
    @Override
    public Connection getConnection(String driverClassName, String url, String username, String password, HttpRequest request) {
        final Connection connection;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException ex) {
            throw new ReportException(StatisticServices.class.getName() + " " + ex);
        }
        return connection;
    }

    @Override
    public Response getDialog(String driverClassName, String url, String username, String password, HttpRequest request, String errorMessage) {
        return getDialog("/ru/apertum/qsystem/reports/web/get_period_for_statistic_services.html", request, errorMessage);
    }

    @Override
    public String validate(String driverClassName, String url, String username, String password, HttpRequest request, HashMap<String, String> params) {
        //sd=20.01.2009&ed=28.01.2009
        // проверка на корректность введенных параметров
        QLog.l().logger().trace("Принятые параметры \"" + params.toString() + "\".");
        if (params.size() == 2) {
            Date sd;
            Date fd;
            Date fd1;
            try {
                sd = Uses.FORMAT_DD_MM_YYYY.parse(params.get("sd"));
                fd = Uses.FORMAT_DD_MM_YYYY.parse(params.get("ed"));
                fd1 = DateUtils.addDays(Uses.FORMAT_DD_MM_YYYY.parse(params.get("ed")), 1);
            } catch (ParseException ex) {
                return "<br>Ошибка ввода параметров! Не все параметры введены корректно(дд.мм.гггг).";
            }
            if (!sd.after(fd)) {
                paramMap.put("sd", sd);
                paramMap.put("ed", fd);
                paramMap.put("ed1", fd1);
            } else {
                return "<br>Ошибка ввода параметров! Дата начала больше даты завершения.";
            }

        } else {
            return "<br>Ошибка ввода параметров!";
        }
        return null;// все нормально
    }
}
