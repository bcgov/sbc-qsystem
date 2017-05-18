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
package ru.apertum.qsystem.server.model;

import java.util.LinkedList;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.controller.ServerEvents;

/**
 *
 * @param <T>
 * @author Evgeniy Egorov
 */
public abstract class ATreeModel<T extends ITreeIdGetter> extends DefaultTreeModel {

    protected ATreeModel() {
        super(null);
        createTree();
        ServerEvents.getInstance().registerListener(() -> {
            createTree();
        });
    }

    protected abstract LinkedList<T> load();

    protected final void createTree() {
        final LinkedList<T> nodes = load();
        for (T node : nodes) {
            if (node.getParentId() == null) {
                setRoot(node);
                break;
            }
        }
        bildTree(getRoot(), nodes);
        QLog.l().logger().info("Создали дерево.");
    }

    private void bildTree(T root, LinkedList<T> nodes) {
        nodes.stream().filter((node) -> (root.getId().equals(node.getParentId()))).map((node) -> {
            node.setParent(root);
            return node;
        }).map((node) -> {
            root.addChild(node);
            return node;
        }).forEach((node) -> {
            bildTree(node, nodes);
        });
    }

    public LinkedList<T> getNodes() {
        final LinkedList<T> nodes = new LinkedList<>();
        sailToStorm(root, (TreeNode service) -> {
            nodes.add((T) service);
        });
        return nodes;
    }

    /**
     * Получить услугу по ID
     *
     * @param id
     * @return если не найдено то вернет null.
     */
    public T getById(long id) {
        for (T node : getNodes()) {
            if (id == node.getId()) {
                return node;
            }
        }
        throw new ServerException("Не найдена услуга по ID \"" + id + "\"");
    }

    /**
     * Проверка наличия услуги по id
     *
     * @param id имя проверяемой услуги
     * @return есть или нет
     */
    public boolean hasById(long id) {
        return getNodes().stream().anyMatch((node) -> (id == node.getId()));
    }

    /**
     * Проверка наличия услуги по id
     *
     * @param name имя проверяемой услуги
     * @return есть или нет
     */
    public boolean hasByName(String name) {
        return getNodes().stream().anyMatch((node) -> (name.equals(node.getName())));
    }

    public int size() {
        return getNodes().size();
    }

    /**
     * Перебор всех услуг до одной включая корень и узлы
     *
     * @param root
     * @param listener
     */
    public static void sailToStorm(TreeNode root, ISailListener listener) {
        seil(root, listener);
    }

    private static void seil(TreeNode parent, ISailListener listener) {
        listener.actionPerformed(parent);
        for (int i = 0; i < parent.getChildCount(); i++) {
            seil(parent.getChildAt(i), listener);
        }
    }

    @Override
    public T getRoot() {
        return (T) super.getRoot();
    }

    @Override
    public T getChild(Object parent, int index) {
        return (T) ((TreeNode) parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((TreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((TreeNode) node).isLeaf();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeNode) parent).getIndex((TreeNode) child);
    }

    @Override
    public void removeNodeFromParent(MutableTreeNode node) {
        sailToStorm(node, (TreeNode node1) -> {
            deleted.add((T) node1);
        });
        deleted.add((T) node);

        super.removeNodeFromParent(node);
        updateSeqSibling((QService) node.getParent());
    }
    protected final LinkedList<T> deleted = new LinkedList<>();

    @Override
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index) {
        super.insertNodeInto(newChild, parent, index);
        if (parent instanceof QService) { // это подпорка, неожиданно возникла при реализации сортировки
            updateSeqSibling((QService) parent);
        }
    }

    /**
     * Это подпорка, неожиданно возникла при реализации сортировки
     *
     * @param moveChild что двигаем
     * @param parent куда двигаем
     * @param index каким дочерним вставляем
     */
    public void moveNode(MutableTreeNode moveChild, MutableTreeNode parent, int index) {
        if (((QService) moveChild.getParent()).getId().equals(((QService) parent).getId())) {
            final int pos = parent.getIndex(moveChild);
            super.removeNodeFromParent(moveChild);
            super.insertNodeInto(moveChild, parent, index - (pos < index ? 1 : 0));
            updateSeqSibling((QService) parent);
        } else {
            final QService prt = (QService) moveChild.getParent();
            final int pos = prt.getIndex(moveChild);
            prt.remove(moveChild);

            nodesWereRemoved(prt, new int[]{pos}, new Object[]{moveChild});
            insertNodeInto(moveChild, parent, index - (pos < index ? 1 : 0));
            updateSeqSibling((QService) parent);
            updateSeqSibling(prt);
        }
    }

    /**
     * Это подпорка, неожиданно возникла при реализации сортировки
     *
     * @param parent парент для расставления последовательности у его дочерних
     */
    public void updateSeqSibling(QService parent) { // это подпорка, неожиданно возникла при реализации сортировки
        QService sib = (QService) ((QService) parent).getFirstChild();
        while (sib != null) {
            sib.setSeqId(parent.getIndex(sib));
            sib = (QService) sib.getNextSibling();
        }
    }

    public void save() {
        // Вложенные нужно убрать. т.к. они сотрутся по констрейнту
        final LinkedList<T> del = new LinkedList<>();
        deleted.stream().forEach((t) -> {
            boolean flag = false;
            T parent = (T) t.getParent();
            while (parent != null && !flag) {
                for (T t2 : deleted) {
                    if (t2.getId().equals(parent.getId())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    parent = (T) parent.getParent();
                }
            }
            if (flag) {
                del.add(t);
            }
        });
        deleted.removeAll(del);
        Spring.getInstance().getHt().deleteAll(deleted);
        deleted.clear();
        Spring.getInstance().getHt().saveOrUpdateAll(getNodes());
    }
}
