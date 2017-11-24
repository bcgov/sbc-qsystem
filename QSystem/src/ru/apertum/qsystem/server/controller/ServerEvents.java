/*
 *  Copyright (C) 2011 egorov
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
package ru.apertum.qsystem.server.controller;

import java.util.ArrayList;

/**
 * @author egorov
 */
public class ServerEvents implements IServerListener {

    private final ArrayList<IServerListener> listeners = new ArrayList<>();

    private ServerEvents() {
    }

    public static ServerEvents getInstance() {
        return ServerEventsHolder.INSTANCE;
    }

    public void registerListener(IServerListener listener) {
        listeners.add(listener);
    }

    @Override
    public void restartEvent() {
        listeners.stream().forEach((listener) -> {
            listener.restartEvent();
        });
    }

    private static class ServerEventsHolder {

        private static final ServerEvents INSTANCE = new ServerEvents();
    }
}
