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

package com.unidata.mdm.backend.service.job.matching;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;

/**
 * @author Aleksandr Magdenko
 */
public enum MatchingJobCvsHeaders implements CvsElementExtractor<MatchingReportItemDTO> {
    CLUSTER_ID("app.job.matching.report.cluster.id") {
        @Nonnull
        @Override
        public String getElement(MatchingReportItemDTO item) {
            return item.getClusterId() == null ? EMPTY : String.valueOf(item.getClusterId());
        }
    },
    RULE_ID("app.job.matching.report.rule.id") {
        @Nonnull
        @Override
        public String getElement(MatchingReportItemDTO item) {
            return item.getRuleId() == null ? EMPTY : String.valueOf(item.getRuleId());
        }
    },
    RULE_NAME("app.job.matching.report.rule.name") {
        @Nonnull
        @Override
        public String getElement(MatchingReportItemDTO item) {
            return item.getRuleName() == null ? EMPTY : String.valueOf(item.getRuleName());
        }
    },
    ETALON_ID("app.job.matching.report.etalon.id") {
        @Nonnull
        @Override
        public String getElement(MatchingReportItemDTO item) {
            return item.getEtalonId() == null ? EMPTY : item.getEtalonId();
        }
    };

    /**
     * Empty result
     */
    private static final String EMPTY = "";

    /**
     * Header name
     */
    private final String header;

    MatchingJobCvsHeaders(String header) {
        this.header = header;
    }

    @Nonnull
    @Override
    public String headerName() {
        return header;
    }
}
