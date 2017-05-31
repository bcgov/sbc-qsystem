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
package ru.apertum.qsystem.reports.model;

/**
 * Запись в отчете по текущему состоянию.
 * @author Evgeniy Egorov
 */
public class CurRepRecord {

    public CurRepRecord() {
    }

    /**
     * В срезе юзеров
     * @param user имя пользователя
     * @param service название услуге
     * @param user_worked параметр юзера
     * @param user_killed параметр юзера
     * @param user_average_work параметр юзера
     * @param worked параметр услуги, в группе по юзеру
     * @param killed параметр услуги, в группе по юзеру
     * @param avg_time_work параметр услуги, в группе по юзеру
     */
    public CurRepRecord(String user, String service, int user_worked, int user_killed, long user_average_work, int worked, int killed, long avg_time_work) {
        this.user = user;
        this.service = service;
        this.user_worked = user_worked;
        this.user_killed = user_killed;
        this.user_average_work = user_average_work;
        this.worked = worked;
        this.killed = killed;
        this.avg_time_work = avg_time_work;
    }

    /**
     * В разрезе услуг
     * @param user имя юзера
     * @param service название услуги
     * @param service_worked параметр услуги
     * @param service_killed параметр услуги
     * @param service_average_work параметр услуги
     * @param service_wait параметр услуги
     * @param service_average_wait параметр услуги
     * @param worked параметр юзера, в группе по услуге
     * @param killed параметр юзера, в группе по услуге
     * @param avg_time_work параметр юзера, в группе по услуге
     */
    public CurRepRecord(String user, String service, int service_worked, int service_killed, long service_average_work, int service_wait, long service_average_wait, int worked, int killed, long avg_time_work) {
        this.user = user;
        this.service = service;
        this.service_worked = service_worked;
        this.service_killed = service_killed;
        this.service_average_work = service_average_work;
        this.service_wait = service_wait;
        this.service_average_wait = service_average_wait;
        this.worked = worked;
        this.killed = killed;
        this.avg_time_work = avg_time_work;
    }
    private String user;
    private String service;
    //
    //
    private int user_worked;
    private int user_killed;
    private long user_average_work;
    //
    //
    private int service_worked;
    private int service_killed;
    private long service_average_work;
    private int service_wait;
    private long service_average_wait;
    //--
    private int worked;
    private int killed;
    private long avg_time_work;

    public long getAvg_time_work() {
        return avg_time_work;
    }

    public void setAvg_time_work(long avg_time_work) {
        this.avg_time_work = avg_time_work;
    }

    public int getKilled() {
        return killed;
    }

    public void setKilled(int killed) {
        this.killed = killed;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public long getService_average_wait() {
        return service_average_wait;
    }

    public void setService_average_wait(long service_average_wait) {
        this.service_average_wait = service_average_wait;
    }

    public long getService_average_work() {
        return service_average_work;
    }

    public void setService_average_work(long service_average_work) {
        this.service_average_work = service_average_work;
    }

    public int getService_killed() {
        return service_killed;
    }

    public void setService_killed(int service_killed) {
        this.service_killed = service_killed;
    }

    public int getService_wait() {
        return service_wait;
    }

    public void setService_wait(int service_wait) {
        this.service_wait = service_wait;
    }

    public int getService_worked() {
        return service_worked;
    }

    public void setService_worked(int service_worked) {
        this.service_worked = service_worked;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getUser_average_work() {
        return user_average_work;
    }

    public void setUser_average_work(long user_average_work) {
        this.user_average_work = user_average_work;
    }

    public int getUser_killed() {
        return user_killed;
    }

    public void setUser_killed(int user_killed) {
        this.user_killed = user_killed;
    }

    public int getUser_worked() {
        return user_worked;
    }

    public void setUser_worked(int user_worked) {
        this.user_worked = user_worked;
    }

    public int getWorked() {
        return worked;
    }

    public void setWorked(int worked) {
        this.worked = worked;
    }
}
