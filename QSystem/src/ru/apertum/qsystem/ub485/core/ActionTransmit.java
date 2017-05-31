/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.ub485.core;

import java.util.ServiceLoader;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.extra.IButtonDeviceFuctory;

/**
 * Класс содержит код для распараллеливаия обработки пришедшего пакета
 *
 * @author Evgeniy Egorov
 */
public class ActionTransmit implements Runnable {

    private byte[] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public ActionTransmit() {
        this.bytes = new byte[0];
    }

    @Override
    public void run() {
        // поддержка расширяемости плагинами
        IButtonDeviceFuctory devFuctory = null;
        for (final IButtonDeviceFuctory event : ServiceLoader.load(IButtonDeviceFuctory.class)) {
            QLog.l().logger().info("Invoke SPI ext. Description: " + event.getDescription());
            devFuctory = event;
            break;
        }

        if (devFuctory != null || (bytes.length == 4 && bytes[0] == 0x01 && bytes[3] == 0x07)) {
            // должно быть 4 байта, иначе коллизия
            final IButtonDeviceFuctory.IButtonDevice dev = devFuctory == null ? AddrProp.getInstance().getAddrByRSAddr(bytes[1]) : devFuctory.getButtonDevice(bytes, UBForm.users);
            if (dev == null) {
                throw new RuntimeException("Anknown address from user device. " + (devFuctory == null ? "Hohlov device." : (devFuctory.toString() + " key=" + new String(bytes))));
            }
            dev.doAction(bytes);
        } else {
            System.err.println("Collision! Package lenght is not 4 bytes.");
        }
    }
}
