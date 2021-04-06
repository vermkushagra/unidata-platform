/**
 * Date: 08.06.2016
 */

package com.unidata.mdm.backend.service.job.softdeletecleanup;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.PassThroughItemProcessor;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SoftDeleteCleanupItemProcessor extends PassThroughItemProcessor<List<DeleteRequestContext>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftDeleteCleanupItemProcessor.class);

    @Override
    public List<DeleteRequestContext> process(List<DeleteRequestContext> item) throws Exception {
        LOGGER.debug("Checkpoint [process item] with {} records to process.", item.size());

        return item;
    }
}
