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
package ru.apertum.qsystem.server.model.schedule;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.OnDelete;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.server.model.IidGetter;
import ru.apertum.qsystem.server.model.calendar.QCalendar;

/**
 * Класс для специального календаря, который на период в расписании расписания.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "spec_schedule")
public class QSpecSchedule implements IidGetter, Serializable {

    public QSpecSchedule() {
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return getId() == null ? "NEW" : schedule.getName();
    }

    @Override
    public String toString() {
        return Uses.FORMAT_DD_MM_YYYY.format(getFrom()) + " - " + Uses.FORMAT_DD_MM_YYYY.format(getTo()) + "   " + getSchedule();
    }

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id")
    private QSchedule schedule;

    public QSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(QSchedule schedule) {
        this.schedule = schedule;
    }

    //MOSCOW1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "calendar_id")
    private QCalendar calendar;

    public QCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(QCalendar calendar) {
        this.calendar = calendar;
    }

    @Column(name = "date_from")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date from;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    @Column(name = "date_to")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date to;

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

}
