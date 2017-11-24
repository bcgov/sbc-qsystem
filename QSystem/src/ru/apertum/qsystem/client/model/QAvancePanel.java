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
package ru.apertum.qsystem.client.model;

import ru.apertum.qsystem.common.Uses;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Класс панели для нажатия при выборе время предварительной записи.
 * @author Evgeniy Egorov
 */
public class QAvancePanel extends QPanel {

    private final JLabel label = new JLabel();
    private final IAdviceEvent event;
    private final Date data;

    public QAvancePanel(IAdviceEvent advanceEvent, Date adviceDate, final boolean enable) {
        event = advanceEvent;
        final GregorianCalendar gc11 = new GregorianCalendar();
        gc11.setTime(adviceDate);
        gc11.set(GregorianCalendar.SECOND, 0);
        gc11.set(GregorianCalendar.MILLISECOND, 0);
        data = gc11.getTime();
        //Элементы на ячейке
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.blue));
        setLayout(new GridLayout(1, 1));
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(Font.getFont("Verdana"));
        //label.setText("<html><b><p align=center><span style='font-size:12.0pt;color:black'>" + ( "00:00".equals(Uses.FORMAT_HH_MM.format(data)) ? "24:00" : Uses.FORMAT_HH_MM.format(data) ) + "</span><br/><span style='font-size:13.0pt;color:" + (enable ? "green'>Свободно" : "red'>Занято"));
        label.setText("<html><p align=center><span style='font-size:15.0pt;color:" + (enable ? "black" : "red") + "'>" + ("00:00".equals(Uses.FORMAT_HH_MM.format(data)) ? "24:00" : Uses.FORMAT_HH_MM.format(data)) + "</span>");
        add(label);
        //Реакция на нажатие мышки
        addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (enable && evt.getClickCount() == 1) {
                    event.eventPerformed(data);
                }
            }
        });
        //Градиент
        setStartPoint(new Point(30, 0));
        setEndPoint(enable ? new Point(400, 400) : new Point(160, 160));
        setStartColor(Color.white);
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(data);
        if (gc.get(GregorianCalendar.DAY_OF_WEEK) == 1 || gc.get(GregorianCalendar.DAY_OF_WEEK) == 7) {
            setEndColor(Color.red);
        } else {
            setEndColor(Color.green);
        }
        if (!enable) {
            setEndColor(Color.DARK_GRAY);
        }
        setGradient(true);
        setBorder(new LineBorder(Color.GRAY, 5));

    }

    public void setCaption(String caption) {
        label.setText(caption);
    }
}
