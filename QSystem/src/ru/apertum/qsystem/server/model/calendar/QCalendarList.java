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

import java.util.LinkedList;
import javax.swing.ComboBoxModel;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.ATListModel;

/**
 *
 * @author Evgeniy Egorov
 */
public class QCalendarList extends ATListModel<QCalendar> implements ComboBoxModel {

    private QCalendarList() {
        super();
    }

    public static QCalendarList getInstance() {
        return QCalendarListHolder.INSTANCE;
    }

    private static class QCalendarListHolder {

        private static final QCalendarList INSTANCE = new QCalendarList();
    }

    @Override
    protected LinkedList<QCalendar> load() {
        return new LinkedList<>(Spring.getInstance().getHt().
                findByCriteria(DetachedCriteria.forClass(QCalendar.class).
                        setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)));
    }
    private QCalendar selected;

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (QCalendar) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
}
