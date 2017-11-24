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
 * @author Evgeniy Egorov Сдесь находятся константы и общеиспользуемые конструкции
 */
public final class Uses {

    // значения приоритета "очередника"
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HI = 2;
    public static final int PRIORITY_VIP = 3;
    public static final int[] PRIORITYS = {PRIORITY_LOW, PRIORITY_NORMAL, PRIORITY_HI,
        PRIORITY_VIP};
    public static final String PROPERTIES_FILE = "config/qsystem.properties";
    // значения приоритета обрабатываемых услуг для юзера
    // public static final int SERVICE_EXCLUDE = -1;
    public static final int SERVICE_REMAINS = 0;
    public static final int SERVICE_NORMAL = 1;
    public static final int SERVICE_VIP = 2;
    //public static final int[] SERVICE_PRIORITYS = {SERVICE_EXCLUDE, SERVICE_REMAINS, SERVICE_NORMAL, SERVICE_VIP};
    public static final LinkedHashMap<Integer, String> COEFF_WORD = new LinkedHashMap<>();
    // Naming tags and attributes in XML statistics protocols
    public static final String TAG_REP_STATISTIC = "Statistics";//"Статистика";
    public static final String TAG_REP_PARAM_COUNT = "Denominator";//"Знаменатель";
    public static final String TAG_REP_PARAM_AVG = "Average";//"Среднее";
    public static final String TAG_REP_RECORD = "Recording";//"Запись";
    public static final String TAG_REP_SERVICE_WORKED = "Serviced on a Service";//"ОбслуженоПоУслуге";
    public static final String TAG_REP_SERVICE_WAIT = "Waiting for Service";//"ОжидаютПоУслуге";
    public static final String TAG_REP_SERVICE_AVG_WORK = "Servicing Services";//"СрВрОбслуживанияПоУслуге";
    public static final String TAG_REP_SERVICE_AVG_WAIT = "WAITING FOR SERVICE";//"СрВрОжиданияПоУслуге";
    public static final String TAG_REP_SERVICE_KILLED = "Declined on the Service";//"ОтклоненныхПоУслуге";
    public static final String TAG_REP_USER_WORKED = "Served by User";//"ОбслуженоПользователем";
    public static final String TAG_REP_USER_AVG_WORK = "User Service";//"СрВрОбслуживанияПользователем";
    public static final String TAG_REP_USER_KILLED = "Declined by User";//"ОтклоненныхПользователем";
    public static final String TAG_REP_WORKED = "Served";//"Обслуженных";
    public static final String TAG_REP_AVG_TIME_WORK = "Maintenance";//"СрВрОбслуживания";
    public static final String TAG_REP_KILLED = "Disapproved";//"Отклоненных";
    // Tags and attribute names of the setup file
    public static final String TAG_PROP_SERVICES = "The services";//"Услуги";
    public static final String TAG_PROP_SERVICE = "Service";//"Услуга";
    public static final String TAG_PROP_NAME = "Name";//"Наименование";
    public static final String TAG_PROP_DESCRIPTION = "Description";//"Описание";
    public static final String TAG_PROP_PREFIX = "Prefix";//"Префикс";
    public static final String TAG_PROP_ADVANCE_LIMIT = "Limit";//"Лимит";
    public static final String TAG_PROP_ADVANCE_PERIOD_LIMIT = "Limit Prior Records In Days";//"ЛимитПредвЗаписиВДнях";
    public static final String TAG_PROP_USERS = "Members List";//"Пользователи";
    public static final String TAG_PROP_USER = "User";//"Пользователь";
    public static final String TAG_PROP_PASSWORD = "Password";//"Пароль";
    public static final String TAG_PROP_OWN_SERVS = "Services Provided";//"ОказываемыеУслуги";
    public static final String TAG_PROP_OWN_SRV = "The Service";//"ОказываемаяУслуга";
    public static final String TAG_PROP_KOEF = "Coefficient of Participation";//"КоэффициентУчастия";
    public static final String TAG_PROP_CONNECTION = "Net";//"Сеть";
    public static final String TAG_PROP_SERV_PORT = "Port Server";//"ПортСервера";
    public static final String TAG_PROP_WEB_SERV_PORT = "Port Web Server";//"ПортВебСервера";
    public static final String TAG_PROP_CLIENT_PORT = "Port Customer";//"ПортКлиента";
    public static final String TAG_PROP_SERV_ADDRESS = "Server Address";//"АдресСервера";
    public static final String TAG_PROP_CLIENT_ADDRESS = "Customer Address";//"АдресКлиента";
    public static final String TAG_PROP_STATUS = "Status";//"Статус";
    public static final String TAG_PROP_START_TIME = "Start Time";//"ВремяНачалаРаботы";
    public static final String TAG_PROP_FINISH_TIME = "Completion Time";//"ВремяЗавершенияРаботы";
    public static final String TAG_PROP_VERSION = "Configuration Warehouse Version";//"ВерсияХранилищаКонфигурации";
    public static final String TAG_PROP_INPUT_REQUIRED = "Requirement for Client Data";//"ТребованиеКлиентскихДанных";
    public static final String TAG_PROP_INPUT_CAPTION = "Data Entry Form Header";//"ЗаголовокФормыВводаКлДанных";
    public static final String TAG_PROP_RESULT_REQUIRED = "Requirement of Work Results";//"ТребованиеРезультатаРаботы";
    // Tags and attribute names of the configuration files of the main scoreboard
    public static final String TAG_BOARD_PROPS = "Options"; //"Параметры";
    public static final String TAG_BOARD_PROP = "Parameter"; //"Параметер";
    public static final String TAG_BOARD_NAME = "Name"; //"Наименование";
    public static final String TAG_BOARD_VALUE = "Value"; //"Значение";
    public static final String TAG_BOARD_TYPE = "Type"; //"Тип";
    public static final String TAG_BOARD_READ_ONLY = "ReadOnly";
    // имена параметров для табло
    // Parameter names for the scoreboard
    public static final String TAG_BOARD_FRACTAL = "Fractal";//"Fractal";
    public static final String TAG_BOARD_MONITOR = "Additional monitor number for the scoreboard";//"Номер дополнительного монитора для табло";
    public static final String TAG_BOARD_LINES_COUNT = "Number of lines on the scoreboard";//"Количество строк на табло";
    public static final String TAG_BOARD_COLS_COUNT = "Number of columns on the scoreboard";//"Количество столбцов на табло";
    public static final String TAG_BOARD_DELAY_VISIBLE = "Minimum display time on the scoreboard";//"Минимальное время индикации на табло";
    public static final String TAG_BOARD_FON_IMG = "Background image";//"Фоновое изображение";
    public static final String TAG_BOARD_FONT_SIZE = "Font size";//"Размер шрифта";
    public static final String TAG_BOARD_FONT_COLOR = "Font Color";//"Цвет шрифта";
    public static final String TAG_BOARD_PANEL_SIZE = "The size";//"Размер";
    public static final String TAG_BOARD_RUNNING_TEXT = "Running text";//"Бегущий текст";
    public static final String TAG_BOARD_VIDEO_FILE = "Video file";//"Видеофайл";
    public static final String TAG_BOARD_VISIBLE_PANEL = "Visible";//"visible";
    public static final String TAG_BOARD_SPEED_TEXT = "Speed ​​of the running text";//"Скорость бегущего текста";
    public static final String TAG_BOARD_GRID_NEXT_COLS = "Columns table footprint";//"Колонки табл след";
    public static final String TAG_BOARD_GRID_NEXT_ROWS = "Rows of footprints";//"Строки табл след";
    public static final String TAG_BOARD_SIMPLE_DATE = "Simple date";//"Простая дата";
    public static final String TAG_BOARD_GRID_NEXT = "The following table";//"Таблица следующих";
    public static final String TAG_BOARD_FON_COLOR = "Background color";//"Цвет фона";
    public static final String TAG_BOARD_FONT_SIZE_CAPTION = "Header font size";//"Размер шрифта заголовка";
    public static final String TAG_BOARD_FONT_NAME = "Font name";//"Font name";
    public static final String TAG_BOARD_FONT_SIZE_LINE = "The font size of the lines";//"Размер шрифта строк";
    public static final String TAG_BOARD_FONT_COLOR_CAPTION = "Header font color";//"Цвет шрифта заголовка";
    public static final String TAG_BOARD_FONT_COLOR_LEFT = "The font color of the left column";//"Цвет шрифта левого столбца";
    public static final String TAG_BOARD_FONT_COLOR_RIGHT = "The font color of the right column";//"Цвет шрифта правого столбца";
    public static final String TAG_BOARD_FONT_COLOR_LINE = "Color of the line of the score line";//"Цвет надписи строки табло";
    public static final String TAG_BOARD_LINE_BORDER = "Rim Edging";//"Окантовка строк";
    public static final String TAG_BOARD_LINE_DELIMITER = "Column separator";//"Разделитель столбцов";
    public static final String TAG_BOARD_LEFT_PIC = "Left column pic";//"Left column pic";
    public static final String TAG_BOARD_RIGHT_PIC = "Right column pic";//"Right column pic";
    public static final String TAG_BOARD_EXT_PIC = "Ext column pic";//"Ext column pic";
    public static final String TAG_BOARD_LEFT_CAPTION = "Header of the left column";//"Заголовок левого столбца";
    public static final String TAG_BOARD_RIGHT_CAPTION = "Title of the right column";//"Заголовок правого столбца";
    public static final String TAG_BOARD_EXT_CAPTION = "Additional column header";//"Заголовок дополнительного столбца";
    public static final String TAG_BOARD_EXT_POSITION = "The order of the additional column";//"Порядок дополнительного столбца";
    public static final String TAG_BOARD_GRID_NEXT_CAPTION = "The title of the following table";//"Заголовок таблицы следующих";
    public static final String TAG_BOARD_GRID_NEXT_FRAME_BORDER = "The table of the following";//"Рамка таблицы следующих";
    public static final String TAG_BOARD_LINE_COLOR = "Border color of the score line";//"Цвет рамки строки табло";
    public static final String TAG_BOARD_LINE_CAPTION = "The inscription of the score line";//"Надпись строки табло";
    public static final String TAG_BOARD_CALL_PANEL = "The panel called";//"Панель вызванного";
    public static final String TAG_BOARD_CALL_PANEL_BACKGROUND = "Picture of the panel called";//"Картинка панели вызванного";
    public static final String TAG_BOARD_CALL_PANEL_X = "Panel called-X";//"Панель вызванного-X";
    public static final String TAG_BOARD_CALL_PANEL_Y = "Panel called-Y";//"Панель вызванного-Y";
    public static final String TAG_BOARD_CALL_PANEL_WIDTH = "Panel called-width";//"Панель вызванного-ширина";
    public static final String TAG_BOARD_CALL_PANEL_HEIGHT = "Panel called-height";//"Панель вызванного-высота";
    public static final String TAG_BOARD_CALL_PANEL_DELAY = "Call panel-show time";//"Панель вызванного-время показа сек";
    public static final String TAG_BOARD_CALL_PANEL_TEMPLATE = "The panel of the called-text html + ###";//"Панель вызванного-текст html+###";
    // имена тегов-разделов для табло
    // Names of tag-sections for the scoreboard
    //имена тегов-разделов для табло
    public static final String TAG_BOARD = "Board";
    public static final String TAG_BOARD_MAIN = "Main";
    public static final String TAG_BOARD_TOP = "Top";
    public static final String TAG_BOARD_BOTTOM = "Bottom";
    public static final String TAG_BOARD_BOTTOM_2 = "Bottom2";
    public static final String TAG_BOARD_LEFT = "Left";
    public static final String TAG_BOARD_RIGHT = "Right";
    // Наименования параметров конфигурационных файлов главных табло
    // Names of parameters of configuration files of the main scoreboard
    public static final String BOARD_VALUE_PAUSE = "The time of the presence of a record on the scoreboard";//"Время присутствия записи на табло";
    public static final String BOARD_ADRESS_MAIN_BOARD = "Address of the main board of the system";// "Адрес главного табло системы";
    public static final int BOARD_TYPE_INT = 1;
    public static final int BOARD_TYPE_DOUBLE = 2;
    public static final int BOARD_TYPE_STR = 3;
    public static final int BOARD_TYPE_BOOL = 4;
    // Наименования заданий
    public static final String TASK_FOR_ALL_SITE = "Для всех сайтов домена";
    public static final String TASK_STAND_IN = "Поставить в очередь";
    public static final String TASK_STAND_COMPLEX = "Поставить в несколько очередей";
    public static final String TASK_ADVANCE_STAND_IN = "Поставить в очередь предварительно";
    public static final String TASK_ADVANCE_CHECK_AND_STAND = "Поставить предварительно записанного";
    public static final String TASK_REMOVE_ADVANCE_CUSTOMER = "Удалить предварительно записанного";
    public static final String TASK_REDIRECT_CUSTOMER = "Переадресовать клиента к другой услуге";
    public static final String TASK_GET_SERVICES = "Получить перечень услуг";
    public static final String TASK_ABOUT_SERVICE = "Получить описание услуги";
    public static final String TASK_GET_SERVICE_CONSISANCY = "Получить очередь услуги";
    public static final String TASK_ABOUT_SERVICE_PERSON_LIMIT = "Получить возможность встать с этими данными";
    public static final String TASK_GET_SERVICE_PREINFO = "Получить информацию по услуге";
    public static final String TASK_GET_INFO_PRINT = "Получить информацию для печати";
    public static final String TASK_GET_USERS = "Получить перечень пользователей";
    public static final String TASK_GET_SELF = "Получить описание пользователя";
    public static final String TASK_GET_SELF_SERVICES = "Получить состояние очередей";
    public static final String TASK_GET_POSTPONED_POOL = "Получить состояние пула отложенных";
    public static final String TASK_GET_BAN_LIST = "Получить список забаненых";
    public static final String TASK_INVITE_POSTPONED = "Вызвать отложенного из пула отложенных";
    public static final String TASK_GET_SELF_SERVICES_CHECK = "Получить состояние очередей с проверкой";
    public static final String TASK_INVITE_NEXT_CUSTOMER = "Получить следующего клиента";
    public static final String TASK_KILL_NEXT_CUSTOMER = "Удалить следующего клиента";
    public static final String TASK_CUSTOMER_TO_POSTPON = "Клиента в пул отложенных";
    public static final String TASK_POSTPON_CHANGE_STATUS = "Сменить статус отложенному";
    public static final String TASK_START_CUSTOMER = "Начать работу с клиентом";
    public static final String TASK_FINISH_CUSTOMER = "Закончить работу с клиентом";
    public static final String TASK_I_AM_LIVE = "Я горец!";
    public static final String TASK_RESTART = "RESTART";
    public static final String TASK_RESTART_MAIN_TABLO = "Рестарт главного твбло";
    public static final String TASK_REFRESH_POSTPONED_POOL = "NEW_POSTPONED_NOW";
    public static final String TASK_SERVER_STATE = "Получить состояние сервера";
    public static final String TASK_SET_SERVICE_FIRE = "Добавить услугу на горячую";
    public static final String TASK_DELETE_SERVICE_FIRE = "Удалить услугу на горячую";    // Наименования отчетов, сдесь писать исключительно маленькими латинскими буквами без пробелов
    public static final String TASK_GET_BOARD_CONFIG = "Получить конфигурацию табло";
    public static final String TASK_SAVE_BOARD_CONFIG = "Сохранить конфигурацию табло";
    public static final String TASK_GET_GRID_OF_WEEK = "Получить недельную предварительную таблицу";
    public static final String TASK_GET_GRID_OF_DAY = "Получить дневную предварительную таблицу";
    public static final String TASK_GET_INFO_TREE = "Получить информационное дерево";
    public static final String TASK_GET_RESULTS_LIST = "Получить получение списка возможных результатов";
    public static final String TASK_GET_RESPONSE_LIST = "Получить список отзывов";
    public static final String TASK_SET_RESPONSE_ANSWER = "Оставить отзыв";
    public static final String REPORT_CURRENT_USERS = "current_users";
    public static final String REPORT_CURRENT_SERVICES = "current_services";
    public static final String TASK_GET_CLIENT_AUTHORIZATION = "Идентифицировать клиента";
    public static final String TASK_SET_CUSTOMER_PRIORITY = "Изменить приоритет";
    public static final String TASK_CHECK_CUSTOMER_NUMBER = "Проверить номер";
    public static final String TASK_CHANGE_FLEX_PRIORITY = "Изменить гибкий приоритет";
    public static final String TASK_CHANGE_RUNNING_TEXT_ON_BOARD = "Изменить бегущий текст на табло";
    public static final String TASK_CHANGE_TEMP_AVAILABLE_SERVICE = "Изменить временную доступность";
    public static final String TASK_GET_STANDARDS = "Получить нормативы";
    public static final String TASK_SET_BUSSY = "Перерыв оператора";
    public static final String TASK_GET_PROPERTIES = "Все параметры из БД";
    public static final String TASK_SAVE_PROPERTIES = "Сохранить все параметры в БД";
    public static final String TASK_INIT_PROPERTIES = "Создать все параметры в БД";
    public static final String TASK_SERVE_CUSTOMER = "serve_customer";
    public static final String TASK_INVITE_SELECTED_CUSTOMER = "Invite selected customer";
    public static final String TASK_CHANGE_SERVICE = "Change the service of the customer";
    public static final String TASK_CUSTOMER_RETURN_QUEUE = "Return the same customer to queue";
    // Формат отчетов
    public static final String REPORT_FORMAT_HTML = "html";
    public static final String REPORT_FORMAT_RTF = "rtf";
    public static final String REPORT_FORMAT_PDF = "pdf";
    public static final String REPORT_FORMAT_XLSX = "xlsx";
    public static final String REPORT_FORMAT_CSV = "csv";
    // Якорь для списка аналитических отчетов
    public static final String ANCHOR_REPORT_LIST = "<tr><td><center>#REPORT_LIST_ANCHOR#</center></td></tr>";
    public static final String ANCHOR_DATA_FOR_REPORT = "#DATA_FOR_REPORT#";
    public static final String ANCHOR_ERROR_INPUT_DATA = "#ERROR_INPUT_DATA#";
    public static final String ANCHOR_USERS_FOR_REPORT = "#USERS_LIST_ANCHOR#";
    public static final String ANCHOR_PROJECT_NAME_FOR_REPORT = "#PROJECT_NAME_ANCHOR#";
    public static final String ANCHOR_COOCIES = "#COOCIES_ANCHOR#";
    // Задания для пункта регистрации
    public static final String WELCOME_LOCK = "#WELCOME_LOCK#";
    public static final String WELCOME_UNLOCK = "#WELCOME_UNLOCK#";
    public static final String WELCOME_OFF = "#WELCOME_OFF#";
    public static final String WELCOME_REINIT = "#WELCOME_REINIT#";
    public final static String[] RUSSIAN_MONAT = {
        "Января",
        "Февраля",
        "Марта",
        "Апреля",
        "Мая",
        "Июня",
        "Июля",
        "Августа",
        "Сентября",
        "Октября",
        "Ноября",
        "Декабря"
    };
    public final static String[] UKRAINIAN_MONAT = {
        "Січня",
        "Лютого",
        "Березня",
        "Квітня",
        "Травня",
        "Червня",
        "Липня",
        "Серпня",
        "Вересня",
        "Жовтня",
        "Листопада",
        "Грудня"
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
     * Формат даты
     */
    public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    /**
     * Формат даты без времени
     */
    public static final String DATE_FORMAT_ONLY = "dd.MM.yyyy";
    /**
     * Формат даты.
     */
    public final static DateFormat FORMAT_HH_MM = new SimpleDateFormat("HH:mm");
    /**
     * Формат даты.
     */
    public final static DateFormat FORMAT_HH_MM_SS = new SimpleDateFormat("hh:mm:ss aa");
    /**
     * Формат даты.
     */
    public final static DateFormat FORMAT_DD_MM_YYYY = new SimpleDateFormat(DATE_FORMAT_ONLY);
    /**
     * Формат даты.
     */
    public final static DateFormat FORMAT_DD_MM_YYYY_TIME = new SimpleDateFormat(DATE_FORMAT);
    /**
     * Формат даты./2009-01-26 16:10:41
     */
    public final static DateFormat FORMAT_FOR_REP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Формат даты./2009-01-26 16:10
     */
    public final static DateFormat FORMAT_FOR_TRANS = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * Временная папка для файлов сохранения состояния для помехоустойчивости
     */
    public static final String TEMP_FOLDER = "temp";
    /**
     * временный файл сохранения состояния для помехоустойчивости
     */
    public static final String TEMP_STATE_FILE = "temp.json";
    /**
     * временный файл сохранения конфигурации комплексных услуг
     */
    public static final String TEMP_COMPLEX_FILE = "complex.json";
    /**
     * временный файл сохранения текущей статистики для помехоустойчивости
     */
    public static final String TEMP_STATATISTIC_FILE = "temp_statistic.xml";
    /**
     * Задержка перед возвратом в корень меню при вопросе "Желаете встать в очередь?" когда в
     * очереди более трех человек.
     */
    public static final int DELAY_BACK_TO_ROOT = 10000;
    /**
     * Задержка перед проверкой блокирования пункта регистрации
     */
    public static final int DELAY_CHECK_TO_LOCK = 55000;
    /**
     * Константа возврата в пункт регистрации кол-во клиентов в очереди, в случае если услуга не
     * обрабатывается ни одним пользователем
     */
    public static final int LOCK_INT = 1000000000;
    /**
     * Константа возврата в пункт регистрации кол-во клиентов в очереди, в случае если услуга не
     * оказывается учитывая расписание
     */
    public static final int LOCK_FREE_INT = 1000000011;
    /**
     * Константа возврата в пункт регистрации кол-во клиентов в очереди, в случае если услуга не
     * оказывается учитывая ограничение посещений в день и лимит достигнут
     */
    public static final int LOCK_PER_DAY_INT = 1000000022;
    /**
     * Вопрос о живости
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
         * Инициализация
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
     * Рекурентный формирователь для public static ArrayList elements(Element root, String
     * tagName).
     *
     * @param list массив элементов
     * @param el корневой элемент ветви
     * @param tagName имя искомых узлов
     */
    private static void getList(ArrayList list, Element el, String tagName) {
        list.addAll(el.elements(tagName));
        el.elements().stream().forEach((obj) -> {
            getList(list, (Element) obj, tagName);
        });
    }

    /**
     * Возвращает массив эолементов с определенным именем из ветви
     *
     * @param root корневой элемент ветви
     * @param tagName имя искомых узлов
     * @return массив элементов
     */
    public static ArrayList<Element> elements(Element root, String tagName) {
        ArrayList<Element> list = new ArrayList<>();
        //list.addAll(root.elements(tagName));
        getList(list, root, tagName);
        return list;
    }

    /**
     * Рекурентный формирователь для public static ArrayList elementsByAttr(...).
     *
     * @param list массив элементов
     * @param el корневой элемент ветви
     * @param attrName имя искомых атрибутов
     * @param attrValue значение атрибута
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
     * Возвращает массив эолементов с определенным значением атрибута из ветви
     *
     * @param root корневой элемент ветви
     * @param attrName имя искомых атрибутов
     * @param attrValue значение атрибута
     * @return массив элементов
     */
    public static ArrayList<Element> elementsByAttr(Element root, String attrName,
        String attrValue) {
        ArrayList<Element> list = new ArrayList<>();
        //list.addAll(root.elements(tagName));
        getList(list, root, attrName, attrValue);
        return list;
    }

    /**
     * Рекурентный формирователь для public static ArrayList elementsByAttr(...).
     *
     * @param list массив элементов
     * @param el корневой элемент ветви
     * @param text значение CData
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
     * Возвращает массив эолементов с определенным значением CData из ветви
     *
     * @param root корневой элемент ветви
     * @param text текст в CData в xml-узле
     * @return массив элементов
     */
    public static ArrayList<Element> elementsByCData(Element root, String text) {
        ArrayList<Element> list = new ArrayList<>();
        //list.addAll(root.elements(tagName));
        getListCData(list, root, text);
        return list;
    }

    /**
     * Получение адреса из строчки.
     *
     * @param adress строчка типа "125.256.214.854" или "rambler.ru"
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
     * Послать сообщение по UDP
     *
     * @param message текст посылаемого сообщения
     * @param address адрес получателя. Если адрес "255.255.255.255", то рассылка будет
     * широковещательной.
     * @param port порт получателя
     */
    public static void sendUDPMessage(String message, InetAddress address, int port) {
        QLog.l().logger().trace(
            "Отправка UDP сообшение \"" + message + "\" по адресу \"" + address.getHostAddress()
                + "\" на порт \"" + port + "\"");
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
     * Послать сообщение по UDP широковещательно
     *
     * @param message текст посылаемого сообщения
     * @param port порт получателя
     */
    public static void sendUDPBroadcast(String message, int port) {
        try {
            sendUDPMessage(message, InetAddress.getByName("255.255.255.255"), port);
        } catch (UnknownHostException ex) {
            throw new ServerException("Address issues " + ex.getMessage());
        }
    }

    /**
     * Загрузка ресурса из jar-файла
     *
     * @param o - класс, нужен для получения ресурса
     * @param resourceName путь к ресурсу в jar-файле
     * @return массив байт, содержащий ресурс
     */
    public static byte[] readResource(Object o, String resourceName) throws IOException {
        // Выдаем ресурс  "/ru/apertum/qsystem/reports/web/name.jpg"
        final InputStream inStream = o.getClass().getResourceAsStream(resourceName);
        return readInputStream(inStream);
    }

    /**
     * грузит картинку из файла или ресурсов. Если Параметр пустой, то возвращает null.
     *
     * @param o Объект для загрузки ресурса из jar, чаще всего класс в котором понадобилась эта
     * картинка.
     * @param resourceName путь к ресурсу или файлу картинки. Может быть пустым.
     * @param defaultResourceName Если нифайла ни ресурса не найдется, то загрузится этот ресурс
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
                            "При загрузки ресурса не нашлось ни файла, ни ресурса, НИ ДЕФОЛТНОГО РЕСУРСА \""
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
     * Для чтения байт из потока. не применять для потока связанного с сокетом.
     * readSocketInputStream(InputStream stream)
     *
     * @param stream из него читаем
     * @return byte[] результат
     */
    public static byte[] readInputStream(InputStream stream) throws IOException {
        final byte[] result;
        final DataInputStream dis = new DataInputStream(stream);
        result = new byte[stream.available()];
        dis.readFully(result);
        return result;
    }

    /**
     * Округление до нескольких знаков после запятой.
     *
     * @return Готовое обрезанное дробное число.
     */
    public static double roundAs(double value, int scale) {
        return new BigDecimal(value).setScale(scale, RoundingMode.UP).doubleValue();
    }

    /**
     * Вызывает диалог выбора файла.
     *
     * @param parent Относительно чего показывать форму диалога.
     * @param title Заголовок диалогового окна.
     * @param description Описание фильтра, например "Файлы XML(*.xml)".
     * @param extension Фильтр по расширению файлов, например "xml".
     * @return Полное имя файла или null если не выбрали.
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
     * Отцентирируем Окно по центру экрана
     *
     * @param component это окно и будем центрировать
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
     * Растянем окно на весь экран
     *
     * @param component это окно и будем растягивать
     */
    public static void setFullSize(Component component) {
        component.setBounds(0, 0, firstMonitor.getDefaultConfiguration().getBounds().width,
            firstMonitor.getDefaultConfiguration().getBounds().height);
    }

    /**
     * Загрузка всех jar из папки в класспаф
     *
     * @param folder из этой папки закрузим.
     */
    public static void loadPlugins(String folder) {
        // Загрузка плагинов из папки plugins
        QLog.l().logger().info("Загрузка плагинов из папки plugins.");
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
     * Создание и показ сплэш-заставки с блокировкой запуска второй копии
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
     * Создание и показ сплэш-заставки с блокировкой запуска второй копии
     */
    public static void startSplash() {
        sh = true;
        SwingUtilities.invokeLater(new SplashRun());
    }

    /**
     * Создание и показ сплэш-заставки без блокировки запуска второй копии
     */
    public static void showSplash() {
        sh = true;
        SwingUtilities.invokeLater(new SplashRun());
    }

    /**
     * Скрытие сплэш-заставки
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
                QLog.l().logger().error("Не найден файл \"" + img + "\" для HTML.");
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
     * Класс заставки
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
            setTitle("Запуск QSystem");
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
