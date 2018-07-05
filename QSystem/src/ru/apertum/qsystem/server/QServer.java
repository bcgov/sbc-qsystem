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
package ru.apertum.qsystem.server;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ServiceLoader;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import ru.apertum.qsystem.About;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.forms.FAbout;
import ru.apertum.qsystem.common.CodepagePrintStream;
import ru.apertum.qsystem.common.GsonPool;
import ru.apertum.qsystem.common.Mailer;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.JsonRPC20;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.ATalkingClock;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.IStartServer;
import ru.apertum.qsystem.hibernate.AnnotationSessionFactoryBean;
import ru.apertum.qsystem.reports.model.QReportsList;
import ru.apertum.qsystem.reports.model.WebServer;
import ru.apertum.qsystem.server.controller.Executer;
import ru.apertum.qsystem.server.http.JettyRunner;
import ru.apertum.qsystem.server.jobs.QRefreshJob;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;

/**
 * ÐšÐ»Ð°Ñ�Ñ� Ñ�Ñ‚Ð°Ñ€Ñ‚Ð° Ð¸ exit Ð¸Ð½Ð¸Ñ†Ð¸Ð°Ð»Ð¸Ð·Ð°Ñ†Ð¸Ð¸ Ñ�ÐµÑ€Ð²ÐµÑ€Ð°. ÐžÑ€Ð³Ð°Ð½Ð¸Ð·Ð°Ñ†Ð¸Ñ� Ð¿Ð¾Ñ‚Ð¾ÐºÐ¾Ð² Ð²Ñ‹Ð¿Ð¾Ð»Ð½ÐµÐ½Ð¸Ñ� Ð·Ð°Ð´Ð°Ð½Ð¸Ð¹.
 *
 * @author Evgeniy Egorov
 */
public class QServer extends Thread {

    private static volatile boolean globalExit = false;
    private final Socket socket;

    /**
     * @param socket
     */
    public QServer(Socket socket) {
        this.socket = socket;
        // Ð¸ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ°ÐµÐ¼ Ð½Ð¾Ð²Ñ‹Ð¹ Ð²Ñ‹Ñ‡Ð¸Ñ�Ð»Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ñ‹Ð¹ Ð¿Ð¾Ñ‚Ð¾Ðº (Ñ�Ð¼. Ñ„-ÑŽ run())
        setDaemon(true);
        setPriority(NORM_PRIORITY);
    }

    /**
     * @param args - Ð¿ÐµÑ€Ð²Ñ‹Ð¼ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ð¾Ð¼ Ð¿ÐµÑ€ÐµÐ´Ð°ÐµÑ‚Ñ�Ñ� Ð¿Ð¾Ð»Ð½Ð¾Ðµ Ð¸Ð¼Ñ� Ð½Ð°Ñ�Ñ‚Ñ€Ð¾ÐµÑ‡Ð½Ð¾Ð³Ð¾ XML-Ñ„Ð°Ð¹Ð»Ð°
     */
    public static void main(String[] args) throws Exception {
        About.printdef();
        QLog.initial(args, 0);
        Locale.setDefault(Locales.getInstance().getLangCurrent());

        //Ð£Ñ�Ñ‚Ð°Ð½Ð¾Ð²ÐºÐ° Ð²Ñ‹Ð²Ð¾Ð´Ð° ÐºÐ¾Ð½Ñ�Ð¾Ð»ÑŒÐ½Ñ‹Ñ… Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ð¹ Ð² Ð½ÑƒÐ¶Ð½Ð¾Ð¹ ÐºÐ¾Ð´Ð¸Ñ€Ð¾Ð²ÐºÐµ
        if ("\\".equals(File.separator)) {
            try {
                String consoleEnc = System.getProperty("console.encoding", "Cp866");
                System.setOut(new CodepagePrintStream(System.out, consoleEnc));
                System.setErr(new CodepagePrintStream(System.err, consoleEnc));
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unable to setup console codepage: " + e);
            }
        }

        System.out.println("Welcome to the QSystem server. Your MySQL must be prepared.");
        FAbout.loadVersionSt();

        if (Locales.getInstance().isRuss) {
            if ("0".equals(FAbout.CMRC_)) {
                System.out
                    .println(
                        "Ð”Ð¾Ð±Ñ€Ð¾ Ð¿Ð¾Ð¶Ð°Ð»Ð¾Ð²Ð°Ñ‚ÑŒ Ð½Ð° Ñ�ÐµÑ€Ð²ÐµÑ€ QSystem. Ð”Ð»Ñ� Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ð½ÐµÐ¾Ð±Ñ…Ð¾Ð´Ð¸Ð¼ MySQL5.5 Ð¸Ð»Ð¸ Ð²Ñ‹ÑˆÐµ.");
                System.out
                    .println(
                        "Ð’ÐµÑ€Ñ�Ð¸Ñ� Ñ�ÐµÑ€Ð²ÐµÑ€Ð°: " + FAbout.VERSION_ + "-community QSystem Server (GPL)");
                System.out.println(
                    "Ð’ÐµÑ€Ñ�Ð¸Ñ� Ð±Ð°Ð·Ñ‹ Ð´Ð°Ð½Ð½Ñ‹Ñ…: " + FAbout.VERSION_DB_
                        + " for MySQL 5.5-community Server (GPL)");
                System.out.println("Ð”Ð°Ñ‚Ð° Ð²Ñ‹Ð¿ÑƒÑ�ÐºÐ° : " + FAbout.DATE_);
                System.out.println("Copyright (c) 2016, Apertum Projects. Ð’Ñ�Ðµ Ð¿Ñ€Ð°Ð²Ð° Ð·Ð°Ñ‰Ð¸Ñ‰ÐµÐ½Ñ‹.");
                System.out
                    .println("QSystem Ñ�Ð²Ð»Ñ�ÐµÑ‚Ñ�Ñ� Ñ�Ð²Ð¾Ð±Ð¾Ð´Ð½Ñ‹Ð¼ Ð¿Ñ€Ð¾Ð³Ñ€Ð°Ð¼Ð¼Ð½Ñ‹Ð¼ Ð¾Ð±ÐµÑ�Ð¿ÐµÑ‡ÐµÐ½Ð¸ÐµÐ¼, Ð²Ñ‹ Ð¼Ð¾Ð¶ÐµÑ‚Ðµ");
                System.out.println(
                    "Ñ€Ð°Ñ�Ð¿Ñ€Ð¾Ñ�Ñ‚Ñ€Ð°Ð½Ñ�Ñ‚ÑŒ Ð¸/Ð¸Ð»Ð¸ Ð¸Ð·Ð¼ÐµÐ½Ñ�Ñ‚ÑŒ ÐµÐ³Ð¾ Ñ�Ð¾Ð³Ð»Ð°Ñ�Ð½Ð¾ ÑƒÑ�Ð»Ð¾Ð²Ð¸Ñ�Ð¼ Ð¡Ñ‚Ð°Ð½Ð´Ð°Ñ€Ñ‚Ð½Ð¾Ð¹ ÐžÐ±Ñ‰ÐµÑ�Ñ‚Ð²ÐµÐ½Ð½Ð¾Ð¹");
                System.out.println(
                    "Ð›Ð¸Ñ†ÐµÐ½Ð·Ð¸Ð¸ GNU (GNU GPL), Ð¾Ð¿ÑƒÐ±Ð»Ð¸ÐºÐ¾Ð²Ð°Ð½Ð½Ð¾Ð¹ Ð¤Ð¾Ð½Ð´Ð¾Ð¼ Ñ�Ð²Ð¾Ð±Ð¾Ð´Ð½Ð¾Ð³Ð¾ Ð¿Ñ€Ð¾Ð³Ñ€Ð°Ð¼Ð¼Ð½Ð¾Ð³Ð¾");
                System.out.println(
                    "Ð¾Ð±ÐµÑ�Ð¿ÐµÑ‡ÐµÐ½Ð¸Ñ� (FSF), Ð»Ð¸Ð±Ð¾ Ð›Ð¸Ñ†ÐµÐ½Ð·Ð¸Ð¸ Ð²ÐµÑ€Ñ�Ð¸Ð¸ 3, Ð»Ð¸Ð±Ð¾ Ð±Ð¾Ð»ÐµÐµ Ð¿Ð¾Ð·Ð´Ð½ÐµÐ¹ Ð²ÐµÑ€Ñ�Ð¸Ð¸.");
                System.out
                    .println(
                        "Ð’Ñ‹ Ð´Ð¾Ð»Ð¶Ð½Ñ‹ Ð±Ñ‹Ð»Ð¸ Ð¿Ð¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ ÐºÐ¾Ð¿Ð¸ÑŽ Ð¡Ñ‚Ð°Ð½Ð´Ð°Ñ€Ñ‚Ð½Ð¾Ð¹ ÐžÐ±Ñ‰ÐµÑ�Ñ‚Ð²ÐµÐ½Ð½Ð¾Ð¹ Ð›Ð¸Ñ†ÐµÐ½Ð·Ð¸Ð¸ GNU Ð²Ð¼ÐµÑ�Ñ‚Ðµ");
                System.out
                    .println("Ñ� Ñ�Ñ‚Ð¾Ð¹ Ð¿Ñ€Ð¾Ð³Ñ€Ð°Ð¼Ð¼Ð¾Ð¹. Ð•Ñ�Ð»Ð¸ Ñ�Ñ‚Ð¾ Ð½Ðµ Ñ‚Ð°Ðº, Ð½Ð°Ð¿Ð¸ÑˆÐ¸Ñ‚Ðµ Ð² Ð¤Ð¾Ð½Ð´ Ð¡Ð²Ð¾Ð±Ð¾Ð´Ð½Ð¾Ð³Ð¾ ÐŸÐž ");
                System.out
                    .println(
                        "(Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA)");
            }

            System.out.println("Ð�Ð°Ð±Ð¸Ñ€Ð¸Ñ‚Ðµ 'exit' Ñ‡Ñ‚Ð¾Ð±Ñ‹ ÑˆÑ‚Ð°Ñ‚Ð½Ð¾ Ð¾Ñ�Ñ‚Ð°Ð½Ð¾Ð²Ð¸Ñ‚ÑŒ Ñ€Ð°Ð±Ð¾Ñ‚Ñƒ Ñ�ÐµÑ€Ð²ÐµÑ€Ð°.");
            System.out.println();
        } else {
            if ("0".equals(FAbout.CMRC_)) {
                System.out
                    .println(
                        "Server version: " + FAbout.VERSION_ + "-community QSystem Server (GPL)");
                System.out.println(
                    "Database version: " + FAbout.VERSION_DB_
                        + " for MySQL 5.5-community Server (GPL)");
                System.out.println("Released : " + FAbout.DATE_);

                System.out.println(
                    "Copyright (c) 2010-2016, Apertum Projects and/or its affiliates. All rights reserved.");
                System.out
                    .println(
                        "This software comes with ABSOLUTELY NO WARRANTY. This is free software,");
                System.out
                    .println(
                        "and you are welcome to modify and redistribute it under the GPL v3 license");
                System.out.println(
                    "Text of this license on your language located in the folder with the program.");
            }

            System.out.println("Type 'exit' to stop work and close server.");
            System.out.println();
        }

        final long start = System.currentTimeMillis();

        // Ð—Ð°Ð³Ñ€ÑƒÐ·ÐºÐ° Ð¿Ð»Ð°Ð³Ð¸Ð½Ð¾Ð² Ð¸Ð· Ð¿Ð°Ð¿ÐºÐ¸ plugins
        if (!QConfig.cfg().isNoPlugins()) {
            Uses.loadPlugins("./plugins/");
        }

        // Ð¿Ð¾Ñ�Ð¼Ð¾Ñ‚Ñ€Ð¸Ð¼ Ð½Ðµ Ð½ÑƒÐ¶Ð½Ð¾ Ð»Ð¸ Ñ�Ñ‚Ð°Ñ€Ñ‚Ð°Ð½ÑƒÑ‚ÑŒ jetty
        // Ð´Ð»Ñ� Ñ�Ñ‚Ð¾Ð³Ð¾ Ð½ÑƒÐ¶Ð½Ð¾ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ°Ñ‚ÑŒ Ñ� ÐºÐ»ÑŽÑ‡Ð¾Ð¼ http
        // ÐµÑ�Ð»Ð¸ ÐµÑ‚Ñ�ÑŒ ÐºÐ»ÑŽÑ‡ http, Ñ‚Ð¾ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ°ÐµÐ¼ Ñ�ÐµÑ€Ð²ÐµÑ€ Ð¸ Ð¿Ñ€Ð¸Ð½Ð¸Ð¼Ð°ÐµÐ¼ Ð½Ð° Ð½ÐµÐ¼ ÐºÐ¾Ð¼Ð°Ð½Ð´Ñ‹ Ñ�ÐµÑ€Ð²ÐµÑ€Ñƒ Ñ�ÑƒÐ¾
        if (QConfig.cfg().getHttp() > 0) {
            QLog.l().logger().info("Run Jetty.");
            try {
                JettyRunner.start(QConfig.cfg().getHttp());
            } catch (NumberFormatException ex) {
                QLog.l().logger().error(
                    "Ð�Ð¾Ð¼ÐµÑ€ Ð¿Ð¾Ñ€Ñ‚Ð° Ð´Ð»Ñ� Jetty Ð² Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ð°Ñ… Ð·Ð°Ð¿ÑƒÑ�ÐºÐ° Ð½Ðµ Ñ�Ð²Ð»Ñ�ÐµÑ‚Ñ�Ñ� Ñ‡Ð¸Ñ�Ð»Ð¾Ð¼. Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ð° Ð´Ð»Ñ� Ð¿Ð¾Ñ€Ñ‚Ð° 8081 '-http 8081'.",
                    ex);
            }
        }

        // ÐžÑ‚Ñ‡ÐµÑ‚Ð½Ñ‹Ð¹ Ñ�ÐµÑ€Ð²ÐµÑ€, Ð²Ñ‹Ñ�Ñ‚ÑƒÐ¿Ð°ÑŽÑ‰Ð¸Ð¹ Ð² Ñ€Ð¾Ð»Ð¸ Ð²Ñ�Ð±Ñ�ÐµÑ€Ð²ÐµÑ€Ð°, Ð¾Ð±Ñ€Ð°Ð±Ð°Ñ‚Ñ‹Ð²Ð°ÑŽÑ‰ÐµÐ³Ð¾ Ð·Ð°Ð¿Ñ€Ð¾Ñ�Ñ‹ Ð½Ð° Ð²Ñ‹Ð´Ð°Ñ‡Ñƒ Ð¾Ñ‚Ñ‡ÐµÑ‚Ð¾Ð²
        WebServer.getInstance()
            .startWebServer(ServerProps.getInstance().getProps().getWebServerPort());
        loadPool();
        // Ð·Ð°Ð¿ÑƒÑ�ÐºÐ°ÐµÐ¼ Ð´Ð²Ð¸Ð¶Ð¾Ðº Ð¸Ð½Ð´Ð¸ÐºÐ°Ñ†Ð¸Ð¸ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ñ� Ð´Ð»Ñ� ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð²
        //MainBoard.getInstance().showBoard();
        // test ServerProps.getInstance().getProps().getZoneBoardServAddrList();
        if (!(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getStartTime())
            .equals(
                Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getFinishTime())))) {
            /**
             * Ð¢Ð°Ð¹Ð¼ÐµÑ€, Ð¿Ð¾ ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð¼Ñƒ Ð±ÑƒÐ´ÐµÐ¼ ÐžÑ‡Ð¸Ñ�Ñ‚ÐºÐ° Ð²Ñ�ÐµÑ… ÑƒÑ�Ð»ÑƒÐ³ Ð¸ Ñ€Ð°Ñ�Ñ�Ñ‹Ð»ÐºÐ° Ñ�Ð¿Ð°Ð¼Ð° Ñ� Ð´Ð½ÐµÐ²Ð½Ñ‹Ð¼ Ð¾Ñ‚Ñ‡ÐµÑ‚Ð¾Ð¼.
             */
            ATalkingClock clearServices = new ATalkingClock(Uses.DELAY_CHECK_TO_LOCK, 0) {

                @Override
                public void run() {
                    // Ñ�Ñ‚Ð¾ Ð¾Ð±Ð½ÑƒÐ»ÐµÐ½Ð¸Ðµ :: This obnulenye
                    if (!QConfig.cfg().isRetain() && Uses.FORMAT_HH_MM
                        .format(new Date(new Date().getTime() + 10 * 60 * 1000)).equals(
                            Uses.FORMAT_HH_MM
                                .format(ServerProps.getInstance().getProps().getStartTime()))) {
                        QLog.l().logger().info("ÐžÑ‡Ð¸Ñ�Ñ‚ÐºÐ° Ð²Ñ�ÐµÑ… ÑƒÑ�Ð»ÑƒÐ³.");
                        // Ð¿Ð¾Ñ‡Ð¸Ñ�Ñ‚Ð¸Ð¼ Ð²Ñ�Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸ Ð¾Ñ‚ Ñ‚Ñ€ÑƒÐ¿Ð¾Ð² ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ñ� Ð¿Ñ€Ð¾ÑˆÐ»Ð¾Ð³Ð¾ Ð´Ð½Ñ�
                        QServer.clearAllQueue();
                    }

                    // Ñ�Ñ‚Ð¾ Ñ€Ð°Ñ�Ñ�Ñ‹Ð»ÐºÐ° Ð´Ð½ÐµÐ²Ð½Ð¾Ð³Ð¾ Ð¾Ñ‚Ñ‡ÐµÑ‚Ð° :: This is a daily report
                    if (("true".equalsIgnoreCase(Mailer.fetchConfig().getProperty("mailing")) || "1"
                        .equals(Mailer.fetchConfig().getProperty("mailing")))
                        && Uses.FORMAT_HH_MM.format(new Date(new Date().getTime() - 30 * 60 * 1000))
                        .equals(
                            Uses.FORMAT_HH_MM
                                .format(ServerProps.getInstance().getProps().getFinishTime()))) {
                        QLog.l().logger().info("Ð Ð°Ñ�Ñ�Ñ‹Ð»ÐºÐ° Ð´Ð½ÐµÐ²Ð½Ð¾Ð³Ð¾ Ð¾Ñ‚Ñ‡ÐµÑ‚Ð°.");
                        // Ð¿Ð¾Ñ‡Ð¸Ñ�Ñ‚Ð¸Ð¼ Ð²Ñ�Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸ Ð¾Ñ‚ Ñ‚Ñ€ÑƒÐ¿Ð¾Ð² ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð² Ñ� Ð¿Ñ€Ð¾ÑˆÐ»Ð¾Ð³Ð¾ Ð´Ð½Ñ�
                        // Clean all services from the corpses of custodians from the last day
                        for (QUser user : QUserList.getInstance().getItems()) {
                            if (user.getReportAccess()) {
                                final HashMap<String, String> p = new HashMap<>();
                                p.put("date", Uses.FORMAT_DD_MM_YYYY.format(new Date()));
                                final byte[] result = QReportsList.getInstance()
                                    .generate(user, "/distribution_job_day.pdf", p);
                                try {
                                    try (FileOutputStream fos = new FileOutputStream(
                                        "temp/distribution_job_day.pdf")) {
                                        fos.write(result);
                                        fos.flush();
                                    }
                                    Mailer.sendReporterMailAtFon(null, null, null,
                                        "temp/distribution_job_day.pdf");
                                } catch (Exception ex) {
                                    QLog.l().logger().error("ÐšÐ°ÐºÐ¾Ð¹-Ñ‚Ð¾ Ð¾Ð±Ð»Ð¾Ð¼ Ñ� Ð´Ð½ÐµÐ²Ð½Ñ‹Ð¼ Ð¾Ñ‚Ñ‡ÐµÑ‚Ð¾Ð¼", ex);
                                }
                                break;
                            }
                        }
                    }
                }
            };
            clearServices.start();
        }

        // Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ñ� Ð¿Ð»Ð°Ð³Ð¸Ð½Ð¾Ð², ÐºÐ¾Ñ‚Ð¾Ñ€Ñ‹Ðµ Ñ�Ñ‚Ð°Ñ€Ñ‚ÑƒÑŽÑ‚ Ð² Ñ�Ð°Ð¼Ð¾Ð¼ Ð½Ð°Ñ‡Ð°Ð»Ðµ.
        // Ð¿Ð¾Ð´Ð´ÐµÑ€Ð¶ÐºÐ° Ñ€Ð°Ñ�ÑˆÐ¸Ñ€Ñ�ÐµÐ¼Ð¾Ñ�Ñ‚Ð¸ Ð¿Ð»Ð°Ð³Ð¸Ð½Ð°Ð¼Ð¸
        for (final IStartServer event : ServiceLoader.load(IStartServer.class)) {
            QLog.l().logger().info("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ�. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + event.getDescription());
            try {
                new Thread(() -> {
                    event.start();
                }).start();
            } catch (Throwable tr) {
                QLog.l().logger().error("Ð’Ñ‹Ð·Ð¾Ð² SPI Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸Ñ� Ð·Ð°Ð²ÐµÑ€ÑˆÐ¸Ð»Ñ�Ñ� Ð¾ÑˆÐ¸Ð±ÐºÐ¾Ð¹. ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ: " + tr);
            }
        }

        // Ð¿Ñ€Ð¸Ð²Ð¸Ð½Ñ‚Ð¸Ñ‚ÑŒ Ñ�Ð¾ÐºÐµÑ‚ Ð½Ð° Ð»Ð¾ÐºÐ°Ð»Ñ…Ð¾Ñ�Ñ‚, Ð¿Ð¾Ñ€Ñ‚ 3128
        final ServerSocket server;
        try {
            QLog.l().logger().info(
                "Ð¡ÐµÑ€Ð²ÐµÑ€ Ñ�Ð¸Ñ�Ñ‚ÐµÐ¼Ñ‹ Ð·Ð°Ñ…Ð²Ð°Ñ‚Ñ‹Ð²Ð°ÐµÑ‚ Ð¿Ð¾Ñ€Ñ‚ \"" + ServerProps.getInstance().getProps()
                    .getServerPort() + "\".");
            server = new ServerSocket(ServerProps.getInstance().getProps().getServerPort());
        } catch (IOException e) {
            throw new ServerException("Network error. Creating net socket is not possible: " + e);
        } catch (Exception e) {
            throw new ServerException("Network error: " + e);
        }
        server.setSoTimeout(500);
        final AnnotationSessionFactoryBean as = (AnnotationSessionFactoryBean) Spring.getInstance()
            .getFactory().getBean("conf");
        System.out.println("Server QSystem started.\n");
        QLog.l().logger().info(
            "Ð¡ÐµÑ€Ð²ÐµÑ€ Ñ�Ð¸Ñ�Ñ‚ÐµÐ¼Ñ‹ 'ÐžÑ‡ÐµÑ€ÐµÐ´ÑŒ' Ð·Ð°Ð¿ÑƒÑ‰ÐµÐ½. DB name='" + as.getName() + "' url=" + as.getUrl());
        int pos = 0;
        boolean exit = false;
        // Ñ�Ð»ÑƒÑˆÐ°ÐµÐ¼ Ð¿Ð¾Ñ€Ñ‚
        while (!globalExit && !exit) {
            // Ð¶Ð´Ñ‘Ð¼ Ð½Ð¾Ð²Ð¾Ð³Ð¾ Ð¿Ð¾Ð´ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ñ�, Ð¿Ð¾Ñ�Ð»Ðµ Ñ‡ÐµÐ³Ð¾ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ°ÐµÐ¼ Ð¾Ð±Ñ€Ð°Ð±Ð¾Ñ‚ÐºÑƒ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð°
            // Ð² Ð½Ð¾Ð²Ñ‹Ð¹ Ð²Ñ‹Ñ‡Ð¸Ñ�Ð»Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ñ‹Ð¹ Ð¿Ð¾Ñ‚Ð¾Ðº Ð¸ ÑƒÐ²ÐµÐ»Ð¸Ñ‡Ð¸Ð²Ð°ÐµÐ¼ Ñ�Ñ‡Ñ‘Ñ‚Ñ‡Ð¸Ðº Ð½Ð° ÐµÐ´Ð¸Ð½Ð¸Ñ‡ÐºÑƒ

            try {
                final QServer qServer = new QServer(server.accept());
                qServer.start();
                if (QConfig.cfg().isDebug()) {
                    System.out.println();
                }
            } catch (SocketTimeoutException e) {
                // Ð½Ð¸Ñ‡ÐµÐ³Ð¾ Ñ�Ñ‚Ñ€Ð°ÑˆÐ½Ð¾Ð³Ð¾, Ð³Ð°Ñ�Ð¸Ð¼ Ð¸Ñ�ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ Ñ�Ñ‚Ð¾Ð±Ñ‹ Ð´Ð°Ñ‚ÑŒ Ð²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ð¾Ñ�Ñ‚ÑŒ Ð¾Ñ‚Ñ€Ð°Ð±Ð¾Ñ‚Ð°Ñ‚ÑŒ Ð²Ñ…Ð¾Ð´Ð½Ð¾Ð¼Ñƒ/Ð²Ñ‹Ñ…Ð¾Ð´Ð½Ð¾Ð¼Ñƒ Ð¿Ð¾Ñ‚Ð¾ÐºÑƒ
            } catch (Exception e) {
                throw new ServerException("Network error: " + e);
            }

            if (!QConfig.cfg().isDebug()) {
                final char ch = '*';
                String progres = "Process: " + ch;
                final int len = 5;
                for (int i = 0; i < pos; i++) {
                    progres = progres + ch;
                }
                for (int i = 0; i < len; i++) {
                    progres = progres + ' ';
                }
                if (++pos == len) {
                    pos = 0;
                }
                //System.out.print(progres);
                //System.out.write(13);// '\b' - Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ ÐºÐ¾Ñ€Ñ€ÐµÑ‚ÐºÑƒ Ð½Ð° Ð¾Ð´Ð½Ñƒ Ð¿Ð¾Ð·Ð¸Ñ†Ð¸ÑŽ Ð½Ð°Ð·Ð°Ð´
            }

            // ÐŸÐ¾Ð¿Ñ€Ð¾Ð±ÑƒÐµÐ¼ Ñ�Ñ‡Ð¸Ñ‚Ð°Ñ‚ÑŒ Ð½Ð°Ð¶Ð°Ñ‚ÑƒÑŽ ÐºÐ»Ð°Ð²Ð¸ÑˆÑƒ
            // ÐµÑ�Ð»Ð¸ Ð½Ð°Ð¶Ð°Ð´Ð¸ ENTER, Ñ‚Ð¾ Ð·Ð°Ð²ÐµÑ€ÑˆÐ°ÐµÐ¼ Ñ€Ð°Ð±Ð¾Ñ‚Ñƒ Ñ�ÐµÑ€Ð²ÐµÑ€Ð°
            // Ð¸ Ð·Ð°Ñ‚Ð¸Ñ€Ð°ÐµÐ¼ Ñ„Ð°Ð¹Ð» Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ð¾Ð³Ð¾ Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ñ� Uses.TEMP_STATE_FILE
            //BufferedReader r = new BufferedReader(new StreamReader(System.in));
            int bytesAvailable = System.in.available();
            if (bytesAvailable > 0) {
                byte[] data = new byte[bytesAvailable];
                System.in.read(data);
                if (bytesAvailable == 5
                    && data[0] == 101
                    && data[1] == 120
                    && data[2] == 105
                    && data[3] == 116
                    && ((data[4] == 10) || (data[4] == 13))) {
                    // Ð½Ð°Ð±Ñ€Ð°Ð»Ð¸ ÐºÐ¾Ð¼Ð°Ð½Ð´Ñƒ "exit" Ð¸ Ð½Ð°Ð¶Ð°Ð»Ð¸ ENTER
                    QLog.l().logger().info("Ð—Ð°Ð²ÐµÑ€ÑˆÐµÐ½Ð¸Ðµ Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹ Ñ�ÐµÑ€Ð²ÐµÑ€Ð°.");
                    exit = true;
                }
            }
        }// while

        QLog.l().logger().debug("Ð—Ð°ÐºÑ€Ñ‹Ð²Ð°ÐµÐ¼ Ñ�ÐµÑ€Ð²ÐµÑ€Ð½Ñ‹Ð¹ Ñ�Ð¾ÐºÐµÑ‚.");
        server.close();
        QLog.l().logger().debug("ÐžÑ�Ñ‚Ð°Ð½Ð¾Ð² Jetty.");
        JettyRunner.stop();
        QLog.l().logger().debug("ÐžÑ�Ñ‚Ð°Ð½Ð¾Ð² Ð¾Ñ‚Ñ‡ÐµÑ‚Ð½Ð¾Ð³Ð¾ Ð²Ñ�Ð±Ñ�ÐµÑ€Ð²ÐµÑ€Ð°.");
        WebServer.getInstance().stopWebServer();
        QLog.l().logger().debug("Ð’Ñ‹ÐºÐ»ÑŽÑ‡ÐµÐ½Ð¸Ðµ Ñ†ÐµÐ½Ñ‚Ñ€Ð°Ð»ÑŒÐ½Ð¾Ð³Ð¾ Ñ‚Ð°Ð±Ð»Ð¾.");
        MainBoard.getInstance().close();

        Thread.sleep(1500);
        QLog.l().logger().info("Ð¡ÐµÑ€Ð²ÐµÑ€ ÑˆÑ‚Ð°Ñ‚Ð½Ð¾ Ð·Ð°Ð²ÐµÑ€ÑˆÐ¸Ð» Ñ€Ð°Ð±Ð¾Ñ‚Ñƒ. Ð’Ñ€ÐµÐ¼Ñ� Ñ€Ð°Ð±Ð¾Ñ‚Ñ‹: " + Uses
            .roundAs(((double) (System.currentTimeMillis() - start)) / 1000 / 60, 2) + " Ð¼Ð¸Ð½.");
        System.exit(0);
    }

    /**
     * Ð—Ð°Ð³Ñ€ÑƒÐ·ÐºÐ° Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ñ� Ð¿ÑƒÐ»Ð° ÑƒÑ�Ð»ÑƒÐ³ Ð¸Ð· Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ð¾Ð³Ð¾ json-Ñ„Ð°Ð¹Ð»Ð°
     */
    static public void loadPool() {
        QLog.l().logQUser().debug("loadPool");

        final LinkedList<QCustomer> customers = new LinkedList<QCustomer>(
            Spring.getInstance().getHt().findByCriteria(
                DetachedCriteria.forClass(QCustomer.class)
                    .add(Property.forName("stateIn").ne(0))
                    .add(Property.forName("stateIn").ne(10))
                    .add(Property.forName("stateIn").ne(11))
                    .add(Property.forName("stateIn").ne(12))
                    .add(Property.forName("stateIn").ne(13))
                    .setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))));

        QLog.l().logQUser().debug("adding Customers to hold list from database");
        for (QCustomer cust : customers) {
//            QLog.l().logQUser().debug("Customer: " + cust + ", " + cust.getId());
//            QLog.l().logQUser().debug("serviceId: " + cust.getService().getId());
            final QService service = QServiceTree.getInstance().getById(cust.getService().getId());
            if (service == null) {
                //QLog.l().logQUser().debug("null... next");
                continue;
            }

            service.setCountPerDay(cust.getService().getCountPerDay());
            service.setDay(cust.getService().getDay());

            final QUser user = cust.getUser();
            cust.setService(service);

            if (user != null) {
                //QLog.l().logQUser().debug("user not null");
                // Ñ�Ð¾Ñ…Ñ€Ð°Ð½ÐµÐ½Ð½Ñ‹Ð¹ ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€ Ð¾Ð±Ñ€Ð°Ð±Ð°Ñ‚Ñ‹Ð²Ð°Ð»Ñ�Ñ� ÑŽÐ·ÐµÑ€Ð¾Ð¼ Ñ� Ð¸Ð¼ÐµÐ½ÐµÐ¼ userId
                if (QUserList.getInstance().getById(user.getId()) == null) {
                    continue;
                }
                QUserList.getInstance().getById(user.getId()).setCustomer(cust);
                cust.setUser(QUserList.getInstance().getById(user.getId()));
            }

            //QLog.l().logQUser().debug("setPriority");
            cust.setPriority(1);

            //QLog.l().logQUser().debug("setState: " + cust.getStateIn());
            Integer state = cust.getStateIn();
            cust.setStateWithoutSave(state);
            cust.setAddedBy(cust.getUser().getName());

            //QLog.l().logQUser().debug("Adding customer to serviceTree");
            if (cust.getStateIn() == 1 ||cust.getStateIn() == 2 || cust.getStateIn() == 3) {
                //QLog.l().logQUser().debug("Adding customer to serviceTree");
                QServiceTree.getInstance().getById(cust.getService().getId()).addCustomer(cust);
            } else {
                //QLog.l().logQUser().debug("Skip adding customer");
            }

        }

        //QLog.l().logQUser().debug("Refreshing postponed list");
        QPostponedList.getInstance().loadPostponedList(new LinkedList<QCustomer>());
/*
        //Set a job to refresh every two minutes the necessary lists and tree caches
        JobDetail job = JobBuilder.newJob(QRefreshJob.class)
            .withIdentity("OfficeRefreshJob", "group1").build();

        Trigger trigger = TriggerBuilder
            .newTrigger()
            .withIdentity("refreshQSystemLists", "group1")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?""))
            .build();

        try {
            QLog.l().logQUser().info("Starting schedule for refresh");
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            QLog.l().logQUser().info("Success");
        } catch(SchedulerException e) {
            QLog.l().logQUser().warn("Error scheduling refresh", e);
        }
*/

        return;
    }

    static public void clearAllQueue() {
        // Ð¿Ð¾Ñ‡Ð¸Ñ�Ñ‚Ð¸Ð¼ Ð²Ñ�Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸ Ð¾Ñ‚ Ñ‚Ñ€ÑƒÐ¿Ð¾Ð² ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð²
        QServiceTree.getInstance().getNodes().forEach((service) -> {
            service.clearNextNumber();
            service.freeCustomers();
        });
        QService.clearNextStNumber();

        QPostponedList.getInstance().clear();
        MainBoard.getInstance().clear();

        // Ð¡Ð¾Ñ‚Ñ€ÐµÐ¼ Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ñ‹Ðµ Ñ„Ð°Ð¹Ð»Ñ‹
        QLog.l().logger().info("ÐžÑ‡Ð¸Ñ�Ñ‚ÐºÐ° Ð²Ñ�ÐµÑ… Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¹ Ð¾Ñ‚ Ð¿Ñ€Ð¸Ð²Ñ�Ð·Ð°Ð½Ð½Ñ‹Ñ… ÐºÐ°Ñ�Ñ‚Ð¾Ð¼ÐµÑ€Ð¾Ð².");
        QUserList.getInstance().getItems().forEach((user) -> {
            user.setCustomer(null);
            user.getParallelCustomers().clear();
            user.setShadow(null);
            user.getPlanServices().forEach((plan) -> {
                plan.setAvg_wait(0);
                plan.setAvg_work(0);
                plan.setKilled(0);
                plan.setWorked(0);
            });
        });
    }

    @Override
    public void run() {
        try {
            QLog.l().logger().debug(
                " Start thread for receiving task. host=" + socket.getInetAddress().getHostAddress()
                    + " ip=" + Arrays.toString(socket.getInetAddress().getAddress()));

            // Ð¸Ð· Ñ�Ð¾ÐºÐµÑ‚Ð° ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð° Ð±ÐµÑ€Ñ‘Ð¼ Ð¿Ð¾Ñ‚Ð¾Ðº Ð²Ñ…Ð¾Ð´Ñ�Ñ‰Ð¸Ñ… Ð´Ð°Ð½Ð½Ñ‹Ñ…
            // From the client's socket we take the stream of incoming data
            InputStream is;
            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                throw new ServerException(
                    "Input Stream broken: " + Arrays.toString(e.getStackTrace()));
            }

            final String data;
            try {
                // Ð¿Ð¾Ð´Ð¾Ð¶Ð´Ð°Ñ‚ÑŒ Ð¿Ð¾ÐºÐ° Ñ…Ð¾Ñ‚ÑŒ Ñ‡Ñ‚Ð¾-Ñ‚Ð¾ Ð¿Ñ€Ð¸Ð¿Ð¾Ð»Ð·ÐµÑ‚ Ð¸Ð· Ñ�ÐµÑ‚Ð¸, Ð½Ð¾ Ð½Ðµ Ð±Ð¾Ð»ÐµÐµ 10 Ñ�ÐµÐº.
                // Wait until at least something crawls out of the network, but no more than 10 seconds.
                int i = 0;
                while (is.available() == 0 && i < 100) {
                    Thread.sleep(100);//Ð±Ð»Ñ�
                    i++;
                }

                StringBuilder sb = new StringBuilder(new String(Uses.readInputStream(is)));
                while (is.available() != 0) {
                    sb = sb.append(new String(Uses.readInputStream(is)));
                    Thread.sleep(150);//Ð±Ð»Ñ�
                }
                data = URLDecoder.decode(sb.toString(), "utf-8");
            } catch (IOException ex) {
                throw new ServerException("ÐžÑˆÐ¸Ð±ÐºÐ° Ð¿Ñ€Ð¸ Ñ‡Ñ‚ÐµÐ½Ð¸Ð¸ Ð¸Ð· Ð²Ñ…Ð¾Ð´Ð½Ð¾Ð³Ð¾ Ð¿Ð¾Ñ‚Ð¾ÐºÐ°: " + ex);
            } catch (InterruptedException ex) {
                throw new ServerException("ÐŸÑ€Ð¾Ð±Ð»ÐµÐ¼Ð° Ñ�Ð¾ Ñ�Ð½Ð¾Ð¼: " + ex);
            } catch (IllegalArgumentException ex) {
                throw new ServerException("ÐžÑˆÐ¸Ð±ÐºÐ° Ð´ÐµÐºÐ¾Ð´Ð¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ� Ñ�ÐµÑ‚ÐµÐ²Ð¾Ð³Ð¾ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ñ�: " + ex);
            }
            QLog.l().logger()
                .trace("Task:\n" + (data.length() > 200 ? (data.substring(0, 200) + "...") : data));

            /*
             Ð•Ñ�Ð»Ð¸ Ð¿Ð¾ Ñ�ÐµÑ‚ÐºÐµ Ð¿Ð¾Ð¹Ð¼Ð°Ð»Ð¸ exit, Ñ‚Ð¾ Ñ�Ñ‚Ð¾ Ð·Ð½Ð°Ñ‡Ð¸Ñ‚ Ñ‡Ñ‚Ð¾ Ð·Ð°Ð¿ÑƒÑ�Ñ‚Ð¸Ð»Ð¸ Ð¾Ñ�Ñ‚Ð°Ð½Ð°Ð²Ð»Ð¸Ð²Ð°ÑŽÑ‰Ð¸Ð¹ Ð±Ð°Ñ‚Ð½Ð¸Ðº.
            If you caught the exit on the grid, it means that you started the stop batch file.
             */
            if ("exit".equalsIgnoreCase(data)) {
                globalExit = true;
                return;
            }

            final String answer;
            final JsonRPC20 rpc;
            final Gson gson = GsonPool.getInstance().borrowGson();
            try {
                rpc = gson.fromJson(data, JsonRPC20.class);
                // Ð¿Ð¾Ð»ÑƒÑ‡ÐµÐ½Ð½Ð¾Ðµ Ð·Ð°Ð´Ð°Ð½Ð¸Ðµ Ð¿ÐµÑ€ÐµÐ´Ð°ÐµÐ¼ Ð² Ð¿ÑƒÐ»
                // We send the received task to the pool
                final Object result = Executer.getInstance()
                    .doTask(rpc, socket.getInetAddress().getHostAddress(),
                        socket.getInetAddress().getAddress());
                answer = gson.toJson(result);
            } catch (JsonSyntaxException ex) {
                QLog.l().logger()
                    .error("Received data \"" + data + "\" has not correct JSOM format. ", ex);
                throw new ServerException(
                    "Received data \"" + data + "\" has not correct JSOM format. " + Arrays
                        .toString(ex.getStackTrace()));
            } catch (Exception ex) {
                QLog.l().logger().error("Late caught the error when running the command. ", ex);
                throw new ServerException(
                    "ÐŸÐ¾Ð·Ð´Ð½Ð¾ Ð¿Ð¾Ð¹Ð¼Ð°Ð½Ð½Ð°Ñ� Ð¾ÑˆÐ¸Ð±ÐºÐ° Ð¿Ñ€Ð¸ Ð²Ñ‹Ð¿Ð¾Ð»Ð½ÐµÐ½Ð¸Ð¸ ÐºÐ¾Ð¼Ð°Ð½Ð´Ñ‹: " + Arrays
                        .toString(ex.getStackTrace()));
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }

            // Ð²Ñ‹Ð²Ð¾Ð´Ð¸Ð¼ Ð´Ð°Ð½Ð½Ñ‹Ðµ:
            QLog.l().logger().trace(
                "Response:\n" + (answer.length() > 200 ? (answer.substring(0, 200) + "...")
                    : answer));
            try {
                // ÐŸÐµÑ€ÐµÐ´Ð°Ñ‡Ð° Ð´Ð°Ð½Ð½Ñ‹Ñ… Ð¾Ñ‚Ð²ÐµÑ‚Ð°
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.print(URLEncoder.encode(answer, "utf-8"));
                writer.flush();
            } catch (IOException e) {
                throw new ServerException(
                    "ÐžÑˆÐ¸Ð±ÐºÐ° Ð¿Ñ€Ð¸ Ð·Ð°Ð¿Ð¸Ñ�Ð¸ Ð² Ð¿Ð¾Ñ‚Ð¾Ðº: " + Arrays.toString(e.getStackTrace()));
            }
        } catch (ServerException | JsonParseException ex) {
            final StringBuilder sb = new StringBuilder("\nStackTrace:\n");
            for (StackTraceElement bag : ex.getStackTrace()) {
                sb.append("    at ").append(bag.getClassName()).append(".")
                    .append(bag.getMethodName())
                    .append("(").append(bag.getFileName()).append(":").append(bag.getLineNumber())
                    .append(")\n");
            }
            final String err = sb.toString() + "\n";
            sb.setLength(0);
            throw new ServerException("ÐžÑˆÐ¸Ð±ÐºÐ° Ð¿Ñ€Ð¸ Ð²Ñ‹Ð¿Ð¾Ð»Ð½ÐµÐ½Ð¸Ð¸ Ð·Ð°Ð´Ð°Ð½Ð¸Ñ�.\n" + ex + err);
        } finally {
            // Ð·Ð°Ð²ÐµÑ€ÑˆÐ°ÐµÐ¼ Ñ�Ð¾ÐµÐ´Ð¸Ð½ÐµÐ½Ð¸Ðµ
            try {
                //Ð¾Ð±Ð¾Ñ€Ð°Ñ‡Ð¸Ð²Ð°ÐµÐ¼ close, Ñ‚.Ðº. Ð¾Ð½ Ñ�Ð°Ð¼ Ð¼Ð¾Ð¶ÐµÑ‚ Ñ�Ð³ÐµÐ½ÐµÑ€Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÑŒ Ð¾ÑˆÐ¸Ð±ÐºÑƒ IOExeption. ÐŸÑ€Ð¾Ñ�Ñ‚Ð¾ Ð²Ñ‹ÐºÐ¸Ð½ÐµÐ¼ Ð¡Ñ‚ÐµÐº-Ñ‚Ñ€ÐµÐ¹Ñ�
                socket.close();
            } catch (IOException e) {
                QLog.l().logger().trace(e);
            }
            QLog.l().logger().trace("Response was finished");
        }
    }
}
