/*
 *  Copyright (C) 2016 {Apertum}Projects. web: www.apertum.ru email: info@apertum.ru
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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 * Manager of configure. It Holds all mechanisms for using properties and providing it for other consumers.
 *
 * @author evgeniy.egorov
 */
public final class QConfig {

    private static final String KEY_DEBUG = "debug";
    // ключ, отвечающий за режим демонстрации. При нем не надо прятать мышку и убирать шапку формы
    // Режим демонстрации. При нем не надо прятать мышку и убирать шапку формы.
    private static final String KEY_DEMO = "demo";
    private static final String KEY_IDE = "ide";
    private static final String KEY_START = "ubtn-start";
    // ключ, отвечающий за возможность загрузки плагинов. 
    private static final String KEY_NOPLUGINS = "noplugins";
    // ключ, отвечающий за паузу на старте. 
    private static final String KEY_DELAY = "delay";
    // ключ, отвечающий за возможность работы клиента на терминальном сервере. 
    private static final String KEY_TERMINAL = "terminal";
    // ключ, отвечающий за возможность работы регистрации в кнопочном исполнении.
    // ключ, отвечающий за возможность работы регистрации при наличии только некой клавиатуры. Список услуг в виде картинки с указанием что нажать на клаве для той или иной услуги 
    //touch,info,med,btn,kbd
    public static final String KEY_WELCOME_MODE = "welcome-mode";
    public static final String KEY_WELCOME_TOUCH = "touch";
    public static final String KEY_WELCOME_INFO = "info";
    public static final String KEY_WELCOME_MED = "med";
    public static final String KEY_WELCOME_BTN = "btn";
    public static final String KEY_WELCOME_KBD = "kbd";
    //Всегда грузим temp.json и никогда не чистим состояние.
    private static final String KEY_RETAIN = "retain";
    private static final String KEY_CLANGS = "change-langs";
    // ключ, отвечающий за паузу при вызове только что вставшего с очередь. Чтоб в зал успел вбежать.
    private static final String KEY_DELAY_INVITE_FIRST = "delay-first-invite";
    private static final String KEY_HTTP = "http-server";
    private static final String KEY_HTTP_PROTOCOL_POST = "http-protocol-post";
    private static final String KEY_HTTP_PROTOCOL_GET = "http-protocol-get";
    private static final String KEY_POINT = "point";
    private static final String KEY_BOARD_CFG = "board-config";
    private static final String KEY_BOARD_FX_CFG = "board-fx-config";
    private static final String KEY_S = "server-address";
    private static final String KEY_S_PORT = "server-port";
    private static final String KEY_C_PORT = "client-port";
    private static final String KEY_USER = "user";

    private static final String KEY_LOG_INFO = "loginfo";
    private static final String KEY_USE_EXT_PRIORITY = "use-ext-prority";

    private static final String KEY_NUM_DIVIDER = "number-divider";

    private static final String ZKEY_BOARD_CFG = "zboard-config";
    private static final String TKEY_BOARD_CFG = "tboard-config";

    private static final String KEY_NO_HIDE_CURSOR = "no-hide-cursor";

    private final FileBasedConfiguration config;

    private QConfig() {
        try {
            if (new File(Uses.PROPERTIES_FILE).exists()) {
                final FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(new FileBasedBuilderParametersImpl().setFileName(Uses.PROPERTIES_FILE).setEncoding("utf8"));
                builder.setAutoSave(true);
                config = builder.getConfiguration();
                // config contains all properties read from the file
            } else {
                this.config = new PropertiesConfiguration();
            }
        } catch (ConfigurationException ex) {
            throw new RuntimeException("Properties file wasn't read.", ex);
        }
        options.addOption("?", "hey", false, "Show information about command line arguments");
        options.addOption("h", "help", false, "Show information about command line arguments");

        /*
         CLIENT: ide -s 127.0.0.1 -cport 3129 -sport 3128 -cfg config/clientboard.xml -cfgfx1 config/clientboardfx.properties -point1 234 debug -terminal1
         RECEPTION: ide -s 127.0.0.1 -cport 3129 -sport 3128  debug
         WELCOME: ide -s 127.0.0.1 -sport 3128 -cport 3129 debug med info1 -buttons1 demo1 -clangs1 -keyboard1
         

         Option o = new Option("log", "loglavel", true, "Level for logger log4j. It have higher priority than properties file.");
         o.setArgName("ebat'");
         options.addOption(o);
         */
        // 0-сервер,1-клиент,2-приемная,3-админка,4-киоск,5-сервер хардварных кнопок, 26 - зональник, 17 - редактор табло
        //type = -1;
        options.addOption("ndiv", KEY_NUM_DIVIDER, true, "Divider for client ticket number between prefix and number. For ex: A-800. Default is empty.");
        options.addOption("d", KEY_DEBUG, false, "Debug mode. Show all messages in console and do not make forms fulscreen.");
        options.addOption("li", KEY_LOG_INFO, false, "Logging mode. Info level only.");
        if (type == -1 || type == 0 || type == 1 || type == 4 || type == 26) {
            options.addOption(KEY_DEMO, false, "Demo mode. You can use mouse and you can see header of forms.");
            options.addOption("nhc", KEY_NO_HIDE_CURSOR, false, "No-hide-cursor mode. In some linux GUI could be problen with hide cursor.");
        }

        options.addOption(KEY_IDE, false, "Do not touch it!");

        if (type == -1 || type == 5) {
            options.addOption("ubs", KEY_START, false, "Auto start for hardware user buttons.");
        }

        options.addOption("np", KEY_NOPLUGINS, false, "Do not load plugins.");

        Option o = new Option("p", KEY_DELAY, true, "Do delay before starting. It can be useful for waiting for prepared other components of QSystem.");
        o.setArgName("in seconds");
        options.addOption(o);

        if (type == -1 || type == 0 || type == 1) {
            options.addOption("t", KEY_TERMINAL, false, "If QSystem working in terminal environment. Not on dedicated computers.");
        }

        if (type == -1 || type == 4) {
            o = new Option("wm", KEY_WELCOME_MODE, true, "If welcome app is not a touch kiosk.\ninfo - just show and print an information.\nmed - Input some number and stand for advance.\nbtn - if it is special hardware buttons device.\nkbd- Ability to work for registration point if there is only a keyboard ar mouse. A list of services in the form of a picture which indicate that to press on keyboard or mouse for a particular service.");
            o.setArgName("touch,info,med,btn,kbd");
            options.addOption(o);
            options.addOption("cl", KEY_CLANGS, false, "Manage multi language mode before start welcome point.");
        }

        if (type == -1 || type == 0) {
            options.addOption("r", KEY_RETAIN, false, "Always to keep the state after restart the server QSystem.");
            o = new Option("dfi", KEY_DELAY_INVITE_FIRST, true, "Pause before calling a client by user after getting line. To have time to run into the room.");
            o.setArgName("in seconds");
            options.addOption(o);
            o = new Option("http", KEY_HTTP, true, "To start built-in http server which support servlets and web-socket. Specify a port.");
            o.setArgName("port");
            options.addOption(o);
        }

        if (type == -1 || type == 1) {
            o = new Option("pt", KEY_POINT, true, "Alternative label for user's workplace..");
            o.setOptionalArg(true);
            o.setArgName("label");
            options.addOption(o);
            o = new Option("cfg", KEY_BOARD_CFG, true, "Config xml file for main board.");
            o.setArgName("xml-file");
            options.addOption(o);
            o = new Option("cfgfx", KEY_BOARD_FX_CFG, true, "Config properties file for main board as FX form.");
            o.setArgName("file");
            options.addOption(o);
            o = new Option("u", KEY_USER, true, "User ID for fast login into client app. From DB.");
            o.setArgName("user ID");
            options.addOption(o);
        }

        if (type == -1 || type == 1 || type == 2 || type == 4) {
            //-s 127.0.0.1 -sport 3128 -cport 3129
            o = new Option("s", KEY_S, true, "Address of QMS QSystem server.");
            o.setArgName("label");
            options.addOption(o);
            o = new Option("sport", KEY_S_PORT, true, "TCP port of QMS QSystem server.");
            o.setArgName("port");
            options.addOption(o);
            o = new Option("cport", KEY_C_PORT, true, "UDP port of user's computer for receiving message from server.");
            o.setArgName("port");
            options.addOption(o);
        }
        if (type == -1 || type == 1 || type == 2 || type == 3 || type == 4 || type == 5) {
            o = new Option("httpp", KEY_HTTP_PROTOCOL_POST, true, "Use HTTP as protocol for transpotring POST commands from clients to server.");
            o.setArgName("port");
            options.addOption(o);

            o = new Option("httpg", KEY_HTTP_PROTOCOL_GET, true, "Use HTTP as protocol for transpotring GET commands from clients to server.");
            o.setArgName("port");
            options.addOption(o);
        }
        if (type == -1 || type == 3) {
            options.addOption("uep", KEY_USE_EXT_PRIORITY, false, "Bad. Forget about it. This is amount of additional priorities for services.");
        }
        if (type == -1 || type == 26) {
            o = new Option("zcfg", ZKEY_BOARD_CFG, true, "Config xml file for zone board.");
            o.setArgName("xml-file");
            options.addOption(o);
        }
        if (type == -1 || type == 17) {
            o = new Option("tcfg", TKEY_BOARD_CFG, true, "Config xml file for board.");
            o.setArgName("xml-file");
            options.addOption(o);
        }
        try {
            // create the parser
            line = parser.parse(options, new String[0]);
        } catch (ParseException ex) {
        }

        try {
            tcpServerAddress = InetAddress.getByName(getServerAddress());
        } catch (UnknownHostException exception) {
            throw new ServerException("Address TCP server is not correct.", exception);
        }
    }

    public static QConfig cfg() {
        return ConfigHolder.INSTANCE;
    }

    /**
     * type 0-сервер,1-клиент,2-приемная,3-админка,4-киоск,5-сервер хардварных кнопок
     */
    private static int type = -1;

    public boolean isServer() {
        return type == 0;
    }

    public boolean isClient() {
        return type == 1;
    }

    public boolean isReception() {
        return type == 2;
    }

    public boolean isAdminApp() {
        return type == 3;
    }

    public boolean isWelcome() {
        return type == 4;
    }

    public boolean isUB() {
        return type == 5;
    }

    /**
     *
     * @param tp 0-сервер,1-клиент,2-приемная,3-админка,4-киоск,5-сервер хардварных кнопок
     * @return
     */
    public static QConfig cfg(int tp) {
        type = tp;
        return ConfigHolder.INSTANCE;
    }

    public int getStoppingPort() {
        return line.hasOption("stoppingport")
                ? Integer.parseInt(line.getOptionValue("stoppingport", "27001"))
                : 27001;
    }

    private static class ConfigHolder {

        private static final QConfig INSTANCE = new QConfig();
    }

    private CommandLine line;
    private final Options options = new Options();
    private final CommandLineParser parser = new DefaultParser();

    /**
     * @param args cmd params
     * @return
     */
    public QConfig prepareCLI(String[] args) {
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            throw new RuntimeException("Parsing failed.  Reason: ", exp);
        }

        new HelpFormatter().printHelp("command line parameters for QMS QSystem...", options);
        // automatically generate the help statement
        if (line.hasOption("help") || line.hasOption("h") || line.hasOption("?")) {

            System.exit(0);
        }

        QLog.l().logger().info("Properties are ready.");
        return this;
    }

    public boolean isDebug() {
        return line.hasOption(KEY_DEBUG)
                ? true
                : config.getBoolean(KEY_DEBUG, false);
    }

    public boolean isLogInfo() {
        return line.hasOption(KEY_LOG_INFO)
                ? true
                : config.getBoolean(KEY_LOG_INFO, false);
    }

    public boolean isDemo() {
        return line.hasOption(KEY_DEMO)
                ? true
                : config.getBoolean(KEY_DEMO, false);
    }

    public boolean isHideCursor() {
        return line.hasOption(KEY_NO_HIDE_CURSOR)
                ? false
                : !config.getBoolean(KEY_NO_HIDE_CURSOR, false);
    }

    public boolean isIDE() {
        return line.hasOption(KEY_IDE)
                ? true
                : config.getBoolean(KEY_IDE, false);
    }

    public boolean isUbtnStart() {
        return line.hasOption(KEY_START)
                ? true
                : config.getBoolean(KEY_START, false);
    }

    public boolean isNoPlugins() {
        return line.hasOption(KEY_NOPLUGINS)
                ? true
                : config.getBoolean(KEY_NOPLUGINS, false);
    }

    public boolean isPlaginable() {
        return !isNoPlugins();
    }

    public int getDelay() {
        try {
            return line.hasOption(KEY_DELAY)
                    ? Integer.parseInt(line.getOptionValue(KEY_DELAY, "0"))
                    : config.getInt(KEY_DELAY, 0);
        } catch (Exception ex) {
            System.err.println(ex);
            return 15;
        }
    }

    public boolean isTerminal() {
        return line.hasOption(KEY_TERMINAL)
                ? true
                : config.getBoolean(KEY_TERMINAL, false);
    }

    /**
     * touch,info,med,btn,kbd
     *
     * @return mode
     */
    public String getWelcomeMode() {
        return line.hasOption(KEY_WELCOME_MODE)
                ? line.getOptionValue(KEY_WELCOME_MODE, "touch")
                : config.getString(KEY_WELCOME_MODE, "touch");
    }

    public boolean isChangeLangs() {
        return line.hasOption(KEY_CLANGS)
                ? true
                : config.getBoolean(KEY_CLANGS, false);
    }

    public boolean isRetain() {
        return line.hasOption(KEY_RETAIN)
                ? true
                : config.getBoolean(KEY_RETAIN, false);
    }

    public int getDelayFirstInvite() {
        try {
            return line.hasOption(KEY_DELAY_INVITE_FIRST)
                    ? Integer.parseInt(line.getOptionValue(KEY_DELAY_INVITE_FIRST, "15"))
                    : config.getInt(KEY_DELAY_INVITE_FIRST, 15);
        } catch (Exception ex) {
            System.err.println(ex);
            return 15;
        }
    }

    public int getHttp() {
        try {
            return line.hasOption(KEY_HTTP)
                    ? Integer.parseInt(line.getOptionValue(KEY_HTTP, "0"))
                    : config.getInt(KEY_HTTP, 0);
        } catch (Exception ex) {
            System.err.println(ex);
            return 0;
        }
    }

    /**
     *
     * @return Порт на который надо отправлять HTTP запросы. 0 - отключено
     */
    public int getHttpProtocol() {
        try {
            return line.hasOption(KEY_HTTP_PROTOCOL_GET) || line.hasOption(KEY_HTTP_PROTOCOL_POST)
                    ? Integer.parseInt(line.getOptionValue(KEY_HTTP_PROTOCOL_GET, line.getOptionValue(KEY_HTTP_PROTOCOL_POST, "0")))
                    : config.getInt(KEY_HTTP_PROTOCOL_GET, config.getInt(KEY_HTTP_PROTOCOL_POST, 0));
        } catch (Exception ex) {
            System.err.println(ex);
            return 0;
        }
    }

    /**
     * Проверка на протокол общения с сервером и тип запросов.
     *
     * @return null - не использовать http, true - POST, false - GET
     */
    public Boolean getHttpRequestType() {
        return getHttpProtocol() > 0 ? (line.hasOption(KEY_HTTP_PROTOCOL_POST) || config.getInt(KEY_HTTP_PROTOCOL_POST, 0) > 0) : null;
    }

    public boolean useExtPriorities() {
        return line.hasOption(KEY_USE_EXT_PRIORITY)
                ? true
                : config.getBoolean(KEY_USE_EXT_PRIORITY, false);
    }

    public String getPoint() {
        return line.hasOption(KEY_POINT)
                ? line.getOptionValue(KEY_POINT, "")
                : config.getString(KEY_POINT, "");
    }

    public String getPointN() {
        return getPoint() == null || getPoint().isEmpty() ? null : getPoint();
    }

    public String getUserID() {
        return line.getOptionValue(KEY_USER, "");
    }

    public String getBoardCfgFile() {
        return line.hasOption(KEY_BOARD_CFG)
                ? line.getOptionValue(KEY_BOARD_CFG, "")
                : config.getString(KEY_BOARD_CFG, "");
    }

    public String getBoardCfgFXfile() {
        return line.hasOption(KEY_BOARD_FX_CFG)
                ? line.getOptionValue(KEY_BOARD_FX_CFG, "")
                : config.getString(KEY_BOARD_FX_CFG, "");
    }

    public String getServerAddress() {
        return line.hasOption(KEY_S)
                ? line.getOptionValue(KEY_S, "127.0.0.1")
                : config.getString(KEY_S, "127.0.0.1");
    }

    private final InetAddress tcpServerAddress;

    public InetAddress getInetServerAddress() {
        return tcpServerAddress;
    }

    public int getServerPort() {
        try {
            return line.hasOption(KEY_S_PORT)
                    ? Integer.parseInt(line.getOptionValue(KEY_S_PORT, "3128"))
                    : config.getInt(KEY_S_PORT, 3128);
        } catch (Exception ex) {
            System.err.println(ex);
            return 3128;
        }
    }

    public int getClientPort() {
        try {
            return line.hasOption(KEY_C_PORT)
                    ? Integer.parseInt(line.getOptionValue(KEY_C_PORT, "3129"))
                    : config.getInt(KEY_C_PORT, 3129);
        } catch (Exception ex) {
            System.err.println(ex);
            return 3129;
        }
    }

    public String getNumDivider(String prefix) {
        return (prefix == null || prefix.isEmpty()) ? ""
                : (line.hasOption(KEY_NUM_DIVIDER)
                ? line.getOptionValue(KEY_NUM_DIVIDER, "").replaceAll("_", " ")
                : "");
    }

    public String getZoneBoardCfgFile() {
        return line.hasOption(ZKEY_BOARD_CFG)
                ? line.getOptionValue(ZKEY_BOARD_CFG, "")
                : config.getString(ZKEY_BOARD_CFG, "");
    }

    public String getTabloBoardCfgFile() {
        return line.hasOption(TKEY_BOARD_CFG)
                ? line.getOptionValue(TKEY_BOARD_CFG, "")
                : config.getString(TKEY_BOARD_CFG, "");
    }

}
