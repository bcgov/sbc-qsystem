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
 * FTestFX.java
 *
 * Created on Jul 11, 2013, 8:50:48 PM
 */
package ru.apertum.qsystem.client.forms;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.util.Date;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
//import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import netscape.javascript.JSObject;
import ru.apertum.qsystem.common.BrowserFX;

/**
 *
 * @author Evgeniy Egorov
 */
public class FTestFX extends javax.swing.JFrame {

    final static String tt = "This is a text sample В 1962 году американцы запустили первый космический аппарат для изучения Венеры Маринер-1, потерпевший аварию через несколько минут после старта. Сначала на аппарате отказала антенна, которая получала сигнал от наводящей системы с Земли, после чего управление взял на себя бортовой компьютер. Он тоже не смог исправить отклонение от курса, так как загруженная в него программа содержала единственную ошибку — при переносе инструкций в код для перфокарт в одном из уравнений была пропущена чёрточка над буквой, отсутствие которой коренным образом поменяло математический смысл уравнения. Журналисты вскоре окрестили эту чёрточку «самым дорогим дефисом в истории» (в пересчёте на сегодняшний день стоимость утерянного аппарата составляет 135 000 000 $).";

    /**
     * Creates new form FTestFX
     */
    public FTestFX() {
        initComponents();
        setLocation(500, 300);
        setFX();

        /*
         GridLayout gl = new GridLayout(1, 1);
         qPanel1.setLayout(gl);
         BrowserFX bfx = new BrowserFX();
         qPanel1.add(bfx, BorderLayout.CENTER);
         bfx.load("http://yandex.ru");
         */
    }
    private static JFXPanel javafxPanel;
    private static JFXPanel javafxPanelR;

    private void setFX() {

        javafxPanelR = new JFXPanel();
        javafxPanelR.setOpaque(true);
        //javafxPanel.setBackground(java.awt.Color.red);
        javafxPanelR.setBorder(new BevelBorder(1));
        javafxPanelR.setLayout(new GridLayout(1, 1));
        GridLayout glR = new GridLayout(1, 1);
        panel.setLayout(glR);
        javafxPanelR.setBackground(java.awt.Color.red);
        panel.add(javafxPanelR, BorderLayout.CENTER);

        javafxPanel = new JFXPanel();
        javafxPanel.setOpaque(false);
        //javafxPanel.setBackground(java.awt.Color.red);
        javafxPanel.setBorder(new BevelBorder(1));
        javafxPanel.setLayout(new GridLayout(1, 1));
        GridLayout gl = new GridLayout(1, 1);
        qPanel1.setLayout(gl);
        qPanel1.add(javafxPanel, BorderLayout.CENTER);
        javafxPanel.setBackground(java.awt.Color.red);

        Platform.runLater(new Runnable() {
            private Path generateCurvyPath() {
                final Path path = new Path();
                path.getElements().add(new MoveTo(970, 70));
                //path.getElements().add(new CubicCurveTo(430, 0, 430, 120, 250, 120));
               // path.getElements().add(new CubicCurveTo(50, 120, 50, 240, 430, 240));
                path.getElements().add(new LineTo(0, 70));
             //   path.setOpacity(0.0);
                return path;
            }

            private PathTransition generatePathTransition(
                    final Shape shape, final Path path,
                    final Duration duration, final Duration delay,
                    final OrientationType orientation) {
                final PathTransition pathTransition = new PathTransition();
                pathTransition.setDuration(duration);
                pathTransition.setDelay(delay);
                pathTransition.setPath(path);
                pathTransition.setNode(shape);
                pathTransition.setOrientation(orientation);
                pathTransition.setCycleCount(Timeline.INDEFINITE);
                //pathTransition.setAutoReverse(true);
                return pathTransition;
            }

            @Override
            public void run() {
                Group root = new Group();
                //Scene scene = new Scene(root);
                final Browser bro = new Browser();
                Scene scene = new Scene(bro, 750, 500, Color.web("#666970"));
                bro.load("http://google.com");
                javafxPanel.setScene(scene);
                scene.setFill(new Color(1, 1, 1, 0.8));
                //createJavaFXContent(root);

                Group root2 = new Group();
                //Scene scene = new Scene(root);
                //final Text txt = new Text("asgr g sdfgh dsgh dfgh dfgh dfhg");
                final Text txt = null;
                        //TextBuilder.create().text("RMOUG").x(20).y(120).fill(Color.DARKGRAY)
                       // .font(Font.font(java.awt.Font.SERIF, 155))
                       // .effect(new Glow(0.25)).build();
                
                root2.getChildren().add(txt);
                Scene scene2 = new Scene(root2, 750, 500, Color.web("#666970"));

                javafxPanelR.setScene(scene2);
                scene2.setFill(Color.BLUEVIOLET);

                final Path path = generateCurvyPath();
                root2.getChildren().add(path);

                final PathTransition rmougTransition
                        = generatePathTransition(
                                txt, path, Duration.seconds(8.0), Duration.seconds(0.5),
                                OrientationType.NONE);
                
                rmougTransition.play();
            }

            private void createJavaFXContent(Group root) {

                //Rectangle background = new Rectangle(0, 0, 100, 200);
                //background.setFill(Color.BLACK);
                //root.getChildren().add(background);
                final Browser bro = new Browser();
                root.getChildren().add(bro);
                bro.load("http://google.com");

                final WebView wv = new WebView();
                // hide webview scrollbars whenever they appear.
                wv.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {

                    @Override
                    public void onChanged(Change<? extends Node> change) {
                        Set<Node> deadSeaScrolls = wv.lookupAll(".scroll-bar");
                        for (Node scroll : deadSeaScrolls) {
                            scroll.setVisible(false);
                        }
                    }
                });
                wv.getEngine().load("http://google.com");
                //root.getChildren().add(wv);

                // Start the text animation
                //   TranslateTransition transTransition = TranslateTransitionBuilder.create().
                //                   duration(new Duration(7500)).node(t).fromX(0).fromY(0).toX(-1820).onFinished(onFinished).
                //                   interpolator(Interpolator.EASE_BOTH).cycleCount(Timeline.INDEFINITE).build();
                //       transTransition.play();
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        qPanel1 = new ru.apertum.qsystem.client.model.QPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FTestFX.class);
        panel.setBackground(resourceMap.getColor("panel.background")); // NOI18N
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panel.setName("panel"); // NOI18N

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
        );

        qPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        qPanel1.setBackgroundImgage(resourceMap.getString("qPanel1.backgroundImgage")); // NOI18N
        qPanel1.setName("qPanel1"); // NOI18N

        javax.swing.GroupLayout qPanel1Layout = new javax.swing.GroupLayout(qPanel1);
        qPanel1.setLayout(qPanel1Layout);
        qPanel1Layout.setHorizontalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 996, Short.MAX_VALUE)
        );
        qPanel1Layout.setVerticalGroup(
            qPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 472, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(qPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FTestFX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FTestFX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FTestFX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FTestFX.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FTestFX().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    private ru.apertum.qsystem.client.model.QPanel qPanel1;
    // End of variables declaration//GEN-END:variables

    class Browser extends Region {

        final private WebView browser = new WebView();
        final private WebEngine webEngine = browser.getEngine();

        public Browser() {
            browser.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {

                @Override
                public void onChanged(Change<? extends Node> change) {
                    Set<Node> deadSeaScrolls = browser.lookupAll(".scroll-bar");
                    for (Node scroll : deadSeaScrolls) {
                        scroll.setVisible(false);
                    }
                }
            });
            getChildren().add(browser);
        }

        public void load(String url) {
            webEngine.load(url);
        }

        @Override
        protected void layoutChildren() {
            layoutInArea(browser, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
        }
    }
}
