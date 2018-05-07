/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsys.quser;

import static ru.apertum.qsystem.client.forms.FClient.KEYS_INVITED;
import static ru.apertum.qsystem.client.forms.FClient.KEYS_MAY_INVITE;
import static ru.apertum.qsystem.client.forms.FClient.KEYS_OFF;
import static ru.apertum.qsystem.client.forms.FClient.KEYS_STARTED;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.DefaultBinder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.cmd.CmdParams;
import ru.apertum.qsystem.common.cmd.RpcInviteCustomer;
import ru.apertum.qsystem.common.cmd.RpcStandInService;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.QSession;
import ru.apertum.qsystem.server.QSessions;
import ru.apertum.qsystem.server.controller.Executer;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;
import ru.apertum.qsystem.server.model.results.QResult;
import ru.apertum.qsystem.server.model.results.QResultList;

//  CM:  To send slack messages
import ru.apertum.qsystem.server.controller.SlackApi;
import ru.apertum.qsystem.server.controller.SlackException;
import ru.apertum.qsystem.server.controller.SlackMessage;

//  CM:  To read offices.
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import ru.apertum.qsystem.server.Spring;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

//  CM:  For debugging session info.
//import java.util.Map;
//import java.util.Set;

/**
 * @author Evgeniy Egorov
 */
public class Form {

    private QCustomer pickedPostponed;
    private final LinkedList<QCustomer> postponList = QPostponedList.getInstance().getPostponedCustomers();
    private final LinkedList<QResult> resultList = QResultList.getInstance().getItems();
    // ********************************************************************************************************************************************
    // ** ??????????????? Redirection
    // ********************************************************************************************************************************************
    private final TreeServices treeServs = new TreeServices();

    // *****************************************************
    // **** Multilingual
    // *****************************************************
    /*
     * public ArrayList<Lng> getLangs() { return Multilingual.LANGS; }
     *
     * private Lng lang = new Multilingual().init();
     *
     * public Lng getLang() { return lang; }
     *
     * public void setLang(Lng lang) { this.lang = lang; }
     *
     * @Command("changeLang") public void changeLang() { if (lang != null) { final Session session = Sessions.getCurrent(); final Locale prefer_locale =
     * lang.code.length() > 2 ? new Locale(lang.code.substring(0, 2), lang.code.substring(3)) : new Locale(lang.code);
     * session.setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE, prefer_locale); Executions.sendRedirect(null);
     *
     * } }
     */

    // *****************************************************
    // **** ????? Login
    // *****************************************************
    public LinkedList<String> prior_St = new LinkedList(Uses.get_COEFF_WORD().values());
    public String officeType = "non-reception";
    public LinkedList<QUser> userList = new LinkedList<>();
    public LinkedList<QUser> userListbyOffice = new LinkedList<>();
    private static HashMap<Long, Long> inviteTimes = new HashMap<Long, Long>();

    // Main service page
    @Wire("#incClientDashboard #client_north")
    North clientDashboardNorth;
    // ********************************************************************************************************************************************
    // ** Change priority - By Service
    // ********************************************************************************************************************************************
    @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
    Window changeServicePriorityDialog;
    // ********************************************************************************************************************************************
    // ** ?????????? ???????? ?????????? Postponed Postpone visitor
    // ********************************************************************************************************************************************
    @Wire("#incClientDashboard #incPostponeCustomerDialog #postponeDialog")
    Window postponeCustomerDialog;
    // *** ??????? ????????? ????????? ? ?????? ?????????? Dialogs for changing the state and calling ltalges
    @Wire("#incClientDashboard #incChangePostponedStatusDialog #changePostponedStatusDialog")
    Window changePostponedStatusDialog;
    // *********************************************************************************************************
    // **** ?????? ? ?? ??????? Buttons and their events
    // *********************************************************************************************************
    QService pickedRedirectServ;
    @Wire("#incClientDashboard #incGAManagementDialog #GAManagementDialog")
    Window GAManagementDialogWindow;
    @Wire("#incClientDashboard #incAddNextServiceDialog #addNextServiceDialog")
    Window addNextServiceDialog;
    @Wire("#incClientDashboard #incServicesDialog #servicesDialog")
    Window servicesDialogWindow;
    @Wire("#incClientDashboard #incRedirectCustomerDialog #redirectDialog")
    Window redirectCustomerDialog;
    @Wire("#incClientDashboard #incChangeServiceDialog #changeServiceDialog")
    Window changeServiceDialogWindow;
    @Wire("#incClientDashboard #incServeCustomerDialog #serveCustomerDialog")
    Window serveCustomerDialogWindow;
    @Wire("#incClientDashboard #incAddTicketDialog #addTicketDialog")
    Window addTicketDailogWindow;
    // @wire("#incClientDashboard #GAManagement")
    // Window GAManagement;
    @Wire("#incClientDashboard #incReportingBug #ReportingBug")
    Window ReportingBugWindow;

    //  CM:  Login form, for checkbox.
    @Wire("#incLoginForm #QuickTxnCSR")
    Checkbox csrQuickTxn;

    QService pickedMainService;
    @Wire
    private Textbox typeservices;
    /**
     * ?????????????? ???? Logged user
     */
    private User user = new User();
    private LinkedList<QService> PreviousList = new LinkedList<>();
    /**
     * ??????? ????????? ?????? Current state of the buttons
     */
    private String keys_current = KEYS_OFF;
    private boolean[] btnsDisabled = new boolean[] { true, true, true, true, true, true, true, true,
            true };
    private boolean[] addWindowButtons = new boolean[] { true, false, false, false };
    /* Add Hide Button if Not Receptionist Model */
    private boolean checkCFMSType = false;
    //private boolean serviceSelected = false;
    private String checkCFMSHidden = "display: none;";
    private String checkCFMSHeight = "0%";
    private boolean checkCombo = false;
    private QCustomer customer = null;
    private QCustomer trackCust = null;
    @Wire("#btn_invite")
    private Button btn_invite;
    @Wire("#incClientDashboard #service_list")
    private Listbox service_list;
    @Wire("#incClientDashboard #postpone_list")
    private Listbox postpone_list;
    @Wire("#incGAManagementDialog #GA_list")
    private Listbox GA_list;
    // ********************************************************************************************************************************************
    // ** Change priority - By Customer
    // ********************************************************************************************************************************************
    private QCustomer pickedCustomer;
    private QPlanService pickedService;
    private String oldSt = "";
    private String filter = "";
    private List<QService> listServices;
    private String officeName = "";
    private Combobox cboFmCompress;
    private String filterCa = "";
    private String CSRIcon = "";
    private String beginServiceClass = "act-button";
    private int customersCount = 0;
    private boolean currentState = false;
    private boolean CheckGABoard = false;
    private Long spId = 0L;

    private String lastGoodQuantity = "1";

    private static boolean trackQOnBeginService = false;
    private static boolean trackQOnNextService = false;
    private static boolean trackQOnPreviousService = false;

    // public LinkedList<QUser> test2 = greed.get(2).getShadow();
    // public LinkedList<QUser> userList = QUserList.getInstance().getItems();

    public String l(String resName) {
        return Labels.getLabel(resName);
    }

    public void setKeyRegimForUser(User userK) {
        if (userK != null) {
            user = userK;
            if (user.getUser().getCustomer() != null) {
                customer = user.getUser().getCustomer();
                switch (user.getUser().getCustomer().getState()) {
                    case STATE_DEAD:
                        setKeyRegim(KEYS_INVITED);
                        break;
                    case STATE_INVITED:
                        setKeyRegim(KEYS_INVITED);
                        break;
                    case STATE_INVITED_SECONDARY:
                        setKeyRegim(KEYS_INVITED);
                        break;
                    case STATE_WORK:
                        setKeyRegim(KEYS_STARTED);
                        break;
                    case STATE_WORK_SECONDARY:
                        setKeyRegim(KEYS_STARTED);
                        break;
                    default:
                        setKeyRegim(KEYS_MAY_INVITE);
                }
            }
            else {
                setKeyRegim(KEYS_MAY_INVITE);
            }
        }
    }

    @Init
    public void init() {
        QLog.l().logQUser().debug("==> Loading page: init");

        // CM:  Get environment.
        //        String mySrvName = Executions.getCurrent().getServerName();
        //        String myContext = Executions.getCurrent().getContextPath();
        //        String myPath = Executions.getCurrent().getDesktop().getRequestPath();
        //        String myEnv = System.getenv("QSYSTEM_ENV");
        //        QLog.l().logQUser().debug("    --> Vars SrvName: " + mySrvName + "; Ctx: " + myContext
        //                + "; Path: " + myPath + "; Env: " + myEnv);

        final Session sess = Sessions.getCurrent();
        final User userL = (User) sess.getAttribute("userForQUser");
        setKeyRegimForUser(userL);
        setCFMSAttributes();

        //QLog.l().logQUser().debug("    --> Number of Invite Times: " + inviteTimes.size());

        //  CM:  If invite times not set yet, initialize them.
        if (inviteTimes.size() == 0) {

            QLog.l().logQUser().debug("    --> Invite times not loaded yet.  Loading now ...");

            //  Read a list of all offices.
            List<QOffice> offices = Spring.getInstance().getHt().findByCriteria(
                    DetachedCriteria.forClass(QOffice.class)
                        .add(Property.forName("deleted").isNull())
                        .setFetchMode("services", FetchMode.EAGER)
                        .setResultTransformer((Criteria.DISTINCT_ROOT_ENTITY))
                );

            //  Create last invite time for each office.
            for (QOffice office : offices) {
                inviteTimes.put(office.getId(), System.currentTimeMillis());
            }
        }
            

        //  Set when to log the state of the wait queue.
        trackQOnBeginService = getEnvBoolean("QSYSTEM_TRACK_Q_BEGIN");
        trackQOnNextService = getEnvBoolean("QSYSTEM_TRACK_Q_NEXT");
        trackQOnPreviousService = getEnvBoolean("QSYSTEM_TRACK_Q_PREVIOUS");
        
        //  Test Snowplow.
        //Executer.getInstance().TestSnowplow(99999L, 12, 12);
        //Executer.getInstance().TestSnowplow(88888L, 13, 13);

        QLog.l().logQUser().debug("    --> Number of Invite Times: " + inviteTimes.size());

        //  If a current user, get the office name and set it.
        if (user != null) {
            QUser quser = user.getUser();
            if (quser != null) {
                officeName = user.getUser().getOffice().getName();
            }
        }

    }

    public String getBeginServiceClass() {
        return beginServiceClass;
    }

    private Boolean getEnvBoolean(String envVar) {

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

    public String getBackgroundClass() {

        //  CM:  Get the environment you're running in, set color class accordingly.
        String envClass = "";
        String qSysEnv = System.getenv("QSYSTEM_ENV");

        //  CM:  If null returned, set default background, else process.
        if (qSysEnv == null) {
            qSysEnv = "";
            envClass = "local-background";
        }
        else {
            switch (qSysEnv) {
                case "PROD":
                    envClass = "prod-background";
                    break;
                case "TEST":
                    envClass = "test-background";
                    break;
                case "DEV":
                    envClass = "dev-background";
                    break;
                default:
                    envClass = "local-background";
                    break;

            }
        }

        return envClass;
    }

    /**
     * ??? ????? ???? ?????? include ?? view ? ????? ????????? @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg") This is necessary to do
     * include in the view and then bind
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        //QLog.l().logQUser().debug("Loading page: afterCompose");
        Selectors.wireComponents(view, this, false);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LinkedList<QService> getPreviousList() {
        return this.PreviousList;
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "login", "user", "postponList", "customer",
            "avaitColumn", "officeName", "userList", "currentState", "userListbyOffice" })
    public void login() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Log: Login", "Before", user.getUser(), user.getUser()
                .getCustomer());

        Uses.userTimeZone = (TimeZone) Sessions.getCurrent()
                .getAttribute("org.zkoss.web.preferred.timeZone");
        QLog.l().logQUser().debug("Login : " + user.getName());
        // if (user.getUser().getName().equals("Administrator")) {
        if (user.getUser().getAdminAccess()) {
            user.setGABoard(true);
        }
        QLog.l().logQUser().debug("==> Login GABoard Status: " + user.getGABoard());

        final Session sess = Sessions.getCurrent();
        sess.setAttribute("userForQUser", user);
        customer = user.getUser().getCustomer();

        // TODO for testing
        // need disabled
        user.getPlan().forEach((QPlanService p) -> {
            final CmdParams params = new CmdParams();
            params.serviceId = p.getService().getId();
            params.priority = 2;
            // */todo disabled*/ Executer.getInstance().getTasks().get(Uses.TASK_STAND_IN).process(params, "", new byte[4]);
        });

        setKeyRegimForUser(user);

        // QUser quser = user.getUser();
        Long userId = user.getUser().getId();
        QUser quser = QUserList.getInstance().getById(userId);
        currentState = true;
        quser.setCurrentState(currentState);
        csrQuickTxn.setChecked(false);
        quser.setQuickTxn(csrQuickTxn.isChecked());
        if (quser != null) {
            officeName = user.getUser().getOffice().getName();
        }

        setCFMSAttributes();

        clientDashboardNorth.setSize(checkCFMSHeight);
        clientDashboardNorth.setStyle(checkCFMSHidden);
        clientDashboardNorth.invalidate();

        if (getCFMSType()) {
            btn_invite.setVisible(true);
        }
        else {
            btn_invite.setVisible(false);
        }
        // GA_list.setModel(GA_list.getModel());
        // GA_list.getModel();
        BindUtils.postNotifyChange(null, null, Form.this, "*");

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Log: Login", "After", user.getUser(), user.getUser()
                .getCustomer());
    }

    @Command
    public int servingCSR() {
        LinkedList<QUser> ServingCSRs = getuserListbyOffice();
        // Iterator Iterator = ServingCSRs.iterator();
        Integer counter = 0;
        // LinkedList<String> linkedList = new LinkedList<>();
        for (int i = 0; i < ServingCSRs.size(); i++) {
            if (ServingCSRs.get(i).getCurrentService() == null
                    || "".equals(ServingCSRs.get(i).getCurrentService())) {
                counter = counter;
            }
            else {
                // QLog.l().logQUser().debug("\n WHAT IS THAT: \n" + ServingCSRs.get(i).getCurrentService() + "\n");
                counter++;
            }
        }
        return counter;
    }

    //Get the Logged in CSRs
    @Command
    public int LogginCSR() {
        LinkedList<QUser> LogginCSRs = getuserListbyOffice();
        //        Iterator Iterator = ServingCSRs.iterator();
        Integer counter = 0;
        //        LinkedList<String> linkedList = new LinkedList<>();
        for (int i = 0; i < LogginCSRs.size(); i++) {
            if (LogginCSRs.get(i).getCurrentState()) {
                counter++;
            }
            else {
                //                QLog.l().logQUser().debug("\n WHAT IS THAT: \n" + ServingCSRs.get(i).getCurrentService() + "\n");
                counter = counter;
            }
        }
        return counter;
    }

    @Command
    public void GABoard() {

        //  CM:  Track start.
        Executer.getInstance().TrackUserClick("Log: GABoard", "Before", user.getUser(), user
                .getUser().getCustomer());

        //  CM:  Regular GABoard code.
        GAManagementDialogWindow.setVisible(true);
        GAManagementDialogWindow.doModal();
        CheckGABoard = true;
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        // QLog.l().logQUser().debug("\n\n\n\n Close GA show FLAG: " + user.getGABoard() + "\n\n\n\n");

        //  CM:  Track start.
        Executer.getInstance().TrackUserClick("Log: GABoard", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    // @ContextParam(ContextType.VIEW) Component comp
    @Command
    public void closeGA() {
        //QLog.l().logQUser().debug("==> Start: closeGA");
        CheckGABoard = false;
        GAManagementDialogWindow.addEventListener("onClose", new EventListener() {

            @Override
            public void onEvent(Event event) throws Exception {
                QLog.l().logQUser().debug("    --> Start: onEvent in closeGA");
                // TODO Auto-generated method stub
                event.stopPropagation();
                // GAManagementDialogWindow.detach();
                GAManagementDialogWindow.setVisible(false);

                CheckGABoard = false;

                //                QLog.l().logQUser()
                //                        .debug("        --> user.getGABoard() flag:  " + user.getGABoard());
                //                QLog.l().logQUser().debug("        --> CheckGABoard static var:  " + CheckGABoard);
                //                QLog.l().logQUser().debug("    --> End: onEvent in closeGA");
            }
        });

        //QLog.l().logQUser().debug("==> End: closeGA");
    }

    @Command
    public void ReportBugIndex() {

        //  CM:  Track start, call regular ReportBug, track end.
        //  CM:  Set variables depending on whether null or not.
        QUser trackUser = null;
        trackCust = null;
        if (user != null) {
            trackUser = user.getUser();

            if (trackUser != null) {
                trackCust = trackUser.getCustomer();
            }
        }

        Executer.getInstance().TrackUserClick("Ind: Feedback", "Before", trackUser, trackCust);
        ReportBug();
        Executer.getInstance().TrackUserClick("Ind: Feedback", "After", trackUser, trackCust);
    }

    @Command
    public void ReportBugServe() {
        Executer.getInstance().TrackUserClick("Srv: Feedback", "Before", user.getUser(), user
                .getUser().getCustomer());
        ReportBug();
        Executer.getInstance().TrackUserClick("Srv: Feedback", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    public void ReportBug() {
        ReportingBugWindow.setVisible(true);
        ReportingBugWindow.doModal();
    }

    private Integer getSessionOfficeId() {
        return Integer.parseInt((String) Sessions.getCurrent().getAttribute("office_id"));
    }

    // To get GABoard Customer waiting
    @NotifyChange(value = { "service_list" })
    public int getCustomersCount() {
        int total = 0;
        // Long office_id = Integer.toUnsignedLong(getSessionOfficeId());
        // QOffice office = QOfficeList.getInstance().getById(office_id);
        //
        // total = QServiceTree.getInstance().getNodes()
        // .stream()
        // .filter((service) -> service.getSmartboard().equals("Y"))
        // .map((service) -> service.getCountCustomersByOffice(office))
        // .reduce(total, Integer::sum);
        // QLog.l().logQUser().debug("\n\n\n\n COUNT: " + total + "\n\n\n\n");
        // customersCount = total;
        // total = listServices.size();
        // service_list
        total = service_list.getModel().getSize();
        return total;
    }

    @Command
    public void SendingSlack() {
        String ReportMsg = "";
        String ReportTicket = "";
        String Username = "";
        String BugMsg = ((Textbox) ReportingBugWindow.getFellow("Reportbugs")).getText();
        CSRIcon = ":information_desk_person:";

        // Call Slack Api to connect to address
        //package ru.apertum.qsys.quser;
        //import ru.apertum.qsystem.server.model.QUser;

        SlackApi api = new SlackApi(
                "https://hooks.slack.com/services/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");
        // SlackMessage msg = null;
        SlackMessage msg = new SlackMessage(null);

        if (user.getUser() != null && user.getName() != null) {
            Username = "CSR - " + user.getName();
            if (user.getUser().getCustomer() != null) {
                ReportTicket = user.getUser().getCustomer().getName();
            }
            else {
                ReportTicket = "Ticket number is not provided";
            }
        }
        else {
            Username = "User is not logged in";
        }

        // if (user.getName() !=null && user.getUser().getCustomer()!=null){
        // ReportTicket = user.getUser().getCustomer().getName();
        // }else{
        // ReportTicket = "Ticket numebr is not provided";
        // }
        ReportMsg = ReportMsg + Username + "\n" + "Office Name: " + getOfficeName() + "\n"
                + "Ticket Number: " + ReportTicket + "\n\n" + BugMsg + "\n";

        msg.setIcon(CSRIcon);
        msg.setText(ReportMsg);
        msg.setUsername(Username);
        // api.SlackMessage.setIcon(":information_desk_person:");
        api.call(msg);

        ReportingBugWindow.setVisible(false);
        ((Textbox) ReportingBugWindow.getFellow("Reportbugs")).setText("");
    }

    @Command
    public void CancelReporting() {
        ReportingBugWindow.setVisible(false);

        ((Textbox) ReportingBugWindow.getFellow("Reportbugs")).setText("");
    }

    @Command
    public void about() {
        final Properties settings = new Properties();
        final InputStream inStream = this.getClass()
                .getResourceAsStream("/ru/apertum/qsys/quser/quser.properties");
        try {
            settings.load(inStream);
        }
        catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }

        final Properties settings2 = new Properties();
        final InputStream inStream2 = this.getClass()
                .getResourceAsStream("/ru/apertum/qsystem/common/version.properties");
        try {
            settings2.load(inStream2);
        }
        catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }
        Messagebox.show(
                "*** Plugin QUser ***\n" + "   version " + settings.getProperty("version")
                        + "\n   date "
                        + settings.getProperty("date") + "\n   for QSystem " + settings
                                .getProperty("version_qsystem")
                        + "\n\n*** QMS Apertum-QSystem ***\n" + "   version " + settings2
                                .getProperty("version")
                        + "\n   date " + settings2.getProperty("date") + "\n   DB " + settings2
                                .getProperty("version_db"),
                "QMS Apertum-QSystem", Messagebox.OK, Messagebox.INFORMATION);
    }

    @Command
    @NotifyChange(value = { "user" })
    public void QuickTxnCSRChecked() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Log: CSR QTxn", "Before", user.getUser(), user
                .getUser().getCustomer());

        //  Get user, quick transaction flag, then reset it.
        QUser quser = user.getUser();
        quser.setQuickTxn(csrQuickTxn.isChecked());

        //  More debug.
        //QLog.l().logQUser().debug("    --> Quick start value: " + (save ? "Yes" : "No"));
        //QLog.l().logQUser().debug("    --> New value you want: " + ((!save) ? "Yes" : "No"));
        //QLog.l().logQUser().debug("    --> What got set: " + (quser.getQuickTxn() ? "Yes" : "No"));
        //QLog.l().logQUser().debug("==> End: QuickTxnChecked");

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Log: CSR QTxn", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "login", "user", "postponList", "customer",
            "avaitColumn", "officeName" })
    public void logout() {

        //  CM:  Track start.
        Executer.getInstance().TrackUserClick("Log: Logout", "Before", user.getUser(), user
                .getUser().getCustomer());

        //QLog.l().logQUser().debug("Logout " + user.getName());

        // Set all of the session parameters back to defaults
        setKeyRegim(KEYS_OFF);
        checkCFMSType = false;
        checkCFMSHidden = "display: none;";
        checkCFMSHeight = "0%";
        checkCombo = false;
        customer = null;
        officeName = "";

        // Andrew - to change quser state for GABoard
        QUser quser = user.getUser();
        quser.setQuickTxn(false);
        quser.setCurrentState(false);
        csrQuickTxn.setChecked(false);
        // QLog.l().logQUser().debug("\n\n\n\n COUNT: " + quser.getName() + "\n\n\n\n");
        // QLog.l().logQUser().debug("\n\n\n\n COUNT: " + quser.getCurrentState() + "\n\n\n\n");

        final Session sess = Sessions.getCurrent();
        sess.removeAttribute("userForQUser");
        UsersInside.getInstance().getUsersInside().remove(user.getName() + user.getPassword());
        user.setCustomerList(Collections.<QPlanService> emptyList());
        user.setName("");
        user.setPassword("");
        user.setGABoard(false);

        for (QSession session : QSessions.getInstance().getSessions()) {
            if (user.getUser().getId().equals(session.getUser().getId())) {
                QSessions.getInstance().getSessions().remove(session);
                break;
            }
        }

        clientDashboardNorth.setStyle(checkCFMSHidden);
        clientDashboardNorth.setSize(checkCFMSHeight);
        btn_invite.setVisible(false);

        //  CM:  Track end.
        Executer.getInstance().TrackUserClick("Log: Logout", "After", user.getUser(), user.getUser()
                .getCustomer());
    }

    public LinkedList<QUser> getUsersForLogin() {
        return QUserList.getInstance().getItems();
    }

    public LinkedList<QUser> getuserList() {
        userList = QUserList.getInstance().getItems();
        return userList;
    }

    public boolean isLogin() {
        final Session sess = Sessions.getCurrent();
        final User userL = (User) sess.getAttribute("userForQUser");
        return userL != null;
    }

    /**
     * ???????? ?????????/?????????? ?????? Button on / off mechanism
     */
    public void setKeyRegim(String regim) {
        keys_current = regim;
        btnsDisabled[0] = !(isLogin() && '1' == regim.charAt(0));
        btnsDisabled[1] = !(isLogin() && '1' == regim.charAt(1));
        btnsDisabled[2] = !(isLogin() && '1' == regim.charAt(2));
        btnsDisabled[3] = !(isLogin() && '1' == regim.charAt(3));
        btnsDisabled[4] = !(isLogin() && '1' == regim.charAt(4));
        btnsDisabled[5] = !(isLogin() && '1' == regim.charAt(5));
        btnsDisabled[6] = !(isLogin() && '1' == regim.charAt(6));
        btnsDisabled[7] = !(isLogin() && '1' == regim.charAt(7));
        btnsDisabled[8] = !(isLogin() && '1' == regim.charAt(8));
    }

    public boolean[] getBtnsDisabled() {
        return btnsDisabled;
    }

    public void setBtnsDisabled(boolean[] btnsDisabled) {
        this.btnsDisabled = btnsDisabled;
    }

    public boolean[] getAddWindowButtons() {
        return addWindowButtons;
    }

    public void setAddWindowButtons(boolean[] addWindowButtons) {
        this.addWindowButtons = addWindowButtons;
    }

    public String getOfficeType() {
        return officeType;
    }

    private void setCFMSAttributes() {
        if (getCFMSType()) {
            checkCFMSHidden = "display: inline;";
            checkCFMSHeight = "70%";
            officeType = "reception";
        }
        else {
            checkCFMSHidden = "display: none;";
            checkCFMSHeight = "0%";
            officeType = "non-reception";
        }
    }

    public boolean getCFMSType() {
        if (user == null) {
            return false;
        }
        QUser quser = user.getUser();

        if (quser == null) {
            return false;
        }

        //  Assume not a reception office.
        checkCFMSType = false;

        String qsb = quser.getOffice().getSmartboardType();
        if (qsb.equalsIgnoreCase("callbyticket")) {
            checkCFMSType = true;
        }
        if (qsb.equalsIgnoreCase("callbyname")) {
            checkCFMSType = true;
        }
        return checkCFMSType;
    }

    public boolean isReceptionOffice() {

        //  Just call the getCFMSType function.  This fn has a better name.
        return getCFMSType();
    }

    public void EnableService(boolean enable) {

        //  User wants to enable service.
        //QLog.l().logger().debug("==> EnableService(" + enable + ")");

        //        Button myAdd = (Button) addTicketDailogWindow.getFellow("addAndServeBtn");
        //        if (myAdd != null) {
        //            //QLog.l().logger().debug("    --> Begin button found!!!");
        //            myAdd.setDisabled(true);
        //        }
        //        else {
        //            //QLog.l().logger().debug("    --> Begin button not found ... Sigh ...");
        //        }
    }

    //    @NotifyChange(value = { "pickedRedirectServ" })
    //    public boolean isNoServiceSelected() {
    //
    //        //  CM:  Assume a service is selected.
    //        boolean result = false;
    //
    //        //  No service selected.
    //        if (pickedRedirectServ == null) {
    //            result = true;
    //        }
    //
    //        //  Return the result.
    //        return result;
    //    }
    //
    @Command
    public void serviceSelected() {

        //  CM:  Debugging.
        String service = pickedRedirectServ.getName();
        Executer.getInstance().TrackUserClick("Add: Click Service " + service, "Before", user
                .getUser(), user.getUser().getCustomer());

        //  Call Snowplow to choose service.
        //CallSnowplowChooseService(spId, pickedRedirectServ);

        Executer.getInstance().TrackUserClick("Add: Click Service " + service, "After", user
                .getUser(), user.getUser().getCustomer());
    }

    private void CallSnowplowChooseService(Long spId, QService citizenService) {

        //  Create the arguments that Snowplow needs.
        QUser csr = user.getUser();

        //  Call routine that makes the actual Snowplow call.
        Executer.getInstance().SnowplowChooseService(spId, csr, citizenService);
    }

    public String getCFMSHeight() {
        return checkCFMSHeight;
    }

    public String getCFMSHidden() {
        return checkCFMSHidden;
    }

    public boolean CheckCombobox() {
        if (customer == null) {
            checkCombo = true;
        }
        return checkCombo;
    }

    public void CloseChannelEntry() {
        checkCombo = false;
    }

    public QCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(QCustomer customer) {
        this.customer = customer;
    }

    public String getPriorityCaption(int priority) {
        final String res;
        switch (priority) {
            case 0: {
                res = l("secondary");
                break;
            }
            case 1: {
                res = l("standard");
                break;
            }
            case 2: {
                res = l("high");
                break;
            }
            case 3: {
                res = "V.I.P.";
                break;
            }
            default: {
                res = l("stange");
            }
        }
        return res;
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "customer", "avaitColumn" })
    public void inviteClick() {

        //  CM:  Track the user's click, then call standard invite routine.
        Executer.getInstance().TrackUserClick("Ind: Invite", "Before", user.getUser(), user
                .getUser().getCustomer());
        this.invite();
        Executer.getInstance().TrackUserClick("Ind: Invite", "After", user.getUser(), customer);
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "customer", "avaitColumn" })
    public void invite() {

        //  CM:  See if small time has elapsed since last CSR in this office clicked invite.
        //  CM:  Kludge to prevent two CSRs calling the same citizen.
        Long officeId = user.getUser().getOffice().getId();
        Long lastTime = inviteTimes.get(officeId);
        Long currentTime = System.currentTimeMillis();

        //QLog.l().logQUser().debug("==> Invite: Off: " + officeId + "; Curr: " + currentTime + "; Last: " + lastTime);

        //  CM:  If less than 1 second since last invite in this office, wait.
        if ((currentTime - lastTime) < 1000) {
            QLog.l().logQUser().debug("    --> Have to wait ...");
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch(InterruptedException ex) {
                QLog.l().logQUser().debug("    --> Waiting interrupted.");
            }
            //QLog.l().logQUser().debug("    --> OK, good to go.");
        }

        //  CM:  Update the time of the last invite for this office.
        inviteTimes.put(officeId, currentTime);

        //QLog.l().logQUser().debug("==> Start: invite - Invite by " + user.getName());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();

        // QLog.l().logQUser().debug("\n\n\n\nBEFORE INTO EXCECUTE \n\n\n\n\n");
        final RpcInviteCustomer result = (RpcInviteCustomer) Executer.getInstance().getTasks()
                .get(Uses.TASK_INVITE_NEXT_CUSTOMER).process(params, "", new byte[4]);
        if (result.getResult() != null) {
            customer = result.getResult();
            setKeyRegim(KEYS_INVITED);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
            this.addServeScreen();
        }
        else {
            Messagebox
                    .show(l("no_clients"), l("inviting_next"), Messagebox.OK,
                            Messagebox.INFORMATION);
        }

        service_list.setModel(service_list.getModel());
        refreshListServices();
        service_list.invalidate();
        beginServiceClass = "blink-button";

        //  Debug
        //QLog.l().logQUser().debug("==> End: invite");
    }

    @Command
    public void addServeScreenClick() {

        //  CM:  Track the user's Serve Now click, then call the regular routine.
        Executer.getInstance().TrackUserClick("Ind: Serve Now", "Before", user.getUser(), user
                .getUser().getCustomer());
        this.addServeScreen();
        Executer.getInstance().TrackUserClick("Ind: Serve Now", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    public void addServeScreen() {

    	((Checkbox) serveCustomerDialogWindow.getFellow("inaccurateTimeCheckBox"))
                .setChecked(false);

    	String tempVal = ((Textbox) serveCustomerDialogWindow.getFellow("customer_quantity")).getValue();

    	if (!isNumeric(tempVal)){
        	lastGoodQuantity = customer.getQuantity();
            //QLog.l().logQUser().debug("LastGoodQuantity ==> " +  lastGoodQuantity );
        }else{
        	lastGoodQuantity = tempVal;
            //QLog.l().logQUser().debug("LGQ tempVal ==> " +  tempVal );
        }

        serveCustomerDialogWindow.setVisible(true);
        serveCustomerDialogWindow.doModal();
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "customer" })
    public void kill() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Citizen Left", "Before", user.getUser(), user
                .getUser().getCustomer());

        Messagebox.show("Do you want to remove the client?", "Remove", new Messagebox.Button[]{
                Messagebox.Button.YES, Messagebox.Button.NO}, Messagebox.QUESTION,
                (Messagebox.ClickEvent t) -> {
                    //QLog.l().logQUser().debug("Kill by " + user.getName() + " customer " + customer.getFullNumber());
                    if (t.getButton() != null && t.getButton().compareTo(Messagebox.Button.YES) == 0) {
                        final CmdParams params = new CmdParams();

                        params.userId = user.getUser().getId();
                        Executer.getInstance().getTasks().get(Uses.TASK_KILL_NEXT_CUSTOMER)
                                .process(params, "", new byte[4]);

                        QCustomer saveCustomer = customer;
                        customer.refreshPrevious();
                        customer = null;

                        // Set the current working service to be empty
                        QUser quser = QUserList.getInstance().getById(params.userId);
                        QUser saveUser = quser;
                        quser.setCurrentService("");
                        quser.setCustomer(customer);

                        setKeyRegim(KEYS_MAY_INVITE);
                        service_list.setModel(service_list.getModel());
                        refreshListServices();
                        service_list.invalidate();

                        BindUtils.postNotifyChange(null, null, Form.this, "*");
                        serveCustomerDialogWindow.setVisible(false);

                        Executer.getInstance().TrackUserClick("Srv: Left->Yes", "Answer",
                                saveUser, saveCustomer);
                    }
                    else {
                        Executer.getInstance().TrackUserClick("Srv: Left->No", "Answer",
                                user.getUser(), user.getUser().getCustomer());
                    }
                });

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Citizen Left", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    @NotifyChange(value = { "btnsDisabled" })
    public void beginClick() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Begin Service", "Before", user.getUser(), user
                .getUser().getCustomer());
        begin();
        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Begin Service", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    @NotifyChange(value = { "btnsDisabled" })
    public void begin() {

        this.beginServiceClass = "act-button";
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER)
                .process(params, "", new byte[4]);

        // Andrew - to set quser service for GABoard
        QUser quser = QUserList.getInstance().getById(params.userId);
        quser.setCurrentService(user.getUser().getCustomer().getService().getName());

        setKeyRegim(KEYS_STARTED);
        service_list.setModel(service_list.getModel());
        refreshListServices();
        service_list.invalidate();
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }

    @Command
    public void updateComments() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Comments", "Before", user.getUser(), user
                .getUser().getCustomer());

        // Inheritance the comment from Serve-customer window to hold window
        String tempComment = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments"))
                .getText();
        customer.setTempComments(tempComment);

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Comments", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    public void validateQuantity(@BindingParam("v") String newValue,
            @ContextParam(ContextType.TRIGGER_EVENT) InputEvent event) {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Quantity", "Before", user.getUser(), user
                .getUser().getCustomer());

        QLog.l().logQUser().debug("validateQuantity ==> LastGoodQuantity= " +  lastGoodQuantity );

       // Check if quantity is numeric and display message if not
       if(!isNumeric(newValue)){
    	   ((Textbox) serveCustomerDialogWindow.getFellow("customer_quantity")).setText(lastGoodQuantity);
   			customer.setQuantity(lastGoodQuantity);
        	Messagebox.show(
       				"Please enter a numeric value for quantity.","Quantity Error"
        				, Messagebox.OK, Messagebox.INFORMATION);
       }else{
    	   //save the value
    	   customer.setQuantity(newValue);
    	   //save this as last good value
    	   lastGoodQuantity = newValue;
       }

       //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Quantity", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    public static boolean isNumeric(String str)
    {
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @Command
    public void saveQuantity() {
    	QLog.l().logQUser().debug("saveQuantity ==> LastGoodQuantity= " +  lastGoodQuantity );
    	customer.setQuantity(lastGoodQuantity);
    }

    @Command
    public void postpone() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Place on hold", "Before", user.getUser(), user
                .getUser().getCustomer());

        postponeCustomerDialog.setVisible(true);
        postponeCustomerDialog.doModal();
        BindUtils.postNotifyChange(null, null, Form.this, "*");

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Place on hold", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    public void ReturnedRedirect() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Return to queue", "Before", user.getUser(), user
                .getUser().getCustomer());

        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.resultId = -1L;
        params.priority = Uses.PRIORITY_NORMAL;
        params.isMine = Boolean.FALSE;
        params.welcomeTime = user.getCustomerWelcomeTime();
        params.comments = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments"))
                .getText();

        customer = user.getUser().getCustomer();
        params.serviceId = user.getUser().getCustomer().getService().getId();
        customer.setTempComments(params.comments);

        // Set to User current Comments
        // QUser quser = QUserList.getInstance().getById(params.userId);
        // quser.setCurrentComments(params.comments);

        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_RETURN_QUEUE).process(params, "", new byte[4]);

        customer.setUser(user.getUser());

        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
        refreshListServices();
        service_list.invalidate();
        serveCustomerDialogWindow.setVisible(false);
		customer = null;

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Return to queue", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    @NotifyChange(value = { "addWindowButtons" })
    public void redirect() {
        QLog.l().logQUser().debug("redirect");

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = new CmdParams();
            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.resultId = -1L;
            params.priority = Uses.PRIORITY_NORMAL;
            params.isMine = Boolean.FALSE;
            params.welcomeTime = user.getCustomerWelcomeTime();
            params.comments = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments"))
                    .getText();

            customer.setTempComments(params.comments);

            // Set to User current Comments
            // QUser quser = QUserList.getInstance().getById(params.userId);
            // quser.setCurrentComments(params.comments);

            final QUser redirectUser = QUserList.getInstance().getById(params.userId);
            // ???????????? ?? ????????? ??? ???????????? ??????, ?????? ???????? customerID
            // switch to the custodian with parallel reception, must arrive customerID
            if (params.customerId != null) {
                final QCustomer parallelCust = redirectUser.getParallelCustomers()
                        .get(params.customerId);
                if (parallelCust == null) {
                    QLog.l().logger().warn(
                            "PARALLEL: User have no Customer for switching by customer ID=\""
                                    + params.customerId
                                    + "\"");
                }
                else {
                    redirectUser.setCustomer(parallelCust);
                    QLog.l().logger().debug(
                            "???? \"" + user + "\" ???????????? ?? ????????? \"" + parallelCust
                                    .getFullNumber()
                                    + "\"");
                }
            }

            this.addToQueue(params);

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            refreshListServices();
            service_list.invalidate();
            QLog.l().logQUser().debug("\n\nTEST LIST: " + service_list.getModel() + "\n\n");
            serveCustomerDialogWindow.setVisible(false);
        }
    }

    @Command
    @NotifyChange(value = { "addWindowButtons", "spId" })
    public void backOffice() {

        //  CM:  Track start of Add Citizen via Back Office button.
        Executer.getInstance().TrackUserClick("Ind: Back Office", "Before", user.getUser(), user
                .getUser().getCustomer());

        //  CM:  Create a Snowplow ID number.
        spId = createSpId();
        CallSnowplowAddCitizen(spId);

        //QLog.l().logQUser().debug("addClient");
        user.setCustomerWelcomeTime(new Date());
        addWindowButtons[0] = true;
        addWindowButtons[1] = false;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;
        // customer.setChannels(1);
        pickedRedirectServ = null;
        ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");
        this.addTicketScreen(true, true, false);

        //  CM:  Track end of Add Citizen
        Executer.getInstance().TrackUserClick("Ind: Back Office", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    @NotifyChange(value = { "addWindowButtons", "spId" })
    public void addClient() {

        //  CM:  Track start of Add Citizen
        Executer.getInstance().TrackUserClick("Ind: Add Citizen", "Before", user.getUser(), user
                .getUser().getCustomer());

        //  CM:  Create a Snowplow ID number, then call Snowplow.
        spId = createSpId();
        CallSnowplowAddCitizen(spId);

        //QLog.l().logQUser().debug("addClient");
        user.setCustomerWelcomeTime(new Date());
        addWindowButtons[0] = true;
        addWindowButtons[1] = false;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;
        // customer.setChannels(1);
        pickedRedirectServ = null;
        ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");
        this.addTicketScreen(true, false, false);

        //  CM:  Track end of Add Citizen
        Executer.getInstance().TrackUserClick("Ind: Add Citizen", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    private Long createSpId() {
        Long officeCSR = (1000L * (Long) user.getUser().getOffice().getId()) +
                user.getUser().getId();
        Long timeNow = new Date().getTime();
        return (10000000000000L * officeCSR) + timeNow;
    }

    private void CallSnowplowAddCitizen(Long spId) {

        //  Create the arguments that Snowplow needs.
        QUser csr = user.getUser();

        //  Call routine that makes the actual Snowplow call.
        Executer.getInstance().SnowplowAddCitizen(spId, csr);

    }

    @Command
    @NotifyChange(value = { "addWindowButtons" })
    public void addNextService() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Add Next Service", "Before", user.getUser(),
                customer);

        // Save the customer to the DB before adding a service, so the service_quantity persists
        customer.save();

        addWindowButtons[0] = false;
        addWindowButtons[1] = false;
        addWindowButtons[2] = true;
        addWindowButtons[3] = false;
        // refresh the service list. Remove the default service selection
        pickedRedirectServ = null;
        this.addTicketScreen(false, false, false);

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Add Next Service", "After", user.getUser(),
                user.getUser().getCustomer());
    }

    @Command
    public void disableButtons() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Inaccurate Time", "Before", user.getUser(), user
                .getUser().getCustomer());

        // addWindowButtons[0] = false;
        // addWindowButtons[1] = false;
        // addWindowButtons[2] = false;
        // addWindowButtons[3] = false;
        // addWindowButtons[4] = false;
        // addWindowButtons[5] = false;
        // addWindowButtons[6] = false;
        // addWindowButtons[7] = false;
        // addWindowButtons[8] = false;
        boolean[] inaccurateChecked = new boolean[] { true, true, true, true, true, false, true,
                true,
                true };
        if (((Checkbox) serveCustomerDialogWindow.getFellow("inaccurateTimeCheckBox"))
                .isChecked()) {
            setBtnsDisabled(inaccurateChecked);
        }
        else {
            setKeyRegim(KEYS_STARTED);
        }

        // setBtnsDisabled(inaccurateChecked);
        // QLog.l().logQUser().debug("\n\n\n\n DISABLE BUTTON");
        BindUtils.postNotifyChange(null, null, Form.this, "*");

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Inaccurate Time", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "customer" })
    public void finish() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Finish", "Before", user.getUser(), user
                .getUser().getCustomer());

        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();

        params.resultId = -1L;
        params.textData = "";
        params.inAccurateFinish = ((Checkbox) serveCustomerDialogWindow
                .getFellow("inaccurateTimeCheckBox")).isChecked();

        final RpcStandInService res = (RpcStandInService) Executer.getInstance().getTasks()
                .get(Uses.TASK_FINISH_CUSTOMER).process(params, "", new byte[4]);
        // ???????? ???????? ? ???????? ?? ??? ?? ????? ? ?? ?????? ?????. ?????? ???????????? ??? ?????? ????????? ? ????????? ??????????? ?????? ?
        // ????????????
        if (res.getResult() != null && res.getResult().getService() != null
                && res.getResult().getState() == CustomerState.STATE_WAIT_COMPLEX_SERVICE) {
            Messagebox.show(
                    l("next_service") + " \"" + res.getResult().getService().getName() + "\". " + l(
                            "customer_number") + " \"" + res.getResult().getPrefix()
                            + res.getResult()
                                    .getNumber()
                            + "\"." + "\n\n" + res.getResult().getService().getDescription(),
                    l("contumie_complex_service"), Messagebox.OK, Messagebox.INFORMATION);
        }

        customer.refreshPrevious();
        customer = null;

        // Set the current working service to be empty
        QUser quser = QUserList.getInstance().getById(params.userId);
        quser.setCurrentService("");

        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
        refreshListServices();
        service_list.invalidate();

        BindUtils.postNotifyChange(null, null, Form.this, "*");
        serveCustomerDialogWindow.setVisible(false);

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Finish", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    public QCustomer getPickedCustomer() {
        return pickedCustomer;
    }

    public void setPickedCustomer(QCustomer pickedCustomer) {
        this.pickedCustomer = pickedCustomer;
    }

    public QPlanService getPickedService() {
        return pickedService;
    }

    public void setPickedService(QPlanService pickedService) {
        this.pickedService = pickedService;
    }

    @Command
    // public void clickList(@BindingParam("st") String st) {
    public void clickListServices() {
    }

    @Command
    public void inviteCustomerNow() {
        // 1. Postpone the customer
        // 2. Pick the customer from Postponed list

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Q: Select Wait Queue", "Before", user.getUser(),
                pickedCustomer);

        Boolean OkToContinue = true;
        trackCust = null;
        this.beginServiceClass = "blink-button";

        if (pickedCustomer == null || keys_current == KEYS_INVITED || keys_current == KEYS_STARTED
                || keys_current == KEYS_OFF) {
            OkToContinue = false;
        }

        //  CM:  See if OK to continue.
        if (OkToContinue) {

            //  See if "In sequence" state has expired (5 seconds).
            Long dateNow = (new Date()).getTime();
            Boolean sequenceTimeOut = (dateNow - pickedCustomer.getStandTime().getTime()) > 5000;
            String sto = (sequenceTimeOut ? "Yes" : "No");

            //  CM:  Debug.
            //            QLog.l().logQUser().debug("==> Pick: " + pickedCustomer.getName() + "; Seq: "
            //                    + (pickedCustomer.getIsInSequence() ? "Yes" : "No") + "; State: "
            //                    + pickedCustomer.getStateIn() + "; STO: " + sto);

            //  CM:  Can't pick a customer if they are in a add to queue, invite, begin sequence.
            if (pickedCustomer.getIsInSequence() && (!sequenceTimeOut)) {
                OkToContinue = false;
                Messagebox.show(
                        "Another CSR is serving this citizen.  Please choose another citizen.",
                        "Error picking customer from wait queue", Messagebox.OK,
                        Messagebox.INFORMATION);
            }
        }

        //  CM:  See if OK to continue.
        if (OkToContinue) {
            final CmdParams params = new CmdParams();

            //  New "customer already picked" test.
            Object[] msg = { "" };
            if (!(boolean) Executer.getInstance().CustomerCanBeCalled(pickedCustomer, msg,
                    "WaitQ")) {
                Messagebox.show(msg[0].toString(), "Error picking customer from wait queue",
                        Messagebox.OK,
                    Messagebox.INFORMATION);
            }

            else {
                params.userId = user.getUser().getId();
                params.postponedPeriod = 0;
                params.customerId = pickedCustomer.getId();
                params.isMine = Boolean.TRUE;
                user.getUser().setCustomer(pickedCustomer);

                Executer.getInstance().getTasks().get(Uses.TASK_INVITE_SELECTED_CUSTOMER).process(params, "", new byte[4], pickedCustomer);
                customer = null;

                service_list.setModel(service_list.getModel());
                refreshListServices();
                service_list.invalidate();

                Executer.getInstance().getTasks().get(Uses.TASK_INVITE_POSTPONED).process(params, "", new byte[4]);
                customer = user.getUser().getCustomer();
                trackCust = customer;

                setKeyRegim(KEYS_INVITED);
                BindUtils.postNotifyChange(null, null, Form.this, "*");
                pickedRedirectServ = pickedCustomer.getService(); // for returning to queue use
                pickedCustomer = null; // TEST andrew //debug the clicking white space inviting problem
                this.addServeScreen();
            }
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Q: Select Wait Queue", "After", user.getUser(),
                trackCust);
    }

    public LinkedList<String> getPrior_St() {
        return prior_St;
    }

    public void setPrior_St(LinkedList<String> prior_St) {
        this.prior_St = prior_St;
    }

    @Command
    @NotifyChange(value = { "user" })
    public void closeChangePriorityDialog() {
        changeServicePriorityDialog.setVisible(false);
    }

    public String getAvaitColumn() {
        return user.getTotalLineSizeStr();
    }

    // Long userId = user.getUser().getId();
    // QUser quser = QUserList.getInstance().getById(userId);
    // for(int i=0; i<getuserList().size(); i++){
    // QLog.l().logQUser().debug("\n\n\n\nBEFORE GETTING TRUE: STATE LOOP: "+ getuserList().get(i).getName() + ": " + getuserList().get(i).getCurrentState() +
    // "\n\n\n\n");
    // }
    // currentState = true;
    // quser.setCurrentState(currentState);

    @Command
    @NotifyChange(value = { "service_list", "currentState", "userList", "userListbyOffice" })
    public void refreshGAList() {
        if (isLogin()) {
            // QLog.l().logger().debug("\n\n\n\nGABOARD VISIBILITY: \n" + CheckGABoard + "\n\n");
            // QLog.l().logger().debug("\n\n\n\nGABOARD VISIBILITY user.getGABoard(): \n" + user.getGABoard() + "\n\n");
            // user.getGABoard()
            if (CheckGABoard == true) {
                // UsersInside.getInstance().getUsersInside()
                // .put(user.getName() + user.getPassword(), new Date().getTime());
                // QSessions.getInstance()
                // .update(user.getUser().getId(), Sessions.getCurrent().getRemoteHost(),
                // Sessions.getCurrent().getRemoteAddr().getBytes());
                ////
                // Long userId = user.getUser().getId();
                // QUser quser = QUserList.getInstance().getById(userId);
                // quser.setCurrentState(currentState);

                // aftercompose(#GAManagementDialog);
                GAManagementDialogWindow.doModal();
                Label CW = (Label) GAManagementDialogWindow.getFellow("GA_CW");
                String S_CW = new Integer(getCustomersCount()).toString();
                CW.setValue(S_CW);

                Label SC = (Label) GAManagementDialogWindow.getFellow("GA_SC");
                String S_SC = new Integer(servingCSR()).toString();
                SC.setValue(S_SC);

                Label LC = (Label) GAManagementDialogWindow.getFellow("GA_LC");
                String S_LC = new Integer(LogginCSR()).toString();
                LC.setValue(S_LC);
            }
            // BindUtils.postNotifyChange(null, null, Form.this, "*");
        }
    }

    @Command
    @NotifyChange(value = { "postponList", "avaitColumn" })
    public void refreshListServices() {
        if (isLogin()) {
            // ??? ??????????? ?????? ??? ? ??? ?????????? Here the maintenance of the session as a web application
            UsersInside.getInstance().getUsersInside().put(user.getName() + user.getPassword(), new Date().getTime());
            // ??? ??????????? ?????? ??? ??????????????? ????? ? ??? Here the maintenance of the session as a logged user in the MSA
            QSessions.getInstance()
                    .update(user.getUser().getId(), Sessions.getCurrent().getRemoteHost(), Sessions.getCurrent().getRemoteAddr().getBytes());

            final StringBuilder st = new StringBuilder();
            int number = user.getPlan().size();

            user.getPlan().forEach((QPlanService p) -> {
                st.append(user.getLineSize(p.getService().getId()));
            });

            if (!oldSt.equals(st.toString())) {
                List<QPlanService> plans = user.getPlan();
                user.setCustomerList(plans);
                service_list.setModel(service_list.getModel());
                oldSt = st.toString();
                Sort();
                BindUtils.postNotifyChange(null, null, Form.this, "*");
            }
        }
    }

//    @Command
//    public void refreshQuser(){
//        final Session sess = Sessions.getCurrent();
//        if (sess==null) {
//            Executions.getCurrent().sendRedirect("");
//        }
//    }

    @Command
    public void closePostponeCustomerDialog() {
        postponeCustomerDialog.setVisible(false);
        ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).setText("");
        BindUtils.postNotifyChange(null, null, Form.this, "*");
        serveCustomerDialogWindow.setVisible(false);
    }

    @Command
    @NotifyChange(value = { "postponList", "customer", "btnsDisabled" })
    public void OKPostponeCustomerDialog() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Hold: Yes", "Before", user.getUser(),
                user.getUser().getCustomer());

        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.postponedPeriod = ((Combobox) postponeCustomerDialog.getFellow("timeBox")).getSelectedIndex() * 5;
        params.comments = ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).getText();
        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_TO_POSTPON).process(params, "", new byte[4]);
        QCustomer saveCustomer = customer;
        customer = null;

        QUser quser = QUserList.getInstance().getById(params.userId);
        quser.setCurrentService("");
        quser.setCustomer(customer);

        setKeyRegim(KEYS_MAY_INVITE);
        postpone_list.setModel(postpone_list.getModel());
        postponeCustomerDialog.setVisible(false);
        serveCustomerDialogWindow.setVisible(false);
        ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).setText("");
        BindUtils.postNotifyChange(null, null, Form.this, "*");

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Hold: Yes", "After", quser,
                saveCustomer);
    }

    @Command
    public void DetermineChannels() {

        //  CM:  Initialize variables, get initial channel text for debugging.
        String currentChannelText = "";

        if (getCFMSType()) {
            currentChannelText = ((Combobox) addTicketDailogWindow
                    .getFellow("reception_Channels_options")).getSelectedItem().getValue();
        }
        else {
            currentChannelText = ((Combobox) addTicketDailogWindow
                    .getFellow("general_Channels_options")).getSelectedItem().getValue();
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Change Channel " + currentChannelText, "Before",
                user.getUser(), user.getUser().getCustomer());

        // CM:  NOTE:  Code below doesn't appear to have any impact, as
        //      channelIndex not global, bound, passed back, or used in other routines.
        if (getCFMSType()) {
            int channelIndex = ((Combobox) addTicketDailogWindow
                    .getFellow("reception_Channels_options")).getSelectedIndex() + 1;
        }
        else {
            int channelIndex = ((Combobox) addTicketDailogWindow
                    .getFellow("general_Channels_options")).getSelectedIndex() + 1;
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Change Channel " + currentChannelText, "After",
                user.getUser(), user.getUser().getCustomer());
    }

    @Command
    public void ChangeChannels() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Change Channel", "Before", user.getUser(), user
                .getUser().getCustomer());

        int channelIndex = ((Combobox) serveCustomerDialogWindow.getFellow("Change_Channels"))
                .getSelectedIndex() + 1;
        String channels = ((Combobox) serveCustomerDialogWindow.getFellow("Change_Channels"))
                .getSelectedItem().getValue().toString();
        customer.setChannels(channels);
        customer.setChannelsIndex(channelIndex);

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Change Channel", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    public void refreshChannels() {
        //QLog.l().logger().debug("refreshChannels");
        if (getCFMSType()) {
            ((Combobox) addTicketDailogWindow.getFellow("reception_Channels_options"))
                    .setSelectedIndex(0);
        }
        else {
            ((Combobox) addTicketDailogWindow.getFellow("general_Channels_options"))
                    .setSelectedIndex(0);
        }
        // ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).setSelectedIndex(0);
    }

    public LinkedList<QCustomer> getPostponList() {
//        QLog.l().logger().debug("getPostponList");
        LinkedList<QCustomer> postponedCustomers = QPostponedList.getInstance().getPostponedCustomers();
        postponedCustomers = filterPostponedCustomersByUser(postponedCustomers);
        return postponedCustomers;
    }

    public LinkedList<QResult> getResultList() {
        return QResultList.getInstance().getItems();
    }

    public QCustomer getPickedPostponed() {
        return pickedPostponed;
    }

    public void setPickedPostponed(QCustomer pickedPostponed) {
        this.pickedPostponed = pickedPostponed;
    }

    @Command
    public void clickListPostponedChangeStatus() {
        ((Combobox) changePostponedStatusDialog.getFellow("pndResultBox"))
                .setText(pickedPostponed.getPostponedStatus());
        changePostponedStatusDialog.setVisible(true);
        changePostponedStatusDialog.doModal();
    }

    @Command
    @NotifyChange(value = { "postponList" })
    public void closeChangePostponedStatusDialog() {
        final CmdParams params = new CmdParams();
        // ????
        params.customerId = pickedPostponed.getId();
        // ?? ???
        params.textData = ((Combobox) changePostponedStatusDialog.getFellow("pndResultBox"))
                .getText();
        Executer.getInstance().getTasks().get(Uses.TASK_POSTPON_CHANGE_STATUS)
                .process(params, "", new byte[4]);

        changePostponedStatusDialog.setVisible(false);
    }

    @Command
    public void clickListPostponedInvite() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Q: Select Hold Queue", "Before", user.getUser(),
                pickedPostponed);

        //  CM:  Variable to see if OK to process.
        Boolean OkToContinue = true;
        Object[] msg = { "" };
        trackCust = null;

        //  CM:  Ensure pickedPostponed isn't null.
        if (user.getPlan().isEmpty() || pickedPostponed == null) {
            OkToContinue = false;
        }

        //  CM:  Make sure the customer picked hasn't already been picked by someone else.
        if (OkToContinue) {
            if (!(boolean) Executer.getInstance().CustomerCanBeCalled(pickedPostponed, msg,
                    "HoldQ")) {
                Messagebox.show(msg[0].toString(), "Error picking customer from hold queue",
                        Messagebox.OK,
                        Messagebox.INFORMATION);
                OkToContinue = false;
            }
        }

        if (OkToContinue) {
            Messagebox.show("Do you want to invite citizen " + pickedPostponed.getFullNumber() + " ?",
                    l("inviting_client"), new Messagebox.Button[] {
                            Messagebox.Button.YES, Messagebox.Button.NO
                    },
                    Messagebox.QUESTION,
                    (Messagebox.ClickEvent t) -> {

                        //  CM:  Only proceed if you can still call the customer.
                        if ((t.getButton() != null)
                                && (t.getButton().compareTo(Messagebox.Button.YES) == 0)) {

                            //QLog.l().logger().debug("--> Checking customer can be called from queue.");

                            if ((boolean) Executer.getInstance().CustomerCanBeCalled(pickedPostponed, msg, "HoldQ(2)")) {

                                final CmdParams params = new CmdParams();
                                // @param userId id ????? ??????? ???????? The user who causes
                                // @param id ??? ID ????????? ???????? ???????? ?? ???? ??????????, ??? ???? ?.?. ? ??????????? ????? ????????
                                // It is the ID of the caller which is called from the pool of deferred, it is because With a long-stroke tool we have been working for
                                // a long time
                                params.customerId = pickedPostponed.getId();
                                params.userId = user.getUser().getId();
                                Executer.getInstance().getTasks().get(Uses.TASK_INVITE_POSTPONED)
                                        .process(params, "", new byte[4]);
                                customer = user.getUser().getCustomer();
                                trackCust = customer;

                                setKeyRegim(KEYS_INVITED);
                                BindUtils.postNotifyChange(null, null, Form.this, "postponList");
                                BindUtils.postNotifyChange(null, null, Form.this, "customer");
                                BindUtils.postNotifyChange(null, null, Form.this, "btnsDisabled");

                                //  CM:  Tracking.
                                Executer.getInstance().TrackUserClick("Q: Invite->Yes", "Answer",
                                        user.getUser(), trackCust);

                                this.addServeScreen();
                                this.begin();
                            }
                            else {

                                //  CM:  Another CSR served the customer.
                                Messagebox.show(
                                        msg[0].toString(),
                                        "Error picking customer from hold queue",
                                        Messagebox.OK,
                                        Messagebox.INFORMATION);
                            }
                        }

                        //  CM:  User answered "No" to do you want to invite question.
                        else {
                            //  CM:  Tracking.
                            Executer.getInstance().TrackUserClick("Q: Invite->No", "Answer",
                                    user.getUser(), pickedPostponed);
                        }

                        //  CM:  Whether served or not, set customer to be null.
                        pickedPostponed = null;
                    }
            );
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Q: Select Hold Queue", "After", user.getUser(),
                trackCust);
    }

    public TreeServices getTreeServs() {
        return treeServs;
    }

    public QService getPickedMainService() {
        return pickedMainService;
    }

    // MW
    public void setPickedMainService(QService pickedMainService) {
        this.pickedMainService = pickedMainService;
        //QLog.l().logQUser().debug("Set Main Service: " + getPickedMainService());
    }

    @Command
    public void closeAddTicketScreen() {
        addTicketDailogWindow.setVisible(false);
        addTicketDailogWindow.doModal();
    }

    @Command
    public void addTicketScreen(boolean newCustomer, boolean backOffice, boolean saveComments) {

        //  Debugging
        //QLog.l().logQUser().debug("==> Start: addTicketScreen");

        // CM:  Remove previous comments and categories searched
        this.refreshChannels();
        this.refreshAddWindow(newCustomer, saveComments);
        //this.refreshChannels();

        //  CM:  If a backoffice transaction, preselect this category.
        if (backOffice) {
            ((Combobox) addTicketDailogWindow.getFellow("reception_Channels_options")).setSelectedIndex(2);
            ((Combobox) addTicketDailogWindow.getFellow("general_Channels_options")).setSelectedIndex(2);
        }

        //  CM:  Make add ticket window visible, transfer control.
        addTicketDailogWindow.setVisible(true);
        addTicketDailogWindow.doModal();

        //  Debugging.
        // QLog.l().logQUser().debug("==> End: addTicketScreen");
    }

    public void refreshAddWindow(boolean newCustomer, boolean saveComments) {

        //  CM:  You're about to display the addTicketDialog window.  Set all fields appropriately.
        //  CM:  For add, change, next options, comments always blank.
        String msg = "";
        String oldComments = "";

        //  CM:  Get checkbox field.
        Checkbox quickTxn = (Checkbox) addTicketDailogWindow.getFellow("QuickTxnCust");

        //  CM:  For change and next options (existing customer), retain some old values.
        if (!newCustomer) {

            //  CM:  There is a customer.
            msg += "==> RefreshAddWin: Cust: " + customer.getFullNumber();

            //  CM:  Retain reception or non-reception channel field, depending on office type.
            if (getCFMSType()) {
                msg += "; Reception; Channel: " + customer.getChannels();
                ((Combobox) addTicketDailogWindow.getFellow("reception_Channels_options"))
                        .setSelectedIndex(customer.getChannelsIndex() - 1);
            }
            else {
                msg += "; NonReception; Channel: " + customer.getChannels();
                ((Combobox) addTicketDailogWindow.getFellow("general_Channels_options"))
                        .setSelectedIndex(customer.getChannelsIndex() - 1);
            }

            //  CM:  Retain QuickTxn value.  Disable QuickTxn box.
            quickTxn.setChecked(customer.getTempQuickTxn());
            quickTxn.setDisabled(true);

            //  CM:  Save comments if the saveComments flag set.
            if (saveComments) {
                oldComments = customer.getTempComments();
                ((Textbox) addTicketDailogWindow.getFellow("reception_ticket_comments"))
                        .setText(oldComments);
                ((Textbox) addTicketDailogWindow.getFellow("general_ticket_comments"))
                        .setText(oldComments);
            }
            //  CM:  If not saving comments, set to blank.
            else {
                ((Textbox) addTicketDailogWindow.getFellow("reception_ticket_comments"))
                        .setText("");
                ((Textbox) addTicketDailogWindow.getFellow("general_ticket_comments")).setText("");
            }
        }

        //  CM:  No customer.  Make sure QuickTxn has default value.
        else {
            msg += "==> RefreshAddWin: No current customer";
            quickTxn.setChecked(false);
            quickTxn.setDisabled(false);

            //  This is a new customer.  Clear the comment field.
            ((Textbox) addTicketDailogWindow.getFellow("reception_ticket_comments"))
                    .setText("");
            ((Textbox) addTicketDailogWindow.getFellow("general_ticket_comments")).setText("");
        }

        //  CM:  Debug
        //QLog.l().logger().debug(msg);

        //  CM:  For add, change, next options, service and category always blank.
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText("");
        ((Combobox) addTicketDailogWindow.getFellow("cboFmCompress")).setText("");

        // Reset focus, if not reception.
        if (!getCFMSType()) {
            ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setFocus(true);
        }

        listServices = getAllListServices();
        pickedMainService = null;
        BindUtils.postNotifyChange(null, null, Form.this, "listServices");
    }

    public void onSelect$cboFmCompress(Event event) {
        QLog.l().logQUser()
                .debug("C: ----" + cboFmCompress.getSelectedItem().getValue().toString());
    }

    @Listen("onChange = #categoriesCombobox")
    public void changeCategories() {
        String category = cboFmCompress.getValue();
        QLog.l().logQUser().debug(
                "C:" + category + " , " + pickedMainService.getName() + " , " + pickedMainService
                        .getId()
                        + " , " + pickedMainService.getParentId());
    }

    public String getFilter() {
        return filter;
    }

    @NotifyChange
    public void setFilter(String filter) {
        this.filter = filter;
    }

    private LinkedList<QUser> filterusersByOffice(LinkedList<QUser> AllUsers) {
        LinkedList<QUser> Nusers = new LinkedList<QUser>();
        if (isLogin()) {
            Long userId = user.getUser().getId();
            QUser quser = QUserList.getInstance().getById(userId);
            for (int i = 0; i < AllUsers.size(); i++) {

                if (AllUsers.get(i).getOffice().getId() == quser.getOffice().getId()) {
                    Nusers.add(AllUsers.get(i));
                }
            }
        }

        return Nusers;
    }

    public LinkedList<QUser> getuserListbyOffice() {
        userList = QUserList.getInstance().getItems();
        userListbyOffice = filterusersByOffice(userList);

        //  CM:  Sort the list if more than one element.
        if (userListbyOffice.size() > 1) {
            Comparator sortUser = new QUser.QUserComparator();
            userListbyOffice.sort(sortUser);
        }

        return userListbyOffice;
    }

    public LinkedList<QUser> getNewOfficeusers() {
        userList = QUserList.getInstance().getItems();
        // LinkedList<QUser> UsersbyOffice = new LinkedList<QUser>();
        return filterusersByOffice(userList);
    }

    private LinkedList<QCustomer> filterPostponedCustomersByUser(LinkedList<QCustomer> customers) {

        if (this.user != null && this.user.getUser() != null) {
            QOffice userOffice = this.user.getUser().getOffice();
            // QLog.l().logger().debug("Filtering by office: " + userOffice);

            if (userOffice != null) {
                customers = customers
                        .stream()
                        .filter((QCustomer c) -> (userOffice.equals(c.getOffice())))
                        .collect(Collectors.toCollection(LinkedList::new));
            }
        }
        else {
            //QLog.l().logger().debug("Office is null");
        }

        return customers;
    }

    private List<QService> filterServicesByUser(List<QService> services) {

        if (this.user != null && this.user.getUser() != null) {
            List<QPlanService> officePlanServices = user.getUser().getPlanServices();
            List<QService> officeServices = new LinkedList<QService>();

            for (QPlanService q : officePlanServices) {
                officeServices.add(q.getService());
            }

            services = services
                    .stream()
                    .filter((QService service) -> officeServices.contains(service))
                    .collect(Collectors.toList());
        }

        return services;
    }

    public String getFilterCa() {
        return filterCa;
    }

    @NotifyChange
    public void setFilterCa(String filterCa) {
        this.filterCa = filterCa;
    }

    @NotifyChange("listServices")
    @Command
    public void changeCategory(InputEvent event) {

        //  CM:  Tracking.
        String newCategory = pickedMainService.getName();
        Executer.getInstance().TrackUserClick("Add: Select Category " + newCategory, "Before",
                user.getUser(), user.getUser().getCustomer());

        //  CM:  If you change the category, clear the selected service.
        pickedRedirectServ = null;
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText("");
        listServices = FilterServicesByCategory(false);

        Executer.getInstance().TrackUserClick("Add: Select Category " + newCategory, "After",
                user.getUser(), user.getUser().getCustomer());
    }

    private List<QService> FilterServicesByCategory(boolean BackOffice) {

        //  CM:  Initialize variables.
        List<QService> returnServices = null;
        LinkedList<QService> allServices = QServiceTree.getInstance().getNodes();
        QService backOffice = null;
        List<QService> requiredServices = null;

        //  CM:  If you are filtering by back office, find back office service, set it to be pickedMainService.
        if (BackOffice) {
            for (QService next : allServices) {
                if ("back office".equals(next.getName().toLowerCase())) {
                    setPickedMainService(next);
                    break;
                }
            }
        }

        //  CM:  Continue on as normal.
        if (getPickedMainService() == null) {
            //QLog.l().logQUser().debug("null category was selected");
            requiredServices = allServices
                    .stream()
                    .filter(
                            (QService service) -> service.getParentId() != null
                                    && (service.getParent()
                                            .getName()
                                            .toLowerCase().contains(
                                                    ((Combobox) addTicketDailogWindow
                                                            .getFellow("cboFmCompress")).getValue()
                                                                    .toLowerCase()))
                                    && !service.getParentId().equals(1L))
                    .collect(Collectors.toList());
            //QLog.l().logQUser().debug("The getvalue() returns :");

        }
        else {
            //QLog.l().logQUser().debug("--> Category selected: " + pickedMainService.getName());
            requiredServices = allServices
                    .stream()
                    .filter(
                            (QService service) -> service.getParentId() != null
                                    && (service.getParent()
                                            .getName()
                                            .toLowerCase()
                                            .contains(pickedMainService.getName().toLowerCase()))
                                    && !service
                                            .getParentId().equals(1L))
                    .collect(Collectors.toList());
        }

        returnServices = filterServicesByUser(requiredServices);
        return returnServices;
    }

    // Andrew
    // onChanging category updates the category searching algorithm, searching while typing
    @NotifyChange("listServices")
    @Command
    public void changingCategory(@BindingParam("v") String value,
            @ContextParam(ContextType.TRIGGER_EVENT) InputEvent event) {

        //  CM:  If you start changing the category, clear the selected service.
        pickedRedirectServ = null;
        //EnableService(false);

        listServices.clear();
        LinkedList<QService> allServices = QServiceTree.getInstance().getNodes();
        List<QService> requiredServices = null;

        if (getPickedMainService() == null) {
            //QLog.l().logQUser().debug("null category was selected");
            requiredServices = allServices
                    .stream()
                    .filter(
                            (QService service) -> service.getParentId() != null
                                    && (service.getParent()
                                            .getName()
                                            .toLowerCase().contains(event.getValue().toLowerCase()))
                                    && !service
                                            .getParentId()
                                            .equals(1L))
                    .collect(Collectors.toList());
        }
        else {
            //QLog.l().logQUser().debug("Category " + pickedMainService.getName() + " was selected");
            requiredServices = allServices
                    .stream()
                    .filter(
                            (QService service) -> service.getParentId() != null
                                    && (service.getParent()
                                            .getName()
                                            .toLowerCase().contains(event.getValue().toLowerCase()))
                                    && !service
                                            .getParentId()
                                            .equals(1L))
                    .collect(Collectors.toList());
        }

        listServices = filterServicesByUser(requiredServices);
    }

    //@NotifyChange("listServices pickedRedirectServ")
    @NotifyChange("listServices")
    @Command
    public void doSearch() {

        //  CM:  If you start typing, clear the selected service.
        //EnableService(false);

        //  CM:  Get the new filter string.
        filter = ((Textbox) addTicketDailogWindow.getFellow("typeservices")).getText();

        //  CM:  Reset the selected service.
        pickedRedirectServ = null;

        listServices.clear();
        LinkedList<QService> allServices = QServiceTree.getInstance().getNodes();
        List<QService> requiredServices;

        if (pickedMainService == null) {
            requiredServices = allServices
                    .stream()
                    .filter((QService service) -> service.getParentId() != null
                            && (service.getDescription().toLowerCase()
                                    .contains(filter.toLowerCase())
                                    || service
                                            .getParent().getName().toLowerCase()
                                            .contains(filter.toLowerCase())
                                    || service
                                            .getName().toLowerCase().contains(filter.toLowerCase()))
                            && !service
                                    .getParentId()
                                    .equals(1L))
                    .collect(Collectors.toList());

        }
        else {
            requiredServices = allServices
                    .stream()
                    .filter((
                            QService service) -> service.getParentId() != null
                                    && (service.getDescription().toLowerCase()
                                            .contains(pickedMainService.getName().toLowerCase())
                                            || service.getParent()
                                                    .getName().toLowerCase()
                                                    .contains(pickedMainService.getName()
                                                            .toLowerCase())
                                            || service.getName().toLowerCase()
                                                    .contains(pickedMainService.getName()
                                                            .toLowerCase()))
                                    && (service.getDescription().toLowerCase()
                                            .contains(filter.toLowerCase())
                                            || service
                                                    .getParent().getName().toLowerCase()
                                                    .contains(filter.toLowerCase())
                                            || service
                                                    .getName().toLowerCase()
                                                    .contains(filter.toLowerCase()))
                                    && !service
                                            .getParentId()
                                            .equals(1L))
                    .collect(Collectors.toList());
        }
        listServices = filterServicesByUser(requiredServices);
    }

    public List<QService> getListServices() {

        if (listServices == null) {
            listServices = getAllListServices();
        }
        return listServices;
    }

    public List<QService> getAllListServices() {
        LinkedList<QService> allServices = QServiceTree.getInstance().getNodes();

        List<QService> requiredServices = allServices
                .stream()
                .filter((QService service) -> service.getParentId() != null
                        && !service.getParentId()
                                .equals(1L))
                .collect(Collectors.toList());

        return filterServicesByUser(requiredServices);
    }

    public List<QService> getCategories() {
        List<Long> userServiceParentIds = getAllListServices()
                .stream()
                .map(QService::getParentId)
                .collect(Collectors.toList());

        return QServiceTree.getInstance().getNodes()
                .stream()
                .filter((QService service) -> service.getParentId() != null
                        && service.getParentId().equals(1L)
                        && userServiceParentIds.contains(service.getId()))
                .collect(Collectors.toList());
    }

    @Command
    public void closeAddNextServiceDialog() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Apply Next Service", "Before", user.getUser(),
                user.getUser().getCustomer());

        //  Save start customer ID, to check for switching customers in the Add to queue, invite, begin sequence.
        QCustomer customerStart = user.getUser().getCustomer();
        Long customerIdStart = customerStart.getId();
        String ticketStart = customerStart.getName();

        //  String to save comments in.
        String custComments = "";
        Boolean OkToContinue = true;

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            //  Make a Snowplow call.  Service wanted has been decided on.
            CallSnowplowChooseService(spId, pickedRedirectServ);

            final CmdParams params = new CmdParams();

            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.requestBack = Boolean.FALSE;
            params.resultId = -1L;
            params.channelsIndex = customer.getChannelsIndex();
            params.channels = customer.getChannels();
            if (getCFMSType()) {
                params.new_channels_Index = ((Combobox) addTicketDailogWindow
                        .getFellow("reception_Channels_options")).getSelectedIndex() + 1;
                params.new_channels = ((Combobox) addTicketDailogWindow
                        .getFellow("reception_Channels_options")).getSelectedItem().getValue()
                                .toString();
            }
            else {
                params.new_channels_Index = ((Combobox) addTicketDailogWindow
                        .getFellow("general_Channels_options")).getSelectedIndex() + 1;
                params.new_channels = ((Combobox) addTicketDailogWindow
                        .getFellow("general_Channels_options")).getSelectedItem().getValue()
                                .toString();
            }
            // params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
            // params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();

            if (getCFMSType()) {
                custComments = ((Textbox) addTicketDailogWindow
                        .getFellow("reception_ticket_comments")).getText();
                params.comments = custComments;
            }
            else {
                custComments = ((Textbox) addTicketDailogWindow
                        .getFellow("general_ticket_comments")).getText();
                params.comments = custComments;
            }

            //  CM:  Set status flags.  Citizen is in a sequence, want wait queue logging
            params.in_sequence = true;
            params.log_waitqueue = trackQOnNextService;

            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER)
                    .process(params, "", new byte[4]);

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            refreshListServices();
            service_list.invalidate();
            addTicketDailogWindow.setVisible(false);

            // Reset the combobox to default value/placeHolder
            ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");

            //SleepSeconds(15);
            this.invite();
            this.begin();
            this.refreshChannels();
            // QLog.l().logQUser().debug("Updating channels");
            // QLog.l().logQUser().debug(params.channelsIndex);
            // QLog.l().logQUser().debug(((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex());
            // ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).setSelectedIndex(params.channelsIndex - 1);
            // QLog.l().logQUser().debug(((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex());
            customer.setChannels(params.new_channels);
            customer.setChannelsIndex(params.new_channels_Index);
            customer.setTempComments(custComments);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
        }

        //  Get ending customer ID.
        QCustomer customerEnd = user.getUser().getCustomer();
        Long customerIdEnd = customerEnd.getId();

        //  If different, an error.  Send message to Slack.
        if (customerIdStart != customerIdEnd) {
            //            QLog.l().logQUser().debug("==> Error: Next Service customer switch from "
            //                    + customerIdStart.toString() + " to " + customerIdEnd.toString());
            
            String UserMsg = "Error when adding next service to citizen"
                    + "\nOffice Name: " + user.getUser().getOffice().getName()
                    + "\nCSR Name: " + user.getUser().getName()
                    + "\nTicket Number: " + ticketStart
                    + "\nCustomer ID: " + customerIdStart.toString()
                    + "\nService: " + customerStart.getService().getName()
                    + "\n\nWrong Citizen selected: "
                    + "\n->Wrong Ticket: " + customerEnd.getName()
                    + "\n->Wrong ID: " + customerIdEnd.toString()
                    + "\n->Wrong Service: " + customerEnd.getService().getName();

            //  Send message to Slack.
            Executer.getInstance().SendSlackMessage(user.getUser(), customerStart, UserMsg);
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Apply Next Service", "After", user.getUser(),
                customer);
    }

    @Command
    public void selectPreviousService() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Previous Service", "Before", user.getUser(),
                user.getUser().getCustomer());

        Boolean OkToContinue = true;
        QCustomer customerStart = user.getUser().getCustomer();
        Long customerIdStart = customerStart.getId();
        String ticketStart = customerStart.getName();

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                OkToContinue = false;
            }

            if (OkToContinue) {
                final CmdParams params = new CmdParams();

                params.userId = user.getUser().getId();
                params.serviceId = pickedRedirectServ.getId();
                params.requestBack = Boolean.FALSE;
                params.resultId = -1L;
                params.comments = "";
                params.in_sequence = true;
                params.log_waitqueue = trackQOnPreviousService;

                Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER).process(params,
                        "",
                        new byte[4]);

                customer = null;
                setKeyRegim(KEYS_MAY_INVITE);
                service_list.setModel(service_list.getModel());
                refreshListServices();
                service_list.invalidate();

                //SleepSeconds(15);
                this.invite();
                this.begin();
                this.refreshChannels();
                if (getCFMSType()) {
                    params.new_channels_Index = ((Combobox) addTicketDailogWindow
                            .getFellow("reception_Channels_options")).getSelectedIndex() + 1;
                    params.new_channels = ((Combobox) addTicketDailogWindow
                            .getFellow("reception_Channels_options")).getSelectedItem().getValue()
                                    .toString();
                }
                else {
                    params.new_channels_Index = ((Combobox) addTicketDailogWindow
                            .getFellow("general_Channels_options")).getSelectedIndex() + 1;
                    params.new_channels = ((Combobox) addTicketDailogWindow
                            .getFellow("general_Channels_options")).getSelectedItem().getValue()
                                    .toString();
                }
                // params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
                // params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
                customer.setChannelsIndex(params.new_channels_Index);
                customer.setChannels(params.new_channels);
                BindUtils.postNotifyChange(null, null, Form.this, "*");

                //  If wrong customer selected, an error.
                if (customer != null) {

                    //  Get customer actually chosen.
                    Long customerIdEnd = customer.getId();

                    //  If customer chosen different than customer created, an error.  Send to slack.
                    if (customerIdStart != customerIdEnd) {

                        //            QLog.l().logQUser().debug("==> Error: Next Service customer switch from "
                        //                    + customerIdStart.toString() + " to " + customerIdEnd.toString());

                        String UserMsg = "Error selecting previous service"
                                + "\nOffice Name: " + user.getUser().getOffice().getName()
                                + "\nCSR Name: " + user.getUser().getName()
                                + "\nTicket Number: " + ticketStart
                                + "\nCustomer ID: " + customerIdStart.toString()
                                + "\nService: " + customerStart.getService().getName()
                                + "\n\nWrong Citizen previous service: "
                                + "\n->Wrong Ticket: " + customer.getName()
                                + "\n->Wrong ID: " + customerIdEnd.toString()
                                + "\n->Wrong Service: " + customer.getService().getName();

                        //  Send message to Slack.
                        Executer.getInstance().SendSlackMessage(user.getUser(), customerStart,
                                UserMsg);
                    }
                }

                //////////////////
            }
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Previous Service", "After", user.getUser(),
                customer);
    }

    @Command
    //@NotifyChange(value = { "postponList", "customer", "btnsDisabled" })
    @NotifyChange(value = { "postponList", "btnsDisabled" })
    public void closeAddToQueueDialog() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Add to Queue", "Before", user.getUser(), user
                .getUser().getCustomer());
        trackCust = null;

        //  Debug
        //QLog.l().logQUser().debug("==> Start: closeAddToQueueDialog");

        //  Debug
        String testText = ((Textbox) addTicketDailogWindow
                .getFellow("reception_ticket_comments")).getText();
        //QLog.l().logQUser().debug("    --> Comments: " + testText);

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
            }
            else {

                //  Make a Snowplow call.  Service wanted has been decided on.
                CallSnowplowChooseService(spId, pickedRedirectServ);

                final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_NORMAL,
                        Boolean.FALSE);

                //                //  Need to get service from params.
                //                final QService service = QServiceTree.getInstance().getById(params.serviceId);
                //
                //                QLog.l().logQUser().debug("==> Choosing a service from Params:");
                //                QLog.l().logQUser().debug("    --> Channel:  " + params.channels);
                //                QLog.l().logQUser().debug("    --> SvcId:    " + params.serviceId);
                //                QLog.l().logQUser().debug("    --> SvcPId:   " + service.getParentId());
                //                QLog.l().logQUser().debug("    --> SvcCat:   " + service.getParent().getName());
                //                QLog.l().logQUser().debug("    --> SvtTrans: " + service.getName());
                //                QLog.l().logQUser().debug("    --> Q.Txn:    " + (params.custQtxn ? "True"
                //                        : "False"));
                //                QLog.l().logQUser().debug("    --> PSpId:     " + params.spId);
                //                QLog.l().logQUser().debug("    --> VSpId:     " + this.spId);

                RpcStandInService result = this.addToQueue(params);
                if (result.getResult() != null) {
                    trackCust = result.getResult();

                    //                    QLog.l().logQUser().debug("==> Choosing a service from Add to Queue result:");
                    //                    QLog.l().logQUser().debug("    --> Channel:  " + trackCust.getChannels());
                    //                    QLog.l().logQUser().debug("    --> SvcId:    " + trackCust.getService()
                    //                            .getId());
                    //                    QLog.l().logQUser().debug("    --> SvcPId:   " + trackCust.getService()
                    //                            .getParentId());
                    //                    QLog.l().logQUser().debug("    --> SvcCat:   " + trackCust.getService()
                    //                            .getParent().getName());
                    //                    QLog.l().logQUser().debug("    --> SvtTrans: " + trackCust.getService()
                    //                            .getName());
                    //                    QLog.l().logQUser().debug("    --> Q.Txn:    " + (trackCust.getTempQuickTxn()
                    //                            ? "True" : "False"));
                    //                    QLog.l().logQUser().debug("    --> PSpId:     " + params.spId);
                }

                customer = null;
                setKeyRegim(KEYS_MAY_INVITE);
                service_list.setModel(service_list.getModel());

                addTicketDailogWindow.setVisible(false);

                refreshListServices();
                service_list.invalidate();
            }
        }

        //  Debug
        //QLog.l().logQUser().debug("==> End: closeAddToQueueDialog");

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Add to Queue", "After", user.getUser(),
                trackCust);
    }

    public void Sort() {
        Comparator cTimeAsc = new WaitingPanelComparator(true, 1);
        user.getCustomerList().sort(cTimeAsc); // Sort customerList by time asending order
    }

    public CmdParams paramsForAddingInQueue(Integer priority, Boolean isMine) {

        final CmdParams params = new CmdParams();

        //  Debug
        //QLog.l().logQUser().debug("==> Start: paramsForAddingInQueue");

        params.userId = user.getUser().getId();
        params.serviceId = pickedRedirectServ.getId();
        params.resultId = -1L;
        params.priority = priority;
        params.isMine = isMine;
        if (getCFMSType()) {
            params.comments = ((Textbox) addTicketDailogWindow
                    .getFellow("reception_ticket_comments")).getText();
            params.channelsIndex = ((Combobox) addTicketDailogWindow
                    .getFellow("reception_Channels_options")).getSelectedIndex() + 1;
            params.channels = ((Combobox) addTicketDailogWindow
                    .getFellow("reception_Channels_options")).getSelectedItem().getValue()
                            .toString();
        }
        else {
            params.comments = ((Textbox) addTicketDailogWindow.getFellow("general_ticket_comments"))
                    .getText();
            params.channelsIndex = ((Combobox) addTicketDailogWindow
                    .getFellow("general_Channels_options")).getSelectedIndex() + 1;
            params.channels = ((Combobox) addTicketDailogWindow
                    .getFellow("general_Channels_options")).getSelectedItem().getValue().toString();
        }

        //  Add whether the transaction is a quick transaction or not.
        Checkbox QuickTxn = (Checkbox) addTicketDailogWindow
                .getFellow("QuickTxnCust");

        if (QuickTxn == null) {
            //QLog.l().logQUser().debug("    --> Bad news.  Checkbox could not be found");
            params.custQtxn = false;
        }
        else {
            //QLog.l().logQUser().debug("    --> Yea!  Checkbox is not null");
            boolean Quick = QuickTxn.isChecked();
            //QLog.l().logQUser()
            //        .debug("    --> Checkbox found. It is: " + (Quick ? "Checked" : "Not checked"));
            params.custQtxn = Quick;
        }

        // params.channelsIndex = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
        // params.channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
        params.welcomeTime = user.getCustomerWelcomeTime();
        params.spId = this.spId;

        //  Debug
        //QLog.l().logQUser().debug("==> End: paramsForAddingInQueue");

        return params;
    }

    public RpcStandInService addToQueue(CmdParams params) {

        //QLog.l().logQUser().debug("==> addToQueue: " + (params.in_sequence ? "Yes" : "No"));

        return (RpcStandInService) Executer.getInstance().getTasks().get(Uses.TASK_STAND_IN)
                .process(params, "", new byte[4]);
    }

    @Command
    @NotifyChange(value = { "addWindowButtons" })
    public void changeService() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Change service", "Before", user.getUser(), user
                .getUser().getCustomer());

        addWindowButtons[0] = false;
        addWindowButtons[1] = true;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;

        this.addTicketScreen(false, false, true);

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Srv: Change service", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    @Command
    public void closeChangeServiceDialog() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Change service", "Before", user.getUser(), user
                .getUser().getCustomer());

        Boolean OkToContinue = true;

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                OkToContinue = false;
            }

            if (OkToContinue) {
                if (!user.checkIfUserCanServe(pickedRedirectServ)) {
                    Messagebox.show(user.getName()
                            + " doesn't have rights to serve citizens for this service. Try Add to Queue.",
                            "Access Issues", Messagebox.OK, Messagebox.EXCLAMATION);
                    OkToContinue = false;
                }
            }

            //            QLog.l().logger().debug("    --> CSR:  " + user.getName());
            //            QLog.l().logger().debug("    --> Cust: " + customer.getFullNumber());
            //            QLog.l().logger().debug("    --> Svc:  " + pickedRedirectServ.getName());

            if (OkToContinue) {

                //  Make a Snowplow call.  Changed service wanted has been decided on.
                CallSnowplowChooseService(spId, pickedRedirectServ);

                final CmdParams params = new CmdParams();
                params.userId = user.getUser().getId();
                params.serviceId = pickedRedirectServ.getId();
                if (getCFMSType()) {
                    params.comments = ((Textbox) addTicketDailogWindow
                            .getFellow("reception_ticket_comments")).getText();
                }
                else {
                    params.comments = ((Textbox) addTicketDailogWindow
                            .getFellow("general_ticket_comments")).getText();
                }
                params.channelsIndex = customer.getChannelsIndex();
                params.channels = customer.getChannels();
                if (getCFMSType()) {
                    params.new_channels_Index = ((Combobox) addTicketDailogWindow
                            .getFellow("reception_Channels_options")).getSelectedIndex() + 1;
                    params.new_channels = ((Combobox) addTicketDailogWindow
                            .getFellow("reception_Channels_options")).getSelectedItem().getValue()
                                    .toString();
                }
                else {
                    params.new_channels_Index = ((Combobox) addTicketDailogWindow
                            .getFellow("general_Channels_options")).getSelectedIndex() + 1;
                    params.new_channels = ((Combobox) addTicketDailogWindow
                            .getFellow("general_Channels_options")).getSelectedItem().getValue()
                                    .toString();
                }

                //  CM:  Get quick transaction status.
                params.custQtxn = customer.getTempQuickTxn();

                // params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
                // params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();

                Executer.getInstance().getTasks().get(Uses.TASK_CHANGE_SERVICE).process(params, "",
                        new byte[4]);

                service_list.setModel(service_list.getModel());
                refreshListServices();
                service_list.invalidate();
                addTicketDailogWindow.setVisible(false);
                customer.setChannels(params.new_channels);
                customer.setChannelsIndex(params.new_channels_Index);
                BindUtils.postNotifyChange(null, null, Form.this, "*");
            }

            //  CM:  Debug
            //QLog.l().logger().debug("==> End: Change Service (closeChangeServiceDialog)");
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Change service", "After", user.getUser(),
                customer);
    }

    @Command
    public void closeAddAndServeDialog() {

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Begin Service", "Before", user.getUser(), user
                .getUser().getCustomer());

        //  CM:  For early returns.
        Boolean OkToContinue = true;

        if (pickedRedirectServ != null) {

            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                OkToContinue = false;
            }

            if (OkToContinue && (!user.checkIfUserCanServe(pickedRedirectServ))) {
                Messagebox.show(user.getName()
                        + " doesn't have rights to serve citizens for this service. Try Add to Queue.",
                        "Access Issues", Messagebox.OK, Messagebox.EXCLAMATION);
                OkToContinue = false;
            }

            if (OkToContinue) {

                //  Make a Snowplow call.  Service wanted has been decided on.
                CallSnowplowChooseService(spId, pickedRedirectServ);

                final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_VIP, Boolean.TRUE);
                params.in_sequence = true;
                params.log_waitqueue = trackQOnBeginService;
                final RpcStandInService res = this.addToQueue(params);
                QCustomer customerStart = res.getResult();
                Long customerIdStart = customerStart.getId();
                String ticketStart = customerStart.getName();

                customer = null;
                setKeyRegim(KEYS_MAY_INVITE);
                service_list.setModel(service_list.getModel());
                refreshListServices();
                service_list.invalidate();
                addTicketDailogWindow.setVisible(false);

                //SleepSeconds(15);
                this.invite();
                this.begin();
                BindUtils.postNotifyChange(null, null, Form.this, "*");

                //  If wrong customer selected, an error.
                if (customer != null) {

                    //  Get customer actually chosen.
                    Long customerIdEnd = customer.getId();


                    //  If customer chosen different than customer created, an error.  Send to slack.
                    if (customerIdStart != customerIdEnd) {

                        //            QLog.l().logQUser().debug("==> Error: Next Service customer switch from "
                        //                    + customerIdStart.toString() + " to " + customerIdEnd.toString());

                        String UserMsg = "Error when begining to serve citizen"
                                + "\nOffice Name: " + user.getUser().getOffice().getName()
                                + "\nCSR Name: " + user.getUser().getName()
                                + "\nTicket Number: " + ticketStart
                                + "\nCustomer ID: " + customerIdStart.toString()
                                + "\nService: " + customerStart.getService().getName()
                                + "\n\nWrong Citizen served: "
                                + "\n->Wrong Ticket: " + customer.getName()
                                + "\n->Wrong ID: " + customerIdEnd.toString()
                                + "\n->Wrong Service: " + customer.getService().getName();

                        //  Send message to Slack.
                        Executer.getInstance().SendSlackMessage(user.getUser(), customerStart,
                                UserMsg);
                    }
                }
            }
        }

        //  CM:  Tracking.
        Executer.getInstance().TrackUserClick("Add: Begin Service", "After", user.getUser(), user
                .getUser().getCustomer());
    }

    public void SleepSeconds(Integer seconds) {
        try {
            if (user.getUser().getId() != 88) {
                QLog.l().logQUser().debug("==> Sleeping " + seconds.toString() + " seconds.");
                TimeUnit.SECONDS.sleep(seconds);
            }
        }
        catch (Exception ex) {
            QLog.l().logQUser().debug("==> Sleeping " + seconds.toString() + " seconds.  Error: "
                    + ex.getMessage());
        }
    }
    
    public void SleepMilliSeconds(Integer milli ) {
        try {
            if (user.getUser().getId() != 88) {
                QLog.l().logQUser().debug("==> Sleeping " + milli.toString() + " milliseconds.");
                TimeUnit.MILLISECONDS.sleep(milli);
            }
        }
        catch (Exception ex) {
            QLog.l().logQUser().debug("==> Sleeping " + milli.toString() + " milliseconds.  Error: "
                    + ex.getMessage());
        }
    }

    @Command
    @NotifyChange(value = { "postponList", "customer", "btnsDisabled" })
    public void closeRedirectDialog() {
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = new CmdParams();

            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.resultId = -1L;
            if (getCFMSType()) {
                params.comments = ((Textbox) addTicketDailogWindow
                        .getFellow("reception_ticket_comments")).getText();
            }
            else {
                params.comments = ((Textbox) addTicketDailogWindow
                        .getFellow("general_ticket_comments")).getText();
            }
            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER)
                    .process(params, "", new byte[4]);

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            refreshListServices();
            service_list.invalidate();
            addTicketDailogWindow.setVisible(false);
            serveCustomerDialogWindow.setVisible(false);
        }
    }

    public QService getPickedRedirectServ() {
        return pickedRedirectServ;
    }

    public void setPickedRedirectServ(QService pickedRedirectServ) {
        String serviceName = pickedRedirectServ.getName();

        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText(serviceName);
        this.pickedRedirectServ = pickedRedirectServ;
    }

    public void refreshQuantity() {
        customer = user.getUser().getCustomer();
        customer.setQuantity("1");
    }

    public String getOfficeName() {
        return officeName;
    }
}
