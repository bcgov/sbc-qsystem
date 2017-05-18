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
import java.util.LinkedList;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author Evgeniy Egorov
 */
public class RpcGetUsersList extends JsonRPC20 {

    public RpcGetUsersList() {
    }
    @Expose
    @SerializedName("result")
    private LinkedList<QUser> result;

    public void setResult(LinkedList<QUser> result) {
        this.result = result;
    }

    public LinkedList<QUser> getResult() {
        return result;
    }

    public RpcGetUsersList(LinkedList<QUser> result) {
        this.result = result;
    }
}
