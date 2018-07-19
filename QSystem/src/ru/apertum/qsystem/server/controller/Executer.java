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
package ru.apertum.qsystem.server.controller;

import static ru.apertum.qsystem.common.cmd.JsonRPC20Error.ErrorRPC.ADVANCED_NOT_FOUND;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QConfig;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.SoundPlayer;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.AJsonRPC20;
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
import ru.apertum.qsystem.common.cmd.RpcGetGridOfWeek.GridAndParams;
import ru.apertum.qsystem.common.cmd.RpcGetInfoTree;
import ru.apertum.qsystem.common.cmd.RpcGetInt;
import ru.apertum.qsystem.common.cmd.RpcGetPostponedPoolInfo;
import ru.apertum.qsystem.common.cmd.RpcGetProperties;
import ru.apertum.qsystem.common.cmd.RpcGetRespTree;
import ru.apertum.qsystem.common.cmd.RpcGetResultsList;
import ru.apertum.qsystem.common.cmd.RpcGetSelfSituation;
import ru.apertum.qsystem.common.cmd.RpcGetServerState;
import ru.apertum.qsystem.common.cmd.RpcGetServiceState;
import ru.apertum.qsystem.common.cmd.RpcGetSrt;
import ru.apertum.qsystem.common.cmd.RpcGetStandards;
import ru.apertum.qsystem.common.cmd.RpcGetTicketHistory;
import ru.apertum.qsystem.common.cmd.RpcGetUsersList;
import ru.apertum.qsystem.common.cmd.RpcInviteCustomer;
import ru.apertum.qsystem.common.cmd.RpcStandInService;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.extra.ISelectNextService;
import ru.apertum.qsystem.extra.ITask;
import ru.apertum.qsystem.server.MainBoard;
import ru.apertum.qsystem.server.QServer;
import ru.apertum.qsystem.server.QSessions;
import ru.apertum.qsystem.server.ServerProps;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.QAdvanceCustomer;
import ru.apertum.qsystem.server.model.QAuthorizationCustomer;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.calendar.QCalendarList;
import ru.apertum.qsystem.server.model.infosystem.QInfoTree;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;
import ru.apertum.qsystem.server.model.response.QRespEvent;
import ru.apertum.qsystem.server.model.response.QResponseTree;
import ru.apertum.qsystem.server.model.results.QResult;
import ru.apertum.qsystem.server.model.results.QResultList;
import ru.apertum.qsystem.server.model.schedule.QSchedule;

//  CM:  For config info.
import org.apache.commons.configuration2.FileBasedConfiguration;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Iterator;

//  CM:  For emailing
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;

//  CM:  For slack messages.
import ru.apertum.qsystem.server.controller.SlackApi;
import ru.apertum.qsystem.server.controller.SlackException;
import ru.apertum.qsystem.server.controller.SlackMessage;

//  CM:  Imports for MySQL stored procedure call using JDBC
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//  CM:  For tracking.
import java.sql.Timestamp;

//  CM:  For Snowplow
/******************************************************************************
 * Proof of concept code for CFMS Instrumentation project
 * NOTE: There is a bug right now that doesn't close the session correctly
 *   As a result the program may seem to hang when running
 *   If it logs "SimpleEmitter successfully sent 1 events: code: 200", then 
 *      it was successful, even if it doesn't close out
 ******************************************************************************/

import com.snowplowanalytics.snowplow.tracker.DevicePlatform;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.SimpleEmitter;
import com.snowplowanalytics.snowplow.tracker.emitter.BatchEmitter;
import com.snowplowanalytics.snowplow.tracker.emitter.Emitter;
import com.snowplowanalytics.snowplow.tracker.emitter.RequestCallback;
import com.snowplowanalytics.snowplow.tracker.events.PageView;
import com.snowplowanalytics.snowplow.tracker.events.Unstructured;
import com.snowplowanalytics.snowplow.tracker.payload.SelfDescribingJson;
import com.snowplowanalytics.snowplow.tracker.http.HttpClientAdapter;
import com.snowplowanalytics.snowplow.tracker.http.OkHttpClientAdapter;
import com.snowplowanalytics.snowplow.tracker.payload.TrackerPayload;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.ArrayList;
import java.util.Map;

/**
 * ??? ????????. ??? ???????? - ??????? ????????? ?????????? ?????????. ? ??????? ??????????
 * ????????? ????????, ???????? ??? ???????? ?????? ?????. ??? ???????? XML-??????? ?? ????,
 * ?????????? ????????? ????????. ????????? ???????? ?? ??????????? ????. ????????? ???????,
 * ?????????? ?????????? ????????. ???????? ??? singleton.
 *
 * @author Evgeniy Egorov
 */
public final class Executer {

    /**
     * ???? ?????????? ??? ??????????? ? ?????????? Locking key for manipulating the clocks
     */
    public static final Lock CLIENT_TASK_LOCK = new ReentrantLock();
    /**
     * ???? ?????????? ??? ??????????? ? ???????????. ????? ?? ??????? ??? ?????????????. ?? ?????
     * ???? ?????????? ??????? ?? ???????????? The lock key for manipulating pending. When the timer
     * is pulled out. Do not need to be baffled by a call from the user
     */
    public static final Lock POSTPONED_TASK_LOCK = new ReentrantLock();
    private static final HashMap<String, Date> KILLED_CUSTOMERS = new HashMap<>();
    /**
     * ??? ???????? ???? ????????? ?????????? ????? ???????? ???? ?????????? ???????? ?? ??????
     * ???????? ??? ????????????
     */
    public final HashMap<Long, Long> hashState = new HashMap<>();
    // ??????, ???????? ?? ?? ??????
    private final HashMap<String, ITask> tasks = new HashMap<>();
    /**
     * ?????? ????????? ? ???????. We put the customizer in the queue.
     */
    final AddCustomerTask addCustomerTask = new AddCustomerTask(Uses.TASK_STAND_IN);
    /**
     * ?????? ????????? ? ??????? ? ?????????? ???????. :: We put the customizer in the queue to
     * several services.
     *
     * @return
     */

    //  Info needed for JDBC calls.
    private static String MyDB = System.getenv("MYSQL_DATABASE");
    private static String MyUser = System.getenv("MYSQL_USER");
    private static String MyPw = System.getenv("MYSQL_PASSWORD");
    private static String URL = "jdbc:mysql://" + System.getenv("MYSQL_SERVICE") + "/" + MyDB
            + "?noAccessToProcedureBodies=true";
    private static String SqlInsertStatement =
            "INSERT INTO trackactions (time_now, button_clicked, start_finish, office_id, " +
                    "user_id, client_id, ticket, service_id, state_in, user_quick, " +
                    "client_quick, channel, quantity, priority, srv_user_id, srv_quick) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    //  CM:  ==>  Start of Snowplow variables
    private static final boolean snowplowCall = getEnvBoolean("QSYSTEM_SNOWPLOW_CALL");
    private static final String collectorEndpoint = getEnvString("QSYSTEM_SNOWPLOW_ENDPOINT",
            "https://spm.gov.bc.ca");

    //  For Slack calls.
    private static String slackHookUrl = getEnvString("SLACK_HOOK_URL", "");

    //private static final String collectorEndpoint = "https://ca-bc-gov-main.collector.snplow.net";
    //private static final String collectorEndpoint = "https://spm.gov.bc.ca";

    //========================================
    // Set up the namespace and appID
    private static final String namespace = "CFMS_poc";
    private static final String appID = "demo";
    //========================================
    // Set whether or not to send events base64 encoded. For now, we send nonencoded to ease debugging
    private static final Boolean baseSetting = false;

    //  CM:  Static variables needed to send info to Snowplow.  Hopefully, can reuse.
    private static final HttpClientAdapter okHttpClientAdapter = getClient(collectorEndpoint);
    private static final Emitter emitter = SimpleEmitter.builder()
            .httpClientAdapter(okHttpClientAdapter) // Required
            .threadCount(20) // Default is 50
            .build();
    private static final Tracker tracker = new Tracker.TrackerBuilder(emitter, namespace, appID)
            .base64(baseSetting)
            .platform(DevicePlatform.Desktop)
            .build();

    //  CM:  ==>  End of Snowplow variables.

    //  CM:  ==>  Start of Snowplow routine to get a client.    
    //========================================
    public static HttpClientAdapter getClient(String url) {
        // use okhttp to send events
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.SECONDS);

        return OkHttpClientAdapter.builder()
                .url(url)
                .httpClient(client)
                .build();
    }
    //  CM:  ==>  End of Snowplow routine to get a client.    

    private static Boolean getEnvBoolean(String envVar) {

        //  CM:  Get the environment variable, if it exists.
        Boolean envBool = false;
        String envString = System.getenv(envVar);

        if (envString == null) {
            QLog.l().logQUser().debug("--> Var: " + envVar + " is null");
        }

        //  CM:  Only do processing if string not null.
        if ((envString != null) && (envString.toUpperCase().equals("YES"))) {
            envBool = true;
        }

        QLog.l().logQUser().debug("--> Var " + envVar + (envBool ? ": True" : ": False"));

        return envBool;
    }

    private static String getEnvString(String envVar, String defaultString) {

        //  CM:  Get the environment variable, if it exists.
        String envValue = defaultString;
        String envString = System.getenv(envVar);

        if (envString == null) {
            QLog.l().logQUser().debug("--> Var: " + envVar + " is null");
        }

        //  CM:  Only do processing if string not null.
        if (envString != null) {
            envValue = envString;
        }

        QLog.l().logQUser().debug("--> Var " + envVar + ": " + envValue);

        return envValue;
    }

    //  CM:  ==>  Start of Snowplow routine to add a citizen to the queue.
    public void SnowplowAddCitizen(Long spId, QUser csr) {

        //  Only execute Snowplow calls if requested.
        if (snowplowCall) {

            //  Extract info Snowplow needs.
            Long clientId = spId;
            QOffice csrOffice = csr.getOffice();
            Integer officeNumber = csrOffice.getOfficeNumber();
            QCustomer cust = csr.getCustomer();
            int svcCount = 0;
            if (cust == null) {
                //QLog.l().logger().debug("==> Null customer: Service count set to 1");
                svcCount = 1;
            }
            else {
                svcCount = cust.getServiceCount();
            }
            String officeType = (csrOffice.getSmartboardType().toUpperCase().equals(
                    "NOCALLONSMARTBOARD") ? "non-reception" : "reception");
            Long agentId = csr.getId();
            String agentRole = (csr.getAdminAccess() ? "GA" : "CSR");
            Boolean agentQTxn = csr.getQuickTxn();

            //QLog.l().logger().debug("==> Before Snowplow addcitizen call");

            // Create a Map of the data you want to include...
            //            QLog.l().logger().debug("    --> Client:  " + clientId.toString() + "; Count: "
            //                    + svcCount);
            Map<String, Object> citizenMap = new HashMap<>();
            citizenMap.put("client_id", clientId);
            citizenMap.put("quick_txn", false);
            citizenMap.put("service_count", svcCount);
            SelfDescribingJson citizen = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/citizen/jsonschema/3-0-0", citizenMap);

            //----------------------------------------
            //QLog.l().logger().debug("    --> OfficeId: " + officeId + "; OType: " + officeType);
            Map<String, Object> officeMap = new HashMap<>();
            officeMap.put("office_id", officeNumber);
            officeMap.put("office_type", officeType);
            SelfDescribingJson office = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/office/jsonschema/1-0-0", officeMap);

            //----------------------------------------
            //QLog.l().logger().debug("    --> AgentId:  " + agentId + "; ARole: " + agentRole);
            Map<String, Object> agentMap = new HashMap<>();
            agentMap.put("agent_id", agentId);
            agentMap.put("role", agentRole);
            agentMap.put("quick_txn", agentQTxn);
            SelfDescribingJson agent = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/agent/jsonschema/2-0-0", agentMap);

            //----------------------------------------
            List<SelfDescribingJson> contexts = new ArrayList<>();
            contexts.add(citizen);
            contexts.add(office);
            contexts.add(agent);

            //----------------------------------------
            // Create your event data -- in this example the event has no data of its own
            SelfDescribingJson addcitizenData = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/addcitizen/jsonschema/1-0-0");
            // Track your event with your custom event data
            tracker.track(Unstructured.builder()
                    .eventData(addcitizenData)
                    .customContext(contexts)
                    .build());
            //QLog.l().logger().debug("    ==> After Snowplow addcitizen call");
            //            QLog.l().logger().debug("==> Event: addcitizen; ID: " + clientId + "; Count: "
            //                    + svcCount);
        }
    }
    //  CM:  ==>  End of Snowplow routine to add a citizen to the queue.

    //  CM:  ==>  Start of Snowplow routine to choose a service.
    public void SnowplowChooseService(Long spId, QUser csr, QService citizenService, String svcChannel) {

        //  Only call Snowplow if requested.
        if (snowplowCall) {

            //  Extract info Snowplow needs.
            Long clientId = spId;
            QOffice csrOffice = csr.getOffice();
            Integer officeNumber = csrOffice.getOfficeNumber();
            String officeType = (csrOffice.getSmartboardType().toUpperCase().equals(
                    "NOCALLONSMARTBOARD") ? "non-reception" : "reception");
            QCustomer cust = csr.getCustomer();
            int svcCount = 0;
            if (cust == null) {
                //QLog.l().logger().debug("==> Null customer: Service count set to 1");
                svcCount = 1;
            }
            else {
                svcCount = cust.getServiceCount();
            }
            Long agentId = csr.getId();
            String agentRole = (csr.getAdminAccess() ? "GA" : "CSR");
            Boolean agentQTxn = csr.getQuickTxn();
            Long serviceId = citizenService.getId();
            Long parentId = citizenService.getParentId();
            String pgmName = citizenService.getParent().getName();
            String svcName = citizenService.getName();
            Boolean citizenQtxn = false;

            //QLog.l().logger().debug("==> Before Snowplow chooseservice call");

            // Create a Map of the data you want to include...
            //            QLog.l().logger().debug("    --> Client:  " + clientId.toString() + "; Count: "
            //                    + svcCount);
            Map<String, Object> citizenMap = new HashMap<>();
            citizenMap.put("client_id", clientId);
            citizenMap.put("quick_txn", citizenQtxn);
            citizenMap.put("service_count", svcCount);
            SelfDescribingJson citizen = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/citizen/jsonschema/3-0-0", citizenMap);

            //----------------------------------------
            //QLog.l().logger().debug("    --> OfficeId: " + officeId + "; OType: " + officeType);
            Map<String, Object> officeMap = new HashMap<>();
            officeMap.put("office_id", officeNumber);
            officeMap.put("office_type", officeType);
            SelfDescribingJson office = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/office/jsonschema/1-0-0", officeMap);

            //----------------------------------------
            //QLog.l().logger().debug("    --> AgentId:  " + agentId + "; ARole: " + agentRole);
            Map<String, Object> agentMap = new HashMap<>();
            agentMap.put("agent_id", agentId);
            agentMap.put("role", agentRole);
            agentMap.put("quick_txn", agentQTxn);
            SelfDescribingJson agent = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/agent/jsonschema/2-0-0", agentMap);

            //----------------------------------------
            List<SelfDescribingJson> contexts = new ArrayList<>();
            contexts.add(citizen);
            contexts.add(office);
            contexts.add(agent);

            //            //  Get service information.
            //            QLog.l().logger().debug("    --> Service Info");
            //            QLog.l().logger().debug("        --> Channel:  " + svcChannel);
            //            QLog.l().logger().debug("        --> PgmId:    " + serviceId);
            //            QLog.l().logger().debug("        --> ParentId: " + parentId);
            //            QLog.l().logger().debug("        --> Pgm Name: " + pgmName);
            //            QLog.l().logger().debug("        --> Service:  " + svcName);
            //            QLog.l().logger().debug("        --> QTxn:     " + (citizenQtxn ? "True" : "False"));

            // Create your event data -- in this example the event has data of its own
            Map<String, Object> chooseserviceMap = new HashMap<>();
            chooseserviceMap.put("channel", svcChannel);
            chooseserviceMap.put("program_id", serviceId);
            chooseserviceMap.put("parent_id", parentId);
            chooseserviceMap.put("program_name", pgmName);
            chooseserviceMap.put("transaction_name", svcName);

            SelfDescribingJson chooseserviceData = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/chooseservice/jsonschema/2-0-0", chooseserviceMap);
            // Track your event with your custom event data
            tracker.track(Unstructured.builder()
                    .eventData(chooseserviceData)
                    .customContext(contexts)
                    .build());

            //QLog.l().logger().debug("    ==> After Snowplow chooseservice call");
            //            QLog.l().logger().debug("==> Event: chooseservice; ID: " + clientId + "; Count: "
            //                    + svcCount);
        }
    }
    //  CM:  ==>  End of Snowplow routine to choose a service.

    //  CM:  ==>  Start of Snowplow routine to add a citizen to the queue.
    public void SnowplowLogEvent(QCustomer qCitizen) {

        //  Only call Snowplow if requested.
        if (snowplowCall) {

            //  Extract info Snowplow needs.
            Long clientId = qCitizen.getSpId();
            Boolean clientQTxn = qCitizen.getTempQuickTxn();
            Integer citizenState = qCitizen.getStateIn();
            String previousEvent = qCitizen.getEventPrevious();
            QUser csr = qCitizen.getUser();
            QOffice csrOffice = csr.getOffice();
            Integer officeNumber = csrOffice.getOfficeNumber();
            //String officeName = csrOffice.getName();
            QCustomer cust = csr.getCustomer();
            int svcCount = 0;
            if (cust == null) {
                QLog.l().logger().debug("==> Null customer: Service count set to 1");
                svcCount = 1;
            }
            else {
                svcCount = cust.getServiceCount();
            }
            String officeType = (csrOffice.getSmartboardType().toUpperCase().equals(
                    "NOCALLONSMARTBOARD") ? "non-reception" : "reception");
            Long agentId = csr.getId();
            String agentRole = (csr.getAdminAccess() ? "GA" : "CSR");
            Boolean agentQTxn = csr.getQuickTxn();

            //QLog.l().logger().debug("==> Before Snowplow logevent call");

            // Create a Map of the data you want to include...
            //            QLog.l().logger().debug("    --> Client:  " + clientId.toString() + "; Count: "
            //                    + svcCount);
            Map<String, Object> citizenMap = new HashMap<>();
            citizenMap.put("client_id", clientId);
            citizenMap.put("quick_txn", clientQTxn);
            citizenMap.put("service_count", svcCount);
            SelfDescribingJson citizen = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/citizen/jsonschema/3-0-0", citizenMap);

            //----------------------------------------
            //QLog.l().logger().debug("    --> ONum: " + officeNumber + "; OName: " + officeName);
            Map<String, Object> officeMap = new HashMap<>();
            officeMap.put("office_id", officeNumber);
            officeMap.put("office_type", officeType);
            SelfDescribingJson office = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/office/jsonschema/1-0-0", officeMap);

            //----------------------------------------
            //QLog.l().logger().debug("    --> AgentId:  " + agentId + "; ARole: " + agentRole);
            Map<String, Object> agentMap = new HashMap<>();
            agentMap.put("agent_id", agentId);
            agentMap.put("role", agentRole);
            agentMap.put("quick_txn", agentQTxn);
            SelfDescribingJson agent = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/agent/jsonschema/2-0-0", agentMap);

            //----------------------------------------
            List<SelfDescribingJson> contexts = new ArrayList<>();
            contexts.add(citizen);
            contexts.add(office);
            contexts.add(agent);

            //  Get the event schema to use.
            String schema = "";
            String schemaVersion = "1-0-0";
            Boolean allOK = true;
            Boolean noExtraParameters = true;
            Integer previousState = qCitizen.getStateInPrevious();
            Map<String, Object> eventDataMap = new HashMap<>();
            SelfDescribingJson logData = new SelfDescribingJson(
                    "iglu:ca.bc.gov.cfmspoc/addtoqueue/jsonschema/1-0-0");

            //  Set the json schema depending on the customer state.
            switch (qCitizen.getState()) {
                case STATE_DEAD:  //  State 0, Citizen left
                    schema = "customerleft";
                    break;
                case STATE_WAIT:  //  State 1, Citizen waiting in the queue
                    if (qCitizen.getStateInPrevious() == 0) {
                        schema = "addtoqueue";
                    }
                    else {
                        schema = "returntoqueue";
                    }
                    break;
                case STATE_WAIT_AFTER_POSTPONED:  //  State 2, 
                    schema = "Invalid";
                    allOK = false;
                    break;
                case STATE_WAIT_COMPLEX_SERVICE:  //  State 3,
                    schema = "Invalid";
                    allOK = false;
                    break;
                case STATE_INVITED:  //  State 4, citizen invited to CSR desk
                    schema = "invitecitizen";
                    break;
                case STATE_INVITED_SECONDARY:  //  State 5, citizen invited to CSR desk
                    
                    //                    QLog.l().logger().debug("--> Invite - PS: " + previousState + "; PE: "
                    //                            + previousEvent);

                    //  Previous state determines which event to call.
                    switch (previousState) {
                        case 6:
                            schema = "invitecitizen";
                            break;
                        case 11:
                            schema = "invitefromhold";
                            break;
                        case 12:
                            schema = "invitefromlist";
                            break;
                        default:
                            schema = "Invalid";
                            allOK = false;
                            break;
                    }

                    break;
                case STATE_REDIRECT:  //  State 6
                    schema = "additionalservice";
                    break;
                case STATE_WORK:  //  State 7, citizen being served.
                    schema = "beginservice";
                    break;
                case STATE_WORK_SECONDARY:  //  State 8, citizen being served
                    //  If called from hold queue, don't do a begin service call.
                    if (previousEvent.equals("invitefromhold")) {
                        schema = "ignore";
                        allOK = false;
                    }
                    else {
                        schema = "beginservice";
                    }
                    break;
                case STATE_BACK:  //  State 9, 
                    schema = "Invalid";
                    allOK = false;
                    break;
                case STATE_FINISH:  //  State 10, citizen finished receiving service
                    //                QLog.l().logger().debug("    --> Finish: Time accurate, Count: " + qCitizen
                    //                        .getQuantity());
                    schema = "finish";
                    noExtraParameters = false;
                    eventDataMap.put("inaccurate_time", false);
                    eventDataMap.put("count", Integer.parseInt(qCitizen.getQuantity()));
                    logData = new SelfDescribingJson(
                            "iglu:ca.bc.gov.cfmspoc/finish/jsonschema/1-0-0", eventDataMap);

                    //  Update service count.
                    cust.setServiceCount(svcCount + 1);
                    break;
                case STATE_POSTPONED:  //  State 11
                    schema = "hold";
                    noExtraParameters = false;
                    eventDataMap.put("time", 0);
                    logData = new SelfDescribingJson(
                            "iglu:ca.bc.gov.cfmspoc/hold/jsonschema/1-0-0", eventDataMap);
                    break;
                case STATE_POSTPONED_REDIRECT:  //  State 12, user clicked on user in wait queue.
                    schema = "Ignore";
                    allOK = false;
                    break;
                case STATE_INACCURATE_TIME:    //  State 13
                    //                QLog.l().logger().debug("    --> Finish: Time NOT accurate, Count: " + qCitizen
                    //                        .getQuantity());
                    schema = "finish";
                    noExtraParameters = false;
                    eventDataMap.put("inaccurate_time", true);
                    eventDataMap.put("count", Integer.parseInt(qCitizen.getQuantity()));
                    logData = new SelfDescribingJson(
                            "iglu:ca.bc.gov.cfmspoc/finish/jsonschema/1-0-0", eventDataMap);

                    //  Update service count.  Shouldn't have to do this, as inaccurate time
                    //  only valid for last service, but just in case.
                    cust.setServiceCount(svcCount + 1);
                    break;
                default:
                    schema = "Undefined";
                    allOK = false;
                    break;
            }

            //            QLog.l().logger().debug("    --> Schema:  " + schema + "; State: " +
            //                    qCitizen.getStateIn());

            //----------------------------------------

            //  Set the previous schema.
            qCitizen.setEventPrevious(schema);

            // Create your event data -- in this example the event has no data of its own
            if (allOK) {

                //  Debug.
                //                QLog.l().logger().debug("    --> Schema:  " + schema + "; State: " +
                //                        qCitizen.getStateIn() + "; Count: " + svcCount);

                //  If no extra parameters, use schema with no data.  Use right schema version.
                if (noExtraParameters) {
                    logData = new SelfDescribingJson("iglu:ca.bc.gov.cfmspoc/" + schema
                            + "/jsonschema/" + schemaVersion);
                }

                // Track your event with your custom event data
                tracker.track(Unstructured.builder()
                        .eventData(logData)
                        .customContext(contexts)
                        .build());
                //                QLog.l().logger().debug("==> Event: " + schema + "; ID: " + clientId + "; Count: "
                //                        + svcCount);
            }

            //QLog.l().logger().debug("    ==> After Snowplow logevent call");
        }
    }
    //  CM:  ==>  End of Snowplow routine to add a citizen to the queue.

    //  CM:  This variable sets the states in which a customer can be called.
    //  CM:  Used to prevent two CSRs calling the same customer at the same time.
    private static List<Integer> validInviteStates = Arrays.asList(1, 2, 3, 6, 11);

    //  CM:  This method checks to ensure a customer is in a state where they can be called.
    //  CM:  Used to prevent two CSRs calling the same customer at the same time.
    public boolean CustomerCanBeCalled(QCustomer potentialCustomer, Object[] msg,
            String calledFrom) {

        //  Assume the customer cannot be called.
        boolean okToCall = false;
        String custName = "";

        //  If potential customer not null, it's possible they could be called.
        if (potentialCustomer != null) {

            //  Determine whether the customer is in a valid state to be called.
            okToCall = (validInviteStates.contains(potentialCustomer.getStateIn()));

            //  Debug.
            //            QLog.l().logger().debug("    --> From: " + calledFrom + "; State: "
            //                    + potentialCustomer.getStateIn()
            //                    + "; CallOK: " + okToCall);
            String pcOffice = (potentialCustomer.getOffice() == null ? "Null" : potentialCustomer
                    .getOffice().getName());
            String pcService = (potentialCustomer.getService() == null ? "Null" : potentialCustomer
                    .getService().getName());
            custName = potentialCustomer.getName();
            String pcCSR = (potentialCustomer.getUser() == null ? "Unknown" : potentialCustomer
                    .getUser().getName());

            //            QLog.l().logger().debug("    --> O: " + pcOffice + "; CSR: " + pcCSR + "; Cust: "
            //                    + custName
            //                    + "; Svc: "
            //                    + pcService);

            //  CM:  Set a return message.
            if (okToCall) {
                msg[0] = "OK to call customer " + custName;
            }
            else {
                msg[0] = "Cannot call customer " + custName + ". They are likely being served by "
                        + pcCSR;
            }
        }

        //  CM:  Debug.
        //QLog.l().logger().debug("==> End: CanCall");

        //  Return the result.
        return okToCall;
    }

    final Task addCustomerTaskComplex = new Task(Uses.TASK_STAND_COMPLEX) {

        @Override
        public RpcStandInService process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            super.process(cmdParams, ipAdress, IP);
            QLog.l().logQUser().debug("addCustomerTaskComplex");

            Long serviceID = null;

            // ????????? ????????????? ????????? :: Support extensibility plug-ins
            for (final ISelectNextService event : ServiceLoader.load(ISelectNextService.class)) {
                QLog.l().logger().info(
                    "????? SPI ??????????. ????????:  Call the SPI extension. Description:" + event
                        .getDescription());
                try {
                    serviceID = event.select(null, null, cmdParams.complexId).getId();
                } catch (Throwable tr) {
                    QLog.l().logger().error(
                        "????? SPI ?????????? ?????????? ???????. ????????:  The SPI extension call failed. Description:"
                            + tr);
                }
            }

            // ????????? ????? ????????? ?????? :: Deflative selection of the next service
            if (serviceID == null) {
                for (LinkedList<LinkedList<Long>> ids : cmdParams.complexId) {
                    for (LinkedList<Long> id : ids) {
                        serviceID = id.getFirst();
                        ids.remove(id);
                        break;
                    }
                    if (serviceID != null) {
                        break;
                    }
                }
            } else {
                // ???????? ???????? ??????  :: We will select the chosen service
                for (LinkedList<LinkedList<Long>> ids : cmdParams.complexId) {
                    for (LinkedList<Long> id : ids) {
                        if (serviceID.equals(id.getFirst())) {
                            ids.remove(id);
                            break;
                        } else {
                            for (Long long1 : id) {
                                if (serviceID.equals(long1)) {
                                    id.remove(long1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (serviceID == null) {
                throw new ServerException(
                    "?????? ????????? ? ??????? ? ?????? ???????. ????? ?? ???????. :: An error queued up for many services. No services found.");
            }

            // ??????? ????????? ?????? ??????? ?? ???????? ?????????
            // ???????? ???????
            // create a customizer by calling the job to create a customizer
            // load the job
            cmdParams.serviceId = serviceID;
            final RpcStandInService txtCustomer = addCustomerTask.process(cmdParams, ipAdress, IP);
            txtCustomer.getResult().setComplexId(cmdParams.complexId);
            return txtCustomer;

        }
    };
    /**
     * Change Service of the Client
     */
    final Task changeService = new Task(Uses.TASK_CHANGE_SERVICE) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("changeService");
            super.process(cmdParams, ipAdress, IP);

            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            //???????????? ?? ????????? ??? ???????????? ??????, ?????? ???????? customerID
            // switch to the custodian with parallel reception, must arrive customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers()
                    .get(cmdParams.customerId);
                if (parallelCust == null) {
                    QLog.l().logger()
                        .warn("PARALLEL: User have no Customer for switching by customer ID=\""
                            + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    QLog.l().logger().debug(
                        "???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust
                            .getFullNumber()
                            + "\"");
                }
            }
            final QCustomer customer = user.getCustomer();
            // ?????????? ? ?????? ???????
            // ???????? ?????? ???????
            final QService oldService = customer.getService();
            // ??? ??? ????? ???????.
            final QService newServiceR = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QService newService =
                newServiceR.getLink() != null ? newServiceR.getLink() : newServiceR;

            // ?????? ????? ? ????? ??????.
            customer.setService(newService);
            customer.setTempComments(cmdParams.comments);

            try {
                // ????????? ????????? ????????.
                customer.save();
                //QServer.savePool();
//                Uses.sendUDPBroadcast(newService.getId().toString(), ServerProps.getInstance().getProps().getClientPort());
//                Uses.sendUDPBroadcast(oldService.getId().toString(), ServerProps.getInstance().getProps().getClientPort());
                //????????? ?????????? ? ???, ??? ?????????? ????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????. ?????? ??????????? ?? ???????? ?????
//                MainBoard.getInstance().killCustomer(user);
            } catch (Exception ex) {
                QLog.l().logger().error("Exception TASK_CHANGE_SERVICE" + ex.getLocalizedMessage());
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ?????????? ?????????, ??????? ? ???????. Invite the custom, the first in the queue.
     */
    final Task inviteSelectedCustomerTask = new Task(Uses.TASK_INVITE_SELECTED_CUSTOMER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP, QCustomer pickedCustomer) {
            //QLog.l().logQUser().debug("inviteSelectedCustomerTask");
            super.process(cmdParams, ipAdress, IP, pickedCustomer);
            // ??? ?? ??? ??? ?????? ::: Here he is doing it all
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            //???????????? ?? ????????? ??? ???????????? ??????, ?????? ???????? customerID
            // switch to the custodian with parallel reception, must arrive customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers().get(cmdParams.customerId);
                if (parallelCust == null) {
                    //QLog.l().logger().error("PARALLEL: User have no Customer for switching by customer ID=\"" + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    //QLog.l().logger().error("???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust.getFullNumber() + "\"");
                }
            }
            // ??? ??? ???? ?????????
            final QCustomer customer = user.getCustomer();

            //set invite_time
            customer.setInviteTime(new Date());

            // ??????
            customer.setPostponedStatus(cmdParams.textData);
            // ?? ??????? ????????. 0 - ?????????
//            customer.setPostponPeriod(cmdParams.postponedPeriod);
            customer.setPostponPeriod(0);
            // ???? ???????? ????????? ? ????????? ?????, ?? ????? ?????? ?????? ???????????
            customer.setIsMine(cmdParams.isMine != null && cmdParams.isMine ? cmdParams.userId : null);
            // ? ???? ?????? ????????? ? ?????????
            //"??? ??? ?????? ????? ? ??? - ? ??? ??????????"
            // ?? ??????? ????????? ????????? ?????? ????? ? ??????????, ???? ????? ????????? ???????? ? ?????? ???????????
            customer.setFinishTime(new Date());
            // ???????? ????????? ? ????????? "?????????????", ?? ?? "?????????"
            customer.setState(CustomerState.STATE_POSTPONED_REDIRECT);
            //QLog.l().logQUser().debug("==> Task(InviteSelCust): setState(Redirect)");

            try {
                for (QService service : QServiceTree.getInstance().getNodes()) {
                    for (QCustomer c : service.getClients()) {
                        if (c.getId() == customer.getId()) {
                            service.removeCustomer(c);
                        }
                    }
                }

                //????? ???? ?? ???????? ????????, ?????????? ? ????
                // bobik died but the medal remains, send to the pool
                user.setCustomer(null);
                customer.setUser(null);
                QPostponedList.getInstance().addElement(customer);

                // ????????? ????????? ????????.
                //QServer.savePool();

            } catch (Throwable t) {
                QLog.l().logger().error("????????? ??? ?????.", t);
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ?????????? ????????? ?? ???? ?????????? Invite a customizer from the pending pool
     */
    final Task invitePostponedTask = new Task(Uses.TASK_INVITE_POSTPONED) {

        /**
         * C?????????????, ? ?? ??????? ?????? ? ???? ??. ? ??? ????? ???? ??????? ?????, ??????? "??????????" ???? ? ???? ???????. ????? ????????? ????????
         * ????? ???? ???????? ?????????? ?????????, ??????? ??????????, ? ??????? ???.
         *
         * Synchronize, and then cause the same. And still here you need to call a method that will "speak" whom and where to call. A situation may happen
         * When two call the last custodian, the first will get it, but the second one does not.
         */
        @Override
        synchronized public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("invitePostponedTask");
            super.process(cmdParams, ipAdress, IP);
            // ????????????? ?????? ? ????????
            // Synchronization of work with the client

            // ?????????? ?? ????? ??????? ???? ??????? ?????????.
            // ???? ??? ????? ???????????.
            // ??? ????? ??????? ?????? ?????????? ?? ???? ???????? ? ???? ??????? ????? ??????.
            // Determine from which queue you need to select a customizer.
            // So far without taking into account the coefficient.
            // To do this, we look at the first custodians in all queues and look for the first among the first.
            final QUser user = QUserList.getInstance().getById(cmdParams.userId); // ???? ::User
            final QCustomer customer;
            POSTPONED_TASK_LOCK.lock();
            try {
                // ??????? ??????????? ????????? ?? ??
                // select the deferred custodian by id
                customer = QPostponedList.getInstance().getById(cmdParams.customerId);
                if (customer == null) {
                    return new JsonRPC20Error(JsonRPC20Error.ErrorRPC.POSTPONED_NOT_FOUND,
                        cmdParams.customerId);
                } else {
                    QPostponedList.getInstance().removeElement(customer);
                }
                // ????????? ????? ?????????, ???????? ?? ??????.
                // define the user of the custom that he called.
                user.setCustomer(customer);
                // ???????? ????????? ?????, ??????? ??? ??????.
                // Delivered the user's custom tool, which caused it.
                customer.setUser(user);
                // ?????? ??? ????? ????. ??????? ????? ?????????? ? ?????????? ?? ????????? ??? ???????? ???????. ????? ? statistic ???????? ????????
                // just got up Tipo. It's not like waiting for a queue. Invnt in the statistic expectation of a huge
//                customer.setStandTime(new Date());
                // ?????? ????? ??????
                // put the call time
                customer.setCallTime(new Date());
                // ?? ? ?????? ????????? ???? ??? ??? ?????? ?? ???????? ? ???????, ?? ??????? ????????
                // well, and define the service if the one who called does not work with the service, from which they postponed
                //set invite_time
                customer.setInviteTime(new Date());

                boolean f = true;
                for (QPlanService pl : user.getPlanServices()) {
                    if (pl.getService().getId().equals(customer.getService().getId())) {
                        f = false;
                        break;
                    }
                }
                if (f) {
                    customer.setService(QServiceTree.getInstance()
                        .getById(user.getPlanServices().get(0).getService().getId()));
                }
                // ???????? ????????? ? ????????? "??????????????"
                // the customizer goes into the "invite" state
                customer.setState(CustomerState.STATE_INVITED_SECONDARY);
                // ???? ???????? ????????, ?? ??? ??????????? ????????? ? ?????
                // ?? ??? ???? ? ?????
                // if the caller has volunteered, it must be sent back
                // he already has a user
            } catch (Exception ex) {
                throw new ServerException(
                    "?????? ??? ?????? ??????????? ???????? ????????????? ::: Error when calling pending directly by the user"
                        + ex);
            } finally {
                POSTPONED_TASK_LOCK.unlock();
            }
            try {
                // ??????????? ?????? ::: Sound with a sound
                //SoundPlayer.play("/ru/apertum/qsystem/server/sound/sound.wav");
                //QLog.l().logQUser().debug("SoundPlayer");
                SoundPlayer.inviteClient(customer.getService(),
                    user.getCustomer().getPrefix() + user.getCustomer().getNumber(),
                    user.getPoint(), true);
                // ????????? ????????? ????????.  ::: Save the state of the queues.
                //QServer.savePool();
                //????????? ?????????? ? ???, ??? ???????? ????????? ??????????
                // ?????? ????????????? ???????? ?????
                // send out an alert that a visitor has been called
                // Must be highlighted on the main display
                //QLog.l().logQUser().debug("invitePostponedTask MainBoard invite");
                MainBoard.getInstance().inviteCustomer(user, user.getCustomer());
                //????????? ?????????? ? ???, ??? ??????????? ???????, ????????? ??????? ?????????? ?? ??????????, ?? ??? ?????????? ?????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????
                // send out an alert that the deferred has been called, the status of the queue has changed has not changed, but the pending pool has changed
                // send out broadly by UDP to a specific port
                //QLog.l().logQUser().debug("Uses");
                Uses.sendUDPBroadcast(Uses.TASK_REFRESH_POSTPONED_POOL,
                    ServerProps.getInstance().getProps().getClientPort());
            } catch (Exception ex) {
                QLog.l().logQUser().debug("getStackTrace");
                StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                for (int i = 1; i < elements.length; i++) {
                    StackTraceElement s = elements[i];
                    QLog.l().logQUser().debug("\tat " + s.getClassName() + "." + s.getMethodName()
                        + "(" + s.getFileName() + ":" + s.getLineNumber() + ")");
                }
                QLog.l().logger().error(ex);
            }
            return new RpcInviteCustomer(customer);
        }
    };
    /**
     * ???????? ???????? ????? :: Get a list of services
     */
    final Task getServicesTask = new Task(Uses.TASK_GET_SERVICES) {

        @Override
        public RpcGetAllServices process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getServicesTask");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetAllServices(
                new RpcGetAllServices.ServicesForWelcome(QServiceTree.getInstance().getRoot(),
                    ServerProps.getInstance().getProps()));
        }
    };

    ;
    /**
     * ???? ?????? ??????? ????? ?????? ?????????????, ?? ????? ???????? ??? ?????? ?? ??????? ?????
     * ???? ?????, ?? ??? ?????????? ? ??????? ???? ?? ??????????? ?????? ? ??????? ? ??????
     * ?????????? ???????
     *
     * @return 1 - ????????, 0 - ????? ??????. 2 - ???????
     */
    final Task aboutServicePersonLimit = new Task(Uses.TASK_ABOUT_SERVICE_PERSON_LIMIT) {

        @Override
        public RpcGetInt process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("aboutServicePersonLimit");
            super.process(cmdParams, ipAdress, IP);
            if (RpcBanList.getInstance().isBaned(cmdParams.textData)) {
                return new RpcGetInt(2);
            }
            // ???? ????? ?????????? ???????? ????????? ?????? ??????????? ? ???? ?????????
            final QService srvR = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QService srv = srvR.getLink() != null ? srvR.getLink() : srvR;
            try {
                return new RpcGetInt(srv.isLimitPersonPerDayOver(cmdParams.textData) ? 1 : 0);
            } catch (Exception ex) {
                throw new ServerException("??????? ???-?? ??? ??????????? ???????????.", ex);
            }
        }
    };
    /**
     * ???????? ???????? ????????? ??????
     */
    final Task getServiceConsistemcy = new Task(Uses.TASK_GET_SERVICE_CONSISANCY) {

        @Override
        public RpcGetServiceState process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getServiceConsistemcy");
            super.process(cmdParams, ipAdress, IP);
            final QService srvR = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QService srv = srvR.getLink() != null ? srvR.getLink() : srvR;
            return new RpcGetServiceState(srv.getClients());
        }
    };
    /**
     * ???????? ???????? ????????? ??????
     */
    final Task aboutTask = new Task(Uses.TASK_ABOUT_SERVICE) {

        @Override
        public RpcGetServiceState process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("aboutTask");
            super.process(cmdParams, ipAdress, IP);
            // ???????? ??????????? ?? ?????? ??? ??????
            int min = Uses.LOCK_INT;
            final Date day = new Date();
            final QService srvR = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QService srv = srvR.getLink() != null ? srvR.getLink() : srvR;
            if (srv.getTempReasonUnavailable() != null && !""
                .equals(srv.getTempReasonUnavailable())) {
                return new RpcGetServiceState(0, srv.getTempReasonUnavailable());
            }
            // ???? ?? ????? ?????????? ????????? ???????????? ? ???? ?????????
            if (srv.isLimitPerDayOver()) {
                QLog.l().logger().warn("?????? \"" + srv.getName()
                    + "\" ?? ?????????????? ?????? ?? ?????????? ?????? ????????? ????????? ?????????? ? ????.");
                return new RpcGetServiceState(Uses.LOCK_PER_DAY_INT, "");
            }
            // ???? ??? ??????????, ????????? ??? ???????? ?? ????? ?? ??????????
            if (srv.getSchedule() == null
                || QCalendarList.getInstance().getById(1).checkFreeDay(day)
                || (srv.getCalendar() != null && srv.getCalendar().checkFreeDay(day))) {
                if (srv.getSchedule() == null) {
                    QLog.l().logger().warn("???? ??? ??????????, ?? ????? ?? ??????????.");
                } else if (QCalendarList.getInstance().getById(1).checkFreeDay(day)) {
                    QLog.l().logger().warn("???? ???????? ?? ????? ?? ??????????.");
                } else {
                    QLog.l().logger().warn("???? ??? ????????? ? ???????? ?? ????? ?? ??????????.");
                }
                min = Uses.LOCK_FREE_INT;
            } else {
                // ????????? ????? ?????? ? k???? ?????? ?? ???? ????
                final QSchedule.Interval interval = srv.getSchedule().getWorkInterval(day);
                // ?????????? ?????? ? ????? ???????? ??? ?? ???????
                // ???? ???????? ? ???? ???? ?? ????????? ???????? ?? "??????" ? ??????? ??????????
                final GregorianCalendar gc_day = new GregorianCalendar();
                gc_day.setTime(day);
                if (!(interval.start == null || interval.finish == null)) {
                    final int h = gc_day.get(GregorianCalendar.HOUR_OF_DAY);
                    final int m = gc_day.get(GregorianCalendar.MINUTE);
                    gc_day.setTime(interval.start);
                    final int sh = gc_day.get(GregorianCalendar.HOUR_OF_DAY);
                    final int sm = gc_day.get(GregorianCalendar.MINUTE);
                    gc_day.setTime(interval.finish);
                    final int eh = gc_day.get(GregorianCalendar.HOUR_OF_DAY);
                    final int em = gc_day.get(GregorianCalendar.MINUTE);
                    if (!(sh * 60 + sm <= h * 60 + m && h * 60 + m <= eh * 60 + em) && (!((sh == eh)
                        && (sm
                        == em)))) {
                        QLog.l().logger().warn(
                            "???? ??????? ????? ?? ???????? ? ??????? ???????? ?? ????? ?? ??????????. "
                                + sh
                                + "." + sm + " < " + h + "." + m + " < " + eh + "." + em);
                        min = Uses.LOCK_FREE_INT;
                    }
                } else {
                    QLog.l().logger().warn(
                        "???? ? ???? ???? ?? ?????????? ?????? ??? ????? ?? ????? ?? ??????????."
                            + (
                            interval.start == null ? "start == null" : "end == null"));
                    min = Uses.LOCK_FREE_INT;
                }
            }
            // ???? ?? ????????, ?? ???????? ????? ? ????????? ??????????
            if (min == Uses.LOCK_FREE_INT) {
                QLog.l().logger().warn("?????? \"" + cmdParams.serviceId
                    + "\" ?? ?????????????? ?????? ?? ???????? ??????????.");
                return new RpcGetServiceState(min, "");
            }
            // ????? ?? ?????? ? ??????? ???????????? ?? ??? ??????
            // ???? ??, ?? ??????? ??? ?????? ????? ?  ?????? ???? ?????????? ? ????????
            // ????? ????????? ????? ???????? ? ????? ?? ???????.
            for (QUser user : QUserList.getInstance().getItems()) {
                if (user.hasService(cmdParams.serviceId)) {
                    // ?????? ?? ??????? ?????
                    int sum = 0;
                    for (QPlanService planServ : user.getPlanServices()) {
                        final QService service = QServiceTree.getInstance()
                            .getById(planServ.getService().getId());
                        sum = sum + service.getCountCustomers();
                    }
                    if (min > sum) {
                        min = sum;
                    }
                }
            }
            if (min == Uses.LOCK_INT) {
                QLog.l().logger().warn(
                    "?????? \"" + cmdParams.serviceId
                        + "\" ?? ?????????????? ?? ????? ?????????????.");
            }
            return new RpcGetServiceState(min, "");
        }
    };
    /**
     * ???????? ???????? ????????????? ??? ??????
     */
    final Task getUsersTask = new Task(Uses.TASK_GET_USERS) {

        @Override
        public RpcGetUsersList process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getUsersTask");
            super.process(cmdParams, ipAdress, IP);
            //todo checkUserLive.refreshUsersFon();
            return new RpcGetUsersList(QUserList.getInstance().getItems());
        }
    };
    /**
     * ???????? ???????? ????????? ???????? ??? ????????????. Get a description of the status of the
     * queues for the user.
     */
    final Task getSelfServicesTask = new Task(Uses.TASK_GET_SELF_SERVICES) {

        private final RpcGetSelfSituation DUMMY = new RpcGetSelfSituation(
            new RpcGetSelfSituation.SelfSituation());

        @Override
        public RpcGetSelfSituation process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getSelfServicesTask");
            super.process(cmdParams, ipAdress, IP);
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            //?? ????? ????? ???????? ????? ???????? ??? ????????, ?? ??????? ?????.
            //?? ????? ????? ???????? ????? ???????? ??? ????????, ?? ??????? ?????.
            if (cmdParams.textData != null && !cmdParams.textData.equals("")) {
                user.setPoint(cmdParams.textData);
            }
            long stateH = 0; // ??? ??? ???? ?????????? ?? ?????? ??? ????????????.
            final LinkedList<RpcGetSelfSituation.SelfService> servs = new LinkedList<>();
            for (QPlanService planService : user.getPlanServices()) {
                final QService service = QServiceTree.getInstance()
                    .getById(planService.getService().getId());
                servs.add(new RpcGetSelfSituation.SelfService(service, service.getCountCustomers(),
                    planService.getCoefficient(), planService.getFlexible_coef()));
                stateH =
                    stateH + service.getId() + service.getCountCustomers() * (
                        planService.getCoefficient()
                            + 17);
            }
            // ????? ??????? ????????? ????????????? ?????, ???? ?? ????
            stateH = stateH
                + (user.getCustomer() == null ? -1703
                : (user.getCustomer().getId() + user.getCustomer().getState().ordinal() * 747))
                + ServerProps.getInstance().getProps().getLimitRecall()
                + (user.getShadow() == null ? -147 : user.getShadow().getOldNom());
            for (QCustomer cu : QPostponedList.getInstance().getPostponedCustomers()) {
                stateH =
                    stateH + cu.getId() + cu.getState().ordinal() * 117 + cu.getPostponedStatus()
                        .hashCode();
            }
            final Long hash = hashState.get(cmdParams.userId);
            if (hash == null) {
                hashState.put(cmdParams.userId, stateH);
            } else if (hash.equals(stateH)) {
                if (cmdParams.requestBack == null || !cmdParams.requestBack) {
                    return DUMMY;
                }
            } else {
                hashState.put(cmdParams.userId, stateH);
            }
            return new RpcGetSelfSituation(new RpcGetSelfSituation.SelfSituation(servs,
                user.getCustomer(),
                new LinkedList(user.getParallelCustomers().values()),
                QPostponedList.getInstance().getPostponedCustomers(),
                ServerProps.getInstance().getProps().getLimitRecall(),
                ServerProps.getInstance().getProps().getExtPriorNumber(),
                user.getShadow()));
        }
    };
    /**
     * ???????? ???????? ????????? ???????? ??? ???????????? ? ????????? ??????? ??????? ??????? ??
     * ????? ? ??? ?? ??????. ?? ? ?????? ??????
     */
    final Task getCheckSelfTask = new Task(Uses.TASK_GET_SELF_SERVICES_CHECK) {
        // ???? ?????????? ???????? ??????? ?????? ?? ?? ??? ??????, ?? ?? ?????????? ?????????? ?????????? ? ?? ????????????
        // we need to remember the name of the reception points from the database for users, it will not be reset by the client configuration and not restored
        final HashMap<QUser, String> points = new HashMap<>();

        @Override
        public synchronized RpcGetBool process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getCheckSelfTask");
            if (!QSessions.getInstance().check(cmdParams.userId, ipAdress, IP)) {
                QLog.l().logger().debug(cmdParams.userId + " ACCESS_DENY from " + ipAdress);
                return new RpcGetBool(false);
            }
            hashState.remove(cmdParams.userId);
            super.process(cmdParams, ipAdress, IP);
            //?? ????? ????? ???????? ????? ???????? ??? ????????, ?? ??????? ?????.
            // from the user can come the new name of his office, well, moved the dude.
            if (points.get(QUserList.getInstance().getById(cmdParams.userId)) == null) {
                points.put(QUserList.getInstance().getById(cmdParams.userId),
                    QUserList.getInstance().getById(cmdParams.userId).getPoint());
            } else {
                QUserList.getInstance().getById(cmdParams.userId)
                    .setPoint(points.get(QUserList.getInstance().getById(cmdParams.userId)));
            }
            // ??????? ??????? ??????? ?? ????? ? ??? ?? ??????. ?? ? ?????? ??????
            // ?????? ? ???????? ?? ????? ?????????? ? ???????
            //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + userId);
            /*
             if (checkUserLive.hasId(cmdParams.userId)) {
             QLog.l().logger().debug(cmdParams.userId + " ACCESS_DENY");
             return new RpcGetBool(false);
             }
             // ???? ?????? ?? ???? ???? ???????????? ??? ?????
             checkUserLive.process(cmdParams, ipAdress, IP);
             */
            return new RpcGetBool(true);
        }
    };
    /**
     * ???????? ????????? ???? ??????????
     */
    final Task getPostponedPoolInfo = new Task(Uses.TASK_GET_POSTPONED_POOL) {

        @Override
        public synchronized RpcGetPostponedPoolInfo process(CmdParams cmdParams, String ipAdress,
            byte[] IP) {
            QLog.l().logQUser().debug("getPostponedPoolInfo");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetPostponedPoolInfo(
                QPostponedList.getInstance().getPostponedCustomers());
        }
    };
    /**
     * ???????? ?????? ??????????
     */
    final Task getBanList = new Task(Uses.TASK_GET_BAN_LIST) {

        @Override
        public RpcBanList process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getBanList");
            super.process(cmdParams, ipAdress, IP);
            RpcBanList.getInstance().udo(null);
            return RpcBanList.getInstance();
        }
    };
    /**
     * ??????? ?????????? ?????? ????????? ?? ??????.
     */
    final Task killCustomerTask = new Task(Uses.TASK_KILL_NEXT_CUSTOMER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("==> Start: Task(KillNxtCust).process()");
            super.process(cmdParams, ipAdress, IP);
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            final Long CustId = user.getCustomer().getId();
            int ReturnCode = -1;
            //???????????? ?? ????????? ??? ???????????? ??????, ?????? ???????? customerID
            //Switching to a custodian in parallel reception, must arrive customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers()
                    .get(cmdParams.customerId);
                if (parallelCust == null) {
                    QLog.l().logger()
                        .error("PARALLEL: User have no Customer for switching by customer ID=\""
                            + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    QLog.l().logger().error("???? :: User\"" + user
                        + "\" ???????????? ?? ????????? :: Switched to a customizer \""
                        + parallelCust
                        .getFullNumber() + "\"");
                }
            }
            //QLog.l().logger().error(
            //    "????????: ??????? ?? ?????? ????????? " + user.getCustomer().getPrefix() + "-"
            //        + user
            //        .getCustomer().getNumber() + " ?? ???? \"" + user.getCustomer().getInput_data()
            //        + "\"");
            //QLog.l().logger().error(
            //    "REMOVING: Customer was removing because of absence " + user.getCustomer()
            //        .getPrefix()
            //        + "-" + user.getCustomer().getNumber() + " customer inputted \"" + user
            //        .getCustomer()
            //        .getInput_data() + "\"");
            // ???? ???????? ???? ???-?? ????????? ?? ?????? ???????????, ?? ??????? ???? ????? ?????????? ? ?????? ????????? ???????
            // ? ????????? ??? ? ???, ??? ?????? ???????? ???? ??????? ? ????????? ???????.
            // If the custodian had something entered at the registration point, then delete all such custodians with such entered data
            // and send it to the ban, because Nehru dial a bunch of coupons and sift the queue.
            if (user.getCustomer().getInput_data() != null && !""
                .equals(user.getCustomer().getInput_data())) {
                int cnt = 0;
                for (QService service : QServiceTree.getInstance().getNodes()) {
                    final LinkedList<QCustomer> for_del = new LinkedList<>();
                    service.getClients().stream().filter(
                        (customer) -> (user.getCustomer().getInput_data()
                            .equals(customer.getInput_data())))
                        .forEach((customer) -> {
                            for_del.add(customer);
                        });
                    for_del.stream().forEach((qCustomer) -> {
                        service.removeCustomer(qCustomer);
                    });
                    cnt = cnt + for_del.size();
                }
                if (cnt != 0) {
                    RpcBanList.getInstance().addToBanList(user.getCustomer().getInput_data());
                }
                QLog.l().logger().error(
                    "?????? ? ?????????? " + user.getCustomer().getPrefix() + "-" + user
                        .getCustomer()
                        .getNumber() + " ?? ???? \"" + user.getCustomer().getInput_data()
                        + "\" ??????? ??? ??? " + cnt + " ??????????.");
            }

            // ???????? ????????? ? ????????? "??????????????"
            KILLED_CUSTOMERS.put(user.getCustomer().getFullNumber().toUpperCase(), new Date());
            user.getCustomer().setState(CustomerState.STATE_DEAD);
            try {
                user.setCustomer(null);//????? ???? ? ???????? ?? ????????
                // ????????? ????????? ????????.
                //QServer.savePool();
                //????????? ?????????? ? ???, ??? ?????????? ????????
                // ?????? ??????????? ???????? ?????
                //QLog.l().logQUser().debug("    --> MainBoard...killCustomer(user)");
                MainBoard.getInstance().killCustomer(user);
            } catch (Exception ex) {
                QLog.l().logger().error(ex);
            }

            //  Call John's stored procedure.
            CallStoredProcDone(user, user.getCustomer(), CustId, "Customer left");

            //QLog.l().logQUser().debug("==> End: Task(KillNxtCust).process()");

            return new JsonRPC20OK();
        }
    };
    /**
     * ?????????? ?????????, ??????? ? ???????. Invite the custom, the first in the queue.
     */
    final Task inviteCustomerTask = new Task(Uses.TASK_INVITE_NEXT_CUSTOMER) {

        private final HashSet<QUser> usrs = new HashSet<>();

        private void invite(final QUser user, final boolean isFirst) {
            //            QLog.l().logQUser().debug("==> Start: Task(InvNextCust).invite()");
            //            QLog.l().logQUser().debug("    --> CSR: " + user.getName() + "; Svc: " + user
            //                    .getCurrentService() + "; Cust: " + user.getCustomer().getName());

            if (usrs.contains(user)) {

                //                QLog.l().logQUser().debug("    --> CSR list contains CSR " + user.getName());

                //                QLog.l().logQUser().debug("--> ");
                //                QLog.l().logger().debug("--> Winner:    Cust: " + customer
                //                        .getName() + "; Pri: " + customer
                //                                .getPriority().get() + "; Stand: " + df.format(
                //                                        customer.getStandTime()) + "; Svc: "
                //                        + customer.getService().getName());
                return;
            }
            usrs.add(user);
            final MyRun mr = new MyRun();
            mr.user = user;
            mr.isFrst = isFirst;
            final Thread t = new Thread(mr);
            t.setDaemon(true);
            //QLog.l().logQUser().debug("    --> Starting MyRun thread");
            t.start();
            //QLog.l().logQUser().debug("==> End: Task(InvNextCust).invite()");
        }

        /**
         * C?????????????, ?-?? ??????? ?????? ? ???? ??. ? ??? ????? ???? ??????? ?????, ??????? "??????????" ???? ? ???? ???????. ????? ????????? ????????
         * ????? ???? ???????? ?????????? ?????????, ??????? ??????????, ? ??????? ???.
         */
        @Override
        synchronized public RpcInviteCustomer process(CmdParams cmdParams, String ipAdress,
            byte[] IP) {
            //QLog.l().logQUser().debug("==> Start: Task(InvNextCust).process()");

            super.process(cmdParams, ipAdress, IP);
            // ?????????? ?? ????? ??????? ???? ??????? ?????????.
            // ???? ??? ????? ???????????.
            // ??? ????? ??????? ?????? ?????????? ?? ???? ???????? ? ???? ??????? ????? ??????.
            // Determine from which queue you need to select a customizer.
            // So far without taking into account the coefficient.
            // To do this, we look at the first custodians in all queues and look for the first among the first.

            //  CM:  Get the user that invited the customer.
            final QUser user = QUserList.getInstance().getById(cmdParams.userId); // ????
            //QLog.l().logQUser().debug("--> Ini: " + user.getName());
            if (user.getCustomer() != null) {
                QCustomer tempCust = user.getCustomer();
                //                QLog.l().logger().debug("    --> Cust: " + tempCust.getName() + "; Svc: " + tempCust
                //                        .getService().getName());

                //                QLog.l().logQUser().debug("    --> CSR Id: " + user.getId()
                //                        + "; Param CSR Id: " + cmdParams.userId);
                //
                //                QLog.l().logQUser().debug("    --> CSR CustId: " + user.getCustomer().getId()
                //                        + "; Param CustId: " + cmdParams.customerId);
                //
                //                QLog.l().logQUser().debug("    --> Cust: " + user.getCustomer().getName()
                //                        + "; Svc: " + user.getCustomer().getService().getName());
            }
            else {
                //                QLog.l().logQUser().debug("    --> Customer is null");
            }

            //  CM:  Display info about CSR.
            //QLog.l().logQUser().debug("    --> CSR: " + user.getName() + "; Quick: " + user.getQuickTxn());

            //QLog.l().logQUser().debug("    --> Checking if customer already served by another CSR");

            //  CM: If user has a customer with state of invited, or invited secondary, a recall? 
            //            final boolean isRecall = user.getCustomer() != null && (
            //                CustomerState.STATE_INVITED.equals(user.getCustomer().getState())
            //                    || CustomerState.STATE_INVITED_SECONDARY.equals(user.getCustomer().getState()));

            //  See if this resolves problems.  This may affect other things???
            final boolean isRecall = false;

            // ???? ?? ? ????? ????????? ????????? ????? ???????? ?????
            // Does the user have a called customizer? Then the puerile challenge
            if (isRecall) {

                //                QLog.l().logQUser().debug("    --> Is Recall: CSR: " + user.getName() + "; Cust: "
                //                        + user.getCustomer().getName() + "; CustCSR: " + user.getCustomer()
                //                                .getUser().getName());
                user.getCustomer().upRecallCount(); // ??? ???? ????????? ?????
                //                QLog.l().logger().debug(
                //                    "????????? ????? " + user.getCustomer().getRecallCount() + " ????????? ?" + user
                //                        .getCustomer().getPrefix() + user.getCustomer().getNumber()
                //                        + " ????????????? "
                //                        + cmdParams.userId);

                if (ServerProps.getInstance().getProps().getLimitRecall() != 0
                    && user.getCustomer().getRecallCount() > ServerProps.getInstance().getProps()
                    .getLimitRecall()) {
                    QLog.l().logger().debug(
                            "Customer called too many times: " + user.getCustomer()
                                    .getPrefix() + user.getCustomer().getNumber() + " CSR Id: "
                                    + cmdParams.userId);
                    //?????? ?? ?????? :: Delete for no show
                    killCustomerTask.process(cmdParams, ipAdress, IP);
                } else {
                    // ???????? ????????? ? ????????? ? ??????? ??? ? ????? ? ?????????.
                    // The customizer goes into a state in which he was in this and goes.

                    //  CM:  ONLY if the current CSR unequal to the serving CSR (serving CSR
                    //       didn't double click Invite), ensure another CSR not serving
                    //       the current customer.
                    if (user.getName() != user.getCustomer().getUser().getName()) {
                        Object[] msg = { "" };
                        if (!CustomerCanBeCalled(user.getCustomer(), msg, "Invite")) {
                            QLog.l().logQUser().debug(
                                    "    --> Trying to recall, served by someone else");

                            //  CM:  Reset current CSR's customer to be null.
                            user.setCustomer(null);
                            return new RpcInviteCustomer(null);
                        }
                    }

                    //                    QLog.l().logQUser().debug("    --> Recall: CSR: " + user.getName()
                    //                            + "; CustCSR: " + user.getCustomer().getUser().getName());
                    user.getCustomer().setState(user.getCustomer().getState());

                    // ??????????? ??????
                    // ?????? ????????????? ???????? ?????
                    invite(user, false);

                    return new RpcInviteCustomer(user.getCustomer());
                }
            }

            // ????? ?? ???????? ????? ? ???? ??????? ?? ?????? ?????????
            QCustomer customer = null;
            int servPriority = -1;// ????????? ?????????? ??? ?????????? ?????
            // ????????????? ?????? ? ????????
            CLIENT_TASK_LOCK.lock();
            try {

                //QLog.l().logQUser().debug("    --> Trying the lock");

                // ????????. ????? ?? ??????. :: It's an abomination. Call by number.
                if (cmdParams.textData != null && !cmdParams.textData.isEmpty()) {
                    final String num = cmdParams.textData.replaceAll("[^\\p{L}+\\d]", "");
                    QLog.l().logger().debug("Warning! Corruption was detected! \"" + num + "\"");
                    for (QService service : QServiceTree.getInstance().getNodes()) {
                        if ((customer = service.gnawOutCustomerByNumber(num)) != null) {
                            QLog.l().logger()
                                .debug("Warning! Corruption was detected! \"" + num + "\"");
                            break;
                        }
                    }
                    if (customer == null) {
                        QLog.l().logQUser().debug("    --> Customer null, returning.");
                        return new RpcInviteCustomer(null);
                    } else {
                        // ?????????? ? ????????, ????? ??????? ?? ?? ????? ??????
                        // We will understand with services, suddenly they called out from their services
                        boolean f = true;
                        for (QPlanService plan : user.getPlanServices()) {
                            if (plan.getService().getId().equals(customer.getService().getId())) {
                                f = false;
                                break;
                            }
                        }
                        if (f) {
                            customer.setService(user.getPlanServices().get(0).getService());
                        }
                    }
                }

                //  CM:  No customer yet.
                if (customer == null) {

                    //  CM:  New code to get all customers in an office.
                    final PriorityQueue<QCustomer> custAll = new PriorityQueue<QCustomer>();

                    //  CM:  Loop through all services the user (CSR) can offer?
                    //  CM:  Judging from number of times loop executes, might be
                    //  CM:  looping through all services all offices offer.
                    for (QPlanService plan : user.getPlanServices()) {

                        //  CM:  Get the next service, only for services user (CSR) can offer? 
                        final QService serv = QServiceTree.getInstance()
                            .getById(plan.getService().getId()); // ????????? ???????
                        //QLog.l().logQUser().debug("TASK_InvNxtCust peekCustomer, Service: " + serv.getName());

                        //  CM:  New code, get all customers (not just one) wanting the current service in office.
                        final PriorityQueue<QCustomer> custSvc = serv.peekAllCustomerByOffice(user.getOffice());
                        if (custSvc.size() != 0) {
                            //QLog.l().logQUser().debug("--> Before add customers");
                            custAll.addAll(custSvc);
                            //QLog.l().logQUser().debug("--> After add customers");
                        }
                    }

                    //  For formatting dates.
                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    
                    //  CM:  Get whether user is q quick txn CSR or not.
                    boolean userQuick = user.getQuickTxn();
                    
                    //  CM:  Initialize nextCust to be null.
                    QCustomer custToServe = null;

                    //  Debug:
                    //QLog.l().logQUser().debug("==> Checking QTxn match");

                    //  CM:  Look for a Quick Txn match that is not part of a sequence.
                    Long dateNow = (new Date()).getTime();
                    Long csrMe = user.getId();
                    //                        QLog.l().logQUser().debug("==> Cust: " + custName + "; Q: " + qms + "; C: "
                    //                                + cms + "; S: " + iss + "; STO: " + istos);

                    //  See if any customer in the queue has their log wait queue flag set.
                    boolean logWaitQueue = false;
                    for (QCustomer nextCustInLine : custAll) {
                        logWaitQueue = logWaitQueue || nextCustInLine.getLogWaitQueue();
                    }

                    //  If any customer has the log wait queue flag set, then log the queue.
                    if (logWaitQueue) {

                        //                        //  Let the user know you're logging the queue.
                        //                        QLog.l().logQUser().debug("==> Next Service, Logging Wait queue");

                        for (QCustomer nextCustInLine : custAll) {
                            //                            QLog.l().logQUser().debug("    --> Cust: " + nextCustInLine.getName()
                            //                                    + "; #: " + nextCustInLine.getId());

                            TrackUserClick("Track: Add, Invite, Begin sequence", "Log Queue", user,
                                    nextCustInLine);
                        }
                    }

                    //  Loop through all customers, looking for a match.
                    for (QCustomer nextCustInLine : custAll) {

                        //  CM:  Check whether in sequence or not.  Special case.
                        String custName = nextCustInLine.getName();
                        Long csrCust = nextCustInLine.getUser().getId();
                        Boolean quickMatch = nextCustInLine.getTempQuickTxn() == userQuick;
                        String qms = (quickMatch ? "Y" : "N");
                        Boolean csrMatch = csrMe == csrCust;
                        String cms = (csrMatch ? "Y" : "N");
                        Boolean inSequence = nextCustInLine.getIsInSequence();
                        String iss = (inSequence ? "Y" : "N");
                        Boolean notInSequence = !inSequence;
                        String nss = (notInSequence ? "Y" : "N");
                        Boolean inSequenceTimeOut = inSequence &&
                                (dateNow - nextCustInLine.getStandTime().getTime()) > 5000;
                        String istos = (inSequenceTimeOut ? "Y" : "N");

                        //                        QLog.l().logQUser().debug("==> Cust: " + custName + "; Q: " + qms + "; C: "
                        //                                + cms + "; S: " + iss + "; STO: " + istos);

                        //  CM:  Look for a Quick Txn match. 
                        //  if (quickMatch && (notInSequence || inSequenceTimeOut || (inSequence
                        //          && csrMatch))) {
                        if ((notInSequence && quickMatch) || (inSequence && csrMatch)
                                || (inSequenceTimeOut)) {

                            // if (nextCustInLine.getTempQuickTxn() == userQuick) {
                            
                            //QLog.l().logQUser().debug("    --> QTxn match with customer");
                            //  CM:  You have a match.  If no next customer, take this one in line.
                            if (custToServe == null) {
                                custToServe = nextCustInLine;
                                //QLog.l().logQUser().debug("        --> First cust chosen: " + nextCust);
                            }
                            
                            //  CM:  You have a match, and a tentative next customer.  See who is next.
                            //  CM:  NOTE!!!  Not taking priority (coefficient) into account here.
                            else {

                                //  Compare customers.
                                //QLog.l().logQUser().debug("        --> Curr Cust : " + nextCust + " Test Next: " + custHere);

                                //  CM:  NOTE!!!  Not taking priority (coefficient) into account here.
                                if (custToServe.compareTo(nextCustInLine) == 1) {
                                    custToServe = nextCustInLine;
                                    //                                    QLog.l().logger().debug("    --> Win:    Cust: " + custToServe
                                    //                                            .getName() + "; Pri: " + custToServe
                                    //                                                    .getPriority().get() + "; Stand: " + df.format(
                                    //                                                            custToServe.getStandTime()) + "; Svc: "
                                    //                                            + custToServe.getService().getName());

                                }
                                //                                else {
                                //                                    QLog.l().logger().debug("    --> Kpt:    Cust: " + custToServe
                                //                                            .getName() + "; Pri: " + custToServe
                                //                                                    .getPriority().get() + "; Stand: " + df.format(
                                //                                                            custToServe.getStandTime()) + "; Svc: "
                                //                                            + custToServe.getService().getName());
                                //                                }
                            }
                        }
                    }

                    //  Debug
                    //                    QLog.l().logQUser().debug("    --> No Q.Txn match, ignoring Q.Txn state");

                    //  CM:  If nextCust is null, no customer in the queue matched USER QuickTxn state.
                    if (custToServe == null) {

                        //  CM:  Pick next customer, regardless of QuickTxn state.
                        for (QCustomer nextCustInLine : custAll) {

                            String custName = nextCustInLine.getName();
                            Long csrCust = nextCustInLine.getUser().getId();
                            Boolean quickMatch = nextCustInLine.getTempQuickTxn() == userQuick;
                            String qms = (quickMatch ? "Y" : "N");
                            Boolean csrMatch = csrMe == csrCust;
                            String cms = (csrMatch ? "Y" : "N");
                            Boolean inSequence = nextCustInLine.getIsInSequence();
                            String iss = (inSequence ? "Y" : "N");
                            Boolean notInSequence = !inSequence;
                            String nss = (notInSequence ? "Y" : "N");
                            Boolean inSequenceTimeOut = inSequence &&
                                    (dateNow - nextCustInLine.getStandTime().getTime()) > 5000;
                            String istos = (inSequenceTimeOut ? "Y" : "N");

                            if (notInSequence || inSequenceTimeOut || (inSequence && csrMatch)) {

                                //  CM:  If no next customer, take the first customer in the list.
                                if (custToServe == null) {
                                    custToServe = nextCustInLine;
                                    // QLog.l().logQUser().debug("        --> First cust chosen: " + nextCust);
                                }

                                //  CM:  You have a tentative next customer.  See who is next.
                                //  CM:  NOTE!!!  Not taking priority (coefficient) into account here.
                                else {

                                    //  Compare customers.
                                    // QLog.l().logQUser().debug("        --> Curr Cust : " + nextCust + " Test Next: " + custHere);

                                    if (custToServe.compareTo(nextCustInLine) == 1) {
                                        custToServe = nextCustInLine;
                                        //                                    QLog.l().logger().debug("    --> Win:    Cust: " + custToServe
                                        //                                            .getName() + "; Pri: " + custToServe
                                        //                                                    .getPriority().get() + "; Stand: " + df.format(
                                        //                                                            custToServe.getStandTime()) + "; Svc: "
                                        //                                            + custToServe.getService().getName());
                                    }
                                }
                            }
                        }
                    }
                    
                    //  Debug
                    if (custToServe == null) {
                        QLog.l().logQUser().debug("--> CSR " + user.getName()
                                        + " No customer found to serve (likely none in queue)");
                    }
                    else {
                        //QLog.l().logQUser().debug("    --> QTxn method next customer: " + nextCust);
                        //  By the time you get here, you should have the next customer in line, if there is one.
                        customer = custToServe;
                        customer.setLogWaitQueue(false);
                        //                        QLog.l().logger().debug("--> CSR: " + user.getName() + "; Cust: "
                        //                                + customer.getName() + "; Svc: " + customer.getService().getName());
                    }

                    //  CM:  Set customer to be QTxn selection, not original selection.
                    //  CM:  Strangeness going on.
                    //QLog.l().logQUser().debug("    --> Before switch: Cust=" + customer + "; Next = " + nextCust);
                    customer = custToServe;
                    //QLog.l().logQUser().debug("    --> After switch:  Cust=" + customer + "; Next = " + nextCust);

                    //  By the time you get here, you should have the next customer in line, if there is one.
                    //                    if (customer != null) {
                    //                        QLog.l().logger().debug("--> Winner:    Cust: " + customer
                    //                                .getName() + "; Pri: " + customer
                    //                                        .getPriority().get() + "; Stand: " + df.format(
                    //                                                customer.getStandTime()) + "; Svc: "
                    //                                + customer.getService().getName());
                    //                    }
                    //?????????? ?????? ??????? ?? ?????? ????????? ????????? ?? ???????? ?????, ??? ???? ?????? ??? ?? ????? ???????.
                    // ??????, ????? ???? ?????????, ?? ????? ??????
                    //??? ??????????? ?????????? ??????? ????????????? ??????? ????????? ??????.

                    //  If no next customer in line, return.
                    if (customer == null) {
                        //QLog.l().logQUser().debug("-->  No final customer selected");
                        return new RpcInviteCustomer(null);
                    }

                    //  CM:  If the customer is already being served, return.
                    Object[] msg = { "" };
                    if (!CustomerCanBeCalled(customer, msg, "Invite")) {
                        return new RpcInviteCustomer(null);
                    }

                    //  CM:  There is a customer.
                    //QLog.l().logQUser().debug("Getting customer");

                    //  CM:  Again, every office polled for the given service (not all services this time).
                    //  CM:  Only people wanting given service in CSR office selected.
                    //  CM:  The polCustomerByOffice same as peekCustomerByOffice, except pol
                    //  CM:  attempts to remove the customer from the queue.
                    // customer = QServiceTree.getInstance()
                    //    .getById(customer.getService().getId())
                    //     .polCustomerByOffice(user.getOffice());

                    //  CM:  Don't use original find/then act on found customer code.
                    //  CM:  Instead, call new code to act on already selected customer.
                    //polCustomerSelected(QCustomer customer)
                    customer = QServiceTree.getInstance().getById(customer.getService().getId()).polCustomerSelected(customer);
                    //QLog.l().logQUser().debug("    --> After polCustSelect:  Cust=" + customer);

                    //  CM:  This should return the same customer as from peekCustomerByOffice.
                    //QLog.l().logQUser().debug("Found him: " + customer);

                    //  CM:  This appears to be unlinking customer from service???  Loop through all services.
                    for (QService service : QServiceTree.getInstance().getNodes()) {
                        //QLog.l().logQUser().debug("Looping for service: " + service);
                        for (QCustomer c : service.getClients()) {
                            //QLog.l().logQUser().debug("Looping through service clients");
                            if (c.getId() == customer.getId()) {
                                // QLog.l().logQUser().debug("Remove customer from service list");
                                service.removeCustomer(c);
                            }
                        }
                    }
                    //QLog.l().logQUser().debug("Done");
                    if (customer == null) {
                        //QLog.l().logQUser().debug("Customer null");
                        return new RpcInviteCustomer(null);
                    }
                }
            } catch (Exception ex) {
                QLog.l().logQUser().debug("Exception: " + ex.getMessage());
                //throw new ServerException("?????? ??? ?????????? ??????? ? ???????" + ex);
                throw new ServerException("Error creating client in the queue: " + ex);
            } finally {
                CLIENT_TASK_LOCK.unlock();
            }
            if (customer == null) {
                throw new ServerException(
                    "???????? ???????? ? ?????????? ????????? ? ????????? ??? ?? ???????.");
            }
            // ????????? ????? ?????????, ???????? ?? ??????.
            customer.setIsInSequence(false);
            user.setCustomer(customer);
            // ???????? ????????? ?????, ??????? ??? ??????.
            customer.setUser(user);
            // ?????? ????? ??????
            customer.setCallTime(new Date());
            // ???????? ????????? ? ????????? "??????????????"
            CustomerState currentState = customer.getState();
            CustomerState newState = currentState == CustomerState.STATE_WAIT
                    ? CustomerState.STATE_INVITED : CustomerState.STATE_INVITED_SECONDARY;
            //            customer.setState(
            //                customer.getState() == CustomerState.STATE_WAIT ? CustomerState.STATE_INVITED
            //                    : CustomerState.STATE_INVITED_SECONDARY);
            customer.setState(newState);
            // set Customer Invite Time
            customer.setInviteTime(new Date());

            // ??? ??? ?????????, ????? ?? ???????? ????????? ?? ?????.
            // ???? ??? ?????? ??? ??????(????????????? ? ?????????? ??????), ?? ??? ?? ????? ?????,
            // ? ????? ?????? ???-?? ??????.
            // ???????? ????????? ????????? ?? ?????????????? ??????, ??? ???? ??? ?? ????? ?????, ?????? ?????????????? ????? ?
            // ????????????? ?? ??????, ??? ?? ?????? ??? ? ????? ?????.
            if (customer.getService().getEnable() != 1) { // ?????? ?? ??????? ??????
                // ????? ?????? ?????? ? ????? ? ??????????.
                customer.setStartTime(new Date());
                // ???????? ????????? ? ????????? "?????? ?????????" ??? "??????????? ??????"
                customer.setState(
                    user.getCustomer().getState() == CustomerState.STATE_INVITED
                        ? CustomerState.STATE_WORK
                        : CustomerState.STATE_WORK_SECONDARY);
            }

            // ???? ???????? ????????, ?? ??? ??????????? ????????? ? ?????
            // ?? ??? ???? ? ?????
            try {
                // ????????? ????????? ????????.
                //QServer.savePool();
                if (customer.getService().getEnable()
                    == 1) { // ?????? ??????? ?????? :: The service requires a call
                    // ????
                    // ?????? ????????????? ???????? ?????
                    //  CM:  Finally, call the customer.
                    invite(user, true);
                }
                //????????? ?????????? ? ???, ??? ?????????? ???????, ????????? ??????? ??????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????
                Uses.sendUDPBroadcast(customer.getService().getId().toString(),
                    ServerProps.getInstance().getProps().getClientPort());
            } catch (Exception ex) {
                QLog.l().logger().error(ex);
            }

            //QLog.l().logQUser().debug("==> End: Task(InvNextCust).process()");

            return new RpcInviteCustomer(customer);
        }

        class MyRun implements Runnable {

            private QUser user;
            private boolean isFrst;

            @Override
            public void run() {
                //QLog.l().logQUser().debug("==> Start: MyRun.run() for Task: TASK_INVITE_NEXT_CUSTOMER");
                final long delta =
                    System.currentTimeMillis() - user.getCustomer().getStandTime().getTime();
                //System.out.println("################## " + QLog.l().getPauseFirst());
                if (delta < QConfig.cfg().getDelayFirstInvite() * 1000) {
                    try {
                        Thread.sleep(QConfig.cfg().getDelayFirstInvite() * 1000 - delta);
                    } catch (InterruptedException ex) {
                    }
                }
                // ??????????? ?????? :: Sound with a sound
                if (user.getCustomer() != null && (
                    user.getCustomer().getState() == CustomerState.STATE_WAIT
                        || user.getCustomer().getState() == CustomerState.STATE_INVITED_SECONDARY
                        || user.getCustomer().getState() == CustomerState.STATE_INVITED
                        || user.getCustomer().getState() == CustomerState.STATE_BACK
                        || user.getCustomer().getState() == CustomerState.STATE_WAIT_AFTER_POSTPONED
                        || user.getCustomer().getState()
                        == CustomerState.STATE_WAIT_COMPLEX_SERVICE)) {
                    SoundPlayer.inviteClient(user.getCustomer().getService(),
                        user.getCustomer().getPrefix() + user.getCustomer().getNumber(),
                        user.getPoint(),
                        isFrst);
                    // ?????? ????????????? ???????? ????? :: Must be highlighted on the main board
                    //QLog.l().logQUser().debug("    --> Before MainBoard...inviteCustomer(user, cust)");
                    MainBoard.getInstance().inviteCustomer(user, user.getCustomer());

//                    QLog.l().logger().debug("CUSTOMER HEREERERERE  _inside loop\n\n" + user.getCustomer() + "\n\n\n");
                }
                usrs.remove(user);

                //QLog.l().logQUser().debug("==> End: MyRun.run() for Task: TASK_INVITE_NEXT_CUSTOMER");
            }
        }
    };
    /**
     * ?????? ?????? ? ?????????? ??????????. Start work with the called customizer.
     */
    final Task getStartCustomerTask = new Task(Uses.TASK_START_CUSTOMER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("==> Start: Task(TASK_START_CUSTOMER)");
            super.process(cmdParams, ipAdress, IP);

            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            // ????? ?????? ?????? ? ????? ? ??????????.
            user.getCustomer().setStartTime(new Date());
            user.getCustomer().setPostponPeriod(0);
            // ???????? ????????? ? ????????? "?????? ?????????" ??? "??????????? ??????"
            user.getCustomer().setState(
                user.getCustomer().getState() == CustomerState.STATE_INVITED
                    ? CustomerState.STATE_WORK
                    : CustomerState.STATE_WORK_SECONDARY);
            //QLog.l().logQUser().debug("getStartCustomerTask MainBoard work");
            MainBoard.getInstance().workCustomer(user);
            // ????????? ????????? ????????.

            //QServer.savePool();

            //QLog.l().logQUser().debug("==> End: Task(TASK_START_CUSTOMER)");
            return new JsonRPC20OK();
        }
    };
    /**
     * ?????? ?????? ? ?????????? ??????????. Start work with the called customizer.
     */
    final Task startCustomerTask = new Task(Uses.TASK_SERVE_CUSTOMER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP,
            QCustomer customer) {
            QLog.l().logQUser().debug("startCustomerTask");
            super.process(cmdParams, ipAdress, IP, customer);

            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            user.setCustomer(customer);

            // ???????? ????????? ?????, ??????? ??? ??????.
            // Delivered the user's custom tool, which caused it.
            customer.setUser(user);

            // ?????? ??? ????? ????. ??????? ????? ?????????? ? ?????????? ?? ????????? ??? ???????? ???????. ????? ? statistic ???????? ????????
            // just got up Tipo. It's not like waiting for a queue. Invnt in the statistic expectation of a huge
//            customer.setStandTime(new Date());

            // ?????? ????? ??????
            // put the call time
            customer.setCallTime(new Date());
            customer.setState(
                customer.getState() == CustomerState.STATE_WAIT ? CustomerState.STATE_INVITED
                    : CustomerState.STATE_INVITED_SECONDARY);

            try {
                // ??????????? ?????? ::: Sound with a sound
                //SoundPlayer.play("/ru/apertum/qsystem/server/sound/sound.wav");
                SoundPlayer.inviteClient(customer.getService(),
                    user.getCustomer().getPrefix() + user.getCustomer().getNumber(),
                    user.getPoint(), true);

                // ????????? ????????? ????????.  ::: Save the state of the queues.
                //QServer.savePool();
                //????????? ?????????? ? ???, ??? ???????? ????????? ??????????
                // ?????? ????????????? ???????? ?????
                // send out an alert that a visitor has been called
                // Must be highlighted on the main display
                QLog.l().logQUser().debug("startCustomerTask MainBoard invite");
                MainBoard.getInstance().inviteCustomer(user, customer);

                //????????? ?????????? ? ???, ??? ??????????? ???????, ????????? ??????? ?????????? ?? ??????????, ?? ??? ?????????? ?????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????
                // send out an alert that the deferred has been called, the status of the queue has changed has not changed, but the pending pool has changed
                // send out broadly by UDP to a specific port
//                Uses.sendUDPBroadcast(Uses.TASK_REFRESH_POSTPONED_POOL, ServerProps.getInstance().getProps().getClientPort());

            } catch (Exception ex) {
                QLog.l().logger().error("Exception at 1036:" + ex);
            }

            return new JsonRPC20OK();
        }
    };
    final Task customerReturnQueueTask = new Task(Uses.TASK_CUSTOMER_RETURN_QUEUE) {
        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("customerReturnQueueTask");
            super.process(cmdParams, ipAdress, IP);
            // ??? ?? ??? ??? ?????? ::: Here he is doing it all
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            // switch to the custodian with parallel reception, must arrive customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers()
                    .get(cmdParams.customerId);
                if (parallelCust == null) {
                    QLog.l().logger()
                        .warn("PARALLEL: User have no Customer for switching by customer ID=\""
                            + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    QLog.l().logger().debug(
                        "???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust
                            .getFullNumber()
                            + "\"");
                }
            }

            final QCustomer customer = user.getCustomer();
            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
//            customer.setWelcomeTime(cmdParams.welcomeTime);

            // Define the customizer in the queue
            customer.setService(service);

            if (service.getLink() != null) {
                customer.setService(service.getLink());
            }

            // the setting time is automatically inserted when creating a customizer.
            // Priority "like everyone else"
            customer.setPriority(cmdParams.priority);

            // The data entered by the customizer
            customer.setTempComments(cmdParams.comments);
            customer.setTempQuickTxn(cmdParams.custQtxn);

            // add the customer to the list
            (service.getLink() != null ? service.getLink() : service).addCustomer(customer);

            customer.setState(CustomerState.STATE_WAIT);

            try {

                //  CM:  Do not persist last customer when returning customer to wait queue.
                user.setCustomer(null);//????? ???? ?? ???????? ????????, ?????????? ? ????
                customer.setUser(null);
//                user.setCustomer(null);//????? ???? ?? ???????? ????????, ?????????? ? ????
//                customer.setUser(null);
//                QPostponedList.getInstance().addElement(customer);
                // ????????? ????????? ????????.
                //QServer.savePool();
                //????????? ?????????? ? ???, ??? ?????????? ???????
//                Uses.sendUDPBroadcast(Uses.TASK_REFRESH_POSTPONED_POOL, ServerProps.getInstance().getProps().getClientPort());
                //????????? ???????????????? ?? UDP ?? ???????????? ????. ?????? ????????????? ???????? ?????
//                MainBoard.getInstance().killCustomer(user);

                Uses.sendUDPBroadcast(service.getId().toString(),
                    ServerProps.getInstance().getProps().getClientPort());

                // ?????? ????????????? ???????? ????? ? ??????? ?????????
                // Must be highlighted on the main scoreboard in the nearest table
                //QLog.l().logQUser().debug("customerReturnQueueTask MainBoard standInf");
                MainBoard.getInstance().customerStandIn(customer);
            } catch (Throwable t) {
                QLog.l().logger().error("return to queue error", t);
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ??????????? ?????????? ?????? ????????? ? ??? ??????????.
     */
    final Task customerToPostponeTask = new Task(Uses.TASK_CUSTOMER_TO_POSTPON) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("customerToPostponeTask");
            super.process(cmdParams, ipAdress, IP);
            // ??? ?? ??? ??? ?????? ::: Here he is doing it all
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            //???????????? ?? ????????? ??? ???????????? ??????, ?????? ???????? customerID
            // switch to the custodian with parallel reception, must arrive customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers()
                    .get(cmdParams.customerId);
                if (parallelCust == null) {
                    QLog.l().logger()
                        .warn("PARALLEL: User have no Customer for switching by customer ID=\""
                            + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    QLog.l().logger().debug(
                        "???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust
                            .getFullNumber()
                            + "\"");
                }
            }
            // ??? ??? ???? ?????????
            final QCustomer customer = user.getCustomer();
            //set User who postponed as added
            customer.setAddedBy(QUserList.getInstance().getById(cmdParams.userId).getName());
            // ??????
            // customer.setPostponedStatus(cmdParams.textData);
            // ?? ??????? ????????. 0 - ?????????
            customer.setPostponPeriod(cmdParams.postponedPeriod);
            // ???? ???????? ????????? ? ????????? ?????, ?? ????? ?????? ?????? ???????????
            customer
                .setIsMine(cmdParams.isMine != null && cmdParams.isMine ? cmdParams.userId : null);
            // ? ???? ?????? ????????? ? ?????????
            //"??? ??? ?????? ????? ? ??? - ? ??? ??????????"
            // ?? ??????? ????????? ????????? ?????? ????? ? ??????????, ???? ????? ????????? ???????? ? ?????? ???????????
            customer.setFinishTime(new Date());
            // ???????? ????????? ? ????????? "?????????????", ?? ?? "?????????"

            customer.setTempComments(cmdParams.comments);
            customer.setTempQuickTxn(cmdParams.custQtxn);
            customer.setState(CustomerState.STATE_POSTPONED);
            try {
                user.setCustomer(null);//????? ???? ?? ???????? ????????, ?????????? ? ????
                customer.setUser(null);
                QPostponedList.getInstance().addElement(customer);
                // ????????? ????????? ????????.
                //QServer.savePool();
                //????????? ?????????? ? ???, ??? ?????????? ???????
                Uses.sendUDPBroadcast(Uses.TASK_REFRESH_POSTPONED_POOL,
                    ServerProps.getInstance().getProps().getClientPort());
                //????????? ???????????????? ?? UDP ?? ???????????? ????. ?????? ????????????? ???????? ?????
                //QLog.l().logQUser().debug("customerToPostponeTask MainBoard kill");
                MainBoard.getInstance().killCustomer(user);
            } catch (Throwable t) {
                QLog.l().logger().error("????????? ??? ?????.", t);
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ????????? ??????????? ????????? ???????
     */
    final Task postponCustomerChangeStatusTask = new Task(Uses.TASK_POSTPON_CHANGE_STATUS) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("postponCustomerChangeStatusTask");
            super.process(cmdParams, ipAdress, IP);
            final QCustomer cust = QPostponedList.getInstance().getById(cmdParams.customerId);
            if (cust != null) {
                cust.setPostponedStatus(cmdParams.textData);
                //????????? ?????????? ? ???, ??? ?????????? ???????, ????????? ??????? ??????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????
                Uses.sendUDPBroadcast(Uses.TASK_REFRESH_POSTPONED_POOL,
                    ServerProps.getInstance().getProps().getClientPort());
                return new JsonRPC20OK();
            } else {
                return new JsonRPC20Error(JsonRPC20Error.ErrorRPC.POSTPONED_NOT_FOUND,
                    cmdParams.customerId);
            }

        }
    };
    /**
     * ????????? ?????? ? ?????????? ??????????. Finish the work with the called customizer.
     */
    final Task getFinishCustomerTask = new Task(Uses.TASK_FINISH_CUSTOMER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {

            //QLog.l().logQUser().debug("==> Start: Task(FinishCust).process()");

            super.process(cmdParams, ipAdress, IP);
            // ??? ?? ??? ??? ??????
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            //???????????? ?? ????????? ??? ???????????? ??????, ?????? ???????? customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers()
                    .get(cmdParams.customerId);
                if (parallelCust == null) {
                    QLog.l().logger()
                        .warn("PARALLEL: User have no Customer for switching by customer ID=\""
                            + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    //QLog.l().logger().debug(
                    //    "???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust
                    //        .getFullNumber()
                    //        + "\"");
                }
            }
            // ??? ??? ???? ?????????
            final QCustomer customer = user.getCustomer();
            final Long CustId = customer.getId();
            
            // ????????
            customer.setTempComments(cmdParams.textData);
            // ???? ?????????? ?? ??????? ?? ???? ???????? ???????? ? ????? ???? ???????.
            final QService backSrv = user.getCustomer().getServiceForBack();

            if (backSrv != null) {
                //QLog.l().logger().debug("????????? ??????? ????? ?????????.");
                // ???????? ?? ?????????? ?????? ????? ??? ??????????
                customer.setFinishTime(new Date());
                // ???????? ????????? ? ????????? "????????", ??? ??? ? ? ???? ????????, ???? ????.
//                customer.setState(CustomerState.STATE_BACK, backSrv.getId());
                // ??????????? ????????? ? ??????? ? ?????? ????????
                backSrv.addCustomer(customer);
                // ???? ????????? ???????????? ??. ???????

                // ???????? ????????? ?????, ??????? ??? ??????.
                // ???? ? ?????? ??????? ???????? ??????
                customer.setUser(null);
                // ?????? ????? ? ????? ??????.
                customer.setService(backSrv);

                //????????? ?????????? ? ???, ??? ???????? ?????????? ????? ?????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????
                Uses.sendUDPBroadcast(backSrv.getId().toString(),
                    ServerProps.getInstance().getProps().getClientPort());
                //QLog.l().logger().info(
                //    "?????? \"" + user.getCustomer().getPrefix() + user.getCustomer().getNumber()
                //        + "\" ????????? ? ?????? \"" + backSrv.getName() + "\"");
            } else {
                //QLog.l().logger().debug("? ???? ????????.");

                // ? ???? ?????? ????????? ? ?????????
                //"??? ??? ?????? ????? ? ??? - ????"
                // ?? ??????? ????????? ????????? ?????? ????? ? ??????????, ???? ????? ????????? ???????? ? ?????? ???????????
                // ????? ???????? -1 ???? ????????? ?? ??????????
                final QResult result;
                if (cmdParams.resultId != -1) {
                    result = QResultList.getInstance().getById(cmdParams.resultId);
                } else {
                    result = null;
                }
                ((QCustomer) customer).setResult(result);
                customer.setFinishTime(new Date());
                // ???????? ????????? ? ????????? "?????????????", ?? ?? "?????????"
//                customer.setState(CustomerState.STATE_FINISH);

                if (cmdParams.inAccurateFinish) {
                    customer.setState(CustomerState.STATE_INACCURATE_TIME);
                } else {
                    // ???????? ????????? ? ????????? "?????????????", ?? ?? "?????????"
                    customer.setState(CustomerState.STATE_FINISH);
                }

                // ???? ?????, ???????? ????? ???? ?? ?????? ?????, ?.?. ???? ????? ?????, ???? ????????? ??????, ? ??????? ??? ????? ?? ?????? ?????
                // ??? ????????? ????? ??? ??? ??????? ?? ?????? ??????????? ?????, ???? ? ???? ?????? ??? ????
                // ???? ??????? ?? ? ???? ?????? ?????? STATE_WAIT_COMPLEX_SERVICE
                // ???? ???, ?? ?????? STATE_FINISH ? ?????? ? ??????????? ?? ????
                // ???????? ? ?????? ?????? ??? ????? ? ??????? IChangeCustomerStateEvent
                // ???? ??? ?? ????????? ?????????? ? ????????? ??????.
                if (customer.getState() == CustomerState.STATE_FINISH
                    && customer.getComplexId() != null) {
                    int len = 0;
                    len = customer.getComplexId().stream().map((li) -> li.size())
                        .reduce(len, Integer::sum);
                    if (len != 0) {
                        //QLog.l().logger()
                        //    .debug("????????? ???????? ?? ??????????? ???????. ???????? " + len);
                        Long serviceID = null;
                        for (LinkedList<LinkedList<Long>> ids : customer.getComplexId()) {
                            for (LinkedList<Long> id : ids) {
                                serviceID = id.getFirst();
                                ids.remove(id);
                                break;
                            }
                            if (serviceID != null) {
                                break;
                            }
                        }
                        final QService nextServ = QServiceTree.getInstance().getById(serviceID);
                        nextServ.addCustomer(customer);
                        customer.setService(nextServ);
                        customer.setState(CustomerState.STATE_WAIT_COMPLEX_SERVICE);
                    }
                }
                // ???? ?? ??????? ???????? ?? ?????, ?? ?????????? ????????? ?????? ??? ? ??? ??????
                if (customer.getState() == CustomerState.STATE_WAIT_COMPLEX_SERVICE) {
                    //????????? ?????????? ? ???, ??? ???????? ?????????? ????? ?????????
                    //????????? ???????????????? ?? UDP ?? ???????????? ????
                    Uses.sendUDPBroadcast(customer.getService().getId().toString(),
                        ServerProps.getInstance().getProps().getClientPort());
                    //QLog.l().logger().info("?????? \"" + customer.getPrefix() + customer.getNumber()
                    //    + "\" ???????? ?? ????? ? ?????? \"" + customer.getService().getName()
                    //    + "\"");
                }
            }

            //  CM:  Save customer before setting to null.
            QCustomer savedCustomer = customer;

            try {
                user.setCustomer(null);//????? ???? ? ???????? ?? ????????
                // ????????? ????????? ????????.
                //QServer.savePool();
                //????????? ?????????? ? ???, ??? ?????????? ????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????. ?????? ????????????? ???????? ?????
                //QLog.l().logQUser().debug("    --> Before finish customer, MainBoard....killCustomer(user)");
                MainBoard.getInstance().killCustomer(user);
            } catch (Exception ex) {
                QLog.l().logger().error(ex);
            }

            //QLog.l().logQUser().debug("==> End: Task(FinishCust).process()");

            //  CM:  Call John's MySql stored procedure.
            CallStoredProcDone(user, savedCustomer, CustId, "Customer finished");

            //QLog.l().logQUser().debug("==> End: Task(TskFinCust).process()");

            return new RpcStandInService(customer);
        }
    };

    private void CallStoredProcDone(QUser user, QCustomer customer, Long custId, String from) {

        //  Debug.
        //QLog.l().logQUser().debug("==> Start: CallStoredProcDone(" + custId + ", " + from + ")");

        //  CM:  Initialize variables.
        int ReturnCode = -1;
        int sqlErrorNo = -2;
        String ErrorMsg = "You should not see this message.";
        String ProcMsg = "You should not see this either.";
        String UserMsg = "";
        String UserName = "User not logged in";
        String TicketName = "Not known";
        Connection conn = null;
        CallableStatement cStmt = null;

        //  CM:  Calling John's MySql stored procedure.
        try {
            String Sql = "{call load_client_visit(?, ?, ?, ?)}";

            //  CM:  See if you're getting the right info.
            //QLog.l().logQUser().debug("    --> Service: " + URL + "; DB: " + MyDB + "; User: " + MyUser + "; Pw: " + MyPw);
            //QLog.l().logQUser().debug("    --> Cust Id: " + custId + "; Sql: " + Sql);

            conn = DriverManager.getConnection(URL, MyUser, MyPw);
            cStmt = conn.prepareCall(Sql);
            cStmt.setLong(1, custId);
            cStmt.setInt(2, ReturnCode);
            cStmt.setInt(3, sqlErrorNo);
            cStmt.setString(4, ProcMsg);
            cStmt.registerOutParameter(2, Types.INTEGER);
            cStmt.registerOutParameter(3, Types.INTEGER);
            cStmt.registerOutParameter(4, Types.VARCHAR);

            //  The actual call statement.
            cStmt.execute();

            //  Try getting return code.
            ReturnCode = cStmt.getInt(2);
            sqlErrorNo = cStmt.getInt(3);
            ProcMsg = cStmt.getString(4);

            //  See what the return code was.
            //QLog.l().logQUser().debug("    --> Code before: " + RetBefore + "; RC var: " + ReturnCode + "; RC get: " + RetCall);
            
            //  See if an error or not.
            if (ReturnCode == 0) {
                ErrorMsg = "All OK.  No error.";
            }
            else {
                ErrorMsg = "Error executing load_client_visit stored procedure.";
            }
        }

        //  CM:  Catch any error trying to call the stored procedure.
        catch (Exception ex) {
            QLog.l().logQUser().debug("    --> Exception: " + ex.getMessage());
            ReturnCode = -2;
            ErrorMsg = "Error trying to call load_client_visit stored procedure.";
        }

        //  CM:  If any error, handle it.
        finally {
            
            //  Close the callable statement and connection.
            if (cStmt != null) {
                try {
                    cStmt.close();
                }
                catch (Exception ex) {
                    QLog.l().logQUser().debug("    --> Exception closing JDBC callable statement: "
                            + ex.getMessage());
                }
            }
            if (conn != null) {
                try {
                conn.close();
                }
                catch (Exception ex) {
                    QLog.l().logQUser().debug("    --> Exception closing JDBC connection: " + ex
                            .getMessage());
                }
            }
            
            //QLog.l().logQUser().debug("    --> Finally: Code =  " + ReturnCode + "; ErrMsg = " + ErrorMsg);
        }

        //  CM:  Create standard error message.
        if (user.getName() != null) {
            UserName = user.getName();
        }
        if (customer != null) {
            TicketName = customer.getName();
        }

        //  CM:  Test message only.
        //ReturnCode = 999;

        UserMsg = ErrorMsg
                + "\nOffice Name: " + user.getOffice().getName()
                + "\nCSR Name: " + UserName
                + "\nTicket Number: " + TicketName
                + "\nCustomer ID: " + custId.toString()
                + "\nHappened When: " + from
                + "\nStored Procedure Return Code: " + ReturnCode
                + "\nMySql Error Code: " + sqlErrorNo
                + "\nMySql Error Message:  " + ProcMsg;

        //  Debug.
        //QLog.l().logQUser().debug("==> End: CallStoredProcDone(" + custId + ", " + from + ")");
        //        QLog.l().logQUser().debug("==> StoreProc CustId: " + custId.toString() + "; RC: "
        //                + ReturnCode + "; SqlC: " + sqlErrorNo + "; RM: " + ProcMsg);

        //QLog.l().logQUser().debug("    --> Error Message: " + UserMsg);

        //  CM:  Send a Slack message.
        if (ReturnCode != 0) {
            SendSlackMessage(user, customer, UserMsg);
            //SendEmailMessage(user, customer, UserMsg);
        }
    }

    public void SendSlackMessage(QUser user, QCustomer customer, String errorMessage) {

        //  Only call Slack if the Slack hook variable is set.
        if (!slackHookUrl.isEmpty()) {

            //  CM:  Set slack variables.
            String CSRIcon = ":information_desk_person:";
            String Username = "";
            SlackApi api = new SlackApi(slackHookUrl);
            SlackMessage slackMsg = new SlackMessage(null);

            //  Try and get user name.
            if (user.getName() != null) {
                Username = "CSR - " + user.getName();
            }
            else {
                Username = "User is not logged in";
            }

            slackMsg.setIcon(CSRIcon);
            slackMsg.setText(errorMessage);
            slackMsg.setUsername(Username);
            api.call(slackMsg);
        }
    }

    //    public void SendEmailMessage(QUser user, QCustomer customer, String errorMessage) {
    //
    //        //  Test of config info.  Initialize variables.
    //        FileBasedConfiguration config = QConfig.cfg().getQsysProperties();
    //        String currentKey = "";
    //        String currentValue = "";
    //
    //        Iterator<String> keys = config.getKeys();
    //
    //        while (keys.hasNext()) {
    //            currentKey = keys.next();
    //            currentValue = config.getString(currentKey);
    //            //QLog.l().logger().debug("    --> Key: " + currentKey + "; Value: " + currentValue);
    //        }
    //
    //        //  CM:  Try another way.  Note: No config.properties, so nothing here.
    //        Properties props = new Properties();
    //        props.put("mail.smtp.host", config.getString("SMTPServer"));
    //        Enumeration propKeys = props.keys();
    //        while (propKeys.hasMoreElements()) {
    //            currentKey = (String) propKeys.nextElement();
    //            currentValue = props.getProperty(currentKey);
    //            //QLog.l().logger().debug("    --> PKey: " + currentKey + "; PValue: " + currentValue);
    //        }
    //
    //        //  CM:  Add some properties, try to send a message.
    //        Session session = Session.getDefaultInstance(props, null);
    //        //session.setDebug(true);
    //        Message msg = new MimeMessage(session);
    //        try {
    //            msg.setFrom(new InternetAddress(config.getString("Sender")));
    //            msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] {
    //                    new InternetAddress(config.getString("Developers")) });
    //            msg.setSubject(
    //                    "SBC-QSystem Error: Could not write summary statistics records for client "
    //                            + customer.getId());
    //            msg.setText(errorMessage);
    //            Transport.send(msg);
    //        }
    //        catch (Exception ex) {
    //            QLog.l().logger().debug("    --> Email exception: " + ex.getMessage());
    //        }
    //    }

    public void TrackUserClick(String clickButton, String beforeAfter, QUser user,
            QCustomer customer) {

        //  CM:  Initialize variables.
        Connection conn = null;
        PreparedStatement pStmt = null;
        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Date dateNow = new Date();
        String timeNow = df.format(dateNow);
        Long officeId = 0L;
        Long userId = 0L;
        Boolean userQuick = false;
        QCustomer cust = null;
        Long custId = 0L;
        String ticket = "";
        Long serviceId = 0L;
        Integer state = -1;
        Boolean custQuick = false;
        String custChannel = "";
        String custQuantity = "1";
        int custPriority = 1;
        Long custUserId = 0L;
        Boolean custUserQTxn = false;

        //  CM:  If user not null, get good values.
        if (user != null) {
            officeId = user.getOffice().getId();
            userId = user.getId();
            userQuick = user.getQuickTxn();

            //  CM:  If input customer null, get it from CSR.
            if (customer == null) {
                cust = user.getCustomer();
            }
            else {
                cust = customer;
            }

            //  CM:  If cust not null, get info from it.
            if (cust != null) {
                custId = cust.getId();
                ticket = cust.getPrefix() + Long.toString(cust.getNumber());
                serviceId = cust.getService().getId();
                state = cust.getStateIn();
                custQuick = cust.getTempQuickTxn();
                custChannel = cust.getChannels();
                custQuantity = cust.getQuantity();

                //  If customer has a priority, get it.
                if (cust.getPriority() != null) {
                    custPriority = cust.getPriority().get();
                }

                //  If CSR serving customer not null, track it too.
                if (cust.getUser() != null) {
                    custUserId = cust.getUser().getId();
                    custUserQTxn = cust.getUser().getQuickTxn();
                }
            }
        }

        //  CM:  Debug.
        //        QLog.l().logQUser().debug("==> Start Track: Time: " + timeNow + "; B: " + clickButton
        //                + "; SF: " + beforeAfter + "; Off: " + officeId + "; Usr: " + userId + "; CId: "
        //                + custId + "; T: " + ticket + "; SId: " + serviceId + "; State: " + state);

        //  CM:  Try writing data to the database.
        try {
            //  CM:  Create connection, prepare statement.
            conn = DriverManager.getConnection(URL, MyUser, MyPw);
            pStmt = conn.prepareStatement(SqlInsertStatement);

            //            private static String SqlInsertStatement =
            //                    "INSERT INTO trackactions (time_now, button_clicked, start_finish, " +
            //                            "office_id, user_id, client_id, ticket, service_id, state_in) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            //  CM:  Set parameters.
            pStmt.setTimestamp(1, Timestamp.valueOf(timeNow));
            pStmt.setString(2, clickButton);
            pStmt.setString(3, beforeAfter);
            pStmt.setLong(4, officeId);
            pStmt.setLong(5, userId);
            pStmt.setLong(6, custId);
            pStmt.setString(7, ticket);
            pStmt.setLong(8, serviceId);
            pStmt.setInt(9, state);
            pStmt.setBoolean(10, userQuick);
            pStmt.setBoolean(11, custQuick);
            pStmt.setString(12, custChannel);
            pStmt.setString(13, custQuantity);
            pStmt.setInt(14, custPriority);
            pStmt.setLong(15, custUserId);
            pStmt.setBoolean(16, custUserQTxn);
            pStmt.executeUpdate();
            //  Autocommit seems to be on, can't call commmit.
            //conn.commit();
        }

        //  CM:  Catch any error trying to call the stored procedure.
        catch (Exception ex) {
            QLog.l().logQUser().debug("    --> Exception tracking: " + ex.getMessage());
        }

        //  CM:  If any error, handle it.
        finally {

            //  Close the callable statement and connection.
            if (pStmt != null) {
                try {
                    pStmt.close();
                }
                catch (Exception ex) {
                    QLog.l().logQUser().debug(
                            "    --> Exception closing JDBC track prepared statement: "
                                    + ex.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (Exception ex) {
                    QLog.l().logQUser().debug("    --> Exception closing JDBC track connection: "
                            + ex
                                    .getMessage());
                }
            }

            //QLog.l().logQUser().debug("    --> Finally: Code =  " + ReturnCode + "; ErrMsg = " + ErrorMsg);
        }
    }

    /**
     * ?????????????? ??????? ? ?????? ??????. Forward the client to another service.
     */
    final Task redirectCustomerTask = new Task(Uses.TASK_REDIRECT_CUSTOMER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("redirectCustomerTask");
            super.process(cmdParams, ipAdress, IP);
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            // switch to the custodian with parallel reception, must arrive customerID
            if (cmdParams.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers()
                    .get(cmdParams.customerId);
                if (parallelCust == null) {
                    QLog.l().logger()
                        .warn("PARALLEL: User have no Customer for switching by customer ID=\""
                            + cmdParams.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    QLog.l().logger().debug(
                        "???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust
                            .getFullNumber()
                            + "\"");
                }
            }
            final QCustomer customer = user.getCustomer();
            // ???????? ?? ?????????
            customer.setTempComments(cmdParams.comments);
            customer.setIsInSequence(cmdParams.in_sequence);
            customer.setLogWaitQueue(cmdParams.log_waitqueue);

            // set added by which user
            customer.setAddedBy(QUserList.getInstance().getById(cmdParams.userId).getName());

            final QService oldService = customer.getService();
            final QService newServiceR = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QService newService = newServiceR.getLink() != null ? newServiceR.getLink() : newServiceR;
            customer.setFinishTime(new Date());
            final QResult result;
            if (cmdParams.resultId != -1) {
                result = QResultList.getInstance().getById(cmdParams.resultId);
            } else {
                result = null;
            }
            customer.setResult(result);
            //Set the state to finish to get an accurate DB result in statistic
            customer.setState(CustomerState.STATE_FINISH, cmdParams.serviceId);

            //Now update the customer to the new state
            customer.setService(newService);
            customer.setState(CustomerState.STATE_REDIRECT, cmdParams.serviceId);
            //QLog.l().logQUser().debug("==> Task(RedirectCust): setState(Redirect)");
            customer.setPreviousList(oldService);

            if (!oldService.getId().equals(cmdParams.serviceId)) {
                //                QLog.l().logQUser().debug("==> NxtSvc diff: OId: " + oldService.getId()
                //                        + "; O: " + oldService.getName()
                //                        + "; NId: " + cmdParams.serviceId);
                //                QLog.l().logQUser().debug("    --> NPriB: " + customer.getPriority().get());
                //                QLog.l().logQUser().debug("    --> Hi priority set as per normal");
                customer.setPriority(Uses.PRIORITY_HI);
                //                QLog.l().logQUser().debug("    --> NPriA: " + customer.getPriority().get());
            }
            else {
                //                QLog.l().logQUser().debug("==> NxtSvc same: OId: " + oldService.getId()
                //                        + "; O: " + oldService.getName()
                //                        + "; NId: " + cmdParams.serviceId);
                //                QLog.l().logQUser().debug("    --> NPriB: " + customer.getPriority().get());
                //                QLog.l().logQUser().debug("    --> Hi priority set anyway");
                customer.setPriority(Uses.PRIORITY_HI);
                //                QLog.l().logQUser().debug("    --> NPriA: " + customer.getPriority().get());
            }
            customer.setStandTime(new Date());
            newService.addCustomer(customer);

            try {
                customer.save();
                Uses.sendUDPBroadcast(newService.getId().toString(), ServerProps.getInstance().getProps().getClientPort());
                Uses.sendUDPBroadcast(oldService.getId().toString(), ServerProps.getInstance().getProps().getClientPort());
                //QLog.l().logQUser().debug("redirectCustomerTask MainBoard kill");
                MainBoard.getInstance().killCustomer(user);
            } catch (Exception ex) {
                QLog.l().logger().error(ex);
            }

            return new JsonRPC20OK();
        }
    };
    /**
     * ???????? ?????? ???????????? ?? ??????? ?? ???????. ??? ?????????? ???? ???????. Binding the
     * service to the user on a hot on command. This is the handler for this command.
     */
    final Task setServiceFire = new Task(Uses.TASK_SET_SERVICE_FIRE) {

        @Override
        synchronized public RpcGetSrt process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("setServiceFire");
            super.process(cmdParams, ipAdress, IP);
            if (cmdParams.userId == null || cmdParams.serviceId == null) {
                return new RpcGetSrt("???????? ??????????? ???????.");
            }
            if (!QServiceTree.getInstance().hasById(cmdParams.serviceId)) {
                return new RpcGetSrt(
                    "????????? ?????? ?? ???????????? ? ??????? ??????????? ???????????? ???????.");
            }
            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
            if (!QUserList.getInstance().hasById(cmdParams.userId)) {
                return new RpcGetSrt(
                    "????????? ???????????? ?? ???????????? ? ??????? ??????????? ???????????? ???????.");
            }
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);

            if (user.hasService(cmdParams.serviceId)) {
                return new RpcGetSrt("????????? ?????? ??? ????????? ????? ????????????.");
            }
            user.addPlanService(service, cmdParams.coeff);
            //????????? ?????????? ? ???, ??? ? ???????????? ?????????? ???????????? ?????
            //????????? ???????????????? ?? UDP ?? ???????????? ????
            Uses.sendUDPBroadcast(String.valueOf(cmdParams.userId),
                ServerProps.getInstance().getProps().getClientPort());
            return new RpcGetSrt(
                "?????? \"" + cmdParams.serviceId + "\" ????????? ???????????? \""
                    + cmdParams.userId
                    + "\" ???????.");
        }
    };
    /**
     * ???????? ???????? ?????? ???????????? ?? ??????? ?? ???????. ??? ?????????? ???? ???????.
     */
    final Task deleteServiceFire = new Task(Uses.TASK_DELETE_SERVICE_FIRE) {

        @Override
        synchronized public RpcGetSrt process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("deleteServiceFire");
            super.process(cmdParams, ipAdress, IP);
            if (cmdParams.userId == null || cmdParams.serviceId == null) {
                return new RpcGetSrt("???????? ??????????? ???????.");
            }
            if (!QServiceTree.getInstance().hasById(cmdParams.serviceId)) {
                return new RpcGetSrt(
                    "????????? ?????? ?? ???????????? ? ??????? ??????????? ???????????? ???????.");
            }
            if (!QUserList.getInstance().hasById(cmdParams.userId)) {
                return new RpcGetSrt(
                    "????????? ???????????? ?? ???????????? ? ??????? ??????????? ???????????? ???????.");
            }
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);

            if (!user.hasService(cmdParams.serviceId)) {
                return new RpcGetSrt("????????? ?????? ?? ????????? ????? ????????????.");
            }
            user.deletePlanService(cmdParams.serviceId);
            //????????? ?????????? ? ???, ??? ? ???????????? ?????????? ???????????? ?????
            //????????? ???????????????? ?? UDP ?? ???????????? ????
            Uses.sendUDPBroadcast(String.valueOf(cmdParams.userId),
                ServerProps.getInstance().getProps().getClientPort());
            return new RpcGetSrt(
                "?????? \"" + cmdParams.serviceId + "\" ??????? ? ???????????? \""
                    + cmdParams.userId
                    + "\" ???????.");
        }
    };
    /**
     * ???????? ????????? ??????????? ?????? ??? ???????? Change the temporary availability of the
     * service to provide
     */
    final Task changeTempAvailableService = new Task(Uses.TASK_CHANGE_TEMP_AVAILABLE_SERVICE) {

        @Override
        synchronized public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("changeTempAvailableService");
            super.process(cmdParams, ipAdress, IP);
            if (!QServiceTree.getInstance().hasById(cmdParams.serviceId)) {
                return new JsonRPC20Error();
            }
            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
            service.setTempReasonUnavailable(cmdParams.textData);
            QLog.l().logger()
                .trace("???????? ??????????? ??? ?????? \"" + service.getName() + "\" - "
                    + (cmdParams.textData == null || cmdParams.textData.isEmpty() ? "????????"
                    : ("?? ????????, ??????? '" + cmdParams.textData + "'")));
            return new JsonRPC20OK();
        }
    };
    /**
     * ????????? ???????????? ???????? ????? - ?? ??? ??????. ??? XML-???? ??????? ? ?????
     * ?????????? mainboard.xml
     */
    final Task getBoardConfig = new Task(Uses.TASK_GET_BOARD_CONFIG) {

        @Override
        public RpcGetSrt process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getBoardConfig");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetSrt(MainBoard.getInstance().getConfig().asXML());
        }
    };
    /**
     * ?????????? ???????????? ???????? ????? - ?? ??? ??????. ??? XML-???? ??????? ? ?????
     * ?????????? mainboard.xml
     */
    final Task saveBoardConfig = new Task(Uses.TASK_SAVE_BOARD_CONFIG) {

        @Override
        synchronized public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("saveBoardConfig");
            super.process(cmdParams, ipAdress, IP);
            try {
                MainBoard.getInstance()
                    .saveConfig(DocumentHelper.parseText(cmdParams.textData).getRootElement());
            } catch (DocumentException ex) {
                QLog.l().logger().error("?? ??????????? ???????????? ?????.", ex);
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ????????? ??????? ?????????? ????? ???????? ?? ????. Getting a table of previously recorded
     * customers for the day.
     */
    final Task getGridOfDay = new Task(Uses.TASK_GET_GRID_OF_DAY) {

        @Override
        public RpcGetGridOfDay process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getGridOfDay");
            super.process(cmdParams, ipAdress, IP);
            //????????? ??????
            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QSchedule sch1 = service.getCalendar() == null
                ? QCalendarList.getInstance().getById(1).getSpecSchedule(new Date(cmdParams.date))
                : (service.getCalendar().getSpecSchedule(new Date(cmdParams.date)));
            final QSchedule sch = (sch1 == null ? service.getSchedule() : sch1);

            final RpcGetGridOfDay.GridDayAndParams advCusts = new RpcGetGridOfDay.GridDayAndParams();
            advCusts.setAdvanceLimit(service.getAdvanceLimit());
            if (sch == null) {
                return new RpcGetGridOfDay(advCusts);
            }

            final GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date(cmdParams.date));
            gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
            gc.set(GregorianCalendar.MINUTE, 0);
            final Date startDay = gc.getTime();
            gc.set(GregorianCalendar.HOUR_OF_DAY, 23);
            gc.set(GregorianCalendar.MINUTE, 59);
            final Date endDay = gc.getTime();

            // ????????? ?? ????????? ??????? ?? ????.
            // ????????? ????? ???? ???, ????? ? id=1 ? ??????????? ???????????
            // ???? ???? ???????????? ??? ????????(???????????? ? ?? ? ??????? ???????? ????), ?? ????????? ? ?????????? ???
            if (!QCalendarList.getInstance().getById(1).checkFreeDay(startDay)
                && !(service.getCalendar() != null
                && service.getCalendar().checkFreeDay(startDay))) {

                // ????????? ????? ?????? ? ????? ?????? ?? ???? ????
                final QSchedule.Interval interval = sch.getWorkInterval(gc.getTime());

                // ???? ???????? ? ???? ???? ?? ????????? ???? ?? ??????? ??? ????? ??????????
                if (!(interval.start == null || interval.finish == null)) {
                    // ??????? ?? ???????? ???? ??? ?.?. ??? ??? ?? ???????? ?? ?????e ???????????
                    // ????? ????? ? ???? ??????. ??? ??? ???????????? ????????? ??????, ??? ??????????? ????????? ??????? ?? ???????? ??????,
                    // ??? ???????? ??????????? ????? ??? ?????? ????????????
                    gc.setTime(interval.start);
                    gc.add(GregorianCalendar.MINUTE, service.getAdvanceTimePeriod());
                    Date start = gc.getTime();
                    gc.setTime(interval.finish);
                    gc.add(GregorianCalendar.MINUTE, -service.getAdvanceTimePeriod());
                    Date end = gc.getTime();

                    QLog.l().logger().trace(
                        "???????? ??? ??????? ??????? ????? ??????????? ??????????? ?? "
                            + Uses.FORMAT_FOR_REP
                            .format(startDay) + " ?? " + Uses.FORMAT_FOR_REP.format(endDay));
                    // ???????? ??? ??????? ??????? ????? ??????????? ???????????
                    final List<QAdvanceCustomer> advCustomers = Spring.getInstance().getHt().find(
                        "FROM QAdvanceCustomer a WHERE advance_time >'" + Uses.FORMAT_FOR_REP
                            .format(startDay)
                            + "' and advance_time <= '" + Uses.FORMAT_FOR_REP.format(endDay)
                            + "' and service_id = " + service.getId());

                    // ????? ?? ????? ?????? ???
                    while (start.before(end) || start.equals(end)) {
                        // ???????? ?? ???????. ? ????????? ??? ??????????? ????????????, ?? ????? ??? ????? ?? ?????? ? ????? ???????????
                        gc.setTime(start);
                        gc.add(GregorianCalendar.MINUTE, service.getAdvanceTimePeriod() - 3);
                        if (!sch.inBreak(start, gc.getTime())) { // ????? ?? ?????? ? ???????
                            int cnt = 0;

                            gc.setTime(start);
                            gc.set(GregorianCalendar.SECOND, 0);
                            gc.set(GregorianCalendar.MILLISECOND, 0);
                            RpcGetGridOfDay.AdvTime atime = new RpcGetGridOfDay.AdvTime(
                                gc.getTime()); //??? ??? ????, ??????? ?????????? ? ???????? ?????????? ???????

                            // ????????? ?? ?????????? ??????????
                            for (QAdvanceCustomer advCustomer : advCustomers) {
                                gc.setTime(start);
                                final int s = gc.get(GregorianCalendar.HOUR_OF_DAY);
                                final int s_m = gc.get(GregorianCalendar.MINUTE);
                                gc.setTime(advCustomer.getAdvanceTime());
                                final int e = gc.get(GregorianCalendar.HOUR_OF_DAY);
                                final int e_m = gc.get(GregorianCalendar.MINUTE);
                                // ???? ?????? ???? ? ??? ? ??????, ?? ???????? ??????? ???????????? ?? ???? ??? ? ??????
                                // ??? ????????? ??? ??????? ????? ???????????????? ????? ???? ?? ?? ?????, ? ??????? ????????? ?????.
                                if (s * 60 + s_m <= e * 60 + e_m
                                    && s * 60 + s_m + service.getAdvanceTimePeriod()
                                    > e * 60 + e_m) {
                                    cnt++;
                                    atime.addACustomer(advCustomer);
                                    // ?????? ?? ???? ????? ???? ? ????? ?????? ?? ????????? ?????????????? ? ???? ?????? ?? ???? ????.
                                    // ?????? ?????.???????? ?? ?????? ???? ????? ?? ??? ? ???????????????? ?? ???? ???
                                    if (cmdParams.customerId != null && cmdParams.customerId != -1
                                        && advCustomer.getAuthorizationCustomer() != null
                                        && advCustomer.getAuthorizationCustomer().getId() != null
                                        && advCustomer.getAuthorizationCustomer().getId()
                                        .equals(cmdParams.customerId)) {
                                        cnt = 1999999999;
                                        break;
                                    }
                                }
                            }
                            // ???? ??? ?????????? ???????????? ?? ????????? ??????????? ?? ??????, ?? ??????? ???? ??? ??? ????????? ??? ??????
                            for (int i = cnt; i < service.getAdvanceLimit(); i++) {
                                atime.addACustomer(new QAdvanceCustomer(0L));
                            }
                            advCusts.addTime(atime);
                        } // ?? ? ???????? ? ?? ????? ????????? ??????? ??? ??? ?????? ? ?? ????????? ?? ????? ????????? ???????????

                        // ???????? ?? ????????? ??????
                        gc.setTime(start);
                        gc.add(GregorianCalendar.MINUTE, service.getAdvanceTimePeriod());
                        start = gc.getTime();
                    }

                }

            }
            return new RpcGetGridOfDay(advCusts);
        }
    };
    /**
     * ????????? ??????? ?????????? ????? ???????? ?? ??????. Getting a table of previously recorded
     * customers for a week.
     */
    final Task getGridOfWeek = new Task(Uses.TASK_GET_GRID_OF_WEEK) {

        @Override
        public RpcGetGridOfWeek process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getGridOfWeek");
            super.process(cmdParams, ipAdress, IP);
            //????????? ??????
            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
            QSchedule sch = service.getSchedule();
            if (sch == null) {
                return new RpcGetGridOfWeek(
                    new RpcGetGridOfWeek.GridAndParams("????????? ?????? ?? ????? ??????????."));
            }

            final Date startWeek = new Date(cmdParams.date);
            final GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(startWeek);
            gc.set(GregorianCalendar.DAY_OF_YEAR, gc.get(GregorianCalendar.DAY_OF_YEAR) + 7);
            final Date endWeek = gc.getTime();

            QLog.l().logger().trace(
                "???????? ??? ??????? ??????? ????? ??????????? ??????????? ?? "
                    + Uses.FORMAT_FOR_REP
                    .format(startWeek) + " ?? " + Uses.FORMAT_FOR_REP.format(endWeek));
            // ???????? ??? ??????? ??????? ????? ??????????? ???????????
            final List<QAdvanceCustomer> advCustomers = Spring.getInstance().getHt().find(
                "FROM QAdvanceCustomer a WHERE advance_time >'" + Uses.FORMAT_FOR_REP
                    .format(startWeek)
                    + "' and advance_time <= '" + Uses.FORMAT_FOR_REP.format(endWeek)
                    + "' and service_id = " + service.getId());

            final GridAndParams advCusts = new GridAndParams();
            advCusts.setStartTime(ServerProps.getInstance().getProps().getStartTime());
            advCusts.setFinishTime(ServerProps.getInstance().getProps().getFinishTime());
            advCusts.setAdvanceLimit(service.getAdvanceLimit());
            advCusts.setAdvanceTimePeriod(service.getAdvanceTimePeriod());
            advCusts.setAdvanceLimitPeriod(
                service.getAdvanceLimitPeriod() == null ? 0 : service.getAdvanceLimitPeriod());
            // ?????????? ?????? ????????? ??????
            Date day = startWeek;
            while (day.before(endWeek)) {
                final GregorianCalendar gc_day = new GregorianCalendar();
                gc_day.setTime(day);
                // ????????? ?? ????????? ??????? ?? ????.
                // ????????? ????? ???? ???, ????? ? id=1 ? ??????????? ???????????
                // ???? ???? ???????????? ??? ????????(???????????? ? ?? ? ??????? ???????? ????), ?? ????????? ? ?????????? ???
                if (!QCalendarList.getInstance().getById(1).checkFreeDay(day)
                    && !(service.getCalendar() != null
                    && service.getCalendar().checkFreeDay(day))) {
                    // ????????? ????? ?????? ? ????? ?????? ?? ???? ????/ ?????????? ????? ???? ????????? ? ?????????
                    final QSchedule sch1 = service.getCalendar() == null
                        ? QCalendarList.getInstance().getById(1).getSpecSchedule(gc_day.getTime())
                        : (service.getCalendar().getSpecSchedule(gc_day.getTime()));
                    sch = (sch1 == null ? service.getSchedule() : sch1);
                    final QSchedule.Interval interval = sch.getWorkInterval(gc_day.getTime());

                    // ???? ???????? ? ???? ???? ?? ????????? ???? ?? ??????? ??? ????? ??????????
                    if (!(interval.start == null || interval.finish == null)) {
                        // ??????? ?? ??? ???? ??? ?.?. ??? ??? ?? ???????? ?? ?????e ???????????
                        // ????? ????? ? ???? ??????. ??? ??? ???????????? ????????? ??????, ??? ??????????? ????????? ??????? ?? ???????? ??????,
                        // ??? ???????? ??????????? ????? ??? ?????? ????????????
                        gc.setTime(interval.start);
                        gc.add(GregorianCalendar.MINUTE, service.getAdvanceTimePeriod());
                        Date start = gc.getTime();
                        gc.setTime(interval.finish);
                        gc.add(GregorianCalendar.MINUTE, -service.getAdvanceTimePeriod());
                        final Date end = gc.getTime();

                        // ????? ?? ????? ?????? ???
                        while (start.before(end) || start.equals(end)) {

                            // ???????? ?? ???????. ? ????????? ??? ??????????? ????????????, ?? ????? ??? ????? ?? ?????? ? ????? ???????????
                            gc.setTime(start);
                            gc.add(GregorianCalendar.MINUTE, service.getAdvanceTimePeriod() - 3);
                            if (!sch.inBreak(start, gc.getTime())) { // ????? ?? ?????? ? ???????

                                int cnt = 0;
                                // ????????? ?? ?????????? ??????????
                                for (QAdvanceCustomer advCustomer : advCustomers) {
                                    gc.setTime(start);
                                    final int s = gc.get(GregorianCalendar.HOUR_OF_DAY);
                                    final int s_m = gc.get(GregorianCalendar.MINUTE);
                                    gc.setTime(advCustomer.getAdvanceTime());
                                    final int e = gc.get(GregorianCalendar.HOUR_OF_DAY);
                                    final int e_m = gc.get(GregorianCalendar.MINUTE);
                                    // ???? ?????? ???? ? ??? ? ??????, ?? ???????? ??????? ???????????? ?? ???? ??? ? ??????
                                    // ??? ????????? ??? ??????? ????? ???????????????? ????? ???? ?? ?? ?????, ? ??????? ????????? ?????.
                                    if ((gc.get(GregorianCalendar.DAY_OF_YEAR) == gc_day
                                        .get(GregorianCalendar.DAY_OF_YEAR))
                                        && (s * 60 + s_m <= e * 60 + e_m
                                        && s * 60 + s_m + service.getAdvanceTimePeriod()
                                        > e * 60 + e_m)) {
                                        cnt++;
                                        // ?????? ?? ???? ????? ???? ? ????? ?????? ?? ????????? ?????????????? ? ???? ?????? ?? ???? ????.
                                        // ?????? ?????.???????? ?? ?????? ???? ????? ?? ??? ? ???????????????? ?? ???? ???
                                        if (cmdParams.customerId != -1
                                            && advCustomer.getAuthorizationCustomer() != null
                                            && advCustomer.getAuthorizationCustomer().getId()
                                            != null
                                            && advCustomer.getAuthorizationCustomer().getId()
                                            .equals(cmdParams.customerId)) {
                                            cnt = 1999999999;
                                            break;
                                        }
                                    }
                                }
                                // ???? ??? ?????????? ???????????? ?? ????????? ??????????? ?? ??????, ?? ??????? ???? ??? ??? ????????? ??? ??????
                                if (cnt < service.getAdvanceLimit()) {
                                    gc.setTime(day);
                                    final GregorianCalendar gc2 = new GregorianCalendar();
                                    gc2.setTime(start);
                                    gc.set(GregorianCalendar.HOUR_OF_DAY,
                                        gc2.get(GregorianCalendar.HOUR_OF_DAY));
                                    gc.set(GregorianCalendar.MINUTE,
                                        gc2.get(GregorianCalendar.MINUTE));
                                    gc.set(GregorianCalendar.SECOND, 0);
                                    gc.set(GregorianCalendar.MILLISECOND, 0);
                                    advCusts.addTime(gc.getTime());
                                }
                            } // ?? ? ???????? ? ?? ????? ????????? ??????? ??? ??? ?????? ? ?? ????????? ?? ????? ????????? ???????????

                            // ???????? ?? ????????? ???
                            gc.setTime(start);
                            gc.add(GregorianCalendar.MINUTE, service.getAdvanceTimePeriod());
                            start = gc.getTime();
                        }

                    }
                } // ???????? ?? ????????? ???? ?????????
                // ??????? ?? ????????? ????
                gc_day.add(GregorianCalendar.DAY_OF_YEAR, 1);
                day = gc_day.getTime();
            }
            return new RpcGetGridOfWeek(advCusts);
        }
    };
    /**
     * ???????? ????????? ?????????????? ? ??????. :: Record the customizer in advance to the
     * service.
     */
    final Task standAdvanceInService = new Task(Uses.TASK_ADVANCE_STAND_IN) {

        @Override
        synchronized public RpcGetAdvanceCustomer process(CmdParams cmdParams, String ipAdress,
            byte[] IP) {
            QLog.l().logQUser().debug("standAdvanceInService");
            super.process(cmdParams, ipAdress, IP);

            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
            QLog.l().logger().trace(
                "?????????????? ?????????? c ID=" + cmdParams.customerId + " ? ?????? \"" + service
                    .getName() + "\"(" + service.getPrefix() + "/" + cmdParams.serviceId + ")"
                    + " ?? ??????? " + new Date(cmdParams.date));
            // ???????? ????? ??????????? ?????????
            final QAdvanceCustomer customer = new QAdvanceCustomer(cmdParams.textData);

            // ????????? ID ??????????????? ????????????, ???? ?????? ???????????, ?? ??? = -1
            final Long authCustonerID = cmdParams.customerId;
            // ???????? ?? ???? ??????????
            QAuthorizationCustomer acust = new QAuthorizationCustomer();
            if (cmdParams.customerId != -1) {
                Spring.getInstance().getHt().load(acust, authCustonerID);
                if (acust.getId() == null || acust.getName() == null) {
                    throw new ServerException("??????????? ?? ???????.");
                }
            } else {
                acust = null;
            }
            customer.setAuthorizationCustomer(acust);
            // ????????? ???? ? ????? ??? ?????????
            final GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date(cmdParams.date));
            gc.set(GregorianCalendar.SECOND, 0);
            gc.set(GregorianCalendar.MILLISECOND, 0);
            final Date startTime = gc.getTime();
            //?????? ?? ???????? ????????? ?????? ? ????? ?? ??????????? ?? ???? ?? ????
            customer.setAdvanceTime(startTime);
            customer.setService(service);
            customer.setComments(cmdParams.comments);
            // ????? ?????????? ????????????? ????????? ??? ???????? ?????????.
            // ????????? "??? ???"
            customer.setPriority(2);

            //???????? ?????? ???????????????? ????????????
            QLog.l().logger().debug("????? ?????????? ??????????????? ?????? ? ????.");
            //Uses.getSessionFactory().merge(this);
            Spring.getInstance().getTt().execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        Spring.getInstance().getHt().saveOrUpdate(customer);
                        QLog.l().logger().debug("?????????.");
                    } catch (Exception ex) {
                        QLog.l().logger()
                            .error("?????? ??? ?????????? \n" + ex.toString() + "\n" + Arrays
                                .toString(ex.getStackTrace()));
                        status.setRollbackOnly();
                    }
                }
            });
            return new RpcGetAdvanceCustomer(customer);
        }
    };
    /**
     * ????????? ????????? ? ??????? ?????????????? ???????????. ????????? ?????, ????????? ???
     * ????????. Put the customizer in the pre-recorded queue. Check the reservation, place or
     * refuse.
     */
    final Task standAdvanceCheckAndStand = new Task(Uses.TASK_ADVANCE_CHECK_AND_STAND) {

        @Override
        public RpcStandInService process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("standAdvanceCheckAndStand");
            super.process(cmdParams, ipAdress, IP);

            // ??????? ?? ???? ???????????????? ?????????
            final QAdvanceCustomer advCust = Spring.getInstance().getHt()
                .get(QAdvanceCustomer.class, cmdParams.customerId);
            if (advCust == null || advCust.getId() == null || advCust.getAdvanceTime() == null) {
                QLog.l().logger().debug("?? ?????? ?????? ?? ??? ID=" + cmdParams.customerId);
                // ???? ?????
                return new RpcStandInService(null, "?? ?????? ????? ??????????????? ??????.");
            }
            final GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(advCust.getAdvanceTime());
            gc.set(GregorianCalendar.HOUR_OF_DAY, gc.get(GregorianCalendar.HOUR_OF_DAY) - 1);
            final GregorianCalendar gc1 = new GregorianCalendar();
            gc1.setTime(advCust.getAdvanceTime());
            gc1.set(GregorianCalendar.MINUTE, gc1.get(GregorianCalendar.MINUTE) + 20);
            if (new Date().before(gc1.getTime()) && new Date().after(gc.getTime())) {
                // ?????? ?????????
                //???? ?????? ? ??????? ??????????????? ???????

                Spring.getInstance().getTt().execute(new TransactionCallbackWithoutResult() {

                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        try {
                            Spring.getInstance().getHt().delete(advCust);
                            QLog.l().logger().debug("??????? ???????????????? ?????? ? ?????????.");
                        } catch (Exception ex) {
                            status.setRollbackOnly();
                            throw new ServerException(
                                "?????? ??? ???????? \n" + ex.toString() + "\n" + Arrays
                                    .toString(ex.getStackTrace()));
                        }
                    }
                });
                // ??????? ????????? ?????? ??????? ?? ???????? ?????????
                // ???????? ???????
                final CmdParams params = new CmdParams();
                params.serviceId = advCust.getService().getId();
                params.password = "";
                params.priority = advCust.getPriority();
                params.textData = advCust.getInputData();
                final RpcStandInService txtCustomer = addCustomerTask.process(params, ipAdress, IP);
                txtCustomer.getResult().setInput_data(advCust.getInputData());
                return txtCustomer;
            } else {
                String answer = Locales.locMes("advclient_out_date");
                QLog.l().logger().trace(answer);
                // ???? ?????
                return new RpcStandInService(null, answer);
            }
        }
    };
    /**
     * ??????? ?????????????? ??????????? ????????? :: Delete the pre-recorded customizer
     */
    final Task removeAdvanceCustomer = new Task(Uses.TASK_REMOVE_ADVANCE_CUSTOMER) {

        @Override
        public JsonRPC20OK process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("removeAdvanceCustomer");
            super.process(cmdParams, ipAdress, IP);
            QLog.l().logger().error("------------------");
            // ??????? ?? ???? ???????????????? ?????????
            // Getting out of the base of the preliminary custodian
            final QAdvanceCustomer advCust = Spring.getInstance().getHt()
                .get(QAdvanceCustomer.class, cmdParams.customerId);

            if (advCust == null || advCust.getId() == null || advCust.getAdvanceTime() == null) {
                QLog.l().logger().error("NOT FOUND ------------------");
                QLog.l().logger()
                    .error("?? ?????? ?????? ?? ??? ID= :: Did not find the client by its ID"
                        + cmdParams.customerId);
                // ???? ????? :: Helmet failure
                return new JsonRPC20OK(ADVANCED_NOT_FOUND);
            }
            //???? ?????? ? ??????? ??????????????? ???????
            // three records in the table of preliminary records
            Spring.getInstance().getTt().execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        Spring.getInstance().getHt().delete(advCust);
                        QLog.l().logger().error(
                            "??????? ???????????????? ?????? ? ?????????. :: We removed the pre-master record of the custodian.");
                    } catch (Exception ex) {
                        status.setRollbackOnly();
                        QLog.l().logger().error("error while deleting");
                        throw new ServerException(
                            "?????? ??? ???????? :: Error while deleting \n" + ex.toString() + "\n"
                                + Arrays
                                .toString(ex.getStackTrace()));
                    }
                }
            });
            return new JsonRPC20OK();
        }
    };
    /**
     * ????????? ?????? ???????.
     */
    final Task getResponseList = new Task(Uses.TASK_GET_RESPONSE_LIST) {

        @Override
        public RpcGetRespTree process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getResponseList");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetRespTree(QResponseTree.getInstance().getRoot());
        }
    };
    /**
     * ??????????? ??????.
     */
    final Task setResponseAnswer = new Task(Uses.TASK_SET_RESPONSE_ANSWER) {

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("setResponseAnswer");
            super.process(cmdParams, ipAdress, IP);
            final JsonRPC20OK rpc = new JsonRPC20OK();
            final QRespEvent event = new QRespEvent();
            event.setDate(new Date());
            event.setRespID(cmdParams.responseId);
            event.setServiceID(cmdParams.serviceId);
            event.setUserID(cmdParams.userId);
            event.setClientID(cmdParams.customerId);
            event.setClientData(cmdParams.textData);
            event.setComment(cmdParams.comments == null ? "" : cmdParams.comments);
            if (cmdParams.userId != null && cmdParams.customerId == null) {
                if (QUserList.getInstance().hasById(cmdParams.userId)) {
                    final QUser user = QUserList.getInstance().getById(cmdParams.userId);
                    if (user.getShadow() != null) {
                        event.setClientID(user.getShadow().getIdOldCustomer());
                        event.setServiceID(user.getShadow().getIdOldService());
                        //event.setClientData(user.getShadow().getInputData() == null || user.getShadow().getInputData().isEmpty() ? user.getShadow().getOldCustomer().getFullNumber() : user.getShadow().getInputData());
                    }
                    // ? ?????? ???????.
                    // ?.?. ????? ????? ?????? ?? ????? ?????? ? ??????????, ? ???? ?? ?????? ??????, ?? ??? ??? ? ????, ? ? ?????? ????? ????
                    // ????? ?? ID ?????????, ????????????? ??????? ?? ???????????.
                    // ?? ????? ???? ????? ? ?????? ?????????, ?????????? ??????? ????????? ??? ????? ????????? ?????????.
                    if (user.getCustomer() != null
                        && (user.getCustomer().getState() == CustomerState.STATE_WAIT
                        || user.getCustomer().getState() == CustomerState.STATE_INVITED
                        || user.getCustomer().getState() == CustomerState.STATE_WORK)) {
                        user.getCustomer().addNewRespEvent(event);
                        return rpc;
                    }
                } else {
                    QLog.l().logger()
                        .error("It is a bull shit! No user by id=\"" + cmdParams.userId + "\"");
                }
            }

            final JsonRPC20Error rpcErr = new JsonRPC20Error(0, null);
            Spring.getInstance().getTt().execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        Spring.getInstance().getHt().saveOrUpdate(event);
                        QLog.l().logger().debug("????????? ????? ? ????.");
                    } catch (Exception ex) {
                        rpcErr.setError(
                            new JsonRPC20Error.ErrorRPC(JsonRPC20Error.ErrorRPC.RESPONCE_NOT_SAVE,
                                ex));
                        QLog.l().logger()
                            .error("?????? ??? ?????????? \n" + ex.toString() + "\n" + Arrays
                                .toString(ex.getStackTrace()));
                        status.setRollbackOnly();
                    }
                }
            });
            return rpcErr.getError().getCode() == 0 || rpcErr.getError().getData() == null ? rpc
                : rpcErr;
        }
    };
    /**
     * ????????? ??????????????? ??????.
     */
    final Task getInfoTree = new Task(Uses.TASK_GET_INFO_TREE) {

        @Override
        public RpcGetInfoTree process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getInfoTree");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetInfoTree(QInfoTree.getInstance().getRoot());
        }
    };
    /**
     * ???????????????? ????????? ?? ??? ID.
     */
    final Task getClientAuthorization = new Task(Uses.TASK_GET_CLIENT_AUTHORIZATION) {

        @Override
        public RpcGetAuthorizCustomer process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getClientAuthorization");
            super.process(cmdParams, ipAdress, IP);
            // ??????? ?? ???? ???????????????? ?????????
            if (cmdParams.clientAuthId == null || cmdParams.clientAuthId.isEmpty()) {
                return new RpcGetAuthorizCustomer(null);
            }
            final List<QAuthorizationCustomer> authCusts = Spring.getInstance().getHt()
                .findByCriteria(
                    DetachedCriteria.forClass(QAuthorizationCustomer.class)
                        .add(Restrictions.eq("authId", cmdParams.clientAuthId).ignoreCase()));
            final QAuthorizationCustomer authCust;
            if (authCusts.isEmpty() || authCusts.get(0) == null || authCusts.get(0).getId() == null
                || authCusts.get(0).getName() == null) {
                QLog.l().logger()
                    .trace("?? ?????? ?????? ?? ??? ID = '" + cmdParams.clientAuthId + "'");
                authCust = null;
            } else {
                authCust = authCusts.get(0);
            }
            return new RpcGetAuthorizCustomer(authCust);
        }
    };
    /**
     * ????????? ?????? ??????????? ?? ????????? ?????? ???????????? ? ????????.
     */
    final Task getResultsList = new Task(Uses.TASK_GET_RESULTS_LIST) {

        @Override
        public RpcGetResultsList process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getResultsList");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetResultsList(QResultList.getInstance().getItems());
        }
    };
    /**
     * ????????? ?????????? ????????? Changing the priority of the customizer
     */
    final Task setCustomerPriority = new Task(Uses.TASK_SET_CUSTOMER_PRIORITY) {

        @Override
        public RpcGetSrt process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("setCustomerPriority");
            super.process(cmdParams, ipAdress, IP);
            // ????? ???-?? ???????? ?????????
            final String num = cmdParams.clientAuthId.replaceAll("[^\\p{L}+\\d]", "");
            String s = "";
            for (QService service : QServiceTree.getInstance().getNodes()) {
                if (service.changeCustomerPriorityByNumber(num, cmdParams.priority)) {
                    s = "??????? ? ??????? \"" + num + "\" ? ?????? \"" + service.getName()
                        + "\" ??????? ?????????.";
                    break;
                }
            }
            return new RpcGetSrt(
                "".equals(s) ? String.format(Locales.locMes("client_not_found_by_num"), num) : s);
        }
    };
    /**
     * ????????? ????? ????????? :: Check Castomer Number
     */
    final Task checkCustomerNumber = new Task(Uses.TASK_CHECK_CUSTOMER_NUMBER) {

        @Override
        public RpcGetTicketHistory process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("checkCustomerNumber");
            super.process(cmdParams, ipAdress, IP);
            final String num = cmdParams.clientAuthId.trim().replaceAll("[^\\p{L}+\\d]", "");
            String s = "";
            for (QService service : QServiceTree.getInstance().getNodes()) {
                for (QCustomer customer : service.getClients()) {
                    if (num.equalsIgnoreCase(customer.getPrefix() + customer.getNumber())) {
                        s = String
                            .format(Locales.locMes("client_with_number_to_service"), num,
                                service.getName());
                        break;
                    }
                }
            }
            if ("".equals(s)) {
                for (QCustomer customer : QPostponedList.getInstance().getPostponedCustomers()) {
                    if (num.equalsIgnoreCase(customer.getPrefix() + customer.getNumber())) {
                        s = String.format(Locales.locMes("client_with_number_postponed"), num);
                        break;
                    }
                }
            }

            if ("".equals(s)) {
                for (QUser user : QUserList.getInstance().getItems()) {
                    if (user.getCustomer() != null && num
                        .equalsIgnoreCase(user.getCustomer().getFullNumber())) {
                        s = String.format(Locales.locMes("client_with_number_in_work"), num,
                            user.getName());
                        break;
                    }
                }
            }

            if ("".equals(s) && KILLED_CUSTOMERS.get(num) != null) {
                s = String.format(Locales.locMes("client_with_number_removed"), num,
                    Locales.getInstance().format_for_label.format(KILLED_CUSTOMERS.get(num)));
            }

            final String n = num.replaceAll("\\D+", "");
            final String p = num.replaceAll(n, "");
            final List<QCustomer> custs = Spring.getInstance().getHt().find(
                "FROM QCustomer a WHERE service_prefix ='" + p + "' and number = " + (n.isEmpty()
                    ? "0"
                    : n));
            final LinkedList lc = new LinkedList();
            custs.forEach((cust) -> {
                lc.add(
                    Uses.FORMAT_DD_MM_YYYY_TIME.format(cust.getStandTime())
                        + "&nbsp;&nbsp;&nbsp;&nbsp;" + (
                        cust.getService().getName().length() > 96 ?
                            cust.getService().getName().substring(0, 95) + "..."
                            : cust.getService().getName()) + "&nbsp;&nbsp;&nbsp;&nbsp;" + cust
                        .getUser()
                        .getName() + "&nbsp;&nbsp;&nbsp;&nbsp;" + CustomerState.values()[cust
                        .getStateIn()]);
            });
            return new RpcGetTicketHistory(new RpcGetTicketHistory.TicketHistory(
                "".equals(s) ? String.format(Locales.locMes("client_not_found_by_num_at_all"), num)
                    : s,
                lc));
        }
    };
    /**
     * ??????? ??????? ?? ???????
     */
    final Task restartServer = new Task(Uses.TASK_RESTART) {

        @Override
        public AJsonRPC20 process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("restartServer");
            super.process(cmdParams, ipAdress, IP);
            ServerEvents.getInstance().restartEvent();
            QPostponedList.getInstance().clear();
            QServer.loadPool();
            MainBoard.getInstance().refresh();
            return new JsonRPC20OK();
        }
    };
    /**
     * ??????? ???????? ????? ?? ???????
     */
    final Task restarMainTablo = new Task(Uses.TASK_RESTART_MAIN_TABLO) {

        @Override
        public AJsonRPC20 process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("restarMainTablo");
            super.process(cmdParams, ipAdress, IP);
            MainBoard.getInstance().refresh();
            return new JsonRPC20OK();
        }
    };
    /**
     * ???????? ??????? ????? ?? ?????
     */
    final Task refreshRunningText = new Task(Uses.TASK_CHANGE_RUNNING_TEXT_ON_BOARD) {

        @Override
        public AJsonRPC20 process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("refreshRunningText");
            super.process(cmdParams, ipAdress, IP);
            if (MainBoard.getInstance() instanceof QIndicatorBoardMonitor) {
                final QIndicatorBoardMonitor mon = (QIndicatorBoardMonitor) MainBoard.getInstance();
                if (Uses.TAG_BOARD_TOP.equals(cmdParams.infoItemName)) {
                    mon.indicatorBoard.getTopRunningLabel().stop();
                    mon.indicatorBoard.getTopRunningLabel().setText("");
                    mon.indicatorBoard.getTopRunningLabel().setShowTime(false);
                    mon.indicatorBoard.getTopRunningLabel().setRunningText(cmdParams.textData);
                    mon.indicatorBoard.getTopRunningLabel().start();
                }
                if (Uses.TAG_BOARD_LEFT.equals(cmdParams.infoItemName)) {
                    mon.indicatorBoard.getLeftRunningLabel().stop();
                    mon.indicatorBoard.getLeftRunningLabel().setText("");
                    mon.indicatorBoard.getLeftRunningLabel().setShowTime(false);
                    mon.indicatorBoard.getLeftRunningLabel().setRunningText(cmdParams.textData);
                    mon.indicatorBoard.getLeftRunningLabel().start();
                }
                if (Uses.TAG_BOARD_RIGHT.equals(cmdParams.infoItemName)) {
                    mon.indicatorBoard.getRightRunningLabel().stop();
                    mon.indicatorBoard.getRightRunningLabel().setText("");
                    mon.indicatorBoard.getRightRunningLabel().setShowTime(false);
                    mon.indicatorBoard.getRightRunningLabel().setRunningText(cmdParams.textData);
                    mon.indicatorBoard.getRightRunningLabel().start();
                }
                if (Uses.TAG_BOARD_BOTTOM.equals(cmdParams.infoItemName)) {
                    mon.indicatorBoard.getBottomRunningLabel().stop();
                    mon.indicatorBoard.getBottomRunningLabel().setText("");
                    mon.indicatorBoard.getBottomRunningLabel().setShowTime(false);
                    mon.indicatorBoard.getBottomRunningLabel().setRunningText(cmdParams.textData);
                    mon.indicatorBoard.getBottomRunningLabel().start();
                }
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ?????? ?? ????????? ??????????? ??????????? ????? ?? ?????? Request to change the priorities
     * of services provided by users
     */
    final Task changeFlexPriority = new Task(Uses.TASK_CHANGE_FLEX_PRIORITY) {

        @Override
        public AJsonRPC20 process(final CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("changeFlexPriority");
            super.process(cmdParams, ipAdress, IP);
            final QUser user = QUserList.getInstance().getById(cmdParams.userId);
            for (String str : cmdParams.textData.split("&")) {
                final String[] ss = str.split("=");
                if (!"".equals(ss[0]) && !"".equals(ss[1])) {
                    user.getPlanService(Long.parseLong(ss[0]))
                        .setCoefficient(Integer.parseInt(ss[1]));
                }
            }
            return new JsonRPC20OK();
        }
    };
    /**
     * ????????? ??????????.
     */
    final Task getStandards = new Task(Uses.TASK_GET_STANDARDS) {

        @Override
        public RpcGetStandards process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getStandards");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetStandards(ServerProps.getInstance().getStandards());
        }
    };
    /**
     * ????????? ????? ? ????????????. Pause the user.
     */
    final Task setPause = new Task(Uses.TASK_SET_BUSSY) {

        @Override
        public RpcGetBool process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("setPause");
            super.process(cmdParams, ipAdress, IP);
            QUserList.getInstance().getById(cmdParams.userId).setPause(cmdParams.requestBack);
            return new RpcGetBool(QUserList.getInstance().getById(cmdParams.userId).isPause());
        }
    };
    /**
     * ???????? ????????? ?? ?? ?? ??????? Get parameters from the DB from the server
     */
    final Task getProperties = new Task(Uses.TASK_GET_PROPERTIES) {

        @Override
        public RpcGetProperties process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getProperties");
            super.process(cmdParams, ipAdress, IP);
            return new RpcGetProperties(ServerProps.getInstance().getDBproperties());
        }
    };
    /**
     * ?????????? ????????? ?? ?? ? ??????? Run parameters from DB in the server
     */
    final Task initProperties = new Task(Uses.TASK_INIT_PROPERTIES) {

        @Override
        public RpcGetProperties process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("initProperties");
            super.process(cmdParams, ipAdress, IP);
            cmdParams.properties.forEach(prop -> {
                if (ServerProps.getInstance().getSection(prop.getSection()) == null
                    || ServerProps.getInstance().getSection(prop.getSection()).getProperty(name)
                    == null) {
                    ServerProps.getInstance().saveOrUpdateProperty(prop);
                }
            });
            return new RpcGetProperties(ServerProps.getInstance().getDBproperties());
        }
    };
    /**
     * ????????? ????????? ? ?? ???????
     */
    final Task saveProperties = new Task(Uses.TASK_SAVE_PROPERTIES) {

        @Override
        public RpcGetProperties process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getsaveProperties");
            super.process(cmdParams, ipAdress, IP);
            cmdParams.properties.forEach(prop -> {
                ServerProps.getInstance().saveOrUpdateProperty(prop);
            });
            return new RpcGetProperties(ServerProps.getInstance().getDBproperties());
        }
    };
    //
    //*******************************************************************************************************
    //**************************  ??????????? ??????? *******************************************************
    //*******************************************************************************************************
    //
    /**
     * ???????? ????????? ???????. :: Get server status
     */
    private final Task getServerState = new Task(Uses.TASK_SERVER_STATE) {

        @Override
        public RpcGetServerState process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            QLog.l().logQUser().debug("getServerState");
            super.process(cmdParams, ipAdress, IP);
            final LinkedList<RpcGetServerState.ServiceInfo> srvs = new LinkedList<>();

            QServiceTree.getInstance().getNodes().stream().filter((service) -> (service.isLeaf()))
                .forEach((service) -> {
                    final QCustomer customer = service.peekCustomer();
                    srvs.add(new RpcGetServerState.ServiceInfo(service, service.getCountCustomers(),
                        customer != null ? customer.getPrefix() + customer.getNumber() : "-"));
                });
            return new RpcGetServerState(srvs);
        }
    };

    /**
     * ??????????? ???? ???????? ????? ????? ?????????? ?????? ?????? ?????????? ??? ???????? ??
     * ?????.
     */
    private Executer() {
        // ????????? ????????????? ?????????
        for (final ITask task : ServiceLoader.load(ITask.class)) {
            QLog.l().logger().info("Load extra task via tasks.put(): " + task.getDescription());
            try {
                tasks.put(task.getName(), task);
            } catch (Throwable tr) {
                QLog.l().logger().error(
                    "????? SPI ?????????? ?????????? ???????. ????????: The SPI extension call failed. Description: "
                        + tr);
            }
        }
    }

    public static Executer getInstance() {
        return ExecuterHolder.INSTANCE;
    }

    public HashMap<String, ITask> getTasks() {
        return tasks;
    }

    /**
     * ?????????? ???? ???????, ????????? ?? ?????????
     *
     * @param rpc ?????? ???????
     * @param ipAdress ????? ???? ??? ??????? ???????
     * @param IP ????? ???? ??? ??????? ???????
     * @return ?????? ?????????? ?????????? ???????
     */
    public Object doTask(JsonRPC20 rpc, String ipAdress, byte[] IP) {
        final long start = System.currentTimeMillis();
        QLog.l().logQUser().debug("start task");
        if (!QConfig.cfg().isDebug()) {
            System.out.println("Task processing: '" + rpc.getMethod());
        }

        if (tasks.get(rpc.getMethod()) == null) {
            throw new ServerException(
                "? ??????? ?? ????? ??????? ???????? ????????: '" + rpc.getMethod() + "'");
        }

        final Object result;
        // ????? ??????????? ??????? ?? ???????????????
        // ????????????? ????????? ?????? ????? ???????????? ? ??????? ??????????
        // ??? ??????? ?????? ??? ????????? ????? ???????, ??????? ?? ???? ????????????????.
        // ? ?? ??? ?????????? ????????????????, ?? ????????????? ? ????? ????????????.
        result = tasks.get(rpc.getMethod()).process(rpc.getParams(), ipAdress, IP);

        QLog.l().logger().info(
            "Task was finished. Time: " + ((double) (System.currentTimeMillis() - start)) / 1000
                + " sec.");
        QLog.l().logQUser()
            .debug("Task finished in: " + ((double) (System.currentTimeMillis() - start)) / 1000);
        return result;
    }

    private static class ExecuterHolder {

        private static final Executer INSTANCE = new Executer();
    }

    /**
     * @author Evgeniy Egorov ??????? ????? ???????????? ???????. ??? ???? ?????????? ?
     * HashMap[String, ATask] tasks. ????? process ????????? ???????.
     */
    public class Task implements ITask {

        protected final String name;
        protected CmdParams cmdParams;
        protected QCustomer customerCreated;

        public Task(String name) {

            //  Debug
            //            QLog.l().logQUser().debug("==> Start: Task(" + name + "); Count before = " + tasks
            //                    .size());

            this.name = name;
            final Task tk = this;
            tasks.put(name, tk);

            //  Debug
            //QLog.l().logQUser().debug("==> End: Task(" + name + "); Count after = " + tasks.size());
        }

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP) {

            //QLog.l().logQUser().debug("==> Start: Task.process(a)");
            Boolean sessionOK = QSessions.getInstance().check(cmdParams == null ? null
                    : cmdParams.userId, ipAdress, IP);
            //            QLog.l().logQUser().debug("    --> Start: Task(a) SOK: " + sessionOK + "; IP: "
            //                    + ipAdress);
            QSessions.getInstance()
                .update(cmdParams == null ? null : cmdParams.userId, ipAdress, IP);

            //            if ((cmdParams != null) && cmdParams.custQtxn) {
            //                QLog.l().logQUser().debug("    --> parms QTxn: Yes");
            //            }
            //            else {
            //                QLog.l().logQUser().debug("    --> parms QTxn: No");
            //            }
            this.cmdParams = cmdParams;
            //QLog.l().logQUser().debug("==> End: Task.process(CmdParams, String, byte[])");
            return new JsonRPC20OK();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return "Standard internal QSystem task.";
        }

        @Override
        public long getUID() {
            return 777L;
        }

        @Override
        public AJsonRPC20 process(CmdParams cmdParams, String ipAdress, byte[] IP,
            QCustomer customer) {
            Boolean sessionOK = QSessions.getInstance().check(cmdParams == null ? null
                    : cmdParams.userId, ipAdress, IP);
            //QLog.l().logQUser().debug("    --> Start: Task.process(b) SessionOK = " + sessionOK);
            QSessions.getInstance()
                .update(cmdParams == null ? null : cmdParams.userId, ipAdress, IP);
            this.cmdParams = cmdParams;
            this.customerCreated = customer;
            //QLog.l().logQUser().debug("==> End: Task.process(CmdParams, String, byte[], QCustomer)");
            return new JsonRPC20OK();
        }
    }

//****************************************************************************
//********************* ????? ?????????? ? ??? ???????????? ???????  *********
//********************* END of adding task handlers to the Map       ***********
//**********************************************************************************************
//**********************************   ????????? ???????  **************************************
//**********************************  TREATMENT OF JOBS   **************************************

    class AddCustomerTask extends Task {

        public AddCustomerTask(String name) {
            super(name);
        }

        @Override
        public RpcStandInService process(CmdParams cmdParams, String ipAdress, byte[] IP) {
            //QLog.l().logQUser().debug("==> Start: AddCustomerTask.process(CmdParams, String, byte[])");
            super.process(cmdParams, ipAdress, IP);
            final QService service = QServiceTree.getInstance().getById(cmdParams.serviceId);
            final QCustomer customer;
            Long userId = cmdParams.userId;

            final QUser user = QUserList.getInstance().getById(cmdParams.userId);

            //QLog.l().logQUser().debug(user);
            QOffice userOffice = user.getOffice();

            // ?????????????? ?????? ? ????????
            // Synchronize the work with the client
            CLIENT_TASK_LOCK.lock();
            try {
                // ???????? ????? ??????????? ?????????
                // Create a new baked customer
                customer = new QCustomer(service.getNextNumber());

                //set customer welcome time
                customer.setWelcomeTime(cmdParams.welcomeTime);

                // ????????? ????????? ? ???????
                // Define the customizer in the queue
                customer.setService(service);

                if (service.getLink() != null) {
                    customer.setService(service.getLink());
                }

                // ????? ?????????? ????????????? ????????? ??? ???????? ?????????.
                // ????????? "??? ???"
                // the setting time is automatically inserted when creating a customizer.
                // Priority "like everyone else"
                customer.setPriority(cmdParams.priority);

                // ????????? ?????????? ??????
                // The data entered by the customizer
                customer.setTempComments(cmdParams.comments);

                //Add Added by user
                customer.setAddedBy(QUserList.getInstance().getById(cmdParams.userId).getName());

                //Add the channel info
                customer.setChannels(cmdParams.channels);
                customer.setChannelsIndex(cmdParams.channelsIndex);
                customer.setOffice(userOffice);
                customer.setUser(user);

                //  Add quick txn or not, sequence or not.
                customer.setTempQuickTxn(cmdParams.custQtxn);
                customer.setIsInSequence(cmdParams.in_sequence);
                customer.setLogWaitQueue(cmdParams.log_waitqueue);
                customer.setSpId(cmdParams.spId);
                //                QLog.l().logQUser().debug("    --> Cust: " + customer.getName() + "; Cust Seq: "
                //                        + (customer.getIsInSequence() ? "Yes" : "No"));

                //??????? ?????? ????????????
                // add a new user
                (service.getLink() != null ? service.getLink() : service).addCustomer(customer);

                // ????????? ? ???? "????, ???".
                // His condition is "I'm standing, waiting."
                //QLog.l().logQUser().debug("setState");
                customer.setState(CustomerState.STATE_WAIT);
            } catch (Exception ex) {
                throw new ServerException("?????? ??? ?????????? ??????? ? ??????? ::: Error placing the client in the queue :", ex);
            } finally {
                CLIENT_TASK_LOCK.unlock();
            }

            // ???? ???????? ?????????, ?? ??? ??????????? ????????? ? ????? ?.?.
            // ?? ??? ???? ? ???????
            // if the customizer is added, then it must be sent in response.
            // it already exists in the system
            try {
                // ????????? ????????? ????????.
                // store the state of the queues.
                //QServer.savePool();

                //????????? ?????????? ? ???, ??? ???????? ??????????
                //????????? ???????????????? ?? UDP ?? ???????????? ????

                // send out an alert that a visitor has appeared
                // send out broadly by UDP to a specific port
                Uses.sendUDPBroadcast(service.getId().toString(),
                    ServerProps.getInstance().getProps().getClientPort());

                // ?????? ????????????? ???????? ????? ? ??????? ?????????
                // Must be highlighted on the main scoreboard in the nearest table
                //QLog.l().logQUser().debug("AddCustomerTask MainBoard standIn");
                MainBoard.getInstance().customerStandIn(customer);
            } catch (Exception ex) {
                QLog.l().logger().error(ex);
            }

            //QLog.l().logQUser().debug("==> End: AddCustomerTask.process(CmdParams, String, byte[])");

            return new RpcStandInService(customer);
        }
    }
}