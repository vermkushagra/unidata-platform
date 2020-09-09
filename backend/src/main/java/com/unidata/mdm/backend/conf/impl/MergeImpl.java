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


import com.unidata.mdm.backend.common.integration.exits.MergeListener;
import com.unidata.mdm.conf.Merge;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;


/**
 * @author Mikhail Mikhailov
 *
 */
public class MergeImpl extends Merge {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -7108107634917811459L;

    /**
     * Before delete.
     */
    private final MultiValuedMap<String, MergeListener> beforeMergeInstances = new ArrayListValuedHashMap<>();

    /**
     * After delete.
     */
    private final MultiValuedMap<String, MergeListener> afterMergeInstances = new ArrayListValuedHashMap<>();

    /**
     * Constructor.
     */
    public MergeImpl() {
        super();
    }

    /**
     * @return the beforeMergeInstances
     */
    public MultiValuedMap<String, MergeListener> getBeforeMergeInstances() {
        return beforeMergeInstances;
    }


    /**
     * @return the afterMergeInstances
     */
    public MultiValuedMap<String, MergeListener> getAfterMergeInstances() {
        return afterMergeInstances;
    }

}
