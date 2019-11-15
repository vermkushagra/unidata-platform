package org.unidata.mdm.core.type.model;

import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Attributes map wrapper.
 */
public interface AttributedModelElement {
    /**
     * Gets attributes map.
     * @return map
     */
    Map<String, AttributeModelElement> getAttributes();
}
