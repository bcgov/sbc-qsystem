/*
 * Copyright (C) 2013 Evgeniy Egorov
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

 /*
 * FReception.java
 *
 * Created on Jun 20, 2013, 6:01:50 PM
 */
package ru.apertum.qsystem.client.forms;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.QProperties;
import ru.apertum.qsystem.client.common.ClientNetProperty;
import ru.apertum.qsystem.client.model.JTreeComboBox;
import ru.apertum.qsystem.client.model.QTray;
import ru.apertum.qsystem.common.*;
import ru.apertum.qsystem.common.cmd.JsonRPC20OK;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices.ServicesForWelcome;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfDay;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfDay.AdvTime;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfDay.GridDayAndParams;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation.SelfService;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation.SelfSituation;
import ru.apertum.qsystem.common.cmd.RpcGetServerState.ServiceInfo;
import ru.apertum.qsystem.common.cmd.RpcGetServiceState.ServiceState;
import ru.apertum.qsystem.common.cmd.RpcGetTicketHistory;
import ru.apertum.qsystem.common.cmd.RpcStandInService;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.model.IClientNetProperty;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IStartReception;
import ru.apertum.qsystem.server.model.*;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Evgeniy Egorov
 */
public class FReception extends javax.swing.JFrame {

    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FReception.class);
        }
        return localeMap.getString(key);
    }
    final private IClientNetProperty netProperty;

    public INetProperty getNetProperty() {
        return netProperty;
    }
    /**
     * Системный трей.
     */
    private final QTray tray;

    /**
     * Creates new form FReception
     *
     * @param netProperty
     */
    public FReception(IClientNetProperty netProperty) {
        initComponents();
        this.netProperty = netProperty;

        // инициализим trayIcon, т.к. setSituation() требует работу с tray
        final JFrame fr = this;
        tray = QTray.getInstance(fr, "/ru/apertum/qsystem/client/forms/resources/monitor.png", title()); //NOI18N
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

        init();
    }

    private String title() {
        return getTitle();
    }

    private void init() {

        btnPushToTalk.setVisible(false);

        setTitle(getTitle() + " " + Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF())); //NOI18N

        try {
            setIconImage(ImageIO.read(FAdmin.class.getResource("/ru/apertum/qsystem/client/forms/resources/monitor.png"))); //NOI18N
        } catch (IOException ex) {
            System.err.println(ex);
        }

        // Фича. По нажатию Escape закрываем форму
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
            setVisible(false);
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        int ii = 1;
        final ButtonGroup bg = new ButtonGroup();
        final String currLng = Locales.getInstance().getLangCurrName();
        for (String lng : Locales.getInstance().getAvailableLocales()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FReception.class, this).get("setCurrentLang")); //NOI18N
            bg.add(item);
            item.setSelected(lng.equals(currLng));
            item.setText(lng); // NOI18N
            item.setName("QRadioButtonMenuItem" + (ii++)); // NOI18N
            menuLangs.add(item);
        }

        treeServices.addTreeSelectionListener((TreeSelectionEvent e) -> {
            serviceListChange();
        });

        // Определим события выбора итемов в списках.
        listUsers.addListSelectionListener((ListSelectionEvent e) -> {
            userListChange();
        });

        tablePreReg.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Left mouse click
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // Do something
                } // Right mouse click
                else if (SwingUtilities.isRightMouseButton(e)) {
                    // get the coordinates of the mouse click
                    Point p = e.getPoint();

                    // get the row index that contains that coordinate
                    int rowNumber = tablePreReg.rowAtPoint(p);

                    // Get the ListSelectionModel of the JTable
                    ListSelectionModel model = tablePreReg.getSelectionModel();

                    // set the selected interval of rows. Using the "rowNumber"
                    // variable for the beginning and end selects only that one row.
                    model.setSelectionInterval(rowNumber, rowNumber);

                    final QService service = (QService) comboBoxServices.getSelectedItem();
                    if (tablePreReg.getSelectedRow() >= 0 && service != null && service.isLeaf()) {
                        final FirstPreCell fc = (FirstPreCell) tablePreReg.getValueAt(tablePreReg.getSelectedRow(), 0);
                        final GregorianCalendar gc = new GregorianCalendar();
                        gc.add(GregorianCalendar.HOUR_OF_DAY, 1);
                        if (fc.getAcust().getAdvanceTime() == null && fc.getDate().before(gc.getTime())) {
                            miAdvanceToLine.setEnabled(false);
                            miStandadvance.setEnabled(false);
                            return;
                        }
                        popupMenuAdvance.setEnabled(true);
                        if (fc.getAcust().getAdvanceTime() == null) {
                            miAdvanceToLine.setEnabled(false);
                            miStandadvance.setEnabled(true);
                        } else {
                            miAdvanceToLine.setEnabled(true);
                            miStandadvance.setEnabled(false);
                        }
                    } else {
                        miAdvanceToLine.setEnabled(false);
                        miStandadvance.setEnabled(false);
                    }
                }
            }
        });
    }
    JTreeComboBox comboBoxServices;
    private QService lastSelected = null;

    private void setModelForComboBoxServices(DefaultTreeModel model) {
        comboBoxServices = new JTreeComboBox(model);
        panelTreeCmbx.removeAll();
        panelTreeCmbx.setLayout(new GridLayout(1, 1));
        panelTreeCmbx.add(comboBoxServices);

        comboBoxServices.addItemListener((ItemEvent e) -> {
            if (((QService) e.getItem()).isLeaf()) {
                lastSelected = (QService) e.getItem();
                preRegChange(false);
            } else {
                comboBoxServices.setSelectedItem(lastSelected);
            }
        });
        comboBoxServices.setSelectedItem(model.getRoot());
    }

    private void userListChange() {
        if (listUsers.getLastVisibleIndex() != -1) {
            final QUser user = (QUser) listUsers.getSelectedValue();
            if (user == null) {
                return;
            }
            listServicesForUser.setModel(new DefaultComboBoxModel(user.getPlanServices().toArray()));
        }
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
        listLine.setModel(new DefaultListModel<>());
        String s = ""; //NOI18N
        for (QServiceLang sl : service.getLangs()) {
            s = s + ", " + sl.getLang(); //NOI18N
        }
        s = s.length() > 1 ? "[" + s.substring(2) + "]" : ""; //NOI18N

        labelServiceInfo.setText("<html><body text=\"#336699\"> " + (service.getEnable() == 1 ? "" : "<font color=\"#FF0000\">!*** </font>") + s + " " + getLocaleMessage("service.service") + service.getSeqId() + ": \"" + "<font color=\"#000000\">" + service.getName() + "\"    " + "</font>" //NOI18N
                + "<font color=\"#"
                + (service.getStatus() == 1 //NOI18N //NOI18N
                        ? "00AA00\">" + getLocaleMessage("service.kind.active") //NOI18N
                        : (service.getStatus() == 0 ? "CCAA00\">" + getLocaleMessage("service.kind.not_active") : "DD0000\">" + getLocaleMessage("service.kind.unavailable"))) + "/" + service.getPoint() //NOI18N
                + "</font>"
                + ";    " + getLocaleMessage("service.prefix") + ": " + "<font color=\"#DD0000\">" + service.getPrefix() + "</font>" + ";  " + getLocaleMessage("service.description") + ": " + service.getDescription() //NOI18N //NOI18N
                + ";<br>" + getLocaleMessage("service.restrict_day_reg") + ": " + (service.getDayLimit() == 0 ? getLocaleMessage("service.work_calendar.no") : service.getDayLimit()) //NOI18N
                + ";<br>" + getLocaleMessage("service.restrict_adv_reg") + " " + service.getAdvanceTimePeriod() + " " + getLocaleMessage("service.min") + ": " + service.getAdvanceLimit() //NOI18N
                + ";<br>  " + getLocaleMessage("service.restrict_adv_period") + ": " + service.getAdvanceLimitPeriod() //NOI18N
                + ";<br>"// + getLocaleMessage("service.work_calendar") + ": " + "<font color=\"#" + (service.getCalendar() == null ? "DD0000\">" + getLocaleMessage("service.work_calendar.no") : "000000\">" + service.getCalendar().toString()) + "</font>" + ";  " + getLocaleMessage("service.work_calendar.plan") + ": " + "<font color=\"#" + (service.getSchedule() == null ? "DD0000\">" + getLocaleMessage("service.work_calendar.no") : "000000\">" + service.getSchedule().toString()) + "</font>" + ";<br>" //NOI18N
                + (service.getInput_required() ? getLocaleMessage("service.required_client_data") + ": \"" + service.getInput_caption().replaceAll("<[^>]*>", "") + "\"(" + service.getPersonDayLimit() + ")" : getLocaleMessage("service.required_client_data.not")) + ";<br>   "
                + (service.getResult_required() ? getLocaleMessage("service.required_result") : getLocaleMessage("service.required_result.not")) + ";");

        // покажем пользователей
        final LinkedList<QUser> usr = new LinkedList<>();
        for (int i = 0; i < listUsers.getModel().getSize(); i++) {
            final QUser user = ((DefaultComboBoxModel<QUser>) listUsers.getModel()).getElementAt(i);
            if (user.hasService(service)) {
                usr.add(user);
            }
        }
        listUsersOfService.setModel(new DefaultComboBoxModel(usr.toArray()));
    }

    /**
     * Действия по смене выбранного дня и услуги для предвариловки.
     */
    private void preRegChange(boolean force) {
        if (!force && (comboBoxServices == null
                || comboBoxServices.getSelectedItem() == null
                || (calPrereg.getDate().equals(preDate) && comboBoxServices.getSelectedItem().equals(preService))
                || !((QService) comboBoxServices.getSelectedItem()).isLeaf())) {
            tablePreReg.setModel(new DefaultTableModel());
            return;
        }
        preDate = calPrereg.getDate();
        labelPreDate.setText(Locales.getInstance().format_dd_MMMM_yyyy.format(preDate));
        preService = (QService) comboBoxServices.getSelectedItem();

        calPrereg.setMinSelectableDate(new Date());
        if (preService.getAdvanceLimitPeriod() != 0) {
            final GregorianCalendar gc = new GregorianCalendar();
            gc.add(GregorianCalendar.DAY_OF_YEAR, preService.getAdvanceLimitPeriod());
            calPrereg.setMaxSelectableDate(gc.getTime());
        }

        final RpcGetGridOfDay.GridDayAndParams greed = NetCommander.getPreGridOfDay(netProperty, preService.getId(), preDate, -1);
        tablePreReg.setModel(new PreTableModel(greed));
        tablePreReg.getColumnModel().getColumn(0).setMaxWidth(50);
        tablePreReg.getColumnModel().getColumn(1).setMaxWidth(110);
    }
    private Date preDate;
    private QService preService;

    private static class PreTableModel extends AbstractTableModel {

        final private RpcGetGridOfDay.GridDayAndParams greed;

        public GridDayAndParams getGreed() {
            return greed;
        }

        public PreTableModel(GridDayAndParams greed) {
            this.greed = greed;
        }

        @Override
        public int getRowCount() {
            return (greed.getAdvanceLimit()) * greed.getTimes().size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final AdvTime time = greed.getTimes().get(rowIndex / (greed.getAdvanceLimit()));
            final QAdvanceCustomer cust = time.getAcusts().get(rowIndex % (greed.getAdvanceLimit()));
            switch (columnIndex) {
                case 0:
                    return new FirstPreCell(time.getDate(), cust);
                case 1:
                    final GregorianCalendar gc = new GregorianCalendar();
                    gc.add(GregorianCalendar.HOUR_OF_DAY, 1);
                    final String clr = (time.getDate().before(gc.getTime())) ? "red" : "green";
                    return cust.getAdvanceTime() == null ? "<html><span style=\'color:" + clr + "\'>" + getLocaleMessage("Free") : cust.getId();
                case 2:
                    return cust.getInputData();
                case 3:
                    return cust.getComments();
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return getLocaleMessage("TIME");
                case 1:
                    return getLocaleMessage("NUMBER");
                case 2:
                    return getLocaleMessage("client.data");
                case 3:
                    return getLocaleMessage("comments");
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return FirstPreCell.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                default:
                    throw new AssertionError();
            }
        }
    }

    private class UsersMonModel extends AbstractTableModel {

        final private LinkedList<QUser> greed;

        public UsersMonModel(LinkedList<QUser> greed) {
            this.greed = greed;
        }

        @Override
        public int getRowCount() {
            return greed.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return greed.get(rowIndex);
                case 1:
                    if (greed.get(rowIndex).getShadow() == null) {
                        return "<html><span style='color:purple'>" + getLocaleMessage("no.work") + "</SPAN>";
                    } else if (greed.get(rowIndex).getShadow().getStartTime() == null) {
                        final int mnt = Math.round((new Date().getTime() - greed.get(rowIndex).getShadow().getFinTime().getTime()) / 1000 / 60);
                        final boolean toolong = (mnt > standards.getDowntimeMax());
                        return "<HTML><SPAN STYLE='COLOR:" + (toolong ? "RED" : "GREEN") + "'>" + getLocaleMessage("Free2") + " " + mnt + getLocaleMessage("min.min") + "</span>";
                    } else {
                        final int mnt = Math.round((new Date().getTime() - greed.get(rowIndex).getShadow().getStartTime().getTime()) / 1000 / 60);
                        final boolean toolong = (mnt > standards.getDowntimeMax());
                        return "<html><span style='color:" + (toolong ? "red" : "green") + "'>" + "В работе" + " " + mnt + "мин." + "</span>"; //NOI18N
                    }
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return getLocaleMessage("operator");
                case 1:
                    return getLocaleMessage("state");
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return QUser.class;
                case 1:
                    return String.class;
                default:
                    throw new AssertionError();
            }
        }
    }

    private class ServicesMonModel extends AbstractTableModel {

        final private LinkedList<ServiceInfo> greed;

        public ServicesMonModel(LinkedList<ServiceInfo> greed) {
            this.greed = greed;
        }

        @Override
        public int getRowCount() {
            return greed.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return greed.get(rowIndex).getCountWait();
                case 1:
                    return greed.get(rowIndex).getWaitMax();
                case 2:
                    return greed.get(rowIndex);
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Integer.class;
                case 1:
                    return Integer.class;
                case 2:
                    return ServiceInfo.class;
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return getLocaleMessage("clients");
                case 1:
                    return getLocaleMessage("wait.min");
                case 2:
                    return getLocaleMessage("service");
                default:
                    throw new AssertionError();
            }
        }
    }

    private static class FirstPreCell {

        public FirstPreCell(Date date, QAdvanceCustomer acust) {
            this.date = date;
            this.acust = acust;
        }
        final Date date;
        final QAdvanceCustomer acust;

        public QAdvanceCustomer getAcust() {
            return acust;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public String toString() {
            return Uses.FORMAT_HH_MM.format(date);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupServiceTree = new javax.swing.JPopupMenu();
        menuItemStand = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemAdv = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuItemServDisable = new javax.swing.JMenuItem();
        popupLineList = new javax.swing.JPopupMenu();
        menuSetPriority = new javax.swing.JMenuItem();
        popupPostponed = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        popupMenuAdvance = new javax.swing.JPopupMenu();
        miStandadvance = new javax.swing.JMenuItem();
        miAdvanceToLine = new javax.swing.JMenuItem();
        tabsPane = new javax.swing.JTabbedPane();
        panelServices = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textFieldSerchService = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        tabbedPaneService = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        labelServiceInfo = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        listUsersOfService = new javax.swing.JList();
        panelLineState = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        listLine = new javax.swing.JList();
        labelWarringOfLineSize = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeServices = new javax.swing.JTree();
        panelUsers = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList();
        jSplitPane4 = new javax.swing.JSplitPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        listServicesForUser = new javax.swing.JList();
        jPanel12 = new javax.swing.JPanel();
        buttonRefreshUser = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        labelUserInfo = new javax.swing.JLabel();
        labelUser = new javax.swing.JLabel();
        panelPrereg = new javax.swing.JPanel();
        calPrereg = new com.toedter.calendar.JCalendar();
        panelTreeCmbx = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tablePreReg = new javax.swing.JTable();
        checkBoxPrintAdvTicket = new javax.swing.JCheckBox();
        labelPreDate = new javax.swing.JLabel();
        buttonRemoveAdvanceCustomer = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel9 = new javax.swing.JPanel();
        buttonRefreshPostponed = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        listPostponed = new javax.swing.JList();
        jPanel10 = new javax.swing.JPanel();
        buttonRefreshBlack = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        listBlack = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        labelTotalCustomers = new javax.swing.JLabel();
        jSplitPane5 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tableUsersMon = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tableServicesMon = new javax.swing.JTable();
        buttonRefreshMainData = new javax.swing.JButton();
        btnPushToTalk = new javax.swing.JButton();
        panelComplexServ = new javax.swing.JPanel();
        MenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuRefreshMainData = new javax.swing.JMenuItem();
        menuSendMessage = new javax.swing.JMenuItem();
        menuLangs = new javax.swing.JMenu();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemExit = new javax.swing.JMenuItem();
        menuCustomers = new javax.swing.JMenu();
        menuItemChangePriority = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuItemCheckTicket = new javax.swing.JMenuItem();
        menuAbout = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

        popupServiceTree.setComponentPopupMenu(popupServiceTree);
        popupServiceTree.setName("popupServiceTree"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FReception.class, this);
        menuItemStand.setAction(actionMap.get("setInLine")); // NOI18N
        menuItemStand.setName("menuItemStand"); // NOI18N
        popupServiceTree.add(menuItemStand);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popupServiceTree.add(jSeparator2);

        menuItemAdv.setAction(actionMap.get("preReg")); // NOI18N
        menuItemAdv.setName("menuItemAdv"); // NOI18N
        popupServiceTree.add(menuItemAdv);

        jSeparator4.setName("jSeparator4"); // NOI18N
        popupServiceTree.add(jSeparator4);

        menuItemServDisable.setAction(actionMap.get("serviceDisable")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FReception.class);
        menuItemServDisable.setText(resourceMap.getString("menuItemServDisable.text")); // NOI18N
        menuItemServDisable.setName("menuItemServDisable"); // NOI18N
        popupServiceTree.add(menuItemServDisable);

        popupLineList.setName("popupLineList"); // NOI18N

        menuSetPriority.setAction(actionMap.get("setPriority")); // NOI18N
        menuSetPriority.setName("menuSetPriority"); // NOI18N
        popupLineList.add(menuSetPriority);

        popupPostponed.setName("popupPostponed"); // NOI18N

        jMenuItem1.setAction(actionMap.get("changeStatusForPostponed")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        popupPostponed.add(jMenuItem1);

        popupMenuAdvance.setName("popupMenuAdvance"); // NOI18N

        miStandadvance.setText(resourceMap.getString("preReg.Action.text")); // NOI18N
        miStandadvance.setName("miStandadvance"); // NOI18N
        miStandadvance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miStandadvanceActionPerformed(evt);
            }
        });
        popupMenuAdvance.add(miStandadvance);

        miAdvanceToLine.setText(resourceMap.getString("setInLine.Action.text")); // NOI18N
        miAdvanceToLine.setName("miAdvanceToLine"); // NOI18N
        miAdvanceToLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAdvanceToLineActionPerformed(evt);
            }
        });
        popupMenuAdvance.add(miAdvanceToLine);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N

        tabsPane.setName("tabsPane"); // NOI18N

        panelServices.setName("panelServices"); // NOI18N

        jPanel7.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel7.setName("jPanel7"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        textFieldSerchService.setText(resourceMap.getString("textFieldSerchService.text")); // NOI18N
        textFieldSerchService.setName("textFieldSerchService"); // NOI18N
        textFieldSerchService.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSerchServiceKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textFieldSerchService)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textFieldSerchService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setAutoscrolls(true);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        tabbedPaneService.setName("tabbedPaneService"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        labelServiceInfo.setText(resourceMap.getString("labelServiceInfo.text")); // NOI18N
        labelServiceInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelServiceInfo.setName("labelServiceInfo"); // NOI18N
        jScrollPane2.setViewportView(labelServiceInfo);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1044, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );

        tabbedPaneService.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        listUsersOfService.setName("listUsersOfService"); // NOI18N
        listUsersOfService.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listUsersOfServiceMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(listUsersOfService);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1044, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );

        tabbedPaneService.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        panelLineState.setName("panelLineState"); // NOI18N

        jButton1.setAction(actionMap.get("refreshLines")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        listLine.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listLine.setComponentPopupMenu(popupLineList);
        listLine.setName("listLine"); // NOI18N
        jScrollPane3.setViewportView(listLine);

        labelWarringOfLineSize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelWarringOfLineSize.setText(resourceMap.getString("labelWarringOfLineSize.text")); // NOI18N
        labelWarringOfLineSize.setToolTipText(resourceMap.getString("labelWarringOfLineSize.toolTipText")); // NOI18N
        labelWarringOfLineSize.setName("labelWarringOfLineSize"); // NOI18N

        javax.swing.GroupLayout panelLineStateLayout = new javax.swing.GroupLayout(panelLineState);
        panelLineState.setLayout(panelLineStateLayout);
        panelLineStateLayout.setHorizontalGroup(
            panelLineStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLineStateLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 945, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLineStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelWarringOfLineSize, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelLineStateLayout.setVerticalGroup(
            panelLineStateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLineStateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelWarringOfLineSize, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
        );

        tabbedPaneService.addTab(resourceMap.getString("panelLineState.TabConstraints.tabTitle"), panelLineState); // NOI18N

        jSplitPane1.setBottomComponent(tabbedPaneService);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        treeServices.setAutoscrolls(true);
        treeServices.setComponentPopupMenu(popupServiceTree);
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
        jScrollPane1.setViewportView(treeServices);

        jSplitPane1.setLeftComponent(jScrollPane1);

        javax.swing.GroupLayout panelServicesLayout = new javax.swing.GroupLayout(panelServices);
        panelServices.setLayout(panelServicesLayout);
        panelServicesLayout.setHorizontalGroup(
            panelServicesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelServicesLayout.setVerticalGroup(
            panelServicesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelServicesLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
        );

        tabsPane.addTab(resourceMap.getString("panelServices.TabConstraints.tabTitle"), panelServices); // NOI18N

        panelUsers.setName("panelUsers"); // NOI18N

        jSplitPane3.setDividerLocation(250);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        listUsers.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("listUsers.border.title"))); // NOI18N
        listUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listUsers.setName("listUsers"); // NOI18N
        listUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listUsersMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(listUsers);

        jSplitPane3.setLeftComponent(jScrollPane6);

        jSplitPane4.setDividerLocation(200);
        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setContinuousLayout(true);
        jSplitPane4.setName("jSplitPane4"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        listServicesForUser.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("listServicesForUser.border.title"))); // NOI18N
        listServicesForUser.setName("listServicesForUser"); // NOI18N
        listServicesForUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listServicesForUserMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(listServicesForUser);

        jSplitPane4.setTopComponent(jScrollPane8);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel12.border.title"))); // NOI18N
        jPanel12.setName("jPanel12"); // NOI18N

        buttonRefreshUser.setText(resourceMap.getString("buttonRefreshUser.text")); // NOI18N
        buttonRefreshUser.setName("buttonRefreshUser"); // NOI18N
        buttonRefreshUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshUserActionPerformed(evt);
            }
        });

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        labelUserInfo.setText(resourceMap.getString("labelUserInfo.text")); // NOI18N
        labelUserInfo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelUserInfo.setName("labelUserInfo"); // NOI18N
        jScrollPane9.setViewportView(labelUserInfo);

        labelUser.setFont(resourceMap.getFont("labelUser.font")); // NOI18N
        labelUser.setText(resourceMap.getString("labelUser.text")); // NOI18N
        labelUser.setName("labelUser"); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
                    .addComponent(labelUser)
                    .addComponent(buttonRefreshUser, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addComponent(labelUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRefreshUser))
        );

        jSplitPane4.setRightComponent(jPanel12);

        jSplitPane3.setRightComponent(jSplitPane4);

        javax.swing.GroupLayout panelUsersLayout = new javax.swing.GroupLayout(panelUsers);
        panelUsers.setLayout(panelUsersLayout);
        panelUsersLayout.setHorizontalGroup(
            panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1051, Short.MAX_VALUE)
        );
        panelUsersLayout.setVerticalGroup(
            panelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
        );

        tabsPane.addTab(resourceMap.getString("panelUsers.TabConstraints.tabTitle"), panelUsers); // NOI18N

        panelPrereg.setName("panelPrereg"); // NOI18N

        calPrereg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        calPrereg.setFont(resourceMap.getFont("calPrereg.font")); // NOI18N
        calPrereg.setName("calPrereg"); // NOI18N
        calPrereg.setTodayButtonVisible(true);
        calPrereg.setWeekOfYearVisible(false);
        calPrereg.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calPreregPropertyChange(evt);
            }
        });

        panelTreeCmbx.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelTreeCmbx.border.title"))); // NOI18N
        panelTreeCmbx.setName("panelTreeCmbx"); // NOI18N

        javax.swing.GroupLayout panelTreeCmbxLayout = new javax.swing.GroupLayout(panelTreeCmbx);
        panelTreeCmbx.setLayout(panelTreeCmbxLayout);
        panelTreeCmbxLayout.setHorizontalGroup(
            panelTreeCmbxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1039, Short.MAX_VALUE)
        );
        panelTreeCmbxLayout.setVerticalGroup(
            panelTreeCmbxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        tablePreReg.setAutoCreateRowSorter(true);
        tablePreReg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Время", "Номер", "Данные посетителя", "Комментарии"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablePreReg.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tablePreReg.setComponentPopupMenu(popupMenuAdvance);
        tablePreReg.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tablePreReg.setName("tablePreReg"); // NOI18N
        tablePreReg.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablePreReg.setShowVerticalLines(false);
        tablePreReg.getTableHeader().setResizingAllowed(false);
        tablePreReg.getTableHeader().setReorderingAllowed(false);
        tablePreReg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablePreRegMouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(tablePreReg);

        checkBoxPrintAdvTicket.setSelected(true);
        checkBoxPrintAdvTicket.setText(resourceMap.getString("checkBoxPrintAdvTicket.text")); // NOI18N
        checkBoxPrintAdvTicket.setName("checkBoxPrintAdvTicket"); // NOI18N
        checkBoxPrintAdvTicket.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkBoxPrintAdvTicketItemStateChanged(evt);
            }
        });

        labelPreDate.setFont(resourceMap.getFont("labelPreDate.font")); // NOI18N
        labelPreDate.setText(resourceMap.getString("labelPreDate.text")); // NOI18N
        labelPreDate.setName("labelPreDate"); // NOI18N

        buttonRemoveAdvanceCustomer.setText(resourceMap.getString("buttonRemoveAdvanceCustomer.text")); // NOI18N
        buttonRemoveAdvanceCustomer.setName("buttonRemoveAdvanceCustomer"); // NOI18N
        buttonRemoveAdvanceCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveAdvanceCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPreregLayout = new javax.swing.GroupLayout(panelPrereg);
        panelPrereg.setLayout(panelPreregLayout);
        panelPreregLayout.setHorizontalGroup(
            panelPreregLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTreeCmbx, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelPreregLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPreregLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPreregLayout.createSequentialGroup()
                        .addComponent(calPrereg, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPreregLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPreregLayout.createSequentialGroup()
                                .addComponent(labelPreDate)
                                .addContainerGap())
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPreregLayout.createSequentialGroup()
                        .addComponent(checkBoxPrintAdvTicket)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 691, Short.MAX_VALUE)
                        .addComponent(buttonRemoveAdvanceCustomer)
                        .addContainerGap())))
        );
        panelPreregLayout.setVerticalGroup(
            panelPreregLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPreregLayout.createSequentialGroup()
                .addComponent(panelTreeCmbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPreregLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calPrereg, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelPreregLayout.createSequentialGroup()
                        .addComponent(labelPreDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPreregLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRemoveAdvanceCustomer)
                            .addComponent(checkBoxPrintAdvTicket))
                        .addContainerGap())))
        );

        tabsPane.addTab(resourceMap.getString("panelPrereg.TabConstraints.tabTitle"), panelPrereg); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N

        jSplitPane2.setDividerLocation(251);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel9.border.title"))); // NOI18N
        jPanel9.setName("jPanel9"); // NOI18N

        buttonRefreshPostponed.setText(resourceMap.getString("buttonRefreshPostponed.text")); // NOI18N
        buttonRefreshPostponed.setName("buttonRefreshPostponed"); // NOI18N
        buttonRefreshPostponed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshPostponedActionPerformed(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        listPostponed.setComponentPopupMenu(popupPostponed);
        listPostponed.setName("listPostponed"); // NOI18N
        jScrollPane4.setViewportView(listPostponed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRefreshPostponed)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(193, Short.MAX_VALUE)
                .addComponent(buttonRefreshPostponed)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
        );

        jSplitPane2.setTopComponent(jPanel9);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel10.border.title"))); // NOI18N
        jPanel10.setName("jPanel10"); // NOI18N

        buttonRefreshBlack.setText(resourceMap.getString("buttonRefreshBlack.text")); // NOI18N
        buttonRefreshBlack.setName("buttonRefreshBlack"); // NOI18N
        buttonRefreshBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshBlackActionPerformed(evt);
            }
        });

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        listBlack.setName("listBlack"); // NOI18N
        jScrollPane5.setViewportView(listBlack);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonRefreshBlack)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(284, Short.MAX_VALUE)
                .addComponent(buttonRefreshBlack)
                .addContainerGap())
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(jPanel10);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        tabsPane.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel2.setName("jPanel2"); // NOI18N

        labelTotalCustomers.setBackground(resourceMap.getColor("labelTotalCustomers.background")); // NOI18N
        labelTotalCustomers.setFont(resourceMap.getFont("labelTotalCustomers.font")); // NOI18N
        labelTotalCustomers.setText(resourceMap.getString("labelTotalCustomers.text")); // NOI18N
        labelTotalCustomers.setName("labelTotalCustomers"); // NOI18N
        labelTotalCustomers.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTotalCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTotalCustomers)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane5.setDividerLocation(400);
        jSplitPane5.setContinuousLayout(true);
        jSplitPane5.setName("jSplitPane5"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        tableUsersMon.setAutoCreateRowSorter(true);
        tableUsersMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableUsersMon.setName("tableUsersMon"); // NOI18N
        tableUsersMon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableUsersMon.getTableHeader().setReorderingAllowed(false);
        tableUsersMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableUsersMonMouseClicked(evt);
            }
        });
        jScrollPane11.setViewportView(tableUsersMon);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
        );

        jSplitPane5.setLeftComponent(jPanel3);

        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        tableServicesMon.setAutoCreateRowSorter(true);
        tableServicesMon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableServicesMon.setName("tableServicesMon"); // NOI18N
        tableServicesMon.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableServicesMon.getTableHeader().setReorderingAllowed(false);
        tableServicesMon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableServicesMonMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(tableServicesMon);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
        );

        jSplitPane5.setRightComponent(jPanel5);

        buttonRefreshMainData.setAction(actionMap.get("refreshMainData")); // NOI18N
        buttonRefreshMainData.setText(resourceMap.getString("buttonRefreshMainData.text")); // NOI18N
        buttonRefreshMainData.setName("buttonRefreshMainData"); // NOI18N

        btnPushToTalk.setText(resourceMap.getString("btnPushToTalk.text")); // NOI18N
        btnPushToTalk.setToolTipText(resourceMap.getString("btnPushToTalk.toolTipText")); // NOI18N
        btnPushToTalk.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        btnPushToTalk.setName("btnPushToTalk"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonRefreshMainData)
                .addGap(18, 18, 18)
                .addComponent(btnPushToTalk, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jSplitPane5, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPushToTalk, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                    .addComponent(buttonRefreshMainData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabsPane.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        panelComplexServ.setName("panelComplexServ"); // NOI18N

        javax.swing.GroupLayout panelComplexServLayout = new javax.swing.GroupLayout(panelComplexServ);
        panelComplexServ.setLayout(panelComplexServLayout);
        panelComplexServLayout.setHorizontalGroup(
            panelComplexServLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1051, Short.MAX_VALUE)
        );
        panelComplexServLayout.setVerticalGroup(
            panelComplexServLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );

        tabsPane.addTab(resourceMap.getString("panelComplexServ.TabConstraints.tabTitle"), panelComplexServ); // NOI18N

        MenuBar.setName("MenuBar"); // NOI18N

        menuFile.setText(resourceMap.getString("menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        menuRefreshMainData.setAction(actionMap.get("refreshMainData")); // NOI18N
        menuRefreshMainData.setText(resourceMap.getString("menuRefreshMainData.text")); // NOI18N
        menuRefreshMainData.setName("menuRefreshMainData"); // NOI18N
        menuFile.add(menuRefreshMainData);

        menuSendMessage.setAction(actionMap.get("sendMessage")); // NOI18N
        menuSendMessage.setText(resourceMap.getString("menuSendMessage.text")); // NOI18N
        menuSendMessage.setName("menuSendMessage"); // NOI18N
        menuFile.add(menuSendMessage);

        menuLangs.setAction(actionMap.get("setCurrentLang")); // NOI18N
        menuLangs.setText(resourceMap.getString("menuLangs.text")); // NOI18N
        menuLangs.setName("menuLangs"); // NOI18N
        menuFile.add(menuLangs);

        jSeparator1.setName("jSeparator1"); // NOI18N
        menuFile.add(jSeparator1);

        menuItemExit.setAction(actionMap.get("quit")); // NOI18N
        menuItemExit.setText(resourceMap.getString("menuItemExit.text")); // NOI18N
        menuItemExit.setName("menuItemExit"); // NOI18N
        menuFile.add(menuItemExit);

        MenuBar.add(menuFile);

        menuCustomers.setText(resourceMap.getString("menuCustomers.text")); // NOI18N
        menuCustomers.setName("menuCustomers"); // NOI18N

        menuItemChangePriority.setAction(actionMap.get("setAnyPriority")); // NOI18N
        menuItemChangePriority.setText(resourceMap.getString("menuItemChangePriority.text")); // NOI18N
        menuItemChangePriority.setName("menuItemChangePriority"); // NOI18N
        menuCustomers.add(menuItemChangePriority);

        jSeparator3.setName("jSeparator3"); // NOI18N
        menuCustomers.add(jSeparator3);

        menuItemCheckTicket.setAction(actionMap.get("checkAnyTicket")); // NOI18N
        menuItemCheckTicket.setText(resourceMap.getString("menuItemCheckTicket.text")); // NOI18N
        menuItemCheckTicket.setName("menuItemCheckTicket"); // NOI18N
        menuCustomers.add(menuItemCheckTicket);

        MenuBar.add(menuCustomers);

        menuAbout.setText(resourceMap.getString("menuAbout.text")); // NOI18N
        menuAbout.setName("menuAbout"); // NOI18N

        menuItemAbout.setAction(actionMap.get("getAbout")); // NOI18N
        menuItemAbout.setText(resourceMap.getString("menuItemAbout.text")); // NOI18N
        menuItemAbout.setName("menuItemAbout"); // NOI18N
        menuAbout.add(menuItemAbout);

        MenuBar.add(menuAbout);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldSerchServiceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSerchServiceKeyReleased
        TreeNode node = null;
        boolean flag = evt.getKeyCode() != 114;
        final QService ser = (QService) treeServices.getLastSelectedPathComponent();
        for (Object object : ((ATreeModel) treeServices.getModel()).getNodes()) {
            final QService service = (QService) object;
            if (flag) {
                if (service.toString().toLowerCase().contains(textFieldSerchService.getText().trim().toLowerCase())) {
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
    }//GEN-LAST:event_textFieldSerchServiceKeyReleased

    private void treeServicesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeServicesKeyReleased
        if (evt.getKeyCode() == 114) {
            textFieldSerchServiceKeyReleased(evt);
        }
    }//GEN-LAST:event_treeServicesKeyReleased

    private void buttonRefreshPostponedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshPostponedActionPerformed
        QLog.l().logger().info(getLocaleMessage("request.to.server"));
        //элемент ответа.
        try {
            listPostponed.setModel(QPostponedList.getInstance().loadPostponedList(NetCommander.getPostponedPoolInfo(netProperty)));
        } catch (Exception ex) {
            QLog.l().logger().error(getLocaleMessage("server.not.work") + ex + "\""); //NOI18N
            tray.showMessageTray(getLocaleMessage("tray.server"), getLocaleMessage("tray.message.stop_server"), QTray.MessageType.WARNING);
        }
    }//GEN-LAST:event_buttonRefreshPostponedActionPerformed

    private void buttonRefreshBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshBlackActionPerformed
        listBlack.setModel(new DefaultComboBoxModel(NetCommander.getBanedList(netProperty).toArray()));
    }//GEN-LAST:event_buttonRefreshBlackActionPerformed

    private void buttonRefreshUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshUserActionPerformed
        if (listUsers.getLastVisibleIndex() != -1) {
            final QUser user = (QUser) listUsers.getSelectedValue();
            if (user == null) {
                return;
            }
            labelUser.setText(user.getName() + " " + user.getPoint());
            final SelfSituation plan;
            try {
                plan = NetCommander.getSelfServices(netProperty, user.getId(), true);
            } catch (QException ex) {
                throw new ClientException(ex);
            }

            String tempAll = "";
            String color = "blue";
            int inCount = 0;

            // построим новую html с описанием состояния очередей
            for (SelfService serv : plan.getSelfservices()) {
                final int count = serv.getCountWait();
                if (0 == count) {
                    continue;
                }
                final String serviceName = serv.getServiceName();

                final String people = " " + getLocaleMessage("messages.people");// множественное

                tempAll = tempAll + "<span style='color:" + (0 == count ? "green" : "red") + "'> - " + serviceName + ": " + count + people
                        + ((((count % 10) >= 2) && ((count % 10) <= 4)) ? "a" : "") + "</span><br>";
                if (count != 0) {
                    color = "purple";
                }
                inCount = inCount + count;
            }
            final String allClients = getLocaleMessage("messages.allClients") + ": ";
            //labelResume.setText("<html><span style='color:" + color + "'>" + allClients + inCount + "</span>");
            tempAll = "<span style='color:" + color + "'>" + allClients + inCount + "</span><br>" + tempAll;

            // посмотрим, не приехал ли кастомер, который уже вызванный
            // если приехал, то его надо учесть
            if (plan.getCustomer() != null) {
                QLog.l().logger().trace("От сервера приехал кастомер, который обрабатывается юзером."); //NOI18N

                QLog.l().logger().trace("Установливаем кастомера работающему клиенту и выводем его."); //NOI18N
                QCustomer customer = plan.getCustomer();
                final long min = (customer.getState() == CustomerState.STATE_INVITED || customer.getState() == CustomerState.STATE_INVITED_SECONDARY ? 0 : (new Date().getTime() - customer.getStartTime().getTime()) / 1000 / 60);
                if (min > standards.getWorkMax()) {
                    labelUserInfo.setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/attention.png"), "")); //NOI18N
                    JOptionPane.showMessageDialog(this, getLocaleMessage("to.long.work.client"), getLocaleMessage("attention"), JOptionPane.WARNING_MESSAGE); //NOI18N
                } else {
                    labelUserInfo.setIcon(null);
                }
                // выведем на экран некую инфу о приглашенном кастомере
                final String textCust = customer.getPrefix() + customer.getNumber();
                //     labelNextNumber.setText(textCust);
                // Выведем номер вызванного.

                final String priority;
                switch (customer.getPriority().get()) {
                    case 0: {
                        priority = getLocaleMessage("messages.priority.low"); //NOI18N
                        break;
                    }
                    case 1: {
                        priority = getLocaleMessage("messages.priority.standart"); //NOI18N
                        break;
                    }
                    case 2: {
                        priority = getLocaleMessage("messages.priority.hi"); //NOI18N
                        break;
                    }
                    case 3: {
                        priority = getLocaleMessage("messages.priority.vip"); //NOI18N
                        break;
                    }
                    default: {
                        priority = getLocaleMessage("messages.priority.strange"); //NOI18N
                    }
                }
                String s = customer.getService().getInput_caption();
                if (s == null) {
                    s = ""; //NOI18N
                } else {
                    s = "<br>" + s + "<br>" + customer.getInput_data(); //NOI18N
                }
                tempAll = tempAll + "<br>" + getLocaleMessage("working.with.client") + " " + textCust + " " + min + " " + getLocaleMessage("mints") + "<br><b><span style='color:#000000'> " + getLocaleMessage("messages.service") + ": " + customer.getService().getName() + "<br>" + getLocaleMessage("messages.priority") + ": " + priority + s + "</span></b>";
                //     labelNextCustomerInfo.setText("<html><b><span style='color:#000000'> " + getLocaleMessage("messages.service") + ": " + customer.getService().getName() + "<br>" + getLocaleMessage("messages.priority") + ": " + priority + s + "</span></b>");
                //     textAreaComments.setText(customer.getTempComments());
                //     textAreaComments.setCaretPosition(0);

            } else {
                labelUserInfo.setIcon(null);
                if (plan.getShadow() == null) {
                    labelUserInfo.setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/attention.png"), "")); //NOI18N
                    tempAll = tempAll + "<br><span style='color:purple'>" + getLocaleMessage("not.working") + "</span>";
                } else {
                    final int mnt = Math.round((new Date().getTime() - plan.getShadow().getFinTime().getTime()) / 1000 / 60);
                    final boolean toolong = 0 != inCount && mnt > standards.getDowntimeMax();
                    if (toolong) {
                        labelUserInfo.setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/attention.png"), ""));
                    }
                    tempAll = tempAll + "<br><span style='color:" + (toolong ? "red" : "green") + "'>" + getLocaleMessage("free3") + " " + mnt + getLocaleMessage("min.dot") + "</span>";
                }
            }

            labelUserInfo.setText("<html>" + tempAll);

        }

    }//GEN-LAST:event_buttonRefreshUserActionPerformed

    private void calPreregPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_calPreregPropertyChange
        preRegChange(false);
    }//GEN-LAST:event_calPreregPropertyChange

    private void tablePreRegMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablePreRegMouseClicked
        final QService service = (QService) comboBoxServices.getSelectedItem();
        if (((evt == null) || (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() > 1)) && tablePreReg.getSelectedRow() >= 0 && service != null && service.isLeaf()) {
            final FirstPreCell fc = (FirstPreCell) tablePreReg.getValueAt(tablePreReg.getSelectedRow(), 0);
            final GregorianCalendar gc = new GregorianCalendar();
            gc.add(GregorianCalendar.HOUR_OF_DAY, 1);
            if (fc.getAcust().getAdvanceTime() == null && fc.getDate().before(gc.getTime())) {
                return;
            }

            if (fc.getAcust().getAdvanceTime() == null) {
                if (0 == JOptionPane.showConfirmDialog(this, getLocaleMessage("make.pre.reg ") + fc + "?", getLocaleMessage("pre.reg"), JOptionPane.YES_NO_OPTION)) {

                    String inputData = null;
                    if (service.getInput_required()) {
                        inputData = (String) JOptionPane.showInputDialog(this, service.getInput_caption(), "***", 3, null, null, ""); //NOI18N
                        if (inputData == null) {
                            return;
                        }
                    }

                    String comments = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.comments"), "***", 3, null, null, ""); //NOI18N
                    if (inputData == null) {
                        inputData = ""; //NOI18N
                    }

                    final QAdvanceCustomer res;
                    try {
                        res = NetCommander.standInServiceAdvance(netProperty, service.getId(), fc.getDate(), -1, inputData, comments);
                    } catch (Exception ex) {
                        throw new ClientException(getLocaleMessage("admin.send_cmd_adv.err") + " " + ex); //NOI18N
                    }
                    if (res == null) {
                        return;
                    }
                    // печатаем результат
                    if (checkBoxPrintAdvTicket.isSelected()) {
                        new Thread(() -> {
                            FWelcome.printTicketAdvance(res, ((QService) treeServices.getModel().getRoot()).getTextToLocale(QService.Field.NAME));
                        }).start();
                    }
                    preRegChange(true);
                    JOptionPane.showMessageDialog(this, getLocaleMessage("admin.client_adv_dialog.msg_1") + " \"" + service.getName() + "\". " + getLocaleMessage("admin.client_adv_dialog.msg_2") + " \"" + res.getId() + "\".", getLocaleMessage("admin.client_adv_dialog.title"), JOptionPane.INFORMATION_MESSAGE);

                }
            } else if (0 == JOptionPane.showConfirmDialog(this, getLocaleMessage("clint.came") + fc + "?", getLocaleMessage("pre.reg.2"), JOptionPane.YES_NO_OPTION)) {

                final RpcStandInService res = NetCommander.standAndCheckAdvance(netProperty, fc.getAcust().getId());

                if (res != null) {

                    if (res.getMethod() == null) {// костыль. тут приедет текст запрета если нельзя встать в очередь
                        QLog.l().logger().info(getLocaleMessage("print.ticket"));
                        new Thread(() -> {
                            FWelcome.printTicket(res.getResult(), ((QService) treeServices.getModel().getRoot()).getName());
                        }).start();
                        preRegChange(true);
                        JOptionPane.showMessageDialog(this, getLocaleMessage("admin.client_adv_dialog.msg_3"), getLocaleMessage("admin.client_adv_dialog.title"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, res.getMethod(), getLocaleMessage("admin.client_adv_dialog.title"), JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        }
    }//GEN-LAST:event_tablePreRegMouseClicked

    private void checkBoxPrintAdvTicketItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkBoxPrintAdvTicketItemStateChanged
        config.setProperty("reception.print_adv_ticket", checkBoxPrintAdvTicket.isSelected());
        try {
            builder.save();
        } catch (ConfigurationException ex) {
            throw new ClientException(ex);
        }
    }//GEN-LAST:event_checkBoxPrintAdvTicketItemStateChanged

    private void listUsersOfServiceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listUsersOfServiceMouseClicked
        if (evt.getClickCount() > 1 && listUsersOfService.getSelectedIndex() != -1) {
            listUsers.setSelectedValue(listUsersOfService.getModel().getElementAt(listUsersOfService.getSelectedIndex()), true);
            tabsPane.setSelectedComponent(panelUsers);
            buttonRefreshUserActionPerformed(null);
        }
    }//GEN-LAST:event_listUsersOfServiceMouseClicked

    private void listServicesForUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listServicesForUserMouseClicked
        if (evt.getClickCount() > 1 && listServicesForUser.getSelectedIndex() != -1) {
            tabsPane.setSelectedComponent(panelServices);
            TreeNode[] nodes = ((DefaultTreeModel) treeServices.getModel()).getPathToRoot(((ATreeModel) treeServices.getModel()).getById(((QPlanService) listServicesForUser.getSelectedValue()).getService().getId()));
            TreePath path = new TreePath(nodes);
            treeServices.setSelectionPath(path);
            treeServices.setExpandsSelectedPaths(true);
            treeServices.scrollPathToVisible(path);
        }
    }//GEN-LAST:event_listServicesForUserMouseClicked

    private void listUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listUsersMouseClicked
        if (evt.getClickCount() > 1 && listUsers.getSelectedIndex() != -1) {
            buttonRefreshUserActionPerformed(null);
        }
    }//GEN-LAST:event_listUsersMouseClicked

    private void treeServicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeServicesMouseClicked
        if (evt.getClickCount() > 1) {
            final TreePath selectedPath = treeServices.getSelectionPath();
            if (selectedPath != null) {
                tabbedPaneService.setSelectedComponent(panelLineState);
                refreshLines();
            }
        }
    }//GEN-LAST:event_treeServicesMouseClicked

    private void tableServicesMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableServicesMonMouseClicked
        if (evt.getClickCount() > 1 && tableServicesMon.getSelectedRow() != -1) {
            tabsPane.setSelectedComponent(panelServices);
            TreeNode[] nodes = ((DefaultTreeModel) treeServices.getModel()).getPathToRoot(((ATreeModel) treeServices.getModel()).getById(((ServiceInfo) tableServicesMon.getModel().getValueAt(tableServicesMon.getSelectedRow(), 2)).getId()));
            TreePath path = new TreePath(nodes);
            treeServices.setSelectionPath(path);
            treeServices.setExpandsSelectedPaths(true);
            treeServices.scrollPathToVisible(path);
        }
    }//GEN-LAST:event_tableServicesMonMouseClicked

    private void tableUsersMonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableUsersMonMouseClicked
        if (evt.getClickCount() > 1 && tableUsersMon.getSelectedRow() != -1) {
            listUsers.setSelectedValue(tableUsersMon.getModel().getValueAt(tableUsersMon.getSelectedRow(), 0), true);
            tabsPane.setSelectedComponent(panelUsers);
            buttonRefreshUserActionPerformed(null);
        }
    }//GEN-LAST:event_tableUsersMonMouseClicked

    private void buttonRemoveAdvanceCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveAdvanceCustomerActionPerformed
        if (tablePreReg.getSelectedRow() >= 0) {
            final FirstPreCell fc = (FirstPreCell) tablePreReg.getValueAt(tablePreReg.getSelectedRow(), 0);
            final GregorianCalendar gc = new GregorianCalendar();
            gc.add(GregorianCalendar.HOUR_OF_DAY, 1);
            if (fc.getDate().before(gc.getTime())) {
                return;
            }

            if (fc.getAcust().getAdvanceTime() == null) {

            } else if (0 == JOptionPane.showConfirmDialog(this, "Удалить предварительную запись " + fc.getAcust().getId() + " ко времени " + fc + " безвозвратно?", getLocaleMessage("pre.reg.2"), JOptionPane.YES_NO_OPTION)) {

                final JsonRPC20OK res = NetCommander.removeAdvancedCustomer(netProperty, fc.getAcust().getId());

                if (res != null) {

                    if (res.getResult() == 1) {// костыль. тут приедет ID отказа
                        QLog.l().logger().info("Удалили предваоительного " + fc.getAcust().getId() + " на " + fc);

                        preRegChange(true);
                        JOptionPane.showMessageDialog(this, getLocaleMessage("admin.client_adv_remove.msg_3"), getLocaleMessage("admin.client_adv_dialog.title"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Не найдена предварительная запись по номеру " + fc.getAcust().getId(), getLocaleMessage("admin.client_adv_dialog.title"), JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        }
    }//GEN-LAST:event_buttonRemoveAdvanceCustomerActionPerformed

    private void miStandadvanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miStandadvanceActionPerformed
        tablePreRegMouseClicked(null);
    }//GEN-LAST:event_miStandadvanceActionPerformed

    private void miAdvanceToLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAdvanceToLineActionPerformed
        tablePreRegMouseClicked(null);
    }//GEN-LAST:event_miAdvanceToLineActionPerformed
    static FileBasedConfiguration config;
    static FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        QLog.initial(args, 2);
        Locale.setDefault(Locales.getInstance().getLangCurrent());
        Uses.showSplash();
        // Загрузка плагинов из папки plugins
        if (QConfig.cfg().isPlaginable()) {
            Uses.loadPlugins("./plugins/");
        }

        builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new FileBasedBuilderParametersImpl().setFileName("config/reception.properties").setEncoding("utf8"));
        builder.setAutoSave(true);
        try {
            config = builder.getConfiguration();
            // config contains all properties read from the file
        } catch (ConfigurationException cex) {
            throw new ClientException(cex);
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

        final boolean res;
        try {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    //System.out.println(info.getName());
                    /*Metal Nimbus CDE/Motif Windows   Windows Classic  */
                    if ("Windows".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }
            fReception = new FReception(netProperty);
            Uses.setLocation(fReception);
            res = fReception.load();
        } catch (Exception ex) {
            Uses.closeSplash();
            throw new ClientException(ex);
        }

        // подключения плагинов, которые стартуют в самом начале.
        // поддержка расширяемости плагинами
        for (final IStartReception event : ServiceLoader.load(IStartReception.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                new Thread(() -> {
                    event.start(fReception);
                }).start();
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
        }

        Uses.closeSplash();
        if (res) {
            java.awt.EventQueue.invokeLater(() -> {
                try {
                    fReception.setVisible(true);
                } catch (Exception ex) {
                    Uses.closeSplash();
                    throw new ClientException(ex);
                } finally {
                    Uses.closeSplash();
                }
            });
        } else {
            System.exit(13);
        }
    }
    private QStandards standards;

    public boolean load() {
        checkBoxPrintAdvTicket.setSelected(config.getBoolean("reception.print_adv_ticket"));

        try {
            standards = NetCommander.getStandards(netProperty);
            System.out.println(standards);
        } catch (Exception ex) {
            Uses.closeSplash();
            QLog.l().logger().error("Не загрузились пользователи. ", ex); //NOI18N
            return false;
        }

        final LinkedList<QUser> users;
        try {
            users = NetCommander.getUsers(netProperty);
            listUsers.setModel(new DefaultComboBoxModel(users.toArray()));
        } catch (Exception ex) {
            Uses.closeSplash();
            QLog.l().logger().error("Не загрузились пользователи. ", ex); //NOI18N
            return false;
        }
        tableUsersMon.setModel(new UsersMonModel(users));

        try {
            final ServicesForWelcome servs = NetCommander.getServices(netProperty);
            final LinkedList<QService> slist = new LinkedList<>();
            QServiceTree.sailToStorm(servs.getRoot(), (TreeNode service) -> {
                slist.add((QService) service);
            });

            for (QService qService : slist) {
                qService.getChildren().stream().map((qService1) -> {
                    qService1.setParent(qService);
                    return qService1;
                }).forEach((qService1) -> {
                    qService1.setParentId(qService.getId());
                });
            }

            final ATreeModel tm = new ATreeModel<QService>() {

                @Override
                protected LinkedList<QService> load() {
                    return slist;
                }
            };
            treeServices.setModel(tm);
            setModelForComboBoxServices(tm);

            treeServices.setSelectionPath(treeServices.getPathForLocation(0, 0));
            showServiceInfo((QService) treeServices.getModel().getRoot());
        } catch (Exception ex) {
            Uses.closeSplash();
            QLog.l().logger().error("Не загрузились услуги. ", ex); //NOI18N
            return false;
        }

        panelComplexServ.removeAll();
        panelComplexServ.setLayout(new GridLayout(1, 1));
        // в темповый файл
        final File file;
        (new File(Uses.TEMP_FOLDER)).mkdir();
        file = new File(Uses.TEMP_FOLDER + File.separator + Uses.TEMP_COMPLEX_FILE);
        panelComplexServ.add(new PComplexService((ATreeModel) (treeServices.getModel()), file, netProperty));

        final LinkedList<ServiceInfo> srvs;
        try {
            srvs = NetCommander.getServerState(netProperty);
            int amt = 0;
            amt = srvs.stream().map((serviceInfo) -> serviceInfo.getCountWait()).reduce(amt, Integer::sum);
            labelTotalCustomers.setText("<html><span style='color:" + (amt > standards.getLineTotalMax() ? "red" : "green") + "'>" + getLocaleMessage("total.line") + " " + amt);
            tableServicesMon.setModel(new ServicesMonModel(srvs));

            tableServicesMon.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(table.getSelectedRow() == row ? table.getSelectionBackground() : Color.WHITE);
                    switch (column) {
                        case 0:
                            if (((Integer) value) > standards.getLineServiceMax()) {
                                c.setBackground(reddy);
                            }
                            break;
                        case 1:
                            if (((Integer) value) > standards.getWaitMax()) {
                                c.setBackground(reddy);
                            }
                            break;

                    }
                    return c;
                }
            });

            tableServicesMon.getColumnModel().getColumn(0).setMaxWidth(110);
            tableServicesMon.getColumnModel().getColumn(1).setMaxWidth(120);
            tableServicesMon.getColumnModel().getColumn(1).setMinWidth(120);
        } catch (Exception ex) {
            QLog.l().logger().error("Сервер ответил на запрос о состоянии: \"" + ex + "\""); //NOI18N
            tray.showMessageTray(getLocaleMessage("tray.server"), getLocaleMessage("tray.message.stop_server"), QTray.MessageType.WARNING); //NOI18N
            return false;
        }

        return true;
    }
    private final Color reddy = new Color(255, 230, 230);

    @Action
    public void setCurrentLang() {
        for (int i = 0; i < menuLangs.getItemCount(); i++) {
            if (((JRadioButtonMenuItem) menuLangs.getItem(i)).isSelected()) {
                Locales.getInstance().setLangCurrent(((JRadioButtonMenuItem) menuLangs.getItem(i)).getText());
            }
        }
    }

    @Action
    public void getAbout() {
        FAbout.showAbout(this, true);
    }

    @Action
    public void setInLine() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && service.isLeaf()) {

            if (service.getPreInfoHtml() != null && !service.getPreInfoHtml().isEmpty()) {
                JOptionPane.showMessageDialog(this, service.getPreInfoHtml(), "!!!", JOptionPane.INFORMATION_MESSAGE); //NOI18N
            }

            //Если услуга требует ввода данных пользователем, то нужно получить эти данные из диалога ввода
            String inputData = null;
            if (service.getInput_required()) {
                inputData = (String) JOptionPane.showInputDialog(this, service.getInput_caption().replaceAll("<[^>]*>", ""), "***", 3, null, null, ""); //NOI18N
                if (inputData == null || inputData.isEmpty()) {
                    return;
                }
            }

            final QCustomer customer;
            try {
                customer = NetCommander.standInService(netProperty, service.getId(), "1", 1, inputData); //NOI18N
            } catch (Exception ex) {
                throw new ClientException(getLocaleMessage("admin.print_ticket_error") + " " + ex);
            }
            FWelcome.printTicket(customer, ((QService) treeServices.getModel().getRoot()).getName());
            String pref = customer.getPrefix();
            pref = "".equals(pref) ? "" : pref + "-";
            JOptionPane.showMessageDialog(this, getLocaleMessage("admin.print_ticket.title") + " \"" + service.getName() + "\". " + getLocaleMessage("admin.print_ticket.title_1") + " \"" + pref + customer.getNumber() + "\".", getLocaleMessage("admin.print_ticket.caption"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Action
    public void preReg() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && service.isLeaf()) {
            comboBoxServices.setSelectedItem(service);
            tabsPane.setSelectedComponent(panelPrereg);
        }

    }

    private class QCustomer2 implements IidGetter {

        final private QCustomer customer;
        final private int nom;

        public QCustomer2(QCustomer customer, int nom) {
            this.customer = customer;
            this.nom = nom;
        }

        @Override
        public String toString() {
            final String priority;
            switch (customer.getPriority().get()) {
                case 0: {
                    priority = "[" + getLocaleMessage("messages.priority.low") + "]"; //NOI18N
                    break;
                }
                case 1: {
                    priority = "[" + getLocaleMessage("messages.priority.standart") + "]"; //NOI18N
                    break;
                }
                case 2: {
                    priority = "<span style='color:red'>[" + getLocaleMessage("messages.priority.hi") + "]</span>"; //NOI18N
                    break;
                }
                case 3: {
                    priority = "<span style='color:red'>[" + getLocaleMessage("messages.priority.vip") + "]</span>"; //NOI18N
                    break;
                }
                default: {
                    priority = "<span style='color:red'>[" + getLocaleMessage("messages.priority.strange") + "]</span>"; //NOI18N
                }
            }

            final long min = ((new Date().getTime() - customer.getStandTime().getTime()) / 1000 / 60);

            return "<html>" + nom + ".  " + customer.getPrefix() + customer.getNumber() + (customer.getPostponedStatus().isEmpty() ? "" : " " + customer.getPostponedStatus()
                    + (customer.getPostponPeriod() > 0 ? " (" + customer.getPostponPeriod() + "min.)   " : "  "))
                    + (customer.getInput_data().isEmpty() ? "" : "  <u>" + customer.getInput_data()) + "</u>    "
                    + /*getLocaleMessage("messages.priority") + ": " +*/ priority + "  "
                    + "<span style='color:" + (min <= standards.getWaitMax() ? "green" : "red") + "'>" + " - " + getLocaleMessage("waiting") + " " + min + " " + getLocaleMessage("mints2") + "</span>";
        }

        @Override
        public String getName() {
            return customer.getName();
        }

        @Override
        public Long getId() {
            return (long) nom;
        }
    }

    @Action
    public void refreshLines() {
        final QService service = (QService) treeServices.getLastSelectedPathComponent();
        if (service != null && service.isLeaf()) {
            final ServiceState customers;
            try {
                customers = NetCommander.getServiceConsistency(netProperty, service.getId());
            } catch (QException ex) {
                throw new ClientException(getLocaleMessage("admin.print_ticket_error") + " " + ex);
            }
            final ATListModel<QCustomer> lm = new ATListModel<QCustomer>() {

                @Override
                protected LinkedList load() {
                    final LinkedList<QCustomer2> c2 = new LinkedList<>();
                    int i = 1;
                    for (QCustomer qCustomer : customers.getClients()) {
                        c2.add(new QCustomer2(qCustomer, i++));
                    }
                    return c2;
                }
            };
            listLine.setModel(lm);
            if (customers.getClients().size() > standards.getLineServiceMax()) {
                labelWarringOfLineSize.setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/attention.png"), ""));
                JOptionPane.showMessageDialog(this, getLocaleMessage("break.line.standard"), getLocaleMessage("attention"), JOptionPane.WARNING_MESSAGE);
            } else {
                labelWarringOfLineSize.setIcon(null);
            }

        }
    }

    @Action
    public void setPriority() {
        final QCustomer2 cus = (QCustomer2) listLine.getSelectedValue();
        if (cus != null) {
            final String name = (String) JOptionPane.showInputDialog(this,
                    getLocaleMessage("admin.action.change_priority.get.message"), //NOI18N
                    getLocaleMessage("admin.action.change_priority.get.title"), //NOI18N
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Uses.get_PRIORITYS_WORD().values().toArray(),
                    Uses.get_PRIORITYS_WORD().values().toArray()[1]);
            //Если не выбрали, то выходим
            if (name != null) {
                for (int i = 0; i < Uses.get_PRIORITYS_WORD().size(); i++) {
                    if (name.equals(Uses.get_PRIORITYS_WORD().get(i))) {
                        JOptionPane.showMessageDialog(this, NetCommander.setCustomerPriority(netProperty, i, cus.customer.getFullNumber()), getLocaleMessage("admin.action.change_priority.title"), JOptionPane.INFORMATION_MESSAGE);
                        refreshLines();
                    }
                }
            }
        }
    }

    @Action
    public void setAnyPriority() {
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
                        JOptionPane.showMessageDialog(this, NetCommander.setCustomerPriority(netProperty, i, num), getLocaleMessage("admin.action.change_priority.title"), JOptionPane.INFORMATION_MESSAGE);

                    }
                }
            }
        }
    }

    @Action
    public void checkAnyTicket() {
        final String num = (String) JOptionPane.showInputDialog(this, getLocaleMessage("admin.action.change_priority.num.message"), getLocaleMessage("admin.action.change_priority.num.title"), 3, null, null, "");
        if (num != null) {
            final FInfoByTicketNumber f = new FInfoByTicketNumber(this, true);
            f.setTitle(num.toUpperCase());
            Uses.setLocation(f);
            RpcGetTicketHistory.TicketHistory t = NetCommander.checkCustomerNumber(netProperty, num);
            f.setInfo(t.getInfo());
            StringBuilder sb = new StringBuilder("<html>");
            t.getCusts().stream().forEach((c) -> {
                sb.append(c).append("<br>");
            });
            f.setHistory(sb.toString());
            sb.setLength(0);
            f.setVisible(true);
            //JOptionPane.showMessageDialog(this, NetCommander.checkCustomerNumber(netProperty, num), getLocaleMessage("admin.action.change_priority.num.title"), JOptionPane.INFORMATION_MESSAGE);
        }
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

    @Action
    public void changeStatusForPostponed() {
        if (listPostponed.getSelectedIndex() != -1) {
            final QCustomer cust = (QCustomer) listPostponed.getSelectedValue();

            String status = (String) JOptionPane.showInputDialog(this, getLocaleMessage("resultwork.dialog.caption"), getLocaleMessage("resultwork.dialog.title"), JOptionPane.QUESTION_MESSAGE, null, getResults(), null);
            if (status == null) {
                return;
            }
            NetCommander.postponeCustomerChangeStatus(netProperty, cust.getId(), status);
            buttonRefreshPostponedActionPerformed(null);
        }
    }

    @Action
    public void refreshMainData() {
        load();
    }

    @Action
    public void sendMessage() {
        FMessager.getMessager(this, netProperty.getClientPort(), listUsers.getModel(), treeServices.getModel());
    }

    @Action
    public void serviceDisable() {
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
                    NetCommander.changeTempAvailableService(netProperty, service.getId(), "");
                } else {
                    final String mess = (String) JOptionPane.showInputDialog(this,
                            getLocaleMessage("admin.ability.enter_reason"),
                            getLocaleMessage("admin.select_ability.title"),
                            JOptionPane.QUESTION_MESSAGE);
                    if (mess != null) {
                        NetCommander.changeTempAvailableService(netProperty, service.getId(), mess);
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
    private static FReception fReception;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar MenuBar;
    public javax.swing.JButton btnPushToTalk;
    private javax.swing.JButton buttonRefreshBlack;
    private javax.swing.JButton buttonRefreshMainData;
    private javax.swing.JButton buttonRefreshPostponed;
    private javax.swing.JButton buttonRefreshUser;
    private javax.swing.JButton buttonRemoveAdvanceCustomer;
    private com.toedter.calendar.JCalendar calPrereg;
    private javax.swing.JCheckBox checkBoxPrintAdvTicket;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JLabel labelPreDate;
    private javax.swing.JLabel labelServiceInfo;
    private javax.swing.JLabel labelTotalCustomers;
    private javax.swing.JLabel labelUser;
    private javax.swing.JLabel labelUserInfo;
    private javax.swing.JLabel labelWarringOfLineSize;
    private javax.swing.JList listBlack;
    private javax.swing.JList listLine;
    private javax.swing.JList listPostponed;
    private javax.swing.JList listServicesForUser;
    private javax.swing.JList listUsers;
    private javax.swing.JList listUsersOfService;
    private javax.swing.JMenu menuAbout;
    private javax.swing.JMenu menuCustomers;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemAdv;
    private javax.swing.JMenuItem menuItemChangePriority;
    private javax.swing.JMenuItem menuItemCheckTicket;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemServDisable;
    private javax.swing.JMenuItem menuItemStand;
    private javax.swing.JMenu menuLangs;
    private javax.swing.JMenuItem menuRefreshMainData;
    private javax.swing.JMenuItem menuSendMessage;
    private javax.swing.JMenuItem menuSetPriority;
    private javax.swing.JMenuItem miAdvanceToLine;
    private javax.swing.JMenuItem miStandadvance;
    private javax.swing.JPanel panelComplexServ;
    private javax.swing.JPanel panelLineState;
    private javax.swing.JPanel panelPrereg;
    private javax.swing.JPanel panelServices;
    private javax.swing.JPanel panelTreeCmbx;
    private javax.swing.JPanel panelUsers;
    private javax.swing.JPopupMenu popupLineList;
    private javax.swing.JPopupMenu popupMenuAdvance;
    private javax.swing.JPopupMenu popupPostponed;
    private javax.swing.JPopupMenu popupServiceTree;
    private javax.swing.JTabbedPane tabbedPaneService;
    private javax.swing.JTable tablePreReg;
    private javax.swing.JTable tableServicesMon;
    private javax.swing.JTable tableUsersMon;
    public javax.swing.JTabbedPane tabsPane;
    private javax.swing.JTextField textFieldSerchService;
    private javax.swing.JTree treeServices;
    // End of variables declaration//GEN-END:variables
}
