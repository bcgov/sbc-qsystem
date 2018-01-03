package ru.apertum.qsystem.server.jobs;

import java.util.LinkedList;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.QOfficeList;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;

public class QRefreshJob implements org.quartz.Job {

    private void addCustomerToService(QCustomer customer, QService service) {
        service.setCountPerDay(customer.getService().getCountPerDay());
        service.setDay(customer.getService().getDay());

        final QUser customerUser = customer.getUser();
        customer.setService(service);

        if (customerUser != null && QUserList.getInstance().getById(customerUser.getId()) != null) {
            QUserList.getInstance().getById(customerUser.getId()).setCustomer(customer);
            customer.setUser(QUserList.getInstance().getById(customerUser.getId()));
        }

        customer.setPriority(1);

        Integer state = customer.getStateIn();
        customer.setStateWithoutSave(state);

        QLog.l().logQUser().debug("Add customer to serviceTree");
        service.addCustomer(customer);
        QLog.l().logQUser().debug("Complete");
    }

    private void refreshServiceCustomers() {
        final LinkedList<QCustomer> customers = new LinkedList<QCustomer>(
            Spring.getInstance().getHt().findByCriteria(
                DetachedCriteria.forClass(QCustomer.class)
                    .add(Property.forName("stateIn").ne(0))
                    .add(Property.forName("stateIn").ne(10))
                    .add(Property.forName("stateIn").ne(11))
                    .add(Property.forName("stateIn").ne(12))
                    .add(Property.forName("stateIn").ne(13))
                    .setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))));

        for (QCustomer customerFromDB : customers) {
            QLog.l().logQUser().debug("Checking customer " + customerFromDB.getId() + " to be in serviceTree client list");
            final QService service = QServiceTree.getInstance().getById(customerFromDB.getService().getId());
            if (service == null) {
                QLog.l().logQUser().debug("Service null, continue");
                continue;
            }

            Boolean addToService = Boolean.TRUE;

            for (QCustomer customerFromService : service.getClients()) {
                QLog.l().logQUser().debug("Checking " + customerFromDB.getId() + " against serviceCustomer: " + customerFromService.getId());
                if (customerFromService.getId().equals(customerFromDB.getId())) {
                    QLog.l().logQUser().debug("Found in service: " + service);
                    addToService = Boolean.FALSE;
                    break;
                }
            }

            if (addToService) {
                QLog.l().logQUser().debug("DB Customer not in service list. Adding to service");
                addCustomerToService(customerFromDB, service);
            }
        }

        for (QService serviceFromTree : QServiceTree.getInstance().getNodes()) {
            QLog.l().logQUser().debug("Checking customer in service: " + serviceFromTree);
            for (QCustomer customerFromServiceTree : serviceFromTree.getClients()) {
                Boolean removeCustomer = Boolean.TRUE;
                QLog.l().logQUser().debug("Checking customer " + customerFromServiceTree.getId() + " to exist");
                for (QCustomer customerFromDB : customers) {
                    if (customerFromDB.getId().equals(customerFromServiceTree.getId())) {
                        QLog.l().logQUser().debug("Customer exists... continue");
                        removeCustomer = Boolean.FALSE;
                        break;
                    }
                }

                if (removeCustomer) {
                    QLog.l().logQUser().debug("Customer does not exist... remove them");
                    serviceFromTree.removeCustomer(customerFromServiceTree);
                }
            }
        }
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        //These should probably be handled with the restartServer event...
        //QOfficeList.getInstance().refresh();
        //QUserList.getInstance().refresh();
        //QServiceTree.getInstance().rebuildTree();

        QPostponedList.getInstance().loadPostponedList(new LinkedList<QCustomer>());
        refreshServiceCustomers();
    }
}