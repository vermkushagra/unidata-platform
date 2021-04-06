/**
 *
 */

package com.unidata.mdm.integration.job.extsample;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class ExtSampleItemWriter implements ItemWriter<ExtSampleProcessedItem> {
    private static final Logger logger = LoggerFactory.getLogger(ExtSampleItemWriter.class);

    /**
     *
     * @param items
     * @throws Exception
     */
    @Override
    public void write(List<? extends ExtSampleProcessedItem> items) {
        StringBuilder builder = new StringBuilder("Write items [");

        builder.append("size=").append(items.size()).append(", values=[");

        String joined = items.stream().map(ExtSampleProcessedItem::getValue).collect(Collectors.joining(", "));

        builder.append(joined).append("]]");

        logger.info(builder.toString());
    }
}
