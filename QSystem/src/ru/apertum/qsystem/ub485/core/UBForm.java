package ru.apertum.qsystem.ub485.core;

import gnu.io.SerialPortEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.model.QTray;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.RpcGetServerState.ServiceInfo;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.extra.IButtonDeviceFuctory;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QUser;
import ru.evgenic.rxtx.serialPort.IReceiveListener;
import ru.evgenic.rxtx.serialPort.ISerialPort;
import ru.evgenic.rxtx.serialPort.RxtxSerialPort;

/**
 * @author Evgeniy Egorov
 */
public class UBForm extends JFrame {

    /**
     * Ключ блокировки для манипуляции с кстомерами
     */
    public static final Lock receprtTaskLock = new ReentrantLock();
    public static LinkedList<QUser> users = new LinkedList<>();
    public static LinkedList<ServiceInfo> servs = new LinkedList<>();
    public static UBForm form;
    private static ResourceMap localeMap = null;
    /**
     * Системный трей.
     */
    private final QTray tray;
    /**
     * пул потоков для работы с командами отоператоров
     */
    public ExecutorService es;
    public INetProperty netProperty;
    File propFile = null;
    Properties props = null;
    private Timer apdater;
    private Thread th;
    private volatile boolean isrun;
    private ISerialPort port;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChangeAdress;
    private javax.swing.JButton buttonRefesh;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSendSignal;
    private javax.swing.JButton buttonStart;
    private javax.swing.JButton buttonStop;
    private javax.swing.JButton buttonStopTestCOM;
    private javax.swing.JButton buttonTestCOM;
    private javax.swing.JToggleButton buttonTestDev;
    private javax.swing.JButton buttonTestQSys;
    private javax.swing.JCheckBox checkBoxParity;
    private javax.swing.JCheckBox checkBoxSignal;
    private javax.swing.JComboBox comboBoxBits;
    private javax.swing.JComboBox comboBoxSignal;
    private javax.swing.JComboBox comboBoxSpeed;
    private javax.swing.JComboBox comboBoxStopBits;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelTest;
    private javax.swing.JPanel panelDev;
    private javax.swing.JSpinner spinnerAddr;
    private javax.swing.JSpinner spinnerAddr1;
    private javax.swing.JSpinner spinnerServerPort;
    private javax.swing.JTable table;
    private javax.swing.JTextArea textDebug;
    private javax.swing.JTextField textFieldPortName;
    private javax.swing.JTextField textServerAddr;

    /**
     * Creates new form UBForm
     */
    public UBForm() {
        initComponents();
        // поддержка расширяемости плагинами
        IButtonDeviceFuctory devFuctory = null;
        for (final IButtonDeviceFuctory event : ServiceLoader.load(IButtonDeviceFuctory.class)) {
            QLog.l().logger().info("Invoke SPI ext. Description: " + event.getDescription());
            devFuctory = event;
            break;
        }
        table.setModel(devFuctory == null ? new UserTableModel(AddrProp.getInstance())
            : devFuctory.getDeviceTable());

        // Фича. По нажатию Escape закрываем форму
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
                setVisible(false);
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        // инициализим trayIcon, т.к. setSituation() требует работу с tray
        final JFrame fr = this;
        try {
            setIconImage(ImageIO
                .read(UBForm.class
                    .getResource("/ru/apertum/qsystem/client/forms/resources/client.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        tray = QTray.getInstance(fr, "/ru/apertum/qsystem/client/forms/resources/client.png",
            getLocaleMessage("messages.tray.hint"));
        tray.addItem("Открыть", (ActionEvent e) -> {
            setVisible(true);
            setState(JFrame.NORMAL);
        });
        tray.addItem("-", (ActionEvent e) -> {
        });
        tray.addItem("Выход", (ActionEvent e) -> {
            dispose();
            System.exit(0);
        });

        if (QConfig.cfg().isUbtnStart()) {
            final Thread th_start = new Thread(() -> {
                buttonStartActionPerformed(null);
            });
            th_start.start();
        }
    }

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(UBForm.class);
        }
        return localeMap.getString(key);
    }

    public static synchronized void sendToDevice(byte[] b) {
        try {
            String s = "";
            for (byte b1 : b) {
                s = s + (b1 & 0xFF) + "_";
            }
            System.out.print("<<" + s + " ... ");
            form.port.send(b);
            System.out.println(" !!!!!!!!\n");
        } catch (Exception ex) {
            System.err.println(ex);
            form.textDebug.setText("В порт не отослалось. " + ex + "\n" + form.textDebug.getText());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        QLog.initial(args, 5);
        Locale.setDefault(Locales.getInstance().getLangCurrent());
        // Загрузка плагинов из папки plugins
        if (QConfig.cfg().isPlaginable()) {
            Uses.loadPlugins("./plugins/");
        }
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                .getInstalledLookAndFeels()) {
                /*
                 * Metal
                 Nimbus
                 CDE/Motif
                 Windows
                 Windows Classic
                 */
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UBForm.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
        }

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            form = new UBForm();
            form.setLocationRelativeTo(null);
            form.setVisible(true);
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        comboBoxSpeed = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        comboBoxBits = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        checkBoxParity = new javax.swing.JCheckBox();
        comboBoxStopBits = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        textFieldPortName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        buttonRefesh = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        buttonStop = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        textServerAddr = new javax.swing.JTextField();
        spinnerServerPort = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        buttonTestQSys = new javax.swing.JButton();
        buttonTestCOM = new javax.swing.JButton();
        buttonStopTestCOM = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        textDebug = new javax.swing.JTextArea();
        panelDev = new javax.swing.JPanel();
        spinnerAddr = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        checkBoxSignal = new javax.swing.JCheckBox();
        comboBoxSignal = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        spinnerAddr1 = new javax.swing.JSpinner();
        buttonSendSignal = new javax.swing.JButton();
        buttonChangeAdress = new javax.swing.JButton();
        labelTest = new javax.swing.JLabel();
        buttonTestDev = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Сервер управления кнопками вызова клиентов");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры порта"));

        comboBoxSpeed.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{"9600", "14400", "19200", "38400", "57600", "115200", "128000",
                "921600"}));

        jLabel1.setText("Скорость порта");

        comboBoxBits
            .setModel(new javax.swing.DefaultComboBoxModel(new String[]{"4", "5", "6", "7", "8"}));

        jLabel2.setText("Биты данных");

        checkBoxParity.setText("Четность");

        comboBoxStopBits.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1", "2"}));

        jLabel3.setText("Стоповые биты");

        textFieldPortName.setText("Обнови!!!");

        jLabel4.setText("Имя порта");

        buttonRefesh.setText("Обновить");
        buttonRefesh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefeshActionPerformed(evt);
            }
        });

        buttonSave.setText("Сохранить");
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                    32,
                                    Short.MAX_VALUE)
                                .addGroup(jPanel2Layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                        false)
                                    .addComponent(textFieldPortName,
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkBoxParity,
                                        javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboBoxBits, 0,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addComponent(comboBoxSpeed, 0, 89, Short.MAX_VALUE)
                                    .addComponent(comboBoxStopBits,
                                        javax.swing.GroupLayout.Alignment.LEADING, 0,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(buttonRefesh)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                    32,
                                    Short.MAX_VALUE)
                                .addComponent(buttonSave)))
                    .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxSpeed, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxBits, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(checkBoxParity)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxStopBits, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textFieldPortName, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(
                        jPanel2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonRefesh)
                            .addComponent(buttonSave))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String[]{
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(table);

        buttonStop.setText("Стоп");
        buttonStop.setEnabled(false);
        buttonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopActionPerformed(evt);
            }
        });

        buttonStart.setText("Старт");
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры сервера СУО"));

        textServerAddr.setText("Обнови!!!");

        spinnerServerPort.setModel(new javax.swing.SpinnerNumberModel(1, 1, 64000, 1));

        jLabel5.setText("Адрес");

        jLabel6.setText("Порт");

        buttonTestQSys.setText("Тест QSys");
        buttonTestQSys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTestQSysActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(
                            jPanel1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(textServerAddr, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    167,
                                    Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(spinnerServerPort,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 58,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonTestQSys)))
                        .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGroup(
                        jPanel1Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textServerAddr, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        jPanel1Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinnerServerPort, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(buttonTestQSys)))
        );

        buttonTestCOM.setText("Тест COM");
        buttonTestCOM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTestCOMActionPerformed(evt);
            }
        });

        buttonStopTestCOM.setText("Стоп тест СОМ");
        buttonStopTestCOM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStopTestCOMActionPerformed(evt);
            }
        });

        textDebug.setColumns(20);
        textDebug.setRows(5);
        jScrollPane2.setViewportView(textDebug);

        panelDev.setBorder(javax.swing.BorderFactory.createTitledBorder("Операции с устройствами"));
        panelDev.setEnabled(false);

        spinnerAddr.setModel(new javax.swing.SpinnerNumberModel(32, 32, 256, 1));

        jLabel7.setText("Адрес RS485");

        checkBoxSignal.setText("Звук");

        comboBoxSignal.setModel(new javax.swing.DefaultComboBoxModel(
            new String[]{"светодиод погашен", "включен Красный", "включен Зеленый",
                "мигает Красный (200 мс)", "мигает Зеленый (200 мс)", "мигает Красный (500 мс)",
                "мигает Зеленый (500 мс)"}));

        jLabel8.setText("Заменить на адрес RS485");

        spinnerAddr1.setModel(new javax.swing.SpinnerNumberModel(32, 32, 256, 1));

        buttonSendSignal.setText("Маякнуть");
        buttonSendSignal.setEnabled(false);
        buttonSendSignal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendSignalActionPerformed(evt);
            }
        });

        buttonChangeAdress.setText("Заменить");
        buttonChangeAdress.setEnabled(false);
        buttonChangeAdress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChangeAdressActionPerformed(evt);
            }
        });

        labelTest.setText("Тест");

        buttonTestDev.setText("Опросить RS485");
        buttonTestDev.setEnabled(false);
        buttonTestDev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTestDevActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDevLayout = new javax.swing.GroupLayout(panelDev);
        panelDev.setLayout(panelDevLayout);
        panelDevLayout.setHorizontalGroup(
            panelDevLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelDevLayout.createSequentialGroup()
                    .addComponent(checkBoxSignal)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboBoxSignal, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE))
                .addGroup(panelDevLayout.createSequentialGroup()
                    .addComponent(jLabel8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(spinnerAddr1, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                    panelDevLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelDevLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonSendSignal,
                                javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(buttonChangeAdress,
                                javax.swing.GroupLayout.Alignment.TRAILING)))
                .addGroup(panelDevLayout.createSequentialGroup()
                    .addGroup(
                        panelDevLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDevLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerAddr, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    44,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelDevLayout.createSequentialGroup()
                                .addComponent(buttonTestDev)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTest)))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDevLayout.setVerticalGroup(
            panelDevLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelDevLayout.createSequentialGroup()
                    .addGroup(
                        panelDevLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spinnerAddr, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        panelDevLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(checkBoxSignal)
                            .addComponent(comboBoxSignal, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonSendSignal)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(
                        panelDevLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(spinnerAddr1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonChangeAdress)
                    .addGap(18, 18, 18)
                    .addGroup(
                        panelDevLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelTest)
                            .addComponent(buttonTestDev))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                false)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(panelDev, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(buttonTestCOM)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttonStopTestCOM)))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 185,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonStart)
                            .addGap(18, 18, 18)
                            .addComponent(buttonStop)
                            .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 583,
                                Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonTestCOM)
                                    .addComponent(buttonStopTestCOM))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(panelDev, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jScrollPane1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonStop)
                                    .addComponent(buttonStart))))
                    .addContainerGap())
                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonRefeshActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefeshActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Выберите файл конфигурации");
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return !f.isFile() || f.getAbsolutePath().endsWith(".properties");
            }

            @Override
            public String getDescription() {
                return "Файлы свойств (*.cfg)";
            }
        });

        fc.setCurrentDirectory(new File("config"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            FileInputStream fis;
            try {
                propFile = fc.getSelectedFile();
                fis = new FileInputStream(propFile);
                props = new Properties();
                props.load(fis);
                fis.close();
            } catch (IOException ex) {
                System.err.println(ex);
                return;
            }
            textFieldPortName.setText(props.getProperty("port.name", "COM1"));
            checkBoxParity.setSelected(props.getProperty("port.parity", "0").equals("1"));
            comboBoxSpeed.setSelectedItem(props.getProperty("port.speed", "6900"));
            comboBoxBits.setSelectedItem(props.getProperty("port.bits", "8"));
            comboBoxStopBits.setSelectedItem(props.getProperty("port.stopbits", "1"));
            textServerAddr.setText(props.getProperty("qsys.addr", "localhost"));
            spinnerServerPort.setValue(Integer.parseInt(props.getProperty("qsys.port", "1")));
        }
    }//GEN-LAST:event_buttonRefeshActionPerformed

    private void buttonSaveActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        if (propFile != null) {
            try {
                final FileOutputStream out = new FileOutputStream(propFile);

                props.setProperty("port.name", textFieldPortName.getText());
                props.setProperty("port.parity", checkBoxParity.isSelected() ? "1" : "0");
                props.setProperty("port.speed", comboBoxSpeed.getSelectedItem().toString());
                props.setProperty("port.bits", comboBoxBits.getSelectedItem().toString());
                props.setProperty("port.stopbits", comboBoxStopBits.getSelectedItem().toString());
                props.store(out, "Save from test app.");
            } catch (IOException ex) {
                System.err.println(ex);
                JOptionPane.showMessageDialog(this,
                    "Файл конфигурации НЕ сохранен. " + ex,
                    "Сохранение",
                    JOptionPane.ERROR_MESSAGE);
            }

        }
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonStartActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);
        initProps();

        initQsys();
        try {
            initCOM(false);
        } catch (Exception ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(this,
                "Порт не захватился. " + ex,
                "Отсыл",
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
        apdater = new Timer(1000, new ActionListener() {

            boolean fl = true;
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (fl) {
                    System.out.print("***");
                    System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                    System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                    System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                } else {
                    System.out.print("---");
                    System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                    System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                    System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
                }
                i++;
                fl = !fl;
                if (i % 10 != 0) {
                    return;
                }
                final LinkedList<ServiceInfo> servs = NetCommander.getServerState(netProperty);
                for (ButtonDevice adr : AddrProp.getInstance().getAddrs().values()
                    .toArray(new ButtonDevice[0])) {
                    int l = 0;
                    for (QPlanService pser : adr.getUser().getPlanServices()) {
                        for (ServiceInfo serviceInfo : servs) {
                            if (serviceInfo.getId().equals(pser.getService().getId())) {
                                l = l + serviceInfo.getCountWait();
                            }
                        }
                    }
                    adr.setQsize(l);
                }
                ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            }
        });
        apdater.start();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void initProps() {
        FileInputStream fis;
        try {
            propFile = new File("config/qub.properties");
            fis = new FileInputStream(propFile);
            props = new Properties();
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            System.err.println(ex);
            return;
        }
        textFieldPortName.setText(props.getProperty("port.name", "COM1"));
        checkBoxParity.setSelected(props.getProperty("port.parity", "0").equals("1"));
        comboBoxSpeed.setSelectedItem(props.getProperty("port.speed", "6900"));
        comboBoxBits.setSelectedItem(props.getProperty("port.bits", "8"));
        comboBoxStopBits.setSelectedItem(props.getProperty("port.stopbits", "1"));
        textServerAddr.setText(props.getProperty("qsys.addr", "localhost"));
        spinnerServerPort.setValue(Integer.parseInt(props.getProperty("qsys.port", "1")));
        netProperty = new INetProperty() {

            Integer port;
            InetAddress adr;

            @Override
            public Integer getPort() {
                if (port == null) {
                    port = Integer.parseInt(props.getProperty("qsys.port", "1"));
                }
                return port;
            }

            @Override
            public InetAddress getAddress() {
                try {
                    if (adr == null) {
                        adr = InetAddress.getByName(props.getProperty("qsys.addr", "localhost"));
                    }
                    return adr;
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    private void initQsys() {
        if (propFile == null) {
            initProps();
        }
        netProperty = new INetProperty() {

            @Override
            public Integer getPort() {
                return (Integer) spinnerServerPort.getValue();
            }

            @Override
            public InetAddress getAddress() {
                try {
                    return InetAddress.getByName(textServerAddr.getText());
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        users = NetCommander.getUsers(netProperty);
        users.stream().forEach((qUser) -> {
            qUser.getPlanServices().stream().forEach((pser) -> {
                System.out.println(
                    "User: " + qUser.getName() + " => " + pser.getService().getId() + "-" + pser
                        .getService().getName());
            });
        });
        servs = NetCommander.getServerState(netProperty);
        servs.stream().forEach((serviceInfo) -> {
            System.out.println(
                "Servece: " + serviceInfo.getId() + "-" + serviceInfo.getServiceName() + "-"
                    + serviceInfo
                    .getCountWait());
        });

        for (ButtonDevice adr : AddrProp.getInstance().getAddrs().values()
            .toArray(new ButtonDevice[0])) {
            for (QUser qUser : users) {
                if (adr.userId.equals(qUser.getId())) {
                    adr.setUser(qUser);
                    int l = 0;
                    for (QPlanService pser : qUser.getPlanServices()) {
                        for (ServiceInfo serviceInfo : servs) {
                            if (serviceInfo.getId().equals(pser.getService().getId())) {
                                l = l + serviceInfo.getCountWait();
                            }
                        }
                    }
                    adr.qsize = l;
                    break;
                }
            }
            for (ServiceInfo serviceInfo : servs) {
                if (adr.redirectServiceId != null && adr.redirectServiceId
                    .equals(serviceInfo.getId())) {
                    adr.serveceName = serviceInfo.getServiceName();
                    break;
                }
            }
        }
        // поддержка расширяемости плагинами
        IButtonDeviceFuctory devFuctory = null;
        for (final IButtonDeviceFuctory event : ServiceLoader.load(IButtonDeviceFuctory.class)) {
            QLog.l().logger().info("Invoke SPI ext. Description: " + event.getDescription());
            devFuctory = event;
            devFuctory.refreshDeviceTable(users, servs);
            break;
        }
        table.setModel(devFuctory == null ? new UserTableModel(AddrProp.getInstance())
            : devFuctory.getDeviceTable());
    }

    private void initCOM(boolean isTest) throws Exception {
        if (propFile == null) {
            initProps();
        }
        try {
            port = new RxtxSerialPort(textFieldPortName.getText(), (String string, boolean bln) -> {
                if (bln) {
                    QLog.l().logger().error(string);
                } else {
                    QLog.l().logger().trace(string);
                }
            }, (String string) -> {
                QLog.l().logger().error(string);
            });
        } catch (Exception ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(this,
                "Порт не создался. " + ex,
                "Отсыл",
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
        port.setSpeed(Integer.parseInt((String) comboBoxSpeed.getSelectedItem()));
        port.setDataBits(Integer.parseInt((String) comboBoxBits.getSelectedItem()));
        port.setParity(checkBoxParity.isSelected() ? 1 : 0);
        port.setStopBits(Integer.parseInt((String) comboBoxStopBits.getSelectedItem()));

        if (isTest) {
            port.bind(new IReceiveListener() {

                @Override
                public void actionPerformed(SerialPortEvent spe, byte[] bytes) {
                    String s = "";
                    for (byte b : bytes) {
                        s = s + (b & 0xFF) + "_";
                    }
                    System.out.println(">>" + s);
                    textDebug
                        .setText(">>" + s + "|" + new String(bytes) + "\n" + textDebug.getText());
                }

                @Override
                public void actionPerformed(SerialPortEvent spe) {
                }
            });
        } else {
            es = Executors.newFixedThreadPool(24);
            port.bind(new IReceiveListener() {

                @Override
                public void actionPerformed(SerialPortEvent spe, final byte[] bytes) {
                    // синхронизация работы с клиентом
                    receprtTaskLock.lock();
                    String s = "";
                    for (byte b : bytes) {
                        s = s + (b & 0xFF) + "_";
                    }
                    System.out.println(">>" + s);
                    textDebug
                        .setText(">>" + s + "|" + new String(bytes) + "\n" + textDebug.getText());
                    try {
                        final ActionTransmit aTransmitter = ActionRunnablePool.getInstance()
                            .borrowTransmitter();
                        try {
                            aTransmitter.setBytes(bytes);
                            es.submit(aTransmitter);
                        } finally {
                            ActionRunnablePool.getInstance().returnTransmitter(aTransmitter);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(
                            "Ошибка при принятии пакета и работы с ним." + ex);
                    } finally {
                        receprtTaskLock.unlock();
                    }
                }

                @Override
                public void actionPerformed(SerialPortEvent spe) {
                }
            });
        }
    }

    private void buttonStopActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopActionPerformed
        if (apdater != null) {
            apdater.stop();
        }
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false);
        es.shutdown();
        if (port != null) {
            try {
                port.free();
            } catch (Exception ex) {
                System.err.println(ex);
                JOptionPane.showMessageDialog(this,
                    "Порт не закрылся. " + ex,
                    "Отсыл",
                    JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        }
    }//GEN-LAST:event_buttonStopActionPerformed

    private void buttonTestQSysActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTestQSysActionPerformed
        initQsys();
    }//GEN-LAST:event_buttonTestQSysActionPerformed

    private void buttonTestCOMActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTestCOMActionPerformed
        try {
            initCOM(true);
        } catch (Exception ex) {
            buttonTestCOM.setEnabled(true);
            buttonStopTestCOM.setEnabled(false);
            textDebug.setText("Порт не захватился. " + ex);
            System.err.println(ex);
            JOptionPane.showMessageDialog(this,
                "Порт не захватился. " + ex,
                "Отсыл",
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
        textDebug.setText("Порт открылся");
        buttonTestCOM.setEnabled(false);
        buttonStopTestCOM.setEnabled(true);
        panelDev.setEnabled(true);
        buttonSendSignal.setEnabled(true);
        buttonChangeAdress.setEnabled(true);
        buttonTestDev.setEnabled(true);
    }//GEN-LAST:event_buttonTestCOMActionPerformed

    private void buttonStopTestCOMActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStopTestCOMActionPerformed
        buttonTestCOM.setEnabled(true);
        buttonStopTestCOM.setEnabled(false);
        panelDev.setEnabled(false);
        buttonSendSignal.setEnabled(false);
        buttonChangeAdress.setEnabled(false);
        buttonTestDev.setEnabled(false);
        if (port != null) {
            try {
                port.free();
            } catch (Exception ex) {
                System.err.println(ex);
                textDebug.setText("Порт не закрылся. " + ex);
                JOptionPane.showMessageDialog(this,
                    "Порт не закрылся. " + ex,
                    "Отсыл",
                    JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        }
        textDebug.setText("... Порт закрылся\n" + textDebug.getText());
    }//GEN-LAST:event_buttonStopTestCOMActionPerformed

    private void buttonSendSignalActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendSignalActionPerformed
        if (port == null) {
            try {
                initCOM(true);
            } catch (Exception ex) {
                textDebug.setText("Порт не захватился. " + ex);
                System.err.println(ex);
                JOptionPane.showMessageDialog(this,
                    "Порт не захватился. " + ex,
                    "Отсыл",
                    JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        }
        final byte[] mess = new byte[4];
        mess[0] = 1;
        mess[1] = ((Integer) spinnerAddr.getModel().getValue()).byteValue();
        mess[mess.length - 1] = 7;
        /*
         * светодиод погашен
         включен Красный
         включен Зеленый
         мигает Красный (200 мс)
         мигает Зеленый (200 мс)
         мигает Красный (500 мс)
         мигает Зеленый (500 мс)
         */
        switch (comboBoxSignal.getSelectedIndex()) {
            case 0:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x37 : 0x30);
                break;
            case 1:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x38 : 0x31);
                break;
            case 2:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x39 : 0x32);
                break;
            case 3:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x3A : 0x33);
                break;
            case 4:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x3B : 0x34);
                break;
            case 5:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x3C : 0x35);
                break;
            case 6:
                mess[2] = (byte) (checkBoxSignal.isSelected() ? 0x3D : 0x36);
                break;
            default:
                throw new AssertionError();
        }

        String s = "";
        for (byte b : mess) {
            s = s + (b & 0xFF) + "_";
        }
        textDebug.setText("Отсылаем " + s + "...\n" + textDebug.getText());
        try {
            port.send(mess);
        } catch (Exception ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(this,
                "В порт не отослалось. " + ex,
                "Отсыл",
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
        textDebug.setText("  OK -> " + textDebug.getText());
    }//GEN-LAST:event_buttonSendSignalActionPerformed

    private void buttonTestDevActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTestDevActionPerformed
        if (port == null) {
            try {
                initCOM(true);
            } catch (Exception ex) {
                textDebug.setText("Порт не захватился. " + ex);
                System.err.println(ex);
                JOptionPane.showMessageDialog(this,
                    "Порт не захватился. " + ex,
                    "Отсыл",
                    JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        } else {
        }

        if (th == null || !isrun) {
            th = new Thread(() -> {
                byte[] mess = new byte[3];
                mess[0] = 4;
                mess[mess.length - 1] = 7;
                int i = 32;
                while (isrun && i < 255) {
                    mess[1] = (byte) i;
                    String s = "";
                    for (byte b : mess) {
                        s = s + (b & 0xFF) + "_";
                    }
                    System.out.println(s);
                    final String ss = s;
                    SwingUtilities.invokeLater(() -> {
                        labelTest.setText(ss);
                    });
                    try {
                        /*
                         if (i == 60) {
                         mess = new byte[2];
                         mess[0] = 5;
                         mess[mess.length - 1] = 7;
                         }
                         */
                        port.send(mess);
                    } catch (Exception ex) {
                        System.err.println(ex);
                        JOptionPane.showMessageDialog(null,
                            "В порт не отослалось. " + ex,
                            "Отсыл",
                            JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(ex);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    i++;
                }
                SwingUtilities.invokeLater(() -> {
                    textDebug.setText("  OK -> \n" + textDebug.getText());
                });
            });
            isrun = true;
            th.setDaemon(true);
            th.start();
            textDebug.setText("Сканирование...\n" + textDebug.getText());
        } else {
            isrun = false;
        }
    }//GEN-LAST:event_buttonTestDevActionPerformed

    private void buttonChangeAdressActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChangeAdressActionPerformed
        if (port == null) {
            try {
                initCOM(true);
            } catch (Exception ex) {
                textDebug.setText("Порт не захватился. " + ex);
                System.err.println(ex);
                JOptionPane.showMessageDialog(this,
                    "Порт не захватился. " + ex,
                    "Отсыл",
                    JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(ex);
            }
        }
        final byte[] mess = new byte[4];
        mess[0] = 3;
        mess[1] = ((Integer) spinnerAddr.getModel().getValue()).byteValue();
        mess[2] = ((Integer) spinnerAddr1.getModel().getValue()).byteValue();
        mess[mess.length - 1] = 7;

        String s = "";
        for (byte b : mess) {
            s = s + (b & 0xFF) + "_";
        }
        textDebug.setText("Отсылаем " + s + "...\n" + textDebug.getText());
        try {
            port.send(mess);
        } catch (Exception ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(this,
                "В порт не отослалось. " + ex,
                "Отсыл",
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
        textDebug.setText("  OK -> " + textDebug.getText());
    }//GEN-LAST:event_buttonChangeAdressActionPerformed
    // End of variables declaration//GEN-END:variables
}
