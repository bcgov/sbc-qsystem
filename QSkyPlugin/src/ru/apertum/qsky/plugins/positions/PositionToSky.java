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
package ru.apertum.qsky.plugins.positions;

import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QService;

/**
 *
 * @author egorov
 */
public class PositionToSky {

    /**
     * 
     */
    public static enum Mode {

        INSERT, REMOVE
    }
    final private Mode mode;
    final private QCustomer customer;
    final private QService service;
    final private QCustomer before;
    final private QCustomer after;

    public PositionToSky(Mode mode, QCustomer customer, QCustomer before, QCustomer after) {
        this.mode = mode;
        this.customer = customer;
        this.service = customer.getService();
        this.before = before;
        this.after = after;
    }

    public QCustomer getAfter() {
        return after;
    }

    public QCustomer getBefore() {
        return before;
    }

    public QCustomer getCustomer() {
        return customer;
    }

    public Mode getMode() {
        return mode;
    }

    public QService getService() {
        return service;
    }
}
