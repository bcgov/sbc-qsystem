/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.web;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.PieModel;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import org.apache.commons.lang.StringUtils;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.zkoss.util.resource.Labels;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.Chart;

import org.zkoss.zul.SimplePieModel;

public class PieChartVM {
    
    public static String l(String resName) {
        return Labels.getLabel(resName);
    }

    private final PieChartEngine engine;
    PieModel model;

    public PieChartVM() {
        // prepare chart data
        engine = new PieChartEngine();

        model = PieChartData.getModel();
    }

    public PieChartEngine getEngine() {
        return engine;
    }

    public PieModel getModel() {
        return model;
    }

    public void setModel(PieModel model) {
        this.model = model;
    }
    
    @GlobalCommand("dataChanged")
    @NotifyChange("model")
    public void onDataChanged(@BindingParam("category") String category, @BindingParam("num") Number num) {
        model.setValue(category, num);
    }

    public static class PieChartEngine extends JFreeChartEngine {

        private boolean explode = true;

        @Override
        public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
            jfchart.setBackgroundPaint(Color.white);

            final PiePlot piePlot = (PiePlot) jfchart.getPlot();
            piePlot.setLabelBackgroundPaint(ChartColors.COLOR_4);

            //override some default colors
            final Paint[] colors = new Paint[]{ChartColors.COLOR_1, ChartColors.COLOR_2, ChartColors.COLOR_3, ChartColors.COLOR_4,
                ChartColors.COLOR_5, ChartColors.COLOR_6, ChartColors.COLOR_7, ChartColors.COLOR_8, ChartColors.COLOR_9, ChartColors.COLOR_10};
            DefaultDrawingSupplier defaults = new DefaultDrawingSupplier();
            piePlot.setDrawingSupplier(new DefaultDrawingSupplier(colors, 
                    new Paint[]{defaults.getNextFillPaint()}, 
                    new Paint[]{defaults.getNextOutlinePaint()},
                    new Stroke[]{defaults.getNextStroke()},
                    new Stroke[]{defaults.getNextOutlineStroke()},
                    new Shape[]{defaults.getNextShape()}));

            piePlot.setShadowPaint(new Color(0xE5E5E5));

            piePlot.setSectionOutlinesVisible(false);

            piePlot.setExplodePercent(l("refresh_data"), explode ? 0.2 : 0);

            return false;
        }

        public void setExplode(boolean explode) {
            this.explode = explode;
        }
    }

    private static class PieChartData {

        public static PieModel getModel() {
            PieModel model = new SimplePieModel();
            model.setValue(l("refresh_data"), 100);
            return model;
        }
    }

    private static class ChartColors {

        //main colors
        public static Color COLOR_1 = new Color(0x96FFE6);
        public static Color COLOR_2 = new Color(0x1C85FF);
        public static Color COLOR_3 = new Color(0xFF1692);
        public static Color COLOR_4 = new Color(0xFFFA0A);
        public static Color COLOR_5 = new Color(0x0FFFFF);
        //additional colors
        public static Color COLOR_6 = new Color(0xFF0CFF);
        public static Color COLOR_7 = new Color(0x1CFF85);
        public static Color COLOR_8 = new Color(0x780AFF);
        public static Color COLOR_9 = new Color(0xFF530F);
        public static Color COLOR_10 = new Color(0x1CFF36);

        public static String toHtmlColor(Color color) {
            return "#" + toHexColor(color);
        }

        public static String toHexColor(Color color) {
            return StringUtils.leftPad(Integer.toHexString(color.getRGB() & 0xFFFFFF), 6, '0');
        }

    }
}
