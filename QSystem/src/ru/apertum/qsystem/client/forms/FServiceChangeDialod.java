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

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreeNode;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WysiwygDlg;
import ru.apertum.qsystem.client.model.JTreeComboBox;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QOfficeList;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceLang;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.calendar.QCalendar;
import ru.apertum.qsystem.server.model.schedule.QSchedule;

/**
 * Created on 27.08.2009, 11:13:04
 *
 * @author Evgeniy Egorov
 */
public class FServiceChangeDialod extends javax.swing.JDialog {

    private static ResourceMap localeMap = null;
    private static FServiceChangeDialod serviceChangeDialod;
    JTreeComboBox comboBoxServices;
    QServiceLang serviceLng;
    private QService service;
    private List<QOffice> officesToAdd;
    private List<QOffice> officesToRemove;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton WysiwygButton;
    private javax.swing.ButtonGroup bgGo;
    private javax.swing.ButtonGroup bgGong;
    private javax.swing.JButton butWYS2;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonSave;
    private javax.swing.JCheckBox cbClient;
    private javax.swing.JCheckBox cbClientNumber;
    private javax.swing.JCheckBox cbGoNumber;
    private javax.swing.JCheckBox cbRequaredAsColumnExt;
    private javax.swing.JCheckBox cbSound;
    private javax.swing.JCheckBox checkBoxBackoffice;
    private javax.swing.JCheckBox checkBoxInputRequired;
    private javax.swing.JCheckBox checkBoxResultRequired;
    private javax.swing.JComboBox comboBoxCalendar;
    private javax.swing.JComboBox comboBoxEnabled;
    private javax.swing.JComboBox comboBoxPeriod;
    private javax.swing.JComboBox comboBoxSchedule;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelCaptionButton;
    private javax.swing.JLabel labelDayLimit;
    private javax.swing.JLabel labelInfoDialog;
    private javax.swing.JLabel labelLimitForOnePerson;
    private javax.swing.JLabel labelLink;
    private javax.swing.JLabel labelTabloText;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelProps;
    private javax.swing.JPanel panelTreeCmbx;
    private javax.swing.JRadioButton rbCabinet;
    private javax.swing.JRadioButton rbGong;
    private javax.swing.JRadioButton rbGongFirstOnly;
    private javax.swing.JRadioButton rbNoGo;
    private javax.swing.JRadioButton rbNoGong;
    private javax.swing.JRadioButton rbStoika;
    private javax.swing.JRadioButton rbTable;
    private javax.swing.JRadioButton rbWindow;
    private javax.swing.JSpinner spinButB;
    private javax.swing.JSpinner spinButH;
    private javax.swing.JSpinner spinButX;
    private javax.swing.JSpinner spinButY;
    private javax.swing.JSpinner spinnerDayLimit;
    private javax.swing.JSpinner spinnerDuration;
    private javax.swing.JSpinner spinnerExpectation;
    private javax.swing.JSpinner spinnerLimit;
    private javax.swing.JSpinner spinnerLimitForOnePerson;
    private javax.swing.JSpinner spinnerLimitPeriod;
    private javax.swing.JSpinner spinnerPunktReg;
    private javax.swing.JTextArea textAreaButtonCaption;
    private javax.swing.JTextArea textAreaInfoHtml;
    private javax.swing.JTextArea textAreaTextPrint;
    private javax.swing.JTextField textFieldInputCaption;
    private javax.swing.JTextField textFieldPrefix;
    private javax.swing.JTextField textFieldServiceDescript;
    private javax.swing.JTextField textFieldServiceName;
    private javax.swing.JTextField textFieldTicketText;
    private javax.swing.JTextField tfID;
    private javax.swing.JTextField tfTabloText;
    private javax.swing.JCheckBox jServiceSmartboardYnCheckbox;
    private javax.swing.JPanel jServiceOfficePanel;
    private javax.swing.JList jOfficeSelected;
    private javax.swing.JList jOfficeAvailable;
    private javax.swing.JButton jRemoveSelectedOfficeButton;
    private javax.swing.JButton jAddSelectedOfficeButton;
    private javax.swing.JPanel jServiceOfficeButtonPanel;
    private javax.swing.JButton jAddAllOfficesButton;
    private javax.swing.JButton jRemoveAllOfficesButton;

    /**
     * Creates new form FServiceChangeDialod
     *
     * @param parent родительская форма
     * @param modal модальность
     */
    public FServiceChangeDialod(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(FServiceChangeDialod.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Основной метод редактирования услуги.
     *
     * @param parent родительская форма
     * @param modal модальность
     * @param service Услуга для редактирования в XML-виде
     */
    public static void changeService(Frame parent, boolean modal, QService service,
        ComboBoxModel scheduleModel, ComboBoxModel calendarModel) {
        QLog.l().logger().info("Редактирование услуги \"" + service.getName() + "\"");
        if (serviceChangeDialod == null) {
            serviceChangeDialod = new FServiceChangeDialod(parent, modal);
        }

        serviceChangeDialod.comboBoxServices = new JTreeComboBox(QServiceTree.getInstance());
        serviceChangeDialod.comboBoxServices.setBorder(new BevelBorder(1));
        serviceChangeDialod.comboBoxServices.addActionListener((ActionEvent e) -> {
            QService ser = (QService) serviceChangeDialod.comboBoxServices.getSelectedItem();
            if (ser != null && !ser.isLeaf()) {
                serviceChangeDialod.comboBoxServices.setSelectedItem(null);
            }
        });
        // cmp.setSize(250, 24);
        serviceChangeDialod.panelTreeCmbx.removeAll();
        serviceChangeDialod.panelTreeCmbx.setLayout(new GridLayout(1, 1));
        serviceChangeDialod.panelTreeCmbx.add(serviceChangeDialod.comboBoxServices);

        serviceChangeDialod.setTitle(getLocaleMessage("dialog.title") + "  ID " + service.getId());
        serviceChangeDialod.tfID.setText(service.getId().toString());
        serviceChangeDialod.comboBoxSchedule.setModel(scheduleModel);
        serviceChangeDialod.comboBoxCalendar.setModel(calendarModel);
        serviceChangeDialod.loadService(service);
        Uses.setLocation(serviceChangeDialod);

        DefaultListModel officesAvailableModel = new DefaultListModel();
        DefaultListModel officesSelectedModel = new DefaultListModel();

        QOfficeList officeList = QOfficeList.getInstance();

        for (QOffice office : officeList.getItems()) {
            officesAvailableModel.addElement(office);
        }

        for (QOffice office : service.getOffices()) {
            officesSelectedModel.addElement(office);
        }

        serviceChangeDialod.jOfficeAvailable.setModel(officesAvailableModel);
        serviceChangeDialod.jOfficeSelected.setModel(officesSelectedModel);
        serviceChangeDialod.setVisible(true);
    }

    public static void changeServiceLang(Frame parent, boolean modal, QServiceLang serviceLng) {
        QLog.l().logger().info("Редактирование многоязычности услуги \"" + serviceLng + "\"");
        if (serviceChangeDialod == null) {
            serviceChangeDialod = new FServiceChangeDialod(parent, modal);

        }
        serviceChangeDialod.setTitle(serviceLng.toString());
        serviceChangeDialod.loadServiceLang(serviceLng);
        Uses.setLocation(serviceChangeDialod);
        serviceChangeDialod.setVisible(true);
    }

    private void init() {
        // Фича. По нажатию Escape закрываем форму
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
                setVisible(false);
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public QService getSelectedService() {
        final QService ser = (QService) comboBoxServices.getSelectedItem();
        return ser != null && ser.isLeaf() ? ser : null;
    }

    private void loadService(QService service) {
        comboBoxServices.setSelectedItem(service.getLink());
        this.service = service;
        this.serviceLng = null;
        textFieldPrefix.setText(service.getPrefix());
        //textFieldPrefix.setEditable(service.isLeaf());
        textFieldServiceName.setText(service.getName());
        textFieldServiceName.setEditable(service.isRoot());
        textFieldServiceDescript.setText(service.getDescription());
        textAreaButtonCaption.setText(service.getButtonText());
        textAreaInfoHtml.setText(service.getPreInfoHtml());
        textAreaTextPrint.setText(service.getPreInfoPrintText());
        if (service.getStatus() >= 2) {
            comboBoxEnabled.setSelectedIndex(service.getStatus() + 1);
        } else {
            comboBoxEnabled.setSelectedIndex(service.getStatus() * (-1) + 1);
        }
        comboBoxEnabled.setEnabled(!service.isRoot());
        comboBoxPeriod.setSelectedIndex((service.getAdvanceTimePeriod() - 30) / 15);
        spinnerDayLimit.setValue(service.getDayLimit());
        spinnerLimitForOnePerson.setValue(service.getPersonDayLimit());
        spinnerLimit.setValue(service.getAdvanceLimit());
        spinnerLimitPeriod.setValue(service.getAdvanceLimitPeriod());
        spinnerDuration.setValue(service.getDuration());
        spinnerExpectation.setValue(service.getExpectation());
        if (service.getSchedule() == null) {
            comboBoxSchedule.setSelectedIndex(-1);
        } else {
            comboBoxSchedule.getModel().setSelectedItem(service.getSchedule());
        }
        if (service.getCalendar() == null) {
            comboBoxCalendar.setSelectedIndex(-1);
        } else {
            comboBoxCalendar.getModel().setSelectedItem(service.getCalendar());
        }
        if ("Y".equals(service.getSmartboard())) {
            jServiceSmartboardYnCheckbox.setSelected(true);
        } else {
            jServiceSmartboardYnCheckbox.setSelected(false);
        }
        checkBoxInputRequired.setSelected(service.getInput_required());
        textFieldInputCaption.setText(service.getInput_caption());
        cbRequaredAsColumnExt.setSelected(service.getInputedAsExt());
        textFieldTicketText.setText(service.getTicketText());
        tfTabloText.setText(service.getTabloText());
        checkBoxResultRequired.setSelected(service.getResult_required());
        checkBoxBackoffice.setSelected(service.getEnable() != 1);
        spinnerPunktReg.setValue(service.getPoint());

        spinButX.setValue(service.getButX());
        spinButY.setValue(service.getButY());
        spinButB.setValue(service.getButB());
        spinButH.setValue(service.getButH());

        setHide(true);
        textAreaButtonCaptionKeyPressed(null);
        textAreaInfoHtmlKeyReleased(null);
        // Загрузим шаблон оповещения
        cbSound.setSelected(
            service.getSoundTemplate() == null ? false
                : (service.getSoundTemplate().startsWith("1")));
        if (service.getSoundTemplate() != null) {
            if (service.getSoundTemplate().length() > 1) {
                switch (service.getSoundTemplate().substring(1, 2)) {
                    case "1":
                        rbNoGong.setSelected(true);
                        break;
                    case "2":
                        rbGong.setSelected(true);
                        break;
                    case "3":
                        rbGongFirstOnly.setSelected(true);
                        break;
                    default:
                        rbNoGong.setSelected(true);
                }
            }
            if (service.getSoundTemplate().length() > 2) {
                cbClient.setSelected("1".equals(service.getSoundTemplate().substring(2, 3)));
            }
            if (service.getSoundTemplate().length() > 3) {
                cbClientNumber.setSelected("1".equals(service.getSoundTemplate().substring(3, 4)));
            }
            if (service.getSoundTemplate().length() > 4) {
                switch (service.getSoundTemplate().substring(4, 5)) {
                    case "1":
                        rbCabinet.setSelected(true);
                        break;
                    case "2":
                        rbWindow.setSelected(true);
                        break;
                    case "3":
                        rbStoika.setSelected(true);
                        break;
                    case "4":
                        rbTable.setSelected(true);
                        break;
                    case "5":
                        rbNoGo.setSelected(true);
                        break;
                    default:
                        rbNoGo.setSelected(true);
                }
            }
            if (service.getSoundTemplate().length() > 5) {
                cbGoNumber.setSelected(service.getSoundTemplate().endsWith("1"));
            }
        }

        this.officesToAdd = new LinkedList<QOffice>();
        this.officesToRemove = new LinkedList<QOffice>();
    }

    private void saveService() {
        if ("".equals(textAreaButtonCaption.getText())) {
            throw new ClientException(getLocaleMessage("dialog.message1"));
        }
        if (textAreaButtonCaption.getText().length() > 2500) {
            throw new ClientException(getLocaleMessage("dialog.message2"));
        }
        if (textAreaInfoHtml.getText().length() > 100000) {
            throw new ClientException(getLocaleMessage("dialog.message3"));
        }
        if (textAreaTextPrint.getText().length() > 100000) {
            throw new ClientException(getLocaleMessage("dialog.message4"));
        }
        if (textAreaButtonCaption.getText().length() < 2500 && !""
            .equals(textAreaButtonCaption.getText())) {
            service.setButtonText(textAreaButtonCaption.getText());
        }

        final ArrayList<Boolean> f = new ArrayList<>();
        QServiceTree.sailToStorm(QServiceTree.getInstance().getRoot(), (TreeNode srvc) -> {
            if (!service.equals(srvc) && srvc.isLeaf()) {
                final String pr = ((QService) srvc).getPrefix();
                if (!pr.isEmpty() && pr.equalsIgnoreCase(textFieldPrefix.getText())) {
                    f.add(true);
                }
            }
        });
        if (!f.isEmpty()) {
            if (JOptionPane
                .showConfirmDialog(null, getLocaleMessage("dialog.message5"),
                    getLocaleMessage("confirm"),
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                throw new ServerException(getLocaleMessage("dialog.message5"));
            }
        }
        service.setPrefix(textFieldPrefix.getText());
        service.setAdvanceTimePeriod(comboBoxPeriod.getSelectedIndex() * 15 + 30);
        service.setName(textFieldServiceName.getText());
        service.setDescription(textFieldServiceDescript.getText());
        service.setDayLimit((Integer) spinnerDayLimit.getValue());
        service.setPersonDayLimit((Integer) spinnerLimitForOnePerson.getValue());
        service.setAdvanceLinit((Integer) spinnerLimit.getValue());
        service.setDuration((Integer) spinnerDuration.getValue());
        service.setExpectation((Integer) spinnerExpectation.getValue());
        service.setAdvanceLimitPeriod(
            (Integer) spinnerLimitPeriod.getValue() < 0 ? 0
                : (Integer) spinnerLimitPeriod.getValue());

        if (comboBoxEnabled.getSelectedIndex() >= 3) {
            service.setStatus(comboBoxEnabled.getSelectedIndex() - 1);
        } else {
            service.setStatus((comboBoxEnabled.getSelectedIndex() - 1) * (-1));
        }
        service.setSchedule((QSchedule) comboBoxSchedule.getModel().getSelectedItem());
        service.setCalendar((QCalendar) comboBoxCalendar.getModel().getSelectedItem());
        if (jServiceSmartboardYnCheckbox.isSelected()) {
            service.setSmartboard("Y");
        } else {
            service.setSmartboard("N");
        }
        service.setInput_required(checkBoxInputRequired.isSelected());
        service.setInput_caption(textFieldInputCaption.getText());
        service.setInputedAsExt(cbRequaredAsColumnExt.isSelected());
        service.setResult_required(checkBoxResultRequired.isSelected());
        service.setEnable(checkBoxBackoffice.isSelected() ? 2 : 1);
        service.setPoint((Integer) spinnerPunktReg.getModel().getValue());
        service.setPreInfoHtml(textAreaInfoHtml.getText());
        service.setPreInfoPrintText(textAreaTextPrint.getText());
        service.setTicketText(textFieldTicketText.getText());
        service.setTabloText(tfTabloText.getText());

        service.setButX((Integer) spinButX.getValue());
        service.setButY((Integer) spinButY.getValue());
        service.setButB((Integer) spinButB.getValue());
        service.setButH((Integer) spinButH.getValue());

        // сохраним шаблон оповещения
        final String tmpl = (cbSound.isSelected() ? "1" : "0")
            + (rbNoGong.isSelected() ? "1" : (rbGong.isSelected() ? "2" : "3"))
            + (cbClient.isSelected() ? "1" : "0")
            + (cbClientNumber.isSelected() ? "1" : "0")
            + (rbCabinet.isSelected() ? "1" : (rbWindow.isSelected() ? "2"
            : (rbStoika.isSelected() ? "3" : (rbTable.isSelected() ? "4" : "5"))))
            + (cbGoNumber.isSelected() ? "1" : "0");
        service.setSoundTemplate(tmpl);

        service.setLink(getSelectedService());

        //Remove the requested offices
        for (QOffice o : officesToRemove) {
            QLog.l().logger().info("Removing office: " + o);
            if (service.getOffices().contains(o)) {
                QLog.l().logger().info("Service contains office to remove");

                if (!officesToAdd.contains(o)) {
                    QLog.l().logger()
                        .info("Office will not be re-added in next step... remove it!");
                    service.getOffices().remove(o);
                } else {
                    QLog.l().logger().info("Office would be re-added in next step... continue");
                }

            } else {
                QLog.l().logger().info("Service does not contain office, continue");
            }
        }

        //Add the requested offices
        for (QOffice o : officesToAdd) {
            QLog.l().logger().info("Adding office: " + o);

            if (!service.getOffices().contains(o)) {
                QLog.l().logger().info("Service does not already have office... add it!");
                service.getOffices().add(o);
            } else {
                QLog.l().logger().info("Service already has office... continue");
            }

        }
    }

    private void setHide(boolean b) {
        textFieldPrefix.setEnabled(b);
        comboBoxEnabled.setEnabled(b);
        comboBoxPeriod.setEnabled(b);
        spinnerDayLimit.setEnabled(b);
        spinnerLimitForOnePerson.setEnabled(b);
        spinnerLimit.setEnabled(b);
        spinnerLimitPeriod.setEnabled(b);
        comboBoxSchedule.setEnabled(b);
        comboBoxCalendar.setEnabled(b);
        checkBoxInputRequired.setEnabled(b);
        checkBoxResultRequired.setEnabled(b);
        checkBoxBackoffice.setEnabled(b);
        spinnerPunktReg.setEnabled(b);
    }

    private void loadServiceLang(QServiceLang serviceLng) {
        this.serviceLng = serviceLng;
        this.service = null;
        textFieldServiceName.setText(serviceLng.getName());
        textFieldServiceDescript.setText(serviceLng.getDescription());
        textAreaButtonCaption.setText(serviceLng.getButtonText());
        textAreaInfoHtml.setText(serviceLng.getPreInfoHtml());
        textAreaTextPrint.setText(serviceLng.getPreInfoPrintText());
        textFieldInputCaption.setText(serviceLng.getInput_caption());
        textFieldTicketText.setText(serviceLng.getTicketText());
        tfTabloText.setText(serviceLng.getTicketText());

        setHide(false);
        textFieldInputCaption.setEnabled(true);
        textFieldServiceName.setEnabled(true);
        textFieldServiceName.setEditable(true);

        textAreaButtonCaptionKeyPressed(null);
        textAreaInfoHtmlKeyReleased(null);
    }

    private void saveServiceLang() {
        if ("".equals(textAreaButtonCaption.getText())) {
            throw new ClientException(getLocaleMessage("dialog.message1"));
        }
        if (textAreaButtonCaption.getText().length() > 2500) {
            throw new ClientException(getLocaleMessage("dialog.message2"));
        }
        if (textAreaInfoHtml.getText().length() > 100000) {
            throw new ClientException(getLocaleMessage("dialog.message3"));
        }
        if (textAreaTextPrint.getText().length() > 100000) {
            throw new ClientException(getLocaleMessage("dialog.message4"));
        }
        if (textAreaButtonCaption.getText().length() < 2500 && !""
            .equals(textAreaButtonCaption.getText())) {
            serviceLng.setButtonText(textAreaButtonCaption.getText());
        }

        serviceLng.setName(textFieldServiceName.getText());
        serviceLng.setDescription(textFieldServiceDescript.getText());
        serviceLng.setInput_caption(textFieldInputCaption.getText());
        serviceLng.setPreInfoHtml(textAreaInfoHtml.getText());
        serviceLng.setPreInfoPrintText(textAreaTextPrint.getText());
        serviceLng.setTicketText(textFieldTicketText.getText());
        serviceLng.setTabloText(tfTabloText.getText());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        bgGong = new javax.swing.ButtonGroup();
        bgGo = new javax.swing.ButtonGroup();
        panelProps = new javax.swing.JPanel();
        comboBoxEnabled = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        textFieldPrefix = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        textFieldServiceName = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        textFieldServiceDescript = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        spinnerLimit = new javax.swing.JSpinner();
        spinnerLimitPeriod = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        checkBoxResultRequired = new javax.swing.JCheckBox();
        checkBoxInputRequired = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        textFieldInputCaption = new javax.swing.JTextField();
        comboBoxSchedule = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        comboBoxCalendar = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        spinnerDayLimit = new javax.swing.JSpinner();
        labelDayLimit = new javax.swing.JLabel();
        labelLimitForOnePerson = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        textFieldTicketText = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        comboBoxPeriod = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        spinnerDuration = new javax.swing.JSpinner();
        jLabel20 = new javax.swing.JLabel();
        spinnerExpectation = new javax.swing.JSpinner();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        spinnerLimitForOnePerson = new javax.swing.JSpinner();
        cbRequaredAsColumnExt = new javax.swing.JCheckBox();
        tfTabloText = new javax.swing.JTextField();
        labelTabloText = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        labelCaptionButton = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaButtonCaption = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        spinButX = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        spinButY = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        spinButB = new javax.swing.JSpinner();
        jLabel17 = new javax.swing.JLabel();
        spinButH = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        WysiwygButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaTextPrint = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaInfoHtml = new javax.swing.JTextArea();
        butWYS2 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        labelInfoDialog = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        cbSound = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        rbNoGong = new javax.swing.JRadioButton();
        rbGong = new javax.swing.JRadioButton();
        rbGongFirstOnly = new javax.swing.JRadioButton();
        cbClient = new javax.swing.JCheckBox();
        cbClientNumber = new javax.swing.JCheckBox();
        cbGoNumber = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        rbCabinet = new javax.swing.JRadioButton();
        rbWindow = new javax.swing.JRadioButton();
        rbStoika = new javax.swing.JRadioButton();
        rbTable = new javax.swing.JRadioButton();
        rbNoGo = new javax.swing.JRadioButton();
        checkBoxBackoffice = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        spinnerPunktReg = new javax.swing.JSpinner();
        tfID = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        labelLink = new javax.swing.JLabel();
        panelTreeCmbx = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        panelButtons = new javax.swing.JPanel();
        buttonSave = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        jServiceSmartboardYnCheckbox = new javax.swing.JCheckBox();
        jServiceSmartboardYnCheckbox.setText("Display for SmartBoards?");

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FServiceChangeDialod.class);
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        panelProps.setBorder(new javax.swing.border.MatteBorder(null));
        panelProps.setName("panelProps"); // NOI18N

        comboBoxEnabled.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{"Активна", "Неактивна", "Невидима", "Только предварительная запись",
                "Заглушка"}));
        comboBoxEnabled.setName("comboBoxEnabled"); // NOI18N
        comboBoxEnabled.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{getLocaleMessage("active"), getLocaleMessage("notActive"),
                getLocaleMessage("invisible"), getLocaleMessage("forPrereg"),
                getLocaleMessage("forDummy")}));

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        textFieldPrefix.setText(resourceMap.getString("textFieldPrefix.text")); // NOI18N
        textFieldPrefix.setName("textFieldPrefix"); // NOI18N

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        textFieldServiceName.setText(resourceMap.getString("textFieldServiceName.text")); // NOI18N
        textFieldServiceName.setName("textFieldServiceName"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        textFieldServiceDescript
            .setText(resourceMap.getString("textFieldServiceDescript.text")); // NOI18N
        textFieldServiceDescript.setName("textFieldServiceDescript"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        spinnerLimit.setName("spinnerLimit"); // NOI18N

        spinnerLimitPeriod.setName("spinnerLimitPeriod"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        checkBoxResultRequired
            .setText(resourceMap.getString("checkBoxResultRequired.text")); // NOI18N
        checkBoxResultRequired.setName("checkBoxResultRequired"); // NOI18N

        checkBoxInputRequired
            .setText(resourceMap.getString("checkBoxInputRequired.text")); // NOI18N
        checkBoxInputRequired.setName("checkBoxInputRequired"); // NOI18N
        checkBoxInputRequired.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkBoxInputRequiredStateChanged(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        textFieldInputCaption
            .setText(resourceMap.getString("textFieldInputCaption.text")); // NOI18N
        textFieldInputCaption.setName("textFieldInputCaption"); // NOI18N

        comboBoxSchedule.setModel(
            new javax.swing.DefaultComboBoxModel(
                new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        comboBoxSchedule.setName("comboBoxSchedule"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        comboBoxCalendar.setModel(
            new javax.swing.DefaultComboBoxModel(
                new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        comboBoxCalendar.setName("comboBoxCalendar"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        spinnerDayLimit.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinnerDayLimit.setName("spinnerDayLimit"); // NOI18N

        labelDayLimit.setText(resourceMap.getString("labelDayLimit.text")); // NOI18N
        labelDayLimit.setName("labelDayLimit"); // NOI18N

        labelLimitForOnePerson
            .setText(resourceMap.getString("labelLimitForOnePerson.text")); // NOI18N
        labelLimitForOnePerson.setName("labelLimitForOnePerson"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        textFieldTicketText.setText(resourceMap.getString("textFieldTicketText.text")); // NOI18N
        textFieldTicketText.setName("textFieldTicketText"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        comboBoxPeriod.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{"30", "45", "60", "75", "90", "105", "120", "135", "150"}));
        comboBoxPeriod.setName("comboBoxPeriod"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        spinnerDuration.setModel(new javax.swing.SpinnerNumberModel(1, 1, 480, 1));
        spinnerDuration.setName("spinnerDuration"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        spinnerExpectation.setModel(new javax.swing.SpinnerNumberModel(0, 0, 60, 1));
        spinnerExpectation.setName("spinnerExpectation"); // NOI18N

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jLabel28.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        spinnerLimitForOnePerson.setModel(new javax.swing.SpinnerNumberModel(0, 0, 999, 1));
        spinnerLimitForOnePerson.setName("spinnerLimitForOnePerson"); // NOI18N

        cbRequaredAsColumnExt
            .setText(resourceMap.getString("cbRequaredAsColumnExt.text")); // NOI18N
        cbRequaredAsColumnExt.setName("cbRequaredAsColumnExt"); // NOI18N

        tfTabloText.setText(resourceMap.getString("tfTabloText.text")); // NOI18N
        tfTabloText.setName("tfTabloText"); // NOI18N

        labelTabloText.setText(resourceMap.getString("labelTabloText.text")); // NOI18N
        labelTabloText.setName("labelTabloText"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(labelDayLimit)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerDayLimit,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 44,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel28))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(
                                                    javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(checkBoxResultRequired)
                                                    .addComponent(jServiceSmartboardYnCheckbox)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(jLabel8)
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(spinnerLimitPeriod,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            43,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGroup(jPanel3Layout.createParallelGroup(
                                                            javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel19)
                                                            .addComponent(jLabel26))
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel3Layout.createParallelGroup(
                                                            javax.swing.GroupLayout.Alignment.LEADING,
                                                            false)
                                                            .addComponent(spinnerExpectation,
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(spinnerDuration,
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                54, Short.MAX_VALUE))
                                                        .addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jPanel3Layout.createParallelGroup(
                                                            javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel20)
                                                            .addComponent(jLabel25))))
                                                .addGap(128, 128, 128))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(spinnerLimit,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE, 44,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel11)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(comboBoxPeriod,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12)))
                                .addContainerGap(192, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel3Layout.createSequentialGroup()
                                    .addGap(48, 48, 48)
                                    .addComponent(textFieldInputCaption)
                                    .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout
                                        .createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                            jPanel3Layout.createSequentialGroup()
                                                .addComponent(labelTabloText)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(tfTabloText))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(comboBoxSchedule, 0, 709,
                                                Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                            jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(comboBoxCalendar, 0, 719,
                                                    Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                            jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(textFieldTicketText,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE, 692,
                                                    Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                            jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(
                                                    javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGap(48, 48, 48)
                                                        .addGroup(jPanel3Layout.createParallelGroup(
                                                            javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel4)
                                                            .addComponent(cbRequaredAsColumnExt)
                                                            .addGroup(
                                                                jPanel3Layout
                                                                    .createSequentialGroup()
                                                                    .addComponent(
                                                                        labelLimitForOnePerson)
                                                                    .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(
                                                                        spinnerLimitForOnePerson,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        50,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                    .addComponent(checkBoxInputRequired))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                    .addGap(18, 18, 18))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelDayLimit)
                            .addComponent(spinnerDayLimit, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(spinnerLimit, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(comboBoxPeriod, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(spinnerLimitPeriod,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(spinnerDuration, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel26)
                            .addComponent(spinnerExpectation,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jServiceSmartboardYnCheckbox)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(checkBoxResultRequired)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(checkBoxInputRequired)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel4)
                    .addGap(2, 2, 2)
                    .addComponent(textFieldInputCaption, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelLimitForOnePerson)
                            .addComponent(spinnerLimitForOnePerson,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cbRequaredAsColumnExt)
                    .addGap(18, 18, 18)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxSchedule, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(comboBoxCalendar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textFieldTicketText,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfTabloText, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelTabloText))
                    .addContainerGap(18, Short.MAX_VALUE))
        );

        jTabbedPane1
            .addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jSplitPane2.setDividerLocation(181);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setAutoscrolls(true);
        jSplitPane2.setContinuousLayout(true);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        labelCaptionButton.setText(resourceMap.getString("labelCaptionButton.text")); // NOI18N
        labelCaptionButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelCaptionButton.setName("labelCaptionButton"); // NOI18N
        jScrollPane4.setViewportView(labelCaptionButton);

        jSplitPane2.setRightComponent(jScrollPane4);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textAreaButtonCaption.setColumns(20);
        textAreaButtonCaption.setFont(resourceMap.getFont("textAreaButtonCaption.font")); // NOI18N
        textAreaButtonCaption.setRows(5);
        textAreaButtonCaption
            .setText(resourceMap.getString("textAreaButtonCaption.text")); // NOI18N
        textAreaButtonCaption.setName("textAreaButtonCaption"); // NOI18N
        textAreaButtonCaption.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textAreaButtonCaptionKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(textAreaButtonCaption);

        jSplitPane2.setLeftComponent(jScrollPane1);

        jPanel6.setBorder(javax.swing.BorderFactory
            .createTitledBorder(resourceMap.getString("jPanel6.border.title"))); // NOI18N
        jPanel6.setName("jPanel6"); // NOI18N

        spinButX.setModel(new javax.swing.SpinnerNumberModel(0, 0, 2000, 10));
        spinButX.setName("spinButX"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        spinButY.setModel(new javax.swing.SpinnerNumberModel(0, 0, 2000, 10));
        spinButY.setName("spinButY"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        spinButB.setModel(new javax.swing.SpinnerNumberModel(100, 50, 2000, 10));
        spinButB.setName("spinButB"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        spinButH.setModel(new javax.swing.SpinnerNumberModel(50, 50, 2000, 10));
        spinButH.setName("spinButH"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel6Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel14)
                                .addComponent(jLabel16)
                                .addComponent(jLabel17)
                                .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel6Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(spinButH, javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                                .addComponent(spinButB, javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                                .addComponent(spinButY, javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                                .addComponent(spinButX, javax.swing.GroupLayout.Alignment.TRAILING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                        .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel6Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinButX, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel6Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinButY, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel6Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinButB, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel6Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinButH, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                    .addContainerGap(260, Short.MAX_VALUE))
        );

        WysiwygButton.setText(resourceMap.getString("WysiwygButton.text")); // NOI18N
        WysiwygButton.setName("WysiwygButton"); // NOI18N
        WysiwygButton.setPreferredSize(new java.awt.Dimension(79, 15));
        WysiwygButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                WysiwygButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(WysiwygButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 674,
                                Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout
                                        .createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel24)
                                        .addComponent(WysiwygButton,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        371, Short.MAX_VALUE)))
                        .addContainerGap())
        );

        jTabbedPane1
            .addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        jSplitPane3.setDividerLocation(260);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setContinuousLayout(true);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

        jSplitPane1.setBorder(new javax.swing.border.MatteBorder(null));
        jSplitPane1.setDividerLocation(131);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setContinuousLayout(true);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        textAreaTextPrint.setColumns(20);
        textAreaTextPrint.setFont(resourceMap.getFont("textAreaTextPrint.font")); // NOI18N
        textAreaTextPrint.setRows(5);
        textAreaTextPrint.setName("textAreaTextPrint"); // NOI18N
        jScrollPane3.setViewportView(textAreaTextPrint);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jLabel7)
                    .addContainerGap(534, Short.MAX_VALUE))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 783,
                    Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jLabel7)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 102,
                        Short.MAX_VALUE))
        );

        jSplitPane1.setBottomComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        textAreaInfoHtml.setColumns(20);
        textAreaInfoHtml.setFont(resourceMap.getFont("textAreaInfoHtml.font")); // NOI18N
        textAreaInfoHtml.setRows(5);
        textAreaInfoHtml.setName("textAreaInfoHtml"); // NOI18N
        textAreaInfoHtml.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textAreaInfoHtmlKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(textAreaInfoHtml);

        butWYS2.setText(resourceMap.getString("butWYS2.text")); // NOI18N
        butWYS2.setName("butWYS2"); // NOI18N
        butWYS2.setPreferredSize(new java.awt.Dimension(81, 15));
        butWYS2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butWYS2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(butWYS2, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 783,
                    Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(butWYS2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 109,
                        Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jSplitPane3.setTopComponent(jSplitPane1);

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        labelInfoDialog.setText(resourceMap.getString("labelInfoDialog.text")); // NOI18N
        labelInfoDialog.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelInfoDialog.setName("labelInfoDialog"); // NOI18N
        jScrollPane5.setViewportView(labelInfoDialog);

        jSplitPane3.setRightComponent(jScrollPane5);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane3)
                    .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 392,
                        Short.MAX_VALUE)
                    .addContainerGap())
        );

        jTabbedPane1
            .addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        cbSound.setText(resourceMap.getString("cbSound.text")); // NOI18N
        cbSound.setName("cbSound"); // NOI18N

        jPanel8.setBorder(javax.swing.BorderFactory
            .createTitledBorder(resourceMap.getString("jPanel8.border.title"))); // NOI18N
        jPanel8.setName("jPanel8"); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory
            .createTitledBorder(resourceMap.getString("jPanel9.border.title"))); // NOI18N
        jPanel9.setName("jPanel9"); // NOI18N

        bgGong.add(rbNoGong);
        rbNoGong.setText(resourceMap.getString("rbNoGong.text")); // NOI18N
        rbNoGong.setName("rbNoGong"); // NOI18N

        bgGong.add(rbGong);
        rbGong.setSelected(true);
        rbGong.setText(resourceMap.getString("rbGong.text")); // NOI18N
        rbGong.setName("rbGong"); // NOI18N

        bgGong.add(rbGongFirstOnly);
        rbGongFirstOnly.setText(resourceMap.getString("rbGongFirstOnly.text")); // NOI18N
        rbGongFirstOnly.setName("rbGongFirstOnly"); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbNoGong)
                            .addComponent(rbGong)
                            .addComponent(rbGongFirstOnly))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addComponent(rbNoGong)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rbGong)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rbGongFirstOnly))
        );

        cbClient.setText(resourceMap.getString("cbClient.text")); // NOI18N
        cbClient.setName("cbClient"); // NOI18N

        cbClientNumber.setText(resourceMap.getString("cbClientNumber.text")); // NOI18N
        cbClientNumber.setName("cbClientNumber"); // NOI18N

        cbGoNumber.setText(resourceMap.getString("cbGoNumber.text")); // NOI18N
        cbGoNumber.setName("cbGoNumber"); // NOI18N

        jPanel10.setBorder(javax.swing.BorderFactory
            .createTitledBorder(resourceMap.getString("jPanel10.border.title"))); // NOI18N
        jPanel10.setName("jPanel10"); // NOI18N

        bgGo.add(rbCabinet);
        rbCabinet.setText(resourceMap.getString("rbCabinet.text")); // NOI18N
        rbCabinet.setName("rbCabinet"); // NOI18N

        bgGo.add(rbWindow);
        rbWindow.setText(resourceMap.getString("rbWindow.text")); // NOI18N
        rbWindow.setName("rbWindow"); // NOI18N

        bgGo.add(rbStoika);
        rbStoika.setText(resourceMap.getString("rbStoika.text")); // NOI18N
        rbStoika.setName("rbStoika"); // NOI18N

        bgGo.add(rbTable);
        rbTable.setText(resourceMap.getString("rbTable.text")); // NOI18N
        rbTable.setName("rbTable"); // NOI18N

        bgGo.add(rbNoGo);
        rbNoGo.setSelected(true);
        rbNoGo.setText(resourceMap.getString("rbNoGo.text")); // NOI18N
        rbNoGo.setName("rbNoGo"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel10Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbCabinet)
                            .addComponent(rbWindow)
                            .addComponent(rbStoika)
                            .addComponent(rbTable)
                            .addComponent(rbNoGo))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addComponent(rbCabinet)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rbWindow)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rbStoika)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rbTable)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rbNoGo))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cbClient)
                    .addGap(1, 1, 1)
                    .addComponent(cbClientNumber)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cbGoNumber)
                    .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbClient)
                                .addComponent(cbClientNumber))
                            .addComponent(cbGoNumber)))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbSound))
                    .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(cbSound)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(221, Short.MAX_VALUE))
        );

        jTabbedPane1
            .addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        jServiceOfficePanel = new javax.swing.JPanel();

        jOfficeSelected = new javax.swing.JList();
        jRemoveSelectedOfficeButton = new javax.swing.JButton();
        jRemoveSelectedOfficeButton.setText("Remove Office");
        jRemoveSelectedOfficeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRemoveSelectedOfficeButtonAction(evt);
            }
        });

        jOfficeAvailable = new javax.swing.JList();
        jAddSelectedOfficeButton = new javax.swing.JButton();
        jAddSelectedOfficeButton.setText("Add Office");
        jAddSelectedOfficeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddSelectedOfficeButtonAction(evt);
            }
        });

        jServiceOfficeButtonPanel = new javax.swing.JPanel();
        jAddAllOfficesButton = new javax.swing.JButton();
        jAddAllOfficesButton.setText("Add All Offices");
        jAddAllOfficesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddAllOfficesButtonAction(evt);
            }
        });
        jRemoveAllOfficesButton = new javax.swing.JButton();
        jRemoveAllOfficesButton.setText("Remove All Offices");
        jRemoveAllOfficesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRemoveAllOfficesButtonAction(evt);
            }
        });

        javax.swing.GroupLayout jServiceOfficeButtonPanelLayout = new javax.swing.GroupLayout(
            jServiceOfficeButtonPanel);
        jServiceOfficeButtonPanel.setLayout(jServiceOfficeButtonPanelLayout);
        jServiceOfficeButtonPanelLayout.setHorizontalGroup(
            jServiceOfficeButtonPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jServiceOfficeButtonPanelLayout.createSequentialGroup())
                .addGroup(jServiceOfficeButtonPanelLayout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAddAllOfficesButton)
                    .addComponent(jRemoveAllOfficesButton))
        );

        jServiceOfficeButtonPanelLayout.setVerticalGroup(
            jServiceOfficeButtonPanelLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jServiceOfficeButtonPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jServiceOfficeButtonPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jAddAllOfficesButton, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(18, 18, 18)
                    .addGroup(jServiceOfficeButtonPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jRemoveAllOfficesButton, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jServiceOfficePanelLayout = new javax.swing.GroupLayout(
            jServiceOfficePanel);
        jServiceOfficePanel.setLayout(jServiceOfficePanelLayout);
        jServiceOfficePanelLayout.setHorizontalGroup(
            jServiceOfficePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jServiceOfficePanelLayout.createSequentialGroup()
                    .addGroup(jServiceOfficePanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jOfficeSelected, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRemoveSelectedOfficeButton))
                    .addGap(18, 18, 18)
                    .addGroup(jServiceOfficePanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jOfficeAvailable, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jAddSelectedOfficeButton))
                    .addGap(18, 18, 18)
                    .addGroup(jServiceOfficePanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jServiceOfficeButtonPanel))
                    .addGap(18, 18, 18)
                )
        );

        jServiceOfficePanelLayout.setVerticalGroup(
            jServiceOfficePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jServiceOfficePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jServiceOfficePanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jOfficeSelected, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jOfficeAvailable, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jServiceOfficeButtonPanel,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(jServiceOfficePanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRemoveSelectedOfficeButton)
                        .addComponent(jAddSelectedOfficeButton,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18))
        );

        JScrollPane panelPane = new JScrollPane(jServiceOfficePanel);
        jTabbedPane1.addTab("Map to Offices", panelPane);

        checkBoxBackoffice.setText(resourceMap.getString("checkBoxBackoffice.text")); // NOI18N
        checkBoxBackoffice.setName("checkBoxBackoffice"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        spinnerPunktReg.setModel(new javax.swing.SpinnerNumberModel(0, 0, 99, 1));
        spinnerPunktReg.setName("spinnerPunktReg"); // NOI18N

        tfID.setEditable(false);
        tfID.setText(resourceMap.getString("tfID.text")); // NOI18N
        tfID.setName("tfID"); // NOI18N

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        labelLink.setText(resourceMap.getString("labelLink.text")); // NOI18N
        labelLink.setName("labelLink"); // NOI18N

        panelTreeCmbx.setBorder(new javax.swing.border.MatteBorder(null));
        panelTreeCmbx.setName("panelTreeCmbx"); // NOI18N

        javax.swing.GroupLayout panelTreeCmbxLayout = new javax.swing.GroupLayout(panelTreeCmbx);
        panelTreeCmbx.setLayout(panelTreeCmbxLayout);
        panelTreeCmbxLayout.setHorizontalGroup(
            panelTreeCmbxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTreeCmbxLayout.setVerticalGroup(
            panelTreeCmbxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
        );

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPropsLayout = new javax.swing.GroupLayout(panelProps);
        panelProps.setLayout(panelPropsLayout);
        panelPropsLayout.setHorizontalGroup(
            panelPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane1)
                .addGroup(panelPropsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        panelPropsLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                panelPropsLayout.createSequentialGroup()
                                    .addComponent(jLabel23)
                                    .addGap(10, 10, 10)
                                    .addComponent(textFieldServiceDescript))
                            .addGroup(panelPropsLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldServiceName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfID, javax.swing.GroupLayout.PREFERRED_SIZE, 91,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelPropsLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(8, 8, 8)
                                .addComponent(comboBoxEnabled,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldPrefix,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkBoxBackoffice)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerPunktReg,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(panelPropsLayout.createSequentialGroup()
                                .addComponent(labelLink)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelTreeCmbx, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)))
                    .addContainerGap())
        );
        panelPropsLayout.setVerticalGroup(
            panelPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelPropsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        panelPropsLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textFieldServiceName,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(tfID, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        panelPropsLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel21)
                            .addComponent(textFieldPrefix, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(checkBoxBackoffice)
                            .addComponent(jLabel9)
                            .addComponent(spinnerPunktReg, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        panelPropsLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textFieldServiceDescript,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(panelPropsLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelTreeCmbx, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelLink, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jTabbedPane1))
        );

        panelButtons.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtons.setName("panelButtons"); // NOI18N

        buttonSave.setText(resourceMap.getString("buttonSave.text")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    panelButtonsLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonCancel)
                        .addContainerGap())
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    panelButtonsLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelButtonsLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonCancel)
                            .addComponent(buttonSave))
                        .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panelProps, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addComponent(panelProps, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        if (service != null) {
            saveService();
        }
        if (serviceLng != null) {
            saveServiceLang();
        }
        setVisible(false);
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonCancelActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void checkBoxInputRequiredStateChanged(
        javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkBoxInputRequiredStateChanged
        textFieldInputCaption.setEnabled(checkBoxInputRequired.isSelected());
        cbRequaredAsColumnExt.setEnabled(checkBoxInputRequired.isSelected());
        spinnerLimitForOnePerson.setEnabled(checkBoxInputRequired.isSelected());
    }//GEN-LAST:event_checkBoxInputRequiredStateChanged

    private void textAreaInfoHtmlKeyReleased(
        java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaInfoHtmlKeyReleased
        labelInfoDialog.setText(textAreaInfoHtml.getText());
    }//GEN-LAST:event_textAreaInfoHtmlKeyReleased

    private void textAreaButtonCaptionKeyPressed(
        java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaButtonCaptionKeyPressed
        labelCaptionButton.setText(textAreaButtonCaption.getText());
    }//GEN-LAST:event_textAreaButtonCaptionKeyPressed

    private void WysiwygButtonActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_WysiwygButtonActionPerformed
        textAreaButtonCaption.setText(WysiwygDlg.showInstance(textAreaButtonCaption.getText()));
        labelCaptionButton.setText(textAreaButtonCaption.getText());
    }//GEN-LAST:event_WysiwygButtonActionPerformed

    private void butWYS2ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butWYS2ActionPerformed
        textAreaInfoHtml.setText(WysiwygDlg.showInstance(textAreaInfoHtml.getText()));
        labelInfoDialog.setText(textAreaInfoHtml.getText());
    }//GEN-LAST:event_butWYS2ActionPerformed

    private void jButton1ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        comboBoxServices.setSelectedItem(null);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jRemoveSelectedOfficeButtonAction(java.awt.event.ActionEvent evt) {
        DefaultListModel model = (DefaultListModel) jOfficeSelected.getModel();
        int selectedIndex = jOfficeSelected.getSelectedIndex();
        QOffice officeToRemove = (QOffice) jOfficeSelected.getSelectedValue();
        if (selectedIndex != -1) {
            model.remove(selectedIndex);
        }
        officesToRemove.add(officeToRemove);

        //Remove the office from the "Add" list, if it exists there
        if (officesToAdd.contains(officeToRemove)) {
            officesToAdd.remove(officeToRemove);
        }
    }

    private void jAddSelectedOfficeButtonAction(java.awt.event.ActionEvent evt) {
        DefaultListModel model = (DefaultListModel) jOfficeSelected.getModel();
        QOffice officeToAdd = (QOffice) jOfficeAvailable.getSelectedValue();
        model.addElement(officeToAdd);
        officesToAdd.add(officeToAdd);

        //Remove the office from the "Remove" list, if it exists there
        if (officesToRemove.contains(officeToAdd)) {
            officesToRemove.remove(officeToAdd);
        }
    }

    private void jAddAllOfficesButtonAction(java.awt.event.ActionEvent evt) {
        DefaultListModel availableModel = (DefaultListModel) jOfficeAvailable.getModel();
        DefaultListModel selectedModel = (DefaultListModel) jOfficeSelected.getModel();

        for (int i = 0; i < availableModel.getSize(); i++) {
            QOffice officeToAdd = (QOffice) availableModel.getElementAt(i);

            if (!selectedModel.contains(officeToAdd)) {
                selectedModel.addElement(officeToAdd);
                officesToAdd.add(officeToAdd);
            }
        }

        //Clear the remove list, since we're adding all of the offices
        officesToRemove = new LinkedList<QOffice>();
    }

    private void jRemoveAllOfficesButtonAction(java.awt.event.ActionEvent evt) {
        DefaultListModel selectedModel = (DefaultListModel) jOfficeSelected.getModel();

        while (selectedModel.getSize() > 0) {
            QOffice officeToRemove = (QOffice) selectedModel.getElementAt(0);
            selectedModel.removeElement(officeToRemove);
            officesToRemove.add(officeToRemove);
        }

        //Clear the add list, since we're removing all of the offices
        officesToAdd = new LinkedList<QOffice>();
    }
    // End of variables declaration//GEN-END:variables
}
