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
package org.unidata.mdm.meta.type.parse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.type.info.impl.EnumerationInfoHolder;

/**
 * @author Mikhail Mikhailov
 * Enumerations parser.
 */
public class EnumerationsParser implements ModelParser<EnumerationInfoHolder> {

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
    public Map<String, EnumerationInfoHolder> parse(Model model){
        final Map<String, EnumerationInfoHolder> enumeratios = new ConcurrentHashMap<>();
        List<EnumerationDataType> defs = model.getEnumerations();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            enumeratios.put(defs.get(i).getName(), new EnumerationInfoHolder(defs.get(i)));
        }
        return enumeratios;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EnumerationInfoHolder> getValueType() {
        return EnumerationInfoHolder.class;
    }

}
