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
import ru.apertum.qsystem.server.model.QNet;
import ru.apertum.qsystem.server.model.QService;

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetAllServices extends JsonRPC20 {

    public RpcGetAllServices() {
    }

    public RpcGetAllServices(ServicesForWelcome result) {
        this.result = result;
    }

    public static class ServicesForWelcome {

        public ServicesForWelcome() {
        }

        public ServicesForWelcome(QService root, QNet qnet) {
            this.root = root;
            this.startTime = qnet.getStartTime();
            this.finishTime = qnet.getFinishTime();
            this.buttonFreeDesign = qnet.getButtonFreeDesign();
        }
        @Expose
        @SerializedName("root")
        private QService root;

        public QService getRoot() {
            return root;
        }

        public void setRoot(QService root) {
            this.root = root;
        }
        @Expose
        @SerializedName("start_time")
        private Date startTime;

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
        @Expose
        @SerializedName("finish_time")
        private Date finishTime;
        /**
         * Свободное расположение кнопок на пункте регистрации
         */
        @Expose
        @SerializedName("btn_free_dsn")
        private Boolean buttonFreeDesign;

        public Boolean getButtonFreeDesign() {
            return buttonFreeDesign;
        }

        public void setButtonFreeDesign(Boolean buttonFreeDesign) {
            this.buttonFreeDesign = buttonFreeDesign;
        }
    }
    @Expose
    @SerializedName("result")
    private ServicesForWelcome result;

    public void setResult(ServicesForWelcome result) {
        this.result = result;
    }

    public ServicesForWelcome getResult() {
        return result;
    }
}
