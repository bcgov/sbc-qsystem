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

/**
 * Параметры для табло
 * @author Evgeniy Egorov
 */
public class QBoardParams {

    @Expose
    @SerializedName("top")
    private QSegmentParams top = new QSegmentParams();
    @Expose
    @SerializedName("bottom")
    private QSegmentParams bottom = new QSegmentParams();
    @Expose
    @SerializedName("left")
    private QSegmentParams left = new QSegmentParams();
    @Expose
    @SerializedName("right")
    private QSegmentParams right = new QSegmentParams();
    @Expose
    @SerializedName("center")
    private QCenterParams center;
    @Expose
    @SerializedName("visible")
    private boolean isVisible;
    @Expose
    @SerializedName("monitor")
    private int monitor;
    @Expose
    @SerializedName("description")
    private String description;

    public QSegmentParams getBottom() {
        return bottom;
    }

    public void setBottom(QSegmentParams bottom) {
        this.bottom = bottom;
    }

    public QCenterParams getCenter() {
        return center;
    }

    public void setCenter(QCenterParams center) {
        this.center = center;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public QSegmentParams getLeft() {
        return left;
    }

    public void setLeft(QSegmentParams left) {
        this.left = left;
    }

    public int getMonitor() {
        return monitor;
    }

    public void setMonitor(int monitor) {
        this.monitor = monitor;
    }

    public QSegmentParams getRight() {
        return right;
    }

    public void setRight(QSegmentParams right) {
        this.right = right;
    }

    public QSegmentParams getTop() {
        return top;
    }

    public void setTop(QSegmentParams top) {
        this.top = top;
    }
}
