/**
 *
 */

package com.unidata.mdm.backend.service.job.samplerestart;

import org.springframework.batch.item.ItemProcessor;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleRestartItemProcessor implements
    ItemProcessor<SampleRestartItemSubmission, SampleRestartProcessedItem> {
    @Override
    public SampleRestartProcessedItem process(SampleRestartItemSubmission item) throws Exception {

        Thread.sleep(1000);

        return new SampleRestartProcessedItem(item.getValue());
    }
}
