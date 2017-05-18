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
package ru.apertum.qsystem.server.model.calendar;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.IidGetter;
import ru.apertum.qsystem.server.model.schedule.QSchedule;
import ru.apertum.qsystem.server.model.schedule.QSpecSchedule;

/**
 * Класс календаря для расписания.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "calendar")
public class QCalendar implements IidGetter, Serializable {

    public QCalendar() {
    }
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = new Date().getTime();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /**
     * Наименование плана.
     */
    @Column(name = "name")
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //MOSCOW1
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "calendar_id", updatable = false, insertable = false)
    private List<QSpecSchedule> specSchedules = new LinkedList<>();

    public List<QSpecSchedule> getSpecSchedules() {
        return specSchedules;
    }

    public void setSpecSchedules(List<QSpecSchedule> specSchedules) {
        this.specSchedules = specSchedules;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof QCalendar)) {
            throw new TypeNotPresentException("Неправильный тип для сравнения", new ServerException("Неправильный тип для сравнения"));
        }
        return id.equals(((QCalendar) o).id);
    }

    @Override
    public int hashCode() {
        return (int) (this.id != null ? this.id : 0);
    }

    /**
     * Проверка даты на нерабочую в определенном календаре
     *
     * @param date проверяемая дата, важен месяц и день
     * @return Выходной день в этом календаре или нет
     */
    public boolean checkFreeDay(Date date) {
        final DetachedCriteria criteria = DetachedCriteria.forClass(FreeDay.class);
        criteria.add(Property.forName("calendarId").eq(getId()));
        criteria.add(Property.forName("date").eq(date));
        return !(new LinkedList<>(Spring.getInstance().getHt().findByCriteria(criteria)).isEmpty());
    }

    public QSchedule getSpecSchedule(Date forDate) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(forDate);
        gc.set(GregorianCalendar.HOUR_OF_DAY, 12);
        gc.set(GregorianCalendar.MINUTE, 0);
        forDate = gc.getTime();
        for (QSpecSchedule sps : getSpecSchedules()) {
            gc.setTime(sps.getFrom());
            gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
            gc.set(GregorianCalendar.MINUTE, 0);
            final Date f = gc.getTime();
            gc.setTime(sps.getTo());
            gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
            gc.set(GregorianCalendar.MINUTE, 59);
            if (f.before(forDate) && gc.getTime().after(forDate)) {
                return sps.getSchedule();
            }
        }
        return null;
    }

}
