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
package ru.apertum.qsky.plugins.ws;

import ru.apertum.qsky.plugins.events.WorkerEvents;
import ru.apertum.qsky.plugins.events.EventToSky;
import ru.apertum.qsky.plugins.positions.PositionToSky;
import ru.apertum.qsky.plugins.positions.WorkerPositions;

/**
 * Создает и стартует поток отправки событий WorkerEvents
 * Тут есть метод добавления события на отправку addEvent
 * @author egorov
 */
public class WorkThreads {

    private final Thread threadEvents;
    private final WorkerEvents workerEvents;
    private final Thread threadPositions;
    private final WorkerPositions workerPositions;

    private WorkThreads() {
        workerEvents = new WorkerEvents();
        threadEvents = new Thread(workerEvents);
        workerPositions = new WorkerPositions();
        threadPositions = new Thread(workerPositions);
    }

    public static WorkThreads getInstance() {
        return WorkThreadHolder.INSTANCE;
    }

    private static class WorkThreadHolder {

        private static final WorkThreads INSTANCE = getInstance();

        private static WorkThreads getInstance() {
            final WorkThreads res = new WorkThreads();
            res.threadEvents.start();
            res.threadPositions.start();
            return res;
        }
    }

    public void addEvent(EventToSky event) {
        workerEvents.sendEvent(event);
    }

    public void moveClient(PositionToSky event) {
        workerPositions.sendEvent(event);
    }
}
