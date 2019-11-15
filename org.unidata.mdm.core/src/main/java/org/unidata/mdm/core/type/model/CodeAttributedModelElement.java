package org.unidata.mdm.core.type.model;

import java.util.Collection;
import java.util.Collections;

/**
 * Code attribute holder element.
 * @author Mikhail Mikhailov on Nov 7, 2019
 */
public interface CodeAttributedModelElement {
    /**
     * Gets the code attribute.
     * @return
     */
    default AttributeModelElement getCodeAttribute() {
        return null;
    }
    /**
     * Gets code alternative attributes.
     * @return attributes collection
     */
    default Collection<AttributeModelElement> getCodeAliases() {
        return Collections.emptyList();
    }
}
