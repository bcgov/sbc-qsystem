/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.web;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.naming.NamingException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.TreeDataEvent;
import ru.apertum.qsky.common.HibernateUtil;
import ru.apertum.qsky.common.Multilingual;
import ru.apertum.qsky.controller.branch_tree.AdvancedTreeModel;
import ru.apertum.qsky.controller.branch_tree.BranchTreeNode;
import ru.apertum.qsky.ejb.IHibernateEJBLocal;
import ru.apertum.qsky.model.Branch;
import ru.apertum.qsky.model.BranchTreeModel;
import ru.apertum.qsky.model.Customer;
import ru.apertum.qsky.model.Dicts;
import ru.apertum.qsky.model.StatisticViewModel;
import ru.apertum.qsky.model.Step;

/**
 *
 * @author Evgeniy Egorov
 */
public class Dashboard {

    public String l(String resName) {
        return Labels.getLabel(resName);
    }

    @Init
    public void init() {

    }

    @Command
    public void about() {
        final Properties settings = new Properties();
        final InputStream inStream = this.getClass().getResourceAsStream("/qskyapi.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new RuntimeException("Cant read version. " + ex);
        }
        Messagebox.show("***  QSkyAPI  ***\n"
                + "   version " + settings.getProperty("version")
                + "\n   date " + settings.getProperty("date")
                + "\n   for QSkySenderPlugin version=" + settings.getProperty("support_clients"),
                "QMS Apertum-QSystem", Messagebox.OK, Messagebox.INFORMATION);
    }

    private User user;

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        final org.zkoss.zk.ui.Session sess = Sessions.getCurrent();
        user = (User) sess.getAttribute("USER");

        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        doAfterCompose(null);
    }

    //*****************************************************
    //**** Multilingual
    //*****************************************************
    public ArrayList<Multilingual.Lng> getLangs() {
        return Multilingual.LANGS;
    }

    private Multilingual.Lng lang = new Multilingual().init();

    public Multilingual.Lng getLang() {
        return lang;
    }

    public void setLang(Multilingual.Lng lang) {
        this.lang = lang;
    }

    @Command("changeLang")
    public void changeLang() {
        if (lang != null) {
            final org.zkoss.zk.ui.Session session = Sessions.getCurrent();
            final Locale prefer_locale = lang.code.length() > 2
                    ? new Locale(lang.code.substring(0, 2), lang.code.substring(3)) : new Locale(lang.code);
            session.setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE, prefer_locale);
            Executions.sendRedirect(null);
        }
    }
    //**** Multilingual
    ////////////**************************************************
    ////////////**************************************************
    ////////////**************************************************

    private static final long serialVersionUID = 3814570327995355261L;

    @Wire
    private Window dashboardWindow;
    @Wire
    private Tree tree;

    // *** Диалоги изменения состояния
    @Wire("#incBranchPropsDialog #branchPropsDialog")
    Window branchPropsDialog;

    private AdvancedTreeModel contactTreeModel;

    //  @Override
    public void doAfterCompose(Component comp)/* throws Exception*/ {
        // super.doAfterCompose(comp);
        contactTreeModel = new AdvancedTreeModel(new BranchTreeModel(user).getRoot());

        tree.setItemRenderer(new ContactTreeRenderer());
        tree.setModel(contactTreeModel);
    }

    private IHibernateEJBLocal hib;

    public IHibernateEJBLocal getHib() {
        try {
            if (hib == null) {
                hib = (IHibernateEJBLocal) ((new javax.naming.InitialContext()).lookup("java:comp/env/" + "qskyapi/HibernateEJB"));
            }
        } catch (NamingException ex) {
            throw new RuntimeException("No EJB Hib factory! " + ex);
        }
        return hib;
    }

    /**
     * The structure of tree
     *
     * <pre>
     * &lt;treeitem>
     *   &lt;treerow>
     *     &lt;treecell>...&lt;/treecell>
     *   &lt;/treerow>
     *   &lt;treechildren>
     *     &lt;treeitem>...&lt;/treeitem>
     *   &lt;/treechildren>
     * &lt;/treeitem>
     * </pre>
     */
    private final class ContactTreeRenderer implements TreeitemRenderer<BranchTreeNode> {

        @Override
        public void render(final Treeitem treeItem, BranchTreeNode treeNode, int index) throws Exception {
            final BranchTreeNode ctn = treeNode;
            final Branch branch = (Branch) ctn.getData();
            final Treerow dataRow = new Treerow();
            dataRow.setParent(treeItem);
            treeItem.setValue(ctn);
            treeItem.setOpen(ctn.isOpen());

            final Hlayout hl = new Hlayout();
            hl.appendChild(new Image("/resources/img/label32.png"));
            hl.appendChild(new Label(branch.getName()));
            hl.setSclass("h-inline-block");
            final Treecell treeCell = new Treecell();
            treeCell.appendChild(hl);
            dataRow.setDraggable("true");
            dataRow.appendChild(treeCell);
            dataRow.appendChild(new Treecell("" + branch.getBranchId() + " " + (branch.getActive() ? "Active" : "Disable")));
            dataRow.addEventListener(Events.ON_DOUBLE_CLICK, (Event event) -> {
                final BranchTreeNode clickedNodeValue = (BranchTreeNode) ((Treeitem) event.getTarget().getParent()).getValue();
                final Branch br = clickedNodeValue.getData();
                final Window w = branchPropsDialog;
                w.setTitle(br.toString());
                ((Textbox) w.getFellow("nameBranch")).setText(br.getName());
                ((Intbox) w.getFellow("idBranch")).setValue(br.getBranchId().intValue());
                ((Intbox) w.getFellow("timeZone")).setValue(br.getTimeZone());
                w.setVisible(true);
                w.setPosition("center");
                w.doModal();
            });

            // Both category row and contact row can be item dropped
            dataRow.setDroppable("true");
            dataRow.addEventListener(Events.ON_DROP, (Event event) -> {
                // The dragged target is a TreeRow belongs to an
                // Treechildren of TreeItem.
                final Treeitem draggedItem = (Treeitem) ((DropEvent) event).getDragged().getParent();
                final BranchTreeNode draggedValue = (BranchTreeNode) draggedItem.getValue();
                //System.out.println("--------------------------START draggedValue " + draggedValue.getData().getId() + " to " + ((BranchTreeNode) treeItem.getValue()).getData().getId());

                // разрешение переносить корневые ветки
                if (draggedValue.getData().getParent() == null && !user.getBranches().isEmpty()) {
                    return;
                }
                if (!draggedValue.isParentOf((BranchTreeNode) treeItem.getValue())) {// не в свою ветку

                    if (draggedValue.getData().getParent() == null
                            || !draggedValue.getData().getParent().getId().equals(((BranchTreeNode) treeItem.getValue()).getData().getId())) {// не в свой уровень

                        contactTreeModel.remove(draggedValue);
                        contactTreeModel.add((BranchTreeNode) treeItem.getValue(), new BranchTreeNode[]{draggedValue});

                        final boolean remove = draggedValue.getData().getParent() == null ? true : draggedValue.getData().getParent().getChildren().remove(draggedValue.getData());
                        if (!remove) {
                            //System.out.println("--------------- "+draggedValue.getData().getParent() + " -- " + draggedValue.getData() + " - " + draggedValue.getData().getParent()..getChildren().);
                            throw new RuntimeException("Not delete the child ");
                        }
                        draggedValue.getData().setParent(((BranchTreeNode) treeItem.getValue()).getData());
                        ((BranchTreeNode) treeItem.getValue()).getData().getChildren().add(draggedValue.getData());

                        final Session ses = HibernateUtil.getSessionFactory().openSession();
                        try {
                            ses.beginTransaction();
                            ses.saveOrUpdate(((BranchTreeNode) treeItem.getValue()).getData());
                            ses.saveOrUpdate(draggedValue.getData());
                            ses.getTransaction().commit();
                        } catch (Exception ex) {
                            ses.getTransaction().rollback();
                            throw new RuntimeException("Not updated the tree ");
                        } finally {
                            ses.close();
                        }

                    }
                }
            });

        }

    }

    @Command
    public void addBranch() {
        if (tree.getSelectedItem() == null && !user.getBranches().isEmpty()) {
            return;
        }
        final long l = new Date().getTime() % 10000;
        Branch br = new Branch("New branch " + l);
        br.setBranchId(l);

        if (user.getBranches().isEmpty()) {
            ((BranchTreeNode) tree.getModel().getRoot()).insert(new BranchTreeNode(br), 0);
        } else {
            br.setParent(((BranchTreeNode) tree.getSelectedItem().getValue()).getData());
            ((BranchTreeNode) tree.getSelectedItem().getValue()).getData().getChildren().add(br);
            final BranchTreeNode node = new BranchTreeNode(br);

            ((BranchTreeNode) tree.getSelectedItem().getValue()).add(node);
        }

        final Session ses = HibernateUtil.getSessionFactory().openSession();
        try {
            ses.beginTransaction();
            ses.save(br);
            ses.getTransaction().commit();
        } catch (Exception ex) {
            ses.getTransaction().rollback();
            throw new RuntimeException("Not created the new branch " + ex);
        } finally {
            ses.close();
        }

    }

    @Command
    public void removeBranch() {
        if (tree.getSelectedItem() != null) {
            if (((BranchTreeNode) tree.getSelectedItem().getValue()).getParent().getData() == null && !user.getBranches().isEmpty()) {
                return;
            }
            if (((BranchTreeNode) tree.getSelectedItem().getValue()).getChildCount() != 0) {
                Messagebox.show(l("mess_del_branch_not_empty_tree"), l("del_impossible"), Messagebox.OK, Messagebox.ERROR);
                return;
            }
            final Branch br = ((BranchTreeNode) tree.getSelectedItem().getValue()).getData();
            Messagebox.show(l("shure_del_branch") + " \"" + br + "\"?", l("removing"), Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, (Event evt) -> {
                if (evt.getName().equals("onOK")) {

                    if (br.getParent() != null && !br.getParent().getChildren().remove(br)) {
                        throw new RuntimeException("Not delete the child ");
                    }
                    final Session ses = HibernateUtil.getSessionFactory().openSession();
                    try {
                        ses.beginTransaction();
                        ses.delete(br);
                        ses.getTransaction().commit();
                    } catch (Exception ex) {
                        ses.getTransaction().rollback();
                        throw new RuntimeException("Not deleted the branch " + ex);
                    } finally {
                        ses.close();
                    }
                    ((BranchTreeNode) tree.getSelectedItem().getValue()).getParent().remove(((BranchTreeNode) tree.getSelectedItem().getValue()));
                }
            });
        }
    }

    @Command
    public void closeBranchPropsDialog() {
        if (tree.getSelectedItem() != null) {
            BranchTreeNode br_node = (BranchTreeNode) tree.getSelectedItem().getValue();
            Branch br = ((BranchTreeNode) tree.getSelectedItem().getValue()).getData();

            br.setName(((Textbox) branchPropsDialog.getFellow("nameBranch")).getText());
            br.setBranchId(((Intbox) branchPropsDialog.getFellow("idBranch")).getValue().longValue());
            br.setBranchId(((Intbox) branchPropsDialog.getFellow("idBranch")).getValue().longValue());
            br.setTimeZone(((Intbox) branchPropsDialog.getFellow("timeZone")).getValue());

            final Session ses = HibernateUtil.getSessionFactory().openSession();
            try {
                ses.beginTransaction();
                ses.saveOrUpdate(br);
                ses.getTransaction().commit();
            } catch (Exception ex) {
                ses.getTransaction().rollback();
                throw new RuntimeException("Not saved the branch " + ex);
            } finally {
                ses.close();
            }

            final int[] ii1 = contactTreeModel.getPath(br_node);
            final int[] ii2 = (new int[ii1.length - 1]);
            System.arraycopy(ii1, 0, ii2, 0, ii2.length);
            contactTreeModel.fireEvent(TreeDataEvent.CONTENTS_CHANGED, ii2, tree.getSelectedItem().getIndex(), tree.getSelectedItem().getIndex());
            branchPropsDialog.setVisible(false);
        }
    }

    @Wire
    private Label branchName;

    @Wire
    private Panel statPapamPanel;
    private Branch selectedBranch = null;

    @Listen("onSelect = #tree")
    @NotifyChange(value = {"branchName", "statPapamPanel"})
    public void selectBranch() {
        BranchTreeNode selectedNode = (BranchTreeNode) tree.getSelectedItem().getValue();
        System.out.println("SELECTED " + selectedNode);
        Sessions.getCurrent().setAttribute("BRANCH", selectedNode.getData());
        selectedBranch = selectedNode.getData();
        branchName.setValue(selectedNode.getData().getName());
        statPapamPanel.setTitle(selectedNode.getData().getName());
    }

    public static class Pair implements Comparable<Pair> {

        Long l;
        Integer i;
        String st;

        public Long getL() {
            return l;
        }

        public void setL(Long l) {
            this.l = l;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public String getSt() {
            return st;
        }

        public void setSt(String st) {
            this.st = st;
        }

        public Pair(Long l, Integer i) {
            this.l = l;
            this.i = i;
        }

        public Pair(String st, Integer i) {
            this.st = st;
            this.i = i;
        }

        @Override
        public int compareTo(Pair o) {
            return i.compareTo(o.i);
        }

    }

    @Command("showBranchSituation")
    @NotifyChange(value = {"pieChart", "dialChart", "servicesCustList"})
    public synchronized void showBranchSituation() {
        if (selectedBranch == null) {
            return;
        }
        final Session ses = HibernateUtil.getSessionFactory().openSession();
        final List<Customer> custs;
        try {
            ses.beginTransaction();
            final GregorianCalendar day = new GregorianCalendar();
            day.set(GregorianCalendar.HOUR_OF_DAY, 0);
            day.set(GregorianCalendar.MINUTE, 0);
            final Date today_m = day.getTime();
            day.set(GregorianCalendar.HOUR_OF_DAY, 23);
            day.set(GregorianCalendar.MINUTE, 59);

            custs = ses.createCriteria(Customer.class)
                    .add(Restrictions.eq("branchId", selectedBranch.getBranchId()))
                    .add(Restrictions.between("visitTime", today_m, day.getTime())).list();
        } catch (Exception ex) {
            throw new RuntimeException("Not loaded a list of customers. " + ex);
        } finally {
            ses.getTransaction().rollback();
            ses.close();
        }

        final Predicate<? super Customer> filter = (cust) -> {
            return cust.getState() != 0 && cust.getState() != 10;
        };

        final HashMap<Long, Integer> cnt = new HashMap<>();
        Stream<Customer> scu = custs.stream().filter(filter);

        final long clntsCnt = custs.stream().filter(filter).count();
        scu.forEach((cust) -> {
            Integer serv = cnt.get(cust.getServiceId());
            if (serv != null) {
                cnt.put(cust.getServiceId(), ++serv);
            } else {
                cnt.put(cust.getServiceId(), 1);
            }
        });
        scu = custs.stream().filter(filter);

        final Optional<Customer> mw = scu.max((cust1, cust2) -> {
            return (cust1.getWaiting() == 0 ? (new Long(new Date().getTime() - cust1.getVisitTime().getTime())) : cust1.getWaiting()).compareTo(cust2.getWaiting() == 0 ? (new Date().getTime() - cust2.getVisitTime().getTime()) : cust2.getWaiting());
        });
        long maxWaiting = mw.isPresent() ? (mw.get().getWaiting() == 0 ? (new Date().getTime() - mw.get().getVisitTime().getTime()) : mw.get().getWaiting()) : 0;

        int i = 0;
        long avg = 0;
        for (Customer cust : custs) {
            if (cust.getState() != 0 && cust.getWaiting() != null) {
                avg = avg + (cust.getWaiting() == 0 ? (new Date().getTime() - cust.getVisitTime().getTime()) : cust.getWaiting());
                i++;
            }
        }

        final List<Pair> ls = new ArrayList<>();
        cnt.keySet().stream().forEach((l) -> {
            ls.add(new Pair(l, cnt.get(l)));
        });
        final PieModel model = new SimplePieModel();
        ls.sort((Pair o1, Pair o2) -> {
            return Integer.compare(o2.i, o1.i);
        });
        ls.stream().limit(10).forEach((Pair p) -> {
            final String name = Dicts.getInstance().getServiceName(selectedBranch.getBranchId(), p.l);
            model.setValue((name.length() > 30 ? name.substring(0, 30) + "..." : name) + "(" + p.i + ")", p.i);
        });
        servicesCustList.clear();
        ls.stream().forEach((Pair p) -> {
            final String name = Dicts.getInstance().getServiceName(selectedBranch.getBranchId(), p.l);
            servicesCustList.add(new Pair(name, p.i));
        });
        pieChart.setModel(model);

        dialChart.averageModel.setValue(0, i == 0 ? 0 : avg / i / 1000 / 60);
        dialChart.customersModel.setValue(0, clntsCnt);
        dialChart.waitingModel.setValue(0, maxWaiting < 1000 * 60 ? (clntsCnt == 0 ? 0 : 1) : maxWaiting / 1000 / 60);
    }

    private final ArrayList<Pair> servicesCustList = new ArrayList();

    public ArrayList<Pair> getServicesCustList() {
        return servicesCustList;
    }

    private final PieChartVM pieChart = new PieChartVM();

    public PieChartVM getPieChart() {
        return pieChart;
    }

    private final DialChartVM dialChart = new DialChartVM();

    public DialChartVM getDialChart() {
        return dialChart;
    }

    private final StatisticViewModel statVM = new StatisticViewModel();

    public StatisticViewModel getStatVM() {
        return statVM;
    }

    @Wire
    private Grid statisticGrid;

    @Wire
    private Footer footer_category;

    @Command("showBranchStatistic")
    @NotifyChange(value = {"statVM", "statisticGrid", "footer_category"})
    public synchronized void showBranchStatistic() {
        if (selectedBranch == null) {
            return;
        }
        final List<Step> stst;
        final Session ses = HibernateUtil.getSessionFactory().openSession();
        try {
            stst = statVM.loadStats(selectedBranch.getBranchId(), ses);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ses.close();
        }
        GroupsModelArray mo = statVM.getRegim() == 0
                ? new StatisticViewModel.StaticticGroupingEmplsModel(statVM.prepareDataByUser(stst), new StatisticViewModel.RecordEmployeeComparator())
                : new StatisticViewModel.StaticticGroupingServsModel(statVM.prepareDataByUser(stst), new StatisticViewModel.RecordServiceComparator());
        statVM.setStatisticModel(mo);
        statisticGrid.setModel(mo);
        footer_category.setLabel((statVM.getRegim() == 0 ? l("amount_users") + " : " : l("amount_services") + " : ") + mo.getGroupCount());
    }

    @Command("downloadBranchStatistic")
    public synchronized void downloadBranchStatistic() {
        if (selectedBranch == null) {
            return;
        }
        final Session ses = getHib().openSession();
        final List<Customer> custs;
        try {
            ses.beginTransaction();
            final GregorianCalendar day = new GregorianCalendar();
            day.setTime(statVM.getStart());
            day.set(GregorianCalendar.HOUR_OF_DAY, 0);
            day.set(GregorianCalendar.MINUTE, 0);
            final Date today_m = day.getTime();
            day.setTime(statVM.getFinish());
            day.set(GregorianCalendar.HOUR_OF_DAY, 23);
            day.set(GregorianCalendar.MINUTE, 59);

            custs = ses.createCriteria(Customer.class)
                    .add(Restrictions.eq("branchId", selectedBranch.getBranchId()))
                    .add(Restrictions.between("visitTime", today_m, day.getTime())).list();
        } catch (Exception ex) {
            throw new RuntimeException("Not loaded a list of customers. " + ex);
        } finally {
            ses.getTransaction().rollback();
            ses.close();
        }
        final StringBuffer sb;
        sb = new StringBuffer(l("captions_csv") + "\n");
        int nom = 0;
        for (Customer cust : custs) {
            Step step = cust.getFirstStep();
            while (step != null) {
                sb.append(++nom).append(";");
                sb.append(selectedBranch.getName()).append(";");
                sb.append(Dicts.getInstance().getServiceName(selectedBranch.getBranchId(), step.getServiceId())).append(";");
                sb.append(Dicts.getInstance().getEmployeeName(selectedBranch.getBranchId(), step.getEmployeeId())).append(";");
                sb.append(cust.getPrefix()).append(cust.getNumber()).append(";");
                sb.append(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(step.getStandTime())).append(";");
                if (step.getFinishState() == null || step.getFinishState() == 0) {
                    sb.append(";;;;");
                } else {
                    sb.append(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(step.getStartTime())).append(";");
                    sb.append(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(step.getFinishTime())).append(";");
                    sb.append(step.getWaiting() / 1000 / 60).append(";");
                    sb.append(step.getWorking() / 1000 / 60).append(";");
                }
                sb.append(step.getStartState()).append(";");
                sb.append(step.getFinishState() == null ? "" : step.getFinishState()).append(";");
                step = step.getAfter();
                sb.append("\n");
            }
        }
        Filedownload.save(sb.toString().getBytes(), "text/csv", "qstat_" + SimpleDateFormat.getDateInstance().format(statVM.getStart()) + "-" + SimpleDateFormat.getDateInstance().format(statVM.getFinish()) + ".csv");
        sb.setLength(0);
    }

}
