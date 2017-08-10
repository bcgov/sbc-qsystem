/*
 * Copyright (C) 2015 Evgeniy Egorov
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
package ru.apertum.qsystem.smartboard;

import java.util.LinkedList;
import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.AIndicatorBoard;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author Evgeniy Egorov
 */
public class QSBoard extends AIndicatorBoard {

    @Override
    public Integer getLinesCount() {        
        return 0;
        // return PrintRecords.getInstance().getLinesCount();
    }

    @Override
    protected void showOnBoard(LinkedList<Record> records) {
        PrintRecords.getInstance().setRecords(records);
    }

    @Override
    public synchronized void inviteCustomer(QUser user, QCustomer customer) {
        super.inviteCustomer(user, customer);
        PrintRecords.getInstance().setInvited(true);
    }

    /**
     *
     * @param record
     * @deprecated при конфигурации с мониторами в качестве табло пользовательские моники подключаются к пользовательским компам.
     */
    @Deprecated
    @Override
    protected void showToUser(Record record) {
    }

    @Override
    public void refresh() {
    }

    @Override
    public void showBoard() {
    }

    @Override
    public void clear() {
        records.clear();
        showOnBoard(new LinkedList(records.values()));
    }

    @Override
    public Element getConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveConfig(Element element) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AFBoardRedactor getRedactor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        return Version.description;
    }

    @Override
    public long getUID() {
        return Version.UID;
    }

    @Override
    public Object getBoardForm() {
        System.out.println("Strange! Strange! Strange! Strange! Strange!");
        return null;
    }
}
