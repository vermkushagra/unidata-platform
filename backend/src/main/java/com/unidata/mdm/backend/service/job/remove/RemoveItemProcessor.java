package com.unidata.mdm.backend.service.job.remove;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;

public class RemoveItemProcessor implements ItemProcessor<String, DeleteRequestContext> {

    /**
     * entity name
     */
    private String entityName;

    /**
     * wipe
     */
    private boolean wipe;

    /**
     * operation id
     */
    private String operationId;
    /**
     * operation executor
     */
    private String operationExecutor;

    @Override
    public DeleteRequestContext process(String etalonId) throws Exception {
        DeleteRequestContext ctx = DeleteRequestContext.builder()
                                                       .etalonKey(etalonId)
                                                       .entityName(entityName)
                                                       .cascade(false)
                                                       .wipe(wipe)
                                                       .inactivateEtalon(wipe ? false : true)
                                                       .build();
        ctx.setOperationId(operationId);
        return ctx;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Required
    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }


    public String getOperationExecutor() {
        return operationExecutor;
    }

    public void setOperationExecutor(String operationExecutor) {
        this.operationExecutor = operationExecutor;
    }
}
