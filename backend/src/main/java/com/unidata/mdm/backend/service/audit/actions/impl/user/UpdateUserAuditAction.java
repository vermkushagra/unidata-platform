package com.unidata.mdm.backend.service.audit.actions.impl.user;

import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.service.search.Event;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class UpdateUserAuditAction extends UserAuditAction {
    private static final String ACTION_NAME = "UPDATE";

    public static final UpdateUserAuditAction INSTANCE = new UpdateUserAuditAction();

    @Override
    public void enrichEvent(Event event, Object... input) {
        UserWithPasswordDTO user = (UserWithPasswordDTO) input[0];
        String details = "Имя пользователя: " + user.getLogin() + ". ";
        String pass = StringUtils.isBlank(user.getPassword()) ? "Пароль не был изменен." : "Пароль был изменен.";
        event.putDetails(details + pass);
        //what we should log!
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof UserWithPasswordDTO;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}
