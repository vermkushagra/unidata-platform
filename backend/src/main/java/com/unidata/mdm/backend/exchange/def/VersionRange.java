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

package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

/**
 * The Class VersionRange.
 *
 * @author Mikhail Mikhailov Version range definition.
 */
public class VersionRange implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2941387040423850682L;
    /**
     * Whether to normalize from part.
     */
    private boolean normalizeFrom;
    /**
     * Whether to normalize to part.
     */
    private boolean normalizeTo;
    /**
     * Valid from.
     */
    private ExchangeField validFrom;
    /**
     * Valid to.
     */
    private ExchangeField validTo;
    /**
     * Range status.
     */
    private ExchangeField isActive;
    /**
     * Constructor.
     */
    public VersionRange() {
        super();
    }

    /**
     * Checks if is normalize from.
     *
     * @return the normalizeFrom
     */
    public boolean isNormalizeFrom() {
        return normalizeFrom;
    }

    /**
     * Sets the normalize from.
     *
     * @param normalizeFrom
     *            the normalizeFrom to set
     */
    public void setNormalizeFrom(boolean normalizeFrom) {
        this.normalizeFrom = normalizeFrom;
    }

    /**
     * Checks if is normalize to.
     *
     * @return the normalizeTo
     */
    public boolean isNormalizeTo() {
        return normalizeTo;
    }

    /**
     * Sets the normalize to.
     *
     * @param normalizeTo
     *            the normalizeTo to set
     */
    public void setNormalizeTo(boolean normalizeTo) {
        this.normalizeTo = normalizeTo;
    }

    /**
     * Gets the valid from.
     *
     * @return the validFrom
     */
    public ExchangeField getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the valid from.
     *
     * @param validFrom
     *            the validFrom to set
     */
    public void setValidFrom(ExchangeField validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Gets the valid to.
     *
     * @return the validTo
     */
    public ExchangeField getValidTo() {
        return validTo;
    }

    /**
     * Sets the valid to.
     *
     * @param validTo
     *            the validTo to set
     */
    public void setValidTo(ExchangeField validTo) {
        this.validTo = validTo;
    }

    /**
     * Gets the checks if is active.
     *
     * @return the isActive
     */
    public ExchangeField getIsActive() {
        return isActive;
    }

    /**
     * Sets the checks if is active.
     *
     * @param isActive
     *            the isActive to set
     */
    public void setIsActive(ExchangeField isActive) {
        this.isActive = isActive;
    }
}
