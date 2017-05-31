/*
 * Copyright (C) 2011 Евгений
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
package ru.apertum.qsystem.fx;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
/*
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
 * 
 */
import javax.swing.Timer;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author Евгений
 */
public class OrangeMainboard extends ABoardFX {

    @Override
    public void showBoard() {
        File paramFile = new File("config/mainboardfx.properties");
        try {
            final FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(new FileBasedBuilderParametersImpl().setFile(paramFile).setEncoding("utf8"));
            cfg = builder.getConfiguration();
        } catch (ConfigurationException ex) {
            QLog.l().logger().error("Не загружен файл конфигурации " + paramFile.getAbsolutePath(), ex);
            throw new ServerException("Не загружен файл конфигурации " + paramFile.getAbsolutePath());
        }

        super.showBoard();
    }
    /*todo

    @Override
    void createJavaFXContent(Group root) {
        if (0 == cfg.getInt("device", 0)) {
            return;
        }
        Rectangle background = new Rectangle(0, 0, win_w, win_h);
        background.setFill(Color.BLACK);
        root.getChildren().add(background);


        Rectangle rec_logo = new Rectangle(win_w * 0.025, win_h * 0.025, win_w * 0.075, win_w * 0.075);
        rec_logo.setFill(Color.rgb(255, 138, 22));
        root.getChildren().add(rec_logo);

        Rectangle rec_num = new Rectangle(win_w * 0.025, win_h * 0.2, win_w * 0.656, win_h * 0.756);
        rec_num.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(50, 50, 50)),
                    new Stop(1, Color.rgb(90, 90, 90))}));
        rec_num.setArcHeight(30);
        rec_num.setArcWidth(30);
        root.getChildren().add(rec_num);

        Rectangle rec = new Rectangle(win_w * 0.025, win_h * 0.20, win_w * 0.656, win_h * 0.089);
        rec.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(242, 113, 0)),
                    new Stop(1, Color.rgb(252, 198, 0))}));
        rec.setArcHeight(30);
        rec.setArcWidth(30);

        Rectangle rec_in = new Rectangle(win_w * 0.025 + 5, win_h * 0.20 + 5, win_w * 0.656 - 10, win_h * 0.089 - 10);
        rec_in.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT, new Stop[]{
                    new Stop(0, Color.rgb(250, 220, 190)),
                    //   new Stop(0, Color.rgb(250, 0, 0)),
                    new Stop(0.40, Color.rgb(243, 152, 0)),
                    new Stop(0.60, Color.rgb(239, 128, 0)),
                    new Stop(0.70, Color.rgb(239, 128, 0)),
                    new Stop(1, Color.rgb(248, 174, 0))
                //     new Stop(1, Color.rgb(0, 174, 0))
                }));
        rec_in.setArcHeight(20);
        rec_in.setArcWidth(20);

        root.getChildren().add(rec);
        root.getChildren().add(rec_in);


        Rectangle rec_next = new Rectangle(win_w * 0.713, win_h * 0.2, win_w * 0.263, win_h * 0.5);
        rec_next.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(50, 50, 50)),
                    new Stop(1, Color.rgb(90, 90, 90))}));
        rec_next.setArcHeight(30);
        rec_next.setArcWidth(30);
        root.getChildren().add(rec_next);

        Rectangle rec_nxt = new Rectangle(win_w * 0.713, win_h * 0.20, win_w * 0.263, win_h * 0.089);
        rec_nxt.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(56, 55, 59)),
                    new Stop(1, Color.rgb(137, 135, 140))}));
        rec_nxt.setArcHeight(30);
        rec_nxt.setArcWidth(30);

        Rectangle rec_nxt_in = new Rectangle(win_w * 0.713 + 5, win_h * 0.20 + 5, win_w * 0.263 - 10, win_h * 0.089 - 10);
        rec_nxt_in.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT, new Stop[]{
                    new Stop(0, Color.rgb(195, 197, 200)),
                    //   new Stop(0, Color.rgb(250, 0, 0)),
                    new Stop(0.35, Color.rgb(85, 85, 85)),
                    new Stop(0.40, Color.rgb(70, 70, 70)),
                    new Stop(0.470, Color.rgb(60, 60, 60)),
                    new Stop(0.550, Color.rgb(60, 60, 60)),
                    new Stop(0.80, Color.rgb(85, 84, 88)),
                    new Stop(1, Color.rgb(124, 122, 127))
                //     new Stop(1, Color.rgb(0, 174, 0))
                }));
        rec_nxt_in.setArcHeight(20);
        rec_nxt_in.setArcWidth(20);

        root.getChildren().add(rec_nxt);
        root.getChildren().add(rec_nxt_in);




        Rectangle rec_data = new Rectangle(win_w * 0.713, win_h * 0.756, win_w * 0.263, win_h * 0.2);
        rec_data.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(50, 50, 50)),
                    new Stop(1, Color.rgb(90, 90, 90))}));
        rec_data.setArcHeight(30);
        rec_data.setArcWidth(30);
        root.getChildren().add(rec_data);

        Rectangle rec_dt = new Rectangle(win_w * 0.713, win_h * 0.756, win_w * 0.263, win_h * 0.089);
        rec_dt.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(242, 113, 0)),
                    new Stop(1, Color.rgb(252, 198, 0))}));
        rec_dt.setArcHeight(30);
        rec_dt.setArcWidth(30);

        Rectangle rec_dt_in = new Rectangle(win_w * 0.713 + 5, win_h * 0.756 + 5, win_w * 0.263 - 10, win_h * 0.089 - 10);
        rec_dt_in.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT, new Stop[]{
                    new Stop(0, Color.rgb(250, 220, 190)),
                    //   new Stop(0, Color.rgb(250, 0, 0)),
                    new Stop(0.40, Color.rgb(243, 152, 0)),
                    new Stop(0.60, Color.rgb(239, 128, 0)),
                    new Stop(0.70, Color.rgb(239, 128, 0)),
                    new Stop(1, Color.rgb(248, 174, 0))
                //     new Stop(1, Color.rgb(0, 174, 0))
                }));
        rec_dt_in.setArcHeight(20);
        rec_dt_in.setArcWidth(20);

        root.getChildren().add(rec_dt);
        root.getChildren().add(rec_dt_in);


        Label lab_caption = new Label("состояние очереди");
        Font font = new Font("verdana", 110);
        lab_caption.setTextFill(Color.WHITESMOKE);
        lab_caption.fontProperty().setValue(font);
        lab_caption.setLayoutX(win_w * 0.125);
        lab_caption.setLayoutY(0);
        lab_caption.setPrefHeight(win_h * 0.18);
        lab_caption.setPrefWidth(win_w * 1);
        lab_caption.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(lab_caption);

        Label lab_logo = new Label("orange");
        Font font_logo = new Font("Serif", 28);
        lab_logo.setTextFill(Color.WHITESMOKE);
        lab_logo.fontProperty().setValue(font_logo);
        lab_logo.setLayoutX(win_w * 0.025);
        lab_logo.setLayoutY(win_h * 0.025);
        lab_logo.setPrefHeight(win_w * 0.07);
        lab_logo.setPrefWidth(win_w * 0.075);
        lab_logo.setAlignment(Pos.BOTTOM_CENTER);
        root.getChildren().add(lab_logo);

        Label lab_num = new Label("обслуживаемые в данный момент клиенты");
        Font font_num = new Font("Serif", 37);
        lab_num.setTextFill(Color.WHITESMOKE);
        lab_num.fontProperty().setValue(font_num);
        lab_num.setLayoutX(win_w * 0.025);
        lab_num.setLayoutY(win_h * 0.2);
        lab_num.setPrefHeight(win_h * 0.089);
        lab_num.setPrefWidth(win_w * 0.656);
        lab_num.setAlignment(Pos.CENTER);
        root.getChildren().add(lab_num);

        Label lab_next = new Label("ближайшие клиенты");
        Font font_next = new Font("Miriam", 34);
        lab_next.setTextFill(Color.WHITESMOKE);
        lab_next.fontProperty().setValue(font_next);
        lab_next.setLayoutX(win_w * 0.717);
        lab_next.setLayoutY(win_h * 0.21);
        lab_next.setPrefHeight(win_h * 0.089);
        lab_next.setPrefWidth(win_w * 0.656);
        lab_next.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(lab_next);

        final Label lab_date = new Label("20 Сентября 2011");
        Font font_date = new Font("Serif", 36);
        lab_date.setTextFill(Color.WHITESMOKE);
        lab_date.fontProperty().setValue(font_date);
        lab_date.setLayoutX(win_w * 0.717);
        lab_date.setLayoutY(win_h * 0.756);
        lab_date.setPrefHeight(win_h * 0.089);
        lab_date.setPrefWidth(win_w * 0.656);
        lab_date.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(lab_date);

        final Label lab_time = new Label("22:15");
        Font font_time = new Font("Serif", 60);
        lab_time.setTextFill(Color.WHITESMOKE);
        lab_time.fontProperty().setValue(font_time);
        lab_time.setLayoutX(win_w * 0.727);
        lab_time.setLayoutY(win_h * 0.85);
        lab_time.setPrefHeight(win_h * 0.089);
        lab_time.setPrefWidth(win_w * 0.656);
        lab_time.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(lab_time);

        //  win_w * 0.025, win_h * 0.20, win_w * 0.656, win_h * 0.089
        double k = 66.7 / 8 / 100;
        for (int i = 0; i < 3; i++) {
            Rectangle rec_n = new Rectangle(win_w * 0.025, win_h * 0.20 + win_h * 0.089 + (i * 2 + 1) * win_h * k,
                    win_w * 0.656, win_h * k);
            rec_n.setFill(Color.rgb(70 + i * 10, 70 + i * 10, 70 + i * 10));
            root.getChildren().add(rec_n);
        }
        int ii = 3;
        Rectangle rec_nl = new Rectangle(win_w * 0.025, win_h * 0.20 + win_h * 0.089 + (ii * 2) * win_h * k,
                win_w * 0.656, win_h * k);
        rec_nl.setFill(Color.rgb(70, 70, 70));
        root.getChildren().add(rec_nl);
        for (int i = 0; i < 8; i++) {

            Label user = new Label("C1");
            Font font_user = new Font("Serif", 55);
            user.setTextFill(Color.WHITESMOKE);
            user.fontProperty().setValue(font_user);
            user.setLayoutX(win_w * 0.025 + 20);
            user.setLayoutY(win_h * 0.20 + win_h * 0.089 + (i) * win_h * k - 2);
            user.setPrefHeight(win_h * k);
            user.setPrefWidth(win_w * 0.656);
            user.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().add(user);


            Label client = new Label("C4");
            Font font_client = new Font("Serif", 55);
            client.setTextFill(Color.rgb(255, 131, 19));
            client.fontProperty().setValue(font_client);
            client.setLayoutX(win_w * 0.025 + win_w * 0.656 / 2);
            client.setLayoutY(win_h * 0.20 + win_h * 0.089 + (i) * win_h * k - 2);
            client.setPrefHeight(win_h * k);
            client.setPrefWidth(win_w * 0.656 / 2);
            client.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().add(client);
            if (i == 0) {
                Font font_cap = new Font("Serif", 40);
                client.fontProperty().setValue(font_cap);
                user.fontProperty().setValue(font_cap);
                client.setTextFill(Color.WHITESMOKE);
                client.setText("клиент");
                user.setText("консультант");
            } else {
                clients.add(client);
                users.add(user);

                final Timeline timeline_c = new Timeline();
                timeline_c.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, // set start position at 0s
                        new KeyValue(client.opacityProperty(), 1)),
                        new KeyFrame(new Duration(500), // set start position at 0s
                        new KeyValue(client.opacityProperty(), 1)),
                        //   new KeyFrame(new Duration(500), // set start position at 0s
                        //    new KeyValue<Number>(lab_nom.opacityProperty(),  0.5)),
                        new KeyFrame(new Duration(700), // set start position at 0s
                        new KeyValue(client.opacityProperty(), 0.1)),
                        new KeyFrame(new Duration(800), // set end position at 40s
                        new KeyValue(client.opacityProperty(), 0.1)));
                timeline_c.setAutoReverse(true);
                timeline_c.setCycleCount(Animation.INDEFINITE);
                clients_t.add(timeline_c);

                final Timeline timeline_u = new Timeline();
                timeline_u.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO, // set start position at 0s
                        new KeyValue(user.opacityProperty(), 1)),
                        new KeyFrame(new Duration(500), // set start position at 0s
                        new KeyValue(user.opacityProperty(), 1)),
                        //   new KeyFrame(new Duration(500), // set start position at 0s
                        //    new KeyValue<Number>(lab_nom.opacityProperty(),  0.5)),
                        new KeyFrame(new Duration(700), // set start position at 0s
                        new KeyValue(user.opacityProperty(), 0.1)),
                        new KeyFrame(new Duration(800), // set end position at 40s
                        new KeyValue(user.opacityProperty(), 0.1)));
                timeline_u.setAutoReverse(true);
                timeline_u.setCycleCount(Animation.INDEFINITE);
                users_t.add(timeline_u);

            }


        }



        // win_w * 0.713, win_h * 0.20, win_w * 0.263, win_h * 0.089
        for (int i = 0; i < 2; i++) {
            Rectangle rec_n = new Rectangle(win_w * 0.713, win_h * 0.20 + win_h * 0.089 + (i * 2 + 1) * win_h * k,
                    win_w * 0.263, win_h * k);
            rec_n.setFill(Color.rgb(70 + i * 10, 70 + i * 10, 70 + i * 10));
            root.getChildren().add(rec_n);

        }
        for (int i = 0; i < 5; i++) {

            Label user = new Label("");
            Font font_user = new Font("Serif", 55);
            user.setTextFill(Color.rgb(255, 131, 19));
            user.fontProperty().setValue(font_user);
            user.setLayoutX(win_w * 0.713 + 10);
            user.setLayoutY(win_h * 0.20 + win_h * 0.089 + (i) * win_h * k - 2);
            user.setPrefHeight(win_h * k);
            user.setPrefWidth(win_w * 0.263);
            user.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().add(user);


            Label client = new Label("");
            Font font_client = new Font("Serif", 55);
            client.setTextFill(Color.rgb(255, 131, 19));
            client.fontProperty().setValue(font_client);
            client.setLayoutX(win_w * 0.713 + win_w * 0.263 / 2);
            client.setLayoutY(win_h * 0.20 + win_h * 0.089 + (i) * win_h * k - 2);
            client.setPrefHeight(win_h * k);
            client.setPrefWidth(win_w * 0.263);
            client.setAlignment(Pos.CENTER_LEFT);
            root.getChildren().add(client);

            nexts.add(user);
            nexts.add(client);
        }



        Timer timer = new Timer(500, new ActionListener() {

            boolean b = false;
            int day;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                GregorianCalendar gc = new GregorianCalendar();
                lab_time.setText(gc.get(GregorianCalendar.HOUR_OF_DAY) + (b ? ":" : " ") + (gc.get(GregorianCalendar.MINUTE) < 10 ? "0" : "") + gc.get(GregorianCalendar.MINUTE));
                b = !b;
                if (day != gc.get(GregorianCalendar.DAY_OF_MONTH)) {
                    day = gc.get(GregorianCalendar.DAY_OF_MONTH);
                    String m = "";
                    switch (gc.get(GregorianCalendar.MONTH) + 1) {
                        case 1:
                            m = "Января";
                            break;
                        case 2:
                            m = "Февраля";
                            break;
                        case 3:
                            m = "Марта";
                            break;
                        case 4:
                            m = "Апреля";
                            break;
                        case 5:
                            m = "Мая";
                            break;
                        case 6:
                            m = "Июня";
                            break;
                        case 7:
                            m = "Июля";
                            break;
                        case 8:
                            m = "Августа";
                            break;
                        case 9:
                            m = "Сетнября";
                            break;
                        case 10:
                            m = "Октября";
                            break;
                        case 11:
                            m = "Ноября";
                            break;
                        case 12:
                            m = "Декабря";
                            break;
                        default:
                            throw new AssertionError();
                    }
                    lab_date.setText("" + day + " " + m + " " + gc.get(GregorianCalendar.YEAR));
                }
            }
        });
        timer.start();
    }
    // private static Label lab_nom;
    private LinkedList<Label> users = new LinkedList<>();
    private LinkedList<Label> clients = new LinkedList<>();
    private LinkedList<Label> nexts = new LinkedList<>();
    private LinkedList<Timeline> clients_t = new LinkedList<>();
    private LinkedList<Timeline> users_t = new LinkedList<>();

    public void showData(final ArrayList<QUser> forShow, final ArrayList<QUser> forBlink) {
        if (0 == cfg.getInt("device", 0)) {
            return;
        }
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).setText(i < forShow.size() ? forShow.get(i).getCustomer().getPrefix() + forShow.get(i).getCustomer().getNumber() : "");
                    users.get(i).setText(i < forShow.size() ? forShow.get(i).getPoint() : "");
                    if (i < forShow.size() && forBlink.contains(forShow.get(i))) {
                        clients_t.get(i).play();
                        users_t.get(i).play();
                    } else {
                        clients_t.get(i).stop();
                        clients.get(i).opacityProperty().set(1);
                        users_t.get(i).stop();
                        users.get(i).opacityProperty().set(1);
                    }
                }
            }
        });
    }

    public void showNextCustomers(final LinkedList<String> nexts_nums) {
        if (0 == cfg.getInt("device", 0)) {
            return;
        }
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < nexts.size(); i++) {
                    nexts.get(i).setText(i < nexts_nums.size() ? nexts_nums.get(i) : "");
                }
            }
        });
    }
     * 
     */
}
