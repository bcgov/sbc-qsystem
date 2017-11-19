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
package ru.apertum.qsystem.server.model;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.server.Spring;

import java.util.Date;
import java.util.LinkedList;


public class QOfficeList extends ATListModel<QOffice> {

    public static QOfficeList getInstance() {
        return QOfficeListHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QOffice> load() {
        final LinkedList<QOffice> offices = new LinkedList<>(
                Spring.getInstance().getHt().findByCriteria(
                        DetachedCriteria.forClass(QOffice.class)
                                .add(Property.forName("deleted").isNull())
                                .setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))));
        return offices;
    }

    private static class QOfficeListHolder {

        private static final QOfficeList INSTANCE = new QOfficeList();
    }

    private QOfficeList() {
        super();
    }

    @Override
    public void save() {
        deleted.stream().forEach((qOffice) -> {
            qOffice.setDeleted(new Date());
        });
        Spring.getInstance().getHt().saveOrUpdateAll(deleted);
        deleted.clear();
        Spring.getInstance().getHt().saveOrUpdateAll(getItems());
    }
}
