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

import java.util.Map;

import org.unidata.mdm.core.type.model.ModelElement;
import org.unidata.mdm.meta.Model;

/**
 * The Interface ModelParser.
 *
 * @param <V>
 *            the value type
 *            @author ilya.bykov
 */
public interface ModelParser<V extends ModelElement> {

    /**
     * Parse meta model to Map<String, V>.
     *
     * @param model
     *            meta model {@link Model}
     * @return result of parsing.
     * @throws Exception
     *             the exception
     */
    Map<String, V> parse(Model model);

    /**
     * Gets the value class.
     *
     * @return the value class
     */
    Class<V> getValueType();
}
