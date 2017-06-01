package ru.apertum.qsky.controller.branch_tree;

import org.zkoss.zul.DefaultTreeNode;

import ru.apertum.qsky.model.Branch;

public class BranchTreeNode extends DefaultTreeNode<Branch> {

    private static final long serialVersionUID = -7012663776755277499L;

    private boolean open = true;

    public BranchTreeNode(Branch data, BranchTreeNode[] children) {
        super(data, children);
    }

    public BranchTreeNode(Branch data, BranchTreeNode[] children, boolean open) {
        super(data, children);
        setOpen(open);
    }

    public BranchTreeNode(Branch data) {
        super(data, new BranchTreeNode[0]);

    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
    
    public boolean isParentOf(BranchTreeNode br) {
        return getData().isParentOf(br.getData());
    }

}
