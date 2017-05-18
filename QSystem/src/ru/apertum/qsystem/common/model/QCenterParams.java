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
import java.util.LinkedList;

/**
 * Параметры центра табло с отображением вызовов
 * @author Evgeniy Egorov
 */
public class QCenterParams {

    @Expose
    @SerializedName("props")
    private LinkedList<QParam> params = new LinkedList<>();

    public LinkedList<QParam> getParams() {
        return params;
    }

    public void setParams(LinkedList<QParam> params) {
        this.params = params;
    }
    /*
    
    /**
     * Фоновое изображение
     *
    @Expose
    @SerializedName("backgroundImg")
    private String backgroundImg;
    /**
     * Количество строк на табло
     *
    @Expose
    @SerializedName("rows")
    private int rows;
    /**
     * Количество столбцов на табло
     *
    @Expose
    @SerializedName("cols")
    private int cols;
    /**
     * Окантовка строк
     *
    @Expose
    @SerializedName("border")
    private boolean border;
    /**
     * Размер шрифта заголовка
     *
    @Expose
    @SerializedName("captionFontSize")
    private int captionFontSize;
    /**
     * Размер шрифта строк
     *
    @Expose
    @SerializedName("lineFontSize")
    private int lineFontSize;
    /**
     * Размер шрифта строк
     *
    @Expose
    @SerializedName("captionFontColor")
    private Color captionFontColor;
    /**
     * Цвет шрифта левого столбца
     *
    @Expose
    @SerializedName("leftColColor")
    private Color leftColColor;
    /**
     * Цвет шрифта правого столбца
     *
    @Expose
    @SerializedName("rightColColor")
    private Color rightColColor;
    /**
     * Цвет фона
     *
    @Expose
    @SerializedName("backgroundColor")
    private Color backgroundColor;
    /**
     * Разделитель столбцов
     *
    @Expose
    @SerializedName("delemiter")
    private String delemiter;
    /**
     * Минимальное время индикации на табло
     *
    @Expose
    @SerializedName("minTime")
    private int minTime;
    /**
     * Заголовок левого столбца
     *
    @Expose
    @SerializedName("captionLeftCol")
    private String captionLeftCol;
    /**
     * Заголовок правого столбца
     *
    @Expose
    @SerializedName("captionRightCol")
    private String captionRightCol;
    /**
     * Заголовок таблицы следующих
     *
    @Expose
    @SerializedName("captionNextGrid")
    private String captionNextGrid;
    /**
     * Цвет рамки строки табло
     *
    @Expose
    @SerializedName("frameLineColor")
    private Color frameLineColor;
    /**
     * Надпись строки табло
     *
    @Expose
    @SerializedName("frameLineCaption")
    private String frameLineCaption;
    /**
     * Цвет надписи строки табло
     *
    @Expose
    @SerializedName("frameLineCaptionColor")
    private Color frameLineCaptionColor;
    
    public Color getBackgroundColor() {
    return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
    }
    
    public String getBackgroundImg() {
    return backgroundImg;
    }
    
    public void setBackgroundImg(String backgroundImg) {
    this.backgroundImg = backgroundImg;
    }
    
    public boolean isBorder() {
    return border;
    }
    
    public void setBorder(boolean border) {
    this.border = border;
    }
    
    public Color getCaptionFontColor() {
    return captionFontColor;
    }
    
    public void setCaptionFontColor(Color captionFontColor) {
    this.captionFontColor = captionFontColor;
    }
    
    public int getCaptionFontSize() {
    return captionFontSize;
    }
    
    public void setCaptionFontSize(int captionFontSize) {
    this.captionFontSize = captionFontSize;
    }
    
    public String getCaptionLeftCol() {
    return captionLeftCol;
    }
    
    public void setCaptionLeftCol(String captionLeftCol) {
    this.captionLeftCol = captionLeftCol;
    }
    
    public String getCaptionNextGrid() {
    return captionNextGrid;
    }
    
    public void setCaptionNextGrid(String captionNextGrid) {
    this.captionNextGrid = captionNextGrid;
    }
    
    public String getCaptionRightCol() {
    return captionRightCol;
    }
    
    public void setCaptionRightCol(String captionRightCol) {
    this.captionRightCol = captionRightCol;
    }
    
    public int getCols() {
    return cols;
    }
    
    public void setCols(int cols) {
    this.cols = cols;
    }
    
    public String getDelemiter() {
    return delemiter;
    }
    
    public void setDelemiter(String delemiter) {
    this.delemiter = delemiter;
    }
    
    public String getFrameLineCaption() {
    return frameLineCaption;
    }
    
    public void setFrameLineCaption(String frameLineCaption) {
    this.frameLineCaption = frameLineCaption;
    }
    
    public Color getFrameLineCaptionColor() {
    return frameLineCaptionColor;
    }
    
    public void setFrameLineCaptionColor(Color frameLineCaptionColor) {
    this.frameLineCaptionColor = frameLineCaptionColor;
    }
    
    public Color getFrameLineColor() {
    return frameLineColor;
    }
    
    public void setFrameLineColor(Color frameLineColor) {
    this.frameLineColor = frameLineColor;
    }
    
    public Color getLeftColColor() {
    return leftColColor;
    }
    
    public void setLeftColColor(Color leftColColor) {
    this.leftColColor = leftColColor;
    }
    
    public int getLineFontSize() {
    return lineFontSize;
    }
    
    public void setLineFontSize(int lineFontSize) {
    this.lineFontSize = lineFontSize;
    }
    
    public int getMinTime() {
    return minTime;
    }
    
    public void setMinTime(int minTime) {
    this.minTime = minTime;
    }
    
    public Color getRightColColor() {
    return rightColColor;
    }
    
    public void setRightColColor(Color rightColColor) {
    this.rightColColor = rightColColor;
    }
    
    public int getRows() {
    return rows;
    }
    
    public void setRows(int rows) {
    this.rows = rows;
    }
     * 
     */
}
