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
package ru.apertum.qsystem.reports.generators;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRDataSource;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.RepResBundle;
import ru.apertum.qsystem.reports.common.Response;
import ru.apertum.qsystem.reports.model.AGenerator;
import ru.apertum.qsystem.reports.model.QReportsList;
import ru.apertum.qsystem.reports.net.NetUtil;

/**
 *
 * @author Evgeniy Egorov
 */
public class ReportsList extends AGenerator {

    public ReportsList(String href, String resourceNameTemplate) {
        super(href, resourceNameTemplate);
    }

    @Override
    protected JRDataSource getDataSource(HttpRequest request) {
        throw new ReportException("Ошибочное обращение к методу.");
    }

    @Override
    protected Response preparationReport(HttpRequest request) {
        // в запросе должен быть пароль и пользователь, если нету, то отказ на вход
        String entityContent = NetUtil.getEntityContent(request);
        QLog.l().logger().trace("Принятые параметры \"" + entityContent + "\".");
        // ресурс для выдачи в браузер. это либо список отчетов при корректном логининге или отказ на вход
        String res = "/ru/apertum/qsystem/reports/web/error_login.html";
        String usr = "err";
        String pwd = "err";
        // разбирем параметры
        final HashMap<String, String> cookie = NetUtil.getCookie(entityContent, "&");
        if (cookie.containsKey("username") && cookie.containsKey("password")) {
            if (QReportsList.getInstance().isTrueUser(cookie.get("username"), cookie.get("password"))) {
                res = "/ru/apertum/qsystem/reports/web/reportList.html";
                usr = cookie.get("username");
                pwd = cookie.get("password");
            }
        }
        final InputStream inStream = getClass().getResourceAsStream(res);
        byte[] result = null;
        try {
            result = RepResBundle.getInstance().prepareString(
                    new String(Uses.readInputStream(inStream), "UTF-8")).
                    replaceFirst(Uses.ANCHOR_PROJECT_NAME_FOR_REPORT, Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF())).
                    getBytes("UTF-8");
            if ("/ru/apertum/qsystem/reports/web/reportList.html".equals(res)) {
                // добавим список аналитических отчетов
                result = new String(result, "UTF-8").replaceFirst(Uses.ANCHOR_REPORT_LIST, QReportsList.getInstance().getHtmlRepList()).getBytes("UTF-8");
                // Добавим кукисы сессии
                //<META HTTP-EQUIV="Set-Cookie" CONTENT="NAME=value; EXPIRES=date; DOMAIN=domain_name; PATH=path; SECURE">
                final String coocie = "<META HTTP-EQUIV=\"Set-Cookie\" CONTENT=\"username=" + URLEncoder.encode(usr, "utf-8") + "\">\n<META HTTP-EQUIV=\"Set-Cookie\" CONTENT=\"password=" + URLEncoder.encode(pwd, "utf-8") + "\">";
                result = new String(result, "UTF-8").replaceFirst(Uses.ANCHOR_COOCIES, coocie).getBytes("UTF-8");
            }
        } catch (IOException ex) {
            throw new ReportException("Ошибка чтения ресурса для диалогового выбора отчета. " + ex);
        }
        return new Response(result);
    }

    @Override
    protected HashMap getParameters(HttpRequest request) {
        throw new ReportException("Ошибочное обращение к методу.");
    }

    @Override
    protected Connection getConnection(HttpRequest request) {
        throw new ReportException("Ошибочное обращение к методу.");
    }

    @Override
    protected Response getDialog(HttpRequest request, String errorMessage) {
        throw new ReportException("Ошибочное обращение к методу.");
    }

    @Override
    protected String validate(HttpRequest request, HashMap<String, String> params) {
        return null;
    }
}
