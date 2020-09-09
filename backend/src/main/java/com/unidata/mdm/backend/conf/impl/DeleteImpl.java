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


import com.unidata.mdm.backend.common.integration.exits.DeleteListener;
import com.unidata.mdm.backend.common.integration.exits.DeleteRelationListener;
import com.unidata.mdm.conf.Delete;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteImpl extends Delete {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -8343557084870357948L;

    /**
     * Before delete.
     */
    private final MultiValuedMap<String, DeleteListener> beforeEtalonDeactivationInstances = new ArrayListValuedHashMap<>();

    /**
     * After delete.
     */
    private final MultiValuedMap<String, DeleteListener> afterEtalonDeactivationInstances = new ArrayListValuedHashMap<>();
    /**
     * Before relation delete.
     */
    private final MultiValuedMap<String, DeleteRelationListener> beforeRelationDeactivationInstances = new ArrayListValuedHashMap<>();
    /**
     * After relation delete.
     */
    private final MultiValuedMap<String, DeleteRelationListener> afterRelationDeactivationInstances = new ArrayListValuedHashMap<>();

    /**
     * Constructor.
     */
    public DeleteImpl() {
        super();
    }

    /**
     * @return the beforeEtalonDeactivationListeners
     */
    public MultiValuedMap<String, DeleteListener> getBeforeEtalonDeactivationInstances() {
        return beforeEtalonDeactivationInstances;

    }


    /**
     * @return the afterEtalonDeactivationListeners
     */
    public MultiValuedMap<String, DeleteListener> getAfterEtalonDeactivationInstances() {
        return afterEtalonDeactivationInstances;
    }


    public MultiValuedMap<String, DeleteRelationListener> getBeforeRelationDeactivationInstances() {
        return beforeRelationDeactivationInstances;
    }


    public MultiValuedMap<String, DeleteRelationListener> getAfterRelationDeactivationInstances() {
        return afterRelationDeactivationInstances;
    }
}
