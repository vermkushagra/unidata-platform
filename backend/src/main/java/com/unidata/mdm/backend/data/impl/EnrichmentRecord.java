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
