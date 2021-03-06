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
package ru.apertum.qsystem.client.common;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import ru.apertum.qsystem.common.QLog;

/**
 * @author Evgeniy Egorov
 */
public class BackDoor extends javax.swing.JDialog {

    private String pass = "";
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butNum;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;

    /**
     * Creates new form BackDoor
     */
    public BackDoor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        final Timer t = new Timer(5000, (ActionEvent e) -> {
            setVisible(false);
        });
        t.start();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butNum = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setName("Form"); // NOI18N
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(770, 75));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(BackDoor.class);
        butNum.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        butNum.setText(resourceMap.getString("butNum.text")); // NOI18N
        butNum.setBorder(
            new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        butNum.setName("butNum"); // NOI18N
        butNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(butNum);

        jButton2.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);

        jButton3.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);

        jButton4.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4);

        jButton5.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5);

        jButton6.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6);

        jButton7.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton7);

        jButton8.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8);

        jButton9.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton9);

        jButton10.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10
            .setBorder(
                new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNumActionPerformed(evt);
            }
        });
        getContentPane().add(jButton10);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butNumActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNumActionPerformed
        pass = pass + evt.getActionCommand();
        if ("1914".equals(pass)) {
            QLog.l().logger().warn("Came out through the backdoor.");
            System.exit(1914);
        }
    }//GEN-LAST:event_butNumActionPerformed
    // End of variables declaration//GEN-END:variables
}
