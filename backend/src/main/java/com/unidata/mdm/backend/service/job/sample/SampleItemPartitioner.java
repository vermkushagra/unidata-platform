/**
 * Date: 19.02.2016
 */

package com.unidata.mdm.backend.service.job.sample;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleItemPartitioner implements Partitioner {
    private static final Logger logger = LoggerFactory.getLogger(SampleItemPartitioner.class);

    private static int RANGE = 100;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();

        int min = 0;
        int max = RANGE;

        int targetSize = (max - min) / gridSize;


        int number = 0;
        int start = 0;
        int end = start + targetSize - 1;

        while (start <= max) {
            ExecutionContext value = new ExecutionContext();
            result.put(Integer.toString(number), value);

            if (end >= max) {
                end = max;
            }

            value.putInt("minValue", start);
            value.putInt("maxValue", end);

            start += targetSize;
            end += targetSize;
            number++;
        }

        logger.info("Created partitions [size={}, keys={}]", result.size(), result.keySet());

        return result;
    }
}
