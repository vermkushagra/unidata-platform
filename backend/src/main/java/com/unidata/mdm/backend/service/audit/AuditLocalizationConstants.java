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

package com.unidata.mdm.backend.service.audit;

/**
 * @author Dmitry Kopin on 10.10.2018.
 */
public class AuditLocalizationConstants {

    public static final String MANUAL_MERGE = "app.audit.record.operation.merge.manual";
    public static final String AUTO_MERGE = "app.audit.record.operation.merge.auto";
    public static final String MERGE_WITH_CONFLICTS = "app.audit.record.operation.merge.conflicts";
    public static final String LOGOUT_BY_TIMEOUT="app.audit.record.operation.logout.byTimeout";
    public static final String USER_LOGOUT="app.audit.record.operation.logout.userLogout";
    public static final String LOGOUT_AFTER_CHANGE_SETTINGS ="app.audit.record.operation.logout.changeSettings";
    public static final String LOGOUT_AFTER_CHANGE_ROLES = "app.audit.record.operation.logout.changeRoles";
}
