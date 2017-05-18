/*
 *  Copyright (C) 2011 egorov
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
package ru.apertum.qsystem.server;

import java.util.ServiceLoader;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.controller.IIndicatorBoard;
import ru.apertum.qsystem.server.controller.QIndicatorBoardMonitor;
import ru.apertum.qsystem.server.htmlboard.QIndicatorHtmlboard;

/**
 *
 * @author egorov
 */
public class MainBoard {

    private MainBoard() {
    }

    public static IIndicatorBoard getInstance() {
        return MainBoardHolder.INSTANCE;
    }

    private static class MainBoardHolder {

        //private static final IIndicatorBoard INSTANCE = (IIndicatorBoard) Spring.getInstance().getFactory().getBean("indicatorBoard");
        private static final IIndicatorBoard INSTANCE = setup();

        private static IIndicatorBoard setup() {
            // поддержка расширяемости плагинами
            IIndicatorBoard res = null;
            for (final IIndicatorBoard board : ServiceLoader.load(IIndicatorBoard.class)) {
                QLog.l().logger().info("Вызов SPI расширения. Описание: " + board.getDescription());
                try {
                    res = board;
                } catch (Throwable tr) {
                    QLog.l().logger().error("Вызов SPI расширения завершился ошибкой. Описание: " + tr);
                }
                // раз напечатили и хорошь
                if (res != null) {
                    break;
                }
            }
            if (res == null) {
                final boolean bClassicType = IIndicatorBoard.CLASSIC.equalsIgnoreCase(ServerProps.getInstance().getProperty(IIndicatorBoard.SECTION, IIndicatorBoard.PARAMETER, IIndicatorBoard.CLASSIC));
                if (bClassicType) {
                    res = new QIndicatorBoardMonitor();
                    ((QIndicatorBoardMonitor) res).setConfigFile("config\\mainboard.xml");
                } else {
                    res = new QIndicatorHtmlboard();
                }

            }
            return res;
        }
    }
}
