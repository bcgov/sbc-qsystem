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
package ru.apertum.qsky.plugins.ping;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import ru.apertum.qsky.plugins.IQSkyPluginUID;
import ru.apertum.qsky.plugins.ws.SkyService;
import ru.apertum.qsystem.extra.IPing;

/**
 *
 * @author egorov
 */
public class Pinger implements IPing, IQSkyPluginUID {

    @Override
    public String getDescription() {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/qskyplugin.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new RuntimeException("Проблемы с чтением версии. ", ex);
        }
        return "Плагин \"QSkySenderPlugin\" v.=" + settings.getProperty("version") + " дата=" + settings.getProperty("date") + " опрашивет облако на существование и совместимость.";
    }

    @Override
    public int ping() {
        final Properties settings = new Properties();
        final InputStream inStream = settings.getClass().getResourceAsStream("/qskyplugin.properties");
        try {
            settings.load(inStream);
        } catch (IOException ex) {
            throw new RuntimeException("Проблемы с чтением версии. ", ex);
        }
        return SkyService.getInstance().getQsky().ping(settings.getProperty("version"));
    }

    @Override
    public long getUID() {
        return UID;
    }
}
