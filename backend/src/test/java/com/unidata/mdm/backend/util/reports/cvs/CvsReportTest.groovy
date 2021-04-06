package com.unidata.mdm.backend.util.reports.cvs

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test for cvs reporter
 */
class CvsReportTest extends Specification {

    @Unroll("#elemetns should be converted to cvs like #result")
    def "single row check"() {
        setup: "create cvs report"
        CvsReport cvsReport = new CvsReport(';' as char, charSet);
        when: "add elements to report"
        elemetns.each { cvsReport.addElement(it as String) }
        then: "check that byte arrays is the same"
        assert cvsReport.generate() == result.getBytes(charSet);
        where:
        elemetns | result                    | charSet
        0..10    | '0;1;2;3;4;5;6;7;8;9;10;' | "utf-8"
        0..5     | '0;1;2;3;4;5;'            | "ansi-1251"
    }

    @Unroll
    def "multi row check"() {
        setup:
        CvsReport cvsReport = new CvsReport(';' as char, charSet);
        when:
        elemetns.each { cvsReport.addElement(it as String) }
        cvsReport.newRow();
        elemetns.each { cvsReport.addElement(it as String) }
        then:
        assert cvsReport.generate() == result.getBytes(charSet);
        where:
        elemetns | result                                             | charSet
        0..10    | '0;1;2;3;4;5;6;7;8;9;10;\n0;1;2;3;4;5;6;7;8;9;10;' | "utf-8"
        0..5     | '0;1;2;3;4;5;\n0;1;2;3;4;5;'                       | "ansi-1251"
    }

    @Unroll
    def "check replacement of separator and new row"() {
        setup:
        CvsReport cvsReport = new CvsReport(';' as char, charSet);
        when:
        elemetns.each { cvsReport.addElement(it as String) }
        then:
        assert cvsReport.generate() == result.getBytes(charSet);
        where:
        elemetns                    | result           | charSet
        ["a", "b", "c;cc"]          | 'a;b;c cc;'      | "utf-8"
        ["a|c", "b..", "c;cc!"]     | 'a|c;b..;c cc!;' | "utf-8"
        ["a\naa", ";b;", "c;;\ncc"] | 'aaa; b ;c  cc;' | "ansi-1251"
    }
}
