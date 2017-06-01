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
@Table(name = "step")
public class Step extends Element {

    public Step() {
    }

    public Step(Long branchId, Long customerId) {
        this.branchId = branchId;
        this.customerId = customerId;
    }
    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
    @Column(name = "customer_id", nullable = false)
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
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "before_step_id", referencedColumnName = "id")
    })
    private Step before;

    public Step getBefore() {
        return before;
    }

    public void setBefore(Step before) {
        this.before = before;
    }
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
        @JoinColumn(name = "after_step_id", referencedColumnName = "id")
    })
    private Step after;

    public Step getAfter() {
        return after;
    }

    public void setAfter(Step after) {
        this.after = after;
    }

    public Step getLastStep() {
        Step last = this;
        while (last.getAfter() != null) {
            last = last.getAfter();
        }
        return last;
    }

    public int getStepsCount() {
        int res = 1;
        Step last = this;
        while (last.getAfter() != null) {
            last = last.getAfter();
            res++;
        }
        return res;
    }
    //***************************************************************************************************************
    @Column(name = "stand_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date standTime;

    public Date getStandTime() {
        return standTime;
    }

    public void setStandTime(Date standTime) {
        this.standTime = standTime;
    }
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    @Column(name = "finish_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTime;

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
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
    @Column(name = "start_state")
    private Integer startState;

    public Integer getStartState() {
        return startState;
    }

    public void setStartState(Integer startState) {
        this.startState = startState;
    }
    @Column(name = "finish_state")
    private Integer finishState;

    public Integer getFinishState() {
        return finishState;
    }

    public void setFinishState(Integer finishState) {
        this.finishState = finishState;
    }

    @Override
    public String toString() {
        return branchId + "/" + serviceId + "/" + employeeId;
    }

}
