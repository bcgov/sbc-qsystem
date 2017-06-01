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
package ru.apertum.qsky.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 *
 * @author egorov
 */
public class ServerProps {

    private final ArrayList<String> vers;

    //final Properties settings;
    private ServerProps() {
        final Properties settings = new Properties();
        final InputStream inStream = this.getClass().getResourceAsStream("/qskyapi.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new RuntimeException("Cant read version. " + ex);
        }
        vers = new ArrayList<>(Arrays.asList(settings.getProperty("support_clients").split(";")));
    }

    public static ServerProps getInstance() {
        return ServerPropsHolder.INSTANCE;
    }

    private static class ServerPropsHolder {

        private static final ServerProps INSTANCE = new ServerProps();
    }

    public boolean isSupportClient(String clientVersion) {
        return vers.contains(clientVersion);
    }
}
