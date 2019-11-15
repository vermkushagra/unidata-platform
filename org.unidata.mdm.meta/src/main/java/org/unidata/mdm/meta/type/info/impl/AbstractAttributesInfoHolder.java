package org.unidata.mdm.meta.type.info.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributedModelElement;

/**
 * @author Mikhail Mikhailov
 * Base entity wrapper type.
 */
public abstract class AbstractAttributesInfoHolder implements AttributedModelElement {
    /**
     * Attributes map.
     */
    private final Map<String, AttributeModelElement> attrs;
    /**
     * Constructor.
     */
    public AbstractAttributesInfoHolder(final Map<String, AttributeModelElement> attrs) {
        super();
        this.attrs = attrs;
    }
    /**
     * @return the attrs
     */
    @Override
    public Map<String, AttributeModelElement> getAttributes() {
        return attrs;
    }

    //todo create attribute structure!
    /**
     * Gets first main displayable attribute, if it exists.
     * @return attribute
     */
    public Pair<String, AttributeModelElement> getFirstMainDisplayableAttribute() {
        return attrs == null
                ? null
                : attrs.entrySet().stream()
                    .filter(e -> e.getValue().isMainDisplayable())
                    .findFirst()
                    .map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
                    .orElse(null);
    }

    /**
     * Gets all main displayable attributes, if it exists.
     *
     * @return attribute
     */
    public Collection<Pair<String, AttributeModelElement>> getMainDisplayableAttributes() {
        return attrs == null
                ? Collections.emptyList()
                : attrs.entrySet().stream()
                .filter(e -> e.getValue().isMainDisplayable())
                .map(e -> new ImmutablePair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
