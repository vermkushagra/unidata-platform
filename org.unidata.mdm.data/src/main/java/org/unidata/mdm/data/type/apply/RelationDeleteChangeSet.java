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

package org.unidata.mdm.data.type.apply;

import java.util.ArrayList;
import java.util.List;

import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;

/**
 * @author Mikhail Mikhailov
 * Relation delete change set.
 */
public class RelationDeleteChangeSet extends RelationChangeSet {
    /**
     * Wipe records.
     */
    protected final List<RelationKeysPO> wipeRelationKeys = new ArrayList<>(2);
    /**
     * External keys to wipe.
     */
    protected final List<RelationExternalKeyPO> wipeExternalKeys = new ArrayList<>(8);
    /**
     * Constructor.
     */
    public RelationDeleteChangeSet() {
        super();
    }
    /**
     * Gets the elements to wipe.
     * @return the wipe
     */
    public List<RelationKeysPO> getWipeRelationKeys() {
        return wipeRelationKeys;
    }
    /**
     * @return the wipeExternalKeys
     */
    public List<RelationExternalKeyPO> getWipeExternalKeys() {
        return wipeExternalKeys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return wipeRelationKeys.isEmpty()
            && wipeExternalKeys.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        wipeRelationKeys.clear();
        wipeExternalKeys.clear();
        super.clear();
    }
}
