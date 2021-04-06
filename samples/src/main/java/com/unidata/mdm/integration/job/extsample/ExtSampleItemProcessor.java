/**
 *
 */

package com.unidata.mdm.integration.job.extsample;

import org.springframework.batch.item.ItemProcessor;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class ExtSampleItemProcessor implements ItemProcessor<ExtSampleItemSubmission, ExtSampleProcessedItem> {
    @Override
    public ExtSampleProcessedItem process(ExtSampleItemSubmission item) throws Exception {

        Thread.sleep(1000);

        return new ExtSampleProcessedItem(item.getValue());
    }
}
