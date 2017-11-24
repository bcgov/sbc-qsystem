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
package ru.apertum.qsystem.server.model.infosystem;

import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.controller.ServerEvents;
import ru.apertum.qsystem.server.model.ATreeModel;

import java.util.LinkedList;

/**
 *
 * @author Evgeniy Egorov
 */
public class QInfoTree extends ATreeModel<QInfoItem> {

    public static QInfoTree getInstance() {
        return QInfoTreeHolder.INSTANCE;
    }

    @Override
    protected LinkedList<QInfoItem> load() {
        return new LinkedList<>(Spring.getInstance().getHt().loadAll(QInfoItem.class));
    }

    private static class QInfoTreeHolder {

        private static final QInfoTree INSTANCE = new QInfoTree();
    }

    private QInfoTree() {
        super();
        ServerEvents.getInstance().registerListener(() -> {
            createTree();
        });
    }
}
