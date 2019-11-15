package org.unidata.mdm.meta.type.search;

import java.util.Date;

import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

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
    public IndexType getSearchType() {
        return EntityIndexType.RECORD;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @param to the period to date - the source of the id
     * @return index id
     */
    public static RecordIndexId of(String entityName, String etalonId, Date to) {

        RecordIndexId id = new RecordIndexId();

        id.entityName = entityName;
        id.indexId = PeriodIdUtils.childPeriodId(PeriodIdUtils.ensureDateValue(to), etalonId);
        id.routing = etalonId;

        return id;
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
