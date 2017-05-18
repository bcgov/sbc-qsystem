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

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetGridOfWeek extends JsonRPC20 {

    public RpcGetGridOfWeek() {
    }

    public RpcGetGridOfWeek(GridAndParams result) {
        this.result = result;
    }

    public static class GridAndParams {

        /**
         * Костыль. Если вместо данных возвращается строка с текстом ошибки.
         */
        @Expose
        @SerializedName("error")
        private String spError;

        public GridAndParams(String spError) {
            this.spError = spError;
        }

        public String getSpError() {
            return spError;
        }

        public void setSpError(String spError) {
            this.spError = spError;
        }

        public GridAndParams() {
        }

        @Expose
        @SerializedName("times")
        private LinkedList<Date> times = new LinkedList<>();

        public void addTime(Date time) {
            if (times == null) {
                times = new LinkedList<>();
            }
            times.add(time);
        }

        public LinkedList<Date> getTimes() {
            return times;
        }

        @Expose
        @SerializedName("start")
        private Date startTime;
        @Expose
        @SerializedName("finish")
        private Date finishTime;
        @Expose
        @SerializedName("limit")
        private int advanceLimit;
        @Expose
        @SerializedName("limit_period")
        private int advanceLimitPeriod;
        @Expose
        @SerializedName("limit_time")
        private int advanceTimePeriod;

        public int getAdvanceTimePeriod() {
            return advanceTimePeriod;
        }

        public void setAdvanceTimePeriod(int advanceTimePeriod) {
            this.advanceTimePeriod = advanceTimePeriod;
        }

        public int getAdvanceLimit() {
            return advanceLimit;
        }

        public void setAdvanceLimit(int advanceLimit) {
            this.advanceLimit = advanceLimit;
        }

        public int getAdvanceLimitPeriod() {
            return advanceLimitPeriod;
        }

        public void setAdvanceLimitPeriod(int advanceLimitPeriod) {
            this.advanceLimitPeriod = advanceLimitPeriod;
        }

        public Date getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(Date finishTime) {
            this.finishTime = finishTime;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }
    }
    @Expose
    @SerializedName("result")
    private GridAndParams result;

    public void setResult(GridAndParams result) {
        this.result = result;
    }

    public GridAndParams getResult() {
        return result;
    }
}
