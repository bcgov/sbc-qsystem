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
import java.util.Date;
import java.util.LinkedList;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetGridOfDay extends JsonRPC20 {

    public RpcGetGridOfDay() {
    }

    public RpcGetGridOfDay(GridDayAndParams result) {
        this.result = result;
    }

    public static class GridDayAndParams {

        public GridDayAndParams() {
        }

        public GridDayAndParams(int advanceLimit) {
            this.advanceLimit = advanceLimit;
        }
        @Expose
        @SerializedName("limit")
        private int advanceLimit;

        public int getAdvanceLimit() {
            return advanceLimit;
        }

        public void setAdvanceLimit(int advanceLimit) {
            this.advanceLimit = advanceLimit;
        }
        @Expose
        @SerializedName("times")
        private LinkedList<AdvTime> times =  new LinkedList<>();

        public void addTime(AdvTime time) {
            if (times == null) {
                times = new LinkedList<>();
            }
            times.add(time);
        }

        public LinkedList<AdvTime> getTimes() {
            return times;
        }
    }

    public static class AdvTime {

        public AdvTime() {
        }
        public AdvTime(Date date) {
            this.date = date;
        }
        @Expose
        @SerializedName("date")
        private Date date;
        @Expose
        @SerializedName("acusts")
        private LinkedList<QAdvanceCustomer> acusts;

        public LinkedList<QAdvanceCustomer> getAcusts() {
            return acusts;
        }

        public void setAcusts(LinkedList<QAdvanceCustomer> acusts) {
            this.acusts = acusts;
        }

        public void addACustomer(QAdvanceCustomer aCustomer) {
            if (acusts == null) {
                acusts = new LinkedList<>();
            }
            acusts.add(aCustomer);
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
    @Expose
    @SerializedName("result")
    private GridDayAndParams result;

    public void setResult(GridDayAndParams result) {
        this.result = result;
    }

    public GridDayAndParams getResult() {
        return result;
    }
}
