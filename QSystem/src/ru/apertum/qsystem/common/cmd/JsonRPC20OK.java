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

/**
 *
 * @author Evgeniy Egorov
 */
public class JsonRPC20OK extends AJsonRPC20 {
    
    public JsonRPC20OK() {
    }
    
    @Expose
    @SerializedName("result")
    private int result = 1;

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    /**
     * 
     * @param result Результат успешного выполнения отличный от 1 
     */
    public JsonRPC20OK(int result) {
        this.result = result;
    }
    
}
