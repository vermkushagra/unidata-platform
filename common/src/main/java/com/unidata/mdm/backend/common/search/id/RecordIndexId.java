package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * @author Mikhail Mikhailov
 * Record period index id.
 */
public class RecordIndexId extends AbstractManagedIndexId {
    /**
     * Constructor.
     */
    private RecordIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SearchType getSearchType() {
        return EntitySearchType.ETALON_DATA;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RecordIndexId of(String entityName, String etalonId, long periodId) {

        RecordIndexId id = new RecordIndexId();

        id.entityName = entityName;
        id.indexId = PeriodIdUtils.childPeriodId(periodId, etalonId);
        id.routing = etalonId;

        return id;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @param periodIdAsString the period id in string representation
     * @return index id
     */
    public static RecordIndexId of(String entityName, String etalonId, String periodIdAsString) {

        RecordIndexId id = new RecordIndexId();

        id.entityName = entityName;
        id.indexId = PeriodIdUtils.childPeriodId(etalonId, periodIdAsString);
        id.routing = etalonId;

        return id;
    }
}
