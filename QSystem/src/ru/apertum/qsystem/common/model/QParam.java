/*
 * Copyright (C) 2012 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
 * Copyright (C) 2012 Evgeniy Egorov
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
package ru.apertum.qsystem.common.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.awt.Color;

/**
 * Параметр для табло
 * @author Evgeniy Egorov
 */
public class QParam {

    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("value")
    private String value;

    public void setValue(int value) {
        this.value = String.valueOf(value);
    }

    public void setValue(double value) {
        this.value = String.valueOf(value);
    }

    public void setValue(boolean value) {
        this.value = value ? "1" : "0";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getAsInt() {
        return Integer.parseInt(value);
    }

    public Color getAsColor() {
        return new Color(Integer.parseInt(value));
    }

    public boolean getAsBool() {
        return !"0".equals(value);
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
