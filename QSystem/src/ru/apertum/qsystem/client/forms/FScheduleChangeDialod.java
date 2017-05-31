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

import java.awt.Frame;
import java.text.ParseException;
import javax.swing.DefaultComboBoxModel;
import org.apache.commons.lang3.ArrayUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.schedule.QBreaks;
import ru.apertum.qsystem.server.model.schedule.QBreaksList;
import ru.apertum.qsystem.server.model.schedule.QSchedule;

/**
 * Created on 27.08.2009, 11:13:04
 * @author Evgeniy Egorov
 */
public class FScheduleChangeDialod extends javax.swing.JDialog {

    private static FScheduleChangeDialod scheduleChangeDialod;

    /** Creates new form FServiceChangeDialod
     * @param parent
     * @param modal
     */
    public FScheduleChangeDialod(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(FScheduleChangeDialod.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Основной метод редактирования услуги.
     * @param parent
     * @param modal
     * @param schedule
     */
    public static void changeSchedule(Frame parent, boolean modal, QSchedule schedule) {
        QLog.l().logger().info("Редактирование услуги \"" + schedule.getName() + "\"");
        if (scheduleChangeDialod == null) {
            scheduleChangeDialod = new FScheduleChangeDialod(parent, modal);
            scheduleChangeDialod.setTitle(getLocaleMessage("dialog.title"));
        }
        scheduleChangeDialod.loadSchedule(schedule);
        Uses.setLocation(scheduleChangeDialod);
        scheduleChangeDialod.setVisible(true);
    }

    private void loadSchedule(QSchedule schedule) {
        this.schedule = schedule;
        textFieldPlaneName.setText(schedule.getName());

        c1.setSelected(schedule.getTime_begin_1() != null && schedule.getTime_end_1() != null);
        if (c1.isSelected()) {
            s1.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_1()));
            e1.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_1()));
        }
        c2.setSelected(schedule.getTime_begin_2() != null && schedule.getTime_end_2() != null);
        if (c2.isSelected()) {
            s2.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_2()));
            e2.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_2()));
        }
        c3.setSelected(schedule.getTime_begin_3() != null && schedule.getTime_end_3() != null);
        if (c3.isSelected()) {
            s3.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_3()));
            e3.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_3()));
        }
        c4.setSelected(schedule.getTime_begin_4() != null && schedule.getTime_end_4() != null);
        if (c4.isSelected()) {
            s4.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_4()));
            e4.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_4()));
        }
        c5.setSelected(schedule.getTime_begin_5() != null && schedule.getTime_end_5() != null);
        if (c5.isSelected()) {
            s5.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_5()));
            e5.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_5()));
        }
        c6.setSelected(schedule.getTime_begin_6() != null && schedule.getTime_end_6() != null);
        if (c6.isSelected()) {
            s6.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_6()));
            e6.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_6()));
        }
        c7.setSelected(schedule.getTime_begin_7() != null && schedule.getTime_end_7() != null);
        if (c7.isSelected()) {
            s7.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_begin_7()));
            e7.setText(Uses.FORMAT_HH_MM.format(schedule.getTime_end_7()));
        }
        radioButtonWeek.setSelected(schedule.getType() == 0);
        radioButtonChet.setSelected(schedule.getType() == 1);

        QBreaks[] o = QBreaksList.getInstance().getItems().toArray(new QBreaks[0]);
        o = (QBreaks[]) ArrayUtils.addAll(new QBreaks[1], o);
        
        cb1.setModel(new DefaultComboBoxModel(o));
        cb2.setModel(new DefaultComboBoxModel(o));
        cb3.setModel(new DefaultComboBoxModel(o));
        cb4.setModel(new DefaultComboBoxModel(o));
        cb5.setModel(new DefaultComboBoxModel(o));
        cb6.setModel(new DefaultComboBoxModel(o));
        cb7.setModel(new DefaultComboBoxModel(o));


        cb1.setSelectedItem(schedule.getBreaks_1());
        cb2.setSelectedItem(schedule.getBreaks_2());
        cb3.setSelectedItem(schedule.getBreaks_3());
        cb4.setSelectedItem(schedule.getBreaks_4());
        cb5.setSelectedItem(schedule.getBreaks_5());
        cb6.setSelectedItem(schedule.getBreaks_6());
        cb7.setSelectedItem(schedule.getBreaks_7());

    }
    private QSchedule schedule;

    private void saveSchedule() {
        schedule.setName(textFieldPlaneName.getText());
        schedule.setType(radioButtonWeek.isSelected() ? 0 : 1);
        try {
            schedule.setTime_begin_1(c1.isSelected() ? Uses.FORMAT_HH_MM.parse(s1.getText()) : null);
            schedule.setTime_end_1(c1.isSelected() ? Uses.FORMAT_HH_MM.parse(e1.getText()) : null);
            schedule.setTime_begin_2(c2.isSelected() ? Uses.FORMAT_HH_MM.parse(s2.getText()) : null);
            schedule.setTime_end_2(c2.isSelected() ? Uses.FORMAT_HH_MM.parse(e2.getText()) : null);
            schedule.setTime_begin_3(c3.isSelected() ? Uses.FORMAT_HH_MM.parse(s3.getText()) : null);
            schedule.setTime_end_3(c3.isSelected() ? Uses.FORMAT_HH_MM.parse(e3.getText()) : null);
            schedule.setTime_begin_4(c4.isSelected() ? Uses.FORMAT_HH_MM.parse(s4.getText()) : null);
            schedule.setTime_end_4(c4.isSelected() ? Uses.FORMAT_HH_MM.parse(e4.getText()) : null);
            schedule.setTime_begin_5(c5.isSelected() ? Uses.FORMAT_HH_MM.parse(s5.getText()) : null);
            schedule.setTime_end_5(c5.isSelected() ? Uses.FORMAT_HH_MM.parse(e5.getText()) : null);
            schedule.setTime_begin_6(c6.isSelected() ? Uses.FORMAT_HH_MM.parse(s6.getText()) : null);
            schedule.setTime_end_6(c6.isSelected() ? Uses.FORMAT_HH_MM.parse(e6.getText()) : null);
            schedule.setTime_begin_7(c7.isSelected() ? Uses.FORMAT_HH_MM.parse(s7.getText()) : null);
            schedule.setTime_end_7(c7.isSelected() ? Uses.FORMAT_HH_MM.parse(e7.getText()) : null);

            schedule.setBreaks_1((QBreaks) cb1.getSelectedItem());
            schedule.setBreaks_2((QBreaks) cb2.getSelectedItem());
            schedule.setBreaks_3((QBreaks) cb3.getSelectedItem());
            schedule.setBreaks_4((QBreaks) cb4.getSelectedItem());
            schedule.setBreaks_5((QBreaks) cb5.getSelectedItem());
            schedule.setBreaks_6((QBreaks) cb6.getSelectedItem());
            schedule.setBreaks_7((QBreaks) cb7.getSelectedItem());
        } catch (ParseException ex) {
            throw new ServerException(ex.toString());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupTypes = new javax.swing.ButtonGroup();
        panelProps = new javax.swing.JPanel();
        radioButtonWeek = new javax.swing.JRadioButton();
        radioButtonChet = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        c1 = new javax.swing.JCheckBox();
        c2 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        s1 = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        e1 = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        s2 = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        e2 = new javax.swing.JFormattedTextField();
        panelWeek = new javax.swing.JPanel();
        c3 = new javax.swing.JCheckBox();
        c4 = new javax.swing.JCheckBox();
        c5 = new javax.swing.JCheckBox();
        c6 = new javax.swing.JCheckBox();
        c7 = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        s7 = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        e3 = new javax.swing.JFormattedTextField();
        e4 = new javax.swing.JFormattedTextField();
        e5 = new javax.swing.JFormattedTextField();
        e6 = new javax.swing.JFormattedTextField();
        e7 = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        s6 = new javax.swing.JFormattedTextField();
        s5 = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        s4 = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        s3 = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cb3 = new javax.swing.JComboBox();
        cb4 = new javax.swing.JComboBox();
        cb5 = new javax.swing.JComboBox();
        cb6 = new javax.swing.JComboBox();
        cb7 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cb1 = new javax.swing.JComboBox();
        cb2 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        textFieldPlaneName = new javax.swing.JTextField();
        panelButtons = new javax.swing.JPanel();
        buttonSave = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        panelProps.setBorder(new javax.swing.border.MatteBorder(null));
        panelProps.setName("panelProps"); // NOI18N

        buttonGroupTypes.add(radioButtonWeek);
        radioButtonWeek.setSelected(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(FScheduleChangeDialod.class);
        radioButtonWeek.setText(resourceMap.getString("radioButtonWeek.text")); // NOI18N
        radioButtonWeek.setName("radioButtonWeek"); // NOI18N
        radioButtonWeek.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                radioButtonWeekStateChanged(evt);
            }
        });

        buttonGroupTypes.add(radioButtonChet);
        radioButtonChet.setText(resourceMap.getString("radioButtonChet.text")); // NOI18N
        radioButtonChet.setName("radioButtonChet"); // NOI18N

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));
        jPanel1.setName("jPanel1"); // NOI18N

        c1.setText(resourceMap.getString("c1.text")); // NOI18N
        c1.setName("c1"); // NOI18N

        c2.setText(resourceMap.getString("c2.text")); // NOI18N
        c2.setName("c2"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        s1.setText(resourceMap.getString("s1.text")); // NOI18N
        s1.setName("s1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        e1.setText(resourceMap.getString("e1.text")); // NOI18N
        e1.setName("e1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        s2.setText(resourceMap.getString("s2.text")); // NOI18N
        s2.setName("s2"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        e2.setText(resourceMap.getString("e2.text")); // NOI18N
        e2.setName("e2"); // NOI18N

        panelWeek.setBorder(new javax.swing.border.MatteBorder(null));
        panelWeek.setName("panelWeek"); // NOI18N

        c3.setText(resourceMap.getString("c3.text")); // NOI18N
        c3.setName("c3"); // NOI18N

        c4.setText(resourceMap.getString("c4.text")); // NOI18N
        c4.setName("c4"); // NOI18N

        c5.setText(resourceMap.getString("c5.text")); // NOI18N
        c5.setName("c5"); // NOI18N

        c6.setText(resourceMap.getString("c6.text")); // NOI18N
        c6.setName("c6"); // NOI18N

        c7.setText(resourceMap.getString("c7.text")); // NOI18N
        c7.setName("c7"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        s7.setText(resourceMap.getString("s7.text")); // NOI18N
        s7.setName("s7"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        e3.setText(resourceMap.getString("e3.text")); // NOI18N
        e3.setName("e3"); // NOI18N

        e4.setText(resourceMap.getString("e4.text")); // NOI18N
        e4.setName("e4"); // NOI18N

        e5.setText(resourceMap.getString("e5.text")); // NOI18N
        e5.setName("e5"); // NOI18N

        e6.setText(resourceMap.getString("e6.text")); // NOI18N
        e6.setName("e6"); // NOI18N

        e7.setText(resourceMap.getString("e7.text")); // NOI18N
        e7.setName("e7"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        s6.setText(resourceMap.getString("s6.text")); // NOI18N
        s6.setName("s6"); // NOI18N

        s5.setText(resourceMap.getString("s5.text")); // NOI18N
        s5.setName("s5"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        s4.setText(resourceMap.getString("s4.text")); // NOI18N
        s4.setName("s4"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        s3.setText(resourceMap.getString("s3.text")); // NOI18N
        s3.setName("s3"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        cb3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb3.setName("cb3"); // NOI18N

        cb4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb4.setName("cb4"); // NOI18N

        cb5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb5.setName("cb5"); // NOI18N

        cb6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb6.setName("cb6"); // NOI18N

        cb7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb7.setName("cb7"); // NOI18N

        javax.swing.GroupLayout panelWeekLayout = new javax.swing.GroupLayout(panelWeek);
        panelWeek.setLayout(panelWeekLayout);
        panelWeekLayout.setHorizontalGroup(
            panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelWeekLayout.createSequentialGroup()
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(c7)
                    .addComponent(c6)
                    .addComponent(c5)
                    .addComponent(c4)
                    .addComponent(c3))
                .addGap(18, 18, 18)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelWeekLayout.createSequentialGroup()
                        .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17))
                        .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelWeekLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(s6))
                            .addGroup(panelWeekLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(s7, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                            .addGroup(panelWeekLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(s5))))
                    .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelWeekLayout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addGap(18, 18, 18)
                            .addComponent(s4))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelWeekLayout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addGap(18, 18, 18)
                            .addComponent(s3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21)
                    .addComponent(jLabel20))
                .addGap(18, 18, 18)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(e4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(e3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                        .addComponent(e6, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(e5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE))
                    .addComponent(e7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelWeekLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(cb7, 0, 162, Short.MAX_VALUE))
                    .addGroup(panelWeekLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(cb6, 0, 162, Short.MAX_VALUE))
                    .addGroup(panelWeekLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(cb5, 0, 162, Short.MAX_VALUE))
                    .addGroup(panelWeekLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(cb4, 0, 162, Short.MAX_VALUE))
                    .addGroup(panelWeekLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(cb3, 0, 162, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelWeekLayout.setVerticalGroup(
            panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelWeekLayout.createSequentialGroup()
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(c3)
                    .addComponent(jLabel11)
                    .addComponent(s3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(e3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cb3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(c4)
                    .addComponent(jLabel16)
                    .addComponent(s4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(e4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(cb4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(c5)
                        .addComponent(jLabel17))
                    .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(s5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)
                        .addComponent(e5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(cb5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(c6)
                    .addComponent(jLabel18)
                    .addComponent(s6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(e6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(cb6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelWeekLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(c7)
                    .addComponent(s7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(e7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(cb7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        cb1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb1.setName("cb1"); // NOI18N

        cb2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb2.setName("cb2"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelWeek, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(c1)
                            .addComponent(c2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(s2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(s1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(e2))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(e1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(cb2, 0, 162, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(cb1, 0, 162, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(c1)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(e1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(s1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cb1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(c2)
                    .addComponent(s2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(e2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(cb2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelWeek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        textFieldPlaneName.setText(resourceMap.getString("textFieldPlaneName.text")); // NOI18N
        textFieldPlaneName.setName("textFieldPlaneName"); // NOI18N

        javax.swing.GroupLayout panelPropsLayout = new javax.swing.GroupLayout(panelProps);
        panelProps.setLayout(panelPropsLayout);
        panelPropsLayout.setHorizontalGroup(
            panelPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPropsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(radioButtonChet, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioButtonWeek, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldPlaneName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelPropsLayout.setVerticalGroup(
            panelPropsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPropsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldPlaneName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radioButtonWeek)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonChet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelButtons.setBorder(new javax.swing.border.MatteBorder(null));
        panelButtons.setName("panelButtons"); // NOI18N

        buttonSave.setText(resourceMap.getString("buttonSave.text")); // NOI18N
        buttonSave.setName("buttonSave"); // NOI18N
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });

        buttonCancel.setText(resourceMap.getString("buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addContainerGap(393, Short.MAX_VALUE)
                .addComponent(buttonSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonCancel)
                .addContainerGap())
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonCancel)
                    .addComponent(buttonSave))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelProps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelProps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        saveSchedule();
        setVisible(false);
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void radioButtonWeekStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radioButtonWeekStateChanged
        c1.setText(radioButtonWeek.isSelected() ? getLocaleMessage("c1.text") : getLocaleMessage("dialog.parity"));
        c2.setText(radioButtonWeek.isSelected() ? getLocaleMessage("c2.text") : getLocaleMessage("dialog.not_parity"));
        panelWeek.setVisible(radioButtonWeek.isSelected());
    }//GEN-LAST:event_radioButtonWeekStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.ButtonGroup buttonGroupTypes;
    private javax.swing.JButton buttonSave;
    private javax.swing.JCheckBox c1;
    private javax.swing.JCheckBox c2;
    private javax.swing.JCheckBox c3;
    private javax.swing.JCheckBox c4;
    private javax.swing.JCheckBox c5;
    private javax.swing.JCheckBox c6;
    private javax.swing.JCheckBox c7;
    private javax.swing.JComboBox cb1;
    private javax.swing.JComboBox cb2;
    private javax.swing.JComboBox cb3;
    private javax.swing.JComboBox cb4;
    private javax.swing.JComboBox cb5;
    private javax.swing.JComboBox cb6;
    private javax.swing.JComboBox cb7;
    private javax.swing.JFormattedTextField e1;
    private javax.swing.JFormattedTextField e2;
    private javax.swing.JFormattedTextField e3;
    private javax.swing.JFormattedTextField e4;
    private javax.swing.JFormattedTextField e5;
    private javax.swing.JFormattedTextField e6;
    private javax.swing.JFormattedTextField e7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelProps;
    private javax.swing.JPanel panelWeek;
    private javax.swing.JRadioButton radioButtonChet;
    private javax.swing.JRadioButton radioButtonWeek;
    private javax.swing.JFormattedTextField s1;
    private javax.swing.JFormattedTextField s2;
    private javax.swing.JFormattedTextField s3;
    private javax.swing.JFormattedTextField s4;
    private javax.swing.JFormattedTextField s5;
    private javax.swing.JFormattedTextField s6;
    private javax.swing.JFormattedTextField s7;
    private javax.swing.JTextField textFieldPlaneName;
    // End of variables declaration//GEN-END:variables
}
