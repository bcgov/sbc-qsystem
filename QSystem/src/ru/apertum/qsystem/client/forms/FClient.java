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
package ru.apertum.qsystem.client.forms;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ServiceLoader;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import org.dom4j.DocumentException;
import org.jdesktop.application.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.dom4j.Element;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.INetProperty;
import org.dom4j.io.SAXReader;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.QProperties;
import ru.apertum.qsystem.client.common.ClientNetProperty;
import ru.apertum.qsystem.client.help.Helper;
import ru.apertum.qsystem.client.model.ParallelCellRenderer;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.client.model.QTray;
import ru.apertum.qsystem.client.model.QTray.MessageType;
import ru.apertum.qsystem.common.AUDPServer;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation.SelfService;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation.SelfSituation;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IStartClient;
import ru.apertum.qsystem.fx.OrangeClientboard;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;

/**
 * Created on 11 Сентябрь 2008 г., 16:57
 *
 * @author Evgeniy Egorov
 */
public final class FClient extends javax.swing.JFrame {

    /**
     * Информация для взаимодействия по сети. Формируется по данным из командной строки.
     */
    private final INetProperty netProperty;

    public INetProperty getNetProperty() {
        return netProperty;
    }
    /**
     * Системный трей.
     */
    private final QTray tray;
    /**
     * Кастомер, с которым работает юзер.
     */
    private QCustomer customer = null;

    /**
     * Устанавливаем кастомера для работы. Не может быть NULL
     *
     * @param customer С ним работаем. Не может быть NULL.
     */
    public void setCustomer(QCustomer customer) {
        this.customer = customer;
        if (customer == null) {
            labelNextNumber.setText("");
            textAreaComments.setText("");
            return;
        }
        QLog.l().logger().trace("Установливаем кастомера работающему клиенту и выводем его.");
        // выведем на экран некую инфу о приглашенном кастомере
        final String textCust = customer.getFullNumber();
        // Выведем номер вызванного.
        printCustomerNumber(customer.getPrefix(), customer.getNumber(), 0);

        final String priority;
        switch (customer.getPriority().get()) {
            case 0: {
                priority = getLocaleMessage("messages.priority.low");
                break;
            }
            case 1: {
                priority = getLocaleMessage("messages.priority.standart");
                break;
            }
            case 2: {
                priority = getLocaleMessage("messages.priority.hi");
                break;
            }
            case 3: {
                priority = getLocaleMessage("messages.priority.vip");
                break;
            }
            default: {
                priority = getLocaleMessage("messages.priority.strange");
            }
        }
        String s = customer.getService().getInput_caption().replaceAll("<[^>]*>", "");
        if (s == null) {
            s = "";
        } else {
            s = "<u>" + s + "</u><br>" + customer.getInput_data();
        }
        labelNextNumber.setContentType("text/html");
        labelNextNumber.setText("");
        labelNextNumber.setText("<html>"
                + "<div style='text-align: center;'><span style='font-size:32.0pt;color:purple;'>" + textCust + "</span>"
                + "<span style='font-size:14.0pt;color:gray'> " + priority + "</span>"
                + "</div>"
                + "<div style='margin: 0px 0px 0px 2px'>"
                + "<span style='font-size:14.0pt;color:black'> " + getLocaleMessage("messages.service") + ": " + customer.getService().getName() + "</span><br>"
                + "<span style='font-size:14.0pt;color:gray'> " + s + "</span>"
                + "</div>");
        textAreaComments.setText(customer.getTempComments());
        textAreaComments.setCaretPosition(0);
        // прикроем кнопки, которые недоступны на этом этапе работы с кастомером.
        // тут в зависимости от состояния кастомера открываем разные наборы кнопок
        switch (customer.getState()) {
            case STATE_INVITED: {
                setBlinkBoard(true);
                setKeyRegim(KEYS_INVITED);
                break;
            }
            case STATE_INVITED_SECONDARY: {
                setBlinkBoard(true);
                setKeyRegim(KEYS_INVITED);
                break;
            }
            case STATE_WORK: {
                setBlinkBoard(false);
                setKeyRegim(KEYS_STARTED);
                buttonFinish.setEnabled(customer.getService().getEnable() == 1);
                break;
            }
            case STATE_WORK_SECONDARY: {
                setBlinkBoard(false);
                setKeyRegim(KEYS_STARTED);
                buttonFinish.setEnabled(customer.getService().getEnable() == 1);
                break;
            }
            default: {
                throw new ClientException("Не известное состояние клиента \"" + customer.getState() + "\" для данного случая.");
            }
        }
        //нефиг счелкать касторами пока процесс вызывания идет при параллельном приеме
        if (user.getParallelAccess()) {
            listParallelClients.setEnabled(CustomerState.STATE_WORK.equals(customer.getState()) || CustomerState.STATE_WORK_SECONDARY.equals(customer.getState()));
        }
        // ну и разрешим параллельный вызов если есть доступ и есть кого вызывать. 
        // И если нет процесса вызывания, этот процесс должен быть завершен и не остался висеть в параллели
        if (user.getParallelAccess() && (CustomerState.STATE_WORK.equals(customer.getState()) || CustomerState.STATE_WORK_SECONDARY.equals(customer.getState()))) {
            buttonInvite.setEnabled(customersCountForUser != 0);
            menuItemInvitePostponed.setEnabled(listPostponed.getModel().getSize() != 0); // тут тоже надо, для парралельных
        }
    }

    /**
     * Заставляем мигать все табло.
     *
     * @param blinked мигаем или нет.
     */
    private void setBlinkBoard(boolean blinked) {
        if (indicatorBoard != null) {
            indicatorBoard.printRecord(0, customer.getPrefix(), customer.getNumber(), "", "", blinked ? 0 : -1);
        }
        if (clientboardFX) {
            //todo   board.showData(customer.getPrefix() + customer.getNumber(), blinked);
        }
    }

    public QCustomer getCustomer() {
        return customer;
    }

    /**
     * Обозначим результат работы с клиентом если требуется
     */
    private Long setResult() {
        Long rs = (long) -1;
        if (customer.getService().getResult_required()) {
            getResults();
            Object res = JOptionPane.showInputDialog(this, getLocaleMessage("resultwork.dialog.caption"), getLocaleMessage("resultwork.dialog.title"), JOptionPane.QUESTION_MESSAGE, null, getResults(), null);
            rs = res == null ? null : results.get((String) res);
        }
        return rs;
    }
    private final LinkedHashMap<String, Long> results = new LinkedHashMap<>();

    private Object[] getResults() {
        if (results.isEmpty()) {
            NetCommander.getResultsList(netProperty).stream().forEach((result) -> {
                results.put(result.getName(), result.getId());
            });
        }
        return results.keySet().toArray();
    }

    /**
     * UDP Сервер. Обнаруживает изменение состояния очередей.
     */
    protected final class UDPServer extends AUDPServer {

        public UDPServer(int port) {
            super(port);
        }
        int i = 0;
        Timer t;
        String pref = "";

        public void start2() {
            new Thread(() -> {
                start();
            }).start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
            if (!isActivate() && QConfig.cfg().isTerminal()) { // порт не занялся, т.к. в терминальном режиме, то нужно подсасывать мессаги из файла
                QLog.l().logger().trace("Старт PIPE.");
                if (!new File(Uses.TEMP_FOLDER + File.separator + "pipe").exists()) {
                    new File(Uses.TEMP_FOLDER + File.separator).mkdir();
                    try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(Uses.TEMP_FOLDER + File.separator + "pipe")), true)) {
                        w.println("" + new Date().getTime() + "^");
                    } catch (IOException ex) {
                        throw new ServerException(ex);
                    }
                }

                i = 0;
                if (t != null) {
                    t.stop();
                }
                try (RandomAccessFile raf = new RandomAccessFile(Uses.TEMP_FOLDER + File.separator + "pipe", "r")) {

                    final RandomAccessFile raf2 = raf;
                    t = new Timer(1000, (ActionEvent e) -> {
                        i++;
                        if (raf2 != null) {
                            try {
                                raf2.seek(0);
                                final String s = raf2.readLine();
                                final String[] ss = s.split("\\^");
                                if (ss.length == 2 && !pref.equals(ss[0])) {
                                    System.out.println(ss[1]);
                                    pref = ss[0];
                                    getData2(ss[1], null, 0);
                                }
                            } catch (IOException ex) {
                                QLog.l().logger().error("Не прочитался pipe. ", ex);
                            } finally {
                                raf2.close();
                            }
                        }
                        if (i > 180) {
                            i = 0;
                            checkPort();
                        }
                    });
                    t.start();
                } catch (FileNotFoundException ex) {
                    QLog.l().logger().error("Не открылся pipe. ", ex);
                } catch (IOException ex) {
                    QLog.l().logger().error("Error: ", ex);
                }
            }
        }

        private void checkPort() {
            if (t != null) {
                t.stop();
            }
            start2();
        }

        @Override
        synchronized protected void getData(String data, InetAddress clientAddress, int clientPort) {
            if (QConfig.cfg().isTerminal()) {
                if (!new File(Uses.TEMP_FOLDER + File.separator + "pipe").exists()) {
                    new File(Uses.TEMP_FOLDER + File.separator).mkdir();
                    try (final PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(Uses.TEMP_FOLDER + File.separator + "pipe")), true)) {
                        w.println("" + new Date().getTime() + "^");
                        w.close();
                    } catch (IOException ex) {
                        QLog.l().logger().error("Не записался пакет UDP в pipe. ", ex);
                    }
                }
                try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(Uses.TEMP_FOLDER + File.separator + "pipe")), true)) {
                    w.println("" + new Date().getTime() + "^" + data);
                    w.close();
                } catch (IOException ex) {
                    QLog.l().logger().error("Не записался пакет UDP в pipe. ", ex);
                }
            }
            getData2(data, clientAddress, clientPort);
        }

        protected void getData2(String data, InetAddress clientAddress, int clientPort) {
            //Определяем, по нашей ли услуге пришел кастомер
            boolean my = false;
            for (SelfService srv : getUserPlan().getSelfservices()) {
                if (String.valueOf(srv.getId()).equals(data)) {
                    my = true;
                }
            }
            //Если кастомер встал в очередь, обрабатываемую этим юзером, то апдейтим состояние очередей.
            if (my || user.getId().toString().equals(data)) {
                //Получаем состояние очередей для юзера
                refreshSituation();
                return;
            }
            if (Uses.TASK_REFRESH_POSTPONED_POOL.equals(data)) {
                //Получаем состояние пула отложенных
                LinkedList<QCustomer> custs = NetCommander.getPostponedPoolInfo(netProperty);
                LinkedList<QCustomer> rem = new LinkedList<>();
                custs.stream().filter((cust) -> (cust.getIsMine() != null && !cust.getIsMine().equals(user.getId()))).forEach((cust) -> {
                    rem.add(cust);
                });
                custs.removeAll(rem);
                listPostponed.setModel(QPostponedList.getInstance().loadPostponedList(custs));
                if (listPostponed.getModel().getSize() != 0) {
                    listPostponed.setSelectedIndex(0);
                }
                return;
            }
            if (Uses.HOW_DO_YOU_DO.equals(data)) {
                //Отправим по TCP/IP
                // NetCommander.setLive(netProperty, user.getId()); // заборонено. гы-гы. теперь жить будем по новому, даздравствует Новороссия!
            }
            if (data.startsWith("message#") && (data.startsWith("message#ALL##") || isMyMessage(data))) {
                final String mess = data.substring(data.indexOf("##") + 2);
                tray.showMessageTray(getLocaleMessage("messages.tray.information"), mess, MessageType.INFO);

                labelMessage.setText(labelMessage.getText() + "<b><span style='color:black'>" + Uses.FORMAT_HH_MM.format(new Date()) + " " + getLocaleMessage("messages.tray.message") + ":</span></b><br><span style='color:blue'>" + mess.replaceAll("\n", "<br>") + "</span><br>");
            }
        }

        private boolean isMyMessage(String txt) {
            final String adr = txt.substring(0, txt.indexOf("##"));
            if (adr.contains("@" + user.getId() + "@")) {
                return true;
            }
            return getUserPlan().getSelfservices().stream().anyMatch((srv) -> (adr.contains("@" + srv.getId() + "@")));
        }
    };
    private final UDPServer udpServer;
    /**
     * Описание того, кто залогинелся.
     */
    private final QUser user;

    public QUser getUser() {
        return user;
    }
    /**
     * Описание того, сколько народу стоит в очередях к этому юзеру, ну и прочее(потом)mess Не использовать на прямую.
     *
     * see setSituation(Element plan)
     */
    private SelfSituation userPlan;
    /**
     * А сколько юзеров стоит в очереди к нам
     */
    private int customersCountForUser = 0;

    public SelfSituation getUserPlan() {
        return userPlan;
    }

    private static ResourceMap localeMap = null;

    public static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FClient.class);
        }
        return localeMap.getString(key);
    }

    public static String loc(String key) {
        return getLocaleMessage(key);
    }

    /**
     * Creates new form FClient
     *
     * @param user
     * @param netProperty
     * @throws AWTException
     */
    public FClient(QUser user, final IClientNetProperty netProperty) throws AWTException {
        QLog.l().logQUser().debug("FClient");
        this.user = user;
        this.netProperty = netProperty;
        // инициализим trayIcon, т.к. setSituation() требует работу с tray
        final JFrame fr = this;
        tray = QTray.getInstance(fr, "/ru/apertum/qsystem/client/forms/resources/client.png", getLocaleMessage("messages.tray.hint"));
        tray.addItem(getLocaleMessage("messages.tray.showClient"), (ActionEvent e) -> {
            setVisible(true);
            setState(JFrame.NORMAL);
        });
        tray.addItem("-", (ActionEvent e) -> {
        });
        tray.addItem(getLocaleMessage("messages.tray.close"), (ActionEvent e) -> {
            dispose();
            System.exit(0);
        });

        init(user, netProperty);

        // стартуем UDP сервер для обнаружения изменения состояния очередей
        udpServer = new UDPServer(netProperty.getClientPort());
        udpServer.start2();
    }

    private void init(QUser user, final IClientNetProperty netProperty) {
        QLog.l().logQUser().debug("init");
        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // остановим UDP сервер
                udpServer.stop();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        initComponents();
        btnPushToTalk.setVisible(false);
        setTitle(Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF()) + " - " + getTitle());

        try {
            setIconImage(ImageIO.read(FAdmin.class.getResource("/ru/apertum/qsystem/client/forms/resources/client.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }

        // Еслми нет разрешения вести параллельный прием, то скрыть панель вызванных
        if (!user.getParallelAccess()) {
            setSize(getPreferredSize().width - panelParallerCusts.getPreferredSize().width, getPreferredSize().height);
            panelParallerCusts.setVisible(user.getParallelAccess());
        } else {

            listParallelClients.setCellRenderer(new ParallelCellRenderer());
            /*
            LinkedList<QCustomer> cs = new LinkedList<>();
            for (int i = 131; i < 148; i++) {
                QCustomer c = new QCustomer(i);
                c.setPrefix("F");
                c.setPriority(1);
                cs.add(c);
            }
            listParallelClients.setModel(new DefaultComboBoxModel<>(cs.toArray(new QCustomer[cs.size()])));
             */
            listParallelClients.addListSelectionListener((ListSelectionEvent e) -> {
                final QCustomer customer1 = listParallelClients.getSelectedValue();
                if (customer1 != null && !e.getValueIsAdjusting() && (customer == null || !customer1.getId().equals(customer.getId()))) {
                    setCustomer(customer1);
                }
            });

        }

        // отрехтуем дизайн формы.
        //panelBottom.setVisible(false);
        jPanel4.setVisible(false);
        //menuBar.setVisible(false);
        labelNextNumber.setContentType("text/html");
        labelNextNumber.setText("<html><br><p align=center><span style='font-size:24.0pt;color:black'>" + getLocaleMessage("messages.noCall") + "</span></p>");
        printCustomerNumber("", null, -1);
        // Фича. По нажатию Escape закрываем форму
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
            setVisible(false);
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        labelUser.setText(user.getName() + " - " + (QConfig.cfg().getPointN() != null ? QConfig.cfg().getPointN() : user.getPoint()));
        ch.setSelected(user.isPause());
        refreshSituation();

        int ii = 1;
        final ButtonGroup bg = new ButtonGroup();
        final String currLng = Locales.getInstance().getLangCurrName();
        for (String lng : Locales.getInstance().getAvailableLocales()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FClient.class, this).get("setCurrentLang"));
            bg.add(item);
            item.setSelected(lng.equals(currLng));
            item.setText(lng); // NOI18N
            item.setName("RRadioButtonMenuItem" + (ii++)); // NOI18N
            menuLangs.add(item);
        }

        //привязка помощи к форме.
        final Helper helper = Helper.getHelp("ru/apertum/qsystem/client/help/client.hs");
        helper.setHelpListener(menuItemHelp);
        helper.enableHelpKey(panelDown, "client");

        ch.setOpaque(false);
        ch.addActionListener((ActionEvent e) -> {
            final boolean state1 = NetCommander.setBussy(netProperty, getUser().getId(), ch.isSelected());
            ch.setSelected(state1);
            if (state1) {
                ch.setForeground(new Color(250, 0, 0));
                ch.setText(getLocaleMessage("client.pause").toUpperCase());
            } else {
                ch.setForeground(new Color(0, 150, 0));
                ch.setText(getLocaleMessage("client.pause"));
            }
        });
        menuBar.add(ch);

        Font f = new Font("Time New Roman", 0, 16);
        labelMotiv.setFont(f);
        labelMotiv.setForeground(new Color(0, 150, 0));
        menuBar.add(labelMotiv);

        final Timer t = new Timer(1000, new TimerMotiv());
        t.start();

        menuItemId.setText("ID=" + user.getId());
    }
    private final JLabel labelMotiv = new JLabel();
    private final JCheckBox ch = new JCheckBox(getLocaleMessage("client.pause"));

    class TimerMotiv implements ActionListener {

        String oldKeys = "";
        long start;
        final String prob = "                                                                                       ";
        long t1 = 5000;
        long t2 = 10000;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!oldKeys.equals(keys_current)) {
                oldKeys = keys_current;
                start = new Date().getTime();
            }
            final String action;
            switch (oldKeys) {
                case KEYS_OFF:
                    action = getLocaleMessage("no_customers");
                    t1 = 990000000;
                    t2 = 990000900;
                    break;
                case KEYS_MAY_INVITE:
                    action = getLocaleMessage("inaction");
                    t1 = 60000;
                    t2 = 120000;
                    break;
                case KEYS_INVITED:
                    action = getLocaleMessage("invited");
                    t1 = 35000;
                    t2 = 45000;
                    break;
                case KEYS_STARTED:
                    action = getLocaleMessage("in_work");
                    t1 = 300000;
                    t2 = 60000;
                    break;
                case KEYS_ALL:
                    action = "What?";
                    t1 = 1000;
                    t2 = 2000;
                    break;
                default:
                    action = "Somethink new";
            }
            final long tt = System.currentTimeMillis() - start;
            labelMotiv.setForeground(tt < t1 ? new Color(0, 150, 0) : (tt > t2 ? new Color(200, 0, 0) : new Color(90, 0, 230)));
            final String tts = prob + action + "  " + tt / 1000 / 60 + ":" + (tt / 1000) % 60;
            labelMotiv.setText(tts.substring(tts.length() - 26));
            // тут типа костылик для автообновления. удп у них видите ли не доходят! Лохи криворукие! Раз в три минуты если давно не обновлялось...
            // ок-ок. раз в 55 скеунд, ок, хрен с вами, в 25 секунд. Это обновление будет еще сессию обновлять, такая так сказать долбилка для сессии.
            if (System.currentTimeMillis() - refreshTime > 1 * 25 * 1000) {
                refreshTime = System.currentTimeMillis();
                refreshSituation();
            }
        }
    }

    /**
     * Создадим форму, спозиционируем, сконфигурируем и покажем
     *
     * @param configFilePath файл конфигурации табло, приезжает из Spring
     */
    private static void initIndicatorBoard(final String cfgFile) throws DocumentException {
        File f = new File(cfgFile);
        if (indicatorBoard == null && f.exists()) {

            // todo indicatorBoard = FIndicatorBoard.getIndicatorBoard(new SAXReader(false).read(cfgFile).getRootElement(), false);
            java.awt.EventQueue.invokeLater(() -> {
                Element root = null;
                try {
                    root = new SAXReader(false).read(cfgFile).getRootElement();
                } catch (DocumentException ex) {
                    throw new ServerException("Не создали клиентское табло.", ex);
                }
                indicatorBoard = FIndicatorBoard.getIndicatorBoard(root, false);
                if (indicatorBoard != null) {
                    try {
                        indicatorBoard.setIconImage(ImageIO.read(FIndicatorBoard.class.getResource("/ru/apertum/qsystem/client/forms/resources/client.png")));
                    } catch (IOException ex) {
                        throw new ServerException(ex);
                    }
                    indicatorBoard.toPosition(QConfig.cfg().isDebug(), Integer.parseInt(root.attributeValue("x", "0")), Integer.parseInt(root.attributeValue("y", "0")));
                    indicatorBoard.setVisible(true);
                }
            });
        }
    }
    final private static OrangeClientboard BOARD = new OrangeClientboard();

    private static void initIndicatorBoardFX(String cfgFile) throws DocumentException {
        File f = new File(cfgFile);
        if (!clientboardFX && f.exists()) {
            // todo
            //todo    board.showBoard(f);
            clientboardFX = true;
        }
    }
    private long refreshTime = 0;
    private static int extPriorClient = 0;

    public static int getExtPriorClient() {
        return extPriorClient;
    }

    public static void setExtPriorClient(int ext) {
        extPriorClient = ext;
    }

    /**
     * Определяет какова ситуация в очереди к пользователю.
     *
     * @param plan - ситуация в XML
     */
    public void setSituation(SelfSituation plan) {
        QLog.l().logger().info("Set situation?");

        if (plan.getParallelList().size() == 0) {
            QLog.l().logger().info("parallelList is zero");
        }

        for (QCustomer cu : plan.getParallelList()) {
            QLog.l().logger().info(cu);
        }
        refreshTime = System.currentTimeMillis();
        if (plan.getSelfservices() == null) {
            return;
        }
        /**
         * На первую закладку.
         */
        String temp = "";
        /**
         * На вторую закладку
         */
        String tempAll = "";
        String temp1 = "";
        String color = "blue";
        int inCount = 0;

        if (plan.getExtPror() != null) {
            extPriorClient = plan.getExtPror();
        }

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
        final DefaultTreeModel tree = new DefaultTreeModel(root);
        // построим новую html с описанием состояния очередей
        for (SelfService serv : plan.getSelfservices()) {
            final int count = serv.getCountWait();
            final String serviceName = serv.getServiceName();

            final String people = " " + getLocaleMessage("messages.people");// множественное
            final String mins = getLocaleMessage("fclient.min");
            if (count != 0) {
                temp = temp + "<span style='color:" + (0 == count ? "green" : "red") + "'> - " + serviceName + ": " + count + people
                        + ((((count % 10) >= 2) && ((count % 10) <= 4)) ? "a" : "") + "</span><br>";
                final DefaultMutableTreeNode servNode = new DefaultMutableTreeNode(serviceName + ": " + count + people);
                root.add(servNode);
                serv.getLine().forEach(cu -> servNode.add(new DefaultMutableTreeNode(cu.number + (cu.waiting == null ? "" : (" " + cu.waiting + " " + mins + "")) + (cu.data == null || cu.data.isEmpty() ? "" : (" \"" + cu.data + "\"")))));
                temp1 = temp1 + " - " + serviceName + ": " + count + people
                        + ((((count % 10) >= 2) && ((count % 10) <= 4)) ? "a" : "") + "<br>";
            }
            tempAll = tempAll + "<span style='color:" + (0 == count ? "green" : "red") + "'> - " + serviceName + ": " + count + people
                    + ((((count % 10) >= 2) && ((count % 10) <= 4)) ? "a" : "") + "</span><br>";
            if (count != 0) {
                color = "purple";
            }
            inCount = inCount + count;
        }
        final String allClients = getLocaleMessage("messages.allClients") + ": ";
        labelResume.setText("<html><span style='color:" + color + "'>" + allClients + inCount + "</span>");
        temp1 = temp1 + allClients + inCount;

        // Обозначим очередь иконкой
        if (inCount == 0) {
            tray.setNewIcon("/ru/apertum/qsystem/client/forms/resources/client.png");
        } else {
            tray.setNewIcon("/ru/apertum/qsystem/client/forms/resources/clientBusy.png");
        }
        // покажим сообщение в трее если очередь была пуста и кто-то приперся
        if (customersCountForUser == 0 && inCount != 0 && plan.getCustomer() == null) {
            Toolkit.getDefaultToolkit().beep();
            tray.showMessageTray(Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF()), temp1.replaceAll("<br>", "\n"), MessageType.INFO);
            tray.getTrayIcon().getActionListeners()[0].actionPerformed(null);
        }
        customersCountForUser = inCount;
        // посмотрим, не приехал ли кастомер, который уже вызванный
        // если приехал, то его надо учесть
        setCustomer(plan.getCustomer());
        if (plan.getCustomer() != null) {
            QLog.l().logger().trace("От сервера приехал кастомер, который обрабатывается юзером.");
        } else {
            if (inCount == 0) {
                setKeyRegim(KEYS_OFF);//* нет клиентов, нечеого вызывать*/
            } else {
                setKeyRegim(KEYS_MAY_INVITE); //*в очереди кто-то есть, можно вызвать*/
            }
            //нефиг счелкать касторами пока процесс вызывания идет при параллельном приеме
            // но сейчас кастомера вообще нет, атк что можно щелкать
            if (user.getParallelAccess()) {
                listParallelClients.setEnabled(true);
            }

            labelNextNumber.setContentType("text/html");
            labelNextNumber.setText("<html><br><p align=center><span style='font-size:24.0pt;color:black'>" + getLocaleMessage("messages.noCall") + "</span></p>");
            printCustomerNumber("", null, -1);
            textAreaComments.setText("");
        }
        //при параллельном приеме могут быть кастомеры в работе. Отобразим их.
        if (user.getParallelAccess() && plan.getParallelList() != null) {
            listParallelClients.setModel(new DefaultComboBoxModel<>(plan.getParallelList().toArray(new QCustomer[plan.getParallelList().size()])));
            if (plan.getCustomer() != null) {
                for (QCustomer cu : plan.getParallelList()) {
                    if (plan.getCustomer().getId().equals(cu.getId())) {
                        listParallelClients.setSelectedValue(plan.getCustomer(), true);
                        break;
                    }
                }
            }
        }

        //теперь описание очередей новое
        userPlan = plan;
        treeSituation.setModel(tree);
        //labelSituation.setText("<html>" + temp);
        labelSituationAll.setText("<html>" + tempAll);

        // Ну и обновим модель для списка отложенных
        listPostponed.setModel(QPostponedList.getInstance().loadPostponedList(plan.getPostponedList()));
        LinkedList<QCustomer> custs = plan.getPostponedList();
        LinkedList<QCustomer> rem = new LinkedList<>();
        custs.stream().filter((cust) -> (cust.getIsMine() != null && !cust.getIsMine().equals(user.getId()))).forEach((cust) -> {
            rem.add(cust);
        });
        custs.removeAll(rem);
        listPostponed.setModel(QPostponedList.getInstance().loadPostponedList(custs));
        if (listPostponed.getModel().getSize() != 0) {
            listPostponed.setSelectedIndex(0);
        }
        menuItemInvitePostponed.setEnabled(customer == null && listPostponed.getModel().getSize() != 0);
        menuItemChangeStatusPostponed.setEnabled(listPostponed.getModel().getSize() != 0);
        // ну и разрешим параллельный вызов если есть доступ и есть кого вызывать. 
        // И если нет процесса вызывания, этот процесс должен быть завершен и не остался висеть в параллели
        if (user.getParallelAccess() && customer != null && (CustomerState.STATE_WORK.equals(customer.getState()) || CustomerState.STATE_WORK_SECONDARY.equals(customer.getState()))) {
            menuItemInvitePostponed.setEnabled(listPostponed.getModel().getSize() != 0); // тут тоже надо, для парралельных
        }
        color = plan.getPostponedList().isEmpty() ? "blue" : "purple";
        labelPost.setText("<html><span style='color:" + color + "'>" + plan.getPostponedList().size() + "</span>");
    }
    /**
     * Возможный состояния кнопок 1 - доступна кнопка, 0 - не доступна
     */
    public static final String KEYS_OFF = "000000000";
    public static final String KEYS_ALL = "111111111";
    public static final String KEYS_MAY_INVITE = "100000100";
    public static final String KEYS_INVITED = "011000000";
    public static final String KEYS_STARTED = "000111011";
    private String keys_current = KEYS_OFF;

    public String getKeys_current() {
        return keys_current;
    }

    /**
     * Механизм включения/отключения кнопок
     *
     * @param regim Конфигурация кнопок
     */
    public void setKeyRegim(String regim) {
        keys_current = regim;
        QLog.l().logger().trace("Конфигурация кнопок \"" + regim + "\".");
        menuItemInvitePostponed.setEnabled((KEYS_MAY_INVITE.equals(regim) || KEYS_OFF.equals(regim)) && listPostponed.getModel().getSize() != 0);
        menuItemChangeStatusPostponed.setEnabled(listPostponed.getModel().getSize() != 0);
        buttonInvite.setEnabled('1' == regim.charAt(0));
        buttonKill.setEnabled('1' == regim.charAt(1));
        buttonStart.setEnabled('1' == regim.charAt(2));
        buttonRedirect.setEnabled('1' == regim.charAt(3));
        buttonMoveToPostponed.setEnabled('1' == regim.charAt(4));
        buttonFinish.setEnabled('1' == regim.charAt(5));

        menuItemInvite.setEnabled('1' == regim.charAt(0));
        menuItemKill.setEnabled('1' == regim.charAt(1));
        menuItemStart.setEnabled('1' == regim.charAt(2));
        menuItemRedirect.setEnabled('1' == regim.charAt(3));
        menuItemMoveToPostponed.setEnabled('1' == regim.charAt(4));
        menuItemFinish.setEnabled('1' == regim.charAt(5));
    }

    /**
     * *****************************************************************************************************************
     * /*******************************************************************************************************************
     * /************************************ Обработчики кнопок ***********************************************
     */
    private long go() {
        QLog.l().logger().trace("Начало действия");
        return System.currentTimeMillis();
    }

    private void end(long start) {
        QLog.l().logger().trace("Действие завершено. Затрачено времени: " + ((double) (System.currentTimeMillis() - start)) / 1000 + " сек.\n");
    }

    private void extPluginIStartClientPressButton(QUser user, INetProperty netProperty, RpcGetSelfSituation.SelfSituation situation, ActionEvent evt, int keyId) {
        // поддержка расширяемости плагинами
        for (final IStartClient event : ServiceLoader.load(IStartClient.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                new Thread(() -> {
                    event.pressButton(user, netProperty, getUserPlan(), evt, keyId);
                }).start();
            } catch (Exception tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
        }
    }

    /**
     * Действие по нажатию кнопки "Вызов"
     *
     * @param evt
     */
    @Action
    public void inviteNextCustomer(ActionEvent evt) {
        QLog.l().logQUser().debug("inviteNextCustomer");
        try {
            final long start = go();
            // Вызываем кастомера
            final QCustomer cust = NetCommander.inviteNextCustomer(netProperty, user.getId());
            if (cust != null && cust.getPostponPeriod() > 0) {
                JOptionPane.showMessageDialog(this,
                        getLocaleMessage("invite.posponed.mess.1") + " " + cust.getPostponPeriod() + " " + getLocaleMessage("invite.posponed.mess.2") + " \"" + cust.getPostponedStatus() + "\".",
                        getLocaleMessage("invite.posponed.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
            // Показываем обстановку
            setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
            // поддержка расширяемости плагинами
            extPluginIStartClientPressButton(user, netProperty, getUserPlan(), evt, 1);
            end(start);
        } catch (HeadlessException | QException th) {
            throw new ClientException(new Exception(th));
        }
    }
    private boolean fkill = false;

    /**
     * Действие по нажатию кнопки "Отклонить"
     *
     * @param evt
     */
    @Action
    public void killCustomer(ActionEvent evt) {
        QLog.l().logQUser().debug("killCustomer");
        try {
            if (customer.getService().getExpectation() != 0 && (new Date().getTime() - customer.getStandTime().getTime()) / 1000 / 60 < customer.getService().getExpectation()) {
                if (fkill) {
                    if (JOptionPane.showConfirmDialog(this,
                            getLocaleMessage("messages.expectationl.ask") + " " + customer.getService().getExpectation() + " " + getLocaleMessage("fclient.min")
                            + ".\n\n                                                   " + getLocaleMessage("message.expectation.chance"),
                            getLocaleMessage("messages.expectationl.caption"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE) == 0) {
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            getLocaleMessage("messages.expectationl.ask") + " " + customer.getService().getExpectation() + " " + getLocaleMessage("fclient.min"),
                            getLocaleMessage("messages.expectationl.caption"),
                            JOptionPane.INFORMATION_MESSAGE);
                    fkill = true;
                    return;
                }

            }
            fkill = false;
            final long start = go();
            // Уточним намерения
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("messages.kill.ask"),
                    getLocaleMessage("messages.kill.caption"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            // Убиваем пользователя
            NetCommander.killNextCustomer(netProperty, user.getId(), customer.getId());
            // Получаем новую обстановку
            //Получаем состояние очередей для юзера
            setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
            // поддержка расширяемости плагинами
            extPluginIStartClientPressButton(user, netProperty, getUserPlan(), evt, 2);
            end(start);
        } catch (HeadlessException | QException th) {
            throw new ClientException(new Exception(th));
        }
    }

    /**
     * Действие по нажатию кнопки "Начать прием"
     *
     * @param evt
     */
    @Action
    public void getStartCustomer(ActionEvent evt) {
        QLog.l().logQUser().debug("getStartCustomer");
        try {
            final long start = go();
            // Переводим кастомера в разряд обрабатываемых
            NetCommander.getStartCustomer(netProperty, user.getId());
            // Получаем новую обстановку
            //Получаем состояние очередей для юзера
            setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
            // поддержка расширяемости плагинами
            extPluginIStartClientPressButton(user, netProperty, getUserPlan(), evt, 3);
            end(start);
        } catch (Exception th) {
            throw new ClientException(new Exception(th));
        }
    }

    /**
     * Действие по нажатию кнопки "Завершить прием"
     *
     * @param evt
     */
    @Action
    public void getStopCustomer(ActionEvent evt) {
        QLog.l().logQUser().debug("getStopCustomer");
        try {
            //Обозначим результат если требуется
            final long start = go();
            // Переводим кастомера в разряд обрабатанных
            // это должно выкинуть кастомера нафиг, но как обработанного
            final Long res = setResult();
            if (res == null) {
                return;
            }

            String resComments = user.getName() + ": \n_______________________\n" + customer.getTempComments();
            if (customer.needBack()) {
                //Диалог ввода коментария по кастомеру если он редиректенный и нужно его вернуть
                final FRedirect dlg = FRedirect.getService(netProperty, this, customer.getTempComments(), true);
                if (dlg != null) {
                    //Если не выбрали, то выходим
                    resComments = user.getName() + ": " + dlg.getTempComments();
                }
            }
            // вернется кастомер и возможно он еще не домой а по списку услуг. Список определяется при старте кастомера в обработку специяльным юзером в регистратуре
            final QCustomer cust = NetCommander.getFinishCustomer(netProperty, user.getId(), customer.getId(), res, resComments);
            if (cust != null && cust.getService() != null && cust.getState() == CustomerState.STATE_WAIT_COMPLEX_SERVICE) {
                JOptionPane.showMessageDialog(this, "Следующая услуга" + " \"" + cust.getService().getName() + "\". " + "Номер посетителя" + " \"" + cust.getPrefix() + cust.getNumber() + "\"." + "\n\n" + cust.getService().getDescription(), "Продолжение комплексой услуги", JOptionPane.INFORMATION_MESSAGE);
            }
            // Получаем новую обстановку
            //Получаем состояние очередей для юзера
            setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
            // поддержка расширяемости плагинами
            extPluginIStartClientPressButton(user, netProperty, getUserPlan(), evt, 6);
            end(start);
        } catch (HeadlessException | QException th) {
            throw new ClientException(new Exception(th));
        }
    }
    protected FRedirect servicesForm = null;

    /**
     * Действие по нажатию кнопки "Перенаправить"
     *
     * @param evt
     */
    @Action
    public void redirectCustomer(ActionEvent evt) {
        QLog.l().logQUser().debug("redirectCustomer");
        try {
            final long start = go();
            // Обозначим результат если надо
            final Long res = setResult();
            if (res == null) {
                return;
            }
            // Переводим кастомера в другую услугу
            // это должно выкинуть кастомера в другую очередь с приоритетом "переведенный"
            //Диалог выбора очереди для редиректа
            final FRedirect dlg = FRedirect.getService(netProperty, this, customer.getTempComments(), false);
            if (dlg == null) {
                //Если не выбрали, то выходим
                return;
            }

            NetCommander.redirectCustomer(netProperty, user.getId(), customer.getId(), dlg.getSelectedService().getId(), dlg.getRequestBack(), user.getName() + ": " + dlg.getTempComments(), res);
            // Получаем новую обстановку
            //Получаем состояние очередей для юзера
            setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
            // поддержка расширяемости плагинами
            extPluginIStartClientPressButton(user, netProperty, getUserPlan(), evt, 4);
            end(start);
        } catch (Exception th) {
            throw new ClientException(new Exception(th));
        }
    }

    //*******************************    Конец обработчиков кнопок    ***************************************************
    //*******************************************************************************************************************
    @Action
    public void showAboutBox() {
        FAbout.showAbout(this, true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenuTray = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem5 = new javax.swing.JMenuItem();
        popupMenuPostpone = new javax.swing.JPopupMenu();
        menuItemInvitePostponed = new javax.swing.JMenuItem();
        menuItemChangeStatusPostponed = new javax.swing.JMenuItem();
        panelDown = new QPanel("/ru/apertum/qsystem/client/forms/resources/fon_client.jpg");
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelBottom = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        textAreaComments = new javax.swing.JTextArea();
        btnPushToTalk = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        buttonInvite = new javax.swing.JButton();
        buttonKill = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        buttonFinish = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        buttonRedirect = new javax.swing.JButton();
        buttonMoveToPostponed = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        labelNextNumber = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        labelUser = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelResume = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        treeSituation = new javax.swing.JTree();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        labelSituationAll = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelMessage = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelPost = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listPostponed = new javax.swing.JList();
        panelParallerCusts = new javax.swing.JPanel();
        labelPaallelcaption = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listParallelClients = new javax.swing.JList<>();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        menuLangs = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemRefresh = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        menuItemFlexPriority = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuItemId = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        menuItemInvite = new javax.swing.JMenuItem();
        menuItemKill = new javax.swing.JMenuItem();
        menuItemStart = new javax.swing.JMenuItem();
        menuItemRedirect = new javax.swing.JMenuItem();
        menuItemMoveToPostponed = new javax.swing.JMenuItem();
        menuItemFinish = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        popupMenuTray.setName("popupMenuTray"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FClient.class, this);
        jMenuItem1.setAction(actionMap.get("getNextCustomer")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        popupMenuTray.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("killCustomer")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        popupMenuTray.add(jMenuItem2);

        jMenuItem3.setAction(actionMap.get("getStartCustomer")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        popupMenuTray.add(jMenuItem3);

        jMenuItem4.setAction(actionMap.get("getStopCustomer")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        popupMenuTray.add(jMenuItem4);

        jSeparator1.setName("jSeparator1"); // NOI18N
        popupMenuTray.add(jSeparator1);

        jMenuItem5.setAction(actionMap.get("quit")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        popupMenuTray.add(jMenuItem5);

        popupMenuPostpone.setName("popupMenuPostpone"); // NOI18N

        menuItemInvitePostponed.setAction(actionMap.get("invitePostponed")); // NOI18N
        menuItemInvitePostponed.setName("menuItemInvitePostponed"); // NOI18N
        popupMenuPostpone.add(menuItemInvitePostponed);

        menuItemChangeStatusPostponed.setAction(actionMap.get("changeStatusForPosponed")); // NOI18N
        menuItemChangeStatusPostponed.setName("menuItemChangeStatusPostponed"); // NOI18N
        popupMenuPostpone.add(menuItemChangeStatusPostponed);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FClient.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(getIconImage());
        setIconImages(getIconImages());
        setMinimumSize(new java.awt.Dimension(545, 400));
        setName("Form"); // NOI18N

        panelDown.setBorder(new javax.swing.border.MatteBorder(null));
        panelDown.setName("panelDown"); // NOI18N

        jPanel4.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.setName("jPanel4"); // NOI18N

        jLabel2.setIcon(resourceMap.getIcon("jLabel2.icon")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
        );

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(410);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOpaque(false);

        panelBottom.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelBottom.border.title"))); // NOI18N
        panelBottom.setName("panelBottom"); // NOI18N
        panelBottom.setOpaque(false);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        textAreaComments.setEditable(false);
        textAreaComments.setColumns(20);
        textAreaComments.setLineWrap(true);
        textAreaComments.setRows(5);
        textAreaComments.setWrapStyleWord(true);
        textAreaComments.setBorder(null);
        textAreaComments.setName("textAreaComments"); // NOI18N
        jScrollPane5.setViewportView(textAreaComments);

        btnPushToTalk.setText(resourceMap.getString("btnPushToTalk.text")); // NOI18N
        btnPushToTalk.setToolTipText(resourceMap.getString("btnPushToTalk.toolTipText")); // NOI18N
        btnPushToTalk.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        btnPushToTalk.setName("btnPushToTalk"); // NOI18N

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBottomLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPushToTalk, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
            .addComponent(btnPushToTalk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane1.setBottomComponent(panelBottom);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);

        buttonInvite.setAction(actionMap.get("inviteNextCustomer")); // NOI18N
        buttonInvite.setName("buttonInvite"); // NOI18N

        buttonKill.setAction(actionMap.get("killCustomer")); // NOI18N
        buttonKill.setText(resourceMap.getString("buttonKill.text")); // NOI18N
        buttonKill.setToolTipText(resourceMap.getString("buttonKill.toolTipText")); // NOI18N
        buttonKill.setName("buttonKill"); // NOI18N

        buttonStart.setAction(actionMap.get("getStartCustomer")); // NOI18N
        buttonStart.setName("buttonStart"); // NOI18N

        buttonFinish.setAction(actionMap.get("getStopCustomer")); // NOI18N
        buttonFinish.setName("buttonFinish"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        buttonRedirect.setAction(actionMap.get("redirectCustomer")); // NOI18N
        buttonRedirect.setName("buttonRedirect"); // NOI18N

        buttonMoveToPostponed.setAction(actionMap.get("moveToPOstponed")); // NOI18N
        buttonMoveToPostponed.setText(resourceMap.getString("buttonMoveToPostponed.text")); // NOI18N
        buttonMoveToPostponed.setName("buttonMoveToPostponed"); // NOI18N

        jScrollPane2.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setOpaque(false);

        labelNextNumber.setEditable(false);
        labelNextNumber.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        labelNextNumber.setContentType("text/html"); // NOI18N
        labelNextNumber.setFont(resourceMap.getFont("labelNextNumber.font")); // NOI18N
        labelNextNumber.setName("labelNextNumber"); // NOI18N
        jScrollPane2.setViewportView(labelNextNumber);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonInvite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonKill, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonStart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonRedirect, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonMoveToPostponed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonFinish, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonInvite)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonKill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonStart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRedirect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonMoveToPostponed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonFinish)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);

        jLabel3.setFont(resourceMap.getFont("jLabel3.font")); // NOI18N
        jLabel3.setIcon(resourceMap.getIcon("jLabel3.icon")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        labelUser.setFont(resourceMap.getFont("labelUser.font")); // NOI18N
        labelUser.setForeground(resourceMap.getColor("labelUser.foreground")); // NOI18N
        labelUser.setText(resourceMap.getString("labelUser.text")); // NOI18N
        labelUser.setName("labelUser"); // NOI18N

        jLabel4.setFont(resourceMap.getFont("jLabel4.font")); // NOI18N
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/qiui.png"))); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        labelResume.setFont(resourceMap.getFont("labelResume.font")); // NOI18N
        labelResume.setText(resourceMap.getString("labelResume.text")); // NOI18N
        labelResume.setName("labelResume"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);

        jScrollPane6.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane6.setName("jScrollPane6"); // NOI18N

        treeSituation.setAutoscrolls(true);
        treeSituation.setExpandsSelectedPaths(false);
        treeSituation.setName("treeSituation"); // NOI18N
        treeSituation.setRootVisible(false);
        treeSituation.setShowsRootHandles(true);
        jScrollPane6.setViewportView(treeSituation);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setOpaque(false);

        jScrollPane3.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setOpaque(false);

        labelSituationAll.setBackground(resourceMap.getColor("labelSituationAll.background")); // NOI18N
        labelSituationAll.setText(resourceMap.getString("labelSituationAll.text")); // NOI18N
        labelSituationAll.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelSituationAll.setName("labelSituationAll"); // NOI18N
        labelSituationAll.setOpaque(true);
        jScrollPane3.setViewportView(labelSituationAll);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        jScrollPane1.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        labelMessage.setBackground(resourceMap.getColor("labelMessage.background")); // NOI18N
        labelMessage.setText(resourceMap.getString("labelMessage.text")); // NOI18N
        labelMessage.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelMessage.setName("labelMessage"); // NOI18N
        labelMessage.setOpaque(true);
        jScrollPane1.setViewportView(labelMessage);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        jLabel5.setFont(resourceMap.getFont("jLabel5.font")); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/group.png"))); // NOI18N
        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        labelPost.setText(resourceMap.getString("labelPost.text")); // NOI18N
        labelPost.setName("labelPost"); // NOI18N

        jScrollPane4.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane4.setName("jScrollPane4"); // NOI18N

        listPostponed.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        listPostponed.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listPostponed.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPostponed.setComponentPopupMenu(popupMenuPostpone);
        listPostponed.setName("listPostponed"); // NOI18N
        jScrollPane4.setViewportView(listPostponed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelPost))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelResume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelResume))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(labelPost))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout panelDownLayout = new javax.swing.GroupLayout(panelDown);
        panelDown.setLayout(panelDownLayout);
        panelDownLayout.setHorizontalGroup(
            panelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelDownLayout.setVerticalGroup(
            panelDownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDownLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE))
        );

        panelParallerCusts.setBackground(resourceMap.getColor("panelParallerCusts.background")); // NOI18N
        panelParallerCusts.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelParallerCusts.setName("panelParallerCusts"); // NOI18N

        labelPaallelcaption.setFont(resourceMap.getFont("labelPaallelcaption.font")); // NOI18N
        labelPaallelcaption.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPaallelcaption.setText(resourceMap.getString("labelPaallelcaption.text")); // NOI18N
        labelPaallelcaption.setName("labelPaallelcaption"); // NOI18N

        jScrollPane7.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane7.setName("jScrollPane7"); // NOI18N

        listParallelClients.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("listParallelClients.border.lineColor"), 5)); // NOI18N
        listParallelClients.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listParallelClients.setName("listParallelClients"); // NOI18N
        jScrollPane7.setViewportView(listParallelClients);

        javax.swing.GroupLayout panelParallerCustsLayout = new javax.swing.GroupLayout(panelParallerCusts);
        panelParallerCusts.setLayout(panelParallerCustsLayout);
        panelParallerCustsLayout.setHorizontalGroup(
            panelParallerCustsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelParallerCustsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelParallerCustsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane7)
                    .addComponent(labelPaallelcaption, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelParallerCustsLayout.setVerticalGroup(
            panelParallerCustsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelParallerCustsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPaallelcaption)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        menuLangs.setText(resourceMap.getString("menuLangs.text")); // NOI18N
        menuLangs.setName("menuLangs"); // NOI18N
        fileMenu.add(menuLangs);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        menuItemRefresh.setAction(actionMap.get("refreshClient")); // NOI18N
        menuItemRefresh.setName("menuItemRefresh"); // NOI18N
        fileMenu.add(menuItemRefresh);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        optionsMenu.setText(resourceMap.getString("optionsMenu.text")); // NOI18N
        optionsMenu.setName("optionsMenu"); // NOI18N

        menuItemFlexPriority.setAction(actionMap.get("manageFlexPriority")); // NOI18N
        menuItemFlexPriority.setText(resourceMap.getString("menuItemFlexPriority.text")); // NOI18N
        menuItemFlexPriority.setName("menuItemFlexPriority"); // NOI18N
        optionsMenu.add(menuItemFlexPriority);

        jSeparator3.setName("jSeparator3"); // NOI18N
        optionsMenu.add(jSeparator3);

        menuItemId.setText(resourceMap.getString("menuItemId.text")); // NOI18N
        menuItemId.setName("menuItemId"); // NOI18N
        menuItemId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemIdActionPerformed(evt);
            }
        });
        optionsMenu.add(menuItemId);

        menuBar.add(optionsMenu);

        editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        menuItemInvite.setAction(actionMap.get("inviteNextCustomer")); // NOI18N
        menuItemInvite.setName("menuItemInvite"); // NOI18N
        editMenu.add(menuItemInvite);

        menuItemKill.setAction(actionMap.get("killCustomer")); // NOI18N
        menuItemKill.setName("menuItemKill"); // NOI18N
        editMenu.add(menuItemKill);

        menuItemStart.setAction(actionMap.get("getStartCustomer")); // NOI18N
        menuItemStart.setName("menuItemStart"); // NOI18N
        editMenu.add(menuItemStart);

        menuItemRedirect.setAction(actionMap.get("redirectCustomer")); // NOI18N
        menuItemRedirect.setName("menuItemRedirect"); // NOI18N
        editMenu.add(menuItemRedirect);

        menuItemMoveToPostponed.setAction(actionMap.get("moveToPOstponed")); // NOI18N
        menuItemMoveToPostponed.setName("menuItemMoveToPostponed"); // NOI18N
        editMenu.add(menuItemMoveToPostponed);

        menuItemFinish.setAction(actionMap.get("getStopCustomer")); // NOI18N
        menuItemFinish.setName("menuItemFinish"); // NOI18N
        editMenu.add(menuItemFinish);

        menuBar.add(editMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        menuItemHelp.setAction(actionMap.get("help")); // NOI18N
        menuItemHelp.setText(resourceMap.getString("menuItemHelp.text")); // NOI18N
        menuItemHelp.setName("menuItemHelp"); // NOI18N
        helpMenu.add(menuItemHelp);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(panelParallerCusts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelParallerCusts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(978, 649));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void menuItemIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemIdActionPerformed
        if (JOptionPane.showInputDialog(this, "User ID from database:", "ID", 1, null, null, user.getId().toString()) != null) {
            final StringSelection stringSelection = new StringSelection(user.getId().toString());
            final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        }
    }//GEN-LAST:event_menuItemIdActionPerformed
    /**
     * Табло вывода кастомера.
     */
    private static FIndicatorBoard indicatorBoard = null;
    private static boolean clientboardFX = false;

    /**
     * Если есть монитор для вывода номера клиента, то выведет текст нома на него. Если текст для вывода пуст, то тобло перестанет моргать и отчистиццо.
     *
     * @param text текст нома клиента.
     * @param blinkCount 0 - постоянное мигание, -1 не мигает. число - количество миганий
     */
    private void printCustomerNumber(String pref, Integer num, int blinkCount) {
        if (indicatorBoard != null) {
            indicatorBoard.printRecord(0, pref, num, "", "", blinkCount);
        }
        if (clientboardFX) {
            //todo   board.showData("", false);
        }
    }

    /**
     * @param args the command line arguments
     * @throws DocumentException
     */
    public static void main(String args[]) throws DocumentException {
        QLog.initial(args, 1);
        Locale.setDefault(Locales.getInstance().getLangCurrent());
        Uses.startSplashClient();
        // Загрузка плагинов из папки plugins
        if (QConfig.cfg().isPlaginable()) {
            Uses.loadPlugins("./plugins/");
        }

        final IClientNetProperty netProperty = new ClientNetProperty(args);
        //Загрузим серверные параметры
        QProperties.get().load(netProperty);
        // это заплата на баг с коннектом.
        // без предконнекта из main в дальнейшем сокет не хочет работать,
        // долго висит и вываливает минут через 15-20 эксепшн java.net.SocketException: Malformed reply from SOCKS server  
        /*
         Socket skt = null;
         try {
         skt = new Socket(netProperty.getAddress(), 61111);
         skt.close();
         } catch (IOException ex) {
         }
         */

        if (!QConfig.cfg().isTerminal()) {// в терминальном режиме запускаем много копий
            // Отсечем вторую копию.
            try {
                final DatagramSocket socket = new DatagramSocket(netProperty.getClientPort());
                socket.close();
            } catch (SocketException ex) {
                QLog.l().logger().info(ex.getMessage());
                QLog.l().logger().error("Сервер UDP не запустился, вторая копия не позволяется.");
                JOptionPane.showMessageDialog(null, getLocaleMessage("messages.restart.mess"), getLocaleMessage("messages.restart.caption"), JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }

        }
        // Определим кто работает на данном месте.
        final QUser user = FLogin.logining(netProperty, null, true, 3, FLogin.LEVEL_USER);
        Uses.showSplash();
        try {
            //Определим, надо ли выводить кастомера на второй экран.
            // Для этого должны быть переданы две координаты для определения этого монитора
            // -posx x -posy y
            if (new File(QConfig.cfg().getBoardCfgFile()).exists()) {
                initIndicatorBoard(QConfig.cfg().getBoardCfgFile());
            } else if (new File(QConfig.cfg().getBoardCfgFXfile()).exists()) {
                initIndicatorBoardFX(QConfig.cfg().getBoardCfgFXfile());
            }

            // Посмотрим, не пытались ли влезть под уже имеющейся в системе ролью
            if (!NetCommander.getSelfServicesCheck(netProperty, user.getId())) {
                Uses.closeSplash();
                JOptionPane.showMessageDialog(null, getLocaleMessage("messages.stop.access_denay.mess"), getLocaleMessage("messages.stop.mess"), JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
            //Показываем форму и передаем в нее описание того кто залогинился
            fClient = new FClient(user, netProperty);
            // подключения плагинов, которые стартуют в самом начале.
            // поддержка расширяемости плагинами
            for (final IStartClient event : ServiceLoader.load(IStartClient.class)) {
                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                try {
                    new Thread(() -> {
                        event.start(fClient);
                    }).start();
                } catch (Exception tr) {
                    QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                }
            }
            fClient.setVisible(true);
        } catch (AWTException ex) {
            QLog.l().logger().error("Ошибка работы с tray: ", ex);
            System.exit(0);
        } catch (DocumentException | HeadlessException ex) {
            QLog.l().logger().error("Ошибка при старте: ", ex);
            System.exit(0);
        } finally {
            Uses.closeSplash();
        }
        //     }
        //  });
    }
    private static FClient fClient;

    public static FClient getForm() {
        return fClient;
    }

    @Action
    public void refreshClient() {
        //Получаем состояние очередей для юзера
        refreshSituation(true);
    }

    public void refreshSituation() {
        refreshSituation(null);
    }

    public void refreshSituation(Boolean forced) {
        //Получаем состояние очередей для юзера
        try {
            setSituation(NetCommander.getSelfServices(netProperty, user.getId(), forced));
            spd = -1;
        } catch (Exception th) {
            spd++;
            QLog.l().logger().error("Ошибка при обновлении состояния: ", th);
            if (spd % 20 == 0) {
                tray.showMessageTray(getLocaleMessage("qsys_server_warning"), loc("qsys_server_down"), MessageType.WARNING);
            }
        }
    }
    private int spd = -1;

    @Action
    public void help() {
    }

    @Action
    public void setCurrentLang() {
        for (int i = 0; i < menuLangs.getItemCount(); i++) {
            if (((JRadioButtonMenuItem) menuLangs.getItem(i)).isSelected()) {
                Locales.getInstance().setLangCurrent(((JRadioButtonMenuItem) menuLangs.getItem(i)).getText());
            }
        }
    }

    @Action
    public void moveToPOstponed() {
        try {
            final long start = go();

            if (moveToPostponed == null) {
                moveToPostponed = new FMoveToPostponed(fClient, true, getResults());
            }
            Uses.setLocation(moveToPostponed);
            moveToPostponed.setVisible(true);
            if (!moveToPostponed.isOK()) {
                return;
            }
            NetCommander.customerToPostpone(netProperty, user.getId(), customer.getId(), moveToPostponed.getResult(), moveToPostponed.getPeriod(), moveToPostponed.isMine());
            // Показываем обстановку
            setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
            // поддержка расширяемости плагинами
            extPluginIStartClientPressButton(user, netProperty, getUserPlan(), new ActionEvent(buttonMoveToPostponed, 13, buttonMoveToPostponed.getActionCommand(), System.currentTimeMillis(), 1), 5);
            end(start);
        } catch (Exception th) {
            throw new ClientException(new Exception(th));
        }
    }
    private FMoveToPostponed moveToPostponed;

    @Action
    public void invitePostponed() throws QException {
        if (listPostponed.getSelectedIndex() != -1) {
            if (customer == null
                    || (user.getParallelAccess()
                    && customer != null
                    && (CustomerState.STATE_WORK.equals(customer.getState()) || CustomerState.STATE_WORK_SECONDARY.equals(customer.getState())))) {
                final long start = go();
                final QCustomer cust = (QCustomer) listPostponed.getSelectedValue();
                NetCommander.invitePostponeCustomer(netProperty, user.getId(), cust.getId());
                // Показываем обстановку
                setSituation(NetCommander.getSelfServices(netProperty, user.getId()));
                // поддержка расширяемости плагинами
                extPluginIStartClientPressButton(user, netProperty, getUserPlan(), new ActionEvent(menuItemInvitePostponed, 1, KEYS_MAY_INVITE), 1);
                end(start);
            } else {
                JOptionPane.showMessageDialog(null, getLocaleMessage("postponed.already_invited"), getLocaleMessage("invite.posponed.title"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Action
    public void changeStatusForPosponed() {
        if (listPostponed.getSelectedIndex() != -1) {
            final long start = go();
            final QCustomer cust = (QCustomer) listPostponed.getSelectedValue();

            String status = (String) JOptionPane.showInputDialog(this, getLocaleMessage("resultwork.dialog.caption"), getLocaleMessage("resultwork.dialog.title"), JOptionPane.QUESTION_MESSAGE, null, getResults(), null);
            if (status == null) {
                return;
            }
            NetCommander.postponeCustomerChangeStatus(netProperty, cust.getId(), status);
            // Показываем обстановку, должно быть оповешение по udp
            //setSituation(NetCommander.getSelfServices(netProperty, user.getName()));
            end(start);
        }
    }

    @Action
    public void manageFlexPriority() {
        FServicePriority.show(netProperty, this, getUserPlan(), user.getId());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    public javax.swing.JButton btnPushToTalk;
    private javax.swing.JButton buttonFinish;
    private javax.swing.JButton buttonInvite;
    private javax.swing.JButton buttonKill;
    private javax.swing.JButton buttonMoveToPostponed;
    private javax.swing.JButton buttonRedirect;
    private javax.swing.JButton buttonStart;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    public javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelMessage;
    public javax.swing.JTextPane labelNextNumber;
    private javax.swing.JLabel labelPaallelcaption;
    private javax.swing.JLabel labelPost;
    private javax.swing.JLabel labelResume;
    private javax.swing.JLabel labelSituationAll;
    private javax.swing.JLabel labelUser;
    private javax.swing.JList<QCustomer> listParallelClients;
    private javax.swing.JList listPostponed;
    public javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuItemChangeStatusPostponed;
    private javax.swing.JMenuItem menuItemFinish;
    private javax.swing.JMenuItem menuItemFlexPriority;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenuItem menuItemId;
    private javax.swing.JMenuItem menuItemInvite;
    private javax.swing.JMenuItem menuItemInvitePostponed;
    private javax.swing.JMenuItem menuItemKill;
    private javax.swing.JMenuItem menuItemMoveToPostponed;
    private javax.swing.JMenuItem menuItemRedirect;
    private javax.swing.JMenuItem menuItemRefresh;
    private javax.swing.JMenuItem menuItemStart;
    private javax.swing.JMenu menuLangs;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelDown;
    private javax.swing.JPanel panelParallerCusts;
    private javax.swing.JPopupMenu popupMenuPostpone;
    private javax.swing.JPopupMenu popupMenuTray;
    private javax.swing.JTextArea textAreaComments;
    public javax.swing.JTree treeSituation;
    // End of variables declaration//GEN-END:variables
}
