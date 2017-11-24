package ru.apertum.qsystem.reports.formirovators;

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

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.RepResBundle;
import ru.apertum.qsystem.reports.common.Response;

/**
 * @author Igor Savin
 */
public class DistributionMedDayServices extends AFormirovator {

    /**
     * Для параметров
     */
    final private HashMap<String, Object> paramMap = new HashMap<>();

    /**
     * Метод формирования параметров для отчета. В отчет нужно передать некие параметры. Они
     * упаковываются в Мар. Если параметры не нужны, то сформировать пустой Мар.
     */
    @Override
    public Map getParameters(String driverClassName, String url, String username, String password,
        HttpRequest request) {
        return paramMap;
    }

    /**
     * Метод получения коннекта к базе если отчет строится через коннект. Если отчет строится не
     * через коннект, а формироватором, то выдать null.
     *
     * @return коннект соединения к базе или null.
     */
    @Override
    public Connection getConnection(String driverClassName, String url, String username,
        String password, HttpRequest request) {
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
    public Response getDialog(String driverClassName, String url, String username, String password,
        HttpRequest request, String errorMessage) {
        final Response result = getDialog(
            "/ru/apertum/qsystem/reports/web/get_date_distribution.html",
            request, errorMessage);
        final StringBuilder services_select = new StringBuilder();

        try {
            result.setData(new String(result.getData(), "UTF-8").replaceFirst("#DATA_FOR_TITLE#",
                RepResBundle.getInstance().getStringSafe("distribution_med_services"))
                .replaceFirst("#DATA_FOR_SERVICES#", services_select.toString()).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
        }
        services_select.setLength(0);
        return result;
    }

    @Override
    public String validate(String driverClassName, String url, String username, String password,
        HttpRequest request, HashMap<String, String> params) {
        // проверка на корректность введенных параметров
        QLog.l().logger().trace("Принятые параметры \"" + params.toString() + "\".");
        if (params.size() == 1) {
            // date/service_id/service
            Date date;
            String sdate;
            try {
                date = Uses.FORMAT_DD_MM_YYYY.parse(params.get("date"));
                sdate = (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(date);
            } catch (NumberFormatException | ParseException ex) {
                return "<br>Ошибка ввода параметров! Не все параметры введены корректно (дд.мм.гггг).";
            }
            paramMap.put("sdate", sdate);
            paramMap.put("date", date);
        } else {
            return "<br>Ошибка ввода параметров!";
        }
        return null;
    }
}
