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

package org.unidata.mdm.data.dto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.unidata.mdm.core.dto.ResourceSpecificRightDTO;
import org.unidata.mdm.core.type.data.impl.SimpleAttributesDiff;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.dto.AbstractCompositeResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 *         Get record result DTO.
 */
public class GetRecordDTO extends AbstractCompositeResult
    implements RecordDTO, OriginRecordsDTO, EtalonRecordDTO, PipelineOutput {
    /**
     * Record keys.
     */
    private RecordKeys recordKeys;
    /**
     * Rights.
     */
    private ResourceSpecificRightDTO rights;
    /**
     * Minimum lower bound.
     */
    private Date rangeFromMax;
    /**
     * Maximum upper bound.
     */
    private Date rangeToMin;
    /**
     * Golden record.
     */
    private EtalonRecord etalon;
    /**
     * 0 or more origin records.
     */
    private List<OriginRecord> origins;
    /**
     * Accessory map
     */
    private Map<String, String> attributeWinnerMap;
    /**
     * Version field.
     */
    private int version;
    /**
     * Diff to draft.
     */
    private SimpleAttributesDiff diffToDraft;
    /**
     * Diff to previous state.
     */
    private SimpleAttributesDiff diffToPrevious;
    /**
     * Constructor.
     */
    public GetRecordDTO() {
        super();
    }
    /**
     * Constructor.
     * @param keys the keys
     */
    public GetRecordDTO(RecordKeys keys) {
        super();
        this.recordKeys = keys;
    }
    /**
     * @return the recordKeys
     */
    @Override
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }

    /**
     * @param recordKeys the recordKeys to set
     */
    public void setRecordKeys(RecordKeys recordKeys) {
        this.recordKeys = recordKeys;
    }

    /**
     * @return the rangeFromMax
     */
    public Date getRangeFromMax() {
        return rangeFromMax;
    }

    /**
     * @param rangeFromMax the rangeFromMax to set
     */
    public void setRangeFromMax(Date rangeFromMax) {
        this.rangeFromMax = rangeFromMax;
    }

    /**
     * @return the rangeToMin
     */
    public Date getRangeToMin() {
        return rangeToMin;
    }

    /**
     * @param rangeFromMin the rangeToMin to set
     */
    public void setRangeToMin(Date rangeFromMin) {
        this.rangeToMin = rangeFromMin;
    }

    /**
     * @return the goldenRecord
     */
    @Override
    public EtalonRecord getEtalon() {
        return etalon;
    }

    /**
     * @param goldenRecord the goldenRecord to set
     */
    public void setEtalon(EtalonRecord goldenRecord) {
        this.etalon = goldenRecord;
    }

    /**
     * @return the origins
     */
    @Override
    public List<OriginRecord> getOrigins() {
        return origins == null ? Collections.emptyList() : origins;
    }

    /**
     * @param originRecords the origins to set
     */
    public void setOrigins(List<OriginRecord> originRecords) {
        this.origins = originRecords;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return
     */
    public Map<String, String> getAttributeWinnersMap() {
        return attributeWinnerMap;
    }

    /**
     * @param attributeWinnerMap
     */
    public void setAttributeWinnerMap(Map<String, String> attributeWinnerMap) {
        this.attributeWinnerMap = attributeWinnerMap;
    }

    /**
     * @return the rights
     */
    public ResourceSpecificRightDTO getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(ResourceSpecificRightDTO rights) {
        this.rights = rights;
    }
    /**
     * @return the diffToDraft
     */
    public SimpleAttributesDiff getDiffToDraft() {
        return diffToDraft;
    }
    /**
     * @param diffToDraft the diffToDraft to set
     */
    public void setDiffToDraft(SimpleAttributesDiff diffToDraft) {
        this.diffToDraft = diffToDraft;
    }
    /**
     * @return the diffToPrevious
     */
    public SimpleAttributesDiff getDiffToPrevious() {
        return diffToPrevious;
    }
    /**
     * @param diffToPrevious the diffToPrevious to set
     */
    public void setDiffToPrevious(SimpleAttributesDiff diffToPrevious) {
        this.diffToPrevious = diffToPrevious;
    }
}
