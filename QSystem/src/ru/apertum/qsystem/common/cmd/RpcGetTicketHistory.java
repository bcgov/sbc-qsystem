/*
 * Copyright (C) 2015 Evgeniy Egorov
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
import java.util.LinkedList;
import java.util.List;
import ru.apertum.qsystem.common.model.QCustomer;

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetTicketHistory extends JsonRPC20 {

    public RpcGetTicketHistory(TicketHistory result) {
        this.result = result;
    }

    @Expose
    @SerializedName("result")
    private TicketHistory result;

    public void setResult(TicketHistory result) {
        this.result = result;
    }

    public TicketHistory getResult() {
        return result;
    }

    public static class TicketHistory {

        public TicketHistory(String info, List<String> custs) {
            this.info = info;
            this.custs = custs;
        }

        @Expose
        @SerializedName("info")
        private String info;

        @Expose
        @SerializedName("history")
        private List<String> custs;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public List<String> getCusts() {
            return custs;
        }

        public void setCusts(LinkedList<String> custs) {
            this.custs = custs;
        }

    }

}
