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

import java.io.IOException;
import java.net.Socket;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ReportException;

/**
 * Класс приема данных для Apache-HTTP Core
 * @author Evgeniy Egorov
 */
public class RunnableSocket implements Runnable {

    /**
     * Socket for receive data
     */
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public RunnableSocket() {
    }

    @Override
    public void run() {

        final DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
        try {
            conn.bind(socket, QSystemHtmlInstance.htmlInstance().getParams());
        } catch (IOException ex) {
            throw new ReportException("Not bind socket to connection." + ex);
        }

        final HttpContext context = new BasicHttpContext(null);
        try {
            while (!Thread.interrupted() && conn.isOpen()) {
                QSystemHtmlInstance.htmlInstance().getHttpService().handleRequest(conn, context);
            }
        } catch (ConnectionClosedException ex) {
            QLog.l().logRep().error("Client closed connection", ex);
        } catch (IOException ex) {
            QLog.l().logRep().error("I/O error: " + ex.getMessage(), ex);
        } catch (HttpException ex) {
            QLog.l().logRep().error("Unrecoverable HTTP protocol violation: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            QLog.l().logRep().error("Something with HTTP server.", ex);
        } finally {
            try {
                conn.shutdown();
            } catch (IOException ex) {
                QLog.l().logRep().error("Default Http Server Connection have error then shutdown.", ex);
            } catch (Exception ex) {
                QLog.l().logRep().error("Something with runnableSocket.", ex);
            }
        }


    }
}
