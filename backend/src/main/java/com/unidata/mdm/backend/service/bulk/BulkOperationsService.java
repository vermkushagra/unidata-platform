package com.unidata.mdm.backend.service.bulk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.common.dto.GetBulkOperationDTO;
import com.unidata.mdm.backend.common.types.BulkOperationType;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * @author Mikhail Mikhailov
 * Bulk operations service.
 */
@Service
public class BulkOperationsService {
    /**
     * Operations.
     */
    private Map<BulkOperationType, BulkOperation> operations;

    /**
     * @param operations the operations to set
     */
    @Required
    @Resource(name = "bulkOperationsMap")
    public void setOperations(Map<BulkOperationType, BulkOperation> operations) {
        this.operations = operations;
    }
    /**
     * Constructor.
     */
    public BulkOperationsService() {
        super();
    }
    /**
     * Gets available bulk operations.
     * @return operations list
     */
    public List<GetBulkOperationDTO> list() {

        List<GetBulkOperationDTO> result = new ArrayList<>(operations !=  null ? operations.size() : 0);
        Iterator<Entry<BulkOperationType, BulkOperation>> i
            = operations != null
                ? operations.entrySet().iterator()
                : null;
        while (i != null && i.hasNext()) {
            Entry<BulkOperationType, BulkOperation> entry = i.next();
            result.add(new GetBulkOperationDTO(
                    entry.getKey(),
                    MessageUtils.getMessage(entry.getKey().getDecsription())));
        }

        return result;
    }

    /**
     * Gets configuration, specific to a bulk operation.
     * @param type BO type
     * @return information
     */
    public BulkOperationInformationDTO configure(BulkOperationType type) {

        BulkOperation operation = operations.get(type);
        if (operation != null) {
            return operation.configure();
        }

        return null;
    }

    /**
     * Run specific operation type.
     * @param type the type
     * @return
     */
    public boolean run(BulkOperationRequestContext ctx) {

        BulkOperation operation = operations.get(ctx.getConfiguration().getType());
        if (operation != null) {
            return operation.run(ctx);
        }

        return false;
    }
}
