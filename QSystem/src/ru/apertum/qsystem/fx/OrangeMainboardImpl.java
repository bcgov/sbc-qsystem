/*
 * Copyright (C) 2011 Евгений
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
package ru.apertum.qsystem.fx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.Timer;
import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.controller.IIndicatorBoard;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author Евгений
 */
public class OrangeMainboardImpl implements IIndicatorBoard {

    final OrangeMainboard board = new OrangeMainboard();

    final Timer timer = new Timer(2000, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            //todo  board.showData(forShow, forBlink);
            timer.stop();
        }
    });
    private boolean flag = true;

    @Override
    public void inviteCustomer(QUser user, QCustomer customer) {
        forBlink.add(user);
        forShow.remove(user);
        forShow.add(user);

        if (!timer.isRunning()) {
            timer.start();
            if (flag) {
                //todo      board.showData(forShow, forBlink);
            }
            flag = false;
        }

        LinkedList<String> nests = new LinkedList<>();
        for (QService service : QServiceTree.getInstance().getNodes()) {
            if (service.isLeaf()) {
                QCustomer customer_next = service.peekCustomer();
                if (customer_next != null) {
                    nests.add(customer_next.getPrefix() + customer_next.getNumber());
                }
            }
        }
        //todo  board.showNextCustomers(nests);
    }
    final private ArrayList<QUser> forBlink = new ArrayList<>();
    final private ArrayList<QUser> forShow = new ArrayList<>();

    @Override
    public void workCustomer(QUser user) {
        forBlink.remove(user);
        if (!timer.isRunning()) {
            //todo   board.showData(forShow, forBlink);
        }
    }

    @Override
    public void killCustomer(QUser user) {
        forBlink.remove(user);
        forShow.remove(user);
        if (!timer.isRunning()) {
            //todo  board.showData(forShow, forBlink);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public void showBoard() {
        board.showBoard();
    }

    @Override
    public Element getConfig() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveConfig(Element element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AFBoardRedactor getRedactor() {
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
    public Object getBoardForm() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
