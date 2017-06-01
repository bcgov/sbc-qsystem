/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsky.plugins.ws;

import java.net.URL;
import ru.apertum.qsky.plugins.ws.ws_client.CustomerEvents;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.ServerProps;

/**
 * Через этот класс используем коннектор до вебсервисов.
 *
 * @author egorov
 */
public class SkyService {

    private CustomerEvents customerEvents;
    private String sUrl = "";

    private SkyService() {

    }

    private void recreateCustomerEvents() {
        if (!sUrl.equals(ServerProps.getInstance().getProps().getSkyServerUrl())) {
            final CustomerEvents ce;
            try {
                ce = new CustomerEvents(new URL(ServerProps.getInstance().getProps().getSkyServerUrl()));
            } catch (Exception ex) {
                throw new ServerException("Impossible create access to web service. ", ex);
            }
            sUrl = ServerProps.getInstance().getProps().getSkyServerUrl();
            customerEvents = ce;
        }
    }

    private synchronized CustomerEvents getCustomerEvents() {
        recreateCustomerEvents();
        return customerEvents;
    }

    public static CustomerEvents getInstance() {
        return CEHolder.INSTANCE.getCustomerEvents();
    }

    private static class CEHolder {

        private static final SkyService INSTANCE = new SkyService();
    }
}
