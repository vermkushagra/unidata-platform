/**
 * Date: 19.02.2016
 */

package com.unidata.mdm.backend.service.job.sample;

import org.springframework.batch.item.ItemProcessor;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleItemProcessor implements ItemProcessor<SampleItemSubmission, SampleProcessedItem> {
    @Override
    public SampleProcessedItem process(SampleItemSubmission item) throws Exception {

        Thread.sleep(1000);

        return new SampleProcessedItem(item.getValue());
    }
}
