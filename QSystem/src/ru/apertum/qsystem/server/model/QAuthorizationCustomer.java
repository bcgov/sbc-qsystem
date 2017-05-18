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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Класс предварительно записанного кастомера. Должен уметь работать с БД, генерировать XML. И прочая логика.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "clients_authorization")
public class QAuthorizationCustomer implements Serializable {

    public QAuthorizationCustomer() {
    }
    // это добавлено для предвариловки с вводом клиентских данных

    public QAuthorizationCustomer(String name) {
        this.name = name;
    }
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    @SerializedName("id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "auth_id")
    @Expose
    @SerializedName("auth_id")
    private String authId;

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    @Column(name = "name")
    @Expose
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "surname")
    @Expose
    @SerializedName("surname")
    private String surname;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    @Column(name = "otchestvo")
    @Expose
    @SerializedName("otchestvo")
    private String otchestvo;

    public String getOtchestvo() {
        return otchestvo;
    }

    public void setOtchestvo(String otchestvo) {
        this.otchestvo = otchestvo;
    }
    @Column(name = "birthday")
    @Expose
    @SerializedName("birthday")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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
