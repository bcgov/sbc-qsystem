/*
 * Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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
package ru.apertum.qsystem.server.http;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.cmd.CmdParams;
import ru.apertum.qsystem.common.cmd.JsonRPC20;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.controller.Executer;

/**
 *
 * @author Evgeniy Egorov
 */
public class CommandHandler extends AbstractHandler {

    public static final String CMD_URL_PATTERN = "/qsystem/command";
    public static final String WS_URL_PATTERN = "/qsystem/ws";
    public static final String INFO_URL_PATTERN = "/qsystem/info";

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        final String result;
        final int status;
        String contentType = "text/json;charset=utf-8";
        switch (target) {
            case WS_URL_PATTERN:
                // этот урл нужно учесть, что бы запрос в jetty дальше пробросился по хандлерам.
                return;
            case INFO_URL_PATTERN:
                QLog.l().logger().trace("HTTP task: " + INFO_URL_PATTERN);
                final Properties settings = new Properties();
                //"/ru/apertum/qsystem/reports/web/"
                final InputStream inStream = this.getClass().getResourceAsStream("/ru/apertum/qsystem/common/version.properties");

                try {
                    settings.load(inStream);
                } catch (IOException ex) {
                    throw new ServerException("Проблемы с чтением версии. " + ex);
                }
                result = "<html><h1>QSystem<hr><br>Welcome to server QSystem!<br><br>Добро пожаловать на сервер QSystem!</h1><br>"
                        + FAbout.getLocaleMessage("about.version") + " : " + settings.getProperty(FAbout.VERSION)
                        + "<br>"
                        + FAbout.getLocaleMessage("about.db_version") + " : " + ServerProps.getInstance().getProps().getVersion()
                        + "<br>"
                        + FAbout.getLocaleMessage("about.data") + " : " + settings.getProperty(FAbout.DATE);
                status = HttpServletResponse.SC_OK;
                contentType = "text/html;charset=utf-8";
                break;
            case CMD_URL_PATTERN:
                final JsonRPC20 rpc;
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    QLog.l().logger().trace("HTTP GET task: \"" + request.getParameter(CmdParams.CMD) + "\"\n" + request.getQueryString());
                    rpc = new JsonRPC20(request.getParameter(CmdParams.CMD), new CmdParams(request.getQueryString()));
                } else {//POST
                    final String data;
                    try {
                        request.setCharacterEncoding("utf-8");
                        final StringBuilder sb = new StringBuilder();
                        try (ServletInputStream fis = request.getInputStream(); Scanner s = new Scanner(new InputStreamReader(fis, "UTF-8"))) {
                            while (s.hasNextLine()) {
                                final String line = s.nextLine().trim();
                                sb.append(line);
                            }
                        } catch (IOException ex) {
                            throw ex;
                        }

                        data = sb.toString();
                        sb.setLength(0);
                    } catch (IOException ex) {
                        status = HttpServletResponse.SC_BAD_REQUEST;
                        result = "<h1>Ошибка чтения входных данных по http.</h1>";
                        contentType = "text/html;charset=utf-8";
                        QLog.l().logger().error("Error of reading input data on http.", ex);
                        break;
                        //throw new ServerException("Ошибка чтения входных данных по http. ", ex);
                    }
                    if (data == null || data.isEmpty()) {
                        status = HttpServletResponse.SC_BAD_REQUEST;
                        result = "<h1>Не получен текст коменды по http.</h1>";
                        contentType = "text/html;charset=utf-8";
                        QLog.l().logger().error("Не получен текст команды по http..");
                        break;
                    }
                    QLog.l().logger().trace("HTTP POST task:\n" + (data.length() > 200 ? (data.substring(0, 200) + "...") : data));

                    final Gson gson = GsonPool.getInstance().borrowGson();
                    try {
                        rpc = gson.fromJson(data, JsonRPC20.class);
                    } finally {
                        GsonPool.getInstance().returnGson(gson);
                    }
                }

                String answer;
                final Gson gson = GsonPool.getInstance().borrowGson();
                boolean f = false;
                try {
                    // полученное задание передаем в пул
                    final Object res = Executer.getInstance().doTask(rpc, request.getRemoteAddr(), request.getRemoteAddr().getBytes());
                    answer = gson.toJson(res);

                } catch (Exception ex) {
                    answer = "Произошла ошибка обработки задания. " + ex;
                    f = true;
                    QLog.l().logger().error("Произошла ошибка обработки задания. ", ex);
                } finally {
                    GsonPool.getInstance().returnGson(gson);
                }
                result = answer;
                // выводим данные:
                QLog.l().logger().trace("HTTP response:\n" + (answer.length() > 200 ? (answer.substring(0, 200) + "...") : answer));
                status = f ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : HttpServletResponse.SC_OK;
                break;
            default:
                //status = HttpServletResponse.SC_OK;
                //result = "<h1>QSystem<hr><br><br>URL не поддерживается // URL not supply</h1>";
                return;
        }
        //System.out.println(status + "/n" + result);
        response.setContentType(contentType);
        response.setStatus(status);
        //response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        try {
            response.getWriter().println(result);
        } catch (IOException ex) {
            throw new ServerException("Накрылась сборка ответа от сервера по HTTP.", ex);
        }
    }
}
