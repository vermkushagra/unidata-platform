/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Map;

import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.EnumerationDataType;

/**
 * @author Mikhail Mikhailov
 *
 */
public class EnumerationWrapper extends ModelWrapper {

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
    public EnumerationWrapper(EnumerationDataType enumeration) {
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
    public String getUniqueIdentifier() {
        return enumeration.getName();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return enumeration.getVersion();
    }
}
