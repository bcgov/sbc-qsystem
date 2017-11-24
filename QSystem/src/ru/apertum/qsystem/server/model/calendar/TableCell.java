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
package ru.apertum.qsystem.server.model.calendar;

import ru.apertum.qsystem.client.forms.FAdmin;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Ячейка сетки календаря
 * @author Evgeniy Egorov
 */
public class TableCell extends JLabel implements TableCellRenderer {

    final int year;

    public TableCell(Integer year) {
        this.year = year;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            // setBackground(Color.lightGray);
            switch (row) {
                case 0:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.january"));
                    break;
                case 1:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.february"));
                    break;
                case 2:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.march"));
                    break;
                case 3:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.april"));
                    break;
                case 4:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.may"));
                    break;
                case 5:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.june"));
                    break;
                case 6:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.july"));
                    break;
                case 7:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.augustus"));
                    break;
                case 8:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.september"));
                    break;
                case 9:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.october"));
                    break;
                case 10:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.november"));
                    break;
                case 11:
                    setText(FAdmin.getLocaleMessage("admin.calendar.month.december"));
                    break;
            }
            final GridLayout gl = new GridLayout(1, 1);
            final JPanel panel = new JPanel(gl);
            panel.setBorder(new BevelBorder(0, panel.getBackground(), panel.getBackground()));
            panel.add(this);
            return panel;
        } else {
            setText("");
            // залочим несуществующие даты, таблица все же прямоугольная
            if (checkDate(row, column)) {
                if (isSelected && table.hasFocus()) {
                    setBackground(((CalendarTableModel) table.getModel()).addDay(getDate(row, column), /*!hasFocus*/ !(table.getSelectedColumnCount() == 1 && table.getSelectedRowCount() == 1)) ? Color.lightGray : getWorkColor(row));
                } else {
                    setBackground(((CalendarTableModel) table.getModel()).isFreeDate(getDate(row, column)) != null ? Color.lightGray : getWorkColor(row));
                }
            } else {
                setBackground(Color.black);
            }
        }
        setOpaque(true);
        table.getColumnModel().getColumn(column).setResizable(false);
        return this;
    }

    private Color getWorkColor(int row) {
        return row % 2 == 1 ? new Color(247, 247, 255) : Color.WHITE;
    }

    private boolean checkDate(int month, int day) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.YEAR, year);
        gc.set(GregorianCalendar.MONTH, month);
        gc.set(GregorianCalendar.DAY_OF_MONTH, day);
        return month == gc.get(GregorianCalendar.MONTH);
    }

    private Date getDate(int month, int day) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.YEAR, year);
        gc.set(GregorianCalendar.MONTH, month);
        gc.set(GregorianCalendar.DAY_OF_MONTH, day);
        return gc.getTime();
    }
}
