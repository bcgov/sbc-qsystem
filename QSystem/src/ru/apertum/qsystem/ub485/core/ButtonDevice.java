/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.ub485.core;

import java.io.UnsupportedEncodingException;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.NetCommander;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IButtonDeviceFuctory.IButtonDevice;
import ru.apertum.qsystem.server.model.QUser;

/**
 * Default buttons
 *
 * @author Evgeniy Egorov
 */
public class ButtonDevice extends Object implements IButtonDevice {

    public final byte addres;
    public final boolean redirect;
    public final Long redirectServiceId;
    public final Long userId;
    private QUser user;
    public Integer qsize = 0;

    public void setQsize(Integer qsize) {
        if ((user == null
                || user.getShadow() == null
                || user.getShadow().getCustomerState() == null
                || user.getShadow().getCustomerState() == CustomerState.STATE_BACK
                || user.getShadow().getCustomerState() == CustomerState.STATE_DEAD
                || user.getShadow().getCustomerState() == CustomerState.STATE_FINISH
                || user.getShadow().getCustomerState() == CustomerState.STATE_POSTPONED
                || user.getShadow().getCustomerState() == CustomerState.STATE_REDIRECT
                || user.getShadow().getCustomerState() == CustomerState.STATE_REDIRECT)
                && (this.qsize == 0 && qsize != 0)) {
            beReadyBeep();
        }
        this.qsize = qsize;
    }
    public String serveceName = null;

    public ButtonDevice(Long userId, byte addres, Long redirectServiceId) {
        this.addres = addres;
        this.userId = userId;
        this.redirectServiceId = redirectServiceId;
        this.redirect = redirectServiceId != null && redirectServiceId != 0;

        mess[0] = 1;
        mess[1] = addres;
        mess[mess.length - 1] = 7;
    }

    @Override
    public String toString() {
        return user == null ? String.valueOf(userId) : user.getName();
    }

    /*
     * mess[2] = 
     * 0x30 – светодиод погашен;
     0x31 – включен Красный;
     0x32 – включен Зеленый;
     0x33 – мигает Красный (200 мс);
     0x34 – мигает Зеленый (200 мс);
     0x35 – мигает Красный (500 мс);
     0x36 – мигает Зеленый (500 мс);
     0x37 – писк (500 мс) + светодиод погашен;
     0x38 – писк (500 мс) + включен Красный;
     0x39 – писк (500 мс) + включен Зеленый;
     0x3A – писк (500 мс) + мигает Красный (200 мс);
     0x3B – писк (500 мс) + мигает Зеленый (200 мс);
     0x3C – писк (500 мс) + мигает Красный (500 мс);
     0x3D – писк (500 мс) + мигает Зеленый (500 мс).
     */
    final private byte[] mess = new byte[4];

    /**
     * Тут вся логика работы кнопок и их нажатия
     *
     * @param bb это команда от устройства
     */
    @Override
    public void doAction(byte[] bb) {
        byte b = bb[2];
        switch (b) {
            case 0x31:
                System.out.println("b == 0x31 -- 49");
                break;
            case 0x32:
                System.out.println("b == 0x32 -- 50");
                break;
            case 0x33:
                System.out.println("b == 0x33 -- 51");
                break;
            case 0x34:
                System.out.println("b == 0x34 -- 52");
                break;
            default:
                System.out.println("PIZDEzzzz.....");
                break;
        }
        if (user == null) {
            System.out.println("user == null");
        } else if (user.getShadow() == null) {
            System.out.println("user.getShadow() == null");
        } else if (user.getShadow().getCustomerState() == null) {
            System.out.println("user.getShadow().getCustomerState() == null");
        } else {
            System.out.println("user.getShadow().getCustomerState() == " + user.getShadow().getCustomerState());
        }
        // первичный вызов
        if ((user.getShadow() == null
                || user.getShadow().getCustomerState() == null
                || user.getShadow().getCustomerState() == CustomerState.STATE_BACK
                || user.getShadow().getCustomerState() == CustomerState.STATE_DEAD
                || user.getShadow().getCustomerState() == CustomerState.STATE_FINISH
                || user.getShadow().getCustomerState() == CustomerState.STATE_POSTPONED
                || user.getShadow().getCustomerState() == CustomerState.STATE_REDIRECT)
                && (b == 0x31)) {
            //команда вызова кастомера
            System.out.println("Invite Next Customer by " + user.getName());
            final QCustomer cust = NetCommander.inviteNextCustomer(UBForm.form.netProperty, userId);
            System.out.println("inv ** 0");
            if (cust != null) {
                System.out.println("inv ** 1");
                if (user.getShadow() == null) {
                    user.setShadow(new QUser.Shadow(cust));
                    System.out.println("inv ** 1`");
                }
                user.getShadow().setCustomerState(cust.getState());
                System.out.println("inv ** 2");
                user.getShadow().setOldPref(cust.getPrefix());
                System.out.println("inv ** 3");
                user.getShadow().setOldNom(cust.getNumber());
                System.out.println("inv ** 4");

                //добавляем табло на пульте
                byte[] bytes = mess;
                try {
                    bytes = ("123" + (cust.getFullNumber() + "    ").substring(0, 3) + "7").getBytes("cp1251");
                } catch (UnsupportedEncodingException ex) {
                    System.err.println("!!! ERROR !!! " + ex);
                }
                bytes[0] = 1;
                bytes[1] = addres;
                bytes[bytes.length - 1] = 7;
                //mess[0] = 0x01; // начало
                //mess[10] = 0x07; // конец
                //mess[1] = addr.addres; // адрес
                bytes[2] = 0x21;//0x20; // мигание Режим мигания: 0x20 – не мигает; 0x21 – мигает постоянно; 0x22…0x7F – мигает  (N-0x21) раз.
                UBForm.sendToDevice(bytes);
                /*
                 try {
                 Thread.sleep(150);
                 } catch (InterruptedException ex) {
                 }

                 //ответ о результате на кнопку
                 mess[2] = 0x36; // – мигает Зеленый (500 мс);
                 UBForm.sendToDevice(mess);
                 */
            } else {
                System.out.println("inv ** 5");
                user.getShadow().setCustomerState(CustomerState.STATE_FINISH);
                System.out.println("inv ** 6");
                lightDown();
                System.out.println("inv ** 7");
            }
            System.out.println("inv ** 8");
            return;
        }

        // повторный вызов
        if ((user != null && user.getShadow() != null && user.getShadow().getCustomerState() != null)
                && (user.getShadow().getCustomerState() == CustomerState.STATE_INVITED || user.getShadow().getCustomerState() == CustomerState.STATE_INVITED_SECONDARY)
                && (b == 0x31)) {
            //команда вызова кастомера
            System.out.println("Recall by " + user.getName());
            final QCustomer cust = NetCommander.inviteNextCustomer(UBForm.form.netProperty, userId);
            System.out.println("--<>\n");
            return;
        }

        // начало работы
        if ((user != null && user.getShadow() != null && user.getShadow().getCustomerState() != null)
                && (user.getShadow().getCustomerState() == CustomerState.STATE_INVITED || user.getShadow().getCustomerState() == CustomerState.STATE_INVITED_SECONDARY)
                && (b == 0x32)) {
            //команда вызова кастомера
            System.out.println("get Start Customer by " + user.getName());
            NetCommander.getStartCustomer(UBForm.form.netProperty, userId);
            System.out.println("--1\n");
            user.getShadow().setCustomerState(CustomerState.STATE_WORK);
            System.out.println("--2\n");

            //добавляем табло на пульте
            byte[] bytes = mess;
            try {
                bytes = ("123" + (user.getCustomer().getFullNumber() + "    ").substring(0, 3) + "7").getBytes("cp1251");
            } catch (UnsupportedEncodingException ex) {
                System.err.println("!!! ERROR !!! " + ex);
            }
            bytes[0] = 1;
            bytes[1] = addres;
            bytes[bytes.length - 1] = 7;
            //mess[0] = 0x01; // начало
            //mess[10] = 0x07; // конец
            //mess[1] = addr.addres; // адрес
            bytes[2] = 0x20;//0x20; // мигание Режим мигания: 0x20 – не мигает; 0x21 – мигает постоянно; 0x22…0x7F – мигает  (N-0x21) раз.
            UBForm.sendToDevice(bytes);
            /*
             try {
             Thread.sleep(150);
             } catch (InterruptedException ex) {
             }

             //ответ о результате на кнопку
             mess[2] = 0x32; //– включен Зеленый;
             System.out.println("--3\n");
             UBForm.sendToDevice(mess);
             System.out.println("--4\n");
             */
            return;
        }

        // отклонить по неявке
        if ((user != null && user.getShadow() != null && user.getShadow().getCustomerState() != null)
                && (user.getShadow().getCustomerState() == CustomerState.STATE_INVITED || user.getShadow().getCustomerState() == CustomerState.STATE_INVITED_SECONDARY)
                && (b == 0x34)) {
            //команда вызова кастомера
            System.out.println("kill Next Customer by " + user.getName());
            NetCommander.killNextCustomer(UBForm.form.netProperty, userId, null);
            user.getShadow().setCustomerState(CustomerState.STATE_DEAD);
            //ответ о результате на кнопку
            if (qsize == 0) {
                lightDown();
            } else {
                beReady();
            }
            return;
        }

        // закончить работу
        if ((user != null && user.getShadow() != null && user.getShadow().getCustomerState() != null)
                && (user.getShadow().getCustomerState() == CustomerState.STATE_WORK || user.getShadow().getCustomerState() == CustomerState.STATE_WORK_SECONDARY)
                && (b == 0x34)) {
            //команда завершения работы

            System.out.println("get Finish Customer by " + user.getName());
            NetCommander.getFinishCustomer(UBForm.form.netProperty, userId, null, -1L, "");
            user.getShadow().setCustomerState(CustomerState.STATE_FINISH);
            //ответ о результате на кнопку
            if (qsize == 0) {
                lightDown();
            } else {
                beReady();
            }
            return;
        }

        // закончить работу
        if ((user != null && user.getShadow() != null && user.getShadow().getCustomerState() != null)
                && (user.getShadow().getCustomerState() == CustomerState.STATE_WORK || user.getShadow().getCustomerState() == CustomerState.STATE_WORK_SECONDARY)
                && (b == 0x33) && redirect) {
            //команда  редирект
            System.out.println("redirect Customer by " + user.getName());
            NetCommander.redirectCustomer(UBForm.form.netProperty, userId, null, redirectServiceId, false, "", -1L);
            user.getShadow().setCustomerState(CustomerState.STATE_FINISH);
            //ответ о результате на кнопку
            if (qsize == 0) {
                lightDown();
            } else {
                beReady();
            }
        }

        // обновить состояние
        /*
         if (b == 0x34) {
         if (user != null
         || user.getShadow() != null
         || user.getShadow().getCustomerState() != null) {
         if (qsize == 0) {
         lightDown();
         } else {
         beReady();
         }
         } else {
         if (user.getShadow().getCustomerState() == CustomerState.STATE_INVITED
         || user.getShadow().getCustomerState() == CustomerState.STATE_INVITED_SECONDARY) {
         mess[2] = 0x36; // – мигает Зеленый (500 мс);
         UBForm.sendToDevice(mess);
         } else {
         if (user.getShadow().getCustomerState() == CustomerState.STATE_WORK
         || user.getShadow().getCustomerState() == CustomerState.STATE_WORK_SECONDARY) {
         mess[2] = 0x32; //– включен Зеленый;
         UBForm.sendToDevice(mess);
         } else {
         if (qsize == 0) {
         lightDown();
         } else {
         beReady();
         }
         }
         }
         }
         }
         * */
    }

    private void beReady() {
        System.out.println("beReady()");

        //добавляем табло на пульте
        byte[] bytes = mess;
        try {
            bytes = ("123CAL7").getBytes("cp1251");
        } catch (UnsupportedEncodingException ex) {
            System.err.println("!!! ERROR !!! " + ex);
        }
        bytes[0] = 1;
        bytes[1] = addres;
        bytes[bytes.length - 1] = 7;
        //mess[0] = 0x01; // начало
        //mess[10] = 0x07; // конец
        //mess[1] = addr.addres; // адрес
        bytes[2] = 0x20;//0x20; // мигание Режим мигания: 0x20 – не мигает; 0x21 – мигает постоянно; 0x22…0x7F – мигает  (N-0x21) раз.
        UBForm.sendToDevice(bytes);

        /*
         mess[2] = 0x34;// – мигает Зеленый (200 мс);
         UBForm.sendToDevice(mess);
         */
    }

    private void beReadyBeep() {
        System.out.println("beReadyBeep()");

        //добавляем табло на пульте
        byte[] bytes = mess;
        try {
            bytes = ("123CAL7").getBytes("cp1251");
        } catch (UnsupportedEncodingException ex) {
            System.err.println("!!! ERROR !!! " + ex);
        }
        bytes[0] = 1;
        bytes[1] = addres;
        bytes[bytes.length - 1] = 7;
        //mess[0] = 0x01; // начало
        //mess[10] = 0x07; // конец
        //mess[1] = addr.addres; // адрес
        bytes[2] = 0x21;//0x20; // мигание Режим мигания: 0x20 – не мигает; 0x21 – мигает постоянно; 0x22…0x7F – мигает  (N-0x21) раз.
        UBForm.sendToDevice(bytes);

        /*
         mess[2] = 0x3B; // – писк (500 мс) + мигает Зеленый (200 мс);
         UBForm.sendToDevice(mess);
         */
    }

    private void lightDown() {
        System.out.println("lightDown()");

        //добавляем табло на пульте
        byte[] bytes = mess;
        try {
            bytes = ("123    7").getBytes("cp1251");
        } catch (UnsupportedEncodingException ex) {
            System.err.println("!!! ERROR !!! " + ex);
        }
        bytes[0] = 1;
        bytes[1] = addres;
        bytes[bytes.length - 1] = 7;
        //mess[0] = 0x01; // начало
        //mess[10] = 0x07; // конец
        //mess[1] = addr.addres; // адрес
        bytes[2] = 0x20;//0x20; // мигание Режим мигания: 0x20 – не мигает; 0x21 – мигает постоянно; 0x22…0x7F – мигает  (N-0x21) раз.
        UBForm.sendToDevice(bytes);
        /*
         try {
         Thread.sleep(150);
         } catch (InterruptedException ex) {
         }

         mess[2] = 0x30;// – светодиод погашен;
         UBForm.sendToDevice(mess);
         */
    }

    @Override
    public void getFeedback() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changeAdress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void check() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public QUser getUser() {
        return user;
    }

    @Override
    public String getId() {
        byte[] bb = new byte[1];
        bb[0] = addres;
        return new String(bb);
    }

    @Override
    public void setUser(QUser user) {
        this.user = user;
    }
}
