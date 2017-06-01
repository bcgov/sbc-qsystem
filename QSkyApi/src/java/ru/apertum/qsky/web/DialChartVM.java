/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.web;

import java.awt.Color;
import org.apache.commons.lang.StringUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.DialModel;
import org.zkoss.zul.DialModelScale;

public class DialChartVM {

    DialModel customersModel;
    DialModel waitingModel;
    DialModel averageModel;

    public DialModel getCustomersModel() {
        return customersModel;
    }

    public DialModel getWaitingModel() {
        return waitingModel;
    }

    public DialModel getAverageModel() {
        return averageModel;
    }

    public DialChartVM() {
        customersModel = ChartData.createCustomersModel(0);
        waitingModel = ChartData.createWaitingModel(0);
        averageModel = ChartData.createAverageModel(0);
    }

    @GlobalCommand("configChanged")
    public void onConfigChanged(@BindingParam("isCelsius") boolean isCelsius, @BindingParam("degree") int degree) {
        if (isCelsius) {
            customersModel.getScale(0).setValue(degree);
            waitingModel.getScale(0).setValue(ChartData.toFahrenhit(degree));
            // ((StandardDialScale) celsiusModel.getScale(0)).setTickLabelFormatter(NumberFormat.getIntegerInstance());
        } else {
            customersModel.getScale(0).setValue(ChartData.toCelsius(degree));
            waitingModel.getScale(0).setValue(degree);
        }
    }

    public static String l(String resName) {
        return Labels.getLabel(resName);
    }

    private static class ChartData {

        public static DialModel createCustomersModel(int value) {
            DialModel model = new DialModel();
            DialModelScale scale = model.newScale(0, 35, 230, -280, 5, 4);
            //scale's configuration data
            scale.setValue(value);
            scale.setText(l("customers"));
            scale.newRange(0, 5, ChartColors.toHtmlColor(Color.getHSBColor(0.55f, 0.8f, 1)), 0.61, 0.603);
            scale.newRange(5, 10, ChartColors.toHtmlColor(Color.getHSBColor(0.3f, 0.8f, 1)), 0.61, 0.603);
            scale.newRange(10, 15, ChartColors.toHtmlColor(Color.getHSBColor(0.18f, 0.8f, 1)), 0.61, 0.603);
            scale.newRange(15, 20, ChartColors.toHtmlColor(Color.getHSBColor(0.12f, 0.8f, 1)), 0.61, 0.603);
            scale.newRange(20, 25, ChartColors.toHtmlColor(Color.getHSBColor(0.08f, 0.8f, 1)), 0.61, 0.603);
            scale.newRange(25, 30, ChartColors.toHtmlColor(Color.getHSBColor(0.05f, 0.8f, 1)), 0.61, 0.603);
            scale.newRange(30, 35, ChartColors.toHtmlColor(Color.getHSBColor(0.0f, 0.8f, 1)), 0.61, 0.603);
            scale.setTickColor("#FFFFFF");
            scale.setNeedleType("pin");
            scale.setNeedleColor("#FF0000");

            model.setFrameFgColor("#808080");
            model.setFrameBgAlpha(255);
            model.setFrameBgColor("#DDDDDD");
            model.setFrameBgColor1("#777777");
            model.setFrameBgColor2("#777777");

            model.setCapRadius(0.1);

            model.setGradientDirection("vertical");

            return model;
        }

        public static DialModel createWaitingModel(int value) {
            DialModel model = new DialModel();
            DialModelScale scale = model.newScale(0, 70, 230, -280, 10, 9);

            //scale's configuration data
            scale.setValue(value);
            scale.setText(l("waiting"));
            scale.newRange(0, 10, ChartColors.toHtmlColor(Color.getHSBColor(0.55f, 0.8f, 1)), 0.91, 0.903);
            scale.newRange(10, 20, ChartColors.toHtmlColor(Color.getHSBColor(0.3f, 0.8f, 1)), 0.91, 0.903);
            scale.newRange(20, 30, ChartColors.toHtmlColor(Color.getHSBColor(0.18f, 0.8f, 1)), 0.91, 0.903);
            scale.newRange(30, 40, ChartColors.toHtmlColor(Color.getHSBColor(0.12f, 0.8f, 1)), 0.91, 0.903);
            scale.newRange(40, 50, ChartColors.toHtmlColor(Color.getHSBColor(0.08f, 0.8f, 1)), 0.91, 0.903);
            scale.newRange(50, 60, ChartColors.toHtmlColor(Color.getHSBColor(0.05f, 0.8f, 1)), 0.91, 0.903);
            scale.newRange(60, 70, ChartColors.toHtmlColor(Color.getHSBColor(0.0f, 0.8f, 1)), 0.91, 0.903);
            scale.setTickColor("#000000");
            scale.setNeedleColor("#FF0000");

            model.setFrameFgColor("#505050");
            model.setFrameBgAlpha(0);
            model.setFrameBgColor("#DDDDDD");
            model.setFrameBgColor1("#FFFFFF");
            model.setFrameBgColor2("#FFFFFF");

            model.setCapRadius(0.06);

            model.setGradientDirection("vertical");

            return model;
        }

        public static DialModel createAverageModel(int value) {
            DialModel model = new DialModel();
            DialModelScale scale = model.newScale(0, 56, 230, -280, 8, 3);
            scale.setTickLabelOffset(0.2);
            scale.setText(l("avg_chart"));
            scale.newRange(0, 8, ChartColors.toHtmlColor(Color.getHSBColor(0.55f, 0.8f, 1)), 0.91, 0.45);
            scale.newRange(8, 16, ChartColors.toHtmlColor(Color.getHSBColor(0.3f, 0.8f, 1)), 0.91, 0.45);
            scale.newRange(16, 24, ChartColors.toHtmlColor(Color.getHSBColor(0.18f, 0.8f, 1)), 0.91, 0.45);
            scale.newRange(24, 32, ChartColors.toHtmlColor(Color.getHSBColor(0.12f, 0.8f, 1)), 0.91, 0.45);
            scale.newRange(32, 40, ChartColors.toHtmlColor(Color.getHSBColor(0.08f, 0.8f, 1)), 0.91, 0.45);
            scale.newRange(40, 48, ChartColors.toHtmlColor(Color.getHSBColor(0.05f, 0.8f, 1)), 0.91, 0.45);
            scale.newRange(48, 56, ChartColors.toHtmlColor(Color.getHSBColor(0.0f, 0.8f, 1)), 0.91, 0.45);
            scale.setTickColor("#FF0000");
            scale.setNeedleType("pin");
            scale.setNeedleColor("#FF0000");

            model.setFrameFgColor("#FFFFFF");
            model.setFrameBgAlpha(255);
            model.setFrameBgColor("#EEEEEE");
            model.setFrameBgColor1("#CCCCCC");
            model.setFrameBgColor2("#CCCCCC");

            model.setCapRadius(0.14);

            model.setGradientDirection("vertical");

            return model;
        }

        public static int toFahrenhit(int celsius) {
            return Math.round(celsius * 9 / 5 + 32);
        }

        public static int toCelsius(int fahrenheit) {
            return Math.round((fahrenheit - 32) * 5 / 9);
        }
    }

    private static class ChartColors {

        public static Color COLOR_1 = new Color(0x3E454C);
        public static Color COLOR_2 = new Color(0x2185C5);
        public static Color COLOR_3 = new Color(0x7ECEFD);
        public static Color COLOR_4 = new Color(0xFFF6E5);
        public static Color COLOR_5 = new Color(0xFF7F66);
        //additional colors
        public static Color COLOR_6 = new Color(0x98D9FF);
        public static Color COLOR_7 = new Color(0x4689B1);
        public static Color COLOR_8 = new Color(0xB17C35);
        public static Color COLOR_9 = new Color(0xFDC77E);

        public static String toHtmlColor(Color color) {
            return "#" + toHexColor(color);
        }

        public static String toHexColor(Color color) {
            return StringUtils.leftPad(Integer.toHexString(color.getRGB() & 0xFFFFFF), 6, '0');
        }
    }
}
