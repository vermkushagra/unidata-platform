package com.unidata.mdm.backend.service.job.exchange.out;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;

/**
 * @author Denis Kostovarov
 */
@Component
@Scope(value = "step")
public class ExportDataItemProcessor extends ExportDataStepChainMember implements ItemProcessor<Long, GetRecordDTO> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataConstants.EXPORT_JOB_LOGGER_NAME);
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * The operation id to use.
     */
    @Value("#{stepExecutionContext[operationId]}")
    private String operationId;
    /**
     * Updates after.
     */
    @Value("#{jobParameters[updatesAfter]}")
    private Date updatesAfter;
    /**
     * As of.
     */
    @Value("#{jobParameters[asOf]}")
    private Date asOf;
    /**
     * Skip relations or not.
     */
    @Value("#{jobParameters[skipRelations]}")
    private boolean skipRelations;
    /**
     * Records service component.
     */
    @Autowired
    private DataRecordsService recordServiceComponent;

    @Override
    public GetRecordDTO process(Long gsn) throws Exception {
        RecordKeys keys = commonRecordsComponent.identify(gsn);
        if (keys == null) {
            LOGGER.warn("GSN {}: not found. Skipping.");
            getStatisticPage().incrementFailed(1L);
            return null;
        }

        GetRequestContext ctx = GetRequestContext.builder()
                                                 .gsn(gsn)
                                                 .fetchRelations(!skipRelations)
                                                 .forDate(asOf)
                                                 .updatesAfter(updatesAfter)
                                                 .build();

        ctx.putToStorage(ctx.keysId(), keys);
        ctx.setOperationId(operationId);

        GetRecordDTO result = recordServiceComponent.getRecord(ctx);
        if (result == null || result.getEtalon() == null || result.getRecordKeys() == null) {
            LOGGER.info(
                    "GSN {}: Has no updates or doesn't have active periods for given parameters ['forDate': {}, 'updatesAfter' {}].",
                    gsn, asOf, updatesAfter);

            getStatisticPage().incrementSkept(1L);
            return null;
        }

        return result;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * @param updatesAfter the updatesAfter to set
     */
    public void setUpdatesAfter(Date updatesAfter) {
        this.updatesAfter = updatesAfter;
    }

    /**
     * @param asOf the asOf to set
     */
    public void setAsOf(Date asOf) {
        this.asOf = asOf;
    }
}
