package ru.apertum.qsky.web;

import java.util.ArrayList;
import java.util.Locale;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import ru.apertum.qsky.common.Multilingual;
import ru.apertum.qsky.common.Multilingual.Lng;

public class UserLoginForm {

    @Init
    public void init() {

    }

    public ArrayList<Lng> getLangs() {
        return Multilingual.LANGS;
    }

    private Lng lang = new Multilingual().init();

    public Lng getLang() {
        return lang;
    }

    public void setLang(Lng lang) {
        this.lang = lang;
    }

    @Command("changeLang")
    public void changeLang() {
        if (lang != null) {
            final org.zkoss.zk.ui.Session session = Sessions.getCurrent();
            final Locale prefer_locale = lang.code.length() > 2
                    ? new Locale(lang.code.substring(0, 2), lang.code.substring(3)) : new Locale(lang.code);
            session.setAttribute(org.zkoss.web.Attributes.PREFERRED_LOCALE, prefer_locale);
            Executions.sendRedirect(null);
        }
    }
    //**** Multilingual
    ////////////**************************************************
    ////////////**************************************************
    ////////////**************************************************

    private User user = new User();

    public User getUser() {
        final User cl = (User) Sessions.getCurrent().getAttribute("USER");
        if (cl != null) {
            user = cl;
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Command
    public void submit() {
        final User usr = new User(user.getName(), user.getPassword());
        final String usrs = System.getProperty("QSKY_USERS", "admin=admin");
        String[] ss = usrs.toLowerCase().replaceAll("\\s+", "").split(";|,");
        for (String s : ss) {
            final String[] ss1 = s.split("=");
            if (ss1.length != 2) {
                continue;
            }
            final String[] ss2 = ss1[1].split("@|$|&|%|#");
            if (ss2.length == 2 && user.getName().equals(ss1[0]) && user.getPassword().equals(ss2[0])) {
                final String[] ss3 = ss2[1].split("\\.|_");
                for (String ss31 : ss3) {
                    if (ss31.matches("\\d+")) {
                        usr.addBranch(Long.decode(ss31));
                    }
                }
                break;
            }
        }

        try {
            Sessions.getCurrent().setAttribute("USER", usr);
            Executions.sendRedirect("/dashboard.zul");
        } catch (Throwable t) {
            System.err.println("Server SOO is down! " + t);
            Executions.sendRedirect("/error.zul");
        }
    }
}
