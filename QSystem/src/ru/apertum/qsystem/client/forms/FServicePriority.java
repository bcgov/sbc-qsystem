/*
 * Copyright (C) 2011 Evgeniy Egorov
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
 * FServicePriority.java
 *
 * Created on Oct 10, 2011, 10:21:13 AM
 */
package ru.apertum.qsystem.client.forms;

import java.util.HashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.model.FlexPriorityCell;
import ru.apertum.qsystem.client.model.FlexPriorityMableModel;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation.SelfService;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation.SelfSituation;
import ru.apertum.qsystem.common.model.INetProperty;

/**
 *
 * @author Evgeniy Egorov
 */
public class FServicePriority extends javax.swing.JDialog {

    private static ResourceMap localeMap = null;

    /**
     * Сделано паблик чтоб отсюда брать названия приоритетов услуг
     *
     * @param key какой требуется текст
     * @return локализованный текст
     */
    public static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FServicePriority.class);
        }
        return localeMap.getString(key);
    }
    /**
     * Используемая ссылка на диалоговое окно.
     */
    private static FServicePriority dialog;

    /**
     * Creates new form FServicePriority
     *
     * @param parent
     * @param modal
     */
    public FServicePriority(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * Показать список обрабатываемых услуг с возможностью менять им приоритеты
     *
     * @param netProperty свойства коннекта
     * @param owner для модальности
     * @param plan список обрабатываемых услуг
     * @param userId
     */
    public static void show(INetProperty netProperty, JFrame owner, SelfSituation plan, Long userId) {
        QLog.l().logger().info("Диалог управления приоритетами.");
        if (dialog == null) {
            dialog = new FServicePriority(owner, true);
        }
        dialog.fillPlan(plan);
        dialog.setLocation(Math.round(owner.getLocation().x + owner.getWidth() / 2 - dialog.getWidth() / 2),
                Math.round(owner.getLocation().y + owner.getHeight() / 2 - dialog.getHeight() / 2));

        dialog.isUndo = true;
        dialog.netProperty = netProperty;
        dialog.userId = userId;
        dialog.setVisible(true);
        if (dialog.isUndo) {
            dialog.undo();
        }
    }
    private final HashMap<Long, Integer> oldValues = new HashMap<>();
    private boolean isUndo = true;
    private INetProperty netProperty;
    private Long userId;

    private void undo() {
        // вернем старые значения
        for (SelfService service : plan.getSelfservices()) {
            if (service.isFlexy() && oldValues.get(service.getId()) != null) {
                service.setPriority(oldValues.get(service.getId()));
            }
        }
    }
    private SelfSituation plan;

    public void fillPlan(SelfSituation plan) {
        this.plan = plan;
        // запомним старые значения на случай отката
        oldValues.clear();
        for (SelfService service : plan.getSelfservices()) {
            if (service.isFlexy()) {
                oldValues.put(service.getId(), service.getPriority());
            }
        }

        tablePlan.setModel(new FlexPriorityMableModel(plan));

        JComboBox<String> cb = new JComboBox<>();
        /*
         cb.addItem(Uses.get_COEFF_WORD().get(Uses.SERVICE_REMAINS));
         cb.addItem(Uses.get_COEFF_WORD().get(Uses.SERVICE_NORMAL));
         cb.addItem(Uses.get_COEFF_WORD().get(Uses.SERVICE_VIP));
         */
        Uses.get_COEFF_WORD().values().forEach(pr -> cb.addItem(pr));
        TableCellEditor editor = new DefaultCellEditor(cb);
        TableColumnModel cm = tablePlan.getColumnModel();
        TableColumn tc = cm.getColumn(1);
        tc.setCellEditor(editor);

        tablePlan.setDefaultRenderer(SelfService.class, new FlexPriorityCell());
    }

    /**
     * команду на сервер для изменения текущих приоритетов. Изменения только текущие, ничего не сохранияется в БД
     *
     * @param netProperty
     * @param userId
     */
    public void savePlan(INetProperty netProperty, Long userId) {
        final StringBuilder sb = new StringBuilder();
        for (SelfService service : plan.getSelfservices()) {
            if (service.isFlexy()) {
                sb.append(sb.length() == 0 ? "" : "&").append(service.getId()).append("=").append(service.getPriority());
            }
        }
        NetCommander.changeFlexPriority(netProperty, userId, sb.toString());
        sb.setLength(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        buttonExit = new javax.swing.JButton();
        buttonOK = new javax.swing.JButton();
        buttonApply = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePlan = new javax.swing.JTable();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FServicePriority.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setIconImage(null);
        setName("Form"); // NOI18N

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setName("jPanel1"); // NOI18N

        buttonExit.setText(resourceMap.getString("buttonExit.text")); // NOI18N
        buttonExit.setName("buttonExit"); // NOI18N
        buttonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExitActionPerformed(evt);
            }
        });

        buttonOK.setText(resourceMap.getString("buttonOK.text")); // NOI18N
        buttonOK.setName("buttonOK"); // NOI18N
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        buttonApply.setText(resourceMap.getString("buttonApply.text")); // NOI18N
        buttonApply.setName("buttonApply"); // NOI18N
        buttonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonApplyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonApply)
                .addGap(18, 18, 18)
                .addComponent(buttonExit)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonExit)
                    .addComponent(buttonApply)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablePlan.setModel(new javax.swing.table.DefaultTableModel(
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
        tablePlan.setName("tablePlan"); // NOI18N
        tablePlan.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(tablePlan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExitActionPerformed
        isUndo = true;
        dialog.setVisible(false);
    }//GEN-LAST:event_buttonExitActionPerformed

    private void buttonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonApplyActionPerformed
        // сохраним изменения
        savePlan(netProperty, userId);
        // запомним старые значения на случай отката
        oldValues.clear();
        for (SelfService service : plan.getSelfservices()) {
            if (service.isFlexy()) {
                oldValues.put(service.getId(), service.getPriority());
            }
        }
        isUndo = true;
    }//GEN-LAST:event_buttonApplyActionPerformed

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        savePlan(netProperty, userId);
        isUndo = false;
        dialog.setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonApply;
    private javax.swing.JButton buttonExit;
    private javax.swing.JButton buttonOK;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablePlan;
    // End of variables declaration//GEN-END:variables
}
