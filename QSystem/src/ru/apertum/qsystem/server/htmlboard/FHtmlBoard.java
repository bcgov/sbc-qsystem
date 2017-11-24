/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.htmlboard;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import javax.swing.JFrame;
import ru.apertum.qsystem.common.BrowserFX;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;

/**
 * @author Evgeniy Egorov
 */
public class FHtmlBoard extends javax.swing.JFrame {

    private final BrowserFX bfx = new BrowserFX();
    private final BrowserFX bfxT = new BrowserFX();
    private final BrowserFX bfxB = new BrowserFX();
    private final BrowserFX bfxL = new BrowserFX();
    private final BrowserFX bfxR = new BrowserFX();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelRight;
    private javax.swing.JPanel panelTop;
    private javax.swing.JSplitPane spAll;
    private javax.swing.JSplitPane spBottom;
    private javax.swing.JSplitPane spLeft;
    private javax.swing.JSplitPane spRight;

    /**
     * Creates new form Fbs
     */
    public FHtmlBoard() {
        QLog.l().logger().info("Создаем окно для информации.");
        initComponents();

        if (!QConfig.cfg().isDebug()) {
            debugMode();
        }

        GridLayout gl = new GridLayout(1, 1);
        panelMain.setLayout(gl);
        panelMain.add(bfx, BorderLayout.CENTER);

        if (HtmlBoardProps.getInstance().getTopSize() == 0) {
            panelTop.setVisible(false);
        } else {
            spAll.setDividerLocation(HtmlBoardProps.getInstance().getTopSize());

            GridLayout glT = new GridLayout(1, 1);
            panelTop.setLayout(glT);
            panelTop.add(bfxT, BorderLayout.CENTER);
            bfxT.load(HtmlBoardProps.getInstance().getTopUrl());
        }

        if (HtmlBoardProps.getInstance().getBottomSize() == 0) {
            panelBottom.setVisible(false);
        } else {
            GridLayout glB = new GridLayout(1, 1);
            panelBottom.setLayout(glB);
            panelBottom.add(bfxB, BorderLayout.CENTER);
            bfxB.load(HtmlBoardProps.getInstance().getBottomUrl());
        }
        if (HtmlBoardProps.getInstance().getLeftSize() == 0) {
            panelLeft.setVisible(false);
        } else {
            spLeft.setDividerLocation(HtmlBoardProps.getInstance().getLeftSize());
            GridLayout glL = new GridLayout(1, 1);
            panelLeft.setLayout(glL);
            panelLeft.add(bfxL, BorderLayout.CENTER);
            bfxL.load(HtmlBoardProps.getInstance().getLeftUrl());
        }
        if (HtmlBoardProps.getInstance().getRightSize() == 0) {
            panelRight.setVisible(false);
        } else {
            GridLayout glR = new GridLayout(1, 1);
            panelRight.setLayout(glR);
            panelRight.add(bfxR);
            bfxR.load(HtmlBoardProps.getInstance().getRightUrl());
        }

        QLog.l().logger().trace("Прочитали настройки для окна информации.");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println(ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FHtmlBoard().setVisible(true);
        });
    }

    public BrowserFX getBfx() {
        return bfx;
    }

    public void toPosition(boolean isDebug, int x, int y) {
        // Определим форму на монитор
        setLocation(x, y);
        setAlwaysOnTop(!isDebug);
        //setResizable(isDebug);
        // Отрехтуем форму в зависимости от режима.
        if (!isDebug) {

            setAlwaysOnTop(true);
            //setResizable(false);
            // спрячем курсор мыши
            int[] pixels = new int[16 * 16];
            Image image = Toolkit.getDefaultToolkit()
                .createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
            Cursor transparentCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(image, new Point(0, 0), "invisibleCursor");
            setCursor(transparentCursor);
            addWindowListener(new WindowAdapter() {

                @Override
                public void windowOpened(WindowEvent e) {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });
        } else {
            setSize(1280, 720);
        }
    }

    public void loadContent(String cnt) {
        bfx.loadContent(cnt);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spAll = new javax.swing.JSplitPane();
        panelTop = new javax.swing.JPanel();
        spBottom = new javax.swing.JSplitPane();
        panelBottom = new javax.swing.JPanel();
        spLeft = new javax.swing.JSplitPane();
        panelLeft = new javax.swing.JPanel();
        spRight = new javax.swing.JSplitPane();
        panelMain = new javax.swing.JPanel();
        panelRight = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        spAll.setDividerLocation(150);
        spAll.setDividerSize(0);
        spAll.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout panelTopLayout = new javax.swing.GroupLayout(panelTop);
        panelTop.setLayout(panelTopLayout);
        panelTopLayout.setHorizontalGroup(
            panelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 598, Short.MAX_VALUE)
        );
        panelTopLayout.setVerticalGroup(
            panelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 100, Short.MAX_VALUE)
        );

        spAll.setTopComponent(panelTop);

        spBottom.setBorder(new javax.swing.border.MatteBorder(null));
        spBottom.setDividerLocation(350);
        spBottom.setDividerSize(0);
        spBottom.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 596, Short.MAX_VALUE)
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 112, Short.MAX_VALUE)
        );

        spBottom.setBottomComponent(panelBottom);

        spLeft.setBorder(new javax.swing.border.MatteBorder(null));
        spLeft.setDividerLocation(150);
        spLeft.setDividerSize(0);

        javax.swing.GroupLayout panelLeftLayout = new javax.swing.GroupLayout(panelLeft);
        panelLeft.setLayout(panelLeftLayout);
        panelLeftLayout.setHorizontalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 149, Short.MAX_VALUE)
        );
        panelLeftLayout.setVerticalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 347, Short.MAX_VALUE)
        );

        spLeft.setLeftComponent(panelLeft);

        spRight.setBorder(new javax.swing.border.MatteBorder(null));
        spRight.setDividerLocation(300);
        spRight.setDividerSize(0);

        panelMain.setBorder(new javax.swing.border.MatteBorder(null));

        javax.swing.GroupLayout panelMainLayout = new javax.swing.GroupLayout(panelMain);
        panelMain.setLayout(panelMainLayout);
        panelMainLayout.setHorizontalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 297, Short.MAX_VALUE)
        );
        panelMainLayout.setVerticalGroup(
            panelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 343, Short.MAX_VALUE)
        );

        spRight.setLeftComponent(panelMain);

        javax.swing.GroupLayout panelRightLayout = new javax.swing.GroupLayout(panelRight);
        panelRight.setLayout(panelRightLayout);
        panelRightLayout.setHorizontalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 144, Short.MAX_VALUE)
        );
        panelRightLayout.setVerticalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 345, Short.MAX_VALUE)
        );

        spRight.setRightComponent(panelRight);

        spLeft.setRightComponent(spRight);

        spBottom.setLeftComponent(spLeft);

        spAll.setRightComponent(spBottom);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spAll)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(spAll, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(
        java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if (HtmlBoardProps.getInstance().getBottomSize() != 0) {
            spBottom
                .setDividerLocation(
                    spBottom.getHeight() - HtmlBoardProps.getInstance().getBottomSize());
        }
        if (HtmlBoardProps.getInstance().getRightSize() != 0) {
            spRight.setDividerLocation(
                spRight.getWidth() - HtmlBoardProps.getInstance().getRightSize());
        }
    }//GEN-LAST:event_formComponentResized
    // End of variables declaration//GEN-END:variables

    private void debugMode() {
        dispose();
        setUndecorated(true);
        setType(Type.UTILITY);
    }
}
