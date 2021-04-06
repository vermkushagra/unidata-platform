package com.unidata.mdm.backend.exchange.chain.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

import com.unidata.mdm.backend.exchange.chain.Result;

/**
 * @param <T>
 * @param <B>
 */
public abstract class JobEmulationRunnable<T, B> implements Callable<Result> {

    protected final static Logger LOGGER = LoggerFactory.getLogger(JobEmulationRunnable.class);
    /**
     * batch size
     */
    protected final ExecutionContext executionContext;
    /**
     * Spring batch reader
     */
    protected ItemReader<B> reader;
    /**
     * spring batch processor
     */
    protected ItemProcessor<B, T> itemProcessor;
    /**
     * spring batch writer
     */
    protected ItemWriter<T> itemWriter;

    public JobEmulationRunnable(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    @Override
    public Result call() throws Exception {
        try {
            List<T> items = new ArrayList<>(executionContext.getInt("bulkSize"));
            while (true) {
                B item = reader.read();
                if (Objects.isNull(item)) {
                    break;
                }
                T processedItem = itemProcessor.process(item);
                if (Objects.nonNull(processedItem)) {
                    items.add(processedItem);
                }
            }
            itemWriter.write(items);
        } catch (Exception e) {
            LOGGER.error("Error during job execution {}", e);
        }
        return new Result();
    }
}
