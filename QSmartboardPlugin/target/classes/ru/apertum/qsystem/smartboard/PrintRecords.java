/*
 * Copyright (C) 2015 Evgeniy Egorov
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
package ru.apertum.qsystem.smartboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Properties;
import ru.apertum.qsystem.server.controller.AIndicatorBoard;
import ru.apertum.qsystem.server.model.QUser;

/**
 *
 * @author Evgeniy Egorov
 */
public class PrintRecords {

    private int linesCount = 6;
    private String topSize = "0px";
    private String topUrl = "";
    private String leftSize = "0px";
    private String leftUrl = "";
    private String rightSize = "0px";
    private String rightUrl = "";
    private String bottomSize = "0px";
    private String bottomUrl = "";

    private String columnFirst = "Clients column";
    private String columnSecond = "To point";
    private String columnExt = "Ext column";
    private String customerDisplay="padding:0px";
    private String display = "";
    private QUser user = null;

    public int getLinesCount() {
        return linesCount;
    }

    public String getTopSize() {
        return topSize;
    }

    public String getTopUrl() {
        return topUrl;
    }

    public String getLeftSize() {
        return leftSize;
    }

    public String getLeftUrl() {
        return leftUrl;
    }

    public String getRightSize() {
        return rightSize;
    }

    public String getRightUrl() {
        return rightUrl;
    }

    public String getBottomSize() {
        return bottomSize;
    }

    public String getBottomUrl() {
        return bottomUrl;
    }

    public String getColumnFirst() {
        return columnFirst;
    }

    public String getColumnSecond() {
        return columnSecond;
    }

    public String getColumnExt() {
        return columnExt;
    }
    
    public String getCustomerDisplay() {        
        return customerDisplay;
    }    
    
    public PrintRecords() {
		String qsb = System.getenv ("QSB");
		File f = new File("config/QSmartboardPlugin.properties");
		if (qsb.equalsIgnoreCase("callbyticket")) {
			f = new File("config/QSmartboardPlugin-original.properties");
		};
		if (qsb.equalsIgnoreCase("callbyname")) {
			f = new File("config/QSmartboardPlugin-name.properties");
		};		
		if (f.exists()) {
            final FileInputStream inStream;
            try {
                inStream = new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            final Properties settings = new Properties();
            try {
                settings.load(new InputStreamReader(inStream, "UTF-8"));
            } catch (IOException ex) {
                throw new RuntimeException("Cant read version. " + ex);
            }

            linesCount = Integer.parseInt(settings.getProperty("lines_count", "6"));
            topSize = settings.getProperty("top.size").matches("^-?\\d+(%|px)$") ? settings.getProperty("top.size") : "0px";
            topUrl = settings.getProperty("top.url");
            leftSize = settings.getProperty("left.size").matches("^-?\\d+(%|px)$") ? settings.getProperty("left.size") : "0px";
            leftUrl = settings.getProperty("left.url");
            rightSize = settings.getProperty("right.size").matches("^-?\\d+(%|px)$") ? settings.getProperty("right.size") : "0px";
            rightUrl = settings.getProperty("right.url");
            bottomSize = settings.getProperty("bottom.size").matches("^-?\\d+(%|px)$") ? settings.getProperty("bottom.size") : "0px";
            bottomUrl = settings.getProperty("bottom.url");

            columnFirst = settings.getProperty("column.first");
            columnSecond = settings.getProperty("column.second");
            columnExt = settings.getProperty("column.ext");
            customerDisplay = settings.getProperty("customer.display");  
        }
    }

       
    public static PrintRecords getInstance() {
        return PrintRecordsHolder.INSTANCE;
    }

    private static class PrintRecordsHolder {

        private static final PrintRecords INSTANCE = new PrintRecords();
    }

    private LinkedList<AIndicatorBoard.Record> records = new LinkedList<>();

    public LinkedList<AIndicatorBoard.Record> getRecords() {
        return records;
    }

    public void setRecords(LinkedList<AIndicatorBoard.Record> records) {
        this.records = records;
    }

    private boolean invited;

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(boolean invited) {
        this.invited = invited;
    }
    
    public QUser getCurrentUser() {
        return user;
    }
        
    public void setCurrentUser(QUser user) {
        this.user = user;
    }    
  
}
