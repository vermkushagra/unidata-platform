package com.unidata.mdm.backend.configuration.application

import com.google.common.base.Optional
import com.hazelcast.core.EntryEvent
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.hazelcast.core.Member
import reactor.core.publisher.BaseSubscriber
import reactor.test.publisher.TestPublisher
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class HazelcastConfigurationStorageSpec extends Specification {

    def configurationUpdatesPublisher = TestPublisher.<Map<String, Optional<? extends Serializable>>>create()

    def member = Mock(Member) {
        localMember() >> false
    }

    def configurationMap = Mock(IMap)

    def hazelcastInstance = Mock(HazelcastInstance) {
        getMap(_) >> configurationMap
    }

    @Subject hazelcastConfigurationStorage = new HazelcastConfigurationStorage(hazelcastInstance)

    def mapEventListener

    @Unroll
    def "on update configuration save in hazelcast: ['#key': '#value']"() {
        given:
        hazelcastConfigurationStorage.subscribe(configurationUpdatesPublisher.flux())

        when:
        configurationUpdatesPublisher.next([(key): value])

        then:
        1 * configurationMap.putAll(_) >> {
            it[0] == [(key): value.orNull()]
        }

        where:
        key   | value
        "property" | Optional.of("value")
        "property" | Optional.absent()
        "property" | Optional.of(1)
    }

    def "publish updates on configuration"() {
        given:
        def index = 0
        def listExpected = [
                [property: Optional.of("value")],
                [property: Optional.of("value1")],
                [property: Optional.absent()]
        ]

        when:
        new HazelcastConfigurationStorage(hazelcastInstance).updates().subscribe(
                new BaseSubscriber<Map<String, Optional<? extends Serializable>>>() {
                    @Override
                    protected void hookOnNext(Map<String, Optional<? extends Serializable>> value) {
                        assert value == listExpected[index]
                    }
                }
        )

        mapEventListener.onEntryEvent(
                new EntryEvent<>("test", member, 1, "property", Optional.of("value"))
        )
        index += 1
        mapEventListener.onEntryEvent(
                new EntryEvent<>("test", member, 1, "property", Optional.of("value1"))
        )
        index += 1
        mapEventListener.onEntryEvent(
                new EntryEvent<>("test", member, 1, "property", Optional.absent())
        )

        then:
        1 * configurationMap.addEntryListener(*_) >> {
            mapEventListener = it[0]
        }
    }
}