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

package org.unidata.mdm.meta.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;


/**
 * @author Dmitry Kopin on 31.05.2019.
 */
public interface LookupService {
    /**
     * Get lookup display name by code
     * @param lookupName lookup name
     * @param codeAttrValue code attr value
     * @param validFrom valid from for filter
     * @param validTo valid to for filter
     * @param toBuildAttrs custom display attributes list
     * @param useAttributeNameForDisplay use  attribute names for build display name
     * @return return pair of linkedEtalonId, Display name
     */
    Pair<String, String> getLookupDisplayNameById(String lookupName, Object codeAttrValue, Date validFrom, Date validTo, List<String> toBuildAttrs, boolean useAttributeNameForDisplay);
}
