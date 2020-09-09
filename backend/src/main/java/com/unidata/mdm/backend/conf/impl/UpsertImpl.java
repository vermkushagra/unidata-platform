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
package com.unidata.mdm.backend.conf.impl;


import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.integration.exits.UpsertRelationListener;
import com.unidata.mdm.conf.Upsert;


/**
 * @author Mikhail Mikhailov
 */
public class UpsertImpl extends Upsert {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -3023457488036107833L;
    /**
     * Before origin upsert.
     */
    private final MultiValuedMap<String, UpsertListener> beforeOriginUpsertInstances = new ArrayListValuedHashMap<>();
    /**
     * After origin upsert.
     */
    private final MultiValuedMap<String, UpsertListener> afterOriginUpsertInstances = new ArrayListValuedHashMap<>();
    /**
     * After etalon composition.
     */
    private final MultiValuedMap<String, UpsertListener> afterEtalonCompositionInstances = new ArrayListValuedHashMap<>();
    /**
     * Before origin relation upsert.
     */
    private final MultiValuedMap<String, UpsertRelationListener> beforeOriginRelationUpsertInstances = new ArrayListValuedHashMap<>();
    /**
     * After origin relation upsert.
     */
    private final MultiValuedMap<String, UpsertRelationListener> afterOriginRelationUpsertInstances = new ArrayListValuedHashMap<>();
    /**
     * After complete.
     */
    private final MultiValuedMap<String, UpsertListener> afterCompleteInstances = new ArrayListValuedHashMap<>();
    /**
     * Constructor.
     */
    public UpsertImpl() {
        super();
    }
    /**
     * @return the beforeOriginUpsertInstances
     */
    public MultiValuedMap<String, UpsertListener> getBeforeOriginUpsertInstances() {
        return beforeOriginUpsertInstances;
    }
    /**
     * @return the afterOriginUpsertInstances
     */
    public MultiValuedMap<String, UpsertListener> getAfterOriginUpsertInstances() {
        return afterOriginUpsertInstances;
    }
    /**
     * @return the afterEtalonCompositionInstances
     */
    public MultiValuedMap<String, UpsertListener> getAfterEtalonCompositionInstances() {
        return afterEtalonCompositionInstances;
    }
    /**
     * @return the afterCompleteInstances
     */
    public MultiValuedMap<String, UpsertListener> getAfterCompleteInstances() {
        return afterCompleteInstances;
    }
    /**
     * @return the beforeOriginUpsertInstances
     */
    public MultiValuedMap<String, UpsertRelationListener> getBeforeOriginRelationUpsertInstances() {
        return beforeOriginRelationUpsertInstances;
    }
    /**
     * @return the afterOriginUpsertInstances
     */
    public MultiValuedMap<String, UpsertRelationListener> getAfterOriginRelationUpsertInstances() {
        return afterOriginRelationUpsertInstances;
    }

}
