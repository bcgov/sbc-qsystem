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
 */
package ru.apertum.qsystem.server.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Нормативные настройки системы.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "standards")
public class QStandards implements Serializable {

    public QStandards() {
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    /**
     * Максимальное время ожидания, в минутах
     */
    @Column(name = "wait_max")
    @Expose
    @SerializedName("wait_max")
    private Integer waitMax;
    /**
     * Максимальное время работы с одним клиентом, в минутах
     */
    @Column(name = "work_max")
    @Expose
    @SerializedName("work_max")
    private Integer workMax;
    /**
     * Максимальное время простоя при наличии очереди, в минутах
     */
    @Column(name = "downtime_max")
    @Expose
    @SerializedName("downtime_max")
    private Integer downtimeMax;
    /**
     * Максимальная длинна очереди к одной услуге
     */
    @Column(name = "line_service_max")
    @Expose
    @SerializedName("line_service_max")
    private Integer lineServiceMax;
    /**
     * Максимальное количество клиентов ко всем услугам
     */
    @Column(name = "line_total_max")
    @Expose
    @SerializedName("line_total_max")
    private Integer lineTotalMax;

    public Integer getDowntimeMax() {
        return downtimeMax;
    }

    public void setDowntimeMax(Integer downtimeMax) {
        this.downtimeMax = downtimeMax;
    }

    public Integer getLineServiceMax() {
        return lineServiceMax;
    }

    public void setLineServiceMax(Integer lineServiceMax) {
        this.lineServiceMax = lineServiceMax;
    }

    public Integer getLineTotalMax() {
        return lineTotalMax;
    }

    public void setLineTotalMax(Integer lineTotalMax) {
        this.lineTotalMax = lineTotalMax;
    }

    public Integer getWaitMax() {
        return waitMax;
    }

    public void setWaitMax(Integer waitMax) {
        this.waitMax = waitMax;
    }

    public Integer getWorkMax() {
        return workMax;
    }

    public void setWorkMax(Integer workMax) {
        this.workMax = workMax;
    }

    @Override
    public String toString() {
        return "[MaxWait=" + getWaitMax()
                + ",WorkMax=" + getWorkMax()
                + ",DowntimeMax=" + getDowntimeMax()
                + ",LineServiceMax=" + getLineServiceMax()
                + ",LineTotalMax=" + getLineTotalMax() + "]";
    }

    /**
     * типа параметр если есть перемещение, например между корпусами или ходьба до оператора
     */
    @Column(name = "relocation")
    @Expose
    @SerializedName("relocation")
    private Integer relocation;

    public Integer getRelocation() {
        return relocation;
    }

    public void setRelocation(Integer relocation) {
        this.relocation = relocation;
    }

}
