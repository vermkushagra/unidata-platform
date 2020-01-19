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

import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;

/**
 * @author Mikhail Mikhailov
 * Relation upsert change set.
 */
public class RelationUpsertChangeSet extends RelationChangeSet {
    /**
     * Etalon relation insert PO.
     */
    protected RelationEtalonPO etalonRelationInsertPO;
    /**
     * Origin relation insert POs.
     */
    protected final List<RelationOriginPO> originRelationInsertPOs = new ArrayList<>(2);
    /**
     * External key POs.
     */
    protected final List<RelationExternalKeyPO> externalKeyInsertPOs = new ArrayList<>(2);
    /**
     * Constructor.
     */
    public RelationUpsertChangeSet() {
        super();
    }
    /**
     * @return the etalonRelationPO
     */
    public RelationEtalonPO getEtalonRelationInsertPO() {
        return etalonRelationInsertPO;
    }
    /**
     * @param etalonRelationPO the etalonRelationPO to set
     */
    public void setEtalonRelationInsertPO(RelationEtalonPO etalonRelationPO) {
        this.etalonRelationInsertPO = etalonRelationPO;
    }
    public boolean isEtalonUpdate() {
        return etalonRelationInsertPO == null && etalonRelationUpdatePOs != null;
    }
    public boolean isEtalonInsert() {
        return etalonRelationInsertPO != null && etalonRelationUpdatePOs == null;
    }
    /**
     * @return the originRelationPOs
     */
    public List<RelationOriginPO> getOriginRelationInsertPOs() {
        return originRelationInsertPOs;
    }
    /**
     * @return the externalKeyInsertPOs
     */
    public List<RelationExternalKeyPO> getExternalKeyInsertPOs() {
        return externalKeyInsertPOs;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return etalonRelationInsertPO == null
            && originRelationInsertPOs.isEmpty()
            && externalKeyInsertPOs.isEmpty()
            && super.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        etalonRelationInsertPO = null;
        originRelationInsertPOs.clear();
        externalKeyInsertPOs.clear();
        super.clear();
    }
}
