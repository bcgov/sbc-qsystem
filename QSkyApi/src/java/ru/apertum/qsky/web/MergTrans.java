/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.web;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Evgeniy Egorov
 */
public class MergTrans {

    public static void main(String[] args) {
        final String paramsFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label.properties";
        final String dataFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\src.txt";

        String resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_pt_PT.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_es_ES.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_de_DE.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_fr_FR.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_it_IT.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_cs_CZ.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_pl_PL.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_sk_SK.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_ro_RO.properties";
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_sr_SP.properties";//serbian
        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_uk_UA.properties";
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_tr_TR.properties";
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_hi_IN.properties";
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_ar_EG.properties";
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_iw_IL.properties";
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_kk_KZ.properties";
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_in_ID.properties";//indonesia
//        resFileName = "E:\\WORK\\apertum-qsystem.qskyapi\\web\\WEB-INF\\i3-label_fi_FI.properties";

        final LinkedList<String> params = new LinkedList<>();

        System.out.println();
        System.out.println(paramsFileName);
        System.out.println();
        try (FileInputStream fis = new FileInputStream(paramsFileName); Scanner s = new Scanner(fis)) {
            while (s.hasNextLine()) {
                final String line = s.nextLine().trim();
                if (!line.startsWith("#") && !line.isEmpty() && line.contains("=")) {
                    params.add(line.split("=")[0].trim());
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
        params.stream().forEach((param) -> {
            System.out.println(param);
        });

        final LinkedList<String> data = new LinkedList<>();

        System.out.println();
        System.out.println(dataFileName);
        System.out.println();
        try (FileInputStream fis = new FileInputStream(dataFileName); Scanner s = new Scanner(fis)) {
            while (s.hasNextLine()) {
                final String line = s.nextLine().trim();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    data.add(line.substring(0, 1).toUpperCase() + line.substring(1));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
        data.stream().forEach((data1) -> {
            System.out.println(data1);
        });

        System.out.println();
        System.out.println(resFileName);
        System.out.println();
        try (FileOutputStream fos = new FileOutputStream(resFileName);) {
            for (int i = 0; i < params.size() && i < data.size(); i++) {
                fos.write((params.get(i) + "=" + data.get(i) + "\n").getBytes("utf-8"));
                System.out.println(params.get(i) + "=" + data.get(i));
            }
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
    }

}
