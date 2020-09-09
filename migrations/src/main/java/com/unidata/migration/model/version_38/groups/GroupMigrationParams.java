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

package com.unidata.migration.model.version_38.groups;

import com.beust.jcommander.Parameter;

import com.unidata.migration.model.ModelMigrationParams;

public class GroupMigrationParams extends ModelMigrationParams {

    @Parameter(names = "--group_name", description = "Root Group Name")
    private String rootGroupName = EntityGroupMigrator.ROOT_GROUP_NAME;


    @Parameter(names = "--group_title", description = "Root Group Title")
    private String rootGroupTitle = EntityGroupMigrator.ROOT_GROUP_TITLE;

    public String getRootGroupName() {
        return rootGroupName;
    }

    public void setRootGroupName(String rootGroupName) {
        this.rootGroupName = rootGroupName;
    }

    public String getRootGroupTitle() {
        return rootGroupTitle;
    }

    public void setRootGroupTitle(String rootGroupTitle) {
        this.rootGroupTitle = rootGroupTitle;
    }
}
