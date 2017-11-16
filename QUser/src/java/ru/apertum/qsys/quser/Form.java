/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsys.quser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import static ru.apertum.qsystem.client.forms.FClient.*;
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
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;
import ru.apertum.qsystem.server.model.results.QResult;
import ru.apertum.qsystem.server.model.results.QResultList;
import ru.apertum.qsys.quser.WaitingPanelComparator;
import ru.apertum.qsystem.client.forms.FReception;

/**
 *
 * @author Evgeniy Egorov
 */
public class Form{

    public String l(String resName) {
        return Labels.getLabel(resName);
    }

    @Init
    public void init() {
        final Session sess = Sessions.getCurrent();

        final User userL = (User) sess.getAttribute("userForQUser");
        if (userL != null) {
            user = userL;
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
        }
    }

    @Wire
    private Textbox typeservices;
    
    /**
     * Это нужно чтоб делать include во view и потом связывать @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
     * This is necessary to do include in the view and then bind
     * @param view
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        QLog.l().logQUser().debug("view :");
        QLog.l().logQUser().debug(view.getClass());
        QLog.l().logQUser().debug(view.toString());
        
        QLog.l().logQUser().debug("AFTER COMPOSE --- ---");
        Selectors.wireComponents(view, this, false);
       
    }

    //*****************************************************
    //**** Multilingual
    //*****************************************************
    /*
    public ArrayList<Lng> getLangs() {
        return Multilingual.LANGS;
    }

    private Lng lang = new Multilingual().init();

    public Lng getLang() {
        return lang;
    }

    public void setLang(Lng lang) {
        this.lang = lang;
    }

    @Command("changeLang")
    public void changeLang() {
        if (lang != null) {
            final Session session = Sessions.getCurrent();
            final Locale prefer_locale = lang.code.length() > 2
                    ? new Locale(lang.code.substring(0, 2), lang.code.substring(3)) : new Locale(lang.code);
            session.setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE, prefer_locale);
            Executions.sendRedirect(null);
            
        }
    }
*/
   
    //*****************************************************
    //**** Логин Login
    //*****************************************************
    /**
     * Залогиневшейся юзер Logged user
     */
    private User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    private LinkedList<QService> PreviousList = new LinkedList<>();
    
    public LinkedList<QService> getPreviousList(){
        return this.PreviousList;
    };

    @Command
    @NotifyChange(value = {"btnsDisabled", "login", "user", "postponList", "customer", "avaitColumn"})
    public void login() {
        
        Uses.userTimeZone = (TimeZone)Sessions.getCurrent().getAttribute("org.zkoss.web.preferred.timeZone");
        QLog.l().logQUser().debug("Login : " + user.getName());
        if(user.getName().equals("Administrator")){
            user.setGABoard(true);
        }
        QLog.l().logQUser().debug("STATUS : " + user.getGABoard());
//        ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).setSelectedIndex(0);
        

        final Session sess = Sessions.getCurrent();
        sess.setAttribute("userForQUser", user);
        customer = user.getUser().getCustomer();        
        
        // TODO for testing
        // need disabled
        user.getPlan().forEach((QPlanService p) -> {
            final CmdParams params = new CmdParams();
            params.serviceId = p.getService().getId();            
            params.priority = 2;
            //*/todo disabled*/ Executer.getInstance().getTasks().get(Uses.TASK_STAND_IN).process(params, "", new byte[4]);
        });
                
        if (customer != null) {            
            switch (customer.getState()) {
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
        } else {
            setKeyRegim(KEYS_MAY_INVITE);
            
        }

    }

    @Wire("#incClientDashboard #incGAManagementDialog #GAManagementDialog")
    Window GAManagementDialogWindow;
    
    @Command
    public void GABoard() {
        GAManagementDialogWindow.setVisible(true);
        GAManagementDialogWindow.doModal();
//        postponeCustomerDialog
        
    }
    
    @Command
    public void about() {
        final Properties settings = new Properties();
        final InputStream inStream = this.getClass().getResourceAsStream("/ru/apertum/qsys/quser/quser.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }

        final Properties settings2 = new Properties();
        final InputStream inStream2 = this.getClass().getResourceAsStream("/ru/apertum/qsystem/common/version.properties");
        try {
            settings2.load(inStream2);
        } catch (IOException ex) {
            throw new ServerException("Cant read version. " + ex);
        }
        Messagebox.show("*** Plugin QUser ***\n" + "   version " + settings.getProperty("version") + "\n   date " + settings.getProperty("date") + "\n   for QSystem " + settings.getProperty("version_qsystem")
                + "\n\n*** QMS Apertum-QSystem ***\n" + "   version " + settings2.getProperty("version") + "\n   date " + settings2.getProperty("date") + "\n   DB " + settings2.getProperty("version_db"),
                "QMS Apertum-QSystem", Messagebox.OK, Messagebox.INFORMATION);
    }

    @Command
    @NotifyChange(value = {"btnsDisabled", "login", "user", "customer"})
    public void logout() {
        QLog.l().logQUser().debug("Logout " + user.getName());

        setKeyRegim(KEYS_OFF);
        final Session sess = Sessions.getCurrent();
        sess.removeAttribute("userForQUser");
        UsersInside.getInstance().getUsersInside().remove(user.getName() + user.getPassword());
        user.setCustomerList(Collections.<QPlanService> emptyList());
        user.setName("");
        user.setPassword("");
        customer = null;

        for (QSession session : QSessions.getInstance().getSessions()) {
            if (user.getUser().getId().equals(session.getUser().getId())) {
                QSessions.getInstance().getSessions().remove(session);
                return;
            }
        }
        // тут уже ретурн может быть и есть There may already be a rethurn
        
    }

    public LinkedList<QUser> getUsersForLogin() {
//        QLog.l().logQUser().debug("\n\n\n\nQUSERLIST\n\n :\n"   + greed.get(2).getShadow() +  "\n\n\n\n\n");
        return QUserList.getInstance().getItems();
    }
    
//    public LinkedList<QUser> test2 = greed.get(2).getShadow();
//    public LinkedList<QUser> userList = QUserList.getInstance().getItems();

    
    public LinkedList<QUser> userList = new LinkedList<>();
    public LinkedList<QUser> getuserList(){
        return userList = QUserList.getInstance().getItems();
    }
 
    
//    greed.get(rowIndex).getShadow()
    //ANDREW

    public boolean isLogin() {
        final Session sess = Sessions.getCurrent();
        final User userL = (User) sess.getAttribute("userForQUser");
        return userL != null;
    }

    //*********************************************************************************************************
    //**** Кнопки и их события Buttons and their events
    //*********************************************************************************************************
    /**
     * текущее состояние кнопок Current state of the buttons
     */
    private String keys_current = KEYS_OFF;

    /**
     * Механизм включения/отключения кнопок Button on / off mechanism
     *
     * @param regim
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

    private boolean[] btnsDisabled = new boolean[]{true, true, true, true, true, true, true, true, true};
    
    public boolean[] getBtnsDisabled() {
        return btnsDisabled;
    }
    
    public void setBtnsDisabled(boolean[] btnsDisabled) {
        this.btnsDisabled = btnsDisabled;
    }
    
    private boolean[] addWindowButtons = new boolean[]{true, false, false, false};
    
    public void setAddWindowButtons(boolean[] addWindowButtons) {
        this.addWindowButtons = addWindowButtons;
    }
    
    public boolean[] getAddWindowButtons() {
        return addWindowButtons;
    }
    
    /* Add Hide Button if Not Receptionist Model */

       private boolean checkCFMSType = false;
       private String checkCFMSHidden = "display: none;";
       private String checkCFMSHeight = "0%";

       public boolean getCFMSType() {
               String qsb = System.getenv ("QSB");
               if (qsb.equalsIgnoreCase("callbyticket")) {
                       checkCFMSType = true;
               };
               if (qsb.equalsIgnoreCase("callbyname")) {
                       checkCFMSType = true;
               };
               return checkCFMSType;
       }
       
       private boolean checkCombo = false;
        public boolean CheckCombobox() {
//            QLog.l().logQUser().debug("\n\n\n\nCHECK COMBO BOX\n\n Redirect:\n" + pickedRedirectServ + "\n Main Service:\n"  +pickedMainService +  "\n\n\n\n\n");
            QLog.l().logQUser().debug("\n\n\n\nCHECK CUSTOMER\n\n :\n"   +customer +  "\n\n\n\n\n");
            
            if (customer == null) {
                checkCombo = true;
            }
            QLog.l().logQUser().debug("\n\n\n\nCHECK CUSTOMER\n\n :\n"   +addWindowButtons[0] +  "\n\n\n\n\n");
             return checkCombo;
        }
        
        public void CloseChannelEntry(){
            checkCombo=false;
        }

       public String getCFMSHidden() {
               String qsb = System.getenv ("QSB");
               if (qsb.equalsIgnoreCase("callbyticket")) {
                       checkCFMSHidden = "display: inline;";
               };
               if (qsb.equalsIgnoreCase("callbyname")) {
                       checkCFMSHidden = "display: inline;";
               };
               return checkCFMSHidden;
       }

       public String getCFMSHeight() {
               String qsb = System.getenv ("QSB");
               if (qsb.equalsIgnoreCase("callbyticket")) {
                       checkCFMSHeight = "70%";
               };
               if (qsb.equalsIgnoreCase("callbyname")) {
                       checkCFMSHeight = "70%";
               };
               return checkCFMSHeight;
       }
    
    private QCustomer customer = null;
    
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
    
    @Wire("#btn_invite")
    private Button btn_invite;

    @Wire("#incClientDashboard #service_list")
    private Listbox service_list;
    
    @Wire("#incClientDashboard #postpone_list")
    private Listbox postpone_list;

    @Command
    @NotifyChange(value = {"btnsDisabled", "customer", "avaitColumn"})
    public void invite() {
       
        QLog.l().logQUser().debug("Invite by " + user.getName());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        
//        QLog.l().logQUser().debug("\n\n\n\nBEFORE INTO EXCECUTE \n\n\n\n\n");
        final RpcInviteCustomer result = (RpcInviteCustomer) Executer.getInstance().getTasks().get(Uses.TASK_INVITE_NEXT_CUSTOMER).process(params, "", new byte[4]);
        if (result.getResult() != null) {
            customer = result.getResult();
//            if (customer != null && customer.getPostponPeriod() > 0) {
//                Messagebox.show(l("client_was_postponed_on")
//                        + " " + customer.getPostponPeriod() + " "
//                        + l("min_invited_status")
//                        + " \"" + customer.getPostponedStatus() + "\".", l("inviting_postponed"), Messagebox.OK, Messagebox.INFORMATION);
//            }
            setKeyRegim(KEYS_INVITED);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
            this.addServeScreen();
        } else {
            Messagebox.show(l("no_clients"), l("inviting_next"), Messagebox.OK, Messagebox.INFORMATION);
        }
        service_list.setModel(service_list.getModel());
        
    }
    
    @Command
    public void addServeScreen() {
        ((Checkbox) serveCustomerDialogWindow.getFellow("inaccurateTimeCheckBox")).setChecked(false);
        serveCustomerDialogWindow.setVisible(true);
        serveCustomerDialogWindow.doModal();
    }

    @Command
    @NotifyChange(value = {"btnsDisabled", "customer"})
    public void kill() {
        
         Messagebox.show("Do you want to remove the client?", "Remove", new Messagebox.Button[]{
            Messagebox.Button.YES, Messagebox.Button.NO}, Messagebox.QUESTION, (Messagebox.ClickEvent t) -> {
                QLog.l().logQUser().debug("Kill by " + user.getName() + " customer " + customer.getFullNumber());
                if (t.getButton() != null && t.getButton().compareTo(Messagebox.Button.YES) == 0) {
                    final CmdParams params = new CmdParams();
                    
                    params.userId = user.getUser().getId();
                    Executer.getInstance().getTasks().get(Uses.TASK_KILL_NEXT_CUSTOMER).process(params, "", new byte[4]);
                    
                    customer.refreshPrevious();
                    customer = null;
                    
                    
                    setKeyRegim(KEYS_MAY_INVITE);
                    service_list.setModel(service_list.getModel());
                    
                    BindUtils.postNotifyChange(null, null, Form.this, "*");
                    serveCustomerDialogWindow.setVisible(false);

                }
         });
    }

    @Command
    @NotifyChange(value = {"btnsDisabled"})
    public void begin() {
//        QLog.l().logQUser().debug("Begin by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER).process(params, "", new byte[4]);

        setKeyRegim(KEYS_STARTED);
        service_list.setModel(service_list.getModel());
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }

    @Command
    public void updateComments(){
        // Inheritance the comment from Serve-customer window to hold window
        String tempComment = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments")).getText();
        customer.setTempComments(tempComment);
        QLog.l().logQUser().debug("\n\nPostponed!!:\n" + customer.getTempComments() + "\n\n\n");
    }
    
    @Command
    public void postpone() {
        QLog.l().logQUser().debug("Postpone by " + user.getName() + " customer " + customer.getFullNumber());
        postponeCustomerDialog.setVisible(true);
        postponeCustomerDialog.doModal();
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }
    
    @Command
    public void ReturnedRedirect(){
        

//        final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_NORMAL, Boolean.FALSE);
//        //params.textData = ((Combobox) postponeCustomerDialog.getFellow("resultBox")).getSelectedItem().getLabel();
//        params.comments = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments")).getText();
//        customer.setTempComments(params.comments);
//        
//        
//        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_TO_POSTPON).process(params, "", new byte[4]);
//        customer = null;
//
//        setKeyRegim(KEYS_MAY_INVITE);
//        postpone_list.setModel(postpone_list.getModel());
//        postponeCustomerDialog.setVisible(false);
//        serveCustomerDialogWindow.setVisible(false);
//        ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).setText("");
//        BindUtils.postNotifyChange(null, null, Form.this, "*");
//        
//        final QUser user = QUserList.getInstance().getById(params.userId);
//        //переключение на кастомера при параллельном приеме, должен приехать customerID
//        // switch to the custodian with parallel reception, must arrive customerID
//        if (params.customerId != null) {
//            final QCustomer parallelCust = user.getParallelCustomers().get(params.customerId);
//            if (parallelCust == null) {
//                QLog.l().logger().warn("PARALLEL: User have no Customer for switching by customer ID=\"" + params.customerId + "\"");
//            } else {
//                user.setCustomer(parallelCust);
//                QLog.l().logger().debug("Юзер \"" + user + "\" переключился на кастомера \"" + parallelCust.getFullNumber() + "\"");
//            }
//        }
//
////            Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER).process(params, "", new byte[4]);
//        
//        this.addToQueue(params);
//        setKeyRegim(KEYS_MAY_INVITE);
//        service_list.setModel(service_list.getModel());
//        serveCustomerDialogWindow.setVisible(false);

//        if (pickedRedirectServ != null) {
//            if (!pickedRedirectServ.isLeaf()) {
//                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
//                return;
//            }
//            customer = user.getUser().getCustomer();
            final CmdParams params = new CmdParams();
            params.userId = user.getUser().getId();
            params.resultId = -1l;
            params.priority = Uses.PRIORITY_NORMAL;
            params.isMine = Boolean.FALSE;          
            params.welcomeTime = user.getCustomerWelcomeTime();
//            final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_NORMAL, Boolean.FALSE);
            params.comments = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments")).getText();

//            Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER).process(params, "", new byte[4]);
            
//            QLog.l().logQUser().debug("\n\n\n CHECK SERVICE NAME" + user.getUser().getCustomer().getService().getName() + "\nCHECK SERVICEID: " + params.serviceId + "\n\n");
         
            customer = user.getUser().getCustomer();
            params.serviceId = user.getUser().getCustomer().getService().getId();
            customer.setTempComments(params.comments);      
            
            Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_RETURN_QUEUE).process(params, "", new byte[4]);
//            Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER).process(params, "", new byte[4]);
            
//            this.addToQueue(params);
            
            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
//            QLog.l().logQUser().debug("\n\nTEST LIST: " + service_list.getModel() + "\n\n");
            serveCustomerDialogWindow.setVisible(false);
//        }




    }
    
    @Command
    @NotifyChange(value = {"addWindowButtons"})
    public void redirect() {

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            

            final CmdParams params = new CmdParams();
            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.resultId = -1l;
            params.priority = Uses.PRIORITY_NORMAL;
            params.isMine = Boolean.FALSE;
           
            params.welcomeTime = user.getCustomerWelcomeTime();
//            final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_NORMAL, Boolean.FALSE);

            params.comments = ((Textbox) serveCustomerDialogWindow.getFellow("editable_comments")).getText();

//            Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER).process(params, "", new byte[4]);
            customer.setTempComments(params.comments);
            
            
            final QUser user = QUserList.getInstance().getById(params.userId);
            //переключение на кастомера при параллельном приеме, должен приехать customerID
            // switch to the custodian with parallel reception, must arrive customerID
            if (params.customerId != null) {
                final QCustomer parallelCust = user.getParallelCustomers().get(params.customerId);
                if (parallelCust == null) {
                    QLog.l().logger().warn("PARALLEL: User have no Customer for switching by customer ID=\"" + params.customerId + "\"");
                } else {
                    user.setCustomer(parallelCust);
                    QLog.l().logger().debug("Юзер \"" + user + "\" переключился на кастомера \"" + parallelCust.getFullNumber() + "\"");
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
    @NotifyChange(value = {"addWindowButtons"})
    public void addClient(){
        user.setCustomerWelcomeTime(new Date());
        addWindowButtons[0] = true;
        addWindowButtons[1] = false;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;
//        customer.setChannels(1);
        pickedRedirectServ=null;
        ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");
        
        this.addTicketScreen();
    }
    
    @Command
    @NotifyChange(value = {"addWindowButtons"})
    public void addNextService(){
        addWindowButtons[0] = false;
        addWindowButtons[1] = false;
        addWindowButtons[2] = true;
        addWindowButtons[3] = false;
        // refresh the service list. Remove the default service selection
        pickedRedirectServ = null;
        this.addTicketScreen();
//        this.refreshQuantity();
    }
    
    @Command
    public void disableButtons(){
//        addWindowButtons[0] = false;
//        addWindowButtons[1] = false;
//        addWindowButtons[2] = false;
//        addWindowButtons[3] = false;
//        addWindowButtons[4] = false;
//        addWindowButtons[5] = false;
//        addWindowButtons[6] = false;
//        addWindowButtons[7] = false;
//        addWindowButtons[8] = false;
        boolean[] inaccurateChecked = new boolean[]{true, true, true, true, true, false, true, true, true};
        if(((Checkbox) serveCustomerDialogWindow.getFellow("inaccurateTimeCheckBox")).isChecked()){
            setBtnsDisabled(inaccurateChecked);
        }else{setKeyRegim(KEYS_STARTED);
        }
        
//        setBtnsDisabled(inaccurateChecked);
//        QLog.l().logQUser().debug("\n\n\n\n DISABLE BUTTON");
        BindUtils.postNotifyChange(null, null, Form.this, "*");
    }

    @Command
    @NotifyChange(value = {"btnsDisabled", "customer"})
    public void finish() {
        QLog.l().logQUser().debug("Finish by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        
        params.resultId = -1L;
        params.textData = "";

//        params.requestBack = true;
//        params.inAccurateFinish = true;

        params.inAccurateFinish = ((Checkbox) serveCustomerDialogWindow.getFellow("inaccurateTimeCheckBox")).isChecked();

                
        final RpcStandInService res = (RpcStandInService) Executer.getInstance().getTasks().get(Uses.TASK_FINISH_CUSTOMER).process(params, "", new byte[4]);
        // вернется кастомер и возможно он еще не домой а по списку услуг. Список определяется при старте кастомера в обработку специяльным юзером в регистратуре
        if (res.getResult() != null && res.getResult().getService() != null && res.getResult().getState() == CustomerState.STATE_WAIT_COMPLEX_SERVICE) {
            Messagebox.show(l("next_service") + " \"" + res.getResult().getService().getName() + "\". " + l("customer_number") + " \"" + res.getResult().getPrefix() + res.getResult().getNumber() + "\"." + "\n\n" + res.getResult().getService().getDescription(), l("contumie_complex_service"), Messagebox.OK, Messagebox.INFORMATION);
        }
        
        customer.refreshPrevious();
        customer = null;
        
        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
        BindUtils.postNotifyChange(null, null, Form.this, "*");
        serveCustomerDialogWindow.setVisible(false);

    }

    //********************************************************************************************************************************************
    //**  Change priority - By Customer
    //********************************************************************************************************************************************
    private QCustomer pickedCustomer;

    public QCustomer getPickedCustomer() {
        return pickedCustomer;
    }

    public void setPickedCustomer(QCustomer pickedCustomer) {
        this.pickedCustomer = pickedCustomer;
    }

    
    //********************************************************************************************************************************************
    //**  Change priority - By Service
    //********************************************************************************************************************************************
    @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
    Window changeServicePriorityDialog;
    private QPlanService pickedService;

    public QPlanService getPickedService() {
        return pickedService;
    }

    public void setPickedService(QPlanService pickedService) {
        this.pickedService = pickedService;
    }

    @Command
    //public void clickList(@BindingParam("st") String st) {
    public void clickListServices() {

//        if (pickedService != null) {
//            if (!pickedService.getFlexible_coef()) {
//                Messagebox.show(l("forbid_change_priority"), l("change_priority"), Messagebox.OK, Messagebox.INFORMATION);
//                return;
//            }
//            changeServicePriorityDialog.setVisible(true);
//            changeServicePriorityDialog.doModal();
//        }
    }

    @Command
    public void inviteCustomerNow() {
        // 1. Postpone the customer 
        // 2. Pick the customer from Postponed list
        
        if (pickedCustomer==null || keys_current == KEYS_INVITED || keys_current== KEYS_STARTED || keys_current == KEYS_OFF){
            return;
        }
        
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.postponedPeriod = 0;
        params.customerId = pickedCustomer.getId();
        params.isMine = Boolean.TRUE;
        user.getUser().setCustomer(pickedCustomer);
        Executer.getInstance().getTasks().get(Uses.TASK_INVITE_SELECTED_CUSTOMER).process(params, "", new byte[4],pickedCustomer);
        customer = null;

        service_list.setModel(service_list.getModel());

        Executer.getInstance().getTasks().get(Uses.TASK_INVITE_POSTPONED).process(params, "", new byte[4]);
        customer = user.getUser().getCustomer();

        setKeyRegim(KEYS_INVITED);
        BindUtils.postNotifyChange(null, null, Form.this, "*");
        pickedRedirectServ = pickedCustomer.getService();       //for returning to queue use
        pickedCustomer = null;  //TEST andrew                   //debug the clicking white space inviting problem
        this.addServeScreen();
    }

    public LinkedList<String> prior_St = new LinkedList(Uses.get_COEFF_WORD().values());

    public LinkedList<String> getPrior_St() {
        return prior_St;
    }

    public void setPrior_St(LinkedList<String> prior_St) {
        this.prior_St = prior_St;
    }

    @Command
    @NotifyChange(value = {"user"})
    public void closeChangePriorityDialog() {
        changeServicePriorityDialog.setVisible(false);
    }
    private String oldSt = "";
    
    public String getAvaitColumn(){
        return user.getTotalLineSizeStr();
    }

    @Command
    @NotifyChange(value = {"postponList", "avaitColumn"})
    public void refreshListServices() {
        if (isLogin()) {
            // тут поддержание сессии как в веб приложении Here the maintenance of the session as a web application
            UsersInside.getInstance().getUsersInside().put(user.getName() + user.getPassword(), new Date().getTime());
            // тут поддержание сессии как залогинившегося юзера в СУО Here the maintenance of the session as a logged user in the MSA
            QSessions.getInstance().update(user.getUser().getId(), Sessions.getCurrent().getRemoteHost(), Sessions.getCurrent().getRemoteAddr().getBytes());


            final StringBuilder st = new StringBuilder();
            int number = user.getPlan().size();
            
            user.getPlan().forEach((QPlanService p) -> {
                st.append(user.getLineSize(p.getService().getId()));
            });
            
            if (!oldSt.equals(st.toString())) {
                /*
                if ("".equals(oldSt.replaceAll("0+", "")) && customer == null) {
                    Clients.showNotification(l("do_invite"), Clients.NOTIFICATION_TYPE_WARNING, btn_invite, "before_start", 0, true);
                }
                */
                user.setCustomerList(user.getPlan());                
                service_list.setModel(service_list.getModel());
                oldSt = st.toString();
                Sort();
                BindUtils.postNotifyChange(null, null, Form.this, "*");  
                
            }
        }
    }

    //********************************************************************************************************************************************
    //** Отложенные Отложить посетителя Postponed Postpone visitor
    //********************************************************************************************************************************************
    @Wire("#incClientDashboard #incPostponeCustomerDialog #postponeDialog")
    Window postponeCustomerDialog;

    @Command
    public void closePostponeCustomerDialog() {
        postponeCustomerDialog.setVisible(false);
        ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).setText("");
        BindUtils.postNotifyChange(null, null, Form.this, "*");
        serveCustomerDialogWindow.setVisible(false);

    }

    @Command
    @NotifyChange(value = {"postponList", "customer", "btnsDisabled"})
    public void OKPostponeCustomerDialog() {
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        //params.textData = ((Combobox) postponeCustomerDialog.getFellow("resultBox")).getSelectedItem().getLabel();
//        QLog.l().logger().debug("\n\n\n\n\n\nTIMEBOX" + ((Combobox) postponeCustomerDialog.getFellow("timeBox")).getSelectedIndex() +  "\n\n\n\n\n\n");
        params.postponedPeriod = ((Combobox) postponeCustomerDialog.getFellow("timeBox")).getSelectedIndex() * 5;
        params.comments = ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).getText();
         
        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_TO_POSTPON).process(params, "", new byte[4]);
        customer = null;

        setKeyRegim(KEYS_MAY_INVITE);
        postpone_list.setModel(postpone_list.getModel());
        postponeCustomerDialog.setVisible(false);
        serveCustomerDialogWindow.setVisible(false);
        ((Textbox) postponeCustomerDialog.getFellow("tb_onHold")).setText("");
        BindUtils.postNotifyChange(null, null, Form.this, "*");
        
    }
    
    @Command
    public void DetermineChannels(){
//        QLog.l().logger().debug("\n\n\nI GOT CHANNELS  REDIRECT SERVE" + pickedRedirectServ +  "\n\n\n");
//        QLog.l().logger().debug("\n\n\nI GOT CHANNELS  MAIN SERVE" + pickedMainService +  "\n\n\n");

//        Remove selecting service first function
//        if (pickedRedirectServ != null || pickedMainService!=null ) {
//            if (!pickedRedirectServ.isLeaf()) {
//                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
//                return;
//            }
            int channelIndex = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex()+1;
            
//            QLog.l().logger().debug("\n\n\nAdd and serve button, which WINDOW:    " + addWindowButtons[0] +  "\n\n\n");
            

// Auto close for redirect channels
//            if(channelIndex>4){
//                if(addWindowButtons[0])
//                    this.closeAddAndServeDialog();
//                else
//                    this.closeAddNextServiceDialog();
//                
//                this.finish();
//            }

//        }

//        remove pop-up alert
//        else{
//            Messagebox.show(l("first_select_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
//            refreshChannels();      //set channels to default when popup window show up. 
//        }
    }
    
    @Command
    public void ChangeChannels(){
        int channelIndex = ((Combobox) serveCustomerDialogWindow.getFellow("Change_Channels")).getSelectedIndex() + 1;
        String channels = ((Combobox) serveCustomerDialogWindow.getFellow("Change_Channels")).getSelectedItem().getValue().toString();
        customer.setChannels(channels);
        customer.setChannelsIndex(channelIndex);
//      Remove auto close channels in serve_customer window
//        if(channelIndex>4){
//            this.finish();
//        }
    }
    
    public void refreshChannels(){
        ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).setSelectedIndex(0);
    }

    private final LinkedList<QCustomer> postponList = QPostponedList.getInstance().getPostponedCustomers();

    public LinkedList<QCustomer> getPostponList() {
        return QPostponedList.getInstance().getPostponedCustomers();
    }

    private final LinkedList<QResult> resultList = QResultList.getInstance().getItems();

    public LinkedList<QResult> getResultList() {
        return QResultList.getInstance().getItems();
    }

    private static QCustomer pickedPostponed;

    public QCustomer getPickedPostponed() {
        return pickedPostponed;
    }

    public void setPickedPostponed(QCustomer pickedPostponed) {
        this.pickedPostponed = pickedPostponed;
    }

    // *** Диалоги изменения состояния и вызова лтложенных Dialogs for changing the state and calling ltalges
    @Wire("#incClientDashboard #incChangePostponedStatusDialog #changePostponedStatusDialog")
    Window changePostponedStatusDialog;

    @Command
    public void clickListPostponedChangeStatus() {
        ((Combobox) changePostponedStatusDialog.getFellow("pndResultBox")).setText(pickedPostponed.getPostponedStatus());
        changePostponedStatusDialog.setVisible(true);
        changePostponedStatusDialog.doModal();
    }

    @Command
    @NotifyChange(value = {"postponList"})
    public void closeChangePostponedStatusDialog() {
        final CmdParams params = new CmdParams();
        // кому
        params.customerId = pickedPostponed.getId();
        // на что
        params.textData = ((Combobox) changePostponedStatusDialog.getFellow("pndResultBox")).getText();
        Executer.getInstance().getTasks().get(Uses.TASK_POSTPON_CHANGE_STATUS).process(params, "", new byte[4]);

        changePostponedStatusDialog.setVisible(false);
    }

    @Command
    public void clickListPostponedInvite() {
        if (user.getPlan().isEmpty() || pickedPostponed==null) {
            return;
        }
        Messagebox.show("Do you want to invite citizen " + pickedPostponed.getFullNumber() + " ?", l("inviting_client"), new Messagebox.Button[]{
            Messagebox.Button.YES, Messagebox.Button.NO}, Messagebox.QUESTION, (Messagebox.ClickEvent t) -> {
            QLog.l().logQUser().debug("Invite postponed by " + user.getName() + " citizen " + pickedPostponed.getFullNumber());
            if (t.getButton() != null && t.getButton().compareTo(Messagebox.Button.YES) == 0) {
                final CmdParams params = new CmdParams();
                // @param userId id юзера который вызывает The user who causes
                // @param id это ID кастомера которого вызываем из пула отложенных, оно есть т.к. с качстомером давно работаем
                // It is the ID of the caller which is called from the pool of deferred, it is because With a long-stroke tool we have been working for a long time
                params.customerId = pickedPostponed.getId();
                params.userId = user.getUser().getId();
                Executer.getInstance().getTasks().get(Uses.TASK_INVITE_POSTPONED).process(params, "", new byte[4]);
                customer = user.getUser().getCustomer();

                setKeyRegim(KEYS_INVITED);
                BindUtils.postNotifyChange(null, null, Form.this, "postponList");
                BindUtils.postNotifyChange(null, null, Form.this, "customer");
                BindUtils.postNotifyChange(null, null, Form.this, "btnsDisabled");
                
                this.addServeScreen();
                this.begin();
                
                pickedPostponed = null;
            }
            else{
                pickedPostponed = null;
            }
        });
    }
    //********************************************************************************************************************************************
    //** Перенаправление Redirection
    //********************************************************************************************************************************************
    private final TreeServices treeServs = new TreeServices();

    public TreeServices getTreeServs() {
        
        return treeServs;
    }
    
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
    

    
    @Wire("#incClientDashboard #GAManagement")
    Window GAManagement;
    
    private Combobox cboFmCompress;
    
    QService pickedMainService;

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
        //Remove previous comments and categories searched
        this.refreshAddWindow();
        this.refreshChannels();
//        this.CloseChannelEntry();
        
        addTicketDailogWindow.setVisible(true);
        addTicketDailogWindow.doModal();
        
    }
    
    public void refreshAddWindow() {
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText("");
        ((Textbox) addTicketDailogWindow.getFellow("ticket_comments")).setText("");
        ((Combobox) addTicketDailogWindow.getFellow("cboFmCompress")).setText("");
        
        listServices = getAllListServices();
        pickedMainService=null;
        BindUtils.postNotifyChange(null, null, Form.this, "listServices");
    }
    
    public void onSelect$cboFmCompress(Event event) {
   	QLog.l().logQUser().debug("C: ----" + cboFmCompress.getSelectedItem().getValue().toString());
   }
    
    @Listen("onChange = #categoriesCombobox")
    public void changeCategories() {
        String category = cboFmCompress.getValue();
        QLog.l().logQUser().debug("C:" + category + " , " +pickedMainService.getName() +" , "+ pickedMainService.getId() + " , " + pickedMainService.getParentId());
        

//        if(((ListModelList<String>)countriesModel).contains(country)) {
//            car.setCountry(country);
//            showNotify("Changed to: " + country, countriesCombobox);
//        } else {
//            showNotify("Unknow country : " + country, countriesCombobox);
//        }
    }
    
    private String filter="";
    
    public String getFilter() {
        return filter;
    }
    
    @NotifyChange
    public void setFilter(String filter) {
        this.filter = filter;
    }
    
    @NotifyChange("listServices")
    @Command    
    public void changeCategory(){
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText( "" );
        
        LinkedList<QService> allServices =  QServiceTree.getInstance().getNodes();
        List<QService> requiredServices = null;

        if (getPickedMainService() == null){
            QLog.l().logQUser().debug("null category was selected");
            requiredServices = allServices
                    .stream()
                    .filter((QService service) -> service.getParentId()!=null  && !service.getParentId().equals(1L))
                    .collect(Collectors.toList());
        } else {
            QLog.l().logQUser().debug("Category " + pickedMainService.getName() + " was selected");
            requiredServices = allServices
                    .stream()
                    .filter((QService service) -> service.getParentId()!=null && (service.getParent().getName().toLowerCase().contains(pickedMainService.getName().toLowerCase()) || service.getName().toLowerCase().contains(pickedMainService.getName().toLowerCase())) && !service.getParentId().equals(1L))
                    .collect(Collectors.toList());
        }
        
        listServices = requiredServices;
    }
    
    @NotifyChange("listServices")
    @Command
    public void doSearch() {
        listServices.clear();
            LinkedList<QService> allServices =  QServiceTree.getInstance().getNodes();
            List<QService> requiredServices;
            
            if (pickedMainService==null){
                requiredServices = allServices
                    .stream()
                    .filter((QService service) -> service.getParentId()!=null && (service.getDescription().toLowerCase().contains(filter.toLowerCase()) || service.getParent().getName().toLowerCase().contains(filter.toLowerCase()) || service.getName().toLowerCase().contains(filter.toLowerCase())) && !service.getParentId().equals(1L))
                    .collect(Collectors.toList());

            }else{
                 requiredServices = allServices
                    .stream()
                    .filter((QService service) -> service.getParentId()!=null && (service.getDescription().toLowerCase().contains(pickedMainService.getName().toLowerCase()) || service.getParent().getName().toLowerCase().contains(pickedMainService.getName().toLowerCase()) || service.getName().toLowerCase().contains(pickedMainService.getName().toLowerCase())) && (service.getDescription().toLowerCase().contains(filter.toLowerCase()) || service.getParent().getName().toLowerCase().contains(filter.toLowerCase()) || service.getName().toLowerCase().contains(filter.toLowerCase())) && !service.getParentId().equals(1L))
                    .collect(Collectors.toList());
            }
            
            
            listServices = requiredServices;
    }
    
    private List<QService> listServices;
    
    public List<QService> getListServices() {
        
        if(listServices == null) {
            listServices = getAllListServices();
	}
	return listServices;
    }
    
    public List<QService> getAllListServices() {
        LinkedList<QService> allServices =  QServiceTree.getInstance().getNodes();
                
            List<QService> requiredServices = allServices
                .stream()
                .filter((QService service) -> service.getParentId()!=null && !service.getParentId().equals(1L))
                .collect(Collectors.toList());

            return requiredServices;
    }
    
    public List<QService> getCategories() {
        LinkedList<QService> allServices =  QServiceTree.getInstance().getNodes();
        
         List<QService> requiredServices = allServices
                .stream()
                .filter(service -> service.getParentId()!=null && service.getParentId().equals(1L))
                .collect(Collectors.toList());

         return requiredServices;
    }
    
    
    @Command
    public void closeAddNextServiceDialog(){

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

//            this.refreshQuantity();
            final CmdParams params = new CmdParams();

            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.requestBack = Boolean.FALSE;
            params.resultId = -1l;
            params.channelsIndex = customer.getChannelsIndex();
            params.channels = customer.getChannels();
            params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
            params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
            
            params.comments = ((Textbox) addTicketDailogWindow.getFellow("ticket_comments")).getText();
//            params.channelsIndex = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
//            QLog.l().logQUser().debug("\n\nDEBUG COMMENTS" + params.comments + "\n\n");

            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER).process(params, "", new byte[4]);
            
            customer = null;            
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            addTicketDailogWindow.setVisible(false);
            
            //Reset the combobox to default value/placeHolder 
            ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).setText("");
            
            this.invite();
            this.begin();
            this.refreshChannels();
            String replace = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
            customer.setChannels(params.new_channels);
            customer.setChannelsIndex(params.new_channels_Index);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
        }
    }
    
    @Command
    public void selectPreviousService(){
//        pickedRedirectServ = ((Combobox) serveCustomerDialogWindow.getFellow("previous_services")).getSelectedItem().getValue().toString();
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

//            this.refreshQuantity();
            final CmdParams params = new CmdParams();

            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.requestBack = Boolean.FALSE;
            params.resultId = -1l;
//            params.channelsIndex = customer.getChannelsIndex();
//            params.channels = customer.getChannels();
//            params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
//            params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
            
            params.comments = "";
//            params.channelsIndex = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;


            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER).process(params, "", new byte[4]);
            
            customer = null;            
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
//            serveCustomerDialogWindow.setVisible(false);
            
            this.invite();
            this.begin();
            this.refreshChannels();
            params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
            params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();           
            customer.setChannelsIndex(params.new_channels_Index);
            customer.setChannels(params.new_channels);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
        }
        
    }
    
    @Command
    public void closeAddToQueueDialog(){

        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            
            final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_NORMAL, Boolean.FALSE);
            
            this.addToQueue(params);
            QLog.l().logQUser().debug("\n\n\nWELCOME TIME:\n" + user.getCustomerWelcomeTime() +  "\n\n");
            
            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());          

            ListModelList model = (ListModelList) service_list.getModel();
            
            addTicketDailogWindow.setVisible(false);
        }
    }
    
    public void Sort() {
        Comparator cTimeAsc = new WaitingPanelComparator(true, 1);
        Comparator cTimeDsc = new WaitingPanelComparator(false, 1);
        user.getCustomerList().sort(cTimeAsc);  //Sort customerList by time asending order
    };
    
    
    public CmdParams paramsForAddingInQueue(Integer priority, Boolean isMine) {
        final CmdParams params = new CmdParams();

        params.userId = user.getUser().getId();
        params.serviceId = pickedRedirectServ.getId();
        params.resultId = -1l;
        params.priority = priority;
        params.isMine = isMine;
        params.comments = ((Textbox) addTicketDailogWindow.getFellow("ticket_comments")).getText();
        params.channelsIndex = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
        params.channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
        params.welcomeTime = user.getCustomerWelcomeTime();
        
        return params;
    }
    
    public RpcStandInService addToQueue(CmdParams params) {
            final RpcStandInService res = (RpcStandInService)Executer.getInstance().getTasks().get(Uses.TASK_STAND_IN).process(params, "", new byte[4]);
            return res;            
    }
    
    @Command
    @NotifyChange(value = {"addWindowButtons"})
    public void changeService(){
        addWindowButtons[0] = false;
        addWindowButtons[1] = true;
        addWindowButtons[2] = false;
        addWindowButtons[3] = false;
        
        this.addTicketScreen();
    }
    
    @Command
    public void closeChangeServiceDialog(){
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            if (!user.checkIfUserCanServe(pickedRedirectServ)){
                Messagebox.show(user.getName() + " doesn't have rights to serve citizens for this service. Try Add to Queue." , "Access Issues", Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            
            
            final CmdParams params = new CmdParams();
            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            params.comments = ((Textbox) addTicketDailogWindow.getFellow("ticket_comments")).getText();
            params.channelsIndex = customer.getChannelsIndex();
            params.channels = customer.getChannels();
            params.new_channels_Index = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedIndex() + 1;
            params.new_channels = ((Combobox) addTicketDailogWindow.getFellow("Channels_options")).getSelectedItem().getValue().toString();
            
            Executer.getInstance().getTasks().get(Uses.TASK_CHANGE_SERVICE).process(params, "", new byte[4]);
            
            service_list.setModel(service_list.getModel());
            addTicketDailogWindow.setVisible(false);
            customer.setChannels(params.new_channels);
            customer.setChannelsIndex(params.new_channels_Index);
            BindUtils.postNotifyChange(null, null, Form.this, "*");
        }
    }
    

    @Command
    public void closeAddAndServeDialog(){
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            if (!user.checkIfUserCanServe(pickedRedirectServ)){
                Messagebox.show(user.getName() + " doesn't have rights to serve citizens for this service. Try Add to Queue." , "Access Issues", Messagebox.OK, Messagebox.EXCLAMATION);
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
    @NotifyChange(value = {"postponList", "customer", "btnsDisabled"})
    public void closeRedirectDialog() {
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = new CmdParams();

            params.userId = user.getUser().getId();
            params.serviceId = pickedRedirectServ.getId();
            //params.requestBack = ((Checkbox) redirectCustomerDialog.getFellow("cb_redirect")).isChecked();
            params.resultId = -1l;
            params.comments = ((Textbox) addTicketDailogWindow.getFellow("ticket_comments")).getText();
            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER).process(params, "", new byte[4]);
            
            customer = null;            
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            addTicketDailogWindow.setVisible(false);
            serveCustomerDialogWindow.setVisible(false);
        }
    }
    
    QService pickedRedirectServ;

    public QService getPickedRedirectServ() {
        return pickedRedirectServ;
    }

    public void setPickedRedirectServ(QService pickedRedirectServ) {        
        String serviceName = pickedRedirectServ.getName();
        
        ((Textbox) addTicketDailogWindow.getFellow("typeservices")).setText( serviceName );
        this.pickedRedirectServ = pickedRedirectServ;
    }
    
    public void refreshQuantity(){      
        customer = user.getUser().getCustomer();
        customer.setQuantity("1");
//        List<QService> servicesListDuplicate = user.getUser().getCustomer().getServicesList();
//        for (QService resetQty : servicesListDuplicate){
//            resetQty.setQuantity("1");
//        }
    }
    

}
