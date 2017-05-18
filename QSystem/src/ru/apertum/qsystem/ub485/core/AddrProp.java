/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.ub485.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Evgeniy Egorov
 */
public class AddrProp {

    final private HashMap<Long, ButtonDevice> addrs = new HashMap<>();

    public HashMap<Long, ButtonDevice> getAddrs() {
        return addrs;
    }
    final static private File ADDR_FILE = new File("config/qub.adr");

    private AddrProp() {
        try (FileInputStream fis = new FileInputStream(ADDR_FILE); Scanner s = new Scanner(fis)) {
            while (s.hasNextLine()) {
                final String line = s.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    final String[] ss = line.split("=");
                    final String[] ssl = ss[1].split(" ");
                    addrs.put(Long.valueOf(ss[0]), new ButtonDevice(Long.valueOf(ss[0]), Byte.parseByte(ssl[0]), ssl.length == 1 ? null : Long.parseLong(ssl[1])));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
    }

    public static AddrProp getInstance() {
        return AddrPropHolder.INSTANCE;
    }

    private static class AddrPropHolder {

        private static final AddrProp INSTANCE = new AddrProp();
    }

    public ButtonDevice getAddr(Long userId) {
        return addrs.get(userId);
    }
    
    public ButtonDevice getAddrByRSAddr(byte rsAddr) {
        for (ButtonDevice adr : AddrProp.getInstance().getAddrs().values().toArray(new ButtonDevice[0])) {
            if (adr.addres == rsAddr) {
                return adr;
            }
        }
        return null;
    }

    public static void main(String[] ss) {
        System.out.println("addrs:");
        getInstance().addrs.keySet().stream().forEach((l) -> {
            System.out.println(l + "=" + getInstance().getAddr(l).addres
                    + " " + getInstance().getAddr(l).redirectServiceId);
        });
    }
}
