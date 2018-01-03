package ru.apertum.qsys.quser;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.zkoss.util.resource.Labels;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.model.QCustomer;
import ru.apertum.qsystem.server.model.QOffice;
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QService;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

public class User {

    private String name = "";
    private String password = "";
    private QUser user;
    private List<QPlanService> plan = new LinkedList<>();
    private LinkedList<QCustomer> customerList = new LinkedList<>();
    private Date customerWelcomeTime;
    private boolean GABoard = false;

    public String l(String resName) {
        return Labels.getLabel(resName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;

        for (QUser us : QUserList.getInstance().getItems()) {
            if (us.getName().equalsIgnoreCase(name)) {
                user = us;
                plan = user.getPlanServiceList().getServices();
                setCustomerList(user.getPlanServiceList().getServices());
                customerList = getCustomerList();
                return;
            }
        }
    }

    public Date getCustomerWelcomeTime() {
        return customerWelcomeTime;
    }

    public void setCustomerWelcomeTime(Date customerWelcomeTime) {
        this.customerWelcomeTime = customerWelcomeTime;
    }

    public boolean getGABoard() {
        return GABoard;
    }

    public void setGABoard(boolean ga) {
        this.GABoard = ga;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public QUser getUser() {
        return user;
    }

    public void setUser(QUser user) {
        this.user = user;
    }

    public List<QPlanService> getPlan() {
        return plan;
    }

    public void setPlan(List<QPlanService> plan) {
        this.plan = plan;
    }

    public boolean checkIfUserCanServe(QService service) {
        return this.getPlan().stream()
            .filter(planService -> planService.getService().getId().equals(service.getId()))
            .findAny()
            .isPresent();
    }

    private LinkedList<QCustomer> filterCustomerList(LinkedList<QCustomer> customers) {
        QOffice userOffice = this.user.getOffice();

        if (userOffice != null) {
            customers = customers
                .stream()
                .filter((QCustomer c) -> (c.getUser() == null ||
                    c.getUser().getOffice().equals(userOffice)))
                .collect(Collectors.toCollection(LinkedList::new));
        }

        return customers;
    }

    public LinkedList<QCustomer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<QPlanService> planServices) {

        QOffice userOffice = this.getUser().getOffice();
        while (!customerList.isEmpty()) {
            customerList.removeFirst();
        }
        planServices.forEach((QPlanService p) -> {
            QService ser = QServiceTree.getInstance().getById(p.getService().getId());
            ser.getClients().stream().forEach((c) -> {
                if (c.getOffice().equals(userOffice)) {
                    customerList.add(c);
                }
            });
        });
    }

    public int getLineSize(long serviceId) {
        QUser u = this.getUser();

        if (u != null) {
            return QServiceTree.getInstance()
                .getById(serviceId)
                .getCountCustomersByOffice(u.getOffice());
        } else {
            return QServiceTree.getInstance()
                .getById(serviceId)
                .getCountCustomers();
        }
    }

    public int getTotalLineSize() {
        int total = 0;
        QUser u = this.getUser();

        if (u != null) {
            total = plan.stream()
                .map((plan1) -> QServiceTree.getInstance()
                    .getById(plan1.getService().getId())
                    .getCountCustomersByOffice(u.getOffice()))
                .reduce(total, Integer::sum);
        } else {
            total = plan.stream()
                .map((plan1) -> QServiceTree.getInstance()
                    .getById(plan1.getService().getId())
                    .getCountCustomers())
                .reduce(total, Integer::sum);
        }
        return total;
    }

    public String getTotalLineSizeStr() {
        return l("vaiting") + " " + getTotalLineSize();
    }

    public String getPriority(long serviceId) {
        for (QPlanService qPlanService : plan) {
            if (qPlanService.getService().getId().equals(serviceId)) {
                final String res;
                switch (qPlanService.getCoefficient()) {
                    case 0: {
                        res = l("secondary");
                        break;
                    }
                    case 1: {
                        res = l("standard");
                        break;
                    }
                    case 2: {
                        res = "V.I.P.";
                        break;
                    }

                    default: {
                        res = l("stange");
                    }
                }
                return res;
            }
        }
        return "--";
    }

    @Override
    public String toString() {
        return name;
    }

}
