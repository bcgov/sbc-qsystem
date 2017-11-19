/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.qboard;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Columnchildren;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.model.*;
import ru.apertum.qsystem.smartboard.PrintRecords;
import ru.apertum.qsystem.smartboard.PrintRecordsList;
import ru.apertum.qsystem.server.QSessions;
import org.zkoss.zk.ui.Sessions;

/**
 *
 * @author Evgeniy Egorov
 */
public class QBoard extends GenericForwardComposer {
   
    /**
     * Это нужно чтоб делать include во view и потом связывать @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
     *
     * @param view
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view)  {
        String office_id = Executions.getCurrent().getParameter("office_id");
        Sessions.getCurrent().setAttribute("office_id", office_id);
        /*
         <!--div class="lineDivOdd" width="100%" height="14%" >
         <vbox id="str1a" width="100%" height="100%" pack="center" align="center">  </vbox>
         </div>
         */
        Selectors.wireComponents(view, this, false);
        final int he = ((100 - 16) / getLinesCount());
        final int lhe = 100 - 16 - he * (getLinesCount() - 1);
        for (int i = 1; i <= getLinesCount(); i++) {

            final Div da = new Div();
            da.setClass("lineDiv" + (i % 2 == 1 ? "Odd" : ""));
            da.setWidth("100%");
            da.setHeight((i == getLinesCount() ? lhe : he) + "%");
            final Vbox va = new Vbox();
            va.setId("str" + i + "a");
            va.setWidth("100%");
            va.setHeight("100%");
            va.setPack("center");
            va.setAlign("center");
            da.appendChild(va);
            left.appendChild(da);

            final Div db = new Div();
            db.setClass("lineDiv" + (i % 2 == 1 ? "Odd" : ""));
            db.setWidth("100%");
            db.setHeight((i == getLinesCount() ? lhe : he) + "%");
            final Vbox vb = new Vbox();
            vb.setId("str" + i + "b");
            vb.setWidth("100%");
            vb.setHeight("100%");
            vb.setPack("center");
            vb.setAlign("center");
            db.appendChild(vb);
            right.appendChild(db);

            lines.put(i, new Str(va, vb));
        }

        Selectors.wireComponents(view, this, false);
    }
    private final LinkedHashMap<Integer, Str> lines = new LinkedHashMap<>();

    private Boolean plFlag = null;

    private boolean checkPlugin() {
        if (plFlag == null) {
            try {
                Class.forName("ru.apertum.qsystem.smartboard.PrintRecords");
            } catch (ClassNotFoundException e) {
                System.out.println("Plugin QSmartboardPlugin not found.");
                plFlag = false;
            }
            plFlag = true;
        }
        return plFlag;
    }

    private PrintRecords getPrintRecordsByOfficeId(Integer office_id) {
        if (office_id == null) {
            throw new UnsupportedOperationException("Office ID cannot be null");
        }

        Long office = Integer.toUnsignedLong(office_id);

        for (PrintRecords pr : PrintRecordsList.getInstance().getPrintRecords()) {
            if (office == pr.getOffice().getId()) {
                return pr;
            }
        }
        throw new UnsupportedOperationException("Office not found.");
    }

    private PrintRecords getPrintRecordsByOffice(QOffice office) {
        if (office == null) {
            throw new UnsupportedOperationException("Office cannot be null");
        }
        for (PrintRecords pr : PrintRecordsList.getInstance().getPrintRecords()) {
            if (office.equals(pr.getOffice())) {
                return pr;
            }
        }
        throw new UnsupportedOperationException("Office not found.");
    }

    private Integer getSessionOfficeId() {
        return Integer.parseInt((String) Sessions.getCurrent().getAttribute("office_id"));
    }

    @Command("clickMe")
    public void clickMe() {
        if (!checkPlugin()) {
            return;
        }

        Integer office_id = getSessionOfficeId();
        PrintRecords records = getPrintRecordsByOfficeId(office_id);
        if (records.isInvited()) {
            records.setInvited(false);
            Clients.evalJavaScript("DHTMLSound()");
        }

        for (int i = 1; i < lines.size(); i++) {
            final Str line = lines.get(i);
            line.clear();
            if (i <= records.getRecords().size()) {
                line.labelA = new Label(records.getRecords().get(i - 1).customerPrefix + records.getRecords().get(i - 1).customerNumber);
                line.labelB = new Label(records.getRecords().get(i - 1).point);

                final boolean blink = (records.getRecords().get(i - 1).getState() == CustomerState.STATE_INVITED
                        || records.getRecords().get(i - 1).getState() == CustomerState.STATE_INVITED);
                line.labelA.setClass(blink ? "blink_me" : "no_blink");
                line.labelB.setClass(blink ? "blink_me" : "no_blink");

                line.set();
            }
        }

    }

    public static class Str {

        final public Vbox strA;
        final public Vbox strB;
        public Label labelA;
        public Label labelB;

        public Str(Vbox strA, Vbox strB) {
            this.strA = strA;
            this.strB = strB;
        }

        public void setLabs(Label labA, Label labB) {
            labelA = labA;
            labelB = labB;
        }

        public void set() {
            strA.appendChild(labelA);
            strB.appendChild(labelB);
        }

        public void set(Label labA, Label labB) {
            setLabs(labA, labB);
            set();
        }

        public boolean isReal() {
            return strA != null && strB != null;
        }

        public void clear() {
            if (labelA != null) {
                strA.removeChild(labelA);
            }
            if (labelB != null) {
                strB.removeChild(labelB);
            }
        }
    }
    @Wire
    Columnchildren left;
    @Wire
    Columnchildren right;

    // ************************************************************************************************************************************************
    // ************************************************************************************************************************************************
    // Настройки табло
    // ************************************************************************************************************************************************
    // ************************************************************************************************************************************************
    public String getTopSize() {
       return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getTopSize() : "0px";
    }

    public boolean getTopVisible() {
        return checkPlugin() ? !"".equals(getPrintRecordsByOfficeId(getSessionOfficeId()).getTopSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getTopUrl() {
       return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getTopUrl() : "";
    }

    public String getLeftSize() {
       return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getLeftSize() : "0px";
    }

    public boolean getLeftVisible() {
        return checkPlugin() ? !"".equals(getPrintRecordsByOfficeId(getSessionOfficeId()).getLeftSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getLeftUrl() {
       return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getLeftUrl() : "";
    }

    public String getRightSize() {
       return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getRightSize() : "0px";
    }

    public String getRightVisible() {
        String rt = "background:#B0E7A0;";
        if ( checkPlugin() && "".equals(getPrintRecordsByOfficeId(getSessionOfficeId()).getRightSize().replaceAll("0|%|(px)", "") ) ) {
            rt = "display:none";
        }  
        return rt;
    }
    
    public String getSouthHeight() {
		return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getBottomSize() : "0px";
	}   

    public String getRightUrl() {
        return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getRightUrl() : "";
    }

    public String getBottomSize() {
        return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getBottomSize() : "0px";
    }

    public boolean getBottomVisible() {
        return checkPlugin() ? !"".equals(getPrintRecordsByOfficeId(getSessionOfficeId()).getBottomSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getBottomUrl() {
        return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getBottomUrl() : "";
    }

    public String getColumnFirst() {
        return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getColumnFirst() : "For clients";
    }

    public String getColumnSecond() {
        return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getColumnSecond() : "To point";
    }

    public int getLinesCount() {
        return checkPlugin() ? getPrintRecordsByOfficeId(getSessionOfficeId()).getLinesCount() : 6;
    }
    
    public String getCustomerDisplay() {
        return checkPlugin() ? getPrintRecordsByOfficeId(1).getCustomerDisplay(): "padding:0px" ;
    }   

    
    private List<QPlanService> plan = new LinkedList<>();
    public List<QPlanService> getPlan() {
        return plan;
    }

    public void setPlan(List<QPlanService> plan) {
        this.plan = plan;
    }

    public int getCustomersCount() {
        int total = 0;
        Long office_id = Integer.toUnsignedLong(getSessionOfficeId());
        QOffice office = QOfficeList.getInstance().getById(office_id);

        total = QServiceTree.getInstance().getNodes()
                .stream()
                .filter((service) -> service.getSmartboard().equals("Y") )
                .map((service) -> service.getCountCustomersByOffice(office))
                .reduce(total, Integer::sum);
              
        return total;   
    }

    
//    public int getEstimatedTime() {      
//        int estimatedTime = 0;
//        for (QUser user : QUserList.getInstance().getItems()) {  
//          if (user.getName().equalsIgnoreCase("Smartboard")) {  
//                plan = user.getPlanServiceList().getServices();  
//                
//                for (QPlanService qPlanService : plan) {  
//                    //Following number 10 min is hard code here. It will be in TODO list in the future.
//                     estimatedTime += (QServiceTree.getInstance().getById(qPlanService.getService().getId()).getCountCustomers()) * 10; 
//                }   
//           }
//        }
//        
//        return estimatedTime;
//    }
//    
    
    
    @Command
    @NotifyChange(value = {"customersCount"})
    public void refreshListServices() {
        for (QUser user : QUserList.getInstance().getItems()) {
            if (user.getName().equalsIgnoreCase("Smartboard")) {
                QSessions.getInstance().update(user.getId(), Sessions.getCurrent().getRemoteHost(), Sessions.getCurrent().getRemoteAddr().getBytes());
            }
        }
    }
}
