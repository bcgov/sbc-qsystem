package ru.apertum.qsystem.server.model;

import java.util.Date;
import java.util.LinkedList;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.server.Spring;

/**
 * This is the officeList, which uses a holder subClass to hold a cached version of all of the
 * offices at all times
 *
 * @author Sean Rumsby
 */
public class QOfficeList extends ATListModel<QOffice> {

    private QOfficeList() {
        super();
    }

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

    @Override
    public void save() {
        deleted.stream().forEach((qOffice) -> {
            qOffice.setDeleted(new Date());
        });
        Spring.getInstance().getHt().saveOrUpdateAll(deleted);
        deleted.clear();
        Spring.getInstance().getHt().saveOrUpdateAll(getItems());
    }

    private static class QOfficeListHolder {

        private static final QOfficeList INSTANCE = new QOfficeList();
    }
}
