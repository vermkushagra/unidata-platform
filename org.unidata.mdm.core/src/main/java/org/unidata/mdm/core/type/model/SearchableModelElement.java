package org.unidata.mdm.core.type.model;

import java.util.Collection;
/**
 * Marks searchable model elements.
 *
 * @author Mikhail Mikhailov
 */
public interface SearchableModelElement extends AttributedModelElement {
    /**
     * Gets the model search object description.
     * @return the description
     */
    ModelSearchObject getSearchObject();

    ModelSearchObject getModelSearchElement();
}
