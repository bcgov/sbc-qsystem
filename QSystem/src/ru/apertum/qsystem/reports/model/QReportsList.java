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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.ComboBoxModel;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.RepResBundle;
import ru.apertum.qsystem.reports.common.Response;
import ru.apertum.qsystem.reports.generators.RepCurrentUsers;
import ru.apertum.qsystem.reports.generators.ReportCurrentServices;
import ru.apertum.qsystem.reports.generators.ReportsList;
import ru.apertum.qsystem.reports.net.NetUtil;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.ATListModel;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

/**
 *
 * @author Evgeniy Egorov
 */
public class QReportsList extends ATListModel<QReport> implements ComboBoxModel {

    private QReportsList() {
        super();
    }

    public static QReportsList getInstance() {
        return QResultListHolder.INSTANCE;
    }

    private static class QResultListHolder {

        private static final QReportsList INSTANCE = new QReportsList();
    }

    @Override
    protected LinkedList<QReport> load() {
        final LinkedList<QReport> reports = new LinkedList<>(Spring.getInstance().getHt().loadAll(QReport.class));
        QLog.l().logRep().debug("Загружено из базы " + reports.size() + " отчетов.");

        passMap = new HashMap<>();
        htmlRepList = "";
        htmlUsersList = "";
        reports.stream().map((report) -> {
            addGenerator(report);
            return report;
        }).forEach((report) -> {
            htmlRepList = htmlRepList.concat(
                    "<tr>\n"
                    + "<td style=\"text-align: left; padding-left: 60px;\">\n"
                    + "<a href=\"" + report.getHref() + ".html\" target=\"_blank\">"
                    + (RepResBundle.getInstance().present(report.getHref()) ? RepResBundle.getInstance().getStringSafe(report.getHref()) : report.getName())
                    + "</a>\n"
                    + "<a href=\"" + report.getHref() + ".rtf\" target=\"_blank\">[RTF]</a>\n"
                    + "<a href=\"" + report.getHref() + ".pdf\" target=\"_blank\">[PDF]</a>\n"
                    + "<a href=\"" + report.getHref() + ".xlsx\" target=\"_blank\">[XLSX]</a>\n"
                    + "<a href=\"" + report.getHref() + ".csv\" target=\"_blank\">[CSV]</a>\n"
                    + "</td>\n"
                    + "</tr>\n");
            report.setName(RepResBundle.getInstance().present(report.getHref()) ? RepResBundle.getInstance().getStringSafe(report.getHref()) : report.getName());
        });
        /*
         * Это не отчет. это генератор списка отчетов, который проверяет пароль и пользователя и формирует
         * coocies для браузера, чтоб далее браузер подставлял жти куки в запрос и тем самым сервак "узнавал пользователя".
         * Сдесь нужен только метод preparation(), т.к. никакой генерации нет.
         */
        addGenerator(new ReportsList("reportList", ""));
        /*
         * Отчет по текущему состоянию в разрее услуг
         */
        addGenerator(new ReportCurrentServices(Uses.REPORT_CURRENT_SERVICES.toLowerCase(), "/ru/apertum/qsystem/reports/templates/currentStateServices.jasper"));
        /*
         * Отчет по текущему состоянию в разрезе пользователей
         */
        addGenerator(new RepCurrentUsers(Uses.REPORT_CURRENT_USERS.toLowerCase(), "/ru/apertum/qsystem/reports/templates/currentStateUsers.jasper"));

        String sel = " selected";
        for (QUser user : QUserList.getInstance().getItems()) {
            // список пользователей, допущенных до отчетов
            if (user.getReportAccess()) {
                htmlUsersList = htmlUsersList.concat("<option" + sel + ">").concat(user.getName()).concat("</option>\n");
                sel = "";
                if (user.getReportAccess()) {
                    passMap.put(user.getName(), user.getPassword());
                }
            }
        }

        return reports;
    }
    private QReport selected;

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (QReport) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
    // задания, доступны по их ссылкам
    private final static HashMap<String, IGenerator> generators = new HashMap<>();

    private static void addGenerator(IGenerator generator) {
        generators.put(generator.getHref().toLowerCase(), generator);
    }
    private String htmlRepList;

    public String getHtmlRepList() {
        return htmlRepList;
    }
    private String htmlUsersList;

    public String getHtmlUsersList() {
        return htmlUsersList;
    }
    /**
     * Список паролей пользователей имя - пароль
     */
    private HashMap<String, String> passMap;

    public boolean isTrueUser(String userName, String pwd) {
        return pwd.equals(passMap.get(userName));
    }

    /**
     * Генерация отчета по его имени.
     *
     * @param request запрос пришедший от клиента
     * @return Отчет в виде массива байт.
     */
    public synchronized Response generate(HttpRequest request) {
        final long start = System.currentTimeMillis();
        String url = NetUtil.getUrl(request);
        final String nameReport = url.lastIndexOf(".") == -1 ? url.substring(1) : url.substring(1, url.lastIndexOf("."));

        final IGenerator generator = generators.get(nameReport.toLowerCase());
        // если нет такого отчета
        if (generator == null) {
            return null;
        }
        // Значит такой отчет есть и его можно сгенерировать
        // но если запрошен отчет, то должны приехать пароль и пользователь в куках
        // для определения доступа к отчетам.
        // Cookie: username=%D0%90%D0%B4%D0%BC%D0%B8%D0%BD%D0%B8%D1%81%D1%82%D1%80%D0%B0%D1%82%D0%BE%D1%80; password=
        // Проверим правильность доступа, и если все нормально сгенерируем отчет.
        // Иначе выдадим страничку запрета доступа
        // Но есть нюанс, формирование списка отчетов - тоже формироватор, и к нему доступ не по кукисам,
        // а по введеному паролю и пользователю. По этому надо проверить если приехали параметры пароля и пользователя,
        // введенные юзером, то игнорировать проверку кукисов. Т.е. если гениратор reportList, то не проверяем кукисы
        if (!"/reportList.html".equals(url)) {

            if (request.getFirstHeader("Cookie") == null) {
                // если куков нет
                return getLoginPage();
            }
            final HashMap<String, String> cookie = NetUtil.getCookie(request.getFirstHeader("Cookie").getValue(), "; ");
            final String pass = cookie.get("password");
            final String usr = cookie.get("username");
            if (pass == null || usr == null) {
                // если не нашлось в куках
                return getLoginPage();
            }
            if (!isTrueUser(usr, pass)) {
                // если не совпали пароли
                return getLoginPage();
            }
        }
        System.out.println("Report build: '" + nameReport + "'\n");
        QLog.l().logRep().info("Генерация отчета: '" + nameReport + "'");
        /*
         * Вот сама генерация отчета.
         */
        final Response result = generator.process(request);

        QLog.l().logRep().info("Генерация завершено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.");
        return result;
    }

    public synchronized byte[] generate(QUser user, String uri, HashMap<String, String> params) {
        final HttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", uri);
        r.addHeader("Cookie", "username=" + user.getName() + "; password=" + user.getPassword());
        final StringBuilder sb = new StringBuilder();
        params.keySet().stream().forEach((st) -> {
            sb.append("&").append(st).append("=").append(params.get(st));
        });
        final InputStream is = new ByteArrayInputStream(sb.substring(1).getBytes());
        final BasicHttpEntity b = new BasicHttpEntity();
        b.setContent(is);
        r.setEntity(b);
        sb.setLength(0);
        return generate(r).getData();
    }

    /**
     * Загрузим страничку ввода пароля и пользователя
     *
     * @return страница в виде массива байт.
     */
    private Response getLoginPage() {
        byte[] result = null;
        // Выдаем ресурс  "/ru/apertum/qsystem/reports/web/"
        final InputStream inStream = getClass().getResourceAsStream("/ru/apertum/qsystem/reports/web/login.html");
        if (inStream != null) {
            try {
                result = Uses.readInputStream(inStream);
            } catch (IOException ex) {
                throw new ReportException("Ошибка чтения ресурса логирования. " + ex);
            }
        } else {
            final String s = "<html><head><meta http-equiv = \"Content-Type\" content = \"text/html; charset=windows-1251\" ></head><p align=center>Ресурс для входа не найден.</p></html>";
            return new Response(s.getBytes());
        }
        Response res = null;
        try {
            res = new Response(RepResBundle.getInstance().prepareString(new String(result, "UTF-8")).
                    replaceFirst(Uses.ANCHOR_USERS_FOR_REPORT, getHtmlUsersList()).
                    replaceFirst(Uses.ANCHOR_PROJECT_NAME_FOR_REPORT, Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF())).
                    getBytes("UTF-8")); //"Cp1251"
        } catch (UnsupportedEncodingException ex) {
        }
        return res;
    }

    private boolean checkLogin(HttpRequest request) {
        boolean res = false;
        // в запросе должен быть пароль и пользователь, если нету, то отказ на вход
        String entityContent = NetUtil.getEntityContent(request);
        QLog.l().logger().trace("Принятые параметры \"" + entityContent + "\".");
        // ресурс для выдачи в браузер. это либо список отчетов при корректном логининге или отказ на вход
        // разбирем параметры
        final HashMap<String, String> cookie = NetUtil.getCookie(entityContent, "&");
        if (cookie.containsKey("username") && cookie.containsKey("password")) {
            if (isTrueUser(cookie.get("username"), cookie.get("password"))) {
                res = true;
            }
        }
        return res;
    }
}
