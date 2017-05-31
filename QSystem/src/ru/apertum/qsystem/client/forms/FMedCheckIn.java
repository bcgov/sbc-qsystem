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

import gnu.io.SerialPortEvent;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import javax.swing.JComponent;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.server.model.QAuthorizationCustomer;
import ru.evgenic.rxtx.serialPort.IReceiveListener;
import ru.evgenic.rxtx.serialPort.ISerialPort;

/**
 * Created on 14.01.2010, 12:52:27
 *
 * @author Evgeniy Egorov
 */
public class FMedCheckIn extends javax.swing.JDialog {

    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FMedCheckIn.class);
        }
        return localeMap.getString(key);
    }

    private static FMedCheckIn medCheckIn;

    public static boolean isShowen() {
        return medCheckIn != null && medCheckIn.isVisible();
    }
    private static QAuthorizationCustomer result = null;
    private static INetProperty netProperty;
    private ISerialPort port;

    public void setPort(final ISerialPort port) throws Exception {
        this.port = port;
        port.bind(new IReceiveListener() {

            @Override
            public void actionPerformed(SerialPortEvent event, byte[] data) {
                textFieldNumber.setText(new String(data));
                QLog.l().logger().debug("Приняли сообщение в СОМ-порт \"" + result + "\"");
                buttonEnterActionPerformed(null);
            }

            @Override
            public void actionPerformed(SerialPortEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    /**
     * Creates new form FMedCheckIn
     *
     * @param parent
     * @param modal
     */
    public FMedCheckIn(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        buttonEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/checkmark.png"))); // NOI18N
    }

    /**
     * Статический метод который показывает модально диалог регистрации клиентов.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param modal модальный диалог или нет
     * @param netProperty свойства работы с сервером
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param port
     * @return XML-описание результата предварительной записи. если null, то отказались от предварительной записи
     */
    public static QAuthorizationCustomer showMedCheckIn(Frame parent, boolean modal, INetProperty netProperty, boolean fullscreen, ISerialPort port) {
        QLog.l().logger().info("Показать форму идентификации клиента для предварительной записи");
        if (medCheckIn == null) {
            medCheckIn = new FMedCheckIn(parent, modal);
            medCheckIn.setTitle("Идентификация клиента.");
        }
        result = null;
        Uses.setLocation(medCheckIn);
        FMedCheckIn.netProperty = netProperty;
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo() && !fullscreen)) {
            Uses.setFullSize(medCheckIn);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                medCheckIn.setCursor(transparentCursor);
            }
        } else {
            medCheckIn.setBounds(10, 10, 1280, 768);
        }
        medCheckIn.setFordFocuseEdit(medCheckIn.textFieldNumber);
        if (port != null && port.getListeners().isEmpty()) {
            try {
                medCheckIn.setPort(port);
            } catch (Exception ex) {
                QLog.l().logger().error("Ошибка при присвоении порту листенера.");
            }
        }
        medCheckIn.setVisible(true);
        return result;
    }

    public static void closeMedCheckIn() {
        medCheckIn.textFieldNumber.setText("");
        medCheckIn.setVisible(false);
    }

    public static void setBlockDialog(boolean isBlock) {
        if (medCheckIn != null) {
            medCheckIn.panelButtonsNumeric.setVisible(!isBlock);
            medCheckIn.buttonEnter.setVisible(!isBlock);
            medCheckIn.textFieldNumber.setText(isBlock ? getLocaleMessage("med.lock") : "");
        }
    }

    private void setFordFocuseEdit(final JComponent e) {
        e.requestFocus();
        e.requestFocusInWindow();
        // фокус для приема данных со сканера штрих-кодов
        java.awt.EventQueue.invokeLater(() -> {
            e.requestFocus();
        });
    }
    /**
     * через сколько сотрем введенное
     */
    private static final int delay = 10000;
    /**
     * Таймер, по которому будем стирать введенный номер.
     */
    public ATalkingClock clockBack = new ATalkingClock(delay, 1) {

        @Override
        public void run() {
            textFieldNumber.setText("");
        }
    };
    private static final String DEFAULT_KOD = "";

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new ru.apertum.qsystem.client.model.QPanel();
        jLabel1 = new javax.swing.JLabel();
        textFieldNumber = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        panelButtonsNumeric = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        buttonEnter = new javax.swing.JButton();

        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setIconImage(null);
        setIconImages(null);
        setName("Form"); // NOI18N
        setUndecorated(true);

        panelMain.setBorder(new javax.swing.border.MatteBorder(null));
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FMedCheckIn.class);
        panelMain.setBackgroundImgage(resourceMap.getString("panelMain.backgroundImgage")); // NOI18N
        panelMain.setName("panelMain"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setName("jLabel1"); // NOI18N

        textFieldNumber.setFont(resourceMap.getFont("textFieldNumber.font")); // NOI18N
        textFieldNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        textFieldNumber.setText(resourceMap.getString("textFieldNumber.text")); // NOI18N
        textFieldNumber.setName("textFieldNumber"); // NOI18N
        textFieldNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldNumberActionPerformed(evt);
            }
        });
        textFieldNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldNumberKeyTyped(evt);
            }
        });

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        panelButtonsNumeric.setBackground(resourceMap.getColor("panelButtonsNumeric.background")); // NOI18N
        panelButtonsNumeric.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtonsNumeric.setName("panelButtonsNumeric"); // NOI18N
        panelButtonsNumeric.setLayout(new java.awt.GridLayout(4, 3, 5, 5));

        jButton10.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton10.setForeground(resourceMap.getColor("jButton10.foreground")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton10);

        jButton11.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton11.setForeground(resourceMap.getColor("jButton11.foreground")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton11);

        jButton12.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton12.setForeground(resourceMap.getColor("jButton12.foreground")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton12);

        jButton7.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton7.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton7);

        jButton8.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton8.setForeground(resourceMap.getColor("jButton8.foreground")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton8);

        jButton9.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton9.setForeground(resourceMap.getColor("jButton9.foreground")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton9);

        jButton4.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton4.setForeground(resourceMap.getColor("jButton4.foreground")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton4);

        jButton5.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton5.setForeground(resourceMap.getColor("jButton5.foreground")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton5);

        jButton6.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton6.setForeground(resourceMap.getColor("jButton6.foreground")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton6);

        jButton3.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton3.setForeground(resourceMap.getColor("jButton3.foreground")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton3);

        jButton14.setFont(resourceMap.getFont("jButton14.font")); // NOI18N
        jButton14.setForeground(resourceMap.getColor("jButton14.foreground")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton14.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton14.setName("jButton14"); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        panelButtonsNumeric.add(jButton14);

        jButton13.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton13.setForeground(resourceMap.getColor("jButton13.foreground")); // NOI18N
        jButton13.setIcon(resourceMap.getIcon("jButton13.icon")); // NOI18N
        jButton13.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("jButton4.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        jButton13.setName("jButton13"); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton13);

        buttonEnter.setFont(resourceMap.getFont("buttonEnter.font")); // NOI18N
        buttonEnter.setForeground(resourceMap.getColor("buttonEnter.foreground")); // NOI18N
        buttonEnter.setIcon(resourceMap.getIcon("buttonEnter.icon")); // NOI18N
        buttonEnter.setText(resourceMap.getString("buttonEnter.text")); // NOI18N
        buttonEnter.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, resourceMap.getColor("buttonEnter.border.outsideBorder.highlightInnerColor"), null, null), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED))); // NOI18N
        buttonEnter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonEnter.setName("buttonEnter"); // NOI18N
        buttonEnter.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        buttonEnter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEnterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelMainLayout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1099, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                            .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(textFieldNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(47, 47, 47)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                        .addComponent(panelButtonsNumeric, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(textFieldNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelButtonsNumeric, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonEnter))
                .addContainerGap(140, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonClickNumeric(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClickNumeric
        if (clockBack.isActive()) {
            clockBack.stop();
        }

        clockBack.start();
        if ("".equals(evt.getActionCommand())) {
            // удаление
            if (DEFAULT_KOD.equals(textFieldNumber.getText()) || "".equals(textFieldNumber.getText())) {
                textFieldNumber.setText("");
            } else {
                textFieldNumber.setText(textFieldNumber.getText().substring(0, textFieldNumber.getText().length() - 1));
            }

        } else {
            // добавление цифры
            if (DEFAULT_KOD.equals(textFieldNumber.getText())) {
                textFieldNumber.setText("");
            }
            textFieldNumber.setText(textFieldNumber.getText() + evt.getActionCommand());
        }
        setFordFocuseEdit(textFieldNumber);
    }//GEN-LAST:event_buttonClickNumeric

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        textFieldNumber.setText("");
        setFordFocuseEdit(textFieldNumber);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void buttonEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEnterActionPerformed
        setFordFocuseEdit(textFieldNumber);
        if ("".equals(textFieldNumber.getText().trim()) || buttonEnter.isVisible() == false) {
            return;
        }
        //Послать запрос на идентификацию клиента и отработать результат
        final QAuthorizationCustomer res = NetCommander.getClientAuthorization(netProperty, textFieldNumber.getText().trim());
        // Шлем отказ
        final String notFound = "<html><p align=center><span style='font-size:45.0pt;color:purple'>Номер не обнаружен.</span><br><span style='font-size:60.0pt;color:green'>Обратитесь в регистратуру.</span>";

        if (res == null) {
            QLog.l().logger().debug("Не идентифицирован клиент по номеру \"" + textFieldNumber.getText() + "\"");
            FTimedDialog.showTimedDialog(null, true, notFound, 5000);
        } else {
            QLog.l().logger().debug("Клиент опознан, предлагаем выбрать услугу");
            result = res;
            closeMedCheckIn();
        }
    }//GEN-LAST:event_buttonEnterActionPerformed

    private void textFieldNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldNumberKeyTyped
        if (textFieldNumber.getText().indexOf('#') != -1) {
            textFieldNumber.setText("");
        }

    }//GEN-LAST:event_textFieldNumberKeyTyped

    private void textFieldNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldNumberActionPerformed
        buttonEnterActionPerformed(null);
    }//GEN-LAST:event_textFieldNumberActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonEnter;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel panelButtonsNumeric;
    private ru.apertum.qsystem.client.model.QPanel panelMain;
    private javax.swing.JTextField textFieldNumber;
    // End of variables declaration//GEN-END:variables
}
