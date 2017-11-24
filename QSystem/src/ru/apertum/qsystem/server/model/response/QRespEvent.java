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
package ru.apertum.qsystem.server.model.response;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "response_event")
public class QRespEvent implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
// авто нельзя, т.к. id нужны для формирования дерева
    private Long id;
    /**
     * Время оставления отзыва
     */
    @Column(name = "resp_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    /**
     * Какой отзыв оставили
     */
    @Column(name = "response_id")
    private Long respID;
    /**
     * По какому оператору отзыв оставили
     */
    @Column(name = "users_id")
    private Long userID;
    /**
     * По Какой услуге отзыв оставили
     */
    @Column(name = "services_id")
    private Long serviceID;
    /**
     * По какому посетителю отзыв оставили
     */
    @Column(name = "clients_id")
    private Long clientID;
    /**
     * Данные кастомера, который отзыв оставил
     */
    @Column(name = "client_data")
    private String clientData = "";
    /**
     * Комментарии кастомера, который отзыв оставил и его попросили их оставить в настройках отзыва
     */
    @Column(name = "comment")
    private String comment = "";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getRespID() {
        return respID;
    }

    public void setRespID(Long respID) {
        this.respID = respID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getServiceID() {
        return serviceID;
    }

    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    public Long getClientID() {
        return clientID;
    }

    public void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    public String getClientData() {
        return clientData;
    }

    public void setClientData(String clientData) {
        this.clientData = clientData;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
