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
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QUser.Shadow;

import java.util.LinkedList;

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetSelfSituation extends JsonRPC20 {

    public RpcGetSelfSituation() {
    }

    public RpcGetSelfSituation(SelfSituation result) {
        this.result = result;
    }

    public static class SelfSituation {

        public SelfSituation() {
        }

        public SelfSituation(LinkedList<SelfService> selfservices, QCustomer customer, LinkedList<QCustomer> parallelList, LinkedList<QCustomer> postponedList, int limitRecall, int extPrior, Shadow shadow) {
            this.selfservices = selfservices;
            this.customer = customer;
            this.parallelList = parallelList;
            this.postponedList = postponedList;
            this.limitRecall = limitRecall;
            this.extPror = extPrior;
            this.shadow = shadow;
        }
        @Expose
        @SerializedName("self_services")
        private LinkedList<SelfService> selfservices;
        @Expose
        @SerializedName("customer")
        private QCustomer customer;

        public QCustomer getCustomer() {
            return customer;
        }

        public void setCustomer(QCustomer customer) {
            this.customer = customer;
        }

        public LinkedList<SelfService> getSelfservices() {
            return selfservices;
        }

        public void setSelfservices(LinkedList<SelfService> selfservices) {
            this.selfservices = selfservices;
        }
        @Expose
        @SerializedName("postponed")
        private LinkedList<QCustomer> postponedList;

        public LinkedList<QCustomer> getPostponedList() {
            return postponedList;
        }

        public void setPostponedList(LinkedList<QCustomer> postponedList) {
            this.postponedList = postponedList;
        }

        @Expose
        @SerializedName("parallel")
        private LinkedList<QCustomer> parallelList;

        public LinkedList<QCustomer> getParallelList() {
            return parallelList;
        }

        public void setParallelList(LinkedList<QCustomer> parallelList) {
            this.parallelList = parallelList;
        }

        @Expose
        @SerializedName("limit_remove")
        private Integer limitRecall;

        public Integer getLimitRemove() {
            return limitRecall;
        }

        public void setLimitRemove(Integer limitRemove) {
            this.limitRecall = limitRemove;
        }

        @Expose
        @SerializedName("shadow")
        private Shadow shadow;

        public Shadow getShadow() {
            return shadow;
        }

        public void setShadow(Shadow shadow) {
            this.shadow = shadow;
        }

        @Expose
        @SerializedName("ext_prior")
        private Integer extPror = 0;

        public Integer getExtPror() {
            return extPror;
        }

        public void setExtPror(Integer extPror) {
            this.extPror = extPror;
        }

    }

    public static class StPair {

        @Expose
        @SerializedName("num")
        public final String number;
        @Expose
        @SerializedName("dat")
        public final String data;
        @Expose
        @SerializedName("waiting")
        public final Integer waiting;

        public StPair(String number, String data, Integer waiting) {
            this.number = number;
            this.data = data;
            this.waiting = waiting;
        }

        public StPair() {
            this.number = null;
            this.data = null;
            this.waiting = null;
        }

    }

    public static class SelfService {

        public SelfService() {
        }

        /**
         *
         * @param service услуга по которой данная статистика
         * @param countWait количество ожидающих в этой услуге
         * @param priority приоритет услуги
         * @param flexy возможность менять приоритет услуги юзеру
         */
        public SelfService(QService service, int countWait, int priority, boolean flexy) {
            this.serviceName = service.getName();
            this.countWait = countWait;
            this.priority = priority;
            this.flexy = flexy;
            this.id = service.getId();
            this.line = new LinkedList<>();
            for (QCustomer cu : service.getClients()) {// не переделывать на лямбду.
                final String fn = cu.getFullNumber();
                final StPair sp = new StPair(fn, cu.getInput_data(), cu.getWaitingMinutes());
                line.addLast(sp);
            }
        }
        @Expose
        @SerializedName("id")
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
        @Expose
        @SerializedName("waiting")
        private int countWait;

        public int getCountWait() {
            return countWait;
        }

        public void setCountWait(int countWait) {
            this.countWait = countWait;
        }
        @Expose
        @SerializedName("service_name")
        private String serviceName;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        @Expose
        @SerializedName("priority")
        private int priority;

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
        @Expose
        @SerializedName("flexy")
        private boolean flexy;

        public boolean isFlexy() {
            return flexy;
        }

        public void setFlexy(boolean flexy) {
            this.flexy = flexy;
        }

        @Expose
        @SerializedName("line")
        private LinkedList<StPair> line;

        public LinkedList<StPair> getLine() {
            return line;
        }

        public void setLine(LinkedList<StPair> line) {
            this.line = line;
        }

    }
    @Expose
    @SerializedName("result")
    private SelfSituation result;

    public void setResult(SelfSituation result) {
        this.result = result;
    }

    public SelfSituation getResult() {
        return result;
    }
}
