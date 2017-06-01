/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.common;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Evgeniy Egorov
 */
public class Uses {

    public static Date getNow() {
        final GregorianCalendar gc = new GregorianCalendar();
        String d = System.getProperty("QSKY_TIME_SHIFT", "0");
        if (d.startsWith("-")) {
            d = d.substring(1);
            int sh = Integer.parseInt(d);
            gc.add(GregorianCalendar.MINUTE, -sh);
        } else {
            if (d.startsWith("+")) {
                d = d.substring(1);
            }
            int sh = Integer.parseInt(d);
            gc.add(GregorianCalendar.MINUTE, sh);
        }
        return gc.getTime();
    }

}
