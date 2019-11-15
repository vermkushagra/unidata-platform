package org.unidata.mdm.core.dto.reports.csv;

import org.unidata.mdm.core.dto.reports.ElementExtractor;
import org.unidata.mdm.core.dto.reports.ReportInfoHolder;

public interface CvsElementExtractor<I extends ReportInfoHolder> extends CvsHeader, ElementExtractor<String, I> {
}
