package com.unidata.mdm.backend.service.classifier

import spock.lang.Specification

class BatchCollectorSpec extends Specification {
    def "test BatchCollector"() {
        given:
        def range = 1..22

        when:
        def rangeToBatches = range.toList().stream().collect(new ClsfServiceImpl.BatchCollector<Integer>(5))

        then:
        rangeToBatches.size() == 5
        rangeToBatches.get(0).size() == 5
        rangeToBatches.get(0).get(2) == 3
        rangeToBatches.get(4).size() == 2
    }
}
