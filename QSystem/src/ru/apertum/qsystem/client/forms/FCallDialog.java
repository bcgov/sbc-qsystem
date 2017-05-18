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
 * FCallDialog.java
 *
 * Created on Mar 21, 2013, 7:49:55 PM
 */
package ru.apertum.qsystem.client.forms;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.dom4j.Element;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;

/**
 *
 * @author Evgeniy Egorov
 */
public class FCallDialog extends javax.swing.JDialog {

    /**
     * Настройки из xml, что поделаешь, архаизм
     */
    private final Element cfg;

    final private java.awt.Frame prnt;
    private final int delay;

    public void show(String moner, String point) {
        show(moner, point, "", "", "");
    }

    public void show(String nomer, String point, String userTxt, String serviceTxt, String inputed) {
        // запустим поток который ждет номера для показа и является таймером показа
        if (!t.isAlive() || t.isInterrupted()) {
            t.start();
        }
        bq.add(template.replaceAll("(#client)", nomer).replaceAll("(#point)", point).replaceAll("(#user)", userTxt).replaceAll("(#service)", serviceTxt).replaceAll("(#inputed)", inputed));
    }
    final private BlockingQueue<String> bq = new LinkedBlockingQueue<>();

    /**
     * Creates new form FCallDialog
     *
     * @param parent
     * @param modal
     * @param mainElement
     */
    public FCallDialog(java.awt.Frame parent, boolean modal, Element mainElement) {
        super(parent, modal);
        initComponents();
        prnt = parent;
        cfg = mainElement;
        delay = Integer.parseInt(Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_DELAY).get(0).attributeValue(Uses.TAG_BOARD_VALUE)) * 1000;
        init();
    }

    private void init() {
        setLocation(prnt.getX() + Integer.parseInt(Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_X).get(0).attributeValue(Uses.TAG_BOARD_VALUE)),
                prnt.getY() + Integer.parseInt(Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_Y).get(0).attributeValue(Uses.TAG_BOARD_VALUE)));
        setSize(Integer.parseInt(Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_WIDTH).get(0).attributeValue(Uses.TAG_BOARD_VALUE)),
                Integer.parseInt(Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_HEIGHT).get(0).attributeValue(Uses.TAG_BOARD_VALUE)));

        // Нарисуем картинку если надо. Загрузить можно из файла или ресурса
        String resourceName = Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_BACKGROUND).get(0).attributeValue(Uses.TAG_BOARD_VALUE);
        if ("".equals(resourceName) || !new File(resourceName).exists()) {
            resourceName = "/ru/apertum/qsystem/client/forms/resources/fon_call_dialog.jpg";
        }
        panel.setBackgroundImgage(resourceName);
        template = Uses.elementsByAttr(cfg, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_CALL_PANEL_TEMPLATE).get(0).attributeValue(Uses.TAG_BOARD_VALUE);
        template = template.toLowerCase().contains("html") ? template : "<html>" + template;
    }
    private final Image background = null;
    private String template;
    final private Thread t = new Thread(new Runnable() {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                setVisible(false);
                try {
                    label.setText(bq.take());
                    setVisible(true);
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    QLog.l().logger().error("Не дождались получения номера вызванного для показа в диалоге на главном табло. " + ex);
                }
            }
        }
    });

    @Override
    public void paintComponents(Graphics g) {
        if (background != null) {
            //Image scaledImage = background.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH); // это медленный вариант
            final Image scaledImage = resizeToBig(background, getWidth(), getHeight());
            final Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(scaledImage, 0, 0, null, null);
            super.paintComponents(g);
        } else {
            super.paintComponents(g);
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

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new ru.apertum.qsystem.client.model.QPanel();
        label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setFocusable(false);
        setName("Form"); // NOI18N
        setUndecorated(true);
        setResizable(false);

        panel.setName("panel"); // NOI18N

        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FCallDialog.class);
        label.setText(resourceMap.getString("label.text")); // NOI18N
        label.setName("label"); // NOI18N

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    private ru.apertum.qsystem.client.model.QPanel panel;
    // End of variables declaration//GEN-END:variables
}
