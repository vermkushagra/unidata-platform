/**
 *
 */
package com.unidata.mdm.backend.po;

/**
 * @author Mikhail Mikhailov
 *
 */
public class MetaStoragePO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "meta_storage";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Name.
     */
    public static final String FIELD_NAME = "name";

    /**
     * ID.
     */
    protected String id;

    /**
     * Storage id.
     */
    protected String name;

    /**
     * Constructor.
     */
    public MetaStoragePO() {
        super();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
