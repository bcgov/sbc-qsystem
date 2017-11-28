/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsys.quser;

import org.zkoss.zul.AbstractTreeModel;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;

/**
 * @author Evgeniy Egorov
 */
public class TreeServices extends AbstractTreeModel<QService> {

    public TreeServices() {
        super(QServiceTree.getInstance().getRoot());
    }

    @Override
    public boolean isLeaf(QService e) {
        return e.isLeaf();
    }

    @Override
    public QService getChild(QService e, int i) {
        return e.getChildAt(i);
    }

    @Override
    public int getChildCount(QService e) {
        return e.getChildCount();
    }

}
