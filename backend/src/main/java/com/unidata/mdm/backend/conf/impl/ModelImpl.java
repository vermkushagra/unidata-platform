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

package com.unidata.mdm.backend.conf.impl;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.unidata.mdm.backend.common.integration.exits.MetaModelListener;
import com.unidata.mdm.conf.Model;

/**
 * The Class ModelImpl.
 */
public class ModelImpl extends Model {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8376735987351212518L;



    /** The upsert draft instances. */
    private final MultiValuedMap<String, MetaModelListener> upsertDraftInstances = new ArrayListValuedHashMap<>();


    /** The apply model instances. */
    private final MultiValuedMap<String, MetaModelListener> applyModelInstances = new ArrayListValuedHashMap<>();


    /**
     * Instantiates a new model impl.
     */
    public ModelImpl() {
        super();
    }

 
    /**
     * Gets the upsert draft instances.
     *
     * @return the upsert draft instances
     */
    public MultiValuedMap<String, MetaModelListener> getUpsertDraftInstances() {
        return upsertDraftInstances;
    }


    /**
     * Gets the apply model instances.
     *
     * @return the apply model instances
     */
    public MultiValuedMap<String, MetaModelListener> getApplyModelInstances() {
        return applyModelInstances;
    }
}
