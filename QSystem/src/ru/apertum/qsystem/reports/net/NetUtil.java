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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.exceptions.ReportException;

/**
 *
 *@author Evgeniy Egorov
 */
public class NetUtil {

    private static final HashMap<HttpRequest, String> map_ec = new HashMap<>();

    public static synchronized String getEntityContent(HttpRequest request) {
        String result = map_ec.get(request);
        if (result == null) {

            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                try {
                    result = EntityUtils.toString(entity);
                } catch (IOException | ParseException ex) {
                    throw new ReportException(ex.toString());
                }
            } else {
                result = "";
            }
            try {
                result = URLDecoder.decode(result, "utf-8");
            } catch (UnsupportedEncodingException ex) {
                throw new ReportException(ex.toString());
            }
            map_ec.put(request, result);
        }
        return result;
    }

    public static synchronized void freeEntityContent(HttpRequest request) {
        map_ec.remove(map_ec);
    }

    public static synchronized HashMap<String, String> getCookie(String data, String delimiter) {
        final HashMap<String, String> res = new HashMap<>();
        final String[] ss = data.split(delimiter);
        for (String s : ss) {
            final String[] ss0 = s.split("=");
            try {
                res.put(URLDecoder.decode(ss0[0], "utf-8"), URLDecoder.decode(ss0.length == 1 ? "" : ss0[1], "utf-8"));
            } catch (UnsupportedEncodingException ex) {
                QLog.l().logRep().error(ss0[1], ex);
            }
        }
        return res;
    }

    public static synchronized String getUrl(HttpRequest request) {
        final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (method.equals("GET")) {
            return request.getRequestLine().getUri().split("\\?")[0];
        } else {
            return request.getRequestLine().getUri();
        }
    }

    public static synchronized HashMap<String, String> getParameters(HttpRequest request) {
        final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        final HashMap<String, String> res = new HashMap<>();
        final String data;
        if (method.equals("GET")) {
            String[] ss = request.getRequestLine().getUri().split("\\?");
            if (ss.length == 2) {
                try {
                    data = URLDecoder.decode(request.getRequestLine().getUri().split("\\?")[1], "utf-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new ReportException(ex.toString());
                }
            } else {
                data = "";
            }
        } else {
            data = getEntityContent(request);
        }
        String[] ss = data.split("&");
        for (String str : ss) {
            String[] ss1 = str.split("=");
            if (!ss1[0].isEmpty()) {
                res.put(ss1[0], ss1.length == 1 ? "" : ss1[1]);
            }
        }
        return res;
    }
}
