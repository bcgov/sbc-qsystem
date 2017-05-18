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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.common.WelcomeParams;
import ru.apertum.qsystem.client.model.QButton;
import ru.apertum.qsystem.client.model.QPanel;
import ru.apertum.qsystem.common.BrowserFX;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.server.model.response.QRespItem;

/**
 * @author Evgeniy Egorov
 */
public class FResponseDialog extends javax.swing.JDialog {

    private static FResponseDialog respDialog;

    /**
     * Creates new form FStandAdvance
     *
     * @param parent
     * @param modal
     */
    public FResponseDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        if (WelcomeParams.getInstance().btnFont != null) {
            buttonBack.setFont(WelcomeParams.getInstance().btnFont);
            buttonInRoot.setFont(WelcomeParams.getInstance().btnFont);
            jButton2.setFont(WelcomeParams.getInstance().btnFont);
        }
        
        //На верхней панели пункта регистрации, там где заголовок и картинка в углу, можно вывести вэб-контент по URL. Оставьте пустым если не требуется
        if (!WelcomeParams.getInstance().topURL.isEmpty()) {
            panelUp.removeAll();
            final BrowserFX bro = new BrowserFX();
            final GridLayout gl = new GridLayout(1, 1);
            panelUp.setLayout(gl);
            panelUp.add(bro);
            bro.load(Uses.prepareAbsolutPathForImg(WelcomeParams.getInstance().topURL));
        }
    }
    private static QRespItem result = null;
    private static int delay = 10000;

    public static void setRoot(QRespItem rootEl) {
        root = rootEl;
        preLevel = rootEl;
    }
    /**
     * Корень справочной системы
     */
    private static QRespItem root;
    /**
     * Предыдущий уровень кнопок
     */
    private static QRespItem preLevel;
    /**
     * Текущий уровень кнопок
     */
    private static QRespItem level;
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FResponseDialog.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Статический метод который показывает модально диалог выбора времени для предварительной записи клиентов.
     *
     * @param parent фрейм относительно которого будет модальность
     * @param respList XML-список возможных отзывов
     * @param modal модальный диалог или нет
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @return XML-описание результата предварительной записи, по сути это номерок. если null, то отказались от предварительной записи
     */
    public static QRespItem showResponseDialog(Frame parent, QRespItem respList, boolean modal, boolean fullscreen, int delay) {
        FResponseDialog.delay = delay;
        QLog.l().logger().info("Выбор отзыва");
        if (respDialog == null) {
            respDialog = new FResponseDialog(parent, modal);
            respDialog.setTitle(getLocaleMessage("dialog.title"));
        }
        respDialog.changeTextToLocale();
        FResponseDialog.setRoot(respList);
        result = null;
        Uses.setLocation(respDialog);
        respDialog.changeTextToLocale();
        if (!(QConfig.cfg().isDebug() || QConfig.cfg().isDemo() && !fullscreen)) {
            Uses.setFullSize(respDialog);
            if (QConfig.cfg().isHideCursor()) {
                int[] pixels = new int[16 * 16];
                Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
                Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
                respDialog.setCursor(transparentCursor);
            }

        } else {
            respDialog.setSize(1280, 768);
            Uses.setLocation(respDialog);
        }
        respDialog.showLevel(FResponseDialog.root);
        if (respDialog.clockBack.isActive()) {
            respDialog.clockBack.stop();
        }
        if (respDialog.clockBack.getInterval() > 1000) {
            respDialog.clockBack.start();
        }
        respDialog.setVisible(true);
        return result;
    }

    private void showLevel(QRespItem level) {
        panelMain.removeAll();
        panelMain.repaint();
        if (level.getParent() == null && level != root) {
            level.setParent(FResponseDialog.level);
        }
        FResponseDialog.level = level;
        if (level.isLeaf() || level.getChildCount() == 1) {
            final JLabel label = new JLabel(Uses.prepareAbsolutPathForImg(level.isLeaf() ? level.getHTMLText() : level.getChildren().getFirst().getHTMLText()));
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            label.setBorder(new LineBorder(Color.GRAY, 10));
            label.setHorizontalAlignment(JLabel.CENTER);
            final GridLayout gl = new GridLayout();
            panelMain.setLayout(gl);
            panelMain.add(label);
            label.setBounds(-1, -1, panelMain.getWidth() + 2, panelMain.getHeight() + 2);
        } else {
            int delta = 10;
            switch (Toolkit.getDefaultToolkit().getScreenSize().width) {
                case 640:
                    delta = 10;
                    break;
                case 800:
                    delta = 20;
                    break;
                case 1024:
                    delta = 30;
                    break;
                case 1366:
                    delta = 25;
                    break;
                case 1280:
                    delta = 40;
                    break;
                case 1600:
                    delta = 50;
                    break;
                case 1920:
                    delta = 60;
                    break;
            }
            if (QConfig.cfg().isDebug() || QConfig.cfg().isDemo()) {
                delta = 25;
            }
            int cols = 1;
            int rows = level.getChildCount() + (root.getHTMLText().trim().isEmpty() ? 0 : 1);

            panelMain.setLayout(new GridLayout(rows < 5 ? 5 : rows, cols, delta, delta / 2));
            if (!root.getHTMLText().trim().isEmpty()) {
                final FResponseDialog.RespButton button = new FResponseDialog.RespButton(root, WelcomeParams.getInstance().buttonType);
                button.addActionListener(null);
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusable(false);
                panelMain.add(button);
            }
            for (QRespItem item : level.getChildren()) {
                final FResponseDialog.RespButton button = new FResponseDialog.RespButton(item, WelcomeParams.getInstance().buttonType);
                panelMain.add(button);
            }
            if (level != root) {
                preLevel = level.getParent();
            } else {
                preLevel = root;
            }
            setSize(getWidth() + s(), getHeight());
        }
    }
    private static int s = 1;

    private static int s() {
        s = (-1) * s;
        return s;
    }

    /**
     * Эта ботва для кнпки. Картинка на кнопке рисуемая.
     */
    private static Image background;
    private final static HashMap<String, Image> imgs = new HashMap<>();

    private static class RespButton extends JButton {

        final Long id;
        final QRespItem el;

        public RespButton(QRespItem item, String resourceName) {
            this.el = item;
            id = item.getId();
            setFocusPainted(false);
            setText(item.getHTMLText());
            setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new BevelBorder(BevelBorder.RAISED)));
            addActionListener((ActionEvent e) -> {
                if (el.isLeaf()) {
                    //Если услуга требует ввода данных пользователем, то нужно получить эти данные из диалога ввода, т.к. потом при постановки в очередь предварительных
                    // нет ввода данных, только номера регистрации.
                    String inputData = null;
                    if (el.getInput_required()) {
                        inputData = FInputDialog.showInputDialog(null, true, FWelcome.netProperty, false, WelcomeParams.getInstance().delayBack, el.getInput_caption());
                    }
                    FTimedDialog.showTimedDialog(null, true, el.getHTMLText(), 1000);
                    el.data = inputData;
                    result = el;
                    respDialog.setVisible(false);
                } else {
                    respDialog.showLevel(el);
                }
            });

            // Нарисуем картинку на кнопке если надо. Загрузить можно из файла или ресурса
            if ("".equals(resourceName)) {
                background = null;
            } else {
                background = imgs.get(resourceName);
                if (background == null) {
                    File file = new File(resourceName);
                    if (file.exists()) {
                        try {
                            background = ImageIO.read(file);
                            imgs.put(resourceName, background);
                        } catch (IOException ex) {
                            background = null;
                            QLog.l().logger().error(ex);
                        }
                    } else {
                        final DataInputStream inStream = new DataInputStream(getClass().getResourceAsStream(resourceName));
                        byte[] b = null;
                        try {
                            b = new byte[inStream.available()];
                            inStream.readFully(b);
                        } catch (IOException ex) {
                            background = null;
                            QLog.l().logger().error(ex);
                        }
                        background = new ImageIcon(b).getImage();
                        imgs.put(resourceName, background);
                    }
                }
            }
            //займемся внешним видом
            // либо просто стандартная кнопка, либо картинка на кнопке если она есть
            if (background == null) {
                setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new BevelBorder(BevelBorder.RAISED)));
            } else {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
            }
            if (!item.isRoot()) {
                if (WelcomeParams.getInstance().responseImg) {
                    if (item.isLeaf()) {
                        setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/feedback.png")));
                    } else {
                        setIcon(new ImageIcon(getClass().getResource("/ru/apertum/qsystem/client/forms/resources/feedback_group.png")));
                    }
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            if (background != null) {
                //Image scaledImage = background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH); // это медленный вариант
                final Image scaledImage = resizeToBig(background, getWidth(), getHeight());
                final Graphics2D g2 = (Graphics2D) g;
                g2.drawImage(scaledImage, 0, 0, null, null);
                super.paintComponent(g);
            } else {
                super.paintComponent(g);
            }
        }

        private Image resizeToBig(Image originalImage, int biggerWidth, int biggerHeight) {
            final BufferedImage resizedImage = new BufferedImage(biggerWidth, biggerHeight, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = resizedImage.createGraphics();

            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(originalImage, 0, 0, biggerWidth, biggerHeight, this);
            g.dispose();

            return resizedImage;
        }
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
        LabelCaption = new javax.swing.JLabel();
        panelBottom = new ru.apertum.qsystem.client.model.QPanel();
        jButton2 = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonBack = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonInRoot = new QButton(WelcomeParams.getInstance().servButtonType);
        panelMain = new ru.apertum.qsystem.client.model.QPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FResponseDialog.class);
        panelAll.setBackground(resourceMap.getColor("panelAll.background")); // NOI18N
        panelAll.setName("panelAll"); // NOI18N

        panelUp.setBorder(new javax.swing.border.MatteBorder(null));
        panelUp.setCycle(java.lang.Boolean.FALSE);
        panelUp.setEndColor(resourceMap.getColor("panelUp.endColor")); // NOI18N
        panelUp.setEndPoint(new java.awt.Point(0, 70));
        panelUp.setName("panelUp"); // NOI18N
        panelUp.setOpaque(false);
        panelUp.setPreferredSize(new java.awt.Dimension(969, 150));
        panelUp.setStartColor(resourceMap.getColor("panelUp.startColor")); // NOI18N
        panelUp.setStartPoint(new java.awt.Point(0, -50));

        LabelCaption.setFont(resourceMap.getFont("LabelCaption.font")); // NOI18N
        LabelCaption.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelCaption.setText(resourceMap.getString("LabelCaption.text")); // NOI18N
        LabelCaption.setName("LabelCaption"); // NOI18N

        javax.swing.GroupLayout panelUpLayout = new javax.swing.GroupLayout(panelUp);
        panelUp.setLayout(panelUpLayout);
        panelUpLayout.setHorizontalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelCaption, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
        );
        panelUpLayout.setVerticalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelCaption, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        panelBottom.setBorder(new javax.swing.border.MatteBorder(null));
        panelBottom.setEndPoint(new java.awt.Point(0, 100));
        panelBottom.setName("panelBottom"); // NOI18N
        panelBottom.setOpaque(false);
        panelBottom.setStartColor(resourceMap.getColor("panelBottom.startColor")); // NOI18N

        jButton2.setFont(resourceMap.getFont("jButton2.font")); // NOI18N
        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        jButton2.setFocusPainted(false);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        buttonBack.setFont(resourceMap.getFont("buttonBack.font")); // NOI18N
        buttonBack.setIcon(resourceMap.getIcon("buttonBack.icon")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
        buttonBack.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonBack.setFocusPainted(false);
        buttonBack.setName("buttonBack"); // NOI18N
        buttonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackActionPerformed(evt);
            }
        });

        buttonInRoot.setFont(resourceMap.getFont("buttonInRoot.font")); // NOI18N
        buttonInRoot.setIcon(resourceMap.getIcon("buttonInRoot.icon")); // NOI18N
        buttonInRoot.setText(resourceMap.getString("buttonInRoot.text")); // NOI18N
        buttonInRoot.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonInRoot.setFocusPainted(false);
        buttonInRoot.setName("buttonInRoot"); // NOI18N
        buttonInRoot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInRootActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonInRoot, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                    .addComponent(buttonBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonInRoot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelMain.setBackground(resourceMap.getColor("panelMain.background")); // NOI18N
        panelMain.setBorder(new javax.swing.border.MatteBorder(null));
        panelMain.setName("panelMain"); // NOI18N
        panelMain.setOpaque(false);

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 571, Short.MAX_VALUE)
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 462, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelUp, javax.swing.GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
            .addComponent(panelBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelAllLayout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(250, 250, 250))
        );
        panelAllLayout.setVerticalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAllLayout.createSequentialGroup()
                .addComponent(panelUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
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
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FResponseDialog.class);
        LabelCaption.setText(resourceMap.getString("LabelCaption.text")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N

        buttonInRoot.setText(resourceMap.getString("buttonInRoot.text")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        result = null;
        if (clockBack.isActive()) {
            clockBack.stop();
        }
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void buttonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackActionPerformed
        if (level.isLeaf() || level.getChildCount() == 1) {
            if (level.getParent() != null) {
                showLevel(level.getParent());
            }
        } else {
            showLevel(preLevel);
        }
        if (respDialog.clockBack.isActive()) {
            respDialog.clockBack.stop();
        }
        respDialog.clockBack.start();
    }//GEN-LAST:event_buttonBackActionPerformed

    private void buttonInRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInRootActionPerformed
        showLevel(root);
        if (respDialog.clockBack.isActive()) {
            respDialog.clockBack.stop();
        }
        respDialog.clockBack.start();
    }//GEN-LAST:event_buttonInRootActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelCaption;
    private javax.swing.JButton buttonBack;
    private javax.swing.JButton buttonInRoot;
    private javax.swing.JButton jButton2;
    private ru.apertum.qsystem.client.model.QPanel panelAll;
    private ru.apertum.qsystem.client.model.QPanel panelBottom;
    private ru.apertum.qsystem.client.model.QPanel panelMain;
    private ru.apertum.qsystem.client.model.QPanel panelUp;
    // End of variables declaration//GEN-END:variables
}
