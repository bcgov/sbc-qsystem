/*
 * Copyright (C) 2013 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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
package ru.apertum.qsystem.server.model.schedule;

import java.util.ArrayList;
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
public class QBreaksList extends ATListModel<QBreaks> implements ComboBoxModel {

    private QBreaksList() {
    }

    public static QBreaksList getInstance() {
        return QBreaksListHolder.INSTANCE;
    }

    private static class QBreaksListHolder {

        private static final QBreaksList INSTANCE = new QBreaksList();
    }

    @Override
    protected LinkedList<QBreaks> load() {
        return new LinkedList<>(Spring.getInstance().getHt().
                findByCriteria(DetachedCriteria.forClass(QBreaks.class).
                        setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)));
    }
    private QBreaks selected;

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (QBreaks) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public void save() {
        Spring.getInstance().getHt().deleteAll(breakForDel);
        breakForDel.clear();
        super.save();
        getItems().stream().forEach((qBreaks) -> {
            Spring.getInstance().getHt().saveOrUpdateAll(qBreaks.getBreaks());
        });
    }
    private final ArrayList<QBreak> breakForDel = new ArrayList<>();

    public void addBreakForDelete(QBreak qbreak) {
        breakForDel.add(qbreak);
    }
}
