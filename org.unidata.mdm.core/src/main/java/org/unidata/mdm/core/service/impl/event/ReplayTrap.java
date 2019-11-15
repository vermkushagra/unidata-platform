package org.unidata.mdm.core.service.impl.event;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.type.event.Event;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * Simple replay counter.
 * @author Mikhail Mikhailov on Oct 30, 2019
 */
public class ReplayTrap {
    /**
     * The registry.
     */
    private final ReceiverRegistry registry;
    /**
     * Replay tracker.
     */
    private final ConcurrentHashMap<String, CountDownLatch> replays;
    /**
     * Constructor.
     */
    public ReplayTrap(final ReceiverRegistry registry) {
        super();
        this.registry = registry;
        this.replays = new ConcurrentHashMap<>();
    }
    /**
     * Gets a latch, initialized with the number of active subscribers cluster - wide.
     * @param event the event to set up the latch for
     * @return latch or null, if there're 0 active subscribers for the event type
     */
    public CountDownLatch init(Event event) {

        if (Objects.isNull(event.getId())) {
            throw new PlatformFailureException("No valid id for event [{}].",
                    CoreExceptionIds.EX_EVENT_NO_VALID_ID, event.getClass().getName());
        }

        if (replays.containsKey(event.getId())) {
            throw new PlatformFailureException("Already waiting for event [{}].",
                    CoreExceptionIds.EX_EVENT_ALREADY_WAITING, event.getId());
        }

        ReceiverWorker s = registry.info(event);
        if (s.knownCount() == 0) {
            return null;
        }

        CountDownLatch latch = new CountDownLatch(s.knownCount());
        replays.put(event.getId(), latch);

        return latch;
    }
    /**
     * Must be called from finally.
     * @param event the event
     */
    public void fini(Event event) {
        replays.remove(event.getId());
    }
    /**
     * Reacts to replay event
     * @param event the event
     */
    public void replay(Event event) {
        CountDownLatch latch = replays.get(event.getId());
        if (Objects.nonNull(latch)) {
            SystemReplayEvent sys = (SystemReplayEvent) event;
            for (int i = 0; i < sys.getReceiverCount(); i++) {
                latch.countDown();
            }
        }
    }
}
