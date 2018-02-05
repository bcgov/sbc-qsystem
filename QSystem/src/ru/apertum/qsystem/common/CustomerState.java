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
 * ÐŸÐµÑ€ÐµÑ‡Ð¸Ñ�Ð»ÐµÐ½Ð¸Ðµ Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ð¹ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð° Ð² ÐºÐ¾Ñ‚Ð¾Ñ€Ñ‹Ñ… Ð¾Ð½ Ð¼Ð¾Ð¶ÐµÑ‚ Ð½Ð°Ñ…Ð¾Ð´Ð¸Ñ‚ÑŒÑ�Ñ�
 *
 * @author Evgeniy Egorov
 */
public enum CustomerState {
    // Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸Ñ� Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ñ� "Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð½Ð¸ÐºÐ°"

    /**
     * 0 ÑƒÐ´Ð°Ð»ÐµÐ½ Ð¿Ð¾ Ð½ÐµÑ�Ð²ÐºÐµ :: Was deleted by default
     */
    STATE_DEAD,
    /**
     * 1 Ñ�Ñ‚Ð¾Ð¸Ñ‚ Ð¸ Ð¶Ð´ÐµÑ‚ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ :: Standing and waiting in line
     */
    STATE_WAIT,
    /**
     * 2 Ñ�Ñ‚Ð¾Ð¸Ñ‚ Ð¸ Ð¶Ð´ÐµÑ‚ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ Ð¿Ð¾Ñ�Ð»Ðµ Ñ‚Ð¾Ð³Ð¾, ÐºÐ°Ðº Ð¾Ñ‚Ð»ÐµÐ¶Ð°Ð»Ñ�Ñ� Ð² Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ñ‹Ñ… Ð¿Ð¾Ð»Ð¾Ð¶ÐµÐ½Ð½Ð¾Ðµ Ð²Ñ€ÐµÐ¼Ñ� Ð¸
     * Ð°Ð²Ñ‚Ð¾Ð¼Ð°Ñ‚Ð¸Ñ‡ÐµÑ�ÐºÐ¸ Ð¾Ñ‚Ð¿Ñ€Ð°Ð²Ð¸Ð»Ñ�Ñ� Ð² Ð¿Ñ€ÐµÐ¶Ð½ÑŽÑŽ Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ñ� Ð¿Ð¾Ð²Ñ‹ÑˆÐµÐ½Ð½Ñ‹Ð¼ Ð¿Ñ€Ð¸Ð¾Ñ€Ð¸Ñ‚ÐµÑ‚Ð¾Ð¼
     * Stands and waits in the
     * queue after being resting in the pending put time and automatically went to the previous
     * queue with an increased priority
     */
    STATE_WAIT_AFTER_POSTPONED,
    /**
     * 3 ÐšÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€ Ð±Ñ‹Ð» Ð¾Ð¿Ñ�Ñ‚ÑŒ Ð¿Ð¾Ñ�Ñ‚Ð°Ð²Ð»ÐµÐ½ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ñ‚.Ðº. ÑƒÑ�Ð»ÑƒÐ³Ð° ÐºÐ¾Ð¼Ð¿Ð»ÐµÐºÑ�Ñ‚Ð°Ñ� Ð¸ Ð¶Ð´ÐµÑ‚ Ñ� Ð½Ð¾Ð¼ÐµÑ€Ð¾Ð¼ 3
     * The customizer was again queued up. Service bundled and waiting with the number
     */
    STATE_WAIT_COMPLEX_SERVICE,
    /**
     * 4 Ð¿Ñ€Ð¸Ð³Ð»Ð°Ñ�Ð¸Ð»Ð¸ :: Invited
     */
    STATE_INVITED,
    /**
     * 5 Ð¿Ñ€Ð¸Ð³Ð»Ð°Ñ�Ð¸Ð»Ð¸ Ð¿Ð¾Ð²Ñ‚Ð¾Ñ€Ð½Ð¾ Ð² Ñ†ÐµÐ¿Ð¾Ñ‡ÐºÐµ Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚ÐºÐ¸. Ñ‚.Ðµ. ÐºÐ»Ð¸ÐµÐ½Ñ‚ Ð²Ñ‹Ð·Ð²Ð°Ð½ Ðº Ð¾Ð¿ÐµÑ€Ð°Ñ‚Ð¾Ñ€Ñƒ Ð½Ðµ Ð¿ÐµÑ€Ð²Ñ‹Ð¹ Ñ€Ð°Ð· Ð°
     * Ð¿Ð¾Ñ�Ð»Ðµ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ð° Ð¸Ð»Ð¸ Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ð¾Ñ�Ñ‚Ð
     * Was re-invited in the processing chain. those. The client is
     * called to the operator not the first time and after the redirect or deferred
     */
    STATE_INVITED_SECONDARY,
    /**
     * 6 Ð¾Ñ‚Ð¿Ñ€Ð°Ð²Ð¸Ð»Ð¸ Ð² Ð´Ñ€ÑƒÐ³ÑƒÑŽ Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ, Ð¸Ð´ÐµÑ‚ ÐºÐ°Ðº Ð±Ñ‹ Ð¿Ð¾ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ñƒ Ð² Ð²ÐµÑ€Ñ….
     *  Ð¡Ñ‚Ð¾Ð¸Ñ‚ Ð¶Ð´ÐµÑ‚ Ðº Ð½Ð¾Ð²Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ.
     * 6 sent to another queue, goes like a redirect to the top. Worth waiting for a new service.
     */
    STATE_REDIRECT,
    /**
     * 7 Ð½Ð°Ñ‡Ð°Ð»Ð¸ Ñ� Ð½Ð¸Ð¼ Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ñ‚ÑŒ ::
     * Began to work with him
     */
    STATE_WORK,
    /**
     * 8 Ð½Ð°Ñ‡Ð°Ð»Ð¸ Ñ� Ð½Ð¸Ð¼ Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ñ‚ÑŒ Ð¿Ð¾Ð²Ñ‚Ð¾Ñ€Ð½Ð¾ Ð² Ñ†ÐµÐ¿Ð¾Ñ‡ÐºÐµ Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚ÐºÐ¸ 8 They began to work with him again in
     * the processing chain
     */
    STATE_WORK_SECONDARY,
    /**
     * 9 Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ ÐºÐ¾Ð³Ð´Ð° ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€ Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚Ñ�Ñ� Ðº Ð¿Ñ€ÐµÐ¶Ð½ÐµÐ¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ Ð¿Ð¾Ñ�Ð»Ðµ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ð°, Ð¿Ð¾ Ñ€ÐµÐ´Ð¸Ñ€ÐµÐºÑ‚Ñƒ Ð² Ð½Ð¸Ð·.
     * Ð¡Ñ‚Ð¾Ð¸Ñ‚ Ð¶Ð´ÐµÑ‚ Ðº Ñ�Ñ‚Ð°Ñ€Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ. 9 state when the customizer returns to the previous service after
     * the redirect, Â Â Â Â Â * On a redirect to the bottom. Worth waiting for the old service.
     */
    STATE_BACK,
    /**
     * 10 Ñ� ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð¼ Ð·Ð°ÐºÐ¾Ð½Ñ‡Ð¸Ð»Ð¸ Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ñ‚ÑŒ Ð¸ Ð¾Ð½ Ð¸Ð´ÐµÑ‚ Ð´Ð¾Ð¼Ð¾Ð¹ 10 with the customizer finished working
     * and he goes home
     */
    STATE_FINISH,
    /**
     * 11 Ñ� ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð¼ Ð·Ð°ÐºÐ¾Ð½Ñ‡Ð¸Ð»Ð¸ Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ñ‚ÑŒ Ð¸ Ð¿Ð¾Ð¼ÐµÑ�Ñ‚Ð¸Ð»Ð¸ Ð² Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ñ‹Ðµ. Ð´Ð¾Ð¼Ð¾Ð¹ Ð½Ðµ Ð¸Ð´ÐµÑ‚, Ñ�Ð¸Ð´Ð¸Ñ‚ Ð¶Ð´ÐµÑ‚ Ð¿Ð¾ÐºÑƒÐ´Ð°
     * Ð½Ðµ Ð²Ñ‹Ð·Ð¾Ð²ÑƒÑ‚. 11 with the customizer finished working and placed in the deferred. Home does not
     * go, sits waiting until they call.
     */
    STATE_POSTPONED,

    /**
     * 12 This state is extension for the STATE_POSTPONED, In which we are making start time equal
     * to stand time
     */
    STATE_POSTPONED_REDIRECT,

    /**
     * +     * 13 Inaccurate time. In case CSRs forgets to hit finish immediately after they
     * finishes the job. +
     */

    STATE_INACCURATE_TIME
}
