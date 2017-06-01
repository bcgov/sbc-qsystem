/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.controller;

import java.util.Map;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;
import ru.apertum.qsky.web.User;

/**
 *
 * @author Evgeniy Egorov
 */
public class AuthenticationInit implements Initiator {

    @Override
    public void doInit(Page page, Map<String, Object> map) throws Exception {
        final User cl = (User) Sessions.getCurrent().getAttribute("USER");
        if (cl == null) {
            Executions.sendRedirect("/");
        }
    }
}
