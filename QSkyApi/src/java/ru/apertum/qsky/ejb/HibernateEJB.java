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
package ru.apertum.qsky.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author egorov
 */
@Singleton(mappedName = "ejb/qskyapi/hibernate_session_factory", name = "qskyapi/HibernateEJB")
public class HibernateEJB implements IHibernateEJBLocal {
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    private SessionFactory sessionFactory;

    @PostConstruct
    private void buildHibernateSessionFactory() {
      //  getFactory();
    }

    protected SessionFactory getFactory() {
        if (sessionFactory == null) {
            try {
                // Create the SessionFactory from standard (hibernate.cfg.xml)
                // config file.
                final Configuration configuration = new Configuration();
                configuration.configure();
                final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Throwable ex) {
                // Log the exception.
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    @PreDestroy
    private void closeHibernateSessionFactory() {
        getFactory().close();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return getFactory();
    }

    @Override
    public Session openSession() {
        return getFactory().openSession();
    }

    @Override
    public Session getCurrentSession() {
        return getFactory().getCurrentSession();
    }

    @Override
    public Session cs() {
        return getFactory().getCurrentSession();
    }
}
