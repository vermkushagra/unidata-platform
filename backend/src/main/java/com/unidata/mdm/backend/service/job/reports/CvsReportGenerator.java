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

package com.unidata.mdm.backend.service.job.reports;

import com.unidata.mdm.backend.common.ReportInfoHolder;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.Report;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import com.unidata.mdm.backend.util.reports.cvs.CvsHeader;
import com.unidata.mdm.backend.util.reports.cvs.CvsReport;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CvsReportGenerator<I extends ReportInfoHolder> extends ReportGenerator {

    /**
     * CVS mime type
     */
    private static final String CVS_MIME_TYPE = "text/x-cvs";

    /**
     * Csv separator
     */
    private char separator;

    /**
     * Char set
     */
    private String charSet;

    @Override
    protected final byte[] getReport(JobExecution jobExecution) {
        return getReport(jobExecution, getInfo());
    }

    protected final byte[] getReport(JobExecution jobExecution, Collection<? extends I> reportInformation) {
        if(CollectionUtils.isEmpty(reportInformation)){
            return null;
        }
        Report<String> report = new CvsReport(separator, charSet);

        Arrays.stream(getCvsHeaders()).map(CvsHeader::headerName).forEach(headerName -> {
            // see UN-3868 and http://www.alunr.com/excel-csv-import-returns-an-sylk-file-format-error/
            headerName = MessageUtils.getMessage(headerName);
            if("ID".equals(headerName)){
                headerName = " " + headerName;
            }
            report.addElement(headerName);
        });
        report.newRow();
        for (I info : reportInformation) {
            Arrays.stream(getCvsHeaders()).map(xs -> xs.getElement(info)).forEach(report::addElement);
            report.newRow();
        }
        return report.generate();
    }

    @Required
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    protected abstract Collection<? extends I> getInfo();

    protected abstract CvsElementExtractor<I>[] getCvsHeaders();

    @Override
    public void setReportType(String reportType) {
        super.setReportType(CVS_MIME_TYPE);
    }
}
