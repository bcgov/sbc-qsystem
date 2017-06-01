/*     */ package ru.apertum.qsky.plugins.ws.ws_client;
/*     */ 
/*     */ import javax.xml.bind.JAXBElement;
/*     */ import javax.xml.bind.annotation.XmlElementDecl;
/*     */ import javax.xml.bind.annotation.XmlRegistry;
/*     */ import javax.xml.namespace.QName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @XmlRegistry
/*     */ public class ObjectFactory
/*     */ {
/*  27 */   private static final QName _PingResponse_QNAME = new QName("http://ws.qsky.apertum.ru/", "pingResponse");
/*  28 */   private static final QName _ChangeCustomerStatus_QNAME = new QName("http://ws.qsky.apertum.ru/", "changeCustomerStatus");
/*  29 */   private static final QName _RemoveCustomer_QNAME = new QName("http://ws.qsky.apertum.ru/", "removeCustomer");
/*  30 */   private static final QName _InsertCustomer_QNAME = new QName("http://ws.qsky.apertum.ru/", "insertCustomer");
/*  31 */   private static final QName _SendUserName_QNAME = new QName("http://ws.qsky.apertum.ru/", "sendUserName");
/*  32 */   private static final QName _Ping_QNAME = new QName("http://ws.qsky.apertum.ru/", "ping");
/*  33 */   private static final QName _SendServiceName_QNAME = new QName("http://ws.qsky.apertum.ru/", "sendServiceName");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SendServiceName createSendServiceName()
/*     */   {
/*  47 */     return new SendServiceName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Ping createPing()
/*     */   {
/*  55 */     return new Ping();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InsertCustomer createInsertCustomer()
/*     */   {
/*  63 */     return new InsertCustomer();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SendUserName createSendUserName()
/*     */   {
/*  71 */     return new SendUserName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ChangeCustomerStatus createChangeCustomerStatus()
/*     */   {
/*  79 */     return new ChangeCustomerStatus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RemoveCustomer createRemoveCustomer()
/*     */   {
/*  87 */     return new RemoveCustomer();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PingResponse createPingResponse()
/*     */   {
/*  95 */     return new PingResponse();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="pingResponse")
/*     */   public JAXBElement<PingResponse> createPingResponse(PingResponse value)
/*     */   {
/* 104 */     return new JAXBElement(_PingResponse_QNAME, PingResponse.class, null, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="changeCustomerStatus")
/*     */   public JAXBElement<ChangeCustomerStatus> createChangeCustomerStatus(ChangeCustomerStatus value)
/*     */   {
/* 113 */     return new JAXBElement(_ChangeCustomerStatus_QNAME, ChangeCustomerStatus.class, null, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="removeCustomer")
/*     */   public JAXBElement<RemoveCustomer> createRemoveCustomer(RemoveCustomer value)
/*     */   {
/* 122 */     return new JAXBElement(_RemoveCustomer_QNAME, RemoveCustomer.class, null, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="insertCustomer")
/*     */   public JAXBElement<InsertCustomer> createInsertCustomer(InsertCustomer value)
/*     */   {
/* 131 */     return new JAXBElement(_InsertCustomer_QNAME, InsertCustomer.class, null, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="sendUserName")
/*     */   public JAXBElement<SendUserName> createSendUserName(SendUserName value)
/*     */   {
/* 140 */     return new JAXBElement(_SendUserName_QNAME, SendUserName.class, null, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="ping")
/*     */   public JAXBElement<Ping> createPing(Ping value)
/*     */   {
/* 149 */     return new JAXBElement(_Ping_QNAME, Ping.class, null, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @XmlElementDecl(namespace="http://ws.qsky.apertum.ru/", name="sendServiceName")
/*     */   public JAXBElement<SendServiceName> createSendServiceName(SendServiceName value)
/*     */   {
/* 158 */     return new JAXBElement(_SendServiceName_QNAME, SendServiceName.class, null, value);
/*     */   }
/*     */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\ObjectFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */