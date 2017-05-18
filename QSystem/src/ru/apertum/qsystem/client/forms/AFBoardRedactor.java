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
package ru.apertum.qsystem.client.forms;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.model.INetProperty;
import ru.apertum.qsystem.common.NetCommander;

/**
 * Created on 16 Апрель 2009 г., 20:25
 * Абстрактный предок редакторов параметров для табло.
 * Хранит XML-конфигурацию табло и умеет её сохранить.
 * Имеет главное меню с основными пунктами сохранения и закрытия редактора.
 * Для обновления состояния редактора необходимо реализовать метод refresh();
 * @author Evgeniy Egorov
 */
abstract public class AFBoardRedactor extends javax.swing.JDialog {

    protected INetProperty netProperty;
    private Element params;
    protected JFrame parent;
    protected boolean modal;

    public Element getParams() {
        return params;
    }

    /** Creates new form AFBoardRedactor
     * @param parent
     * @param modal
     */
    public AFBoardRedactor(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parent = parent;
        this.modal = modal;
        setLocation();
    }

    private void setLocation() {
        Uses.setLocation(this);
    }
    private static ResourceMap localeMap = null;

    private static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext().getResourceMap(AFBoardRedactor.class);
        }
        return localeMap.getString(key);
    }

    /**
     * Это метод сохранения результатов редактирования параметров табло в форме редактора.
     * измененная XML-конфигурация табло
     * @throws IOException
     */
    public void saveResult() throws IOException {
        NetCommander.saveBoardConfig(netProperty, params);
        JOptionPane.showMessageDialog(this, getLocaleMessage("dialog.message.caption"), getLocaleMessage("dialog.message.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Метод передачи данных в редактор для изменения
     * @param netProperty параметры сети для получения с сервера конфигурации
     * @throws DocumentException
     */
    public void setParams(INetProperty netProperty) throws DocumentException {
        this.netProperty = netProperty;
        this.params = NetCommander.getBoardConfig(netProperty);
        refresh();
        onChangeParams();
    }

    /**
     * Метод передачи данных в редактор для ихменения
     * @param params сами параметры в формате XML
     */
    public void setParams(Element params) {
        this.params = params;
        refresh();
        onChangeParams();
    }

    /**
     * Событие изменения параметров
     */
    protected void onChangeParams() {
    }

    /**
     * Обновить конфигурацию на форме.
     */
    abstract protected void refresh();

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainMenu = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();

        setName("Form"); // NOI18N

        mainMenu.setName("mainMenu"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getResourceMap(AFBoardRedactor.class);
        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ru.apertum.qsystem.QSystem.class).getContext().getActionMap(AFBoardRedactor.class, this);
        jMenuItem1.setAction(actionMap.get("saveBoardConfig")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenu1.add(jMenuItem1);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu1.add(jSeparator1);

        jMenuItem2.setAction(actionMap.get("hideRedactor")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu1.add(jMenuItem2);

        mainMenu.add(jMenu1);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void saveBoardConfig() throws IOException {
        saveResult();
    }

    @Action
    public void hideRedactor() {
        setVisible(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenu jMenu1;
    protected javax.swing.JMenuItem jMenuItem1;
    protected javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JSeparator jSeparator1;
    protected javax.swing.JMenuBar mainMenu;
    // End of variables declaration//GEN-END:variables
}
