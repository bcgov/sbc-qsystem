/*    */ package ru.apertum.qsky.plugins.ws.ws_client;
/*    */ 
/*    */ import javax.xml.bind.annotation.XmlAccessType;
/*    */ import javax.xml.bind.annotation.XmlAccessorType;
/*    */ import javax.xml.bind.annotation.XmlElement;
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
/*    */ @XmlType(name="pingResponse", propOrder={"_return"})
/*    */ public class PingResponse
/*    */ {
/*    */   @XmlElement(name="return")
/*    */   protected Integer _return;
/*    */   
/*    */   public Integer getReturn()
/*    */   {
/* 47 */     return this._return;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setReturn(Integer value)
/*    */   {
/* 59 */     this._return = value;
/*    */   }
/*    */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\PingResponse.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */