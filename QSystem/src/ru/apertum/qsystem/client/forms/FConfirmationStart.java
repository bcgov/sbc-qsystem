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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.model.ATalkingClock;

/**
 * Окно вывода предварительной статистики перед постановкой в очередь. Created on 29 Сентябрь 2008
 * г., 18:11 Клас сокна вывода статистики перед постановкой в очередь. Это дает возможность оценить
 * обстановку и принять решение стоять или нет.
 *
 * @author Evgeniy Egorov
 */
public class FConfirmationStart extends JDialog {

    /**
     * Результат
     */
    private static boolean ok = false;
    /**
     * Используемая ссылка на диалоговое окно.
     */
    private static FConfirmationStart confirmationForm;
    /**
     * Таймер, по которому будем убирать окно со статистикой.
     */
    private static ATalkingClock clock = new ATalkingClock(Uses.DELAY_BACK_TO_ROOT, 1) {

        @Override
        public void run() {
            if (confirmationForm == null || confirmationForm.isVisible()) {
                ok = false;
                confirmationForm.setVisible(false);
            }
        }
    };
    private static ResourceMap localeMap = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelInfo;

    /**
     * Creates new form FConfirmationStart
     */
    public FConfirmationStart(JFrame owner, int count) {
        super(owner, "Статистика", true);
        initComponents();

        buttonOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clock.stop();
                ok = true;
                setVisible(false);
            }
        });
        buttonCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clock.stop();
                ok = false;
                setVisible(false);
            }
        });
    }

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(FConfirmationStart.class);
        }
        return localeMap.getString(key);
    }

    public static boolean getMayContinue(JFrame owner, int count) {
        QLog.l().logger()
            .info("Просмотр состояния очереди и принятие решения о постановки себя в очередь.");
        ok = false;
        if (confirmationForm == null) {
            confirmationForm = new FConfirmationStart(owner, count);
        }
        confirmationForm.changeTextToLocale();
        confirmationForm.labelInfo.setText(
            "<HTML><b><p align=center><span style='font-size:60.0pt;color:green'>"
                + getLocaleMessage(
                "dialod.text_before") + "</span><br>"
                + "<span style='font-size:100.0pt;color:red'>" + count
                + "</span><br><span style='font-size:60.0pt;color:red'>" + getLocaleMessage(
                "dialod.text_before_people")
                + (
                ((!Locale.getDefault().getLanguage().startsWith("en")) && ((count % 10) >= 2) && (
                    (count % 10) <= 4)) ? "a" : "") + "</span></p></b>");
        confirmationForm.setBounds(owner.getLocation().x, owner.getLocation().y, owner.getWidth(),
            owner.getHeight());
        // если кастомер провафлил и ушел, то надо закрыть диалог
        clock.start();
        confirmationForm.setVisible(true);
        return ok;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new QPanel(WelcomeParams.getInstance().backgroundImg);
        buttonOk = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonCancel = new QButton(WelcomeParams.getInstance().servButtonType);
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        labelInfo = new javax.swing.JLabel();

        setResizable(false);
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FConfirmationStart.class);
        jPanel1.setBorder(javax.swing.BorderFactory
            .createLineBorder(resourceMap.getColor("jPanel1.border.lineColor"), 5)); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        buttonOk.setFont(resourceMap.getFont("buttonOk.font")); // NOI18N
        buttonOk.setIcon(resourceMap.getIcon("buttonOk.icon")); // NOI18N
        buttonOk.setText(resourceMap.getString("buttonOk.text")); // NOI18N
        buttonOk.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonOk.setFocusPainted(false);
        buttonOk.setName("buttonOk"); // NOI18N

        buttonCancel.setFont(resourceMap.getFont("buttonCancel.font")); // NOI18N
        buttonCancel.setIcon(resourceMap.getIcon("buttonCancel.icon")); // NOI18N
        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        buttonCancel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
            javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonCancel.setFocusPainted(false);
        buttonCancel.setName("buttonCancel"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(resourceMap.getIcon("jLabel1.icon")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setName("jLabel2"); // NOI18N

        labelInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelInfo.setText(resourceMap.getString("labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE, 801, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 801,
                                Short.MAX_VALUE)
                            .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 801,
                                Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                jPanel1Layout.createSequentialGroup()
                                    .addComponent(buttonCancel,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        361, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                        73, Short.MAX_VALUE)
                                    .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        367,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 242,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 66,
                        Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 182,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(
                        jPanel1Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 86,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 91,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeTextToLocale() {
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FConfirmationStart.class);
        buttonOk.setText(resourceMap.getString("buttonOk.text")); // NOI18N
        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        labelInfo.setText(resourceMap.getString("labelInfo.text")); // NOI18N
    }
    // End of variables declaration//GEN-END:variables
}
