/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsys.quser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

/**
 *
 * @author Evgeniy Egorov
 */
public class Multilingual {

    /**
     *
     * @return code
     */
    public Lng init() {
        final org.zkoss.zk.ui.Session sess = Sessions.getCurrent();
        final String ln;
        if (sess.getAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE) == null && Executions.getCurrent().getHeader("accept-language") != null && !Executions.getCurrent().getHeader("accept-language").isEmpty()) {
            ln = Executions.getCurrent().getHeader("accept-language").split(";")[0].replace("-", "_").split(",")[0];

        } else {
            ln = sess.getAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE) == null ? "hz_Ch"
                    : (((Locale) sess.getAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE)).getLanguage() + "_"
                    + ((Locale) sess.getAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE)).getCountry());

        }
        Lng lang = ENG_LNG;
        boolean f = true;
        for (Lng lang1 : LANGS) {
            if (lang1.code.equalsIgnoreCase(ln)) {
                lang = lang1;
                f = false;
                break;
            }
        }

        if (f) {
            for (Lng lang1 : LANGS) {
                if (lang1.code.toLowerCase().startsWith(ln.split("_")[0].toLowerCase())) {
                    lang = lang1;
                    break;
                }
            }
        }
        final Locale prefer_locale = lang.code.length() > 2
                ? new Locale(lang.code.substring(0, 2), lang.code.substring(3)) : new Locale(lang.code);
        sess.setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE, prefer_locale);
        if (!ln.equalsIgnoreCase(lang.code)) {
            Executions.sendRedirect(null);
        }
        return lang;

    }

    //*****************************************************
    //**** Multilingual
    //*****************************************************
    public static final Lng ENG_LNG = new Lng("English", "en_GB");
    public static final ArrayList<Lng> LANGS = new ArrayList<>(Arrays.asList(new Lng("Русский", "ru_RU"), ENG_LNG, new Lng("Español", "es_ES"),
            new Lng("Deutsch", "de_DE"), new Lng("Português", "pt_PT"), new Lng("Français", "fr_FR"), new Lng("Italiano", "it_IT"), new Lng("čeština", "cs_CZ"),
            new Lng("Polski", "pl_PL"),
            new Lng("Slovenčina", "sk_SK"), new Lng("Român", "ro_RO"), new Lng("Cрпски", "sr_SP"), new Lng("Український", "uk_UA"), new Lng("Türk", "tr_TR"),
            new Lng("हिंदी", "hi_IN"),
            new Lng("العربية", "ar_EG"), new Lng("עברית", "iw_IL"), new Lng("Қазақ", "kk_KZ"), new Lng("Indonesia", "in_ID"), new Lng("Suomi", "fi_FI")));

    public static class Lng {

        final public String name;
        final public String code;

        public Lng(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static String getName(String code) {
        for (Lng lang1 : LANGS) {
            if (lang1.code.equalsIgnoreCase(code)) {
                return lang1.name;
            }
        }

        for (Lng lang1 : LANGS) {
            if (lang1.code.toLowerCase().startsWith(code.split("_")[0].toLowerCase())) {
                return lang1.name;
            }
        }
        return code;
    }

    public static String getCode(String name) {
        for (Lng lang1 : LANGS) {
            if (lang1.name.equalsIgnoreCase(name)) {
                return lang1.code;
            }
        }
        return name;
    }
}
