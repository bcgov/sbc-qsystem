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

import java.util.concurrent.LinkedBlockingQueue;
import ru.apertum.qsky.plugins.ws.SkyService;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.ServerProps;

/**
 * Поток с сочередью событий, которые надо отправить на сервер в облако
 * Тут происходит ожидание событий и использование коннектора отсылки SkyService
 * @author egorov
 */
public class WorkerPositions implements Runnable {

    private final LinkedBlockingQueue<PositionToSky> eventsToSky = new LinkedBlockingQueue<>();

    public void sendEvent(PositionToSky event) {
        eventsToSky.offer(event);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            final PositionToSky event;
            try {
                event = eventsToSky.take();
            } catch (InterruptedException ex) {
                QLog.l().logger().error(ex);
                continue;
            }
            try {
                switch (event.getMode()) {
                    case INSERT:
                        SkyService.getInstance().getQsky().insertCustomer(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getCustomer().getId(),
                                event.getBefore() == null ? -1 : event.getBefore().getId(),
                                event.getAfter() == null ? -1 : event.getAfter().getId());
                        break;
                    case REMOVE:
                        SkyService.getInstance().getQsky().removeCustomer(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getCustomer().getId());
                        break;
                }
            } catch (Exception ex) {
                QLog.l().logger().error("Ошибка с вызовом вебсервиса.", ex);
            }
        }
    }
}
