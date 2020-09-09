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

package com.unidata.mdm.backend.service.audit.actions.impl.auth;

import com.unidata.mdm.backend.service.audit.SubSystem;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;

import java.util.Map;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public abstract class AuthAuditAction implements AuditAction {


    /**
     * Used when login in not given
     */
    protected static final String NO_LOGIN = "NO-LOGIN-NAME-GIVEN";


    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof Map;
    }

    @Override
    public SubSystem getSubsystem() {
        return SubSystem.AUTH;
    }

}
