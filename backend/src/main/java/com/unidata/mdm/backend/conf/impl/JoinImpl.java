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


import com.unidata.mdm.backend.common.integration.exits.AfterJoinListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeJoinListener;
import com.unidata.mdm.conf.Join;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class JoinImpl extends Join {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -7108107634917811459L;

    /**
     * Before delete.
     */
    private final MultiValuedMap<String, BeforeJoinListener> beforeJoinInstances = new ArrayListValuedHashMap<>();

    /**
     * After delete.
     */
    private final MultiValuedMap<String, AfterJoinListener> afterJoinInstances = new ArrayListValuedHashMap<>();

    /**
     * @return the beforeJoinInstances
     */
    public MultiValuedMap<String, BeforeJoinListener> getBeforeJoinInstances() {
        return beforeJoinInstances;
    }


    /**
     * @return the afterJoinInstances
     */
    public MultiValuedMap<String, AfterJoinListener> getAfterJoinInstances() {
        return afterJoinInstances;
    }
}
