package com.unidata.mdm.backend.configuration.application

import com.google.common.base.Optional
import com.unidata.mdm.backend.common.configuration.ConfigurationConstants
import com.unidata.mdm.backend.util.MessageUtils
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.test.publisher.TestPublisher
import spock.lang.Specification

class RuntimePropertiesServiceImplSpec extends Specification {

    def firstConfigurationUpdatesPublisher = TestPublisher.<Map<String, Optional<? extends Serializable>>>create()
    def secondConfigurationUpdatesPublisher = TestPublisher.<Map<String, Optional<? extends Serializable>>>create()

    def configurationCacheService = Mock(ConfigurationCacheService) {
        updates() >> Flux.merge(firstConfigurationUpdatesPublisher, secondConfigurationUpdatesPublisher)
    }
    def firstConfigurationUpdatesProducer = Mock(ConfigurationUpdatesProducer) {
        updates() >> firstConfigurationUpdatesPublisher.flux()
    }
    def secondConfigurationUpdatesProducer = Mock(ConfigurationUpdatesProducer) {
        updates() >> secondConfigurationUpdatesPublisher.flux()
    }
    def configurationUpdatesConsumer = Mock(ConfigurationUpdatesConsumer)

    def configurationService = new RuntimePropertiesServiceImpl(
            configurationCacheService,
            [firstConfigurationUpdatesProducer, secondConfigurationUpdatesProducer],
            [],
            [configurationUpdatesConsumer]
    )

    def setupSpec() {
        MessageUtils.init(Mock(ApplicationContext) {
            getEnvironment() >> Mock(Environment) {
                getProperty(ConfigurationConstants.DEFAULT_LOCALE_PROPERTY, "ru") >> "ru"
            }
        })
    }

    def updatesPublisher

    def "configuration updates should be in right order"() {
        given:
        def index = 0
        def updates = [
                [test1: Optional.absent()],
                [test2: Optional.of("str")],
                [test2: Optional.of("str1")]
        ]

        when:
        new RuntimePropertiesServiceImpl(
                configurationCacheService,
                [firstConfigurationUpdatesProducer, secondConfigurationUpdatesProducer],
                [],
                [configurationUpdatesConsumer]
        )
        updatesPublisher.subscribe(
                new BaseSubscriber<Map<String, Optional<? extends Serializable>>>() {
                    @Override
                    protected void hookOnNext(Map<String, Optional<? extends Serializable>> value) {
                        assert value == updates[index]
                    }
                }
        )
        firstConfigurationUpdatesPublisher.next(updates[index])
        index += 1
        secondConfigurationUpdatesPublisher.next(updates[index])
        index += 1
        firstConfigurationUpdatesPublisher.next(updates[index])

        then:
        1 * configurationUpdatesConsumer.subscribe(_) >> {
            updatesPublisher = it[0]
        }
    }

    def "available properties should be with current values from cache"() {
        when:
        def availableProperties = configurationService.availableProperties()

        then:
        availableProperties.size() == UnidataConfigurationProperty.values().size()
        UnidataConfigurationProperty.values().size() * configurationCacheService.propertyValue(_) >> Optional.absent()
    }

    def "conversion to DTO object should place value in right fields"() {
        given:
        def propertyForCheck = UnidataConfigurationProperty.values()[0]

        when:
        def property = configurationService.availableProperties().find {
            it.name == propertyForCheck.key
        }

        then:
        property != null
        1 * configurationCacheService.propertyValue(propertyForCheck.key) >> Optional.of(10)
        _ * configurationCacheService.propertyValue(_) >> Optional.absent()
        property.name == propertyForCheck.key
        property.displayName == MessageUtils.getMessage(propertyForCheck.key)
        property.group == MessageUtils.getMessage(propertyForCheck.groupKey)
        property.type == propertyForCheck.propertyType
        property.value == 10
        with(property.meta) {
            defaultValue == propertyForCheck.defaultValue.orNull()
            availableValues.size() == propertyForCheck.availableValues.size()
            required == propertyForCheck.required
        }
    }

    def "configuration updates from external place should send to all consumers"() {
        given:
        def property = UnidataConfigurationProperty.values().find {
            it.defaultValue != null
        }

        when:
        def configurationService = new RuntimePropertiesServiceImpl(
                configurationCacheService,
                [firstConfigurationUpdatesProducer, secondConfigurationUpdatesProducer],
                [],
                [configurationUpdatesConsumer]
        )
        updatesPublisher.subscribe(
                new BaseSubscriber<Map<String, Optional<? extends Serializable>>>() {
                    @Override
                    protected void hookOnNext(Map<String, Optional<? extends Serializable>> value) {
                        assert value == [(property.key): property.defaultValue]
                    }
                }
        )
        configurationService.updatePropertiesValuesFromExternalPlace([(property.key): property.defaultValue.toString()])

        then:
        1 * configurationUpdatesConsumer.subscribe(_) >>{
            updatesPublisher = it[0]
        }
    }
}
