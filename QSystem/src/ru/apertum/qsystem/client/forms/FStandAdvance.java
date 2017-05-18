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
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.cmd.RpcStandInService;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QConfig;

/**
 * Created on 18.09.2009, 11:33:46 Диалог постановки в очередь по коду предварительной регистрации Имеет метод для осуществления всех действий. Вся логика
 * инкапсулирована в этом классе. Должен уметь работать с комовским сканером.
 *
 * @author Evgeniy Egorov
 */
public class FStandAdvance extends javax.swing.JDialog {

    private static FStandAdvance standAdvance;

    /**
     * Creates new form FStandAdvance
     *
     * @param parent
     * @param modal
     */
    public FStandAdvance(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    private static INetProperty netProperty;
    private static String serviceName;
    private static String siteMark;
    private static RpcStandInService result = null;
    private static int delay = 10000;
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FStandAdvance.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Статический метод который показывает модально диалог выбора времени для предварительной записи клиентов.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param modal модальный диалог или нет
     * @param netProperty свойства работы с сервером
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @return XML-описание результата предварительной записи, по сути это номерок. если null, то отказались от предварительной записи
     */
    public static RpcStandInService showAdvanceStandDialog(Frame parent, boolean modal, INetProperty netProperty, boolean fullscreen, int delay) {
        FStandAdvance.delay = delay;
        QLog.l().logger().info("Ввод кода предварительной записи");
        if (standAdvance == null) {
            standAdvance = new FStandAdvance(parent, modal);
            standAdvance.setTitle(getLocaleMessage("dialog.input_code"));
        }
        result = null;
        Uses.setLocation(standAdvance);
        FStandAdvance.netProperty = netProperty;
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo() && !fullscreen)) {
            //Uses.setFullSize(standAdvance);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                standAdvance.setCursor(transparentCursor);
            }

        }
        standAdvance.changeTextToLocale();
        if (standAdvance.clockBack.isActive()) {
            standAdvance.clockBack.stop();
        }
        standAdvance.clockBack.start();
        standAdvance.setVisible(true);
        return result;
    }
    private static String DEFAULT_KOD = getLocaleMessage("dialog.default_code");
    /**
     * Таймер, по которому будем выходить в корень меню.
     */
    public ATalkingClock clockBack = new ATalkingClock(delay, 1) {

        @Override
        public void run() {
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
        panelUp = new ru.apertum.qsystem.client.model.QPanel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FStandAdvance.class);
        panelAll.setBackground(resourceMap.getColor("panelAll.background")); // NOI18N
        panelAll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 7));
        panelAll.setName("panelAll"); // NOI18N

        panelUp.setBorder(new javax.swing.border.MatteBorder(null));
        panelUp.setCycle(java.lang.Boolean.FALSE);
        panelUp.setEndColor(resourceMap.getColor("panelUp.endColor")); // NOI18N
        panelUp.setEndPoint(new java.awt.Point(0, 70));
        panelUp.setName("panelUp"); // NOI18N
        panelUp.setOpaque(false);
        panelUp.setStartColor(resourceMap.getColor("panelUp.startColor")); // NOI18N
        panelUp.setStartPoint(new java.awt.Point(0, -50));

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout panelUpLayout = new javax.swing.GroupLayout(panelUp);
        panelUp.setLayout(panelUpLayout);
        panelUpLayout.setHorizontalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUpLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelUpLayout.setVerticalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUpLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelBottom.setBorder(new javax.swing.border.MatteBorder(null));
        panelBottom.setEndPoint(new java.awt.Point(0, 100));
        panelBottom.setName("panelBottom"); // NOI18N
        panelBottom.setOpaque(false);
        panelBottom.setStartColor(resourceMap.getColor("panelBottom.startColor")); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        panelAdvKod.setEndColor(resourceMap.getColor("panelAdvKod.endColor")); // NOI18N
        panelAdvKod.setEndPoint(new java.awt.Point(0, 50));
        panelAdvKod.setName("panelAdvKod"); // NOI18N
        panelAdvKod.setOpaque(false);
        panelAdvKod.setStartColor(resourceMap.getColor("panelAdvKod.startColor")); // NOI18N

        labelKod.setBackground(resourceMap.getColor("labelKod.background")); // NOI18N
        labelKod.setFont(resourceMap.getFont("labelKod.font")); // NOI18N
        labelKod.setForeground(resourceMap.getColor("labelKod.foreground")); // NOI18N
        labelKod.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelKod.setText(resourceMap.getString("labelKod.text")); // NOI18N
        labelKod.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("labelKod.border.lineColor"), 8)); // NOI18N
        labelKod.setName("labelKod"); // NOI18N
        labelKod.setOpaque(true);

        javax.swing.GroupLayout panelAdvKodLayout = new javax.swing.GroupLayout(panelAdvKod);
        panelAdvKod.setLayout(panelAdvKodLayout);
        panelAdvKodLayout.setHorizontalGroup(
            panelAdvKodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdvKodLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelKod, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelAdvKodLayout.setVerticalGroup(
            panelAdvKodLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdvKodLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelKod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelButtonsNumeric.setBackground(resourceMap.getColor("panelButtonsNumeric.background")); // NOI18N
        panelButtonsNumeric.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtonsNumeric.setName("panelButtonsNumeric"); // NOI18N
        panelButtonsNumeric.setOpaque(false);
        panelButtonsNumeric.setLayout(new java.awt.GridLayout(1, 0, 5, 2));

        jButton3.setFont(resourceMap.getFont("jButton7.font")); // NOI18N
        jButton3.setForeground(resourceMap.getColor("jButton7.foreground")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton6.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton7.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton8.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton9.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton10.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton11.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton12.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
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
        jButton13.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton13.setName("jButton13"); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClickNumeric(evt);
            }
        });
        panelButtonsNumeric.add(jButton13);

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAdvKod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(panelButtonsNumeric, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addComponent(panelAdvKod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelButtonsNumeric, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelUp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void changeTextToLocale() {
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FStandAdvance.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        DEFAULT_KOD = getLocaleMessage("dialog.default_code");
        labelKod.setText(DEFAULT_KOD);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
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
        if (labelKod.getText().length() != 0 && !DEFAULT_KOD.equals(labelKod.getText())) {
            if (clockBack.isActive()) {
                clockBack.stop();
            }
            result = NetCommander.standAndCheckAdvance(netProperty, Long.parseLong(labelKod.getText()));
            setVisible(false);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void buttonClickNumeric(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClickNumeric

        if (clockBack.isActive()) {
            clockBack.stop();
        }

        clockBack.start();
        if ("".equals(evt.getActionCommand())) {
            // удаление
            if (DEFAULT_KOD.equals(labelKod.getText()) || "".equals(labelKod.getText()) || labelKod.getText().length() == 1) {
                labelKod.setText(DEFAULT_KOD);
            } else {
                labelKod.setText(labelKod.getText().substring(0, labelKod.getText().length() - 1));
            }

        } else {
            // добавление цифры
            if (DEFAULT_KOD.equals(labelKod.getText())) {
                labelKod.setText("");
            }
            if (labelKod.getText().length() < 18) {
                labelKod.setText(labelKod.getText() + evt.getActionCommand());
            }
        }
    }//GEN-LAST:event_buttonClickNumeric
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel labelKod;
    private ru.apertum.qsystem.client.model.QPanel panelAdvKod;
    private ru.apertum.qsystem.client.model.QPanel panelAll;
    private ru.apertum.qsystem.client.model.QPanel panelBottom;
    private javax.swing.JPanel panelButtonsNumeric;
    private ru.apertum.qsystem.client.model.QPanel panelMain;
    private ru.apertum.qsystem.client.model.QPanel panelUp;
    // End of variables declaration//GEN-END:variables
}
