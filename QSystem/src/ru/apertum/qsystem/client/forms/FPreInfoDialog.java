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

/*
 * FPreInfoDialog.java
 *
 * Created on Sep 16, 2010, 4:34:30 PM
 * @author Evgeniy Egorov
 */
package ru.apertum.qsystem.client.forms;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.ATalkingClock;

/**
 *
 * @author egorov
 */
public class FPreInfoDialog extends javax.swing.JDialog {

    private static FPreInfoDialog preInfoDialog;
    private static boolean result = false;
    private static int delay = 30000;
    private static String htmlText = "";
    private static String printText = "";

    /**
     * Creates new form FPreInfoDialog
     *
     * @param parent
     * @param modal
     */
    public FPreInfoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        preInfoDialog = this;
        initComponents();
        buttonBack.setVisible(!FWelcome.isInfo);
        if (WelcomeParams.getInstance().btnFont != null) {
            buttonBack.setFont(WelcomeParams.getInstance().btnFont);
            buttonInRoot.setFont(WelcomeParams.getInstance().btnFont);
            jButton2.setFont(WelcomeParams.getInstance().btnFont);
        }
    }
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FPreInfoDialog.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Статический метод который показывает модально диалог с информацией для клиентов.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param htmlText текст для прочтения
     * @param printText текст для печати
     * @param modal модальный диалог или нет
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @return продолжат сравить кастомера в очередь или нет
     */
    public static boolean showPreInfoDialog(Frame parent, String htmlText, String printText, boolean modal, boolean fullscreen, int delay) {
        FPreInfoDialog.delay = delay;
        QLog.l().logger().info("Чтение обязательной информации перед постановкой в очередь");
        if (preInfoDialog == null) {
            preInfoDialog = new FPreInfoDialog(parent, modal);
            preInfoDialog.setTitle(getLocaleMessage("dialog.title"));
            preInfoDialog.LabelCaption2.setText(getLocaleMessage("LabelCaption2.text"));
        }
        preInfoDialog.changeTextToLocale();
        FPreInfoDialog.htmlText = Uses.prepareAbsolutPathForImg(htmlText);
        FPreInfoDialog.printText = printText;
        preInfoDialog.labelHtml.setText(FPreInfoDialog.htmlText);
        FPreInfoDialog.result = false;
        Uses.setLocation(preInfoDialog);
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo() && !fullscreen)) {
            Uses.setFullSize(preInfoDialog);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                preInfoDialog.setCursor(transparentCursor);
            }
        } else {
            preInfoDialog.setSize(1280, 768);
            Uses.setLocation(preInfoDialog);
        }
        preInfoDialog.clockBack.start();
        preInfoDialog.setVisible(true);
        return result;
    }
    /**
     * Таймер, по которому будем выходить в корень меню.
     */
    public ATalkingClock clockBack = new ATalkingClock(delay, 1) {

        @Override
        public void run() {
            setVisible(false);
        }
    };

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelAll = new QPanel(WelcomeParams.getInstance().backgroundImg);
        panelUp = new QPanel(WelcomeParams.getInstance().topImgSecondary);
        LabelCaption2 = new javax.swing.JLabel();
        panelBottom = new ru.apertum.qsystem.client.model.QPanel();
        jButton2 = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonInRoot = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonBack = new QButton(WelcomeParams.getInstance().servButtonType);
        panelMain = new ru.apertum.qsystem.client.model.QPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        labelHtml = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FPreInfoDialog.class);
        panelAll.setBackground(resourceMap.getColor("panelAll.background")); // NOI18N
        panelAll.setName("panelAll"); // NOI18N

        panelUp.setBorder(new javax.swing.border.MatteBorder(null));
        panelUp.setCycle(java.lang.Boolean.FALSE);
        panelUp.setEndColor(resourceMap.getColor("panelUp.endColor")); // NOI18N
        panelUp.setEndPoint(new java.awt.Point(0, 70));
        panelUp.setName("panelUp"); // NOI18N
        panelUp.setOpaque(false);
        panelUp.setPreferredSize(new java.awt.Dimension(880, 150));
        panelUp.setStartColor(resourceMap.getColor("panelUp.startColor")); // NOI18N
        panelUp.setStartPoint(new java.awt.Point(0, -50));

        LabelCaption2.setFont(resourceMap.getFont("LabelCaption2.font")); // NOI18N
        LabelCaption2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelCaption2.setText(resourceMap.getString("LabelCaption2.text")); // NOI18N
        LabelCaption2.setName("LabelCaption2"); // NOI18N

        javax.swing.GroupLayout panelUpLayout = new javax.swing.GroupLayout(panelUp);
        panelUp.setLayout(panelUpLayout);
        panelUpLayout.setHorizontalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelCaption2, javax.swing.GroupLayout.DEFAULT_SIZE, 1113, Short.MAX_VALUE)
        );
        panelUpLayout.setVerticalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelCaption2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        panelBottom.setBorder(new javax.swing.border.MatteBorder(null));
        panelBottom.setEndPoint(new java.awt.Point(0, 100));
        panelBottom.setName("panelBottom"); // NOI18N
        panelBottom.setOpaque(false);
        panelBottom.setStartColor(resourceMap.getColor("panelBottom.startColor")); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FPreInfoDialog.class, this);
        jButton2.setAction(actionMap.get("refuse")); // NOI18N
        jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton2.setFocusPainted(false);
        jButton2.setName("jButton2"); // NOI18N

        buttonInRoot.setAction(actionMap.get("printHint")); // NOI18N
        buttonInRoot.setFont(resourceMap.getFont("buttonInRoot.font")); // NOI18N
        buttonInRoot.setIcon(resourceMap.getIcon("buttonInRoot.icon")); // NOI18N
        buttonInRoot.setText(resourceMap.getString("buttonInRoot.text")); // NOI18N
        buttonInRoot.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonInRoot.setFocusPainted(false);
        buttonInRoot.setName("buttonInRoot"); // NOI18N

        buttonBack.setAction(actionMap.get("proceed")); // NOI18N
        buttonBack.setFont(resourceMap.getFont("buttonBack.font")); // NOI18N
        buttonBack.setIcon(resourceMap.getIcon("buttonBack.icon")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
        buttonBack.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonBack.setFocusPainted(false);
        buttonBack.setName("buttonBack"); // NOI18N

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(buttonInRoot, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonBack, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(buttonInRoot, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelMain.setBackground(resourceMap.getColor("panelMain.background")); // NOI18N
        panelMain.setBorder(new javax.swing.border.MatteBorder(null));
        panelMain.setName("panelMain"); // NOI18N
        panelMain.setOpaque(false);

        jScrollPane1.setBackground(resourceMap.getColor("jScrollPane1.background")); // NOI18N
        jScrollPane1.setBorder(new javax.swing.border.MatteBorder(null));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setOpaque(false);

        labelHtml.setBackground(resourceMap.getColor("labelHtml.background")); // NOI18N
        labelHtml.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelHtml.setText(resourceMap.getString("labelHtml.text")); // NOI18N
        labelHtml.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("labelHtml.border.lineColor"), 10)); // NOI18N
        labelHtml.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelHtml.setName("labelHtml"); // NOI18N
        labelHtml.setOpaque(true);
        jScrollPane1.setViewportView(labelHtml);

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1093, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelUp, javax.swing.GroupLayout.DEFAULT_SIZE, 1115, Short.MAX_VALUE)
            .addComponent(panelBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelAllLayout.setVerticalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAllLayout.createSequentialGroup()
                .addComponent(panelUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void changeTextToLocale() {
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FPreInfoDialog.class);
        LabelCaption2.setText(resourceMap.getString("LabelCaption2.text")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        buttonInRoot.setText(resourceMap.getString("buttonInRoot.text")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
    }

    @Action
    public void refuse() {
        QLog.l().logger().debug("Отказ встать в очередь после чтения обязательной информации");
        result = false;
        if (clockBack.isActive()) {
            clockBack.stop();
        }
        setVisible(false);
    }

    @Action
    public void printHint() {
        QLog.l().logger().info("Печать подсказки об обязательной информации");
        FWelcome.printPreInfoText(printText);
    }

    @Action
    public void proceed() {
        QLog.l().logger().debug("Встаем в очередь после чтения обязательной информации");
        result = true;
        if (clockBack.isActive()) {
            clockBack.stop();
        }
        setVisible(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelCaption2;
    private javax.swing.JButton buttonBack;
    private javax.swing.JButton buttonInRoot;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelHtml;
    private ru.apertum.qsystem.client.model.QPanel panelAll;
    private ru.apertum.qsystem.client.model.QPanel panelBottom;
    private ru.apertum.qsystem.client.model.QPanel panelMain;
    private ru.apertum.qsystem.client.model.QPanel panelUp;
    // End of variables declaration//GEN-END:variables
}
