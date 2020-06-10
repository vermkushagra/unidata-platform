package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.PeriodIdUtils;

/**
 * @author Mikhail Mikhailov
 * From side index id.
 */
public class RelationFromIndexId extends RelationIndexId {
    /**
     * Constructor.
     */
    private RelationFromIndexId() {
        super();
    }
    /**
     * Creates a new 'from' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RelationFromIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, long periodId) {

        RelationFromIndexId id = new RelationFromIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(periodId, fromEtalonId, relationName, toEtalonId);
        id.routing = fromEtalonId;

        return id;
    }
    /**
     * Creates a new 'from' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodIdAsString the period id in string representation
     * @return index id
     */
    public static RelationFromIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, String periodIdAsString) {

        RelationFromIndexId id = new RelationFromIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(fromEtalonId, relationName, toEtalonId, periodIdAsString);
        id.routing = fromEtalonId;

        return id;
    }
}
