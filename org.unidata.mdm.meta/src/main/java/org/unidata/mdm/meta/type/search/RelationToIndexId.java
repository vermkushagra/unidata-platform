package org.unidata.mdm.meta.type.search;

import java.util.Date;

import org.unidata.mdm.core.util.PeriodIdUtils;

/**
 * @author Mikhail Mikhailov
 * To side index id.
 */
public class RelationToIndexId extends RelationIndexId {
    /**
     * Constructor.
     */
    private RelationToIndexId() {
        super();
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RelationToIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, Date to) {

        RelationToIndexId id = new RelationToIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(PeriodIdUtils.ensureDateValue(to), toEtalonId, relationName, fromEtalonId);
        id.routing = toEtalonId;

        return id;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodId the period id
     * @return index id
     */
    public static RelationToIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, long periodId) {

        RelationToIndexId id = new RelationToIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(periodId, toEtalonId, relationName, fromEtalonId);
        id.routing = toEtalonId;

        return id;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param relationName the relation name
     * @param fromEtalonId the from etalon id
     * @param toEtalonId the to etalon id
     * @param periodIdAsString the period id in string representation
     * @return index id
     */
    public static RelationToIndexId of(String entityName, String relationName, String fromEtalonId, String toEtalonId, String periodIdAsString) {

        RelationToIndexId id = new RelationToIndexId();

        id.entityName = entityName;
        id.relationName = relationName;
        id.indexId = PeriodIdUtils.childPeriodId(toEtalonId, relationName, fromEtalonId, periodIdAsString);
        id.routing = toEtalonId;

        return id;
    }
}
