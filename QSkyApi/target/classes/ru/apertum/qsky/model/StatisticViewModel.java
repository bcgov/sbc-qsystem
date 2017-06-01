/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.GroupComparator;
import org.zkoss.zul.GroupsModelArray;

/**
 *
 * @author Evgeniy Egorov
 */
public class StatisticViewModel {

    private Date start;
    private Date finish;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getFinish() {
        return finish;
    }

    public void setFinish(Date finish) {
        this.finish = finish;
    }

    private int regim;

    public int getRegim() {
        return regim;
    }

    public void setRegim(int regim) {
        this.regim = regim;
    }

    public boolean showGroup = true;

    private GroupsModelArray statisticModel = new StaticticGroupingEmplsModel(new ArrayList<>(), new RecordEmployeeComparator());

    public GroupsModelArray getStatisticModel() {
        return statisticModel;
    }

    public void setStatisticModel(GroupsModelArray statisticModel) {
        this.statisticModel = statisticModel;
    }

    public List<Step> loadStats(long branchId, Session ses) {
        ses.beginTransaction();
        final List<Step> custs;
        try {

            final GregorianCalendar day = new GregorianCalendar();
            day.setTime(finish);
            day.set(GregorianCalendar.HOUR_OF_DAY, 23);
            day.set(GregorianCalendar.MINUTE, 59);

            custs = ses.createCriteria(Step.class)
                    .add(Restrictions.eq("branchId", branchId))
                    .add(Restrictions.isNotNull("finishTime"))
                    .add(Restrictions.between("standTime", start, day.getTime())).list();
        } catch (Exception ex) {
            throw new RuntimeException("Not loaded a list of steps. " + ex);
        } finally {
            ses.getTransaction().rollback();
        }
        return custs;
    }

    public ArrayList<Record> prepareDataByUser(List<Step> steps) {
        final ArrayList<Record> recs = new ArrayList<>();
        for (Step step : steps) {
            boolean f = true;
            for (Record rec : recs) {
                if (rec.branchId.equals(step.getBranchId()) && rec.serviceId.equals(step.getServiceId()) && rec.employeeId.equals(step.getEmployeeId())) {
                    if (step.getFinishState() == 0) {
                        rec.removed++;
                    } else {
                        rec.avgMin = rec.avgMin * rec.served + (int) (step.getWorking() / 1000 / 60);
                        rec.served++;
                        rec.avgMin = rec.avgMin / rec.served;
                    }
                    f = false;
                    break;
                }
            }
            if (f) {
                recs.add(new Record(step.getBranchId(), step.getServiceId(), step.getEmployeeId(), step.getFinishState() == 0 ? 0 : 1, step.getFinishState() == 0 ? 1 : 0, step.getFinishState() == 0 ? 0 : ((int) (step.getWorking() / 1000 / 60))));
            }
        }
        System.out.println("+++ " + recs);
        return recs;
    }

    public static class StaticticGroupingServsModel extends GroupsModelArray<Record, String, String, Object> {

        public String l(String resName) {
            return Labels.getLabel(resName);
        }

        public StaticticGroupingServsModel(List<Record> data, Comparator<Record> cmpr) {
            super(data.toArray(new Record[data.size()]), cmpr);
        }

        @Override
        protected String createGroupHead(Record[] groupdata, int index, int col) {
            int served = 0;
            int removed = 0;
            int avgMin = 0;
            for (Record rec : groupdata) {
                served = served + rec.served;
                removed = removed + rec.removed;
                avgMin = avgMin + rec.avgMin;
            }
            return groupdata.length == 0 ? l("empty_group")
                    : l("service") + " " + "\"" + (groupdata[0].serviceName.length() > 30 ? groupdata[0].serviceName.substring(0, 30) + "..." : groupdata[0].serviceName) + "\""
                    + " " + l("served2") + " " + served
                    + ", " + l("killed2") + " " + removed
                    + ", " + l("avd_working") + " " + (avgMin / groupdata.length);
        }

        @Override
        protected String createGroupFoot(Record[] groupdata, int index, int col) {
            return String.format(l("amount_users_for_1service") + ": %d", groupdata.length);
        }

        @Override
        public boolean hasGroupfoot(int groupIndex) {
            return true;
        }
    }

    public static class StaticticGroupingEmplsModel extends GroupsModelArray<Record, String, String, Object> {

        public String l(String resName) {
            return Labels.getLabel(resName);
        }

        public StaticticGroupingEmplsModel(List<Record> data, Comparator<Record> cmpr) {
            super(data.toArray(new Record[data.size()]), cmpr);
        }

        @Override
        protected String createGroupHead(Record[] groupdata, int index, int col) {
            int served = 0;
            int removed = 0;
            int avgMin = 0;
            for (Record rec : groupdata) {
                served = served + rec.served;
                removed = removed + rec.removed;
                avgMin = avgMin + rec.avgMin;
            }
            return groupdata.length == 0 ? l("empty_group")
                    : l("employee") + " " + "\"" + groupdata[0].employeeName + "\""
                    + " " + l("served2") + " " + served
                    + ", " + l("killed2") + " " + removed
                    + ", " + l("avd_working") + " " + (avgMin / groupdata.length);
        }

        @Override
        protected String createGroupFoot(Record[] groupdata, int index, int col) {
            return String.format(l("served_in_amount") + ": %d", groupdata.length);
        }

        @Override
        public boolean hasGroupfoot(int groupIndex) {
            return true;
        }
    }

    public static class RecordEmployeeComparator implements Comparator<Record>, GroupComparator<Record>, Serializable {

        @Override
        public int compare(Record o1, Record o2) {
            return o1.getEmployeeId().compareTo(o2.getEmployeeId());
        }

        @Override
        public int compareGroup(Record o1, Record o2) {
            return o1.getEmployeeId().compareTo(o2.getEmployeeId());
        }
    }

    public static class RecordServiceComparator implements Comparator<Record>, GroupComparator<Record>, Serializable {

        @Override
        public int compare(Record o1, Record o2) {
            return o1.getServiceId().compareTo(o2.getServiceId());
        }

        @Override
        public int compareGroup(Record o1, Record o2) {
            return o1.getServiceId().compareTo(o2.getServiceId());
        }
    }

    public static class Record {

        private final Long branchId;
        private final Long serviceId;
        private final Long employeeId;
        private int served;
        private int removed;
        private int avgMin;
        private final String serviceName;
        private final String employeeName;

        public Record(long branchId, long serviceId, long employeeId, int served, int removed, int avgMin) {
            this.branchId = branchId;
            this.serviceId = serviceId;
            this.employeeId = employeeId;
            this.served = served;
            this.removed = removed;
            this.avgMin = avgMin;

            serviceName = Dicts.getInstance().getServiceName(branchId, serviceId);
            employeeName = Dicts.getInstance().getEmployeeName(branchId, employeeId);
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public Long getBranchId() {
            return branchId;
        }

        public Long getServiceId() {
            return serviceId;
        }

        public Long getEmployeeId() {
            return employeeId;
        }

        public int getServed() {
            return served;
        }

        public void setServed(int served) {
            this.served = served;
        }

        public int getRemoved() {
            return removed;
        }

        public void setRemoved(int removed) {
            this.removed = removed;
        }

        public int getAvgMin() {
            return avgMin;
        }

        public void setAvgMin(int avgMin) {
            this.avgMin = avgMin;
        }

        @Override
        public String toString() {
            return serviceId + "/" + employeeId;
        }

    }

}
