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

    default ModelSearchObject getModelSearchElement() {

        ModelSearchObject modelSearchObject = getSearchObject();
        Collection<AttributeModelElement> attributes = getAttributes().values();
        attributes.stream()
                .map(AttributeModelElement::getName)
                .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.attributeName, name));

        attributes.stream()
                .map(AttributeModelElement::getName)
                .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.attributeDisplayName, name));

        return modelSearchObject;
    }
}
