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
package ru.apertum.qsky.model;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author egorov
 */
@Entity
@Table(name = "customer")
public class Customer extends Element {

    public Customer() {
    }

    public Customer(Long branchId, Long customerId) {
        this.branchId = branchId;
        this.customerId = customerId;
        this.visitTime = new Date();
    }
    @Column(name = "number")
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
    @Column(name = "service_prefix")
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    @Column(name = "visit_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitTime;

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }
    @Column(name = "waiting", nullable = false)
    private Long waiting = new Long(0);

    public Long getWaiting() {
        return waiting;
    }

    public void setWaiting(Long waiting) {
        this.waiting = waiting;
    }
    @Column(name = "working", nullable = false)
    private Long working = new Long(0);

    public Long getWorking() {
        return working;
    }

    public void setWorking(Long working) {
        this.working = working;
    }
    //***************************************************************************************************************
    @Column(name = "branch_id")
    private Long branchId;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
    @Column(name = "customer_id")
    private Long customerId;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    @Column(name = "service_id")
    private Long serviceId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "employee_id")
    private Long employeeId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    //***************************************************************************************************************
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "before_customer_id", referencedColumnName = "id")
    })
    private Customer before;

    public Customer getBefore() {
        return before;
    }

    public void setBefore(Customer before) {
        this.before = before;
    }
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "after_customer_id", referencedColumnName = "id")
    })
    private Customer after;

    public Customer getAfter() {
        return after;
    }

    public void setAfter(Customer after) {
        this.after = after;
    }
    //***************************************************************************************************************
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "first_step_id", referencedColumnName = "id")
    })
    private Step firstStep;

    public Step getFirstStep() {
        return firstStep;
    }

    public void setFirstStep(Step firstStep) {
        this.firstStep = firstStep;
    }

    @Column(name = "present_state")
    private Integer state;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
