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

package com.unidata.mdm.backend.common.dto.classifier.model;

import java.util.Date;

import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;

/**
 * The Class ClsfNodeSimpleAttrDTO.
 */
public class ClsfNodeSimpleAttrDTO extends ClsfNodeAttrDTO {

    /**
     * Enum data type.
     */
    private String enumDataType;

    /** The default value. */
    private Object value;

    /**
     * Instantiates a new clsf node attr DTO.
     */
    public ClsfNodeSimpleAttrDTO() {
    }

    /**
     * Instantiates a new clsf node attr DTO.
     *
     * @param name the name
     * @param displayName the display name
     * @param description the description
     * @param dataType the data type
     * @param enumDataType the enum data type
     * @param lookupEntityType the lookup data type
     * @param readOnly the read only
     * @param hidden the hidden
     * @param nullable the nullable
     * @param inherited the inherited
     * @param unique the unique
     * @param searchable the searchable
     * @param value the value
     * @param createdAt the created at
     * @param updatedAt the updated at
     * @param createdBy the created by
     * @param updatedBy the updated by
     */
    public ClsfNodeSimpleAttrDTO(
            final String name,
            final String displayName,
            final String description,
            final DataType dataType,
            final String enumDataType,
            final String lookupEntityType,
            final CodeAttribute.CodeDataType lookupEntityCodeAttributeType,
            final boolean readOnly,
            final boolean hidden,
            final boolean nullable,
            final boolean inherited,
            final boolean unique,
            final boolean searchable,
            final Object value,
            final Date createdAt,
            final Date updatedAt,
            final String createdBy,
            final String updatedBy,
            final int order
    ) {
        super(
                name,
                displayName,
                description,
                dataType,
                lookupEntityType,
                lookupEntityCodeAttributeType,
                readOnly,
                hidden,
                nullable,
                inherited,
                unique,
                searchable,
                createdAt,
                updatedAt,
                createdBy,
                updatedBy,
                order
        );
        this.enumDataType = enumDataType;
        this.value = value;
    }

    /**
     * Instantiates a new clsf node attr DTO.
     *
     * @param name the name
     * @param dataType the data type
     * @param enumDataType the enum data type
     * @param value the value
     * @param displayName the display name
     * @param description the description
     * @param hidden the hidden
     * @param nullable the nullable
     * @param readOnly the read only
     * @param searchable the searchable
     * @param unique the unique
     * @param createdBy the created by
     * @param createdAt the created at
     */
    public ClsfNodeSimpleAttrDTO(
            final String name,
            final DataType dataType,
            final String enumDataType,
            final Object value,
            final String displayName,
            final String description,
            final boolean hidden,
            final boolean nullable,
            final boolean readOnly,
            final boolean searchable,
            final boolean unique,
            final String createdBy,
            final Date createdAt,
            final int order
    ) {
        super(
                name,
                dataType,
                displayName,
                description,
                hidden,
                nullable,
                readOnly,
                searchable,
                unique,
                createdBy,
                createdAt,
                order
        );
        this.enumDataType = enumDataType;
        this.value = value;
    }

    /**
     * Gets the enum data type.
     *
     * @return the enum data type
     */
    public String getEnumDataType() {
		return enumDataType;
	}

	/**
	 * Sets the enum data type.
	 *
	 * @param enumDataType the new enum data type
	 */
	public void setEnumDataType(String enumDataType) {
		this.enumDataType = enumDataType;
	}

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public Object getDefaultValue() {
        return value;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue
     *            the new default value
     */
    public void setDefaultValue(Object defaultValue) {
        this.value = defaultValue;
    }
    @Override
    public boolean isSimple() {
        return true;
    }
}
