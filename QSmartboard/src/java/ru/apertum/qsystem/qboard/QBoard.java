/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.qboard;

import java.util.LinkedHashMap;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Columnchildren;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.smartboard.PrintRecords;

/**
 *
 * @author Evgeniy Egorov
 */
public class QBoard {

    /**
     * Это нужно чтоб делать include во view и потом связывать @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
     *
     * @param view
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {

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

    @Command("clickMe")
    public void clickMe() {
        if (!checkPlugin()) {
            return;
        }
        if (PrintRecords.getInstance().isInvited()) {
            PrintRecords.getInstance().setInvited(false);
            Clients.evalJavaScript("DHTMLSound()");
        }

        //System.out.println("recs.length=" + PrintRecords.getInstance().getRecords().size() + " / " + PrintRecords.getInstance().getRecords().toString());
        for (int i = 1; i < lines.size(); i++) {
            final Str line = lines.get(i);
            line.clear();
            if (i <= PrintRecords.getInstance().getRecords().size()) {
                line.labelA = new Label(PrintRecords.getInstance().getRecords().get(i - 1).customerPrefix + PrintRecords.getInstance().getRecords().get(i - 1).customerNumber);
                line.labelB = new Label(PrintRecords.getInstance().getRecords().get(i - 1).point);

                final boolean blink = (PrintRecords.getInstance().getRecords().get(i - 1).getState() == CustomerState.STATE_INVITED
                        || PrintRecords.getInstance().getRecords().get(i - 1).getState() == CustomerState.STATE_INVITED);
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
        return checkPlugin() ? PrintRecords.getInstance().getTopSize() : "0px";
    }

    public boolean getTopVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getTopSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getTopUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getTopUrl() : "";
    }

    public String getLeftSize() {
        return checkPlugin() ? PrintRecords.getInstance().getLeftSize() : "0px";
    }

    public boolean getLeftVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getLeftSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getLeftUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getLeftUrl() : "";
    }

    public String getRightSize() {
        return checkPlugin() ? PrintRecords.getInstance().getRightSize() : "0px";
    }

    public boolean getRightVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getRightSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getRightUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getRightUrl() : "";
    }

    public String getBottomSize() {
        return checkPlugin() ? PrintRecords.getInstance().getBottomSize() : "0px";
    }

    public boolean getBottomVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getBottomSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getBottomUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getBottomUrl() : "";
    }

    public String getColumnFirst() {
        return checkPlugin() ? PrintRecords.getInstance().getColumnFirst() : "For clients";
    }

    public String getColumnSecond() {
        return checkPlugin() ? PrintRecords.getInstance().getColumnSecond() : "To point";
    }

    public int getLinesCount() {
        return checkPlugin() ? PrintRecords.getInstance().getLinesCount() : 6;
    }

}
