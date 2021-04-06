package com.unidata.mdm.backend.configuration.application

import reactor.test.publisher.TestPublisher
import spock.lang.Specification
import spock.lang.Subject
import com.google.common.base.Optional

class LocalConfigurationCacheSpec extends Specification {

    @Subject inMemoryLocalConfigurationStorageService = new LocalConfigurationCache()

    def "after configuration updates properties should be cached"() {
        given:
        def testPublisher = TestPublisher.<Map<String, Optional<? extends Serializable>>>create()
        inMemoryLocalConfigurationStorageService.subscribe(testPublisher.flux())

        when:
        testPublisher.next(
                [prop1: Optional.of("val1")],
                [prop1: Optional.of("val2"), prop2: Optional.absent()]
        )

        then:
        inMemoryLocalConfigurationStorageService.propertyValue("prop1").get() == "val2"
        inMemoryLocalConfigurationStorageService.propertyValue("prop2") == Optional.absent()
    }
}
