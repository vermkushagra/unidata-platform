package com.unidata.mdm.cleanse.postaladdress

import com.unidata.mdm.backend.util.JaxbUtils
import com.unidata.mdm.data.*
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Pavel Alexeev.
 * @created 2016-01-20 20:49.
 */
@Ignore
class CFFiasCheckTest extends Specification {
    @Unroll
    def "test toBaseValue: #resType.simpleName (#from)"() {
        when:
            CFFiasCheck.toBaseValue(new CFFiasCheckTest())
        then:
            def e = thrown(IllegalArgumentException)
            e.message == "Can't create baseValue for type CFFiasCheckTest"

        when:
            BaseValue res = CFFiasCheck.toBaseValue(from)
        then:
            noExceptionThrown()
            check(res)
            res.class.isAssignableFrom(resType)
        where:
            from                                            || resType      || check
            10                                              | IntValue      | {it.getIntValue() == 10}
            10L                                             | IntValue      | {it.getIntValue() == 10}
            new Double(0.3)                                 | NumberValue   | {new Double(0.3).equals(it.getNumberValue())}
            new Date().parse('yyyy-MM-dd', '2016-01-26')    | DateValue     | { JaxbUtils.dateToXMGregorianCalendar(new Date().parse('yyyy-MM-dd', '2016-01-26')) == it.getDateValue() }
            "string"                                        | StringValue   | { "string" == it.getStringValue() }
            Boolean.TRUE                                    | BooleanValue  | { Boolean.TRUE == it.isBoolValue() }
    }

    /**
     * Did not test outputPortValue separately because address testing happened iin {@link com.unidata.mdm.cleanse.postaladdress.addressmaster.AddressmasterClientTest},
     * there most important output ports fill
     *
     * Requires additional dependencies and configure test Spring context. Does it have worth enable?
     */
/*    def "test execute"() {
        given:
            Map<String, Object> result;
            CFFiasCheck f = new CFFiasCheck();
        when:
            f.execute(
                [inputAddress: new SimpleAttribute().withName("inputAddress").withType(StringValue).withValue("Средний пр. 88а  Санкт-Петербург")]
                ,result
            );
        then:
            result
            result.size() == 8
    }*/
}
