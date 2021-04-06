package com.unidata.mdm.backend.service.job.removerelations;

import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoveRelationsItemWriter implements ItemWriter<List<DeleteRelationRequestContext>> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRelationsItemWriter.class);

    /**
     * Data record service
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    @Override
    public void write(List<? extends List<DeleteRelationRequestContext>> items) throws Exception {
        items.stream().flatMap(Collection::stream).forEach(ctx -> {
            try {
                relationsServiceComponent.deleteRelation(ctx);
            } catch (Exception e) {
                LOGGER.error("Error during removing relation :{}", e);
            }
        });
    }
}
