/*
 * Copyright (C) 2016 Evgeniy Egorov (c) Apertum Projects
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

import javax.swing.table.AbstractTableModel;
import ru.apertum.qsystem.client.forms.FAdmin;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.model.QProperty;

/**
 *
 * @author Evgeniy Egorov
 */
public class PropsTableModel extends AbstractTableModel {

    final ServerProps.Section section;

    public PropsTableModel(ServerProps.Section item) {
        section = item;
    }

    @Override
    public int getRowCount() {
        return section.getProperties().size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return FAdmin.getLocaleMessage("key");
            case 1:
                return FAdmin.getLocaleMessage("value");
            case 2:
                return FAdmin.getLocaleMessage("comment");
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final String key = section.getProperties().keySet().toArray(new String[section.getProperties().size()])[rowIndex];
        switch (columnIndex) {
            case 0:
                return key;
            case 1:
                return section.getProperty(key).getValue();
            case 2:
                return section.getProperty(key).getComment();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        final String key = section.getProperties().keySet().toArray(new String[section.getProperties().size()])[rowIndex];
        switch (columnIndex) {
            case 1:
                section.getProperty(key).setValue((String) aValue);
                break;
            case 2:
                section.getProperty(key).setComment((String) aValue);
                break;
            default:
                throw new AssertionError();
        }
    }

    public QProperty getPropertyByKey(String key) {
        return section.getProperty(key);
    }

    public void removeByKey(QProperty p) {
        if (p.getId() == null) {
            section.removeProperty(p);
        } else {
            ServerProps.getInstance().removeProperty(p);
        }

    }
}
