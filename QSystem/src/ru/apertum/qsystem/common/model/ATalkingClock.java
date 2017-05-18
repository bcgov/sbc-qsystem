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
package ru.apertum.qsystem.common.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Таймер, однако. Кудаж без него. В наследниках реализовать метод run(). Умеет отмерять единичные интервалы времени.
 *
 * @author Evgeniy Egorov
 */
public abstract class ATalkingClock {

    /**
     * Количество срабатываний таймера
     */
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    private int cntr = 0;
    /**
     * Интервал в мсек.
     */
    private int interval = 500;

    public int getInterval() {
        return interval;
    }

    public final void setInterval(int interval) {
        this.interval = interval;
    }
    private final ActionListener timeAction = (ActionEvent e) -> {
        run();
        if (count != 0) {
            cntr++;
            if (cntr >= count) {
                stop();
            }
        }
    };
    private Timer t;
    private boolean active = false;

    /**
     * Реализовать в наследниках. Этот метод выполнится по таймеру.
     */
    abstract public void run();

    /**
     * Конструктор таймера
     *
     * @param interval интервал срабатываний таймера
     * @param count количество срабатываний таймера, если 0 то постоянное
     */
    public ATalkingClock(int interval, int count) {
        setInterval(interval);
        this.count = count;
    }

    /**
     * Запуск таймера. При старте каждый раз создается новый таймер, не используется старый, т.к. интервал ему можно сменить тока с глюками влюбой момент перед
     * стартом.
     */
    public void start() {
        if (isActive()) {
            stop();
        }
        cntr = 0;
        t = new Timer(getInterval(), timeAction);
        t.start();
        active = true;
    }

    /**
     * Останов таймера
     */
    public void stop() {
        cntr = 0;
        active = false;
        t.stop();
    }

    /**
     * Проверка активности таймера
     *
     * @return признай активности
     */
    public boolean isActive() {
        return active;
    }
}
