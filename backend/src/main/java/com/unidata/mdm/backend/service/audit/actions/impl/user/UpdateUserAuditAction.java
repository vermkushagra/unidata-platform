/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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