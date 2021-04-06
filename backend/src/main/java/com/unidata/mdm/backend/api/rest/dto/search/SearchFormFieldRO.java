/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.search;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.SearchFormFieldDeserializer;

/**
 * @author Mikhail Mikhailov
 * REST search form field.
 */
@JsonDeserialize(using = SearchFormFieldDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchFormFieldRO {

    /**
     * Search type.
     */
    private final SimpleDataType type;
    /**
     * Path.
     */
    private final String path;
    /**
     * Single value.
     */
    private final Object single;
    /**
     * Range.
     */
    private final Pair<Object, Object> range;

    /**
     * inverted
     */
    private final boolean inverted;
    /**
     * like
     */
    private final boolean like;
    /**
     * start with
     */
    private final boolean startWith;
    /**
     * fuzzy
     */
    private final boolean fuzzy;
    /**
     * Search ефлштп morphology into account.
     */
    private final boolean morphological;
    /**
     * Constructor.
     */
    public SearchFormFieldRO(SimpleDataType type, String path, Object single, boolean inverted, boolean like, boolean startWith, boolean morphological) {
        super();
        this.type = type;
        this.path = path;
        this.single = single;
        this.range = null;
        this.inverted = inverted;
        this.like = like;
        this.startWith = startWith;
        this.fuzzy = false;
        this.morphological = morphological;
    }

    /**
     * Constructor.
     */
    public SearchFormFieldRO(SimpleDataType type, String path, Object single,
            boolean inverted, boolean like, boolean startWith, boolean fuzzy, boolean morphological) {
        super();
        this.type = type;
        this.path = path;
        this.single = single;
        this.range = null;
        this.inverted = inverted;
        this.like = like;
        this.startWith = startWith;
        this.fuzzy = fuzzy;
        this.morphological = morphological;
    }

    /**
     * Constructor for ranges.
     */
    public SearchFormFieldRO(SimpleDataType type, String path, Pair<Object, Object> range, boolean inverted, boolean like, boolean startWith) {
        super();
        this.type = type;
        this.path = path;
        this.single = null;
        this.range = range;
        this.inverted = inverted;
        this.like = like;
        this.startWith = startWith;
        this.fuzzy = false;
        this.morphological = false;
    }

    /**
     * @return the type
     */
    public SimpleDataType getType() {
        return type;
    }


    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }


    /**
     * @return the single
     */
    public Object getSingle() {
        return single;
    }


    /**
     * @return the range
     */
    public Pair<Object, Object> getRange() {
        return range;
    }

    /**
     *
     * @return is it inverted form
     */
    public boolean isInverted() {
        return inverted;
    }

    /**
     *
     * @return is it like form
     */
    public boolean isLike() {
        return like;
    }

    /**
     *
     * @return is it start with form
     */
    public boolean isStartWith() {
        return startWith;
    }
    /**
     *
     * @return is it fuzzy form
     */
    public boolean isFuzzy() {
        return fuzzy;
    }

    /**
     * @return the morphological
     */
    public boolean isMorphological() {
        return morphological;
    }
}
