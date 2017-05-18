/*
 * Copyright (C) 2013 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.extra;

/**
 * Get array of bytes from COM port and turn into ID from welcome_buttons.properties
 *
 * @author Evgeniy Egorov
 */
public interface IBytesButtensAdapter extends IExtra {

    /**
     * Message from device turn into ID from welcome_buttons.properties
     *
     * @param bytes data from device for pressing a button
     * @return ID - look out to welcome_buttons.properties
     */
    public Byte convert(byte[] bytes);

}
