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

import java.util.HashMap;

/**
 *
 * @author Evgeniy Egorov
 */
public class JsonRPC20Error extends AJsonRPC20 {

    public static class ErrorRPC {

        public static final Integer UNKNOWN_ERROR = -1;
        public static final Integer RESPONCE_NOT_SAVE = 2;
        public static final Integer POSTPONED_NOT_FOUND = 3;
        public static final Integer ADVANCED_NOT_FOUND = 4;

        public static final class ErrorCode {

            private static final HashMap<Integer, String> MESSAGE = new HashMap<>();

            public static String getMessage(Integer code) {
                return MESSAGE.get(code);
            }

            static {
                MESSAGE.put(UNKNOWN_ERROR, "Unknown error.");
                MESSAGE.put(RESPONCE_NOT_SAVE, "Не сохранили отзыв в базе.");
                MESSAGE.put(POSTPONED_NOT_FOUND, "Отложенный пользователь не найден по его ID.");
                MESSAGE.put(ADVANCED_NOT_FOUND, "Не верный номер предварительной записи.");
            }
        }

        public ErrorRPC() {
        }

        public ErrorRPC(Integer code, Object data) {
            this.code = code;
            this.message = ErrorCode.getMessage(code);
            this.data = data;
        }

        public Integer getCode() {
            return code;
        }

        public Object getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
        @Expose
        @SerializedName("code")
        private Integer code;
        @Expose
        @SerializedName("message")
        private String message;
        @Expose
        @SerializedName("data")
        private Object data;
    }

    public JsonRPC20Error() {
    }

    public JsonRPC20Error(Integer code, Object data) {
        error = new ErrorRPC(code, data);
    }
    @Expose
    @SerializedName("error")
    private ErrorRPC error;

    public void setError(ErrorRPC error) {
        this.error = error;
    }

    public ErrorRPC getError() {
        return error;
    }
}
