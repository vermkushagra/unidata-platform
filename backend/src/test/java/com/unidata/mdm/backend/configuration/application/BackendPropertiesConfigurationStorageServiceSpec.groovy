package com.unidata.mdm.backend.configuration.application

import com.google.common.base.Optional
import reactor.test.StepVerifier
import spock.lang.Specification

class BackendPropertiesConfigurationStorageServiceSpec extends Specification {

    def backedProperties = Mock(Properties)

    def backendPropertiesConfigurationStorageService = new BackendPropertiesConfigurationStorageService(backedProperties)

    def "get only one update"() {
        when:
        def updates = backendPropertiesConfigurationStorageService.updates()
        StepVerifier.create(updates).expectNextCount(1L).verifyComplete()

        then:
        UnidataConfigurationProperty.values().size() * backedProperties.containsKey(_) >> {
            UnidataConfigurationProperty.findByKey(it[0]).defaultValue != Optional.absent()
        }
        _ * backedProperties.getProperty(_) >> {
            UnidataConfigurationProperty.findByKey(it[0]).defaultValue.get().toString()
        }
    }
}
