package com.unidata.mdm.backend.configuration.application

import spock.lang.Specification
import com.google.common.base.Optional

class ValueValidatorsSpec extends Specification {

    def "int validator should accept only int strings"() {
        expect:
        ValueValidators.INT_VALIDATOR.test(Optional.fromNullable(value)) == result

        where:
        value   | result
        null    | false
        ""      | false
        "1.9"   | false
        "1"     | true
        "9999"  | true
    }

    def "boolean validator should accept only true or false ignore case strings"() {
        expect:
        ValueValidators.BOOLEAN_VALIDATOR.test(Optional.fromNullable(value)) == result

        where:
        value   | result
        null    | false
        ""      | false
        "1"     | false
        "True"  | true
        "TRUE"  | true
        "true"  | true
        "False" | true
        "FALSE" | true
        "false" | true
    }
}
