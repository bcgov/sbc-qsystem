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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.dom4j.Element;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.help.Helper;
import ru.apertum.qsystem.common.Uses;

/**
 * Created on 10 Апрель 2009 г., 10:27 Конфигурирование главного табло на плазме или ЖК
 *
 * @author Evgeniy Egorov
 */
public class FBoardConfig extends AFBoardRedactor {

    private static ResourceMap localeMap = null;
    private static FBoardConfig boardConfig;
    private Element topElement;
    private Element bottomElement;
    private Element bottomElement2;
    private Element leftElement;
    private Element rightElement;
    private Element mainElement;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonDown;
    private javax.swing.JButton buttonDown2;
    private javax.swing.JButton buttonLeft;
    private javax.swing.JButton buttonMain;
    private javax.swing.JButton buttonRight;
    private javax.swing.JButton buttonTop;
    private javax.swing.JCheckBox checkBoxDown;
    private javax.swing.JCheckBox checkBoxDown2;
    private javax.swing.JCheckBox checkBoxLeft;
    private javax.swing.JCheckBox checkBoxRight;
    private javax.swing.JCheckBox checkBoxUp;
    private javax.swing.JLabel labelDown;
    private javax.swing.JLabel labelDown2;
    private javax.swing.JLabel labelLeft;
    private javax.swing.JLabel labelRight;
    private javax.swing.JLabel labelUp;
    private javax.swing.JPanel panelDown1;
    private javax.swing.JPanel panelDown2;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelRight;
    private javax.swing.JPanel panelUp;
    private javax.swing.JSplitPane spDown;
    private javax.swing.JSplitPane spDown2;
    private javax.swing.JSplitPane spLeft;
    private javax.swing.JSplitPane spRight;
    private javax.swing.JSplitPane spUp;

    /**
     * Creates new form FBoardConfig
     */
    public FBoardConfig(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                setDividerLocation();
            }
        });
        try {
            setIconImage(ImageIO.read(
                FParamsEditor.class
                    .getResource("/ru/apertum/qsystem/client/forms/resources/admin.png")));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        //привязка помощи к форме.
        final Helper helper = Helper.getHelp("ru/apertum/qsystem/client/help/admin.hs");
        helper.enableHelpKey(spUp, "editTablo");
    }

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(FBoardConfig.class);
        }
        return localeMap.getString(key);
    }

    public static FBoardConfig getBoardConfig(JFrame parent, boolean modal) {
        if (boardConfig == null || (parent != boardConfig.parent || modal != boardConfig.modal)) {
            boardConfig = new FBoardConfig(parent, modal);
        }
        return boardConfig;
    }

    /**
     * Обновить параметры
     */
    @Override
    protected void refresh() {
        topElement = getParams().element(Uses.TAG_BOARD_TOP);
        bottomElement = getParams().element(Uses.TAG_BOARD_BOTTOM);
        bottomElement2 = getParams().element(Uses.TAG_BOARD_BOTTOM_2);
        leftElement = getParams().element(Uses.TAG_BOARD_LEFT);
        rightElement = getParams().element(Uses.TAG_BOARD_RIGHT);
        mainElement = getParams().element(Uses.TAG_BOARD_MAIN);
        //выставим размеры и видимость
        checkBoxUp.setSelected("1".equals(topElement.attributeValue(Uses.TAG_BOARD_VISIBLE_PANEL)));
        checkBoxLeft
            .setSelected("1".equals(leftElement.attributeValue(Uses.TAG_BOARD_VISIBLE_PANEL)));
        checkBoxRight
            .setSelected("1".equals(rightElement.attributeValue(Uses.TAG_BOARD_VISIBLE_PANEL)));
        checkBoxDown
            .setSelected("1".equals(bottomElement.attributeValue(Uses.TAG_BOARD_VISIBLE_PANEL)));
        checkBoxDown2
            .setSelected("1".equals(bottomElement2.attributeValue(Uses.TAG_BOARD_VISIBLE_PANEL)));

        setDividerLocation();

    }

    private void setDividerLocation() {
        spUp.setDividerLocation(
            Double.parseDouble(topElement.attributeValue(Uses.TAG_BOARD_PANEL_SIZE)));
        spDown.setDividerLocation(
            Double.parseDouble(bottomElement.attributeValue(Uses.TAG_BOARD_PANEL_SIZE)));
        spLeft.setDividerLocation(
            Double.parseDouble(leftElement.attributeValue(Uses.TAG_BOARD_PANEL_SIZE)));
        spRight.setDividerLocation(
            Double.parseDouble(rightElement.attributeValue(Uses.TAG_BOARD_PANEL_SIZE)));
        spDown2.setDividerLocation(Double.parseDouble(bottomElement2.attributeValue(
            Uses.TAG_BOARD_PANEL_SIZE))); // вычитаемое это подгон адекватного открытия, видно пикселки на что-то еще жрутся.
    }

    @Override
    public void saveResult() throws IOException {
        saveForm();
        super.saveResult();
    }

    private void refreshPanel(Element elem) {
    }

    protected void saveForm() {
        // visible="1" Размер="35"
        topElement.addAttribute(Uses.TAG_BOARD_VISIBLE_PANEL, checkBoxUp.isSelected() ? "1" : "0");
        leftElement
            .addAttribute(Uses.TAG_BOARD_VISIBLE_PANEL, checkBoxLeft.isSelected() ? "1" : "0");
        rightElement
            .addAttribute(Uses.TAG_BOARD_VISIBLE_PANEL, checkBoxRight.isSelected() ? "1" : "0");
        bottomElement
            .addAttribute(Uses.TAG_BOARD_VISIBLE_PANEL, checkBoxDown.isSelected() ? "1" : "0");
        bottomElement2
            .addAttribute(Uses.TAG_BOARD_VISIBLE_PANEL, checkBoxDown2.isSelected() ? "1" : "0");

        topElement.addAttribute(Uses.TAG_BOARD_PANEL_SIZE, String.valueOf(
            Uses.roundAs(new Double(spUp.getDividerLocation()) / (spUp.getHeight() + 0.009), 2)));
        leftElement.addAttribute(Uses.TAG_BOARD_PANEL_SIZE, String.valueOf(
            Uses.roundAs(new Double(spLeft.getDividerLocation()) / (spLeft.getWidth() + 0.009),
                2)));
        rightElement.addAttribute(Uses.TAG_BOARD_PANEL_SIZE, String.valueOf(
            Uses.roundAs(new Double(spRight.getDividerLocation()) / spRight.getWidth() + 0.009,
                2)));
        bottomElement.addAttribute(Uses.TAG_BOARD_PANEL_SIZE, String.valueOf(
            Uses.roundAs(new Double(spDown.getDividerLocation()) / spDown.getHeight() + 0.009, 2)));
        bottomElement2.addAttribute(Uses.TAG_BOARD_PANEL_SIZE, String.valueOf(
            Uses.roundAs(new Double(spDown2.getDividerLocation()) / spDown2.getHeight() - 0.03,
                2)));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spUp = new javax.swing.JSplitPane();
        panelUp = new javax.swing.JPanel();
        checkBoxUp = new javax.swing.JCheckBox();
        labelUp = new javax.swing.JLabel();
        buttonTop = new javax.swing.JButton();
        spDown = new javax.swing.JSplitPane();
        spLeft = new javax.swing.JSplitPane();
        panelLeft = new javax.swing.JPanel();
        checkBoxLeft = new javax.swing.JCheckBox();
        buttonLeft = new javax.swing.JButton();
        labelLeft = new javax.swing.JLabel();
        spRight = new javax.swing.JSplitPane();
        panelRight = new javax.swing.JPanel();
        checkBoxRight = new javax.swing.JCheckBox();
        labelRight = new javax.swing.JLabel();
        buttonRight = new javax.swing.JButton();
        panelMain = new javax.swing.JPanel();
        buttonMain = new javax.swing.JButton();
        spDown2 = new javax.swing.JSplitPane();
        panelDown1 = new javax.swing.JPanel();
        checkBoxDown = new javax.swing.JCheckBox();
        labelDown = new javax.swing.JLabel();
        buttonDown = new javax.swing.JButton();
        panelDown2 = new javax.swing.JPanel();
        checkBoxDown2 = new javax.swing.JCheckBox();
        labelDown2 = new javax.swing.JLabel();
        buttonDown2 = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FBoardConfig.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        spUp.setDividerLocation(150);
        spUp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spUp.setContinuousLayout(true);
        spUp.setName("spUp"); // NOI18N

        panelUp.setBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelUp.setName("panelUp"); // NOI18N
        panelUp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelUpComponentResized(evt);
            }
        });

        checkBoxUp.setText(resourceMap.getString("checkBoxUp.text")); // NOI18N
        checkBoxUp.setToolTipText(resourceMap.getString("checkBoxUp.toolTipText")); // NOI18N
        checkBoxUp.setName("checkBoxUp"); // NOI18N

        labelUp.setText(resourceMap.getString("labelUp.text")); // NOI18N
        labelUp.setName("labelUp"); // NOI18N

        buttonTop.setText(resourceMap.getString("buttonTop.text")); // NOI18N
        buttonTop.setToolTipText(resourceMap.getString("buttonTop.toolTipText")); // NOI18N
        buttonTop.setName("buttonTop"); // NOI18N
        buttonTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelUpLayout = new javax.swing.GroupLayout(panelUp);
        panelUp.setLayout(panelUpLayout);
        panelUpLayout.setHorizontalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelUpLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(checkBoxUp)
                    .addGap(6, 6, 6)
                    .addComponent(labelUp)
                    .addGap(18, 18, 18)
                    .addComponent(buttonTop)
                    .addContainerGap(581, Short.MAX_VALUE))
        );
        panelUpLayout.setVerticalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelUpLayout.createSequentialGroup()
                    .addGroup(
                        panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelUpLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelUpLayout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonTop, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        14,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelUp)))
                            .addComponent(checkBoxUp))
                    .addContainerGap(120, Short.MAX_VALUE))
        );

        spUp.setTopComponent(panelUp);

        spDown.setDividerLocation(250);
        spDown.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spDown.setContinuousLayout(true);
        spDown.setName("spDown"); // NOI18N

        spLeft.setDividerLocation(150);
        spLeft.setContinuousLayout(true);
        spLeft.setName("spLeft"); // NOI18N

        panelLeft.setBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelLeft.setName("panelLeft"); // NOI18N
        panelLeft.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelLeftComponentResized(evt);
            }
        });

        checkBoxLeft.setText(resourceMap.getString("checkBoxLeft.text")); // NOI18N
        checkBoxLeft.setToolTipText(resourceMap.getString("checkBoxLeft.toolTipText")); // NOI18N
        checkBoxLeft.setName("checkBoxLeft"); // NOI18N

        buttonLeft.setText(resourceMap.getString("buttonLeft.text")); // NOI18N
        buttonLeft.setToolTipText(resourceMap.getString("buttonLeft.toolTipText")); // NOI18N
        buttonLeft.setName("buttonLeft"); // NOI18N
        buttonLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLeftActionPerformed(evt);
            }
        });

        labelLeft.setText(resourceMap.getString("labelLeft.text")); // NOI18N
        labelLeft.setName("labelLeft"); // NOI18N

        javax.swing.GroupLayout panelLeftLayout = new javax.swing.GroupLayout(panelLeft);
        panelLeft.setLayout(panelLeftLayout);
        panelLeftLayout.setHorizontalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLeftLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        panelLeftLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonLeft)
                            .addComponent(labelLeft)
                            .addComponent(checkBoxLeft))
                    .addContainerGap(90, Short.MAX_VALUE))
        );
        panelLeftLayout.setVerticalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelLeftLayout.createSequentialGroup()
                    .addComponent(checkBoxLeft)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(labelLeft)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(buttonLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 15,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(180, Short.MAX_VALUE))
        );

        spLeft.setLeftComponent(panelLeft);

        spRight.setDividerLocation(320);
        spRight.setContinuousLayout(true);
        spRight.setName("spRight"); // NOI18N

        panelRight.setBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelRight.setName("panelRight"); // NOI18N
        panelRight.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelRightComponentResized(evt);
            }
        });

        checkBoxRight.setText(resourceMap.getString("checkBoxRight.text")); // NOI18N
        checkBoxRight.setToolTipText(resourceMap.getString("checkBoxRight.toolTipText")); // NOI18N
        checkBoxRight.setName("checkBoxRight"); // NOI18N

        labelRight.setText(resourceMap.getString("labelRight.text")); // NOI18N
        labelRight.setName("labelRight"); // NOI18N

        buttonRight.setText(resourceMap.getString("buttonRight.text")); // NOI18N
        buttonRight.setToolTipText(resourceMap.getString("buttonRight.toolTipText")); // NOI18N
        buttonRight.setName("buttonRight"); // NOI18N
        buttonRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRightLayout = new javax.swing.GroupLayout(panelRight);
        panelRight.setLayout(panelRightLayout);
        panelRightLayout.setHorizontalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelRightLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        panelRightLayout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonRight)
                            .addComponent(labelRight)
                            .addComponent(checkBoxRight))
                    .addContainerGap(172, Short.MAX_VALUE))
        );
        panelRightLayout.setVerticalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelRightLayout.createSequentialGroup()
                    .addComponent(checkBoxRight)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(labelRight)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(buttonRight, javax.swing.GroupLayout.PREFERRED_SIZE, 15,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(178, Short.MAX_VALUE))
        );

        spRight.setRightComponent(panelRight);

        panelMain.setBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelMain.setName("panelMain"); // NOI18N

        buttonMain.setText(resourceMap.getString("buttonMain.text")); // NOI18N
        buttonMain.setToolTipText(resourceMap.getString("buttonMain.toolTipText")); // NOI18N
        buttonMain.setName("buttonMain"); // NOI18N
        buttonMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonMainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelMainLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(buttonMain)
                    .addContainerGap(260, Short.MAX_VALUE))
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelMainLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(buttonMain, javax.swing.GroupLayout.PREFERRED_SIZE, 15,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(215, Short.MAX_VALUE))
        );

        spRight.setLeftComponent(panelMain);

        spLeft.setRightComponent(spRight);

        spDown.setLeftComponent(spLeft);

        spDown2.setDividerLocation(100);
        spDown2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spDown2.setAutoscrolls(true);
        spDown2.setContinuousLayout(true);
        spDown2.setName("spDown2"); // NOI18N

        panelDown1.setName("panelDown1"); // NOI18N
        panelDown1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelDown1ComponentResized(evt);
            }
        });

        checkBoxDown.setText(resourceMap.getString("checkBoxDown.text")); // NOI18N
        checkBoxDown.setToolTipText(resourceMap.getString("checkBoxDown.toolTipText")); // NOI18N
        checkBoxDown.setName("checkBoxDown"); // NOI18N

        labelDown.setText(resourceMap.getString("labelDown.text")); // NOI18N
        labelDown.setName("labelDown"); // NOI18N

        buttonDown.setText(resourceMap.getString("buttonDown.text")); // NOI18N
        buttonDown.setToolTipText(resourceMap.getString("buttonDown.toolTipText")); // NOI18N
        buttonDown.setName("buttonDown"); // NOI18N
        buttonDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDownActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDown1Layout = new javax.swing.GroupLayout(panelDown1);
        panelDown1.setLayout(panelDown1Layout);
        panelDown1Layout.setHorizontalGroup(
            panelDown1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelDown1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(checkBoxDown)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(labelDown, javax.swing.GroupLayout.PREFERRED_SIZE, 47,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(buttonDown)
                    .addContainerGap(576, Short.MAX_VALUE))
        );
        panelDown1Layout.setVerticalGroup(
            panelDown1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelDown1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        panelDown1Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDown1Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelDown)
                                .addComponent(buttonDown, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    14,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(checkBoxDown))
                    .addContainerGap(67, Short.MAX_VALUE))
        );

        spDown2.setTopComponent(panelDown1);

        panelDown2.setName("panelDown2"); // NOI18N
        panelDown2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panelDown2ComponentResized(evt);
            }
        });

        checkBoxDown2.setText(resourceMap.getString("checkBoxDown2.text")); // NOI18N
        checkBoxDown2.setName("checkBoxDown2"); // NOI18N

        labelDown2.setText(resourceMap.getString("labelDown2.text")); // NOI18N
        labelDown2.setName("labelDown2"); // NOI18N

        buttonDown2.setText(resourceMap.getString("buttonDown2.text")); // NOI18N
        buttonDown2.setName("buttonDown2"); // NOI18N
        buttonDown2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDown2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDown2Layout = new javax.swing.GroupLayout(panelDown2);
        panelDown2.setLayout(panelDown2Layout);
        panelDown2Layout.setHorizontalGroup(
            panelDown2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelDown2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(checkBoxDown2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(labelDown2, javax.swing.GroupLayout.PREFERRED_SIZE, 52,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonDown2)
                    .addContainerGap(577, Short.MAX_VALUE))
        );
        panelDown2Layout.setVerticalGroup(
            panelDown2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelDown2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        panelDown2Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkBoxDown2)
                            .addGroup(panelDown2Layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelDown2)
                                .addComponent(buttonDown2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    14,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(54, Short.MAX_VALUE))
        );

        spDown2.setRightComponent(panelDown2);

        spDown.setRightComponent(spDown2);

        spUp.setRightComponent(spDown);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spUp, javax.swing.GroupLayout.DEFAULT_SIZE, 717, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spUp, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panelUpComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelUpComponentResized
        labelUp.setText(
            String.valueOf(Math.round(new Double(panelUp.getHeight()) / (spUp.getHeight()) * 100))
                + "%");
    }//GEN-LAST:event_panelUpComponentResized

    private void panelLeftComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelLeftComponentResized
        labelLeft.setText(
            String.valueOf(Math.round(new Double(panelLeft.getWidth()) / (spUp.getWidth()) * 100))
                + "%");
    }//GEN-LAST:event_panelLeftComponentResized

    private void panelRightComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelRightComponentResized
        labelRight.setText(
            String
                .valueOf(Math.round(new Double(panelRight.getWidth()) / (panelUp.getWidth()) * 100))
                + "%");
    }//GEN-LAST:event_panelRightComponentResized

    private void buttonMainActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonMainActionPerformed
        FParamsEditor
            .changeParams(this.parent, true, mainElement, getLocaleMessage("cfg.params.main"));
    }//GEN-LAST:event_buttonMainActionPerformed

    private void buttonRightActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRightActionPerformed
        FBoardParams
            .changeParams(this.parent, rightElement, getLocaleMessage("cfg.params.right"),
                netProperty);
    }//GEN-LAST:event_buttonRightActionPerformed

    private void buttonDownActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDownActionPerformed
        FBoardParams.changeParams(this.parent, bottomElement, getLocaleMessage("cfg.params.bottom"),
            netProperty);
    }//GEN-LAST:event_buttonDownActionPerformed

    private void buttonLeftActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLeftActionPerformed
        FBoardParams
            .changeParams(this.parent, leftElement, getLocaleMessage("cfg.params.left"),
                netProperty);
    }//GEN-LAST:event_buttonLeftActionPerformed

    private void buttonTopActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTopActionPerformed
        FBoardParams
            .changeParams(this.parent, topElement, getLocaleMessage("cfg.params.top"), netProperty);
    }//GEN-LAST:event_buttonTopActionPerformed

    private void buttonDown2ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDown2ActionPerformed
        FBoardParams
            .changeParams(this.parent, bottomElement2, getLocaleMessage("cfg.params.bottom2"),
                netProperty);
    }//GEN-LAST:event_buttonDown2ActionPerformed

    private void panelDown1ComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelDown1ComponentResized
        labelDown.setText(String.valueOf(Math.round(
            new Double(panelDown1.getHeight()) / (spUp.getHeight() + panelDown2.getHeight()) * 100))
            + "%");
    }//GEN-LAST:event_panelDown1ComponentResized

    private void panelDown2ComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_panelDown2ComponentResized
        labelDown2.setText(String.valueOf(Math.round(
            new Double(panelDown2.getHeight()) / (spUp.getHeight() + panelDown1.getHeight()) * 100))
            + "%");
    }//GEN-LAST:event_panelDown2ComponentResized
    // End of variables declaration//GEN-END:variables
}
