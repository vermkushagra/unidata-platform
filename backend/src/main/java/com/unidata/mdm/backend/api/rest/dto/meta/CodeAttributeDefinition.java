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

package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 29.05.2015.
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeAttributeDefinition extends AttributeDefinition {
    /**
     * Attribute is generally displayable.
     */
    private boolean displayable;
    /**
     * The attribute is the main displayable attribute.
     */
    private boolean mainDisplayable;
    /**
     * Input mask.
     */
    private String mask;
    /**
     * Ext. ID generation strategy.
     */
    private ExternalIdGenerationStrategyRO externalIdGenerationStrategy;
    /**
     * @return the displayable
     */
    public boolean isDisplayable() {
        return displayable;
    }

    /**
     * @param displayable the displayable to set
     */
    public void setDisplayable(boolean displayable) {
        this.displayable = displayable;
    }

    /**
     * @return the mainDisplayable
     */
    public boolean isMainDisplayable() {
        return mainDisplayable;
    }

    /**
     * @param mainDisplayable the mainDisplayable to set
     */
    public void setMainDisplayable(boolean mainDisplayable) {
        this.mainDisplayable = mainDisplayable;
    }

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

    /**
     * @return the externalIdGenerationStrategy
     */
    public ExternalIdGenerationStrategyRO getExternalIdGenerationStrategy() {
        return externalIdGenerationStrategy;
    }

    /**
     * @param externalIdGenerationStrategy the externalIdGenerationStrategy to set
     */
    public void setExternalIdGenerationStrategy(ExternalIdGenerationStrategyRO externalIdGenerationStrategy) {
        this.externalIdGenerationStrategy = externalIdGenerationStrategy;
    }
}
