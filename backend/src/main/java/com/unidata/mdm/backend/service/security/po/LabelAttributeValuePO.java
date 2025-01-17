package com.unidata.mdm.backend.service.security.po;

import java.io.Serializable;

/**
 * The persistent class for the s_label_attribute_value database table.
 *
 * @author ilya.bykov
 */
public class LabelAttributeValuePO extends BaseSecurityPO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant TABLE_NAME. */
    public static final String TABLE_NAME = "s_label_attribute_value";
    /** The id. */
    private Integer id;

    /** The value. */
    private String value;

    /** The group. */
    private int group;

    /** The label attribute. */
    // //bi-directional many-to-one association to LabelAttributePO
    private LabelAttributePO labelAttribute;

    /**
     * Instantiates a new label attribute value po.
     */
    public LabelAttributeValuePO() {
    }

    public LabelAttributeValuePO(
            final Integer id,
            final String value,
            final int group,
            final LabelAttributePO labelAttribute
    ) {
        this.id = id;
        this.value = value;
        this.group = group;
        this.labelAttribute = labelAttribute;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id
     *            the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the label attribute.
     *
     * @return the label attribute
     */
    public LabelAttributePO getLabelAttribute() {
        return this.labelAttribute;
    }

    /**
     * Sets the label attribute.
     *
     * @param labelAttribute
     *            the new label attribute
     */
    public void setLabelAttribute(LabelAttributePO labelAttribute) {
        this.labelAttribute = labelAttribute;
    }

    /**
     * Gets the group.
     *
     * @return the group
     */
    public int getGroup() {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group
     *            the new group
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * The Class Fields.
     */
    public static final class Fields extends BaseSecurityPO.Fields {

        /**
         * Instantiates a new fields.
         */
        private Fields() {
            super();
        }

        /** The Constant S_USER_ID. */
        public static final String S_USER_ID = "S_USER_ID";

        /** The Constant S_LABEL_ATTRIBUTE_ID. */
        public static final String S_LABEL_ATTRIBUTE_ID = "S_LABEL_ATTRIBUTE_ID";

        /** The Constant VALUE. */
        public static final String VALUE = "VALUE";

        /** The Constant S_LABEL_GROUP. */
        public static final String S_LABEL_GROUP = "S_LABEL_GROUP";

        /** All fields combined. */
        public static final String ALL = String.join(DELIMETER, ID, S_USER_ID, S_LABEL_ATTRIBUTE_ID, VALUE,
                S_LABEL_GROUP, CREATED_AT, UPDATED_AT, CREATED_BY, UPDATED_BY);
    }
}