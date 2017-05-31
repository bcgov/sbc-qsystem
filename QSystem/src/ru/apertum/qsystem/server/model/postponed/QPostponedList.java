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
package ru.apertum.qsystem.server.model.postponed;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import org.apache.commons.collections.CollectionUtils;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.controller.Executer;

/**
 *
 * @author Evgeniy Egorov
 */
public class QPostponedList extends DefaultListModel {

    public QPostponedList loadPostponedList(LinkedList<QCustomer> customers) {
        clear();
        for (QCustomer cust : customers) {

            boolean fl = true;
            for (int i = 0; i < size(); i++) {
                final QCustomer inn = (QCustomer) get(i);
                if (inn.getPostponedStatus().compareTo(cust.getPostponedStatus()) > 0) {
                    add(i, cust);
                    fl = false;
                    break;
                }
            }
            if (fl) {
                addElement(cust);
            }

        }
        return this;
    }
    /**
     * Таймер по которому будем выгонять временных отложенных
     */
    private Timer timerOut;

    private QPostponedList() {
        if (QConfig.cfg().isServer()) {
            timerOut = new Timer(60 * 1000, (ActionEvent e) -> {
                Executer.POSTPONED_TASK_LOCK.lock();
                try {
                    Executer.CLIENT_TASK_LOCK.lock();
                    try {
                        final ArrayList<QCustomer> forDel = new ArrayList<>();
                        for (QCustomer customer : getPostponedCustomers()) {
                            if (customer.getPostponPeriod() > 0 && customer.getFinishPontpone() < System.currentTimeMillis()) {
                                QLog.l().logger().debug("Перемещение по таймеру из отложенных кастомера №" + customer.getPrefix() + customer.getNumber() + " в услугу \"" + customer.getService().getName() + "\"");
                                // время сидения вышло, пора отправляться в очередь.
                                forDel.add(customer);
                                // в очередь, сукины дети
                                // время постановки проставляется автоматом при создании кастомера.
                                customer.setPriority(customer.getPriority().get() + 1);
                                //добавим нового пользователя
                                customer.getService().addCustomer(customer);
                                // вроде как только что встал в очередь, ну и время проставим, а то ожидание будет огромное
                                // только что встал типо. Поросто время нахождения в отложенных не считаетка как ожидание очереди. Инвче в statistic ожидание огромное
                                customer.setStandTime(new Date());
                                // Состояние у него "Стою, жду".
                                customer.setState(CustomerState.STATE_WAIT_AFTER_POSTPONED);
                                // разослать оповещение
                                Uses.sendUDPBroadcast(customer.getService().getId().toString(), ServerProps.getInstance().getProps().getClientPort());
                            }
                        }
                        forDel.stream().forEach((qCustomer) -> {
                            removeElement(qCustomer);
                        });
                    } catch (Exception ex) {
                        throw new ServerException("Ошибка при постановке клиента в очередь", ex);
                    } finally {
                        Executer.CLIENT_TASK_LOCK.unlock();
                    }
                } catch (Exception ex) {
                    throw new ServerException("Ошибка при перемещении в очередь отложенного из пула по таймеру " + ex);
                } finally {
                    Executer.POSTPONED_TASK_LOCK.unlock();
                }
            });
            timerOut.start();
        }
    }

    public static QPostponedList getInstance() {
        return QPostponedListHolder.INSTANCE;
    }

    private static class QPostponedListHolder {

        private static final QPostponedList INSTANCE = new QPostponedList();
    }

    public LinkedList<QCustomer> getPostponedCustomers() {
        final LinkedList<QCustomer> list = new LinkedList<>();
        CollectionUtils.addAll(list, elements());
        return list;
    }

    /**
     * Может вернуть NULL если не нашлось
     * @param id
     * @return
     */
    public QCustomer getById(long id) {
        for (Object object : toArray()) {
            QCustomer c = (QCustomer) object;
            if (id == c.getId()) {
                return c;
            }
        }
        return null;
    }
}
