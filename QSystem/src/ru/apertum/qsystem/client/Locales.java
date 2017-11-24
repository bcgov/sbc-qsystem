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
package ru.apertum.qsystem.client;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.common.Uses;
import ru.apertum.qsystem.common.exceptions.ServerException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Evgeniy Egorov
 */
public final class Locales {

    private static final ResourceBundle TRANSLATE = ResourceBundle.getBundle("ru/apertum/qsystem/common/resources/i3-label", Locales.getInstance().getLangCurrent());

    public static String locMes(String key) {
        return TRANSLATE.getString(key);
    }
    /**
     * Формат даты без времени, с годом и месяц прописью
     */
    public static final String DATE_FORMAT_FULL = "dd MMMM yyyy";
    /**
     * Форматы дат./2009 январь 26 16:10:41
     */
    public final SimpleDateFormat format_for_label;
    public final SimpleDateFormat format_for_label2;
    public final SimpleDateFormat format_for_print;
    public final SimpleDateFormat format_dd_MMMM;
    public final SimpleDateFormat format_dd_MM_yyyy_time;
    public final SimpleDateFormat format_dd_MMMM_yyyy;

    private Locales() {

        // Загрузка плагинов из папки plugins
        QLog.l().logger().info("Languages are loading...");
        QLog.l().logger().error("Current directory " + System.getProperty("user.dir"));
        final File[] list = new File("languages").listFiles((File dir, String name) -> name.matches(".._..\\.(jar|JAR)"));
        if (list != null && list.length != 0) {
            final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            final Class sysclass = URLClassLoader.class;
            final Class[] parameters = new Class[]{URL.class};
            for (File file : list) {
                //QLog.l().logger().debug("Langusge " + file.getName().split("\\.")[0]);
                try {
                    final Method method = sysclass.getDeclaredMethod("addURL", parameters);
                    method.setAccessible(true);
                    method.invoke(sysloader, new Object[]{file.toURI().toURL()});
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | MalformedURLException ex) {
                    QLog.l().logger().error("Language " + file.getName() + " did NOT load. " + ex);
                }
            }
        } else {
            throw new ServerException("Lacales wast loaded.");
        }

        final HashSet<String> locs = new HashSet<>();
        for (File list1 : list) {
            final String s = list1.getName().split("\\.")[0];
            locs.add(s);
            final Properties settings = new Properties();
            final InputStream in;
            final InputStreamReader inR;
            try {
                in = settings.getClass().getResourceAsStream("/" + s + ".properties");
                inR = new InputStreamReader(in, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                QLog.l().logger().error("Language " + list1.getName() + " have no description. " + ex);
                continue;
            }
            try {
                settings.load(inR);
            } catch (IOException ex) {
                QLog.l().logger().error("Language description " + list1.getName() + " did NOT load. " + ex);
                continue;
            }
            QLog.l().logger().debug("   Langusge: " + settings.getProperty("name") + " " + settings.getProperty("lng") + "_" + settings.getProperty("country"));

            final Locale locale = new Locale(settings.getProperty("lng"), settings.getProperty("country"));
            locales.put(s, locale);
            locales_name.put(locale, s);
            lngs.put(settings.getProperty("name"), s);
            lngs_names.put(s, settings.getProperty("name"));
            lngs_buttontext.put(s, settings.getProperty("buttontext"));
        }

        File f = new File(configFileName);
        if (!f.exists()) {
            configFileName = "../" + configFileName;
            f = new File(configFileName);
            if (!f.exists()) {
                final Exception ex = new FileNotFoundException(configFileName);
                QLog.l().logger().error(ex);
                throw new RuntimeException(ex);
            }
        }

        final FileBasedConfigurationBuilder<FileBasedConfiguration> builder
                = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(new FileBasedBuilderParametersImpl().setFileName(configFileName).setEncoding("utf8"));
        builder.setAutoSave(true);
        try {
            config = builder.getConfiguration();
            // config contains all properties read from the file
        } catch (ConfigurationException cex) {
            QLog.l().logger().error(cex);
            throw new RuntimeException(cex);
        }

        locs.stream().forEach((loc) -> {
            lngs_welcome.put(loc, config.getString(loc, "1"));
        });

        //System.out.println("- 0 --" + getLangCurrent());
        //System.out.println("- 01 --" + getLangCurrent().getISO3Language());
        isUkr = getLangCurrent().getISO3Language().toLowerCase().startsWith("ukr");
        //System.out.println("- 1 --" + Locale.getDefault());
        //System.out.println("- 2 --" + locales_name.get(Locale.getDefault()));

        //isRuss = getNameOfPresentLocale().toLowerCase().startsWith("ru") && !isUkr;
        isRuss = getLangCurrent().getISO3Language().startsWith("ru") && !isUkr;

        russSymbolDateFormat = new DateFormatSymbols(getLocaleByName("RU"));
        russSymbolDateFormat.setMonths(Uses.RUSSIAN_MONAT);

        ukrSymbolDateFormat = new DateFormatSymbols(getLocaleByName("UA"));
        ukrSymbolDateFormat.setMonths(Uses.UKRAINIAN_MONAT);

        final DateFormatSymbols symbols = new DateFormatSymbols(getLangCurrent());
        switch (getLangCurrent().toString().toLowerCase(Locale.US)) {
            case "ru_ru":
                symbols.setMonths(Uses.RUSSIAN_MONAT);
                break;
            case "uk_ua":
                symbols.setMonths(Uses.UKRAINIAN_MONAT);
                break;
            case "az_az":
                symbols.setMonths(Uses.AZERBAIJAN_MONAT);
                break;
        }

        format_for_label = new SimpleDateFormat("dd MMMM HH.mm.ss", symbols);
        format_for_label2 = new SimpleDateFormat("dd MMMM HH.mm:ss", symbols);
        format_for_print = new SimpleDateFormat("dd MMMM HH:mm", symbols);
        format_dd_MMMM = new SimpleDateFormat("dd MMMM", symbols);
        format_dd_MM_yyyy_time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", symbols);
        format_dd_MMMM_yyyy = new SimpleDateFormat(DATE_FORMAT_FULL, symbols);
    }
    private String configFileName = "config/langs.properties";
    private final FileBasedConfiguration config;
    public final boolean isRuss;
    public final boolean isUkr;
    private final DateFormatSymbols russSymbolDateFormat;
    private final DateFormatSymbols ukrSymbolDateFormat;

    public DateFormatSymbols getRussSymbolDateFormat() {
        return russSymbolDateFormat;
    }

    public DateFormatSymbols getUkrSymbolDateFormat() {
        return ukrSymbolDateFormat;
    }
    /**
     * eng -> Locale(eng)
     */
    private final LinkedHashMap<String, Locale> locales = new LinkedHashMap<>();
    /**
     * Locale(eng)-> eng
     */
    private final LinkedHashMap<Locale, String> locales_name = new LinkedHashMap<>();
    /**
     * English -> eng
     */
    private final LinkedHashMap<String, String> lngs = new LinkedHashMap<>();
    /**
     * eng -> English
     */
    private final LinkedHashMap<String, String> lngs_names = new LinkedHashMap<>();
    /**
     * eng -> buttontext
     */
    private final LinkedHashMap<String, String> lngs_buttontext = new LinkedHashMap<>();
    /**
     * eng -> 1/0
     */
    private final LinkedHashMap<String, String> lngs_welcome = new LinkedHashMap<>();

    public static Locales getInstance() {
        return LocalesHolder.INSTANCE;
    }

    private static class LocalesHolder {

        private static final Locales INSTANCE = new Locales();
    }
    private final String WELCOME = "welcome";
    private final String LANG_CURRENT = "locale.current";
    private final String WELCOME_LNG = "welcome.multylangs";
    private final String WELCOME_LNG_POS = "welcome.multylangs.position";
    private final String WELCOME_LNG_BTN_FILL = "welcome.multylangs.areafilled";
    private final String WELCOME_LNG_BTN_BORDER = "welcome.multylangs.border";

    public boolean isWelcomeMultylangs() {
        return config.getString(WELCOME_LNG) == null ? false : "1".equals(config.getString(WELCOME_LNG)) || config.getString(WELCOME_LNG).startsWith("$");
    }

    public void setWelcomeMultylangs(boolean multylangs) {
        if (!config.getString(WELCOME_LNG).startsWith("$")) {
            config.setProperty(WELCOME_LNG, multylangs ? "1" : "0");
        }
    }

    public boolean isIDE() {
        return config.getString(WELCOME_LNG).startsWith("$");
    }

    public boolean isWelcomeFirstLaunch() {
        return config.getString(WELCOME) == null ? false : ("1".equals(config.getString(WELCOME)) && !config.getString(WELCOME_LNG).startsWith("$"));
    }

    public boolean isWelcomeMultylangsButtonsFilled() {
        return config.getString(WELCOME_LNG_BTN_FILL) == null ? true : "1".equals(config.getString(WELCOME_LNG_BTN_FILL));
    }

    public boolean isWelcomeMultylangsButtonsBorder() {
        return config.getString(WELCOME_LNG_BTN_BORDER) == null ? true : "1".equals(config.getString(WELCOME_LNG_BTN_BORDER));
    }

    public int getMultylangsPosition() {
        return config.getString(WELCOME_LNG_POS) == null ? 1 : Integer.parseInt(config.getString(WELCOME_LNG_POS));
    }

    public Locale getLangCurrent() {
        return locales.get(config.getString(LANG_CURRENT)) == null ? Locale.getDefault() : locales.get(config.getString(LANG_CURRENT));
    }

    public Locale getLocaleByName(String name) {
        return locales.get(name) == null ? Locale.getDefault() : locales.get(name);
    }

    public String getLangCurrName() {
        return "".equals(config.getString(LANG_CURRENT)) ? lngs_names.get("eng") : lngs_names.get(config.getString(LANG_CURRENT));
    }

    public String getLangButtonText(String lng) {
        return lngs_buttontext.get(lng);
    }

    public String getLangWelcome(String lng) {
        return lngs_welcome.get(lng);
    }

    public String getNameOfPresentLocale() {
        return locales_name.get(Locale.getDefault());
    }

    /**
     *
     * @param name English к примеру eng
     */
    public void setLangCurrent(String name) {
        config.setProperty(LANG_CURRENT, lngs.get(name));
    }

    public void setWelcome(String count) {
        config.setProperty(WELCOME, count);
    }

    public void setLangWelcome(String name, boolean on) {
        config.setProperty(name, on ? "1" : "0");
        lngs_welcome.put(name, on ? "1" : "0");
    }

    public ArrayList<String> getAvailableLocales() {
        final ArrayList<String> res = new ArrayList<>(lngs.keySet());
        return res;
    }

    public ArrayList<String> getAvailableLangs() {
        final ArrayList<String> res = new ArrayList<>(lngs_names.keySet());
        return res;
    }
}
