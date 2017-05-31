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
package ru.apertum.qsystem.reports.generators;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.reports.common.Response;
import ru.apertum.qsystem.reports.model.AGenerator;
import ru.apertum.qsystem.reports.model.CurRepRecord;
import ru.apertum.qsystem.server.model.QServiceTree;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

/**
 *
 * @author Evgeniy Egorov
 */
public class ReportCurrentServices extends AGenerator {

    public ReportCurrentServices(String href, String resourceNameTemplate) {
        super(href, resourceNameTemplate);
    }

    @Override
    protected JRDataSource getDataSource(HttpRequest request) {
        final LinkedList<CurRepRecord> dataSource = new LinkedList<>();
        QServiceTree.getInstance().getNodes().stream().filter((service) -> (service.isLeaf())).forEach((service) -> {
            int service_worked = 0;
            int service_killed = 0;
            long service_avg_time_work = 0;
            long service_avg_time_wait = 0;
            for (QUser user : QUserList.getInstance().getItems()) {
                if (user.hasService(service)) {
                    service_worked += user.getPlanService(service).getWorked();
                    service_killed += user.getPlanService(service).getKilled();
                    service_avg_time_work += (user.getPlanService(service).getAvg_work() * user.getPlanService(service).getWorked());
                    service_avg_time_wait += (user.getPlanService(service).getAvg_wait() * user.getPlanService(service).getWorked());
                }
            }
            service_avg_time_work = service_worked == 0 ? 0 : service_avg_time_work / service_worked;
            service_avg_time_wait = service_worked == 0 ? 0 : service_avg_time_wait / service_worked;
            for (QUser user : QUserList.getInstance().getItems()) {
                if (user.hasService(service)) {
                    dataSource.add(new CurRepRecord(user.getName(), service.getName(), service_worked, service_killed, service_avg_time_work, service.getCountCustomers(), service_avg_time_wait,
                            user.getPlanService(service).getWorked(), user.getPlanService(service).getKilled(), user.getPlanService(service).getAvg_work()));
                }
            }
        });
        return new JRBeanCollectionDataSource(dataSource);
    }

    @Override
    protected HashMap getParameters(HttpRequest request) {
        return new HashMap();
    }

    @Override
    protected Connection getConnection(HttpRequest request) {
        return null;
    }

    @Override
    protected Response preparationReport(HttpRequest request) {
        return null;
    }

    @Override
    protected Response getDialog(HttpRequest request, String errorMessage) {
        return null;
    }

    @Override
    protected String validate(HttpRequest request, HashMap<String, String> params) {
        return null;
    }
}
