package com.unidata.mdm.backend.util.reports.cvs;

import com.unidata.mdm.backend.common.ReportInfoHolder;
import com.unidata.mdm.backend.util.reports.ElementExtractor;
import com.unidata.mdm.backend.util.reports.cvs.CvsHeader;

public interface CvsElementExtractor<I extends ReportInfoHolder> extends CvsHeader, ElementExtractor<String, I> {
}
