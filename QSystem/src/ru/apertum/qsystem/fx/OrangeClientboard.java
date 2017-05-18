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

import java.io.File;
/* todo
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
 * 
 */
import javax.swing.SwingUtilities;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 *
 * @author Евгений
 */
public class OrangeClientboard extends ABoardFX {
    /* todo

    @Override
    void createJavaFXContent(Group root) {
        Rectangle background = new Rectangle(0, 0, win_w, win_h);
        background.setFill(Color.BLACK);
        root.getChildren().add(background);


        Rectangle rec_num = new Rectangle(win_w * 0.244, win_h * 0.203, win_w * 0.719, win_h * 0.617);
        rec_num.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(46, 46, 50)),
                    new Stop(1, Color.rgb(91, 90, 95))}));
        rec_num.setArcHeight(30);
        rec_num.setArcWidth(30);
        root.getChildren().add(rec_num);

        Rectangle rec = new Rectangle(win_w * 0.244, win_h * 0.203, win_w * 0.719, win_h * 0.094);
        rec.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(242, 113, 0)),
                    new Stop(1, Color.rgb(252, 198, 0))}));
        rec.setArcHeight(30);
        rec.setArcWidth(30);
        //     rec.setStrokeType(StrokeType.INSIDE);
        //rec.setStroke(Color.web("yellow", 0.16));
        //rec.setStrokeWidth(10);

        Rectangle rec_in = new Rectangle(win_w * 0.244 + 5, win_h * 0.203 + 5, win_w * 0.719 - 10, win_h * 0.094 - 10);
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

        //    rec.widthProperty().bind(primaryStage.widthProperty().subtract(win_w * 0.281));
        //     rec_in.widthProperty().bind(primaryStage.widthProperty().subtract(win_w * 0.281 + 10));

        //    rec.heightProperty().bind(primaryStage.heightProperty().subtract(win_h * 0.906));
        //     rec_in.heightProperty().bind(primaryStage.heightProperty().subtract(win_h * 0.906 + 10));


        Rectangle rec_up = new Rectangle(win_w * 0.0375, win_h * 0.047, win_w * 0.925, win_h * 0.102);
        rec_up.setFill(new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.REFLECT, new Stop[]{
                    new Stop(0, Color.rgb(56, 55, 59)),
                    new Stop(1, Color.rgb(137, 135, 140))}));
        rec_up.setArcHeight(30);
        rec_up.setArcWidth(30);
        //     rec.setStrokeType(StrokeType.INSIDE);
        //rec.setStroke(Color.web("yellow", 0.16));
        //rec.setStrokeWidth(10);

        Rectangle rec_up_in = new Rectangle(win_w * 0.0375 + 5, win_h * 0.047 + 5, win_w * 0.925 - 10, win_h * 0.102 - 10);
        rec_up_in.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.REPEAT, new Stop[]{
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
        rec_up_in.setArcHeight(20);
        rec_up_in.setArcWidth(20);

        root.getChildren().add(rec_up);
        root.getChildren().add(rec_up_in);





        Font font = new Font("verdana", 50);
        lab.setTextFill(Color.WHITESMOKE);
        lab.fontProperty().setValue(font);
        lab.setLayoutX(win_w * 0.244);
        lab.setLayoutY(win_h * 0.203);
        lab.setPrefHeight(win_h * 0.094 - 10);
        lab.setPrefWidth(win_w * 0.719 - 10);
        lab.setAlignment(Pos.CENTER);

        Font font_caption = new Font("verdana", 50);
        lab_caption.setTextFill(Color.rgb(245, 123, 0));
        lab_caption.fontProperty().setValue(font_caption);
        lab_caption.setLayoutX(win_w * 0.0375 + 5);
        lab_caption.setLayoutY(win_h * 0.047 + 5);
        lab_caption.setPrefHeight(win_h * 0.102 - 10);
        lab_caption.setPrefWidth(win_w * 0.925 - 10);
        lab_caption.setAlignment(Pos.CENTER);

        Font font_oper = new Font("verdana", 50);
        lab_oper.setTextFill(Color.WHITESMOKE);
        lab_oper.fontProperty().setValue(font_oper);
        lab_oper.setLayoutX(win_w * 0.0375 + 5);
        lab_oper.setLayoutY(win_h * 0.203);
        lab_oper.setPrefHeight(win_h * 0.094 - 10);
        lab_oper.setPrefWidth(win_w * 0.719 - 10);
        lab_oper.setAlignment(Pos.CENTER_LEFT);

        Font font_oper_num = new Font("verdana", 150);
        lab_oper_num.setTextFill(Color.WHITESMOKE);
        lab_oper_num.fontProperty().setValue(font_oper_num);
        lab_oper_num.setLayoutX(win_w * 0.0375);
        lab_oper_num.setLayoutY(win_h * 0.25);
        lab_oper_num.setPrefHeight(200);
        lab_oper_num.setPrefWidth(win_w * 0.24 - win_w * 0.0375);
        lab_oper_num.setAlignment(Pos.BASELINE_CENTER);


        Font font_bottom = new Font("Miriam", 100);
        lab_bottom.setTextFill(Color.WHITESMOKE);
        lab_bottom.fontProperty().setValue(font_bottom);
        lab_bottom.setLayoutX(win_w * 0.0375);
        lab_bottom.setLayoutY(win_h * 0.82);
        lab_bottom.setPrefHeight(win_h - win_h * 0.82);
        lab_bottom.setPrefWidth(win_w - 2 * win_w * 0.0375);
        lab_bottom.setAlignment(Pos.CENTER_RIGHT);

        //lab_nom = new Label("Ж888");
        lab_nom = new Label("");
        Font font_nom = new Font("Serif", 350);
        lab_nom.setTextFill(Color.WHITESMOKE);
        lab_nom.fontProperty().setValue(font_nom);
        lab_nom.setLayoutX(win_w * 0.244 - 100);
        lab_nom.setLayoutY(win_h * 0.25);
        lab_nom.setPrefHeight(win_h * 0.617);
        lab_nom.setPrefWidth(win_w * 0.719 + 200);
        lab_nom.setAlignment(Pos.CENTER);
        lab_nom.setEffect(new Lighting());

        root.getChildren().add(lab);
        root.getChildren().add(lab_caption);
        root.getChildren().add(lab_oper);
        root.getChildren().add(lab_oper_num);
        root.getChildren().add(lab_bottom);
        root.getChildren().add(lab_nom);


        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, // set start position at 0s
                new KeyValue(lab_nom.opacityProperty(), 1)),
                new KeyFrame(new Duration(500), // set start position at 0s
                new KeyValue(lab_nom.opacityProperty(), 1)),
                //   new KeyFrame(new Duration(500), // set start position at 0s
                //    new KeyValue<Number>(lab_nom.opacityProperty(),  0.5)),
                new KeyFrame(new Duration(700), // set start position at 0s
                new KeyValue(lab_nom.opacityProperty(), 0.1)),
                new KeyFrame(new Duration(800), // set end position at 40s
                new KeyValue(lab_nom.opacityProperty(), 0.1)));
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);

    }
    private static Label lab_nom;
    private static Timeline timeline;

    public void showData(final String data, final boolean isBlink) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                lab_nom.setText(data);
                if (isBlink) {
                    timeline.play();
                } else {
                    timeline.stop();
                    lab_nom.opacityProperty().set(1);
                }
            }
        });
    }
    private final static Label lab_bottom = new Label("консультант ожидает вас");
    private final static Label lab_oper_num = new Label("01");
    private final static Label lab_oper = new Label("оператор");
    private final static Label lab_caption = new Label("обслуживание юридических лиц");
    private final static Label lab = new Label("сейчас обслуживается клиент");

    public void showBoard(final File paramFile) {

        try {
            cfg = new PropertiesConfiguration();
            cfg.setEncoding("utf8");
            cfg.load(paramFile);
        } catch (ConfigurationException ex) {
            QLog.l().logger().error("Не загружен файл конфигурации " + paramFile.getAbsolutePath(), ex);
            throw new ServerException("Не загружен файл конфигурации " + paramFile.getAbsolutePath());
        }
        cfg.setEncoding("utf8");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                lab_caption.setText(cfg.getString("caption"));
                lab_oper.setText(cfg.getString("point.caption"));
                lab_oper_num.setText(cfg.getString("point.num"));
                lab.setText(cfg.getString("middle"));
                lab_bottom.setText(cfg.getString("bottom"));
            }
        });
        showBoard();
    }
     * 
     */
}
