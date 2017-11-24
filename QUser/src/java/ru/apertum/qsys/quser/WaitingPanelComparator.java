/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsys.quser;

import java.io.Serializable;
import java.util.Comparator;
import ru.apertum.qsystem.common.model.QCustomer;

/**
 * @author jassingh
 */
public class WaitingPanelComparator implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    private boolean asc = true;
    private int type = 0;

    public WaitingPanelComparator(boolean asc, int type) {
        this.asc = asc;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compare(Object o1, Object o2) {
        QCustomer customer1 = (QCustomer) o1;
        QCustomer customer2 = (QCustomer) o2;
        switch (type) {
            case 1: // Ticket Time
                return customer1.standTimeinHHMMSS().compareTo(customer2.standTimeinHHMMSS()) * (asc
                    ? 1
                    : -1);
            case 2: // Ticket Number
                return this.compareTicketNumber(customer1, customer2, asc);
            case 3: // Service Name
                return customer1.getService().getName().compareTo(customer2.getService().getName())
                    * (asc
                    ? 1 : -1);
            case 4: // Priority
                return customer1.taskPriority().compareTo(customer2.taskPriority()) * (asc ? 1
                    : -1);
            case 5: // State In
                return customer1.currentStateIn().compareTo(customer2.currentStateIn()) * (asc ? 1
                    : -1);
            default: // Ticket Number //
                return customer1.standTimeinHHMMSS().compareTo(customer2.standTimeinHHMMSS()) * (asc
                    ? 1
                    : -1);
        }
    }

    public int compareTicketNumber(QCustomer customer1, QCustomer customer2, boolean asc) {
        if (customer1.getPrefix().equals(customer2.getPrefix())
            && customer1.getFullNumber().length() != customer2.getFullNumber().length()) {
            if (customer1.getNumber() > customer2.getNumber()) {
                return 1 * (asc ? 1 : -1);
            } else {
                return -1 * (asc ? 1 : -1);
            }
        } else {
            return customer1.getFullNumber().compareTo(customer2.getFullNumber()) * (asc ? 1 : -1);
        }
    }

}
