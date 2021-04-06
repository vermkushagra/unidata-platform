/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRelationToRO;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * REST Parameter class specific for modify bulk operation.
 *
 * @author Mikhail Mikhailov
 * 
 */
public class ModifyRecordsBulkOperationRO extends BulkOperationBaseRO {

    /** The etalon record RO. */
    private EtalonRecordRO etalonRecordRO;

    /** The relations. */
    private List<EtalonRelationToRO> relations;

    /**
     * Constructor.
     */
    public ModifyRecordsBulkOperationRO() {
        super();
    }

    /**
     * Gets the etalon record RO.
     *
     * @return the etalon record RO
     */
    public EtalonRecordRO getEtalonRecordRO() {
        return etalonRecordRO;
    }

    /**
     * Sets the etalon record RO.
     *
     * @param etalonRecordRO the new etalon record RO
     */
    public void setEtalonRecordRO(EtalonRecordRO etalonRecordRO) {
        this.etalonRecordRO = etalonRecordRO;
    }

    /**
     * Gets the relations.
     *
     * @return the relations
     */
    public List<EtalonRelationToRO> getRelations() {
        return relations;
    }

    /**
     * Sets the relations.
     *
     * @param relations the new relations
     */
    public void setRelations(List<EtalonRelationToRO> relations) {
        this.relations = relations;
    }

    /**
     * Bulk operation type.
     *
     * @return type
     */
    @JsonIgnore
    public BulkOperationType getType() {
        return BulkOperationType.MODIFY_RECORDS;
    }
}
