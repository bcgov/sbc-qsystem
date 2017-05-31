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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.MemoryImageSource;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.INetProperty;

/**
 * Created on 18.09.2009, 11:33:46 Диалог постановки в очередь по коду предварительной регистрации Имеет метод для осуществления всех действий. Вся логика
 * инкапсулирована в этом классе. Должен уметь работать с комовским сканером.
 *
 * @author Evgeniy Egorov
 */
public class FInputDialog extends javax.swing.JDialog {

    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FInputDialog.class);
        }
        return localeMap.getString(key);
    }
    private static FInputDialog inputDialog;

    /**
     * Creates new form FStandAdvance
     *
     * @param parent
     * @param modal
     */
    public FInputDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        panelButtonsNumeric.setVisible(WelcomeParams.getInstance().numeric_keyboard);
        panelLettersUp.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelLettersMiddle.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelLettersMiddleLeft.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelLettersMiddleRight.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelLettersBottom.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelLettersBottomLeft.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panellLettersBottomRight.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelSpase.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelSpaseLeft.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        panelSpaseRight.setVisible(WelcomeParams.getInstance().alphabetic_keyboard);
        labelKod.setFont(new Font("arial", 0, WelcomeParams.getInstance().input_font_size));

        panelSpecButtons.setVisible(!"".equals(WelcomeParams.getInstance().spec_keyboard));
        if (!"".equals(WelcomeParams.getInstance().spec_keyboard)) {
            final String[] ss = WelcomeParams.getInstance().spec_keyboard.split(";");
            final GridLayout gl = new GridLayout(1, ss.length);
            gl.setHgap(20);
            panelSpecButtons.setLayout(gl);

            for (final String s : ss) {
                final JButton b = new JButton(s.trim());
                b.setFont(new Font("Tahoma", 0, 36));
                b.setName("but_spec_" + s);
                b.addActionListener((java.awt.event.ActionEvent evt) -> {
                    java.awt.event.ActionEvent se = new ActionEvent(b, evt.getID(), s);
                    buttonClickNumeric(se);
                });
                b.setBorder(new CompoundBorder(new BevelBorder(0), new BevelBorder(0)));
                panelSpecButtons.add(b);
            }

        }

        char ascending = 'A';
        char descending = 'z';
        while (ascending <= 'Z') {
            //System.out.println(ascending + " " + descending);
            getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(ascending), "Run and move");
            getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(descending), "Run and move");
            ascending++;
            descending--;
        }
        ascending = 'А';
        descending = 'я';
        while (ascending <= 'Я') {
            //System.out.println(ascending + " " + descending);
            getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(ascending), "Run and move");
            getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(descending), "Run and move");
            ascending++;
            descending--;
        }
        ascending = '0';
        while (ascending <= '9') {
            //System.out.println(ascending);
            getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(ascending), "Run and move");
            ascending++;
        }
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(' '), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('.'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(','), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('/'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('\\'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('+'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('-'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('*'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('?'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('!'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('@'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('$'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('#'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('№'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('&'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('^'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke('%'), "Run and move");
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Run and move"); //8
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Run ENTER"); // 10
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Run ESC"); // 
        getRootPane().getActionMap().put("Run and move", new AbstractAction() {

            private static final long serialVersionUID = 8491492566619071329L;

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Hi! " + e.getActionCommand() + " " + (int) e.getActionCommand().charAt(0));

                if ((int) e.getActionCommand().charAt(0) == 8) {
                    buttonClickNumeric(new ActionEvent(jButton1, 0, ""));
                    return;
                }

                e.setSource(" ".equals(e.getActionCommand()) ? buttonSpaceV : jButton1);
                buttonClickNumeric(e);
                if (" ".equals(e.getActionCommand())) {
                    egnor = true;
                }
            }
        });

        getRootPane().getActionMap().put("Run ESC", new AbstractAction() {

            private static final long serialVersionUID = 8349567394546546759L;

            @Override
            public void actionPerformed(ActionEvent e) {
                jButton2ActionPerformed(e);
            }
        });
        getRootPane().getActionMap().put("Run ENTER", new AbstractAction() {

            private static final long serialVersionUID = 348567345689347559L;

            @Override
            public void actionPerformed(ActionEvent e) {
                jButton1ActionPerformed(e);
            }
        });

        if (WelcomeParams.getInstance().btnFont != null) {
            jButton1.setFont(WelcomeParams.getInstance().btnFont);
            jButton2.setFont(WelcomeParams.getInstance().btnFont);
        }

    }
    private static INetProperty netProperty;
    private static String serviceName;
    private static String siteMark;
    private static String result = null;
    private static int delay = 25000;

    /**
     * Статический метод который показывает модально диалог ввода строки.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param modal модальный диалог или нет
     * @param netProperty свойства работы с сервером
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @param caption
     * @return XML-описание результата предварительной записи, по сути это номерок. если null, то отказались от предварительной записи
     */
    public static String showInputDialog(Frame parent, boolean modal, INetProperty netProperty, boolean fullscreen, int delay, String caption) {
        FInputDialog.delay = delay;
        QLog.l().logger().info("Ввод клиентской информации");
        if (inputDialog == null) {
            inputDialog = new FInputDialog(parent, modal);
            inputDialog.setTitle("Ввод клиентской информации");
        }
        result = "";
        inputDialog.setSize(1280, 768);
        Uses.setLocation(inputDialog);
        FInputDialog.netProperty = netProperty;
        inputDialog.jLabel2.setText(caption);
        inputDialog.changeTextToLocale();
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo() && !fullscreen)) {
            Uses.setFullSize(inputDialog);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                inputDialog.setCursor(transparentCursor);
            }

        }
        inputDialog.labelKod.setText(inputDialog.resourceMap.getString("labelKod.text"));
        if (inputDialog.clockBack.isActive()) {
            inputDialog.clockBack.stop();
        }
        inputDialog.clockBack.start();
        inputDialog.setVisible(true);
        return result;
    }
    /**
     * Таймер, по которому будем выходить в корень меню.
     */
    public ATalkingClock clockBack = new ATalkingClock(delay, 1) {

        @Override
        public void run() {
            result = null;
            setVisible(false);
        }
    };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelAll = new QPanel(WelcomeParams.getInstance().backgroundImg);
        panelUp = new QPanel(WelcomeParams.getInstance().topImgSecondary);
        jLabel2 = new javax.swing.JLabel();
        panelBottom = new ru.apertum.qsystem.client.model.QPanel();
        jButton1 = new QButton(WelcomeParams.getInstance().servButtonType);
        jButton2 = new QButton(WelcomeParams.getInstance().servButtonType);
        panelMain = new ru.apertum.qsystem.client.model.QPanel();
        panelAdvKod = new ru.apertum.qsystem.client.model.QPanel();
        labelKod = new javax.swing.JLabel();
        panelButtonsNumeric = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        panelLettersUp = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        panelLettersMiddleLeft = new javax.swing.JPanel();
        panelLettersMiddleRight = new javax.swing.JPanel();
        panelLettersMiddle = new javax.swing.JPanel();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        panelLettersBottomLeft = new javax.swing.JPanel();
        panellLettersBottomRight = new javax.swing.JPanel();
        panelLettersBottom = new javax.swing.JPanel();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        panelSpaseLeft = new javax.swing.JPanel();
        panelSpaseRight = new javax.swing.JPanel();
        panelSpase = new javax.swing.JPanel();
        buttonSpaceV = new javax.swing.JButton();
        panelSpecButtons = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FInputDialog.class);
        panelAll.setBackground(resourceMap.getColor("panelAll.background")); // NOI18N
        panelAll.setName("panelAll"); // NOI18N

        panelUp.setBorder(new javax.swing.border.MatteBorder(null));
        panelUp.setCycle(false);
        panelUp.setEndColor(resourceMap.getColor("panelUp.endColor")); // NOI18N
        panelUp.setEndPoint(new java.awt.Point(0, 70));
        panelUp.setName("panelUp"); // NOI18N
        panelUp.setOpaque(false);
        panelUp.setPreferredSize(new java.awt.Dimension(1134, 150));
        panelUp.setStartColor(resourceMap.getColor("panelUp.startColor")); // NOI18N
        panelUp.setStartPoint(new java.awt.Point(0, -50));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout panelUpLayout = new javax.swing.GroupLayout(panelUp);
        panelUp.setLayout(panelUpLayout);
        panelUpLayout.setHorizontalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUpLayout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE)
                .addGap(114, 114, 114))
        );
        panelUpLayout.setVerticalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        panelBottom.setBorder(new javax.swing.border.MatteBorder(null));
        panelBottom.setEndPoint(new java.awt.Point(0, 100));
        panelBottom.setName("panelBottom"); // NOI18N
        panelBottom.setOpaque(false);
        panelBottom.setStartColor(resourceMap.getColor("panelBottom.startColor")); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton1.setFocusPainted(false);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton2.setFocusPainted(false);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 533, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelMain.setBackground(resourceMap.getColor("panelMain.background")); // NOI18N
        panelMain.setBorder(new javax.swing.border.MatteBorder(null));
        panelMain.setName("panelMain"); // NOI18N
        panelMain.setOpaque(false);

        panelAdvKod.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("panelAdvKod.border.lineColor"), 8)); // NOI18N
        panelAdvKod.setEndColor(resourceMap.getColor("panelAdvKod.endColor")); // NOI18N
        panelAdvKod.setEndPoint(new java.awt.Point(0, 50));
        panelAdvKod.setGradient(true);
        panelAdvKod.setName("panelAdvKod"); // NOI18N
        panelAdvKod.setStartColor(resourceMap.getColor("panelAdvKod.startColor")); // NOI18N

        labelKod.setBackground(resourceMap.getColor("labelKod.background")); // NOI18N
        labelKod.setFont(resourceMap.getFont("labelKod.font")); // NOI18N
        labelKod.setForeground(resourceMap.getColor("labelKod.foreground")); // NOI18N
        labelKod.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelKod.setText(resourceMap.getString("labelKod.text")); // NOI18N
        labelKod.setName("labelKod"); // NOI18N
        labelKod.setOpaque(true);

        javax.swing.GroupLayout panelAdvKodLayout = new javax.swing.GroupLayout(panelAdvKod);
        panelAdvKod.setLayout(panelAdvKodLayout);
        panelAdvKodLayout.setHorizontalGroup(
            panelAdvKodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelKod, javax.swing.GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE)
        );
        panelAdvKodLayout.setVerticalGroup(
            panelAdvKodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelKod, javax.swing.GroupLayout.PREFERRED_SIZE, 40, Short.MAX_VALUE)
        );

        panelButtonsNumeric.setBackground(resourceMap.getColor("panelButtonsNumeric.background")); // NOI18N
        panelButtonsNumeric.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtonsNumeric.setName("panelButtonsNumeric"); // NOI18N
        panelButtonsNumeric.setOpaque(false);
        panelButtonsNumeric.setLayout(new java.awt.GridLayout(1, 0, 25, 0));

        jButton3.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton3.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton3.setFocusPainted(false);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton3);

        jButton4.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton4.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton4.setFocusPainted(false);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton4);

        jButton5.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton5.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton5.setFocusPainted(false);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton5);

        jButton6.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton6.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton6.setFocusPainted(false);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton6);

        jButton7.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton7.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton7.setFocusPainted(false);
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton7);

        jButton8.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton8.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton8.setFocusPainted(false);
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton8);

        jButton9.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton9.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton9.setFocusPainted(false);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton9);

        jButton10.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton10.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton10.setFocusPainted(false);
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton10);

        jButton11.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton11.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton11.setFocusPainted(false);
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton11);

        jButton12.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton12.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton12.setFocusPainted(false);
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton12);

        jButton13.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton13.setForeground(resourceMap.getColor("jButton13.foreground")); // NOI18N
        jButton13.setIcon(resourceMap.getIcon("jButton13.icon")); // NOI18N
        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton13.setFocusPainted(false);
        jButton13.setName("jButton13"); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton13);

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);

        panelLettersUp.setName("panelLettersUp"); // NOI18N
        panelLettersUp.setOpaque(false);
        panelLettersUp.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton14.setFont(resourceMap.getFont("jButton14.font")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton14.setFocusPainted(false);
        jButton14.setName("jButton14"); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton14);

        jButton15.setFont(resourceMap.getFont("jButton15.font")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton15.setFocusPainted(false);
        jButton15.setName("jButton15"); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton15);

        jButton16.setFont(resourceMap.getFont("jButton16.font")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton16.setFocusPainted(false);
        jButton16.setName("jButton16"); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton16);

        jButton17.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton17.setFocusPainted(false);
        jButton17.setName("jButton17"); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton17);

        jButton18.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton18.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton18.setFocusPainted(false);
        jButton18.setName("jButton18"); // NOI18N
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton18);

        jButton19.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton19.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton19.setFocusPainted(false);
        jButton19.setName("jButton19"); // NOI18N
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton19);

        jButton20.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton20.setFocusPainted(false);
        jButton20.setName("jButton20"); // NOI18N
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton20);

        jButton21.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton21.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton21.setFocusPainted(false);
        jButton21.setName("jButton21"); // NOI18N
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton21);

        jButton22.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton22.setFocusPainted(false);
        jButton22.setName("jButton22"); // NOI18N
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton22);

        jButton23.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton23.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton23.setFocusPainted(false);
        jButton23.setName("jButton23"); // NOI18N
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton23);

        jButton24.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton24.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton24.setFocusPainted(false);
        jButton24.setName("jButton24"); // NOI18N
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton24);

        jButton25.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton25.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton25.setFocusPainted(false);
        jButton25.setName("jButton25"); // NOI18N
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersUp.add(jButton25);

        panelLettersMiddleLeft.setBorder(new javax.swing.border.MatteBorder(null));
        panelLettersMiddleLeft.setName("panelLettersMiddleLeft"); // NOI18N
        panelLettersMiddleLeft.setOpaque(false);

        javax.swing.GroupLayout panelLettersMiddleLeftLayout = new javax.swing.GroupLayout(panelLettersMiddleLeft);
        panelLettersMiddleLeft.setLayout(panelLettersMiddleLeftLayout);
        panelLettersMiddleLeftLayout.setHorizontalGroup(
            panelLettersMiddleLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );
        panelLettersMiddleLeftLayout.setVerticalGroup(
            panelLettersMiddleLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 55, Short.MAX_VALUE)
        );

        panelLettersMiddleRight.setBorder(new javax.swing.border.MatteBorder(null));
        panelLettersMiddleRight.setName("panelLettersMiddleRight"); // NOI18N
        panelLettersMiddleRight.setOpaque(false);

        javax.swing.GroupLayout panelLettersMiddleRightLayout = new javax.swing.GroupLayout(panelLettersMiddleRight);
        panelLettersMiddleRight.setLayout(panelLettersMiddleRightLayout);
        panelLettersMiddleRightLayout.setHorizontalGroup(
            panelLettersMiddleRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );
        panelLettersMiddleRightLayout.setVerticalGroup(
            panelLettersMiddleRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 55, Short.MAX_VALUE)
        );

        panelLettersMiddle.setName("panelLettersMiddle"); // NOI18N
        panelLettersMiddle.setOpaque(false);
        panelLettersMiddle.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton26.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton26.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton26.setFocusPainted(false);
        jButton26.setName("jButton26"); // NOI18N
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton26);

        jButton27.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton27.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton27.setFocusPainted(false);
        jButton27.setName("jButton27"); // NOI18N
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton27);

        jButton28.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton28.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton28.setFocusPainted(false);
        jButton28.setName("jButton28"); // NOI18N
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton28);

        jButton29.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton29.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton29.setFocusPainted(false);
        jButton29.setName("jButton29"); // NOI18N
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton29);

        jButton30.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton30.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton30.setFocusPainted(false);
        jButton30.setName("jButton30"); // NOI18N
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton30);

        jButton31.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton31.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton31.setFocusPainted(false);
        jButton31.setName("jButton31"); // NOI18N
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton31);

        jButton32.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton32.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton32.setFocusPainted(false);
        jButton32.setName("jButton32"); // NOI18N
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton32);

        jButton33.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton33.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton33.setFocusPainted(false);
        jButton33.setName("jButton33"); // NOI18N
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton33);

        jButton34.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton34.setText(resourceMap.getString("jButton34.text")); // NOI18N
        jButton34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton34.setFocusPainted(false);
        jButton34.setName("jButton34"); // NOI18N
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton34);

        jButton35.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton35.setText(resourceMap.getString("jButton35.text")); // NOI18N
        jButton35.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton35.setFocusPainted(false);
        jButton35.setName("jButton35"); // NOI18N
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton35);

        jButton36.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton36.setText(resourceMap.getString("jButton36.text")); // NOI18N
        jButton36.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton36.setFocusPainted(false);
        jButton36.setName("jButton36"); // NOI18N
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersMiddle.add(jButton36);

        panelLettersBottomLeft.setBorder(new javax.swing.border.MatteBorder(null));
        panelLettersBottomLeft.setName("panelLettersBottomLeft"); // NOI18N
        panelLettersBottomLeft.setOpaque(false);

        javax.swing.GroupLayout panelLettersBottomLeftLayout = new javax.swing.GroupLayout(panelLettersBottomLeft);
        panelLettersBottomLeft.setLayout(panelLettersBottomLeftLayout);
        panelLettersBottomLeftLayout.setHorizontalGroup(
            panelLettersBottomLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panelLettersBottomLeftLayout.setVerticalGroup(
            panelLettersBottomLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 54, Short.MAX_VALUE)
        );

        panellLettersBottomRight.setBorder(new javax.swing.border.MatteBorder(null));
        panellLettersBottomRight.setName("panellLettersBottomRight"); // NOI18N
        panellLettersBottomRight.setOpaque(false);

        javax.swing.GroupLayout panellLettersBottomRightLayout = new javax.swing.GroupLayout(panellLettersBottomRight);
        panellLettersBottomRight.setLayout(panellLettersBottomRightLayout);
        panellLettersBottomRightLayout.setHorizontalGroup(
            panellLettersBottomRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panellLettersBottomRightLayout.setVerticalGroup(
            panellLettersBottomRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 54, Short.MAX_VALUE)
        );

        panelLettersBottom.setName("panelLettersBottom"); // NOI18N
        panelLettersBottom.setOpaque(false);
        panelLettersBottom.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton37.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton37.setText(resourceMap.getString("jButton37.text")); // NOI18N
        jButton37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton37.setFocusPainted(false);
        jButton37.setName("jButton37"); // NOI18N
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton37);

        jButton38.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton38.setText(resourceMap.getString("jButton38.text")); // NOI18N
        jButton38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton38.setFocusPainted(false);
        jButton38.setName("jButton38"); // NOI18N
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton38);

        jButton39.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton39.setText(resourceMap.getString("jButton39.text")); // NOI18N
        jButton39.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton39.setFocusPainted(false);
        jButton39.setName("jButton39"); // NOI18N
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton39);

        jButton40.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton40.setText(resourceMap.getString("jButton40.text")); // NOI18N
        jButton40.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton40.setFocusPainted(false);
        jButton40.setName("jButton40"); // NOI18N
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton40);

        jButton41.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton41.setText(resourceMap.getString("jButton41.text")); // NOI18N
        jButton41.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton41.setFocusPainted(false);
        jButton41.setName("jButton41"); // NOI18N
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton41);

        jButton42.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton42.setText(resourceMap.getString("jButton42.text")); // NOI18N
        jButton42.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton42.setFocusPainted(false);
        jButton42.setName("jButton42"); // NOI18N
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton42);

        jButton43.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton43.setText(resourceMap.getString("jButton43.text")); // NOI18N
        jButton43.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton43.setFocusPainted(false);
        jButton43.setName("jButton43"); // NOI18N
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton43);

        jButton44.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton44.setText(resourceMap.getString("jButton44.text")); // NOI18N
        jButton44.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton44.setFocusPainted(false);
        jButton44.setName("jButton44"); // NOI18N
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton44);

        jButton45.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton45.setText(resourceMap.getString("jButton45.text")); // NOI18N
        jButton45.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton45.setFocusPainted(false);
        jButton45.setName("jButton45"); // NOI18N
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton45);

        jButton46.setFont(resourceMap.getFont("jButton46.font")); // NOI18N
        jButton46.setIcon(resourceMap.getIcon("jButton46.icon")); // NOI18N
        jButton46.setText(resourceMap.getString("jButton46.text")); // NOI18N
        jButton46.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        jButton46.setFocusPainted(false);
        jButton46.setName("jButton46"); // NOI18N
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelLettersBottom.add(jButton46);

        panelSpaseLeft.setBorder(new javax.swing.border.MatteBorder(null));
        panelSpaseLeft.setName("panelSpaseLeft"); // NOI18N
        panelSpaseLeft.setOpaque(false);

        javax.swing.GroupLayout panelSpaseLeftLayout = new javax.swing.GroupLayout(panelSpaseLeft);
        panelSpaseLeft.setLayout(panelSpaseLeftLayout);
        panelSpaseLeftLayout.setHorizontalGroup(
            panelSpaseLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );
        panelSpaseLeftLayout.setVerticalGroup(
            panelSpaseLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        panelSpaseRight.setBorder(new javax.swing.border.MatteBorder(null));
        panelSpaseRight.setName("panelSpaseRight"); // NOI18N
        panelSpaseRight.setOpaque(false);

        javax.swing.GroupLayout panelSpaseRightLayout = new javax.swing.GroupLayout(panelSpaseRight);
        panelSpaseRight.setLayout(panelSpaseRightLayout);
        panelSpaseRightLayout.setHorizontalGroup(
            panelSpaseRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 261, Short.MAX_VALUE)
        );
        panelSpaseRightLayout.setVerticalGroup(
            panelSpaseRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        panelSpase.setName("panelSpase"); // NOI18N
        panelSpase.setOpaque(false);
        panelSpase.setLayout(new java.awt.GridLayout(1, 0));

        buttonSpaceV.setFont(resourceMap.getFont("buttonSpaceV.font")); // NOI18N
        buttonSpaceV.setText(resourceMap.getString("buttonSpaceV.text")); // NOI18N
        buttonSpaceV.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(0), javax.swing.BorderFactory.createBevelBorder(0)));
        buttonSpaceV.setFocusPainted(false);
        buttonSpaceV.setName("buttonSpaceV"); // NOI18N
        buttonSpaceV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelSpase.add(buttonSpaceV);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelLettersBottomLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelLettersBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(panellLettersBottomRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelLettersMiddleLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelLettersMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 960, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(panelLettersMiddleRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelLettersUp, javax.swing.GroupLayout.DEFAULT_SIZE, 1096, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelSpaseLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelSpase, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(panelSpaseRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelLettersUp, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelLettersMiddleLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelLettersMiddleRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelLettersMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(panelLettersBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panellLettersBottomRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelLettersBottomLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelSpaseLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSpase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSpaseRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        panelSpecButtons.setBorder(new javax.swing.border.MatteBorder(null));
        panelSpecButtons.setName("panelSpecButtons"); // NOI18N
        panelSpecButtons.setOpaque(false);
        panelSpecButtons.setPreferredSize(new java.awt.Dimension(1118, 52));

        javax.swing.GroupLayout panelSpecButtonsLayout = new javax.swing.GroupLayout(panelSpecButtons);
        panelSpecButtons.setLayout(panelSpecButtonsLayout);
        panelSpecButtonsLayout.setHorizontalGroup(
            panelSpecButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1116, Short.MAX_VALUE)
        );
        panelSpecButtonsLayout.setVerticalGroup(
            panelSpecButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelAdvKod, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSpecButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelButtonsNumeric, javax.swing.GroupLayout.DEFAULT_SIZE, 1118, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainLayout.createSequentialGroup()
                .addComponent(panelAdvKod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelButtonsNumeric, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSpecButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelUp, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
            .addComponent(panelBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelAllLayout.setVerticalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAllLayout.createSequentialGroup()
                .addComponent(panelUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    final private org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FInputDialog.class);

    private void changeTextToLocale() {
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        labelKod.setText(resourceMap.getString("labelKod.text")); // NOI18N

        buttonSpaceV.setText(resourceMap.getString("buttonSpaceV.text")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton34.setText(resourceMap.getString("jButton34.text")); // NOI18N
        jButton35.setText(resourceMap.getString("jButton35.text")); // NOI18N
        jButton36.setText(resourceMap.getString("jButton36.text")); // NOI18N
        jButton37.setText(resourceMap.getString("jButton37.text")); // NOI18N
        jButton38.setText(resourceMap.getString("jButton38.text")); // NOI18N
        jButton39.setText(resourceMap.getString("jButton39.text")); // NOI18N
        jButton40.setText(resourceMap.getString("jButton40.text")); // NOI18N
        jButton41.setText(resourceMap.getString("jButton41.text")); // NOI18N
        jButton42.setText(resourceMap.getString("jButton42.text")); // NOI18N
        jButton43.setText(resourceMap.getString("jButton43.text")); // NOI18N
        jButton44.setText(resourceMap.getString("jButton44.text")); // NOI18N
        jButton45.setText(resourceMap.getString("jButton45.text")); // NOI18N
        jButton46.setText(resourceMap.getString("jButton46.text")); // NOI18N
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        result = null;
        if (clockBack.isActive()) {
            clockBack.stop();
        }
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // послать запрос на регистрацию предварительного
        // в ответ должен вернуться номерок в XML виде
        if (labelKod.getText().length() != 0 && !resourceMap.getString("labelKod.text").equals(labelKod.getText())) {
            if (clockBack.isActive()) {
                clockBack.stop();
            }
            result = labelKod.getText().replaceAll("_", " ").replace("<html>", "");
            setVisible(false);
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    private boolean egnor = false; // чтоб по нажатию пробела на клаве не печаталась еще буква от виртуальной кнопки, что сработает по пробелу
    private void buttonClickNumeric(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClickNumeric

        if (clockBack.isActive()) {
            clockBack.stop();
        }

        if (egnor) {
            egnor = false;
            return;
        }

        clockBack.start();
        if ("".equals(evt.getActionCommand())) {
            // удаление
            if (resourceMap.getString("labelKod.text").equals(labelKod.getText().replace("<html>", "")) || "".equals(labelKod.getText().replace("<html>", ""))) {
                labelKod.setText("");
            } else {
                String s = labelKod.getText().substring(0, labelKod.getText().length() - 1);
                if (s.endsWith(" ")) {
                    s = s.substring(0, s.length() - 1) + "_";
                }
                labelKod.setText(s);
            }

        } else {
            // добавление цифры
            if (resourceMap.getString("labelKod.text").equals(labelKod.getText())) {
                labelKod.setText("");
            }
            labelKod.setText(("<html>" + labelKod.getText().replace("<html>", "").replaceAll("_", " ").replaceAll("  ", " ")
                    + (((JButton) evt.getSource()).getName().equalsIgnoreCase("buttonSpaceV") ? "_" : evt.getActionCommand())).replaceAll(" _", "_"));
        }
    }//GEN-LAST:event_buttonClickNumeric
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonSpaceV;
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
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelKod;
    private ru.apertum.qsystem.client.model.QPanel panelAdvKod;
    private ru.apertum.qsystem.client.model.QPanel panelAll;
    private ru.apertum.qsystem.client.model.QPanel panelBottom;
    private javax.swing.JPanel panelButtonsNumeric;
    private javax.swing.JPanel panelLettersBottom;
    private javax.swing.JPanel panelLettersBottomLeft;
    private javax.swing.JPanel panelLettersMiddle;
    private javax.swing.JPanel panelLettersMiddleLeft;
    private javax.swing.JPanel panelLettersMiddleRight;
    private javax.swing.JPanel panelLettersUp;
    private ru.apertum.qsystem.client.model.QPanel panelMain;
    private javax.swing.JPanel panelSpase;
    private javax.swing.JPanel panelSpaseLeft;
    private javax.swing.JPanel panelSpaseRight;
    private javax.swing.JPanel panelSpecButtons;
    private ru.apertum.qsystem.client.model.QPanel panelUp;
    private javax.swing.JPanel panellLettersBottomRight;
    // End of variables declaration//GEN-END:variables
}
