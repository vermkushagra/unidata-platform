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
package com.unidata.mdm.backend.data.impl;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * A special enrichment record.
 */
public class EnrichmentRecord {

    /**
     * Etalon key.
     */
    private final EtalonKey etalonKey;
    /**
     * Enrichment origin record.
     */
    private final OriginRecord originRecord;

    /**
     * Constructor.
     */
    public EnrichmentRecord(EtalonKey etalonKey, OriginRecord originRecord) {
        super();
        this.etalonKey = etalonKey;
        this.originRecord = originRecord;
    }

    /**
     * @return the etalonKey
     */
    public EtalonKey getEtalonKey() {
        return etalonKey;
    }

    /**
     * @return the originRecord
     */
    public OriginRecord getOriginRecord() {
        return originRecord;
    }

    /**
     * Checks if this enrichment is capable for upsert.
     * @return
     */
    public boolean isValid() {

        if (etalonKey == null || etalonKey.getId() == null) {
            return false;
        }

        if (originRecord == null
         || originRecord.getInfoSection() == null
         || originRecord.getInfoSection().getOriginKey() == null) {
            return false;
        }

        OriginKey key = originRecord.getInfoSection().getOriginKey();
        return (key.getExternalId() != null
             && key.getEntityName() != null
             && key.getSourceSystem() != null) || key.getId() != null;
    }

}
