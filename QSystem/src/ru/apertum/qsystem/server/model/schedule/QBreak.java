/*
 * Copyright (C) 2013 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server.model.schedule;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.server.model.IidGetter;

/**
 * Перерывы в работе для предвариловки
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "break")
public class QBreak implements IidGetter, Serializable {

    public QBreak(Date from_time, Date to_time, QBreaks breaks) {
        this.from_time = from_time;
        this.to_time = to_time;
        this.breaks = breaks;
    }

    public QBreak() {
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
    /**
     * Время начала перерыва
     */
    @Column(name = "from_time")
    @Temporal(TemporalType.TIME)
    private Date from_time;

    public Date getFrom_time() {
        return from_time;
    }

    public void setFrom_time(Date from_time) {
        this.from_time = from_time;
    }
    /**
     * Время конца перерыва
     */
    @Column(name = "to_time")
    @Temporal(TemporalType.TIME)
    private Date to_time;

    public Date getTo_time() {
        return to_time;
    }

    public void setTo_time(Date to_time) {
        this.to_time = to_time;
    }

    @Override
    public String getName() {
        return Uses.FORMAT_HH_MM.format(from_time) + "-" + Uses.FORMAT_HH_MM.format(to_time);
    }

    @Override
    public String toString() {
        return Uses.FORMAT_HH_MM.format(from_time) + "-" + Uses.FORMAT_HH_MM.format(to_time);
    }
    
    @ManyToOne
    @JoinColumn(name = "breaks_id")
    private QBreaks breaks;

    public QBreaks getBreaks() {
        return breaks;
    }

    public void setBreaks(QBreaks breaks) {
        this.breaks = breaks;
    }
    
    public long diff(){
        return getTo_time().getTime() - getFrom_time().getTime();
    }
    
}
