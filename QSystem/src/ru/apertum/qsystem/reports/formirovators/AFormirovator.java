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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.RepResBundle;
import ru.apertum.qsystem.reports.common.Response;
import ru.apertum.qsystem.reports.net.NetUtil;

/**
 * Формирует источник данных для отчета.
 * Сделан для удобства. чтоб не делать каждый раз ненужные методы.
 * @author Evgeniy Egorov
 */
abstract public class AFormirovator implements IFormirovator {

    /** 
     * Получение источника данных для отчета.
     * @return Готовая структура для компилирования в документ.
     */
    @Override
    public JRDataSource getDataSource(String driverClassName, String url, String username, String password, HttpRequest request) {
        return null;
    }

    /**
     * Метод формирования параметров для отчета.
     * В отчет нужно передать некие параметры. Они упаковываются в Мар.
     * Если параметры не нужны, то сформировать пустой Мар. Иначе перекрыть и сформировать Map с параметрами.
     * Перекрыть при необходимости.
     * @return
     */
    @Override
    public Map getParameters(String driverClassName, String url, String username, String password, HttpRequest request) {
        return new HashMap();
    }

    /**
     * Метод получения коннекта к базе если отчет строится через коннект.
     * Если отчет строится не через коннект, а формироватором, то выдать null.
     * Перекрыть при необходимости.
     * @return коннект соединения к базе или null.
     */
    @Override
    public Connection getConnection(String driverClassName, String url, String username, String password, HttpRequest request) {
        return null;
    }

    /**
     * Типо если просто нужно отдать страницу
     * @param HTMLfilePath
     * @param request 
     * @param errorMessage
     * @return готовая загруженная страница
     */
    protected Response getDialog(String HTMLfilePath, HttpRequest request, String errorMessage) {

        // вставим необходимую ссылку на отчет в форму ввода
        // и выдадим ее клиенту на заполнение.
        // после заполнения вызовется нужный отчет с введенными параметрами и этот метод вернет null,
        // что продолжет генерить отчет методом getDataSource с нужными параметрами.
        // А здесь мы просто знаем какой формироватор должен какие формы выдавать пользователю. На то он и формироватор, индивидуальный для каждого отчета.
        // get_period_for_statistic_services.html
        final InputStream inStream = getClass().getResourceAsStream(HTMLfilePath);
        byte[] result = null;
        try {
            result = Uses.readInputStream(inStream);
        } catch (IOException ex) {
            throw new ReportException("Ошибка чтения ресурса для диалогового ввода периода. " + ex);
        }
        if (errorMessage == null) {
            errorMessage = "";
        }
        Response res = null;
        try {
            res = new Response(RepResBundle.getInstance().prepareString(new String(result, "UTF-8").
                    replaceFirst(Uses.ANCHOR_DATA_FOR_REPORT, NetUtil.getUrl(request)).
                    replaceFirst(Uses.ANCHOR_ERROR_INPUT_DATA, errorMessage).
                    replaceFirst(Uses.ANCHOR_PROJECT_NAME_FOR_REPORT, Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF()))).
                    getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
        }
        return res;
    }

    /**
     * Ксли ничего дополнительного не требуется то метод и так вернет null.
     * При необходимости перекрыть
     * @param driverClassName
     * @param url
     * @param username
     * @param password
     * @param request
     * @return данные. которые будут отосланы пользователю, т.к. этого не требуется то для удобства null чтобы постоянно его не реализовывать
     */
    @Override
    public Response preparationReport(String driverClassName, String url, String username, String password, HttpRequest request) {
        return null;
    }
}
