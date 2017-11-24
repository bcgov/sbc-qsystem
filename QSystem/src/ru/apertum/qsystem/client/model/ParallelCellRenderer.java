/*
 * Copyright (C) 2016 Evgeniy Egorov
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
package ru.apertum.qsystem.client.model;

import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.model.QCustomer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static ru.apertum.qsystem.client.forms.FClient.getLocaleMessage;

/**
 *
 * @author Evgeniy Egorov
 */
public class ParallelCellRenderer extends JTextPane implements ListCellRenderer<QCustomer> {

    private static final Dimension DIMN = new Dimension(300, 120);
    private static final LineBorder BORDER_FOCUS = new LineBorder(new Color(51, 202, 241), 3, true);
    private static final LineBorder BORDER_NO_FOCUS = new LineBorder(Color.BLACK, 1, true);
    private static final LineBorder BORDER_INVITED = new LineBorder(Color.RED, 3, true);
    private static final CompoundBorder BORDER_NO_FOCUS2 = new CompoundBorder(new EmptyBorder(5, 0, 5, 0), BORDER_NO_FOCUS);
    private static final CompoundBorder BORDER_FOCUS2 = new CompoundBorder(new MatteBorder(5, 0, 5, 0, Color.WHITE), BORDER_FOCUS);
    private static final CompoundBorder BORDER_INVITED2 = new CompoundBorder(new MatteBorder(5, 0, 5, 0, Color.WHITE), BORDER_INVITED);

    public ParallelCellRenderer() {

    }

    @Override
    public Dimension getMinimumSize() {
        return DIMN;
    }

    @Override
    public Dimension getPreferredSize() {
        return DIMN;
    }

    @Override
    public Dimension getSize() {
        return DIMN;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends QCustomer> list, QCustomer value, int index, boolean isSelected, boolean cellHasFocus) {
        if (CustomerState.STATE_INVITED.equals(value.getState()) || CustomerState.STATE_INVITED_SECONDARY.equals(value.getState())) {
            setBorder(BORDER_INVITED2);
            setBackground(Color.cyan);
        } else {
            setBorder(isSelected ? BORDER_FOCUS2 : BORDER_NO_FOCUS2);
            setBackground(isSelected ? new Color(255, 255, 120) : Color.WHITE);
        }
        // выведем на экран некую инфу о приглашенном кастомере
        final String textCust = value.getFullNumber();
        final String priority;
        switch (value.getPriority().get()) {
            case 0: {
                priority = getLocaleMessage("messages.priority.low");
                break;
            }
            case 1: {
                priority = getLocaleMessage("messages.priority.standart");
                break;
            }
            case 2: {
                priority = getLocaleMessage("messages.priority.hi");
                break;
            }
            case 3: {
                priority = getLocaleMessage("messages.priority.vip");
                break;
            }
            default: {
                priority = getLocaleMessage("messages.priority.strange");
            }
        }
        String s = value.getService().getInput_caption().replaceAll("<[^>]*>", "");
        if (s == null) {
            s = "";
        } else {
            s = "<u>" + s + "</u><br>" + value.getInput_data();
        }
        setContentType("text/html");
        setText("");
        setText("<html>"
                + "<div style='text-align: center;'><span style='font-size:32.0pt;color:purple;'>" + textCust + "</span>"
                + "<span style='font-size:14.0pt;color:gray'> " + priority + "</span>"
                + "</div>"
                + "<div style='margin: 0px 0px 0px 2px'>"
                + "<span style='font-size:14.0pt;color:black'> " + getLocaleMessage("messages.service") + ": " + value.getService().getName() + "</span><br>"
                + "<span style='font-size:14.0pt;color:gray'> " + s + "</span>"
                + "</div>");

        return this;
    }
}
