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

package ru.apertum.qsystem.reports.net;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.HttpRequestHandlerRegistry;

/**
 *
 * @author Evgeniy Egorov
 */
public class QSystemHtmlInstance {

    private static final QSystemHtmlInstance HTML_INSTANCE = new QSystemHtmlInstance();

    public static QSystemHtmlInstance htmlInstance(){
        return HTML_INSTANCE;
    }

    private QSystemHtmlInstance() {
        this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000).
                setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024).
                setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).
                setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true).
                setParameter(CoreProtocolPNames.ORIGIN_SERVER, "QSystemReportHttpServer/1.1");

        // Set up the HTTP protocol processor
        final BasicHttpProcessor httpproc = new BasicHttpProcessor();
        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());

        // Set up request handlers
        final HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
        reqistry.register("*", new HttpQSystemReportsHandler());

        // Set up the HTTP service
        this.httpService = new HttpService(
                httpproc,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory(), reqistry, this.params);
    }
    private final HttpParams params = new BasicHttpParams();
    private final HttpService httpService;

    public HttpService getHttpService() {
        return httpService;
    }

    public HttpParams getParams() {
        return params;
    }
}