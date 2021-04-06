package com.unidata.mdm.backend.po;


import java.util.Date;

import com.unidata.mdm.backend.service.model.ModelType;

/**
 * Meta model custom property persistent object
 */
public class MetaModelCustomPropertyPO extends AbstractPO {

    public static final String TABLE_NAME = "meta_model_custom_property";

    public static final String PK_META_MODEL_CUSTOM_PROPERTY = "pk_meta_model_custom_property";

    public static final String FIELD_META_MODEL_ELEMENT_ID = "meta_model_element_id";

    public static final String FIELD_META_MODEL_ELEMENT_TYPE = "meta_model_element_type";

    public static final String FIELD_NAME = "name";

    public static final String FIELD_VALUE = "value";

    /**
     * Meta model element id which contains custom property.
     */
    private String metaModelElementId;

    /**
     * Meta model element type.
     */
    private ModelType metaModelElementType;

    /**
     * Custom property name.
     */
    private String name;

    /**
     * Custom property value.
     */
    private String value;

    public MetaModelCustomPropertyPO() {
    }

    public String getMetaModelElementId() {
        return metaModelElementId;
    }

    public void setMetaModelElementId(String metaModelElementId) {
        this.metaModelElementId = metaModelElementId;
    }

    public ModelType getMetaModelElementType() {
        return metaModelElementType;
    }

    public void setMetaModelElementType(ModelType metaModelElementType) {
        this.metaModelElementType = metaModelElementType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
