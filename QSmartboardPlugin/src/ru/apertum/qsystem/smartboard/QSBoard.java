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
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.AIndicatorBoard;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author Evgeniy Egorov
 */
public class QSBoard extends AIndicatorBoard {

    public PrintRecords getPrintRecordsByOffice(QOffice office) {
        QLog.l().logQUser().debug("QSBoard: getPrintRecordsByOffice");
        if (office == null) {
            throw new UnsupportedOperationException("Office cannot be null");
        }
        for (PrintRecords pr : PrintRecordsList.getInstance().getPrintRecords()) {
            if (office.equals(pr.getOffice())) {
                return pr;
            }
        }
        throw new UnsupportedOperationException("Office not found.");
    }

    @Override
    public Integer getLinesCount() {
        QLog.l().logQUser().debug("QSBoard: getLinesCount");
        QLog.l().logQUser().debug("getStackTrace");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            QLog.l().logQUser().debug("\tat " + s.getClassName() + "." + s.getMethodName()
                    + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
        }

        return 0;
    }

    @Override
    protected void showOnBoard(LinkedList<Record> records) {
        QLog.l().logQUser().debug("QSBoard: showOnBoard");
        QLog.l().logQUser().debug("getStackTrace");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            QLog.l().logQUser().debug("\tat " + s.getClassName() + "." + s.getMethodName()
                    + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
        }

        return;
    }

    @Override
    public synchronized void inviteCustomer(QUser user, QCustomer customer) {
        QLog.l().logQUser().debug("QSBoard: inviteCustomer");
        super.inviteCustomer(user, customer);

        //Only set invited sound if the service is smartboard enabled
        QService service = customer.getService();
        if (!"Y".equals(service.getSmartboard())) {
            QLog.l().logQUser().debug("Smartboard not enabled for service, return");
            return;
        }
        getPrintRecordsByOffice(user.getOffice()).setInvited(true);
    }

    @Override
    public Integer getLinesCountForOffice(QOffice office) {
        QLog.l().logQUser().debug("QSBoard: getLinesCountForOffice");

        Integer c = getPrintRecordsByOffice(office).getLinesCount();

        QLog.l().logQUser().debug("QSBoard: getLinesCountForOffice. Count: " + c);

        return c;
    }

    protected void showOnBoardForOffice(LinkedList<Record> records, QOffice office) {
        QLog.l().logQUser().debug("QSBoard: showOnBoardForOffice");
        getPrintRecordsByOffice(office).setRecords(records);
    }

    /**
     *
     * @param record
     * @deprecated при конфигурации с мониторами в качестве табло пользовательские моники подключаются к пользовательским компам.
     */
    @Deprecated
    @Override
    protected void showToUser(Record record) {
        QLog.l().logQUser().debug("QSBoard: showToUser");
        System.out.println("Record");
    }

    @Override
    public void refresh() {
        QLog.l().logQUser().debug("QSBoard: refresh");
        System.out.println("Refresh");
    }

    @Override
    public void showBoard() {
        QLog.l().logQUser().debug("QSBoard: showBoard");
        System.out.println("Show Board");
    }

    @Override
    public void clear() {
        QLog.l().logQUser().debug("QSBoard: clear");
        System.out.println("Clear");
        records.clear();
        showOnBoard(new LinkedList(records.values()));
    }

    @Override
    public Element getConfig() {
        QLog.l().logQUser().debug("QSBoard: getConfig");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveConfig(Element element) {
        QLog.l().logQUser().debug("QSBoard: saveConfig");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AFBoardRedactor getRedactor() {
        QLog.l().logQUser().debug("QSBoard: getRedactor");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        QLog.l().logQUser().debug("QSBoard: getDescription");
        return Version.description;
    }

    @Override
    public long getUID() {
        QLog.l().logQUser().debug("QSBoard: getUID");
        return Version.UID;
    }

    @Override
    public Object getBoardForm() {
        QLog.l().logQUser().debug("QSBoard: getBoardForm");
        System.out.println("Strange! Strange! Strange! Strange! Strange!");
        return null;
    }
}
