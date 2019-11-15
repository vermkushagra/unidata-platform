package org.unidata.mdm.meta.type.info.impl;

import java.util.Map;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.util.ModelUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public class EnumerationInfoHolder implements IdentityModelElement {
    /**
     * Enumeration
     */
    private final EnumerationDataType enumeration;
    /**
     * Enumeration map.
     */
    private final Map<String, String> enumerationMap;
    /**
     * Constructor.
     */
    public EnumerationInfoHolder(EnumerationDataType enumeration) {
        super();
        this.enumeration = enumeration;
        this.enumerationMap = ModelUtils.createEnumerationMap(enumeration);
    }

    /**
     * @return the enumeration
     */
    public EnumerationDataType getEnumeration() {
        return enumeration;
    }

    /**
     * @return the enumerationMap
     */
    public Map<String, String> getEnumerationMap() {
        return enumerationMap;
    }

    @Override
    public String getId() {
        return enumeration.getName();
    }

    @Override
    public Long getVersion() {
        return enumeration.getVersion();
    }
}
