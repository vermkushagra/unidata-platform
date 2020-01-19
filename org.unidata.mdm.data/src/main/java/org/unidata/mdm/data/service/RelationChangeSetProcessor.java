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

package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
/**
 * @author Mikhail Mikhailov
 * Basic relation change set processor.
 */
public interface RelationChangeSetProcessor {
    /**
     * Applies upsert change set to DB and index.
     * @param set the set to apply
     */
    void apply(RelationUpsertChangeSet set);
    /**
     * Applies delete change set to DB and index.
     * @param set the set to apply
     */
    void apply(RelationDeleteChangeSet set);
    /**
     * Applies merge change set to DB and index.
     * @param set the set to apply
     */
    void apply(RelationMergeChangeSet set);
}