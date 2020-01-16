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

import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;

import org.unidata.mdm.core.po.LargeObjectPO;

/**
 * @author Mikhail Mikhailov
 * Upsert specific suff.
 */
public class RecordUpsertChangeSet extends RecordChangeSet {
    /**
     * Etalon record persistant object.
     */
    protected RecordEtalonPO etalonRecordInsertPO;
    /**
     * Origin record persistant objects.
     */
    protected final List<RecordOriginPO> originRecordInsertPOs = new ArrayList<>(2);
    /**
     * External keys PO objects.
     */
    protected final List<RecordExternalKeysPO> externalKeysPOs = new ArrayList<>(2);
    /**
     * Binary data, possibly existing.
     */
    protected final List<LargeObjectPO> largeObjectPOs = new ArrayList<>(2);
    /**
     * Constructor.
     */
    public RecordUpsertChangeSet() {
        super();
    }
    /**
     * @return the etalonRecordPO
     */
    public RecordEtalonPO getEtalonRecordInsertPO() {
        return etalonRecordInsertPO;
    }
    /**
     * @param etalonRecordPO the etalonRecordPO to set
     */
    public void setEtalonRecordInsertPO(RecordEtalonPO etalonRecordPO) {
        this.etalonRecordInsertPO = etalonRecordPO;
    }
    /**
     * @return the originRecordPOs
     */
    public List<RecordOriginPO> getOriginRecordInsertPOs() {
        return originRecordInsertPOs;
    }
    /**
     * @return the recordExternalKeysPOs
     */
    public List<RecordExternalKeysPO> getExternalKeysInsertPOs() {
        return externalKeysPOs;
    }
    /**
     * @return the largeObjectPOs
     */
    public List<LargeObjectPO> getLargeObjectPOs() {
        return largeObjectPOs;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return etalonRecordInsertPO == null
            && originRecordInsertPOs.isEmpty()
            && largeObjectPOs.isEmpty()
            && externalKeysPOs.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        etalonRecordInsertPO = null;
        originRecordInsertPOs.clear();
        largeObjectPOs.clear();
        externalKeysPOs.clear();
        super.clear();
    }
}
