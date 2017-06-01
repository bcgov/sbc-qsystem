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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlType(name="changeCustomerStatus", propOrder={"branchId", "serviceId", "employeeId", "customerId", "status", "number", "prefix"})
/*     */ public class ChangeCustomerStatus
/*     */ {
/*     */   protected Long branchId;
/*     */   protected Long serviceId;
/*     */   protected Long employeeId;
/*     */   protected Long customerId;
/*     */   protected Integer status;
/*     */   protected Integer number;
/*     */   protected String prefix;
/*     */   
/*     */   public Long getBranchId()
/*     */   {
/*  63 */     return this.branchId;
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
/*  75 */     this.branchId = value;
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
/*  87 */     return this.serviceId;
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
/*  99 */     this.serviceId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Long getEmployeeId()
/*     */   {
/* 111 */     return this.employeeId;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setEmployeeId(Long value)
/*     */   {
/* 123 */     this.employeeId = value;
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
/* 135 */     return this.customerId;
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
/* 147 */     this.customerId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer getStatus()
/*     */   {
/* 159 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setStatus(Integer value)
/*     */   {
/* 171 */     this.status = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Integer getNumber()
/*     */   {
/* 183 */     return this.number;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNumber(Integer value)
/*     */   {
/* 195 */     this.number = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getPrefix()
/*     */   {
/* 207 */     return this.prefix;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPrefix(String value)
/*     */   {
/* 219 */     this.prefix = value;
/*     */   }
/*     */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\ChangeCustomerStatus.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */