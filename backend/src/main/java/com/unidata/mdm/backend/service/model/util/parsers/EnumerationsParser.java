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
package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 * Enumerations parser.
 */
public class EnumerationsParser implements ModelParser<EnumerationWrapper> {

    /**
     * Constructor.
     */
    public EnumerationsParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, EnumerationWrapper> parse(Model model){
        final Map<String, EnumerationWrapper> enumeratios = new ConcurrentHashMap<>();
        List<EnumerationDataType> defs = model.getEnumerations();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            enumeratios.put(defs.get(i).getName(), new EnumerationWrapper(defs.get(i)));
        }
        return enumeratios;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EnumerationWrapper> getValueType() {
        return EnumerationWrapper.class;
    }

}
