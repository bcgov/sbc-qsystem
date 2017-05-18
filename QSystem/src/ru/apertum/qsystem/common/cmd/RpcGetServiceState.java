/*
 * Copyright (C) 2013 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.common.cmd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.concurrent.LinkedBlockingDeque;
import ru.apertum.qsystem.common.model.QCustomer;

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetServiceState extends JsonRPC20 {

    public RpcGetServiceState() {
    }

    public RpcGetServiceState(int code, String message) {
        this.result = new ServiceState(code, message);
    }
    public RpcGetServiceState(LinkedBlockingDeque<QCustomer> line) {
        this.result = new ServiceState(line);
    }
    @Expose
    @SerializedName("result")
    private ServiceState result;

    public ServiceState getResult() {
        return result;
    }

    public void setResult(ServiceState result) {
        this.result = result;
    }

    public static class ServiceState {

        public ServiceState() {
        }

        public ServiceState(int code, String message) {
            this.code = code;
            this.message = message;
        }
        public ServiceState(LinkedBlockingDeque<QCustomer> line) {
            this.code = 1;
            this.message = null;
            this.clients = line;
        }
        @Expose
        @SerializedName("code")
        private int code;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
        @Expose
        @SerializedName("message")
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        
        @Expose
        @SerializedName("clients")
        private LinkedBlockingDeque<QCustomer> clients;

        public LinkedBlockingDeque<QCustomer> getClients() {
            return clients;
        }

        public void setClients(LinkedBlockingDeque<QCustomer> clients) {
            this.clients = clients;
        }
    }
}
