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
package ru.apertum.qsystem.reports.model;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import net.sf.jasperreports.engine.JRDataSource;
import org.apache.http.HttpRequest;
import ru.apertum.qsystem.common.exceptions.ReportException;
import ru.apertum.qsystem.reports.common.Response;
import ru.apertum.qsystem.reports.formirovators.IFormirovator;
import ru.apertum.qsystem.server.Spring;
import ru.apertum.qsystem.server.model.IidGetter;

/**
 * Класс описания аналитических отчетов.
 * @author Evgeniy Egorov
 */
@Entity
@Table(name = "reports")
public class QReport extends AGenerator implements IidGetter, Serializable {

    private Long id;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    private String className;

    @Column(name = "className")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
        try {
            formirovator = (IFormirovator) Class.forName(getClassName()).newInstance();
        } catch (InstantiationException ex) {
            throw new ReportException("Чет не в порядке \"" + className + "\". " + ex);
        } catch (IllegalAccessException ex) {
            throw new ReportException("Нет доступа \"" + className + "\". " + ex);
        } catch (ClassNotFoundException ex) {
            throw new ReportException("Класс не найден \"" + className + "\". " + ex);
        }
    }
    private IFormirovator formirovator;
    private String name;

    @Column(name = "name")
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

    @Override
    protected JRDataSource getDataSource(HttpRequest request) {
        return formirovator.getDataSource(Spring.getInstance().getDriverClassName(), Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword(), request);
    }

     @Override
    protected Map getParameters(HttpRequest request) {
        return formirovator.getParameters(Spring.getInstance().getDriverClassName(), Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword(), request);
    }

    @Override
    protected Connection getConnection(HttpRequest request) {
        return formirovator.getConnection(Spring.getInstance().getDriverClassName(), Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword(), request);
    }

    @Override
    protected Response preparationReport(HttpRequest request) {
        return formirovator.preparationReport(Spring.getInstance().getDriverClassName(), Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword(), request);
    }

    @Override
    protected Response getDialog(HttpRequest request, String errorMessage) {
        return formirovator.getDialog(Spring.getInstance().getDriverClassName(), Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword(), request, errorMessage);
    }

    @Override
    protected String validate(HttpRequest request, HashMap<String, String> params) {
        return formirovator.validate(Spring.getInstance().getDriverClassName(), Spring.getInstance().getUrl(), Spring.getInstance().getUsername(), Spring.getInstance().getPassword(), request, params);
    }
}
