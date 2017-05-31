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
import ru.apertum.qsystem.server.model.QPlanService;
import ru.apertum.qsystem.server.model.QUserList;

/**
 *
 * @author Evgeniy Egorov
 */
public class RepCurrentUsers extends AGenerator {

    public RepCurrentUsers(String href, String resourceNameTemplate) {
        super(href, resourceNameTemplate);
    }

    @Override
    protected JRDataSource getDataSource(HttpRequest request) {
        final LinkedList<CurRepRecord> dataSource = new LinkedList<>();
        QUserList.getInstance().getItems().stream().forEach((user) -> {
            int user_worked = 0;
            int user_killed = 0;
            long user_avg_time_work = 0;
            for (QPlanService plan : user.getPlanServices()) {
                user_worked += plan.getWorked();
                user_killed += plan.getKilled();
                user_avg_time_work += (plan.getAvg_work() * plan.getWorked());
            }
            user_avg_time_work = user.getPlanServices().isEmpty() || user_worked == 0 ? 0 : user_avg_time_work / user_worked;
            for (QPlanService plan : user.getPlanServices()) {
                dataSource.add(new CurRepRecord(user.getName(), plan.getService().getName(), user_worked, user_killed, user_avg_time_work, plan.getWorked(), plan.getKilled(), plan.getAvg_work()));
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
