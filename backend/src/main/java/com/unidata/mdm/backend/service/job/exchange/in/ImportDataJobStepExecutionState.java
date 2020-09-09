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

package com.unidata.mdm.backend.service.job.exchange.in;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.common.keys.Keys;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeObject;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.service.data.batch.BatchKeyReference;
import com.unidata.mdm.backend.service.job.common.StepExecutionState;

/**
 * @author Mikhail Mikhailov
 * Various objects, which can not be otherwise transferred to destination.
 */
public final class ImportDataJobStepExecutionState implements StepExecutionState {
    /**
     * External ids cache. Exists for historical entities only.
     * Local to the block, being processed by this thread.
     */
    private Map<String, BatchKeyReference<? extends Keys>> ids;
    /**
     * The exchange object.
     */
    private ExchangeObject exchangeObject;
    /**
     * Default 'from' date (statisc value, found in the definition).
     * Used by both record and relation import.
     */
    private Date from;
    /**
     * Default 'to' date (statisc value, found in the definition).
     * Used by both record and relation import.
     */
    private Date to;
    /**
     * Code attribute alias pointers.
     * Used by records import.
     */
    private Collection<CodeAttributeAlias> codeAttributeAliases;
    /**
     * The from source system.
     * This is used by relations import and is part of the parent entity description.
     */
    private String fromSourceSystem;
    /**
     * The from entity name.
     * This is used by relations import and is part of the parent entity description.
     */
    private String fromEntityName;
    /**
     * Failed.
     */
    private long failed = 0L;
    /**
     * Skept.
     */
    private long skept = 0L;
    /**
     * Updated.
     */
    private long updated = 0L;
    /**
     * Inserted.
     */
    private long inserted = 0L;
    /**
     * Deleted.
     */
    private long deleted = 0L;
    /**
     * Classifiers failed.
     */
    private long classifiersFailed = 0L;
    /**
     * Classifiers skipped.
     */
    private long classifiersSkipped = 0L;
    /**
     * Classifiers updated.
     */
    private long classifiersUpdated = 0L;
    /**
     * Classifiers inserted.
     */
    private long classifiersInserted = 0L;
    /**
     * Classifiers deleted.
     */
    private long classifiersDeleted = 0L;
    /**
     * @return the exchangeObject
     */
    @SuppressWarnings("unchecked")
    public <T extends ExchangeObject> T  getExchangeObject() {
        return (T) exchangeObject;
    }
    /**
     * @param exchangeObject the exchangeObject to set
     */
    public void setExchangeObject(ExchangeObject exchangeObject) {
        this.exchangeObject = exchangeObject;

        boolean initIdCache;
        if (exchangeObjectIsEntity() && ((ExchangeEntity) exchangeObject).isMultiVersion()) {
            initIdCache = true;
        } else {
            initIdCache = (exchangeObjectIsContainmentRelation() && ((ContainmentRelation) exchangeObject).getEntity().isMultiVersion())
                       || (exchangeObjectIsRelatesToRelation() && ((RelatesToRelation) exchangeObject).isMultiVersion());
        }

        // Init keys cache for historical entities.
        if (initIdCache)  {
            ids = new HashMap<>();
        }
    }
    /**
     * Tells if this exchange object denotes an entity.
     * @return true, if so, false otherwise
     */
    public boolean exchangeObjectIsEntity() {
        return exchangeObject != null && exchangeObject instanceof ExchangeEntity;
    }
    /**
     * Tells if this exchange object denotes a relation.
     * @return true, if so, false otherwise
     */
    public boolean exchangeObjectIsRelation() {
        return exchangeObject != null && exchangeObject instanceof ExchangeRelation;
    }
    /**
     * Tells if this exchange object denotes a containment relation.
     * @return true, if so, false otherwise
     */
    public boolean exchangeObjectIsContainmentRelation() {
        return exchangeObjectIsRelation() && exchangeObject instanceof ContainmentRelation;
    }
    /**
     * Tells if this exchange object denotes a 'relates to' relation.
     * @return true, if so, false otherwise
     */
    public boolean exchangeObjectIsRelatesToRelation() {
        return exchangeObjectIsRelation() && exchangeObject instanceof RelatesToRelation;
    }
    /**
     * @return the aliasCodeAttributePointers
     */
    public Collection<CodeAttributeAlias> getCodeAttributeAliases() {
        return codeAttributeAliases;
    }
    /**
     * Serts pointers.
     * @param aliasCodeAttributePointers
     */
    public void setCodeAttributeAliases(Collection<CodeAttributeAlias> aliasCodeAttributePointers) {
        this.codeAttributeAliases = aliasCodeAttributePointers;
    }
    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }
    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }
    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }
    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }
    /**
     * @return the fromSourceSystem
     */
    public String getFromSourceSystem() {
        return fromSourceSystem;
    }
    /**
     * @param fromSourceSystem the fromSourceSystem to set
     */
    public void setFromSourceSystem(String fromSourceSystem) {
        this.fromSourceSystem = fromSourceSystem;
    }
    /**
     * @return the fromEntityName
     */
    public String getFromEntityName() {
        return fromEntityName;
    }
    /**
     * @param fromEntityName the fromEntityName to set
     */
    public void setFromEntityName(String fromEntityName) {
        this.fromEntityName = fromEntityName;
    }
    /**
     * @param failed the failed to set
     */
    public void incrementFailed(long failed) {
        this.failed += failed;
    }
    /**
     * @param skept the skept to set
     */
    public void incrementSkept(long skept) {
        this.skept += skept;
    }
    /**
     * @param updated the updated to set
     */
    public void incrementUpdated(long updated) {
        this.updated += updated;
    }
    /**
     * @param inserted the inserted to set
     */
    public void incrementInserted(long inserted) {
        this.inserted += inserted;
    }
    /**
     * @param deleted the deleted to set
     */
    public void incrementDeleted(long deleted) {
        this.deleted += deleted;
    }
    /**
     * @return the failed
     */
    public long getFailed() {
        return failed;
    }
    /**
     * @return the skept
     */
    public long getSkept() {
        return skept;
    }
    /**
     * @return the updated
     */
    public long getUpdated() {
        return updated;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
    /**
     * @return the deleted
     */
    public long getDeleted() {
        return deleted;
    }
    /**
     * @return the ids
     */
    public Map<String, BatchKeyReference<? extends Keys>> getIdCache() {
        return ids;
    }
    /**
     * @return the ids
     */
    public Map<String, BatchKeyReference<? extends Keys>> getIds() {
        return ids;
    }
    /**
     * @return the classifiersFailed
     */
    public long getClassifiersFailed() {
        return classifiersFailed;
    }
    /**
     * @return the classifiersSkipped
     */
    public long getClassifiersSkipped() {
        return classifiersSkipped;
    }
    /**
     * @return the classifiersUpdated
     */
    public long getClassifiersUpdated() {
        return classifiersUpdated;
    }
    /**
     * @return the classifiersInserted
     */
    public long getClassifiersInserted() {
        return classifiersInserted;
    }
    /**
     * @return the classifiersDeleted
     */
    public long getClassifiersDeleted() {
        return classifiersDeleted;
    }
    /**
     * @param failed the failed to set
     */
    public void incrementClassifiersFailed(long failed) {
        this.classifiersFailed += failed;
    }
    /**
     * @param skept the skept to set
     */
    public void incrementClassifiersSkipped(long skept) {
        this.classifiersSkipped += skept;
    }
    /**
     * @param updated the updated to set
     */
    public void incrementClassifiersUpdated(long updated) {
        this.classifiersUpdated += updated;
    }
    /**
     * @param inserted the inserted to set
     */
    public void incrementClassifiersInserted(long inserted) {
        this.classifiersInserted += inserted;
    }
    /**
     * @param deleted the deleted to set
     */
    public void incrementClassifiersDeleted(long deleted) {
        this.classifiersDeleted += deleted;
    }
}
