/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.util.reports.string;

import static com.unidata.mdm.backend.util.reports.ReportUtil.COLON;
import static com.unidata.mdm.backend.util.reports.ReportUtil.DOT;
import static com.unidata.mdm.backend.util.reports.ReportUtil.SPACE;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

/**
 * Create report which look like ''
 */
//todo think about generalization (in case when more when two sentence will be present)
public class FailedSuccessReport {

    private final int successCount;

    private final int failedCount;

    private final String emptyMessage;

    private final String successMessage;

    private final String failedMessage;

    private boolean noTrailingSpace;

    private boolean noTrailingDot;

    private final Function<Integer, String> mapper;

    FailedSuccessReport(int success, int failed, String empty, String successMessage, String failedMessage,
            Function<Integer, String> mapper, boolean noTrailingSpace, boolean noTrailingDot) {
        this.successCount = success;
        this.failedCount = failed;
        this.emptyMessage = empty;
        this.successMessage = successMessage;
        this.failedMessage = failedMessage;
        this.mapper = mapper;
        this.noTrailingSpace = noTrailingSpace;
        this.noTrailingDot = noTrailingDot;
    }

    public String generateReport() {

        if ((successCount + failedCount) == 0) {
            return SPACE + emptyMessage;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SPACE);
        stringBuilder.append(successMessage);
        stringBuilder.append(COLON);
        stringBuilder.append(SPACE);
        stringBuilder.append(successCount);
        stringBuilder.append(noTrailingSpace ? StringUtils.EMPTY : SPACE);
        stringBuilder.append(mapper.apply(successCount));
        stringBuilder.append(noTrailingDot ?  StringUtils.EMPTY : DOT);
        if (failedCount > 0) {
            stringBuilder.append(SPACE);
            stringBuilder.append(failedMessage);
            stringBuilder.append(COLON);
            stringBuilder.append(SPACE);
            stringBuilder.append(failedCount);
            stringBuilder.append(noTrailingSpace ? StringUtils.EMPTY : SPACE);
            stringBuilder.append(mapper.apply(failedCount));
            stringBuilder.append(noTrailingDot ?  StringUtils.EMPTY : DOT);
        }

        return stringBuilder.toString();
    }

    public static FailedSuccessReportBuilder builder(){
        return new FailedSuccessReportBuilder();
    }
}