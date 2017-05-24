/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.apertum.qsys.quser.forms;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

/**
 *
 * @author Evgeniy Egorov
 */
public class RedirectDialogController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
     
    @Wire
    Window redirectDialog;
     
    @Listen("onClick = #closeBtn")
    public void closeModal(Event e) {
        redirectDialog.detach();
    }
    @Listen("onClick = #applyBtn")
    public void okModal(Event e) {
        redirectDialog.detach();
    }
}
