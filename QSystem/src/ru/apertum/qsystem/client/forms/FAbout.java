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

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.server.ServerProps;

/**
 * Created on 3 Март 2009 г., 14:54
 *
 * @author Evgeniy Egorov
 */
public class FAbout extends javax.swing.JDialog {

    /**
     * Используемая ссылка на диалоговое окно.
     */
    private static FAbout aboutForm;

    /**
     * Creates new form FAbout
     *
     * @param parent
     * @param modal
     */
    public FAbout(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //setSize(500, 313);
        loadVersion();

        // Фича. По нажатию Escape закрываем форму
        // свернем по esc
        getRootPane().registerKeyboardAction((ActionEvent e) -> {
            setVisible(false);
        },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Показать информацию о сборке.
     *
     * @param parent относительно этого контрола модальность и позиционирование
     * @param modal режим модальности
     */
    public static void showAbout(JFrame parent, boolean modal) {
        loadVersionSt();
        if (!"0".equals(CMRC_)) {
            JOptionPane.showMessageDialog(parent, getLocaleMessage("about.version") + " : " + VERSION_ + "\n"
                    + getLocaleMessage("about.data") + " : " + DATE_ + "\n\nApertum Projects © 2009-2014", Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF()), JOptionPane.INFORMATION_MESSAGE);
        } else {
            getForm(parent, modal, "");
            aboutForm.setVisible(true);
        }
    }

    /**
     * Показать информацию о сборке и о версии БД.
     *
     * @param parent относительно этого контрола модальность и позиционирование
     * @param modal режим модальности
     * @param verDB отображаемая версия БД.
     */
    public static void showAbout(JFrame parent, boolean modal, String verDB) {
        loadVersionSt();
        if (!"0".equals(CMRC_)) {
            JOptionPane.showMessageDialog(parent, getLocaleMessage("about.version") + " : " + VERSION_ + "\n"
                    + ("".equals(verDB) ? "" : (getLocaleMessage("about.db_version") + " : " + verDB))
                    + "\n" + getLocaleMessage("about.data") + " : " + DATE_ + "\n\nApertum Projects © 2009-2014", Uses.getLocaleMessage("project.name" + FAbout.getCMRC_SUFF()), JOptionPane.INFORMATION_MESSAGE);
        } else {
            getForm(parent, modal, verDB);
            aboutForm.setVisible(true);
        }
    }

    /**
     * Опредилить Singleton.
     *
     * @param parent относительно этого контрола модальность и позиционирование
     * @param modal режим модальности
     */
    private static void getForm(JFrame parent, boolean modal, String verDB) {
        QLog.l().logger().info("Демонстрация информации о программе.");
        if (aboutForm == null) {
            aboutForm = new FAbout(parent, modal);
        }
        if (aboutForm != null) {
            // Отцентирируем
            final Toolkit kit = Toolkit.getDefaultToolkit();
            aboutForm.setLocation((Math.round(kit.getScreenSize().width - aboutForm.getWidth()) / 2),
                    (Math.round(kit.getScreenSize().height - aboutForm.getHeight()) / 2));
        }
        aboutForm.labelDBVer.setText("".equals(verDB) ? "" : (getLocaleMessage("about.db_version") + " : " + verDB));
    }
    private static ResourceMap localeMap = null;

    public static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FAbout.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Загрузим параметры сборки билда из файла с версией.
     */
    public static void loadVersionSt() {
        final Properties settings = new Properties();
        //"/ru/apertum/qsystem/reports/web/"
        final InputStream inStream = new String().getClass().getResourceAsStream("/ru/apertum/qsystem/common/version.properties");

        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new ClientException("Проблемы с чтением версии. " + ex);
        }
        DATE_ = settings.getProperty(DATE);
        VERSION_ = settings.getProperty(VERSION);
        NAME_ = settings.getProperty(NAME, "");
        VERSION_DB_ = settings.getProperty(VERSION_DB);
        CMRC_ = settings.getProperty(CMRC);
        CMRC_SUFF = "0".equals(CMRC_) || CMRC_.isEmpty() ? "" : (CMRC_.startsWith(".") ? CMRC_ : "." + CMRC_);
    }
    public final static String DATE = "date";
    public final static String VERSION = "version";
    public final static String NAME = "name";
    public final static String VERSION_DB = "version_db";
    public final static String CMRC = "cmrc";
    public static String DATE_ = "";
    public static String VERSION_ = "";
    public static String NAME_ = "";
    public static String VERSION_DB_ = "";
    public static String CMRC_ = "";
    public static String CMRC_SUFF = "";

    public static String getCMRC_SUFF() {
        if (CMRC_.isEmpty()) {
            loadVersionSt();
        }
        return CMRC_SUFF;
    }

    private void loadVersion() {
        loadVersionSt();
        labelDate.setText(getLocaleMessage("about.data") + " : " + DATE_);
        labelVersion.setText(getLocaleMessage("about.version") + " : " + VERSION_);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel1 = new QPanel("/ru/apertum/qsystem/client/forms/resources/fon_about.jpg");
        labelRight = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labelDBVer = new javax.swing.JLabel();
        labelVersion = new javax.swing.JLabel();
        labelDate = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jToggleButtonLic = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FAbout.class, this);
        jButton1.setAction(actionMap.get("close")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setResizable(false);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setMaximumSize(new java.awt.Dimension(500, 313));
        jPanel1.setMinimumSize(new java.awt.Dimension(500, 313));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPanel1KeyPressed(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FAbout.class);
        labelRight.setFont(resourceMap.getFont("labelRight.font")); // NOI18N
        labelRight.setForeground(resourceMap.getColor("labelRight.foreground")); // NOI18N
        labelRight.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelRight.setText(resourceMap.getString("labelRight.text")); // NOI18N
        labelRight.setToolTipText(resourceMap.getString("labelRight.toolTipText")); // NOI18N
        labelRight.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRight.setName("labelRight"); // NOI18N
        labelRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRightMouseClicked(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 223, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );

        jPanel3.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel4.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setOpaque(false);

        labelDBVer.setText(resourceMap.getString("labelDBVer.text")); // NOI18N
        labelDBVer.setName("labelDBVer"); // NOI18N

        labelVersion.setText(resourceMap.getString("labelVersion.text")); // NOI18N
        labelVersion.setName("labelVersion"); // NOI18N

        labelDate.setText(resourceMap.getString("labelDate.text")); // NOI18N
        labelDate.setName("labelDate"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelDBVer)
                    .addComponent(labelVersion)
                    .addComponent(labelDate))
                .addContainerGap(169, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDBVer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelVersion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelDate)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 188, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        jToggleButtonLic.setText(resourceMap.getString("jToggleButtonLic.text")); // NOI18N
        jToggleButtonLic.setName("jToggleButtonLic"); // NOI18N
        jToggleButtonLic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonLicActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelRight))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jToggleButtonLic)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelRight, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(277, Short.MAX_VALUE)
                .addComponent(jToggleButtonLic)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 313));

        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(resourceMap.getFont("jTextArea1.font")); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText(resourceMap.getString("jTextArea1.text")); // NOI18N
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(-16777216,true), 4));
        jTextArea1.setMinimumSize(new java.awt.Dimension(102, 200));
        jTextArea1.setName("jTextArea1"); // NOI18N
        jTextArea1.setPreferredSize(new java.awt.Dimension(148, 220));
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 311, 503, 295));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-500)/2, (screenSize.height-313)/2, 500, 313);
    }// </editor-fold>//GEN-END:initComponents

private void jPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyPressed

    setVisible(false);
}//GEN-LAST:event_jPanel1KeyPressed

private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked

    setVisible(false);
}//GEN-LAST:event_jPanel1MouseClicked

private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed

    setVisible(false);
}//GEN-LAST:event_jButton1KeyPressed

private void labelRightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRightMouseClicked
    try {
        Desktop.getDesktop().browse(new URI("http://www.apertum.ru"));
    } catch (URISyntaxException | IOException ex) {
        QLog.l().logger().error(ex);
    }
}//GEN-LAST:event_labelRightMouseClicked

private void jToggleButtonLicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonLicActionPerformed
    final int delta = 290 * (jToggleButtonLic.isSelected() ? 1 : -1);
    aboutForm.setSize(aboutForm.getSize().width, aboutForm.getSize().height + delta);
}//GEN-LAST:event_jToggleButtonLicActionPerformed

    @Action
    public void close() {
        setVisible(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToggleButton jToggleButtonLic;
    private javax.swing.JLabel labelDBVer;
    private javax.swing.JLabel labelDate;
    private javax.swing.JLabel labelRight;
    private javax.swing.JLabel labelVersion;
    // End of variables declaration//GEN-END:variables
}
