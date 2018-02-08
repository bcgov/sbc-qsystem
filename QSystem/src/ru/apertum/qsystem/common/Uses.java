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
package ru.apertum.qsystem.common;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.dom4j.Element;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import ru.apertum.qsystem.QSystem;
import ru.apertum.qsystem.client.Locales;
import ru.apertum.qsystem.client.forms.FClient;
import ru.apertum.qsystem.client.forms.FServicePriority;
import ru.apertum.qsystem.common.exceptions.ServerException;
import ru.apertum.qsystem.server.ServerProps;

/**
 * @author Evgeniy Egorov Ð¡Ð´ÐµÑ�ÑŒ Ð½Ð°Ñ…Ð¾Ð´Ñ�Ñ‚Ñ�Ñ� ÐºÐ¾Ð½Ñ�Ñ‚Ð°Ð½Ñ‚Ñ‹ Ð¸ Ð¾Ð±Ñ‰ÐµÐ¸Ñ�Ð¿Ð¾Ð»ÑŒÐ·ÑƒÐµÐ¼Ñ‹Ðµ ÐºÐ¾Ð½Ñ�Ñ‚Ñ€ÑƒÐºÑ†Ð¸Ð¸
 */
public final class Uses {

    // Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸Ñ� Ð¿Ñ€Ð¸Ð¾Ñ€Ð¸Ñ‚ÐµÑ‚Ð° "Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð½Ð¸ÐºÐ°"
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HI = 2;
    public static final int PRIORITY_VIP = 3;
    public static final int[] PRIORITYS = {PRIORITY_LOW, PRIORITY_NORMAL, PRIORITY_HI,
        PRIORITY_VIP};
    public static final String PROPERTIES_FILE = "config/qsystem.properties";
    // Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸Ñ� Ð¿Ñ€Ð¸Ð¾Ñ€Ð¸Ñ‚ÐµÑ‚Ð° Ð¾Ð±Ñ€Ð°Ð±Ð°Ñ‚Ñ‹Ð²Ð°ÐµÐ¼Ñ‹Ñ… ÑƒÑ�Ð»ÑƒÐ³ Ð´Ð»Ñ� ÑŽÐ·ÐµÑ€Ð°
    // public static final int SERVICE_EXCLUDE = -1;
    public static final int SERVICE_REMAINS = 0;
    public static final int SERVICE_NORMAL = 1;
    public static final int SERVICE_VIP = 2;
    //public static final int[] SERVICE_PRIORITYS = {SERVICE_EXCLUDE, SERVICE_REMAINS, SERVICE_NORMAL, SERVICE_VIP};
    public static final LinkedHashMap<Integer, String> COEFF_WORD = new LinkedHashMap<>();
    // Naming tags and attributes in XML statistics protocols
    public static final String TAG_REP_STATISTIC = "Statistics";//"Ð¡Ñ‚Ð°Ñ‚Ð¸Ñ�Ñ‚Ð¸ÐºÐ°";
    public static final String TAG_REP_PARAM_COUNT = "Denominator";//"Ð—Ð½Ð°Ð¼ÐµÐ½Ð°Ñ‚ÐµÐ»ÑŒ";
    public static final String TAG_REP_PARAM_AVG = "Average";//"Ð¡Ñ€ÐµÐ´Ð½ÐµÐµ";
    public static final String TAG_REP_RECORD = "Recording";//"Ð—Ð°Ð¿Ð¸Ñ�ÑŒ";
    public static final String TAG_REP_SERVICE_WORKED = "Serviced on a Service";//"ÐžÐ±Ñ�Ð»ÑƒÐ¶ÐµÐ½Ð¾ÐŸÐ¾Ð£Ñ�Ð»ÑƒÐ³Ðµ";
    public static final String TAG_REP_SERVICE_WAIT = "Waiting for Service";//"ÐžÐ¶Ð¸Ð´Ð°ÑŽÑ‚ÐŸÐ¾Ð£Ñ�Ð»ÑƒÐ³Ðµ";
    public static final String TAG_REP_SERVICE_AVG_WORK = "Servicing Services";//"Ð¡Ñ€Ð’Ñ€ÐžÐ±Ñ�Ð»ÑƒÐ¶Ð¸Ð²Ð°Ð½Ð¸Ñ�ÐŸÐ¾Ð£Ñ�Ð»ÑƒÐ³Ðµ";
    public static final String TAG_REP_SERVICE_AVG_WAIT = "WAITING FOR SERVICE";//"Ð¡Ñ€Ð’Ñ€ÐžÐ¶Ð¸Ð´Ð°Ð½Ð¸Ñ�ÐŸÐ¾Ð£Ñ�Ð»ÑƒÐ³Ðµ";
    public static final String TAG_REP_SERVICE_KILLED = "Declined on the Service";//"ÐžÑ‚ÐºÐ»Ð¾Ð½ÐµÐ½Ð½Ñ‹Ñ…ÐŸÐ¾Ð£Ñ�Ð»ÑƒÐ³Ðµ";
    public static final String TAG_REP_USER_WORKED = "Served by User";//"ÐžÐ±Ñ�Ð»ÑƒÐ¶ÐµÐ½Ð¾ÐŸÐ¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¼";
    public static final String TAG_REP_USER_AVG_WORK = "User Service";//"Ð¡Ñ€Ð’Ñ€ÐžÐ±Ñ�Ð»ÑƒÐ¶Ð¸Ð²Ð°Ð½Ð¸Ñ�ÐŸÐ¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¼";
    public static final String TAG_REP_USER_KILLED = "Declined by User";//"ÐžÑ‚ÐºÐ»Ð¾Ð½ÐµÐ½Ð½Ñ‹Ñ…ÐŸÐ¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¼";
    public static final String TAG_REP_WORKED = "Served";//"ÐžÐ±Ñ�Ð»ÑƒÐ¶ÐµÐ½Ð½Ñ‹Ñ…";
    public static final String TAG_REP_AVG_TIME_WORK = "Maintenance";//"Ð¡Ñ€Ð’Ñ€ÐžÐ±Ñ�Ð»ÑƒÐ¶Ð¸Ð²Ð°Ð½Ð¸Ñ�";
    public static final String TAG_REP_KILLED = "Disapproved";//"ÐžÑ‚ÐºÐ»Ð¾Ð½ÐµÐ½Ð½Ñ‹Ñ…";
    // Tags and attribute names of the setup file
    public static final String TAG_PROP_SERVICES = "The services";//"Ð£Ñ�Ð»ÑƒÐ³Ð¸";
    public static final String TAG_PROP_SERVICE = "Service";//"Ð£Ñ�Ð»ÑƒÐ³Ð°";
    public static final String TAG_PROP_NAME = "Name";//"Ð�Ð°Ð¸Ð¼ÐµÐ½Ð¾Ð²Ð°Ð½Ð¸Ðµ";
    public static final String TAG_PROP_DESCRIPTION = "Description";//"ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ";
    public static final String TAG_PROP_PREFIX = "Prefix";//"ÐŸÑ€ÐµÑ„Ð¸ÐºÑ�";
    public static final String TAG_PROP_ADVANCE_LIMIT = "Limit";//"Ð›Ð¸Ð¼Ð¸Ñ‚";
    public static final String TAG_PROP_ADVANCE_PERIOD_LIMIT = "Limit Prior Records In Days";//"Ð›Ð¸Ð¼Ð¸Ñ‚ÐŸÑ€ÐµÐ´Ð²Ð—Ð°Ð¿Ð¸Ñ�Ð¸Ð’Ð”Ð½Ñ�Ñ…";
    public static final String TAG_PROP_USERS = "Members List";//"ÐŸÐ¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»Ð¸";
    public static final String TAG_PROP_USER = "User";//"ÐŸÐ¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÑŒ";
    public static final String TAG_PROP_PASSWORD = "Password";//"ÐŸÐ°Ñ€Ð¾Ð»ÑŒ";
    public static final String TAG_PROP_OWN_SERVS = "Services Provided";//"ÐžÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÐ¼Ñ‹ÐµÐ£Ñ�Ð»ÑƒÐ³Ð¸";
    public static final String TAG_PROP_OWN_SRV = "The Service";//"ÐžÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÐ¼Ð°Ñ�Ð£Ñ�Ð»ÑƒÐ³Ð°";
    public static final String TAG_PROP_KOEF = "Coefficient of Participation";//"ÐšÐ¾Ñ�Ñ„Ñ„Ð¸Ñ†Ð¸ÐµÐ½Ñ‚Ð£Ñ‡Ð°Ñ�Ñ‚Ð¸Ñ�";
    public static final String TAG_PROP_CONNECTION = "Net";//"Ð¡ÐµÑ‚ÑŒ";
    public static final String TAG_PROP_SERV_PORT = "Port Server";//"ÐŸÐ¾Ñ€Ñ‚Ð¡ÐµÑ€Ð²ÐµÑ€Ð°";
    public static final String TAG_PROP_WEB_SERV_PORT = "Port Web Server";//"ÐŸÐ¾Ñ€Ñ‚Ð’ÐµÐ±Ð¡ÐµÑ€Ð²ÐµÑ€Ð°";
    public static final String TAG_PROP_CLIENT_PORT = "Port Customer";//"ÐŸÐ¾Ñ€Ñ‚ÐšÐ»Ð¸ÐµÐ½Ñ‚Ð°";
    public static final String TAG_PROP_SERV_ADDRESS = "Server Address";//"Ð�Ð´Ñ€ÐµÑ�Ð¡ÐµÑ€Ð²ÐµÑ€Ð°";
    public static final String TAG_PROP_CLIENT_ADDRESS = "Customer Address";//"Ð�Ð´Ñ€ÐµÑ�ÐšÐ»Ð¸ÐµÐ½Ñ‚Ð°";
    public static final String TAG_PROP_STATUS = "Status";//"Ð¡Ñ‚Ð°Ñ‚ÑƒÑ�";
    public static final String TAG_PROP_START_TIME = "Start Time";//"Ð’Ñ€ÐµÐ¼Ñ�Ð�Ð°Ñ‡Ð°Ð»Ð°Ð Ð°Ð±Ð¾Ñ‚Ñ‹";
    public static final String TAG_PROP_FINISH_TIME = "Completion Time";//"Ð’Ñ€ÐµÐ¼Ñ�Ð—Ð°Ð²ÐµÑ€ÑˆÐµÐ½Ð¸Ñ�Ð Ð°Ð±Ð¾Ñ‚Ñ‹";
    public static final String TAG_PROP_VERSION = "Configuration Warehouse Version";//"Ð’ÐµÑ€Ñ�Ð¸Ñ�Ð¥Ñ€Ð°Ð½Ð¸Ð»Ð¸Ñ‰Ð°ÐšÐ¾Ð½Ñ„Ð¸Ð³ÑƒÑ€Ð°Ñ†Ð¸Ð¸";
    public static final String TAG_PROP_INPUT_REQUIRED = "Requirement for Client Data";//"Ð¢Ñ€ÐµÐ±Ð¾Ð²Ð°Ð½Ð¸ÐµÐšÐ»Ð¸ÐµÐ½Ñ‚Ñ�ÐºÐ¸Ñ…Ð”Ð°Ð½Ð½Ñ‹Ñ…";
    public static final String TAG_PROP_INPUT_CAPTION = "Data Entry Form Header";//"Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾ÐºÐ¤Ð¾Ñ€Ð¼Ñ‹Ð’Ð²Ð¾Ð´Ð°ÐšÐ»Ð”Ð°Ð½Ð½Ñ‹Ñ…";
    public static final String TAG_PROP_RESULT_REQUIRED = "Requirement of Work Results";//"Ð¢Ñ€ÐµÐ±Ð¾Ð²Ð°Ð½Ð¸ÐµÐ ÐµÐ·ÑƒÐ»ÑŒÑ‚Ð°Ñ‚Ð°Ð Ð°Ð±Ð¾Ñ‚Ñ‹";
    // Tags and attribute names of the configuration files of the main scoreboard
    public static final String TAG_BOARD_PROPS = "Options"; //"ÐŸÐ°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ñ‹";
    public static final String TAG_BOARD_PROP = "Parameter"; //"ÐŸÐ°Ñ€Ð°Ð¼ÐµÑ‚ÐµÑ€";
    public static final String TAG_BOARD_NAME = "Name"; //"Ð�Ð°Ð¸Ð¼ÐµÐ½Ð¾Ð²Ð°Ð½Ð¸Ðµ";
    public static final String TAG_BOARD_VALUE = "Value"; //"Ð—Ð½Ð°Ñ‡ÐµÐ½Ð¸Ðµ";
    public static final String TAG_BOARD_TYPE = "Type"; //"Ð¢Ð¸Ð¿";
    public static final String TAG_BOARD_READ_ONLY = "ReadOnly";
    // Ð¸Ð¼ÐµÐ½Ð° Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ð¾Ð² Ð´Ð»Ñ� Ñ‚Ð°Ð±Ð»Ð¾
    // Parameter names for the scoreboard
    public static final String TAG_BOARD_FRACTAL = "Fractal";//"Fractal";
    public static final String TAG_BOARD_MONITOR = "Additional monitor number for the scoreboard";//"Ð�Ð¾Ð¼ÐµÑ€ Ð´Ð¾Ð¿Ð¾Ð»Ð½Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð³Ð¾ Ð¼Ð¾Ð½Ð¸Ñ‚Ð¾Ñ€Ð° Ð´Ð»Ñ� Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_LINES_COUNT = "Number of lines on the scoreboard";//"ÐšÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ñ�Ñ‚Ñ€Ð¾Ðº Ð½Ð° Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_COLS_COUNT = "Number of columns on the scoreboard";//"ÐšÐ¾Ð»Ð¸Ñ‡ÐµÑ�Ñ‚Ð²Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð¾Ð² Ð½Ð° Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_DELAY_VISIBLE = "Minimum display time on the scoreboard";//"ÐœÐ¸Ð½Ð¸Ð¼Ð°Ð»ÑŒÐ½Ð¾Ðµ Ð²Ñ€ÐµÐ¼Ñ� Ð¸Ð½Ð´Ð¸ÐºÐ°Ñ†Ð¸Ð¸ Ð½Ð° Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_FON_IMG = "Background image";//"Ð¤Ð¾Ð½Ð¾Ð²Ð¾Ðµ Ð¸Ð·Ð¾Ð±Ñ€Ð°Ð¶ÐµÐ½Ð¸Ðµ";
    public static final String TAG_BOARD_FONT_SIZE = "Font size";//"Ð Ð°Ð·Ð¼ÐµÑ€ ÑˆÑ€Ð¸Ñ„Ñ‚Ð°";
    public static final String TAG_BOARD_FONT_COLOR = "Font Color";//"Ð¦Ð²ÐµÑ‚ ÑˆÑ€Ð¸Ñ„Ñ‚Ð°";
    public static final String TAG_BOARD_PANEL_SIZE = "The size";//"Ð Ð°Ð·Ð¼ÐµÑ€";
    public static final String TAG_BOARD_RUNNING_TEXT = "Running text";//"Ð‘ÐµÐ³ÑƒÑ‰Ð¸Ð¹ Ñ‚ÐµÐºÑ�Ñ‚";
    public static final String TAG_BOARD_VIDEO_FILE = "Video file";//"Ð’Ð¸Ð´ÐµÐ¾Ñ„Ð°Ð¹Ð»";
    public static final String TAG_BOARD_VISIBLE_PANEL = "Visible";//"visible";
    public static final String TAG_BOARD_SPEED_TEXT = "Speed â€‹â€‹of the running text";//"Ð¡ÐºÐ¾Ñ€Ð¾Ñ�Ñ‚ÑŒ Ð±ÐµÐ³ÑƒÑ‰ÐµÐ³Ð¾ Ñ‚ÐµÐºÑ�Ñ‚Ð°";
    public static final String TAG_BOARD_GRID_NEXT_COLS = "Columns table footprint";//"ÐšÐ¾Ð»Ð¾Ð½ÐºÐ¸ Ñ‚Ð°Ð±Ð» Ñ�Ð»ÐµÐ´";
    public static final String TAG_BOARD_GRID_NEXT_ROWS = "Rows of footprints";//"Ð¡Ñ‚Ñ€Ð¾ÐºÐ¸ Ñ‚Ð°Ð±Ð» Ñ�Ð»ÐµÐ´";
    public static final String TAG_BOARD_SIMPLE_DATE = "Simple date";//"ÐŸÑ€Ð¾Ñ�Ñ‚Ð°Ñ� Ð´Ð°Ñ‚Ð°";
    public static final String TAG_BOARD_GRID_NEXT = "The following table";//"Ð¢Ð°Ð±Ð»Ð¸Ñ†Ð° Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰Ð¸Ñ…";
    public static final String TAG_BOARD_FON_COLOR = "Background color";//"Ð¦Ð²ÐµÑ‚ Ñ„Ð¾Ð½Ð°";
    public static final String TAG_BOARD_FONT_SIZE_CAPTION = "Header font size";//"Ð Ð°Ð·Ð¼ÐµÑ€ ÑˆÑ€Ð¸Ñ„Ñ‚Ð° Ð·Ð°Ð³Ð¾Ð»Ð¾Ð²ÐºÐ°";
    public static final String TAG_BOARD_FONT_NAME = "Font name";//"Font name";
    public static final String TAG_BOARD_FONT_SIZE_LINE = "The font size of the lines";//"Ð Ð°Ð·Ð¼ÐµÑ€ ÑˆÑ€Ð¸Ñ„Ñ‚Ð° Ñ�Ñ‚Ñ€Ð¾Ðº";
    public static final String TAG_BOARD_FONT_COLOR_CAPTION = "Header font color";//"Ð¦Ð²ÐµÑ‚ ÑˆÑ€Ð¸Ñ„Ñ‚Ð° Ð·Ð°Ð³Ð¾Ð»Ð¾Ð²ÐºÐ°";
    public static final String TAG_BOARD_FONT_COLOR_LEFT = "The font color of the left column";//"Ð¦Ð²ÐµÑ‚ ÑˆÑ€Ð¸Ñ„Ñ‚Ð° Ð»ÐµÐ²Ð¾Ð³Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð°";
    public static final String TAG_BOARD_FONT_COLOR_RIGHT = "The font color of the right column";//"Ð¦Ð²ÐµÑ‚ ÑˆÑ€Ð¸Ñ„Ñ‚Ð° Ð¿Ñ€Ð°Ð²Ð¾Ð³Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð°";
    public static final String TAG_BOARD_FONT_COLOR_LINE = "Color of the line of the score line";//"Ð¦Ð²ÐµÑ‚ Ð½Ð°Ð´Ð¿Ð¸Ñ�Ð¸ Ñ�Ñ‚Ñ€Ð¾ÐºÐ¸ Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_LINE_BORDER = "Rim Edging";//"ÐžÐºÐ°Ð½Ñ‚Ð¾Ð²ÐºÐ° Ñ�Ñ‚Ñ€Ð¾Ðº";
    public static final String TAG_BOARD_LINE_DELIMITER = "Column separator";//"Ð Ð°Ð·Ð´ÐµÐ»Ð¸Ñ‚ÐµÐ»ÑŒ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð¾Ð²";
    public static final String TAG_BOARD_LEFT_PIC = "Left column pic";//"Left column pic";
    public static final String TAG_BOARD_RIGHT_PIC = "Right column pic";//"Right column pic";
    public static final String TAG_BOARD_EXT_PIC = "Ext column pic";//"Ext column pic";
    public static final String TAG_BOARD_LEFT_CAPTION = "Header of the left column";//"Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð»ÐµÐ²Ð¾Ð³Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð°";
    public static final String TAG_BOARD_RIGHT_CAPTION = "Title of the right column";//"Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð¿Ñ€Ð°Ð²Ð¾Ð³Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð°";
    public static final String TAG_BOARD_EXT_CAPTION = "Additional column header";//"Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð´Ð¾Ð¿Ð¾Ð»Ð½Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð³Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð°";
    public static final String TAG_BOARD_EXT_POSITION = "The order of the additional column";//"ÐŸÐ¾Ñ€Ñ�Ð´Ð¾Ðº Ð´Ð¾Ð¿Ð¾Ð»Ð½Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð³Ð¾ Ñ�Ñ‚Ð¾Ð»Ð±Ñ†Ð°";
    public static final String TAG_BOARD_GRID_NEXT_CAPTION = "The title of the following table";//"Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ñ‚Ð°Ð±Ð»Ð¸Ñ†Ñ‹ Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰Ð¸Ñ…";
    public static final String TAG_BOARD_GRID_NEXT_FRAME_BORDER = "The table of the following";//"Ð Ð°Ð¼ÐºÐ° Ñ‚Ð°Ð±Ð»Ð¸Ñ†Ñ‹ Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰Ð¸Ñ…";
    public static final String TAG_BOARD_LINE_COLOR = "Border color of the score line";//"Ð¦Ð²ÐµÑ‚ Ñ€Ð°Ð¼ÐºÐ¸ Ñ�Ñ‚Ñ€Ð¾ÐºÐ¸ Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_LINE_CAPTION = "The inscription of the score line";//"Ð�Ð°Ð´Ð¿Ð¸Ñ�ÑŒ Ñ�Ñ‚Ñ€Ð¾ÐºÐ¸ Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TAG_BOARD_CALL_PANEL = "The panel called";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾";
    public static final String TAG_BOARD_CALL_PANEL_BACKGROUND = "Picture of the panel called";//"ÐšÐ°Ñ€Ñ‚Ð¸Ð½ÐºÐ° Ð¿Ð°Ð½ÐµÐ»Ð¸ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾";
    public static final String TAG_BOARD_CALL_PANEL_X = "Panel called-X";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾-X";
    public static final String TAG_BOARD_CALL_PANEL_Y = "Panel called-Y";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾-Y";
    public static final String TAG_BOARD_CALL_PANEL_WIDTH = "Panel called-width";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾-ÑˆÐ¸Ñ€Ð¸Ð½Ð°";
    public static final String TAG_BOARD_CALL_PANEL_HEIGHT = "Panel called-height";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾-Ð²Ñ‹Ñ�Ð¾Ñ‚Ð°";
    public static final String TAG_BOARD_CALL_PANEL_DELAY = "Call panel-show time";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾-Ð²Ñ€ÐµÐ¼Ñ� Ð¿Ð¾ÐºÐ°Ð·Ð° Ñ�ÐµÐº";
    public static final String TAG_BOARD_CALL_PANEL_TEMPLATE = "The panel of the called-text html + ###";//"ÐŸÐ°Ð½ÐµÐ»ÑŒ Ð²Ñ‹Ð·Ð²Ð°Ð½Ð½Ð¾Ð³Ð¾-Ñ‚ÐµÐºÑ�Ñ‚ html+###";
    // Ð¸Ð¼ÐµÐ½Ð° Ñ‚ÐµÐ³Ð¾Ð²-Ñ€Ð°Ð·Ð´ÐµÐ»Ð¾Ð² Ð´Ð»Ñ� Ñ‚Ð°Ð±Ð»Ð¾
    // Names of tag-sections for the scoreboard
    //Ð¸Ð¼ÐµÐ½Ð° Ñ‚ÐµÐ³Ð¾Ð²-Ñ€Ð°Ð·Ð´ÐµÐ»Ð¾Ð² Ð´Ð»Ñ� Ñ‚Ð°Ð±Ð»Ð¾
    public static final String TAG_BOARD = "Board";
    public static final String TAG_BOARD_MAIN = "Main";
    public static final String TAG_BOARD_TOP = "Top";
    public static final String TAG_BOARD_BOTTOM = "Bottom";
    public static final String TAG_BOARD_BOTTOM_2 = "Bottom2";
    public static final String TAG_BOARD_LEFT = "Left";
    public static final String TAG_BOARD_RIGHT = "Right";
    // Ð�Ð°Ð¸Ð¼ÐµÐ½Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ð¾Ð² ÐºÐ¾Ð½Ñ„Ð¸Ð³ÑƒÑ€Ð°Ñ†Ð¸Ð¾Ð½Ð½Ñ‹Ñ… Ñ„Ð°Ð¹Ð»Ð¾Ð² Ð³Ð»Ð°Ð²Ð½Ñ‹Ñ… Ñ‚Ð°Ð±Ð»Ð¾
    // Names of parameters of configuration files of the main scoreboard
    public static final String BOARD_VALUE_PAUSE = "The time of the presence of a record on the scoreboard";//"Ð’Ñ€ÐµÐ¼Ñ� Ð¿Ñ€Ð¸Ñ�ÑƒÑ‚Ñ�Ñ‚Ð²Ð¸Ñ� Ð·Ð°Ð¿Ð¸Ñ�Ð¸ Ð½Ð° Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String BOARD_ADRESS_MAIN_BOARD = "Address of the main board of the system";// "Ð�Ð´Ñ€ÐµÑ� Ð³Ð»Ð°Ð²Ð½Ð¾Ð³Ð¾ Ñ‚Ð°Ð±Ð»Ð¾ Ñ�Ð¸Ñ�Ñ‚ÐµÐ¼Ñ‹";
    public static final int BOARD_TYPE_INT = 1;
    public static final int BOARD_TYPE_DOUBLE = 2;
    public static final int BOARD_TYPE_STR = 3;
    public static final int BOARD_TYPE_BOOL = 4;
    // Ð�Ð°Ð¸Ð¼ÐµÐ½Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð·Ð°Ð´Ð°Ð½Ð¸Ð¹
    public static final String TASK_FOR_ALL_SITE = "Ð”Ð»Ñ� Ð²Ñ�ÐµÑ… Ñ�Ð°Ð¹Ñ‚Ð¾Ð² Ð´Ð¾Ð¼ÐµÐ½Ð°";
    public static final String TASK_STAND_IN = "ÐŸÐ¾Ñ�Ñ‚Ð°Ð²Ð¸Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ";
    public static final String TASK_STAND_COMPLEX = "ÐŸÐ¾Ñ�Ñ‚Ð°Ð²Ð¸Ñ‚ÑŒ Ð² Ð½ÐµÑ�ÐºÐ¾Ð»ÑŒÐºÐ¾ Ð¾Ñ‡ÐµÑ€ÐµÐ´ÐµÐ¹";
    public static final String TASK_ADVANCE_STAND_IN = "ÐŸÐ¾Ñ�Ñ‚Ð°Ð²Ð¸Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾";
    public static final String TASK_ADVANCE_CHECK_AND_STAND = "ÐŸÐ¾Ñ�Ñ‚Ð°Ð²Ð¸Ñ‚ÑŒ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ð¾Ð³Ð¾";
    public static final String TASK_REMOVE_ADVANCE_CUSTOMER = "Ð£Ð´Ð°Ð»Ð¸Ñ‚ÑŒ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ð·Ð°Ð¿Ð¸Ñ�Ð°Ð½Ð½Ð¾Ð³Ð¾";
    public static final String TASK_REDIRECT_CUSTOMER = "ÐŸÐµÑ€ÐµÐ°Ð´Ñ€ÐµÑ�Ð¾Ð²Ð°Ñ‚ÑŒ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð° Ðº Ð´Ñ€ÑƒÐ³Ð¾Ð¹ ÑƒÑ�Ð»ÑƒÐ³Ðµ";
    public static final String TASK_GET_SERVICES = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¿ÐµÑ€ÐµÑ‡ÐµÐ½ÑŒ ÑƒÑ�Ð»ÑƒÐ³";
    public static final String TASK_ABOUT_SERVICE = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¾Ð¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ ÑƒÑ�Ð»ÑƒÐ³Ð¸";
    public static final String TASK_GET_SERVICE_CONSISANCY = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ ÑƒÑ�Ð»ÑƒÐ³Ð¸";
    public static final String TASK_ABOUT_SERVICE_PERSON_LIMIT = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ð¾Ñ�Ñ‚ÑŒ Ð²Ñ�Ñ‚Ð°Ñ‚ÑŒ Ñ� Ñ�Ñ‚Ð¸Ð¼Ð¸ Ð´Ð°Ð½Ð½Ñ‹Ð¼Ð¸";
    public static final String TASK_GET_SERVICE_PREINFO = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¸Ð½Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸ÑŽ Ð¿Ð¾ ÑƒÑ�Ð»ÑƒÐ³Ðµ";
    public static final String TASK_GET_INFO_PRINT = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¸Ð½Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸ÑŽ Ð´Ð»Ñ� Ð¿ÐµÑ‡Ð°Ñ‚Ð¸";
    public static final String TASK_GET_USERS = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¿ÐµÑ€ÐµÑ‡ÐµÐ½ÑŒ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¹";
    public static final String TASK_GET_SELF = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¾Ð¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»Ñ�";
    public static final String TASK_GET_SELF_SERVICES = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ Ð¾Ñ‡ÐµÑ€ÐµÐ´ÐµÐ¹";
    public static final String TASK_GET_POSTPONED_POOL = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ Ð¿ÑƒÐ»Ð° Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ñ‹Ñ…";
    public static final String TASK_GET_BAN_LIST = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð¿Ð¸Ñ�Ð¾Ðº Ð·Ð°Ð±Ð°Ð½ÐµÐ½Ñ‹Ñ…";
    public static final String TASK_INVITE_POSTPONED = "Ð’Ñ‹Ð·Ð²Ð°Ñ‚ÑŒ Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ð¾Ð³Ð¾ Ð¸Ð· Ð¿ÑƒÐ»Ð° Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ñ‹Ñ…";
    public static final String TASK_GET_SELF_SERVICES_CHECK = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ Ð¾Ñ‡ÐµÑ€ÐµÐ´ÐµÐ¹ Ñ� Ð¿Ñ€Ð¾Ð²ÐµÑ€ÐºÐ¾Ð¹";
    public static final String TASK_INVITE_NEXT_CUSTOMER = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰ÐµÐ³Ð¾ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð°";
    public static final String TASK_KILL_NEXT_CUSTOMER = "Ð£Ð´Ð°Ð»Ð¸Ñ‚ÑŒ Ñ�Ð»ÐµÐ´ÑƒÑŽÑ‰ÐµÐ³Ð¾ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð°";
    public static final String TASK_CUSTOMER_TO_POSTPON = "ÐšÐ»Ð¸ÐµÐ½Ñ‚Ð° Ð² Ð¿ÑƒÐ» Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ñ‹Ñ…";
    public static final String TASK_POSTPON_CHANGE_STATUS = "Ð¡Ð¼ÐµÐ½Ð¸Ñ‚ÑŒ Ñ�Ñ‚Ð°Ñ‚ÑƒÑ� Ð¾Ñ‚Ð»Ð¾Ð¶ÐµÐ½Ð½Ð¾Ð¼Ñƒ";
    public static final String TASK_START_CUSTOMER = "Ð�Ð°Ñ‡Ð°Ñ‚ÑŒ Ñ€Ð°Ð±Ð¾Ñ‚Ñƒ Ñ� ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð¼";
    public static final String TASK_FINISH_CUSTOMER = "Ð—Ð°ÐºÐ¾Ð½Ñ‡Ð¸Ñ‚ÑŒ Ñ€Ð°Ð±Ð¾Ñ‚Ñƒ Ñ� ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð¼";
    public static final String TASK_I_AM_LIVE = "Ð¯ Ð³Ð¾Ñ€ÐµÑ†!";
    public static final String TASK_RESTART = "RESTART";
    public static final String TASK_RESTART_MAIN_TABLO = "Ð ÐµÑ�Ñ‚Ð°Ñ€Ñ‚ Ð³Ð»Ð°Ð²Ð½Ð¾Ð³Ð¾ Ñ‚Ð²Ð±Ð»Ð¾";
    public static final String TASK_REFRESH_POSTPONED_POOL = "NEW_POSTPONED_NOW";
    public static final String TASK_SERVER_STATE = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ðµ Ñ�ÐµÑ€Ð²ÐµÑ€Ð°";
    public static final String TASK_SET_SERVICE_FIRE = "Ð”Ð¾Ð±Ð°Ð²Ð¸Ñ‚ÑŒ ÑƒÑ�Ð»ÑƒÐ³Ñƒ Ð½Ð° Ð³Ð¾Ñ€Ñ�Ñ‡ÑƒÑŽ";
    public static final String TASK_DELETE_SERVICE_FIRE = "Ð£Ð´Ð°Ð»Ð¸Ñ‚ÑŒ ÑƒÑ�Ð»ÑƒÐ³Ñƒ Ð½Ð° Ð³Ð¾Ñ€Ñ�Ñ‡ÑƒÑŽ";    // Ð�Ð°Ð¸Ð¼ÐµÐ½Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð¾Ñ‚Ñ‡ÐµÑ‚Ð¾Ð², Ñ�Ð´ÐµÑ�ÑŒ Ð¿Ð¸Ñ�Ð°Ñ‚ÑŒ Ð¸Ñ�ÐºÐ»ÑŽÑ‡Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ð¼Ð°Ð»ÐµÐ½ÑŒÐºÐ¸Ð¼Ð¸ Ð»Ð°Ñ‚Ð¸Ð½Ñ�ÐºÐ¸Ð¼Ð¸ Ð±ÑƒÐºÐ²Ð°Ð¼Ð¸ Ð±ÐµÐ· Ð¿Ñ€Ð¾Ð±ÐµÐ»Ð¾Ð²
    public static final String TASK_GET_BOARD_CONFIG = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ ÐºÐ¾Ð½Ñ„Ð¸Ð³ÑƒÑ€Ð°Ñ†Ð¸ÑŽ Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TASK_SAVE_BOARD_CONFIG = "Ð¡Ð¾Ñ…Ñ€Ð°Ð½Ð¸Ñ‚ÑŒ ÐºÐ¾Ð½Ñ„Ð¸Ð³ÑƒÑ€Ð°Ñ†Ð¸ÑŽ Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TASK_GET_GRID_OF_WEEK = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð½ÐµÐ´ÐµÐ»ÑŒÐ½ÑƒÑŽ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½ÑƒÑŽ Ñ‚Ð°Ð±Ð»Ð¸Ñ†Ñƒ";
    public static final String TASK_GET_GRID_OF_DAY = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð´Ð½ÐµÐ²Ð½ÑƒÑŽ Ð¿Ñ€ÐµÐ´Ð²Ð°Ñ€Ð¸Ñ‚ÐµÐ»ÑŒÐ½ÑƒÑŽ Ñ‚Ð°Ð±Ð»Ð¸Ñ†Ñƒ";
    public static final String TASK_GET_INFO_TREE = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¸Ð½Ñ„Ð¾Ñ€Ð¼Ð°Ñ†Ð¸Ð¾Ð½Ð½Ð¾Ðµ Ð´ÐµÑ€ÐµÐ²Ð¾";
    public static final String TASK_GET_RESULTS_LIST = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð¿Ð¾Ð»ÑƒÑ‡ÐµÐ½Ð¸Ðµ Ñ�Ð¿Ð¸Ñ�ÐºÐ° Ð²Ð¾Ð·Ð¼Ð¾Ð¶Ð½Ñ‹Ñ… Ñ€ÐµÐ·ÑƒÐ»ÑŒÑ‚Ð°Ñ‚Ð¾Ð²";
    public static final String TASK_GET_RESPONSE_LIST = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ñ�Ð¿Ð¸Ñ�Ð¾Ðº Ð¾Ñ‚Ð·Ñ‹Ð²Ð¾Ð²";
    public static final String TASK_SET_RESPONSE_ANSWER = "ÐžÑ�Ñ‚Ð°Ð²Ð¸Ñ‚ÑŒ Ð¾Ñ‚Ð·Ñ‹Ð²";
    public static final String REPORT_CURRENT_USERS = "current_users";
    public static final String REPORT_CURRENT_SERVICES = "current_services";
    public static final String TASK_GET_CLIENT_AUTHORIZATION = "Ð˜Ð´ÐµÐ½Ñ‚Ð¸Ñ„Ð¸Ñ†Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÑŒ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð°";
    public static final String TASK_SET_CUSTOMER_PRIORITY = "Ð˜Ð·Ð¼ÐµÐ½Ð¸Ñ‚ÑŒ Ð¿Ñ€Ð¸Ð¾Ñ€Ð¸Ñ‚ÐµÑ‚";
    public static final String TASK_CHECK_CUSTOMER_NUMBER = "ÐŸÑ€Ð¾Ð²ÐµÑ€Ð¸Ñ‚ÑŒ Ð½Ð¾Ð¼ÐµÑ€";
    public static final String TASK_CHANGE_FLEX_PRIORITY = "Ð˜Ð·Ð¼ÐµÐ½Ð¸Ñ‚ÑŒ Ð³Ð¸Ð±ÐºÐ¸Ð¹ Ð¿Ñ€Ð¸Ð¾Ñ€Ð¸Ñ‚ÐµÑ‚";
    public static final String TASK_CHANGE_RUNNING_TEXT_ON_BOARD = "Ð˜Ð·Ð¼ÐµÐ½Ð¸Ñ‚ÑŒ Ð±ÐµÐ³ÑƒÑ‰Ð¸Ð¹ Ñ‚ÐµÐºÑ�Ñ‚ Ð½Ð° Ñ‚Ð°Ð±Ð»Ð¾";
    public static final String TASK_CHANGE_TEMP_AVAILABLE_SERVICE = "Ð˜Ð·Ð¼ÐµÐ½Ð¸Ñ‚ÑŒ Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½ÑƒÑŽ Ð´Ð¾Ñ�Ñ‚ÑƒÐ¿Ð½Ð¾Ñ�Ñ‚ÑŒ";
    public static final String TASK_GET_STANDARDS = "ÐŸÐ¾Ð»ÑƒÑ‡Ð¸Ñ‚ÑŒ Ð½Ð¾Ñ€Ð¼Ð°Ñ‚Ð¸Ð²Ñ‹";
    public static final String TASK_SET_BUSSY = "ÐŸÐµÑ€ÐµÑ€Ñ‹Ð² Ð¾Ð¿ÐµÑ€Ð°Ñ‚Ð¾Ñ€Ð°";
    public static final String TASK_GET_PROPERTIES = "Ð’Ñ�Ðµ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ñ‹ Ð¸Ð· Ð‘Ð”";
    public static final String TASK_SAVE_PROPERTIES = "Ð¡Ð¾Ñ…Ñ€Ð°Ð½Ð¸Ñ‚ÑŒ Ð²Ñ�Ðµ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ñ‹ Ð² Ð‘Ð”";
    public static final String TASK_INIT_PROPERTIES = "Ð¡Ð¾Ð·Ð´Ð°Ñ‚ÑŒ Ð²Ñ�Ðµ Ð¿Ð°Ñ€Ð°Ð¼ÐµÑ‚Ñ€Ñ‹ Ð² Ð‘Ð”";
    public static final String TASK_SERVE_CUSTOMER = "serve_customer";
    public static final String TASK_INVITE_SELECTED_CUSTOMER = "Invite selected customer";
    public static final String TASK_CHANGE_SERVICE = "Change the service of the customer";
    public static final String TASK_CUSTOMER_RETURN_QUEUE = "Return the same customer to queue";
    // Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð¾Ñ‚Ñ‡ÐµÑ‚Ð¾Ð²
    public static final String REPORT_FORMAT_HTML = "html";
    public static final String REPORT_FORMAT_RTF = "rtf";
    public static final String REPORT_FORMAT_PDF = "pdf";
    public static final String REPORT_FORMAT_XLSX = "xlsx";
    public static final String REPORT_FORMAT_CSV = "csv";
    // Ð¯ÐºÐ¾Ñ€ÑŒ Ð´Ð»Ñ� Ñ�Ð¿Ð¸Ñ�ÐºÐ° Ð°Ð½Ð°Ð»Ð¸Ñ‚Ð¸Ñ‡ÐµÑ�ÐºÐ¸Ñ… Ð¾Ñ‚Ñ‡ÐµÑ‚Ð¾Ð²
    public static final String ANCHOR_REPORT_LIST = "<tr><td><center>#REPORT_LIST_ANCHOR#</center></td></tr>";
    public static final String ANCHOR_DATA_FOR_REPORT = "#DATA_FOR_REPORT#";
    public static final String ANCHOR_ERROR_INPUT_DATA = "#ERROR_INPUT_DATA#";
    public static final String ANCHOR_USERS_FOR_REPORT = "#USERS_LIST_ANCHOR#";
    public static final String ANCHOR_PROJECT_NAME_FOR_REPORT = "#PROJECT_NAME_ANCHOR#";
    public static final String ANCHOR_COOCIES = "#COOCIES_ANCHOR#";
    // Ð—Ð°Ð´Ð°Ð½Ð¸Ñ� Ð´Ð»Ñ� Ð¿ÑƒÐ½ÐºÑ‚Ð° Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸
    public static final String WELCOME_LOCK = "#WELCOME_LOCK#";
    public static final String WELCOME_UNLOCK = "#WELCOME_UNLOCK#";
    public static final String WELCOME_OFF = "#WELCOME_OFF#";
    public static final String WELCOME_REINIT = "#WELCOME_REINIT#";
    public final static String[] RUSSIAN_MONAT = {
        "Ð¯Ð½Ð²Ð°Ñ€Ñ�",
        "Ð¤ÐµÐ²Ñ€Ð°Ð»Ñ�",
        "ÐœÐ°Ñ€Ñ‚Ð°",
        "Ð�Ð¿Ñ€ÐµÐ»Ñ�",
        "ÐœÐ°Ñ�",
        "Ð˜ÑŽÐ½Ñ�",
        "Ð˜ÑŽÐ»Ñ�",
        "Ð�Ð²Ð³ÑƒÑ�Ñ‚Ð°",
        "Ð¡ÐµÐ½Ñ‚Ñ�Ð±Ñ€Ñ�",
        "ÐžÐºÑ‚Ñ�Ð±Ñ€Ñ�",
        "Ð�Ð¾Ñ�Ð±Ñ€Ñ�",
        "Ð”ÐµÐºÐ°Ð±Ñ€Ñ�"
    };
    public final static String[] UKRAINIAN_MONAT = {
        "Ð¡Ñ–Ñ‡Ð½Ñ�",
        "Ð›ÑŽÑ‚Ð¾Ð³Ð¾",
        "Ð‘ÐµÑ€ÐµÐ·Ð½Ñ�",
        "ÐšÐ²Ñ–Ñ‚Ð½Ñ�",
        "Ð¢Ñ€Ð°Ð²Ð½Ñ�",
        "Ð§ÐµÑ€Ð²Ð½Ñ�",
        "Ð›Ð¸Ð¿Ð½Ñ�",
        "Ð¡ÐµÑ€Ð¿Ð½Ñ�",
        "Ð’ÐµÑ€ÐµÑ�Ð½Ñ�",
        "Ð–Ð¾Ð²Ñ‚Ð½Ñ�",
        "Ð›Ð¸Ñ�Ñ‚Ð¾Ð¿Ð°Ð´Ð°",
        "Ð“Ñ€ÑƒÐ´Ð½Ñ�"
    };
    public final static String[] AZERBAIJAN_MONAT = {"Yanvar",
        "Fevral",
        "Mart",
        "Aprel",
        "May",
        "Iyun",
        "Iyul",
        "Avqust",
        "Sentyabr",
        "Oktyabr",
        "Noyabr",
        "Dekabr"};
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹
     */
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹ Ð±ÐµÐ· Ð²Ñ€ÐµÐ¼ÐµÐ½Ð¸
     */
    public static final String DATE_FORMAT_ONLY = "dd.MM.yyyy";
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹.
     */
    public final static DateFormat FORMAT_HH_MM = new SimpleDateFormat("HH:mm");
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹.
     */
    public final static DateFormat FORMAT_HH_MM_SS = new SimpleDateFormat("hh:mm:ss aa");
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹.
     */
    public final static DateFormat FORMAT_DD_MM_YYYY = new SimpleDateFormat(DATE_FORMAT_ONLY);
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹.
     */
    public final static DateFormat FORMAT_DD_MM_YYYY_TIME = new SimpleDateFormat(DATE_FORMAT);
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹./2009-01-26 16:10:41
     */
    public final static DateFormat FORMAT_FOR_REP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Ð¤Ð¾Ñ€Ð¼Ð°Ñ‚ Ð´Ð°Ñ‚Ñ‹./2009-01-26 16:10
     */
    public final static DateFormat FORMAT_FOR_TRANS = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * Ð’Ñ€ÐµÐ¼ÐµÐ½Ð½Ð°Ñ� Ð¿Ð°Ð¿ÐºÐ° Ð´Ð»Ñ� Ñ„Ð°Ð¹Ð»Ð¾Ð² Ñ�Ð¾Ñ…Ñ€Ð°Ð½ÐµÐ½Ð¸Ñ� Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ñ� Ð´Ð»Ñ� Ð¿Ð¾Ð¼ÐµÑ…Ð¾ÑƒÑ�Ñ‚Ð¾Ð¹Ñ‡Ð¸Ð²Ð¾Ñ�Ñ‚Ð¸
     */
    public static final String TEMP_FOLDER = "temp";
    /**
     * Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ñ‹Ð¹ Ñ„Ð°Ð¹Ð» Ñ�Ð¾Ñ…Ñ€Ð°Ð½ÐµÐ½Ð¸Ñ� Ñ�Ð¾Ñ�Ñ‚Ð¾Ñ�Ð½Ð¸Ñ� Ð´Ð»Ñ� Ð¿Ð¾Ð¼ÐµÑ…Ð¾ÑƒÑ�Ñ‚Ð¾Ð¹Ñ‡Ð¸Ð²Ð¾Ñ�Ñ‚Ð¸
     */
    public static final String TEMP_STATE_FILE = "temp.json";
    /**
     * Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ñ‹Ð¹ Ñ„Ð°Ð¹Ð» Ñ�Ð¾Ñ…Ñ€Ð°Ð½ÐµÐ½Ð¸Ñ� ÐºÐ¾Ð½Ñ„Ð¸Ð³ÑƒÑ€Ð°Ñ†Ð¸Ð¸ ÐºÐ¾Ð¼Ð¿Ð»ÐµÐºÑ�Ð½Ñ‹Ñ… ÑƒÑ�Ð»ÑƒÐ³
     */
    public static final String TEMP_COMPLEX_FILE = "complex.json";
    /**
     * Ð²Ñ€ÐµÐ¼ÐµÐ½Ð½Ñ‹Ð¹ Ñ„Ð°Ð¹Ð» Ñ�Ð¾Ñ…Ñ€Ð°Ð½ÐµÐ½Ð¸Ñ� Ñ‚ÐµÐºÑƒÑ‰ÐµÐ¹ Ñ�Ñ‚Ð°Ñ‚Ð¸Ñ�Ñ‚Ð¸ÐºÐ¸ Ð´Ð»Ñ� Ð¿Ð¾Ð¼ÐµÑ…Ð¾ÑƒÑ�Ñ‚Ð¾Ð¹Ñ‡Ð¸Ð²Ð¾Ñ�Ñ‚Ð¸
     */
    public static final String TEMP_STATATISTIC_FILE = "temp_statistic.xml";
    /**
     * Ð—Ð°Ð´ÐµÑ€Ð¶ÐºÐ° Ð¿ÐµÑ€ÐµÐ´ Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‚Ð¾Ð¼ Ð² ÐºÐ¾Ñ€ÐµÐ½ÑŒ Ð¼ÐµÐ½ÑŽ Ð¿Ñ€Ð¸ Ð²Ð¾Ð¿Ñ€Ð¾Ñ�Ðµ "Ð–ÐµÐ»Ð°ÐµÑ‚Ðµ Ð²Ñ�Ñ‚Ð°Ñ‚ÑŒ Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´ÑŒ?" ÐºÐ¾Ð³Ð´Ð° Ð²
     * Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸ Ð±Ð¾Ð»ÐµÐµ Ñ‚Ñ€ÐµÑ… Ñ‡ÐµÐ»Ð¾Ð²ÐµÐº.
     */
    public static final int DELAY_BACK_TO_ROOT = 10000;
    /**
     * Ð—Ð°Ð´ÐµÑ€Ð¶ÐºÐ° Ð¿ÐµÑ€ÐµÐ´ Ð¿Ñ€Ð¾Ð²ÐµÑ€ÐºÐ¾Ð¹ Ð±Ð»Ð¾ÐºÐ¸Ñ€Ð¾Ð²Ð°Ð½Ð¸Ñ� Ð¿ÑƒÐ½ÐºÑ‚Ð° Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸
     */
    public static final int DELAY_CHECK_TO_LOCK = 55000;
    /**
     * ÐšÐ¾Ð½Ñ�Ñ‚Ð°Ð½Ñ‚Ð° Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‚Ð° Ð² Ð¿ÑƒÐ½ÐºÑ‚ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ ÐºÐ¾Ð»-Ð²Ð¾ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð² Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸, Ð² Ñ�Ð»ÑƒÑ‡Ð°Ðµ ÐµÑ�Ð»Ð¸ ÑƒÑ�Ð»ÑƒÐ³Ð° Ð½Ðµ
     * Ð¾Ð±Ñ€Ð°Ð±Ð°Ñ‚Ñ‹Ð²Ð°ÐµÑ‚Ñ�Ñ� Ð½Ð¸ Ð¾Ð´Ð½Ð¸Ð¼ Ð¿Ð¾Ð»ÑŒÐ·Ð¾Ð²Ð°Ñ‚ÐµÐ»ÐµÐ¼
     */
    public static final int LOCK_INT = 1000000000;
    /**
     * ÐšÐ¾Ð½Ñ�Ñ‚Ð°Ð½Ñ‚Ð° Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‚Ð° Ð² Ð¿ÑƒÐ½ÐºÑ‚ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ ÐºÐ¾Ð»-Ð²Ð¾ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð² Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸, Ð² Ñ�Ð»ÑƒÑ‡Ð°Ðµ ÐµÑ�Ð»Ð¸ ÑƒÑ�Ð»ÑƒÐ³Ð° Ð½Ðµ
     * Ð¾ÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÑ‚Ñ�Ñ� ÑƒÑ‡Ð¸Ñ‚Ñ‹Ð²Ð°Ñ� Ñ€Ð°Ñ�Ð¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ
     */
    public static final int LOCK_FREE_INT = 1000000011;
    /**
     * ÐšÐ¾Ð½Ñ�Ñ‚Ð°Ð½Ñ‚Ð° Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‚Ð° Ð² Ð¿ÑƒÐ½ÐºÑ‚ Ñ€ÐµÐ³Ð¸Ñ�Ñ‚Ñ€Ð°Ñ†Ð¸Ð¸ ÐºÐ¾Ð»-Ð²Ð¾ ÐºÐ»Ð¸ÐµÐ½Ñ‚Ð¾Ð² Ð² Ð¾Ñ‡ÐµÑ€ÐµÐ´Ð¸, Ð² Ñ�Ð»ÑƒÑ‡Ð°Ðµ ÐµÑ�Ð»Ð¸ ÑƒÑ�Ð»ÑƒÐ³Ð° Ð½Ðµ
     * Ð¾ÐºÐ°Ð·Ñ‹Ð²Ð°ÐµÑ‚Ñ�Ñ� ÑƒÑ‡Ð¸Ñ‚Ñ‹Ð²Ð°Ñ� Ð¾Ð³Ñ€Ð°Ð½Ð¸Ñ‡ÐµÐ½Ð¸Ðµ Ð¿Ð¾Ñ�ÐµÑ‰ÐµÐ½Ð¸Ð¹ Ð² Ð´ÐµÐ½ÑŒ Ð¸ Ð»Ð¸Ð¼Ð¸Ñ‚ Ð´Ð¾Ñ�Ñ‚Ð¸Ð³Ð½ÑƒÑ‚
     */
    public static final int LOCK_PER_DAY_INT = 1000000022;
    /**
     * Ð’Ð¾Ð¿Ñ€Ð¾Ñ� Ð¾ Ð¶Ð¸Ð²Ð¾Ñ�Ñ‚Ð¸
     */
    public static final String HOW_DO_YOU_DO = "do you live?";
    /**
     * mointors
     */
    public static final HashMap<Integer, Rectangle> DISPLAYS = new HashMap<>();
    private static final LinkedHashMap<Integer, String> PRIORITYS_WORD = new LinkedHashMap<>();
    private static final HashMap<String, Image> HASH_IMG = new HashMap<>();
    public static TimeZone userTimeZone;
    public static GraphicsDevice firstMonitor = null;
    static ServerSocket stopStartSecond;
    private static boolean sh = false;
    private static ResourceMap localeMap = null;

    static {
        /**
         * Ð˜Ð½Ð¸Ñ†Ð¸Ð°Ð»Ð¸Ð·Ð°Ñ†Ð¸Ñ�
         */
        GraphicsDevice[] screenDevices = null;
        try {
            screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        } catch (HeadlessException th) {
            System.out.println("No screen Devices");
        }
        if (screenDevices != null && screenDevices.length > 0) {
            firstMonitor = screenDevices[0];
            int i = 1;
            for (GraphicsDevice graphicsDevice : screenDevices) {
                System.out.println(
                    "monitor " + i + "; graphicsDevice = " + graphicsDevice.getIDstring() + " "
                        + graphicsDevice.toString()
                        + "; height, width = " + graphicsDevice.getDefaultConfiguration()
                        .getBounds().height
                        + "x" + graphicsDevice.getDefaultConfiguration().getBounds().width
                        + "; Coloreness = " + graphicsDevice.getDisplayMode().getBitDepth()
                        + "; RefreshRate = " + graphicsDevice.getDisplayMode().getRefreshRate()
                        + "; Origin(x, y) = " + graphicsDevice.getDefaultConfiguration()
                        .getBounds().x
                        + "-" + graphicsDevice.getDefaultConfiguration().getBounds().y);
                DISPLAYS.put(i++, graphicsDevice.getDefaultConfiguration().getBounds());
                if (graphicsDevice.getDefaultConfiguration().getBounds().x == 0
                    && graphicsDevice.getDefaultConfiguration().getBounds().y == 0) {
                    firstMonitor = graphicsDevice;
                }
            }
        }
        COEFF_WORD.put(SERVICE_REMAINS, FServicePriority.getLocaleMessage("service.priority.low"));
        COEFF_WORD.put(SERVICE_NORMAL, FServicePriority.getLocaleMessage("service.priority.basic"));
        COEFF_WORD.put(SERVICE_VIP, FServicePriority.getLocaleMessage("service.priority.vip"));
    }

    public static LinkedHashMap<Integer, String> get_PRIORITYS_WORD() {
        PRIORITYS_WORD.put(PRIORITY_LOW, FServicePriority.getLocaleMessage("client.priority.low"));
        PRIORITYS_WORD
            .put(PRIORITY_NORMAL, FServicePriority.getLocaleMessage("client.priority.standard"));
        PRIORITYS_WORD.put(PRIORITY_HI, FServicePriority.getLocaleMessage("client.priority.hi"));
        PRIORITYS_WORD.put(PRIORITY_VIP, FServicePriority.getLocaleMessage("client.priority.vip"));
        return PRIORITYS_WORD;
    }

    public static LinkedHashMap<Integer, String> get_COEFF_WORD() {
        COEFF_WORD.put(SERVICE_REMAINS, FServicePriority.getLocaleMessage("service.priority.low"));
        COEFF_WORD.put(SERVICE_NORMAL, FServicePriority.getLocaleMessage("service.priority.basic"));
        int n = 0;
        if (QConfig.cfg().isAdminApp()) {
            n = ServerProps.getInstance().getProps().getExtPriorNumber();
        }
        if (QConfig.cfg().isClient()) {
            n = FClient.extPriorClient;
        }
        for (int i = 2; i <= n + 1; i++) {
            COEFF_WORD.put(i, Integer.toString(i));
        }
        COEFF_WORD.put(SERVICE_VIP + n, FServicePriority.getLocaleMessage("service.priority.vip"));

        return COEFF_WORD;
    }

    public static String getRusDate(Date date, String format) {
        return new SimpleDateFormat(format, Locales.getInstance().getRussSymbolDateFormat())
            .format(date);
    }

    public static String getUkrDate(Date date, String format) {
        return new SimpleDateFormat(format, Locales.getInstance().getUkrSymbolDateFormat())
            .format(date);
    }

    /**
     * Ð ÐµÐºÑƒÑ€ÐµÐ½Ñ‚Ð½Ñ‹Ð¹ Ñ„Ð¾Ñ€Ð¼Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÐµÐ»ÑŒ Ð´Ð»Ñ� public static ArrayList elements(Element root, String
     * tagName).
     *
     * @param list Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð²
     * @param el ÐºÐ¾Ñ€Ð½ÐµÐ²Ð¾Ð¹ Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚ Ð²ÐµÑ‚Ð²Ð¸
     * @param tagName Ð¸Ð¼Ñ� Ð¸Ñ�ÐºÐ¾Ð¼Ñ‹Ñ… ÑƒÐ·Ð»Ð¾Ð²
     */
    private static void getList(ArrayList list, Element el, String tagName) {
        list.addAll(el.elements(tagName));
        el.elements().stream().forEach((obj) -> {
            getList(list, (Element) obj, tagName);
        });
    }

    /**
     * Ð’Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð¾Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð² Ñ� Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÐµÐ½Ð½Ñ‹Ð¼ Ð¸Ð¼ÐµÐ½ÐµÐ¼ Ð¸Ð· Ð²ÐµÑ‚Ð²Ð¸
     *
     * @param root ÐºÐ¾Ñ€Ð½ÐµÐ²Ð¾Ð¹ Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚ Ð²ÐµÑ‚Ð²Ð¸
     * @param tagName Ð¸Ð¼Ñ� Ð¸Ñ�ÐºÐ¾Ð¼Ñ‹Ñ… ÑƒÐ·Ð»Ð¾Ð²
     * @return Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð²
     */
    public static ArrayList<Element> elements(Element root, String tagName) {
        ArrayList<Element> list = new ArrayList<>();
        //list.addAll(root.elements(tagName));
        getList(list, root, tagName);
        return list;
    }

    /**
     * Ð ÐµÐºÑƒÑ€ÐµÐ½Ñ‚Ð½Ñ‹Ð¹ Ñ„Ð¾Ñ€Ð¼Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÐµÐ»ÑŒ Ð´Ð»Ñ� public static ArrayList elementsByAttr(...).
     *
     * @param list Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð²
     * @param el ÐºÐ¾Ñ€Ð½ÐµÐ²Ð¾Ð¹ Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚ Ð²ÐµÑ‚Ð²Ð¸
     * @param attrName Ð¸Ð¼Ñ� Ð¸Ñ�ÐºÐ¾Ð¼Ñ‹Ñ… Ð°Ñ‚Ñ€Ð¸Ð±ÑƒÑ‚Ð¾Ð²
     * @param attrValue Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸Ðµ Ð°Ñ‚Ñ€Ð¸Ð±ÑƒÑ‚Ð°
     */
    private static void getList(ArrayList list, Element el, String attrName, String attrValue) {
        if (attrValue.equals(el.attributeValue(attrName))) {
            list.add(el);
        }
        el.elements().stream().forEach((obj) -> {
            getList(list, (Element) obj, attrName, attrValue);
        });
    }

    /**
     * Ð’Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð¾Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð² Ñ� Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÐµÐ½Ð½Ñ‹Ð¼ Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸ÐµÐ¼ Ð°Ñ‚Ñ€Ð¸Ð±ÑƒÑ‚Ð° Ð¸Ð· Ð²ÐµÑ‚Ð²Ð¸
     *
     * @param root ÐºÐ¾Ñ€Ð½ÐµÐ²Ð¾Ð¹ Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚ Ð²ÐµÑ‚Ð²Ð¸
     * @param attrName Ð¸Ð¼Ñ� Ð¸Ñ�ÐºÐ¾Ð¼Ñ‹Ñ… Ð°Ñ‚Ñ€Ð¸Ð±ÑƒÑ‚Ð¾Ð²
     * @param attrValue Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸Ðµ Ð°Ñ‚Ñ€Ð¸Ð±ÑƒÑ‚Ð°
     * @return Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð²
     */
    public static ArrayList<Element> elementsByAttr(Element root, String attrName,
        String attrValue) {
        ArrayList<Element> list = new ArrayList<>();
        //list.addAll(root.elements(tagName));
        getList(list, root, attrName, attrValue);
        return list;
    }

    /**
     * Ð ÐµÐºÑƒÑ€ÐµÐ½Ñ‚Ð½Ñ‹Ð¹ Ñ„Ð¾Ñ€Ð¼Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÐµÐ»ÑŒ Ð´Ð»Ñ� public static ArrayList elementsByAttr(...).
     *
     * @param list Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð²
     * @param el ÐºÐ¾Ñ€Ð½ÐµÐ²Ð¾Ð¹ Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚ Ð²ÐµÑ‚Ð²Ð¸
     * @param text Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸Ðµ CData
     */
    private static void getListCData(ArrayList list, Element el, String text) {
        if (text.equals(el.getTextTrim())) {
            list.add(el);
        }
        el.elements().stream().forEach((obj) -> {
            getListCData(list, (Element) obj, text);
        });
    }

    /**
     * Ð’Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð¾Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð² Ñ� Ð¾Ð¿Ñ€ÐµÐ´ÐµÐ»ÐµÐ½Ð½Ñ‹Ð¼ Ð·Ð½Ð°Ñ‡ÐµÐ½Ð¸ÐµÐ¼ CData Ð¸Ð· Ð²ÐµÑ‚Ð²Ð¸
     *
     * @param root ÐºÐ¾Ñ€Ð½ÐµÐ²Ð¾Ð¹ Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚ Ð²ÐµÑ‚Ð²Ð¸
     * @param text Ñ‚ÐµÐºÑ�Ñ‚ Ð² CData Ð² xml-ÑƒÐ·Ð»Ðµ
     * @return Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ñ�Ð»ÐµÐ¼ÐµÐ½Ñ‚Ð¾Ð²
     */
    public static ArrayList<Element> elementsByCData(Element root, String text) {
        ArrayList<Element> list = new ArrayList<>();
        //list.addAll(root.elements(tagName));
        getListCData(list, root, text);
        return list;
    }

    /**
     * ÐŸÐ¾Ð»ÑƒÑ‡ÐµÐ½Ð¸Ðµ Ð°Ð´Ñ€ÐµÑ�Ð° Ð¸Ð· Ñ�Ñ‚Ñ€Ð¾Ñ‡ÐºÐ¸.
     *
     * @param adress Ñ�Ñ‚Ñ€Ð¾Ñ‡ÐºÐ° Ñ‚Ð¸Ð¿Ð° "125.256.214.854" Ð¸Ð»Ð¸ "rambler.ru"
     * @return InetAddress
     */
    public static InetAddress getInetAddress(String adress) {
        InetAddress adr = null;
        try {
            adr = InetAddress.getByName(adress);
        } catch (UnknownHostException ex) {
            throw new ServerException("Error getting address on line \'" + adress + "\". " + ex);
        }
        return adr;
    }

    /**
     * ÐŸÐ¾Ñ�Ð»Ð°Ñ‚ÑŒ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ðµ Ð¿Ð¾ UDP
     *
     * @param message Ñ‚ÐµÐºÑ�Ñ‚ Ð¿Ð¾Ñ�Ñ‹Ð»Ð°ÐµÐ¼Ð¾Ð³Ð¾ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ñ�
     * @param address Ð°Ð´Ñ€ÐµÑ� Ð¿Ð¾Ð»ÑƒÑ‡Ð°Ñ‚ÐµÐ»Ñ�. Ð•Ñ�Ð»Ð¸ Ð°Ð´Ñ€ÐµÑ� "255.255.255.255", Ñ‚Ð¾ Ñ€Ð°Ñ�Ñ�Ñ‹Ð»ÐºÐ° Ð±ÑƒÐ´ÐµÑ‚
     * ÑˆÐ¸Ñ€Ð¾ÐºÐ¾Ð²ÐµÑ‰Ð°Ñ‚ÐµÐ»ÑŒÐ½Ð¾Ð¹.
     * @param port Ð¿Ð¾Ñ€Ñ‚ Ð¿Ð¾Ð»ÑƒÑ‡Ð°Ñ‚ÐµÐ»Ñ�
     */
    public static void sendUDPMessage(String message, InetAddress address, int port) {
//        QLog.l().logger().trace(
//            " UDP  \"" + message + "\"  \"" + address.getHostAddress()
//                + "\"  \"" + port + "\"");
        final DatagramSocket socket;
        final byte mess_b[] = message.getBytes();
        final DatagramPacket packet = new DatagramPacket(mess_b, mess_b.length, address, port);
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            throw new ServerException("Problems with the UDP socket." + ex.getMessage());
        }
        try {
            socket.send(packet);
        } catch (IOException io) {
            throw new ServerException("Error sending message via UDP. " + io.getMessage());
        } finally {
            socket.close();
        }
    }

    /**
     * ÐŸÐ¾Ñ�Ð»Ð°Ñ‚ÑŒ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ðµ Ð¿Ð¾ UDP ÑˆÐ¸Ñ€Ð¾ÐºÐ¾Ð²ÐµÑ‰Ð°Ñ‚ÐµÐ»ÑŒÐ½Ð¾
     *
     * @param message Ñ‚ÐµÐºÑ�Ñ‚ Ð¿Ð¾Ñ�Ñ‹Ð»Ð°ÐµÐ¼Ð¾Ð³Ð¾ Ñ�Ð¾Ð¾Ð±Ñ‰ÐµÐ½Ð¸Ñ�
     * @param port Ð¿Ð¾Ñ€Ñ‚ Ð¿Ð¾Ð»ÑƒÑ‡Ð°Ñ‚ÐµÐ»Ñ�
     */
    public static void sendUDPBroadcast(String message, int port) {
        try {
            sendUDPMessage(message, InetAddress.getByName("255.255.255.255"), port);
        } catch (UnknownHostException ex) {
            throw new ServerException("Address issues " + ex.getMessage());
        }
    }

    /**
     * Ð—Ð°Ð³Ñ€ÑƒÐ·ÐºÐ° Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð° Ð¸Ð· jar-Ñ„Ð°Ð¹Ð»Ð°
     *
     * @param o - ÐºÐ»Ð°Ñ�Ñ�, Ð½ÑƒÐ¶ÐµÐ½ Ð´Ð»Ñ� Ð¿Ð¾Ð»ÑƒÑ‡ÐµÐ½Ð¸Ñ� Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð°
     * @param resourceName Ð¿ÑƒÑ‚ÑŒ Ðº Ñ€ÐµÑ�ÑƒÑ€Ñ�Ñƒ Ð² jar-Ñ„Ð°Ð¹Ð»Ðµ
     * @return Ð¼Ð°Ñ�Ñ�Ð¸Ð² Ð±Ð°Ð¹Ñ‚, Ñ�Ð¾Ð´ÐµÑ€Ð¶Ð°Ñ‰Ð¸Ð¹ Ñ€ÐµÑ�ÑƒÑ€Ñ�
     */
    public static byte[] readResource(Object o, String resourceName) throws IOException {
        // Ð’Ñ‹Ð´Ð°ÐµÐ¼ Ñ€ÐµÑ�ÑƒÑ€Ñ�  "/ru/apertum/qsystem/reports/web/name.jpg"
        final InputStream inStream = o.getClass().getResourceAsStream(resourceName);
        return readInputStream(inStream);
    }

    /**
     * Ð³Ñ€ÑƒÐ·Ð¸Ñ‚ ÐºÐ°Ñ€Ñ‚Ð¸Ð½ÐºÑƒ Ð¸Ð· Ñ„Ð°Ð¹Ð»Ð° Ð¸Ð»Ð¸ Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð¾Ð². Ð•Ñ�Ð»Ð¸ ÐŸÐ°Ñ€Ð°Ð¼ÐµÑ‚Ñ€ Ð¿ÑƒÑ�Ñ‚Ð¾Ð¹, Ñ‚Ð¾ Ð²Ð¾Ð·Ð²Ñ€Ð°Ñ‰Ð°ÐµÑ‚ null.
     *
     * @param o ÐžÐ±ÑŠÐµÐºÑ‚ Ð´Ð»Ñ� Ð·Ð°Ð³Ñ€ÑƒÐ·ÐºÐ¸ Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð° Ð¸Ð· jar, Ñ‡Ð°Ñ‰Ðµ Ð²Ñ�ÐµÐ³Ð¾ ÐºÐ»Ð°Ñ�Ñ� Ð² ÐºÐ¾Ñ‚Ð¾Ñ€Ð¾Ð¼ Ð¿Ð¾Ð½Ð°Ð´Ð¾Ð±Ð¸Ð»Ð°Ñ�ÑŒ Ñ�Ñ‚Ð°
     * ÐºÐ°Ñ€Ñ‚Ð¸Ð½ÐºÐ°.
     * @param resourceName Ð¿ÑƒÑ‚ÑŒ Ðº Ñ€ÐµÑ�ÑƒÑ€Ñ�Ñƒ Ð¸Ð»Ð¸ Ñ„Ð°Ð¹Ð»Ñƒ ÐºÐ°Ñ€Ñ‚Ð¸Ð½ÐºÐ¸. ÐœÐ¾Ð¶ÐµÑ‚ Ð±Ñ‹Ñ‚ÑŒ Ð¿ÑƒÑ�Ñ‚Ñ‹Ð¼.
     * @param defaultResourceName Ð•Ñ�Ð»Ð¸ Ð½Ð¸Ñ„Ð°Ð¹Ð»Ð° Ð½Ð¸ Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð° Ð½Ðµ Ð½Ð°Ð¹Ð´ÐµÑ‚Ñ�Ñ�, Ñ‚Ð¾ Ð·Ð°Ð³Ñ€ÑƒÐ·Ð¸Ñ‚Ñ�Ñ� Ñ�Ñ‚Ð¾Ñ‚ Ñ€ÐµÑ�ÑƒÑ€Ñ�
     */
    public static Image loadImage(Object o, String resourceName, String defaultResourceName) {
        if ("".equals(resourceName)) {
            return null;
        } else {
            Image img = HASH_IMG.get(resourceName);
            if (img != null) {
                return img;
            }

            final DataInputStream inStream;
            File f = new File(resourceName);
            if (f.exists()) {
                img = new ImageIcon(resourceName).getImage();
                HASH_IMG.put(resourceName, img);
                return img;
            } else {
                final InputStream is = o.getClass().getResourceAsStream(resourceName);
                if (is == null) {
                    if (defaultResourceName == null || defaultResourceName.isEmpty()) {
                        return new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
                    }
                    if (o.getClass().getResourceAsStream(defaultResourceName) == null) {
                        QLog.l().logger().error(
                            "ÐŸÑ€Ð¸ Ð·Ð°Ð³Ñ€ÑƒÐ·ÐºÐ¸ Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð° Ð½Ðµ Ð½Ð°ÑˆÐ»Ð¾Ñ�ÑŒ Ð½Ð¸ Ñ„Ð°Ð¹Ð»Ð°, Ð½Ð¸ Ñ€ÐµÑ�ÑƒÑ€Ñ�Ð°, Ð�Ð˜ Ð”Ð•Ð¤ÐžÐ›Ð¢Ð�ÐžÐ“Ðž Ð Ð•Ð¡Ð£Ð Ð¡Ð� \""
                                + defaultResourceName + "\"");
                        return new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_RGB);
                    }
                    inStream = new DataInputStream(
                        o.getClass().getResourceAsStream(defaultResourceName));
                } else {
                    inStream = new DataInputStream(is);
                }

            }
            byte[] b = null;
            try {
                b = new byte[inStream.available()];
                inStream.readFully(b);
                inStream.close();
            } catch (IOException ex) {
                QLog.l().logger().error(ex);
            }
            img = new ImageIcon(b).getImage();
            HASH_IMG.put(resourceName, img);
            return img;
        }
    }

    /**
     * Ð”Ð»Ñ� Ñ‡Ñ‚ÐµÐ½Ð¸Ñ� Ð±Ð°Ð¹Ñ‚ Ð¸Ð· Ð¿Ð¾Ñ‚Ð¾ÐºÐ°. Ð½Ðµ Ð¿Ñ€Ð¸Ð¼ÐµÐ½Ñ�Ñ‚ÑŒ Ð´Ð»Ñ� Ð¿Ð¾Ñ‚Ð¾ÐºÐ° Ñ�Ð²Ñ�Ð·Ð°Ð½Ð½Ð¾Ð³Ð¾ Ñ� Ñ�Ð¾ÐºÐµÑ‚Ð¾Ð¼.
     * readSocketInputStream(InputStream stream)
     *
     * @param stream Ð¸Ð· Ð½ÐµÐ³Ð¾ Ñ‡Ð¸Ñ‚Ð°ÐµÐ¼
     * @return byte[] Ñ€ÐµÐ·ÑƒÐ»ÑŒÑ‚Ð°Ñ‚
     */
    public static byte[] readInputStream(InputStream stream) throws IOException {
        final byte[] result;
        final DataInputStream dis = new DataInputStream(stream);
        result = new byte[stream.available()];
        dis.readFully(result);
        return result;
    }

    /**
     * ÐžÐºÑ€ÑƒÐ³Ð»ÐµÐ½Ð¸Ðµ Ð´Ð¾ Ð½ÐµÑ�ÐºÐ¾Ð»ÑŒÐºÐ¸Ñ… Ð·Ð½Ð°ÐºÐ¾Ð² Ð¿Ð¾Ñ�Ð»Ðµ Ð·Ð°Ð¿Ñ�Ñ‚Ð¾Ð¹.
     *
     * @return Ð“Ð¾Ñ‚Ð¾Ð²Ð¾Ðµ Ð¾Ð±Ñ€ÐµÐ·Ð°Ð½Ð½Ð¾Ðµ Ð´Ñ€Ð¾Ð±Ð½Ð¾Ðµ Ñ‡Ð¸Ñ�Ð»Ð¾.
     */
    public static double roundAs(double value, int scale) {
        return new BigDecimal(value).setScale(scale, RoundingMode.UP).doubleValue();
    }

    /**
     * Ð’Ñ‹Ð·Ñ‹Ð²Ð°ÐµÑ‚ Ð´Ð¸Ð°Ð»Ð¾Ð³ Ð²Ñ‹Ð±Ð¾Ñ€Ð° Ñ„Ð°Ð¹Ð»Ð°.
     *
     * @param parent ÐžÑ‚Ð½Ð¾Ñ�Ð¸Ñ‚ÐµÐ»ÑŒÐ½Ð¾ Ñ‡ÐµÐ³Ð¾ Ð¿Ð¾ÐºÐ°Ð·Ñ‹Ð²Ð°Ñ‚ÑŒ Ñ„Ð¾Ñ€Ð¼Ñƒ Ð´Ð¸Ð°Ð»Ð¾Ð³Ð°.
     * @param title Ð—Ð°Ð³Ð¾Ð»Ð¾Ð²Ð¾Ðº Ð´Ð¸Ð°Ð»Ð¾Ð³Ð¾Ð²Ð¾Ð³Ð¾ Ð¾ÐºÐ½Ð°.
     * @param description ÐžÐ¿Ð¸Ñ�Ð°Ð½Ð¸Ðµ Ñ„Ð¸Ð»ÑŒÑ‚Ñ€Ð°, Ð½Ð°Ð¿Ñ€Ð¸Ð¼ÐµÑ€ "Ð¤Ð°Ð¹Ð»Ñ‹ XML(*.xml)".
     * @param extension Ð¤Ð¸Ð»ÑŒÑ‚Ñ€ Ð¿Ð¾ Ñ€Ð°Ñ�ÑˆÐ¸Ñ€ÐµÐ½Ð¸ÑŽ Ñ„Ð°Ð¹Ð»Ð¾Ð², Ð½Ð°Ð¿Ñ€Ð¸Ð¼ÐµÑ€ "xml".
     * @return ÐŸÐ¾Ð»Ð½Ð¾Ðµ Ð¸Ð¼Ñ� Ñ„Ð°Ð¹Ð»Ð° Ð¸Ð»Ð¸ null ÐµÑ�Ð»Ð¸ Ð½Ðµ Ð²Ñ‹Ð±Ñ€Ð°Ð»Ð¸.
     */
    public static String getFileName(Component parent, String title, String description,
        String extension) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setLocale(Locales.getInstance().getLangCurrent());
        fileChooser.resetChoosableFileFilters();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extension);
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle(title);

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getSelectedFile().exists()) {
                return fileChooser.getSelectedFile().getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * ÐžÑ‚Ñ†ÐµÐ½Ñ‚Ð¸Ñ€Ð¸Ñ€ÑƒÐµÐ¼ ÐžÐºÐ½Ð¾ Ð¿Ð¾ Ñ†ÐµÐ½Ñ‚Ñ€Ñƒ Ñ�ÐºÑ€Ð°Ð½Ð°
     *
     * @param component Ñ�Ñ‚Ð¾ Ð¾ÐºÐ½Ð¾ Ð¸ Ð±ÑƒÐ´ÐµÐ¼ Ñ†ÐµÐ½Ñ‚Ñ€Ð¸Ñ€Ð¾Ð²Ð°Ñ‚ÑŒ
     */
    public static void setLocation(JDialog component) {
        component.setLocationRelativeTo(null);
        //component.setLocation((Math.round(firstMonitor.getDefaultConfiguration().getBounds().width - component.getWidth()) / 2),
        //        (Math.round(firstMonitor.getDefaultConfiguration().getBounds().height - component.getHeight()) / 2));
    }

    public static void setLocation(JFrame component) {
        component.setLocationRelativeTo(null);
        //component.setLocation((Math.round(firstMonitor.getDefaultConfiguration().getBounds().width - component.getWidth()) / 2),
        //        (Math.round(firstMonitor.getDefaultConfiguration().getBounds().height - component.getHeight()) / 2));
    }

    /**
     * Ð Ð°Ñ�Ñ‚Ñ�Ð½ÐµÐ¼ Ð¾ÐºÐ½Ð¾ Ð½Ð° Ð²ÐµÑ�ÑŒ Ñ�ÐºÑ€Ð°Ð½
     *
     * @param component Ñ�Ñ‚Ð¾ Ð¾ÐºÐ½Ð¾ Ð¸ Ð±ÑƒÐ´ÐµÐ¼ Ñ€Ð°Ñ�Ñ‚Ñ�Ð³Ð¸Ð²Ð°Ñ‚ÑŒ
     */
    public static void setFullSize(Component component) {
        component.setBounds(0, 0, firstMonitor.getDefaultConfiguration().getBounds().width,
            firstMonitor.getDefaultConfiguration().getBounds().height);
    }

    /**
     * Ð—Ð°Ð³Ñ€ÑƒÐ·ÐºÐ° Ð²Ñ�ÐµÑ… jar Ð¸Ð· Ð¿Ð°Ð¿ÐºÐ¸ Ð² ÐºÐ»Ð°Ñ�Ñ�Ð¿Ð°Ñ„
     *
     * @param folder Ð¸Ð· Ñ�Ñ‚Ð¾Ð¹ Ð¿Ð°Ð¿ÐºÐ¸ Ð·Ð°ÐºÑ€ÑƒÐ·Ð¸Ð¼.
     */
    public static void loadPlugins(String folder) {
        // Ð—Ð°Ð³Ñ€ÑƒÐ·ÐºÐ° Ð¿Ð»Ð°Ð³Ð¸Ð½Ð¾Ð² Ð¸Ð· Ð¿Ð°Ð¿ÐºÐ¸ plugins
        QLog.l().logger().info("Ð—Ð°Ð³Ñ€ÑƒÐ·ÐºÐ° Ð¿Ð»Ð°Ð³Ð¸Ð½Ð¾Ð² Ð¸Ð· Ð¿Ð°Ð¿ÐºÐ¸ plugins.");
        final File[] list = new File(folder)
            .listFiles((File dir, String name) -> name.toLowerCase().endsWith(".jar"));
        if (list != null && list.length != 0) {
            final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            final Class sysclass = URLClassLoader.class;
            final Class[] parameters = new Class[]{URL.class};
            for (File file : list) {
                QLog.l().logger().debug("Plugin " + file.getName());
                try {
                    final Method method = sysclass.getDeclaredMethod("addURL", parameters);
                    method.setAccessible(true);
                    method.invoke(sysloader, new Object[]{file.toURI().toURL()});
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | MalformedURLException ex) {
                    QLog.l().logger().error("plugin " + file.getName() + " Not loaded. " + ex);
                }
            }
        }

    }

    /**
     * Ð¡Ð¾Ð·Ð´Ð°Ð½Ð¸Ðµ Ð¸ Ð¿Ð¾ÐºÐ°Ð· Ñ�Ð¿Ð»Ñ�Ñˆ-Ð·Ð°Ñ�Ñ‚Ð°Ð²ÐºÐ¸ Ñ� Ð±Ð»Ð¾ÐºÐ¸Ñ€Ð¾Ð²ÐºÐ¾Ð¹ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ° Ð²Ñ‚Ð¾Ñ€Ð¾Ð¹ ÐºÐ¾Ð¿Ð¸Ð¸
     */
    public static void startSplashClient() {
        if (!QConfig.cfg().isTerminal()) {
            try {
                stopStartSecond = new ServerSocket(21210);
            } catch (Exception ex) {
                System.err.println(".QSystem: Application alredy started!!!");
                System.exit(15685);
            }
        }
        startSplash();
    }

    /**
     * Ð¡Ð¾Ð·Ð´Ð°Ð½Ð¸Ðµ Ð¸ Ð¿Ð¾ÐºÐ°Ð· Ñ�Ð¿Ð»Ñ�Ñˆ-Ð·Ð°Ñ�Ñ‚Ð°Ð²ÐºÐ¸ Ñ� Ð±Ð»Ð¾ÐºÐ¸Ñ€Ð¾Ð²ÐºÐ¾Ð¹ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ° Ð²Ñ‚Ð¾Ñ€Ð¾Ð¹ ÐºÐ¾Ð¿Ð¸Ð¸
     */
    public static void startSplash() {
        sh = true;
        SwingUtilities.invokeLater(new SplashRun());
    }

    /**
     * Ð¡Ð¾Ð·Ð´Ð°Ð½Ð¸Ðµ Ð¸ Ð¿Ð¾ÐºÐ°Ð· Ñ�Ð¿Ð»Ñ�Ñˆ-Ð·Ð°Ñ�Ñ‚Ð°Ð²ÐºÐ¸ Ð±ÐµÐ· Ð±Ð»Ð¾ÐºÐ¸Ñ€Ð¾Ð²ÐºÐ¸ Ð·Ð°Ð¿ÑƒÑ�ÐºÐ° Ð²Ñ‚Ð¾Ñ€Ð¾Ð¹ ÐºÐ¾Ð¿Ð¸Ð¸
     */
    public static void showSplash() {
        sh = true;
        SwingUtilities.invokeLater(new SplashRun());
    }

    /**
     * Ð¡ÐºÑ€Ñ‹Ñ‚Ð¸Ðµ Ñ�Ð¿Ð»Ñ�Ñˆ-Ð·Ð°Ñ�Ñ‚Ð°Ð²ÐºÐ¸
     */
    public static void closeSplash() {
        sh = false;
    }

    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"foo", "bar", "baz"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(120, 140, 200, 40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 120, 150);
    }

    public static String prepareAbsolutPathForImg(String html) {
        File f = new File(html.startsWith("file:///") ? html.substring(8) : html);
        if (f.exists()) {
            return f.toURI().toString().replace("file:/", "file:///");
        }
        final Pattern pattern = Pattern.compile(
            "<\\s*img\\s*src\\s*=\\s*['\"].*?['\"]\\s*>");//<img src='file:///E:\WORK\apertum-qsystem\config\board\q.jpg'>
        final Matcher matcher = pattern.matcher(html);
        String res = html;
        while (matcher.find()) {
            String img = matcher.group();
            final String tci = img.contains("'") ? "'" : "\"";
            img = img.substring(img.indexOf(tci) + 1, img.indexOf(tci, img.indexOf(tci) + 1));

            String ff = img;
            if (img.startsWith("file:///")) {
                ff = img.substring(8);
            }
            f = new File(ff);
            if (f.isFile()) {
                res = res.replace(tci + img + tci,
                    tci + f.toURI().toString().replace("file:/", "file:///") + tci);
            } else {
                QLog.l().logger().error("Ð�Ðµ Ð½Ð°Ð¹Ð´ÐµÐ½ Ñ„Ð°Ð¹Ð» \"" + img + "\" Ð´Ð»Ñ� HTML.");
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String result = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><b><p align=\"center\"><span style=\"font-size:20.0pt;color:blue\">Some service</span></p></b></body></html>";
        System.out.println(result);
        result = result.replaceAll("<.?(html).*?>", "");
        System.out.println(result);
        result = result.replaceAll("(<.?(head).*?>).*?(<.?(head).*?>)", "");
        System.out.println(result);
        result = result.replaceAll("<.?(body).*?>", "");
        System.out.println(result);

        result = result.replaceAll("<p\\S*?", "\n<p ");
        System.out.println(result);

        System.out.println("");
        System.out.println("");

        System.out.println(prepareAbsolutPathForImg("<img src='file:///config/board/q.png'>"));
        System.out.println(prepareAbsolutPathForImg("<img src=\"file:///config/board/q.png\">"));
        System.out.println(prepareAbsolutPathForImg("config/board/q.png"));
        System.out.println(prepareAbsolutPathForImg("file:///www/timed.html"));
        System.out.println(prepareAbsolutPathForImg("www/timed.html"));
    }

    public static String getLocaleMessage(String key) {
        if (localeMap == null) {
            localeMap = Application.getInstance(QSystem.class).getContext()
                .getResourceMap(Uses.class);
        }
        return localeMap.getString(key);
    }

    /**
     * ÐšÐ»Ð°Ñ�Ñ� Ð·Ð°Ñ�Ñ‚Ð°Ð²ÐºÐ¸
     */
    private static class SplashScreen extends JFrame {

        final BorderLayout borderLayout1 = new BorderLayout();
        final JLabel imageLabel = new JLabel();
        final JLabel imageLabel2 = new JLabel();
        final JLayeredPane lp = new JDesktopPane();
        final ImageIcon imageIcon;
        final ImageIcon imageIcon2;
        final Timer timer = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (sh == false) {
                    stopTimer();
                    setVisible(false);
                }
            }

            private void stopTimer() {
                timer.stop();
            }
        });

        public SplashScreen() {
            imageIcon = new ImageIcon(SplashScreen.class
                .getResource("/ru/apertum/qsystem/client/forms/resources/fon_login_bl.jpg"));
            imageIcon2 = new ImageIcon(
                SplashScreen.class
                    .getResource("/ru/apertum/qsystem/client/forms/resources/loading.gif"));
            init();
        }

        private void init() {
            try {
                setIconImage(ImageIO.read(SplashScreen.class
                    .getResource("/ru/apertum/qsystem/client/forms/resources/client.png")));
            } catch (IOException ex) {
                System.err.println(ex);
            }
            setTitle("Ð—Ð°Ð¿ÑƒÑ�Ðº QSystem");
            setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
            imageLabel.setIcon(imageIcon);
            imageLabel2.setIcon(imageIcon2);
            lp.setBounds(0, 0, 400, 400);
            lp.setOpaque(false);
            add(lp);
            this.getContentPane().add(imageLabel, BorderLayout.CENTER);
            imageLabel2.setBounds(175, 165, 300, 30);
            lp.add(imageLabel2, null);
            timer.start();
        }
    }

    private static class SplashRun implements Runnable {

        @Override
        public void run() {
            final SplashScreen screen = new SplashScreen();
            //screen.setSize(480, 360);
            screen.setUndecorated(true);
            screen.setResizable(false);
            setLocation(screen);
            screen.pack();
            screen.setVisible(true);
            screen.setAlwaysOnTop(true);
        }
    }
}
