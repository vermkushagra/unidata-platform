/**
 */

package com.unidata.mdm.integration.job.extsample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class ExtSampleItemReader implements ItemReader<ExtSampleItemSubmission> {
    private static final Logger logger = LoggerFactory.getLogger(ExtSampleItemReader.class);

    private Integer from;
    private Integer to;
    private Integer currentCount;
    private String firstParameter;

    public void setTo(Integer to) {
        this.to = to;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setFirstParameter(String firstParameter) {
        this.firstParameter = firstParameter;
    }

    @Override
    public ExtSampleItemSubmission read() {
        if (from == null || to == null) {
            logger.error("Failed to get valid initializations");

            return null;
        }

        if (currentCount == null) {
            currentCount = from;
        }

        logger.info("Reader [currentCount={}, from={}, to={}, firstParameter={}]", currentCount, from, to,
            firstParameter);

        ExtSampleItemSubmission result = null;

        if (currentCount <= to) {
            result = new ExtSampleItemSubmission("" + currentCount);
            currentCount++;
        }

        return result;
    }
}
