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

package org.unidata.mdm.meta.dto;

import org.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Mikhail Mikhailov on Nov 29, 2019
 */
public class GetModelLookupDTO {
    /**
     * The lookup.
     */
    private LookupEntityDef lookup;
    /**
     * Constructor.
     */
    public GetModelLookupDTO() {
        super();
    }
    /**
     * Constructor.
     * @param lookup the lookup.
     */
    public GetModelLookupDTO(LookupEntityDef lookup) {
        super();
        this.lookup = lookup;
    }
    /**
     * @return the lookup
     */
    public LookupEntityDef getLookup() {
        return lookup;
    }
    /**
     * @param lookup the lookup to set
     */
    public void setLookup(LookupEntityDef lookup) {
        this.lookup = lookup;
    }
}
