/*
 *  Copyright (C) 2012 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.ServerProps;

/**
 * Список забаненых по неявке.
 * @author Evgeniy Egorov
 */
public class RpcBanList extends JsonRPC20 {

    @Expose
    @SerializedName("result")
    private final LinkedList<String> banCustomers = new LinkedList<>();

    public LinkedList<String> getBanList() {
        return banCustomers;
    }
    private final HashMap<String, Long> banSrok = new HashMap<>();

    /**
     * @deprecated Эт вообще синглтн. Нужет тлько для отправки банлиста в админку.
     * Эт лист нигде не сохраняется, только сливается сюда инфа при удалении кастомера за неявку.
     */
    public RpcBanList() {
    }

    public static RpcBanList getInstance() {
        return BanListHolder.INSTANCE;
    }

    private static class BanListHolder {

        private static final RpcBanList INSTANCE = new RpcBanList();
    }

    /**
     * Посмотреть кто уже отсидел два ччаса и отпустить
     */
    public void udo(String data) {
        if (data == null) {
            final LinkedList<String> li = new LinkedList<>();
            for (String string : banCustomers) {
                final Long l = banSrok.get(string);
                if (l != null && new Date().getTime() - l > 1000 * 60 * ServerProps.getInstance().getProps().getBlackTime()) {
                    deleteFromBanList(string);
                    li.add(string);
                }
            }
            banCustomers.removeAll(li);
        } else {
            final Long l = banSrok.get(data.trim());
            if (l != null && new Date().getTime() - l > 1000 * 60 * ServerProps.getInstance().getProps().getBlackTime()) {
                deleteFromBanList(data.trim());
            }
        }
    }

    public void addToBanList(QCustomer customer) {
        banCustomers.add(customer.getInput_data().trim());
        banSrok.put(customer.getInput_data().trim(), new Date().getTime());
    }

    public void addToBanList(String data) {
        banCustomers.add(data.trim());
        banSrok.put(data.trim(), new Date().getTime());
    }

    public boolean isBaned(QCustomer customer) {
        udo(customer.getInput_data());
        return ServerProps.getInstance().getProps().getBlackTime() > 0 && banCustomers.contains(customer.getInput_data().trim());
    }

    public boolean isBaned(String data) {
        udo(data);
        return ServerProps.getInstance().getProps().getBlackTime() > 0 && banCustomers.contains(data.trim());
    }

    public void deleteFromBanList(QCustomer customer) {
        banCustomers.remove(customer.getInput_data().trim());
        banSrok.remove(customer.getInput_data().trim());
    }

    public void deleteFromBanList(String data) {
        banCustomers.remove(data.trim());
        banSrok.remove(data.trim());
    }
}
