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

/**
 * @author Dmitry Kopin on 18.10.2018.
 */
public class MetaCustomPropertiesConstants {

    private MetaCustomPropertiesConstants() {
        super();
    }

    public static final String SEARCH_SHARDS_NUMBER = "unidata_search_shards_number";

    public static final String SEARCH_REPLICAS_NUMBER = "unidata_search_replicas_number";

    public static final String SKIP_RELATION_VALIDATION = "unidata_skip_relation_validation";

    public static final String CUSTOM_TOKENIZE_PROPERTY = "tokenize_on_chars";
}

