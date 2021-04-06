package com.unidata.mdm.backend.api.rest.dto.meta;

public class EntityGroupNode {

    private String title;
    private String groupName;

    public EntityGroupNode(String title, String groupName) {
        this.title = title;
        this.groupName = groupName;
    }


    public EntityGroupNode() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
