/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.plugins.ping;

/**
 * Класс проверки доступности облака
 * @author egorov
 */
public class PingResult {

    private final int result;

    private PingResult() {
        int res = -1;
        try {
            res = new Pinger().ping();
        } catch (Throwable ex) {
            res = -1;
        }
        result = res;
    }

    public boolean isReady() {
        return true; //result == 1;
    }

    public static PingResult getInstance() {
        return PingResultHolder.INSTANCE;
    }

    private static class PingResultHolder {

        private static final PingResult INSTANCE = new PingResult();
    }
}
