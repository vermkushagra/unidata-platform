/**
 *
 */

package com.unidata.mdm.backend.service.job.republishregistry;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.PassThroughItemProcessor;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RepublishRegistryItemProcessor extends PassThroughItemProcessor<List<UpsertRequestContext>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepublishRegistryItemProcessor.class);

    @Override
    public List<UpsertRequestContext> process(List<UpsertRequestContext> item) throws Exception {
        LOGGER.debug("Checkpoint [process item] with [{}] records to process.", item.size());

        return item;
    }
}