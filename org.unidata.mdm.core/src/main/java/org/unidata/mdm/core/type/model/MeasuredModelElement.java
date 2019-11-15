package org.unidata.mdm.core.type.model;

/**
 * @author Mikhail Mikhailov
 * Measured value adjuster.
 */
public interface MeasuredModelElement {
    /**
     * Value id
     * @return id
     */
    String getValueId();
    /**
     * Default unit id.
     * @return id
     */
    String getDefaultUnitId();
}
