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
/*     */ @XmlAccessorType(XmlAccessType.FIELD)
/*     */ @XmlType(name="sendUserName", propOrder={"branchId", "employeeId", "name"})
/*     */ public class SendUserName
/*     */ {
/*     */   protected Long branchId;
/*     */   protected Long employeeId;
/*     */   protected String name;
/*     */   
/*     */   public Long getBranchId()
/*     */   {
/*  51 */     return this.branchId;
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
/*  63 */     this.branchId = value;
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
/*  75 */     return this.employeeId;
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
/*  87 */     this.employeeId = value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  99 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setName(String value)
/*     */   {
/* 111 */     this.name = value;
/*     */   }
/*     */ }


/* Location:              C:\Users\georg\Downloads\QSkySenderPlugin.jar!\ru\apertum\qsky\plugins\ws\ws_client\SendUserName.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */