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
package ru.apertum.qsystem.server.model.monitorium;

import java.util.LinkedList;
import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.server.controller.AIndicatorBoard;

/**
 * Класс для системы внешних табло Monitorium
 * Через этот класс сервер будет взаимодействовать с системой Moniturium.
 * Т.е.выдавать в эту систему всю необходимую информацию по движению бизнеспроцесса.
 *
 * @author Evgeniy Egorov
 */
public class QMonitoriumBoard extends AIndicatorBoard {

    public QMonitoriumBoard() {
        int i = 0;
        //i = i+i;
        //i= i * 83;
        //System.out.println("i = " + i);
    }


    @Override
    protected void showOnBoard(LinkedList<Record> records) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void showToUser(Record record) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showBoard() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Element getConfig() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveConfig(Element element) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AFBoardRedactor getRedactor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getUID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Integer getLinesCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getBoardForm() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
