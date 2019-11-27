package org.unidata.mdm.system.service.impl.event;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.system.type.event.Event;
import org.unidata.mdm.system.type.event.EventReceiver;

import com.hazelcast.core.HazelcastInstance;

/**
 * Simple subscription registry.
 * @author Mikhail Mikhailov on Oct 30, 2019
 */
public class ReceiverRegistry {

    private final Map<Class<? extends Event>, ReceiverWorker> classified;

    private final HazelcastInstance hazelcastInstance;

    public ReceiverRegistry(final HazelcastInstance hazelcastInstance) {
        super();
        this.hazelcastInstance = hazelcastInstance;
        this.classified = new ConcurrentHashMap<>();
    }

    public ReceiverWorker info(Event event) {
        return classified.computeIfAbsent(
                event.getClass(),
                type -> new ReceiverWorker(hazelcastInstance.getAtomicLong(type.getClass().getName() + "#S")));
    }

    public<T extends Event> void register(EventReceiver subscriber, Class<T> eventType) {

        if (Objects.isNull(subscriber) || Objects.isNull(eventType)) {
            return;
        }

        classified.computeIfAbsent(
                eventType,
                type -> new ReceiverWorker(hazelcastInstance.getAtomicLong(eventType.getClass().getName() + "#S")))
            .subscribe(subscriber);
    }

    public void register(EventReceiver subscriber) {

        if (Objects.isNull(subscriber)) {
            return;
        }

        classified.keySet().forEach(key -> register(subscriber, key));
    }

    public <T extends Event> void unregister(EventReceiver subscriber, Class<T> eventType) {
        classified.computeIfPresent(eventType, (k, v) -> { v.unsubscribe(subscriber); return v; });
    }

    public void unregister(EventReceiver subscriber) {
        classified.keySet().forEach(key -> unregister(subscriber, key));
    }

    public int receive(Event event, boolean fromLocalMember) {
        ReceiverWorker info = info(event);
        return info.receive(event, fromLocalMember);
    }
}
