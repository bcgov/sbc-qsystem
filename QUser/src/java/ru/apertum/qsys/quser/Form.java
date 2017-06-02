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
import java.util.Locale;
import java.util.Properties;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import ru.apertum.qsys.quser.Multilingual.Lng;
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
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;
import ru.apertum.qsystem.server.model.postponed.QPostponedList;
import ru.apertum.qsystem.server.model.results.QResult;
import ru.apertum.qsystem.server.model.results.QResultList;

/**
 *
 * @author Evgeniy Egorov
 */
public class Form {

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
            /*
             try {
             Clients.reloadMessages(prefer_locale);
             } catch (IOException ex) {
             System.err.println("Locales bad-bad! " + ex);
             }
             Locales.setThreadLocal(prefer_locale);
             */
        }
    }

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

    @Command
    @NotifyChange(value = {"btnsDisabled", "login", "user", "postponList", "customer", "avaitColumn"})
    public void login() {
        QLog.l().logQUser().debug("Login " + user.getName());

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
        return QUserList.getInstance().getItems();
    }

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
        btnsDisabled[6] = !(isLogin() && '1' == regim.charAt(0));
        btnsDisabled[7] = !(isLogin() && '1' == regim.charAt(0));
    }

    private boolean[] btnsDisabled = new boolean[]{true, true, true, true, true, true, true, true};

    public boolean[] getBtnsDisabled() {
        return btnsDisabled;
    }

    public void setBtnsDisabled(boolean[] btnsDisabled) {
        this.btnsDisabled = btnsDisabled;
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

    @Command
    @NotifyChange(value = {"btnsDisabled", "customer", "avaitColumn"})
    public void invite() {
        QLog.l().logQUser().debug("Invite by " + user.getName());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        final RpcInviteCustomer result = (RpcInviteCustomer) Executer.getInstance().getTasks().get(Uses.TASK_INVITE_NEXT_CUSTOMER).process(params, "", new byte[4]);
        if (result.getResult() != null) {
            customer = result.getResult();
            if (customer != null && customer.getPostponPeriod() > 0) {
                Messagebox.show(l("client_was_postponed_on")
                        + " " + customer.getPostponPeriod() + " "
                        + l("min_invited_status")
                        + " \"" + customer.getPostponedStatus() + "\".", l("inviting_postponed"), Messagebox.OK, Messagebox.INFORMATION);
            }
            setKeyRegim(KEYS_INVITED);
        } else {
            Messagebox.show(l("no_clients"), l("inviting_next"), Messagebox.OK, Messagebox.INFORMATION);
        }
        service_list.setModel(service_list.getModel());
    }

    @Command
    @NotifyChange(value = {"btnsDisabled", "customer"})
    public void kill() {
        QLog.l().logQUser().debug("Kill by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        Executer.getInstance().getTasks().get(Uses.TASK_KILL_NEXT_CUSTOMER).process(params, "", new byte[4]);
        customer = null;
        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
    }

    @Command
    @NotifyChange(value = {"btnsDisabled"})
    public void begin() {
        QLog.l().logQUser().debug("Begin by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        Executer.getInstance().getTasks().get(Uses.TASK_START_CUSTOMER).process(params, "", new byte[4]);

        setKeyRegim(KEYS_STARTED);
        service_list.setModel(service_list.getModel());
    }

    @Command
    public void postpone() {
        QLog.l().logQUser().debug("Postpone by " + user.getName() + " customer " + customer.getFullNumber());
        postponeCustomerDialog.setVisible(true);
        postponeCustomerDialog.doModal();
    }

    @Command
    public void redirect() {
        QLog.l().logQUser().debug("Redirect by " + user.getName() + " customer " + customer.getFullNumber());
        redirectCustomerDialog.setVisible(true);
        redirectCustomerDialog.doModal();
    }
    
    @Command
    public void addClient(){
            servicesDialogWindow.setVisible(true);
            servicesDialogWindow.doModal();
    }
    
    @Command
    public void addNextService(){
        QLog.l().logQUser().debug("Add Next Service");    
    }

    @Command
    @NotifyChange(value = {"btnsDisabled", "customer"})
    public void finish() {
        QLog.l().logQUser().debug("Finish by " + user.getName() + " customer " + customer.getFullNumber());
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.resultId = -1L;
        params.textData = "";
        final RpcStandInService res = (RpcStandInService) Executer.getInstance().getTasks().get(Uses.TASK_FINISH_CUSTOMER).process(params, "", new byte[4]);
        // вернется кастомер и возможно он еще не домой а по списку услуг. Список определяется при старте кастомера в обработку специяльным юзером в регистратуре
        if (res.getResult() != null && res.getResult().getService() != null && res.getResult().getState() == CustomerState.STATE_WAIT_COMPLEX_SERVICE) {
            Messagebox.show(l("next_service") + " \"" + res.getResult().getService().getName() + "\". " + l("customer_number") + " \"" + res.getResult().getPrefix() + res.getResult().getNumber() + "\"." + "\n\n" + res.getResult().getService().getDescription(), l("contumie_complex_service"), Messagebox.OK, Messagebox.INFORMATION);
        }
        customer = null;
        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
    }

    //********************************************************************************************************************************************
    //**  Change priority
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
        if (pickedService != null) {
            if (!pickedService.getFlexible_coef()) {
                Messagebox.show(l("forbid_change_priority"), l("change_priority"), Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
            changeServicePriorityDialog.setVisible(true);
            changeServicePriorityDialog.doModal();
        }
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
            user.getPlan().forEach((QPlanService p) -> {
                st.append(user.getLineSize(p.getService().getId()));
            });

            if (!oldSt.equals(st.toString())) {
                if ("".equals(oldSt.replaceAll("0+", "")) && customer == null) {
                    Clients.showNotification(l("do_invite"), Clients.NOTIFICATION_TYPE_WARNING, btn_invite, "start_center", 0, true);
                }
                service_list.setModel(service_list.getModel());
                oldSt = st.toString();
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
    }

    @Command
    @NotifyChange(value = {"postponList", "customer", "btnsDisabled"})
    public void OKPostponeCustomerDialog() {
        final CmdParams params = new CmdParams();
        params.userId = user.getUser().getId();
        params.textData = ((Combobox) postponeCustomerDialog.getFellow("resultBox")).getSelectedItem().getLabel();
        params.postponedPeriod = ((Combobox) postponeCustomerDialog.getFellow("timeBox")).getSelectedIndex() * 5;
        Executer.getInstance().getTasks().get(Uses.TASK_CUSTOMER_TO_POSTPON).process(params, "", new byte[4]);
        customer = null;

        setKeyRegim(KEYS_MAY_INVITE);
        service_list.setModel(service_list.getModel());
        postponeCustomerDialog.setVisible(false);
    }

    private final LinkedList<QCustomer> postponList = QPostponedList.getInstance().getPostponedCustomers();

    public LinkedList<QCustomer> getPostponList() {
        return QPostponedList.getInstance().getPostponedCustomers();
    }

    private final LinkedList<QResult> resultList = QResultList.getInstance().getItems();

    public LinkedList<QResult> getResultList() {
        return QResultList.getInstance().getItems();
    }

    private QCustomer pickedPostponed;

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
        if (user.getPlan().isEmpty()) {
            return;
        }
        Messagebox.show(l("do_you_want_invite"), l("inviting_client"), new Messagebox.Button[]{
            Messagebox.Button.YES, Messagebox.Button.NO}, Messagebox.QUESTION, (Messagebox.ClickEvent t) -> {
            QLog.l().logQUser().debug("Invite postponed by " + user.getName() + " customer " + pickedPostponed.getFullNumber());
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
    
    @Wire("#incClientDashboard #incServicesDialog #servicesDialog")
    Window servicesDialogWindow;

    @Wire("#incClientDashboard #incRedirectCustomerDialog #redirectDialog")
    Window redirectCustomerDialog;

    @Command
    public void closeAddToQueueDialog(){
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }
            
            final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_NORMAL, Boolean.FALSE);
            this.addToQueue(params);
            
            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            servicesDialogWindow.setVisible(false);
        }
    }
    
    public CmdParams paramsForAddingInQueue(Integer priority, Boolean isMine) {
        final CmdParams params = new CmdParams();

        params.userId = user.getUser().getId();
        params.serviceId = pickedRedirectServ.getId();
        params.resultId = -1l;
        params.textData = ((Textbox) redirectCustomerDialog.getFellow("tb_redirect")).getText();
        params.priority = priority;
        params.isMine = isMine;
            
        return params;
    }
    
    public RpcStandInService addToQueue(CmdParams params) {
            final RpcStandInService res = (RpcStandInService)Executer.getInstance().getTasks().get(Uses.TASK_STAND_IN).process(params, "", new byte[4]);
            return res;            
    }

    @Command
    public void closeAddAndServeDialog(){
        if (pickedRedirectServ != null) {
            if (!pickedRedirectServ.isLeaf()) {
                Messagebox.show(l("group_not_service"), l("selecting_service"), Messagebox.OK, Messagebox.EXCLAMATION);
                return;
            }

            final CmdParams params = this.paramsForAddingInQueue(Uses.PRIORITY_VIP, Boolean.TRUE);
            final RpcStandInService res = this.addToQueue(params);
            customer = res.getResult();

            customer = null;
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            servicesDialogWindow.setVisible(false);
            
            this.invite();
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
            params.requestBack = ((Checkbox) redirectCustomerDialog.getFellow("cb_redirect")).isChecked();
            params.resultId = -1l;
            params.textData = ((Textbox) redirectCustomerDialog.getFellow("tb_redirect")).getText();
            Executer.getInstance().getTasks().get(Uses.TASK_REDIRECT_CUSTOMER).process(params, "", new byte[4]);
            
            customer = null;            
            setKeyRegim(KEYS_MAY_INVITE);
            service_list.setModel(service_list.getModel());
            redirectCustomerDialog.setVisible(false);
        }
    }
    QService pickedRedirectServ;

    public QService getPickedRedirectServ() {
        return pickedRedirectServ;
    }

    public void setPickedRedirectServ(QService pickedRedirectServ) {
        this.pickedRedirectServ = pickedRedirectServ;
    }

}
