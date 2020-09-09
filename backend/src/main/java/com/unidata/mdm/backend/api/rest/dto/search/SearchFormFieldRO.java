/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
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
     * Inverted flag
     */
    private final boolean inverted;

    private SearchTypeRO searchTypeRO;


    /**
     * Constructor.
     */
    public SearchFormFieldRO(SimpleDataType type, String path, Object single, boolean inverted, SearchTypeRO searchType) {
        super();
        this.type = type;
        this.path = path;
        this.single = single;
        this.range = null;
        this.inverted = inverted;
        this.searchTypeRO = searchType;
    }

    /**
     * Constructor for ranges.
     */
    public SearchFormFieldRO(SimpleDataType type, String path, Pair<Object, Object> range, boolean inverted, SearchTypeRO searchType) {
        super();
        this.type = type;
        this.path = path;
        this.single = null;
        this.range = range;
        this.inverted = inverted;
        this.searchTypeRO = searchType;
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
        return SearchTypeRO.LIKE.equals(searchTypeRO);
    }

    /**
     *
     * @return is it start with form
     */
    public boolean isStartWith() {
        return SearchTypeRO.START_WITH.equals(searchTypeRO);
    }
    /**
     *
     * @return is it fuzzy form
     */
    public boolean isFuzzy() {
        return SearchTypeRO.FUZZY.equals(searchTypeRO);
    }

    /**
     * @return the morphological
     */
    public boolean isMorphological() {
        return SearchTypeRO.MORPHOLOGICAL.equals(searchTypeRO);
    }

    public SearchTypeRO getSearchTypeRO() {
        return searchTypeRO;
    }

    public void setSearchTypeRO(SearchTypeRO searchTypeRO) {
        this.searchTypeRO = searchTypeRO;
    }

    public enum SearchTypeRO{
        DEFAULT,
        EXACT,
        FUZZY,
        MORPHOLOGICAL,
        EXIST,
        START_WITH,
        LIKE,
        RANGE
    }
}
