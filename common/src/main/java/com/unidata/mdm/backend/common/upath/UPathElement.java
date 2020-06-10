package com.unidata.mdm.backend.common.upath;

import java.util.function.Predicate;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Mikhail Mikhailov
 * UPath element.
 */
public class UPathElement {
    /**
     * Predicate to evaluate.
     */
    private final Predicate<DataRecord> predicate;
    /**
     * Path element.
     */
    private final String element;
    /**
     * Type of element.
     */
    private final UPathElementType type;
    /**
     * Element MM info.
     */
    private final AttributeInfoHolder info;
    /**
     * Constructor.
     * @param element the original element
     * @param type element type {@link UPathElementType}.
     * @param p the filtering predicate if any
     * @param info attribute info
     */
    public UPathElement(String element, UPathElementType type, Predicate<DataRecord> p, AttributeInfoHolder info) {
        super();
        this.element = element;
        this.type = type;
        this.predicate = p;
        this.info = info;
    }
    /**
     * @return the predicate
     */
    public Predicate<DataRecord> getPredicate() {
        return predicate;
    }
    /**
     * @return the element
     */
    public String getElement() {
        return element;
    }
    /**
     * @return the type
     */
    public UPathElementType getType() {
        return type;
    }
    /**
     * @return the info
     */
    public AttributeInfoHolder getInfo() {
        return info;
    }
    /**
     * Combo for element type.
     * @return true if filtering, false for collecting
     */
    public boolean isFiltering() {
        return type == UPathElementType.EXPRESSION || type == UPathElementType.SUBSCRIPT;
    }
    /**
     * Combo for element type.
     * @return true if collecting, false for filtering
     */
    public boolean isCollecting() {
        return type == UPathElementType.COLLECTING;
    }
}