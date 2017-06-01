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
package ru.apertum.qsky.plugins.events;

import java.util.concurrent.LinkedBlockingQueue;
import ru.apertum.qsky.plugins.ws.SkyService;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.ServerProps;

/**
 * Поток с сочередью событий, которые надо отправить на сервер в облако Тут происходит ожидание событий и использование коннектора отсылки SkyService
 *
 * @author egorov
 */
public class WorkerEvents implements Runnable {

    private final LinkedBlockingQueue<EventToSky> eventsToSky = new LinkedBlockingQueue<>();

    public void sendEvent(EventToSky event) {
        eventsToSky.offer(event);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            final EventToSky event;
            try {
                event = eventsToSky.take();
            } catch (InterruptedException ex) {
                QLog.l().logger().error(ex);
                continue;
            }
            try {
                /*
                 *branchId"
                 "serviceId"
                 "employeeId"
                 "customerId"
                 "status"
                 "number"
                 "prefix
                 */
                switch (event.getCustomerState()) {
                    case STATE_DEAD:
                        //(branchId, serviceId, customerId, employeeId, status)
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_WAIT:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                null,
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_WAIT_AFTER_POSTPONED:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                null,
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_WAIT_COMPLEX_SERVICE:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;

                    case STATE_INVITED:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_INVITED_SECONDARY:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_REDIRECT:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getNewServiceId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_WORK:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(), -1, "");
                        break;
                    case STATE_WORK_SECONDARY:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_BACK:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getNewServiceId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_FINISH:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                event.getService().getId(),
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                    case STATE_POSTPONED:
                        SkyService.getInstance().getQsky().changeCustomerStatus(ServerProps.getInstance().getProps().getBranchOfficeId(),
                                null,
                                event.getUser().getId(),
                                event.getCustomer().getId(),
                                event.getCustomer().getState().ordinal(),
                                event.getCustomer().getNumber(),
                                event.getCustomer().getPrefix());
                        break;
                }
            } catch (Exception ex) {
                QLog.l().logger().error("Ошибка с вызовом вебсервиса.", ex);
            }
        }
    }
}
