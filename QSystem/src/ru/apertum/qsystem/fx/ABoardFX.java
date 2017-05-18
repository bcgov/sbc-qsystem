/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.fx;

//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import javafx.scene.Group;
//import javafx.scene.Scene;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import ru.apertum.qsystem.common.QConfig;

/**
 *
 * @author Евгений
 */
public abstract class ABoardFX extends JPanel /*implements IClientboardFX*/ {

    //private static JFXPanel javafxPanel;

    private void initFXscene() {
        //Create the scene and the stage
/*todo
        Platform.runLater(new Runnable() {

            public void run() {
                Group root = new Group();
                Scene scene = new Scene(root);
                createJavaFXContent(root);
                javafxPanel.setScene(scene);
            }
        });
         * 
         */
    }

 //todo   abstract void createJavaFXContent(Group root);
    protected int win_x = 0;
    protected int win_y = 0;
    protected int win_w = 0;
    protected int win_h = 0;
    protected FileBasedConfiguration cfg = new PropertiesConfiguration();

    /**
     */
    public void showBoard() {
        if (0 != cfg.getInt("device", 0)) {

            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            int i = 1;
            for (GraphicsDevice graphicsDevice : screenDevices) {
                System.out.println("graphicsDevice = " + graphicsDevice.getIDstring() + " " + graphicsDevice.toString()
                        + "\nРазрешение экрана " + graphicsDevice.getDefaultConfiguration().getBounds().height + "x" + graphicsDevice.getDefaultConfiguration().getBounds().width
                        + "\nГлубина цвета " + graphicsDevice.getDisplayMode().getBitDepth()
                        + "\nЧастота " + graphicsDevice.getDisplayMode().getRefreshRate()
                        + "\nНачало координат " + graphicsDevice.getDefaultConfiguration().getBounds().x
                        + "-" + graphicsDevice.getDefaultConfiguration().getBounds().y);
                if (i == cfg.getInt("device")) {
                    win_x = graphicsDevice.getDefaultConfiguration().getBounds().x;
                    win_y = graphicsDevice.getDefaultConfiguration().getBounds().y;
                    win_w = graphicsDevice.getDefaultConfiguration().getBounds().width;
                    win_h = graphicsDevice.getDefaultConfiguration().getBounds().height;
                }
                i++;
            }


            SwingUtilities.invokeLater(() -> {
                initAndShowGUI();
            });
        }
    }

    private void initAndShowGUI() {
        JFrame w = new JFrame();
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (!QConfig.cfg().isDebug()) {
            w.setUndecorated(true);
        }
        // Create JavaFX panel.
        /* todo
        javafxPanel = new JFXPanel();
        javafxPanel.setPreferredSize(new Dimension(win_w, win_h));
        w.getContentPane().add(javafxPanel, BorderLayout.CENTER);
         * 
         */

        initFXscene();

        // Show frame.
        w.pack();
        w.setLocationRelativeTo(null);
        w.setVisible(true);
        if (QConfig.cfg().isDebug()) {
            w.setBounds(100, 100, 1024, 768);
        } else {
            w.setBounds(win_x, win_y, win_w, win_h);
            w.setAlwaysOnTop(true);
        }
    }
}
