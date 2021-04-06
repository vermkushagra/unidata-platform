/**
 * Date: 19.02.2016
 */

package com.unidata.mdm.backend.service.job.sample;

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
public class SampleItemWriter implements ItemWriter<SampleProcessedItem> {
    private static final Logger logger = LoggerFactory.getLogger(SampleItemWriter.class);

    /**
     *
     * @param items
     * @throws Exception
     */
    @Override
    public void write(List<? extends SampleProcessedItem> items) throws Exception {
        StringBuilder builder = new StringBuilder("Write items [");

        builder.append("size=").append(items.size()).append(", values=[");

        String joined = items.stream().map(SampleProcessedItem::getValue).collect(Collectors.joining(", "));

        builder.append(joined).append("]]");

        logger.info(builder.toString());
    }
}
