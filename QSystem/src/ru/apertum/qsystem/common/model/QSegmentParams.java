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
 *
 * @author Evgeniy Egorov
 */
public class QSegmentParams {

    /**
     * visible
     */
    @Expose
    @SerializedName("visible")
    private boolean visible;
    /**
     * size
     */
    @Expose
    @SerializedName("size")
    private double size;
    /**
     * HTML текст
     */
    @Expose
    @SerializedName("html")
    private String html;
    /**
     * Бегущий текст
     */
    @Expose
    @SerializedName("runningText")
    private String runningText;
    /**
     * Скорость бегущего текста
     */
    @Expose
    @SerializedName("runningTextSpeed")
    private int runningTextSpeed;
    /**
     * Размер шрифта
     */
    @Expose
    @SerializedName("fontSize")
    private int fontSize;
    /**
     * Цвет шрифта
     */
    @Expose
    @SerializedName("fontColor")
    private Color fontColor;
    /**
     * Простая дата
     */
    @Expose
    @SerializedName("date")
    private boolean date;
    /**
     * Фоновое изображение
     */
    @Expose
    @SerializedName("backgroundImg")
    private String backgroundImg;
    /**
     * Видеофайл
     */
    @Expose
    @SerializedName("video")
    private String video;
    /**
     * Таблица следующих
     */
    @Expose
    @SerializedName("gridNext")
    private boolean gridNext;
    /**
     * Колонки табл след
     */
    @Expose
    @SerializedName("colsGridNext")
    private int colsGridNext;
    /**
     * Строки табл след
     */
    @Expose
    @SerializedName("rowsGridNext")
    private int rowsGridNext;

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public int getColsGridNext() {
        return colsGridNext;
    }

    public void setColsGridNext(int colsGridNext) {
        this.colsGridNext = colsGridNext;
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isGridNext() {
        return gridNext;
    }

    public void setGridNext(boolean gridNext) {
        this.gridNext = gridNext;
    }

    public int getRowsGridNext() {
        return rowsGridNext;
    }

    public void setRowsGridNext(int rowsGridNext) {
        this.rowsGridNext = rowsGridNext;
    }

    public String getRunningText() {
        return runningText;
    }

    public void setRunningText(String runningText) {
        this.runningText = runningText;
    }

    public int getRunningTextSpeed() {
        return runningTextSpeed;
    }

    public void setRunningTextSpeed(int runningTextSpeed) {
        this.runningTextSpeed = runningTextSpeed;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
