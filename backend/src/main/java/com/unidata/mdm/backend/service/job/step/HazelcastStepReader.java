package com.unidata.mdm.backend.service.job.step;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.integration.partition.StepExecutionRequest;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.unidata.mdm.backend.service.job.HazelcastStepExecutionRequestHandler;

/**
 * @author Mikhail Mikhailov
 * Hazelcast step reader.
 * Creates fixed single thread pool to take messages from a hazelcast distributed partitioning queue.
 * Submits valid messages to {@link HazelcastStepSubmitter} instances, which are hold in a separate pool of configured length.
 */
public class HazelcastStepReader implements Runnable {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastStepReader.class);
    /**
     * Worker lock.
     */
    private final Lock lock = new ReentrantLock();
    /**
     * Worker pool is ready for input (active size is less then threadCount).
     */
    private final Condition workerPoolReady = lock.newCondition();
    /**
     * Number of currently active worker threads.
     */
    private final AtomicInteger workerCounter = new AtomicInteger(0);
    /**
     * Worker threads pool executor.
     */
    private final ThreadPoolExecutor workerExecutor;
    /**
     * This reader (dispatcher) single thread pool executor.
     */
    private final ThreadPoolExecutor readerExecutor;
    /**
     * Step handler bean instance.
     */
    private final HazelcastStepExecutionRequestHandler handler;
    /**
     * True if this step handler has been canceled.
     */
    private volatile boolean canceled = false;
    /**
     * Constructor.
     * @param threadCount the number of threads to initialize for request processing.
     * @param awaitTerminationInMinutes wait that number of minutes for pool shutdown
     */
    public HazelcastStepReader(HazelcastStepExecutionRequestHandler handler) {

        super();
        this.handler = handler;

        // 1. Create pool for workers.
        workerExecutor = new ThreadPoolExecutor(handler.getThreadCount(), handler.getThreadCount(), 60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new CustomizableThreadFactory(handler.getJobName() + "-HazelcastStepWorker-")) {
                @Override
                protected void afterExecute(Runnable r, Throwable t) {

                    lock.lock();
                    try {
                        workerCounter.decrementAndGet();
                        workerPoolReady.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            };

        // 2. Create reader (dispatcher pool)
        readerExecutor = new ThreadPoolExecutor(1, 1, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new CustomizableThreadFactory(handler.getJobName() + "-HazelcastStepReader-"));

        readerExecutor.submit(this);

        LOGGER.info("'{}' job step executor initialized successfully.", handler.getJobName());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        while (true) {

            lock.lock();
            try {

                while (!canceled && (workerCounter.get() >= workerExecutor.getCorePoolSize())) {
                    workerPoolReady.await();
                }

                if (canceled) {
                    LOGGER.info("Job step reader [job={}] canceled. Exiting.", handler.getJobName());
                    return;
                }

                Message<StepExecutionRequest> msg = handler.getQueue().take();

                workerCounter.incrementAndGet();
                workerExecutor.submit(new HazelcastStepSubmitter(msg, handler, canceled));

            } catch (InterruptedException ie) {

                if (!canceled) {
                    LOGGER.error("Reader queue interrupted.", ie);
                }

                Thread.currentThread().interrupt();
                return;
            } catch (RejectedExecutionException ree) {
                LOGGER.debug("Rejected step worker, probably due to pool shutdown.", ree);
                return;
            } catch (HazelcastInstanceNotActiveException hinae) {
                LOGGER.debug("HazelcastInstanceNotActiveException, probably due to application shutdown.");
                return;
            } catch (Exception e) {
                LOGGER.error("Reader queue caught unknown exception.", e);
                return;
            } finally {
                lock.unlock();
            }
        }
    }
    /**
     * Does hierarhical shutdown.
     */
    public void shutdown() {

        try {

            LOGGER.debug("Attempting step reader pool shutdown for job [{}].", handler.getJobName());
            // Set state
            canceled = true;

            // Signal workerPoolReady, if it is waiting on condition
            if (lock.tryLock()) {
                try {
                    LOGGER.debug("Wake up threads waiting workerPoolReady for job [{}].", handler.getJobName());
                    workerPoolReady.signalAll();
                    LOGGER.debug("Wake up threads waiting workerPoolReady for job [{}] done.", handler.getJobName());
                } finally {
                    lock.unlock();
                }
            }

            // Interrupt queue.take() rougly, due to
            // https://github.com/hazelcast/hazelcast/issues/4143
            // https://github.com/hazelcast/hazelcast/issues/5526
            // Poisoning can't be implemented successfully, because of the need for full trip
            // to queue partition and inability to route message from there to exact destination.
            LOGGER.debug("Reader pool shutdown for job step handler [{}].", handler.getJobName());
            readerExecutor.shutdownNow();
        } finally {
            LOGGER.info("Reader executor shutdown complete for step handler [{}].", handler.getJobName());
        }

        try {

            LOGGER.debug("Attempting worker pool shutdown for job [{}]", handler.getJobName());

            workerExecutor.shutdown();
            boolean success = workerExecutor.awaitTermination(handler.getAwaitTerminationInSeconds(), TimeUnit.SECONDS);
            if (!success) {
                LOGGER.debug("Graceful worker pool shutdown with timeout of {} minutes unsuccessful. Force termination.", handler.getAwaitTerminationInSeconds());
                workerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Failed to shutdown worker executor properly", e);
        } finally {
            LOGGER.info("Worker executor shutdown complete for step handler [{}].", handler.getJobName());
        }

        LOGGER.info("End cleaning distributed job resources for job [{}].", handler.getJobName());
    }
}
