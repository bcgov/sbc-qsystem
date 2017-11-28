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

import static ru.apertum.qsystem.common.QConfig.KEY_WELCOME_KBD;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import gnu.io.SerialPortEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;
import java.util.ServiceLoader;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.tree.TreeNode;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.output.OutputException;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.QProperties;
import ru.apertum.qsystem.client.common.BackDoor;
import ru.apertum.qsystem.client.common.ClientNetProperty;
import ru.apertum.qsystem.client.common.WelcomeBGparams;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.BrowserFX;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.Mailer;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.CmdParams;
import ru.apertum.qsystem.common.cmd.JsonRPC20;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices;
import ru.apertum.qsystem.common.cmd.RpcGetSrt;
import ru.apertum.qsystem.common.cmd.RpcStandInService;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IBytesButtensAdapter;
import ru.apertum.qsystem.extra.IPrintTicket;
import ru.apertum.qsystem.extra.IWelcome;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QAuthorizationCustomer;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.infosystem.QInfoItem;
import ru.apertum.qsystem.server.model.response.QRespItem;
import ru.apertum.qsystem.server.model.response.QResponseTree;
import ru.evgenic.rxtx.serialPort.IReceiveListener;
import ru.evgenic.rxtx.serialPort.ISerialPort;
import ru.evgenic.rxtx.serialPort.RxtxSerialPort;

/**
 * Модуль показа окна выбора услуги для постановки в очередь. Created on 8 Сентябрь 2008 г., 16:07
 * Класс, который покажит форму с кнопками, соответствующими услуга. При нажатии на кнопку, кастомер
 * пытается встать в очередь.
 *
 * @author Evgeniy Egorov
 */
public class FWelcome extends javax.swing.JFrame {

    public final static String TEMP_FILE_PROPS = "temp/wlcm.properties";
    private final static JLabel CMP = new JLabel();
    public static QService root;
    /**
     * это печатаем под картинкой если без домена
     */
    public static String caption;
    /**
     * Информация для взаимодействия по сети. Формируется по данным из командной строки.
     */
    public static IClientNetProperty netProperty;
    /**
     * Режим предварительной записи в поликлинике
     */
    public static boolean isMed = false;
    /**
     * Режим инфокиоска, когда получить всю инфу с пункта регистрации можно, а встать в очередь
     * нельзя
     */
    public static boolean isInfo = false;
    protected static QService current;
    /**
     * время блокировки/разблокировки пункта регистрации
     */
    protected static Date startTime;
    protected static Date finishTime;
    protected static boolean btnFreeDesign;
    private static ResourceMap localeMap = null;
    /**
     * XML-список отзывов. перврначально null, грузится при первом обращении. Использовать через
     * геттер.
     */
    private static QRespItem response = null;
    /**
     * XML- дерево информации. перврначально null, грузится при первом обращении. Использовать через
     * геттер.
     */
    private static QInfoItem infoTree = null;
    private static ISerialPort serialPort;
    private static boolean advanceRegim = false;
    // Состояния пункта регистрации
    public final String LOCK = getLocaleMessage("lock");
    public final String UNLOCK = getLocaleMessage("unlock");
    public final String OFF = getLocaleMessage("off");
    /**
     *
     */
    private final Thread server = new Thread(new CommandServer());
    //******************************************************************************************************************
    //******************************************************************************************************************
    //*****************************************Сервер удаленного управления ********************************************
    private final JLabel labelInfo = new JLabel();
    private final LinkedList<Long> clicks = new LinkedList<>();
    public String LOCK_MESSAGE =
        "<HTML><p align=center><b><span style='font-size:40.0pt;color:red'>" + getLocaleMessage(
            "messages.lock_messages") + "</span></b></p>";
    //*****************************************Сервер удаленного управления ********************************************
    public int pageNumber = 0;// на одном уровне может понадобиться листать услуги, не то они расползуться. Это вместо скрола.
    /**
     * Это когда происходит авторизация клиента при постановке в очередь, например перед выбором
     * услуге в регистуре, то сюда попадает ID этого авторизованного пользователя. Дальше этот ID
     * передать в команду постановки предварительного и там если по нему найдется этот клиент, то он
     * должен попасть в табличку предварительно зарегиных.
     */
    public long advancedCustomer = -1;
    /**
     * Флаг завершения сервера удаленного управления
     */
    boolean exitServer = false;
    //==================================================================================================================
    //С рабочего места администратора должна быть возможность заблокировать пункт постановки в очередь,
    //разблокировать, выключить, провести инициализация заново.
    private String stateWindow = UNLOCK;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdvance;
    private javax.swing.JButton buttonBack;
    private javax.swing.JButton buttonBackPage;
    private javax.swing.JButton buttonForwardPage;
    private javax.swing.JButton buttonInfo;
    private javax.swing.JButton buttonResponse;
    private javax.swing.JButton buttonStandAdvance;
    private javax.swing.JButton buttonToBegin;
    private javax.swing.JLabel labelBackPage;
    private javax.swing.JLabel labelCaption;
    private javax.swing.JLabel labelForwardPage;
    private javax.swing.JLabel labelLock;
    private javax.swing.JPanel panelBackground;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelCaption;
    private javax.swing.JPanel panelCentre;
    private javax.swing.JPanel panelForPaging;
    private javax.swing.JPanel panelLngs;
    private javax.swing.JPanel panelLock;
    private javax.swing.JPanel panelMain;
    /**
     * Таймер, по которому будем выходить в корень меню.
     */
    public ATalkingClock clockBack = new ATalkingClock(WelcomeParams.getInstance().delayBack, 1) {

        @Override
        public void run() {
            setAdvanceRegim(false);
            showMed();
        }
    };
    /**
     * Таймер, по которому будем разблокировать и выходить в корень меню.
     */
    public ATalkingClock clockUnlockBack = new ATalkingClock(WelcomeParams.getInstance().delayPrint,
        1) {

        @Override
        public void run() {
            unlock();
            setAdvanceRegim(false);
            buttonToBeginActionPerformed(null);
        }
    };
    /**
     * Таймер, по которому будем включать и выключать пункт регистрации.
     */
    public ATalkingClock lockWelcome = new ATalkingClock(Uses.DELAY_CHECK_TO_LOCK, 0) {

        @Override
        public void run() {
            // если время начала и завершения совпадают, то игнор блокировки.
            if (Uses.FORMAT_HH_MM.format(finishTime).equals(Uses.FORMAT_HH_MM.format(startTime))) {
                return;
            }
            if (Uses.FORMAT_HH_MM.format(new Date()).equals(Uses.FORMAT_HH_MM.format(finishTime))) {
                lock(
                    "<HTML><p align=center><b><span style='font-size:40.0pt;color:red'>Регистрация клиентов остановлена.</span></b></p>");
            }
            if (Uses.FORMAT_HH_MM.format(new Date()).equals(Uses.FORMAT_HH_MM.format(startTime))) {
                unlock();
            }
        }
    };

    public FWelcome(QService root) {
        init(root);
    }

    public static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(FWelcome.class);
        }
        return localeMap.getString(key);
    }

    public static QRespItem getResponse() {
        if (response == null) {
            response = NetCommander.getResporseList(netProperty);
            QResponseTree.formTree(response);
        }
        return response;
    }

    public static QInfoItem getInfoTree() {
        if (infoTree == null) {
            infoTree = NetCommander.getInfoTree(netProperty);
        }
        return infoTree;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) throws Exception {
        QLog.initial(args, 4);
        // Загрузка плагинов из папки plugins
        Uses.loadPlugins("./plugins/");
        Locale.setDefault(Locales.getInstance().getLangCurrent());

        // выберем нужные языки на велкоме если первый раз запускаем или ключ -clangs
        if ((Locales.getInstance().isWelcomeMultylangs() && Locales.getInstance()
            .isWelcomeFirstLaunch()) || QConfig.cfg().isChangeLangs()) {

            JFrame form = new FLangsOnWelcome();
            java.awt.EventQueue.invokeLater(() -> {
                form.setVisible(true);
            });
            Thread.sleep(2000);
            while (form.isVisible()) {
                Thread.sleep(1000);
            }
        }

        netProperty = new ClientNetProperty(args);
        //Загрузим серверные параметры
        QProperties.get().load(netProperty);
        // определим режим пользовательского интерфейса
        for (String s : args) {
            if ("med".equals(s)) {
                isMed = true;
                if (!"".equals(WelcomeParams.getInstance().buttons_COM)) {
                    serialPort = new RxtxSerialPort(WelcomeParams.getInstance().buttons_COM);
                    serialPort.setDataBits(WelcomeParams.getInstance().buttons_databits);
                    serialPort.setParity(WelcomeParams.getInstance().buttons_parity);
                    serialPort.setSpeed(WelcomeParams.getInstance().buttons_speed);
                    serialPort.setStopBits(WelcomeParams.getInstance().buttons_stopbits);
                }
            }
            if ("info".equals(s)) {
                isInfo = true;
            }
        }
        final RpcGetAllServices.ServicesForWelcome servs;
        try {
            servs = NetCommander.getServices(netProperty);
        } catch (Throwable t) {
            QLog.l().logger().error("Start Welcome was failed.", t);
            System.exit(117);
            throw new RuntimeException(t);
        }
        root = servs.getRoot();
        FWelcome.startTime = servs.getStartTime();
        FWelcome.finishTime = servs.getFinishTime();
        FWelcome.btnFreeDesign = servs.getButtonFreeDesign();

        for (final IWelcome event : ServiceLoader.load(IWelcome.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                event.start(netProperty, servs);
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
        }

        //touch,info,med,btn,kbd
        switch (QConfig.cfg().getWelcomeMode()) {
            case KEY_WELCOME_KBD: {
                // ***************************************************************************************************************************************
                // ***  Это клавиатурный ввод символа
                // ***************************************************************************************************************************************
                QLog.l().logger().info("Keyboard mode is starting...");

                final GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(startTime);
                final long stime =
                    gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc.get(GregorianCalendar.MINUTE);
                gc.setTime(finishTime);
                final long ftime =
                    gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc.get(GregorianCalendar.MINUTE);

                final HashMap<String, QService> addrs = new HashMap<>();
                final File addrFile = new File("config/welcome_buttons.properties");
                try (FileInputStream fis = new FileInputStream(addrFile); Scanner s = new Scanner(
                    fis)) {
                    while (s.hasNextLine()) {
                        final String line = s.nextLine().trim();
                        if (!line.startsWith("#")) {
                            final String[] ss = line.split("=");
                            QServiceTree.sailToStorm(root, (TreeNode service) -> {
                                if (((QService) service).getId().equals(Long.valueOf(ss[1]))) {
                                    QLog.l().logger()
                                        .debug("Key " + ss[0] + " = " + ss[1] + " "
                                            + ((QService) service).getName());
                                    addrs.put(ss[0], (QService) service);
                                }
                            });
                        }
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                    throw new RuntimeException(ex);
                }

                final JFrame fr = new JFrame("Keyboard input");

                // спрячем курсор мыши
                if (QConfig.cfg().isHideCursor()) {
                    final int[] pixels = new int[16 * 16];
                    final Image image = Toolkit.getDefaultToolkit()
                        .createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                    Cursor transparentCursor = Toolkit.getDefaultToolkit()
                        .createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                    fr.setCursor(transparentCursor);
                }

                fr.setUndecorated(true);
                fr.setVisible(true);
                fr.setAlwaysOnTop(false);
                fr.setAlwaysOnTop(true);
                fr.setVisible(true);
                fr.toFront();
                fr.requestFocus();
                fr.setForeground(Color.red);
                fr.setBackground(Color.red);
                fr.setOpacity(0.1f);
                final Robot r = new Robot();
                fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                fr.setSize(5, 5);
                fr.addKeyListener(new KeyListener() {

                    long t = 0;

                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {

                        if (System.currentTimeMillis() - t < 5000) {
                            return;
                        }
                        t = System.currentTimeMillis();
                        final GregorianCalendar gc = new GregorianCalendar();
                        final long now =
                            gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc
                                .get(GregorianCalendar.MINUTE);
                        if (now > stime && now < ftime) {
                            final QService serv = addrs.get("" + e.getKeyChar());
                            if (serv == null) {
                                QLog.l().logger()
                                    .error("Service is not found by " + e.getKeyChar());
                                return;
                            }
                            final QCustomer customer;
                            try {
                                customer = NetCommander
                                    .standInService(netProperty, serv.getId(), "1", 1, "");
                            } catch (Exception ex) {
                                QLog.l().logger()
                                    .error("Fail to put in line " + serv.getName() + "  ID=" + serv
                                        .getId(), ex);
                                return;
                            }
                            FWelcome.printTicket(customer, root.getName());

                        } else {
                            QLog.l().logger().warn("Client is out of time: " + new Date());
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                });
                fr.addMouseListener(new MouseListener() {

                    long t = 0;

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("MouseEvent=" + e.getButton());
                        if (System.currentTimeMillis() - t < 5000) {
                            return;
                        }
                        t = System.currentTimeMillis();
                        final GregorianCalendar gc = new GregorianCalendar();
                        final long now =
                            gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc
                                .get(GregorianCalendar.MINUTE);
                        if (now > stime && now < ftime) {
                            final QService serv = addrs.get("" + e.getButton());
                            if (serv == null) {
                                QLog.l().logger()
                                    .error("Service was not found by " + e.getButton());
                                return;
                            }
                            final QCustomer customer;
                            try {
                                customer = NetCommander
                                    .standInService(netProperty, serv.getId(), "1", 1, "");
                            } catch (Exception ex) {
                                QLog.l().logger()
                                    .error(
                                        "Fail to put in line '" + serv.getName() + "'  ID=" + serv
                                            .getId(), ex);
                                return;
                            }
                            FWelcome.printTicket(customer, root.getName());

                        } else {
                            QLog.l().logger().warn("Client is out of time: " + new Date());
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });

                fr.setState(JFrame.NORMAL);
                fr.setVisible(true);
                final Timer t = new Timer(30000, (ActionEvent e) -> {
                    fr.setState(JFrame.NORMAL);
                    //fr.setVisible(false);
                    fr.setAlwaysOnTop(false);
                    fr.setAlwaysOnTop(true);
                    fr.setVisible(true);
                    fr.setAlwaysOnTop(true);
                    fr.toFront();
                    fr.requestFocus();
                    r.mouseMove(fr.getLocation().x + 3, fr.getLocation().y + 3);
                    r.mousePress(InputEvent.BUTTON1_MASK);
                });
                t.start();
                break;
                // ***************************************************************************************************************************************
            }
            case QConfig.KEY_WELCOME_BTN: {
                // ***************************************************************************************************************************************
                // ***  Это кнопочный терминал
                // ***************************************************************************************************************************************
                QLog.l().logger().info("Кнопочный режим пункта регистрации включен.");

                final GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(startTime);
                final long stime =
                    gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc.get(GregorianCalendar.MINUTE);
                gc.setTime(finishTime);
                final long ftime =
                    gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc.get(GregorianCalendar.MINUTE);

                final HashMap<Byte, QService> addrs = new HashMap<>();
                final File addrFile = new File("config/welcome_buttons.properties");
                try (FileInputStream fis = new FileInputStream(addrFile); Scanner s = new Scanner(
                    fis)) {
                    while (s.hasNextLine()) {
                        final String line = s.nextLine().trim();
                        if (!line.startsWith("#")) {
                            final String[] ss = line.split("=");
                            QServiceTree.sailToStorm(root, (TreeNode service) -> {
                                if (((QService) service).getId().equals(Long.valueOf(ss[1]))) {
                                    QLog.l().logger()
                                        .debug(ss[0] + " = " + ss[1] + " " + ((QService) service)
                                            .getName());
                                    addrs.put(Byte.valueOf(ss[0]), (QService) service);
                                }
                            });
                        }
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                    throw new RuntimeException(ex);
                }

                serialPort = new RxtxSerialPort(WelcomeParams.getInstance().buttons_COM);
                serialPort.setDataBits(WelcomeParams.getInstance().buttons_databits);
                serialPort.setParity(WelcomeParams.getInstance().buttons_parity);
                serialPort.setSpeed(WelcomeParams.getInstance().buttons_speed);
                serialPort.setStopBits(WelcomeParams.getInstance().buttons_stopbits);
                serialPort.bind(new IReceiveListener() {

                    @Override
                    public void actionPerformed(SerialPortEvent spe, byte[] bytes) {
                        final GregorianCalendar gc = new GregorianCalendar();
                        final long now =
                            gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gc
                                .get(GregorianCalendar.MINUTE);
                        if (now > stime && now < ftime) {
                            // поддержка расширяемости плагинами
                            Byte flag = null;
                            for (final IBytesButtensAdapter event : ServiceLoader
                                .load(IBytesButtensAdapter.class)) {
                                QLog.l().logger().info(
                                    "Вызов SPI расширения. Описание: " + event.getDescription());
                                try {
                                    flag = event.convert(bytes);
                                } catch (Throwable tr) {
                                    QLog.l().logger()
                                        .error(
                                            "Вызов SPI расширения завершился ошибкой. Описание: ",
                                            tr);
                                }
                                // раз конвертнули и хорошь
                                if (flag != null) {
                                    break;
                                }
                            }
                            if (flag != null || (bytes.length == 4 && bytes[0] == 0x01
                                && bytes[3] == 0x07)) {
                                final QService serv = addrs.get(flag != null ? flag : bytes[2]);
                                if (serv == null) {
                                    QLog.l().logger().error(
                                        "Не найдена услуга по нажатию кнопки " + (flag != null
                                            ? flag : bytes[2]));
                                    return;
                                }
                                final QCustomer customer;
                                try {
                                    customer = NetCommander
                                        .standInService(netProperty, serv.getId(), "1", 1, "");
                                } catch (Exception ex) {
                                    QLog.l().logger()
                                        .error(
                                            "Не поставлен в очередь в " + serv.getName() + "  ID="
                                                + serv.getId(),
                                            ex);
                                    return;
                                }
                                FWelcome.printTicket(customer, root.getName());
                            } else {
                                String s = "";
                                for (byte b : bytes) {
                                    s = s + (b & 0xFF) + "_";
                                }
                                QLog.l().logger()
                                    .error("Collision! Package lenght not 4 bytes or broken: \"" + s
                                        + "\"");
                            }
                        } else {
                            QLog.l().logger()
                                .warn(
                                    "Не поставлен в очередь т.к. не приемные часы в " + new Date());
                        }
                    }

                    @Override
                    public void actionPerformed(SerialPortEvent spe) {
                    }
                });
                int pos = 0;
                boolean exit = false;
                // индикатор
                while (!exit) {
                    Thread.sleep(1500);
                    // ждём нового подключения, после чего запускаем обработку клиента
                    // в новый вычислительный поток и увеличиваем счётчик на единичку

                    if (!QConfig.cfg().isDebug()) {
                        final char ch = '*';
                        String progres = "Process: " + ch;
                        final int len = 5;
                        for (int i = 0; i < pos; i++) {
                            progres = progres + ch;
                        }
                        for (int i = 0; i < len; i++) {
                            progres = progres + ' ';
                        }
                        if (++pos == len) {
                            pos = 0;
                        }
                        System.out.print(progres);
                        System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                    }

                    // Попробуем считать нажатую клавишу
                    // если нажади ENTER, то завершаем работу сервера
                    // и затираем файл временного состояния Uses.TEMP_STATE_FILE
                    //BufferedReader r = new BufferedReader(new StreamReader(System.in));
                    int bytesAvailable = System.in.available();
                    if (bytesAvailable > 0) {
                        byte[] data = new byte[bytesAvailable];
                        System.in.read(data);
                        if (bytesAvailable == 5
                            && data[0] == 101
                            && data[1] == 120
                            && data[2] == 105
                            && data[3] == 116
                            && ((data[4] == 10) || (data[4] == 13))) {
                            // набрали команду "exit" и нажали ENTER
                            QLog.l().logger().info("Завершение работы сервера.");
                            exit = true;
                        }
                    }
                }// while
                serialPort.free();
                break;
                // ***************************************************************************************************************************************
            }
            default: {
                // ***************************************************************************************************************************************
                // ***  Это тачевый терминал
                // ***************************************************************************************************************************************
                java.awt.EventQueue.invokeLater(() -> {
                    final FWelcome w = new FWelcome(root);
                    w.setVisible(true);
                });
                break;
            }
        }
    }

    private static synchronized int increaseTicketCount(int d) {
        File f = new File("temp");
        if (!f.exists()) {
            f.mkdir();
        }

        f = new File(TEMP_FILE_PROPS);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
        final Properties p = new Properties();
        try {
            p.load(new FileInputStream(f));
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
        int i = Math.max(Integer.parseInt(p.getProperty("tickets_cnt", "0").trim()) + d, 0);
        p.setProperty("tickets_cnt", String.valueOf(i));

        try {
            p.store(new FileOutputStream(f), "QSystem Welcome temp properties");
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
        // разошлем весточку о том что бумага заканчивается
        int st = (i - WelcomeParams.getInstance().paper_size_alarm) % WelcomeParams
            .getInstance().paper_alarm_step;
        if (0 <= st && st < d) {
            final String m = Mailer.fetchConfig().getProperty("paper_alarm_mailing", "0");
            if ("1".equals(m) || "true".equalsIgnoreCase(m)) {
                QLog.l().logger().info("QSystem. Paper is running out / израсходование бумаги. " + i
                    + " tickets were printed.");
                Mailer.sendReporterMailAtFon(Mailer.fetchConfig()
                        .getProperty("mail.paper_alarm_subject", "QSystem. Printing paper run out!"),
                    "QSystem. Paper is running out / израсходование бумаги. " + i
                        + " tickets were printed.",
                    Mailer.fetchConfig().getProperty("mail.smtp.paper_alarm_to"),
                    null);
            }
        }
        return i;
    }

    private static int write(Graphics2D g2, String text, int line, int x, double kx, double ky,
        int initY) {
        g2.scale(kx, ky);
        final int y = (int) Math
            .round((initY + line * WelcomeParams.getInstance().lineHeigth) / ky);
        g2.drawString(text, x, y);
        g2.scale(1 / kx, 1 / ky);
        return y;
    }

    /**
     * @param str text for positioning
     * @param alignment -1 left, 0 center, 1 right
     * @return coordinate X
     */
    private static int getHAlignment(Graphics2D g2, String str, int alignment, double kx) {
        if (alignment < 0) {
            return WelcomeParams.getInstance().leftMargin;
        }
        final int sw = (int) (CMP.getFontMetrics(g2.getFont()).stringWidth(str) * kx);
        int pos = alignment == 0 ? (WelcomeParams.getInstance().paperWidht - sw) / 2
            : (WelcomeParams.getInstance().paperWidht - sw);

        return (int) Math.round(pos / kx);
    }
    //==================================================================================================================

    private static int getAlign(String str, int alignmentDef) {
        if (str.toLowerCase().startsWith("[c")) {
            return 0;
        }
        if (str.toLowerCase().startsWith("[r")) {
            return 1;
        }
        if (str.toLowerCase().startsWith("[l")) {
            return -1;
        }
        return alignmentDef == -1 || alignmentDef == 0 || alignmentDef == 1 ? alignmentDef : -1;
    }

    private static String getTrim(String str) {
        return str.trim().replaceFirst("^\\[.+?\\]", "");
    }

    /**
     * @param alignment -1 left, 0 center, 1 right
     * @return new initY
     */
    private static int ptintLines(Graphics2D g2, String text, int alignment, double kx, double ky,
        int initY, int line) {
        final FontMetrics fm = CMP.getFontMetrics(g2.getFont());
        String capt = text;
        while (capt.length() != 0) {
            String prn;
            int leC = fm.stringWidth(capt);
            if (capt.length() > WelcomeParams.getInstance().lineLenght || leC > WelcomeParams
                .getInstance().paperWidht) {
                int fl = 0;

                int br = capt.toLowerCase().indexOf("<br>");
                if (br > 0 && br < WelcomeParams.getInstance().lineLenght) {
                    fl = br;
                }
                if (fl > 0) {
                    prn = capt.substring(0, fl).replaceFirst("<br>", "");
                    int le = fm.stringWidth(prn);
                    if (le > WelcomeParams.getInstance().paperWidht) {
                        fl = 0;
                    }
                }

                for (int i = Math.min(WelcomeParams.getInstance().lineLenght, capt.length());
                    i > 0 && fl == 0; i--) {

                    if (" ".equals(capt.substring(i - 1, i))) {
                        fl = i;
                        prn = capt.substring(0, fl);
                        int le = fm.stringWidth(prn);
                        if (le > WelcomeParams.getInstance().paperWidht) {
                            fl = 0;
                        } else {
                            break;
                        }
                    }
                }
                int pos = fl == 0 ? WelcomeParams.getInstance().lineLenght : fl;
                prn = capt.substring(0, pos).trim();
                capt = capt.substring(pos).trim();
                if (capt.toLowerCase().startsWith("<br>")) {
                    capt = capt.replaceFirst("<br>", "");
                }
            } else {
                prn = capt.trim();
                capt = "";
            }
            write(g2, getTrim(prn), line,
                (int) getHAlignment(g2, getTrim(prn), getAlign(prn, alignment), kx), kx, ky, initY);
            //System.out.println("-->" + prn + " / " + capt);
            int h = CMP.getFontMetrics(g2.getFont()).getHeight();
            if (!capt.isEmpty()) {
                initY = initY + Math
                    .round(new Float(h * (h > 30 ? (h > 60 ? 0.65 : 0.7) : (h > 10 ? 0.83 : 1))));
            }
        }
        return initY;
    }

    public static void printTicket(QCustomer customer, String caption) {
        FWelcome.caption = ".".equals(caption) ? "" : caption;
        printTicket(customer);
    }

    public static synchronized void printTicket(final QCustomer customer) {
        increaseTicketCount(1);
        // поддержка расширяемости плагинами
        boolean flag = false;
        for (final IPrintTicket event : ServiceLoader.load(IPrintTicket.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                flag = event.printTicket(customer, FWelcome.caption);
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: ", tr);
            }
            // раз напечатили и хорошь
            if (flag) {
                return;
            }
        }

        final Printable canvas = new Printable() {

            private final JLabel comp = new JLabel();
            Graphics2D g2;
            private int initY = WelcomeParams.getInstance().topMargin;

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
                initY = WelcomeParams.getInstance().topMargin;
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                g2 = (Graphics2D) graphics;
                final Font f_standard;
                if (WelcomeParams.getInstance().ticketFontName != null && !WelcomeParams
                    .getInstance().ticketFontName.isEmpty()) {
                    f_standard = (new Font(WelcomeParams.getInstance().ticketFontName,
                        g2.getFont().getStyle(),
                        WelcomeParams.getInstance().ticketFontSize > 2 ? WelcomeParams
                            .getInstance().ticketFontSize : g2.getFont().getSize()));
                } else {
                    f_standard = g2.getFont();
                }
                g2.setFont(f_standard);
                g2.drawLine(WelcomeParams.getInstance().paperWidht + 20, 0,
                    WelcomeParams.getInstance().paperWidht + 20, 20);
                if (WelcomeParams.getInstance().logo) {
                    g2.drawImage(Uses.loadImage(this, WelcomeParams.getInstance().logoImg,
                        "/ru/apertum/qsystem/client/forms/resources/logo_ticket_a.png"),
                        WelcomeParams.getInstance().logoLeft, WelcomeParams.getInstance().logoTop,
                        null);
                }
                g2.scale(WelcomeParams.getInstance().scaleHorizontal,
                    WelcomeParams.getInstance().scaleVertical);
                //позиционируем начало координат
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int line = 0;

                if (caption != null && !caption.isEmpty()) {
                    line = 1;
                    g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(),
                        WelcomeParams.getInstance().ticketFontH2Size));
                    initY = ptintLines(g2, caption, 0, 1, 1, initY, line);
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                }
                g2.setFont(f_standard);
                write(g2, getLocaleMessage("ticket.your_number"), ++line,
                    getHAlignment(g2, getLocaleMessage("ticket.your_number"), 0, 1), 1, 1, initY);

                g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(),
                    WelcomeParams.getInstance().ticketFontH1Size));
                int h = comp.getFontMetrics(g2.getFont()).getHeight();
                initY = initY + Math.round(new Float(h * (h > 30 ? (h > 60 ? 0.65 : 0.7) : 1)));
                final String num =
                    customer.getPrefix() + QConfig.cfg().getNumDivider(customer.getPrefix())
                        + customer
                        .getNumber();
                write(g2, num, line, getHAlignment(g2, num, 0, 1), 1, 1, initY);
                initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                g2.setFont(f_standard);

                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                write(g2, getLocaleMessage("ticket.service"), ++line,
                    WelcomeParams.getInstance().leftMargin, 1, 1, initY);
                g2.setFont(f_standard);
                String name = customer.getService().getTextToLocale(QService.Field.NAME);
                initY = ptintLines(g2, name, -1, 1, 1, initY, ++line);

                initY = initY + WelcomeParams.getInstance().lineHeigth / 3;

                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                write(g2, getLocaleMessage("ticket.time"), ++line,
                    WelcomeParams.getInstance().leftMargin,
                    1, 1, initY);
                g2.setFont(f_standard);

                write(g2,
                    Locales.getInstance().isRuss ? Uses
                        .getRusDate(customer.getStandTime(), "dd MMMM HH:mm")
                        : (Locales.getInstance().isUkr ? Uses
                            .getUkrDate(customer.getStandTime(), "dd MMMM HH:mm")
                            : Locales.getInstance().format_for_label
                                .format(customer.getStandTime())),
                    ++line, WelcomeParams.getInstance().leftMargin, 1, 1, initY);
                // если клиент что-то ввел, то напечатаем это на его талоне
                if (customer.getService().getInput_required()) {
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                    g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                    write(g2, customer.getService().getTextToLocale(QService.Field.INPUT_CAPTION)
                            .replaceAll("<.*?>", ""), ++line, WelcomeParams.getInstance().leftMargin, 1,
                        1,
                        initY);
                    g2.setFont(f_standard);
                    write(g2, customer.getInput_data(), ++line,
                        WelcomeParams.getInstance().leftMargin, 1, 1,
                        initY);
                    // если требуется, то введеное напечатаем как qr-код для быстрого считывания сканером
                    if (WelcomeParams.getInstance().input_data_qrcode) {
                        try {
                            final int matrixWidth = 130;
                            final HashMap<EncodeHintType, String> hints = new HashMap();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            final BitMatrix matrix = new QRCodeWriter()
                                .encode(customer.getInput_data(), BarcodeFormat.QR_CODE,
                                    matrixWidth, matrixWidth,
                                    hints);
                            //final BitMatrix matrix = new MultiFormatWriter().encode(customer.getInput_data(), BarcodeFormat.QR_CODE, matrixWidth, matrixWidth);
                            //Write Bit Matrix as image
                            final int y = (int) Math.round(
                                (WelcomeParams.getInstance().topMargin + line * WelcomeParams
                                    .getInstance().lineHeigth) / 1);
                            for (int i = 0; i < matrixWidth; i++) {
                                for (int j = 0; j < matrixWidth; j++) {
                                    if (matrix.get(i, j)) {
                                        g2.fillRect(WelcomeParams.getInstance().leftMargin * 2 + i,
                                            y + j - 10, 1, 1);
                                    }
                                }
                            }
                            line = line + 9;
                        } catch (WriterException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода QR. " + ex);
                        }
                    }
                }
                // если в услуге есть что напечатать на талоне, то напечатаем это на его талоне
                if (customer.getService().getTextToLocale(QService.Field.TICKET_TEXT) != null
                    && !customer
                    .getService().getTextToLocale(QService.Field.TICKET_TEXT).isEmpty()) {
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                    String tt = customer.getService().getTextToLocale(QService.Field.TICKET_TEXT);
                    initY = ptintLines(g2, tt, -1, 1, 1, initY, ++line);
                }

                String wText =
                    WelcomeParams.getInstance().waitText == null || WelcomeParams
                        .getInstance().waitText
                        .isEmpty() ? ("[c]" + getLocaleMessage("ticket.wait"))
                        : WelcomeParams.getInstance().waitText;
                if (wText != null && !wText.trim().isEmpty() && !".".equals(wText)) {
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                    write(g2, getTrim(wText), ++line,
                        getHAlignment(g2, getTrim(wText), getAlign(wText, -1), 1.45), 1.45, 1,
                        initY);
                }
                wText = WelcomeParams.getInstance().promoText;
                if (wText != null && !wText.isEmpty() && !".".equals(wText)) {
                    write(g2, getTrim(wText), ++line,
                        getHAlignment(g2, getTrim(wText), getAlign(wText, -1), 0.7), 0.7, 0.4,
                        initY);
                }

                if (WelcomeParams.getInstance().barcode != 0) {
                    int y = write(g2, "", ++line, 0, 1, 1, initY);

                    if (WelcomeParams.getInstance().barcode == 2) {
                        try {
                            final int matrixWidth = 100;
                            final HashMap<EncodeHintType, String> hints = new HashMap();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            final BitMatrix matrix = new QRCodeWriter()
                                .encode(customer.getId().toString(), BarcodeFormat.QR_CODE,
                                    matrixWidth,
                                    matrixWidth, hints);
                            //Write Bit Matrix as image
                            for (int i = 0; i < matrixWidth; i++) {
                                for (int j = 0; j < matrixWidth; j++) {
                                    if (matrix.get(i, j)) {
                                        g2.fillRect(WelcomeParams.getInstance().leftMargin * 2 + i,
                                            y + j - 18, 1, 1);
                                    }
                                }
                            }
                            line = line + 6;
                        } catch (WriterException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода QR. " + ex);
                        }
                    }

                    if (WelcomeParams.getInstance().barcode == 1) {
                        try {
                            final Barcode barcode = BarcodeFactory
                                .createCode128B(customer.getId().toString());
                            barcode.setBarHeight(5);
                            barcode.setBarWidth(1);
                            barcode.setDrawingText(false);
                            barcode.setDrawingQuietSection(false);
                            barcode
                                .draw(g2, (WelcomeParams.getInstance().paperWidht - barcode
                                        .getSize().width) / 2,
                                    y - 7);
                            line = line + 2;
                        } catch (BarcodeException | OutputException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода 128B. " + ex);
                        }
                    }
                }

                //Напечатаем текст внизу билета
                name = WelcomeParams.getInstance().bottomText;
                int al = getAlign(name, -1);
                name = getTrim(name);
                if (name != null && !name.isEmpty() && !".".equals(name)) {
                    initY = ptintLines(g2, name, al, 1, 1, initY, ++line);
                }
                if (WelcomeParams.getInstance().bottomGap > 0) {
                    write(g2, ".", ++line + WelcomeParams.getInstance().bottomGap, 0, 1, 1, initY);
                }

                return Printable.PAGE_EXISTS;
            }
        };
        final PrinterJob job = PrinterJob.getPrinterJob();
        if (WelcomeParams.getInstance().printService != null) {
            try {
                job.setPrintService(WelcomeParams.getInstance().printService);
            } catch (PrinterException ex) {
                QLog.l().logger().error("Ошибка установки принтера: ", ex);
            }
        }
        job.setPrintable(canvas);
        try {
            job.print(WelcomeParams.getInstance().printAttributeSet);
            //job.print();
        } catch (PrinterException ex) {
            QLog.l().logger().error("Ошибка печати: ", ex);
        }
    }

    public static void printTicketAdvance(QAdvanceCustomer advCustomer, String caption) {
        FWelcome.caption = ".".equals(caption) ? "" : caption;
        printTicketAdvance(advCustomer);
    }

    public static synchronized void printTicketAdvance(final QAdvanceCustomer advCustomer) {
        increaseTicketCount(1);
        // поддержка расширяемости плагинами
        boolean flag = false;
        for (final IPrintTicket event : ServiceLoader.load(IPrintTicket.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                flag = event.printTicketAdvance(advCustomer, FWelcome.caption);
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
            // раз напечатили и хорошь
            if (flag) {
                return;
            }
        }

        final Printable canvas = new Printable() {
            private final JLabel comp = new JLabel();
            Graphics2D g2;
            private int initY = WelcomeParams.getInstance().topMargin;

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
                initY = WelcomeParams.getInstance().topMargin;
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                g2 = (Graphics2D) graphics;
                final Font f_standard;
                if (WelcomeParams.getInstance().ticketFontName != null && !WelcomeParams
                    .getInstance().ticketFontName.isEmpty()) {
                    f_standard = (new Font(WelcomeParams.getInstance().ticketFontName,
                        g2.getFont().getStyle(),
                        WelcomeParams.getInstance().ticketFontSize > 2 ? WelcomeParams
                            .getInstance().ticketFontSize : g2.getFont().getSize()));
                } else {
                    f_standard = g2.getFont();
                }
                g2.setFont(f_standard);
                g2.drawLine(WelcomeParams.getInstance().paperWidht + 20, 0,
                    WelcomeParams.getInstance().paperWidht + 20, 20);

                if (WelcomeParams.getInstance().logo) {
                    g2.drawImage(Uses.loadImage(this, WelcomeParams.getInstance().logoImg,
                        "/ru/apertum/qsystem/client/forms/resources/logo_ticket_a.png"),
                        WelcomeParams.getInstance().logoLeft, WelcomeParams.getInstance().logoTop,
                        null);
                }
                g2.scale(WelcomeParams.getInstance().scaleHorizontal,
                    WelcomeParams.getInstance().scaleVertical);
                //позиционируем начало координат
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int line = 0;

                if (caption != null && !caption.isEmpty()) {
                    line = 1;
                    g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(),
                        WelcomeParams.getInstance().ticketFontH2Size));
                    initY = ptintLines(g2, caption, 0, 1, 1, initY, line);
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                }

                g2.setFont(f_standard);
                write(g2, getLocaleMessage("ticket.adv_purpose"), ++line,
                    getHAlignment(g2, getLocaleMessage("ticket.adv_purpose"), 0, 1), 1, 1, initY);

                final GregorianCalendar gc_time = new GregorianCalendar();
                gc_time.setTime(advCustomer.getAdvanceTime());
                int t = gc_time.get(GregorianCalendar.HOUR_OF_DAY);
                String t_m = ("" + gc_time.get(GregorianCalendar.MINUTE) + "0000").substring(0, 2);
                if (t == 0) {
                    t = 24;
                    gc_time.add(GregorianCalendar.HOUR_OF_DAY, -1);
                }
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                String tx = Locales.getInstance().isRuss ? Uses
                    .getRusDate(gc_time.getTime(), Locales.getInstance().DATE_FORMAT_FULL)
                    : Locales.getInstance().format_dd_MMMM_yyyy.format(gc_time.getTime());
                write(g2, tx, ++line, getHAlignment(g2, tx, 0, 1), 1, 1, initY);
                //write(FWelcome.getLocaleMessage("qbutton.take_adv_ticket_from") + " " + (t) + ":00 " + FWelcome.getLocaleMessage("qbutton.take_adv_ticket_to") + " " + (t + 1) + ":00", ++line + 1, WelcomeParams.getInstance().leftMargin, 2, 1);
                tx = FWelcome.getLocaleMessage("qbutton.take_adv_ticket_come_to") + " " + (t) + ":"
                    + t_m;
                write(g2, tx, ++line, getHAlignment(g2, tx, 0, 1), 1, 1, initY);
                g2.setFont(f_standard);
                initY = initY + WelcomeParams.getInstance().lineHeigth / 3;

                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                write(g2, getLocaleMessage("ticket.service"), ++line,
                    WelcomeParams.getInstance().leftMargin, 1, 1, initY);
                g2.setFont(f_standard);
                String name = advCustomer.getService().getTextToLocale(QService.Field.NAME);
                initY = ptintLines(g2, name, -1, 1, 1, initY, ++line);

                initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                write(g2, getLocaleMessage("ticket.reg_time"), ++line,
                    WelcomeParams.getInstance().leftMargin, 1, 1, initY);
                g2.setFont(f_standard);

                write(g2,
                    Locales.getInstance().isRuss ? Uses.getRusDate(new Date(), "dd MMMM HH:mm")
                        : Locales.getInstance().format_for_label.format(new Date()), ++line,
                    WelcomeParams.getInstance().leftMargin, 1, 1, initY);

                // если клиент что-то ввел, то напечатаем это на его талоне
                if (advCustomer.getService().getInput_required()) {
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                    g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, g2.getFont().getSize()));
                    write(g2, advCustomer.getService().getTextToLocale(QService.Field.INPUT_CAPTION)
                            .replaceAll("<.*?>", ""), ++line, WelcomeParams.getInstance().leftMargin, 1,
                        1,
                        initY);
                    g2.setFont(f_standard);
                    write(g2, advCustomer.getInputData(), ++line,
                        WelcomeParams.getInstance().leftMargin, 1,
                        1, initY);
                    // если требуется, то введеное напечатаем как qr-код для быстрого считывания сканером
                    if (WelcomeParams.getInstance().input_data_qrcode) {
                        try {
                            final int matrixWidth = 130;
                            final HashMap<EncodeHintType, String> hints = new HashMap();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            final BitMatrix matrix = new QRCodeWriter()
                                .encode(advCustomer.getAuthorizationCustomer().getName(),
                                    BarcodeFormat.QR_CODE,
                                    matrixWidth, matrixWidth, hints);
                            //final BitMatrix matrix = new MultiFormatWriter().encode(customer.getInput_data(), BarcodeFormat.QR_CODE, matrixWidth, matrixWidth);
                            //Write Bit Matrix as image
                            final int y = (int) Math.round(
                                (WelcomeParams.getInstance().topMargin + line * WelcomeParams
                                    .getInstance().lineHeigth) / 1);
                            for (int i = 0; i < matrixWidth; i++) {
                                for (int j = 0; j < matrixWidth; j++) {
                                    if (matrix.get(i, j)) {
                                        g2.fillRect(WelcomeParams.getInstance().leftMargin * 2 + i,
                                            y + j - 10, 1, 1);
                                    }
                                }
                            }
                            line = line + 9;
                        } catch (WriterException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода QR. " + ex);
                        }
                    }
                }

                // если в услуге есть что напечатать на талоне, то напечатаем это на его талоне
                if (advCustomer.getService().getTextToLocale(QService.Field.TICKET_TEXT) != null
                    && !advCustomer.getService().getTextToLocale(QService.Field.TICKET_TEXT)
                    .isEmpty()) {
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                    String tt = advCustomer.getService()
                        .getTextToLocale(QService.Field.TICKET_TEXT);
                    initY = ptintLines(g2, tt, -1, 1, 1, initY, ++line);
                }

                write(g2, getLocaleMessage("ticket.adv_code"), ++line,
                    getHAlignment(g2, getLocaleMessage("ticket.adv_code"), 0, 1), 1, 1, initY);
                int y = write(g2, "", ++line, 0, 1, 1, initY);
                if (WelcomeParams.getInstance().barcode != 0) {

                    if (WelcomeParams.getInstance().barcode == 2) {
                        try {
                            final int matrixWidth = 100;
                            final HashMap<EncodeHintType, String> hints = new HashMap();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            final BitMatrix matrix = new QRCodeWriter()
                                .encode(advCustomer.getId().toString(), BarcodeFormat.QR_CODE,
                                    matrixWidth,
                                    matrixWidth, hints);
                            //Write Bit Matrix as image
                            for (int i = 0; i < matrixWidth; i++) {
                                for (int j = 0; j < matrixWidth; j++) {
                                    if (matrix.get(i, j)) {
                                        g2.fillRect(WelcomeParams.getInstance().leftMargin * 2 + i,
                                            y + j - 18, 1, 1);
                                    }
                                }
                            }
                            line = line + 6;
                            write(g2, advCustomer.getId().toString(), ++line,
                                getHAlignment(g2, advCustomer.getId().toString(), 0, 2), 2.0, 1.7,
                                initY);
                        } catch (WriterException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода QR. " + ex);
                        }
                    }

                    if (WelcomeParams.getInstance().barcode == 1) {
                        try {
                            final Barcode barcode = BarcodeFactory
                                .createCode128B(advCustomer.getId().toString());
                            barcode.setBarHeight(5);
                            barcode.setBarWidth(1);
                            barcode.setDrawingText(true);
                            barcode.setDrawingQuietSection(true);
                            barcode
                                .draw(g2, (WelcomeParams.getInstance().paperWidht - barcode
                                        .getSize().width) / 2,
                                    y - 7);
                            line = line + 3;
                        } catch (BarcodeException | OutputException ex) {
                            QLog.l().logger().error("Ошибка вывода штрихкода 128B. " + ex);
                        }
                    }
                } else {
                    //write(advCustomer.getId().toString(), ++line, WelcomeParams.getInstance().leftMargin, 2.0, 1.7);
                    initY = ptintLines(g2, advCustomer.getId().toString(), 0, 2.0, 1.7, initY,
                        ++line);
                }

                String wText = WelcomeParams.getInstance().promoText;
                if (wText != null && !wText.isEmpty() && !".".equals(wText)) {
                    write(g2, getTrim(wText), ++line,
                        getHAlignment(g2, getTrim(wText), getAlign(wText, -1), 0.7), 0.7, 0.4,
                        initY);
                }
                //Напечатаем текст внизу билета

                name = WelcomeParams.getInstance().bottomText;
                int al = getAlign(name, -1);
                name = getTrim(name);
                if (name != null && !name.isEmpty() && !".".equals(name)) {
                    initY = ptintLines(g2, name, al, 1, 1, initY, ++line);
                }

                if (WelcomeParams.getInstance().bottomGap > 0) {
                    write(g2, ".", ++line + WelcomeParams.getInstance().bottomGap, 0, 1, 1, initY);
                }

                return Printable.PAGE_EXISTS;
            }
        };
        final PrinterJob job = PrinterJob.getPrinterJob();
        if (WelcomeParams.getInstance().printService != null) {
            try {
                job.setPrintService(WelcomeParams.getInstance().printService);
            } catch (PrinterException ex) {
                QLog.l().logger().error("Ошибка установки принтера: ", ex);
            }
        }
        job.setPrintable(canvas);
        try {
            job.print(WelcomeParams.getInstance().printAttributeSet);
            //job.print();
        } catch (PrinterException ex) {
            QLog.l().logger().error("Ошибка печати: ", ex);
        }
    }

    public static synchronized void printPreInfoText(final String preInfo) {
        increaseTicketCount(2);
        Printable canvas = new Printable() {
            Graphics2D g2;
            private int initY = WelcomeParams.getInstance().topMargin;

            private int write(String text, int line, int x, double kx, double ky, int pageIndex) {

                if (line <= pageIndex * WelcomeParams.getInstance().pageLinesCount
                    || line > (pageIndex + 1) * WelcomeParams.getInstance().pageLinesCount) {
                    return 0;
                }
                System.out.println(text);
                g2.scale(kx, ky);
                final int y = (int) Math
                    .round((initY + line * WelcomeParams.getInstance().lineHeigth) / ky);
                g2.drawString(text, x, y);
                g2.scale(1 / kx, 1 / ky);
                return y;
            }

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
                initY = WelcomeParams.getInstance().topMargin;

                g2 = (Graphics2D) graphics;
                final Font f_standard;
                if (WelcomeParams.getInstance().ticketFontName != null && !WelcomeParams
                    .getInstance().ticketFontName.isEmpty()) {
                    f_standard = (new Font(WelcomeParams.getInstance().ticketFontName,
                        g2.getFont().getStyle(),
                        WelcomeParams.getInstance().ticketFontSize > 2 ? WelcomeParams
                            .getInstance().ticketFontSize : g2.getFont().getSize()));
                } else {
                    f_standard = g2.getFont();
                }
                g2.setFont(f_standard);
                g2.drawLine(WelcomeParams.getInstance().paperWidht + 20, 0,
                    WelcomeParams.getInstance().paperWidht + 20, 20);
                if (WelcomeParams.getInstance().logo) {
                    g2.drawImage(Uses.loadImage(this, WelcomeParams.getInstance().logoImg,
                        "/ru/apertum/qsystem/client/forms/resources/logo_ticket_a.png"),
                        WelcomeParams.getInstance().logoLeft, WelcomeParams.getInstance().logoTop,
                        null);
                }
                g2.scale(WelcomeParams.getInstance().scaleHorizontal,
                    WelcomeParams.getInstance().scaleVertical);
                //позиционируем начало координат
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int line = 0;

                if (caption != null && !caption.isEmpty()) {
                    line = 1;
                    g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(),
                        WelcomeParams.getInstance().ticketFontH2Size));
                    initY = ptintLines(g2, caption, 0, 1, 1, initY, line);
                    initY = initY + WelcomeParams.getInstance().lineHeigth / 3;
                }
                g2.setFont(f_standard);

                // напечатаем текст подсказки
                final LinkedList<String> strings = new LinkedList<>();
                final Scanner sc = new Scanner(preInfo.replace("<br>", "\n"));
                while (sc.hasNextLine()) {
                    final String w = sc.nextLine();
                    strings.add(w);
                    //line = writeText(w, line, WelcomeParams.getInstance().leftMargin, 1, 1, pageIndex);
                }
                for (String string : strings) {
                    String srt = string;
                    int al = getAlign(srt, -1);
                    srt = getTrim(srt);
                    if (srt != null && !srt.isEmpty() && !".".equals(srt)) {
                        initY = ptintLines(g2, srt, al, 1, 1, initY, ++line);
                    }
                }

                String wText = WelcomeParams.getInstance().promoText;
                if (wText != null && !wText.isEmpty()) {
                    write(getTrim(wText), ++line,
                        getHAlignment(g2, getTrim(wText), getAlign(wText, -1), 0.7),
                        0.7, 0.4, pageIndex);
                }

                //Напечатаем текст внизу билета
                String name = WelcomeParams.getInstance().bottomText;
                int al = getAlign(name, -1);
                name = getTrim(name);
                if (name != null && !name.isEmpty() && !".".equals(name)) {
                    initY = ptintLines(g2, name, al, 1, 1, initY, ++line);
                }
                if (WelcomeParams.getInstance().bottomGap > 0) {
                    write(".", ++line + WelcomeParams.getInstance().bottomGap, 0, 1, 1, pageIndex);
                }

                if ((pageIndex + 0) * WelcomeParams.getInstance().pageLinesCount > line) {
                    return Printable.NO_SUCH_PAGE;
                } else {
                    return Printable.PAGE_EXISTS;
                }
            }
        };
        final PrinterJob job = PrinterJob.getPrinterJob();
        if (WelcomeParams.getInstance().printService != null) {
            try {
                job.setPrintService(WelcomeParams.getInstance().printService);
            } catch (PrinterException ex) {
                QLog.l().logger().error("Ошибка установки принтера: ", ex);
            }
        }
        job.setPrintable(canvas);
        try {
            job.print(WelcomeParams.getInstance().printAttributeSet);
        } catch (PrinterException ex) {
            QLog.l().logger().error("Ошибка печати: ", ex);
        }
    }

    public static boolean isAdvanceRegim() {
        return advanceRegim;
    }

    /**
     * Переключение режима постановки в очередь и предварительной записи
     *
     * @param advanceRegim true - предварительная запись, false - встать в очередь
     */
    public void setAdvanceRegim(boolean advanceRegim) {
        FWelcome.advanceRegim = advanceRegim;
        // \|/ нарисуем кнопки первого экрана // раньше тут был buttonToBeginActionPerformed(null)
        showButtons(root, panelMain);
        current = root;
        // /|\ нарисуем кнопки первого экрана // раньше тут был buttonToBeginActionPerformed(null)
        if (advanceRegim) {
            labelCaption.setText(WelcomeParams.getInstance().patternPickAdvanceTitle.
                replace("dialog_text.part1", getLocaleMessage("messages.select_adv_servece1")).
                replace("dialog_text.part2", getLocaleMessage("messages.select_adv_servece2")));

            buttonAdvance.setText("<html><p align=center>" + getLocaleMessage("lable.reg_calcel"));
            //возврат в начальное состояние из диалога предварительной записи.
            if (clockBack.isActive()) {
                clockBack.stop();
            }
            clockBack.start();
        } else {
            if (clockBack.isActive()) {
                clockBack.stop();
            }
            labelCaption.setText(root.getButtonText());
            buttonAdvance.setText("<html><p align=center>" + getLocaleMessage("lable.adv_reg"));
        }
        //кнопка регистрации пришедших которые записались давно видна только в стандартном режиме и вместе с кнопкой предварительной записи
        if (buttonAdvance.isVisible()) {
            buttonStandAdvance.setVisible(!advanceRegim);
        }
    }

    private void init(QService root) {
        QLog.l().logger().info("Создаем окно приглашения.");
        setLocation(0, 0);
        if (!QConfig.cfg().isDebug()) {
            if (!QConfig.cfg().isDemo()) {
                setUndecorated(true);
                //setAlwaysOnTop(true);
                //setResizable(false);

                // спрячем курсор мыши
                if (QConfig.cfg().isHideCursor()) {
                    final int[] pixels = new int[16 * 16];
                    final Image image = Toolkit.getDefaultToolkit()
                        .createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                    Cursor transparentCursor = Toolkit.getDefaultToolkit()
                        .createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                    setCursor(transparentCursor);
                }
            }
            addWindowListener(new WindowAdapter() {

                @Override
                public void windowOpened(WindowEvent e) {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });
        }

        if (QConfig.cfg().isDemo()) {
            addWindowListener(new WindowAdapter() {

                @Override
                public void windowOpened(WindowEvent e) {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });
        }
        initComponents();
        if (WelcomeParams.getInstance().topSize >= 0) {
            panelCaption.setPreferredSize(
                new Dimension(panelCaption.getWidth(), WelcomeParams.getInstance().topSize));
        }
        try {
            // пиктограмки на кнопках "назад" и "в начало"
            if (WelcomeParams.getInstance().buttonGoBackImg != null) {
                buttonBack
                    .setIcon(
                        new ImageIcon(WelcomeParams.getInstance().buttonGoBackImg.toURI().toURL()));
            }
            if (WelcomeParams.getInstance().buttonToStratImg != null) {
                buttonToBegin
                    .setIcon(new ImageIcon(
                        WelcomeParams.getInstance().buttonToStratImg.toURI().toURL()));
            }
        } catch (MalformedURLException ex) {
            System.err.println("Button icons! " + ex);
        }
        //На верхней панели пункта регистрации, там где заголовок и картинка в углу, можно вывести вэб-контент по URL. Оставьте пустым если не требуется
        if (!WelcomeParams.getInstance().topURL.isEmpty()) {
            panelCaption.removeAll();
            final BrowserFX bro = new BrowserFX();
            final GridLayout gl = new GridLayout(1, 1);
            panelCaption.setLayout(gl);
            panelCaption.add(bro);
            bro.load(Uses.prepareAbsolutPathForImg(WelcomeParams.getInstance().topURL));
        }
        try {
            setIconImage(ImageIO.read(
                FAdmin.class
                    .getResource("/ru/apertum/qsystem/client/forms/resources/checkIn.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        if (QConfig.cfg().isDebug()) {
            setSize(1280, 768);
            //setSize(800, 1280);
        }
        FWelcome.root = root;
        FWelcome.current = root;
        FWelcome.response = null;
        FWelcome.infoTree = null;
        try {
            loadRootParam();
        } catch (Exception ex) {
            QLog.l().logger().error(ex);
            System.exit(0);
        }
        server.start();
        if (!(Uses.FORMAT_HH_MM.format(finishTime).equals(Uses.FORMAT_HH_MM.format(startTime)))) {
            lockWelcome.start();
        }

        if (WelcomeParams.getInstance().btnAdvFont != null) {
            buttonStandAdvance.setFont(WelcomeParams.getInstance().btnAdvFont);
            buttonAdvance.setFont(WelcomeParams.getInstance().btnAdvFont);
            buttonBackPage.setFont(WelcomeParams.getInstance().btnAdvFont);
            buttonForwardPage.setFont(WelcomeParams.getInstance().btnAdvFont);
        }
        if (WelcomeParams.getInstance().btnFont != null) {
            buttonBack.setFont(WelcomeParams.getInstance().btnFont);
            buttonToBegin.setFont(WelcomeParams.getInstance().btnFont);
        }
        /*
         * Кнопки открываются по настройке
         */
        buttonInfo.setVisible(WelcomeParams.getInstance().info);
        buttonResponse.setVisible(WelcomeParams.getInstance().response);
        if (!"".equals(WelcomeParams.getInstance().infoHtml)) {
            buttonInfo.setText(WelcomeParams.getInstance().infoHtml); // NOI18N
        }
        if (!"".equals(WelcomeParams.getInstance().responseHtml)) {
            buttonResponse.setText(WelcomeParams.getInstance().responseHtml); // NOI18N
        }
        buttonAdvance.setVisible(WelcomeParams.getInstance().advance);
        buttonStandAdvance.setVisible(WelcomeParams.getInstance().standAdvance);
        panelLngs.setVisible(Locales.getInstance().isWelcomeMultylangs());
        if (Locales.getInstance().isWelcomeMultylangs()) {
            FlowLayout la = new FlowLayout(Locales.getInstance().getMultylangsPosition(), 50, 10);
            panelLngs.setLayout(la);
            Locales.getInstance().getAvailableLangs().stream().map((lng) -> {
                final JButton btn = new JButton(
                    Uses.prepareAbsolutPathForImg(Locales.getInstance().getLangButtonText(lng)));
                btn.setContentAreaFilled(Locales.getInstance().isWelcomeMultylangsButtonsFilled());
                btn.setFocusPainted(false);
                btn.setBorderPainted(Locales.getInstance().isWelcomeMultylangsButtonsBorder());
                btn.addActionListener(new LngBtnAction(Locales.getInstance().getLocaleByName(lng)));
                btn.setVisible("1".equals(Locales.getInstance().getLangWelcome(lng)));
                return btn;
            }).forEach((btn) -> {
                panelLngs.add(btn);
            });
        }
        showMed();
        // Если режим инфокиоска, то не показываем кнопки предвариловки
        // Показали информацию и все
        if (FWelcome.isInfo) {
            buttonAdvance.setVisible(false);
            buttonStandAdvance.setVisible(false);
        }
    }

    /**
     * Загрузка и инициализация неких параметров из корня дерева описания для старта или
     * реинициализации.
     */
    private void loadRootParam() {
        FWelcome.caption = root.getTextToLocale(QService.Field.NAME);
        FWelcome.caption = ".".equals(FWelcome.caption) ? "" : FWelcome.caption;
        labelCaption
            .setText(
                Uses.prepareAbsolutPathForImg(root.getTextToLocale(QService.Field.BUTTON_TEXT)));
        setStateWindow(UNLOCK);
        showButtons(root, panelMain);
    }

    public final void showMed() {
        if (isMed) {
            final ATalkingClock cl = new ATalkingClock(10, 1) {

                @Override
                public void run() {
                    if (!FMedCheckIn.isShowen()) {
                        final QAuthorizationCustomer customer = FMedCheckIn
                            .showMedCheckIn(null, true, netProperty, false, serialPort);
                        if (customer != null) {
                            advancedCustomer = customer.getId();
                            setAdvanceRegim(true);
                            labelCaption.setText(
                                "<html><p align=center><span style='font-size:55.0pt;color:green'>"
                                    + customer
                                    .getSurname() + " " + customer.getName() + " " + customer
                                    .getOtchestvo()
                                    + "<br></span><span style='font-size:40.0pt;color:red'>"
                                    + getLocaleMessage(
                                    "messages.select_adv_servece"));
                        } else {
                            throw new ClientException(
                                "Нельзя выбирать услугу если не идентифицирован клиент.");
                        }
                    }
                }
            };
            cl.start();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        off();
        lockWelcome.stop();
        try {
            if (serialPort != null) {
                serialPort.free();
            }
        } catch (Exception ex) {
            throw new ClientException("Ошибка освобождения порта. " + ex);
        }
        super.finalize();
    }

    /**
     * Создаем и расставляем кнопки по форме.
     *
     * @param current уровень отображения кнопок.
     */
    public void showButtons(QService current, JPanel panel) {

        QLog.l().logger()
            .info(
                "Показываем набор кнопок уровня: " + current.getName() + " ID=" + current.getId());
        if (current
            != FWelcome.current) { // если смена уровней то страница уровня становится нулевая
            pageNumber = 0;
        }

        // картинки для подложки с каждым набором кнопок из WelcomeBGparams. По дефолту из welcome.properties
        ((QPanel) panelBackground)
            .setBackgroundImgage(WelcomeBGparams.getInstance().getImg(current.getId()));

        if (current != root && current.getParent() == null) {
            current.setParent(FWelcome.current);
        }
        FWelcome.current = current;

        clearPanel(panel);
        int delta = 10;
        switch (Toolkit.getDefaultToolkit().getScreenSize().width) {
            case 640:
                delta = 10;
                break;
            case 800:
                delta = 20;
                break;
            case 1024:
                delta = 30;
                break;
            case 1366:
                delta = 25;
                break;
            case 1280:
                delta = 40;
                break;
            case 1600:
                delta = 50;
                break;
            case 1920:
                delta = 60;
                break;
        }
        if (QConfig.cfg().isDebug() || QConfig.cfg().isDemo()) {
            delta = 25;
        }
        int cols = 3;
        int rows = 5;

        // посмотрим сколько реальных кнопок нужно отобразить
        // тут есть невидимые услуги и услуги не с того киоска
        int childCount = 0;
        childCount = current.getChildren().stream().filter(
            (service) -> (!(isAdvanceRegim() && service.getAdvanceLimit() == 0)
                && service.getStatus() != -1 && (WelcomeParams.getInstance().point == 0 || (
                service.getPoint() == 0 || service.getPoint() == WelcomeParams
                    .getInstance().point))))
            .map((_item) -> 1).reduce(childCount, Integer::sum);

        if (childCount <= WelcomeParams.getInstance().oneColumnButtonCount) {
            cols = 1;
            rows = childCount < 3 ? 3 : childCount;
        }
        if (childCount > WelcomeParams.getInstance().oneColumnButtonCount
            && childCount <= WelcomeParams
            .getInstance().twoColumnButtonCount) {
            cols = 2;
            rows = Math.round((float) childCount / 2f);
        }
        if (childCount > WelcomeParams.getInstance().twoColumnButtonCount) {
            cols = 3;
            rows = Math.round(0.3f + (float) childCount / 3);
        }

        // поправка на то что если кнопок на уровне много и они уже в три колонки, то задействуем ограничение по линиям, а то расползутся
        if (rows > WelcomeParams.getInstance().linesButtonCount && cols >= 3) {
            rows = WelcomeParams.getInstance().linesButtonCount;
            panelForPaging.setVisible(true);
        } else {
            panelForPaging.setVisible(false);
        }

        if (btnFreeDesign) {
            panel.setLayout(null);
        } else {
            final GridLayout la = new GridLayout(rows, cols, delta, delta / 2);
            panel.setLayout(la);
        }
        int i = 0;
        for (QService service : current.getChildren()) {
            boolean f = true;
            if (i / (cols * rows)
                != pageNumber) { // смотрим каая страница из текущего уровня отображается
                f = false;
            }

            final QButton button = new QButton(service, this, panelMain,
                WelcomeParams.getInstance().buttonType);
            if (!(isAdvanceRegim() && service.getAdvanceLimit() == 0) && button.isIsVisible() && (
                WelcomeParams.getInstance().point == 0 || (service.getPoint() == 0
                    || service.getPoint() == WelcomeParams.getInstance().point))) {
                if (f) {
                    panel.add(button);
                    if (btnFreeDesign) {
                        button.setBounds(service.getButX(), service.getButY(), service.getButB(),
                            service.getButH());
                    }
                    buttonForwardPage.setEnabled((i + 1)
                        != childCount); // это чтоб кнопки листания небыли доступны когда листать дальше некуда
                }
                i++;
            }
        }
        buttonBackPage.setEnabled(
            pageNumber > 0); // это чтоб кнопки листания небыли доступны когда листать дальше некуда

        setVisible(true);
        buttonBack.setVisible(current != root);
        buttonToBegin.setVisible(current != root);
    }

    public void clearPanel(JPanel panel) {
        panel.removeAll();
        panel.repaint();
    }

    public void setVisibleButtons(boolean visible) {
        buttonBack.setVisible(visible && current != root);
        buttonToBegin.setVisible(visible && current != root);

        buttonStandAdvance.setVisible(WelcomeParams.getInstance().standAdvance && visible);
        buttonAdvance.setVisible(WelcomeParams.getInstance().advance && visible);

        buttonInfo.setVisible(WelcomeParams.getInstance().info && visible);
        buttonResponse.setVisible(WelcomeParams.getInstance().response && visible);

        int cols = 3;
        int rows = 5;

        // посмотрим сколько реальных кнопок нужно отобразить
        // тут есть невидимые услуги и услуги не с того киоска
        int childCount = 0;
        childCount = current.getChildren().stream().filter(
            (service) -> (service.getStatus() != -1 && (WelcomeParams.getInstance().point == 0 || (
                service.getPoint() == 0 || service.getPoint() == WelcomeParams
                    .getInstance().point))))
            .map((_item) -> 1).reduce(childCount, Integer::sum);

        if (childCount < 4) {
            cols = 1;
            rows = 3;
        }
        if (childCount > 3 && childCount < 11) {
            cols = 2;
            rows = Math.round((float) childCount / 2);
        }
        if (childCount > 10) {
            cols = 3;
            rows = Math.round(0.3f + (float) childCount / 3);
        }

        // поправка на то что если кнопок на уровне много и они уже в три колонки, то задействуем ограничение по линиям, а то расползутся
        if (visible && rows > WelcomeParams.getInstance().linesButtonCount && cols >= 3) {
            panelForPaging.setVisible(true);
        } else {
            panelForPaging.setVisible(false);
        }

        if (visible && Locales.getInstance().isWelcomeMultylangs()) {
            panelLngs.setVisible(true);
        } else {
            panelLngs.setVisible(false);
        }
    }

    public String getStateWindow() {
        return stateWindow;
    }

    public void setStateWindow(String state) {
        this.stateWindow = state;
        panelLock.setVisible(LOCK.equals(state));
        panelMain.setVisible(UNLOCK.equals(state));
        if (isMed) {
            if (LOCK.equals(state)) {
                FMedCheckIn.setBlockDialog(true);
            }
            if (UNLOCK.equals(state)) {
                FMedCheckIn.setBlockDialog(false);
            }
        }
    }

    /**
     * Заблокировать пункт постановки в очередь.
     *
     * @param message Сообщение, которое выведется на экран пункта.
     */
    public void lock(String message) {
        labelLock.setText(message);
        setStateWindow(LOCK);
        setVisibleButtons(false);
        QLog.l().logger().info("Пункт регистрации заблокирован. Состояние \"" + stateWindow + "\"");
    }

    /**
     * Разблокировать пункт постановки в очередь.
     */
    public void unlock() {
        setStateWindow(UNLOCK);
        setVisibleButtons(true);
        QLog.l().logger()
            .info("Пункт регистрации готов к работе. Состояние \"" + stateWindow + "\"");
    }

    /**
     * Выключить пункт постановки в очередь.
     */
    public void off() {
        setStateWindow(OFF);
        exitServer = true;
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            QLog.l().logger().error("Проблемы с таймером. ", ex);
        }
        QLog.l().logger().info("Пункт регистрации выключен. Состояние \"" + stateWindow + "\"");
    }

    /**
     * Инициализация заново пункта постановки в очередь.
     */
    public void reinit(CmdParams params) {
        final RpcGetAllServices.ServicesForWelcome servs = NetCommander.getServices(netProperty);
        final QService reroot = servs.getRoot();
        FWelcome.root = reroot;
        FWelcome.current = reroot;
        FWelcome.response = null;
        FWelcome.infoTree = null;
        FWelcome.startTime = servs.getStartTime();
        FWelcome.finishTime = servs.getFinishTime();
        FWelcome.btnFreeDesign = servs.getButtonFreeDesign();
        loadRootParam();
        if (params.dropTicketsCounter) {
            increaseTicketCount(Integer.MIN_VALUE);
        }
        QLog.l().logger()
            .info("Пункт регистрации реинициализирован. Состояние \"" + stateWindow + "\"");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBackground = new QPanel(WelcomeParams.getInstance().backgroundImg);
        panelCaption = new QPanel(WelcomeParams.getInstance().topImg);
        labelCaption = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        buttonAdvance = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonStandAdvance = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonToBegin = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonBack = new QButton(WelcomeParams.getInstance().servButtonType);
        panelCentre = new javax.swing.JPanel();
        panelMain = new javax.swing.JPanel();
        panelLock = new javax.swing.JPanel();
        labelLock = new javax.swing.JLabel();
        buttonInfo = new QButton(WelcomeParams.getInstance().servVertButtonType);
        buttonResponse = new QButton(WelcomeParams.getInstance().servVertButtonType);
        panelLngs = new javax.swing.JPanel();
        panelForPaging = new javax.swing.JPanel();
        buttonBackPage = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonForwardPage = new QButton(WelcomeParams.getInstance().servButtonType);
        labelForwardPage = new javax.swing.JLabel();
        labelBackPage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FWelcome.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(640, 480));
        setName("Form"); // NOI18N

        panelBackground.setBorder(new javax.swing.border.MatteBorder(null));
        panelBackground.setName("panelBackground"); // NOI18N

        panelCaption.setBorder(new javax.swing.border.MatteBorder(null));
        panelCaption.setName("panelCaption"); // NOI18N
        panelCaption.setOpaque(false);
        panelCaption.setPreferredSize(new java.awt.Dimension(1008, 150));

        labelCaption.setFont(resourceMap.getFont("labelCaption.font")); // NOI18N
        labelCaption.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCaption.setText(resourceMap.getString("labelCaption.text")); // NOI18N
        labelCaption.setName("labelCaption"); // NOI18N

        javax.swing.GroupLayout panelCaptionLayout = new javax.swing.GroupLayout(panelCaption);
        panelCaption.setLayout(panelCaptionLayout);
        panelCaptionLayout.setHorizontalGroup(
            panelCaptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(labelCaption, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelCaptionLayout.setVerticalGroup(
            panelCaptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(labelCaption, javax.swing.GroupLayout.Alignment.TRAILING,
                    javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        panelButtons.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setOpaque(false);
        panelButtons.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelButtonsMouseClicked(evt);
            }
        });
        panelButtons.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        buttonAdvance.setFont(resourceMap.getFont("buttonAdvance.font")); // NOI18N
        buttonAdvance.setText(resourceMap.getString("buttonAdvance.text")); // NOI18N
        buttonAdvance.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
                javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        buttonAdvance.setFocusPainted(false);
        buttonAdvance.setName("buttonAdvance"); // NOI18N
        buttonAdvance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAdvanceActionPerformed(evt);
            }
        });
        panelButtons.add(buttonAdvance);

        buttonStandAdvance.setFont(resourceMap.getFont("buttonStandAdvance.font")); // NOI18N
        buttonStandAdvance.setText(resourceMap.getString("buttonStandAdvance.text")); // NOI18N
        buttonStandAdvance.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
                javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        buttonStandAdvance.setFocusPainted(false);
        buttonStandAdvance.setName("buttonStandAdvance"); // NOI18N
        buttonStandAdvance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStandAdvanceActionPerformed(evt);
            }
        });
        panelButtons.add(buttonStandAdvance);

        buttonToBegin.setFont(resourceMap.getFont("buttonToBegin.font")); // NOI18N
        buttonToBegin.setIcon(resourceMap.getIcon("buttonToBegin.icon")); // NOI18N
        buttonToBegin.setText(resourceMap.getString("buttonToBegin.text")); // NOI18N
        buttonToBegin
            .setActionCommand(resourceMap.getString("buttonToBegin.actionCommand")); // NOI18N
        buttonToBegin.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
                javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        buttonToBegin.setFocusPainted(false);
        buttonToBegin.setName("buttonToBegin"); // NOI18N
        buttonToBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToBeginActionPerformed(evt);
            }
        });
        panelButtons.add(buttonToBegin);

        buttonBack.setFont(resourceMap.getFont("buttonBack.font")); // NOI18N
        buttonBack.setIcon(resourceMap.getIcon("buttonBack.icon")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
        buttonBack.setActionCommand(resourceMap.getString("buttonBack.actionCommand")); // NOI18N
        buttonBack.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
                javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        buttonBack.setFocusPainted(false);
        buttonBack.setName("buttonBack"); // NOI18N
        buttonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackActionPerformed(evt);
            }
        });
        panelButtons.add(buttonBack);

        panelCentre.setBorder(new javax.swing.border.MatteBorder(null));
        panelCentre.setName("panelCentre"); // NOI18N
        panelCentre.setOpaque(false);

        panelMain.setBorder(new javax.swing.border.MatteBorder(null));
        panelMain.setFont(resourceMap.getFont("panelMain.font")); // NOI18N
        panelMain.setName("panelMain"); // NOI18N
        panelMain.setOpaque(false);

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 451, Short.MAX_VALUE)
        );

        panelLock.setBorder(new javax.swing.border.MatteBorder(null));
        panelLock.setName("panelLock"); // NOI18N
        panelLock.setOpaque(false);

        labelLock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLock.setText(resourceMap.getString("labelLock.text")); // NOI18N
        labelLock.setName("labelLock"); // NOI18N

        javax.swing.GroupLayout panelLockLayout = new javax.swing.GroupLayout(panelLock);
        panelLock.setLayout(panelLockLayout);
        panelLockLayout.setHorizontalGroup(
            panelLockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLockLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(labelLock, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        panelLockLayout.setVerticalGroup(
            panelLockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLockLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(labelLock, javax.swing.GroupLayout.DEFAULT_SIZE, 130,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );

        buttonInfo.setFont(resourceMap.getFont("buttonInfo.font")); // NOI18N
        buttonInfo.setText(resourceMap.getString("buttonInfo.text")); // NOI18N
        buttonInfo.setActionCommand(resourceMap.getString("buttonInfo.actionCommand")); // NOI18N
        buttonInfo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonInfo.setFocusPainted(false);
        buttonInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonInfo.setName("buttonInfo"); // NOI18N
        buttonInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInfoActionPerformed(evt);
            }
        });

        buttonResponse.setFont(resourceMap.getFont("buttonResponse.font")); // NOI18N
        buttonResponse.setText(resourceMap.getString("buttonResponse.text")); // NOI18N
        buttonResponse.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonResponse.setFocusPainted(false);
        buttonResponse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonResponse.setName("buttonResponse"); // NOI18N
        buttonResponse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResponseActionPerformed(evt);
            }
        });

        panelLngs.setBorder(new javax.swing.border.MatteBorder(null));
        panelLngs.setName("panelLngs"); // NOI18N
        panelLngs.setOpaque(false);

        javax.swing.GroupLayout panelLngsLayout = new javax.swing.GroupLayout(panelLngs);
        panelLngs.setLayout(panelLngsLayout);
        panelLngsLayout.setHorizontalGroup(
            panelLngsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        panelLngsLayout.setVerticalGroup(
            panelLngsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelCentreLayout = new javax.swing.GroupLayout(panelCentre);
        panelCentre.setLayout(panelCentreLayout);
        panelCentreLayout.setHorizontalGroup(
            panelCentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelCentreLayout.createSequentialGroup()
                    .addGroup(
                        panelCentreLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelCentreLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(panelLngs, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelLock, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(18, 18, 18)
                    .addGroup(panelCentreLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(buttonResponse, javax.swing.GroupLayout.PREFERRED_SIZE, 55,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 55,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        panelCentreLayout.setVerticalGroup(
            panelCentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    panelCentreLayout.createSequentialGroup()
                        .addComponent(buttonInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 378,
                            Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonResponse, javax.swing.GroupLayout.DEFAULT_SIZE, 267,
                            Short.MAX_VALUE))
                .addGroup(panelCentreLayout.createSequentialGroup()
                    .addComponent(panelLngs, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addComponent(panelLock, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelForPaging.setBorder(new javax.swing.border.MatteBorder(null));
        panelForPaging.setName("panelForPaging"); // NOI18N
        panelForPaging.setOpaque(false);

        buttonBackPage.setFont(resourceMap.getFont("buttonForwardPage.font")); // NOI18N
        buttonBackPage.setIcon(resourceMap.getIcon("buttonBackPage.icon")); // NOI18N
        buttonBackPage.setText(resourceMap.getString("buttonBackPage.text")); // NOI18N
        buttonBackPage.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
                javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        buttonBackPage.setFocusPainted(false);
        buttonBackPage.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        buttonBackPage.setName("buttonBackPage"); // NOI18N
        buttonBackPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackPageActionPerformed(evt);
            }
        });

        buttonForwardPage.setFont(resourceMap.getFont("buttonForwardPage.font")); // NOI18N
        buttonForwardPage.setIcon(resourceMap.getIcon("buttonForwardPage.icon")); // NOI18N
        buttonForwardPage.setText(resourceMap.getString("buttonForwardPage.text")); // NOI18N
        buttonForwardPage.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
                javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.RAISED))));
        buttonForwardPage.setFocusPainted(false);
        buttonForwardPage.setName("buttonForwardPage"); // NOI18N
        buttonForwardPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonForwardPageActionPerformed(evt);
            }
        });

        labelForwardPage.setFont(resourceMap.getFont("labelForwardPage.font")); // NOI18N
        labelForwardPage.setText(resourceMap.getString("labelForwardPage.text")); // NOI18N
        labelForwardPage.setName("labelForwardPage"); // NOI18N

        labelBackPage.setFont(resourceMap.getFont("labelBackPage.font")); // NOI18N
        labelBackPage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelBackPage.setText(resourceMap.getString("labelBackPage.text")); // NOI18N
        labelBackPage.setName("labelBackPage"); // NOI18N

        javax.swing.GroupLayout panelForPagingLayout = new javax.swing.GroupLayout(panelForPaging);
        panelForPaging.setLayout(panelForPagingLayout);
        panelForPagingLayout.setHorizontalGroup(
            panelForPagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelForPagingLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(labelBackPage)
                    .addGap(18, 18, 18)
                    .addComponent(buttonBackPage, javax.swing.GroupLayout.PREFERRED_SIZE, 213,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(buttonForwardPage, javax.swing.GroupLayout.PREFERRED_SIZE, 231,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(labelForwardPage)
                    .addContainerGap())
        );
        panelForPagingLayout.setVerticalGroup(
            panelForPagingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    panelForPagingLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelForPagingLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelForwardPage,
                                javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                            .addComponent(labelBackPage, javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                            .addComponent(buttonBackPage, javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                            .addComponent(buttonForwardPage,
                                javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                        .addContainerGap())
        );

        javax.swing.GroupLayout panelBackgroundLayout = new javax.swing.GroupLayout(
            panelBackground);
        panelBackground.setLayout(panelBackgroundLayout);
        panelBackgroundLayout.setHorizontalGroup(
            panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelCaption, javax.swing.GroupLayout.DEFAULT_SIZE, 1140,
                    Short.MAX_VALUE)
                .addGroup(panelBackgroundLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addComponent(panelForPaging, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelCentre, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelBackgroundLayout.setVerticalGroup(
            panelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelBackgroundLayout.createSequentialGroup()
                    .addComponent(panelCaption, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panelCentre, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panelForPaging, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelBackground, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelBackground, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeTextToLocale() {
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FWelcome.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        labelCaption.setText(resourceMap.getString("labelCaption.text")); // NOI18N
        buttonAdvance.setText("<html><p align=center>" + resourceMap
            .getString(advanceRegim ? "lable.reg_calcel" : "lable.adv_reg")); // NOI18N
        buttonStandAdvance.setText(resourceMap.getString("buttonStandAdvance.text")); // NOI18N
        buttonToBegin.setText(resourceMap.getString("buttonToBegin.text")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
        labelLock.setText(resourceMap.getString("labelLock.text")); // NOI18N
        buttonInfo.setText(
            "".equals(WelcomeParams.getInstance().infoHtml) ? resourceMap
                .getString("buttonInfo.text")
                : WelcomeParams.getInstance().infoHtml); // NOI18N
        buttonResponse.setText("".equals(WelcomeParams.getInstance().responseHtml) ? resourceMap
            .getString("buttonResponse.text") : WelcomeParams.getInstance().responseHtml); // NOI18N
        buttonBackPage.setText(resourceMap.getString("buttonBackPage.text")); // NOI18N
        buttonForwardPage.setText(resourceMap.getString("buttonForwardPage.text")); // NOI18N
        labelBackPage.setText(resourceMap.getString("labelBackPage.text")); // NOI18N
        labelForwardPage.setText(resourceMap.getString("labelForwardPage.text")); // NOI18N
    }

    private void buttonBackActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackActionPerformed
        if (!current.equals(root)) {
            showButtons(current.getParent(), panelMain);
        }
    }//GEN-LAST:event_buttonBackActionPerformed

    private void buttonToBeginActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonToBeginActionPerformed
        if (!current.equals(root)) {
            showButtons(root, panelMain);
            current = root;
        }
    }//GEN-LAST:event_buttonToBeginActionPerformed

    /**
     * Заставка на некоторый таймаут
     *
     * @param text текст на заставке
     * @param imagePath картинка на заставке
     */
    public ATalkingClock showDelayFormPrint(String text, String imagePath) {
        setVisibleButtons(false);
        ATalkingClock clock = new ATalkingClock(WelcomeParams.getInstance().delayPrint, 1) {

            @Override
            public void run() {
                setVisibleButtons(true);
                showButtons(root, panelMain);
                showMed();
            }
        };
        clock.start();
        clearPanel(panelMain);
        panelMain.setLayout(new GridLayout(1, 1, 50, 1));
        panelMain.add(labelInfo);
        labelInfo.setText(text);
        labelInfo.setHorizontalAlignment(JLabel.CENTER);
        labelInfo.setVerticalAlignment(JLabel.BOTTOM);
        labelInfo.setVerticalTextPosition(SwingConstants.TOP);
        labelInfo.setHorizontalTextPosition(SwingConstants.CENTER);
        labelInfo.setIconTextGap(45);

        labelInfo.setIcon(new File(imagePath).exists() ? new ImageIcon(imagePath)
            : new ImageIcon(getClass().getResource(imagePath)));

        panelMain.repaint();
        labelInfo.repaint();
        return clock;
    }

    private void buttonAdvanceActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAdvanceActionPerformed
        setAdvanceRegim(!isAdvanceRegim());
        if (isMed && !isAdvanceRegim()) {
            showMed();
        }
        showButtons(root, panelMain);
    }//GEN-LAST:event_buttonAdvanceActionPerformed

    private void buttonStandAdvanceActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStandAdvanceActionPerformed
        final RpcStandInService res = FStandAdvance
            .showAdvanceStandDialog(this, true, FWelcome.netProperty, true,
                WelcomeParams.getInstance().delayBack * 2);

        if (res != null) {

            if (res.getMethod()
                == null) {// костыль. тут приедет текст запрета если нельзя встать в очередь

                showDelayFormPrint(
                    "<HTML><b><p align=center><span style='font-size:50.0pt;color:green'>"
                        + getLocaleMessage("ticket.get_caption") + "<br></span>"
                        + "<span style='font-size:60.0pt;color:blue'>" + getLocaleMessage(
                        "ticket.get_caption_number") + "<br></span>"
                        + "<span style='font-size:100.0pt;color:blue'>" + res.getResult()
                        .getPrefix() + res
                        .getResult().getNumber() + "</span></p>",
                    Uses.firstMonitor.getDefaultConfiguration().getBounds().height > 900
                        || this.getHeight() > 900
                        ? "/ru/apertum/qsystem/client/forms/resources/getTicket.png"
                        : "/ru/apertum/qsystem/client/forms/resources/getTicketSmall.png");

                QLog.l().logger().info("Печать этикетки.");

                new Thread(() -> {
                    FWelcome.printTicket(res.getResult());
                }).start();
            } else {
                showDelayFormPrint(
                    "<HTML><b><p align=center><span style='font-size:60.0pt;color:red'>" + res
                        .getMethod(),
                    "/ru/apertum/qsystem/client/forms/resources/noActive.png");
            }
        }

    }//GEN-LAST:event_buttonStandAdvanceActionPerformed

    private void buttonResponseActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResponseActionPerformed
        if (WelcomeParams.getInstance().responseURL != null) {
            FInfoDialogWeb
                .showInfoDialogWeb(this, true, true, WelcomeParams.getInstance().delayBack * 4,
                    WelcomeParams.getInstance().responseURL);
        } else {
            final QRespItem res = FResponseDialog
                .showResponseDialog(this, getResponse(), true, true,
                    WelcomeParams.getInstance().delayBack * 2);
            if (res != null) {
                NetCommander.setResponseAnswer(netProperty, res, null, null, null, "");
            }
        }
    }//GEN-LAST:event_buttonResponseActionPerformed

    private void buttonInfoActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInfoActionPerformed
        if (WelcomeParams.getInstance().infoURL != null) {
            FInfoDialogWeb
                .showInfoDialogWeb(this, true, true, WelcomeParams.getInstance().delayBack * 4,
                    WelcomeParams.getInstance().infoURL);
        } else {
            FInfoDialog.showInfoDialog(this, getInfoTree(), true, true,
                WelcomeParams.getInstance().delayBack * 3);
        }
    }//GEN-LAST:event_buttonInfoActionPerformed

    private void buttonBackPageActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackPageActionPerformed
        if (pageNumber > 0) {
            pageNumber--;
            showButtons(current, panelMain);
            buttonBackPage.setEnabled(pageNumber > 0);
        }

    }//GEN-LAST:event_buttonBackPageActionPerformed

    private void buttonForwardPageActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonForwardPageActionPerformed
        pageNumber++;
        showButtons(current, panelMain);
        buttonBackPage.setEnabled(pageNumber > 0);
    }//GEN-LAST:event_buttonForwardPageActionPerformed

    private void panelButtonsMouseClicked(
        java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelButtonsMouseClicked
        if (clicks.isEmpty()) {
            clicks.add(new Date().getTime());
            return;
        }
        final long now = new Date().getTime();
        if (now - clicks.getLast() < 500) {
            clicks.add(now);
        } else if (now - clicks.getLast() > 5000 && now - clicks.getLast() < 10000
            && clicks.size() == 10) {
            final BackDoor bd = new BackDoor(this, false);
            bd.setVisible(true);
            clicks.clear();
        } else {

            clicks.clear();
            clicks.add(now);
        }
    }//GEN-LAST:event_panelButtonsMouseClicked

    private class CommandServer implements Runnable {

        @Override
        public void run() {
            // привинтить сокет на локалхост, порт 3129
            final ServerSocket server;
            try {
                server = new ServerSocket(netProperty.getClientPort());
                server.setSoTimeout(500);
            } catch (IOException e) {
                throw new ClientException("Ошибка при создании серверного сокета: " + e);
            }

            System.out.println("Server for managment of registration point started.\n");
            QLog.l().logger().info("Сервер управления пунктом регистрации запущен.");

            // слушаем порт
            while (!exitServer) {
                // ждём нового подключения, после чего запускаем обработку клиента
                // в новый вычислительный поток и увеличиваем счётчик на единичку
                try {
                    doCommand(server.accept());
                } catch (SocketTimeoutException e) {
                } catch (IOException e) {
                    QLog.l().logger().error("Управлялка пунктом чет подглючила.", e);
                }
            }
        }

        private void doCommand(Socket socket) {
            // из сокета клиента берём поток входящих данных
            try {
                InputStream is;
                try {
                    is = socket.getInputStream();
                } catch (IOException e) {
                    throw new ServerException(
                        "Ошибка при получении входного потока: " + Arrays
                            .toString(e.getStackTrace()));
                }

                final String data;
                try {
                    // подождать пока хоть что-то приползет из сети, но не более 10 сек.
                    int i = 0;
                    while (is.available() == 0 && i < 100) {
                        Thread.sleep(100);//бля
                        i++;
                    }
                    Thread.sleep(100);//бля
                    data = URLDecoder.decode(new String(Uses.readInputStream(is)).trim(), "utf-8");
                } catch (IOException ex) {
                    throw new ServerException(
                        "Ошибка при чтении из входного потока: " + Arrays
                            .toString(ex.getStackTrace()));
                } catch (InterruptedException ex) {
                    throw new ServerException(
                        "Проблема со сном: " + Arrays.toString(ex.getStackTrace()));
                }
                QLog.l().logger().trace("Задание:\n" + data);

                final JsonRPC20 rpc;
                final Gson gson = GsonPool.getInstance().borrowGson();
                try {
                    rpc = gson.fromJson(data, JsonRPC20.class);
                } finally {
                    GsonPool.getInstance().returnGson(gson);
                }

                // Обрабатываем задание
                //С рабочего места администратора должна быть возможность заблокировать пункт постановки в очередь,
                //разблокировать, выключить, провести инициализация заново.
                // В любом другом случае будет выслано состояние.
                String upp =
                    ".  " + increaseTicketCount(0) + " " + getLocaleMessage("tickets_were_printed");
                if (Uses.WELCOME_LOCK.equals(rpc.getMethod())) {
                    lock(LOCK_MESSAGE);
                }
                if (Uses.WELCOME_UNLOCK.equals(rpc.getMethod())) {
                    unlock();
                }
                if (Uses.WELCOME_OFF.equals(rpc.getMethod())) {
                    off();
                }
                if (Uses.WELCOME_REINIT.equals(rpc.getMethod())) {
                    reinit(rpc.getParams());
                }

                // выводим данные:
                QLog.l().logger().trace("Ответ: " + stateWindow + upp);
                final String rpc_resp;
                final Gson gson_resp = GsonPool.getInstance().borrowGson();
                try {
                    rpc_resp = gson.toJson(new RpcGetSrt(stateWindow + upp));
                } finally {
                    GsonPool.getInstance().returnGson(gson_resp);
                }
                try {
                    // Передача данных ответа
                    final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.print(rpc_resp);
                    writer.flush();
                } catch (IOException e) {
                    throw new ServerException(
                        "Ошибка при записи в поток: " + Arrays.toString(e.getStackTrace()));
                }
            } finally {
                // завершаем соединение
                try {
                    //оборативаем close, т.к. он сам может сгенерировать ошибку IOExeption. Просто выкинем Стек-трейс
                    socket.close();
                } catch (IOException e) {
                    QLog.l().logger().error(e);
                }
            }
            //Если команда была "выключить"
            if (OFF.equals(stateWindow)) {
                System.exit(0);
            }
        }
    }

    private class LngBtnAction extends AbstractAction {

        final private Locale locale;

        public LngBtnAction(Locale locale) {
            this.locale = locale;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Locale.setDefault(locale);
            changeTextToLocale();
            labelCaption
                .setText(Uses.prepareAbsolutPathForImg(
                    root.getTextToLocale(QService.Field.BUTTON_TEXT)));
            for (Component cmp : panelMain.getComponents()) {
                if (cmp instanceof QButton) {
                    ((QButton) cmp).refreshText();
                }
            }
        }
    }
    // End of variables declaration//GEN-END:variables
}
