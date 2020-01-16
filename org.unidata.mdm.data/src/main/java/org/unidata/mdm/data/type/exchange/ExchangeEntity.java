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
package org.unidata.mdm.data.type.exchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidata.mdm.data.type.exchange.csv.CsvExchangeEntity;
import org.unidata.mdm.data.type.exchange.db.DbExchangeEntity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class ExchangeEntity.
 *
 * @author Mikhail Mikhailov Exchange entity description.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvExchangeEntity.class, name = "CSV"),
    @Type(value = DbExchangeEntity.class, name = "DB")
})
public class ExchangeEntity implements ExchangeObject {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1861815907018501063L;
    /**
     * The name.
     */
    private String name;
    /**
     * Source system name.
     */
    private String sourceSystem;
    /**
     * Xlsx key field.
     */
    private String xlsxKey;
    /**
     * Natural key field.
     */
    private NaturalKey naturalKey;
    /**
     * System key field.
     */
    private SystemKey systemKey;

    /** The classifier mappings. */
    private List<ClassifierMapping> classifierMappings;
    /**
     * Fields.
     */
    private List<ExchangeField> fields;
    /**
     * Import order.
     */
    private int importOrder;
    /**
     * Import only unique records. Always true for lookup entities.
     */
    private boolean unique;
    /**
     * Version range.
     */
    private VersionRange versionRange;
    /**
     * The update mark.
     */
    private UpdateMark updateMark;
    /**
     * Dq errors section
     */
    private DqErrorsSection dqErrorsSection;
    /**
     * Containment relations.
     */
    private List<ContainmentRelation> contains;
    /**
     * RelationTo relations.
     */
    private List<RelatesToRelation> relates;
    /**
     * Settings.
     */
    private Map<String, Object> settings = new HashMap<>();
    /**
     * Prefetch keys from the following entity types.
     */
    private List<String> prefetchKeys = new ArrayList<>();
    /**
     * Whether updates are possible or not.
     */
    private boolean updates = false;
    /**
     * Skip DQ completly or not.
     */
    private boolean skipCleanse = false;
    /**
     * Skip DQ completly or not.
     */
    private boolean skipNotifications = false;
    /**
     * Has historical records.
     */
    private boolean multiVersion;
    /**
     * Import records.
     */
    private boolean processRecords = true;
    /**
     * Import classifiers.
     */
    private boolean processClassifiers = true;
    /**
     * Import relations.
     */
    private boolean processRelations = true;
    /**
     * Limit records count to the value.
     */
    private long maxRecordCount;
    /**
     * Swap dates, if start date greater than end date.
     */
    private boolean swapOddDates;
    /**
     * Constructor.
     */
    public ExchangeEntity() {
        super();
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public List<ExchangeField> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    /**
     * Sets the fields.
     *
     * @param fields
     *            the fields to set
     */
    public void setFields(List<ExchangeField> fields) {
        this.fields = fields;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the import order.
     *
     * @return the importOrder
     */
    public int getImportOrder() {
        return importOrder;
    }

    /**
     * Sets the import order.
     *
     * @param importOrder
     *            the importOrder to set
     */
    public void setImportOrder(int importOrder) {
        this.importOrder = importOrder;
    }

    /**
     * Checks if is unique.
     *
     * @return the unique
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Sets the unique.
     *
     * @param unique
     *            the unique to set
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * Gets the natural key.
     *
     * @return the naturalKey
     */
    public NaturalKey getNaturalKey() {
        return naturalKey;
    }

    /**
     * Sets the natural key.
     *
     * @param naturalKey
     *            the naturalKey to set
     */
    public void setNaturalKey(NaturalKey naturalKey) {
        this.naturalKey = naturalKey;
    }


    /**
     * Gets the system key.
     *
     * @return the system key
     */
    public SystemKey getSystemKey() {
        return systemKey;
    }

    /**
     * Sets the system key.
     *
     * @param systemKey
     *            the new system key
     */
    public void setSystemKey(SystemKey internalKey) {
        this.systemKey = internalKey;
    }

    /**
     * Gets the source system.
     *
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }


    /**
     * Sets the source system.
     *
     * @param sourceSystem
     *            the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }


    /**
     * Gets the version range.
     *
     * @return the versionRange
     */
    public VersionRange getVersionRange() {
        return versionRange;
    }


    /**
     * Sets the version range.
     *
     * @param versionRange
     *            the versionRange to set
     */
    public void setVersionRange(VersionRange versionRange) {
        this.versionRange = versionRange;
    }


    /**
     * @return the updateMark
     */
    public UpdateMark getUpdateMark() {
        return updateMark;
    }

    /**
     * @param updateMark the updateMark to set
     */
    public void setUpdateMark(UpdateMark updateMark) {
        this.updateMark = updateMark;
    }

    /**
     * Gets the contains.
     *
     * @return the contains
     */
    public List<ContainmentRelation> getContains() {
        return contains;
    }


    /**
     * Sets the contains.
     *
     * @param contains
     *            the contains to set
     */
    public void setContains(List<ContainmentRelation> contains) {
        this.contains = contains;
    }


    /**
     * Gets the relates.
     *
     * @return the relates
     */
    public List<RelatesToRelation> getRelates() {
        return relates;
    }


    /**
     * Sets the relates.
     *
     * @param relates
     *            the relates to set
     */
    public void setRelates(List<RelatesToRelation> relates) {
        this.relates = relates;
    }

    /**
     * Gets the settings.
     *
     * @return the settings
     */
    public Map<String, Object> getSettings() {
        return settings;
    }

    /**
     * Sets the settings.
     *
     * @param settings
     *            the settings to set
     */
    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }


    /**
     * Checks if is historical.
     *
     * @return the historical
     */
    public boolean isMultiVersion() {
        return multiVersion;
    }


    /**
     * Sets the historical.
     *
     * @param historical
     *            the historical to set
     */
    public void setMultiVersion(boolean historical) {
        this.multiVersion = historical;
    }


    /**
     * Gets the prefetch keys.
     *
     * @return the prefetchKeys
     */
    public List<String> getPrefetchKeys() {
        return prefetchKeys;
    }


    /**
     * Sets the prefetch keys.
     *
     * @param prefetchKeys
     *            the prefetchKeys to set
     */
    public void setPrefetchKeys(List<String> prefetchKeys) {
        this.prefetchKeys = prefetchKeys;
    }


    /**
     * Checks if is updates.
     *
     * @return the updates
     */
    public boolean isUpdates() {
        return updates;
    }


    /**
     * Sets the updates.
     *
     * @param updatesPossible
     *            the new updates
     */
    public void setUpdates(boolean updatesPossible) {
        this.updates = updatesPossible;
    }


    /**
     * Checks if is skip cleanse.
     *
     * @return the skipCleanse
     */
    public boolean isSkipCleanse() {
        return skipCleanse;
    }


    /**
     * Sets the skip cleanse.
     *
     * @param skipCleanse
     *            the skipCleanse to set
     */
    public void setSkipCleanse(boolean skipCleanse) {
        this.skipCleanse = skipCleanse;
    }


    /**
     * @return the skipNotifications
     */
    public boolean isSkipNotifications() {
        return skipNotifications;
    }

    /**
     * @param skipNotifications the skipNotifications to set
     */
    public void setSkipNotifications(boolean skipNotifications) {
        this.skipNotifications = skipNotifications;
    }

    /**
     * @return the processRecords
     */
    public boolean isProcessRecords() {
        return processRecords;
    }

    /**
     * @param processRecords the processRecords to set
     */
    public void setProcessRecords(boolean processRecords) {
        this.processRecords = processRecords;
    }

    /**
     * @return the processClassifiers
     */
    public boolean isProcessClassifiers() {
        return processClassifiers;
    }

    /**
     * @param processClassifiers the processClassifiers to set
     */
    public void setProcessClassifiers(boolean processClassifiers) {
        this.processClassifiers = processClassifiers;
    }

    /**
     * Checks if is relations only.
     *
     * @return the relationsOnly
     */
    public boolean isProcessRelations() {
        return processRelations;
    }


    /**
     * Sets the relations only.
     *
     * @param relationsOnly
     *            the relationsOnly to set
     */
    public void setProcessRelations(boolean relationsOnly) {
        this.processRelations = relationsOnly;
    }


    /**
     * Gets the max record count.
     *
     * @return the maxRecordCount
     */
    public long getMaxRecordCount() {
        return maxRecordCount;
    }


    /**
     * Sets the max record count.
     *
     * @param maxRecordsCount
     *            the new max record count
     */
    public void setMaxRecordCount(long maxRecordsCount) {
        this.maxRecordCount = maxRecordsCount;
    }


    /**
     * Checks if is swap odd dates.
     *
     * @return the swapOddDates
     */
    public boolean isSwapOddDates() {
        return swapOddDates;
    }


    /**
     * Sets the swap odd dates.
     *
     * @param swapOddDates
     *            the swapOddDates to set
     */
    public void setSwapOddDates(boolean swapOddDates) {
        this.swapOddDates = swapOddDates;
    }

    /**
     * Gets the classifier mappings.
     *
     * @return the classifier mappings
     */
    public List<ClassifierMapping> getClassifierMappings() {
        return classifierMappings;
    }

    /**
     * Sets the classifier mappings.
     *
     * @param classifierMappings the new classifier mappings
     */
    public void setClassifierMappings(List<ClassifierMapping> classifierMappings) {
        this.classifierMappings = classifierMappings;
    }

    /**
     * @return dq errors section
     */
    public DqErrorsSection getDqErrorsSection() {
        return dqErrorsSection;
    }

    /**
     * @param dqErrorsSection - dq errors section
     */
    public void setDqErrorsSection(DqErrorsSection dqErrorsSection) {
        this.dqErrorsSection = dqErrorsSection;
    }

    /**
     * @return xlsxKey
     */
    public String getXlsxKey() {
        return xlsxKey;
    }

    /**
     * @param xlsxKey
     */
    public void setXlsxKey(String xlsxKey) {
        this.xlsxKey = xlsxKey;
    }

}
