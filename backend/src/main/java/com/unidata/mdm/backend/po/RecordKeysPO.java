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

package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * PO for keys digest.
 * @author Mikhail Mikhailov
 */
public class RecordKeysPO {
    /**
     * Etalon ID.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Etalon name.
     */
    public static final String FIELD_ETALON_NAME = "etalon_name";
    /**
     * Etalon status.
     */
    public static final String FIELD_ETALON_STATUS = "etalon_status";
    /**
     * Etalon version.
     */
    public static final String FIELD_ETALON_VERSION = "etalon_version";
    /**
     * Etalon state {@link ApprovalState}.
     */
    public static final String FIELD_ETALON_STATE = "etalon_state";
    /**
     * Etalon Gsn
     */
    public static final String FIELD_ETALON_GSN = "etalon_gsn";
    /**
     * Origin ID.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Origin name.
     */
    public static final String FIELD_ORIGIN_NAME = "origin_name";
    /**
     * Origin status.
     */
    public static final String FIELD_ORIGIN_STATUS = "origin_status";
    /**
     * Origin version.
     */
    public static final String FIELD_ORIGIN_VERSION = "origin_version";
    /**
     * Origin source system.
     */
    public static final String FIELD_ORIGIN_SOURCE_SYSTEM = "origin_source_system";
    /**
     * Origin external id.
     */
    public static final String FIELD_ORIGIN_EXTERNAL_ID = "origin_external_id";
    /**
     * Origin gsn
     */
    public static final String FIELD_ORIGIN_GSN = "origin_gsn";
    /**
     * Origin revision.
     */
    public static final String FIELD_ORIGIN_REVISION = "origin_revision";
    /**
     * Enrichment
     */
    public static final String FIELD_ENRICHMENT = "is_enrichment";
    /**
     * Whether this record has approved revisions or not.
     */
    public static final String FIELD_HAS_APPROVED_REVISIONS = "has_approved_revisions";
    /**
     * Record etalon id.
     */
    private String etalonId;
    /**
     * Type name as set by entity definition.
     */
    private String etalonName;
    /**
     * Etalon version of the record.
     */
    private int etalonVersion;
    /**
     * Etalon status of the record.
     */
    private RecordStatus etalonStatus;
    /**
     * Etalon approval state.
     */
    private ApprovalState etalonState;
    /**
     * Etalon Global sequence number.
     */
    private long etalonGsn;
    /**
     * Record origin id.
     */
    private String originId;
    /**
     * Type name as set by entity definition.
     */
    private String originName;
    /**
     * Origin version of the record.
     */
    private int originVersion;
    /**
     * Origin status of the record.
     */
    private RecordStatus originStatus;
    /**
     * Origin external id.
     */
    private String originExternalId;
    /**
     * Origin source system.
     */
    private String originSourceSystem;
    /**
     * Origin Global sequence number.
     */
    private Long originGsn;
    /**
     * Origin revision of the record.
     */
    private int originRevision;
    /**
     * Is enrich record
     */
    private Boolean isEnrich;
    /**
     * Record was already published.
     */
    private Boolean hasApprovedRevisions;
    /**
     * Constructor.
     */
    public RecordKeysPO() {
        super();
    }
    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonName
     */
    public String getEtalonName() {
        return etalonName;
    }

    /**
     * @param etalonName the etalonName to set
     */
    public void setEtalonName(String etalonName) {
        this.etalonName = etalonName;
    }

    /**
     * @return the etalonVersion
     */
    public int getEtalonVersion() {
        return etalonVersion;
    }

    /**
     * @param etalonVersion the etalonVersion to set
     */
    public void setEtalonVersion(int etalonVersion) {
        this.etalonVersion = etalonVersion;
    }

    /**
     * @return the etalonStatus
     */
    public RecordStatus getEtalonStatus() {
        return etalonStatus;
    }

    /**
     * @param etalonStatus the etalonStatus to set
     */
    public void setEtalonStatus(RecordStatus etalonStatus) {
        this.etalonStatus = etalonStatus;
    }


    /**
     * @return the etalonState
     */
    public ApprovalState getEtalonState() {
        return etalonState;
    }

    /**
     * @param etalonState the etalonState to set
     */
    public void setEtalonState(ApprovalState etalonState) {
        this.etalonState = etalonState;
    }

    /**
     * @return the etalonGsn
     */
    public long getEtalonGsn() {
        return etalonGsn;
    }
    /**
     * @param etalonGsn the etalonGsn to set
     */
    public void setEtalonGsn(long etalonGsn) {
        this.etalonGsn = etalonGsn;
    }
    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
    }

    /**
     * @return the originName
     */
    public String getOriginName() {
        return originName;
    }

    /**
     * @param originName the originName to set
     */
    public void setOriginName(String originName) {
        this.originName = originName;
    }

    /**
     * @return the originVersion
     */
    public int getOriginVersion() {
        return originVersion;
    }

    /**
     * @param originVersion the originVersion to set
     */
    public void setOriginVersion(int originVersion) {
        this.originVersion = originVersion;
    }

    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return originStatus;
    }

    /**
     * @param originStatus the originStatus to set
     */
    public void setOriginStatus(RecordStatus originStatus) {
        this.originStatus = originStatus;
    }

    /**
     * @return the originExternalId
     */
    public String getOriginExternalId() {
        return originExternalId;
    }

    /**
     * @param originExternalId the originExternalId to set
     */
    public void setOriginExternalId(String originExternalId) {
        this.originExternalId = originExternalId;
    }

    /**
     * @return the originSourceSystem
     */
    public String getOriginSourceSystem() {
        return originSourceSystem;
    }

    /**
     * @param originSourceSystem the originSourceSystem to set
     */
    public void setOriginSourceSystem(String originSourceSystem) {
        this.originSourceSystem = originSourceSystem;
    }
    /**
     * @return the originGsn
     */
    public Long getOriginGsn() {
        return originGsn;
    }
    /**
     * @param originGsn the originGsn to set
     */
    public void setOriginGsn(Long originGsn) {
        this.originGsn = originGsn;
    }

    /**
     * @return the originRevision
     */
    public int getOriginRevision() {
        return originRevision;
    }

    /**
     * @param originRevision the originRevision to set
     */
    public void setOriginRevision(int originRevision) {
        this.originRevision = originRevision;
    }

    /**
     * @return is enriched record
     */
    public Boolean isEnriched() {
        return isEnrich;
    }

    /**
     * @param enrich - is enriched record
     */
    public void setEnrich(Boolean enrich) {
        isEnrich = enrich;
    }
    /**
     * @return the isPublished
     */
    public Boolean hasApprovedRevisions() {
        return hasApprovedRevisions;
    }
    /**
     * @param hasApprovedRevisions the isPublished to set
     */
    public void setHasApprovedRevisions(Boolean hasApprovedRevisions) {
        this.hasApprovedRevisions = hasApprovedRevisions;
    }

}
