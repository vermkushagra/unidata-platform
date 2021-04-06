package com.unidata.mdm.backend.common.dto.wf;

import java.io.InputStream;

/**
 * @author Denis Kostovarov
 */
public class WorkflowAttachContentDTO {
    private String id;

    private String name;

    private InputStream inputStream;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
