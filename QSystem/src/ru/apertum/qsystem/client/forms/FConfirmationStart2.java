/*
 * Copyright (C) 2014 Evgeniy Egorov
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
package ru.apertum.qsystem.client.forms;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.model.ATalkingClock;

/**
 *
 * @author Evgeniy Egorov
 */
public class FConfirmationStart2 extends javax.swing.JDialog {

    /**
     * Результат
     */
    private static boolean ok = false;
    /**
     * Используемая ссылка на диалоговое окно.
     */
    private static FConfirmationStart2 confirmationForm;
    /**
     * Таймер, по которому будем убирать окно со статистикой.
     */
    private static final ATalkingClock CLOCK = new ATalkingClock(Uses.DELAY_BACK_TO_ROOT, 1) {

        @Override
        public void run() {
            if (confirmationForm == null || confirmationForm.isVisible()) {
                ok = false;
                confirmationForm.setVisible(false);
            }
        }
    };

    /**
     * Creates new form FConfirmationStart
     *
     * @param owner
     * @param count
     */
    public FConfirmationStart2(JFrame owner, int count) {
        super(owner, "Статистика", true);
        initComponents();

        buttonOk.addActionListener((ActionEvent e) -> {
            CLOCK.stop();
            ok = true;
            setVisible(false);
        });
        buttonCancel.addActionListener((ActionEvent e) -> {
            CLOCK.stop();
            ok = false;
            setVisible(false);
        });
        if (WelcomeParams.getInstance().btnFont != null) {
            buttonOk.setFont(WelcomeParams.getInstance().btnFont);
            buttonCancel.setFont(WelcomeParams.getInstance().btnFont);
        }
    }
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FConfirmationStart.class);
        }
        return localeMap.getString(key);
    }

    public static boolean getMayContinue(JFrame owner, int count) {
        QLog.l().logger().info("Просмотр состояния очереди и принятие решения о постановки себя в очередь.");
        ok = false;
        if (confirmationForm == null) {
            confirmationForm = new FConfirmationStart2(owner, count);
        }
        confirmationForm.changeTextToLocale(count);
        //confirmationForm.labelInfo.setText("<HTML><b><p align=center><span style='font-size:60.0pt;color:green'>" + getLocaleMessage("dialod.text_before") + "</span><br>"
        //        + "<span style='font-size:100.0pt;color:red'>" + count + "</span><br><span style='font-size:60.0pt;color:red'>" + getLocaleMessage("dialod.text_before_people")
        //        + (((!Locale.getDefault().getLanguage().startsWith("en")) && ((count % 10) >= 2) && ((count % 10) <= 4)) ? "a" : "") + "</span></p></b>");
        //confirmationForm.setBounds(owner.getLocation().x, owner.getLocation().y, owner.getWidth(), owner.getHeight());
        Uses.setLocation(confirmationForm);
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo())) {
            Uses.setFullSize(confirmationForm);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                confirmationForm.setCursor(transparentCursor);
            }

        } else {
            confirmationForm.setSize(1280, 768);
            Uses.setLocation(confirmationForm);
        }
        // если кастомер провафлил и ушел, то надо закрыть диалог
        CLOCK.start();
        confirmationForm.setVisible(true);
        return ok;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new QPanel(WelcomeParams.getInstance().backgroundImg);
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();
        labelInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        buttonCancel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/stop.png"))); // NOI18N
        buttonCancel.setText("Отказаться");
        buttonCancel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));

        buttonOk.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        buttonOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/checkmark.png"))); // NOI18N
        buttonOk.setText("Встать в очередь");
        buttonOk.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));

        labelInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/vopros.png"))); // NOI18N
        labelInfo.setText("<html><br/><p align=''>Текст</p><p align='left'>Текст</p>");
        labelInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelInfo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfo)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                        .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeTextToLocale(int count) {
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FConfirmationStart.class);
        buttonOk.setText(resourceMap.getString("buttonOk.text")); // NOI18N
        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        labelInfo.setIcon(new File(WelcomeParams.getInstance().confirmationStartImg).exists() ? new ImageIcon(WelcomeParams.getInstance().confirmationStartImg) : new javax.swing.ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/vopros.png")));
        labelInfo.setText(WelcomeParams.getInstance().patternConfirmationStart.replace("dialog.text_before", getLocaleMessage("dialod.text_before")).
                replace("dialog.count", "" + count).
                replace("dialog.text_people", getLocaleMessage("dialod.text_before_people")).
                replace("[[endRus]]", (((!Locale.getDefault().getLanguage().startsWith("en")) && ((count % 10) >= 2) && ((count % 10) <= 4)) ? "a" : ""))); // NOI18N
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelInfo;
    // End of variables declaration//GEN-END:variables
}
