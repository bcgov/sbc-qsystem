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
package ru.apertum.qsystem.server.model.infosystem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import ru.apertum.qsystem.server.model.ITreeIdGetter;

/**
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "information")
public class QInfoItem extends DefaultMutableTreeNode implements ITreeIdGetter, Serializable {

    @Id
    @Column(name = "id")
    @Expose
    @SerializedName("id")
    //@GeneratedValue(strategy = GenerationType.AUTO) авто нельзя, т.к. id нужны для формирования дерева
    private Long id = new Date().getTime();
    /**
     * Иерархическая ссылка для построения дерева
     */
    @Column(name = "parent_id")
    private Long parentId;
    /**
     * Наименование узла справки
     */
    @Expose
    @SerializedName("name")
    @Column(name = "name")
    private String name;
    /**
     * Текст HTML
     */
    @Expose
    @SerializedName("html")
    @Column(name = "text")
    private String htmlText;
    /**
     * Текст для печати
     */
    @Expose
    @SerializedName("print")
    @Column(name = "text_print")
    private String textPrint;
    /**
     * По сути группа объединения услуг или коернь всего дерева. То во что включена данныя услуга.
     */
    @Transient
    private QInfoItem parentService;
    @Expose
    @SerializedName("child_items")
    @Transient
    private LinkedList<QInfoItem> childrenOfService = new LinkedList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long paremtId) {
        this.parentId = paremtId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getHTMLText() {
        return htmlText;
    }

    public void setHTMLText(String htmlText) {
        this.htmlText = htmlText;
    }
    //*******************************************************************************************************************
    //*******************************************************************************************************************
    //********************** Реализация методов узла в дереве ***********************************************************

    public String getTextPrint() {
        return textPrint;
    }

    public void setTextPrint(String textPrint) {
        this.textPrint = textPrint;
    }

    public LinkedList<QInfoItem> getChildren() {
        return childrenOfService;
    }

    @Override
    public QInfoItem getChildAt(int childIndex) {
        return childrenOfService.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childrenOfService.size();
    }

    @Override
    public QInfoItem getParent() {
        return parentService;
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
        parentService = (QInfoItem) newParent;
        if (parentService != null) {
            setParentId(parentService.id);
        } else {
            parentId = null;
        }
    }

    public int getIndex(QInfoItem node) {
        return childrenOfService.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(childrenOfService);
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
        child.setParent(this);
        this.childrenOfService.add(index, (QInfoItem) child);
    }

    @Override
    public void remove(int index) {
        this.childrenOfService.remove(index);
    }

    @Override
    public void remove(MutableTreeNode node) {
        this.childrenOfService.remove((QInfoItem) node);
    }

    @Override
    public void removeFromParent() {
        getParent().remove(getParent().getIndex(this));
    }

    @Override
    public int getIndex(TreeNode node) {
        return childrenOfService.indexOf(node);
    }

    @Override
    public void addChild(ITreeIdGetter child) {
        childrenOfService.add((QInfoItem) child);
    }
}
