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

/**
 *
 */
package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ComplexAttributeExpansion implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 191071160016394539L;
    /**
     * Level of expansion.
     */
    private int level;
    /**
     * Do expand or not.
     */
    private boolean expand;
    /**
     * Index of the nested data record.
     */
    private Integer index;
    /**
     * Name of the key attribute.
     */
    private String keyAttribute;
    /**
     *
     */
    public ComplexAttributeExpansion() {
        super();
    }
    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    /**
     * @return the expand
     */
    public boolean isExpand() {
        return expand;
    }
    /**
     * @param expand the expand to set
     */
    public void setExpand(boolean expand) {
        this.expand = expand;
    }
    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }
    /**
     * @param index the index to set
     */
    public void setIndex(Integer index) {
        this.index = index;
    }
    /**
     * @return the keyAttribute
     */
    public String getKeyAttribute() {
        return keyAttribute;
    }
    /**
     * @param keyAttribute the keyAttribute to set
     */
    public void setKeyAttribute(String keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

}
