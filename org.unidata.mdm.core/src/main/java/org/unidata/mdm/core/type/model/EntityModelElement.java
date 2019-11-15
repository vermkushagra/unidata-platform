package org.unidata.mdm.core.type.model;

import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * @author Mikhail Mikhailov
 * Marks an abstract entity holder.
 */
public interface EntityModelElement extends ContainerModelElement, AttributedModelElement {
    /**
     * Tells, whether this element is a top level entity.
     * @return true, for entities, false otherwise
     */
    boolean isEntity();
    /**
     * Tells, whether this element is a top level lookup.
     * @return true, for lookups, false otherwise
     */
    boolean isLookup();
    /**
     * Tells, whether this element is a top level relation.
     * @return true, for relations, false otherwise
     */
    boolean isRelation();
    /**
     * Tells, whether this element is BVT capable.
     * @return true, for BVT capable, false otherwise
     */
    boolean isBvtCapable();
    /**
     * Returns the BVT element.
     * @return BVT element
     */
    BvtMapModelElement getBvt();
    /**
     * Gets code attributed element, if this is one (i. e. {@link #isLookup()} returns true) or null
     * @return code attributed or null
     */
    default CodeAttributedModelElement getCodeAttributed() {
        return null;
    }
    /**
     * Gets custom properties defined on the entity, if any.
     * @return map
     */
    Map<String, String> getCustomProperties();
    /**
     * Gets the validity period start.
     * @return the validityStart
     */
    @Nullable
    Date getValidityStart();
    /**
     * Gets the validity period end.
     * @return the validityEnd
     */
    @Nullable
    Date getValidityEnd();
}
