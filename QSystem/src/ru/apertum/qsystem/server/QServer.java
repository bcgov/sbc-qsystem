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
 * Класс старта и exit инициализации сервера. Организация потоков выполнения заданий.
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
        // и запускаем новый вычислительный поток (см. ф-ю run())
        setDaemon(true);
        setPriority(NORM_PRIORITY);
    }

    /**
     * @param args - первым параметром передается полное имя настроечного XML-файла
     */
    public static void main(String[] args) throws Exception {
        About.printdef();
        QLog.initial(args, 0);
        Locale.setDefault(Locales.getInstance().getLangCurrent());

        //Установка вывода консольных сообщений в нужной кодировке
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
                        "Добро пожаловать на сервер QSystem. Для работы необходим MySQL5.5 или выше.");
                System.out
                    .println(
                        "Версия сервера: " + FAbout.VERSION_ + "-community QSystem Server (GPL)");
                System.out.println(
                    "Версия базы данных: " + FAbout.VERSION_DB_
                        + " for MySQL 5.5-community Server (GPL)");
                System.out.println("Дата выпуска : " + FAbout.DATE_);
                System.out.println("Copyright (c) 2016, Apertum Projects. Все права защищены.");
                System.out
                    .println("QSystem является свободным программным обеспечением, вы можете");
                System.out.println(
                    "распространять и/или изменять его согласно условиям Стандартной Общественной");
                System.out.println(
                    "Лицензии GNU (GNU GPL), опубликованной Фондом свободного программного");
                System.out.println(
                    "обеспечения (FSF), либо Лицензии версии 3, либо более поздней версии.");
                System.out
                    .println(
                        "Вы должны были получить копию Стандартной Общественной Лицензии GNU вместе");
                System.out
                    .println("с этой программой. Если это не так, напишите в Фонд Свободного ПО ");
                System.out
                    .println(
                        "(Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA)");
            }

            System.out.println("Набирите 'exit' чтобы штатно остановить работу сервера.");
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

        // Загрузка плагинов из папки plugins
        if (!QConfig.cfg().isNoPlugins()) {
            Uses.loadPlugins("./plugins/");
        }

        // посмотрим не нужно ли стартануть jetty
        // для этого нужно запускать с ключом http
        // если етсь ключ http, то запускаем сервер и принимаем на нем команды серверу суо
        if (QConfig.cfg().getHttp() > 0) {
            QLog.l().logger().info("Run Jetty.");
            try {
                JettyRunner.start(QConfig.cfg().getHttp());
            } catch (NumberFormatException ex) {
                QLog.l().logger().error(
                    "Номер порта для Jetty в параметрах запуска не является числом. Формат параметра для порта 8081 '-http 8081'.",
                    ex);
            }
        }

        // Отчетный сервер, выступающий в роли вэбсервера, обрабатывающего запросы на выдачу отчетов
        WebServer.getInstance()
            .startWebServer(ServerProps.getInstance().getProps().getWebServerPort());
        loadPool();
        // запускаем движок индикации сообщения для кастомеров
        //MainBoard.getInstance().showBoard();
        // test ServerProps.getInstance().getProps().getZoneBoardServAddrList();
        if (!(Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getStartTime())
            .equals(
                Uses.FORMAT_HH_MM.format(ServerProps.getInstance().getProps().getFinishTime())))) {
            /**
             * Таймер, по которому будем Очистка всех услуг и рассылка спама с дневным отчетом.
             */
            ATalkingClock clearServices = new ATalkingClock(Uses.DELAY_CHECK_TO_LOCK, 0) {

                @Override
                public void run() {
                    // это обнуление :: This obnulenye
                    if (!QConfig.cfg().isRetain() && Uses.FORMAT_HH_MM
                        .format(new Date(new Date().getTime() + 10 * 60 * 1000)).equals(
                            Uses.FORMAT_HH_MM
                                .format(ServerProps.getInstance().getProps().getStartTime()))) {
                        QLog.l().logger().info("Очистка всех услуг.");
                        // почистим все услуги от трупов кастомеров с прошлого дня
                        QServer.clearAllQueue();
                    }

                    // это рассылка дневного отчета :: This is a daily report
                    if (("true".equalsIgnoreCase(Mailer.fetchConfig().getProperty("mailing")) || "1"
                        .equals(Mailer.fetchConfig().getProperty("mailing")))
                        && Uses.FORMAT_HH_MM.format(new Date(new Date().getTime() - 30 * 60 * 1000))
                        .equals(
                            Uses.FORMAT_HH_MM
                                .format(ServerProps.getInstance().getProps().getFinishTime()))) {
                        QLog.l().logger().info("Рассылка дневного отчета.");
                        // почистим все услуги от трупов кастомеров с прошлого дня
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
                                    QLog.l().logger().error("Какой-то облом с дневным отчетом", ex);
                                }
                                break;
                            }
                        }
                    }
                }
            };
            clearServices.start();
        }

        // подключения плагинов, которые стартуют в самом начале.
        // поддержка расширяемости плагинами
        for (final IStartServer event : ServiceLoader.load(IStartServer.class)) {
            QLog.l().logger().info("Вызов SPI расширения. Описание: " + event.getDescription());
            try {
                new Thread(() -> {
                    event.start();
                }).start();
            } catch (Throwable tr) {
                QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
            }
        }

        // привинтить сокет на локалхост, порт 3128
        final ServerSocket server;
        try {
            QLog.l().logger().info(
                "Сервер системы захватывает порт \"" + ServerProps.getInstance().getProps()
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
            "Сервер системы 'Очередь' запущен. DB name='" + as.getName() + "' url=" + as.getUrl());
        int pos = 0;
        boolean exit = false;
        // слушаем порт
        while (!globalExit && !exit) {
            // ждём нового подключения, после чего запускаем обработку клиента
            // в новый вычислительный поток и увеличиваем счётчик на единичку

            try {
                final QServer qServer = new QServer(server.accept());
                qServer.start();
                if (QConfig.cfg().isDebug()) {
                    System.out.println();
                }
            } catch (SocketTimeoutException e) {
                // ничего страшного, гасим исключение стобы дать возможность отработать входному/выходному потоку
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
                System.out.print(progres);
                System.out.write(13);// '\b' - возвращает корретку на одну позицию назад
            }

            // Попробуем считать нажатую клавишу
            // если нажади ENTER, то завершаем работу сервера
            // и затираем файл временного состояния Uses.TEMP_STATE_FILE
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
                    // набрали команду "exit" и нажали ENTER
                    QLog.l().logger().info("Завершение работы сервера.");
                    exit = true;
                }
            }
        }// while

        QLog.l().logger().debug("Закрываем серверный сокет.");
        server.close();
        QLog.l().logger().debug("Останов Jetty.");
        JettyRunner.stop();
        QLog.l().logger().debug("Останов отчетного вэбсервера.");
        WebServer.getInstance().stopWebServer();
        QLog.l().logger().debug("Выключение центрального табло.");
        MainBoard.getInstance().close();

        Thread.sleep(1500);
        QLog.l().logger().info("Сервер штатно завершил работу. Время работы: " + Uses
            .roundAs(((double) (System.currentTimeMillis() - start)) / 1000 / 60, 2) + " мин.");
        System.exit(0);
    }

    /**
     * Загрузка состояния пула услуг из временного json-файла
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
            QLog.l().logQUser().debug("Customer: " + cust + ", " + cust.getId());
            QLog.l().logQUser().debug("serviceId: " + cust.getService().getId());
            final QService service = QServiceTree.getInstance().getById(cust.getService().getId());
            if (service == null) {
                QLog.l().logQUser().debug("null... next");
                continue;
            }

            service.setCountPerDay(cust.getService().getCountPerDay());
            service.setDay(cust.getService().getDay());

            final QUser user = cust.getUser();
            cust.setService(service);

            if (user != null) {
                QLog.l().logQUser().debug("user not null");
                // сохраненный кастомер обрабатывался юзером с именем userId
                if (QUserList.getInstance().getById(user.getId()) == null) {
                    continue;
                }
                QUserList.getInstance().getById(user.getId()).setCustomer(cust);
                cust.setUser(QUserList.getInstance().getById(user.getId()));
            }

            QLog.l().logQUser().debug("setPriority");
            cust.setPriority(1);

            QLog.l().logQUser().debug("setState: " + cust.getStateIn());
            Integer state = cust.getStateIn();
            cust.setStateWithoutSave(state);

            QLog.l().logQUser().debug("Adding customer to serviceTree");
            if (cust.getStateIn() == 1 ||cust.getStateIn() == 2 || cust.getStateIn() == 3) {
                QLog.l().logQUser().debug("Adding customer to serviceTree");
                QServiceTree.getInstance().getById(cust.getService().getId()).addCustomer(cust);
            } else {
                QLog.l().logQUser().debug("Skip adding customer");
            }

        }
/*
        QLog.l().logQUser().debug("Refreshing postponed list");
        QPostponedList.getInstance().loadPostponedList(new LinkedList<QCustomer>());

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
        // почистим все услуги от трупов кастомеров
        QServiceTree.getInstance().getNodes().forEach((service) -> {
            service.clearNextNumber();
            service.freeCustomers();
        });
        QService.clearNextStNumber();

        QPostponedList.getInstance().clear();
        MainBoard.getInstance().clear();

        // Сотрем временные файлы
        QLog.l().logger().info("Очистка всех пользователей от привязанных кастомеров.");
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

            // из сокета клиента берём поток входящих данных
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
                // подождать пока хоть что-то приползет из сети, но не более 10 сек.
                // Wait until at least something crawls out of the network, but no more than 10 seconds.
                int i = 0;
                while (is.available() == 0 && i < 100) {
                    Thread.sleep(100);//бля
                    i++;
                }

                StringBuilder sb = new StringBuilder(new String(Uses.readInputStream(is)));
                while (is.available() != 0) {
                    sb = sb.append(new String(Uses.readInputStream(is)));
                    Thread.sleep(150);//бля
                }
                data = URLDecoder.decode(sb.toString(), "utf-8");
            } catch (IOException ex) {
                throw new ServerException("Ошибка при чтении из входного потока: " + ex);
            } catch (InterruptedException ex) {
                throw new ServerException("Проблема со сном: " + ex);
            } catch (IllegalArgumentException ex) {
                throw new ServerException("Ошибка декодирования сетевого сообщения: " + ex);
            }
            QLog.l().logger()
                .trace("Task:\n" + (data.length() > 200 ? (data.substring(0, 200) + "...") : data));

            /*
             Если по сетке поймали exit, то это значит что запустили останавливающий батник.
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
                // полученное задание передаем в пул
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
                    "Поздно пойманная ошибка при выполнении команды: " + Arrays
                        .toString(ex.getStackTrace()));
            } finally {
                GsonPool.getInstance().returnGson(gson);
            }

            // выводим данные:
            QLog.l().logger().trace(
                "Response:\n" + (answer.length() > 200 ? (answer.substring(0, 200) + "...")
                    : answer));
            try {
                // Передача данных ответа
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.print(URLEncoder.encode(answer, "utf-8"));
                writer.flush();
            } catch (IOException e) {
                throw new ServerException(
                    "Ошибка при записи в поток: " + Arrays.toString(e.getStackTrace()));
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
            throw new ServerException("Ошибка при выполнении задания.\n" + ex + err);
        } finally {
            // завершаем соединение
            try {
                //оборачиваем close, т.к. он сам может сгенерировать ошибку IOExeption. Просто выкинем Стек-трейс
                socket.close();
            } catch (IOException e) {
                QLog.l().logger().trace(e);
            }
            QLog.l().logger().trace("Response was finished");
        }
    }
}
