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

import java.util.Date;
import java.util.LinkedList;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.server.Spring;

/**
 * Список пользователей системы Класс, управляющий пользователями системы.
 *
 * @author Evgeniy Egorov
 */
public class QUserList extends ATListModel<QUser> {

    public static QUserList getInstance() {
        return QUserListHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QUser> load() {
        final LinkedList<QUser> users = new LinkedList<>(
                Spring.getInstance().getHt().findByCriteria(
                        DetachedCriteria.forClass(QUser.class).add(Property.forName("deleted").isNull()).setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))));
        users.stream().forEach((qUser) -> {
            qUser.setServicesCnt(qUser.getPlanServiceList().getSize());
        });
        return users;
    }

    private static class QUserListHolder {

        private static final QUserList INSTANCE = new QUserList();
    }

    private QUserList() {
        super();
        getItems().stream().forEach((qUser) -> {
            qUser.setServicesCnt(qUser.getPlanServiceList().getSize());
        });
    }

    @Override
    public void save() {
        deleted.stream().forEach((qUser) -> {
            qUser.setDeleted(new Date());
        });
        Spring.getInstance().getHt().saveOrUpdateAll(deleted);
        deleted.clear();
        /*
         for (QUser qUser : getItems()) {
         qUser.savePlan();
         } 
         */
        // плансервисам нул воткнуть при уладении в юзера
        Spring.getInstance().getHt().saveOrUpdateAll(getItems());
    }
}
