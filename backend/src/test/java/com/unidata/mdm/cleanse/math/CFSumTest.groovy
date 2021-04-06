package com.unidata.mdm.cleanse.math

import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl
import com.unidata.mdm.cleanse.common.CleanseConstants
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class CFSumTest extends Specification {
    @Shared
    CFSum cfSum = new CFSum();

    @Shared
    Map<String, Object> input = new HashMap<>();

    @Unroll
    def "Sum of (#first.getValue()) and (#second.getValue()) should be equal (#result.getValue())"() {
        when: ""
            input.put(CleanseConstants.INPUT1, first);
            input.put(CleanseConstants.INPUT2, second);
            Map<String, Object> res = cfSum.execute(input);
        then:
            noExceptionThrown()
            res.get(CleanseConstants.OUTPUT1).equals(result)
        where:
            first                                  || second                                || result
            new NumberSimpleAttributeImpl("", 10d)  | new NumberSimpleAttributeImpl("", 10d) | new NumberSimpleAttributeImpl("", 20d)
            new NumberSimpleAttributeImpl("", 15d)  | new NumberSimpleAttributeImpl("", 10d) | new NumberSimpleAttributeImpl("", 25d)
            new NumberSimpleAttributeImpl("", -15d) | new NumberSimpleAttributeImpl("", 10d) | new NumberSimpleAttributeImpl("", -5d)
            new NumberSimpleAttributeImpl("", 0d)   | new NumberSimpleAttributeImpl("", 10d) | new NumberSimpleAttributeImpl("", 10d)
            new NumberSimpleAttributeImpl("", -5d)  | new NumberSimpleAttributeImpl("", 10d) | new NumberSimpleAttributeImpl("", 5d)
    }

    @Unroll
    def "Sum of (#first) and (#second.getValue()) should throw exception"() {
        when: ""
        input.put(CleanseConstants.INPUT1, first);
        input.put(CleanseConstants.INPUT2, second);
        Map<String, Object> res = cfSum.execute(input);
        then:
        thrown(CleanseFunctionExecutionException.class);
        where:
        first || second
        null   | new NumberSimpleAttributeImpl("", 10d)
        null   | new NumberSimpleAttributeImpl("", 10d)
        null   | new NumberSimpleAttributeImpl("", 10d)
        null   | new NumberSimpleAttributeImpl("", 10d)
        null   | new NumberSimpleAttributeImpl("", 10d)
    }
}
