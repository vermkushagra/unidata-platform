/**
 * 
 */
package com.unidata.mdm.backend.api.rest.dto.security;

/**
 * @author mikhail
 * Security data source.
 */
public class SecurityDataSourceRO {
    /**
     * Name.
     */
    private String name;
    /**
     * Description.
     */
    private String description;
    /**
     * Constructor.
     */
    public SecurityDataSourceRO() {
        super();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
