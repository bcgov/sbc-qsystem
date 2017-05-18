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
package ru.apertum.qsystem.common;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javax.swing.JPanel;
import ru.apertum.qsystem.common.exceptions.ClientException;

/**
 * Может проигрывать фидеофайлы *.mpg, *.jpg. Для этого используется установленная предварительно на компьютере среда JMF. По умолчанию показ ролика бесконечно
 * в цикле.
 *
 * @author Evgeniy Egorov
 */
public class VideoPlayer extends JPanel {

    private MediaView medView = null;

    public VideoPlayer() {
        init();
    }

    private void init() {
        javafxPanel = new JFXPanel();
        javafxPanel.setOpaque(false);
        setOpaque(false);
        GridLayout gl = new GridLayout(1, 1);
        setLayout(gl);
        add(javafxPanel, BorderLayout.CENTER);

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                final Group root = new Group();
                final Scene scene = new Scene(root);
                createJavaFXContent(root);
                javafxPanel.setScene(scene);
                scene.setFill(new Color(0, 0, 0, 0));
            }

            private void createJavaFXContent(Group root) {
                final MediaView view = new MediaView();
                medView = view;

                javafxPanel.addComponentListener(new ComponentListener() {

                    @Override
                    public void componentResized(ComponentEvent e) {
                        if (view.getMediaPlayer() != null && view.getMediaPlayer().getMedia() != null) {
                            Platform.runLater(() -> {
                                double sx = (double) javafxPanel.getWidth() / (double) view.getMediaPlayer().getMedia().widthProperty().getValue();
                                double dxy = sx;
                                if (view.getMediaPlayer().getMedia().heightProperty().getValue() * sx > javafxPanel.getHeight()) {
                                    dxy = (double) javafxPanel.getHeight() / (double) view.getMediaPlayer().getMedia().heightProperty().getValue();
                                }
                                view.setScaleX(dxy);
                                view.setScaleY(dxy);
                                view.setX((javafxPanel.getWidth() - view.getMediaPlayer().getMedia().widthProperty().getValue()) / 2);
                                view.setY((javafxPanel.getHeight() - view.getMediaPlayer().getMedia().heightProperty().getValue()) / 2);
                            });
                        }
                    }

                    @Override
                    public void componentMoved(ComponentEvent e) {
                    }

                    @Override
                    public void componentShown(ComponentEvent e) {
                    }

                    @Override
                    public void componentHidden(ComponentEvent e) {
                    }
                });
                root.getChildren().add(view);
            }
        });

    }
    private static JFXPanel javafxPanel;

    /**
     * Доступ к медиаплееру для детельной настройки параметров.
     *
     * @return медиаплеер
     */
    public MediaView getMediaView() {
        int k = 0;
        while ((medView == null || medView.getMediaPlayer() == null) && k < 30) {
            k++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        if (medView == null) {
            throw new ClientException("MediaPlayer = NULL");
        }
        return medView;
    }
    private final LinkedList<String> videoFiles = new LinkedList<>();
    private String videoResourcePath;

    private String getNextVideoFile() throws FileNotFoundException {
        if (videoFiles.isEmpty()) {
            if (new File(videoResourcePath).exists()) {
                if (new File(videoResourcePath).isDirectory()) {
                    // ролики в папке
                    final String[] vfs = new File(videoResourcePath).list();
                    videoFiles.clear();
                    for (String string : vfs) {
                        videoFiles.addLast(videoResourcePath + (videoResourcePath.substring(videoResourcePath.length() - 1).equals("/") ? "" : "/") + string);
                    }
                } else {
                    // ролик одним файлом
                    videoFiles.clear();
                    videoFiles.add(videoResourcePath);
                }
            } else {
                videoFiles.clear();
                // список роликов в текстовом файле построчно
                try (FileInputStream fis = new FileInputStream(videoResourcePath); Scanner s = new Scanner(fis)) {
                    while (s.hasNextLine()) {
                        final String line = s.nextLine().trim();
                        if (!line.startsWith("#") && new File(line).isFile()) {
                            videoFiles.addLast(line);
                        }
                    }
                } catch (IOException ex) {
                    System.err.println(ex);
                    throw new RuntimeException(ex);
                }
            }
            if (videoFiles.isEmpty()) {
                throw new FileNotFoundException("No video files in \"" + videoResourcePath + "\"");
            }
            //System.out.println("LOAD " + videoFiles);
        }
        //System.out.println("GO " + videoFiles.getFirst());
        return videoFiles.removeFirst();
    }

    public boolean setVideoResource(String videoResourcePath) {
        videoFiles.clear();
        this.videoResourcePath = videoResourcePath;
        return true;
    }

    private final static HashMap<String, MediaPlayer> VIDS = new HashMap<>();

    private synchronized static MediaPlayer getMediaPlayer(String videoFilePath) {
        // продолжает падать видео через несколько часов после старта на нескольких объектах
        // проделаем такое раз в два часа
        // поможет? а кто его знает, я не в курсе, спросите в Оракле.
        if (System.currentTimeMillis() - dropMp > 2 * 60 * 60 * 1000) {
            VIDS.values().forEach(mp -> mp.dispose());
            VIDS.clear();
            dropMp = System.currentTimeMillis();
        }
        if (VIDS.get(videoFilePath) == null) {
            VIDS.put(videoFilePath, new MediaPlayer(new Media(new File(videoFilePath).toURI().toString())));
        }
        return VIDS.get(videoFilePath);
    }

    private static long dropMp = System.currentTimeMillis();

    /**
     * Сначала установи ресурс
     *
     * @param nativePosition если false, то по всему контролу парента
     */
    public void setVideoSize(boolean nativePosition) {
        if (javafxPanel.getComponentListeners().length > 0) {
            javafxPanel.getComponentListeners()[0].componentResized(null);
        }
    }

    private final Runnable changer = new Runnable() {

        @Override
        public void run() {
            boolean mute = getMediaView().getMediaPlayer().isMute();
            getMediaView().getMediaPlayer().stop();
            getMediaView().setMediaPlayer(null);
            try {
                getMediaView().setMediaPlayer(getMediaPlayer(getNextVideoFile()));
            } catch (FileNotFoundException ex) {
                QLog.l().logger().error("No content.", ex);
                return;
            }
            getMediaView().getMediaPlayer().setCycleCount(1);
            getMediaView().getMediaPlayer().setMute(mute);
            getMediaView().getMediaPlayer().setOnEndOfMedia(changer);
            javafxPanel.getComponentListeners()[0].componentResized(null);
            final Timer t = new Timer(true);
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        getMediaView().getMediaPlayer().play();
                        javafxPanel.getComponentListeners()[0].componentResized(null);
                    } catch (Exception npe) {
                        QLog.l().logger().error("Кодак не поддерживается. Codak not supported. Видео должно быть в формате H.264.", npe);
                    }
                }
            }, 500);
        }
    };

    public void start() {

        Platform.runLater(() -> {
            try {
                getMediaView().setMediaPlayer(getMediaPlayer(getNextVideoFile()));
            } catch (FileNotFoundException ex) {
                QLog.l().logger().error("No content.", ex);
                return;
            }
            getMediaView().getMediaPlayer().setCycleCount(videoFiles.size() > 0 ? 1 : 9000000);
            if (videoFiles.size() > 0) {
                getMediaView().getMediaPlayer().setOnEndOfMedia(changer);
            }
            //getMediaView().getMediaPlayer().setMute(true);
            final Timer t = new Timer(true);
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        getMediaView().getMediaPlayer().play();
                        javafxPanel.getComponentListeners()[0].componentResized(null);
                    } catch (Exception npe) {
                        QLog.l().logger().error("Кодак не поддерживается. Codak not supported. Видео должно быть в формате H.264.", npe);
                    }
                }
            }, 1500);

        });

    }

    public void pause() {
        Platform.runLater(() -> {
            getMediaView().getMediaPlayer().pause();
        });
    }

    public void close() {
        Platform.runLater(() -> {
            getMediaView().getMediaPlayer().stop();
            getMediaView().getMediaPlayer().dispose();
            VIDS.clear();
            videoFiles.clear();
        });
    }
}
