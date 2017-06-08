package ru.apertum.qsky.model;

import java.util.List;
import javax.naming.NamingException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.apertum.qsky.common.HibernateUtil;
import ru.apertum.qsky.controller.branch_tree.BranchTreeNode;
import ru.apertum.qsky.ejb.IHibernateEJBLocal;
import ru.apertum.qsky.web.User;

public class BranchTreeModel {

    public final static String Category = "Category";
    public final static String Contact = "Contact";

    private BranchTreeNode root;

    private IHibernateEJBLocal hib;

    public BranchTreeModel(User user) {
        
        root = null;
        
        Session ses = HibernateUtil.getSessionFactory().openSession();
        try {
            ses.beginTransaction();
            final List<Branch> list;
            if (user.getBranches().isEmpty()) {
                list = ses.createCriteria(Branch.class).add(Restrictions.isNull("parent")).addOrder(Order.asc("id")).list();
            } else {
                list = ses.createCriteria(Branch.class).add(Restrictions.isNull("parent")).add(Restrictions.in("id", user.getBranches())).addOrder(Order.asc("id")).list();
            }

            final BranchTreeNode[] brs = new BranchTreeNode[list.size()];

            for (int i = 0; i < brs.length; i++) {
                brs[i] = list.get(i).getChildren() != null ? new BranchTreeNode(list.get(i), getChildren(list.get(i))) : new BranchTreeNode(list.get(i));

            }

            root = new BranchTreeNode(null, brs);
        }
        catch (Exception e) {
            
                e.printStackTrace();
        }
        finally {
            try {
            ses.getTransaction().rollback();
            ses.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BranchTreeNode getRoot() {
        return root;
    }

    private BranchTreeNode[] getChildren(Branch parent) {
        System.out.println("---------------------------------------------------------------------------------------- " + parent.getName());
        BranchTreeNode[] brs = new BranchTreeNode[parent.getChildren().size()];

        if (parent.getChildren() == null) {
            return brs;
        } else {
            int i = 0;
            for (Branch br : parent.getChildren()) {
                brs[i++] = br.getChildren() != null ? new BranchTreeNode(br, getChildren(br)) : new BranchTreeNode(br);
            }
        }

        return brs;
    }
}
