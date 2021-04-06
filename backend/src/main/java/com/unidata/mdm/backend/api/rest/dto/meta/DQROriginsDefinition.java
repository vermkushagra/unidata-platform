package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.meta.DQRSourceSystemRef;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQROriginsDefinition {

    protected List<String> sourceSystems = new ArrayList<>();
    protected boolean all;

    public List<String> getSourceSystems() {
        return sourceSystems;
    }

    public void setSourceSystems(List<String> sourceSystems) {
        this.sourceSystems = sourceSystems;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }
}
