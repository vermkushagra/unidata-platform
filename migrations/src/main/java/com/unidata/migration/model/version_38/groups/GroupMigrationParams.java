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
