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
 *
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import ru.apertum.qsystem.server.model.QProperty;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Evgeniy Egorov
 */
public class CmdParams {

    public static final String CMD = "cmd";

    public CmdParams() {
    }

    public CmdParams(String params) {
        initFromString(params);
    }

    public boolean inAccurateFinish;
    @Expose
    @SerializedName("service_id")
    public Long serviceId;
    @Expose
    @SerializedName("user_id")
    public Long userId;
    @Expose
    @SerializedName("pass")
    public String password;
    @Expose
    @SerializedName("priority")
    public Integer priority;
    @Expose
    @SerializedName("text_data")
    public String textData;
    @Expose
    @SerializedName("result_id")
    public Long resultId;
    @Expose
    @SerializedName("request_back")
    public Boolean requestBack;
    @Expose
    @SerializedName("drop_tickets_cnt")
    public Boolean dropTicketsCounter;
    @Expose
    @SerializedName("is_only_mine")
    public Boolean isMine;
    @Expose
    @SerializedName("coeff")
    public Integer coeff;
    @Expose
    @SerializedName("date")
    public Long date;
    @Expose
    @SerializedName("customer_id")
    public Long customerId;
    @Expose
    @SerializedName("response_id")
    public Long responseId;
    @Expose
    @SerializedName("client_auth_id")
    public String clientAuthId;
    @Expose
    @SerializedName("info_item_name")
    public String infoItemName;
    @Expose
    @SerializedName("postponed_period")
    public Integer postponedPeriod;
    @Expose
    @SerializedName("comments")
    public String comments;
    @Expose
    @SerializedName("welcome_time")
    public Date welcomeTime;   
    @Expose
    @SerializedName("channelsIndex")
    public int channelsIndex;   
    @Expose
    @SerializedName("channels")
    public String channels;  
    @Expose
    @SerializedName("new_channelsIndex")
    public int new_channels_Index;   
    @Expose
    @SerializedName("new_channels")
    public String new_channels; 
        
    public String AAA;
    
    
   
    
    /**
     * услуги, в которые пытаемся встать. Требует уточнения что это за трехмерный массив. Это пять списков. Первый это вольнопоследовательные услуги. Остальные
     * четыре это зависимопоследовательные услуги, т.е. пока один не закончится на другой не переходить. Что такое элемент списка. Это тоже список. Первый
     * элемент это та самая комплексная услуга(ID). А остальные это зависимости, т.е. если есть еще не оказанные услуги но назначенные, которые в зависимостях,
     * то их надо оказать.
     */
    @Expose
    @SerializedName("complex_id")
    public LinkedList<LinkedList<LinkedList<Long>>> complexId;

    /**
     * Это список свойств для сохранения или инита на сервере.
     */
    @Expose
    @SerializedName("properties")
    public List<QProperty> properties;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("^?");
        final Field[] fs = getClass().getDeclaredFields();
        try {
            for (Field field : fs) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                if (field.get(this) == null) {
                    continue;
                }

                field.setAccessible(true);

                if (field.getType().getSimpleName().contains("List")) {
                    List list = (List) field.get(this);
                    if (list.size() > 0 && list.get(0) instanceof QProperty) {
                        sb.append("&").append(field.getName()).append("=");
                        list.stream().forEach((object) -> {
                            try {
                                sb.append(URLEncoder.encode("{" + object.toString() + "}", "utf-8"));
                            } catch (UnsupportedEncodingException ex) {
                                System.err.println(ex);
                            }
                        });
                    }
                    continue;
                }

                switch (field.getType().getSimpleName().toLowerCase(Locale.US)) {
                    case "int":
                        sb.append("&").append(field.getName()).append("=").append(field.get(this));
                        break;
                    case "integer":
                        sb.append("&").append(field.getName()).append("=").append(field.get(this));
                        break;
                    case "string":
                        sb.append("&").append(field.getName()).append("=").append(URLEncoder.encode((String) field.get(this), "utf-8"));
                        break;
                    case "boolean":
                        sb.append("&").append(field.getName()).append("=").append(field.get(this));
                        break;
                    case "long":
                        sb.append("&").append(field.getName()).append("=").append(field.get(this));
                        break;
                    case "date":
                        sb.append("&").append(field.getName()).append("=").append(field.get(this));
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | UnsupportedEncodingException ex) {
            System.err.println(ex);
        }

        final String st = sb.toString().replaceFirst("^\\^\\?\\&", "");
        sb.setLength(0);
        return st.length() < 3 ? "" : st;
    }

    public final void initFromString(String params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        for (String str : params.split("&")) {
            final String[] pp = str.split("=");

            final Field[] fs = getClass().getDeclaredFields();
            try {
                for (Field field : fs) {
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }

                    if (pp[0].equals(field.getName())) {
                        field.setAccessible(true);

                        if ("properties".equals(field.getName())) {
                            final List<QProperty> list = new LinkedList<>();
                            String lst = URLDecoder.decode(pp[1], "utf-8");
                            lst = lst.substring(1, lst.length() - 1);
                            final String[] ll = lst.split("\\}\\{");
                            for (String el : ll) {
                                final String[] ss = el.split("\\[|\\]|:");
                                if (ss.length == 4) {
                                    list.add(new QProperty(ss[1], ss[2], ss[3]));
                                }
                            }
                            field.set(this, list);
                            continue;
                        }

                        switch (field.getType().getSimpleName().toLowerCase(Locale.US)) {
                            case "int":
                                field.set(this, Integer.parseInt(pp[1]));
                                break;
                            case "integer":
                                field.set(this, Integer.parseInt(pp[1]));
                                break;
                            case "string":
                                field.set(this, URLDecoder.decode(pp[1], "utf-8"));
                                break;
                            case "boolean":
                                field.set(this, Boolean.parseBoolean(pp[1]));
                                break;
                            case "long":
                                field.set(this, Long.parseLong(pp[1]));
                                break;
                            default:
                                throw new AssertionError();
                        }
                    }

                }
            } catch (IllegalArgumentException | IllegalAccessException | UnsupportedEncodingException ex) {
                System.err.println(ex);
            }

        }
    }

    public static void main(String[] args) {
        CmdParams cp = new CmdParams();
        cp.clientAuthId = "str1";
        cp.password = "Парольчег";
        cp.coeff = 101;
        cp.isMine = false;
        cp.requestBack = true;
        cp.date = System.currentTimeMillis();
        cp.properties = new LinkedList<>();
        cp.properties.add(new QProperty("sec1", "key1", "Русс1", "com1"));
        cp.properties.add(new QProperty("sec2", "key2", "Русс2", "com2"));
        cp.properties.add(new QProperty("sec3", "key3", "Русс3", "com3"));

        String url = cp.toString();
        System.out.println(url);

        cp = new CmdParams();
        cp.initFromString(url);
        url = cp.toString();
        System.out.println(url);

        cp = new CmdParams();
        cp.initFromString(url);
        url = cp.toString();
        System.out.println(url);

        cp = new CmdParams();
        cp.initFromString("");
        url = cp.toString();
        System.out.println(url);

        cp = new CmdParams();
        cp.initFromString(null);
        url = cp.toString();
        System.out.println(url);

    }

}
