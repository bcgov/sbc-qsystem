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
package ru.apertum.qsystem.server.model.response;

import java.util.Date;
import java.util.LinkedList;
import javax.swing.tree.TreeNode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.controller.ServerEvents;
import ru.apertum.qsystem.server.model.ATreeModel;

/**
 *
 * @author Evgeniy Egorov
 */
public class QResponseTree extends ATreeModel<QRespItem> {

    public static QResponseTree getInstance() {
        return QInfoTreeHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QRespItem> load() {
        return new LinkedList<>(Spring.getInstance().getHt().findByCriteria(DetachedCriteria.forClass(QRespItem.class).
                setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).
                add(Property.forName("deleted").isNull()).
                addOrder(Property.forName("id").asc())));
    }

    private static class QInfoTreeHolder {

        private static final QResponseTree INSTANCE = new QResponseTree();
    }

    private QResponseTree() {
        super();
        ServerEvents.getInstance().registerListener(() -> {
            createTree();
        });
    }

    public static void formTree(QRespItem root) {
        root.getChildren().stream().map((resp) -> {
            resp.setParent(root);
            return resp;
        }).forEach((resp) -> {
            formTree(resp);
        });
    }

    @Override
    public void save() {
        deleted.stream().forEach((t) -> {
            QResponseTree.sailToStorm(t, (TreeNode service) -> {
                final QRespItem qs = (QRespItem) service;
                qs.setDeleted(new Date());
                if (!deleted.contains(qs)) {
                    deleted.add(qs);
                }
            });
        });
        Spring.getInstance().getHt().saveOrUpdateAll(deleted);
        deleted.clear();
        Spring.getInstance().getHt().saveOrUpdateAll(getNodes());
    }
}
