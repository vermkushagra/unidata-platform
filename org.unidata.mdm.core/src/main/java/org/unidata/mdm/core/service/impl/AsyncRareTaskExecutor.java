package org.unidata.mdm.core.service.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;

import io.prometheus.client.Counter;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.service.AsyncExecutor;
import org.unidata.mdm.system.service.RuntimePropertiesService;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesConsumer;
import org.unidata.mdm.core.configuration.CoreConfigurationProperty;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Service
public class AsyncRareTaskExecutor implements AsyncExecutor, ConfigurationUpdatesConsumer {

    // TODO: 30.10.2018 move QUEUE_SIZE to config
    public static final int QUEUE_SIZE = 100;
    private static final String METRIC_NAME = "unidata_async_rare_task_executor_queue_size";
    private static final String METRIC_HELP_TEXT = "Unidata async rare task executor queue size";

    private static final String TASKS_SUBMITTED_TO_HANDLE_METRIC_NAME = "udidata_async_rare_task_total";
    private static final String TASKS_SUBMITTED_TO_HANDLE_HELP_TEXT = "Rare tasks' counter";

    private static final Counter TASKS_SUBMITTED_TO_HANDLE_COUNTER = Counter.build()
            .name(TASKS_SUBMITTED_TO_HANDLE_METRIC_NAME)
            .help(TASKS_SUBMITTED_TO_HANDLE_HELP_TEXT)
            .create()
            .register();

    private static final CustomizableThreadFactory THREAD_FACTORY =
            new CustomizableThreadFactory("UnidataRareTaskExecutor");

    private final ThreadPoolExecutor threadPoolExecutor;

    public AsyncRareTaskExecutor() {
        final ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        // TODO @Modules
        // QueueSizeCollector.createAndRegister(workQueue, METRIC_NAME, METRIC_HELP_TEXT);
        threadPoolExecutor = new InternalThreadPoolExecutor(
                (Integer) CoreConfigurationProperty.ASYNC_RARE_TASKS_EXECUTOR_THREADS_POOL_SIZE.getDefaultValue().get(),
                workQueue,
                THREAD_FACTORY,
                TASKS_SUBMITTED_TO_HANDLE_COUNTER
        ) {

        };
    }

    @Override
    public <T> CompletableFuture<T> async(final Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, threadPoolExecutor);
    }

    @Override
    public CompletableFuture<Void> async(final Runnable runnable) {
        return CompletableFuture.runAsync(runnable, threadPoolExecutor);
    }

    @Override
    public void execute(@Nonnull final Runnable command) {
        threadPoolExecutor.execute(command);
    }

    @Override
    public Disposable subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String poolSizeKey = CoreConfigurationProperty.ASYNC_RARE_TASKS_EXECUTOR_THREADS_POOL_SIZE.getKey();
        return updates
                .filter(values ->
                        values.containsKey(poolSizeKey) && values.get(poolSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(poolSizeKey).get())
                .subscribe(value -> {
                    threadPoolExecutor.setCorePoolSize(value);
                    threadPoolExecutor.setMaximumPoolSize(value);
                });
    }

    @PreDestroy
    public void preDestroy() {
        threadPoolExecutor.shutdown();
    }

    private static class InternalThreadPoolExecutor extends ThreadPoolExecutor {
        private final Counter counter;
        InternalThreadPoolExecutor(
                final int poolSize,
                final BlockingQueue<Runnable> workQueue,
                final ThreadFactory threadFactory,
                final Counter counter
        ) {
            super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, workQueue, threadFactory);
            this.counter = counter;
        }

        @Override
        public void execute(Runnable command) {
            counter.inc();
            super.execute(command);
        }
    }
}
