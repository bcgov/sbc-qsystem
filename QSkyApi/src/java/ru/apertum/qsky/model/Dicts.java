/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.model;

import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.hibernate.Session;
import ru.apertum.qsky.ejb.IHibernateEJBLocal;

/**
 *
 * @author Evgeniy Egorov
 */
public class Dicts {

    private IHibernateEJBLocal hib;

    private IHibernateEJBLocal getHib() {
        try {
            if (hib == null) {
                hib = (IHibernateEJBLocal) ((new javax.naming.InitialContext()).lookup("java:comp/env/" + "qskyapi/HibernateEJB"));
            }
        } catch (NamingException ex) {
            throw new RuntimeException("No EJB Hib factory! " + ex);
        }
        return hib;
    }

    private Dicts() {
    }

    public static Dicts getInstance() {
        return ServicesHolder.INSTANCE;
    }

    private static class ServicesHolder {

        private static final Dicts INSTANCE = new Dicts();
    }

    private long time = 0;
    private List<Service> servs = null;
    private List<Employee> empls = null;

    private void refreshDicts() {
        final long now = new Date().getTime();
        if (now - time > 1000 * 5 || servs == null || empls == null) {

            final Session ses = getHib().openSession();
            try {
                ses.beginTransaction();
                servs = ses.createCriteria(Service.class).list();
                empls = ses.createCriteria(Employee.class).list();
                time = now;
            } catch (Exception ex) {
                System.err.println("Not loaded a list of dicts. " + ex);
            } finally {
                ses.getTransaction().rollback();
                ses.close();
            }
        }
    }

    public String getServiceName(Long branchId, Long serviceId) {
        refreshDicts();
        for (Service serv : servs) {
            if (serv.getBranchId().equals(branchId) && serv.getServiceId().equals(serviceId)) {
                return serv.getName();
            }
        }
        return serviceId == null ? "" : serviceId.toString();
    }

    public String getEmployeeName(Long branchId, Long employeeId) {
        refreshDicts();
        for (Employee empl : empls) {
            if (empl.getBranchId().equals(branchId) && empl.getEmployeeId().equals(employeeId)) {
                return empl.getName();
            }
        }
        return employeeId == null ? "" : employeeId.toString();
    }

}
