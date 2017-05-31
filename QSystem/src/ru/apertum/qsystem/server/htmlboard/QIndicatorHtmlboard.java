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
package ru.apertum.qsystem.server.htmlboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.client.forms.FParamsEditor;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.QServer;
import ru.apertum.qsystem.server.controller.IIndicatorBoard;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

/**
 * Вывод информации на мониторы. Класс-менеджер вывода информации на общее табло в виде монитора.
 *
 * @author Evgeniy Egorov
 */
public class QIndicatorHtmlboard implements IIndicatorBoard {

    protected FHtmlBoard indicatorBoard = null;
    protected String configFile;

    final public String getConfigFile() {
        return configFile;
    }

    final public void setConfigFile(String configFile) {
        final String err = ("/".equals(File.separator)) ? "\\" : "/";
        while (configFile.contains(err)) {
            configFile = configFile.replace(err, File.separator);
        }
        this.configFile = configFile;
    }

    private String template;
    static public final String CONTENT_FILE_PATH = "config//html_main_board//content.html";

    /**
     * Замена якорей на реальные значения
     *
     * @param cnt Это шаблон
     * @return шаблон с замененными реальными значениями
     */
    private String prepareContent(String cnt) {
        String s = cnt; // - Это будет готовый ответ со всеми заменами

        //****************************************************************************************************************************************************
        //****************************************************************************************************************************************************
        // Для начала отрисуем ближайших по якорям {{1}}{{2}}{{3}}...{{N}}:
        // Построем всех ближайших
        final LinkedList<String> nexts = new LinkedList<>(); // Это все ближайшие по порядку
        final PriorityQueue<QCustomer> customers = new PriorityQueue<>();
        QServiceTree.getInstance().getNodes().stream().filter((service) -> (service.isLeaf())).forEach((service) -> {
            service.getClients().stream().forEach((qCustomer) -> {
                customers.add(qCustomer);
            });
        });
        QCustomer qCust = customers.poll();
        while (qCust != null) {
            nexts.add(qCust.getFullNumber());
            qCust = customers.poll();
        }
        // Теперь ближайшие в списке, произведем замены по якорям {{1}}{{2}}{{3}}...{{N}}.
        int posNext = 1;
        for (String next : nexts) {
            s = s.replaceAll("\\{\\{" + posNext + "\\}\\}", next);
            posNext++;
        }
        // Затрем оставшиеся места с шаблонами для ближайших
        s = s.replaceAll("\\{\\{\\d+\\}\\}", "");

        //*****************************************************************************************************************************************************
        //*****************************************************************************************************************************************************
        // Теперь таблица вызванных {1|blink} {1|N} {1|point}.
        // Нужно найти всех вызванных и обрабатываемых, посмотреть их статус и заменить данные и id для мигания вызванных
        final LinkedList<QCustomer> onBosrd = new LinkedList<>();
        for (QUser user : QUserList.getInstance().getItems()) {
            if (user.getCustomer() != null) {
                int pos = 0;
                for (QCustomer qCustomer : onBosrd) {
                    if (qCustomer.getCallTime().before(user.getCustomer().getCallTime())) {
                        break;
                    }
                    pos++;
                }
                onBosrd.add(pos, user.getCustomer());
            }
        }
        // Заменяем строки вызванных
        int posOnBoard = 1;
        for (QCustomer bCust : onBosrd) {
            s = s.replaceAll("\\{" + posOnBoard + "\\|N\\}", bCust.getFullNumber());
            s = s.replaceAll("\\{" + posOnBoard + "\\|point\\}", bCust.getUser().getPoint());
            s = s.replaceAll("\\{" + posOnBoard + "\\|ext\\}", bCust.getUser().getPointExt());
            if (bCust.getState().equals(CustomerState.STATE_INVITED) || bCust.getState().equals(CustomerState.STATE_INVITED_SECONDARY)) {
                s = s.replaceAll("\\{" + posOnBoard + "\\|blink\\}", "blinkR_" + posOnBoard);
            }
            posOnBoard++;
        }
        // Затрем оставшиеся места с шаблонами для ближайших
        s = s.replaceAll("\\{\\d+\\|(N|point|blink|ext)\\}", "");

        //*****************************************************************************************************************************************************
        //*****************************************************************************************************************************************************
        // Теперь заменим стоящих к операторам
        final ArrayList<String> invList = new ArrayList<>();
        int i = 0;
        for (QUser user : QUserList.getInstance().getItems()) {
            final String id = HtmlBoardProps.getInstance().getId(user.getPoint());
            if (user.getCustomer() != null) {
                System.out.println(user.getCustomer().getState());
                if (user.getCustomer().getState() == CustomerState.STATE_INVITED || user.getCustomer().getState() == CustomerState.STATE_INVITED_SECONDARY) {
                    invList.add(id);
                    String string = "\\[" + id + "\\|blink\\]";
                    s = s.replaceAll(string, "blink_" + i++);

                    string = "\\[" + id + "\\|1\\]";
                    s = s.replaceAll(string, user.getCustomer().getFullNumber());
                }
                String string = "\\[" + id + "\\|name\\]";
                s = s.replaceAll(string, user.getCustomer().getService().getName());

                string = "\\[" + id + "\\|discription\\]";
                s = s.replaceAll(string, user.getCustomer().getService().getDescription());

                string = "\\[" + id + "\\|user\\]";
                s = s.replaceAll(string, user.getName());

                string = "\\[" + id + "\\|ext\\]";
                s = s.replaceAll(string, user.getPointExt());
            }
            String string = "\\[" + id + "\\|point\\]";
            s = s.replaceAll(string, user.getPoint());
        }
        s = s.replaceAll("\\[\\d+\\|(name|discription|point|blink|ext|user)\\]", "");

        ArrayList<String> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("\\[\\d+\\|\\d+\\]").matcher(s);
        while (m.find()) {
            allMatches.add(m.group());
        }
        for (String string : allMatches) {
            final String id = string.substring(1, string.indexOf("|"));
            int pos = Integer.parseInt(string.substring(string.indexOf("|") + 1, string.length() - 1)) - (invList.contains(id) ? 1 : 0);
            final String adr = HtmlBoardProps.getInstance().getAddr(id);
            QUser usr = null;
            for (QUser user : QUserList.getInstance().getItems()) {
                if (user.getPoint().equalsIgnoreCase(adr)) {
                    if (usr == null) {
                        usr = user;
                    } else if (user.getShadow() != null) {
                        usr = user;
                    }
                }
            }
            if (usr == null) {
                s = s.replaceAll(string.replace("[", "\\[").replace("]", "\\]").replace("|", "\\|"), "");
            } else {
                final QService ss = new QService();
                for (QPlanService pser : usr.getPlanServices()) {
                    QService ser = QServiceTree.getInstance().getById(pser.getService().getId());
                    ser.getClients().stream().forEach((c) -> {
                        ss.addCustomer(c);
                    });
                }
                // для получения правильной очередности хвоста
                final PriorityQueue<QCustomer> custs = new PriorityQueue<>();
                final LinkedList<QCustomer> qeue = new LinkedList<>();
                ss.getClients().stream().forEach((qCustomer) -> {
                    custs.offer(qCustomer);
                });
                while (custs.size() > 0) {
                    qeue.add(custs.poll());
                }
                // замена
                s = s.replaceAll(string.replace("[", "\\[").replace("]", "\\]").replace("|", "\\|"), ss.getClients().size() >= pos ? qeue.get(pos - 1).getFullNumber() : "");
            }
        }
        System.out.println("===========================================================");
        System.out.println(s);
        System.out.println("************************************************************");
        return s;
    }

    /**
     * Создадим форму, спозиционируем, сконфигурируем и покажем
     *
     */
    protected void initIndicatorBoard() {
        final File conff = new File(getConfigFile());
        if (conff.exists()) {
            template = "";
            try (FileInputStream fis = new FileInputStream(conff); Scanner s = new Scanner(new InputStreamReader(fis, "UTF-8"))) {
                while (s.hasNextLine()) {
                    final String line = s.nextLine().trim();
                    template += line;
                }
            } catch (IOException ex) {
                System.err.println(ex);
                throw new RuntimeException(ex);
            }

        } else {
            throw new ServerException("Не найден " + getConfigFile(), new FileNotFoundException(getConfigFile()));
        }

        if (indicatorBoard == null) {
            indicatorBoard = new FHtmlBoard();
            if (indicatorBoard == null) {
                QLog.l().logger().warn("Табло не демонстрируется. Отключено в настройках.");
                return;
            }
            try {
                indicatorBoard.setIconImage(ImageIO.read(QServer.class.getResource("/ru/apertum/qsystem/client/forms/resources/recent.png")));
            } catch (IOException ex) {
                System.err.println(ex);
            }
            // Определим форму нв монитор
            indicatorBoard.toPosition(QConfig.cfg().isDebug(), 20, 20);

            indicatorBoard.loadContent(prepareContent(template));

            java.awt.EventQueue.invokeLater(() -> {
                indicatorBoard.setVisible(true);
            });
        } else {
            indicatorBoard.loadContent(prepareContent(template));
        }
    }

    public QIndicatorHtmlboard() {
        setConfigFile(CONTENT_FILE_PATH);
        QLog.l().logger().info("Создание HTML табло для телевизоров или мониторов. Шаблон табло в \"" + getConfigFile() + "\"");
    }

    public QIndicatorHtmlboard(String fileProps) {
        setConfigFile(fileProps);
        QLog.l().logger().info("Создание HTML табло для телевизоров или мониторов. Шаблон табло в \"" + getConfigFile() + "\"");
    }

    @Override
    public Element getConfig() {
        String tr = "<Параметры>\n"
                + "   <Параметер Наименование=\"top.size\" Тип=\"1\" Значение=\"" + HtmlBoardProps.getInstance().topSize + "\"/>\n"
                + "   <Параметер Наименование=\"top.url\" Тип=\"3\" Значение=\"" + HtmlBoardProps.getInstance().topUrl + "\"/>\n"
                + "   <Параметер Наименование=\"left.size\" Тип=\"1\" Значение=\"" + HtmlBoardProps.getInstance().leftSize + "\"/>\n"
                + "   <Параметер Наименование=\"left.url\" Тип=\"3\" Значение=\"" + HtmlBoardProps.getInstance().leftUrl + "\"/>\n"
                + "   <Параметер Наименование=\"right.size\" Тип=\"1\" Значение=\"" + HtmlBoardProps.getInstance().rightSize + "\"/>\n"
                + "   <Параметер Наименование=\"right.url\" Тип=\"3\" Значение=\"" + HtmlBoardProps.getInstance().rightUrl + "\"/>\n"
                + "   <Параметер Наименование=\"bottom.size\" Тип=\"1\" Значение=\"" + HtmlBoardProps.getInstance().bottomSize + "\"/>\n"
                + "   <Параметер Наименование=\"bottom.url\" Тип=\"3\" Значение=\"" + HtmlBoardProps.getInstance().bottomUrl + "\"/>\n"
                + "   <Параметер Наименование=\"need_reload\" Тип=\"4\" Значение=\"" + (HtmlBoardProps.getInstance().needReload ? "1" : "0") + "\"/>\n";

        for (String key : HtmlBoardProps.getInstance().getAddrs().keySet()) {
            tr = tr + "   <Параметер Наименование=\"" + key + "\" Тип=\"3\" Значение=\"" + HtmlBoardProps.getInstance().getAddrs().get(key) + "\" " + Uses.TAG_BOARD_READ_ONLY + "=\"true\"/>\n";
        }

        tr = tr + "</Параметры>";
        final Document document;
        try {
            document = DocumentHelper.parseText(tr);
        } catch (DocumentException ex) {
            throw new ServerException(ex);
        }
        return document.getRootElement();
    }

    @Override
    public void saveConfig(Element element) {

        final List<Element> elist = element.elements("Параметер");

        elist.forEach(elem -> {
            switch (elem.attributeValue("Наименование")) {
                case "top.size":
                    HtmlBoardProps.getInstance().topSize = Integer.parseInt(elem.attributeValue("Значение"));
                    break;
                case "top.url":
                    HtmlBoardProps.getInstance().topUrl = elem.attributeValue("Значение");
                    break;
                case "left.size":
                    HtmlBoardProps.getInstance().leftSize = Integer.parseInt(elem.attributeValue("Значение"));
                    break;
                case "left.url":
                    HtmlBoardProps.getInstance().leftUrl = elem.attributeValue("Значение");
                    break;
                case "right.size":
                    HtmlBoardProps.getInstance().rightSize = Integer.parseInt(elem.attributeValue("Значение"));
                    break;
                case "right.url":
                    HtmlBoardProps.getInstance().rightUrl = elem.attributeValue("Значение");
                    break;
                case "bottom.size":
                    HtmlBoardProps.getInstance().bottomSize = Integer.parseInt(elem.attributeValue("Значение"));
                    break;
                case "bottom.url":
                    HtmlBoardProps.getInstance().bottomUrl = elem.attributeValue("Значение");
                    break;
                case "need_reload":
                    HtmlBoardProps.getInstance().needReload = "1".equals(element.attributeValue("Значение"));
                    break;
            }
            HtmlBoardProps.getInstance().saveProps();

        });
    }

    @Override
    public AFBoardRedactor getRedactor() {
        if (boardConfig == null) {
            boardConfig = FParamsEditor.getParamsEditor(null, false);
        }
        return boardConfig;
    }
    /**
     * Используемая ссылка на диалоговое окно. Singleton
     */
    private static FParamsEditor boardConfig;

    @Override
    public void showBoard() {
        QLog.l().logger().trace("Показываем HTML табло");
        initIndicatorBoard();
        QLog.l().logger().trace("HTML табло должно уже показаться.");
    }

    /**
     * Выключить информационное табло.
     */
    @Override
    public synchronized void close() {
        QLog.l().logger().trace("Закрываем HTML табло");
        if (indicatorBoard != null) {
            indicatorBoard.setVisible(false);
            indicatorBoard = null;
        }
    }

    @Override
    public void refresh() {
        QLog.l().logger().trace("Обновляем HTML табло");
        close();
        indicatorBoard = null;
        initIndicatorBoard();
    }

    @Override
    public void clear() {

    }

    @Override
    public String getDescription() {
        return "Плагин табло со стационарными позициями.";
    }

    @Override
    public long getUID() {
        return 2;
    }

    @Override
    public Object getBoardForm() {
        return indicatorBoard;
    }

    /**
     * Переопределено что бы вызвать появление таблички с номером вызванного поверх главного табло
     *
     * @param user
     * @param customer
     */
    @Override
    public synchronized void inviteCustomer(QUser user, QCustomer customer) {
        QLog.l().logger().trace("Приглшием кастомера на BS табло");
        if (indicatorBoard != null) {
            if (HtmlBoardProps.getInstance().isNeedReload()) {
                indicatorBoard.loadContent(prepareContent(template));
            }
            indicatorBoard.getBfx().executeJavascript("inviteCustomer(" + makeParam(user, customer) + ")");
        }
    }

    @Override
    public void workCustomer(QUser user) {
        QLog.l().logger().trace("Работа с кастомером на BS табло");
        if (indicatorBoard != null) {
            if (HtmlBoardProps.getInstance().isNeedReload()) {
                indicatorBoard.loadContent(prepareContent(template));
            }
            indicatorBoard.getBfx().executeJavascript("workCustomer(" + makeParam(user, user.getCustomer()) + ")");
        }
    }

    @Override
    public void killCustomer(QUser user) {
        QLog.l().logger().trace("Убираем кастомера на BS табло");
        if (indicatorBoard != null) {
            if (HtmlBoardProps.getInstance().isNeedReload()) {
                indicatorBoard.loadContent(prepareContent(template));
            }
            indicatorBoard.getBfx().executeJavascript("killCustomer(" + makeParam(user, user.getCustomer() == null ? user.getShadow().getOldCustomer() : user.getCustomer()) + ")");
        }
    }

    /**
     * {"user":{"name":"Ivanov", "point":"222", "ext":"<b>ext field</b>"}, "servece":{"name":"Spravka", prefix:"A", "description":"Long horn"},
     * "customer":{prefix:"A", "number":"159", "data":"null"}}
     *
     * @param user
     * @param customer
     * @return
     */
    private String makeParam(QUser user, QCustomer customer) {
        //{"user":{"name":"Ivanov", "point":"222", "ext":"<b>ext field</b>"}, "servece":{"name":"Spravka", prefix:"A", "description":"Long horn"}, "customer":{prefix:"A", "number":"159", "data":"null"}} ); //To change body of generated methods, choose Tools | Templates.
        return "{\"user\":{\"name\":\"" + user.getName() + "\", \"point\":\"" + user.getPoint() + "\", \"ext\":\"" + user.getPointExt() + "\"}, "
                + "\"servece\":{\"name\":\"" + customer.getService().getName() + "\", prefix:\"" + customer.getService().getPrefix() + "\", \"description\":\"" + customer.getService().getDescription() + "\"},"
                + " \"customer\":{prefix:\"" + customer.getPrefix() + "\", \"number\":\"" + customer.getNumber() + "\", \"data\":\"" + customer.getInput_data() + "\"}}";
    }

}
