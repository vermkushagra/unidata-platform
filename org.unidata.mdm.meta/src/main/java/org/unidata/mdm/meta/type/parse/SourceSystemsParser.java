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

package org.unidata.mdm.meta.type.parse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.type.info.impl.SourceSystemInfoHolder;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SourceSystemsParser implements ModelParser<SourceSystemInfoHolder> {

    /**
     * Constructor.
     */
    public SourceSystemsParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SourceSystemInfoHolder> parse(Model model){

        final Map<String, SourceSystemInfoHolder> sourceSystems = new ConcurrentHashMap<>();
        List<SourceSystemDef> defs = model.getSourceSystems();
        for (int i = 0; defs != null && i < defs.size(); i++) {
            String systemName = defs.get(i).getName();
            sourceSystems.put(systemName, new SourceSystemInfoHolder(defs.get(i)));
        }

        return sourceSystems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<SourceSystemInfoHolder> getValueType() {
        return SourceSystemInfoHolder.class;
    }

}
