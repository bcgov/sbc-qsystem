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
package ru.apertum.qsky.ejb;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.hibernate.Session;
import org.hibernate.criterion.Property;
import ru.apertum.qsky.api.ICustomerEvents;
import ru.apertum.qsky.common.CustomerState;
import ru.apertum.qsky.common.ServerProps;
import ru.apertum.qsky.model.Branch;
import ru.apertum.qsky.model.Customer;
import ru.apertum.qsky.model.Employee;
import ru.apertum.qsky.model.Service;
import ru.apertum.qsky.model.Step;

/**
 *
 * @author egorov
 */
@Singleton(mappedName = "ejb/qskyapi/customer_events", name = "qskyapi/CustomerEventsEJB")
//@Local(ICustomerEvents.class)
public class CustomerEventsEJB implements ICustomerEvents {

    @EJB(mappedName = "ejb/qskyapi/hibernate_session_factory")
    private IHibernateEJBLocal hib;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public synchronized void changeCustomerStatus(Long branchId, Long serviceId, Long employeeId, Long customerId, Integer status, Integer number, String prefix) {
        System.out.println(branchId + "  ser-" + serviceId + "  usr-" + employeeId + "  cust-" + customerId + "  #" + status + "  №" + prefix + number);

        if (status >= CustomerState.values().length) {

        } else {

            final CustomerState cs = CustomerState.values()[status];
            switch (cs) {
                //0 удален по неявке
                case STATE_DEAD:
                    kickCustomer(branchId, serviceId, customerId, employeeId, status);
                    break;

                // 1 стоит и ждет в очереди
                case STATE_WAIT:
                    standInService(branchId, serviceId, customerId, status, number, prefix);
                    break;

                // 2 стоит и ждет в очереди после того, как отлежался в отложенных положенное время и автоматически отправился в прежнюю очередь с повышенным приоритетом
                case STATE_WAIT_AFTER_POSTPONED:
                    moveToWaitCustomerAfterPostpone(branchId, customerId, serviceId, status);
                    break;

                // 3 Кастомер был опять поставлен в очередь т.к. услуга комплекстая и ждет с номером
                case STATE_WAIT_COMPLEX_SERVICE:
                    moveToWaitNextComplexService(branchId, customerId, serviceId, employeeId, status);
                    break;

                // 4 пригласили
                case STATE_INVITED:
                    inviteCustomer(branchId, customerId, serviceId, employeeId, status);
                    break;

                // 5 пригласили повторно в цепочке обработки. т.е. клиент вызван к оператору не первый раз а после редиректа или отложенности
                case STATE_INVITED_SECONDARY:
                    inviteSecondary(branchId, customerId, serviceId, employeeId, status);
                    break;

                // 6 отправили в другую очередь, идет как бы по редиректу в верх. Стоит ждет к новой услуге.
                case STATE_REDIRECT:
                    redirectCustomer(branchId, customerId, employeeId, serviceId, status);
                    break;

                // 7 начали с ним работать
                case STATE_WORK:
                    startWorkWithCustomer(branchId, customerId, serviceId, employeeId, status);
                    break;

                // 8 начали с ним работать повторно в цепочке обработки
                case STATE_WORK_SECONDARY:
                    startWorkSecondary(branchId, customerId, serviceId, employeeId, status);
                    break;

                // 9 состояние когда кастомер возвращается к прежней услуге после редиректа,
                // по редиректу в низ. Стоит ждет к старой услуге.
                case STATE_BACK:
                    backInService(branchId, customerId, employeeId, serviceId, status);
                    break;

                // 10 с кастомером закончили работать и он идет домой
                case STATE_FINISH:
                    finishWorkWithCustomer(branchId, customerId, employeeId, status);
                    break;

                // 11 с кастомером закончили работать и поместили в отложенные. домой не идет, сидит ждет покуда не вызовут.
                case STATE_POSTPONED:
                    customerToPostponed(branchId, customerId, employeeId, status);
                    break;

                default:
                    throw new AssertionError();
            }

        }

    }

    public void standInService(Long branchId, Long serviceId, Long customerId, Integer status, Integer number, String prefix) {
        System.out.println("Start standInService");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                customer = new Customer(branchId, customerId);
            }
            if (serviceId != null && serviceId > 0) {
                customer.setServiceId(serviceId);
            }
            customer.setNumber(number);
            customer.setPrefix(prefix);
            customer.setState(status);

            final Step firstStep = new Step(branchId, customerId);
            firstStep.setServiceId(serviceId);
            firstStep.setStandTime(new Date());
            firstStep.setStartState(status);
            customer.setFirstStep(firstStep);

            ses.saveOrUpdate(firstStep);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish standInService");
    }

    public void kickCustomer(Long branchId, Long serviceId, Long customerId, Long employeeId, Integer status) {
        System.out.println("Start kickCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                return;
            }
            customer.setState(status);

            if (customer.getFirstStep() != null) {
                final Step step = customer.getFirstStep().getLastStep();
                step.setFinishState(status);
                step.setFinishTime(new Date());
                step.setEmployeeId(employeeId);
                ses.saveOrUpdate(step);
            }
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish kickCustomer");
    }

    public void inviteCustomer(Long branchId, Long customerId, Long serviceId, Long employeeId, Integer status) {
        System.out.println("Start inviteCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                return;
            }
            customer.setState(status);
            customer.setEmployeeId(employeeId);

            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish inviteCustomer");
    }

    public void inviteSecondary(Long branchId, Long customerId, Long serviceId, Long employeeId, Integer status) {
        System.out.println("Start inviteSecondary");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                return;
            }
            customer.setState(status);
            customer.setEmployeeId(employeeId);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish inviteSecondary");
    }

    public void startWorkWithCustomer(Long branchId, Long customerId, Long serviceId, Long employeeId, Integer status) {
        System.out.println("Start startWorkWithCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            if (serviceId != null && serviceId > 0) {
                customer.setServiceId(serviceId);
            }
            customer.setState(status);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setServiceId(serviceId);
            //step.setStartState(Customer.States.WORK_FIRST);
            step.setStartTime(new Date());
            step.setWaiting(step.getStartTime().getTime() - step.getStandTime().getTime());
            customer.setWaiting((customer.getWaiting() * (customer.getFirstStep().getStepsCount() - 1) + step.getWaiting()) / customer.getFirstStep().getStepsCount());

            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish startWorkWithCustomer");
    }

    public void startWorkSecondary(Long branchId, Long customerId, Long serviceId, Long employeeId, Integer status) {
        System.out.println("Start startWorkSecondary");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            if (serviceId != null && serviceId > 0) {
                customer.setServiceId(serviceId);
            }
            customer.setState(status);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setServiceId(serviceId);
            step.setStartTime(new Date());
            step.setWaiting(step.getStartTime().getTime() - step.getStandTime().getTime());
            customer.setWaiting((customer.getWaiting() * (customer.getFirstStep().getStepsCount() - 1) + step.getWaiting()) / customer.getFirstStep().getStepsCount());

            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish startWorkSecondary");
    }

    public void customerToPostponed(Long branchId, Long customerId, Long employeeId, Integer status) {
        System.out.println("Start customerToPostponed");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            customer.setState(status);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setFinishTime(new Date());
            step.setFinishState(status);
            step.setWorking(step.getFinishTime().getTime() - step.getStartTime().getTime());
            customer.setWorking((customer.getWorking() * (customer.getFirstStep().getStepsCount() - 1) + step.getWorking()) / customer.getFirstStep().getStepsCount());

            final Step postponedStep = new Step(branchId, customerId);
            postponedStep.setStandTime(new Date());
            postponedStep.setStartState(status);
            step.setAfter(postponedStep);
            postponedStep.setBefore(step);

            ses.saveOrUpdate(postponedStep);
            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish customerToPostponed");
    }

    public void redirectCustomer(Long branchId, Long customerId, Long employeeId, Long serviceId, Integer status) {
        System.out.println("Start redirectCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            customer.setState(status);
            customer.setServiceId(serviceId);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setFinishTime(new Date());
            step.setFinishState(status);
            step.setWorking(step.getFinishTime().getTime() - step.getStartTime().getTime());
            customer.setWorking((customer.getWorking() * (customer.getFirstStep().getStepsCount() - 1) + step.getWorking()) / customer.getFirstStep().getStepsCount());

            final Step redirectedStep = new Step(branchId, customerId);
            redirectedStep.setStandTime(new Date());
            redirectedStep.setStartState(status);
            redirectedStep.setServiceId(serviceId);
            step.setAfter(redirectedStep);
            redirectedStep.setBefore(step);

            ses.saveOrUpdate(redirectedStep);
            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish redirectCustomer");
    }

    public void moveToWaitCustomerAfterPostpone(Long branchId, Long customerId, Long serviceId, Integer status) {
        System.out.println("Start moveToWaitCustomerAfterPostpone");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            customer.setState(status);
            customer.setServiceId(serviceId);

            final Step step = customer.getFirstStep().getLastStep();
            step.setFinishTime(new Date());
            step.setFinishState(status);
            step.setWaiting(step.getFinishTime().getTime() - step.getStartTime().getTime());
            customer.setWaiting((customer.getWaiting() * (customer.getFirstStep().getStepsCount() - 1) + step.getWaiting()) / customer.getFirstStep().getStepsCount());

            final Step waitStep = new Step(branchId, customerId);
            waitStep.setStandTime(new Date());
            waitStep.setStartState(status);
            waitStep.setServiceId(serviceId);
            step.setAfter(waitStep);
            waitStep.setBefore(step);

            ses.saveOrUpdate(waitStep);
            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish moveToWaitCustomerAfterPostpone");
    }

    public void moveToWaitNextComplexService(Long branchId, Long customerId, Long serviceId, Long employeeId, Integer status) {
        System.out.println("Start moveToWaitNextComplexService");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            customer.setState(status);
            customer.setServiceId(serviceId);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setFinishTime(new Date());
            step.setFinishState(status);
            step.setWorking(step.getFinishTime().getTime() - step.getStartTime().getTime());
            customer.setWorking((customer.getWorking() * (customer.getFirstStep().getStepsCount() - 1) + step.getWorking()) / customer.getFirstStep().getStepsCount());

            final Step nextComplexStep = new Step(branchId, customerId);
            nextComplexStep.setStandTime(new Date());
            nextComplexStep.setStartState(status);
            nextComplexStep.setServiceId(serviceId);
            step.setAfter(nextComplexStep);
            nextComplexStep.setBefore(step);

            ses.saveOrUpdate(nextComplexStep);
            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish moveToWaitNextComplexService");
    }

    public void backInService(Long branchId, Long customerId, Long employeeId, Long serviceId, Integer status) {
        System.out.println("Start backInService");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            customer.setState(status);
            customer.setServiceId(serviceId);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setFinishTime(new Date());
            step.setFinishState(status);
            step.setWorking(step.getFinishTime().getTime() - step.getStartTime().getTime());
            customer.setWorking((customer.getWorking() * (customer.getFirstStep().getStepsCount() - 1) + step.getWorking()) / customer.getFirstStep().getStepsCount());

            final Step redirectedStep = new Step(branchId, customerId);
            redirectedStep.setStandTime(new Date());
            redirectedStep.setStartState(status);
            redirectedStep.setServiceId(serviceId);
            step.setAfter(redirectedStep);
            redirectedStep.setBefore(step);

            ses.saveOrUpdate(redirectedStep);
            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish backInService");
    }

    public void finishWorkWithCustomer(Long branchId, Long customerId, Long employeeId, Integer status) {
        System.out.println("Start finishWorkWithCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                System.out.println("ERROR: Customer not found id=" + customerId);
                return;
            }
            customer.setState(status);

            final Step step = customer.getFirstStep().getLastStep();
            step.setEmployeeId(employeeId);
            step.setFinishState(status);
            step.setFinishTime(new Date());
            step.setWorking(step.getFinishTime().getTime() - step.getStartTime().getTime());
            customer.setWorking((customer.getWorking() * (customer.getFirstStep().getStepsCount() - 1) + step.getWorking()) / customer.getFirstStep().getStepsCount());

            ses.saveOrUpdate(step);
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
        } finally {
            ses.close();
        }
        System.out.println("Finish finishWorkWithCustomer");
    }

    @Override
    public synchronized void insertCustomer(Long branchId, Long serviceId, Long customerId, Long beforeCustId, Long afterCustId) {
        System.out.println("Start insertCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                customer = new Customer(branchId, customerId);
                if (serviceId != null && serviceId > 0) {
                    customer.setServiceId(serviceId);
                }
            }
            final Customer before = getCustomer(ses, branchId, beforeCustId);
            final Customer after = getCustomer(ses, branchId, afterCustId);
            if (before != null) {
                before.setAfter(customer);
                customer.setBefore(before);
            }
            if (after != null) {
                after.setBefore(customer);
                customer.setAfter(after);
            }

            ses.saveOrUpdate(customer);
            if (after != null) {
                ses.saveOrUpdate(after);
            }
            if (before != null) {
                ses.saveOrUpdate(before);
            }
            ses.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
            ses.getTransaction().rollback();
        } finally {
            ses.close();
        }
        System.out.println("Finish insertCustomer");
    }

    @Override
    public synchronized void removeCustomer(Long branchId, Long serviceId, Long customerId) {
        System.out.println("Start removeCustomer");
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();

            final Customer customer = getCustomer(ses, branchId, customerId);
            if (customer == null) {
                return;
            }
            if (customer.getBefore() != null) {
                customer.getBefore().setAfter(customer.getAfter());
            }
            if (customer.getAfter() != null) {
                customer.getAfter().setBefore(customer.getBefore());
            }
            customer.setAfter(null);
            customer.setBefore(null);

            if (customer.getBefore() != null) {
                ses.saveOrUpdate(customer.getBefore());
            }
            if (customer.getAfter() != null) {
                ses.saveOrUpdate(customer.getAfter());
            }
            ses.saveOrUpdate(customer);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
            ses.getTransaction().rollback();
        } finally {
            ses.close();
        }
        System.out.println("Finish removeCustomer");
    }

    @Override
    public Integer ping(String version) {
        return ServerProps.getInstance().isSupportClient(version) ? 1 : -1;
    }

    @Override
    public synchronized void sendServiceName(Long branchId, Long serviceId, String name) {
        System.out.println("Invoke sendServiceName " + name);
        dataLock.lock();
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Service service = getService(ses, branchId, serviceId);
            if (service == null) {
                service = new Service(branchId, serviceId, name);
            }
            service.setName(name);
            ses.saveOrUpdate(service);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
            ses.getTransaction().rollback();
        } finally {
            dataLock.unlock();
            ses.close();
        }
    }
    private static final ReentrantLock dataLock = new ReentrantLock();

    @Override
    public synchronized void sendUserName(Long branchId, Long employeeId, String name) {
        System.out.println("Invoke sendUserName " + name);
        dataLock.lock();
        final Session ses = hib.openSession();
        try {
            ses.beginTransaction();
            Employee employee = getEmployee(ses, branchId, employeeId);
            if (employee == null) {
                employee = new Employee(branchId, employeeId, name);
            }
            employee.setName(name);
            ses.saveOrUpdate(employee);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println("ex = " + ex);
            ex.printStackTrace(System.err);
            ses.getTransaction().rollback();
        } finally {
            dataLock.unlock();
            ses.close();
        }
    }
    //*******************************************************************************************************

    private Customer getCustomer(final Session ses, Long branchId, Long customerId) {
        final List<Customer> list = ses.createCriteria(Customer.class).add(Property.forName("branchId").eq(branchId)).add(Property.forName("customerId").eq(customerId)).list();
        return list.isEmpty() ? null : list.get(0);
    }

    private Employee getEmployee(final Session ses, Long branchId, Long employeeId) {
        final List<Employee> list = ses.createCriteria(Employee.class).add(Property.forName("branchId").eq(branchId)).add(Property.forName("employeeId").eq(employeeId)).list();
        return list.isEmpty() ? null : list.get(0);
    }

    private Service getService(final Session ses, Long branchId, Long serviceId) {
        final List<Service> list = ses.createCriteria(Service.class).add(Property.forName("branchId").eq(branchId)).add(Property.forName("serviceId").eq(serviceId)).list();
        return list.isEmpty() ? null : list.get(0);
    }

    private Branch getBranch(final Session ses, Long branchId) {
        final List<Branch> list = ses.createCriteria(Branch.class).add(Property.forName("branchId").eq(branchId)).list();
        return list.isEmpty() ? null : list.get(0);
    }

}

/*
 // QSkyAPI  listening at address at http://<server_address>:8080/<serviceName>/<name>
 @WebService(name = "qskyapi/CustomerEventsWS", serviceName="customer_events", portName="qsky")
 public class CustomerEventsWS {

 @EJB(mappedName = "ejb/qskyapi/customer_events")
 private ICustomerEvents ejbRef;
 */

/*

 /**
 *
 * @author Evgeniy Egorov
 *
 // QSkyAPI  listening at address at http://<server_address>:8080/<serviceName>/<name>
 @WebService(name = "qskyapi/CustomerEventsWS", serviceName = "customer_events", portName = "qsky")
 public class CustomerEventsWS {
 @EJB
 private ICustomerEvents ejbRef;// Add business logic below. (Right-click in editor and choose
 // "Insert Code > Add Web Service Operation")

 @WebMethod(operationName = "standInService")
 @Oneway
 public void standInService(@WebParam(name = "branchId") Long branchId, @WebParam(name = "serviceId") Long serviceId, @WebParam(name = "customerId") Long customerId, @WebParam(name = "number") Integer number, @WebParam(name = "prefix") String prefix) {
 ejbRef.standInService(branchId, serviceId, customerId, number, prefix);
 }

 @WebMethod(operationName = "kickCustomer")
 @Oneway
 public void kickCustomer(@WebParam(name = "branchId") Long branchId, @WebParam(name = "serviceId") Long serviceId, @WebParam(name = "customerId") Long customerId, @WebParam(name = "employeeId") Long employeeId) {
 ejbRef.kickCustomer(branchId, serviceId, customerId, employeeId);
 }

 */
