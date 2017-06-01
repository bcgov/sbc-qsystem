package ru.apertum.qsky.controller.branch_tree;

import java.util.Arrays;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

import ru.apertum.qsky.model.Branch;

public class AdvancedTreeModel extends DefaultTreeModel<Branch> {

    private static final long serialVersionUID = -5513180500300189445L;

    BranchTreeNode _root;

    public AdvancedTreeModel(BranchTreeNode contactTreeNode) {
        super(contactTreeNode);
        _root = contactTreeNode;
    }

    /**
     * remove the nodes which parent is <code>parent</code> with indexes <code>indexes</code>
     *
     * @param parent The parent of nodes are removed
     * @param indexFrom the lower index of the change range
     * @param indexTo the upper index of the change range
     * @throws IndexOutOfBoundsException - indexFrom < 0 or indexTo > number of parent's children
     */
    public void remove(BranchTreeNode parent, int indexFrom, int indexTo) throws IndexOutOfBoundsException {
        BranchTreeNode stn = parent;
        for (int i = indexTo; i >= indexFrom; i--) {
            stn.getChildren().remove(i);
        }
    }

    public void remove(BranchTreeNode target) throws IndexOutOfBoundsException {
        int index;
        // find the parent and index of target
        final BranchTreeNode parent = dfSearchParent(_root, target);
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index).equals(target)) {
                break;
            }
        }
        remove(parent, index, index);
    }

    /**
     * insert new nodes which parent is <code>parent</code> with indexes <code>indexes</code> by new nodes <code>newNodes</code>
     *
     * @param parent The parent of nodes are inserted
     * @param indexFrom the lower index of the change range
     * @param indexTo the upper index of the change range
     * @param newNodes New nodes which are inserted
     * @throws IndexOutOfBoundsException - indexFrom < 0 or indexTo > number of parent's children
     */
    public void insert(DefaultTreeNode<Branch> parent, int indexFrom, int indexTo, DefaultTreeNode<Branch>[] newNodes)
            throws IndexOutOfBoundsException {
        DefaultTreeNode<Branch> stn = parent;
        for (int i = indexFrom; i <= indexTo; i++) {
            try {
                stn.getChildren().add(i, newNodes[i - indexFrom]);
            } catch (Exception exp) {
                throw new IndexOutOfBoundsException("Out of bound: " + i + " while size=" + stn.getChildren().size());
            }
        }
    }

    /**
     * append new nodes which parent is <code>parent</code> by new nodes <code>newNodes</code>
     *
     * @param parent The parent of nodes are appended
     * @param newNodes New nodes which are appended
     */
    public void add(BranchTreeNode parent, BranchTreeNode[] newNodes) {
        parent.getChildren().addAll(Arrays.asList(newNodes));
    }

    private BranchTreeNode dfSearchParent(BranchTreeNode node, BranchTreeNode target) {
        if (node.getChildren() != null && node.getChildren().contains(target)) {
            return node;
        } else {
            int size = getChildCount(node);
            for (int i = 0; i < size; i++) {
                BranchTreeNode parent = dfSearchParent((BranchTreeNode) getChild(node, i), target);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

}
