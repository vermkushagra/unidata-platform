package com.unidata.mdm.backend.configuration.application

import com.unidata.mdm.backend.dao.ConfigurationDAO
import org.apache.commons.lang3.SerializationUtils
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import spock.lang.Specification
import spock.lang.Unroll
import com.google.common.base.Optional

class DatabaseConfigurationStorageServiceSpec extends Specification {

    def configurationUpdatesPublisher = TestPublisher.<Map<String, Optional<? extends Serializable>>>create()

    def configurationDAO = Mock(ConfigurationDAO)

    def databaseConfigurationStorageService = new DatabaseConfigurationStorageService(configurationDAO)

    @Unroll
    def "on update configuration save in database: ['#key': '#value']"() {
        given:
        databaseConfigurationStorageService.subscribe(configurationUpdatesPublisher.flux())

        when:
        configurationUpdatesPublisher.next([(key): value])

        then:
        1 * configurationDAO.save(_) >> {
            it == [(key): SerializationUtils.serialize(value)]
        }

        where:
        key   | value
        "property" | Optional.of("value")
        "property" | Optional.absent()
        "property" | Optional.of(1)
    }

    def "get only one update"() {
        given:
        def property = ++UnidataConfigurationProperty.values().iterator()

        when:
        def updates = databaseConfigurationStorageService.updates()
        StepVerifier.create(updates).expectNext([(property.key): Optional.of(1)]).verifyComplete()

        then:
        1 * configurationDAO.fetchAllProperties() >> [(property.key): SerializationUtils.serialize(Optional.of(1))]
    }
}
