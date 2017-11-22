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

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.client.model.PropsTableModel;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;

import org.dom4j.DocumentException;
import org.jdesktop.application.Action;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.client.model.QTray;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.INetProperty;

import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.BCodec;
import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.springframework.transaction.TransactionStatus;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.common.WysiwygDlg;
import ru.apertum.qsystem.client.help.Helper;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.cmd.RpcGetServerState.ServiceInfo;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.ClientWarning;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IDataExchange;
import ru.apertum.qsystem.extra.IPing;
import ru.apertum.qsystem.hibernate.AnnotationSessionFactoryBean;
import ru.apertum.qsystem.reports.model.QReportsList;
import ru.apertum.qsystem.server.MainBoard;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.controller.IIndicatorBoard;
import ru.apertum.qsystem.server.model.*;
import ru.apertum.qsystem.server.model.schedule.QSchedule;
import ru.apertum.qsystem.server.model.calendar.CalendarTableModel;
import ru.apertum.qsystem.server.model.calendar.QCalendar;
import ru.apertum.qsystem.server.model.calendar.QCalendarList;
import ru.apertum.qsystem.server.model.calendar.TableCell;
import ru.apertum.qsystem.server.model.calendar.FreeDay;
import ru.apertum.qsystem.server.model.infosystem.QInfoItem;
import ru.apertum.qsystem.server.model.infosystem.QInfoTree;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;
import ru.apertum.qsystem.server.model.response.QRespItem;
import ru.apertum.qsystem.server.model.response.QResponseTree;
import ru.apertum.qsystem.server.model.results.QResult;
import ru.apertum.qsystem.server.model.results.QResultList;
import ru.apertum.qsystem.server.model.schedule.QBreaks;
import ru.apertum.qsystem.server.model.schedule.QBreaksList;
import ru.apertum.qsystem.server.model.schedule.QScheduleList;
import ru.apertum.qsystem.server.model.schedule.QSpecSchedule;

/**
 * Created on 1 Декабрь 2008 г., 18:51
 *
 * @author Evgeniy Egorov
 */
public class FAdmin extends javax.swing.JFrame {

    private static ResourceMap localeMap = null;

    public static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FAdmin.class);
        }
        return localeMap.getString(key);
    }
    /**
     * Константы хранения параметров в файле.
     */
    private static final String SERVER_ADRESS = "server_adress";
    private static final String SERVER_PORT = "server_port";
    private static final String SERVER_AUTO_REQUEST = "server_auto_request";
    private static final String CLIENT_ADRESS = "client_adress";
    private static final String CLIENT_PORT = "client_port";
    private static final String CLIENT_AUTO_REQUEST = "client_auto_request";
    private final QTray tray;
    //******************************************************************************************************************
    //******************************************************************************************************************
    //***************************************** таймер автоматического запроса******************************************
    private static final int DELAY_BLINK = 30000;
    /**
     * Таймер опроса компонент системы.
     */
    private final StartTimer timer = new StartTimer(DELAY_BLINK, new TimerPrinter());

    private class StartTimer extends Timer {

        public StartTimer(int delay, ActionListener listener) {
            super(delay, listener);
        }

        public void startTimer() {
            if (checkBoxServerAuto.isSelected()) {
                checkServer();
            }
            if (checkBoxClientAuto.isSelected()) {
                checkWelcome(null);
            }
            start();
        }
    }

    /**
     * Собыите автосканирования сервера и пункта регистрации на таймер.
     */
    private class TimerPrinter implements ActionListener {

        /**
         * Обеспечение автоматизации запроса.
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkBoxServerAuto.isSelected()) {
                checkBoxServerAuto.setSelected(checkServer());
            }
            if (checkBoxClientAuto.isSelected()) {
                checkBoxClientAuto.setSelected(checkWelcome(null));
            }
        }
    };

    /**
     * Этим методом запускаем таймер автоматического опроса
     */
    private void startTimer() {
        if (checkBoxServerAuto.isSelected() || checkBoxClientAuto.isSelected()) {
            if (!timer.isRunning()) {
                timer.startTimer();
            }
        } else {
            timer.stop();
        }
    }
    //***************************************** таймер автоматического запроса  *************************************************

    private void init() {
        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                timer.stop();
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
                Uses.closeSplash();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        setTitle(getTitle() + " " + Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF()));
        try {
            setIconImage(ImageIO.read(FAdmin.class.getResource("/ru/apertum/qsystem/client/forms/resources/admin.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        // Отцентирируем
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((Math.round(kit.getScreenSize().width - getWidth()) / 2), (Math.round(kit.getScreenSize().height - getHeight()) / 2));
    }

    /**
     * Creates new form FAdmin
     */
    public FAdmin() {

        initComponents();

        init();

        tabbedPaneMain.remove(tabHide);

        // Поставим эконку
        final JFrame fr = this;
        tray = QTray.getInstance(fr, "/ru/apertum/qsystem/client/forms/resources/admin.png", getLocaleMessage("tray.caption"));
        tray.addItem(getLocaleMessage("tray.caption"), (ActionEvent e) -> {
            setVisible(true);
            setState(JFrame.NORMAL);
        });
        tray.addItem("-", (ActionEvent e) -> {
        });
        tray.addItem(getLocaleMessage("tray.exit"), (ActionEvent e) -> {
            dispose();
            System.exit(0);
        });

        int ii = 1;
        final ButtonGroup bg = new ButtonGroup();
        final String currLng = Locales.getInstance().getLangCurrName();
        for (String lng : Locales.getInstance().getAvailableLocales()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FAdmin.class, fr).get("setCurrentLang"));
            bg.add(item);
            item.setSelected(lng.equals(currLng));
            item.setText(lng); // NOI18N
            item.setName("QRadioButtonMenuItem" + (ii++)); // NOI18N
            menuLangs.add(item);
        }

        // Определим события выбора итемов в списках.
        listUsers.addListSelectionListener((ListSelectionEvent e) -> {
            userListChange();
        });
        listUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    final QUser user = (QUser) listUsers.getSelectedValue();
                    if (user != null) {
                        FUserChangeDialog.changeUser(form, true, user);
                    }
                }
            }
        });

        listOffices.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    final QOffice office = (QOffice) listOffices.getSelectedValue();
                    if (office != null) {
                        FOfficeChangeDialog.changeOffice(form, true, office);
                    }
                }
            }
        });
        jPanel11.setVisible(false);// убрали редактирование юзера на форме
        final ListCellRenderer userRenderer = new ListCellRenderer() {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                final QUser user = (QUser) value;
                renderer.setText((user.getAdminAccess() ? "*" : "") + user.getName() + "  (" + user.getPoint() + " / " + user.getAdressRS() + ")");
                return renderer;
            }
        };

        listUsers.setCellRenderer(userRenderer);
        // Определим события выбора итемов в списках.
        treeResp.addTreeSelectionListener((TreeSelectionEvent e) -> {
            responseListChange();
        });
        listSchedule.addListSelectionListener((ListSelectionEvent e) -> {
            scheduleListChange();
        });
        listCalendar.addListSelectionListener(new ListSelectionListener() {

            private int oldSelectedValue = 0;
            private int tmp = 0;

            public int getOldSelectedValue() {
                return oldSelectedValue;
            }

            public void setOldSelectedValue(int oldSelectedValue) {
                this.oldSelectedValue = tmp;
                this.tmp = oldSelectedValue;
            }
            private boolean canceled = false;

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (canceled) {
                    canceled = false;
                } else if (tableCalendar.getModel() instanceof CalendarTableModel) {
                    final CalendarTableModel model = (CalendarTableModel) tableCalendar.getModel();
                    if (!model.isSaved()) {
                        final int res = JOptionPane.showConfirmDialog(null, getLocaleMessage("calendar.change.title"), getLocaleMessage("calendar.change.caption"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        switch (res) {
                            case 0:   // сохранить и переключиться
                                model.save();
                                calendarListChange();
                                setOldSelectedValue(listCalendar.getSelectedIndex());
                                break;
                            case 1: // переключаемся без сохранения

                                calendarListChange();
                                setOldSelectedValue(listCalendar.getSelectedIndex());

                                break;
                            case 2: // не сохранять и остаться на прежнем уровне
                                canceled = true;
                                listCalendar.setSelectedIndex(getOldSelectedValue());
                                break;
                        }
                    } else {
                        calendarListChange();
                        setOldSelectedValue(listCalendar.getSelectedIndex());
                    }
                } else {
                    calendarListChange();
                    setOldSelectedValue(listCalendar.getSelectedIndex());
                }
            }
        });
        // Определим события выбора сайта в списках.
        treeServices.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeInfo.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeResp.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        /*
         treeServices.setCellRenderer(new DefaultTreeCellRenderer() {
        
         @Override
         public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
         super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
         setText(((Element) value).attributeValue(Uses.TAG_NAME));
         return this;
         }
         });*/
        treeServices.addTreeSelectionListener((TreeSelectionEvent e) -> {
            serviceListChange();
        });
        final MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int selRow = treeServices.getRowForLocation(e.getX(), e.getY());
                    TreePath selPath = treeServices.getPathForLocation(e.getX(), e.getY());
                    treeServices.setSelectionPath(selPath);
                    if (selRow > -1) {
                        treeServices.setSelectionRow(selRow);
                    }
                }
            }
        };
        treeServices.addMouseListener(ml);

        treeInfo.addTreeSelectionListener((TreeSelectionEvent e) -> {
            infoListChange();
        });

        sectionsList.setModel(new DefaultComboBoxModel(ServerProps.getInstance().getSections().toArray()));
        sectionsList.addListSelectionListener((ListSelectionEvent e) -> {
            if (sectionsList.getLastVisibleIndex() == -1) {
                return;
            }
            final ServerProps.Section item = sectionsList.getSelectedValue();
            if (item == null) {
                return;
            }
            propsTable.setModel(new PropsTableModel(item));
        });
        propsTable.getColumnModel().getColumn(0).setMaxWidth(180);
        propsTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        propsTable.getColumnModel().getColumn(0).setWidth(180);
        propsTable.getColumnModel().getColumn(0).setMaxWidth(1920);
        if (!ServerProps.getInstance().getSections().isEmpty()) {
            sectionsList.setSelectedIndex(0);
        }

        textFieldStartTime.setInputVerifier(DateVerifier);
        textFieldFinishTime.setInputVerifier(DateVerifier);

        //Загрузим настройки
        loadSettings();
        // Старт таймера автоматических запросов.
        startTimer();
        // Грузим конфигурацию
        loadConfig();

        spinnerPropServerPort.getModel().addChangeListener(new ChangeNet());
        spinnerPropClientPort.getModel().addChangeListener(new ChangeNet());
        spinnerWebServerPort.getModel().addChangeListener(new ChangeNet());

        spinnerServerPort.getModel().addChangeListener(new ChangeSettings());
        spinnerClientPort.getModel().addChangeListener(new ChangeSettings());
        spinnerUserRS.getModel().addChangeListener(new ChangeUser());

        //привязка помощи к форме.
        final Helper helper = Helper.getHelp("ru/apertum/qsystem/client/help/admin.hs");
        helper.setHelpListener(menuItemHelp);
        helper.enableHelpKey(jPanel1, "introduction");
        helper.enableHelpKey(jPanel3, "monitoring");
        helper.enableHelpKey(jPanel4, "configuring");
        helper.enableHelpKey(jPanel8, "net");

        helper.enableHelpKey(jPanel17, "schedulers");
        helper.enableHelpKey(jPanel19, "calendars");
        helper.enableHelpKey(jPanel2, "infoSystem");
        helper.enableHelpKey(jPanel13, "responses");
        helper.enableHelpKey(jPanel18, "results");

        treeServices.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferHandler.TransferSupport info) {
                final JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
                if (dl.getChildIndex() == -1) {
                    return false;
                }
                // Get the string that is being dropped.
                final Transferable t = info.getTransferable();
                final QService data;
                try {
                    data = (QService) t.getTransferData(DataFlavor.stringFlavor);
                    return (data.getParent().getId().equals(((QService) dl.getPath().getLastPathComponent()).getId()));
                } catch (UnsupportedFlavorException | IOException e) {
                    return false;
                }
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport info) {
                if (!info.isDrop()) {
                    return false;
                }
                final QService data;
                try {
                    data = QServiceTree.getInstance().getById(((QService) info.getTransferable().getTransferData(DataFlavor.stringFlavor)).getId());
                } catch (UnsupportedFlavorException | IOException e) {
                    System.err.println(e);
                    return false;
                }
                final JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
                final TreePath tp = dl.getPath();
                final QService parent = (QService) tp.getLastPathComponent();
                ((QServiceTree) treeServices.getModel()).moveNode(data, parent, dl.getChildIndex());
                return true;
            }

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return (QService) ((JTree) c).getLastSelectedPathComponent();
            }
        });
        treeServices.setDropMode(DropMode.INSERT);

        // типо переключалка серверов
        final AnnotationSessionFactoryBean as = (AnnotationSessionFactoryBean) Spring.getInstance().getFactory().getBean("conf");
        if (as.getServers().size() > 1) {
            final JMenu menu = new JMenu(getLocaleMessage("admin.servers"));
            as.getServers().stream().map((ser) -> {
                final JMenuItem mi1 = new JMenuItem(as);
                mi1.setText(ser.isCurrent() ? "<html><u><i>" + ser.getName() + "</i></u>" : ser.getName());
                return mi1;
            }).forEach((mi1) -> {
                menu.add(mi1);
            });
            jMenuBar1.add(menu, 4);
            jMenuBar1.add(new JLabel("<html><span style='font-size:13.0pt;color:red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  [" + as.getName() + "]"));
        }

        // Доп. приоритты
        spinExtPrior.setVisible(QConfig.cfg().useExtPriorities());
        labExtPrior.setVisible(QConfig.cfg().useExtPriorities());
    }

    /**
     * Сохранять спинедиты сетевых настроек
     */
    private class ChangeNet implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            saveNet();
        }
    }

    /**
     * Сохранять настройки спинедита мониторинга
     */
    private class ChangeSettings implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            saveSettings();
        }
    }

    /**
     * Сохранять настройки спинедита юзера
     */
    private class ChangeUser implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            saveUser();
        }
    }
    /**
     * вспомогательные для отсечения событий сохранения
     */
    private boolean changeSite = true;
    private boolean changeUser = true;

    /**
     * Действия по смене выбранного итема в списке пользоватеолей.
     */
    private void userListChange() {
        QLog.l().logger().info("userListChanging");
        if (listUsers.getLastVisibleIndex() == -1) {
            listUserService.setListData(new Object[0]);
            textFieldUserName.setText("");
            textFieldUserIdent.setText("");
            passwordFieldUser.setText("");
            textFieldExtPoint.setText("");
            tfUserId.setText("");
            return;
        }
        final QUser user = (QUser) listUsers.getSelectedValue();
        if (user == null) {
            return;
        }
        changeUser = false;

        try {
            textFieldUserName.setText(user.getName());
            textFieldUserIdent.setText(user.getPoint());
            passwordFieldUser.setText(user.getPassword());
            spinnerUserRS.setValue(user.getAdressRS());
            textFieldExtPoint.setText(user.getPointExt());
            checkBoxAdmin.setSelected(user.getAdminAccess());
            checkBoxReport.setSelected(user.getReportAccess());
            listUserService.setModel(user.getPlanServiceList());
            if (listUserService.getLastVisibleIndex() != -1) {
                listUserService.setSelectedIndex(0);
            }
            tfUserId.setText(user.getId() == null ? "--" : user.getId().toString());
        } finally {
            changeUser = true;
        }
    }

    /**x
     * Действия по смене выбранного итема в списке отзывов.
     */
    private void responseListChange() {
        final TreePath selectedPath = treeResp.getSelectionPath();
        if (selectedPath != null) {
            final QRespItem item = (QRespItem) selectedPath.getLastPathComponent();
            if (item == null) {
                return;
            }

            textFieldResponse.setText(item.getName());
            textPaneResponse.setText(item.getHTMLText());
            labelRespinse.setText(item.getHTMLText());
            tfHeaderCmtResp.setText(item.getInput_caption());
            cbCommentForResp.setSelected(item.isInput_required());
            tfRespID.setText(item.getId() == null ? "--" : item.getId().toString());
        } else {
            textFieldResponse.setText("");
            textPaneResponse.setText("");
            labelRespinse.setText("");
            tfHeaderCmtResp.setText("");
            tfRespID.setText("");
        }
    }

    /**
     * Действия по смене выбранного итема в списке планов расписания.
     */
    private void scheduleListChange() {
        if (listSchedule.getLastVisibleIndex() == -1) {
            textFieldScheduleName.setText("");
            labelSchedule.setText("");
            return;
        }
        final QSchedule item = (QSchedule) listSchedule.getSelectedValue();
        if (item == null) {
            return;
        }
        textFieldScheduleName.setText(item.getName());
        String str = "<HTML>"
                + "<span style='font-size:12.0pt;color:blue;'>"
                + "<b>" + getLocaleMessage("calendar.plan_params") + ":</b>"
                + "<table  border='0'>"
                + (item.getType() == 0
                        ? (((item.getTime_begin_1() == null || item.getTime_end_1() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.monday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_1()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_1()) + "</NOBR></td><td>" + (item.getBreaks_1() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_1()) + "</td></tr>")
                        + ((item.getTime_begin_2() == null || item.getTime_end_2() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.tuesday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_2()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_2()) + "</NOBR></td><td>" + (item.getBreaks_2() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_2()) + "</td></tr>")
                        + ((item.getTime_begin_3() == null || item.getTime_end_3() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.wednesday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_3()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_3()) + "</NOBR></td><td>" + (item.getBreaks_3() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_3()) + "</td></tr>")
                        + ((item.getTime_begin_4() == null || item.getTime_end_4() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.thursday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_4()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_4()) + "</NOBR></td><td>" + (item.getBreaks_4() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_4()) + "</td></tr>")
                        + ((item.getTime_begin_5() == null || item.getTime_end_5() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.friday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_5()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_5()) + "</NOBR></td><td>" + (item.getBreaks_5() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_5()) + "</td></tr>")
                        + ((item.getTime_begin_6() == null || item.getTime_end_6() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.saturday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_6()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_6()) + "</NOBR></td><td>" + (item.getBreaks_6() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_6()) + "</td></tr>")
                        + ((item.getTime_begin_7() == null || item.getTime_end_7() == null) ? "" : "<tr><td>" + getLocaleMessage("calendar.day.sunday") + "</td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_7()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_7()) + "</NOBR></td><td>" + (item.getBreaks_7() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_7()) + "</td></tr>"))
                        : ((item.getTime_begin_1() == null || item.getTime_end_1() == null) ? "" : "<tr><td><NOBR>" + getLocaleMessage("calendar.even") + "</NOBR></td><td>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_1()) + "</td><td>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_1()) + "</td><td>" + (item.getBreaks_1() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_1()) + "</td></tr>"
                                + ((item.getTime_begin_2() == null || item.getTime_end_2() == null) ? "" : "<tr><td><NOBR>" + getLocaleMessage("calendar.odd") + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.from") + " " + Uses.FORMAT_HH_MM.format(item.getTime_begin_2()) + "</NOBR></td><td><NOBR>" + getLocaleMessage("calendar.time.to") + " " + Uses.FORMAT_HH_MM.format(item.getTime_end_2()) + "</NOBR></td><td>" + (item.getBreaks_2() == null ? getLocaleMessage("breaks.no") : getLocaleMessage("breaks.breaks") + ": " + item.getBreaks_2()) + "</td></tr>"))) + "</table>" + "</span>";
        labelSchedule.setText(str);
    }

    /**
     * Действия по смене выбранного итема в списке планов расписания.
     */
    private void calendarListChange() {
        if (listCalendar.getLastVisibleIndex() == -1) {
            textFieldCalendarName.setText("");
            return;
        }
        final QCalendar item = (QCalendar) listCalendar.getSelectedValue();
        if (item == null) {
            return;
        }
        textFieldCalendarName.setText(item.getName());

        tableCalendar.setModel(new CalendarTableModel(item.getId()));
        tableCalendar.setDefaultRenderer(FreeDay.class, new TableCell((Integer) (spinCalendarYear.getValue())));
        tableCalendar.setDefaultRenderer(Object.class, new TableCell((Integer) (spinCalendarYear.getValue())));
        tableCalendar.getColumnModel().getColumn(0).setPreferredWidth(500);

        listSpecSced.setModel(new DefaultComboBoxModel(item.getSpecSchedules().toArray()));
    }

    /**
     * Действия по смене выбранного итема в списке услуг.
     */
    private void serviceListChange() {
        final TreePath selectedPath = treeServices.getSelectionPath();
        if (selectedPath != null) {
            showServiceInfo((QService) selectedPath.getLastPathComponent());
        }
    }

    private void showServiceInfo(QService service) {
        String s = "";
        for (QServiceLang sl : service.getLangs()) {
            s = s + ", " + sl.getLang();
        }
        s = s.length() > 1 ? "[" + s.substring(2) + "]" : "";
        labelServiceInfo.setText("<html><body text=\"#336699\"> "
                + "<font color=\"#"
                + (service.getStatus() == 1
                        ? "00AA00\">" + getLocaleMessage("service.kind.active")
                        : (service.getStatus() == 0 ? "CCAA00\">" + getLocaleMessage("service.kind.not_active") : "DD0000\">" + getLocaleMessage("service.kind.unavailable"))) + "/" + service.getPoint()
                + "</font>"
                + ";    "
                + getLocaleMessage("service.prefix") + ": " + "<font color=\"#DD0000\">" + service.getPrefix() + "</font>" + ";  "
                + (service.getEnable() == 1 ? "" : "<font color=\"#FF0000\">!*** </font>")
                + s + " " + getLocaleMessage("service.service") + service.getSeqId() + ": \""
                + "<font color=\"#000000\">"
                + service.getName() + "\"    "
                + "</font>"
                + getLocaleMessage("service.description") + ": " + service.getDescription()
                + ";<br>" + getLocaleMessage("service.restrict_day_reg") + ": " + (service.getDayLimit() == 0 ? getLocaleMessage("service.work_calendar.no") : service.getDayLimit())
                + ";<br>" + getLocaleMessage("service.restrict_adv_reg") + " " + service.getAdvanceTimePeriod() + " " + getLocaleMessage("service.min") + ": " + service.getAdvanceLimit()
                + ";<br>  " + getLocaleMessage("service.restrict_adv_period") + ": " + service.getAdvanceLimitPeriod()
                + ";<br>" + getLocaleMessage("service.work_calendar") + ": " + "<font color=\"#" + (service.getCalendar() == null ? "DD0000\">" + getLocaleMessage("service.work_calendar.no") : "000000\">" + service.getCalendar().toString()) + "</font>" + ";  " + getLocaleMessage("service.work_calendar.plan") + ": " + "<font color=\"#" + (service.getSchedule() == null ? "DD0000\">" + getLocaleMessage("service.work_calendar.no") : "000000\">" + service.getSchedule().toString()) + "</font>" + ";<br>"
                + (service.getInput_required() ? getLocaleMessage("service.required_client_data") + ": \"" + service.getInput_caption().replaceAll("<[^>]*>", "") + "\"(" + service.getPersonDayLimit() + ")" : getLocaleMessage("service.required_client_data.not")) + ";<br>   "
                + (service.getResult_required() ? getLocaleMessage("service.required_result") : getLocaleMessage("service.required_result.not")) + ";");
        labelButtonCaption.setText(service.getButtonText());

        final LinkedList<QUser> usrs = new LinkedList<>();
        for (QUser user : QUserList.getInstance().getItems()) {
            for (QPlanService plan : user.getPlanServices()) {
                if (plan.getService().getId().equals(service.getId())) {
                    usrs.add(user);
                    break;
                }
            }
        }
        userServsList.setModel(new DefaultComboBoxModel(usrs.toArray()));
    }

    /**
     * Действия по смене выбранного итема в дереве инфоузлов.
     */
    private void infoListChange() {
        final TreePath selectedPath = treeInfo.getSelectionPath();
        if (selectedPath != null) {
            showInfoInfo((QInfoItem) selectedPath.getLastPathComponent());
        }
    }

    private void showInfoInfo(QInfoItem item) {
        textFieldInfoItemName.setText(item.getName());
        labelInfoItem.setText(item.getHTMLText());
        textPaneInfoItem.setText(item.getHTMLText());
        textPaneInfoPrint.setText(item.getTextPrint());
    }
    /**
     * Ограничение ввода время начала и конце работы системы.
     */
    private InputVerifier DateVerifier = new InputVerifier() {

        @Override
        public boolean verify(JComponent input) {
            final DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            try {
                if (input == textFieldStartTime) {
                    dateFormat.parse(textFieldStartTime.getText());
                }
                if (input == textFieldFinishTime) {
                    dateFormat.parse(textFieldFinishTime.getText());
                }
            } catch (ParseException ex) {
                System.err.println("Незапарсилась дата " + textFieldStartTime.getText() + " или" + textFieldFinishTime.getText());
                return false;
            }
            saveNet();
            return true;
        }
    };

    /**
     * Загрузим настройки.
     */
    private void loadSettings() {
        final Properties settings = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream("config" + File.separator + "admin.properties");
        } catch (FileNotFoundException ex) {
            throw new ClientException(getLocaleMessage("error.file_not_read") + ". " + ex);
        }
        try {
            settings.load(in);
        } catch (IOException ex) {
            throw new ClientException(getLocaleMessage("error.params_not_read") + ". " + ex);
        }
        textFieldServerAddr.setText(settings.getProperty(SERVER_ADRESS));
        spinnerServerPort.setValue(Integer.parseInt(settings.getProperty(SERVER_PORT)));
        checkBoxServerAuto.setSelected("1".equals(settings.getProperty(SERVER_AUTO_REQUEST)));
        textFieldClientAdress.setText(settings.getProperty(CLIENT_ADRESS));
        spinnerClientPort.setValue(Integer.parseInt(settings.getProperty(CLIENT_PORT)));
        checkBoxClientAuto.setSelected("1".equals(settings.getProperty(CLIENT_AUTO_REQUEST)));
        serverPluginStat = settings.getProperty("server_plugin_stat", "");
    }

    String serverPluginStat = "";

    /**
     * Сохраним настройки.
     */
    private void saveSettings() {
        final Properties settings = new Properties();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("config" + File.separator + "admin.properties");
        } catch (FileNotFoundException ex) {
            throw new ClientException(getLocaleMessage("error.file_not_save") + ". " + ex);
        }
        settings.put(SERVER_ADRESS, textFieldServerAddr.getText());
        settings.put(SERVER_PORT, String.valueOf(spinnerServerPort.getValue()));
        settings.put(SERVER_AUTO_REQUEST, checkBoxServerAuto.isSelected() ? "1" : "0");
        settings.put(CLIENT_ADRESS, textFieldClientAdress.getText());
        settings.put(CLIENT_PORT, String.valueOf(spinnerClientPort.getValue()));
        settings.put(CLIENT_AUTO_REQUEST, checkBoxClientAuto.isSelected() ? "1" : "0");
        settings.put("server_plugin_stat", serverPluginStat);
        try {
            settings.store(out, "Settings of admining and monitoring");
        } catch (IOException ex) {
            throw new ClientException(getLocaleMessage("error.file_output") + ". " + ex);
        }
    }

    /**
     * Загрузим конфигурацию системы.
     */
    private void loadConfig() {
        listUsers.setModel(QUserList.getInstance());
        listOffices.setModel(QOfficeList.getInstance());
        treeResp.setModel(QResponseTree.getInstance());
        listResults.setModel(QResultList.getInstance());
        treeServices.setModel(QServiceTree.getInstance());
        treeInfo.setModel(QInfoTree.getInstance());
        listSchedule.setModel(QScheduleList.getInstance());
        listBreaks.setModel(QBreaksList.getInstance());
        listCalendar.setModel(QCalendarList.getInstance());
        listReposts.setModel(QReportsList.getInstance());

        spinnerPropServerPort.setValue(ServerProps.getInstance().getProps().getServerPort());
        spinnerPropClientPort.setValue(ServerProps.getInstance().getProps().getClientPort());
        spinnerWebServerPort.setValue(ServerProps.getInstance().getProps().getWebServerPort());
        textFieldStartTime.setText(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getStartTime()));
        textFieldFinishTime.setText(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getFinishTime()));
        textFieldURLWebService.setText(ServerProps.getInstance().getProps().getSkyServerUrl());

        spinnerWaitMax.setValue(ServerProps.getInstance().getStandards().getWaitMax());
        spinnerWorkMax.setValue(ServerProps.getInstance().getStandards().getWorkMax());
        spinnerDowntimeNax.setValue(ServerProps.getInstance().getStandards().getDowntimeMax());
        spinnerLineServiceMax.setValue(ServerProps.getInstance().getStandards().getLineServiceMax());
        spinnerLineTotalMax.setValue(ServerProps.getInstance().getStandards().getLineTotalMax());
        spinnerRelocation.setValue(ServerProps.getInstance().getStandards().getRelocation());

        textFieldZonBoadrServAddr.setText(ServerProps.getInstance().getProps().getZoneBoardServAddr());
        spinnerZonBoadrServPort.setValue(ServerProps.getInstance().getProps().getZoneBoardServPort());

        spinnerBranchId.setValue(ServerProps.getInstance().getProps().getBranchOfficeId());
        spinnerFirstNumber.setValue(ServerProps.getInstance().getProps().getFirstNumber());
        spinnerLastNumber.setValue(ServerProps.getInstance().getProps().getLastNumber());
        spinExtPrior.setValue(ServerProps.getInstance().getProps().getExtPriorNumber());
        rbKindCommon.setSelected(!ServerProps.getInstance().getProps().getNumering());
        rbKindPersonal.setSelected(ServerProps.getInstance().getProps().getNumering());
        spinnerRemoveRecall.getModel().setValue(ServerProps.getInstance().getProps().getLimitRecall());
        chBoxBtnFreeDsn.setSelected(ServerProps.getInstance().getProps().getButtonFreeDesign());

        spinnerBlackListTimeMin.setValue(ServerProps.getInstance().getProps().getBlackTime());

        // выставим начальные позиции в списках
        if (listUsers.getLastVisibleIndex() != -1) {
            listUsers.setSelectedIndex(0);
        }

        if (treeResp.getModel().getRoot() != null) {
            treeResp.setSelectionPath(new TreePath(treeResp.getModel().getRoot()));
        }

        if (listSchedule.getLastVisibleIndex() != -1) {
            listSchedule.setSelectedIndex(0);
        }

        if (listCalendar.getLastVisibleIndex() != -1) {
            listCalendar.setSelectedIndex(0);
        }

        if (treeServices.getModel().getRoot() != null) {
            treeServices.setSelectionPath(new TreePath(treeServices.getModel().getRoot()));
        }

        if (treeInfo.getModel().getRoot() != null) {
            treeInfo.setSelectionPath(new TreePath(treeInfo.getModel().getRoot()));
        }

        if (listUserService.getLastVisibleIndex() != -1) {
            listUserService.setSelectedIndex(0);
        }

        final boolean bClassicType = IIndicatorBoard.CLASSIC.equalsIgnoreCase(ServerProps.getInstance().getProperty(IIndicatorBoard.SECTION, IIndicatorBoard.PARAMETER, IIndicatorBoard.CLASSIC));
        rbmClassic.setSelected(bClassicType);
        rbmHtml.setSelected(!bClassicType);
    }

    private class ServerNetProperty implements INetProperty {

        @Override
        public Integer getPort() {
            return (Integer) spinnerServerPort.getValue();
        }

        @Override
        public InetAddress getAddress() {
            InetAddress adr = null;
            try {
                adr = InetAddress.getByName(textFieldServerAddr.getText());
            } catch (UnknownHostException ex) {
                throw new ClientException("Error! " + ex);
            }
            return adr;
        }
    }

    protected boolean checkServer() {
        QLog.l().logger().info("Запрос о состоянии на сервер.");
        //элемент ответа.
        final LinkedList<ServiceInfo> srvs;
        try {
            final ServerNetProperty snp = new ServerNetProperty();
            srvs = NetCommander.getServerState(snp);
            listPostponed.setModel(QPostponedList.getInstance().loadPostponedList(NetCommander.getPostponedPoolInfo(snp)));
        } catch (Exception ex) {
            listPostponed.setModel(QPostponedList.getInstance().loadPostponedList(new LinkedList<>()));
            labelServerState.setText("<HTML><b><span style='font-size:20.0pt;color:red;'>" + getLocaleMessage("admin.message.server_not_start") + "</span></b>");
            QLog.l().logger().error("Сервер ответил на запрос о состоянии: \"" + ex + "\"");
            tray.showMessageTray(getLocaleMessage("tray.server"), getLocaleMessage("tray.message.stop_server"), QTray.MessageType.WARNING);
            return false;
        }
        //Сформируем ответ
        final String red = "<td align=\"center\"><span style='font-size:12.0pt;color:red;'>";
        final String green = "<td align=\"center\"><span style='font-size:12.0pt;color:green;'>";
        int col = 0;
        String html = "";
        for (ServiceInfo inf : srvs) {
            col += inf.getCountWait();
            html = html
                    + "<tr>"
                    + "" + (0 == inf.getCountWait() ? green : red) + inf.getCountWait() + "</span></td>"
                    + "<td align=\"center\">" + inf.getFirstNumber() + "</td>"
                    + "<td>" + (inf.getServiceName().length() > 80 ? inf.getServiceName().substring(0, 80) + "..." : inf.getServiceName()) + "</td>"
                    + "</tr>";
        }
        final String first = "<html>" + getLocaleMessage("admin.info.total_clients") + ": " + (0 == col ? "<span style='font-size:12.0pt;color:green;'>" : "<span style='font-size:12.0pt;color:red;'>") + col + "</span>";
        labelServerState.setText(first
                + "<table border=\"1\">"
                + "<tr>"
                + " <td align=\"center\"<span style='font-size:16.0pt;color:red;'>"
                + getLocaleMessage("admin.info.total_wait")
                + "</span></td> "
                + "<td align=\"center\"><span style='font-size:16.0pt;color:red;'>"
                + getLocaleMessage("admin.info.next_number")
                + "</span></td>"
                + " <td align=\"center\"><span style='font-size:16.0pt;color:red;'>"
                + getLocaleMessage("service.service")
                + "</span></td>"
                + "</tr>"
                + html
                + "</table></html>");
        return true;
    }

    protected boolean checkWelcome(String command) {
        QLog.l().logger().info("Запрос о состоянии на пункт регистрации.");
        command = command == null ? "Empty" : command;
        final String result;
        try {
            result = NetCommander.getWelcomeState(netPropWelcome(), command, cbDropTicketsCnt.isSelected());
        } catch (Exception ex) {
            labelWelcomeState.setText("<HTML><b><span style='font-size:20.0pt;color:red;'>" + getLocaleMessage("admin.message.welcome_not_start") + "</span></b>");
            QLog.l().logger().error("Пункт регистрации не ответил на запрос о состоянии или поризошла ошибка. \"" + ex + "\"");
            tray.showMessageTray(getLocaleMessage("tray.message_stop_server.title"), getLocaleMessage("tray.message_stop_server.caption"), QTray.MessageType.WARNING);
            return false;
        }
        labelWelcomeState.setText("<HTML><span style='font-size:20.0pt;color:green;'>" + getLocaleMessage("admin.welcome") + " \"" + result + "\"</span>");
        cbDropTicketsCnt.setSelected(false);
        return true;
    }

    protected INetProperty netPropWelcome() {
        return new INetProperty() {

            @Override
            public Integer getPort() {
                return (Integer) spinnerClientPort.getValue();
            }

            @Override
            public InetAddress getAddress() {
                InetAddress adr = null;
                try {
                    adr = InetAddress.getByName(textFieldClientAdress.getText());
                } catch (UnknownHostException ex) {
                    throw new ClientException("Error! " + ex);
                }
                return adr;
            }
        };
    }

    /**
     * Сохранение данных о юзере, повесим на потерю фокуса элементов ввода.
     */
    public void saveUser() {
        if (changeUser) {
            final QUser user = (QUser) listUsers.getSelectedValue();
            user.setName(textFieldUserName.getText());
            user.setPoint(textFieldUserIdent.getText());
            user.setPassword(new String(passwordFieldUser.getPassword()));
            user.setAdressRS((Integer) spinnerUserRS.getValue());
            user.setPointExt(textFieldExtPoint.getText());
            user.setAdminAccess(checkBoxAdmin.isSelected());
            user.setReportAccess(checkBoxReport.isSelected());
        }
    }

    /**
     * Сохранение данных о сетевых настройках, повесим на нажатие кнопок элементов ввода.
     */
    public void saveNet() {

        ServerProps.getInstance().getProps().setServerPort((Integer) spinnerPropServerPort.getValue());
        ServerProps.getInstance().getProps().setClientPort((Integer) spinnerPropClientPort.getValue());
        ServerProps.getInstance().getProps().setWebServerPort((Integer) spinnerWebServerPort.getValue());
        if ((Integer) spinnerFirstNumber.getValue() > (Integer) spinnerLastNumber.getValue()) {
            spinnerFirstNumber.setValue(1);
            spinnerLastNumber.setValue(999);
        }
        ServerProps.getInstance().getProps().setZoneBoardServPort((Integer) spinnerZonBoadrServPort.getValue());
        ServerProps.getInstance().getProps().setZoneBoardServAddr(textFieldZonBoadrServAddr.getText());

        ServerProps.getInstance().getProps().setFirstNumber((Integer) spinnerFirstNumber.getValue());
        ServerProps.getInstance().getProps().setBranchOfficeId((Long) spinnerBranchId.getValue());
        ServerProps.getInstance().getProps().setSkyServerUrl(textFieldURLWebService.getText());
        ServerProps.getInstance().getProps().setLastNumber((Integer) spinnerLastNumber.getValue());
        ServerProps.getInstance().getProps().setExtPriorNumber((Integer) spinExtPrior.getValue());
        ServerProps.getInstance().getProps().setNumering(rbKindPersonal.isSelected());
        ServerProps.getInstance().getProps().setBlackTime((int) spinnerBlackListTimeMin.getValue());
        ServerProps.getInstance().getProps().setLimitRecall((int) spinnerRemoveRecall.getValue());
        ServerProps.getInstance().getProps().setButtonFreeDesign(chBoxBtnFreeDsn.isSelected());

        ServerProps.getInstance().getStandards().setWaitMax((Integer) spinnerWaitMax.getValue());
        ServerProps.getInstance().getStandards().setWorkMax((Integer) spinnerWorkMax.getValue());
        ServerProps.getInstance().getStandards().setDowntimeMax((Integer) spinnerDowntimeNax.getValue());
        ServerProps.getInstance().getStandards().setLineServiceMax((Integer) spinnerLineServiceMax.getValue());
        ServerProps.getInstance().getStandards().setLineTotalMax((Integer) spinnerLineTotalMax.getValue());
        ServerProps.getInstance().getStandards().setRelocation((Integer) spinnerRelocation.getValue());
        try {
            ServerProps.getInstance().getProps().setStartTime(Uses.FORMAT_HH_MM.parse(textFieldStartTime.getText()));
            ServerProps.getInstance().getProps().setFinishTime(Uses.FORMAT_HH_MM.parse(textFieldFinishTime.getText()));
        } catch (ParseException ex) {
            QLog.l().logger().error("Проблемы с сохранение сетевых настроек. ", ex);
        }
    }

    private final static SecureRandom RANDOM = new SecureRandom();

    public static String nextRes(int i) {
        return new BigInteger(i, RANDOM).toString(32);
    }

    private static String getStat() {
        final Properties settings = new Properties();
        FileInputStream in1 = null;
        try {
            in1 = new FileInputStream("config" + File.separator + "admin.properties");
        } catch (FileNotFoundException ex) {
            throw new ClientException(getLocaleMessage("error.file_not_read") + ". " + ex);
        }
        try {
            settings.load(in1);
        } catch (IOException ex) {
            return "err";
        }
        String server_plugin_stat = settings.getProperty("server_plugin_stat", "");

        if (server_plugin_stat.length() < 4 || QConfig.cfg().isIDE()) {
            server_plugin_stat = QConfig.cfg().isIDE() ? "dev" : nextRes(40);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream("config" + File.separator + "admin.properties");
            } catch (FileNotFoundException ex) {
                throw new ClientException(getLocaleMessage("error.file_not_save") + ". " + ex);
            }
            settings.put("server_plugin_stat", server_plugin_stat);
            try {
                settings.store(out, "Settings of admining and monitoring");
            } catch (IOException ex) {
                return "err";
            }
        }
        return server_plugin_stat;
    }

    private static String getMac() {
        String server_plugin_mac = "no";
        try {
            final Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            while (networks.hasMoreElements()) {
                final NetworkInterface network = networks.nextElement();
                final byte[] mac = network.getHardwareAddress();

                if (mac != null) {
                    StringBuilder sb1 = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb1.append(String.format("%02X", mac[i]));
                    }
                    server_plugin_mac = sb1.toString();
                    sb1.setLength(0);
                    if (mac.length == 6) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        return server_plugin_mac;
    }

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        QLog.initial(args, 3);
        Locale.setDefault(Locales.getInstance().getLangCurrent());

        //проверим готовность БД
        String checkdb = "0";
        String checkdb2 = "no";
        try {
            Class.forName(Spring.getInstance().getDriverClassName());
            final ResultSet rs = DriverManager.getConnection(Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword()).prepareStatement("select  (select count(1) from clients) c0, (select count(1) from users where deleted is null) c1, (select count(1) from services where deleted is null) c2, (select name from services where prent_id is null) c3").executeQuery();
            while (rs.next()) {
                checkdb = rs.getInt(1) + "-" + rs.getInt(2) + "-" + rs.getInt(3);
                checkdb2 = rs.getString(4);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ClientException(e.getLocalizedMessage() + "\n" + e.getCause().getLocalizedMessage());
        }
        final String cdb = checkdb;
        final String cdb2 = new BCodec().encode(URLEncoder.encode(checkdb2, "utf8"));
        QLog.l().logger().info("DB is OK.");

        //запустим поток обновления пейджера, пусть поработает
        final Thread tPager = new Thread(() -> {
            FAbout.loadVersionSt();
            String result = "";
            try {
                final URL url = new URL(PAGER_URL + "/qskyapi/getpagerdata?qsysver=" + FAbout.VERSION_ + "&qplugins=" + getMac() + "-" + getStat() + "&checkdb=" + cdb + "&checkdb2=" + cdb2);
                //System.out.println(url.toString());
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Java bot");
                conn.connect();
                final int code = conn.getResponseCode();
                if (code == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf8"))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            result += inputLine;
                        }
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                System.err.println("Pager not enabled. " + e);
                return;
            }
            final Gson gson = GsonPool.getInstance().borrowGson();
            try {
                final Answer answer = gson.fromJson(result, Answer.class);
                forPager = answer;
                if (answer.getData().size() > 0) {
                    forPager.start();
                }
            } catch (Exception e) {
                System.err.println("Pager not enabled but working. " + e);
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }
        });
        tPager.setDaemon(true);
        tPager.start();

        Uses.startSplash();
        // Загрузка плагинов из папки plugins
        Uses.loadPlugins("./plugins/");
        // Определим кто работает на данном месте.
        FLogin.logining(QUserList.getInstance(), null, true, 3, FLogin.LEVEL_ADMIN);
        Uses.showSplash();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    System.out.println(info.getName());
                    /*Metal Nimbus CDE/Motif Windows   Windows Classic  //GTK+*/
                    if ("Windows".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
                if ("/".equals(File.separator)) {
                    final FontUIResource f = new FontUIResource(new Font("Serif", Font.PLAIN, 10));
                    final Enumeration<Object> keys = UIManager.getDefaults().keys();
                    while (keys.hasMoreElements()) {
                        final Object key = keys.nextElement();
                        final Object value = UIManager.get(key);
                        if (value instanceof FontUIResource) {
                            final FontUIResource orig = (FontUIResource) value;
                            final Font font1 = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                            UIManager.put(key, new FontUIResource(font1));
                        }
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }
            try {
                form = new FAdmin();
                if (forPager != null) {
                    forPager.showData(false);
                } else {
                    form.panelPager.setVisible(false);
                    form.tabbedPaneMain.remove(form.tabHide);
                }
                form.setVisible(true);
            } catch (Exception ex) {
                QLog.l().logger().error("Проблемы с ссозданием формы админки. ", ex);
            } finally {
                Uses.closeSplash();
            }
        });
    }
    private static FAdmin form = null;
    private static Answer forPager = null;
    private static final String PAGER_URL = "http://localhost:8080";
    //private static final String PAGER_URL = "http://dev.apertum.ru:8080";
    //private static final String PAGER_URL = "http://109.120.172.108:8080";

    @Action
    public void hideWindow() {
    }

    @Action
    public void addOffice() {
        // Запросим название юзера и если оно уникально, то примем
        String officeName = "";
        boolean flag = true;

        while (flag) {
            officeName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_office_dialog.title"), getLocaleMessage("admin.add_office_dialog.caption"), 3, null, null, officeName);
            if (officeName == null) {
                return;
            }
            if ("".equals(officeName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err1.title"), getLocaleMessage("admin.add_service_dialog.err1.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (officeName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err3.title"), getLocaleMessage("admin.add_service_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (officeName.length() > 100) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err4.title"), getLocaleMessage("admin.add_service_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        QLog.l().logger().debug("Adding a user \"" + officeName + "\"");
        final QOffice office = new QOffice();
        office.setName(officeName);
        office.setSmartboardType("callbyticket");
        QOfficeList.getInstance().addElement(office);
        listOffices.setSelectedValue(office, true);
    }

    @Action
    public void deleteOffice() {
        QLog.l().logger().debug("Delete Office");

        if (listOffices.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.remove_office_dialog.title") + " \"" + ((QOffice) listOffices.getSelectedValue()).getName() + "\"?",
                    getLocaleMessage("admin.remove_user_dialog.caption"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }

            final int del = listOffices.getSelectedIndex();
            final QOfficeList m = (QOfficeList) listOffices.getModel();
            final int col = m.getSize();

            final QOffice office = (QOffice) listOffices.getSelectedValue();
            QOfficeList.getInstance().removeElement(office);

            if (col != 1) {
                if (col == del + 1) {
                    listOffices.setSelectedValue(m.getElementAt(del - 1), true);
                } else if (col > del + 1) {
                    listOffices.setSelectedValue(m.getElementAt(del), true);
                }
            }
        }
    }

    @Action
    public void addUser() {
        // Запросим название юзера и если оно уникально, то примем
        String userName = "";
        QOffice office = null;
        boolean flag = true;

        List<QOffice> offices = Spring.getInstance().getHt().findByCriteria(
            DetachedCriteria.forClass(QOffice.class)
                .add(Property.forName("deleted").isNull())
                    .setFetchMode("services", FetchMode.EAGER)
                .setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))
        );

        while (flag) {
            JTextField userNameTextField = new JTextField();
            JComboBox officeDropdown = new JComboBox();

            for (QOffice dropdownOffice : offices) {
                officeDropdown.addItem(dropdownOffice);
            }

            Object[] message = {
                    "Username:", userNameTextField,
                    "Office:", officeDropdown
            };
            int option = JOptionPane.showConfirmDialog(this, message, getLocaleMessage("admin.add_user_dialog.title"), JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                userName = userNameTextField.getText();
                office = (QOffice) officeDropdown.getSelectedItem();

                if ("".equals(userName)) {
                    flag = true;
                } else if (QUserList.getInstance().hasByName(userName)) {
                    flag = true;
                } else if (userName.indexOf('\"') != -1) {
                    flag = true;
                } else if (userName.length() > 150) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        QLog.l().logger().debug("Adding a user \"" + userName + "\"");
        final QUser user = new QUser();
        user.setPlanServices(new LinkedList<>());
        user.setName(userName);
        user.setOffice(office);
        user.setPassword("");
        user.setPoint("");
        user.setAdressRS(32);
        user.addPlanServiceByOffice();
        QUserList.getInstance().addElement(user);
        listUsers.setSelectedValue(user, true);
    }

    @Action
    public void addNewUserByCopy() {
        if (listUsers.getSelectedIndex() != -1) {
            final QUser user = (QUser) listUsers.getSelectedValue();
            // Запросим название юзера и если оно уникально, то примем
            String userName = "";
            boolean flag = true;
            while (flag) {
                userName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_user_dialog.title"), getLocaleMessage("admin.add_user_dialog.caption"), 3, null, null, userName);
                if (userName == null) {
                    return;
                }
                if ("".equals(userName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_user_dialog.err1.title"), getLocaleMessage("admin.add_user_dialog.err1.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (QUserList.getInstance().hasByName(userName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_user_dialog.err2.title"), getLocaleMessage("admin.add_user_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (userName.indexOf('\"') != -1) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_user_dialog.err3.title"), getLocaleMessage("admin.add_user_dialog.err3.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (userName.length() > 150) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_user_dialog.err4.title"), getLocaleMessage("admin.add_user_dialog.err4.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else {
                    flag = false;
                }
            }
            QLog.l().logger().debug("delete user \"" + userName + "\"");
            final QUser newUser = new QUser();
            LinkedList<QPlanService> plan = new LinkedList<>();
            user.getPlanServices().stream().forEach((pl) -> {
                plan.add(new QPlanService(pl.getService(), pl.getUser(), pl.getCoefficient()));
            });
            newUser.setPlanServices(plan);
            newUser.setName(userName);
            newUser.setPassword("");
            newUser.setPoint(user.getPoint());
            newUser.setAdressRS(user.getAdressRS());
            newUser.setPointExt(user.getPointExt());
            newUser.setReportAccess(user.getReportAccess());
            newUser.setAdminAccess(user.getAdminAccess());
            newUser.setAdminAccess(user.getAdminAccess());
            QUserList.getInstance().addElement(newUser);
            listUsers.setSelectedValue(newUser, true);
        }
    }

    @Action
    public void renameUser() {
        if (listUsers.getSelectedIndex() != -1) {
            final QUser user = (QUser) listUsers.getSelectedValue();
            String userName = user.getName();
            boolean flag = true;
            while (flag) {
                userName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.rename_user_dialog.title"), getLocaleMessage("admin.rename_user_dialog.caption"), 3, null, null, userName);
                if (userName == null) {
                    return;
                }
                if ("".equals(userName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_user_dialog.err1.title"), getLocaleMessage("admin.rename_user_dialog.err1.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (QUserList.getInstance().hasByName(userName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_user_dialog.err2.title"), getLocaleMessage("admin.rename_user_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (userName.indexOf('\"') != -1) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_user_dialog.err3.title"), getLocaleMessage("admin.rename_user_dialog.err3.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (userName.length() > 150) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_user_dialog.err4.title"), getLocaleMessage("admin.rename_user_dialog.err4.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else {
                    flag = false;
                }
            }
            user.setName(userName);
            textFieldUserName.setText(userName);
            listUsers.setSelectedValue(user, true);
        }
    }

    @Action
    public void deleteUser() {
        if (listUsers.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.remove_user_dialog.title") + " \"" + ((QUser) listUsers.getSelectedValue()).getName() + "\"?",
                    getLocaleMessage("admin.remove_user_dialog.caption"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем пользователя \"" + ((QUser) listUsers.getSelectedValue()).getName() + "\"");

            final int del = listUsers.getSelectedIndex();
            final QUserList m = (QUserList) listUsers.getModel();
            final int col = m.getSize();

            final QUser user = (QUser) listUsers.getSelectedValue();
            //проверим не последний ли это админ
            if (user.getAdminAccess()) {
                int cnt = 0;
                for (int i = 0; i < listUsers.getModel().getSize(); i++) {
                    if (((QUser) listUsers.getModel().getElementAt(i)).getAdminAccess()) {
                        cnt++;
                    }
                }
                if (cnt == 1) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.remove_user_dialog.err.title"), getLocaleMessage("admin.remove_user_dialog.err.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            QUserList.getInstance().removeElement(user);

            if (col != 1) {
                if (col == del + 1) {
                    listUsers.setSelectedValue(m.getElementAt(del - 1), true);
                } else if (col > del + 1) {
                    listUsers.setSelectedValue(m.getElementAt(del), true);
                }
            }
        }
    }

    @Action
    public void addService() throws DocumentException {
        // We will request the name of the service and if it is unique and not empty, then we will accept
        String serviceName = "";

        boolean flag = true;
        while (flag) {
            serviceName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_service_dialog.title"), getLocaleMessage("admin.add_service_dialog.caption"), 3, null, null, serviceName);
            if (serviceName == null) {
                return;
            }
            if ("".equals(serviceName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err1.title"), getLocaleMessage("admin.add_service_dialog.err1.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (QServiceTree.getInstance().hasByName(serviceName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err2.title"), getLocaleMessage("admin.add_service_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (serviceName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err3.title"), getLocaleMessage("admin.add_service_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (serviceName.length() > 2001) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_service_dialog.err4.title"), getLocaleMessage("admin.add_service_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        // Create a new service and add it to the model
        final QService newService = new QService();
        newService.setName(serviceName);
        newService.setDescription(serviceName);
        newService.setStatus(1);
        newService.setSoundTemplate("021111");
        newService.setAdvanceTimePeriod(60);
        newService.setSmartboard("Y");
        newService.setCalendar(QCalendarList.getInstance().getById(1));
        if (QScheduleList.getInstance().getSize() != 0) {
            newService.setSchedule(QScheduleList.getInstance().getElementAt(0));
        }
        newService.setButtonText("<html><b><p align=center><span style='font-size:20.0pt;color:red'>" + serviceName + "</span></b>");
        //проставим букавку
        newService.setPrefix("A");
        QServiceTree.sailToStorm(QServiceTree.getInstance().getRoot(), (TreeNode service) -> {
            if (service.isLeaf()) {
                String pr = ((QService) service).getPrefix();
                if (!pr.isEmpty()) {
                    if (pr.substring(pr.length() - 1).compareToIgnoreCase(newService.getPrefix().substring(newService.getPrefix().length() - 1)) >= 0) {
                        newService.setPrefix(String.valueOf((char) (pr.substring(pr.length() - 1).charAt(0) + 1)).toUpperCase());
                    }
                }
            }
        });

        final QService parentService = (QService) treeServices.getLastSelectedPathComponent();
        QServiceTree.getInstance().insertNodeInto(newService, parentService, parentService.getChildCount());
        final TreeNode[] nodes = QServiceTree.getInstance().getPathToRoot(newService);
        final TreePath path = new TreePath(nodes);
        treeServices.scrollPathToVisible(path);
        treeServices.setSelectionPath(path);
        // Parent service to the new service should be excluded from the list of users tied to users. She became a group
        deleteServiceFromUsers(parentService);

        QLog.l().logger().debug("Добавлена услуга \"" + serviceName + "\" в группу \"" + parentService.getName() + "\"");
    }

    @Action
    public void renameService() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null) {
            String serviceName = service.getName();
            boolean flag = true;
            while (flag) {
                serviceName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.rename_service_dialog.title"), getLocaleMessage("admin.rename_service_dialog.caption"), 3, null, null, serviceName);
                if (serviceName == null) {
                    return;
                }
                if ("".equals(serviceName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_service_dialog.err1.title"), getLocaleMessage("admin.rename_service_dialog.err1.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (QServiceTree.getInstance().hasByName(serviceName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_service_dialog.err2.title"), getLocaleMessage("admin.rename_service_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (serviceName.indexOf('\"') != -1) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_service_dialog.err3.title"), getLocaleMessage("admin.rename_service_dialog.err3.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else if (serviceName.length() > 2001) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.rename_service_dialog.err4.title"), getLocaleMessage("admin.rename_service_dialog.err4.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                } else {
                    flag = false;
                }
            }
            service.setName(serviceName);
        }
    }

    /**
     * Из привязок к услугам всех юзеров убрать привязку к данной услуге и всех ее вложенных.
     *
     * @param service удаляемая услуга
     */
    private void deleteServicesFromUsers(QService service) {

        QServiceTree.sailToStorm(service, (TreeNode service1) -> {
            deleteServiceFromUsers((QService) service1);
        });
    }

    /**
     * Из привязок к услугам всех юзеров убрать привязку к данной услуге.
     *
     * @param service удаляемая услуга
     */
    private void deleteServiceFromUsers(QService service) {
        QUserList.getInstance().getItems().stream().filter((user) -> (user.hasService(service.getId()))).forEach((user) -> {
            user.deletePlanService(service.getId());
        });
    }

    @Action
    public void deleteService() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && !service.isRoot()) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.remove_service_dialog.title") + " " + (service.isLeaf() ? getLocaleMessage("admin.remove_service_dialog.title_1") : getLocaleMessage("admin.remove_service_dialog.title_2")) + "\n\"" + (service.getName().length() > 85 ? service.getName().substring(0, 85) + " ..." : service.getName()) + "\"?",
                    getLocaleMessage("admin.remove_service_dialog.caption"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            // Remove this service from users
            deleteServicesFromUsers(service);
            // Remove the service itself
            final int del = service.getParent().getIndex(service);
            final int col = service.getParent().getChildCount();
            ((QServiceTree) treeServices.getModel()).removeNodeFromParent(service);
            // Remove this service tied as a shortcut
            QServiceTree.sailToStorm(((QServiceTree) treeServices.getModel()).getRoot(), (TreeNode srv) -> {
                final QService serv = (QService) srv;
                if (serv.getLink() != null && serv.getLink().getId().equals(service.getId())) {
                    serv.setLink(null);
                }
            });

            // Выделение в услуги в дереве
            if (col == 1) {
                treeServices.setSelectionPath(new TreePath(((QServiceTree) treeServices.getModel()).getPathToRoot(service.getParent())));
            } else if (col == del + 1) {
                treeServices.setSelectionPath(new TreePath(((QServiceTree) treeServices.getModel()).getPathToRoot(service.getParent().getChildAt(del - 1))));
            } else if (col > del + 1) {
                treeServices.setSelectionPath(new TreePath(((QServiceTree) treeServices.getModel()).getPathToRoot(service.getParent().getChildAt(del))));
            }
            QLog.l().logger().debug("Service deleted \"" + service.getName() + "\" from the group \"" + service.getParent().getName() + "\"");
        }
    }

    @Action
    public void addInfoItem() {
        // Запросим название инфоузла и если оно уникально и не пусто, то примем
        String infoName = getLocaleMessage("admin.add_info_dialog.info");
        boolean flag = true;
        while (flag) {
            infoName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_info_dialog.title"), getLocaleMessage("admin.add_info_dialog.caption"), 3, null, null, infoName);
            if (infoName == null) {
                return;
            }
            if ("".equals(infoName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_info_dialog.err1.title"), getLocaleMessage("admin.add_info_dialog.err1.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (infoName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_info_dialog.err2.title"), getLocaleMessage("admin.add_info_dialog.err2.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (infoName.length() > 100) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_info_dialog.err3.title"), getLocaleMessage("admin.add_info_dialog.err3.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        // Созданим новую услугу и добавим ее в модель
        final QInfoItem newItem = new QInfoItem();
        newItem.setName(infoName);
        newItem.setHTMLText("<html><b><p align=center><span style='font-size:20.0pt;color:green'>" + infoName + "</span></b>");
        newItem.setTextPrint("");
        final QInfoItem parentItem = (QInfoItem) treeInfo.getLastSelectedPathComponent();
        ((QInfoTree) treeInfo.getModel()).insertNodeInto(newItem, parentItem, parentItem.getChildCount());
        final TreeNode[] nodes = ((QInfoTree) treeInfo.getModel()).getPathToRoot(newItem);
        final TreePath path = new TreePath(nodes);
        treeInfo.scrollPathToVisible(path);
        treeInfo.setSelectionPath(path);
        textFieldInfoItemName.setEnabled(true);
        //textPaneInfoItem.setEnabled(true);
        //textPaneInfoPrint.setEnabled(true);

        QLog.l().logger().debug("Added node\"" + infoName + "\" to group \"" + parentItem.getName() + "\"");
    }

    @Action
    public void deleteInfoItem() {
        final QInfoItem item = (QInfoItem) treeInfo.getLastSelectedPathComponent();
        if (item != null && !item.isRoot()) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.remove_info_dialog.title") + " " + (item.isLeaf() ? getLocaleMessage("admin.remove_info_dialog.title_1") : getLocaleMessage("admin.remove_info_dialog.title_2")) + "\"" + (item.getName().length() > 85 ? item.getName().substring(0, 85) + " ..." : item.getName()) + "\"?",
                    getLocaleMessage("admin.remove_info_dialog.caption"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            // Удалим сам узел
            final int del = item.getParent().getIndex(item);
            final int col = item.getParent().getChildCount();
            QInfoTree.getInstance().removeNodeFromParent(item);
            // Выделение в узла в дереве
            if (col == 1) {
                treeInfo.setSelectionPath(new TreePath(((QInfoTree) treeInfo.getModel()).getPathToRoot(item.getParent())));
            } else if (col == del + 1) {
                treeInfo.setSelectionPath(new TreePath(((QInfoTree) treeInfo.getModel()).getPathToRoot(item.getParent().getChildAt(del - 1))));
            } else if (col > del + 1) {
                treeInfo.setSelectionPath(new TreePath(((QInfoTree) treeInfo.getModel()).getPathToRoot(item.getParent().getChildAt(del))));
            }
            QLog.l().logger().debug("Removed node \"" + item.getName() + "\" from group \"" + item.getParent().getName() + "\"");
        }
    }

    /*
     * @see http://static.springsource.org/spring/docs/3.0.x/reference/transaction.html#transaction-programmatic
     */
    @Action
    public void saveConfiguration() {
        saveNet();
        final Exception res;
        try {
            res = (Exception) Spring.getInstance().getTt().execute((TransactionStatus status) -> {
                try {
                    //сохраним системные настройки
                    final Collection<QProperty> col = new ArrayList<>();
                    ServerProps.getInstance().getSections().forEach(sec -> {
                        col.addAll(sec.getProperties().values());
                    });
                    Spring.getInstance().getHt().saveOrUpdateAll(col);

                    //Сохраняем сетевые настройки
                    Spring.getInstance().getHt().saveOrUpdate(ServerProps.getInstance().getProps());
                    //Сохраняем нормативные параметры
                    Spring.getInstance().getHt().saveOrUpdate(ServerProps.getInstance().getStandards());
                    // Сохраняем перерывы в расписании, тут теперь стоит, а то в календаре появились расписания.
                    QBreaksList.getInstance().save();

                    // Сохраняем планы расписания
                    QScheduleList.getInstance().save();

                    // хз что за коммент: Сохраняем календари услуг, главное раньше расписаний, не то спец расписания будут ругаться.
                    QCalendarList.getInstance().save();

                    // Сохраняем услуги
                    QServiceTree.getInstance().save();

                    // Сохраняем пользователей
                    QUserList.getInstance().save();
                    // Сохраняем инфоузлы
                    QInfoTree.getInstance().save();
                    // Сохраняем отзывы
                    QResponseTree.getInstance().save();
                    // Сохраняем результаты работы пользователя с клиентами
                    QResultList.getInstance().save();

                    QOfficeList.getInstance().save();
                } catch (Exception ex) {
                    QLog.l().logger().error("Error while saving \n" + ex.toString() + "\n" + Arrays.toString(ex.getStackTrace()));
                    status.setRollbackOnly();
                    return ex;
                }
                return null;
            });
        } catch (RuntimeException ex) {
            throw new ClientException("Error performing the operation of modifying data in the database(JDBC). Perhaps the parameters you entered can not be saved. \n(" + ex.toString() + ")");
        }
        if (res == null) {
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.save.title"), getLocaleMessage("admin.save.caption"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            throw new ClientException("Error performing the operation of modifying data in the database(JDBC). Perhaps the parameters you entered can not be saved.\n[" + res.getLocalizedMessage() + "]\n(" + res.toString() + ")\nSQL: ");
        }
    }

    @Action
    public void addServiceToUser() {
        // вот эта строчка не понятно зачем добавлена. дело в том что после сохранения вновь добавленные услуги юзеру не отображаются в списке. т.е. listUsers.getSelectedIndex() == -1
        listUserService.setModel(((QUser) listUsers.getSelectedValue()).getPlanServiceList());
        final QUser user = (QUser) listUsers.getSelectedValue();
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && service.isLeaf() && listUsers.getSelectedIndex() != -1 && !(user.hasService(service))) {
            user.addPlanService(service);
            if (listUserService.getLastVisibleIndex() != -1) {
                listUserService.setSelectedIndex(listUserService.getLastVisibleIndex());
                QLog.l().logger().debug("User \"" + user.getName() + "\"  appointed a service \"" + service.getName() + "\".");
            }
        }
        if (service != null && !service.isLeaf() && listUsers.getSelectedIndex() != -1 && !(user.hasService(service))) {
            QServiceTree.sailToStorm(service, (TreeNode service1) -> {
                if (service1.isLeaf() && !user.hasService((QService) service1)) {
                    user.addPlanService((QService) service1);
                    QLog.l().logger().debug("User \"" + ((QUser) listUsers.getSelectedValue()).getName() + "\" appointed a service \"" + ((QService) service1).getName() + "\".");
                }
            });
            if (listUserService.getLastVisibleIndex() != -1) {
                listUserService.setSelectedIndex(listUserService.getLastVisibleIndex());
            }
        }
    }

    @Action
    public void deleteServiseFromUser() {
        if (listUserService.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.remove_service_from_user.title") + " \"" + listUserService.getSelectedValue().toString() + "\" " + getLocaleMessage("admin.remove_service_from_user.title_1") + " \"" + listUsers.getSelectedValue().toString() + "\"?",
                    getLocaleMessage("admin.remove_service_from_user.caption"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            final int ind = listUserService.getSelectedIndex();
            ((QUser) listUsers.getSelectedValue()).deletePlanService(((QPlanService) listUserService.getSelectedValue()).getService().getId());
            if (listUserService.getLastVisibleIndex() != -1) {
                listUserService.setSelectedIndex(listUserService.getLastVisibleIndex() < ind ? listUserService.getLastVisibleIndex() : ind);
            }
        }
    }

    @Action
    public void getTicket() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && service.isLeaf()) {
            //Если услуга требует ввода данных пользователем, то нужно получить эти данные из диалога ввода
            String inputData = null;
            if (service.getInput_required()) {
                inputData = (String) JOptionPane.showInputDialog(this, service.getInput_caption().replaceAll("<[^>]*>", ""), "***", 3, null, null, "");
                if (inputData == null || inputData.isEmpty()) {
                    return;
                }
            }

            final QCustomer customer;
            try {
                customer = NetCommander.standInService(new ServerNetProperty(), service.getId(), "1", 1, inputData);
            } catch (Exception ex) {
                throw new ClientException(getLocaleMessage("admin.print_ticket_error") + " " + ex);
            }
            FWelcome.printTicket(customer, ((QService) treeServices.getModel().getRoot()).getName());
            String pref = customer.getPrefix();
            pref = "".equals(pref) ? "" : pref + "-";
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.print_ticket.title") + " \"" + service.getName() + "\". " + getLocaleMessage("admin.print_ticket.title_1") + " \"" + pref + customer.getNumber() + "\".", getLocaleMessage("admin.print_ticket.caption"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupSource = new javax.swing.ButtonGroup();
        popupUser = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem45 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItemEditUser = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        jMenuItem10 = new javax.swing.JMenuItem();
        popupServices = new javax.swing.JPopupMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        miCopyService = new javax.swing.JMenuItem();
        miCutService = new javax.swing.JMenuItem();
        miPasteService = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem42 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem44 = new javax.swing.JMenuItem();
        popupServiceUser = new javax.swing.JPopupMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        jMenuItem15 = new javax.swing.JMenuItem();
        popupInfo = new javax.swing.JPopupMenu();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem27 = new javax.swing.JMenuItem();
        popupResponse = new javax.swing.JPopupMenu();
        jMenuItem28 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem29 = new javax.swing.JMenuItem();
        popupResults = new javax.swing.JPopupMenu();
        jMenuItem30 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItem31 = new javax.swing.JMenuItem();
        popupPlans = new javax.swing.JPopupMenu();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItem34 = new javax.swing.JMenuItem();
        popupCalendar = new javax.swing.JPopupMenu();
        jMenuItem35 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenuItem36 = new javax.swing.JMenuItem();
        buttonGroupKindNum = new javax.swing.ButtonGroup();
        buttonGroupPoint = new javax.swing.ButtonGroup();
        buttonGroupVoice = new javax.swing.ButtonGroup();
        popupBreaks = new javax.swing.JPopupMenu();
        jMenuItem39 = new javax.swing.JMenuItem();
        jMenuItem40 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem41 = new javax.swing.JMenuItem();
        bgPager = new javax.swing.ButtonGroup();
        popupProps = new javax.swing.JPopupMenu();
        muAddProp = new javax.swing.JMenuItem();
        muDeleteProp = new javax.swing.JMenuItem();
        popupSections = new javax.swing.JPopupMenu();
        miAddSection = new javax.swing.JMenuItem();
        miDeleteSection = new javax.swing.JMenuItem();
        bgBoards = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        tabbedPaneMain = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        textFieldClientAdress = new javax.swing.JTextField();
        spinnerClientPort = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        labelWelcomeState = new javax.swing.JLabel();
        checkBoxClientAuto = new javax.swing.JCheckBox();
        buttonClientRequest = new javax.swing.JButton();
        buttonLock = new javax.swing.JButton();
        buttonUnlock = new javax.swing.JButton();
        buttonRestart = new javax.swing.JButton();
        buttonShutDown = new javax.swing.JButton();
        cbDropTicketsCnt = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textFieldServerAddr = new javax.swing.JTextField();
        spinnerServerPort = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        checkBoxServerAuto = new javax.swing.JCheckBox();
        buttonServerRequest = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        labelServerState = new javax.swing.JLabel();
        buttonRestartServer = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        listPostponed = new javax.swing.JList();
        buttonResetMainTablo = new javax.swing.JButton();
        jScrollPane20 = new javax.swing.JScrollPane();
        listBan = new javax.swing.JList();
        buttonRefreshBan = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeServices = new javax.swing.JTree();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        textFieldSearchService = new javax.swing.JTextField();
        jPanel26 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listUserService = new javax.swing.JList();
        jButton6 = new javax.swing.JButton();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel11 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        textFieldUserName = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        textFieldUserIdent = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        spinnerUserRS = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        passwordFieldUser = new javax.swing.JPasswordField();
        checkBoxReport = new javax.swing.JCheckBox();
        checkBoxAdmin = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        textFieldExtPoint = new javax.swing.JTextField();
        tfUserId = new javax.swing.JTextField();
        jPanel27 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane18 = new javax.swing.JScrollPane();
        labelServiceInfo = new javax.swing.JLabel();
        jScrollPane19 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        labelButtonCaption = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane22 = new javax.swing.JScrollPane();
        userServsList = new javax.swing.JList();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        listCalendar = new javax.swing.JList();
        jScrollPane15 = new javax.swing.JScrollPane();
        tableCalendar = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        textFieldCalendarName = new javax.swing.JTextField();
        buttonAddCalendar = new javax.swing.JButton();
        buttonDeleteCalendar = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        spinCalendarYear = new javax.swing.JSpinner();
        panelSpecSc = new javax.swing.JPanel();
        jScrollPane23 = new javax.swing.JScrollPane();
        listSpecSced = new javax.swing.JList();
        butAddSpecSced = new javax.swing.JButton();
        butEditSpecSced = new javax.swing.JButton();
        butDeleteSpecSced = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        listSchedule = new javax.swing.JList();
        jLabel21 = new javax.swing.JLabel();
        textFieldScheduleName = new javax.swing.JTextField();
        buttonScheduleAdd = new javax.swing.JButton();
        buttonSchedulleDelete = new javax.swing.JButton();
        labelSchedule = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        listBreaks = new javax.swing.JList();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        textFieldStartTime = new javax.swing.JTextField();
        textFieldFinishTime = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane7 = new javax.swing.JSplitPane();
        jPanel30 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        treeInfo = new javax.swing.JTree();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jPanel31 = new javax.swing.JPanel();
        textFieldInfoItemName = new javax.swing.JTextField();
        jSplitPane5 = new javax.swing.JSplitPane();
        jScrollPane16 = new javax.swing.JScrollPane();
        textPaneInfoPrint = new javax.swing.JTextPane();
        jPanel29 = new javax.swing.JPanel();
        butWysInfo1 = new javax.swing.JButton();
        jSplitPane6 = new javax.swing.JSplitPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        textPaneInfoItem = new javax.swing.JTextPane();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane17 = new javax.swing.JScrollPane();
        labelInfoItem = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jSplitPane8 = new javax.swing.JSplitPane();
        jPanel32 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane25 = new javax.swing.JScrollPane();
        treeResp = new javax.swing.JTree();
        jSplitPane9 = new javax.swing.JSplitPane();
        jPanel15 = new javax.swing.JPanel();
        labelRespinse = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        textFieldResponse = new javax.swing.JTextField();
        btnWysResp1 = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        textPaneResponse = new javax.swing.JTextPane();
        cbCommentForResp = new javax.swing.JCheckBox();
        tfHeaderCmtResp = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        tfRespID = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        spinnerWaitMax = new javax.swing.JSpinner();
        jLabel27 = new javax.swing.JLabel();
        spinnerWorkMax = new javax.swing.JSpinner();
        jLabel28 = new javax.swing.JLabel();
        spinnerDowntimeNax = new javax.swing.JSpinner();
        jLabel29 = new javax.swing.JLabel();
        spinnerLineServiceMax = new javax.swing.JSpinner();
        jLabel30 = new javax.swing.JLabel();
        spinnerLineTotalMax = new javax.swing.JSpinner();
        spinnerRelocation = new javax.swing.JSpinner();
        jLabel35 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spinnerBlackListTimeMin = new javax.swing.JSpinner();
        spinnerRemoveRecall = new javax.swing.JSpinner();
        jSplitPane4 = new javax.swing.JSplitPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        listReposts = new javax.swing.JList();
        jScrollPane13 = new javax.swing.JScrollPane();
        listResults = new javax.swing.JList();
        jPanel12 = new javax.swing.JPanel();
        dateChooserStartCsv = new com.toedter.calendar.JDateChooser();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        dateChooserFinishCsv = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        cbSeparateCSV = new javax.swing.JComboBox();
        buttonExportToCSV = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        spinnerPropServerPort = new javax.swing.JSpinner();
        spinnerWebServerPort = new javax.swing.JSpinner();
        spinnerPropClientPort = new javax.swing.JSpinner();
        jPanel16 = new javax.swing.JPanel();
        spinnerFirstNumber = new javax.swing.JSpinner();
        spinnerLastNumber = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        rbKindPersonal = new javax.swing.JRadioButton();
        rbKindCommon = new javax.swing.JRadioButton();
        chBoxBtnFreeDsn = new javax.swing.JCheckBox();
        labExtPrior = new javax.swing.JLabel();
        spinExtPrior = new javax.swing.JSpinner();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel23 = new javax.swing.JPanel();
        textFieldURLWebService = new javax.swing.JTextField();
        spinnerBranchId = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        buttonCloudTest = new javax.swing.JButton();
        buttonSendDataToSky = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        textFieldZonBoadrServAddr = new javax.swing.JTextField();
        spinnerZonBoadrServPort = new javax.swing.JSpinner();
        buttonCheckZoneBoardServ = new javax.swing.JButton();
        propsPanel = new javax.swing.JPanel();
        jSplitPane10 = new javax.swing.JSplitPane();
        sectionPanel = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        sectionsList = new javax.swing.JList<>();
        btnDeleteSection = new javax.swing.JButton();
        btnAddSection = new javax.swing.JButton();
        keyvaluePanel = new javax.swing.JPanel();
        jScrollPane26 = new javax.swing.JScrollPane();
        propsTable = new javax.swing.JTable();
        btnRemoveProp = new javax.swing.JButton();
        btnAddProp = new javax.swing.JButton();
        btnReloadProps = new javax.swing.JButton();
        tabHide = new javax.swing.JPanel();
        jScrollPane24 = new javax.swing.JScrollPane();
        labHidePic = new javax.swing.JLabel();
        panelPager = new javax.swing.JPanel();
        labelPager = new javax.swing.JLabel();
        panelPagerRadio = new javax.swing.JPanel();
        rbPager1 = new javax.swing.JRadioButton();
        rbPager2 = new javax.swing.JRadioButton();
        rbPager3 = new javax.swing.JRadioButton();
        panelPagerCombo = new javax.swing.JPanel();
        labelPagerCaptionCombo = new javax.swing.JLabel();
        comboBoxPager = new javax.swing.JComboBox();
        panelEditPager = new javax.swing.JPanel();
        labelPagerCaptionEdit = new javax.swing.JLabel();
        textFieldPager = new javax.swing.JTextField();
        buttonPagerEdit = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuLangs = new javax.swing.JMenu();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        menuBoards = new javax.swing.JMenu();
        rbmClassic = new javax.swing.JRadioButtonMenuItem();
        rbmHtml = new javax.swing.JRadioButtonMenuItem();
        menuUsers = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenuItem37 = new javax.swing.JMenuItem();
        jMenuItem43 = new javax.swing.JMenuItem();
        menuServices = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        menuAbout = new javax.swing.JMenu();
        menuItemHelp = new javax.swing.JMenuItem();
        menuItemAbout = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        jMenuItemBagtracker = new javax.swing.JMenuItem();
        jMenuItemForum = new javax.swing.JMenuItem();

        popupUser.setName("popupUser"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FAdmin.class, this);
        jMenuItem1.setAction(actionMap.get("addUser")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        popupUser.add(jMenuItem1);

        jMenuItem45.setAction(actionMap.get("addNewUserByCopy")); // NOI18N
        jMenuItem45.setName("jMenuItem45"); // NOI18N
        popupUser.add(jMenuItem45);

        jMenuItem20.setAction(actionMap.get("renameUser")); // NOI18N
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        popupUser.add(jMenuItem20);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FAdmin.class);
        jMenuItemEditUser.setText(resourceMap.getString("jMenuItemEditUser.text")); // NOI18N
        jMenuItemEditUser.setName("jMenuItemEditUser"); // NOI18N
        jMenuItemEditUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditUserActionPerformed(evt);
            }
        });
        popupUser.add(jMenuItemEditUser);

        jSeparator7.setName("jSeparator7"); // NOI18N
        popupUser.add(jSeparator7);

        jMenuItem10.setAction(actionMap.get("deleteUser")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        popupUser.add(jMenuItem10);

        popupServices.setComponentPopupMenu(popupServices);
        popupServices.setName("popupServices"); // NOI18N
        popupServices.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupServicesPopupMenuWillBecomeVisible(evt);
            }
        });

        jMenuItem11.setAction(actionMap.get("addService")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        popupServices.add(jMenuItem11);

        jMenuItem21.setAction(actionMap.get("renameService")); // NOI18N
        jMenuItem21.setName("jMenuItem21"); // NOI18N
        popupServices.add(jMenuItem21);

        miCopyService.setText(resourceMap.getString("miCopyService.text")); // NOI18N
        miCopyService.setName("miCopyService"); // NOI18N
        miCopyService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCopyServiceActionPerformed(evt);
            }
        });
        popupServices.add(miCopyService);

        miCutService.setText(resourceMap.getString("miCutService.text")); // NOI18N
        miCutService.setName("miCutService"); // NOI18N
        miCutService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCutServiceActionPerformed(evt);
            }
        });
        popupServices.add(miCutService);

        miPasteService.setText(resourceMap.getString("miPasteService.text")); // NOI18N
        miPasteService.setName("miPasteService"); // NOI18N
        miPasteService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miPasteServiceActionPerformed(evt);
            }
        });
        popupServices.add(miPasteService);

        jMenuItem13.setAction(actionMap.get("deleteService")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        popupServices.add(jMenuItem13);

        jSeparator8.setName("jSeparator8"); // NOI18N
        popupServices.add(jSeparator8);

        jMenuItem22.setAction(actionMap.get("editService")); // NOI18N
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        popupServices.add(jMenuItem22);

        jMenuItem42.setAction(actionMap.get("editLangs")); // NOI18N
        jMenuItem42.setName("jMenuItem42"); // NOI18N
        popupServices.add(jMenuItem42);

        jMenuItem12.setAction(actionMap.get("addServiceToUser")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        popupServices.add(jMenuItem12);

        jSeparator5.setName("jSeparator5"); // NOI18N
        popupServices.add(jSeparator5);

        jMenuItem16.setAction(actionMap.get("getTicket")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        popupServices.add(jMenuItem16);

        jMenuItem24.setAction(actionMap.get("standAdvance")); // NOI18N
        jMenuItem24.setName("jMenuItem24"); // NOI18N
        popupServices.add(jMenuItem24);

        jMenuItem44.setAction(actionMap.get("setDisableService")); // NOI18N
        jMenuItem44.setName("jMenuItem44"); // NOI18N
        popupServices.add(jMenuItem44);

        popupServiceUser.setName("popupServiceUser"); // NOI18N

        jMenuItem14.setAction(actionMap.get("changeServicePriority")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        popupServiceUser.add(jMenuItem14);

        jMenuItem38.setAction(actionMap.get("changeFlexiblePriorityAbility")); // NOI18N
        jMenuItem38.setName("jMenuItem38"); // NOI18N
        popupServiceUser.add(jMenuItem38);

        jMenuItem17.setAction(actionMap.get("setUpdateServiceFire")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        popupServiceUser.add(jMenuItem17);

        jMenuItem18.setAction(actionMap.get("deleteUpdateServiceFire")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        popupServiceUser.add(jMenuItem18);

        jSeparator6.setName("jSeparator6"); // NOI18N
        popupServiceUser.add(jSeparator6);

        jMenuItem15.setAction(actionMap.get("deleteServiseFromUser")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        popupServiceUser.add(jMenuItem15);

        popupInfo.setName("popupInfo"); // NOI18N

        jMenuItem26.setAction(actionMap.get("addInfoItem")); // NOI18N
        jMenuItem26.setName("jMenuItem26"); // NOI18N
        popupInfo.add(jMenuItem26);

        jSeparator9.setName("jSeparator9"); // NOI18N
        popupInfo.add(jSeparator9);

        jMenuItem27.setAction(actionMap.get("deleteInfoItem")); // NOI18N
        jMenuItem27.setName("jMenuItem27"); // NOI18N
        popupInfo.add(jMenuItem27);

        popupResponse.setName("popupResponse"); // NOI18N

        jMenuItem28.setAction(actionMap.get("addRespItem")); // NOI18N
        jMenuItem28.setName("jMenuItem28"); // NOI18N
        popupResponse.add(jMenuItem28);

        jSeparator10.setName("jSeparator10"); // NOI18N
        popupResponse.add(jSeparator10);

        jMenuItem29.setAction(actionMap.get("deleteRespItem")); // NOI18N
        jMenuItem29.setName("jMenuItem29"); // NOI18N
        popupResponse.add(jMenuItem29);

        popupResults.setName("popupResults"); // NOI18N

        jMenuItem30.setAction(actionMap.get("addResult")); // NOI18N
        jMenuItem30.setName("jMenuItem30"); // NOI18N
        popupResults.add(jMenuItem30);

        jSeparator11.setName("jSeparator11"); // NOI18N
        popupResults.add(jSeparator11);

        jMenuItem31.setAction(actionMap.get("deleteResult")); // NOI18N
        jMenuItem31.setName("jMenuItem31"); // NOI18N
        popupResults.add(jMenuItem31);

        popupPlans.setName("popupPlans"); // NOI18N

        jMenuItem32.setAction(actionMap.get("addSchedule")); // NOI18N
        jMenuItem32.setName("jMenuItem32"); // NOI18N
        popupPlans.add(jMenuItem32);

        jMenuItem33.setAction(actionMap.get("editSchedule")); // NOI18N
        jMenuItem33.setName("jMenuItem33"); // NOI18N
        popupPlans.add(jMenuItem33);

        jSeparator12.setName("jSeparator12"); // NOI18N
        popupPlans.add(jSeparator12);

        jMenuItem34.setAction(actionMap.get("deleteSchedule")); // NOI18N
        jMenuItem34.setName("jMenuItem34"); // NOI18N
        popupPlans.add(jMenuItem34);

        popupCalendar.setName("popupCalendar"); // NOI18N

        jMenuItem35.setAction(actionMap.get("addCalendar")); // NOI18N
        jMenuItem35.setName("jMenuItem35"); // NOI18N
        popupCalendar.add(jMenuItem35);

        jSeparator13.setName("jSeparator13"); // NOI18N
        popupCalendar.add(jSeparator13);

        jMenuItem36.setAction(actionMap.get("deleteCalendar")); // NOI18N
        jMenuItem36.setName("jMenuItem36"); // NOI18N
        popupCalendar.add(jMenuItem36);

        popupBreaks.setName("popupBreaks"); // NOI18N

        jMenuItem39.setAction(actionMap.get("addBreakToList")); // NOI18N
        jMenuItem39.setName("jMenuItem39"); // NOI18N
        popupBreaks.add(jMenuItem39);

        jMenuItem40.setAction(actionMap.get("editBreak")); // NOI18N
        jMenuItem40.setName("jMenuItem40"); // NOI18N
        popupBreaks.add(jMenuItem40);

        jSeparator16.setName("jSeparator16"); // NOI18N
        popupBreaks.add(jSeparator16);

        jMenuItem41.setAction(actionMap.get("deleteBreakFromList")); // NOI18N
        jMenuItem41.setName("jMenuItem41"); // NOI18N
        popupBreaks.add(jMenuItem41);

        popupProps.setName("popupProps"); // NOI18N

        muAddProp.setText(resourceMap.getString("muAddProp.text")); // NOI18N
        muAddProp.setName("muAddProp"); // NOI18N
        muAddProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muAddPropActionPerformed(evt);
            }
        });
        popupProps.add(muAddProp);

        muDeleteProp.setText(resourceMap.getString("muDeleteProp.text")); // NOI18N
        muDeleteProp.setName("muDeleteProp"); // NOI18N
        muDeleteProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muDeletePropActionPerformed(evt);
            }
        });
        popupProps.add(muDeleteProp);

        popupSections.setName("popupSections"); // NOI18N

        miAddSection.setText(resourceMap.getString("miAddSection.text")); // NOI18N
        miAddSection.setName("miAddSection"); // NOI18N
        miAddSection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddSectionActionPerformed(evt);
            }
        });
        popupSections.add(miAddSection);

        miDeleteSection.setText(resourceMap.getString("miDeleteSection.text")); // NOI18N
        miDeleteSection.setName("miDeleteSection"); // NOI18N
        miDeleteSection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miDeleteSectionActionPerformed(evt);
            }
        });
        popupSections.add(miDeleteSection);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jPanel1.setBackground(resourceMap.getColor("jPanel1.background")); // NOI18N
        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        tabbedPaneMain.setName("tabbedPaneMain"); // NOI18N
        tabbedPaneMain.setPreferredSize(new java.awt.Dimension(1050, 550));
        tabbedPaneMain.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneMainStateChanged(evt);
            }
        });
        tabbedPaneMain.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tabbedPaneMainFocusLost(evt);
            }
        });

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel3.setAutoscrolls(true);
        jPanel3.setName("jPanel3"); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        textFieldClientAdress.setText(resourceMap.getString("textFieldClientAdress.text")); // NOI18N
        textFieldClientAdress.setName("textFieldClientAdress"); // NOI18N
        textFieldClientAdress.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldClientAdressFocusLost(evt);
            }
        });

        spinnerClientPort.setName("spinnerClientPort"); // NOI18N
        spinnerClientPort.setValue(3128);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        labelWelcomeState.setText(resourceMap.getString("labelWelcomeState.text")); // NOI18N
        labelWelcomeState.setName("labelWelcomeState"); // NOI18N

        checkBoxClientAuto.setText(resourceMap.getString("checkBoxClientAuto.text")); // NOI18N
        checkBoxClientAuto.setName("checkBoxClientAuto"); // NOI18N
        checkBoxClientAuto.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkBoxClientAutoStateChanged(evt);
            }
        });
        checkBoxClientAuto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                checkBoxClientAutoFocusLost(evt);
            }
        });

        buttonClientRequest.setText(resourceMap.getString("buttonClientRequest.text")); // NOI18N
        buttonClientRequest.setName("buttonClientRequest"); // NOI18N
        buttonClientRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClientRequestActionPerformed(evt);
            }
        });

        buttonLock.setText(resourceMap.getString("buttonLock.text")); // NOI18N
        buttonLock.setName("buttonLock"); // NOI18N
        buttonLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLockActionPerformed(evt);
            }
        });

        buttonUnlock.setText(resourceMap.getString("buttonUnlock.text")); // NOI18N
        buttonUnlock.setName("buttonUnlock"); // NOI18N
        buttonUnlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUnlockActionPerformed(evt);
            }
        });

        buttonRestart.setText(resourceMap.getString("buttonRestart.text")); // NOI18N
        buttonRestart.setName("buttonRestart"); // NOI18N
        buttonRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRestartActionPerformed(evt);
            }
        });

        buttonShutDown.setText(resourceMap.getString("buttonShutDown.text")); // NOI18N
        buttonShutDown.setName("buttonShutDown"); // NOI18N
        buttonShutDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShutDownActionPerformed(evt);
            }
        });

        cbDropTicketsCnt.setText(resourceMap.getString("cbDropTicketsCnt.text")); // NOI18N
        cbDropTicketsCnt.setName("cbDropTicketsCnt"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(spinnerClientPort)
                                    .addComponent(textFieldClientAdress, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(checkBoxClientAuto)
                            .addComponent(buttonClientRequest))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(buttonUnlock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonRestart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonShutDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonLock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbDropTicketsCnt)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(labelWelcomeState, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE))
                .addGap(119, 119, 119))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(textFieldClientAdress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinnerClientPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBoxClientAuto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonClientRequest))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(buttonLock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUnlock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRestart)
                            .addComponent(cbDropTicketsCnt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonShutDown)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelWelcomeState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel6.border.title"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        textFieldServerAddr.setText(resourceMap.getString("textFieldServerAddr.text")); // NOI18N
        textFieldServerAddr.setName("textFieldServerAddr"); // NOI18N
        textFieldServerAddr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldServerAddrFocusLost(evt);
            }
        });

        spinnerServerPort.setName("spinnerServerPort"); // NOI18N
        spinnerServerPort.setValue(3128);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        checkBoxServerAuto.setText(resourceMap.getString("checkBoxServerAuto.text")); // NOI18N
        checkBoxServerAuto.setName("checkBoxServerAuto"); // NOI18N
        checkBoxServerAuto.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkBoxServerAutoStateChanged(evt);
            }
        });
        checkBoxServerAuto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                checkBoxServerAutoFocusLost(evt);
            }
        });

        buttonServerRequest.setText(resourceMap.getString("buttonServerRequest.text")); // NOI18N
        buttonServerRequest.setName("buttonServerRequest"); // NOI18N
        buttonServerRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonServerRequestActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("server_info"))); // NOI18N
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        labelServerState.setText(resourceMap.getString("labelServerState.text")); // NOI18N
        labelServerState.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelServerState.setName("labelServerState"); // NOI18N
        jScrollPane2.setViewportView(labelServerState);

        buttonRestartServer.setText(resourceMap.getString("buttonRestartServer.text")); // NOI18N
        buttonRestartServer.setName("buttonRestartServer"); // NOI18N
        buttonRestartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRestartServerActionPerformed(evt);
            }
        });

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        listPostponed.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("listPostponed.border.title"))); // NOI18N
        listPostponed.setName("listPostponed"); // NOI18N
        jScrollPane5.setViewportView(listPostponed);

        buttonResetMainTablo.setText(resourceMap.getString("buttonResetMainTablo.text")); // NOI18N
        buttonResetMainTablo.setName("buttonResetMainTablo"); // NOI18N
        buttonResetMainTablo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonResetMainTabloActionPerformed(evt);
            }
        });

        jScrollPane20.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPane20.border.title"))); // NOI18N
        jScrollPane20.setName("jScrollPane20"); // NOI18N

        listBan.setName("listBan"); // NOI18N
        jScrollPane20.setViewportView(listBan);

        buttonRefreshBan.setText(resourceMap.getString("buttonRefreshBan.text")); // NOI18N
        buttonRefreshBan.setName("buttonRefreshBan"); // NOI18N
        buttonRefreshBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshBanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldServerAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(spinnerServerPort)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxServerAuto)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(buttonServerRequest)
                                .addGap(18, 18, 18)
                                .addComponent(buttonRestartServer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonResetMainTablo))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buttonRefreshBan)
                    .addComponent(jScrollPane20, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textFieldServerAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkBoxServerAuto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spinnerServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonServerRequest)
                    .addComponent(buttonRestartServer)
                    .addComponent(buttonResetMainTablo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonRefreshBan))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel4.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.setAutoscrolls(true);
        jPanel4.setName("jPanel4"); // NOI18N

        jSplitPane1.setDividerLocation(380);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jSplitPane2.setDividerLocation(210);
        jSplitPane2.setContinuousLayout(true);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jPanel25.setName("jPanel25"); // NOI18N

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPane3.border.title"))); // NOI18N
        jScrollPane3.setName("jScrollPane3"); // NOI18N

        treeServices.setModel(null);
        treeServices.setAutoscrolls(true);
        treeServices.setComponentPopupMenu(popupServices);
        treeServices.setDragEnabled(true);
        treeServices.setDropMode(javax.swing.DropMode.ON);
        treeServices.setName("treeServices"); // NOI18N
        treeServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeServicesMouseClicked(evt);
            }
        });
        treeServices.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                treeServicesKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(treeServices);

        jButton5.setAction(actionMap.get("addServiceToUser")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N

        jButton3.setAction(actionMap.get("deleteService")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        jButton4.setAction(actionMap.get("addService")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        textFieldSearchService.setText(resourceMap.getString("textFieldSearchService.text")); // NOI18N
        textFieldSearchService.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textFieldSearchService.border.title"))); // NOI18N
        textFieldSearchService.setName("textFieldSearchService"); // NOI18N
        textFieldSearchService.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchServiceKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3))
            .addComponent(textFieldSearchService, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addComponent(textFieldSearchService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton3)
                    .addComponent(jButton4)))
        );

        jSplitPane2.setRightComponent(jPanel25);

        jPanel26.setName("jPanel26"); // NOI18N

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPane4.border.title"))); // NOI18N
        jScrollPane4.setName("jScrollPane4"); // NOI18N

        listUserService.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listUserService.setComponentPopupMenu(popupServiceUser);
        listUserService.setName("listUserService"); // NOI18N
        listUserService.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listUserServiceMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(listUserService);

        jButton6.setAction(actionMap.get("deleteServiseFromUser")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton6)
                .addGap(10, 10, 10))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6))
        );

        jSplitPane2.setLeftComponent(jPanel26);

        jSplitPane1.setRightComponent(jSplitPane2);

        jSplitPane3.setDividerLocation(170);
        jSplitPane3.setDividerSize(0);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setName("jSplitPane3"); // NOI18N
        jSplitPane3.setPreferredSize(new java.awt.Dimension(40, 25));

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel11.border.title"))); // NOI18N
        jPanel11.setMinimumSize(new java.awt.Dimension(5, 5));
        jPanel11.setName("jPanel11"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        textFieldUserName.setEditable(false);
        textFieldUserName.setText(resourceMap.getString("textFieldUserName.text")); // NOI18N
        textFieldUserName.setName("textFieldUserName"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        textFieldUserIdent.setText(resourceMap.getString("textFieldUserIdent.text")); // NOI18N
        textFieldUserIdent.setName("textFieldUserIdent"); // NOI18N
        textFieldUserIdent.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldUserIdentKeyReleased(evt);
            }
        });

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        spinnerUserRS.setEditor(new javax.swing.JSpinner.NumberEditor(spinnerUserRS, ""));
        spinnerUserRS.setName("spinnerUserRS"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        passwordFieldUser.setText(resourceMap.getString("passwordFieldUser.text")); // NOI18N
        passwordFieldUser.setName("passwordFieldUser"); // NOI18N
        passwordFieldUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passwordFieldUserKeyReleased(evt);
            }
        });

        checkBoxReport.setText(resourceMap.getString("checkBoxReport.text")); // NOI18N
        checkBoxReport.setName("checkBoxReport"); // NOI18N
        checkBoxReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBoxReportMouseClicked(evt);
            }
        });

        checkBoxAdmin.setText(resourceMap.getString("checkBoxAdmin.text")); // NOI18N
        checkBoxAdmin.setName("checkBoxAdmin"); // NOI18N
        checkBoxAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkBoxAdminMouseClicked(evt);
            }
        });

        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        textFieldExtPoint.setText(resourceMap.getString("textFieldExtPoint.text")); // NOI18N
        textFieldExtPoint.setName("textFieldExtPoint"); // NOI18N
        textFieldExtPoint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExtPointKeyReleased(evt);
            }
        });

        tfUserId.setEditable(false);
        tfUserId.setFont(resourceMap.getFont("tfUserId.font")); // NOI18N
        tfUserId.setText(resourceMap.getString("tfUserId.text")); // NOI18N
        tfUserId.setName("tfUserId"); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                        .addGap(86, 86, 86))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(textFieldUserIdent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                            .addComponent(spinnerUserRS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                        .addGap(72, 72, 72))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(passwordFieldUser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                            .addComponent(textFieldUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfUserId))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkBoxAdmin)
                                    .addComponent(checkBoxReport)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel18))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldExtPoint, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(tfUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldUserIdent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerUserRS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldExtPoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(3, Short.MAX_VALUE))
        );

        jSplitPane3.setLeftComponent(jPanel11);

        jUserOfficePane = new javax.swing.JSplitPane();
        popupOffice = new javax.swing.JPopupMenu();
        jOfficePanel = new javax.swing.JPanel();
        listOffices = new javax.swing.JList();
        jOfficeScrollPane = new javax.swing.JScrollPane();
        jAddOfficeButton = new javax.swing.JButton();
        jDeleteOfficeButton = new javax.swing.JButton();

        jOfficePanel.setName("jOfficePanel"); // NOI18N

        jOfficeScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanelOffices.scrollPane.title"))); // NOI18N
        jOfficeScrollPane.setName("jScrollPane1"); // NOI18N

        listOffices.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listOffices.setComponentPopupMenu(popupOffice);
        listOffices.setName("listOffices"); // NOI18N
        jOfficeScrollPane.setViewportView(listOffices);

        jAddOfficeButton.setAction(actionMap.get("addOffice")); // NOI18N
        jAddOfficeButton.setName("jAddOfficeButton"); // NOI18N
        jAddOfficeButton.setText(resourceMap.getString("jAddOfficeButton.text"));

        jDeleteOfficeButton.setAction(actionMap.get("deleteOffice")); // NOI18N
        jDeleteOfficeButton.setName("jDeleteOfficeButton"); // NOI18N
        jDeleteOfficeButton.setText(resourceMap.getString("jDeleteOfficeButton.text"));

        javax.swing.GroupLayout jOfficePanelLayout = new javax.swing.GroupLayout(jOfficePanel);
        jOfficePanel.setLayout(jOfficePanelLayout);
        jOfficePanelLayout.setHorizontalGroup(
                jOfficePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jOfficePanelLayout.createSequentialGroup()
                                .addComponent(jAddOfficeButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                                .addComponent(jDeleteOfficeButton))
                        .addComponent(jOfficeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
        );
        jOfficePanelLayout.setVerticalGroup(
                jOfficePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jOfficePanelLayout.createSequentialGroup()
                                .addComponent(jOfficeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jOfficePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jAddOfficeButton)
                                        .addComponent(jDeleteOfficeButton)))
        );

        jPanel27.setName("jPanel27"); // NOI18N

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPane1.border.title"))); // NOI18N
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listUsers.setComponentPopupMenu(popupUser);
        listUsers.setName("listUsers"); // NOI18N
        jScrollPane1.setViewportView(listUsers);

        jButton1.setAction(actionMap.get("addUser")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(actionMap.get("deleteUser")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jButton2))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)))
        );

        jUserOfficePane.setLeftComponent(jOfficePanel);
        jUserOfficePane.setRightComponent(jPanel27);

        jSplitPane3.setRightComponent(jUserOfficePane);
        jSplitPane1.setLeftComponent(jSplitPane3);

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jScrollPane18.setName("jScrollPane18"); // NOI18N

        labelServiceInfo.setText(resourceMap.getString("labelServiceInfo.text")); // NOI18N
        labelServiceInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelServiceInfo.setName("labelServiceInfo"); // NOI18N
        jScrollPane18.setViewportView(labelServiceInfo);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane18.TabConstraints.tabTitle"), jScrollPane18); // NOI18N

        jScrollPane19.setName("jScrollPane19"); // NOI18N

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPane6.border.title"))); // NOI18N
        jScrollPane6.setName("jScrollPane6"); // NOI18N

        labelButtonCaption.setText(resourceMap.getString("labelButtonCaption.text")); // NOI18N
        labelButtonCaption.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelButtonCaption.setName("labelButtonCaption"); // NOI18N
        jScrollPane6.setViewportView(labelButtonCaption);

        jScrollPane19.setViewportView(jScrollPane6);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane19.TabConstraints.tabTitle"), jScrollPane19); // NOI18N

        jPanel28.setName("jPanel28"); // NOI18N

        jScrollPane22.setName("jScrollPane22"); // NOI18N

        userServsList.setName("userServsList"); // NOI18N
        jScrollPane22.setViewportView(userServsList);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE)
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel28.TabConstraints.tabTitle"), jPanel28); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        tabbedPaneMain.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel19.setAutoscrolls(true);
        jPanel19.setName("jPanel19"); // NOI18N

        jScrollPane14.setName("jScrollPane14"); // NOI18N

        listCalendar.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Общий календарь", " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listCalendar.setComponentPopupMenu(popupCalendar);
        listCalendar.setName("listCalendar"); // NOI18N
        jScrollPane14.setViewportView(listCalendar);

        jScrollPane15.setName("jScrollPane15"); // NOI18N

        tableCalendar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Январь", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Февраль", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Март", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Апрель", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Май", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Июнь", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Июль", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Август", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Сентябрь", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Октябрь", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Ноябрь", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {"Декабрь", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                " ", " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8", " 9", " 10", " 11", " 12", " 13", " 14", " 15", " 16", " 17", " 18", " 19", " 20", " 21", " 22", " 23", " 24", " 25", " 26", " 27", " 28", " 29", " 30", "31"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableCalendar.setCellSelectionEnabled(true);
        tableCalendar.setName("tableCalendar"); // NOI18N
        tableCalendar.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tableCalendar.getTableHeader().setReorderingAllowed(false);
        tableCalendar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tableCalendarFocusGained(evt);
            }
        });
        jScrollPane15.setViewportView(tableCalendar);

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        textFieldCalendarName.setText(resourceMap.getString("textFieldCalendarName.text")); // NOI18N
        textFieldCalendarName.setName("textFieldCalendarName"); // NOI18N
        textFieldCalendarName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldCalendarNameKeyReleased(evt);
            }
        });

        buttonAddCalendar.setAction(actionMap.get("addCalendar")); // NOI18N
        buttonAddCalendar.setText(resourceMap.getString("buttonAddCalendar.text")); // NOI18N
        buttonAddCalendar.setName("buttonAddCalendar"); // NOI18N

        buttonDeleteCalendar.setAction(actionMap.get("deleteCalendar")); // NOI18N
        buttonDeleteCalendar.setText(resourceMap.getString("buttonDeleteCalendar.text")); // NOI18N
        buttonDeleteCalendar.setName("buttonDeleteCalendar"); // NOI18N

        jButton18.setAction(actionMap.get("checkSundays")); // NOI18N
        jButton18.setName("jButton18"); // NOI18N

        jButton16.setAction(actionMap.get("checkSaturday")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N

        jButton17.setAction(actionMap.get("dropCalendarSelection")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N

        jButton15.setAction(actionMap.get("saveCalendar")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setName("jButton15"); // NOI18N

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N

        spinCalendarYear.setModel(new javax.swing.SpinnerNumberModel(2015, 2014, 2050, 1));
        spinCalendarYear.setEditor(new javax.swing.JSpinner.NumberEditor(spinCalendarYear, ""));
        spinCalendarYear.setFocusable(false);
        spinCalendarYear.setName("spinCalendarYear"); // NOI18N
        spinCalendarYear.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinCalendarYearStateChanged(evt);
            }
        });

        panelSpecSc.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelSpecSc.border.title"))); // NOI18N
        panelSpecSc.setName("panelSpecSc"); // NOI18N

        jScrollPane23.setName("jScrollPane23"); // NOI18N

        listSpecSced.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listSpecSced.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSpecSced.setName("listSpecSced"); // NOI18N
        listSpecSced.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSpecScedMouseClicked(evt);
            }
        });
        jScrollPane23.setViewportView(listSpecSced);

        butAddSpecSced.setText(resourceMap.getString("butAddSpecSced.text")); // NOI18N
        butAddSpecSced.setName("butAddSpecSced"); // NOI18N
        butAddSpecSced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAddSpecScedActionPerformed(evt);
            }
        });

        butEditSpecSced.setText(resourceMap.getString("butEditSpecSced.text")); // NOI18N
        butEditSpecSced.setName("butEditSpecSced"); // NOI18N
        butEditSpecSced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butEditSpecScedActionPerformed(evt);
            }
        });

        butDeleteSpecSced.setText(resourceMap.getString("butDeleteSpecSced.text")); // NOI18N
        butDeleteSpecSced.setName("butDeleteSpecSced"); // NOI18N
        butDeleteSpecSced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteSpecScedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSpecScLayout = new javax.swing.GroupLayout(panelSpecSc);
        panelSpecSc.setLayout(panelSpecScLayout);
        panelSpecScLayout.setHorizontalGroup(
            panelSpecScLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSpecScLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(butDeleteSpecSced)
                .addGap(18, 18, 18)
                .addComponent(butEditSpecSced)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butAddSpecSced)
                .addContainerGap())
            .addComponent(jScrollPane23)
        );
        panelSpecScLayout.setVerticalGroup(
            panelSpecScLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSpecScLayout.createSequentialGroup()
                .addComponent(jScrollPane23, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelSpecScLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butAddSpecSced)
                    .addComponent(butEditSpecSced)
                    .addComponent(butDeleteSpecSced)))
        );

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(buttonAddCalendar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDeleteCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldCalendarName, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinCalendarYear, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jButton18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton16)
                                .addGap(18, 18, 18)
                                .addComponent(jButton17)
                                .addGap(18, 18, 18)
                                .addComponent(jButton15)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(panelSpecSc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(textFieldCalendarName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinCalendarYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton18)
                            .addComponent(jButton16)
                            .addComponent(jButton17)
                            .addComponent(jButton15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSpecSc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAddCalendar)
                    .addComponent(buttonDeleteCalendar))
                .addContainerGap())
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel19.TabConstraints.tabTitle"), jPanel19); // NOI18N

        jPanel17.setAutoscrolls(true);
        jPanel17.setName("jPanel17"); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        listSchedule.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("listSchedule.border.title"))); // NOI18N
        listSchedule.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listSchedule.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSchedule.setComponentPopupMenu(popupPlans);
        listSchedule.setName("listSchedule"); // NOI18N
        listSchedule.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listScheduleMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(listSchedule);
        listSchedule.getAccessibleContext().setAccessibleName(resourceMap.getString("jList1.AccessibleContext.accessibleName")); // NOI18N

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        textFieldScheduleName.setEditable(false);
        textFieldScheduleName.setText(resourceMap.getString("textFieldScheduleName.text")); // NOI18N
        textFieldScheduleName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        textFieldScheduleName.setFocusable(false);
        textFieldScheduleName.setName("textFieldScheduleName"); // NOI18N
        textFieldScheduleName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldScheduleNameKeyReleased(evt);
            }
        });

        buttonScheduleAdd.setAction(actionMap.get("addSchedule")); // NOI18N
        buttonScheduleAdd.setText(resourceMap.getString("buttonScheduleAdd.text")); // NOI18N
        buttonScheduleAdd.setName("buttonScheduleAdd"); // NOI18N

        buttonSchedulleDelete.setAction(actionMap.get("deleteSchedule")); // NOI18N
        buttonSchedulleDelete.setText(resourceMap.getString("buttonSchedulleDelete.text")); // NOI18N
        buttonSchedulleDelete.setName("buttonSchedulleDelete"); // NOI18N

        labelSchedule.setText(resourceMap.getString("labelSchedule.text")); // NOI18N
        labelSchedule.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelSchedule.setName("labelSchedule"); // NOI18N

        jScrollPane21.setName("jScrollPane21"); // NOI18N

        listBreaks.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("listBreaks.border.title"))); // NOI18N
        listBreaks.setComponentPopupMenu(popupBreaks);
        listBreaks.setName("listBreaks"); // NOI18N
        listBreaks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listBreaksMouseClicked(evt);
            }
        });
        jScrollPane21.setViewportView(listBreaks);

        jButton13.setAction(actionMap.get("editSchedule")); // NOI18N
        jButton13.setName("jButton13"); // NOI18N

        jButton14.setAction(actionMap.get("addBreakToList")); // NOI18N
        jButton14.setName("jButton14"); // NOI18N

        jButton19.setAction(actionMap.get("deleteBreakFromList")); // NOI18N
        jButton19.setName("jButton19"); // NOI18N

        jButton20.setAction(actionMap.get("editBreak")); // NOI18N
        jButton20.setName("jButton20"); // NOI18N

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel10.border.title"))); // NOI18N
        jPanel10.setName("jPanel10"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        textFieldStartTime.setText(resourceMap.getString("textFieldStartTime.text")); // NOI18N
        textFieldStartTime.setName("textFieldStartTime"); // NOI18N

        textFieldFinishTime.setText(resourceMap.getString("textFieldFinishTime.text")); // NOI18N
        textFieldFinishTime.setName("textFieldFinishTime"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel14))
                .addGap(30, 30, 30)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textFieldStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFieldFinishTime, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldFinishTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton13)
                    .addComponent(buttonScheduleAdd)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSchedulleDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSchedule, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                            .addGroup(jPanel17Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textFieldScheduleName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton20, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonScheduleAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton13)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton19)
                            .addComponent(buttonSchedulleDelete))
                        .addContainerGap())
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldScheduleName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelSchedule, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel17.TabConstraints.tabTitle"), jPanel17); // NOI18N

        jPanel2.setAutoscrolls(true);
        jPanel2.setName("jPanel2"); // NOI18N

        jSplitPane7.setDividerLocation(350);
        jSplitPane7.setContinuousLayout(true);
        jSplitPane7.setName("jSplitPane7"); // NOI18N

        jPanel30.setName("jPanel30"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        treeInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("treeInfo.border.title"))); // NOI18N
        treeInfo.setComponentPopupMenu(popupInfo);
        treeInfo.setName("treeInfo"); // NOI18N
        treeInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeInfoMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(treeInfo);

        jButton9.setAction(actionMap.get("addInfoItem")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N

        jButton10.setAction(actionMap.get("deleteInfoItem")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane8)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10)
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton10))
                .addContainerGap())
        );

        jSplitPane7.setLeftComponent(jPanel30);

        jPanel31.setName("jPanel31"); // NOI18N

        textFieldInfoItemName.setText(resourceMap.getString("textFieldInfoItemName.text")); // NOI18N
        textFieldInfoItemName.setName("textFieldInfoItemName"); // NOI18N
        textFieldInfoItemName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInfoItemNameKeyReleased(evt);
            }
        });

        jSplitPane5.setDividerLocation(310);
        jSplitPane5.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane5.setContinuousLayout(true);
        jSplitPane5.setName("jSplitPane5"); // NOI18N

        jScrollPane16.setName("jScrollPane16"); // NOI18N

        textPaneInfoPrint.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textPaneInfoPrint.border.title"))); // NOI18N
        textPaneInfoPrint.setName("textPaneInfoPrint"); // NOI18N
        textPaneInfoPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textPaneInfoPrintKeyReleased(evt);
            }
        });
        jScrollPane16.setViewportView(textPaneInfoPrint);
        textPaneInfoPrint.getAccessibleContext().setAccessibleName(resourceMap.getString("jTextPane1.AccessibleContext.accessibleName")); // NOI18N

        jSplitPane5.setBottomComponent(jScrollPane16);

        jPanel29.setName("jPanel29"); // NOI18N

        butWysInfo1.setText(resourceMap.getString("butWysInfo1.text")); // NOI18N
        butWysInfo1.setName("butWysInfo1"); // NOI18N
        butWysInfo1.setPreferredSize(new java.awt.Dimension(81, 15));
        butWysInfo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butWysInfo1ActionPerformed(evt);
            }
        });

        jSplitPane6.setDividerLocation(165);
        jSplitPane6.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane6.setContinuousLayout(true);
        jSplitPane6.setName("jSplitPane6"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        textPaneInfoItem.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textPaneInfoItem.border.title"))); // NOI18N
        textPaneInfoItem.setName("textPaneInfoItem"); // NOI18N
        textPaneInfoItem.setPreferredSize(new java.awt.Dimension(18, 200));
        textPaneInfoItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textPaneInfoItemKeyReleased(evt);
            }
        });
        jScrollPane9.setViewportView(textPaneInfoItem);

        jSplitPane6.setTopComponent(jScrollPane9);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel14.border.title"))); // NOI18N
        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setPreferredSize(new java.awt.Dimension(626, 200));

        jScrollPane17.setName("jScrollPane17"); // NOI18N

        labelInfoItem.setText(resourceMap.getString("labelInfoItem.text")); // NOI18N
        labelInfoItem.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelInfoItem.setName("labelInfoItem"); // NOI18N
        jScrollPane17.setViewportView(labelInfoItem);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
        );

        jSplitPane6.setRightComponent(jPanel14);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(butWysInfo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSplitPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(butWysInfo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
        );

        jSplitPane5.setLeftComponent(jPanel29);

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane5)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldInfoItemName)
                .addContainerGap())
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldInfoItemName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE))
        );

        jSplitPane7.setRightComponent(jPanel31);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane7)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane7)
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel13.setAutoscrolls(true);
        jPanel13.setName("jPanel13"); // NOI18N

        jSplitPane8.setDividerLocation(300);
        jSplitPane8.setContinuousLayout(true);
        jSplitPane8.setName("jSplitPane8"); // NOI18N

        jPanel32.setName("jPanel32"); // NOI18N

        jButton8.setAction(actionMap.get("addRespItem")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N

        jButton7.setAction(actionMap.get("deleteRespItem")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N

        jScrollPane25.setName("jScrollPane25"); // NOI18N

        treeResp.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("treeResp.border.title"))); // NOI18N
        treeResp.setName("treeResp"); // NOI18N
        jScrollPane25.setViewportView(treeResp);

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addContainerGap(55, Short.MAX_VALUE))
            .addComponent(jScrollPane25)
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addComponent(jScrollPane25, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton7))
                .addContainerGap())
        );

        jSplitPane8.setLeftComponent(jPanel32);

        jSplitPane9.setDividerLocation(330);
        jSplitPane9.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane9.setContinuousLayout(true);
        jSplitPane9.setName("jSplitPane9"); // NOI18N

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel15.border.title"))); // NOI18N
        jPanel15.setName("jPanel15"); // NOI18N

        labelRespinse.setText(resourceMap.getString("labelRespinse.text")); // NOI18N
        labelRespinse.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelRespinse.setName("labelRespinse"); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelRespinse, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(labelRespinse, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane9.setBottomComponent(jPanel15);

        jPanel33.setName("jPanel33"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        textFieldResponse.setText(resourceMap.getString("textFieldResponse.text")); // NOI18N
        textFieldResponse.setName("textFieldResponse"); // NOI18N
        textFieldResponse.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldResponseKeyReleased(evt);
            }
        });

        btnWysResp1.setText(resourceMap.getString("btnWysResp1.text")); // NOI18N
        btnWysResp1.setName("btnWysResp1"); // NOI18N
        btnWysResp1.setPreferredSize(new java.awt.Dimension(79, 15));
        btnWysResp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWysResp1ActionPerformed(evt);
            }
        });

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        textPaneResponse.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("textPaneResponse.border.title"))); // NOI18N
        textPaneResponse.setName("textPaneResponse"); // NOI18N
        textPaneResponse.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textPaneResponseKeyReleased(evt);
            }
        });
        jScrollPane11.setViewportView(textPaneResponse);

        cbCommentForResp.setText(resourceMap.getString("cbCommentForResp.text")); // NOI18N
        cbCommentForResp.setName("cbCommentForResp"); // NOI18N
        cbCommentForResp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCommentForRespActionPerformed(evt);
            }
        });

        tfHeaderCmtResp.setText(resourceMap.getString("tfHeaderCmtResp.text")); // NOI18N
        tfHeaderCmtResp.setName("tfHeaderCmtResp"); // NOI18N
        tfHeaderCmtResp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfHeaderCmtRespKeyReleased(evt);
            }
        });

        jLabel37.setText(resourceMap.getString("jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N

        tfRespID.setEditable(false);
        tfRespID.setText(resourceMap.getString("tfRespID.text")); // NOI18N
        tfRespID.setName("tfRespID"); // NOI18N

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldResponse, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfRespID, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfHeaderCmtResp))
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbCommentForResp)
                            .addComponent(btnWysResp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldResponse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(tfRespID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbCommentForResp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfHeaderCmtResp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnWysResp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
        );

        jSplitPane9.setLeftComponent(jPanel33);

        jSplitPane8.setRightComponent(jSplitPane9);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane8)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane8, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel13.TabConstraints.tabTitle"), jPanel13); // NOI18N

        jPanel18.setAutoscrolls(true);
        jPanel18.setName("jPanel18"); // NOI18N

        jButton11.setAction(actionMap.get("addResult")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N

        jButton12.setAction(actionMap.get("deleteResult")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel7.border.title"))); // NOI18N
        jPanel7.setName("jPanel7"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        spinnerWaitMax.setModel(new javax.swing.SpinnerNumberModel(0, 0, 60, 1));
        spinnerWaitMax.setName("spinnerWaitMax"); // NOI18N

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        spinnerWorkMax.setModel(new javax.swing.SpinnerNumberModel(0, 0, 600, 1));
        spinnerWorkMax.setName("spinnerWorkMax"); // NOI18N

        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        spinnerDowntimeNax.setModel(new javax.swing.SpinnerNumberModel(0, 0, 600, 1));
        spinnerDowntimeNax.setName("spinnerDowntimeNax"); // NOI18N

        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        spinnerLineServiceMax.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spinnerLineServiceMax.setName("spinnerLineServiceMax"); // NOI18N

        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N

        spinnerLineTotalMax.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinnerLineTotalMax.setName("spinnerLineTotalMax"); // NOI18N

        spinnerRelocation.setModel(new javax.swing.SpinnerNumberModel(1, 1, 600, 1));
        spinnerRelocation.setName("spinnerRelocation"); // NOI18N

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        spinnerBlackListTimeMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1440, 1));
        spinnerBlackListTimeMin.setName("spinnerBlackListTimeMin"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerWorkMax, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerDowntimeNax, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerLineServiceMax, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerLineTotalMax, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerRelocation, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerBlackListTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(spinnerWaitMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(spinnerWorkMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(spinnerDowntimeNax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(spinnerLineServiceMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(spinnerLineTotalMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(spinnerRelocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(spinnerBlackListTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        spinnerRemoveRecall.setModel(new javax.swing.SpinnerNumberModel(0, 0, 5, 1));
        spinnerRemoveRecall.setName("spinnerRemoveRecall"); // NOI18N

        jSplitPane4.setDividerLocation(300);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setAutoscrolls(true);
        jSplitPane4.setContinuousLayout(true);
        jSplitPane4.setName("jSplitPane4"); // NOI18N

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jScrollPane7.border.title"))); // NOI18N
        jScrollPane7.setName("jScrollPane7"); // NOI18N

        listReposts.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listReposts.setName("listReposts"); // NOI18N
        jScrollPane7.setViewportView(listReposts);

        jSplitPane4.setTopComponent(jScrollPane7);

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        listResults.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("listResults.border.title"))); // NOI18N
        listResults.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listResults.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listResults.setComponentPopupMenu(popupResults);
        listResults.setName("listResults"); // NOI18N
        jScrollPane13.setViewportView(listResults);

        jSplitPane4.setBottomComponent(jScrollPane13);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel12.border.title"))); // NOI18N
        jPanel12.setName("jPanel12"); // NOI18N

        dateChooserStartCsv.setDate(new Date());
        dateChooserStartCsv.setName("dateChooserStartCsv"); // NOI18N

        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N

        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        dateChooserFinishCsv.setDate(new Date());
        dateChooserFinishCsv.setName("dateChooserFinishCsv"); // NOI18N

        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        cbSeparateCSV.setEditable(true);
        cbSeparateCSV.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ";", ",", "#", "\\t" }));
        cbSeparateCSV.setName("cbSeparateCSV"); // NOI18N

        buttonExportToCSV.setText(resourceMap.getString("buttonExportToCSV.text")); // NOI18N
        buttonExportToCSV.setName("buttonExportToCSV"); // NOI18N
        buttonExportToCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportToCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dateChooserFinishCsv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateChooserStartCsv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbSeparateCSV, javax.swing.GroupLayout.Alignment.TRAILING, 0, 102, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(buttonExportToCSV)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31)
                            .addComponent(dateChooserStartCsv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel32)
                            .addComponent(dateChooserFinishCsv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(cbSeparateCSV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(buttonExportToCSV, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel25.setName("jLabel25"); // NOI18N

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGap(0, 297, Short.MAX_VALUE)
                                .addComponent(jButton11)
                                .addGap(18, 18, 18)
                                .addComponent(jButton12))
                            .addComponent(jSplitPane4)))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerRemoveRecall, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerRemoveRecall, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jSplitPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton12)
                            .addComponent(jButton11))
                        .addGap(17, 17, 17))))
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel18.TabConstraints.tabTitle"), jPanel18); // NOI18N

        jPanel8.setAutoscrolls(true);
        jPanel8.setName("jPanel8"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel9.border.title"))); // NOI18N
        jPanel9.setName("jPanel9"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        spinnerPropServerPort.setName("spinnerPropServerPort"); // NOI18N

        spinnerWebServerPort.setName("spinnerWebServerPort"); // NOI18N

        spinnerPropClientPort.setName("spinnerPropClientPort"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel3)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spinnerPropServerPort, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerWebServerPort, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerPropClientPort, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(spinnerPropServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(spinnerWebServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerPropClientPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel16.border.title"))); // NOI18N
        jPanel16.setName("jPanel16"); // NOI18N

        spinnerFirstNumber.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10000, 1));
        spinnerFirstNumber.setName("spinnerFirstNumber"); // NOI18N

        spinnerLastNumber.setModel(new javax.swing.SpinnerNumberModel(99, 99, 10000, 1));
        spinnerLastNumber.setName("spinnerLastNumber"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel22.border.title"))); // NOI18N
        jPanel22.setName("jPanel22"); // NOI18N

        buttonGroupKindNum.add(rbKindPersonal);
        rbKindPersonal.setText(resourceMap.getString("rbKindPersonal.text")); // NOI18N
        rbKindPersonal.setName("rbKindPersonal"); // NOI18N

        buttonGroupKindNum.add(rbKindCommon);
        rbKindCommon.setText(resourceMap.getString("rbKindCommon.text")); // NOI18N
        rbKindCommon.setName("rbKindCommon"); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbKindPersonal)
                    .addComponent(rbKindCommon))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(rbKindPersonal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbKindCommon))
        );

        chBoxBtnFreeDsn.setText(resourceMap.getString("chBoxBtnFreeDsn.text")); // NOI18N
        chBoxBtnFreeDsn.setName("chBoxBtnFreeDsn"); // NOI18N

        labExtPrior.setText(resourceMap.getString("labExtPrior.text")); // NOI18N
        labExtPrior.setName("labExtPrior"); // NOI18N

        spinExtPrior.setModel(new javax.swing.SpinnerNumberModel(0, 0, 99, 1));
        spinExtPrior.setName("spinExtPrior"); // NOI18N

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spinnerFirstNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerLastNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(labExtPrior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinExtPrior, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chBoxBtnFreeDsn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(spinnerFirstNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(spinnerLastNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chBoxBtnFreeDsn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinExtPrior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labExtPrior))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jTabbedPane2.border.title"))); // NOI18N
        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel23.border.title"))); // NOI18N
        jPanel23.setName("jPanel23"); // NOI18N

        textFieldURLWebService.setText(resourceMap.getString("textFieldURLWebService.text")); // NOI18N
        textFieldURLWebService.setName("textFieldURLWebService"); // NOI18N

        spinnerBranchId.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));
        spinnerBranchId.setName("spinnerBranchId"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        buttonCloudTest.setText(resourceMap.getString("buttonCloudTest.text")); // NOI18N
        buttonCloudTest.setName("buttonCloudTest"); // NOI18N
        buttonCloudTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloudTestActionPerformed(evt);
            }
        });

        buttonSendDataToSky.setText(resourceMap.getString("buttonSendDataToSky.text")); // NOI18N
        buttonSendDataToSky.setName("buttonSendDataToSky"); // NOI18N
        buttonSendDataToSky.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendDataToSkyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel23Layout.createSequentialGroup()
                                .addComponent(spinnerBranchId, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addComponent(textFieldURLWebService)))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGap(0, 103, Short.MAX_VALUE)
                        .addComponent(buttonCloudTest)
                        .addGap(18, 18, 18)
                        .addComponent(buttonSendDataToSky)
                        .addContainerGap())))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spinnerBranchId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldURLWebService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSendDataToSky)
                    .addComponent(buttonCloudTest))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel23.TabConstraints.tabTitle"), jPanel23); // NOI18N

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel24.border.title"))); // NOI18N
        jPanel24.setName("jPanel24"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        textFieldZonBoadrServAddr.setText(resourceMap.getString("textFieldZonBoadrServAddr.text")); // NOI18N
        textFieldZonBoadrServAddr.setName("textFieldZonBoadrServAddr"); // NOI18N

        spinnerZonBoadrServPort.setModel(new javax.swing.SpinnerNumberModel(500, 500, 60000, 1));
        spinnerZonBoadrServPort.setName("spinnerZonBoadrServPort"); // NOI18N

        buttonCheckZoneBoardServ.setText(resourceMap.getString("buttonCheckZoneBoardServ.text")); // NOI18N
        buttonCheckZoneBoardServ.setName("buttonCheckZoneBoardServ"); // NOI18N
        buttonCheckZoneBoardServ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckZoneBoardServActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel24))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(spinnerZonBoadrServPort, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(textFieldZonBoadrServAddr, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonCheckZoneBoardServ)))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(textFieldZonBoadrServAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(spinnerZonBoadrServPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCheckZoneBoardServ)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab(resourceMap.getString("jPanel24.TabConstraints.tabTitle"), jPanel24); // NOI18N

        propsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("propsPanel.border.title"))); // NOI18N
        propsPanel.setName("propsPanel"); // NOI18N

        jSplitPane10.setDividerLocation(300);
        jSplitPane10.setContinuousLayout(true);
        jSplitPane10.setName("jSplitPane10"); // NOI18N

        sectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("sectionPanel.border.title"))); // NOI18N
        sectionPanel.setName("sectionPanel"); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        sectionsList.setFont(resourceMap.getFont("sectionsList.font")); // NOI18N
        sectionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sectionsList.setComponentPopupMenu(popupSections);
        sectionsList.setName("sectionsList"); // NOI18N
        jScrollPane10.setViewportView(sectionsList);

        btnDeleteSection.setText(resourceMap.getString("miDeleteSection.text")); // NOI18N
        btnDeleteSection.setName("btnDeleteSection"); // NOI18N
        btnDeleteSection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miDeleteSectionActionPerformed(evt);
            }
        });

        btnAddSection.setText(resourceMap.getString("miAddSection.text")); // NOI18N
        btnAddSection.setName("btnAddSection"); // NOI18N
        btnAddSection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAddSectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sectionPanelLayout = new javax.swing.GroupLayout(sectionPanel);
        sectionPanel.setLayout(sectionPanelLayout);
        sectionPanelLayout.setHorizontalGroup(
            sectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
            .addGroup(sectionPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAddSection)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteSection))
        );
        sectionPanelLayout.setVerticalGroup(
            sectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sectionPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteSection)
                    .addComponent(btnAddSection)))
        );

        jSplitPane10.setLeftComponent(sectionPanel);

        keyvaluePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("keyvaluePanel.border.title"))); // NOI18N
        keyvaluePanel.setName("keyvaluePanel"); // NOI18N

        jScrollPane26.setName("jScrollPane26"); // NOI18N

        propsTable.setAutoCreateRowSorter(true);
        propsTable.setFont(resourceMap.getFont("propsTable.font")); // NOI18N
        propsTable.setModel(new PropsTableModel(new ServerProps.Section("", new LinkedHashMap<>())));
        propsTable.setComponentPopupMenu(popupProps);
        propsTable.setName("propsTable"); // NOI18N
        propsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        propsTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane26.setViewportView(propsTable);

        btnRemoveProp.setText(resourceMap.getString("muDeleteProp.text")); // NOI18N
        btnRemoveProp.setName("btnRemoveProp"); // NOI18N
        btnRemoveProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muDeletePropActionPerformed(evt);
            }
        });

        btnAddProp.setText(resourceMap.getString("muAddProp.text")); // NOI18N
        btnAddProp.setName("btnAddProp"); // NOI18N
        btnAddProp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muAddPropActionPerformed(evt);
            }
        });

        btnReloadProps.setText(resourceMap.getString("btnReloadProps.text")); // NOI18N
        btnReloadProps.setName("btnReloadProps"); // NOI18N
        btnReloadProps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadPropsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout keyvaluePanelLayout = new javax.swing.GroupLayout(keyvaluePanel);
        keyvaluePanel.setLayout(keyvaluePanelLayout);
        keyvaluePanelLayout.setHorizontalGroup(
            keyvaluePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
            .addGroup(keyvaluePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnReloadProps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddProp)
                .addGap(18, 18, 18)
                .addComponent(btnRemoveProp))
        );
        keyvaluePanelLayout.setVerticalGroup(
            keyvaluePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keyvaluePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(keyvaluePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoveProp)
                    .addComponent(btnAddProp)
                    .addComponent(btnReloadProps)))
        );

        jSplitPane10.setRightComponent(keyvaluePanel);

        javax.swing.GroupLayout propsPanelLayout = new javax.swing.GroupLayout(propsPanel);
        propsPanel.setLayout(propsPanelLayout);
        propsPanelLayout.setHorizontalGroup(
            propsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane10)
        );
        propsPanelLayout.setVerticalGroup(
            propsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane10)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addComponent(propsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(propsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPaneMain.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        tabHide.setBackground(resourceMap.getColor("tabHide.background")); // NOI18N
        tabHide.setName("tabHide"); // NOI18N

        jScrollPane24.setBackground(resourceMap.getColor("jScrollPane24.background")); // NOI18N
        jScrollPane24.setName("jScrollPane24"); // NOI18N

        labHidePic.setBackground(resourceMap.getColor("labHidePic.background")); // NOI18N
        labHidePic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labHidePic.setIcon(resourceMap.getIcon("labHidePic.icon")); // NOI18N
        labHidePic.setText(resourceMap.getString("labHidePic.text")); // NOI18N
        labHidePic.setName("labHidePic"); // NOI18N
        labHidePic.setOpaque(true);
        jScrollPane24.setViewportView(labHidePic);

        javax.swing.GroupLayout tabHideLayout = new javax.swing.GroupLayout(tabHide);
        tabHide.setLayout(tabHideLayout);
        tabHideLayout.setHorizontalGroup(
            tabHideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
        );
        tabHideLayout.setVerticalGroup(
            tabHideLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane24)
        );

        tabbedPaneMain.addTab(resourceMap.getString("tabHide.TabConstraints.tabTitle"), tabHide); // NOI18N

        jPanel1.add(tabbedPaneMain);

        panelPager.setBackground(resourceMap.getColor("panelPager.background")); // NOI18N
        panelPager.setBorder(new javax.swing.border.MatteBorder(null));
        panelPager.setName("panelPager"); // NOI18N
        panelPager.setPreferredSize(new java.awt.Dimension(1010, 50));

        labelPager.setText(resourceMap.getString("labelPager.text")); // NOI18N
        labelPager.setName("labelPager"); // NOI18N
        labelPager.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelPagerMouseClicked(evt);
            }
        });

        panelPagerRadio.setBackground(resourceMap.getColor("panelPagerRadio.background")); // NOI18N
        panelPagerRadio.setName("panelPagerRadio"); // NOI18N
        panelPagerRadio.setOpaque(false);
        panelPagerRadio.setLayout(new java.awt.GridLayout(3, 0));

        bgPager.add(rbPager1);
        rbPager1.setText(resourceMap.getString("rbPager1.text")); // NOI18N
        rbPager1.setName("rbPager1"); // NOI18N
        rbPager1.setOpaque(false);
        rbPager1.setPreferredSize(new java.awt.Dimension(93, 14));
        rbPager1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPager1ActionPerformed(evt);
            }
        });
        panelPagerRadio.add(rbPager1);

        bgPager.add(rbPager2);
        rbPager2.setText(resourceMap.getString("rbPager2.text")); // NOI18N
        rbPager2.setName("rbPager2"); // NOI18N
        rbPager2.setOpaque(false);
        rbPager2.setPreferredSize(new java.awt.Dimension(93, 14));
        rbPager2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPager1ActionPerformed(evt);
            }
        });
        panelPagerRadio.add(rbPager2);

        bgPager.add(rbPager3);
        rbPager3.setText(resourceMap.getString("rbPager3.text")); // NOI18N
        rbPager3.setName("rbPager3"); // NOI18N
        rbPager3.setOpaque(false);
        rbPager3.setPreferredSize(new java.awt.Dimension(93, 14));
        rbPager3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPager1ActionPerformed(evt);
            }
        });
        panelPagerRadio.add(rbPager3);

        panelPagerCombo.setName("panelPagerCombo"); // NOI18N
        panelPagerCombo.setOpaque(false);

        labelPagerCaptionCombo.setText(resourceMap.getString("labelPagerCaptionCombo.text")); // NOI18N
        labelPagerCaptionCombo.setName("labelPagerCaptionCombo"); // NOI18N

        comboBoxPager.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxPager.setName("comboBoxPager"); // NOI18N
        comboBoxPager.setOpaque(false);
        comboBoxPager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPagerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPagerComboLayout = new javax.swing.GroupLayout(panelPagerCombo);
        panelPagerCombo.setLayout(panelPagerComboLayout);
        panelPagerComboLayout.setHorizontalGroup(
            panelPagerComboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPagerComboLayout.createSequentialGroup()
                .addGroup(panelPagerComboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelPagerCaptionCombo)
                    .addComponent(comboBoxPager, 0, 323, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPagerComboLayout.setVerticalGroup(
            panelPagerComboLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPagerComboLayout.createSequentialGroup()
                .addComponent(labelPagerCaptionCombo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxPager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEditPager.setName("panelEditPager"); // NOI18N
        panelEditPager.setOpaque(false);

        labelPagerCaptionEdit.setText(resourceMap.getString("labelPagerCaptionEdit.text")); // NOI18N
        labelPagerCaptionEdit.setName("labelPagerCaptionEdit"); // NOI18N

        textFieldPager.setText(resourceMap.getString("textFieldPager.text")); // NOI18N
        textFieldPager.setName("textFieldPager"); // NOI18N

        buttonPagerEdit.setText(resourceMap.getString("buttonPagerEdit.text")); // NOI18N
        buttonPagerEdit.setName("buttonPagerEdit"); // NOI18N
        buttonPagerEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPagerEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEditPagerLayout = new javax.swing.GroupLayout(panelEditPager);
        panelEditPager.setLayout(panelEditPagerLayout);
        panelEditPagerLayout.setHorizontalGroup(
            panelEditPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditPagerLayout.createSequentialGroup()
                .addGroup(panelEditPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditPagerLayout.createSequentialGroup()
                        .addComponent(textFieldPager, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonPagerEdit))
                    .addComponent(labelPagerCaptionEdit))
                .addContainerGap())
        );
        panelEditPagerLayout.setVerticalGroup(
            panelEditPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditPagerLayout.createSequentialGroup()
                .addComponent(labelPagerCaptionEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEditPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldPager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonPagerEdit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelPagerLayout = new javax.swing.GroupLayout(panelPager);
        panelPager.setLayout(panelPagerLayout);
        panelPagerLayout.setHorizontalGroup(
            panelPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPagerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPager, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEditPager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPagerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPagerRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelPagerLayout.setVerticalGroup(
            panelPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPagerLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(panelPagerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPagerCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelPagerRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelPagerLayout.createSequentialGroup()
                .addComponent(labelPager)
                .addContainerGap())
            .addComponent(panelEditPager, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        menuFile.setText(resourceMap.getString("menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        menuLangs.setText(resourceMap.getString("menuLangs.text")); // NOI18N
        menuLangs.setName("menuLangs"); // NOI18N
        menuFile.add(menuLangs);

        jSeparator15.setName("jSeparator15"); // NOI18N
        menuFile.add(jSeparator15);

        jMenuItem25.setAction(actionMap.get("sendMessage")); // NOI18N
        jMenuItem25.setName("jMenuItem25"); // NOI18N
        menuFile.add(jMenuItem25);

        jMenuItem8.setAction(actionMap.get("saveConfiguration")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        menuFile.add(jMenuItem8);

        jMenuItem4.setAction(actionMap.get("hideWindow")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        menuFile.add(jMenuItem4);

        jSeparator1.setName("jSeparator1"); // NOI18N
        menuFile.add(jSeparator1);

        jMenuItem3.setAction(actionMap.get("quit")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        menuFile.add(jMenuItem3);

        jMenuBar1.add(menuFile);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem19.setAction(actionMap.get("editMainBoard")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenu1.add(jMenuItem19);

        menuBoards.setText(resourceMap.getString("menuBoards.text")); // NOI18N
        menuBoards.setName("menuBoards"); // NOI18N

        bgBoards.add(rbmClassic);
        rbmClassic.setSelected(true);
        rbmClassic.setText(resourceMap.getString("rbmClassic.text")); // NOI18N
        rbmClassic.setName("rbmClassic"); // NOI18N
        rbmClassic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmClassicActionPerformed(evt);
            }
        });
        menuBoards.add(rbmClassic);

        bgBoards.add(rbmHtml);
        rbmHtml.setText(resourceMap.getString("rbmHtml.text")); // NOI18N
        rbmHtml.setName("rbmHtml"); // NOI18N
        rbmHtml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbmClassicActionPerformed(evt);
            }
        });
        menuBoards.add(rbmHtml);

        jMenu1.add(menuBoards);

        jMenuBar1.add(jMenu1);

        menuUsers.setText(resourceMap.getString("menuUsers.text")); // NOI18N
        menuUsers.setName("menuUsers"); // NOI18N

        jMenuItem5.setAction(actionMap.get("addUser")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        menuUsers.add(jMenuItem5);

        jSeparator2.setName("jSeparator2"); // NOI18N
        menuUsers.add(jSeparator2);

        jMenuItem2.setAction(actionMap.get("deleteUser")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        menuUsers.add(jMenuItem2);

        jSeparator14.setName("jSeparator14"); // NOI18N
        menuUsers.add(jSeparator14);

        jMenuItem37.setAction(actionMap.get("changePriority")); // NOI18N
        jMenuItem37.setName("jMenuItem37"); // NOI18N
        menuUsers.add(jMenuItem37);

        jMenuItem43.setAction(actionMap.get("checkClient")); // NOI18N
        jMenuItem43.setName("jMenuItem43"); // NOI18N
        menuUsers.add(jMenuItem43);

        jMenuBar1.add(menuUsers);

        menuServices.setText(resourceMap.getString("menuServices.text")); // NOI18N
        menuServices.setName("menuServices"); // NOI18N

        jMenuItem7.setAction(actionMap.get("addService")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        menuServices.add(jMenuItem7);

        jSeparator3.setName("jSeparator3"); // NOI18N
        menuServices.add(jSeparator3);

        jMenuItem6.setAction(actionMap.get("deleteService")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        menuServices.add(jMenuItem6);

        jSeparator4.setName("jSeparator4"); // NOI18N
        menuServices.add(jSeparator4);

        jMenuItem9.setAction(actionMap.get("getTicket")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        menuServices.add(jMenuItem9);

        jMenuItem23.setAction(actionMap.get("standAdvance")); // NOI18N
        jMenuItem23.setName("jMenuItem23"); // NOI18N
        menuServices.add(jMenuItem23);

        jMenuBar1.add(menuServices);

        menuAbout.setText(resourceMap.getString("menuAbout.text")); // NOI18N
        menuAbout.setName("menuAbout"); // NOI18N

        menuItemHelp.setAction(actionMap.get("getHelp")); // NOI18N
        menuItemHelp.setName("menuItemHelp"); // NOI18N
        menuAbout.add(menuItemHelp);

        menuItemAbout.setAction(actionMap.get("getAbout")); // NOI18N
        menuItemAbout.setName("menuItemAbout"); // NOI18N
        menuAbout.add(menuItemAbout);

        jSeparator17.setName("jSeparator17"); // NOI18N
        menuAbout.add(jSeparator17);

        jMenuItemBagtracker.setText(resourceMap.getString("jMenuItemBagtracker.text")); // NOI18N
        jMenuItemBagtracker.setName("jMenuItemBagtracker"); // NOI18N
        jMenuItemBagtracker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBagtrackerActionPerformed(evt);
            }
        });
        menuAbout.add(jMenuItemBagtracker);

        jMenuItemForum.setText(resourceMap.getString("jMenuItemForum.text")); // NOI18N
        jMenuItemForum.setName("jMenuItemForum"); // NOI18N
        jMenuItemForum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemForumActionPerformed(evt);
            }
        });
        menuAbout.add(jMenuItemForum);

        jMenuBar1.add(menuAbout);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1005, Short.MAX_VALUE)
            .addComponent(panelPager, javax.swing.GroupLayout.DEFAULT_SIZE, 1005, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(panelPager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void checkBoxServerAutoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkBoxServerAutoStateChanged
    buttonServerRequest.setEnabled(!checkBoxServerAuto.isSelected());
    if (timer.isRunning() && checkBoxServerAuto.isSelected()) {
        checkServer();
    }
    startTimer();
}//GEN-LAST:event_checkBoxServerAutoStateChanged

private void buttonLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLockActionPerformed

    checkWelcome(Uses.WELCOME_LOCK);

}//GEN-LAST:event_buttonLockActionPerformed

private void buttonUnlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUnlockActionPerformed
//GEN-LAST:event_buttonUnlockActionPerformed
        checkWelcome(Uses.WELCOME_UNLOCK);
    }

//**********************************************************************************************************************
//**********************************************************************************************************************
//*****************************************Сохранение*******************************************************************
private void checkBoxServerAutoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_checkBoxServerAutoFocusLost
    saveSettings();
}//GEN-LAST:event_checkBoxServerAutoFocusLost

private void textFieldClientAdressFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldClientAdressFocusLost
    saveSettings();
}//GEN-LAST:event_textFieldClientAdressFocusLost

private void checkBoxClientAutoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_checkBoxClientAutoFocusLost
    saveSettings();
}//GEN-LAST:event_checkBoxClientAutoFocusLost

//*****************************************Сохранение*******************************************************************
//**********************************************************************************************************************
//*************************************** Запрос в ручную **************************************************************
private void buttonServerRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonServerRequestActionPerformed
    checkServer();
}//GEN-LAST:event_buttonServerRequestActionPerformed

private void buttonClientRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClientRequestActionPerformed
    checkWelcome(null);
}//GEN-LAST:event_buttonClientRequestActionPerformed

private void checkBoxClientAutoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkBoxClientAutoStateChanged
    buttonClientRequest.setEnabled(!checkBoxClientAuto.isSelected());
    if (timer.isRunning() && checkBoxClientAuto.isSelected()) {
        checkWelcome(null);
    }
    startTimer();
}//GEN-LAST:event_checkBoxClientAutoStateChanged

private void buttonShutDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShutDownActionPerformed
    // Уточним намерения
    if (JOptionPane.showConfirmDialog(this,
            getLocaleMessage("admin.close_welcame.title"),
            getLocaleMessage("admin.close_welcame.caption"),
            JOptionPane.YES_NO_OPTION) == 1) {
        return;
    }
    checkWelcome(Uses.WELCOME_OFF);
}//GEN-LAST:event_buttonShutDownActionPerformed

private void buttonRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRestartActionPerformed
    checkWelcome(Uses.WELCOME_REINIT);
    final ATalkingClock clock = new ATalkingClock(1000, 1) {

        @Override
        public void run() {
            checkWelcome(null);
            JOptionPane.showConfirmDialog(null, getLocaleMessage("admin.server_reinit.title"), getLocaleMessage("admin.server_reinit.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        }
    };
    clock.start();
}//GEN-LAST:event_buttonRestartActionPerformed

private void textFieldServerAddrFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldServerAddrFocusLost

    saveSettings();
}//GEN-LAST:event_textFieldServerAddrFocusLost

private void passwordFieldUserKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordFieldUserKeyReleased

    saveUser();
}//GEN-LAST:event_passwordFieldUserKeyReleased
    private boolean forHidehide = false;
private void tabbedPaneMainStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneMainStateChanged

    // это событие переключения закладок на табе.
    menuServices.setEnabled(tabbedPaneMain.getSelectedIndex() == 1);
    menuUsers.setEnabled(tabbedPaneMain.getSelectedIndex() == 1);
    // в пейджере листнем новость
    if (form != null && forPager != null && new Date().getTime() - ancorPager > 0/*1000 * 60 * 5*/) {
        ancorPager = new Date().getTime();
        forPager.showData(true);
    }

    if (forHidehide) {
        tabbedPaneMain.remove(tabHide);
        forHidehide = false;
    }
    forHidehide = tabbedPaneMain.getSelectedComponent() == tabHide;
}//GEN-LAST:event_tabbedPaneMainStateChanged
    private long ancorPager = new Date().getTime();
private void textFieldUserIdentKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldUserIdentKeyReleased

    saveUser();
}//GEN-LAST:event_textFieldUserIdentKeyReleased

private void listUserServiceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listUserServiceMouseClicked

    // назначение приоритета услуге.
    if (evt.getClickCount() == 2) {
        changeServicePriority();
    }

}//GEN-LAST:event_listUserServiceMouseClicked

private void checkBoxReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBoxReportMouseClicked

    saveUser();
}//GEN-LAST:event_checkBoxReportMouseClicked

private void checkBoxAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkBoxAdminMouseClicked
    //проверим не последний ли это админ
    final QUser user = (QUser) listUsers.getSelectedValue();
    if (user.getAdminAccess()) {
        int cnt = 0;
        for (int i = 0; i < listUsers.getModel().getSize(); i++) {
            if (((QUser) listUsers.getModel().getElementAt(i)).getAdminAccess()) {
                cnt++;
            }
        }
        if (cnt == 1) {
            JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.edit_user_err.title"), getLocaleMessage("admin.edit_user_err.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            checkBoxAdmin.setSelected(true);
            return;
        }
    }
    saveUser();
}//GEN-LAST:event_checkBoxAdminMouseClicked

private void treeServicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeServicesMouseClicked
    // Редактирование услуги.
    if (evt.getClickCount() == 2) {
        final TreePath selectedPath = treeServices.getSelectionPath();
        if (selectedPath != null) {
            editService();
        }
    }

}//GEN-LAST:event_treeServicesMouseClicked

private void buttonRestartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRestartServerActionPerformed
    if (JOptionPane.showConfirmDialog(null, "Ты точно хочешь проделать эту богомерскую операцию?", "Подумай три раза, о мышкатыкатель!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 1) {
        return;
    }
    NetCommander.restartServer(new ServerNetProperty());
    final ATalkingClock clock = new ATalkingClock(4000, 1) {

        @Override
        public void run() {
            JOptionPane.showConfirmDialog(null, getLocaleMessage("admin.server_restart.title"), getLocaleMessage("admin.server_restart.caption"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            checkServer();
        }
    };
    clock.start();

}//GEN-LAST:event_buttonRestartServerActionPerformed

private void textFieldInfoItemNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInfoItemNameKeyReleased
    final QInfoItem item = (QInfoItem) treeInfo.getLastSelectedPathComponent();
    if (item != null/* && !item.isRoot()*/) {
        item.setName(textFieldInfoItemName.getText());
    }
}//GEN-LAST:event_textFieldInfoItemNameKeyReleased

private void textPaneInfoItemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textPaneInfoItemKeyReleased
    final QInfoItem item = (QInfoItem) treeInfo.getLastSelectedPathComponent();
    if (item != null/* && !item.isRoot()*/) {
        item.setHTMLText(textPaneInfoItem.getText());
        labelInfoItem.setText(textPaneInfoItem.getText());
    }
}//GEN-LAST:event_textPaneInfoItemKeyReleased

private void treeInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeInfoMouseClicked
    final QInfoItem item = (QInfoItem) treeInfo.getLastSelectedPathComponent();
    if (item != null) {
        //textFieldInfoItemName.setEnabled(!item.isRoot());
        //textPaneInfoItem.setEnabled(!item.isRoot());
        //textPaneInfoPrint.setEnabled(!item.isRoot());
    }
}//GEN-LAST:event_treeInfoMouseClicked

private void textFieldResponseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldResponseKeyReleased
    final QRespItem item = (QRespItem) treeResp.getLastSelectedPathComponent();
    if (item != null) {
        item.setName(textFieldResponse.getText());
    }
}//GEN-LAST:event_textFieldResponseKeyReleased

private void textPaneResponseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textPaneResponseKeyReleased
    final QRespItem item = (QRespItem) treeResp.getLastSelectedPathComponent();
    if (item != null) {
        item.setHTMLText(textPaneResponse.getText());
        labelRespinse.setText(textPaneResponse.getText());
    }
}//GEN-LAST:event_textPaneResponseKeyReleased

private void textFieldScheduleNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldScheduleNameKeyReleased
    final QSchedule item = (QSchedule) listSchedule.getSelectedValue();
    if (item != null) {
        item.setName(textFieldScheduleName.getText());
    }
}//GEN-LAST:event_textFieldScheduleNameKeyReleased

private void listScheduleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listScheduleMouseClicked
    // Редактирование услуги.
    if (evt.getClickCount() == 2) {
        final QSchedule item = (QSchedule) listSchedule.getSelectedValue();
        if (item != null) {
            editSchedule();
        }
    }
}//GEN-LAST:event_listScheduleMouseClicked

private void textFieldCalendarNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldCalendarNameKeyReleased
    final QCalendar item = (QCalendar) listCalendar.getSelectedValue();
    if (item != null) {
        item.setName(textFieldCalendarName.getText());
    }
}//GEN-LAST:event_textFieldCalendarNameKeyReleased

private void tabbedPaneMainFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tabbedPaneMainFocusLost
    final CalendarTableModel model = (CalendarTableModel) tableCalendar.getModel();
    if (!model.isSaved()) {
        if (0 == JOptionPane.showConfirmDialog(null, getLocaleMessage("admin.calendar_change.message"), getLocaleMessage("admin.calendar_change.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
            model.save();
        }
    }
}//GEN-LAST:event_tabbedPaneMainFocusLost
    private int inGrid = 0;

private void tableCalendarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tableCalendarFocusGained
    if (inGrid > 0) {
        inGrid = 0;
        return;
    }
    // проверка на то что перед редактированием сетки нужно сохранать сам список календарей
    // инече не будет известно с каким ID привязки к календарю сохранять выходные дни
    inGrid++;
    for (int i = 0; i < listCalendar.getModel().getSize(); i++) {
        boolean flag = false;
        for (QCalendar calendar : QCalendarList.getInstance().getItems()) {
            if (((QCalendar) listCalendar.getModel().getElementAt(i)).getId().equals(calendar.getId())) {
                flag = true;
            }
        }
        if (!flag) {
            JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.calendar_warn.message"), getLocaleMessage("admin.calendar_warn.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }
}//GEN-LAST:event_tableCalendarFocusGained

private void textPaneInfoPrintKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textPaneInfoPrintKeyReleased
    final QInfoItem item = (QInfoItem) treeInfo.getLastSelectedPathComponent();
    if (item != null /*&& !item.isRoot()*/) {
        item.setTextPrint(textPaneInfoPrint.getText());
    }
}//GEN-LAST:event_textPaneInfoPrintKeyReleased

private void buttonResetMainTabloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResetMainTabloActionPerformed
    NetCommander.restartMainTablo(new ServerNetProperty());
    JOptionPane.showConfirmDialog(null, getLocaleMessage("admin.main_tablo_restart.message"), getLocaleMessage("admin.main_tablo_restart.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_buttonResetMainTabloActionPerformed

private void buttonCloudTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloudTestActionPerformed
    // поддержка расширяемости плагинами
    final StringBuilder sb = new StringBuilder(getLocaleMessage("admin.cloud_test_dialog.results") + ":\n");
    try {
        for (final IPing event : ServiceLoader.load(IPing.class)) {
            if (event.getUID() == 01L) {
                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                sb.append(event.getDescription()).append(" ").append(getLocaleMessage("admin.cloud_test_dialog.result")).append(": ").append(event.ping()).append("\n");
            }
        }
    } catch (Throwable ex) {
        QLog.l().logger().error("SPI error: ", ex);
        sb.append(getLocaleMessage("admin.cloud_test_dialog.error"));
    }
    final String res = "URL=\"" + ServerProps.getInstance().getProps().getSkyServerUrl() + "\"\n\n" + sb.toString();
    sb.setLength(0);
    JOptionPane.showConfirmDialog(null, res, getLocaleMessage("admin.cloud_test_dialog.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_buttonCloudTestActionPerformed

private void buttonSendDataToSkyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendDataToSkyActionPerformed
    // поддержка расширяемости плагинами
    final Thread th = new Thread(() -> {
        int all = 0;
        all = QServiceTree.getInstance().getNodes().stream().filter((service) -> (service.isLeaf())).map((_item) -> 1).reduce(all, Integer::sum);
        all += QUserList.getInstance().getSize();
        int tmp = 0;
        try {
            for (final IDataExchange event : ServiceLoader.load(IDataExchange.class)) {
                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                for (QService service : QServiceTree.getInstance().getNodes()) {
                    if (service.isLeaf()) {
                        event.sendServiceName(ServerProps.getInstance().getProps().getBranchOfficeId(), service.getId(), service.getName());
                        tmp++;
                        final String s = "" + tmp + "/" + all + "  " + tmp * 100 / all + "%";
                        SwingUtilities.invokeLater(() -> {
                            buttonSendDataToSky.setText(s);
                        });
                    }
                }
                for (QUser user : QUserList.getInstance().getItems()) {
                    event.sendUserName(ServerProps.getInstance().getProps().getBranchOfficeId(), user.getId(), user.getName());
                    tmp++;
                    final String s = "" + tmp + "/" + all + "  " + tmp * 100 / all + "%";
                    SwingUtilities.invokeLater(() -> {
                        buttonSendDataToSky.setText(s);
                    });
                }
            }
        } catch (Throwable ex) {
            QLog.l().logger().error("Не отослали названия в облако.", ex);
            JOptionPane.showConfirmDialog(null, getLocaleMessage("admin.cloud_senddata_dialog.message_err") + "\n" + ex.getMessage(), getLocaleMessage("admin.cloud_senddata_dialog.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showConfirmDialog(null, getLocaleMessage("admin.cloud_senddata_dialog.message") + " " + tmp + "/" + all, getLocaleMessage("admin.cloud_senddata_dialog.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            buttonSendDataToSky.setText(getLocaleMessage("buttonSendDataToSky.text"));
        });
    });
    th.start();

}//GEN-LAST:event_buttonSendDataToSkyActionPerformed

    private void buttonCheckZoneBoardServActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCheckZoneBoardServActionPerformed
        // поддержка расширяемости плагинами
        final StringBuilder sb = new StringBuilder(getLocaleMessage("admin.zoneboard_test_dialog.results") + ":\n");
        try {
            for (final IPing event : ServiceLoader.load(IPing.class)) {
                QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
                System.out.println(">>SPI: " + event.getDescription());
                sb.append(event.getDescription()).append(" ").append(getLocaleMessage("admin.zoneboard_test_dialog.result")).append(": ").append(event.ping()).append("\n");
            }
        } catch (Throwable ex) {
            QLog.l().logger().error("Ошибка при пинговании зонального сервера. ", ex);
            sb.append(getLocaleMessage("admin.zoneboard_test_dialog.error"));
        }
        final String res = sb.toString();
        sb.setLength(0);
        JOptionPane.showConfirmDialog(null, res, getLocaleMessage("admin.zoneboard_test_dialog.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_buttonCheckZoneBoardServActionPerformed

    private void buttonRefreshBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshBanActionPerformed
        listBan.setModel(new DefaultComboBoxModel(NetCommander.getBanedList(new ServerNetProperty()).toArray()));
    }//GEN-LAST:event_buttonRefreshBanActionPerformed

    private void listBreaksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listBreaksMouseClicked
        // Редактирование перерывов
        if (evt.getClickCount() == 2) {
            final QBreaks item = (QBreaks) listBreaks.getSelectedValue();
            if (item != null) {
                editBreak();
            }
        }
    }//GEN-LAST:event_listBreaksMouseClicked

    private void textFieldSearchServiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchServiceKeyReleased
        TreeNode node = null;
        boolean flag = evt.getKeyCode() != 114;
        final QService ser = (QService) treeServices.getLastSelectedPathComponent();
        for (Object object : ((ATreeModel) treeServices.getModel()).getNodes()) {
            final QService service = (QService) object;
            if (flag) {
                if (service.toString().toLowerCase().contains(textFieldSearchService.getText().trim().toLowerCase())) {
                    node = (TreeNode) object;
                    break;
                }
            } else if (!flag && (ser == null || service.getId().equals(ser.getId()))) {
                flag = true;
            }
        }

        if (node != null) {
            TreeNode[] nodes = ((DefaultTreeModel) treeServices.getModel()).getPathToRoot(node);
            TreePath path = new TreePath(nodes);
            treeServices.setSelectionPath(path);
            treeServices.setExpandsSelectedPaths(true);
            treeServices.scrollPathToVisible(path);

        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_textFieldSearchServiceKeyReleased

    private void treeServicesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeServicesKeyReleased
        if (evt.getKeyCode() == 114) {
            textFieldSearchServiceKeyReleased(evt);
        }
    }//GEN-LAST:event_treeServicesKeyReleased

    private void buttonExportToCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportToCSVActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(getLocaleMessage("save.statictic"));
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return !f.isFile() || f.getAbsolutePath().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return getLocaleMessage("files.type.csv");
            }
        });
        //fc.setCurrentDirectory(new File("config"));
        //fc.setSelectedFile(new File(configuration.getSystemName()));
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final File file;
            //This is where a real application would open the file.
            if (!fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".csv")) {
                file = new File(fc.getSelectedFile().getAbsoluteFile() + ".csv");
            } else {
                file = fc.getSelectedFile();
            }

            Spring.getInstance().getHt().getSessionFactory().openSession().doWork((Connection connection) -> {
                final GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(dateChooserStartCsv.getDate());
                gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
                gc.set(GregorianCalendar.MINUTE, 0);
                gc.set(GregorianCalendar.SECOND, 0);
                gc.set(GregorianCalendar.MILLISECOND, 0);
                final String std = Uses.FORMAT_FOR_REP.format(gc.getTime());
                gc.setTime(dateChooserFinishCsv.getDate());
                gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
                gc.set(GregorianCalendar.MINUTE, 0);
                gc.set(GregorianCalendar.SECOND, 0);
                gc.set(GregorianCalendar.MILLISECOND, 0);
                gc.add(GregorianCalendar.HOUR, 24);
                final String find = Uses.FORMAT_FOR_REP.format(gc.getTime());
                final String sql = " SELECT "
                        + "    s.client_id as id, "
                        + "    concat(c.service_prefix , c.number) as num, "
                        + "    c.input_data as inp,  "
                        + ("org.h2.Driver".equalsIgnoreCase(Spring.getInstance().getDriverClassName())
                        ? "   FORMATDATETIME(s.client_stand_time, 'd.MM.yyyy HH:mm') as stnd, "
                        + "   sv.name as srv, "
                        + "   FORMATDATETIME(s.user_start_time, 'd.MM.yyyy HH:mm') as strt, "
                        + "   FORMATDATETIME(s.user_finish_time, 'd.MM.yyyy HH:mm') as fin, "
                        : "      DATE_FORMAT(s.client_stand_time, '%d.%m.%y %H:%i') as stnd, "
                        + "      sv.name as srv, "
                        + "      DATE_FORMAT(s.user_start_time, '%d.%m.%y %H:%i') as strt, "
                        + "      DATE_FORMAT(s.user_finish_time, '%d.%m.%y %H:%i') as fin, ")
                        + "    u.name as usr, "
                        + "    s.client_wait_period as wt, "
                        + "    s.user_work_period as wrk, "
                        + "    IFNULL(r.name, '') as res "
                        + " FROM statistic s left join results r on s.results_id=r.id, clients c, users u, services sv "
                        + " WHERE s.client_id=c.id and s.user_id=u.id and s.service_id=sv.id "
                        + "    and s.client_stand_time>='" + std + "' and s.client_stand_time<='" + find + "'";
                try (ResultSet set = connection.createStatement().executeQuery(sql)) {
                    Writer writer;
                    try {
                        writer = new OutputStreamWriter(new FileOutputStream(file), "cp1251").append("");
                        writer.append("№");
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.number"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.data"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.stand_time"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.service_name"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.start_time"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.finish_time"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.user"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.wait"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.work"));
                        writer.append(cbSeparateCSV.getSelectedItem().toString());
                        writer.append(getLocaleMessage("csv.result"));
                        writer.append('\n');

                        while (set.next()) {
                            writer.append(set.getString("id"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("num"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("inp"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("stnd"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("srv").replace(cbSeparateCSV.getSelectedItem().toString(), " "));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("strt"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("fin"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("usr"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("wt"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("wrk"));
                            writer.append(cbSeparateCSV.getSelectedItem().toString());
                            writer.append(set.getString("res"));
                            writer.append('\n');
                        }
                        //generate whatever data you want

                        writer.flush();
                        writer.close();
                    } catch (IOException ex) {
                        throw new ClientException(ex);
                    }
                }
                JOptionPane.showMessageDialog(fc,
                        getLocaleMessage("stat.saved"),
                        getLocaleMessage("stat.saving"),
                        JOptionPane.INFORMATION_MESSAGE);
            });

        }
    }//GEN-LAST:event_buttonExportToCSVActionPerformed

    private void textFieldExtPointKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldExtPointKeyReleased
        saveUser();
    }//GEN-LAST:event_textFieldExtPointKeyReleased

    private void buttonPagerEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPagerEditActionPerformed
        sendPager();
    }//GEN-LAST:event_buttonPagerEditActionPerformed

    private void comboBoxPagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxPagerActionPerformed
        sendPager();
    }//GEN-LAST:event_comboBoxPagerActionPerformed

    private void rbPager1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPager1ActionPerformed
        sendPager();
    }//GEN-LAST:event_rbPager1ActionPerformed

    private void spinCalendarYearStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinCalendarYearStateChanged
        System.out.println(spinCalendarYear.getValue());
        tableCalendar.setModel((CalendarTableModel) tableCalendar.getModel());
        tableCalendar.setDefaultRenderer(FreeDay.class, new TableCell((Integer) (spinCalendarYear.getValue())));
        tableCalendar.setDefaultRenderer(Object.class, new TableCell((Integer) (spinCalendarYear.getValue())));

        ((CalendarTableModel) tableCalendar.getModel()).fireTableDataChanged();
        ((CalendarTableModel) tableCalendar.getModel()).fireTableStructureChanged();
        tableCalendar.getColumnModel().getColumn(0).setPreferredWidth(500);
    }//GEN-LAST:event_spinCalendarYearStateChanged

    private void jMenuItemBagtrackerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBagtrackerActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("https://bitbucket.org/Apertum/qsystem/issues"));
        } catch (URISyntaxException | IOException ex) {
            QLog.l().logger().error(ex);
        }
    }//GEN-LAST:event_jMenuItemBagtrackerActionPerformed

    private void jMenuItemForumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemForumActionPerformed
        try {
            Desktop.getDesktop().browse(new URI("http://qsystem.info/index.php/forum/index"));
        } catch (URISyntaxException | IOException ex) {
            QLog.l().logger().error(ex);
        }
    }//GEN-LAST:event_jMenuItemForumActionPerformed

    private void labelPagerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelPagerMouseClicked
        if (pagerUrl != null) {
            try {
                Desktop.getDesktop().browse(new URI(pagerUrl));
            } catch (URISyntaxException | IOException ex) {
                QLog.l().logger().error(ex);
            }
        }
    }//GEN-LAST:event_labelPagerMouseClicked

    private void butWysInfo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butWysInfo1ActionPerformed
        textPaneInfoItem.setText(WysiwygDlg.showInstance(textPaneInfoItem.getText()));
        labelInfoItem.setText(textPaneInfoItem.getText());
    }//GEN-LAST:event_butWysInfo1ActionPerformed

    private void btnWysResp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWysResp1ActionPerformed
        textPaneResponse.setText(WysiwygDlg.showInstance(textPaneResponse.getText()));
        labelRespinse.setText(textPaneResponse.getText());
    }//GEN-LAST:event_btnWysResp1ActionPerformed

    private void butAddSpecScedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAddSpecScedActionPerformed
        QSpecSchedule sps = FSpecScheduleDialog.changeQSpecSchedule(form, true, null);
        if (sps != null) {
            final QCalendar item = (QCalendar) listCalendar.getSelectedValue();
            if (item == null) {
                return;
            }
            sps.setCalendar(item);
            item.getSpecSchedules().add(sps);
            listSpecSced.setModel(new DefaultComboBoxModel(item.getSpecSchedules().toArray()));
        }
    }//GEN-LAST:event_butAddSpecScedActionPerformed

    private void butEditSpecScedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butEditSpecScedActionPerformed
        if (listSpecSced.getSelectedIndex() != -1) {
            QSpecSchedule sps = FSpecScheduleDialog.changeQSpecSchedule(form, true, (QSpecSchedule) listSpecSced.getSelectedValue());
            if (sps != null) {
                final QCalendar item = (QCalendar) listCalendar.getSelectedValue();
                if (item == null) {
                    return;
                }
                listSpecSced.setModel(new DefaultComboBoxModel(item.getSpecSchedules().toArray()));
            }
        }
    }//GEN-LAST:event_butEditSpecScedActionPerformed

    private void butDeleteSpecScedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteSpecScedActionPerformed
        if (listSpecSced.getSelectedIndex() != -1) {
            if (0 != JOptionPane.showConfirmDialog(this, "Do you really want remove the special schedule?", "Removing", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                return;
            }
            QSpecSchedule sps = (QSpecSchedule) listSpecSced.getSelectedValue();
            if (sps != null) {
                final QCalendar item = (QCalendar) listCalendar.getSelectedValue();
                if (item == null) {
                    return;
                }
                item.getSpecSchedules().remove(sps);
                listSpecSced.setModel(new DefaultComboBoxModel(item.getSpecSchedules().toArray()));
            }
        }
    }//GEN-LAST:event_butDeleteSpecScedActionPerformed

    private void listSpecScedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSpecScedMouseClicked
        if (evt.getClickCount() > 1) {
            butEditSpecScedActionPerformed(null);
        }
    }//GEN-LAST:event_listSpecScedMouseClicked

    private void tfHeaderCmtRespKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfHeaderCmtRespKeyReleased
        final QRespItem item = (QRespItem) treeResp.getLastSelectedPathComponent();
        if (item != null) {
            item.setInput_caption(tfHeaderCmtResp.getText());
        }
    }//GEN-LAST:event_tfHeaderCmtRespKeyReleased

    private void cbCommentForRespActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCommentForRespActionPerformed
        final QRespItem item = (QRespItem) treeResp.getLastSelectedPathComponent();
        if (item != null) {
            item.setInput_required(cbCommentForResp.isSelected());
        }
    }//GEN-LAST:event_cbCommentForRespActionPerformed

    private void jMenuItemEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditUserActionPerformed
        final QUser user = (QUser) listUsers.getSelectedValue();
        if (user != null) {
            FUserChangeDialog.changeUser(form, true, user);
        }
    }//GEN-LAST:event_jMenuItemEditUserActionPerformed

    private void muAddPropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muAddPropActionPerformed
        if (sectionsList.getLastVisibleIndex() == -1) {
            return;
        }
        final ServerProps.Section section = sectionsList.getSelectedValue();
        if (section == null) {
            return;
        }

        final String key = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_prop_name.message"), getLocaleMessage("admin.prop.title"), 3, null, null, "");
        if (key == null) {
            return;
        }
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.prop_key_empty.error"), getLocaleMessage("admin.prop.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (section.getProperty(key) != null) {
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.prop_key_dublicate.error"), getLocaleMessage("admin.prop.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        QProperty prop = section.addProperty(key, null, null);
        ((PropsTableModel) propsTable.getModel()).fireTableDataChanged();

        propsTable.setRowSelectionInterval(section.getProperties().size() - 1, section.getProperties().size() - 1);

    }//GEN-LAST:event_muAddPropActionPerformed

    private void muDeletePropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muDeletePropActionPerformed
        if (sectionsList.getLastVisibleIndex() == -1) {
            return;
        }
        final ServerProps.Section section = sectionsList.getSelectedValue();
        if (section == null) {
            return;
        }

        if (!propsTable.getSelectionModel().isSelectionEmpty()) {
            final String key = (String) propsTable.getModel().getValueAt(propsTable.getSelectedRow(), 0);
            final QProperty prop = ((PropsTableModel) propsTable.getModel()).getPropertyByKey(key);
            if (prop == null || JOptionPane.showConfirmDialog(this,
                    java.text.MessageFormat.format(getLocaleMessage("admin.del_prop_asc.msg"), new Object[]{prop.toString()}),
                    getLocaleMessage("admin.prop.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            int i = propsTable.getSelectedRow();
            // удалим навсегда
            ((PropsTableModel) propsTable.getModel()).removeByKey(prop);
            ((PropsTableModel) propsTable.getModel()).fireTableDataChanged();
            // в фокус запись рядом
            if (propsTable.getRowCount() != 0) {
                if (propsTable.getRowCount() - 1 < i) {
                    i--;
                }
                propsTable.getSelectionModel().setSelectionInterval(i, i);
            }
        }
    }//GEN-LAST:event_muDeletePropActionPerformed

    private void miAddSectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAddSectionActionPerformed
        final String secName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_section_name.message"), getLocaleMessage("admin.section.title"), 3, null, null, "");
        if (secName == null) {
            return;
        }
        if (secName.isEmpty()) {
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.section_empty.error"), getLocaleMessage("admin.section.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ServerProps.getInstance().getSection(secName) != null) {
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.section_dublicate.error"), getLocaleMessage("admin.section.title"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        final ServerProps.Section section = ServerProps.getInstance().addSection(secName);
        sectionsList.setModel(new DefaultComboBoxModel(ServerProps.getInstance().getSections().toArray()));
        sectionsList.setSelectedValue(section, true);
    }//GEN-LAST:event_miAddSectionActionPerformed

    private void miDeleteSectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miDeleteSectionActionPerformed
        if (sectionsList.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    java.text.MessageFormat.format(getLocaleMessage("admin.del_section_asc.msg"), new Object[]{sectionsList.getSelectedValue().toString()}),
                    getLocaleMessage("admin.section.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }

            final int del = sectionsList.getSelectedIndex();
            final int col = sectionsList.getModel().getSize();

            final ServerProps.Section section = sectionsList.getSelectedValue();
            ServerProps.getInstance().deleteSection(section.getName());
            sectionsList.setModel(new DefaultComboBoxModel(ServerProps.getInstance().getSections().toArray()));

            if (col != 1) {
                if (col == del + 1) {
                    sectionsList.setSelectedIndex(del - 1);
                } else if (col > del + 1) {
                    sectionsList.setSelectedIndex(del);
                }
            }
        }
    }//GEN-LAST:event_miDeleteSectionActionPerformed

    private void miCopyServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCopyServiceActionPerformed
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && !service.isRoot()) {
            serviceForCopy = service;
            serviceForCut = null;
        }
    }//GEN-LAST:event_miCopyServiceActionPerformed

    private QService serviceForCopy = null;
    private QService serviceForCut = null;

    private void miPasteServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miPasteServiceActionPerformed
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service == null) {
            return;
        }

        if (serviceForCut != null) {
            if (service.getName().equalsIgnoreCase(serviceForCut.getName()) || service.getName().equalsIgnoreCase(serviceForCut.getParent().getName())) {
                return;
            }
            ((QServiceTree) treeServices.getModel()).moveNode(serviceForCut, service, 0);
            //подсветим перенесенный сервис
            final TreeNode[] nodes = QServiceTree.getInstance().getPathToRoot(serviceForCut);
            final TreePath path = new TreePath(nodes);
            treeServices.scrollPathToVisible(path);
            treeServices.setSelectionPath(path);
            // родительскую услугу к новой услуге нужно исключить из списка привязанных к юзерам, т.к. она стала группой
            deleteServiceFromUsers(service);
            QLog.l().logger().debug("Перенесена услуга \"" + serviceForCut.getName() + "\" в группу \"" + service.getName() + "\"");
            serviceForCut = null;
        }

        if (serviceForCopy != null) {
            if (service.getName().equalsIgnoreCase(serviceForCopy.getName()) || service.getName().equalsIgnoreCase(serviceForCopy.getParent().getName())) {
                return;
            }
            // Созданим новую услугу и добавим ее в модель
            final QService newService = new QService();
            newService.setName(serviceForCopy.getName() + "-copy" + (System.currentTimeMillis() % 1000));
            newService.setDescription(serviceForCopy.getDescription());
            newService.setDayLimit(serviceForCopy.getDayLimit());
            newService.setDuration(serviceForCopy.getDuration());
            newService.setStatus(serviceForCopy.getStatus());
            newService.setSoundTemplate(serviceForCopy.getSoundTemplate());
            newService.setAdvanceTimePeriod(serviceForCopy.getAdvanceTimePeriod());
            newService.setAdvanceLimitPeriod(serviceForCopy.getAdvanceLimitPeriod());
            newService.setAdvanceLinit(serviceForCopy.getAdvanceLimit());
            newService.setCalendar(serviceForCopy.getCalendar());
            newService.setCountPerDay(serviceForCopy.getCountPerDay());
            newService.setSchedule(serviceForCopy.getSchedule());
            newService.setButtonText(serviceForCopy.getButtonText());
            //проставим букавку
            newService.setPrefix(serviceForCopy.getPrefix());
            newService.setPreInfoHtml(serviceForCopy.getPreInfoHtml());
            newService.setPreInfoPrintText(serviceForCopy.getPreInfoPrintText());

            newService.setButB(serviceForCopy.getButB());
            newService.setButH(serviceForCopy.getButH());
            newService.setButX(serviceForCopy.getButX());
            newService.setButY(serviceForCopy.getButY());

            newService.setEnable(serviceForCopy.getEnable());
            newService.setExpectation(serviceForCopy.getExpectation());

            newService.setInput_caption(serviceForCopy.getInput_caption());
            newService.setInput_required(serviceForCopy.getInput_required());
            newService.setTicketText(serviceForCopy.getTicketText());
            newService.setTempReasonUnavailable(serviceForCopy.getTempReasonUnavailable());

            newService.setResult_required(serviceForCopy.getResult_required());
            newService.setPoint(serviceForCopy.getPoint());
            newService.setLink(serviceForCopy.getLink());

            final Set<QServiceLang> langs = new HashSet<>();
            serviceForCopy.getLangs().stream().forEach((lang) -> {
                QServiceLang clon = new QServiceLang();
                clon.setButtonText(lang.getButtonText());
                clon.setDescription(lang.getDescription());
                clon.setInput_caption(lang.getInput_caption());
                clon.setLang(lang.getLang());
                clon.setName(lang.getName());
                clon.setPreInfoHtml(lang.getPreInfoHtml());
                clon.setPreInfoPrintText(lang.getPreInfoPrintText());
                clon.setTicketText(lang.getTicketText());
                clon.setService(newService);
                langs.add(clon);
            });
            newService.setLangs(langs);

            QServiceTree.getInstance().insertNodeInto(newService, service, 0);

            // скопированную услугу обрабатывают те же узеры
            QUserList.getInstance().getItems().forEach(user -> {
                if (user.hasService(serviceForCopy.getId())) {
                    user.addPlanService(newService);
                }
            });

            //подсветим перенесенный сервис
            final TreeNode[] nodes = QServiceTree.getInstance().getPathToRoot(newService);
            final TreePath path = new TreePath(nodes);
            treeServices.scrollPathToVisible(path);
            treeServices.setSelectionPath(path);

            // родительскую услугу к новой услуге нужно исключить из списка привязанных к юзерам, т.к. она стала группой
            deleteServiceFromUsers(service);
            serviceForCopy = null;
            QLog.l().logger().debug("Скопирована услуга \"" + newService + "\" в группу \"" + service.getName() + "\"");
        }
    }//GEN-LAST:event_miPasteServiceActionPerformed

    private void miCutServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCutServiceActionPerformed
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && !service.isRoot()) {
            serviceForCut = service;
            serviceForCopy = null;
        }
    }//GEN-LAST:event_miCutServiceActionPerformed

    private void popupServicesPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupServicesPopupMenuWillBecomeVisible
        miPasteService.setEnabled(serviceForCopy != null || serviceForCut != null);
    }//GEN-LAST:event_popupServicesPopupMenuWillBecomeVisible

    private void rbmClassicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbmClassicActionPerformed
        ServerProps.getInstance().saveOrUpdateProperty(IIndicatorBoard.SECTION, IIndicatorBoard.PARAMETER, rbmClassic.isSelected() ? IIndicatorBoard.CLASSIC : IIndicatorBoard.HTML, "Type of main tablo");
    }//GEN-LAST:event_rbmClassicActionPerformed

    private void btnReloadPropsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadPropsActionPerformed
        ServerProps.getInstance().reloadProperties();
        sectionsList.setModel(new DefaultComboBoxModel(ServerProps.getInstance().getSections().toArray()));
        if (!ServerProps.getInstance().getSections().isEmpty()) {
            sectionsList.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnReloadPropsActionPerformed

    private void sendPager() {
        if (forPager != null) {
            final Thread t = new Thread(() -> {
                forPager.sendData();
            });
            t.setDaemon(true);
            t.start();
        }
    }

    @Action
    public void changeServicePriority() {
        final QPlanService plan = (QPlanService) listUserService.getSelectedValue();
        if (plan == null) {
            return;
        }
        // тут надо фокус перекинуть, чтоб названия услуги изменилось с учетом приоритета.
        listUserService.requestFocus();
        listUserService.requestFocusInWindow();
        final String name = (String) JOptionPane.showInputDialog(this,
                getLocaleMessage("admin.select_priority.message"),
                getLocaleMessage("admin.select_priority.title"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                Uses.get_COEFF_WORD().values().toArray(),
                Uses.get_COEFF_WORD().values().toArray()[Uses.get_COEFF_WORD().values().toArray().length > plan.getCoefficient() ? plan.getCoefficient() : 1]);
        //Если не выбрали, то выходим
        if (name != null) {
            for (int i = 0; i < Uses.get_COEFF_WORD().size(); i++) {
                if (name.equals(Uses.get_COEFF_WORD().get(i))) {
                    plan.setCoefficient(i);
                }
            }
        }
    }

    @Action
    public void changeFlexiblePriorityAbility() {
        final QPlanService plan = (QPlanService) listUserService.getSelectedValue();
        if (plan == null) {
            return;
        }
        // тут надо фокус перекинуть, чтоб названия услуги изменилось с учетом приоритета.
        listUserService.requestFocus();
        listUserService.requestFocusInWindow();
        plan.setFlexible_coef(!plan.getFlexible_coef());
    }

    @Action
    public void setUpdateServiceFire() {
        final QPlanService plan = (QPlanService) listUserService.getSelectedValue();
        if (plan == null) {
            return;
        }
        final String res = NetCommander.setServiseFire(new ServerNetProperty(), plan.getService().getId(), plan.getUser().getId(), plan.getCoefficient());
        JOptionPane.showMessageDialog(this, res, getLocaleMessage("admin.add_service_to_user.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void deleteUpdateServiceFire() {
        final QPlanService plan = (QPlanService) listUserService.getSelectedValue();
        if (plan == null) {
            return;
        }
        final String res = NetCommander.deleteServiseFire(new ServerNetProperty(), plan.getService().getId(), plan.getUser().getId());
        JOptionPane.showMessageDialog(this, res, getLocaleMessage("admin.remove_service_to_user.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void getHelp() {
    }

    @Action
    public void editService() {
        final TreePath selectedPath = treeServices.getSelectionPath();
        if (selectedPath != null) {
            final QService service = (QService) selectedPath.getLastPathComponent();
            FServiceChangeDialod.changeService(this, true, service, (ComboBoxModel) listSchedule.getModel(), (ComboBoxModel) listCalendar.getModel());
            showServiceInfo(service);
        }
    }

    @Action
    public void getAbout() {
        FAbout.showAbout(this, true, ServerProps.getInstance().getProps().getVersion());
    }

    @Action
    public void standAdvance() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && service.isLeaf()) {

            String inputData = null;
            if (service.getInput_required()) {
                inputData = (String) JOptionPane.showInputDialog(this, service.getInput_caption(), "***", 3, null, null, "");
                if (inputData == null) {
                    return;
                }
            }

            String comments = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.comments"), "***", 3, null, null, "");
            if (inputData == null) {
                inputData = "";
            }

            final QAdvanceCustomer res;
            try {
                res = FAdvanceCalendar.showCalendar(this, true, new ServerNetProperty(), service, false, 0, -1, inputData, comments);
            } catch (Exception ex) {
                throw new ClientException(getLocaleMessage("admin.send_cmd_adv.err") + " " + ex);
            }
            if (res == null) {
                return;
            }
            // печатаем результат
            new Thread(() -> {
                FWelcome.printTicketAdvance(res, ((QService) treeServices.getModel().getRoot()).getTextToLocale(QService.Field.NAME));
            }).start();

            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.client_adv_dialog.msg_1") + " \"" + service.getName() + "\". " + getLocaleMessage("admin.client_adv_dialog.msg_2") + " \"" + res.getId() + "\".", getLocaleMessage("admin.client_adv_dialog.title"), JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * Редоктор для главного табло. Singleton.
     */
    private AFBoardRedactor board = null;
    private static String adr;
    private static Integer port;

    @Action
    public void editMainBoard() throws IOException {
        QLog.l().logger().info("Открыть редактор главного табло.");
        final ServerNetProperty servProp = new ServerNetProperty();
        try {
            if (board == null) {
                adr = servProp.getAddress().getHostAddress();
                port = servProp.getPort();
                board = MainBoard.getInstance().getRedactor();
                board.setParams(servProp);
            } else if (!servProp.getAddress().getHostAddress().equals(adr)
                    || !servProp.getPort().equals(port)) {
                board.setParams(servProp);
                adr = servProp.getAddress().getHostAddress();
                port = servProp.getPort();
            }
        } catch (Exception e) {
            board = null;
            ClientWarning.showWarning(getLocaleMessage("admin.open_editor.wern") + "\n" + e);
            return;
        }
        // Отцентирируем
        Uses.setLocation(board);
        // Покажем
        board.setVisible(true);
    }    //*****************************************Запрос в ручную*******************************************************************

    @Action
    public void sendMessage() {
        FMessager.getMessager(this, ServerProps.getInstance().getProps().getClientPort(), listUsers.getModel(), treeServices.getModel());
    }

    @Action
    public void addRespItem() {
        // Запросим название юзера и если оно уникально, то примем
        String respName = getLocaleMessage("admin.add_resp_dialog.info");
        boolean flag = true;
        while (flag) {
            respName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_resp_dialog.message"), getLocaleMessage("admin.add_resp_dialog.title"), 3, null, null, respName);
            if (respName == null) {
                return;
            }
            if ("".equals(respName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_resp_dialog.err1.message"), getLocaleMessage("admin.add_resp_dialog.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (respName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_resp_dialog.err2.message"), getLocaleMessage("admin.add_resp_dialog.err2.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (respName.length() > 100) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_resp_dialog.err3.message"), getLocaleMessage("admin.add_resp_dialog.err3.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        QLog.l().logger().debug("Добавляем отзыв \"" + respName + "\"");
        final QRespItem item = new QRespItem();
        item.setName(respName);
        item.setInput_required(false);
        item.setInput_caption("<html>Add your comment:");
        item.setHTMLText("<html><b><p align=center><span style='font-size:20.0pt;color:green'>" + respName + "</span></b>");

        final QRespItem parentItem = (QRespItem) treeResp.getLastSelectedPathComponent();
        ((QResponseTree) treeResp.getModel()).insertNodeInto(item, parentItem, parentItem.getChildCount());
        final TreeNode[] nodes = ((QResponseTree) treeResp.getModel()).getPathToRoot(item);
        final TreePath path = new TreePath(nodes);
        treeResp.scrollPathToVisible(path);
        treeResp.setSelectionPath(path);
    }

    @Action
    public void deleteRespItem() {
        final QRespItem item = (QRespItem) treeResp.getLastSelectedPathComponent();
        if (item != null && !item.isRoot()) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.resp_delete.message") + " \"" + item.getName() + "\"?",
                    getLocaleMessage("admin.resp_delete.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем отзыв \"" + item.getName() + "\"");
            // Удалим сам узел
            final int del = item.getParent().getIndex(item);
            final int col = item.getParent().getChildCount();
            QResponseTree.getInstance().removeNodeFromParent(item);

            // Выделение в узла в дереве
            if (col == 1) {
                treeInfo.setSelectionPath(new TreePath(((QInfoTree) treeInfo.getModel()).getPathToRoot(item.getParent())));
            } else if (col == del + 1) {
                treeInfo.setSelectionPath(new TreePath(((QInfoTree) treeInfo.getModel()).getPathToRoot(item.getParent().getChildAt(del - 1))));
            } else if (col > del + 1) {
                treeInfo.setSelectionPath(new TreePath(((QInfoTree) treeInfo.getModel()).getPathToRoot(item.getParent().getChildAt(del))));
            }
            QLog.l().logger().debug("Удален отзыв \"" + item.getName() + "\" из группы \"" + item.getParent().getName() + "\"");
        }
    }

    @Action
    public void addSchedule() {
        // Запросим название плана и если оно уникально, то примем
        String scheduleName = getLocaleMessage("admin.add_work_plan_dialog.info");
        boolean flag = true;
        while (flag) {
            scheduleName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_work_plan_dialog.message"), getLocaleMessage("admin.add_work_plan_dialog.title"), 3, null, null, scheduleName);
            if (scheduleName == null) {
                return;
            }
            if ("".equals(scheduleName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_work_plan_dialog.err1.message"), getLocaleMessage("admin.add_work_plan_dialog.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (scheduleName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_work_plan_dialog.err2.message"), getLocaleMessage("admin.add_work_plan_dialog.err2.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (scheduleName.length() > 150) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_work_plan_dialog.err3.message"), getLocaleMessage("admin.add_work_plan_dialog.err3.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        QLog.l().logger().debug("Добавляем отзыв \"" + scheduleName + "\"");
        final QSchedule item = new QSchedule();
        item.setName(scheduleName);
        item.setType(0);
        QScheduleList.getInstance().addElement(item);
        listSchedule.setSelectedValue(item, true);
    }

    @Action
    public void deleteSchedule() {
        if (listSchedule.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.work_plan_delete.message") + " \"" + ((QSchedule) listSchedule.getSelectedValue()).getName() + "\"?",
                    getLocaleMessage("admin.work_plan_delete.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем план \"" + ((QSchedule) listSchedule.getSelectedValue()).getName() + "\"");

            final int del = listSchedule.getSelectedIndex();
            final QScheduleList m = (QScheduleList) listSchedule.getModel();
            final int col = m.getSize();

            final QSchedule item = (QSchedule) listSchedule.getSelectedValue();

            QServiceTree.getInstance().getNodes().stream().filter((service) -> (item.equals(service.getSchedule()))).forEach((service) -> {
                service.setSchedule(null);
            });

            // Удалим это расписание как специальное у календарей.
            QCalendarList.getInstance().getItems().forEach(calc -> {
                final LinkedList<QSpecSchedule> fordel = new LinkedList<>();
                calc.getSpecSchedules().forEach(sps -> {
                    if (sps.getSchedule().getId().equals(item.getId())) {
                        fordel.add(sps);
                    }
                });
                calc.getSpecSchedules().removeAll(fordel);
            });
            calendarListChange();

            QScheduleList.getInstance().removeElement(item);

            if (col != 1) {
                if (col == del + 1) {
                    listSchedule.setSelectedValue(m.getElementAt(del - 1), true);
                } else if (col > del + 1) {
                    listSchedule.setSelectedValue(m.getElementAt(del), true);
                }
            }
        }
    }

    @Action
    public void editSchedule() {
        final QSchedule item = (QSchedule) listSchedule.getSelectedValue();
        if (item != null) {
            FScheduleChangeDialod.changeSchedule(this, true, item);
            scheduleListChange();
        }
    }

    @Action
    public void addResult() {
        String resultText = "";
        boolean flag = true;
        while (flag) {
            resultText = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_result_dialog.message"), getLocaleMessage("admin.add_result_dialog.title"), 3, null, null, resultText);
            if (resultText == null) {
                return;
            }
            if ("".equals(resultText)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_result_dialog.err1.message"), getLocaleMessage("admin.add_result_dialog.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (resultText.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_result_dialog.err2.message"), getLocaleMessage("admin.add_result_dialog.err2.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (resultText.length() > 150) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_result_dialog.err3.message"), getLocaleMessage("admin.add_result_dialog.err3.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        QLog.l().logger().debug("Добавляем результат \"" + resultText + "\"");
        final QResult item = new QResult();
        item.setName(resultText);
        QResultList.getInstance().addElement(item);
        listResults.setSelectedValue(item, true);
    }

    @Action
    public void deleteResult() {
        if (listResults.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.result_delete.message") + " \"" + ((QResult) listResults.getSelectedValue()).getName() + "\"?",
                    getLocaleMessage("admin.result_delete.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем результат \"" + ((QResult) listResults.getSelectedValue()).getName() + "\"");

            final int del = listResults.getSelectedIndex();
            final QResultList m = (QResultList) listResults.getModel();
            final int col = m.getSize();

            final QResult item = (QResult) listResults.getSelectedValue();
            QResultList.getInstance().removeElement(item);

            if (col != 1) {
                if (col == del + 1) {
                    listResults.setSelectedValue(m.getElementAt(del - 1), true);
                } else if (col > del + 1) {
                    listResults.setSelectedValue(m.getElementAt(del), true);
                }
            }
        }
    }

    @Action
    public void addCalendar() {
        // Запросим название календаря и если оно уникально, то примем
        String calendarName = getLocaleMessage("admin.add_calendar_dialog.info");
        boolean flag = true;
        while (flag) {
            calendarName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_calendar_dialog.message"), getLocaleMessage("admin.add_calendar_dialog.title"), 3, null, null, calendarName);
            if (calendarName == null) {
                return;
            }
            if ("".equals(calendarName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_calendar_dialog.err1.message"), getLocaleMessage("admin.add_calendar_dialog.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (calendarName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_calendar_dialog.err2.message"), getLocaleMessage("admin.add_calendar_dialog.err2.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (calendarName.length() > 150) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_calendar_dialog.err3.message"), getLocaleMessage("admin.add_calendar_dialog.err3.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        QLog.l().logger().debug("Добавляем календарь \"" + calendarName + "\"");
        final QCalendar item = new QCalendar();
        item.setName(calendarName);
        QCalendarList.getInstance().addElement(item);
        listCalendar.setSelectedValue(item, true);
    }

    @Action
    public void deleteCalendar() {
        if (listCalendar.getSelectedIndex() != -1
                && (((QCalendar) listCalendar.getSelectedValue()).getId() == null || ((QCalendar) listCalendar.getSelectedValue()).getId() != 1)) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.calendar_delete.message") + " \"" + ((QCalendar) listCalendar.getSelectedValue()).getName() + "\"?",
                    getLocaleMessage("admin.calendar_delete.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем календарь \"" + ((QCalendar) listCalendar.getSelectedValue()).getName() + "\"");

            final int del = listCalendar.getSelectedIndex();
            final QCalendarList m = (QCalendarList) listCalendar.getModel();
            final int col = m.getSize();

            final QCalendar item = (QCalendar) listCalendar.getSelectedValue();

            QServiceTree.getInstance().getNodes().stream().filter((service) -> (item.equals(service.getCalendar()))).forEach((service) -> {
                service.setCalendar(null);
            });

            QCalendarList.getInstance().removeElement(item);

            if (col != 1) {
                if (col == del + 1) {
                    listCalendar.setSelectedValue(m.getElementAt(del - 1), true);
                } else if (col > del + 1) {
                    listCalendar.setSelectedValue(m.getElementAt(del), true);
                }
            }
        }
    }

    @Action
    public void dropCalendarSelection() {
        final CalendarTableModel model = (CalendarTableModel) tableCalendar.getModel();
        model.dropCalendar((Integer) (spinCalendarYear.getValue()));
    }

    @Action
    public void checkSaturday() {
        final CalendarTableModel model = (CalendarTableModel) tableCalendar.getModel();
        model.checkSaturday((Integer) (spinCalendarYear.getValue()));
    }

    @Action
    public void checkSundays() {
        final CalendarTableModel model = (CalendarTableModel) tableCalendar.getModel();
        model.checkSunday((Integer) (spinCalendarYear.getValue()));
    }

    @Action
    public void saveCalendar() {
        final CalendarTableModel model = (CalendarTableModel) tableCalendar.getModel();
        model.save();
        JOptionPane.showMessageDialog(this, getLocaleMessage("admin.action.save_calensar.message"), getLocaleMessage("admin.action.save_calensar.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    @Action
    public void changePriority() {
        final String num = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.action.change_priority.num.message"), getLocaleMessage("admin.action.change_priority.num.title"), 3, null, null, "");
        if (num != null) {
            final String name = (String) JOptionPane.showInputDialog(this,
                    getLocaleMessage("admin.action.change_priority.get.message"),
                    getLocaleMessage("admin.action.change_priority.get.title"),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Uses.get_PRIORITYS_WORD().values().toArray(),
                    Uses.get_PRIORITYS_WORD().values().toArray()[1]);
            //Если не выбрали, то выходим
            if (name != null) {
                for (int i = 0; i < Uses.get_PRIORITYS_WORD().size(); i++) {
                    if (name.equals(Uses.get_PRIORITYS_WORD().get(i))) {
                        JOptionPane.showMessageDialog(this, NetCommander.setCustomerPriority(new ServerNetProperty(), i, num), getLocaleMessage("admin.action.change_priority.title"), JOptionPane.INFORMATION_MESSAGE);

                    }
                }
            }
        }
    }

    @Action
    public void checkClient() {
        final String num = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.action.change_priority.num.message"), getLocaleMessage("admin.action.change_priority.num.title"), 3, null, null, "");
        if (num != null) {
            JOptionPane.showMessageDialog(this, NetCommander.checkCustomerNumber(new ServerNetProperty(), num), getLocaleMessage("admin.action.change_priority.num.title"), JOptionPane.INFORMATION_MESSAGE);
        }
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
    public void addBreakToList() {
        // Запросим название плана и если оно уникально, то примем
        String breaksName = getLocaleMessage("admin.add_breaks_dialog.info");
        boolean flag = true;
        while (flag) {
            breaksName = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.add_breaks_dialog.message"), getLocaleMessage("admin.add_breaks_dialog.title"), 3, null, null, breaksName);
            if (breaksName == null) {
                return;
            }
            for (QBreaks qb : QBreaksList.getInstance().getItems()) {
                if (qb.getName().equalsIgnoreCase(breaksName)) {
                    JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.enter_sute_mark.err2.title"), getLocaleMessage("admin.enter_sute_mark.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if ("".equals(breaksName)) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_breaks_dialog.err1.message"), getLocaleMessage("admin.add_work_plan_dialog.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (breaksName.indexOf('\"') != -1) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_breaks_dialog.err2.message"), getLocaleMessage("admin.add_work_plan_dialog.err2.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (breaksName.length() > 150) {
                JOptionPane.showConfirmDialog(this, getLocaleMessage("admin.add_breaks_dialog.err3.message"), getLocaleMessage("admin.add_work_plan_dialog.err3.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else {
                flag = false;
            }
        }
        QLog.l().logger().debug("Добавляем перерывы \"" + breaksName + "\"");
        final QBreaks item = new QBreaks();
        item.setName(breaksName);
        QBreaksList.getInstance().addElement(item);
        listBreaks.setSelectedValue(item, true);
    }

    @Action
    public void deleteBreakFromList() {
        if (listBreaks.getSelectedIndex() != -1) {
            if (JOptionPane.showConfirmDialog(this,
                    getLocaleMessage("admin.breaks_delete.message") + " \"" + ((QBreaks) listBreaks.getSelectedValue()).getName() + "\"?",
                    getLocaleMessage("admin.breaks_delete.title"),
                    JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            QLog.l().logger().debug("Удаляем перерывы \"" + ((QBreaks) listBreaks.getSelectedValue()).getName() + "\"");

            final int del = listBreaks.getSelectedIndex();
            final QBreaksList m = (QBreaksList) listBreaks.getModel();
            final int col = m.getSize();

            final QBreaks item = (QBreaks) listBreaks.getSelectedValue();

            // Уберем удаленные перерывы у расписаний
            boolean f = false;
            for (QSchedule schedule : QScheduleList.getInstance().getItems()) {
                if (item.equals(schedule.getBreaks_1())) {
                    schedule.setBreaks_1(null);
                    f = true;
                }
                if (item.equals(schedule.getBreaks_2())) {
                    schedule.setBreaks_2(null);
                    f = true;
                }
                if (item.equals(schedule.getBreaks_3())) {
                    schedule.setBreaks_3(null);
                    f = true;
                }
                if (item.equals(schedule.getBreaks_4())) {
                    schedule.setBreaks_4(null);
                    f = true;
                }
                if (item.equals(schedule.getBreaks_5())) {
                    schedule.setBreaks_5(null);
                    f = true;
                }
                if (item.equals(schedule.getBreaks_6())) {
                    schedule.setBreaks_6(null);
                    f = true;
                }
                if (item.equals(schedule.getBreaks_7())) {
                    schedule.setBreaks_7(null);
                    f = true;
                }

            }
            if (f) {
                scheduleListChange();
            }

            // Подотрать все прикрепленые интервалы не нужно. Они должны сами подтереться по констрейнту.
            // подотрем сам список
            QBreaksList.getInstance().removeElement(item);

            if (col != 1) {
                if (col == del + 1) {
                    listBreaks.setSelectedValue(m.getElementAt(del - 1), true);
                } else if (col > del + 1) {
                    listBreaks.setSelectedValue(m.getElementAt(del), true);
                }
            }
        }
    }

    @Action
    public void editBreak() {
        final QBreaks item = (QBreaks) listBreaks.getSelectedValue();
        if (item != null) {
            FBreaksChangeDialog.changeSchedule(this, true, item);
            scheduleListChange();
        }
    }

    @Action
    public void editLangs() {
        final TreePath selectedPath = treeServices.getSelectionPath();
        if (selectedPath != null) {
            final QService service = (QService) selectedPath.getLastPathComponent();
            FServiceLangList.changeServiceLangList(this, true, service);
            showServiceInfo(service);
        }
    }

    @Action
    public void setDisableService() {
        final TreePath selectedPath = treeServices.getSelectionPath();
        if (selectedPath != null) {
            final QService service = (QService) selectedPath.getLastPathComponent();
            final String name = (String) JOptionPane.showInputDialog(this,
                    getLocaleMessage("admin.select_ability.message") + " \"" + service.getName() + "\"",
                    getLocaleMessage("admin.select_ability.title"),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{getLocaleMessage("admin.service_ability.yes"), getLocaleMessage("admin.service_ability.no")},
                    null);
            //Если не выбрали, то выходим
            if (name != null) {
                if (name.equalsIgnoreCase(getLocaleMessage("admin.service_ability.yes"))) {
                    NetCommander.changeTempAvailableService(new ServerNetProperty(), service.getId(), "");
                } else {
                    final String mess = (String) JOptionPane.showInputDialog(this,
                            getLocaleMessage("admin.ability.enter_reason"),
                            getLocaleMessage("admin.select_ability.title"),
                            JOptionPane.QUESTION_MESSAGE);
                    if (mess != null) {
                        NetCommander.changeTempAvailableService(new ServerNetProperty(), service.getId(), mess);
                    } else {
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this,
                        getLocaleMessage("admin.select_ability.message") + " " + service.getName() + " \"" + name + "\"",
                        getLocaleMessage("admin.select_ability.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    private String pagerUrl = null;
    private static final String REGEXP_URL = "\\b(https?|ftp|file|mailto):(//|[-a-zA-Z0-9_\\.]+@)+[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern P = Pattern.compile(REGEXP_URL);

    public static class Answer {

        public Answer() {
        }

        public Answer(List<PagerData> data, String currVersion) {
            this.data = data;
            this.currVersion = currVersion;
        }
        @Expose
        @SerializedName("curr_version")
        private String currVersion;

        public String getCurrVersion() {
            return currVersion;
        }

        public void setCurrVersion(String currVersion) {
            this.currVersion = currVersion;
        }
        @Expose
        @SerializedName("urlfun")
        private String urlfun;

        public String getUrlfun() {
            return urlfun;
        }

        public void setUrlfun(String urlfun) {
            this.urlfun = urlfun;
        }

        public String getUrlFunRnd() {
            if (urlfun == null || urlfun.isEmpty()) {
                return null;
            }
            String[] ss = urlfun.split("#");
            return ss[new Random().nextInt(ss.length)];
        }
        @Expose
        @SerializedName("data")
        private List<PagerData> data;

        public List<PagerData> getData() {
            return data;
        }

        public void setData(List<PagerData> data) {
            this.data = data;
        }

        public void start() {
            if (!already) {
                for (int i = 0; i < 2000; i++) {
                    if (form == null || form.panelPager == null) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        showData(false);
                    }
                }
            }
        }
        private boolean already = false;
        private PagerData pd;

        private void showData(boolean forse) {
            if (!already || forse) {
                already = true;
                final int d = (int) (Math.random() * data.size());
                pd = null;
                form.rbPager1.setSelected(false);
                form.rbPager2.setSelected(false);
                form.rbPager3.setSelected(false);
                pd = data.get(d);
                form.labelPager.setText(pd.textData);
                final Matcher m = P.matcher(pd.textData);

                form.pagerUrl = null;
                while (m.find()) {
                    form.pagerUrl = pd.textData.substring(m.start(0), m.end(0));
                }

                form.labelPagerCaptionCombo.setText(pd.quizCaption);
                form.labelPagerCaptionEdit.setText(pd.quizCaption);
                switch (pd.dataType) {
                    case 0:
                        form.panelEditPager.setVisible(false);
                        form.panelPagerCombo.setVisible(false);
                        form.panelPagerRadio.setVisible(false);
                        break;
                    case 1:
                        form.panelEditPager.setVisible(false);
                        form.panelPagerCombo.setVisible(!pd.checked && pd.pagerQuizItemsList.size() > 3);
                        form.panelPagerRadio.setVisible(!pd.checked && pd.pagerQuizItemsList.size() < 4);
                        if (pd.pagerQuizItemsList.size() > 3) {
                            final Object[] ar = ArrayUtils.addAll(new PagerQuizItems[1], pd.pagerQuizItemsList.toArray());
                            form.comboBoxPager.setModel(new DefaultComboBoxModel(ar));
                        } else {
                            form.rbPager1.setText(pd.pagerQuizItemsList.get(0).itemText);
                            form.rbPager2.setText(pd.pagerQuizItemsList.get(1).itemText);
                            if (pd.pagerQuizItemsList.size() == 2) {
                                form.rbPager3.setVisible(false);
                            } else {
                                form.rbPager3.setVisible(true);
                                form.rbPager3.setText(pd.pagerQuizItemsList.get(2).itemText);
                            }
                        }
                        break;
                    case 2:
                        form.panelEditPager.setVisible(!pd.checked);
                        form.panelPagerCombo.setVisible(false);
                        form.panelPagerRadio.setVisible(false);
                        break;
                    default:
                        throw new AssertionError();
                }
                form.panelPager.setVisible(true);
                final String uf = getUrlFunRnd();
                if (uf != null && !uf.isEmpty()) {
                    if (tabbi == 0 && Math.random() < 0.1) {
                        try {
                            ImageIcon ii = new ImageIcon(new URL(uf));
                            if (ii.getIconHeight() < 1 || ii.getIconWidth() < 1) {
                                form.tabbedPaneMain.remove(form.tabHide);
                            } else {
                                form.labHidePic.setIcon(ii);
                                form.tabbedPaneMain.addTab(getLocaleMessage("tabHideTitle"), form.tabHide);
                            }
                        } catch (MalformedURLException ex) {
                            form.tabbedPaneMain.remove(form.tabHide);
                        }
                    }
                    tabbi = 2;
                } else {
                    form.tabbedPaneMain.remove(form.tabHide);
                }

                if (!FAbout.VERSION_.equalsIgnoreCase(currVersion) && !forse) {
                    form.setTitle(form.getTitle() + "  " + getLocaleMessage("qsys.new_ver") + " " + currVersion + " " + getLocaleMessage("qsys.available"));
                }
            }
        }
        int tabbi = 0;

        public void sendData() {
            if (pd != null) {
                String paraqms = "&dataid=" + pd.id;
                switch (pd.dataType) {
                    case 0:
                        break;
                    case 1:
                        if (pd.pagerQuizItemsList.size() > 3) {
                            if (form.comboBoxPager.getSelectedItem() == null) {
                                return;
                            }
                            paraqms = paraqms + "&quizid=" + ((PagerQuizItems) (form.comboBoxPager.getSelectedItem())).id;
                        } else {
                            String sel = "";
                            if (form.rbPager1.isSelected()) {
                                sel = form.rbPager1.getText();
                            } else if (form.rbPager2.isSelected()) {
                                sel = form.rbPager2.getText();
                            } else if (form.rbPager3.isSelected()) {
                                sel = form.rbPager3.getText();
                            }
                            for (PagerQuizItems q : pd.pagerQuizItemsList) {
                                if (q.itemText.equals(sel)) {
                                    paraqms = paraqms + "&quizid=" + q.id;
                                    break;
                                }
                            }
                        }
                        break;
                    case 2:
                        try {
                            paraqms = paraqms + "&inputdata=" + new BCodec().encode(URLEncoder.encode(form.textFieldPager.getText().length() > 545 ? form.textFieldPager.getText().substring(0, 545) : form.textFieldPager.getText(), "utf8"), "utf8");
                        } catch (EncoderException | UnsupportedEncodingException ex) {
                        }
                        break;
                    default:
                        throw new AssertionError();
                }
                try {
                    //http://localhost:8080/qskyapi/setpagerdata?qsysver=1.3.1&dataid=3&inputdata=Hello%20world!
                    //http://localhost:8080/qskyapi/setpagerdata?qsysver=1.3.1&dataid=2&quizid=3
                    final URL url = new URL(PAGER_URL + "/qskyapi/setpagerdata?qsysver=" + FAbout.VERSION_ + "&qplugins=" + getMac() + "-" + getStat() + paraqms);
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Java bot");
                    conn.connect();
                    final int code = conn.getResponseCode();
                    if (code != 200) {
                        System.err.println("Strange! Pager not enabled. Returned code not 200.");
                    } else {
                        pd.checked = true;
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    System.err.println("Pager not enabled.");
                    form.bgPager.clearSelection();
                    return;
                }
                showData(true);
                form.bgPager.clearSelection();
            }
        }
    }

    public static class PagerData {

        @Expose
        @SerializedName("id")
        private Long id;
        @Expose
        @SerializedName("type")
        private int dataType;
        @Expose
        @SerializedName("text")
        private String textData;
        @Expose
        @SerializedName("qcap")
        private String quizCaption;
        @Expose
        @SerializedName("quis_items")
        private List<PagerQuizItems> pagerQuizItemsList;

        public PagerData() {
        }
        public boolean checked = false;
    }

    public static class PagerQuizItems {

        @Expose
        @SerializedName("id")
        private Long id;
        @Expose
        @SerializedName("text")
        private String itemText;

        public PagerQuizItems() {
        }

        @Override
        public String toString() {
            return itemText;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgBoards;
    private javax.swing.ButtonGroup bgPager;
    private javax.swing.JButton btnAddProp;
    private javax.swing.JButton btnAddSection;
    private javax.swing.JButton btnDeleteSection;
    private javax.swing.JButton btnReloadProps;
    private javax.swing.JButton btnRemoveProp;
    private javax.swing.JButton btnWysResp1;
    private javax.swing.JButton butAddSpecSced;
    private javax.swing.JButton butDeleteSpecSced;
    private javax.swing.JButton butEditSpecSced;
    private javax.swing.JButton butWysInfo1;
    private javax.swing.JButton buttonAddCalendar;
    private javax.swing.JButton buttonCheckZoneBoardServ;
    private javax.swing.JButton buttonClientRequest;
    private javax.swing.JButton buttonCloudTest;
    private javax.swing.JButton buttonDeleteCalendar;
    private javax.swing.JButton buttonExportToCSV;
    private javax.swing.ButtonGroup buttonGroupKindNum;
    private javax.swing.ButtonGroup buttonGroupPoint;
    private javax.swing.ButtonGroup buttonGroupSource;
    private javax.swing.ButtonGroup buttonGroupVoice;
    private javax.swing.JButton buttonLock;
    private javax.swing.JButton buttonPagerEdit;
    private javax.swing.JButton buttonRefreshBan;
    private javax.swing.JButton buttonResetMainTablo;
    private javax.swing.JButton buttonRestart;
    private javax.swing.JButton buttonRestartServer;
    private javax.swing.JButton buttonScheduleAdd;
    private javax.swing.JButton buttonSchedulleDelete;
    private javax.swing.JButton buttonSendDataToSky;
    private javax.swing.JButton buttonServerRequest;
    private javax.swing.JButton buttonShutDown;
    private javax.swing.JButton buttonUnlock;
    private javax.swing.JCheckBox cbCommentForResp;
    private javax.swing.JCheckBox cbDropTicketsCnt;
    private javax.swing.JComboBox cbSeparateCSV;
    private javax.swing.JCheckBox chBoxBtnFreeDsn;
    private javax.swing.JCheckBox checkBoxAdmin;
    private javax.swing.JCheckBox checkBoxClientAuto;
    private javax.swing.JCheckBox checkBoxReport;
    private javax.swing.JCheckBox checkBoxServerAuto;
    private javax.swing.JComboBox comboBoxPager;
    private com.toedter.calendar.JDateChooser dateChooserFinishCsv;
    private com.toedter.calendar.JDateChooser dateChooserStartCsv;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem40;
    private javax.swing.JMenuItem jMenuItem41;
    private javax.swing.JMenuItem jMenuItem42;
    private javax.swing.JMenuItem jMenuItem43;
    private javax.swing.JMenuItem jMenuItem44;
    private javax.swing.JMenuItem jMenuItem45;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItemBagtracker;
    private javax.swing.JMenuItem jMenuItemEditUser;
    private javax.swing.JMenuItem jMenuItemForum;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane10;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JSplitPane jSplitPane6;
    private javax.swing.JSplitPane jSplitPane7;
    private javax.swing.JSplitPane jSplitPane8;
    private javax.swing.JSplitPane jSplitPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JPanel keyvaluePanel;
    private javax.swing.JLabel labExtPrior;
    private javax.swing.JLabel labHidePic;
    private javax.swing.JLabel labelButtonCaption;
    private javax.swing.JLabel labelInfoItem;
    private javax.swing.JLabel labelPager;
    private javax.swing.JLabel labelPagerCaptionCombo;
    private javax.swing.JLabel labelPagerCaptionEdit;
    private javax.swing.JLabel labelRespinse;
    private javax.swing.JLabel labelSchedule;
    private javax.swing.JLabel labelServerState;
    private javax.swing.JLabel labelServiceInfo;
    private javax.swing.JLabel labelWelcomeState;
    private javax.swing.JList listBan;
    private javax.swing.JList listBreaks;
    private javax.swing.JList listCalendar;
    private javax.swing.JList listPostponed;
    private javax.swing.JList listReposts;
    private javax.swing.JList listResults;
    private javax.swing.JList listSchedule;
    private javax.swing.JList listSpecSced;
    private javax.swing.JList listUserService;
    private javax.swing.JList listUsers;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JMenu menuBoards;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemHelp;
    private javax.swing.JMenu menuLangs;
    private javax.swing.JMenu menuServices;
    private javax.swing.JMenu menuUsers;
    private javax.swing.JMenuItem miAddSection;
    private javax.swing.JMenuItem miCopyService;
    private javax.swing.JMenuItem miCutService;
    private javax.swing.JMenuItem miDeleteSection;
    private javax.swing.JMenuItem miPasteService;
    private javax.swing.JMenuItem muAddProp;
    private javax.swing.JMenuItem muDeleteProp;
    private javax.swing.JPanel panelEditPager;
    private javax.swing.JPanel panelPager;
    private javax.swing.JPanel panelPagerCombo;
    private javax.swing.JPanel panelPagerRadio;
    private javax.swing.JPanel panelSpecSc;
    private javax.swing.JPasswordField passwordFieldUser;
    private javax.swing.JPopupMenu popupBreaks;
    private javax.swing.JPopupMenu popupCalendar;
    private javax.swing.JPopupMenu popupInfo;
    private javax.swing.JPopupMenu popupPlans;
    private javax.swing.JPopupMenu popupProps;
    private javax.swing.JPopupMenu popupResponse;
    private javax.swing.JPopupMenu popupResults;
    private javax.swing.JPopupMenu popupSections;
    private javax.swing.JPopupMenu popupServiceUser;
    private javax.swing.JPopupMenu popupServices;
    private javax.swing.JPopupMenu popupUser;
    private javax.swing.JPanel propsPanel;
    private javax.swing.JTable propsTable;
    private javax.swing.JRadioButton rbKindCommon;
    private javax.swing.JRadioButton rbKindPersonal;
    private javax.swing.JRadioButton rbPager1;
    private javax.swing.JRadioButton rbPager2;
    private javax.swing.JRadioButton rbPager3;
    private javax.swing.JRadioButtonMenuItem rbmClassic;
    private javax.swing.JRadioButtonMenuItem rbmHtml;
    private javax.swing.JPanel sectionPanel;
    private javax.swing.JList<ServerProps.Section> sectionsList;
    private javax.swing.JSpinner spinCalendarYear;
    private javax.swing.JSpinner spinExtPrior;
    private javax.swing.JSpinner spinnerBlackListTimeMin;
    private javax.swing.JSpinner spinnerBranchId;
    private javax.swing.JSpinner spinnerClientPort;
    private javax.swing.JSpinner spinnerDowntimeNax;
    private javax.swing.JSpinner spinnerFirstNumber;
    private javax.swing.JSpinner spinnerLastNumber;
    private javax.swing.JSpinner spinnerLineServiceMax;
    private javax.swing.JSpinner spinnerLineTotalMax;
    private javax.swing.JSpinner spinnerPropClientPort;
    private javax.swing.JSpinner spinnerPropServerPort;
    private javax.swing.JSpinner spinnerRelocation;
    private javax.swing.JSpinner spinnerRemoveRecall;
    private javax.swing.JSpinner spinnerServerPort;
    private javax.swing.JSpinner spinnerUserRS;
    private javax.swing.JSpinner spinnerWaitMax;
    private javax.swing.JSpinner spinnerWebServerPort;
    private javax.swing.JSpinner spinnerWorkMax;
    private javax.swing.JSpinner spinnerZonBoadrServPort;
    private javax.swing.JPanel tabHide;
    private javax.swing.JTabbedPane tabbedPaneMain;
    private javax.swing.JTable tableCalendar;
    private javax.swing.JTextField textFieldCalendarName;
    private javax.swing.JTextField textFieldClientAdress;
    private javax.swing.JTextField textFieldExtPoint;
    private javax.swing.JTextField textFieldFinishTime;
    private javax.swing.JTextField textFieldInfoItemName;
    private javax.swing.JTextField textFieldPager;
    private javax.swing.JTextField textFieldResponse;
    private javax.swing.JTextField textFieldScheduleName;
    private javax.swing.JTextField textFieldSearchService;
    private javax.swing.JTextField textFieldServerAddr;
    private javax.swing.JTextField textFieldStartTime;
    private javax.swing.JTextField textFieldURLWebService;
    private javax.swing.JTextField textFieldUserIdent;
    private javax.swing.JTextField textFieldUserName;
    private javax.swing.JTextField textFieldZonBoadrServAddr;
    private javax.swing.JTextPane textPaneInfoItem;
    private javax.swing.JTextPane textPaneInfoPrint;
    private javax.swing.JTextPane textPaneResponse;
    private javax.swing.JTextField tfHeaderCmtResp;
    private javax.swing.JTextField tfRespID;
    private javax.swing.JTextField tfUserId;
    private javax.swing.JTree treeInfo;
    private javax.swing.JTree treeResp;
    private javax.swing.JTree treeServices;
    private javax.swing.JList userServsList;

    private javax.swing.JSplitPane jUserOfficePane;
    private javax.swing.JPopupMenu popupOffice;
    private javax.swing.JPanel jOfficePanel;
    private javax.swing.JList listOffices;
    private javax.swing.JScrollPane jOfficeScrollPane;
    private javax.swing.JButton jAddOfficeButton;
    private javax.swing.JButton jDeleteOfficeButton;
    // End of variables declaration//GEN-END:variables
}
