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
 * FMoveToPostponed.java
 *
 * Created on Mar 25, 2013, 4:44:32 PM
 */
package ru.apertum.qsystem.client.forms;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;

/**
 *
 * @author Evgeniy Egorov
 */
public class FMoveToPostponed extends javax.swing.JDialog {

    /**
     * Результат
     */
    private static boolean ok;
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FMoveToPostponed.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Creates new form FMoveToPostponed
     *
     * @param parent
     * @param modal
     * @param results
     */
    public FMoveToPostponed(java.awt.Frame parent, boolean modal, Object[] results) {
        super(parent, modal);
        initComponents();
        comboBoxPeriod.setModel(new javax.swing.DefaultComboBoxModel(getLocaleMessage("conboBox.periods").split(",")));
        comboBoxResults.setModel(new javax.swing.DefaultComboBoxModel(results));
        cbOnlyMine.setSelected(false);
    }

    public int getPeriod(){
        return comboBoxPeriod.getSelectedIndex() * 5;
    }

    public String getResult() {
        return (String) comboBoxResults.getModel().getSelectedItem();
    }

    public boolean isMine() {
        return cbOnlyMine.isSelected() && comboBoxPeriod.getSelectedIndex() == 0;
    }

    public boolean isOK() {
        return ok;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelResult = new javax.swing.JLabel();
        comboBoxResults = new javax.swing.JComboBox();
        labelPeriod = new javax.swing.JLabel();
        comboBoxPeriod = new javax.swing.JComboBox();
        cbOnlyMine = new javax.swing.JCheckBox();
        buttonOK = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FMoveToPostponed.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setModal(true);
        setName("Form"); // NOI18N

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setName("jPanel1"); // NOI18N

        labelResult.setText(resourceMap.getString("labelResult.text")); // NOI18N
        labelResult.setName("labelResult"); // NOI18N

        comboBoxResults.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboBoxResults.setName("comboBoxResults"); // NOI18N

        labelPeriod.setText(resourceMap.getString("labelPeriod.text")); // NOI18N
        labelPeriod.setName("labelPeriod"); // NOI18N

        comboBoxPeriod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Бессрочно", "5 минут", "10 минут", "15 минут", "20 минут", "25 минут", "30 минут" }));
        comboBoxPeriod.setName("comboBoxPeriod"); // NOI18N
        comboBoxPeriod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxPeriodActionPerformed(evt);
            }
        });

        cbOnlyMine.setText(resourceMap.getString("cbOnlyMine.text")); // NOI18N
        cbOnlyMine.setName("cbOnlyMine"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cbOnlyMine))
                    .addComponent(labelResult, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(labelPeriod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxPeriod, 0, 207, Short.MAX_VALUE))
                    .addComponent(comboBoxResults, 0, 414, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelResult)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxResults, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPeriod)
                    .addComponent(comboBoxPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cbOnlyMine)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .addComponent(buttonOK)
                .addGap(18, 18, 18)
                .addComponent(buttonCancel)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonOK))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
        ok = true;
        setVisible(false);
    }//GEN-LAST:event_buttonOKActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        ok = false;
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void comboBoxPeriodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxPeriodActionPerformed
        cbOnlyMine.setVisible(comboBoxPeriod.getSelectedIndex() == 0);
    }//GEN-LAST:event_comboBoxPeriodActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOK;
    private javax.swing.JCheckBox cbOnlyMine;
    private javax.swing.JComboBox comboBoxPeriod;
    private javax.swing.JComboBox comboBoxResults;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelPeriod;
    private javax.swing.JLabel labelResult;
    // End of variables declaration//GEN-END:variables
}
