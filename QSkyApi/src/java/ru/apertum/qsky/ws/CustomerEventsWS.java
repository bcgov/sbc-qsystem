/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.ws;

import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import ru.apertum.qsky.api.ICustomerEvents;

/**
 *
 * @author Evgeniy Egorov
 */
// QSkyAPI  listening at address at http://<server_address>:8080/<serviceName>/<name>
@WebService(name = "qskyapi/CustomerEventsWS", serviceName = "customer_events", portName = "qsky")
public class CustomerEventsWS {

    @EJB
    private ICustomerEvents ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "changeCustomerStatus")
    @Oneway
    public void changeCustomerStatus(@WebParam(name = "branchId") Long branchId, @WebParam(name = "serviceId") Long serviceId, @WebParam(name = "employeeId") Long employeeId, @WebParam(name = "customerId") Long customerId, @WebParam(name = "status") Integer status, @WebParam(name = "number") Integer number, @WebParam(name = "prefix") String prefix) {
        ejbRef.changeCustomerStatus(branchId, serviceId, employeeId, customerId, status, number, prefix);
    }

    @WebMethod(operationName = "insertCustomer")
    @Oneway
    public void insertCustomer(@WebParam(name = "branchId") Long branchId, @WebParam(name = "serviceId") Long serviceId, @WebParam(name = "customerId") Long customerId, @WebParam(name = "beforeCustId") Long beforeCustId, @WebParam(name = "afterCustId") Long afterCustId) {
        ejbRef.insertCustomer(branchId, serviceId, customerId, beforeCustId, afterCustId);
    }

    @WebMethod(operationName = "removeCustomer")
    @Oneway
    public void removeCustomer(@WebParam(name = "branchId") Long branchId, @WebParam(name = "serviceId") Long serviceId, @WebParam(name = "customerId") Long customerId) {
        ejbRef.removeCustomer(branchId, serviceId, customerId);
    }

    @WebMethod(operationName = "ping")
    public Integer ping(@WebParam(name = "version") String version) {
        return ejbRef.ping(version);
    }

    @WebMethod(operationName = "sendServiceName")
    @Oneway
    public void sendServiceName(@WebParam(name = "branchId") Long branchId, @WebParam(name = "serviceId") Long serviceId, @WebParam(name = "name") String name) {
        ejbRef.sendServiceName(branchId, serviceId, name);
    }

    @WebMethod(operationName = "sendUserName")
    @Oneway
    public void sendUserName(@WebParam(name = "branchId") Long branchId, @WebParam(name = "employeeId") Long employeeId, @WebParam(name = "name") String name) {
        ejbRef.sendUserName(branchId, employeeId, name);
    }

}
