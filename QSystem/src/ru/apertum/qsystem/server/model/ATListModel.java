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

import java.util.LinkedList;
import javax.swing.AbstractListModel;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.controller.ServerEvents;

/**
 *
 * @param <T>
 * @author Evgeniy Egorov
 */
public abstract class ATListModel<T extends IidGetter> extends AbstractListModel/*DefaultListModel*/ {

    protected ATListModel() {
        createList();
        ServerEvents.getInstance().registerListener(() -> {
            createList();
        });
    }
    private LinkedList<T> items;

    protected abstract LinkedList<T> load();

    private void createList() {
        items = load();
        QLog.l().logger().info("Создали список.");
    }

    public LinkedList<T> getItems() {
        return items;
    }

    public T getById(long id) throws ServerException {
        for (T item : items) {
            if (id == item.getId()) {
                return item;
            }
        }
        throw new ServerException("Не найден элемент по ID: \"" + id + "\"");
    }

    public boolean hasById(long id) {
        return items.stream().anyMatch((item) -> (id == item.getId()));
    }

    public boolean hasByName(String name) {
        return items.stream().anyMatch((item) -> (name != null && name.equals(item.getName())));
    }
    protected final LinkedList<T> deleted = new LinkedList<>();

    public boolean removeElement(T obj) {
        deleted.add(obj);
        final int index = items.indexOf(obj);
        final boolean res = items.remove(obj);
        fireIntervalRemoved(this, index, index);
        return res;
    }

    public void addElement(T obj) {
        final int index = items.size();
        items.add(obj);
        fireIntervalAdded(this, index, index);
    }

    public void clear() {
        final int index1 = items.size() - 1;

        deleted.addAll(items);
        items.clear();

        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    public interface Filtering {

        public boolean filter(Object item);
    }
    private Filtering filter = null;

    public void setFilter(Filtering filter) {
        this.filter = filter;
    }

    @Override
    public T getElementAt(int index) {
        if (filter == null) {
            return items.get(index);
        } else {
            int i = -1;
            int f = 0;
            while (i != index) {
                if (filter.filter(items.get(f))) {
                    i++;
                }
                f++;
            }
            return items.get(f - 1);
        }

    }

    @Override
    public int getSize() {
        if (filter == null) {
            return items.size();
        } else {
            int i = 0;
            i = items.stream().filter((t) -> (filter.filter(t))).map((_item) -> 1).reduce(i, Integer::sum);
            return i;
        }

    }

    public void save() {
        Spring.getInstance().getHt().deleteAll(deleted);
        deleted.clear();
        Spring.getInstance().getHt().saveOrUpdateAll(items);
    }
}
