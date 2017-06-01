/*     */ package ru.apertum.qsky.plugins.ws.ws_client;
/*     */ 
/*     */ import javax.xml.bind.annotation.XmlAccessType;
/*     */ import javax.xml.bind.annotation.XmlAccessorType;
/*     */ import javax.xml.bind.annotation.XmlType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlType(name="insertCustomer", propOrder={"branchId", "serviceId", "customerId", "beforeCustId", "afterCustId"})
/*     */ public class InsertCustomer
/*     */ {
/*     */   protected Long branchId;
/*     */   protected Long serviceId;
/*     */   protected Long customerId;
/*     */   protected Long beforeCustId;
/*     */   protected Long afterCustId;
/*     */   
/*     */   public Long getBranchId()
/*     */   {
/*  57 */     return this.branchId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBranchId(Long value)
/*     */   {
/*  69 */     this.branchId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Long getServiceId()
/*     */   {
/*  81 */     return this.serviceId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServiceId(Long value)
/*     */   {
/*  93 */     this.serviceId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Long getCustomerId()
/*     */   {
/* 105 */     return this.customerId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCustomerId(Long value)
/*     */   {
/* 117 */     this.customerId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Long getBeforeCustId()
/*     */   {
/* 129 */     return this.beforeCustId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBeforeCustId(Long value)
/*     */   {
/* 141 */     this.beforeCustId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Long getAfterCustId()
/*     */   {
/* 153 */     return this.afterCustId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAfterCustId(Long value)
/*     */   {
/* 165 */     this.afterCustId = value;
/*     */   }
/*     */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\InsertCustomer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */