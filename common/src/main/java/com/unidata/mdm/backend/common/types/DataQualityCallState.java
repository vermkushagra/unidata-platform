package com.unidata.mdm.backend.common.types;

import java.util.List;

/**
 * @author Alexey Tsarapkin
 */
public class DataQualityCallState {
    /**
     * Model path
     */
    private String path;
    /**
     * DQ port
     */
    private String port;
    /**
     * Port values
     */
    private List<Attribute> value;

    public DataQualityCallState(String path, String port, List<Attribute> value) {
        this.path = path;
        this.port = port;
        this.value = value;
    }

    /**
     * Model path
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set model path
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get DQ port
     * @return
     */
    public String getPort() {
        return port;
    }

    /**
     * Set DQ port
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Get port values
     * @return
     */
    public List<Attribute> getValue() {
        return value;
    }

    /**
     * Set port values
     * @param value
     */
    public void setValue(List<Attribute> value) {
        this.value = value;
    }
}
