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
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Класс предварительно записанного кастомера.
 * Должен уметь работать с БД, генерировать XML. И прочая логика.
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "advance")
public class QAdvanceCustomer implements Serializable {

    public QAdvanceCustomer() {
    }
    
    public QAdvanceCustomer(Long id) {
        this.id = id;
    }

    public QAdvanceCustomer(String inputData) {
        this.inputData = inputData;
    }
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    @SerializedName("id")
    private Long id = new Date().getTime() % 1000000;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    @Column(name = "advance_time")
    @Temporal(TemporalType.TIMESTAMP)
    @Expose
    @SerializedName("advance_time")
    private Date advanceTime;

    public Date getAdvanceTime() {
        return advanceTime;
    }

    public void setAdvanceTime(Date advanceTime) {
        this.advanceTime = advanceTime;
    }
    @Column(name = "priority")
    @Expose
    @SerializedName("priority")
    private Integer priority;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "service_id")
    @Expose
    @SerializedName("service")
    private QService service;

    public QService getService() {
        return service;
    }

    public void setService(QService service) {
        this.service = service;
    }
    /**
     * Связь с таблицей клиентов(фамилии, имена, адреса...) если клиент авторизовался перед тем как записаться на будующее время
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "clients_authorization_id")
    @Expose
    @SerializedName("authorization")
    private QAuthorizationCustomer authorizationCustomer;

    public QAuthorizationCustomer getAuthorizationCustomer() {
        return authorizationCustomer;
    }

    public void setAuthorizationCustomer(QAuthorizationCustomer authorizationCustomer) {
        this.authorizationCustomer = authorizationCustomer;
    }
    @Column(name = "input_data")
    @Expose
    @SerializedName("input_data")
    private String inputData;

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
    @Column(name = "comments")
    @Expose
    @SerializedName("comments")
    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
