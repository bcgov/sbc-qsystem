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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.server.ServerProps;

/**
 * Это класс для загрузки набора сервисов обслуживаемых юзером. Ничего хитрого, связь многие-ко-многим + коэффициент участия. Так сделано потому что у сервисов
 * нет привязки к юзерам, эта привязка вроде как односторонняя и еще имеется поле "коэффициент участия", которое будет игнориться при связи "многие-ко-многим".
 * Текстовое название услуги подтягиваеццо отдельно.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "services_users")
public class QPlanService implements Serializable {

    public QPlanService() {
    }

    public QPlanService(QService service, QUser user, Integer coefficient) {
        this.coefficient = coefficient;
        this.service = service;
        this.user = user;
    }
    //@Id
    @Expose
    @SerializedName("id")
    private Long id;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Коэфф. степени участия. По умолчанию основной. низкий/основной/VIP
     */
    @Expose
    @SerializedName("coeff")
    protected Integer coefficient = 1;

    @Column(name = "coefficient", insertable = true, updatable = true)
    public Integer getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Integer coefficient) {
        // выставим корректные параметры приоритета обслуживаемой услуге
        // по умолчанию "норма"
        if (coefficient >= Uses.SERVICE_REMAINS && coefficient <= Uses.get_COEFF_WORD().size() + ServerProps.getInstance().getProps().getExtPriorNumber()) {
            this.coefficient = coefficient;
        } else {
            this.coefficient = 1;
        }
    }
    @Expose
    @SerializedName("flex")
    private Boolean flexible_coef = false;

    @Column(name = "flexible_coef", insertable = true, updatable = true)
    public Boolean getFlexible_coef() {
        return flexible_coef;
    }

    public void setFlexible_coef(Boolean flexible_coef) {
        this.flexible_coef = flexible_coef;
    }

    /**
     * Соответствие услуги.
     */
    @Expose
    @SerializedName("service")
    private QService service;

    @OneToOne(targetEntity = QService.class)
    public QService getService() {
        return service;
    }

    public void setService(QService service) {
        this.service = service;
    }
    /**
     * Соответствие пользователя.
     */
    private QUser user;

    //@OneToOne(targetEntity = QUser.class)
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    public QUser getUser() {
        return user;
    }

    public void setUser(QUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return (getFlexible_coef() ? "* " : "") + "[" + Uses.get_COEFF_WORD().get(getCoefficient()) + "]" + service.getPrefix() + " " + service.getName();
    }
    //******************************************************************************************************************
    //*******            Статистика             *******************************************
    //******************************************************************************************************************
    private int worked = 0;
    private long avg_work = 0;// в минутах
    private int killed = 0;
    private long avg_wait = 0;// в минутах

    /**
     * В минутах
     *
     * @return среднее время ожидания кастомером
     */
    @Transient
    public long getAvg_wait() {
        return avg_wait;
    }
    private int waiters = 0;

    public void setAvg_wait(long avg_wait) {
        if (avg_wait == 0) {
            waiters = 0;
        }
        this.avg_wait = avg_wait;
    }

    /**
     * В минутах
     *
     * @return среднее время работы с кастомерами
     */
    @Transient
    public long getAvg_work() {
        return avg_work;
    }

    public void setAvg_work(long avg_work) {
        this.avg_work = avg_work;
    }

    @Transient
    public int getKilled() {
        return killed;
    }

    public void setKilled(int killed) {
        this.killed = killed;
    }

    @Transient
    public int getWorked() {
        return worked;
    }

    public void setWorked(int worked) {
        this.worked = worked;
    }

    public synchronized void inkKilled() {
        this.killed++;
    }

    /**
     *
     * @param work_time время работы с кастомером, с которым работали. в милисекундах
     */
    public synchronized void inkWorked(long work_time) {
        this.worked++;
        avg_work = (avg_work * (worked - 1) + work_time / 60000) / worked;
        avg_work = avg_work == 0 ? 1 : avg_work;
    }

    /**
     *
     * @param wait_time время ожидания кастомером, с которым начали работать. в милисекундах
     */
    public synchronized void upWait(long wait_time) {
        waiters++;
        avg_wait = (avg_wait * (waiters - 1) + wait_time / 60000) / waiters;
        avg_wait = avg_wait == 0 ? 1 : avg_wait;
    }
}
