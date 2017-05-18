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
import java.util.Date;

/**
 * Class for input protocol. Using RPC2.0
 * @author Evgeniy Egorov
 */
public abstract class AJsonRPC20 {

    public AJsonRPC20() {
    }

    public AJsonRPC20(String id) {
        this.id = id;
    }

    @Expose
    @SerializedName("jsonrpc")
    final private String jsonrpc = "2.0";

    @Expose
    @SerializedName("id")
    private String id = Long.toString(new Date().getTime());

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Expose
    @SerializedName("method")
    protected String method;

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
