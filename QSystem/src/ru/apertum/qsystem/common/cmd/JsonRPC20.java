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
public class JsonRPC20 extends AJsonRPC20 {

    public JsonRPC20() {
    }

    public JsonRPC20(String method, CmdParams params) {
        this.method = method;
        this.params = params;
    }
    @Expose
    @SerializedName("params")
    private CmdParams params;

    public void setParams(CmdParams params) {
        this.params = params;
    }

    public CmdParams getParams() {
        return params;
    }
}
