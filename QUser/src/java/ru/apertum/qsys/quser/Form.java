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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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

/**
 * @author Evgeniy Egorov
 */
public class Form {

    private static QCustomer pickedPostponed;
    private final LinkedList<QCustomer> postponList = QPostponedList.getInstance()
            .getPostponedCustomers();
    private final LinkedList<QResult> resultList = QResultList.getInstance().getItems();
    // ********************************************************************************************************************************************
    // ** Перенаправление Redirection
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
    // **** Логин Login
    // *****************************************************
    public LinkedList<String> prior_St = new LinkedList(Uses.get_COEFF_WORD().values());
    public String officeType = "non-reception";
    public LinkedList<QUser> userList = new LinkedList<>();
    public LinkedList<QUser> userListbyOffice = new LinkedList<>();
    // Main service page
    @Wire("#incClientDashboard #client_north")
    North clientDashboardNorth;
    // ********************************************************************************************************************************************
    // ** Change priority - By Service
    // ********************************************************************************************************************************************
    @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
    Window changeServicePriorityDialog;
    // ********************************************************************************************************************************************
    // ** Отложенные Отложить посетителя Postponed Postpone visitor
    // ********************************************************************************************************************************************
    @Wire("#incClientDashboard #incPostponeCustomerDialog #postponeDialog")
    Window postponeCustomerDialog;
    // *** Диалоги изменения состояния и вызова лтложенных Dialogs for changing the state and calling ltalges
    @Wire("#incClientDashboard #incChangePostponedStatusDialog #changePostponedStatusDialog")
    Window changePostponedStatusDialog;
    // *********************************************************************************************************
    // **** Кнопки и их события Buttons and their events
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

    QService pickedMainService;
    @Wire
    private Textbox typeservices;
    /**
     * Залогиневшейся юзер Logged user
     */
    private User user = new User();
    private LinkedList<QService> PreviousList = new LinkedList<>();
    /**
     * текущее состояние кнопок Current state of the buttons
     */
    private String keys_current = KEYS_OFF;
    private boolean[] btnsDisabled = new boolean[] { true, true, true, true, true, true, true, true,
            true };
    private boolean[] addWindowButtons = new boolean[] { true, false, false, false };
    /* Add Hide Button if Not Receptionist Model */
    private boolean checkCFMSType = false;
    private String checkCFMSHidden = "display: none;";
    private String checkCFMSHeight = "0%";
    private boolean checkCombo = false;
    private QCustomer customer = null;
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
    private int customersCount = 0;
    private boolean currentState = false;
    private boolean CheckGABoard = false;

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
        QLog.l().logQUser().debug("Loding page: init");
        final Session sess = Sessions.getCurrent();

        final User userL = (User) sess.getAttribute("userForQUser");
        setKeyRegimForUser(userL);
        setCFMSAttributes();
    }

    /**
     * Это нужно чтоб делать include во view и потом связывать @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg") This is necessary to do
     * include in the view and then bind
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        QLog.l().logQUser().debug("Loding page: afterCompose");
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

        Uses.userTimeZone = (TimeZone) Sessions.getCurrent()
                .getAttribute("org.zkoss.web.preferred.timeZone");
        QLog.l().logQUser().debug("Login : " + user.getName());
        // if (user.getUser().getName().equals("Administrator")) {
        if (user.getUser().getAdminAccess()) {
            user.setGABoard(true);
        }
        QLog.l().logQUser().debug("STATUS : " + user.getGABoard());

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
    }

    // @ContextParam(ContextType.VIEW) Component comp
    @Command
    public void closeGA() {
        CheckGABoard = false;
        GAManagementDialogWindow.addEventListener("onClose", new EventListener() {

            @Override
            public void onEvent(Event event) throws Exception {
                // TODO Auto-generated method stub
                event.stopPropagation();
                // GAManagementDialogWindow.detach();
                GAManagementDialogWindow.setVisible(false);

                CheckGABoard = false;

                QLog.l().logQUser()
                        .debug("\n\n\n\n Close GA show FLAG:  " + user.getGABoard() + "\n\n\n\n");
                QLog.l().logQUser()
                        .debug("\n\n\n\n Close GA CheckGABoard:  " + CheckGABoard + "\n\n\n\n");
            }
        });

        QLog.l().logQUser()
                .debug("\n\n\n\n OUTSIDE Close GA CheckGABoard:  " + CheckGABoard + "\n\n\n\n");
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
                ReportTicket = "Ticket numebr is not provided";
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
    public void QuickTxnChecked() {
        /*
        boolean quick = true;
        QLog.l().logQUser().debug("--> Start: QuickTxnChecked");
        QUser quser = user.getUser();
        QLog.l().logQUser().debug("    --> User: " + quser.getName());
        QLog.l().logQUser().debug("    --> Old user quick value: " + quser.getQuickTxn());
        QLog.l().logQUser().debug("    --> New debug statement");
        
        QLog.l().logQUser().debug("    --> Quick checkbox value: " + quick);
        */

        QUser quser = user.getUser();
        boolean quick = !quser.getQuickTxn();
        quser.setQuickTxn(quick);
        String strQuick = "Default value";
        strQuick = quick ? "Yes" : "No";
        QLog.l().logQUser().debug("    --> CSR is Quick Transaction? " + strQuick);

        /*
        QLog.l().logQUser().debug("    --> New user quick value: " + quser.getQuickTxn());
        
        QLog.l().logQUser().debug("--> End:   QuickTxnChecked");
        */
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "login", "user", "postponList", "customer",
            "avaitColumn", "officeName" })
    public void logout() {
        QLog.l().logQUser().debug("Logout " + user.getName());

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
        quser.setCurrentState(false);
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
     * Механизм включения/отключения кнопок Button on / off mechanism
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

        String qsb = quser.getOffice().getSmartboardType();
        if (qsb.equalsIgnoreCase("callbyticket")) {
            checkCFMSType = true;
        }
        if (qsb.equalsIgnoreCase("callbyname")) {
            checkCFMSType = true;
        }
        return checkCFMSType;
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
    public void invite() {

        QLog.l().logQUser().debug("==> Start: invite - Invite by " + user.getName());
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

        //  Debug
        QLog.l().logQUser().debug("==> End: invite");
    }

    @Command
    public void addServeScreen() {
        ((Checkbox) serveCustomerDialogWindow.getFellow("inaccurateTimeCheckBox"))
                .setChecked(false);
        serveCustomerDialogWindow.setVisible(true);
        serveCustomerDialogWindow.doModal();
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "customer" })
    public void kill() {

        Messagebox.show("Do you want to remove the client?", "Remove", new Messagebox.Button[] {
                Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION,
                (Messagebox.ClickEvent t) -> {
                    QLog.l().logQUser()
                            .debug("Kill by " + user.getName() + " customer "
                                    + customer.getFullNumber());
                    if (t.getButton() != null
                            && t.getButton().compareTo(Messagebox.Button.YES) == 0) {
                        final CmdParams params = new CmdParams();

                        params.userId = user.getUser().getId();
                        Executer.getInstance().getTasks().get(Uses.TASK_KILL_NEXT_CUSTOMER)
                                .process(params, "", new byte[4]);

                        customer.refreshPrevious();
                        customer = null;

                        // Set the current working service to be empty
                        QUser quser = QUserList.getInstance().getById(params.userId);
                        quser.setCurrentService("");

                        setKeyRegim(KEYS_MAY_INVITE);
                        service_list.setModel(service_list.getModel());

                        BindUtils.postNotifyChange(null, null, Form.this, "*");
                        serveCustomerDialogWindow.setVisible(false);

                    }
                });
    }

    @Command
    @NotifyChange(value = { "btnsDisabled" })
    public void begin() {
        // QLog.l().logQUser().debug("Begin by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER)
                .process(params, "", new byte[4]);

        // Andrew - to set quser service for GABoard
        QUser quser = QUserList.getInstance().getById(params.userId);
        quser.setCurrentService(user.getUser().getCustomer().getService().getName());

        setKeyRegim(KEYS_STARTED);
        service_list.setModel(service_list.getModel());
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }

    @Command
    public void updateComments() {
        // Inheritance the comment from Serve-customer window to hold window
        String tempComment = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments"))
                .getText();
        customer.setTempComments(tempComment);

        // Set to User current Comments
        // QUser quser = QUserList.getInstance().getById(user.getUser().getId());
        // quser.setCurrentComments(tempComment);

        QLog.l().logQUser().debug("\n\nPostponed!!:\n" + customer.getTempComments() + "\n\n\n");
    }

    @Command
    public void postpone() {
        QLog.l().logQUser()
                .debug("Postpone by " + user.getName() + " customer " + customer.getFullNumber());
        postponeCustomerDialog.setVisible(true);
        postponeCustomerDialog.doModal();
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }

    @Command
    public void ReturnedRedirect() {

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

        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_RETURN_QUEUE)
                .process(params, "", new byte[4]);

        customer = null;
        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
        serveCustomerDialogWindow.setVisible(false);
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
            // переключение на кастомера при параллельном приеме, должен приехать customerID
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
                            "Юзер \"" + user + "\" переключился на кастомера \"" + parallelCust
                                    .getFullNumber()
                                    + "\"");
                }
            }

            this.addToQueue(params);

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            QLog.l().logQUser().debug("\n\nTEST LIST: " + service_list.getModel() + "\n\n");
            serveCustomerDialogWindow.setVisible(false);
        }
    }

    @Command
    @NotifyChange(value = { "addWindowButtons" })
    public void addClient() {
        QLog.l().logQUser().debug("addClient");
        user.setCustomerWelcomeTime(new Date());
        addWindowButtons[0] = true;
        addWindowButtons[1] = false;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;
        // customer.setChannels(1);
        pickedRedirectServ = null;
        ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");
        this.addTicketScreen();
    }

    @Command
    @NotifyChange(value = { "addWindowButtons" })
    public void addNextService() {
        // Save the customer to the DB before adding a service, so the service_quantity persists
        customer.save();

        addWindowButtons[0] = false;
        addWindowButtons[1] = false;
        addWindowButtons[2] = true;
        addWindowButtons[3] = false;
        // refresh the service list. Remove the default service selection
        pickedRedirectServ = null;
        this.addTicketScreen();
    }

    @Command
    public void disableButtons() {
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
    }

    @Command
    @NotifyChange(value = { "btnsDisabled", "customer" })
    public void finish() {
        QLog.l().logQUser()
                .debug("Finish by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();

        params.resultId = -1L;
        params.textData = "";
        params.inAccurateFinish = ((Checkbox) serveCustomerDialogWindow
                .getFellow("inaccurateTimeCheckBox")).isChecked();

        final RpcStandInService res = (RpcStandInService) Executer.getInstance().getTasks()
                .get(Uses.TASK_FINISH_CUSTOMER).process(params, "", new byte[4]);
        // вернется кастомер и возможно он еще не домой а по списку услуг. Список определяется при старте кастомера в обработку специяльным юзером в
        // регистратуре
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

        BindUtils.postNotifyChange(null, null, Form.this, "*");
        serveCustomerDialogWindow.setVisible(false);

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

    /*
     * new function prepareInvite() {
     *     look through service list.
     *     set pickedCustomer = one that matches
     *     Write a new function inviteCustomerNow
     *      
     * }
     * 
     * 
     * 
     * 
     *  keep the old inviteCustomerNow, but have it take a parameter of pickedCustomer
     *  
     *  write a new inviteCustomerNow, which takes no parameters, just like the old one.
     *  It only has one line, it calls the old one with a parameter.
     *  
     *  
     *  
     *  public void inviteCustomerNew() {
     *  
     *    inviteCustomerOld(pickedCustomer);
     *  }
     *  
     *  public void inviteCustomerOld(pickedCustomer) {
     *  
     *    all the old code
     *  
     *  }
     *  
     *  
     *  Now, I can write a new function, which looks through the service list,
     *  selects a customer, sets it to be pickedCustomer, and calls the function.
     * 
     */

    @Command
    public void inviteCustomerNow() {
        // 1. Postpone the customer
        // 2. Pick the customer from Postponed list

        if (pickedCustomer == null || keys_current == KEYS_INVITED || keys_current == KEYS_STARTED
                || keys_current == KEYS_OFF) {
            return;
        }

        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.postponedPeriod = 0;
        params.customerId = pickedCustomer.getId();
        params.isMine = Boolean.TRUE;
        user.getUser().setCustomer(pickedCustomer);
        Executer.getInstance().getTasks().get(Uses.TASK_INVITE_SELECTED_CUSTOMER)
                .process(params, "", new byte[4], pickedCustomer);
        customer = null;

        service_list.setModel(service_list.getModel());

        Executer.getInstance().getTasks().get(Uses.TASK_INVITE_POSTPONED)
                .process(params, "", new byte[4]);
        customer = user.getUser().getCustomer();

        setKeyRegim(KEYS_INVITED);
        BindUtils.postNotifyChange(null, null, Form.this, "*");
        pickedRedirectServ = pickedCustomer.getService(); // for returning to queue use
        pickedCustomer = null; // TEST andrew //debug the clicking white space inviting problem
        this.addServeScreen();
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
            // тут поддержание сессии как в веб приложении Here the maintenance of the session as a web application
            UsersInside.getInstance().getUsersInside()
                    .put(user.getName() + user.getPassword(), new Date().getTime());
            // тут поддержание сессии как залогинившегося юзера в СУО Here the maintenance of the session as a logged user in the MSA
            QSessions.getInstance()
                    .update(user.getUser().getId(), Sessions.getCurrent().getRemoteHost(),
                            Sessions.getCurrent().getRemoteAddr().getBytes());

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

    // @Command
    // public void refreshQuser(){
    // final Session sess = Sessions.getCurrent();
    // if (sess==null) {
    // Executions.getCurrent().sendRedirect("");
    // }
    // }

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
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.postponedPeriod = ((Combobox) postponeCustomerDialog.getFellow("timeBox"))
                .getSelectedIndex() * 5;
        params.comments = ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).getText();

        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_TO_POSTPON)
                .process(params, "", new byte[4]);
        customer = null;

        QUser quser = QUserList.getInstance().getById(params.userId);
        quser.setCurrentService("");

        setKeyRegim(KEYS_MAY_INVITE);
        postpone_list.setModel(postpone_list.getModel());
        postponeCustomerDialog.setVisible(false);
        serveCustomerDialogWindow.setVisible(false);
        ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).setText("");
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }

    @Command
    public void DetermineChannels() {

        if (getCFMSType()) {
            int channelIndex = ((Combobox) addTicketDailogWindow
                    .getFellow("reception_Channels_options")).getSelectedIndex() + 1;
        }
        else {
            int channelIndex = ((Combobox) addTicketDailogWindow
                    .getFellow("general_Channels_options")).getSelectedIndex() + 1;
        }
    }

    @Command
    public void ChangeChannels() {
        QLog.l().logger().debug("ChangeChannels");
        int channelIndex = ((Combobox) serveCustomerDialogWindow.getFellow("Change_Channels"))
                .getSelectedIndex() + 1;
        String channels = ((Combobox) serveCustomerDialogWindow.getFellow("Change_Channels"))
                .getSelectedItem().getValue().toString();
        customer.setChannels(channels);
        customer.setChannelsIndex(channelIndex);
    }

    public void refreshChannels() {
        QLog.l().logger().debug("refreshChannels");
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
        // QLog.l().logger().debug("getPostponList");
        LinkedList<QCustomer> postponedCustomers = QPostponedList.getInstance()
                .getPostponedCustomers();
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
        // кому
        params.customerId = pickedPostponed.getId();
        // на что
        params.textData = ((Combobox) changePostponedStatusDialog.getFellow("pndResultBox"))
                .getText();
        Executer.getInstance().getTasks().get(Uses.TASK_POSTPON_CHANGE_STATUS)
                .process(params, "", new byte[4]);

        changePostponedStatusDialog.setVisible(false);
    }

    @Command
    public void clickListPostponedInvite() {
        if (user.getPlan().isEmpty() || pickedPostponed == null) {
            return;
        }
        Messagebox.show("Do you want to invite citizen " + pickedPostponed.getFullNumber() + " ?",
                l("inviting_client"), new Messagebox.Button[] {
                        Messagebox.Button.YES, Messagebox.Button.NO },
                Messagebox.QUESTION,
                (Messagebox.ClickEvent t) -> {
                    QLog.l().logQUser().debug(
                            "Invite postponed by " + user.getName() + " citizen " + pickedPostponed
                                    .getFullNumber());
                    if (t.getButton() != null
                            && t.getButton().compareTo(Messagebox.Button.YES) == 0) {
                        final CmdParams params = new CmdParams();
                        // @param userId id юзера который вызывает The user who causes
                        // @param id это ID кастомера которого вызываем из пула отложенных, оно есть т.к. с качстомером давно работаем
                        // It is the ID of the caller which is called from the pool of deferred, it is because With a long-stroke tool we have been working for
                        // a long time
                        params.customerId = pickedPostponed.getId();
                        params.userId = user.getUser().getId();
                        Executer.getInstance().getTasks().get(Uses.TASK_INVITE_POSTPONED)
                                .process(params, "", new byte[4]);
                        customer = user.getUser().getCustomer();

                        setKeyRegim(KEYS_INVITED);
                        BindUtils.postNotifyChange(null, null, Form.this, "postponList");
                        BindUtils.postNotifyChange(null, null, Form.this, "customer");
                        BindUtils.postNotifyChange(null, null, Form.this, "btnsDisabled");

                        this.addServeScreen();
                        this.begin();

                        pickedPostponed = null;
                    }
                    else {
                        pickedPostponed = null;
                    }
                });
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
        QLog.l().logQUser().debug("Set Main Service: " + getPickedMainService());
    }

    @Command
    public void closeAddTicketScreen() {
        addTicketDailogWindow.setVisible(false);
        addTicketDailogWindow.doModal();
    }

    @Command
    public void addTicketScreen() {

        //  Debugging
        QLog.l().logQUser().debug("==> Start: addTicketScreen");

        //  Get quick transaction check box.
        Checkbox QuickTxn = (Checkbox) addTicketDailogWindow.getFellow("CustQuickTxnId");

        // Remove previous comments and categories searched
        this.refreshAddWindow();
        this.refreshChannels();

        //  You are (???) pulling an existing customer, in queue or on hold.
        if (customer != null) {
            QLog.l().logQUser()
                    .debug("    --> Customer channel index not null: Set addTicket combo box. Index: "
                            + customer.getChannelsIndex());

            //  You are dealing with a reception office.
            if (getCFMSType()) {
                ((Combobox) addTicketDailogWindow.getFellow("reception_Channels_options"))
                        .setSelectedIndex(customer.getChannelsIndex() - 1);
            }

            //  Dealing with a non-reception office.
            else {
                ((Combobox) addTicketDailogWindow.getFellow("general_Channels_options"))
                        .setSelectedIndex(customer.getChannelsIndex() - 1);
            }

            //  Make sure you found the checkbox.            
            if (QuickTxn != null) {
                //  *** NOTE: Temporarily, set quicktxn to false. Set to be customer value.
                //  MUST change this to set it to the Quick trans flag of the customer 
                QuickTxn.setChecked(false);
            }
        }

        //  You are starting a new transaction.
        else {
            QLog.l().logQUser().debug("    -->  Customer of channel index is null");

            //  If no customer, set default quick txn to be false.            
            if (QuickTxn != null) {
                QuickTxn.setChecked(false);
            }
        }

        //  Debug.
        if (QuickTxn != null) {
            boolean isQuick = QuickTxn.isChecked();
            QLog.l().logQUser()
                    .debug("    --> Checkbox is: " + (isQuick ? "Checked" : "Not checked"));
        }
        else {
            QLog.l().logQUser().debug("    --> Bad news!  Could not find QuickTxn checkbox.");
        }

        addTicketDailogWindow.setVisible(true);
        addTicketDailogWindow.doModal();

        //  Debugging.
        QLog.l().logQUser().debug("==> End: addTicketScreen");
    }

    public void refreshAddWindow() {
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText("");
        ((Textbox) addTicketDailogWindow.getFellow("reception_ticket_comments")).setText("");
        ((Textbox) addTicketDailogWindow.getFellow("general_ticket_comments")).setText("");
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
            QLog.l().logger().debug("Office is null");
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
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText("");

        LinkedList<QService> allServices = QServiceTree.getInstance().getNodes();
        List<QService> requiredServices = null;

        if (getPickedMainService() == null) {
            QLog.l().logQUser().debug("null category was selected");
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
            QLog.l().logQUser().debug("The getvalue() returns : \n");

        }
        else {
            QLog.l().logQUser().debug("Category " + pickedMainService.getName() + " was selected");
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

        listServices = filterServicesByUser(requiredServices);

    }

    // Andrew
    // onChanging category updates the category searching algorithm, searching while typing
    @NotifyChange("listServices")
    @Command
    public void changingCategory(@BindingParam("v") String value,
            @ContextParam(ContextType.TRIGGER_EVENT) InputEvent event) {

        listServices.clear();
        LinkedList<QService> allServices = QServiceTree.getInstance().getNodes();
        List<QService> requiredServices = null;

        if (getPickedMainService() == null) {
            QLog.l().logQUser().debug("null category was selected");
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
            QLog.l().logQUser().debug("Category " + pickedMainService.getName() + " was selected");
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

    @NotifyChange("listServices")
    @Command
    public void doSearch() {
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

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

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
            addTicketDailogWindow.setVisible(false);

            // Reset the combobox to default value/placeHolder
            ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");

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
            BindUtils.postNotifyChange(null, null, Form.this, "*");
        }
    }

    @Command
    public void selectPreviousService() {
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = new CmdParams();

            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.requestBack = Boolean.FALSE;
            params.resultId = -1L;
            params.comments = "";

            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER).process(params, "",
                    new byte[4]);

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());

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
        }

    }

    @Command
    public void closeAddToQueueDialog() {
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = this
                    .paramsForAddingInQueue(Uses.PRIORITY_NORMAL, Boolean.FALSE);

            this.addToQueue(params);

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());

            addTicketDailogWindow.setVisible(false);
        }
    }

    public void Sort() {
        Comparator cTimeAsc = new WaitingPanelComparator(true, 1);
        user.getCustomerList().sort(cTimeAsc); // Sort customerList by time asending order
    }

    public CmdParams paramsForAddingInQueue(Integer priority, Boolean isMine) {

        final CmdParams params = new CmdParams();

        //  Debug
        QLog.l().logQUser().debug("==> Start: paramsForAddingInQueue");

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
                .getFellow("CustQuickTxnId");

        if (QuickTxn == null) {
            QLog.l().logQUser().debug("    --> Bad news.  Checkbox could not be found");
            params.custQtxn = false;
        }
        else {
            QLog.l().logQUser().debug("    --> CheYea!  Checkbox is not null");
            boolean Quick = QuickTxn.isChecked();
            QLog.l().logQUser()
                    .debug("    --> Checkbox found. It is: " + (Quick ? "Checked" : "Not checked"));
            params.custQtxn = Quick;
        }

        // params.channelsIndex = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
        // params.channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
        params.welcomeTime = user.getCustomerWelcomeTime();

        //  Debug
        QLog.l().logQUser().debug("==> End: paramsForAddingInQueue");

        return params;
    }

    public RpcStandInService addToQueue(CmdParams params) {
        return (RpcStandInService) Executer.getInstance().getTasks().get(Uses.TASK_STAND_IN)
                .process(params, "", new byte[4]);
    }

    @Command
    @NotifyChange(value = { "addWindowButtons" })
    public void changeService() {
        addWindowButtons[0] = false;
        addWindowButtons[1] = true;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;

        this.addTicketScreen();
    }

    @Command
    public void closeChangeServiceDialog() {
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            if (!user.checkIfUserCanServe(pickedRedirectServ)) {
                Messagebox.show(user.getName()
                        + " doesn't have rights to serve citizens for this service. Try Add to Queue.",
                        "Access Issues", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

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
            // params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
            // params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();

            Executer.getInstance().getTasks().get(Uses.TASK_CHANGE_SERVICE).process(params, "",
                    new byte[4]);

            service_list.setModel(service_list.getModel());
            addTicketDailogWindow.setVisible(false);
            customer.setChannels(params.new_channels);
            customer.setChannelsIndex(params.new_channels_Index);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
        }
    }

    @Command
    public void closeAddAndServeDialog() {
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK,
                        Messagebox.EXCLAMATION);
                return;
            }

            if (!user.checkIfUserCanServe(pickedRedirectServ)) {
                Messagebox.show(user.getName()
                        + " doesn't have rights to serve citizens for this service. Try Add to Queue.",
                        "Access Issues", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_VIP, Boolean.TRUE);
            final RpcStandInService res = this.addToQueue(params);
            customer = res.getResult();

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            addTicketDailogWindow.setVisible(false);

            this.invite();
            this.begin();
            BindUtils.postNotifyChange(null, null, Form.this, "*");

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
