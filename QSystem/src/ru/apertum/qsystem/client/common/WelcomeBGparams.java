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
package ru.apertum.qsystem.client.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import ru.apertum.qsystem.common.QLog;

/**
 *
 * @author Evgeniy Egorov
 */
public class WelcomeBGparams {

    private static final String PROPS_FILE = "config/welcome_bg.properties";
    private static final HashMap<Long, String> IMGS = new HashMap<>();

    private WelcomeBGparams() {
        final File imgsFile = new File(PROPS_FILE);
        if (!imgsFile.exists()) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(imgsFile); Scanner s = new Scanner(fis)) {
            while (s.hasNextLine()) {
                final String line = s.nextLine().trim();
                if (!line.startsWith("#")) {
                    final String[] ss = line.split("=");
                    if (ss.length == 2) {
                        if (new File(ss[1]).exists()) {
                            QLog.l().logger().debug("OK " + ss[0] + " = " + ss[1]);
                            IMGS.put(Long.valueOf(ss[0]), ss[1]);
                        } else {
                            QLog.l().logger().debug("FIle not found " + ss[0] + " = " + ss[1]);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param id for service
     * @return spec file or default = WelcomeParams.getInstance().backgroundImg
     */
    public String getImg(Long id) {
        final String res = IMGS.get(id);
        return res == null ? WelcomeParams.getInstance().backgroundImg : res;
    }

    public static WelcomeBGparams getInstance() {
        return WelcomeBGparamsHolder.INSTANCE;
    }

    private static class WelcomeBGparamsHolder {

        private static final WelcomeBGparams INSTANCE = new WelcomeBGparams();
    }
}
