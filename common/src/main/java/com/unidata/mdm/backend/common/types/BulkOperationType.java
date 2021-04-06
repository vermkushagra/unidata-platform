package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Bulk operation types.
 */
public enum BulkOperationType {

    /**
     * Modify several records at once.
     */
    MODIFY_RECORDS("app.data.bulk.operation.modify"),
    /**
     * Republish several records to a selected notification target.
     */
    REPUBLISH_RECORDS("app.data.bulk.operation.republish"),
    /**
     * Import records from XLS.
     */
    IMPORT_RECORDS_FROM_XLS("app.data.bulk.operation.import"),
    /**
     * Export records to XLS.
     */
    EXPORT_RECORDS_TO_XLS("app.data.bulk.operation.export"),
    /**
     * Logical remove records
     */
    REMOVE_RECORDS("app.data.bulk.operation.remove"),
    /**
     * Remove connections
     */
    REMOVE_RELATIONS_FROM("app.data.bulk.operation.remove.relations.from");
    /**
     * Constructor.
     * @param description the description
     */
    BulkOperationType(String description) {
        this.decsription = description;
    }
    /**
     * Description field.
     */
    private final String decsription;
    /**
     * @return the decsription
     */
    public String getDecsription() {
        return decsription;
    }
}
