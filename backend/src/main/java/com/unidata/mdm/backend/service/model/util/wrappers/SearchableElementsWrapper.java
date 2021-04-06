package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Collection;
import java.util.Map;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.meta.AbstractAttributeDef;

public abstract class SearchableElementsWrapper extends AttributesWrapper {

    /**
     * Constructor.
     *
     * @param id
     * @param attrs
     */
    public SearchableElementsWrapper(String id, Map<String, AttributeInfoHolder> attrs) {
        super(id, attrs);
    }

    protected abstract ModelSearchObject getSearchObject();

    public ModelSearchObject getModelSearchElement() {
        ModelSearchObject modelSearchObject = getSearchObject();
        Collection<AttributeInfoHolder> attributes = this.getAttributes().values();
        attributes.stream()
                .map(AttributeInfoHolder::getAttribute)
                .map(AbstractAttributeDef::getName)
                .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.attributeName, name));

        attributes.stream()
                .map(AttributeInfoHolder::getAttribute)
                .map(AbstractAttributeDef::getDisplayName)
                .forEach(name -> modelSearchObject.addSearchElement(ModelSearchObject.SearchElementType.attributeDisplayName, name));
        return modelSearchObject;
    }

}
