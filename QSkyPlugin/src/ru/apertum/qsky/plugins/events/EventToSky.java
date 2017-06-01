/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.plugins.events;

import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QUser;

/**
 * Класс-хранилище для передачи данных к месту отправки в облако
 * @author egorov
 */
public class EventToSky {

    private final QCustomer qc;
    private final QService qs;
    private final QUser qu;
    private final CustomerState cs;
    private final Long newServiceId;

    public EventToSky(QCustomer qc, CustomerState cs, Long newServiceId) {
        this.qc = qc;
        this.qs = qc.getService();
        this.qu = qc.getUser();
        this.cs = cs;
        this.newServiceId = newServiceId;
    }

    /*
     * Специально для редиректа и возвращаемого после редиректа
     */
    public Long getNewServiceId() {
        return newServiceId;
    }

    public CustomerState getCustomerState() {
        return cs;
    }

    public QCustomer getCustomer() {
        return qc;
    }

    public QService getService() {
        return qs;
    }

    public QUser getUser() {
        return qu;
    }
}
