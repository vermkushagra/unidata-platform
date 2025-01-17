package com.unidata.mdm.backend.util.reports.string;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class FailedSuccessReportBuilder {
    private int success;
    private int failed;
    private String empty;
    private String successMessage;
    private String failedMessage;
    private boolean noTrailingSpace;
    private boolean noTrailingDot;
    private Function<Integer, String> mapper;

    FailedSuccessReportBuilder() {
    }

    public FailedSuccessReportBuilder setSuccessCount(int success) {
        this.success = success;
        return this;
    }

    public FailedSuccessReportBuilder setFailedCount(int failed) {
        this.failed = failed;
        return this;
    }

    public FailedSuccessReportBuilder setEmptyMessage(String empty) {
        this.empty = empty;
        return this;
    }

    public FailedSuccessReportBuilder setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
        return this;
    }

    public FailedSuccessReportBuilder setFailedMessage(String failedMessage) {
        this.failedMessage = failedMessage;
        return this;
    }

    public FailedSuccessReportBuilder setMapper(Function<Integer, String> mapper) {
        this.mapper = mapper;
        return this;
    }

    public FailedSuccessReportBuilder noTrailingSpace(boolean noTrailingSpace) {
        this.noTrailingSpace = noTrailingSpace;
        return this;
    }

    public FailedSuccessReportBuilder noTrailingDot(boolean noTrailingDot) {
        this.noTrailingDot = noTrailingDot;
        return this;
    }

    public FailedSuccessReport createFailedSuccessReport() {
        assert !StringUtils.isBlank(successMessage);
        assert !StringUtils.isBlank(empty);
        assert !StringUtils.isBlank(failedMessage);
        assert mapper != null;
        return new FailedSuccessReport(success, failed, empty, successMessage, failedMessage, mapper, noTrailingSpace, noTrailingDot);
    }
}