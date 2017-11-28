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
package ru.apertum.qsystem.server.model.results;

import java.util.LinkedList;
import javax.swing.ComboBoxModel;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.ATListModel;

/**
 * @author Evgeniy Egorov
 */
public class QResultList extends ATListModel<QResult> implements ComboBoxModel {

    private QResult selected;

    private QResultList() {
        super();
    }

    public static QResultList getInstance() {
        return QResultListHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QResult> load() {
        return new LinkedList<>(Spring.getInstance().getHt().loadAll(QResult.class));
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (QResult) anItem;
    }

    private static class QResultListHolder {

        private static final QResultList INSTANCE = new QResultList();
    }
}
