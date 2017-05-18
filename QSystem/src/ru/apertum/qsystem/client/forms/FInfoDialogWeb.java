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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javax.swing.border.BevelBorder;
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
public class FInfoDialogWeb extends javax.swing.JDialog {

    private final JFXPanel javafxPanel;

    /**
     * Creates new form FInfoDialogWeb
     *
     * @param parent
     * @param modal
     */
    public FInfoDialogWeb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        javafxPanel = new JFXPanel();
        javafxPanel.setOpaque(false);
        javafxPanel.setBorder(new BevelBorder(0));
        javafxPanel.setLayout(new GridLayout(1, 1));
        final GridLayout gl = new GridLayout(1, 1);
        panel.setLayout(gl);
        panel.add(javafxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            bro = new Browser();
            Scene scene = new Scene(bro, 750, 500, Color.web("#666970"));
            //bro.load(WelcomeParams.getInstance().infoURL);
            javafxPanel.setScene(scene);
        });
        if (WelcomeParams.getInstance().btnFont != null) {
            btnBack.setFont(WelcomeParams.getInstance().btnFont);
            btnClose.setFont(WelcomeParams.getInstance().btnFont);
            btnForward.setFont(WelcomeParams.getInstance().btnFont);
            btnHome.setFont(WelcomeParams.getInstance().btnFont);
        }
    }
    private static Browser bro;
    private static Long result = null;
    private static int delay = 10000;
    private static FInfoDialogWeb infoDialog;

    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FInfoDialog.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Статический метод который показывает модально диалог чтения информации.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param modal модальный диалог или нет
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @param url
     * @return XML-описание результата предварительной записи, по сути это номерок. если null, то отказались от предварительной записи
     */
    public static Long showInfoDialogWeb(Frame parent, boolean modal, boolean fullscreen, int delay, String url) {
        FInfoDialogWeb.delay = delay;
        QLog.l().logger().info("Чтение WEB информации");

        if (infoDialog == null) {
            infoDialog = new FInfoDialogWeb(parent, modal);
            infoDialog.setTitle(getLocaleMessage("dialog.title"));
        }

        Platform.runLater(() -> {
            bro.load(url);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }

        infoDialog.changeTextToLocale();
        FInfoDialogWeb.result = null;
        Uses.setLocation(infoDialog);
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo() && !fullscreen)) {
            Uses.setFullSize(infoDialog);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                infoDialog.setCursor(transparentCursor);
            }

        } else {
            infoDialog.setSize(1280, 768);
            Uses.setLocation(infoDialog);
        }

        if (infoDialog.clockBack.isActive()) {
            infoDialog.clockBack.stop();
        }
        if (infoDialog.clockBack.getInterval() > 1000) {
            infoDialog.clockBack.start();
        }

        infoDialog.setVisible(true);
        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        qPanel1 = new QPanel(WelcomeParams.getInstance().backgroundImg);
        qPanel2 = new ru.apertum.qsystem.client.model.QPanel();
        btnClose = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnHome = new javax.swing.JButton();
        panel = new ru.apertum.qsystem.client.model.QPanel();

        setName("Form"); // NOI18N
        setUndecorated(true);

        qPanel1.setName("qPanel1"); // NOI18N

        qPanel2.setBorder(new javax.swing.border.MatteBorder(null));
        qPanel2.setName("qPanel2"); // NOI18N
        qPanel2.setOpaque(false);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FInfoDialogWeb.class);
        btnClose.setFont(resourceMap.getFont("btnClose.font")); // NOI18N
        btnClose.setIcon(resourceMap.getIcon("btnClose.icon")); // NOI18N
        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnForward.setFont(resourceMap.getFont("btnForward.font")); // NOI18N
        btnForward.setIcon(resourceMap.getIcon("btnForward.icon")); // NOI18N
        btnForward.setText(resourceMap.getString("btnForward.text")); // NOI18N
        btnForward.setToolTipText(resourceMap.getString("btnForward.toolTipText")); // NOI18N
        btnForward.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        btnForward.setName("btnForward"); // NOI18N
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });

        btnBack.setFont(resourceMap.getFont("btnBack.font")); // NOI18N
        btnBack.setIcon(resourceMap.getIcon("btnBack.icon")); // NOI18N
        btnBack.setText(resourceMap.getString("btnBack.text")); // NOI18N
        btnBack.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        btnBack.setName("btnBack"); // NOI18N
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnHome.setFont(resourceMap.getFont("btnHome.font")); // NOI18N
        btnHome.setIcon(resourceMap.getIcon("btnHome.icon")); // NOI18N
        btnHome.setText(resourceMap.getString("btnHome.text")); // NOI18N
        btnHome.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        btnHome.setName("btnHome"); // NOI18N
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout qPanel2Layout = new javax.swing.GroupLayout(qPanel2);
        qPanel2.setLayout(qPanel2Layout);
        qPanel2Layout.setHorizontalGroup(
            qPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(qPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 233, Short.MAX_VALUE)
                .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnForward, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        qPanel2Layout.setVerticalGroup(
            qPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(qPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(qPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnForward, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panel.setBorder(new javax.swing.border.MatteBorder(null));
        panel.setName("panel"); // NOI18N

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout qPanel1Layout = new javax.swing.GroupLayout(qPanel1);
        qPanel1.setLayout(qPanel1Layout);
        qPanel1Layout.setHorizontalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        qPanel1Layout.setVerticalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, qPanel1Layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(qPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(qPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        result = null;
        Platform.runLater(() -> {
            bro.webEngine.loadContent("<html><table width=100% height=100% align='center'><tr><td width=100% height=100% align='center'>"
                    + "<p style=\"font-size:80px;color:blue\">Opening...</p>"
                    + "</td></tr></table></html>");
        });
        if (clockBack.isActive()) {
            clockBack.stop();
        }
        setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        bro.goForward();
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        bro.goBack();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        bro.reload();
    }//GEN-LAST:event_btnHomeActionPerformed

    /**
     * Таймер, по которому будем выходить в корень меню.
     */
    public ATalkingClock clockBack = new ATalkingClock(delay, 1) {

        @Override
        public void run() {
            setVisible(false);
        }
    };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnHome;
    private ru.apertum.qsystem.client.model.QPanel panel;
    private ru.apertum.qsystem.client.model.QPanel qPanel1;
    private ru.apertum.qsystem.client.model.QPanel qPanel2;
    // End of variables declaration//GEN-END:variables

    private void changeTextToLocale() {
    }

    static class Browser extends Region {

        final private WebView browser = new WebView();
        final private WebEngine webEngine = browser.getEngine();

        public Browser() {

            try {
                //browser.getEngine().setUserStyleSheetLocation(new File("E:/a.css").toURI().toURL().toString());
                browser.getEngine().setUserStyleSheetLocation(this.getClass().getResource("/ru/apertum/qsystem/fx/css/scroll.css").toString());
            } catch (Exception ex) {
                System.err.println(ex);
            }

            browser.setOnTouchPressed((TouchEvent event) -> {
                if (infoDialog.clockBack.isActive()) {
                    infoDialog.clockBack.stop();
                }
                infoDialog.clockBack.start();
            });

            browser.setOnMouseClicked((MouseEvent event) -> {
                if (infoDialog.clockBack.isActive()) {
                    infoDialog.clockBack.stop();
                }
                infoDialog.clockBack.start();
            });

            getChildren().add(browser);
        }

        public void load(String url) {
            this.url = url;
            webEngine.load(url);
        }
        private String url = null;

        public void reload() {
            if (url != null) {
                Platform.runLater(() -> {
                    bro.load(url);
                });
            }
        }

        @Override
        protected void layoutChildren() {
            layoutInArea(browser, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
        }

        public String goBack() {
            final WebHistory history = webEngine.getHistory();
            ObservableList<WebHistory.Entry> entryList = history.getEntries();
            int currentIndex = history.getCurrentIndex();
            //    Out("currentIndex = "+currentIndex);
            //    Out(entryList.toString().replace("],","]\n"));
            if (currentIndex > 0) {
                Platform.runLater(() -> {
                    history.go(-1);
                });
            }
            return entryList.get(currentIndex > 0 ? currentIndex - 1 : currentIndex).getUrl();
        }

        public String goForward() {
            final WebHistory history = webEngine.getHistory();
            ObservableList<WebHistory.Entry> entryList = history.getEntries();
            int currentIndex = history.getCurrentIndex();
            //    Out("currentIndex = "+currentIndex);
            //    Out(entryList.toString().replace("],","]\n"));
            if (currentIndex < entryList.size() - 1) {
                Platform.runLater(() -> {
                    history.go(1);
                });
            }
            return entryList.get(currentIndex < entryList.size() - 1 ? currentIndex + 1 : currentIndex).getUrl();
        }
    }
}
