/*
 *  Copyright (C) 2013 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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
 *//*

package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "services_offices")
public class QServiceOffice implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    @SerializedName("id")
    private Long id;

    public Long getId() {
        return id;
    }

    public final void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getOffice().getName() + ": " + this.getService().getName();
    }

    public QServiceOffice() {
        super();
    }

    @Expose
    @SerializedName("service_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_id")
    private QService service;

    public QService getService() {
        return service;
    }

    public void setService(QService service) {
        this.service = service;
    }

    @Expose
    @SerializedName("office_id")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "office_id")
    private QOffice office;

    public QOffice getOffice() {
        return office;
    }

    public void setOffice(QOffice office) {
        this.office = office;
    }

}
*/
