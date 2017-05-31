/*
 * Copyright (C) 2013 Evgeniy Egorov
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
 * FBreaksChangeDialog.java
 *
 * Created on Apr 5, 2013, 9:13:13 AM
 */
package ru.apertum.qsystem.client.forms;

import java.awt.Frame;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.server.model.schedule.QBreak;
import ru.apertum.qsystem.server.model.schedule.QBreaks;
import ru.apertum.qsystem.server.model.schedule.QBreaksList;

/**
 *
 * @author Evgeniy Egorov
 */
public class FBreaksChangeDialog extends javax.swing.JDialog {

    /** Creates new form FBreaksChangeDialog
     * @param parent
     * @param modal */
    public FBreaksChangeDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    private static FBreaksChangeDialog breaksChangeDialod;
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FBreaksChangeDialog.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Основной метод редактирования перерыва.
     * @param parent
     * @param modal
     * @param breaks
     */
    public static void changeSchedule(Frame parent, boolean modal, QBreaks breaks) {
        QLog.l().logger().info("Редактирование перерывов \"" + breaks.getName() + "\"");
        if (breaksChangeDialod == null) {
            breaksChangeDialod = new FBreaksChangeDialog(parent, modal);
            breaksChangeDialod.setTitle(getLocaleMessage("Form.title"));
        }
        breaksChangeDialod.breaks = breaks.getBreaks().toArray(new QBreak[0]);
        breaksChangeDialod.breaksParent = breaks;
        breaksChangeDialod.loadBreaks(breaksChangeDialod.breaks);
        Uses.setLocation(breaksChangeDialod);
        breaksChangeDialod.setVisible(true);
    }

    private void loadBreaks(QBreak[] breaks) {
        setTitle(breaksParent.getName());
        list.setModel(new DefaultComboBoxModel(breaks));
    }
    private QBreak[] breaks;
    private QBreaks breaksParent;

    private void saveBreaks() {
        breaksParent.getBreaks().clear();
        for (QBreak qBreak : breaks) {
            breaksParent.getBreaks().add(qBreak);
        }    
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        buttonOK = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        buttonRemove = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        buttonAdd = new javax.swing.JButton();
        cbSH = new javax.swing.JComboBox();
        cbSM = new javax.swing.JComboBox();
        cbFH = new javax.swing.JComboBox();
        cbFM = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FBreaksChangeDialog.class, this);
        jMenuItem1.setAction(actionMap.get("removeButton")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jPopupMenu1.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FBreaksChangeDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        buttonOK.setText(resourceMap.getString("buttonOK.text")); // NOI18N
        buttonOK.setName("buttonOK"); // NOI18N
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setComponentPopupMenu(jPopupMenu1);
        list.setName("list"); // NOI18N
        jScrollPane1.setViewportView(list);

        buttonRemove.setAction(actionMap.get("removeButton")); // NOI18N
        buttonRemove.setText(resourceMap.getString("buttonRemove.text")); // NOI18N
        buttonRemove.setName("buttonRemove"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Новый"));
        jPanel1.setName("jPanel1"); // NOI18N

        buttonAdd.setAction(actionMap.get("addBreak")); // NOI18N
        buttonAdd.setText(resourceMap.getString("buttonAdd.text")); // NOI18N
        buttonAdd.setName("buttonAdd"); // NOI18N

        cbSH.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", " " }));
        cbSH.setSelectedIndex(11);
        cbSH.setName("cbSH"); // NOI18N

        cbSM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" }));
        cbSM.setName("cbSM"); // NOI18N

        cbFH.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", " " }));
        cbFH.setSelectedIndex(12);
        cbFH.setName("cbFH"); // NOI18N

        cbFM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55" }));
        cbFM.setName("cbFM"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(cbSH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                        .addComponent(cbFH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbFM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbSM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(cbSH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cbFM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cbFH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonAdd))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonOK, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(buttonRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(buttonRemove))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        saveBreaks();
        setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }

    @Action
    public void addBreak() {
        final GregorianCalendar gc = new GregorianCalendar(2015, 1, 1, Integer.parseInt(cbSH.getSelectedItem().toString()), Integer.parseInt(cbSM.getSelectedItem().toString()));
        final Date d1 = gc.getTime();
        gc.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(cbFH.getSelectedItem().toString()));
        gc.set(GregorianCalendar.MINUTE, Integer.parseInt(cbFM.getSelectedItem().toString()));
        if (d1.before(gc.getTime())) {
            breaks = (QBreak[]) ArrayUtils.add(breaks, new QBreak(d1, gc.getTime(), breaksParent));
            loadBreaks(breaks);
        } else {
            JOptionPane.showConfirmDialog(this, getLocaleMessage("add_break_dialog.err1.message"), getLocaleMessage("add_break_dialog.err1.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonCancelActionPerformed

    @Action
    public void removeButton() {
        if (list.getSelectedIndex() != -1) {
            breaks = (QBreak[]) ArrayUtils.removeElement(breaks, list.getSelectedValue());
            QBreaksList.getInstance().addBreakForDelete((QBreak) list.getSelectedValue());
            loadBreaks(breaks);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOK;
    private javax.swing.JButton buttonRemove;
    private javax.swing.JComboBox cbFH;
    private javax.swing.JComboBox cbFM;
    private javax.swing.JComboBox cbSH;
    private javax.swing.JComboBox cbSM;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList list;
    // End of variables declaration//GEN-END:variables
}
