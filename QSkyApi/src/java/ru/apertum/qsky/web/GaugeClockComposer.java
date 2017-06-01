/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import ru.apertum.qsky.model.Branch;

/**
 *
 * @author Evgeniy Egorov
 */
public class GaugeClockComposer extends SelectorComposer<Div> {
    
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
    }

    @Wire
    Label timeInBranch;
    public static final String DATE_FORMAT = "HH:mm:ss";
    /**
     * Формат даты.
     */
    public final static DateFormat format_d = new SimpleDateFormat(DATE_FORMAT);

    private long lastRead = 0;
    private int dx = 0;

    @Listen("onTimer = #timer")
    public void updateData() {
        final Date date = new Date();
        if (date.getTime() - lastRead > 3000) {
            lastRead = date.getTime();
            Branch br = (Branch) Sessions.getCurrent().getAttribute("BRANCH");
            dx = br == null ? 0 : br.getTimeZone();
        }
        date.setTime(date.getTime() + dx * 1000 * 60);
        timeInBranch.setValue(format_d.format(date));
    }

}
