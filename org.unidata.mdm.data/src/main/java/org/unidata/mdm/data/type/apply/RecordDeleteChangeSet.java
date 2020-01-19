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

import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;

/**
 * @author Mikhail Mikhailov
 * Delete related stuff.
 */
public class RecordDeleteChangeSet extends RecordChangeSet {
    /**
     * Record keys to wipe.
     */
    protected final List<RecordKeysPO> wipeRecordKeys = new ArrayList<>(4);
    /**
     * External keys to wipe.
     */
    protected final List<RecordExternalKeysPO> wipeExternalKeys = new ArrayList<>(8);
    /**
     * Constructor.
     */
    public RecordDeleteChangeSet() {
        super();
    }
    /**
     * Gets collection of records to wipe.
     * @return collection
     */
    public List<RecordKeysPO> getWipeRecordKeys() {
        return wipeRecordKeys;
    }
    /**
     * Gets collection of external key records to wipe.
     * @return collection
     */
    public List<RecordExternalKeysPO> getWipeExternalKeys() {
        return wipeExternalKeys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return wipeRecordKeys.isEmpty()
            && wipeExternalKeys.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        wipeRecordKeys.clear();
        wipeExternalKeys.clear();
        super.clear();
    }
}
