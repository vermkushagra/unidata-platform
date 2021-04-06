/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.api.rest.dto.search.SearchRequestRO;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Bulk operation base class.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = ModifyRecordsBulkOperationRO.class, name = "MODIFY_RECORDS"),
    @Type(value = RepublishRecordsBulkOperationRO.class, name = "REPUBLISH_RECORDS"),
    @Type(value = ImportRecordsFromXlsBulkOperationRO.class, name = "IMPORT_RECORDS_FROM_XLS"),
    @Type(value = ExportRecordsToXlsBulkOperationRO.class, name = "EXPORT_RECORDS_TO_XLS"),
    @Type(value = RemoveRecordsBulkOperationRO.class, name = "REMOVE_RECORDS"),
    @Type(value = RemoveRelationsFromBulkOperationsRO.class, name = "REMOVE_RELATIONS_FROM")
})
public abstract class BulkOperationBaseRO {

    /**
     * Selected IDs (has higher priority).
     */
    private List<String> selectedByIds;
    /**
     * Selected by request (lower priority).
     */
    private SearchRequestRO selectedByRequest;
    /**
     * Entity name.
     */
    private String entityName;
    /**
     * Constructor.
     */
    public BulkOperationBaseRO() {
        super();
    }

    /**
     * @return the selectedByIds
     */
    public List<String> getSelectedByIds() {
        return selectedByIds;
    }

    /**
     * @param selectedByIds the selectedByIds to set
     */
    public void setSelectedByIds(List<String> selectedByIds) {
        this.selectedByIds = selectedByIds;
    }

    /**
     * @return the selectedByRequest
     */
    public SearchRequestRO getSelectedByRequest() {
        return selectedByRequest;
    }

    /**
     * @param selectedByRequest the selectedByRequest to set
     */
    public void setSelectedByRequest(SearchRequestRO selectedByRequest) {
        this.selectedByRequest = selectedByRequest;
    }

    /**
     * Bulk operation type.
     * @return type
     */
    @JsonIgnore
    public abstract BulkOperationType getType();

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
