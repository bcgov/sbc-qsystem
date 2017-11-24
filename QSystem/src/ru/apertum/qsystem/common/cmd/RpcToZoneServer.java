/*
 * Copyright (C) 2011 Evgeniy Egorov
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

/**
 * @author Evgeniy Egorov
 */
public class RpcToZoneServer extends JsonRPC20 {

    @Expose
    @SerializedName("result")
    private Data result;

    public RpcToZoneServer(Data result) {
        this.result = result;
    }

    public RpcToZoneServer() {
    }

    public Data getResult() {
        return result;
    }

    public void setResult(Data result) {
        this.result = result;
    }

    public static class Data {

        @Expose
        @SerializedName("userName")
        public String userName;
        @Expose
        @SerializedName("userPoint")
        public String userPoint;
        @Expose
        @SerializedName("customerPrefix")
        public String customerPrefix;
        @Expose
        @SerializedName("customerNumber")
        public int customerNumber;
        @Expose
        @SerializedName("userAddrRS")
        public int userAddrRS;

        public Data() {
        }

        public Data(String userName, String userPoint, String customerPrefix, int customerNumber,
            int userAddrRS) {
            this.userName = userName;
            this.userPoint = userPoint;
            this.customerPrefix = customerPrefix;
            this.customerNumber = customerNumber;
            this.userAddrRS = userAddrRS;
        }

        public int getCustomerNumber() {
            return customerNumber;
        }

        public void setCustomerNumber(int customerNumber) {
            this.customerNumber = customerNumber;
        }

        public String getCustomerPrefix() {
            return customerPrefix;
        }

        public void setCustomerPrefix(String customerPrefix) {
            this.customerPrefix = customerPrefix;
        }

        public int getUserAddrRS() {
            return userAddrRS;
        }

        public void setUserAddrRS(int userAddrRS) {
            this.userAddrRS = userAddrRS;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPoint() {
            return userPoint;
        }

        public void setUserPoint(String userPoint) {
            this.userPoint = userPoint;
        }
    }
}
