/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext.GetMultipleRequestContextBuilder;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.ExportToXlsInformationDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * The Class ExportRecordsToXlsBulkOperation.
 *
 * @author Mikhail Mikhailov Export records to XLS bulk operation.
 */
public class ExportRecordsToXlsBulkOperation extends AbstractBulkOperation {

    /** The job service. */
    @Autowired(required = false)
    private JobServiceExt jobServiceExt;

    /** Multiple request context key. */
    private final static String MRCTX = "MRCTX";

    /** The user. */
    private final static String USER_NAME = "USER_NAME";

    /**
     * Constructor.
     */
    public ExportRecordsToXlsBulkOperation() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean run(BulkOperationRequestContext ctx) {
        JobParameterDTO mrctxParameter = new JobParameterDTO(MRCTX,
                jobServiceExt.putComplexParameter(createMultipleRequestContext(ctx)));
        JobParameterDTO userParameter = new JobParameterDTO(USER_NAME,
                jobServiceExt.putComplexParameter(SecurityUtils.getCurrentUserName()));
        JobDTO xslxJob = new JobDTO();
        xslxJob.setDescription("Export records to XLSX");
        xslxJob.setName("Export Job");
        xslxJob.setEnabled(true);
        xslxJob.setJobNameReference("exportJob");
        xslxJob.setParameters(Arrays.asList(mrctxParameter, userParameter));
        jobServiceExt.startSystemJob(xslxJob);
        return true;
    }

    /**
     * Creates multiple request context from Search request context or from the
     * list of etalon records.
     *
     * @param ctx
     *            Bulk operation request context.
     * @return Get multiple request context.
     */
    private GetMultipleRequestContext createMultipleRequestContext(BulkOperationRequestContext ctx) {
        List<String> etalonIds = getEtalonIds(ctx);
        ComplexSearchRequestContext context = ctx.getApplyBySearchContext();
        Date forDate = context == null ? new Date() : context.getMainRequest().getAsOf();
        return new GetMultipleRequestContextBuilder().entityName(ctx.getEntityName())
                .forDate(forDate)
                .etalonKeys(etalonIds)
                .fetchRelations(true)
                .fetchClassifiers(true)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationInformationDTO configure() {
        // Nothing specific so far
        return new ExportToXlsInformationDTO();
    }
}
