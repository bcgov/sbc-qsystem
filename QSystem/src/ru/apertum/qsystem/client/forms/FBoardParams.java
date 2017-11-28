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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.model.INetProperty;

/**
 * Created on 14 Апрель 2009 г., 18:01
 *
 * @author Evgeniy Egorov
 */
public class FBoardParams extends javax.swing.JDialog {

    private static ResourceMap localeMap = null;
    /**
     * Результат
     */
    private static boolean ok;
    /**
     * Используемая ссылка на диалоговое окно.
     */
    private static FBoardParams boardParams;
    final INetProperty netProps;
    /**
     * Ветка XML-параметров
     */
    private Element params = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonColor;
    private javax.swing.JButton buttonOk;
    private javax.swing.JButton buttonRefreshRunningText;
    private javax.swing.JToggleButton buttonRun;
    private javax.swing.JCheckBox checkBoxDate;
    private javax.swing.JCheckBox checkBoxGridNext;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private ru.apertum.qsystem.common.RunningLabel runningLabel;
    private javax.swing.JSpinner spinnerFontSize;
    private javax.swing.JSpinner spinnerGridNextCols;
    private javax.swing.JSpinner spinnerGridNextRows;
    private javax.swing.JSpinner spinnerSpeed;
    private javax.swing.JTextArea textAreaHtml;
    private javax.swing.JTextField textFieldFontColor;
    private javax.swing.JTextField textFieldPict;
    private javax.swing.JTextField textFieldRunning;
    private javax.swing.JTextField textFieldVideo;
    private javax.swing.JTextField tfFRactal;

    /**
     * Creates new form FBoardParams
     *
     * @param parent относительно чего модальна форма
     * @param modal модальна или нет
     */
    public FBoardParams(java.awt.Frame parent, boolean modal, INetProperty netProps) {
        super(parent, modal);
        this.netProps = netProps;
        initComponents();

        buttonOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ok = true;
                // изменить переданые параметры
                saveXML();
                setVisible(false);
            }
        });
        buttonCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ok = false;
                setVisible(false);
            }
        });

        spinnerFontSize.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                final Font fnt = new Font(runningLabel.getFont().getName(),
                    runningLabel.getFont().getStyle(), (Integer) spinnerFontSize.getValue());
                runningLabel.setFont(fnt);
            }
        });
        textFieldFontColor.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                runningLabel.setForeground(Color.decode("#" + textFieldFontColor.getText()));
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //runningLabel.setForeground(Color.decode(textFieldFontColor.getText()));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //runningLabel.setForeground(Color.decode(textFieldFontColor.getText()));
            }
        });
        spinnerSpeed.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                runningLabel.setSpeedRunningText((Integer) spinnerSpeed.getValue());
            }
        });
    }

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(FBoardParams.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Выбор параметров для части общего табло.
     *
     * @param owner относительно этого контрола модальность и позиционирование
     */
    public static void changeParams(JFrame owner, Element params, String caption,
        INetProperty netProps) {
        if (boardParams == null) {
            boardParams = new FBoardParams(owner, true, netProps);
        }
        Uses.setLocation(boardParams);
        boardParams.loadXML(params);
        boardParams.setTitle(caption);
        boardParams.setVisible(true);
    }

    /**
     * Загрузка параметров из XML ветки
     *
     * @param params из нее грузим параметры <Left visible="0" Размер="20"> <Параметер
     * Наименование="Бегущий текст" Тип="3" Значение=""/> <Параметер Наименование="Скорость бегущего
     * текста" Тип="1" Значение="4"/> <Параметер Наименование="Размер шрифта" Тип="1"
     * Значение="50"/> <Параметер Наименование="Цвет шрифта" Тип="1" Значение="222"/> <Параметер
     * Наименование="Простая дата" Тип="4" Значение="0"/> <Параметер Наименование="Фоновое
     * изображение" Тип="3" Значение="config/mainboard/1u.PNG"/> <Параметер Наименование="Видеофайл"
     * Тип="3" Значение="D:/WORK/QSystem/config/mainboard/sezd17.mpg"/> </Left>
     */
    private void loadXML(Element params) {
        if (Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FRACTAL).size() > 0) {
            tfFRactal.setText(
                Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FRACTAL).get(0)
                    .attributeValue(Uses.TAG_BOARD_VALUE));
        } else {
            tfFRactal.setText("Do not touch this!");
        }
        textFieldRunning.setText(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_RUNNING_TEXT).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE));
        textAreaHtml.setText(params.getText().trim());
        runningLabel.setRunningText(textFieldRunning.getText());
        runningLabel.setText(textAreaHtml.getText().trim());
        textFieldPict.setText(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FON_IMG).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE));
        textFieldVideo.setText(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_VIDEO_FILE).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE));
        spinnerFontSize.setValue(Integer.parseInt(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FONT_SIZE).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE)));
        textFieldFontColor.setText(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FONT_COLOR).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE));
        runningLabel.setFont(
            new Font(runningLabel.getFont().getFontName(), runningLabel.getFont().getStyle(),
                (Integer) spinnerFontSize.getValue()));
        runningLabel.setForeground(Color.decode("#" + textFieldFontColor.getText()));
        spinnerSpeed.setValue(Integer.parseInt(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_SPEED_TEXT).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE)));
        runningLabel.setSpeedRunningText((Integer) spinnerSpeed.getValue());
        checkBoxDate.setSelected("1".equals(
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_SIMPLE_DATE).get(0)
                .attributeValue(Uses.TAG_BOARD_VALUE)));
        if (!Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT).isEmpty()) {
            checkBoxGridNext.setSelected("1".equals(
                Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT).get(0)
                    .attributeValue(Uses.TAG_BOARD_VALUE)));
            spinnerGridNextCols.setValue(Integer.parseInt(
                Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT_COLS)
                    .get(0)
                    .attributeValue(Uses.TAG_BOARD_VALUE)));
            spinnerGridNextRows.setValue(Integer.parseInt(
                Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT_ROWS)
                    .get(0)
                    .attributeValue(Uses.TAG_BOARD_VALUE)));
        } else {
            Element n = new DefaultElement(Uses.TAG_BOARD_PROP);
            n.addAttribute(Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT);
            n.addAttribute(Uses.TAG_BOARD_TYPE, "" + Uses.BOARD_TYPE_BOOL);
            n.addAttribute(Uses.TAG_BOARD_VALUE, "0");
            params.add(n);

            n = new DefaultElement(Uses.TAG_BOARD_PROP);
            n.addAttribute(Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT_COLS);
            n.addAttribute(Uses.TAG_BOARD_TYPE, "" + Uses.BOARD_TYPE_INT);
            n.addAttribute(Uses.TAG_BOARD_VALUE, "1");
            params.add(n);

            n = new DefaultElement(Uses.TAG_BOARD_PROP);
            n.addAttribute(Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT_ROWS);
            n.addAttribute(Uses.TAG_BOARD_TYPE, "" + Uses.BOARD_TYPE_INT);
            n.addAttribute(Uses.TAG_BOARD_VALUE, "5");
            params.add(n);
        }
        runningLabel.setShowTime(checkBoxDate.isSelected());
        this.params = params;
    }

    /**
     * Сохраним измененные параметры в XML
     */
    private void saveXML() {
        if (params != null) {
            if (Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FRACTAL).size()
                > 0) {
                Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FRACTAL).get(0)
                    .addAttribute(Uses.TAG_BOARD_VALUE, tfFRactal.getText());
            }
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_RUNNING_TEXT).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE, textFieldRunning.getText());
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FON_IMG).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE, textFieldPict.getText());
            // удалить предыдущие узды CDATA
            for (int i = 0; i < params.nodeCount(); i++) {
                final Node node = params.node(i);
                if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
                    params.remove(node);
                }
            }
            final String str = textAreaHtml.getText();
            if (!"".equals(str)) {
                params.addCDATA(str);
            }
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_VIDEO_FILE).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE, textFieldVideo.getText());
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FONT_SIZE).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE,
                    String.valueOf((Integer) spinnerFontSize.getValue()));
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_SPEED_TEXT).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE,
                    String.valueOf((Integer) spinnerSpeed.getValue()));
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_FONT_COLOR).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE, textFieldFontColor.getText());
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_SIMPLE_DATE).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE, checkBoxDate.isSelected() ? "1" : "0");
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE, checkBoxGridNext.isSelected() ? "1" : "0");
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT_COLS).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE,
                    String.valueOf((Integer) spinnerGridNextCols.getValue()));
            Uses.elementsByAttr(params, Uses.TAG_BOARD_NAME, Uses.TAG_BOARD_GRID_NEXT_ROWS).get(0)
                .addAttribute(Uses.TAG_BOARD_VALUE,
                    String.valueOf((Integer) spinnerGridNextRows.getValue()));

        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        textFieldRunning = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        spinnerSpeed = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        textFieldPict = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        textFieldVideo = new javax.swing.JTextField();
        checkBoxDate = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        spinnerFontSize = new javax.swing.JSpinner();
        buttonRun = new javax.swing.JToggleButton();
        jLabel8 = new javax.swing.JLabel();
        buttonColor = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaHtml = new javax.swing.JTextArea();
        textFieldFontColor = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        runningLabel = new ru.apertum.qsystem.common.RunningLabel();
        buttonRefreshRunningText = new javax.swing.JButton();
        checkBoxGridNext = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        spinnerGridNextCols = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        spinnerGridNextRows = new javax.swing.JSpinner();
        tfFRactal = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
            .getInstance(ru.apertum.qsystem.QSystem.class).getContext()
            .getResourceMap(FBoardParams.class);
        buttonOk.setText(resourceMap.getString("buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N

        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        textFieldRunning.setText(resourceMap.getString("textFieldRunning.text")); // NOI18N
        textFieldRunning.setName("textFieldRunning"); // NOI18N
        textFieldRunning.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldRunningKeyReleased(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        spinnerSpeed.setName("spinnerSpeed"); // NOI18N
        spinnerSpeed.setValue(10);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        textFieldPict.setText(resourceMap.getString("textFieldPict.text")); // NOI18N
        textFieldPict.setName("textFieldPict"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        textFieldVideo.setText(resourceMap.getString("textFieldVideo.text")); // NOI18N
        textFieldVideo.setName("textFieldVideo"); // NOI18N

        checkBoxDate.setText(resourceMap.getString("checkBoxDate.text")); // NOI18N
        checkBoxDate.setName("checkBoxDate"); // NOI18N
        checkBoxDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDateActionPerformed(evt);
            }
        });

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        spinnerFontSize.setName("spinnerFontSize"); // NOI18N
        spinnerFontSize.setValue(20);

        buttonRun.setText(resourceMap.getString("buttonRun.text")); // NOI18N
        buttonRun.setName("buttonRun"); // NOI18N
        buttonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRunActionPerformed(evt);
            }
        });

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        buttonColor.setText(resourceMap.getString("buttonColor.text")); // NOI18N
        buttonColor.setName("buttonColor"); // NOI18N
        buttonColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonColorActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        textAreaHtml.setColumns(20);
        textAreaHtml.setFont(resourceMap.getFont("textAreaHtml.font")); // NOI18N
        textAreaHtml.setLineWrap(true);
        textAreaHtml.setRows(5);
        textAreaHtml.setWrapStyleWord(true);
        textAreaHtml.setName("textAreaHtml"); // NOI18N
        textAreaHtml.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textAreaHtmlKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(textAreaHtml);

        textFieldFontColor.setText(resourceMap.getString("textFieldFontColor.text")); // NOI18N
        textFieldFontColor.setName("textFieldFontColor"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        runningLabel.setBackground(resourceMap.getColor("runningLabel.background")); // NOI18N
        runningLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        runningLabel.setText(resourceMap.getString("runningLabel.text")); // NOI18N
        runningLabel.setName("runningLabel"); // NOI18N
        jScrollPane2.setViewportView(runningLabel);

        buttonRefreshRunningText
            .setText(resourceMap.getString("buttonRefreshRunningText.text")); // NOI18N
        buttonRefreshRunningText.setName("buttonRefreshRunningText"); // NOI18N
        buttonRefreshRunningText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshRunningTextActionPerformed(evt);
            }
        });

        checkBoxGridNext.setText(resourceMap.getString("checkBoxGridNext.text")); // NOI18N
        checkBoxGridNext.setName("checkBoxGridNext"); // NOI18N
        checkBoxGridNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxGridNextActionPerformed(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        spinnerGridNextCols.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));
        spinnerGridNextCols.setName("spinnerGridNextCols"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        spinnerGridNextRows.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));
        spinnerGridNextRows.setName("spinnerGridNextRows"); // NOI18N

        tfFRactal.setText(resourceMap.getString("tfFRactal.text")); // NOI18N
        tfFRactal.setName("tfFRactal"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 706,
                            Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textFieldRunning,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(spinnerFontSize,
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(spinnerSpeed,
                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 60,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel8)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(textFieldFontColor,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE, 77,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonColor)
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                    270, Short.MAX_VALUE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    Short.MAX_VALUE)
                                                .addComponent(buttonRefreshRunningText)
                                                .addGap(18, 18, 18)))
                                        .addComponent(buttonRun))))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING,
                            javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                            .addGap(9, 9, 9)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(textFieldPict,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        640, Short.MAX_VALUE)
                                    .addComponent(textFieldVideo,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        640, Short.MAX_VALUE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(buttonOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                    89,
                                    javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(checkBoxGridNext)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerGridNextCols,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerGridNextRows,
                                            javax.swing.GroupLayout.PREFERRED_SIZE, 48,
                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(checkBoxDate)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel1))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tfFRactal)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfFRactal, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(textFieldRunning, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(spinnerSpeed,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(
                                layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(spinnerFontSize,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8)
                                    .addComponent(textFieldFontColor,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(buttonColor)))
                        .addGroup(
                            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(buttonRun)
                                .addComponent(buttonRefreshRunningText)))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(checkBoxDate)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(checkBoxGridNext)
                        .addComponent(jLabel3)
                        .addComponent(spinnerGridNextCols, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(spinnerGridNextRows, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textFieldPict, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(textFieldVideo, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 114,
                        Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonOk)
                        .addComponent(buttonCancel))
                    .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void textFieldRunningKeyReleased(
        java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldRunningKeyReleased
        //runningLabel.stop();
        //runningLabel.setRunningText(textFieldRunning.getText());
    }//GEN-LAST:event_textFieldRunningKeyReleased

    private void buttonRunActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRunActionPerformed

        if (buttonRun.isSelected()) {
            runningLabel.setRunningText(textFieldRunning.getText());
            runningLabel.start();
            buttonRun.setText(getLocaleMessage("dialog.stop"));
        } else {
            runningLabel.stop();
            buttonRun.setText(getLocaleMessage("dialog.start"));
        }
    }//GEN-LAST:event_buttonRunActionPerformed

    private void checkBoxDateActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDateActionPerformed

        runningLabel.setShowTime(checkBoxDate.isSelected());
        if (checkBoxDate.isSelected()) {
            checkBoxGridNext.setSelected(!checkBoxDate.isSelected());
        }

    }//GEN-LAST:event_checkBoxDateActionPerformed

    private void buttonColorActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonColorActionPerformed

        final JDialog di = new JDialog(this, true);
        di.setTitle(getLocaleMessage("dialog.select_color"));
        final JColorChooser cc = new JColorChooser(
            Color.decode("#" + textFieldFontColor.getText()));
        di.setSize(450, 440);
        LayoutManager l = new FlowLayout(2, 10, 10);
        di.setLayout(l);
        di.add(cc);
        final JButton but = new JButton(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldFontColor
                    .setText(
                        Integer.toHexString(cc.getColor().getRGB()).substring(2).toUpperCase());
                runningLabel.setForeground(Color.decode("#" + textFieldFontColor.getText()));
                di.setVisible(false);
            }
        });
        but.setText(getLocaleMessage("dialog.select"));
        but.setSize(20, 20);
        di.add(but);
        Uses.setLocation(di);
        di.setVisible(true);

    }//GEN-LAST:event_buttonColorActionPerformed

    private void textAreaHtmlKeyReleased(
        java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaHtmlKeyReleased
        runningLabel.setText(textAreaHtml.getText());
    }//GEN-LAST:event_textAreaHtmlKeyReleased

    private void buttonRefreshRunningTextActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshRunningTextActionPerformed
        if (!textFieldRunning.getText().isEmpty() && netProps != null) {
            NetCommander.setRunningText(netProps, textFieldRunning.getText(), params.getName());
        }
    }//GEN-LAST:event_buttonRefreshRunningTextActionPerformed

    private void checkBoxGridNextActionPerformed(
        java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxGridNextActionPerformed
        if (checkBoxGridNext.isSelected()) {
            checkBoxDate.setSelected(!checkBoxGridNext.isSelected());
        }
    }//GEN-LAST:event_checkBoxGridNextActionPerformed
    // End of variables declaration//GEN-END:variables
}
