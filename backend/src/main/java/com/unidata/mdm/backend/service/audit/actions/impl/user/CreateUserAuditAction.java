package com.unidata.mdm.backend.service.audit.actions.impl.user;

import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class CreateUserAuditAction extends UserAuditAction {
    private static final String ACTION_NAME = "CREATE";

    @Override
    public void enrichEvent(Event event, Object... input) {
        UserWithPasswordDTO userWithPasswordDTO = (UserWithPasswordDTO) input[0];
        String details = "Имя пользователя: " + userWithPasswordDTO.getLogin() + ". ";
        event.putDetails(details);
        //mb roles
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
