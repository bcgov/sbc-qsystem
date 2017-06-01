/*    */ package ru.apertum.qsky.plugins.ws.ws_client;
/*    */ 
/*    */ import javax.xml.bind.annotation.XmlAccessType;
/*    */ import javax.xml.bind.annotation.XmlAccessorType;
/*    */ import javax.xml.bind.annotation.XmlType;
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
/*    */ 
/*    */ @XmlAccessorType(XmlAccessType.FIELD)
/*    */ @XmlType(name="ping", propOrder={"version"})
/*    */ public class Ping
/*    */ {
/*    */   protected String version;
/*    */   
/*    */   public String getVersion()
/*    */   {
/* 45 */     return this.version;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setVersion(String value)
/*    */   {
/* 57 */     this.version = value;
/*    */   }
/*    */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\Ping.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */