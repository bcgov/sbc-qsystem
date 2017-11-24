/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsys.quser;

import java.util.HashMap;

/**
 * @author Evgeniy Egorov
 */
public class UsersInside {

    private final HashMap<String, Long> usersInside = new HashMap<>();

    private UsersInside() {
    }

    public static UsersInside getInstance() {
        return UsersInsideHolder.INSTANCE;
    }

    public HashMap<String, Long> getUsersInside() {
        return usersInside;
    }

    private static class UsersInsideHolder {

        private static final UsersInside INSTANCE = new UsersInside();
    }
}
