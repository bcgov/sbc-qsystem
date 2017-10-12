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
package ru.apertum.qsystem.common;

/**
 * Перечисление состояний кастомера в которых он может находиться
 * @author Evgeniy Egorov
 */
public enum CustomerState {
    // значения состояния "очередника"

    /**
     * 0 удален по неявке :: Was deleted by default
     */
    STATE_DEAD,
    /**
     * 1 стоит и ждет в очереди :: Standing and waiting in line
     */
    STATE_WAIT,
    /**
     * 2 стоит и ждет в очереди после того, как отлежался в отложенных положенное время и автоматически отправился в прежнюю очередь с повышенным приоритетом
     * Stands and waits in the queue after being resting in the pending put time and automatically went to the previous queue with an increased priority
     */
    STATE_WAIT_AFTER_POSTPONED,
    /**
     * 3 Кастомер был опять поставлен в очередь т.к. услуга комплекстая и ждет с номером
     * 3 The customizer was again queued up. Service bundled and waiting with the number
     */
    STATE_WAIT_COMPLEX_SERVICE,
    /**
     * 4 пригласили :: Invited
     */
    STATE_INVITED,
    /**
     * 5 пригласили повторно в цепочке обработки. т.е. клиент вызван к оператору не первый раз а после редиректа или отложенности
     * Was re-invited in the processing chain. those. The client is called to the operator not the first time and after the redirect or deferred
     */
    STATE_INVITED_SECONDARY,
    /**
     * 6 отправили в другую очередь, идет как бы по редиректу в верх.
     * 6 sent to another queue, goes like a redirect to the top.
     * Стоит ждет к новой услуге.
     * Worth waiting for a new service.
     */
    STATE_REDIRECT,
    /**
     * 7 начали с ним работать :: Began to work with him
     */
    STATE_WORK,
    /**
     * 8 начали с ним работать повторно в цепочке обработки
     * 8 They began to work with him again in the processing chain
     */
    STATE_WORK_SECONDARY,
    /**
     * 9 состояние когда кастомер возвращается к прежней услуге после редиректа,
     * по редиректу в низ. Стоит ждет к старой услуге.
     * 9 state when the customizer returns to the previous service after the redirect,
     * On a redirect to the bottom. Worth waiting for the old service.
     */
    STATE_BACK,
    /**
     * 10 с кастомером закончили работать и он идет домой
     * 10 with the customizer finished working and he goes home
     */
    STATE_FINISH,
    /**
     * 11 с кастомером закончили работать и поместили в отложенные. домой не идет, сидит ждет покуда не вызовут.
     * 11 with the customizer finished working and placed in the deferred. Home does not go, sits waiting until they call.
     */
    STATE_POSTPONED,
    
    /**
     * 12 This state is extension for the STATE_POSTPONED, In which we are making start time equal to stand time 
     */
    STATE_POSTPONED_REDIRECT, 
    
       /**
 +     * 13 Inaccurate time. In case CSRs forgets to hit finish immediately after they finishes the job.
 +     */
    
    STATE_INACCURATE_TIME
}
