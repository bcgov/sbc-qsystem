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
package ru.apertum.qsystem.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import static org.apache.http.HttpHeaders.USER_AGENT;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jetty.http.HttpHeader;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.cmd.CmdParams;
import ru.apertum.qsystem.common.cmd.JsonRPC20;
import ru.apertum.qsystem.common.cmd.JsonRPC20Error;
import ru.apertum.qsystem.common.cmd.JsonRPC20OK;
import ru.apertum.qsystem.common.cmd.RpcBanList;
import ru.apertum.qsystem.common.cmd.RpcGetAdvanceCustomer;
import ru.apertum.qsystem.common.cmd.RpcGetAllServices;
import ru.apertum.qsystem.common.cmd.RpcGetAuthorizCustomer;
import ru.apertum.qsystem.common.cmd.RpcGetBool;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfDay;
import ru.apertum.qsystem.common.cmd.RpcGetGridOfWeek;
import ru.apertum.qsystem.common.cmd.RpcGetInfoTree;
import ru.apertum.qsystem.common.cmd.RpcGetInt;
import ru.apertum.qsystem.common.cmd.RpcGetPostponedPoolInfo;
import ru.apertum.qsystem.common.cmd.RpcGetProperties;
import ru.apertum.qsystem.common.cmd.RpcGetRespTree;
import ru.apertum.qsystem.common.cmd.RpcGetResultsList;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation;
import ru.apertum.qsystem.common.cmd.RpcGetServerState;
import ru.apertum.qsystem.common.cmd.RpcGetServerState.ServiceInfo;
import ru.apertum.qsystem.common.cmd.RpcGetServiceState;
import ru.apertum.qsystem.common.cmd.RpcGetServiceState.ServiceState;
import ru.apertum.qsystem.common.cmd.RpcGetSrt;
import ru.apertum.qsystem.common.cmd.RpcGetUsersList;
import ru.apertum.qsystem.common.cmd.RpcGetStandards;
import ru.apertum.qsystem.common.cmd.RpcGetTicketHistory;
import ru.apertum.qsystem.common.cmd.RpcGetTicketHistory.TicketHistory;
import ru.apertum.qsystem.common.cmd.RpcInviteCustomer;
import ru.apertum.qsystem.common.cmd.RpcStandInService;
import ru.apertum.qsystem.common.exceptions.ClientException;
import ru.apertum.qsystem.common.exceptions.QException;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.http.CommandHandler;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QAuthorizationCustomer;
import ru.apertum.qsystem.server.model.QProperty;
import ru.apertum.qsystem.server.model.QStandards;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.infosystem.QInfoItem;
import ru.apertum.qsystem.server.model.response.QRespItem;
import ru.apertum.qsystem.server.model.results.QResult;

/**
 * Contains static methods for sending and receiving jobs to the server. Any method returns an XML server response node.
 *
 * @author Evgeniy Egorov
 */
public class NetCommander {

    private static final JsonRPC20 JSON_RPC = new JsonRPC20();

    /**
     * The main work is to send and receive the result.
     *
     * @param netProperty Server connection settings
     * @param commandName
     * @param params
     * @return XML-ответ
     * @throws ru.apertum.qsystem.common.exceptions.QException
     */
    synchronized public static String send(INetProperty netProperty, String commandName, CmdParams params) throws QException {
        JSON_RPC.setMethod(commandName);
        JSON_RPC.setParams(params);
        return sendRpc(netProperty, JSON_RPC);
    }

    synchronized public static String sendRpc(INetProperty netProperty, JsonRPC20 jsonRpc) throws QException {
        final String message;
        Gson gson = GsonPool.getInstance().borrowGson();
        try {
            message = gson.toJson(jsonRpc);
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        final String data;
        try {
            if (QConfig.cfg().getHttpRequestType() != null && !(jsonRpc.getMethod().startsWith("#") || "empty".equalsIgnoreCase(jsonRpc.getMethod()))) {
                data = QConfig.cfg().getHttpRequestType() ? sendPost(netProperty, message, jsonRpc) : sendGet(netProperty, jsonRpc);
            } else {
                QLog.l().logger().trace("Task \"" + jsonRpc.getMethod() + "\" on " + netProperty.getAddress().getHostAddress() + ":" + netProperty.getPort() + "#\n" + message);
                final Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress(netProperty.getAddress(), netProperty.getPort()), 15000);
                } catch (IOException ex) {
                    Uses.closeSplash();
                    throw new QException(Locales.locMes("no_connect_to_server"), ex);
                }
                QLog.l().logger().trace("Socket was created.");
                final PrintWriter writer;
                final Scanner in;
                try {
                    writer = new PrintWriter(socket.getOutputStream());
                    writer.print(URLEncoder.encode(message, "utf-8"));
                    QLog.l().logger().trace("Sending...");
                    writer.flush();
                    QLog.l().logger().trace("Reading...");
                    StringBuilder sb = new StringBuilder();
                    in = new Scanner(socket.getInputStream());
                    while (in.hasNextLine()) {
                        sb = sb.append(in.nextLine()).append("\n");
                    }
                    data = URLDecoder.decode(sb.toString(), "utf-8");
                    writer.close();
                    in.close();
                } finally {
                    socket.close();
                }
                QLog.l().logger().trace("Response:\n" + data);
            }
        } catch (Exception ex) {
            throw new QException(Locales.locMes("no_response_from_server"), ex);
        }
        gson = GsonPool.getInstance().borrowGson();
        try {
            final JsonRPC20Error rpc = gson.fromJson(data, JsonRPC20Error.class);
            if (rpc == null) {
                throw new QException(Locales.locMes("error_on_server_no_get_response"));
            }
            if (rpc.getError() != null) {
                throw new QException(Locales.locMes("tack_failed") + " " + rpc.getError().getCode() + ":" + rpc.getError().getMessage());
            }
        } catch (JsonSyntaxException ex) {
            throw new QException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return data;
    }

    // HTTP POST request
    private static String sendPost(INetProperty netProperty, String outputData, JsonRPC20 jsonRpc) throws Exception {
        String url = QConfig.cfg().getWebServiceURL() + CommandHandler.CMD_URL_PATTERN;
        QLog.l().logger().trace("HTTP POST request \"" + jsonRpc.getMethod() + "\" on " + url + "\n" + outputData);
        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty(HttpHeader.CONTENT_TYPE.asString(), "text/json; charset=UTF-8");

        // Send post request
        con.setDoOutput(true);

        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF8"))) {
            out.append(outputData);
            out.flush();
        }

        if (con.getResponseCode() != 200) {
            QLog.l().logger().error("HTTP response code = " + con.getResponseCode());
            throw new QException(Locales.locMes("no_connect_to_server"));
        }

        final StringBuffer response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        //result
        final String res = response.toString();
        response.setLength(0);
        QLog.l().logger().trace("HTTP response:\n" + res);
        return res;
    }

    // HTTP GET request
    private static String sendGet(INetProperty netProperty, JsonRPC20 jsonRpc) throws Exception {
        final String p = jsonRpc.getParams() == null ? "" : jsonRpc.getParams().toString();
        String url = QConfig.cfg().getWebServiceURL() + CommandHandler.CMD_URL_PATTERN + "?"
                + CmdParams.CMD + "=" + URLEncoder.encode(jsonRpc.getMethod(), "utf-8") + "&"
                + p;
        QLog.l().logger().trace("HTTP GET request \"" + jsonRpc.getMethod() + "\" on " + url + "\n" + p);
        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        if (con.getResponseCode() != 200) {
            QLog.l().logger().error("HTTP response code = " + con.getResponseCode());
            throw new QException(Locales.locMes("no_connect_to_server"));
        }
        final StringBuffer response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        //result
        final String res = response.toString();
        response.setLength(0);
        QLog.l().logger().trace("HTTP response:\n" + res);
        return res;
    }

    /**
     * Получение возможных услуг.
     *
     * @param netProperty параметры соединения с сервером
     * @return XML-ответ
     */
    public static RpcGetAllServices.ServicesForWelcome getServices(INetProperty netProperty) {
        QLog.l().logger().info("Obtaining possible services.");
        // Load answer
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_GET_SERVICES, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        if (res == null) {
            return null;
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetAllServices rpc;
        try {
            rpc = gson.fromJson(res, RpcGetAllServices.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Queuing.
     *
     * @param netProperty netProperty Parameters of connection with the server.
     * @param serviceId Service in which we try to stand up.
     * @param password Password of the one who is trying to complete the task.
     * @param priority a priority.
     * @param inputData
     * @return Created a customizer.
     */
    public static QCustomer standInService(INetProperty netProperty, long serviceId, String password, int priority, String inputData) {
        QLog.l().logger().info("To get in line.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        params.password = password;
        params.priority = priority;
        params.textData = inputData;
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_STAND_IN, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcStandInService rpc;
        try {
            rpc = gson.fromJson(res, RpcStandInService.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Queuing.
     *
     * @param netProperty netProperty parameters for connecting to the server.
     * @param servicesId services we are trying to get into. It requires clarification what kind of 3D array it is. These are five lists. The first is freely sequential
     * Services. The other four are sequentially dependent services, i.e. While one does not end on the other does not go over. What is a list item. It is too
     * list. The first element is the same complex service (ID). And the rest are dependencies, i.e. If there are services not yet provided but designated, which in
     * Dependencies, then they must be provided.
     * @param password is the password of the one who is trying to complete the task.
     * @param priority is the priority.
     * @param inputData
     * @return Created the customizer.
     */
    public static QCustomer standInSetOfServices(INetProperty netProperty, LinkedList<LinkedList<LinkedList<Long>>> servicesId, String password, int priority, String inputData) {
        QLog.l().logger().info("To get in line in a complex.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.complexId = servicesId;
        params.password = password;
        params.priority = priority;
        params.textData = inputData;
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_STAND_COMPLEX, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcStandInService rpc;
        try {
            rpc = gson.fromJson(res, RpcStandInService.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Make the service temporarily inactive or unlock temporary inactivity
     *
     * @param netProperty netProperty Parameters of connection with the server.
     * @param serviceId Service we are trying to manage
     * @param reason
     */
    public static void changeTempAvailableService(INetProperty netProperty, long serviceId, String reason) {
        QLog.l().logger().info("Make the service temporarily inactive / active.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        params.textData = reason;
        try {
            send(netProperty, Uses.TASK_CHANGE_TEMP_AVAILABLE_SERVICE, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
    }

    /**
     * Find out how many people are worth to the service, etc.
     *
     * @param netProperty Parameters of connection with the server.
     * @param serviceId id Services about which we receive information
     * @return Number of precedents.
     * @throws QException
     */
    public static ServiceState aboutService(INetProperty netProperty, long serviceId) throws QException {
        QLog.l().logger().info("To get in line.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_ABOUT_SERVICE, params);
        } catch (QException ex) {// Output of exceptions
            throw new QException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetServiceState rpc;
        try {
            rpc = gson.fromJson(res, RpcGetServiceState.class);
        } catch (JsonSyntaxException ex) {
            throw new QException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получить всю очередь к услуге и т.д.
     *
     * @param netProperty параметры соединения с сервером.
     * @param serviceId id услуги о которой получаем информацию
     * @return количество предшествующих.
     * @throws QException
     */
    public static ServiceState getServiceConsistency(INetProperty netProperty, long serviceId) throws QException {
        QLog.l().logger().info("To get in line.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_GET_SERVICE_CONSISANCY, params);
        } catch (QException ex) {// Output of exceptions
            throw new QException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetServiceState rpc;
        try {
            rpc = gson.fromJson(res, RpcGetServiceState.class);
        } catch (JsonSyntaxException ex) {
            throw new QException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Узнать можно ли вставать в услугу с такими введенными данными
     *
     * @param netProperty параметры соединения с сервером.
     * @param serviceId id услуги о которой получаем информацию
     * @param inputData введенная ботва
     * @return 1 - превышен, 0 - можно встать. 2 - забанен
     * @throws QException
     */
    public static int aboutServicePersonLimitOver(INetProperty netProperty, long serviceId, String inputData) throws QException {
        QLog.l().logger().info("To find out whether it is possible to enter the service with such entered data.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        params.textData = inputData;
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_ABOUT_SERVICE_PERSON_LIMIT, params);
        } catch (QException ex) {// Output of exceptions
            throw new QException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetInt rpc;
        try {
            rpc = gson.fromJson(res, RpcGetInt.class);
        } catch (JsonSyntaxException ex) {
            throw new QException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение описания всех юзеров для выбора себя.
     *
     * @param netProperty параметры соединения с сервером
     * @return XML-ответ все юзеры системы
     */
    public static LinkedList<QUser> getUsers(INetProperty netProperty) {
        QLog.l().logger().info("Получение описания всех юзеров для выбора себя.");
        // Load answer
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_GET_USERS, null);
        } catch (QException e) {// Output of exceptions
            Uses.closeSplash();
            throw new ClientException(Locales.locMes("command_error2"), e);
        } finally {
            if (res == null || res.isEmpty()) {
                System.exit(1);
            }
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetUsersList rpc;
        try {
            rpc = gson.fromJson(res, RpcGetUsersList.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение описания очередей для юзера.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId id пользователя для которого идет опрос
     * @return список обрабатываемых услуг с количеством кастомеров в них стоящих и обрабатываемый кастомер если был
     * @throws ru.apertum.qsystem.common.exceptions.QException
     */
    public static RpcGetSelfSituation.SelfSituation getSelfServices(INetProperty netProperty, long userId) throws QException {
        return getSelfServices(netProperty, userId, null);
    }

    /**
     * Получение описания очередей для юзера.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId id пользователя для которого идет опрос
     * @param forced получить ситуацию даже если она не обновлялась за последнее время
     * @return список обрабатываемых услуг с количеством кастомеров в них стоящих и обрабатываемый кастомер если был
     * @throws ru.apertum.qsystem.common.exceptions.QException
     */
    public static RpcGetSelfSituation.SelfSituation getSelfServices(INetProperty netProperty, long userId, Boolean forced) throws QException {
        QLog.l().logger().info("Получение описания очередей для юзера.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.textData = QConfig.cfg().getPointN();
        params.requestBack = forced;
        String res;
        try {
            res = send(netProperty, Uses.TASK_GET_SELF_SERVICES, params);
        } catch (QException e) {// Output of exceptions
            Uses.closeSplash();
            throw new QException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetSelfSituation rpc;
        try {
            rpc = gson.fromJson(res, RpcGetSelfSituation.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Подпорочка, нужна для того чтоб настроить маркеровку окна приема на клиенте и переслать на сервер, чтоб заменить значение из БД. Это значение, если есть,
     * передается в строке параметров при старке клиентской проги и засовывается сюда, вот такая мегаинициализация.
     */
    //static public String pointId = null; -> QConfig.cfg().getPointN();
    /**
     * Проверка на то что такой юзер уже залогинен в систему
     *
     * @param netProperty параметры соединения с сервером
     * @param userId id пользователя для которого идет опрос
     * @return false - запрешено, true - новый
     */
    public static boolean getSelfServicesCheck(INetProperty netProperty, long userId) {
        QLog.l().logger().info("Получение описания очередей для юзера.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.textData = QConfig.cfg().getPointN();
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_SELF_SERVICES_CHECK, params);
        } catch (QException e) {// Output of exceptions
            Uses.closeSplash();
            throw new ServerException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetBool rpc;
        try {
            rpc = gson.fromJson(res, RpcGetBool.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение слeдующего юзера из очередей, обрабатываемых юзером.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     * @return ответ-кастомер следующий по очереди
     */
    public static QCustomer inviteNextCustomer(INetProperty netProperty, long userId) {
        QLog.l().logger().info("Получение следующего юзера из очередей, обрабатываемых юзером.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_INVITE_NEXT_CUSTOMER, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcInviteCustomer rpc;
        try {
            rpc = gson.fromJson(res, RpcInviteCustomer.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Удаление вызванного юзером кастомера.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     * @param customerId переключиться на этого при параллельном приеме, NULL если переключаться не надо
     */
    public static void killNextCustomer(INetProperty netProperty, long userId, Long customerId) {
        QLog.l().logger().info("Удаление вызванного юзером кастомера.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.customerId = customerId;
        try {
            send(netProperty, Uses.TASK_KILL_NEXT_CUSTOMER, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
    }

    /**
     * Перемещение вызванного юзером кастомера в пул отложенных.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     * @param customerId переключиться на этого при параллельном приеме, NULL если переключаться не надо
     * @param status просто строка. берется из возможных состояний завершения работы
     * @param postponedPeriod
     * @param isMine
     */
    public static void customerToPostpone(INetProperty netProperty, long userId, Long customerId, String status, int postponedPeriod, boolean isMine) {
        QLog.l().logger().info("Перемещение вызванного юзером кастомера в пул отложенных.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.customerId = customerId;
        params.textData = status;
        params.postponedPeriod = postponedPeriod;
        params.isMine = isMine;
        try {
            send(netProperty, Uses.TASK_CUSTOMER_TO_POSTPON, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
    }

    /**
     * Изменение отложенному кастомеру статеса
     *
     * @param netProperty параметры соединения с сервером
     * @param postponCustomerId меняем этому кастомеру
     * @param status просто строка. берется из возможных состояний завершения работы
     */
    public static void postponeCustomerChangeStatus(INetProperty netProperty, long postponCustomerId, String status) {
        QLog.l().logger().info("Перемещение вызванного юзером кастомера в пул отложенных.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.customerId = postponCustomerId;
        params.textData = status;
        try {
            send(netProperty, Uses.TASK_POSTPON_CHANGE_STATUS, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
    }

    /**
     * Начать работу с вызванным кастомером.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     */
    public static void getStartCustomer(INetProperty netProperty, long userId) {
        QLog.l().logger().info("Начать работу с вызванным кастомером.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        try {
            send(netProperty, Uses.TASK_START_CUSTOMER, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
    }

    /**
     * Закончить работу с вызванным кастомером.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     * @param customerId переключиться на этого при параллельном приеме, NULL если переключаться не надо
     * @param resultId
     * @param comments это если закончили работать с редиректенным и его нужно вернуть
     * @return
     */
    public static QCustomer getFinishCustomer(INetProperty netProperty, long userId, Long customerId, Long resultId, String comments) {
        QLog.l().logger().info("Закончить работу с вызванным кастомером.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.customerId = customerId;
        params.resultId = resultId;
        params.textData = comments;
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_FINISH_CUSTOMER, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcStandInService rpc;
        try {
            rpc = gson.fromJson(res, RpcStandInService.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Переадресовать клиента в другую очередь.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     * @param customerId переключиться на этого при параллельном приеме, NULL если переключаться не надо
     * @param serviceId
     * @param requestBack
     * @param resultId
     * @param comments комментарии при редиректе
     */
    public static void redirectCustomer(INetProperty netProperty, long userId, Long customerId, long serviceId, boolean requestBack, String comments, Long resultId) {
        QLog.l().logger().info("Переадресовать клиента в другую очередь.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.customerId = customerId;
        params.serviceId = serviceId;
        params.requestBack = requestBack;
        params.resultId = resultId;
        params.textData = comments;
        try {
            send(netProperty, Uses.TASK_REDIRECT_CUSTOMER, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
    }

    /**
     * Подтверждение живости клиентом для сервера.
     *
     * @param netProperty параметры соединения с сервером
     * @param userId
     * @return XML-ответ
     * @deprecated заборонено. гы-гы. теперь жить будем по новому, даздравствует Новороссия!
     */
    public static Element setLive(INetProperty netProperty, long userId) {
        QLog.l().logger().info("Ответим что живы и здоровы.");
        final CmdParams params = new CmdParams();
        params.userId = userId;
        /*
         try {
         // Load answer
         return send(netProperty, Uses.TASK_I_AM_LIVE, params);
         } catch (IOException e) {// Output of exceptions
         throw new ClientException(Locales.locMes("command_error2"), e);
         } catch (DocumentException e) {
         throw new ClientException(Locales.locMes("bad_response") + "\n" + e.toString());
         }
         *
         */

        return null;
    }

    /**
     * Получение описания состояния сервера.
     *
     * @param netProperty параметры соединения с сервером
     * @return XML-ответ
     */
    public static LinkedList<ServiceInfo> getServerState(INetProperty netProperty) {
        QLog.l().logger().info("Получение описания состояния сервера.");
        // Load answer
        String res = null;
        try {
            res = send(netProperty, Uses.TASK_SERVER_STATE, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetServerState rpc;
        try {
            rpc = gson.fromJson(res, RpcGetServerState.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение описания состояния пункта регистрации.
     *
     * @param netProperty параметры соединения с пунктом регистрации
     * @param message что-то вроде названия команды для пункта регистрации
     * @param dropTicketsCounter сбросить счетчик выданных талонов или нет
     * @return некий ответ от пункта регистрации, вроде прям как строка для вывода
     */
    public static String getWelcomeState(INetProperty netProperty, String message, boolean dropTicketsCounter) {
        QLog.l().logger().info("Получение описания состояния пункта регистрации.");
        // Load answer
        String res = null;
        final CmdParams params = new CmdParams();
        params.dropTicketsCounter = dropTicketsCounter;
        try {
            res = send(netProperty, message, params);
        } catch (QException e) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + e.toString());
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetSrt rpc;
        try {
            rpc = gson.fromJson(res, RpcGetSrt.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Добавить сервис в список обслуживаемых юзером использую параметры. Используется при добавлении на горячую.
     *
     * @param netProperty параметры соединения с пунктом регистрации
     * @param serviceId
     * @param userId
     * @param coeff
     * @return содержить строковое сообщение о результате.
     */
    public static String setServiseFire(INetProperty netProperty, long serviceId, long userId, int coeff) {
        QLog.l().logger().info("Привязка услуги пользователю на горячую.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.serviceId = serviceId;
        params.coeff = coeff;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_SET_SERVICE_FIRE, params);
        } catch (QException e) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + e.toString());
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetSrt rpc;
        try {
            rpc = gson.fromJson(res, RpcGetSrt.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Удалить сервис из списока обслуживаемых юзером использую параметры. Используется при добавлении на горячую.
     *
     * @param netProperty параметры соединения с пунктом регистрации
     * @param serviceId
     * @param userId
     * @return содержить строковое сообщение о результате.
     */
    public static String deleteServiseFire(INetProperty netProperty, long serviceId, long userId) {
        QLog.l().logger().info("Удаление услуги пользователю на горячую.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.serviceId = serviceId;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_DELETE_SERVICE_FIRE, params);
        } catch (QException e) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + e.toString());
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetSrt rpc;
        try {
            rpc = gson.fromJson(res, RpcGetSrt.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение конфигурации главного табло - ЖК или плазмы. Это XML-файл лежащий в папку приложения mainboard.xml
     *
     * @param netProperty параметры соединения с сервером
     * @return корень XML-файла mainboard.xml
     * @throws DocumentException принятый текст может не преобразоваться в XML
     */
    public static Element getBoardConfig(INetProperty netProperty) throws DocumentException {
        QLog.l().logger().info("Получение конфигурации главного табло - ЖК или плазмы.");
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_BOARD_CONFIG, null);
        } catch (QException e) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + e.toString());
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetSrt rpc;
        try {
            rpc = gson.fromJson(res, RpcGetSrt.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return DocumentHelper.parseText(rpc.getResult()).getRootElement();
    }

    /**
     * Сохранение конфигурации главного табло - ЖК или плазмы. Это XML-файл лежащий в папку приложения mainboard.xml
     *
     * @param netProperty параметры соединения с сервером
     * @param boardConfig
     */
    public static void saveBoardConfig(INetProperty netProperty, Element boardConfig) {
        QLog.l().logger().info("Сохранение конфигурации главного табло - ЖК или плазмы.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.textData = boardConfig.asXML();
        try {
            send(netProperty, Uses.TASK_SAVE_BOARD_CONFIG, params);
        } catch (QException e) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + e.toString());
        }
    }

    /**
     * Получение дневной таблици с данными для предварительной записи включающими информацию по занятым временам и свободным.
     *
     * @param netProperty netProperty параметры соединения с сервером.
     * @param serviceId услуга, в которую пытаемся встать.
     * @param date день недели за который нужны данные.
     * @param advancedCustomer ID авторизованного кастомера
     * @return класс с параметрами и списком времен
     */
    public static RpcGetGridOfDay.GridDayAndParams getPreGridOfDay(INetProperty netProperty, long serviceId, Date date, long advancedCustomer) {
        QLog.l().logger().info("Получить таблицу дня");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        params.date = date.getTime();
        params.customerId = advancedCustomer;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_GRID_OF_DAY, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetGridOfDay rpc;
        try {
            rpc = gson.fromJson(res, RpcGetGridOfDay.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение недельной таблици с данными для предварительной записи.
     *
     * @param netProperty netProperty параметры соединения с сервером.
     * @param serviceId услуга, в которую пытаемся встать.
     * @param date первый день недели за которую нужны данные.
     * @param advancedCustomer ID авторизованного кастомера
     * @return класс с параметрами и списком времен
     */
    public static RpcGetGridOfWeek.GridAndParams getGridOfWeek(INetProperty netProperty, long serviceId, Date date, long advancedCustomer) {
        QLog.l().logger().info("Получить таблицу");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        params.date = date.getTime();
        params.customerId = advancedCustomer;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_GRID_OF_WEEK, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetGridOfWeek rpc;
        try {
            rpc = gson.fromJson(res, RpcGetGridOfWeek.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Предварительная запись в очередь.
     *
     * @param netProperty netProperty параметры соединения с сервером.
     * @param serviceId услуга, в которую пытаемся встать.
     * @param date
     * @param advancedCustomer ID авторизованного кастомер. -1 если нет авторизации
     * @param inputData введеные по требованию услуги данные клиентом, может быть null если не вводили
     * @param comments комментарий по предварительно ставящемуся клиенту если ставят из админки или приемной
     * @return предварительный кастомер
     */
    public static QAdvanceCustomer standInServiceAdvance(INetProperty netProperty, long serviceId, Date date, long advancedCustomer, String inputData, String comments) {
        QLog.l().logger().info("Записать предварительно в очередь.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.serviceId = serviceId;
        params.date = date.getTime();
        params.customerId = advancedCustomer;
        params.textData = inputData;
        params.comments = comments;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_ADVANCE_STAND_IN, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetAdvanceCustomer rpc;
        try {
            rpc = gson.fromJson(res, RpcGetAdvanceCustomer.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Предварительная запись в очередь.
     *
     * @param netProperty netProperty параметры соединения с сервером.
     * @param advanceID идентификатор предварительно записанного.
     * @return XML-ответ.
     */
    public static RpcStandInService standAndCheckAdvance(INetProperty netProperty, Long advanceID) {
        QLog.l().logger().info("Постановка предварительно записанных в очередь.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.customerId = advanceID;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_ADVANCE_CHECK_AND_STAND, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcStandInService rpc;
        try {
            rpc = gson.fromJson(res, RpcStandInService.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc;
    }

    /**
     * Удаление предварительной записи в очередь.
     *
     * @param netProperty netProperty параметры соединения с сервером.
     * @param advanceID идентификатор предварительно записанного.
     * @return XML-ответ.
     */
    public static JsonRPC20OK removeAdvancedCustomer(INetProperty netProperty, Long advanceID) {
        QLog.l().logger().info("Удаление предварительно записанных в очередь.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.customerId = advanceID;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_REMOVE_ADVANCE_CUSTOMER, params);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final JsonRPC20OK rpc;
        try {
            rpc = gson.fromJson(res, JsonRPC20OK.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc;
    }

    /**
     * Рестарт сервера.
     *
     * @param netProperty параметры соединения с сервером
     */
    public static void restartServer(INetProperty netProperty) {
        QLog.l().logger().info("Команда на рестарт сервера.");
        try {
            send(netProperty, Uses.TASK_RESTART, null);
        } catch (QException e) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error2"), e);
        }
    }

    /**
     * Получение Дерева отзывов
     *
     * @param netProperty параметры соединения с сервером
     * @return XML-ответ
     */
    public static QRespItem getResporseList(INetProperty netProperty) {
        QLog.l().logger().info("Команда на получение дерева отзывов.");
        String res = null;
        try {
            // Load answer
            res = send(netProperty, Uses.TASK_GET_RESPONSE_LIST, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        if (res == null) {
            return null;
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetRespTree rpc;
        try {
            rpc = gson.fromJson(res, RpcGetRespTree.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Оставить отзыв.
     *
     * @param netProperty параметры соединения с сервером.
     * @param serviceID услуга, может быть null
     * @param userID оператор, может быть null
     * @param customerID
     * @param clientData номер талона, не null
     * @param resp выбранн отзыв
     */
    public static void setResponseAnswer(INetProperty netProperty, QRespItem resp, Long userID, Long serviceID, Long customerID, String clientData) {
        QLog.l().logger().info("Отправка выбранного отзыва.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.responseId = resp.getId();
        params.serviceId = serviceID;
        params.userId = userID;
        params.customerId = customerID;
        params.textData = clientData;
        params.comments = resp.data;
        try {
            send(netProperty, Uses.TASK_SET_RESPONSE_ANSWER, params);
        } catch (QException ex) {// Output of exceptions
            throw new ServerException(Locales.locMes("command_error"), ex);
        }
    }

    /**
     * Получение информационного дерева
     *
     * @param netProperty параметры соединения с сервером
     * @return XML-ответ
     */
    public static QInfoItem getInfoTree(INetProperty netProperty) {
        QLog.l().logger().info("Команда на получение информационного дерева.");
        String res = null;
        try {
            // Load answer
            res = send(netProperty, Uses.TASK_GET_INFO_TREE, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        if (res == null) {
            return null;
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetInfoTree rpc;
        try {
            rpc = gson.fromJson(res, RpcGetInfoTree.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение описания залогинившегося юзера.
     *
     * @param netProperty параметры соединения с сервером
     * @param id
     * @return XML-ответ
     */
    public static QAuthorizationCustomer getClientAuthorization(INetProperty netProperty, String id) {
        QLog.l().logger().info("Получение описания авторизованного пользователя.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.clientAuthId = id;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_CLIENT_AUTHORIZATION, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetAuthorizCustomer rpc;
        try {
            rpc = gson.fromJson(res, RpcGetAuthorizCustomer.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получение списка возможных результатов работы с клиентом
     *
     * @param netProperty параметры соединения с сервером
     * @return свисок возможных завершений работы
     */
    public static LinkedList<QResult> getResultsList(INetProperty netProperty) {
        QLog.l().logger().info("Команда на получение списка возможных результатов работы с клиентом.");
        final String res;
        try {
            // Load answer RpcGetResultsList
            res = send(netProperty, Uses.TASK_GET_RESULTS_LIST, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetResultsList rpc;
        try {
            rpc = gson.fromJson(res, RpcGetResultsList.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Изменение приоритета кастомеру
     *
     * @param netProperty параметры соединения с сервером
     * @param prioritet
     * @param customer
     * @return Текстовый ответ о результате
     */
    public static String setCustomerPriority(INetProperty netProperty, int prioritet, String customer) {
        QLog.l().logger().info("Команда на повышение приоритета кастомеру.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.priority = prioritet;
        params.clientAuthId = customer;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_SET_CUSTOMER_PRIORITY, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetSrt rpc;
        try {
            rpc = gson.fromJson(res, RpcGetSrt.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Пробить номер клиента. Стоит в очереди или отложен или вообще не найден.
     *
     * @param netProperty параметры соединения с сервером
     * @param customerNumber
     * @return Текстовый ответ о результате
     */
    public static TicketHistory checkCustomerNumber(INetProperty netProperty, String customerNumber) {
        QLog.l().logger().info("Команда проверки номера кастомера.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.clientAuthId = customerNumber;
        final String res;
        try {
            res = send(netProperty, Uses.TASK_CHECK_CUSTOMER_NUMBER, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetTicketHistory rpc;
        try {
            rpc = gson.fromJson(res, RpcGetTicketHistory.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Get a list of deferred customers
     *
     * @param netProperty
     * @return List of deferred customers
     */
    public static LinkedList<QCustomer> getPostponedPoolInfo(INetProperty netProperty) {
        QLog.l().logger().info("Team to update the pool of pending.");
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_POSTPONED_POOL, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetPostponedPoolInfo rpc;
        try {
            rpc = gson.fromJson(res, RpcGetPostponedPoolInfo.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получить список забаненных введенных данных
     *
     * @param netProperty
     * @return список отложенных кастомеров
     */
    public static LinkedList<String> getBanedList(INetProperty netProperty) {
        QLog.l().logger().info("Команда получение списка забаненных.");
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_BAN_LIST, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcBanList rpc;
        try {
            rpc = gson.fromJson(res, RpcBanList.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getBanList();
    }

    /**
     * Вызов отложенного кастомера
     *
     * @param netProperty
     * @param userId id юзера который вызывает
     * @param id это ID кастомера которого вызываем из пула отложенных, оно есть т.к. с качстомером давно работаем
     */
    public static void invitePostponeCustomer(INetProperty netProperty, long userId, Long id) {
        QLog.l().logger().info("Команда на вызов кастомера из пула отложенных.");
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.customerId = id;
        // Load answer
        try {
            send(netProperty, Uses.TASK_INVITE_POSTPONED, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
    }

    /**
     * Рестарт главного табло
     *
     * @param serverNetProperty
     */
    public static void restartMainTablo(INetProperty serverNetProperty) {
        QLog.l().logger().info("Команда на рестарт главного табло.");
        // Load answer
        try {
            send(serverNetProperty, Uses.TASK_RESTART_MAIN_TABLO, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
    }

    /**
     * Изменение приоритетов услуг оператором
     *
     * @param netProperty
     * @param userId id юзера который вызывает
     * @param smartData
     */
    public static void changeFlexPriority(INetProperty netProperty, long userId, String smartData) {
        QLog.l().logger().info("Изменение приоритетов услуг оператором.");
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.textData = smartData;
        // Load answer
        try {
            send(netProperty, Uses.TASK_CHANGE_FLEX_PRIORITY, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
    }

    /**
     * Изменение бегущей строки на табло из админской проги
     *
     * @param netProperty параметры соединения с сервером
     * @param text новая строка
     * @param nameSection
     */
    public static void setRunningText(INetProperty netProperty, String text, String nameSection) {
        QLog.l().logger().info("Получение описания авторизованного пользователя.");
        // Load answer
        final CmdParams params = new CmdParams();
        params.textData = text;
        params.infoItemName = nameSection;
        try {
            send(netProperty, Uses.TASK_CHANGE_RUNNING_TEXT_ON_BOARD, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
    }

    /**
     * Получить норрмативы
     *
     * @param netProperty
     * @return класс нормативов
     */
    public static QStandards getStandards(INetProperty netProperty) {
        QLog.l().logger().info("Команда получение нормативов.");
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_STANDARDS, null);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetStandards rpc;
        try {
            rpc = gson.fromJson(res, RpcGetStandards.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return (QStandards) rpc.getResult();
    }

    /**
     * Изменение приоритетов услуг оператором
     *
     * @param netProperty
     * @param userId id юзера который вызывает
     * @param lock
     * @return
     */
    public static boolean setBussy(INetProperty netProperty, long userId, boolean lock) {
        QLog.l().logger().info("Изменение приоритетов услуг оператором.");
        final CmdParams params = new CmdParams();
        params.userId = userId;
        params.requestBack = lock;
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_SET_BUSSY, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetBool rpc;
        try {
            rpc = gson.fromJson(res, RpcGetBool.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Получить параметры из ДБ из сервера
     *
     * @param netProperty
     * @return мапа с секциями
     */
    public static LinkedHashMap<String, ServerProps.Section> getProperties(INetProperty netProperty) {
        QLog.l().logger().info("Получить параметры.");
        final CmdParams params = new CmdParams();
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_GET_PROPERTIES, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetProperties rpc;
        try {
            rpc = gson.fromJson(res, RpcGetProperties.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Изменить и сохранить параметеры в ДБ на сервере
     *
     * @param netProperty
     * @param properties
     * @return Список свежих свойств
     */
    public static LinkedHashMap<String, ServerProps.Section> saveProperties(INetProperty netProperty, List<QProperty> properties) {
        QLog.l().logger().info("Изменить и сохранить параметеры в ДБ на сервере.");
        final CmdParams params = new CmdParams();
        params.properties = properties;
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_INIT_PROPERTIES, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetProperties rpc;
        try {
            rpc = gson.fromJson(res, RpcGetProperties.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }

    /**
     * Если таких параметров нет, то создать их в ДБ на сервере
     *
     * @param netProperty
     * @param properties
     * @return Список свежих свойств
     */
    public static LinkedHashMap<String, ServerProps.Section> initProperties(INetProperty netProperty, List<QProperty> properties) {
        QLog.l().logger().info("Если таких параметров нет, то создать их в ДБ на сервере.");
        final CmdParams params = new CmdParams();
        params.properties = properties;
        // Load answer
        final String res;
        try {
            res = send(netProperty, Uses.TASK_SAVE_PROPERTIES, params);
        } catch (QException ex) {// Output of exceptions
            throw new ClientException(Locales.locMes("command_error"), ex);
        }
        final Gson gson = GsonPool.getInstance().borrowGson();
        final RpcGetProperties rpc;
        try {
            rpc = gson.fromJson(res, RpcGetProperties.class);
        } catch (JsonSyntaxException ex) {
            throw new ClientException(Locales.locMes("bad_response") + "\n" + ex.toString());
        } finally {
            GsonPool.getInstance().returnGson(gson);
        }
        return rpc.getResult();
    }
}
