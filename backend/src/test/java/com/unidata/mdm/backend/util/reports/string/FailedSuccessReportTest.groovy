package com.unidata.mdm.backend.util.reports.string

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Function

class FailedSuccessReportTest extends Specification {

    @Shared
    private Function<Integer, String> mapper = { Integer value -> "times" };

    @Unroll
    def "check report builder asserts"() {
        setup:
        FailedSuccessReportBuilder reportBuilder = FailedSuccessReport.builder();
        when:
        reportBuilder.emptyMessage = empty;
        reportBuilder.mapper = mapper;
        reportBuilder.successMessage = success;
        reportBuilder.failedMessage = failed;
        reportBuilder.createFailedSuccessReport();
        then:
        thrown(AssertionError);
        where:
        empty | success | failed | mapper
        "Not" | null    | "No"   | { Integer value -> value.toString() }
        "Not" | "Yes"   | null   | { Integer value -> value.toString() }
        "Not" | "Yes"   | "No"   | null
        null  | "Yes"   | "No"   | { Integer value -> value.toString() }
    }

    @Unroll
    def "check that report correct for empty result and equal (#empty)"() {
        setup:
        FailedSuccessReportBuilder reportBuilder = FailedSuccessReport.builder();
        when:
        reportBuilder.emptyMessage = empty;
        reportBuilder.mapper = mapper;
        reportBuilder.successMessage = success;
        reportBuilder.failedMessage = failed;
        FailedSuccessReport report = reportBuilder.createFailedSuccessReport();
        then:
        assert report.generateReport() == empty;
        where:
        empty  | success | failed
        "Not1" | "Yes"   | "No"
        "Not2" | "Yes"   | "No"
        "Not3" | "Yes"   | "No"
        "Not4" | "Yes"   | "No"
    }

    @Unroll
    def "check that report correct for only success results and equals (#result)"() {
        setup:
        FailedSuccessReportBuilder reportBuilder = FailedSuccessReport.builder();
        when:
        reportBuilder.emptyMessage = empty;
        reportBuilder.mapper = mapper;
        reportBuilder.successMessage = success;
        reportBuilder.failedMessage = failed;
        reportBuilder.successCount = successCount;
        FailedSuccessReport report = reportBuilder.createFailedSuccessReport();
        then:
        assert report.generateReport() == result;
        where:
        empty  | successCount | success  | failed | result
        "Not1" | 1            | "Yes"    | "No"   | " Yes: 1 times."
        "Not2" | 2            | "Yess"   | "No"   | " Yess: 2 times."
        "Not3" | 3            | "Yesss"  | "No"   | " Yesss: 3 times."
        "Not4" | 4            | "Yessss" | "No"   | " Yessss: 4 times."
    }

    @Unroll
    def "check that report correct for only failed results and equals (#result)"() {
        setup:
        FailedSuccessReportBuilder reportBuilder = FailedSuccessReport.builder();
        when:
        reportBuilder.emptyMessage = empty;
        reportBuilder.mapper = mapper;
        reportBuilder.successMessage = success;
        reportBuilder.failedMessage = failed;
        reportBuilder.failedCount = failedCount;
        FailedSuccessReport report = reportBuilder.createFailedSuccessReport();
        then:
        assert report.generateReport() == result;
        where:
        empty  | successCount | success  | failedCount | failed  | result
        "Not1" | 0            | "Yes"    | 1           | "No"    | " Yes: 0 times. No: 1 times."
        "Not2" | 0            | "Yess"   | 2           | "Noo"   | " Yess: 0 times. Noo: 2 times."
        "Not3" | 0            | "Yesss"  | 3           | "Nooo"  | " Yesss: 0 times. Nooo: 3 times."
        "Not4" | 0            | "Yessss" | 4           | "Noooo" | " Yessss: 0 times. Noooo: 4 times."
    }

    @Unroll
    def "check that report correct for mix results and equals (#result)"() {
        setup:
        FailedSuccessReportBuilder reportBuilder = FailedSuccessReport.builder();
        when:
        reportBuilder.emptyMessage = empty;
        reportBuilder.mapper = mapper;
        reportBuilder.successMessage = success;
        reportBuilder.failedMessage = failed;
        reportBuilder.failedCount = failedCount;
        reportBuilder.successCount = successCount;
        FailedSuccessReport report = reportBuilder.createFailedSuccessReport();
        then:
        assert report.generateReport() == result;
        where:
        empty  | successCount | success  | failedCount | failed  | result
        "Not1" | 1            | "Yes"    | 1           | "No"    | " Yes: 1 times. No: 1 times."
        "Not2" | 2            | "Yess"   | 2           | "Noo"   | " Yess: 2 times. Noo: 2 times."
        "Not3" | 3            | "Yesss"  | 3           | "Nooo"  | " Yesss: 3 times. Nooo: 3 times."
        "Not4" | 4            | "Yessss" | 4           | "Noooo" | " Yessss: 4 times. Noooo: 4 times."
    }

}
