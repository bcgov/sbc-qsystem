/*
 * Copyright © Apertum Projects
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QUser;

/**
 * @author Egorov Evgeniy
 */
public class FUserChangeDialog extends javax.swing.JDialog {

    private static final ResourceBundle RES_BDL = ResourceBundle
        .getBundle("ru/apertum/qsystem/client/forms/resources/FUserChangeDialog");

    private static ResourceMap localeMap = null;
    private static FUserChangeDialog userChangeDialod;
    private static List<QOffice> offices;
    private QUser user;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MainUserPropsPanel;
    private javax.swing.JPanel UserPermitionsPanel;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JCheckBox checkBoxAdmin;
    private javax.swing.JCheckBox checkBoxParallel;
    private javax.swing.JCheckBox checkBoxReport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelTabloText;
    private javax.swing.JLabel labelOfficeDropdown;
    private javax.swing.JPasswordField passwordFieldUser;
    private javax.swing.JSpinner spinnerUserZone;
    private javax.swing.JTextArea taExtPoint;
    private javax.swing.JTextField textFieldUserIdent;
    private javax.swing.JTextField tfTabloText;
    private javax.swing.JComboBox tfOfficeDropdown;
    private javax.swing.JTextField tfUserId;
    private javax.swing.JTextField tfUserName;

    public FUserChangeDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(FUserChangeDialog.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Основной метод редактирования услуги.
     *
     * @param parent родительская форма
     * @param modal модальность
     * @param user Пользователь для редактирования
     */
    public static void changeUser(Frame parent, boolean modal, QUser user) {
        QLog.l().logger().info("Editing of user \"" + user + "\""); //NOI18N //NOI18N
        if (userChangeDialod == null) {
            userChangeDialod = new FUserChangeDialog(parent, modal);
        }

        QLog.l().logger().info("Office: " + user.getOffice().getName());
        QOffice userOffice = user.getOffice();

        //Reload to office dropdown in case it has been updated
        offices = Spring.getInstance().getHt().findByCriteria(
            DetachedCriteria.forClass(QOffice.class)
                .add(Property.forName("deleted").isNull())
                .setFetchMode("services", FetchMode.EAGER)
                .setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))
        );

        userChangeDialod.tfOfficeDropdown.removeAllItems();

        for (QOffice office : offices) {
            userChangeDialod.tfOfficeDropdown.addItem(office);
        }

        userChangeDialod.tfUserName.setText(user.getName());
        userChangeDialod.tfUserId.setText(user.getId() == null ? "" : user.getId().toString());
        userChangeDialod.tfOfficeDropdown.setSelectedItem(user.getOffice());
        userChangeDialod.textFieldUserIdent.setText(user.getPoint());
        userChangeDialod.spinnerUserZone.setValue(user.getAdressRS());
        userChangeDialod.taExtPoint.setText(user.getPointExt());
        userChangeDialod.tfTabloText.setText(user.getTabloText());
        userChangeDialod.passwordFieldUser.setText(user.getPassword());
        userChangeDialod.checkBoxAdmin.setSelected(user.getAdminAccess());
        userChangeDialod.checkBoxReport.setSelected(user.getReportAccess());
        userChangeDialod.checkBoxParallel.setSelected(user.getParallelAccess());

        userChangeDialod.user = user;

        Uses.setLocation(userChangeDialod);
        userChangeDialod.setVisible(true);
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

    private void saveUser() {
        QOffice newOffice = (QOffice) userChangeDialod.tfOfficeDropdown.getSelectedItem();

        user.setPoint(userChangeDialod.textFieldUserIdent.getText());
        user.setAdressRS((Integer) userChangeDialod.spinnerUserZone.getValue());
        user.setPointExt(userChangeDialod.taExtPoint.getText());
        user.setTabloText(userChangeDialod.tfTabloText.getText());
        user.setPassword(new String(userChangeDialod.passwordFieldUser.getPassword()));
        user.setAdminAccess(userChangeDialod.checkBoxAdmin.isSelected());
        user.setReportAccess(userChangeDialod.checkBoxReport.isSelected());
        user.setParallelAccess(userChangeDialod.checkBoxParallel.isSelected());

        QOffice currentOffice = user.getOffice();

        if (!currentOffice.equals(newOffice)) {
            user.setOffice(newOffice);

            List<QPlanService> planServices = user.getPlanServices();

            while (planServices.size() > 0) {
                user.deletePlanService(planServices.get(0).getService().getId());
            }

            for (QService s : newOffice.getServices()) {
                //Only add child services, not the root or the category
                if (s.isLeaf()) {
                    user.addPlanService(s);
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MainUserPropsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        textFieldUserIdent = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        spinnerUserZone = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taExtPoint = new javax.swing.JTextArea();
        tfUserName = new javax.swing.JTextField();
        tfUserId = new javax.swing.JTextField();
        tfOfficeDropdown = new javax.swing.JComboBox<>();

        labelOfficeDropdown = new javax.swing.JLabel();
        tfTabloText = new javax.swing.JTextField();
        labelTabloText = new javax.swing.JLabel();
        UserPermitionsPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        passwordFieldUser = new javax.swing.JPasswordField();
        checkBoxAdmin = new javax.swing.JCheckBox();
        checkBoxReport = new javax.swing.JCheckBox();
        checkBoxParallel = new javax.swing.JCheckBox();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setTitle(RES_BDL.getString("title")); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FUserChangeDialog.class);
        MainUserPropsPanel.setBorder(javax.swing.BorderFactory
            .createTitledBorder(resourceMap.getString("main_properties"))); // NOI18N

        jLabel1.setText(RES_BDL.getString("user")); // NOI18N

        jLabel3.setText(RES_BDL.getString("id")); // NOI18N

        jLabel5.setText(RES_BDL.getString("identifier")); // NOI18N

        textFieldUserIdent.setText("null");

        jLabel6.setText(RES_BDL.getString("nzone")); // NOI18N

        spinnerUserZone.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99999, 1));

        jLabel7.setText(RES_BDL.getString("additional_column")); // NOI18N

        taExtPoint.setColumns(20);
        taExtPoint.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        taExtPoint.setRows(5);
        jScrollPane1.setViewportView(taExtPoint);

        tfUserName.setEditable(false);
        tfUserName.setText("null");

        tfUserId.setEditable(false);
        tfUserId.setText("null");

        labelTabloText.setText(resourceMap.getString("tablo_text")); // NOI18N
        labelOfficeDropdown.setText("Office");

        javax.swing.GroupLayout MainUserPropsPanelLayout = new javax.swing.GroupLayout(
            MainUserPropsPanel);
        MainUserPropsPanel.setLayout(MainUserPropsPanelLayout);
        MainUserPropsPanelLayout.setHorizontalGroup(
            MainUserPropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MainUserPropsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(MainUserPropsPanelLayout.createSequentialGroup()
                            .addGroup(MainUserPropsPanelLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel3)
                                .addComponent(labelOfficeDropdown))
                            .addGap(18, 18, 18)
                            .addGroup(MainUserPropsPanelLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tfUserName)
                                .addComponent(tfUserId)
                                .addComponent(tfOfficeDropdown)))
                        .addGroup(MainUserPropsPanelLayout.createSequentialGroup()
                            .addComponent(labelTabloText)
                            .addGap(18, 18, 18)
                            .addComponent(tfTabloText))
                        .addGroup(MainUserPropsPanelLayout.createSequentialGroup()
                            .addGroup(MainUserPropsPanelLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(MainUserPropsPanelLayout.createSequentialGroup()
                                    .addGroup(MainUserPropsPanelLayout
                                        .createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel5))
                                    .addGap(25, 25, 25)
                                    .addGroup(MainUserPropsPanelLayout
                                        .createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spinnerUserZone,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 80,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textFieldUserIdent,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 135,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jLabel7))
                            .addGap(0, 171, Short.MAX_VALUE))
                        .addComponent(jScrollPane1))
                    .addContainerGap())
        );
        MainUserPropsPanelLayout.setVerticalGroup(
            MainUserPropsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MainUserPropsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(tfUserName, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tfUserId, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelOfficeDropdown)
                        .addComponent(tfOfficeDropdown, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18)
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(textFieldUserIdent, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(22, 22, 22)
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(spinnerUserZone, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(MainUserPropsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfTabloText, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelTabloText))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel7)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 103,
                        Short.MAX_VALUE))
        );

        UserPermitionsPanel.setBorder(javax.swing.BorderFactory
            .createTitledBorder(resourceMap.getString("permissions"))); // NOI18N

        jLabel8.setText(RES_BDL.getString("PASSWORD")); // NOI18N

        passwordFieldUser.setText("null");

        checkBoxAdmin.setText(RES_BDL.getString("ADMINISTRATION_RIGHT")); // NOI18N

        checkBoxReport.setText(RES_BDL.getString("PERMISSION_FOR_GETTING_REPORTS")); // NOI18N

        checkBoxParallel
            .setText(RES_BDL.getString("PERFORMING_PARALLEL_OPERATION_WITH_CUSTOMERS")); // NOI18N

        javax.swing.GroupLayout UserPermitionsPanelLayout = new javax.swing.GroupLayout(
            UserPermitionsPanel);
        UserPermitionsPanel.setLayout(UserPermitionsPanelLayout);
        UserPermitionsPanelLayout.setHorizontalGroup(
            UserPermitionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(UserPermitionsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(UserPermitionsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(UserPermitionsPanelLayout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(passwordFieldUser))
                        .addGroup(UserPermitionsPanelLayout.createSequentialGroup()
                            .addGroup(UserPermitionsPanelLayout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(checkBoxParallel)
                                .addComponent(checkBoxReport)
                                .addComponent(checkBoxAdmin))
                            .addGap(0, 34, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        UserPermitionsPanelLayout.setVerticalGroup(
            UserPermitionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(UserPermitionsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(UserPermitionsPanelLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(passwordFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(checkBoxAdmin)
                    .addGap(18, 18, 18)
                    .addComponent(checkBoxReport)
                    .addGap(18, 18, 18)
                    .addComponent(checkBoxParallel)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCancel.setText(RES_BDL.getString("CANCEL")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setText(RES_BDL.getString("OK")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(MainUserPropsPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(UserPermitionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup()
                                .addComponent(btnOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(MainUserPropsPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(UserPermitionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnOk))
                    .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOkActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        if (user != null) {
            saveUser();
        }
        setVisible(false);
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed
    // End of variables declaration//GEN-END:variables
}
