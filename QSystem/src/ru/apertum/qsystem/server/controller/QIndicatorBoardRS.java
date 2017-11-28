/*
 *  Copyright (C) 2010 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Timer;
import org.dom4j.Element;
import ru.apertum.qsystem.client.forms.AFBoardRedactor;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QUser;
import ru.evgenic.rxtx.serialPort.ISerialPort;

/**
 * Движок для герлянды табло на RS.
 *
 * @author Evgeniy Egorov
 */
public class QIndicatorBoardRS extends AIndicatorBoard {

    //******************************************************************************************************************
    //******************************************************************************************************************
    //***************************************** таймер вывода времени  *************************************************
    private static final int DELAY_BLINK = 5000;
    /**
     * Таймер вывода времени.
     */
    private final Timer timer = new Timer(DELAY_BLINK, new TimerPrinter());
    /**
     * Поле для списка адресов многострочного табло.
     */
    private List<Integer> adresses;
    /**
     * COM порт через который будем работать с герляндой.
     */
    private ISerialPort serialPort;

    public QIndicatorBoardRS() {
        // нет мигания - нет его старта. устройства все равно не переварят
        //startBlinkTimer();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        timer.stop();
    }

    /**
     * @return the adresses.
     */
    public List<Integer> getAdresses() {
        return adresses;
    }

    /**
     * @param adresses The adresses to set.
     */
    public void setAdresses(List<Integer> adresses) {
        this.adresses = adresses;
        timer.start();
        timer.setDelay(60000);
    }

    @Override
    public void refresh() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException(
            "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getUID() {
        throw new UnsupportedOperationException(
            "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Integer getLinesCount() {
        throw new UnsupportedOperationException(
            "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getBoardForm() {
        return null;
    }

    ;

    //***************************************** таймер вывода времени  *************************************************
    @Override
    public void inviteCustomer(QUser user, QCustomer customer) {
        // У юзера высветим номер кастомера и заставим мирать.
        final String point = user.getPoint();
        final String customerNumber = customer.getPrefix() + customer.getNumber();
        final Integer adressRS = user.getAdressRS();
        /*if (records.containsKey(point)) {
            // Повторный вызов

            final Record record = records.get(point);
            //Опустим всех перед ним
            for (Record rec : records.values()) {
                if (rec.onBoard < record.onBoard) {
                    rec.onBoard++;
                    showToBoard(rec);
                }
            }
            // Поствим перевызванного на первое место
            record.onBoard = 0;
            showToBoard(record);
        // Заставим моргать, не моргаем
        //record.showShortBlink();

        } else {
            // Первичный вызов
            // Опустим уже вызнанных
            for (Record record : records.values()) {
                record.onBoard++;
                showToBoard(record);
            }
            // Добавим нового кастомеру.
            show(new Record(point, customerNumber, user.getAdressRS()));
        }*/
    }

    @Override
    public void workCustomer(QUser user) {
        // Прекратить мигание номера юзера.
        // Да не мигает ОН!!!
        //final Record rec = records.get(user.getPoint());
        //rec.stopBlink();
        //show(rec);
    }

    @Override
    public void killCustomer(QUser user) {
        // У юзера подотреем номер кастомера.
        final String point = user.getPoint();
        final Integer adressRS = user.getAdressRS();
        /*final int onBoard = records.get(point).onBoard;
        // замочим стоявшего
        // на табло оператора
        sendMessage("   ", adressRS.byteValue());
        // в списке строк
        records.remove(point);
        // на большом табло
        int max = onBoard;
        //Поднимим всех перед ним
        for (Record record : records.values()) {
            if (record.onBoard >= onBoard) {
                if (record.onBoard > max) {
                    max = record.onBoard;
                }
                record.onBoard--;
                showToBoard(record);
            }
        }
        // подотрем освободившуюся внизу строку.
        if (max >= 0 && max < adresses.size() - 1) {
            sendMessage("   ", adresses.get(max).byteValue());
        }*/
    }

    /**
     * Вывод записи только на общее табло
     */
    protected void showToBoard(Record record) {
        //if (record.onBoard >= 0 && record.onBoard < adresses.size() - 1) {
        //  sendMessage(record.customerNumber + Uses.getNumeration().getDelimiter() + record.point, adresses.get(record.onBoard).byteValue());
        // }
    }

    /**
     * Вывод записи только на табло оператора
     */
    @Override
    protected void showToUser(Record record) {
        sendMessage(
            record.customerPrefix + (record.customerNumber == null ? "" : record.customerNumber),
            record.adressRS.byteValue());
    }


    @Override
    protected void show(Record record) {
        // У юзера высветим номер кастомера и если надо на главном табло.
        String nom = record.customerPrefix + record.customerNumber;
        // высветим на главном табло если нужно
        showToBoard(record);
        if (nom.length() < 3) {
            nom = " " + nom + "  ";
        }
        sendMessage(nom, record.adressRS.byteValue());

    }


    protected void clear(Integer adress, Record record) {
        // чтоб перестало моргать на главном табло через некоторое время.
        if (record != null) {
            //   if (record.onBoard >= 0 && record.onBoard < adresses.size() - 1) {
            //      sendMessage("       ", adresses.get(record.onBoard).byteValue());
            //  }
        }
        // У юзера подотреем номер кастомера.
        sendMessage("       ", adress.byteValue());
    }


    protected void emptyFree() {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        adresses.forEach((i) -> {
            sendMessage("       ", i.byteValue());
        });
    }

    /**
     * Так через Spring мы установим объект работы с COM портом.
     *
     * @param serialPort этот рапаметр определяется в Spring
     */
    public void setComPort(ISerialPort serialPort) {
        this.serialPort = serialPort;
        this.serialPort.setLoggerListener((String message, boolean isError) -> {
            if (isError) {
                QLog.l().logger().error(message);
            } else {
                QLog.l().logger().debug(message);
            }
        });
        this.serialPort.setExceptionListener((String message) -> {
            throw new ServerException(message);
        });
    }

    /**
     * Отсылаем сообщение на табло. В отдельным потоке, чтоб не тормозил вывод выполнение основного.
     * Синхронизированно, т.к. команды шлются по одной и с интервалом 0.3 секунды, чтобы успевало
     * все выполняться.
     *
     * @param message Строка которая отобразиться на табло.
     * @param adress адрес устройства вывода строки.
     */
    private void sendMessage(final String message, final byte adress) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    send();
                } catch (Exception ex) {
                    throw new ServerException("Не возможно отправить сообщение. " + ex);
                }
            }

            synchronized private void send() throws Exception {
                final byte[] mess = ("12" + message + "e").getBytes();
                mess[0] = 1;
                mess[mess.length - 1] = 7;
                mess[1] = adress;
                serialPort.send(mess);
                // Подождем пока отработает СОМ и табло.
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    @Override
    protected void showOnBoard(LinkedList<Record> records) {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void showBoard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showOnBoardForOffice(LinkedList<Record> records, QOffice office) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getLinesCountForOffice(QOffice office) {
        //throw new UnsupportedOperationException("Not supported yet.");
        return 0;
    }

    /**
     * Собыите вывода времени на таймер.
     */
    private class TimerPrinter implements ActionListener {

        /**
         * Обеспечение вывода времени.
         */
        @Override
        public void actionPerformed(ActionEvent e) {

            final DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            final Date d = new java.util.Date();
            sendMessage(" " + dateFormat.format(new Date(d.getTime())),
                adresses.get(adresses.size() - 1).byteValue());
        }
    }
}
