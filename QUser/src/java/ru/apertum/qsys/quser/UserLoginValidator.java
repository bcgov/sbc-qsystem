package ru.apertum.qsys.quser;

import java.util.Date;
import java.util.Map;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.server.QSession;
import ru.apertum.qsystem.server.QSessions;
import ru.apertum.qsystem.server.model.QUser;
import ru.apertum.qsystem.server.model.QUserList;

public class UserLoginValidator extends AbstractValidator {

    private String l(String resName) {
        return Labels.getLabel(resName);
    }

    @Override
    public void validate(ValidationContext ctx) {
        //all the bean properties
        Map<String, Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
        validateName(ctx, (String) beanProps.get("name").getValue());
        validatePassword(ctx, (String) beanProps.get("name").getValue(),
            (String) beanProps.get("password").getValue());
        validateMultipleLogin(ctx, (String) beanProps.get("name").getValue(),
            (String) beanProps.get("password").getValue());
    }

    private void validateName(ValidationContext ctx, String name) {
        if (name == null || name.isEmpty()) {
            this.addInvalidMessage(ctx, "name", l("no_user_at_all"));
        }
        if (name != null && !name.isEmpty() && !QUserList.getInstance().hasByName(name)) {
            this.addInvalidMessage(ctx, "name", l("no_user") + " " + name + "!");
        }
    }

    private void validatePassword(ValidationContext ctx, String name, String pass) {
        for (QUser user : QUserList.getInstance().getItems()) {
            if (user.getName().equalsIgnoreCase(name) && user.isCorrectPassword(pass)) {
                return;
            }
        }
        this.addInvalidMessage(ctx, "name", l("accss_dened"));
    }

    private void validateMultipleLogin(ValidationContext ctx, String name, String pass) {
        final Long l = UsersInside.getInstance().getUsersInside().get(name + pass);
        if (l != null && new Date().getTime() - l < 60000) {
            this.addInvalidMessage(ctx, "name", l("user_rady_workng"));
            //If user already login somewhere else, make him force logout
            //            for (QSession session : QSessions.getInstance().getSessions()) {
            //                if (name.equals(session.getUser().getName())) {
            //                    QLog.l().logQUser().debug("    --> Requestor: " + name + "; Logins: " + session.getUser().getName());
            //                    QSessions.getInstance().getSessions().remove(session);
            //                    return;
            //                }
            //            }
        } else {
            QUser usr = null;
            for (QUser user : QUserList.getInstance().getItems()) {
                if (user.getName().equalsIgnoreCase(name) && user.isCorrectPassword(pass)) {
                    usr = user;
                }
            }
            if (usr == null) {
                this.addInvalidMessage(ctx, "name", l("user_not_found"));
            } else {
                // Sessions.getCurrent().getRemoteHost() Deprecated. as of release 7.0.0, use Execution.getRemoteHost() instead.
                // Sessions.getCurrent().getRemoteAddr() Deprecated. as of release 7.0.0, use Execution.getRemoteAddr() instead.
                
                QLog.l().logQUser().debug("");
                
                
                QLog.l().logQUser().trace(
                    Sessions.getCurrent().hashCode() + " - User validate RemoteHost=" + Sessions
                        .getCurrent().getRemoteHost() + " RemoteAddr=" + Sessions.getCurrent()
                        .getRemoteAddr()
                        + " LocalAddr=" + Sessions.getCurrent().getLocalAddr() + " LocalName="
                        + Sessions
                        .getCurrent().getLocalName() + " ServerName=" + Sessions.getCurrent()
                        .getServerName());
                //if (!QSessions.getInstance().check(usr.getId(), Sessions.getCurrent().getRemoteHost(), Sessions.getCurrent().getRemoteAddr().getBytes())) {
                if (!QSessions.getInstance()
                    .check(usr.getId(), "" + Sessions.getCurrent().hashCode(),
                        ("" + Sessions.getCurrent().hashCode()).getBytes())) {
                    this.addInvalidMessage(ctx, "name", l("user_allerady_in"));
                }
            }
        }
    }
}
