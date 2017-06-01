/*    */ package ru.apertum.qsky.plugins.ws.ws_client;
/*    */ 
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.ws.Service;
/*    */ import javax.xml.ws.WebEndpoint;
/*    */ import javax.xml.ws.WebServiceClient;
/*    */ import javax.xml.ws.WebServiceException;
/*    */ import javax.xml.ws.WebServiceFeature;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @WebServiceClient(name="customer_events", targetNamespace="http://ws.qsky.apertum.ru/", wsdlLocation="file:/E:/WORK/qskyplugin/src/customer_events.wsdl")
/*    */ public class CustomerEvents
/*    */   extends Service
/*    */ {
/*    */   private static final URL CUSTOMEREVENTS_WSDL_LOCATION;
/*    */   private static final WebServiceException CUSTOMEREVENTS_EXCEPTION;
/* 27 */   private static final QName CUSTOMEREVENTS_QNAME = new QName("http://ws.qsky.apertum.ru/", "customer_events");
/*    */   
/*    */   static {
/* 30 */     URL url = null;
/* 31 */     WebServiceException e = null;
/*    */     try {
/* 33 */       url = new URL("file:/E:/WORK/qskyplugin/src/customer_events.wsdl");
/*    */     } catch (MalformedURLException ex) {
/* 35 */       e = new WebServiceException(ex);
/*    */     }
/* 37 */     CUSTOMEREVENTS_WSDL_LOCATION = url;
/* 38 */     CUSTOMEREVENTS_EXCEPTION = e;
/*    */   }
/*    */   
/*    */   public CustomerEvents() {
/* 42 */     super(__getWsdlLocation(), CUSTOMEREVENTS_QNAME);
/*    */   }
/*    */   
/*    */   public CustomerEvents(WebServiceFeature... features) {
/* 46 */     super(__getWsdlLocation(), CUSTOMEREVENTS_QNAME, features);
/*    */   }
/*    */   
/*    */   public CustomerEvents(URL wsdlLocation) {
/* 50 */     super(wsdlLocation, CUSTOMEREVENTS_QNAME);
/*    */   }
/*    */   
/*    */   public CustomerEvents(URL wsdlLocation, WebServiceFeature... features) {
/* 54 */     super(wsdlLocation, CUSTOMEREVENTS_QNAME, features);
/*    */   }
/*    */   
/*    */   public CustomerEvents(URL wsdlLocation, QName serviceName) {
/* 58 */     super(wsdlLocation, serviceName);
/*    */   }
/*    */   
/*    */   public CustomerEvents(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
/* 62 */     super(wsdlLocation, serviceName, features);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @WebEndpoint(name="qsky")
/*    */   public Qskyapi_002fCustomerEventsWS getQsky()
/*    */   {
/* 72 */     return (Qskyapi_002fCustomerEventsWS)super.getPort(new QName("http://ws.qsky.apertum.ru/", "qsky"), Qskyapi_002fCustomerEventsWS.class);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @WebEndpoint(name="qsky")
/*    */   public Qskyapi_002fCustomerEventsWS getQsky(WebServiceFeature... features)
/*    */   {
/* 84 */     return (Qskyapi_002fCustomerEventsWS)super.getPort(new QName("http://ws.qsky.apertum.ru/", "qsky"), Qskyapi_002fCustomerEventsWS.class, features);
/*    */   }
/*    */   
/*    */   private static URL __getWsdlLocation() {
/* 88 */     if (CUSTOMEREVENTS_EXCEPTION != null) {
/* 89 */       throw CUSTOMEREVENTS_EXCEPTION;
/*    */     }
/* 91 */     return CUSTOMEREVENTS_WSDL_LOCATION;
/*    */   }
/*    */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\CustomerEvents.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */