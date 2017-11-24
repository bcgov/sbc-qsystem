/*
 * Copyright (C) 2014 Evgeniy Egorov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.qsystem.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * @author Evgeniy Egorov
 */
public class Exit {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        final String data;
        try {
            final Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("127.0.0.1",
                    ServerProps.getInstance().getProps().getServerPort()), 3000);
            } catch (IOException ex) {
                throw new Exception("Fail to connect to server. ", ex);
            }
            final PrintWriter writer;
            final Scanner in;
            try {
                writer = new PrintWriter(socket.getOutputStream());
                writer.print(URLEncoder.encode("exit", "utf-8"));
                writer.flush();
                writer.close();
            } finally {
                socket.close();
            }
        } catch (IOException ex) {
            throw new Exception("Unable to get a response from the server. ", ex);
        }
    }

}
