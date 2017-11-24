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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import ru.apertum.qsystem.common.AUDPServer;
import ru.apertum.qsystem.common.SoundPlayer;
import ru.apertum.qsystem.common.model.ATalkingClock;

/**
 * Created on 26 Январь 2009 г., 17:41
 *
 * @author Evgeniy Egorov
 */
public class FTest extends javax.swing.JFrame {

    final static String tt = "This is a text sample В 1962 году американцы запустили первый космический аппарат для изучения Венеры Маринер-1, потерпевший аварию через несколько минут после старта. Сначала на аппарате отказала антенна, которая получала сигнал от наводящей системы с Земли, после чего управление взял на себя бортовой компьютер. Он тоже не смог исправить отклонение от курса, так как загруженная в него программа содержала единственную ошибку — при переносе инструкций в код для перфокарт в одном из уравнений была пропущена чёрточка над буквой, отсутствие которой коренным образом поменяло математический смысл уравнения. Журналисты вскоре окрестили эту чёрточку «самым дорогим дефисом в истории» (в пересчёте на сегодняшний день стоимость утерянного аппарата составляет 135 000 000 $).";
    int i = 1;

    ;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton buttonTimer;
    private ATalkingClock timer = new ATalkingClock(4000, 1) {

        @Override
        public void run() {
            buttonTimer.setText(String.valueOf(i++));
        }
    };
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JToggleButton jToggleButton1;
    private ru.apertum.qsystem.client.model.QPanel qPanel1;
    private ru.apertum.qsystem.common.RunningLabel rl;
    private javax.swing.JTextField tfInterval;

    /**
     * Creates new form FTest
     */
    public FTest() {
        initComponents();
        /*
         l = new RunningLabel();
         this.add(l);
         l.setLocation(0, 0);
         l.setSize(1000, 400);
         l.setText("sgdagbfzbf");
         l.setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/Check.png")));
         rl.setBackgroundImg("/ru/apertum/qsystem/client/forms/resources/_welcome.jpg");
         l.setBackground(new Color(250, 200, 200));


         // CENTER  = 0;
         // TOP     = 1;
         // LEFT    = 2;
         // BOTTOM  = 3;
         // RIGHT   = 4;


         l.setFont(new Font("Helvetica", Font.BOLD, 50));
         l.setVerticalAlignment(0);
         l.setHorizontalAlignment(4);
         l.setRunningText("ПрЮвет Волку!!!");


         l.setBlinkCount(0);
         l.setSpeedBlink(150);

         */

        UDPServer us = new UDPServer(30000);
        // qPanel1.get

    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        list.stream().forEach((elem) -> {
            result.add(f.apply(elem));
        });
        list.stream().collect(null);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        String s = "w1,d2; g  3     f  ;4r e5r  ,  s   r6";
        System.out.println(s);
        //s="fsdjhdghy1zdfhsdgh";
        String ss[] = s.split("(\\s*,\\s*|\\s*;\\s*|\\s+)");
        System.out.println(ss.length + ": " + Arrays.toString(ss));

        String str = "centerO дореволюционной России.<br>Чтобы устроиться в «Экспедицию заготовления государственных бумаг» — аналог сегодняшнего Гознака — требовалось поручительство двух работников, знающих новичка лично. Если новичок не оправдывал доверия...<br>[right]увольнялись все трое.";
        str = str.trim().replaceFirst("^\\[.+?\\]", "");
        System.out.println("--" + str);
        if (1 == 1) {
            //     return ;
        }

        String text = "Привет волк!";
        Font ff = new Font("Arial", 0, 150);
        int len = 0;

        System.out.println("total=" + new JLabel().getFontMetrics(ff).stringWidth(text));
        for (int i = 0; i < text.length(); i++) {
            int l = new JLabel().getFontMetrics(ff).stringWidth(text.substring(i, i + 1));
            len = len + l;
            System.out.println(text.substring(i, i + 1) + "=" + l);
        }
        System.out.println("Сумма=" + len);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FTest().setVisible(true);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        qPanel1 = new ru.apertum.qsystem.client.model.QPanel();
        jButton5 = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton6 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        buttonTimer = new javax.swing.JButton();
        tfInterval = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        rl = new ru.apertum.qsystem.common.RunningLabel();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton14 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jButton17 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FTest.class);
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        qPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        qPanel1.setAutoscrolls(true);
        qPanel1.setEndPoint(new java.awt.Point(0, 200));
        qPanel1.setGradient(java.lang.Boolean.TRUE);
        qPanel1.setName("qPanel1"); // NOI18N
        qPanel1.setNativePosition(java.lang.Boolean.FALSE);
        qPanel1.setStartPoint(new java.awt.Point(0, 40));
        qPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                qPanel1ComponentResized(evt);
            }
        });

        javax.swing.GroupLayout qPanel1Layout = new javax.swing.GroupLayout(qPanel1);
        qPanel1.setLayout(qPanel1Layout);
        qPanel1Layout.setHorizontalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 1066, Short.MAX_VALUE)
        );
        qPanel1Layout.setVerticalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 229, Short.MAX_VALUE)
        );

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jToggleButton1.setText(resourceMap.getString("jToggleButton1.text")); // NOI18N
        jToggleButton1.setName("jToggleButton1"); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setName("jTextField2"); // NOI18N

        buttonTimer.setText(resourceMap.getString("buttonTimer.text")); // NOI18N
        buttonTimer.setName("buttonTimer"); // NOI18N
        buttonTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTimerActionPerformed(evt);
            }
        });

        tfInterval.setText(resourceMap.getString("tfInterval.text")); // NOI18N
        tfInterval.setName("tfInterval"); // NOI18N

        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        rl.setBorder(new javax.swing.border.MatteBorder(null));
        rl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rl.setText(resourceMap.getString("rl.text")); // NOI18N
        rl.setFont(resourceMap.getFont("rl.font")); // NOI18N
        rl.setName("rl"); // NOI18N
        rl.setRun(java.lang.Boolean.FALSE);
        rl.setRunningText(resourceMap.getString("rl.runningText")); // NOI18N
        rl.setSpeedRunningText(5);

        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setName("jButton13"); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jList1.setName("jList1"); // NOI18N
        jScrollPane1.setViewportView(jList1);

        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton14.setName("jButton14"); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton15.setIcon(
            new javax.swing.ImageIcon("E:\\WORK\\apertum-qsystem\\logos\\logo32.png")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton15.setIconTextGap(40);
        jButton15.setInheritsPopupMenu(true);
        jButton15.setName("jButton15"); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setName("jButton16"); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new ImageIcon(
            getClass().getResource("/ru/apertum/qsystem/client/forms/resources/client.png")));
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setIconTextGap(20);
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jPanel1.setBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 10, 0, new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 90, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 52, Short.MAX_VALUE)
        );

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText(resourceMap.getString("jRadioButton1.text")); // NOI18N
        jRadioButton1.setName("jRadioButton1"); // NOI18N

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText(resourceMap.getString("jRadioButton2.text")); // NOI18N
        jRadioButton2.setName("jRadioButton2"); // NOI18N

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText(resourceMap.getString("jRadioButton3.text")); // NOI18N
        jRadioButton3.setName("jRadioButton3"); // NOI18N
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setName("jButton17"); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rl, javax.swing.GroupLayout.Alignment.TRAILING,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            Short.MAX_VALUE)
                        .addComponent(qPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                    false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                    layout.createSequentialGroup()
                                        .addComponent(jScrollPane1,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 126,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton14,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2))
                                .addComponent(jTextField2,
                                    javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField1,
                                    javax.swing.GroupLayout.Alignment.LEADING,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                    layout.createSequentialGroup()
                                        .addComponent(jButton5)
                                        .addGap(14, 14, 14)
                                        .addComponent(jToggleButton1)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton6)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jCheckBox1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                49,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jLabel1))
                                                        .addComponent(jButton12,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                            77,
                                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(
                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                    .addComponent(jPanel1,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(280, 280, 280))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton7)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton8)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton9)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE)))
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton13,
                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                layout
                                                    .createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                        false)
                                                    .addGroup(
                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                        layout.createSequentialGroup()
                                                            .addComponent(jButton10)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(jButton11)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE)
                                                            .addComponent(tfInterval,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGap(18, 18, 18)
                                                            .addComponent(buttonTimer))
                                                    .addGroup(
                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                        layout.createSequentialGroup()
                                                            .addComponent(jButton4)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addComponent(jButton3)
                                                            .addGap(18, 18, 18)
                                                            .addComponent(jButton1)
                                                            .addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(jButton2)))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jRadioButton2)
                                            .addComponent(jRadioButton3)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton17)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jRadioButton1)))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton16)
                                                .addGap(142, 142, 142))
                                            .addComponent(jButton15,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE))))))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(rl, javax.swing.GroupLayout.PREFERRED_SIZE, 74,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(qPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout
                                        .createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton15,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton14,
                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                            51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton16)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jButton17))
                            .addGap(4, 4, 4)
                            .addComponent(jRadioButton2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton3)
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField1,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton7)
                                    .addComponent(jButton8)
                                    .addComponent(jButton9)
                                    .addComponent(jButton13))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextField2,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton12))
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jButton10)
                                            .addComponent(jButton11)))
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(6, 6, 6))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(buttonTimer)
                                    .addComponent(tfInterval,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup()
                                .addGroup(layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1)
                                    .addComponent(jButton2)
                                    .addComponent(jButton5)
                                    .addComponent(jButton3)
                                    .addComponent(jButton4)
                                    .addComponent(jToggleButton1)
                                    .addComponent(jButton6)
                                    .addComponent(jCheckBox1))
                                .addContainerGap())
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

// start
        rl.start();


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        //stop
        rl.stop();


    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        rl.startBlink();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        rl.stopBlink();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        qPanel1.startVideo();

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jToggleButton1ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed

        qPanel1.pouseVideo();

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton6ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed

        qPanel1.closeVideo();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed

        if (1 == 1) {

            String regexp = "/^(?:([a-z]+):(?:([a-z]*):)?\\/\\/)?(?:([^:@]*)(?::([^:@]*))?@)?((?:[a-z0-9_-]+\\.)+[a-z]{2,}|localhost|(?:(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}(?:(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])))(?::(\\d+))?(?:([^:\\?\\#]+))?(?:\\?([^\\#]+))?(?:\\#([^\\s]+))?$/i";
            regexp = "\\b(https?|ftp|file|mailto):(//|[-a-zA-Z0-9_\\.]+@)+[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            //regexp = "[<>a-z:]*";
            Pattern p = Pattern.compile(regexp);
            String st = "your url http://asd.saedrhg.sdh.sdh.ru here";
            //st = "your url http://www.asd.com here";
            //st = "your url https://www.asd.com here";
            //st = "your url mailto:asd@asd.com here";
            //st = "your url .<br> <a  href=\"mailto:evgeniy.egorov@gmail.com>email: e|vgeniy.egorov@gmail.com</a> here";
            st = "your url .<br> <a  href=\"https://code.google.com:8080/p/apertum-qsystem/issues/list.php?asd=13&qwe=re2\">email: e|vgeniy.egorov@gmail.com</a> here";
            Matcher m = p.matcher(st);

            while (m.find()) {
                System.out.println(st.substring(m.start(0), m.end(0)));
            }

            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }

        final LinkedList<String> res = new LinkedList<String>();
        // путь к звуковым файлам
        final String path = "/ru/apertum/qsystem/server/sound/";
        res.add(path + "ding.wav");
        res.add(path + "client.wav");

        res.addAll(SoundPlayer.toSoundSimple(path, jTextField1.getText()));

        switch (/*Uses.getNumeration().getPoint()*/0) {
            case 0:
                res.add(path + "tocabinet.wav");
                break;
            case 1:
                res.add(path + "towindow.wav");
                break;
            case 2:
                res.add(path + "tostoika.wav");
                break;
            default:
                res.add(path + "towindow.wav");
        }

        res.addAll(SoundPlayer.toSoundSimple(path, jTextField2.getText()));

        SoundPlayer.play(res);

    }//GEN-LAST:event_jButton7ActionPerformed

    private LinkedList<String> toSound(String path, String nom) {
        final LinkedList<String> res = new LinkedList<String>();
        for (int ii = 0; ii < nom.length(); ii++) {

            String elem = nom.substring(ii, ii + 1);
            if (isNum(nom.charAt(ii))) {
                String ss = elem;
                if (ii != 0 && isNum(nom.charAt(ii - 1))) {
                    ss = "_" + ss;
                }
                int n = ii + 1;
                boolean suff = false;
                while (n < nom.length() && isNum(nom.charAt(n))) {
                    ss = ss + "0";
                    if ('0' != nom.charAt(n)) {
                        suff = true;
                    }
                    n++;
                }
                if (suff) {
                    ss = ss + "_";
                } else {
                    ii = n - 1;
                }
                elem = ss;
                if (elem.indexOf("_0") != -1 && elem.indexOf("0_") != -1) {
                    continue;
                }
                if (isZero(elem)) {
                    elem = "0";
                }
                if (elem.endsWith("10_")) {
                    char[] ch = new char[1];
                    ch[0] = nom.charAt(ii + 1);
                    elem = elem.replaceFirst("10_", "1" + new String(ch));
                    ii++;
                }
            }

            final String file = path + elem + ".wav";
            //System.out.println(nom.substring(i, i + 1) + " - " + file);
            res.add(file);

        }
        return res;
    }

    private void jButton8ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

        SoundPlayer.inviteClient(null, "г1000", "103", true);


    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        SoundPlayer.play("D:/snd/_20_.wav");

    }//GEN-LAST:event_jButton9ActionPerformed

    private void buttonTimerActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTimerActionPerformed

        timer.setInterval(Integer.parseInt(tfInterval.getText()));
        timer.start();

    }//GEN-LAST:event_buttonTimerActionPerformed

    private void jButton10ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        rl.setVerticalAlignment(1);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        rl.setVerticalAlignment(3);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void qPanel1ComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_qPanel1ComponentResized

        qPanel1.refreshVideoSize();
    }//GEN-LAST:event_qPanel1ComponentResized

    private void jButton12ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        System.out.println((float) 3 / 2f);
        System.out.println((float) 3 / 2);
        System.out.println(3 / 2);

        map(Arrays.asList("10", "20", "30"), Integer::valueOf);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        Printable canvas = new Printable() {

            Graphics2D g2;

            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
                if (pageIndex >= 1) {
                    return Printable.NO_SUCH_PAGE;
                }
                g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2.drawString("texttexttexttexttexttexttexttext", 25, 25);
                g2.drawString("texttexttexttexttexttexttexttext", 25, 55);
                g2.drawString("texttexttexttexttexttexttexttext", 25, 75);
                g2.drawString("texttexttexttexttexttexttexttext", 25, 115);
                g2.drawString("texttexttexttexttexttexttexttext", 25, 125);
                return Printable.PAGE_EXISTS;
                //return  Pagable.UNKNOWN_NUMBER_OF_PAGES;
            }
        };

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(canvas);
        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        attr.add(MediaSizeName.EXECUTIVE);
        // размер области
        /*
     int[] insets = {5, 0, 200, 200};
     attr.add(new MediaPrintableArea(
     insets[0], // отсуп слева
     insets[1], // отсуп сверху
     insets[2], // ширина
     insets[3], // высота
     MediaPrintableArea.MM));
     */
        try {
            job.print(attr);
            //job.print();
        } catch (PrinterException ex) {
            System.out.println("ERROR!!!   ERROR!!!   ERROR!!!   ERROR!!!");
        }


    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        Vector v = new Vector();

        JList l = new JList(v);
        qPanel1.add(l);
        l.setSize(100, 100);

        l.setVisible(true);
        v.add("asdasdads");
        v.add("123123123");
        v.remove(0);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jCheckBox1ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed

        qPanel1.setMute(jCheckBox1.isSelected());

    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton15ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        File f = new File("d:\\asd.asd");
        StringBuilder sb = new StringBuilder();
        Scanner in = null;
        try {
            in = new Scanner(new FileInputStream(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (in.hasNextLine()) {
            sb = sb.append(in.nextLine() + "\n");
        }
        jLabel1.setText(sb.toString());
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed

        System.out.print("\7"); //Вот это вот издает звук

        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
        byte[] buf = new byte[1024];
        for (int j = 0; j < buf.length; j++) {
            buf[j] = (byte) j;

        }
        AudioFormat af = new AudioFormat(
            11025f,
            8, // sample size in bits
            2, // channels
            true, // signed
            false // bigendian
        );

        try {
            byte[] b = buf;
            AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(b), af, 512);

            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        buttonGroup1.clearSelection();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jRadioButton3ActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        if (jRadioButton3.isSelected()) {
            System.out.println("Still selected");
        } else {
            System.out.println("NO select");
        }
        buttonGroup1.clearSelection();
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private boolean isNum(char elem) {
        return '1' == elem || '2' == elem || '3' == elem || '4' == elem || '5' == elem
            || '6' == elem
            || '7' == elem || '8' == elem || '9' == elem || '0' == elem;
    }

    private boolean isZero(String str) {
        for (int ii = 0; ii < str.length(); ii++) {
            if (!('0' == str.charAt(ii) || '_' == str.charAt(ii))) {
                return false;
            }
        }
        return true;
    }

    /**
     * UDP Сервер. Обнаруживает изменение состояния очередей.
     */
    protected final class UDPServer extends AUDPServer {

        public UDPServer(int port) {
            super(port);
        }

        @Override
        synchronized protected void getData(String data, InetAddress clientAddress,
            int clientPort) {
            System.out.println(data);
        }
    }
    // End of variables declaration//GEN-END:variables
}
