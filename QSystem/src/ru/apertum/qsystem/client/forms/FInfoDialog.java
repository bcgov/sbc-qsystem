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
import org.jdesktop.application.Action;
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
import ru.apertum.qsystem.server.model.infosystem.QInfoItem;

/**
 * Created on 18.09.2009, 11:33:46 Диалог постановки в очередь по коду предварительной регистрации Имеет метод для осуществления всех действий. Вся логика
 * инкапсулирована в этом классе. Должен уметь работать с комовским сканером.
 *
 * @author Evgeniy Egorov
 */
public class FInfoDialog extends javax.swing.JDialog {

    private static FInfoDialog infoDialog;

    /**
     * Creates new form FStandAdvance
     *
     * @param parent
     * @param modal
     */
    public FInfoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        infoDialog = this;
        initComponents();
        if (WelcomeParams.getInstance().btnFont != null) {
            buttonBack.setFont(WelcomeParams.getInstance().btnFont);
            buttonInRoot.setFont(WelcomeParams.getInstance().btnFont);
            buttonPrint.setFont(WelcomeParams.getInstance().btnFont);
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

    public static void setRoot(QInfoItem rootEl) {
        root = rootEl;
        preLevel = rootEl;
    }
    private static Long result = null;
    private static int delay = 10000;
    /**
     * Корень справочной системы
     */
    private static QInfoItem root;
    /**
     * Предыдущий уровень кнопок
     */
    private static QInfoItem preLevel;
    /**
     * Текущий уровень кнопок
     */
    private static QInfoItem level;
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
     * @param respList XML-дерево информации
     * @param modal модальный диалог или нет
     * @param fullscreen растягивать форму на весь экран и прятать мышку или нет
     * @param delay задержка перед скрытием диалога. если 0, то нет автозакрытия диалога
     * @return XML-описание результата предварительной записи, по сути это номерок. если null, то отказались от предварительной записи
     */
    public static Long showInfoDialog(Frame parent, QInfoItem respList, boolean modal, boolean fullscreen, int delay) {
        FInfoDialog.delay = delay;
        QLog.l().logger().info("Чтение информации");

        if (infoDialog == null) {
            infoDialog = new FInfoDialog(parent, modal);
            infoDialog.setTitle(getLocaleMessage("dialog.title"));
        }
        infoDialog.changeTextToLocale();
        FInfoDialog.setRoot(respList);
        FInfoDialog.result = null;
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
        infoDialog.LabelCaption2.setText(respList.getHTMLText());

        infoDialog.showLevel(FInfoDialog.root);
        if (infoDialog.clockBack.isActive()) {
            infoDialog.clockBack.stop();
        }
        if (infoDialog.clockBack.getInterval() > 1000) {
            infoDialog.clockBack.start();
        }

        infoDialog.setVisible(true);
        return result;
    }

    private void showLevel(QInfoItem level) {
        panelMain.removeAll();
        panelMain.repaint();
        if (level.getParent() == null && level != root) {
            level.setParent(FInfoDialog.level);
        }
        FInfoDialog.level = level;
        buttonPrint.setVisible((level.isLeaf() && level.getTextPrint() != null && !level.getTextPrint().isEmpty())
                || (level.getChildCount() == 1 && level.getChildren().getFirst().getTextPrint() != null && !level.getChildren().getFirst().getTextPrint().isEmpty()));
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
            int cols = 3;
            int rows = 5;
            if (level.getChildCount() < 4) {
                cols = 1;
                rows = 3;
            }
            if (level.getChildCount() > 3 && level.getChildCount() < 11) {
                cols = 2;
                rows = Math.round(new Float(level.getChildCount()) / 2);
            }
            if (level.getChildCount() > 10) {
                cols = 3;
                rows = Math.round(new Float(0.3) + level.getChildCount() / 3);
            }
            panelMain.setLayout(new GridLayout(rows, cols, delta / 2, delta / 2));
            for (QInfoItem item : level.getChildren()) {
                final InfoButton button = new InfoButton(item, WelcomeParams.getInstance().buttonType);
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
    private Image background;
    private final static HashMap<String, Image> imgs = new HashMap<>();

    /**
     * Кнопка для навигации по дереву информации и для вывода этой информации ей в капшен
     */
    private class InfoButton extends JButton {

        final QInfoItem el;

        public InfoButton(final QInfoItem el, String resourceName) {
            this.el = el;
            setText(Uses.prepareAbsolutPathForImg(el.getHTMLText()));
            setBorder(new LineBorder(Color.GRAY, 10));
            addActionListener((ActionEvent e) -> {
                if (!el.isLeaf() || (el.isLeaf() && el.getTextPrint() != null && !"".equals(el.getTextPrint()))) {
                    infoDialog.showLevel(el);
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
        LabelCaption2 = new javax.swing.JLabel();
        panelBottom = new ru.apertum.qsystem.client.model.QPanel();
        jButton2 = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonInRoot = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonBack = new QButton(WelcomeParams.getInstance().servButtonType);
        buttonPrint = new QButton(WelcomeParams.getInstance().servButtonType);
        panelMain = new ru.apertum.qsystem.client.model.QPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setUndecorated(true);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FInfoDialog.class);
        panelAll.setBackground(resourceMap.getColor("panelAll.background")); // NOI18N
        panelAll.setName("panelAll"); // NOI18N

        panelUp.setBorder(new javax.swing.border.MatteBorder(null));
        panelUp.setCycle(java.lang.Boolean.FALSE);
        panelUp.setEndColor(resourceMap.getColor("panelUp.endColor")); // NOI18N
        panelUp.setEndPoint(new java.awt.Point(0, 70));
        panelUp.setName("panelUp"); // NOI18N
        panelUp.setOpaque(false);
        panelUp.setPreferredSize(new java.awt.Dimension(1026, 150));
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
            .addComponent(LabelCaption2, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
        );
        panelUpLayout.setVerticalGroup(
            panelUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelCaption2, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        panelBottom.setBorder(new javax.swing.border.MatteBorder(null));
        panelBottom.setEndPoint(new java.awt.Point(0, 100));
        panelBottom.setName("panelBottom"); // NOI18N
        panelBottom.setOpaque(false);
        panelBottom.setStartColor(resourceMap.getColor("panelBottom.startColor")); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(FInfoDialog.class, this);
        jButton2.setAction(actionMap.get("exit")); // NOI18N
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

        buttonPrint.setAction(actionMap.get("printInfo")); // NOI18N
        buttonPrint.setFont(resourceMap.getFont("buttonPrint.font")); // NOI18N
        buttonPrint.setIcon(resourceMap.getIcon("buttonPrint.icon")); // NOI18N
        buttonPrint.setText(resourceMap.getString("buttonPrint.text")); // NOI18N
        buttonPrint.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        buttonPrint.setFocusPainted(false);
        buttonPrint.setName("buttonPrint"); // NOI18N

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addComponent(buttonPrint)
                .addGap(18, 18, 18)
                .addComponent(buttonInRoot)
                .addGap(18, 18, 18)
                .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonBack, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(buttonInRoot, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                    .addComponent(buttonPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
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
            .addGap(0, 1010, Short.MAX_VALUE)
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelAllLayout = new javax.swing.GroupLayout(panelAll);
        panelAll.setLayout(panelAllLayout);
        panelAllLayout.setHorizontalGroup(
            panelAllLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelUp, javax.swing.GroupLayout.DEFAULT_SIZE, 1032, Short.MAX_VALUE)
            .addComponent(panelBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelAllLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
        final org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FInfoDialog.class);
        LabelCaption2.setText(resourceMap.getString("LabelCaption2.text")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        buttonInRoot.setText(resourceMap.getString("buttonInRoot.text")); // NOI18N
        buttonBack.setText(resourceMap.getString("buttonBack.text")); // NOI18N
        buttonPrint.setText(resourceMap.getString("buttonPrint.text")); // NOI18N
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        /* result = null;
         if (clockBack.isActive()) {
         clockBack.stop();
         }
         setVisible(false);*/
    }//GEN-LAST:event_jButton2ActionPerformed

    private void buttonInRootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInRootActionPerformed
        showLevel(root);
        if (infoDialog.clockBack.isActive()) {
            infoDialog.clockBack.stop();
        }
        infoDialog.clockBack.start();
    }//GEN-LAST:event_buttonInRootActionPerformed

    private void buttonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackActionPerformed
        if (level.isLeaf() || level.getChildCount() == 1) {
            if (level.getParent() != null) {
                showLevel(level.getParent());
            }
        } else {
            showLevel(preLevel);
        }
        if (infoDialog.clockBack.isActive()) {
            infoDialog.clockBack.stop();
        }
        infoDialog.clockBack.start();
    }

    @Action
    public void exit() {
        result = null;
        if (clockBack.isActive()) {
            clockBack.stop();
        }
        setVisible(false);
    }//GEN-LAST:event_buttonBackActionPerformed

    @Action
    public void printInfo() {
        QLog.l().logger().info("Печать информации");
        // Узнать, есть ли информация для печати
        final String txt = level.isRoot() && level.getChildCount() == 1 ? level.getChildAt(0).getTextPrint() : level.getTextPrint();
        if (txt != null && !txt.isEmpty()) {
            FWelcome.printPreInfoText(txt);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelCaption2;
    private javax.swing.JButton buttonBack;
    private javax.swing.JButton buttonInRoot;
    private javax.swing.JButton buttonPrint;
    private javax.swing.JButton jButton2;
    private ru.apertum.qsystem.client.model.QPanel panelAll;
    private ru.apertum.qsystem.client.model.QPanel panelBottom;
    private ru.apertum.qsystem.client.model.QPanel panelMain;
    private ru.apertum.qsystem.client.model.QPanel panelUp;
    // End of variables declaration//GEN-END:variables
}
