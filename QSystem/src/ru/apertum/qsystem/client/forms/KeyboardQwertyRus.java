/*
 * Copyright (C) 2012 Evgeniy Egorov
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
 * KeyboardQwertyRus.java
 *
 * Created on Apr 5, 2012, 6:52:45 PM
 */
package ru.apertum.qsystem.client.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Evgeniy Egorov
 */
public class KeyboardQwertyRus extends javax.swing.JPanel {

    private static final String BACKSPASE = "стереть";
    private static final String SPASE = "пробел";
    public static final byte UP_LOW = 0;
    public static final byte UP = 1;
    public static final byte LOW = 2;

    public static interface KeyPressEvent {

        public void keyPress(String key);

        public void keyBackspasePress();
    }
    private KeyPressEvent keyPressEvent;

    public KeyPressEvent getKeyPressEvent() {
        return keyPressEvent;
    }

    public void setKeyPressEvent(KeyPressEvent keyPressEvent) {
        this.keyPressEvent = keyPressEvent;
    }
    private Font font;
    private Color foregraundColor;

    @Override
    public Font getFont() {
        return font;
    }

    public final void setFont(Font font, Color foregraundColor) {
        this.font = font;
        this.foregraundColor = foregraundColor;
        goCmp(this);
    }

    private void goCmp(JPanel panel) {
        for (Component cmp : panel.getComponents()) {
            if (cmp instanceof JButton && cmp != buttonBackspace) {
                cmp.setFont(font);
                cmp.setForeground(foregraundColor);
            } else {
                if (cmp instanceof JPanel) {
                    goCmp((JPanel) cmp);
                }
            }
        }
    }
    private byte regim = UP_LOW;

    public byte getRegim() {
        return regim;
    }

    public final void setRegim(byte regim) {
        this.regim = regim;
        toggleButtonUpper.setVisible(regim != UP && regim != LOW);
    }

    /** Creates new form KeyboardQwertyRus */
    public KeyboardQwertyRus() {
        initComponents();
        font = new Font("Arial", 2, 29);
        foregraundColor = Color.BLACK;
        setFont(font, foregraundColor);
        setRegim(regim);
    }

    public static void main(String[] args) {
        KeyboardQwertyRus k = new KeyboardQwertyRus();
        k.setKeyPressEvent(new KeyPressEvent() {

            @Override
            public void keyPress(String key) {
                System.out.print(key);
            }

            @Override
            public void keyBackspasePress() {
                System.out.println("DELETE");
            }
        });
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        f.add(k);
        f.setVisible(true);
        f.setSize(1024, 340);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        panelLettersUp = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        panelLettersMiddleLeft = new javax.swing.JPanel();
        panelLettersMiddle = new javax.swing.JPanel();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        panelLettersMiddleRight = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelLettersBottomLeft = new javax.swing.JPanel();
        toggleButtonUpper = new javax.swing.JToggleButton();
        panelLettersBottom = new javax.swing.JPanel();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        panellLettersBottomRight = new javax.swing.JPanel();
        buttonBackspace = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        panelSpase = new javax.swing.JPanel();
        jButton47 = new javax.swing.JButton();
        panelSpaseLeft = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        panelSpaseRight = new javax.swing.JPanel();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(5, 1, 0, 10));

        panelButtonsNumeric.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtonsNumeric.setName("panelButtonsNumeric"); // NOI18N
        panelButtonsNumeric.setLayout(new java.awt.GridLayout(1, 0, 7, 0));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(KeyboardQwertyRus.class);
        jButton3.setFont(resourceMap.getFont("jButton3.font")); // NOI18N
        jButton3.setForeground(resourceMap.getColor("jButton3.foreground")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton3);

        jButton4.setFont(resourceMap.getFont("jButton4.font")); // NOI18N
        jButton4.setForeground(resourceMap.getColor("jButton4.foreground")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton4);

        jButton5.setFont(resourceMap.getFont("jButton5.font")); // NOI18N
        jButton5.setForeground(resourceMap.getColor("jButton5.foreground")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton5);

        jButton6.setFont(resourceMap.getFont("jButton6.font")); // NOI18N
        jButton6.setForeground(resourceMap.getColor("jButton6.foreground")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
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
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton7);

        jButton8.setFont(resourceMap.getFont("jButton8.font")); // NOI18N
        jButton8.setForeground(resourceMap.getColor("jButton8.foreground")); // NOI18N
        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton8);

        jButton9.setFont(resourceMap.getFont("jButton9.font")); // NOI18N
        jButton9.setForeground(resourceMap.getColor("jButton9.foreground")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton9);

        jButton10.setFont(resourceMap.getFont("jButton10.font")); // NOI18N
        jButton10.setForeground(resourceMap.getColor("jButton10.foreground")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton10.setName("jButton10"); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton10);

        jButton11.setFont(resourceMap.getFont("jButton11.font")); // NOI18N
        jButton11.setForeground(resourceMap.getColor("jButton11.foreground")); // NOI18N
        jButton11.setText(resourceMap.getString("jButton11.text")); // NOI18N
        jButton11.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton11.setName("jButton11"); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton11);

        jButton12.setFont(resourceMap.getFont("jButton12.font")); // NOI18N
        jButton12.setForeground(resourceMap.getColor("jButton12.foreground")); // NOI18N
        jButton12.setText(resourceMap.getString("jButton12.text")); // NOI18N
        jButton12.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton12.setName("jButton12"); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton12);

        jButton13.setFont(resourceMap.getFont("jButton13.font")); // NOI18N
        jButton13.setForeground(resourceMap.getColor("jButton13.foreground")); // NOI18N
        jButton13.setText(resourceMap.getString("jButton13.text")); // NOI18N
        jButton13.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton13.setName("jButton13"); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton13);

        jButton48.setFont(resourceMap.getFont("jButton48.font")); // NOI18N
        jButton48.setForeground(resourceMap.getColor("jButton48.foreground")); // NOI18N
        jButton48.setText(resourceMap.getString("jButton48.text")); // NOI18N
        jButton48.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton48.setName("jButton48"); // NOI18N
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton48);

        jButton49.setFont(resourceMap.getFont("jButton49.font")); // NOI18N
        jButton49.setForeground(resourceMap.getColor("jButton49.foreground")); // NOI18N
        jButton49.setText(resourceMap.getString("jButton49.text")); // NOI18N
        jButton49.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton49.setName("jButton49"); // NOI18N
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelButtonsNumeric.add(jButton49);

        add(panelButtonsNumeric);

        panelLettersUp.setName("panelLettersUp"); // NOI18N
        panelLettersUp.setOpaque(false);
        panelLettersUp.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton14.setFont(resourceMap.getFont("jButton14.font")); // NOI18N
        jButton14.setText(resourceMap.getString("jButton14.text")); // NOI18N
        jButton14.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton14.setName("jButton14"); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton14);

        jButton15.setFont(resourceMap.getFont("jButton15.font")); // NOI18N
        jButton15.setText(resourceMap.getString("jButton15.text")); // NOI18N
        jButton15.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton15.setName("jButton15"); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton15);

        jButton16.setFont(resourceMap.getFont("jButton16.font")); // NOI18N
        jButton16.setText(resourceMap.getString("jButton16.text")); // NOI18N
        jButton16.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton16.setName("jButton16"); // NOI18N
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton16);

        jButton17.setFont(resourceMap.getFont("jButton17.font")); // NOI18N
        jButton17.setText(resourceMap.getString("jButton17.text")); // NOI18N
        jButton17.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton17.setName("jButton17"); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton17);

        jButton18.setFont(resourceMap.getFont("jButton18.font")); // NOI18N
        jButton18.setText(resourceMap.getString("jButton18.text")); // NOI18N
        jButton18.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton18.setName("jButton18"); // NOI18N
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton18);

        jButton19.setFont(resourceMap.getFont("jButton19.font")); // NOI18N
        jButton19.setText(resourceMap.getString("jButton19.text")); // NOI18N
        jButton19.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton19.setName("jButton19"); // NOI18N
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton19);

        jButton20.setFont(resourceMap.getFont("jButton20.font")); // NOI18N
        jButton20.setText(resourceMap.getString("jButton20.text")); // NOI18N
        jButton20.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton20.setName("jButton20"); // NOI18N
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton20);

        jButton21.setFont(resourceMap.getFont("jButton21.font")); // NOI18N
        jButton21.setText(resourceMap.getString("jButton21.text")); // NOI18N
        jButton21.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton21.setName("jButton21"); // NOI18N
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton21);

        jButton22.setFont(resourceMap.getFont("jButton22.font")); // NOI18N
        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton22.setName("jButton22"); // NOI18N
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton22);

        jButton23.setFont(resourceMap.getFont("jButton23.font")); // NOI18N
        jButton23.setText(resourceMap.getString("jButton23.text")); // NOI18N
        jButton23.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton23.setName("jButton23"); // NOI18N
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton23);

        jButton24.setFont(resourceMap.getFont("jButton24.font")); // NOI18N
        jButton24.setText(resourceMap.getString("jButton24.text")); // NOI18N
        jButton24.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton24.setName("jButton24"); // NOI18N
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton24);

        jButton25.setFont(resourceMap.getFont("jButton25.font")); // NOI18N
        jButton25.setText(resourceMap.getString("jButton25.text")); // NOI18N
        jButton25.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton25.setName("jButton25"); // NOI18N
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersUp.add(jButton25);

        add(panelLettersUp);

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel3.setName("jPanel3"); // NOI18N

        panelLettersMiddleLeft.setBorder(new javax.swing.border.MatteBorder(null));
        panelLettersMiddleLeft.setName("panelLettersMiddleLeft"); // NOI18N
        panelLettersMiddleLeft.setOpaque(false);

        javax.swing.GroupLayout panelLettersMiddleLeftLayout = new javax.swing.GroupLayout(panelLettersMiddleLeft);
        panelLettersMiddleLeft.setLayout(panelLettersMiddleLeftLayout);
        panelLettersMiddleLeftLayout.setHorizontalGroup(
            panelLettersMiddleLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );
        panelLettersMiddleLeftLayout.setVerticalGroup(
            panelLettersMiddleLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        panelLettersMiddle.setName("panelLettersMiddle"); // NOI18N
        panelLettersMiddle.setOpaque(false);
        panelLettersMiddle.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton26.setFont(resourceMap.getFont("jButton26.font")); // NOI18N
        jButton26.setText(resourceMap.getString("jButton26.text")); // NOI18N
        jButton26.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton26.setName("jButton26"); // NOI18N
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton26);

        jButton27.setFont(resourceMap.getFont("jButton27.font")); // NOI18N
        jButton27.setText(resourceMap.getString("jButton27.text")); // NOI18N
        jButton27.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton27.setName("jButton27"); // NOI18N
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton27);

        jButton28.setFont(resourceMap.getFont("jButton28.font")); // NOI18N
        jButton28.setText(resourceMap.getString("jButton28.text")); // NOI18N
        jButton28.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton28.setName("jButton28"); // NOI18N
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton28);

        jButton29.setFont(resourceMap.getFont("jButton29.font")); // NOI18N
        jButton29.setText(resourceMap.getString("jButton29.text")); // NOI18N
        jButton29.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton29.setName("jButton29"); // NOI18N
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton29);

        jButton30.setFont(resourceMap.getFont("jButton30.font")); // NOI18N
        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton30.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton30.setName("jButton30"); // NOI18N
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton30);

        jButton31.setFont(resourceMap.getFont("jButton31.font")); // NOI18N
        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton31.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton31.setName("jButton31"); // NOI18N
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton31);

        jButton32.setFont(resourceMap.getFont("jButton32.font")); // NOI18N
        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton32.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton32.setName("jButton32"); // NOI18N
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton32);

        jButton33.setFont(resourceMap.getFont("jButton33.font")); // NOI18N
        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton33.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton33.setName("jButton33"); // NOI18N
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton33);

        jButton34.setFont(resourceMap.getFont("jButton34.font")); // NOI18N
        jButton34.setText(resourceMap.getString("jButton34.text")); // NOI18N
        jButton34.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton34.setName("jButton34"); // NOI18N
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton34);

        jButton35.setFont(resourceMap.getFont("jButton35.font")); // NOI18N
        jButton35.setText(resourceMap.getString("jButton35.text")); // NOI18N
        jButton35.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton35.setName("jButton35"); // NOI18N
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton35);

        jButton36.setFont(resourceMap.getFont("jButton36.font")); // NOI18N
        jButton36.setText(resourceMap.getString("jButton36.text")); // NOI18N
        jButton36.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton36.setName("jButton36"); // NOI18N
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersMiddle.add(jButton36);

        panelLettersMiddleRight.setBorder(new javax.swing.border.MatteBorder(null));
        panelLettersMiddleRight.setName("panelLettersMiddleRight"); // NOI18N
        panelLettersMiddleRight.setOpaque(false);
        panelLettersMiddleRight.setPreferredSize(new java.awt.Dimension(60, 77));

        javax.swing.GroupLayout panelLettersMiddleRightLayout = new javax.swing.GroupLayout(panelLettersMiddleRight);
        panelLettersMiddleRight.setLayout(panelLettersMiddleRightLayout);
        panelLettersMiddleRightLayout.setHorizontalGroup(
            panelLettersMiddleRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );
        panelLettersMiddleRightLayout.setVerticalGroup(
            panelLettersMiddleRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(panelLettersMiddleLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelLettersMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, 962, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelLettersMiddleRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLettersMiddleLeft, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelLettersMiddle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(panelLettersMiddleRight, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        add(jPanel3);

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel2.setName("jPanel2"); // NOI18N

        panelLettersBottomLeft.setBorder(new javax.swing.border.MatteBorder(null));
        panelLettersBottomLeft.setName("panelLettersBottomLeft"); // NOI18N
        panelLettersBottomLeft.setOpaque(false);
        panelLettersBottomLeft.setPreferredSize(new java.awt.Dimension(120, 77));

        toggleButtonUpper.setFont(resourceMap.getFont("toggleButtonUpper.font")); // NOI18N
        toggleButtonUpper.setText(resourceMap.getString("toggleButtonUpper.text")); // NOI18N
        toggleButtonUpper.setName("toggleButtonUpper"); // NOI18N

        javax.swing.GroupLayout panelLettersBottomLeftLayout = new javax.swing.GroupLayout(panelLettersBottomLeft);
        panelLettersBottomLeft.setLayout(panelLettersBottomLeftLayout);
        panelLettersBottomLeftLayout.setHorizontalGroup(
            panelLettersBottomLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLettersBottomLeftLayout.createSequentialGroup()
                .addComponent(toggleButtonUpper, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelLettersBottomLeftLayout.setVerticalGroup(
            panelLettersBottomLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLettersBottomLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toggleButtonUpper, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelLettersBottom.setName("panelLettersBottom"); // NOI18N
        panelLettersBottom.setOpaque(false);
        panelLettersBottom.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton37.setFont(resourceMap.getFont("jButton37.font")); // NOI18N
        jButton37.setText(resourceMap.getString("jButton37.text")); // NOI18N
        jButton37.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton37.setName("jButton37"); // NOI18N
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton37);

        jButton38.setFont(resourceMap.getFont("jButton38.font")); // NOI18N
        jButton38.setText(resourceMap.getString("jButton38.text")); // NOI18N
        jButton38.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton38.setName("jButton38"); // NOI18N
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton38);

        jButton39.setFont(resourceMap.getFont("jButton39.font")); // NOI18N
        jButton39.setText(resourceMap.getString("jButton39.text")); // NOI18N
        jButton39.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton39.setName("jButton39"); // NOI18N
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton39);

        jButton40.setFont(resourceMap.getFont("jButton40.font")); // NOI18N
        jButton40.setText(resourceMap.getString("jButton40.text")); // NOI18N
        jButton40.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton40.setName("jButton40"); // NOI18N
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton40);

        jButton41.setFont(resourceMap.getFont("jButton41.font")); // NOI18N
        jButton41.setText(resourceMap.getString("jButton41.text")); // NOI18N
        jButton41.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton41.setName("jButton41"); // NOI18N
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton41);

        jButton42.setFont(resourceMap.getFont("jButton42.font")); // NOI18N
        jButton42.setText(resourceMap.getString("jButton42.text")); // NOI18N
        jButton42.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton42.setName("jButton42"); // NOI18N
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton42);

        jButton43.setFont(resourceMap.getFont("jButton43.font")); // NOI18N
        jButton43.setText(resourceMap.getString("jButton43.text")); // NOI18N
        jButton43.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton43.setName("jButton43"); // NOI18N
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton43);

        jButton44.setFont(resourceMap.getFont("jButton44.font")); // NOI18N
        jButton44.setText(resourceMap.getString("jButton44.text")); // NOI18N
        jButton44.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton44.setName("jButton44"); // NOI18N
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton44);

        jButton45.setFont(resourceMap.getFont("jButton45.font")); // NOI18N
        jButton45.setText(resourceMap.getString("jButton45.text")); // NOI18N
        jButton45.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton45.setName("jButton45"); // NOI18N
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton45);

        jButton46.setText(resourceMap.getString("jButton46.text")); // NOI18N
        jButton46.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton46.setName("jButton46"); // NOI18N
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelLettersBottom.add(jButton46);

        panellLettersBottomRight.setBorder(new javax.swing.border.MatteBorder(null));
        panellLettersBottomRight.setName("panellLettersBottomRight"); // NOI18N
        panellLettersBottomRight.setOpaque(false);
        panellLettersBottomRight.setPreferredSize(new java.awt.Dimension(120, 77));

        buttonBackspace.setFont(resourceMap.getFont("buttonBackspace.font")); // NOI18N
        buttonBackspace.setForeground(resourceMap.getColor("buttonBackspace.foreground")); // NOI18N
        buttonBackspace.setText(resourceMap.getString("buttonBackspace.text")); // NOI18N
        buttonBackspace.setName("buttonBackspace"); // NOI18N
        buttonBackspace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        javax.swing.GroupLayout panellLettersBottomRightLayout = new javax.swing.GroupLayout(panellLettersBottomRight);
        panellLettersBottomRight.setLayout(panellLettersBottomRightLayout);
        panellLettersBottomRightLayout.setHorizontalGroup(
            panellLettersBottomRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panellLettersBottomRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonBackspace, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
        );
        panellLettersBottomRightLayout.setVerticalGroup(
            panellLettersBottomRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panellLettersBottomRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonBackspace, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(panelLettersBottomLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelLettersBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 842, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panellLettersBottomRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLettersBottomLeft, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(panellLettersBottomRight, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(panelLettersBottom, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        add(jPanel2);

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setName("jPanel1"); // NOI18N

        panelSpase.setName("panelSpase"); // NOI18N
        panelSpase.setOpaque(false);
        panelSpase.setLayout(new java.awt.GridLayout(1, 0));

        jButton47.setFont(resourceMap.getFont("jButton47.font")); // NOI18N
        jButton47.setText(resourceMap.getString("jButton47.text")); // NOI18N
        jButton47.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton47.setName("jButton47"); // NOI18N
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });
        panelSpase.add(jButton47);

        panelSpaseLeft.setBorder(new javax.swing.border.MatteBorder(null));
        panelSpaseLeft.setName("panelSpaseLeft"); // NOI18N
        panelSpaseLeft.setOpaque(false);

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        jButton50.setText(resourceMap.getString("jButton50.text")); // NOI18N
        jButton50.setName("jButton50"); // NOI18N
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        jButton51.setText(resourceMap.getString("jButton51.text")); // NOI18N
        jButton51.setName("jButton51"); // NOI18N
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        javax.swing.GroupLayout panelSpaseLeftLayout = new javax.swing.GroupLayout(panelSpaseLeft);
        panelSpaseLeft.setLayout(panelSpaseLeftLayout);
        panelSpaseLeftLayout.setHorizontalGroup(
            panelSpaseLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSpaseLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton51, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );
        panelSpaseLeftLayout.setVerticalGroup(
            panelSpaseLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSpaseLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSpaseLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton50, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jButton51, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)))
        );

        panelSpaseRight.setBorder(new javax.swing.border.MatteBorder(null));
        panelSpaseRight.setName("panelSpaseRight"); // NOI18N
        panelSpaseRight.setOpaque(false);
        panelSpaseRight.setPreferredSize(new java.awt.Dimension(277, 55));

        jButton52.setText(resourceMap.getString("jButton52.text")); // NOI18N
        jButton52.setName("jButton52"); // NOI18N
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        jButton53.setText(resourceMap.getString("jButton53.text")); // NOI18N
        jButton53.setName("jButton53"); // NOI18N
        jButton53.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        jButton54.setText(resourceMap.getString("jButton54.text")); // NOI18N
        jButton54.setName("jButton54"); // NOI18N
        jButton54.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        jButton55.setText(resourceMap.getString("jButton55.text")); // NOI18N
        jButton55.setName("jButton55"); // NOI18N
        jButton55.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClick(evt);
            }
        });

        javax.swing.GroupLayout panelSpaseRightLayout = new javax.swing.GroupLayout(panelSpaseRight);
        panelSpaseRight.setLayout(panelSpaseRightLayout);
        panelSpaseRightLayout.setHorizontalGroup(
            panelSpaseRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSpaseRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton55, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton54, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton53, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelSpaseRightLayout.setVerticalGroup(
            panelSpaseRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSpaseRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSpaseRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton55, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jButton53, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jButton52, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jButton54, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(panelSpaseLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelSpase, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(panelSpaseRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSpaseLeft, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelSpaseRight, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
            .addComponent(panelSpase, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClick
        if (keyPressEvent != null) {
            if (BACKSPASE.equalsIgnoreCase(evt.getActionCommand())) {
                keyPressEvent.keyBackspasePress();
            } else {
                String res = SPASE.equalsIgnoreCase(evt.getActionCommand()) ? " " : evt.getActionCommand();
                switch (regim) {
                    case UP:
                        res = res.toUpperCase();
                        break;
                    case LOW:
                        res = res.toLowerCase();
                        break;
                    default:
                        res = (toggleButtonUpper.isSelected() ? res.toUpperCase() : res.toLowerCase());
                }
                keyPressEvent.keyPress(res);
            }
        }
        toggleButtonUpper.setSelected(false);
    }//GEN-LAST:event_buttonClick

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBackspace;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel panelButtonsNumeric;
    private javax.swing.JPanel panelLettersBottom;
    private javax.swing.JPanel panelLettersBottomLeft;
    private javax.swing.JPanel panelLettersMiddle;
    private javax.swing.JPanel panelLettersMiddleLeft;
    private javax.swing.JPanel panelLettersMiddleRight;
    private javax.swing.JPanel panelLettersUp;
    private javax.swing.JPanel panelSpase;
    private javax.swing.JPanel panelSpaseLeft;
    private javax.swing.JPanel panelSpaseRight;
    private javax.swing.JPanel panellLettersBottomRight;
    private javax.swing.JToggleButton toggleButtonUpper;
    // End of variables declaration//GEN-END:variables
}
