/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.controller;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Evgeniy Egorov
 */
public class PagerAlreadyDone {

    private PagerAlreadyDone() {
    }

    public static PagerAlreadyDone getInstance() {
        return PagerAlreadyDoneHolder.INSTANCE;
    }

    private static class PagerAlreadyDoneHolder {

        private static final PagerAlreadyDone INSTANCE = new PagerAlreadyDone();
    }

    private final HashMap<String, ArrayList<Long>> already = new HashMap<>();

    public void add(String ip, Long dataId) {
        ArrayList<Long> list = already.get(ip);
        if (list == null) {
            list = new ArrayList<>();
            list.add(dataId);
            already.put(ip, list);
        }
        list.add(dataId);
    }

    public boolean check(String ip, Long dataId) {
        final ArrayList<Long> list = already.get(ip);
        if (list == null) {
            return false;
        }
        return list.contains(dataId);
    }
}
