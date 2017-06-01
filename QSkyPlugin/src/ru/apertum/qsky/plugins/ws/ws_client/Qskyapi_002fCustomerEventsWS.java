package ru.apertum.qsky.plugins.ws.ws_client;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(name="qskyapi/CustomerEventsWS", targetNamespace="http://ws.qsky.apertum.ru/")
@XmlSeeAlso({ObjectFactory.class})
public abstract interface Qskyapi_002fCustomerEventsWS
{
  @WebMethod
  @WebResult(targetNamespace="")
  @RequestWrapper(localName="ping", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.Ping")
  @ResponseWrapper(localName="pingResponse", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.PingResponse")
  @Action(input="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/pingRequest", output="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/pingResponse")
  public abstract Integer ping(@WebParam(name="version", targetNamespace="") String paramString);
  
  @WebMethod
  @Oneway
  @RequestWrapper(localName="insertCustomer", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.InsertCustomer")
  @Action(input="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/insertCustomer")
  public abstract void insertCustomer(@WebParam(name="branchId", targetNamespace="") Long paramLong1, @WebParam(name="serviceId", targetNamespace="") Long paramLong2, @WebParam(name="customerId", targetNamespace="") Long paramLong3, @WebParam(name="beforeCustId", targetNamespace="") Long paramLong4, @WebParam(name="afterCustId", targetNamespace="") Long paramLong5);
  
  @WebMethod
  @Oneway
  @RequestWrapper(localName="removeCustomer", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.RemoveCustomer")
  @Action(input="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/removeCustomer")
  public abstract void removeCustomer(@WebParam(name="branchId", targetNamespace="") Long paramLong1, @WebParam(name="serviceId", targetNamespace="") Long paramLong2, @WebParam(name="customerId", targetNamespace="") Long paramLong3);
  
  @WebMethod
  @Oneway
  @RequestWrapper(localName="sendServiceName", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.SendServiceName")
  @Action(input="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/sendServiceName")
  public abstract void sendServiceName(@WebParam(name="branchId", targetNamespace="") Long paramLong1, @WebParam(name="serviceId", targetNamespace="") Long paramLong2, @WebParam(name="name", targetNamespace="") String paramString);
  
  @WebMethod
  @Oneway
  @RequestWrapper(localName="sendUserName", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.SendUserName")
  @Action(input="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/sendUserName")
  public abstract void sendUserName(@WebParam(name="branchId", targetNamespace="") Long paramLong1, @WebParam(name="employeeId", targetNamespace="") Long paramLong2, @WebParam(name="name", targetNamespace="") String paramString);
  
  @WebMethod
  @Oneway
  @RequestWrapper(localName="changeCustomerStatus", targetNamespace="http://ws.qsky.apertum.ru/", className="ru.apertum.qsky.plugins.ws.ws_client.ChangeCustomerStatus")
  @Action(input="http://ws.qsky.apertum.ru/qskyapi/CustomerEventsWS/changeCustomerStatus")
  public abstract void changeCustomerStatus(@WebParam(name="branchId", targetNamespace="") Long paramLong1, @WebParam(name="serviceId", targetNamespace="") Long paramLong2, @WebParam(name="employeeId", targetNamespace="") Long paramLong3, @WebParam(name="customerId", targetNamespace="") Long paramLong4, @WebParam(name="status", targetNamespace="") Integer paramInteger1, @WebParam(name="number", targetNamespace="") Integer paramInteger2, @WebParam(name="prefix", targetNamespace="") String paramString);
}


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\Qskyapi_002fCustomerEventsWS.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */