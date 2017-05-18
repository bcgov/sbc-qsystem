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
import java.util.GregorianCalendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.model.IidGetter;

/**
 * Класс плана для расписания.
 *
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "schedule")
public class QSchedule implements IidGetter, Serializable {

    public QSchedule() {
    }
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = new Date().getTime();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof QSchedule)) {
            throw new TypeNotPresentException("Неправильный тип для сравнения", new ServerException("Неправильный тип для сравнения"));
        }
        return id.equals(((QSchedule) o).id);
    }

    @Override
    public int hashCode() {
        return (int) (this.id != null ? this.id : 0);
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

    @Override
    public String toString() {
        return name;
    }
    /**
     * Тип плана 0 - недельный 1 - четные/нечетные дни
     */
    @Column(name = "type")
    private Integer type;

    /**
     * Тип плана 0 - недельный 1 - четные/нечетные дни
     *
     * @return Тип плана
     */
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * Начало и конец рабочего дня, к примеру.
     */
    public static class Interval {

        public final Date start;
        public final Date finish;

        public Interval(Date start, Date finish) {
            if (start == null || finish == null) {
                this.start = new Date(111);
                this.finish = new Date(222);
            } else {
                if (finish.before(start)) {
                    throw new ServerException("Finish date " + finish + " before than start date " + start);
                }
                this.start = start;
                this.finish = finish;
            }
        }

        public long diff() {
            return finish.getTime() - start.getTime();
        }

    }

    public Interval getWorkInterval(Date date) {
        // Определим время начала и kонца работы на этот день
        final GregorianCalendar gc_day = new GregorianCalendar();
        gc_day.setTime(date);
        final Interval in;
        if (getType() == 1) {
            if (0 == (gc_day.get(GregorianCalendar.DAY_OF_MONTH) % 2)) {
                in = new Interval(getTime_begin_1(), getTime_end_1());
            } else {
                in = new Interval(getTime_begin_2(), getTime_end_2());
            }
        } else {
            switch (gc_day.get(GregorianCalendar.DAY_OF_WEEK)) {
                case 2:
                    in = new Interval(getTime_begin_1(), getTime_end_1());
                    break;
                case 3:
                    in = new Interval(getTime_begin_2(), getTime_end_2());
                    break;
                case 4:
                    in = new Interval(getTime_begin_3(), getTime_end_3());
                    break;
                case 5:
                    in = new Interval(getTime_begin_4(), getTime_end_4());
                    break;
                case 6:
                    in = new Interval(getTime_begin_5(), getTime_end_5());
                    break;
                case 7:
                    in = new Interval(getTime_begin_6(), getTime_end_6());
                    break;
                case 1:
                    in = new Interval(getTime_begin_7(), getTime_end_7());
                    break;
                default:
                    throw new ServerException("32-е мая!");
            }
        }// Определили начало и конец рабочего дня на сегодня
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(in.start);
        gc_day.set(GregorianCalendar.HOUR_OF_DAY, gc.get(GregorianCalendar.HOUR_OF_DAY));
        gc_day.set(GregorianCalendar.MINUTE, gc.get(GregorianCalendar.MINUTE));
        gc_day.set(GregorianCalendar.SECOND, 0);
        final Date ds = gc_day.getTime();
        gc.setTime(in.finish);
        gc_day.setTime(date);
        gc_day.set(GregorianCalendar.HOUR_OF_DAY, gc.get(GregorianCalendar.HOUR_OF_DAY));
        gc_day.set(GregorianCalendar.MINUTE, gc.get(GregorianCalendar.MINUTE));
        gc_day.set(GregorianCalendar.SECOND, 0);
        return new Interval(ds, gc_day.getTime());
    }

    /**
     * Проверка на перерыв. К примеру. В перерывах нет возможности записываться, по этому это время не поедет в пункт регистрации. Есть расписание, у него на
     * каждый день список перерывов. Папало ли время в перерыв на ту дату
     *
     * @param date проверка этой даты на перерыв
     * @return да или нет
     */
    public boolean inBreak(Date date) {
        // Проверка на перерыв. В перерывах нет возможности записываться, по этому это время не поедет в пункт регистрации
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(GregorianCalendar.SECOND, 3);
        gc.set(GregorianCalendar.YEAR, 0);
        gc.set(GregorianCalendar.MONTH, 0);
        gc.set(GregorianCalendar.DAY_OF_YEAR, 0);
        int ii = gc.get(GregorianCalendar.DAY_OF_WEEK) - 1;
        if (ii < 1) {
            ii = 7;
        }
        final QBreaks qb;
        switch (ii) {
            case 1:
                qb = getBreaks_1();
                break;
            case 2:
                qb = getBreaks_2();
                break;
            case 3:
                qb = getBreaks_3();
                break;
            case 4:
                qb = getBreaks_4();
                break;
            case 5:
                qb = getBreaks_5();
                break;
            case 6:
                qb = getBreaks_6();
                break;
            case 7:
                qb = getBreaks_7();
                break;
            default:
                throw new AssertionError();
        }
        if (qb != null) {// может вообще перерывов нет
            for (QBreak br : qb.getBreaks()) {
                final GregorianCalendar gc1 = new GregorianCalendar();
                gc1.setTime(br.getFrom_time());
                gc1.set(GregorianCalendar.YEAR, 0);
                gc1.set(GregorianCalendar.MONTH, 0);
                gc1.set(GregorianCalendar.DAY_OF_YEAR, 0);
                final GregorianCalendar gc2 = new GregorianCalendar();
                gc2.setTime(br.getTo_time());
                gc2.set(GregorianCalendar.YEAR, 0);
                gc2.set(GregorianCalendar.MONTH, 0);
                gc2.set(GregorianCalendar.DAY_OF_YEAR, 0);
                if (gc1.getTime().before(gc.getTime()) && gc2.getTime().after(gc.getTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Проверка на перерыв. К примеру. В перерывах нет возможности записываться, по этому это время не поедет в пункт регистрации. Есть расписание, у него на
     * каждый день список перерывов. Папал ли интервал(пересечение) в перерыв на ту дату
     *
     * @param interval проверка этго интервала на перерыв
     * @return да или нет
     */
    public boolean inBreak(Interval interval) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(interval.finish);
        gc.add(GregorianCalendar.SECOND, -3);
        return inBreak(interval.start) || inBreak(gc.getTime());
    }

    /**
     * Проверка на перерыв. К примеру. В перерывах нет возможности записываться, по этому это время не поедет в пункт регистрации. Есть расписание, у него на
     * каждый день список перерывов. Папал ли интервал(пересечение) в перерыв на ту дату
     *
     * @param start начало этoго интервала на перерыв
     * @param finish конец этoго интервала на перерыв
     * @return да или нет
     */
    public boolean inBreak(Date start, Date finish) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(finish);
        gc.add(GregorianCalendar.SECOND, -3);
        return inBreak(start) || inBreak(gc.getTime());
    }

    /**
     * Время начала работы в первый день недели или в нечетный день, зависит от type
     */
    @Column(name = "time_begin_1")
    @Temporal(TemporalType.TIME)
    private Date time_begin_1;

    public Date getTime_begin_1() {
        return time_begin_1;
    }

    public void setTime_begin_1(Date time_begin_1) {
        this.time_begin_1 = time_begin_1;
    }
    /**
     * Время завершения работы в первый день недели или в нечетный день, зависит от type
     */
    @Column(name = "time_end_1")
    @Temporal(TemporalType.TIME)
    private Date time_end_1;

    public Date getTime_end_1() {
        return time_end_1;
    }

    public void setTime_end_1(Date time_end_1) {
        this.time_end_1 = time_end_1;
    }
    @Column(name = "time_begin_2")
    @Temporal(TemporalType.TIME)
    private Date time_begin_2;

    public Date getTime_begin_2() {
        return time_begin_2;
    }

    public void setTime_begin_2(Date time_begin_2) {
        this.time_begin_2 = time_begin_2;
    }
    @Column(name = "time_end_2")
    @Temporal(TemporalType.TIME)
    private Date time_end_2;

    public Date getTime_end_2() {
        return time_end_2;
    }

    public void setTime_end_2(Date time_end_2) {
        this.time_end_2 = time_end_2;
    }
    @Column(name = "time_begin_3")
    @Temporal(TemporalType.TIME)
    private Date time_begin_3;

    public Date getTime_begin_3() {
        return time_begin_3;
    }

    public void setTime_begin_3(Date time_begin_3) {
        this.time_begin_3 = time_begin_3;
    }
    @Column(name = "time_end_3")
    @Temporal(TemporalType.TIME)
    private Date time_end_3;

    public Date getTime_end_3() {
        return time_end_3;
    }

    public void setTime_end_3(Date time_end_3) {
        this.time_end_3 = time_end_3;
    }
    @Column(name = "time_begin_4")
    @Temporal(TemporalType.TIME)
    private Date time_begin_4;

    public Date getTime_begin_4() {
        return time_begin_4;
    }

    public void setTime_begin_4(Date time_begin_4) {
        this.time_begin_4 = time_begin_4;
    }
    @Column(name = "time_end_4")
    @Temporal(TemporalType.TIME)
    private Date time_end_4;

    public Date getTime_end_4() {
        return time_end_4;
    }

    public void setTime_end_4(Date time_end_4) {
        this.time_end_4 = time_end_4;
    }
    @Column(name = "time_begin_5")
    @Temporal(TemporalType.TIME)
    private Date time_begin_5;

    public Date getTime_begin_5() {
        return time_begin_5;
    }

    public void setTime_begin_5(Date time_begin_5) {
        this.time_begin_5 = time_begin_5;
    }
    @Column(name = "time_end_5")
    @Temporal(TemporalType.TIME)
    private Date time_end_5;

    public Date getTime_end_5() {
        return time_end_5;
    }

    public void setTime_end_5(Date time_end_5) {
        this.time_end_5 = time_end_5;
    }
    @Column(name = "time_begin_6")
    @Temporal(TemporalType.TIME)
    private Date time_begin_6;

    public Date getTime_begin_6() {
        return time_begin_6;
    }

    public void setTime_begin_6(Date time_begin_6) {
        this.time_begin_6 = time_begin_6;
    }
    @Column(name = "time_end_6")
    @Temporal(TemporalType.TIME)
    private Date time_end_6;

    public Date getTime_end_6() {
        return time_end_6;
    }

    public void setTime_end_6(Date time_end_6) {
        this.time_end_6 = time_end_6;
    }
    @Column(name = "time_begin_7")
    @Temporal(TemporalType.TIME)
    private Date time_begin_7;

    public Date getTime_begin_7() {
        return time_begin_7;
    }

    public void setTime_begin_7(Date time_begin_7) {
        this.time_begin_7 = time_begin_7;
    }
    @Column(name = "time_end_7")
    @Temporal(TemporalType.TIME)
    private Date time_end_7;

    public Date getTime_end_7() {
        return time_end_7;
    }

    public void setTime_end_7(Date time_end_7) {
        this.time_end_7 = time_end_7;
    }
    @OneToOne
    @JoinColumn(name = "breaks_id1")
    private QBreaks breaks_1;

    public QBreaks getBreaks_1() {
        return breaks_1;
    }

    public void setBreaks_1(QBreaks breaks_1) {
        this.breaks_1 = breaks_1;
    }
    @ManyToOne
    @JoinColumn(name = "breaks_id2")
    private QBreaks breaks_2;
    @ManyToOne
    @JoinColumn(name = "breaks_id3")
    private QBreaks breaks_3;
    @ManyToOne
    @JoinColumn(name = "breaks_id4")
    private QBreaks breaks_4;
    @ManyToOne
    @JoinColumn(name = "breaks_id5")
    private QBreaks breaks_5;
    @ManyToOne
    @JoinColumn(name = "breaks_id6")
    private QBreaks breaks_6;
    @ManyToOne
    @JoinColumn(name = "breaks_id7")
    private QBreaks breaks_7;

    public QBreaks getBreaks_2() {
        return breaks_2;
    }

    public void setBreaks_2(QBreaks breaks_2) {
        this.breaks_2 = breaks_2;
    }

    public QBreaks getBreaks_3() {
        return breaks_3;
    }

    public void setBreaks_3(QBreaks breaks_3) {
        this.breaks_3 = breaks_3;
    }

    public QBreaks getBreaks_4() {
        return breaks_4;
    }

    public void setBreaks_4(QBreaks breaks_4) {
        this.breaks_4 = breaks_4;
    }

    public QBreaks getBreaks_5() {
        return breaks_5;
    }

    public void setBreaks_5(QBreaks breaks_5) {
        this.breaks_5 = breaks_5;
    }

    public QBreaks getBreaks_6() {
        return breaks_6;
    }

    public void setBreaks_6(QBreaks breaks_6) {
        this.breaks_6 = breaks_6;
    }

    public QBreaks getBreaks_7() {
        return breaks_7;
    }

    public void setBreaks_7(QBreaks breaks_7) {
        this.breaks_7 = breaks_7;
    }
}
