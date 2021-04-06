package com.unidata.mdm.backend.service.job.exchange.in;

import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initSingleDataSource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.po.ImportErrorPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.RelationDef;

@StepScope
public class ImportDataJobItemReader extends JdbcCursorItemReader<ImportDataSet> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Bulk size.
     */
    @Value("#{stepExecutionContext[" + ImportDataJobConstants.PARAM_BLOCK_SIZE + "]}")
    private Integer blockSize;
    /**
     * Offset.
     */
    @Value("#{stepExecutionContext[" + ImportDataJobConstants.PARAM_OFFSET + "]}")
    private Integer offset;
    /**
     * Database url.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DATABASE_URL + "]}")
    private String databaseUrl;
    /**
     * DB vendor.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_IMPORT_DATABASE_VENDOR + "] ?: 'POSTGRESQL'}")
    protected DatabaseVendor databaseVendor;
    /**
     * Operation id
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * Previous successful start date.
     */
    @Value("#{jobParameters[" +  JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE + "]}")
    protected Date previousSuccessStartDate;
    /**
     * Audit writer.
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * The MMS.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Classifier cache
     */
    @Autowired
    private ClsfService classifierService;
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, AttributeInfoHolder> attrs;
        ImportDataJobStepExecutionState parameters = ImportDataJobUtils.getStepState();
        if (parameters.exchangeObjectIsEntity()) {

            DbExchangeEntity exchangeEntity = parameters.getExchangeObject();

            // 1. Entity attributes
            attrs = metaModelService.getAttributesInfoMap(exchangeEntity.getName());

            // 2. Classifier attributes
            // This is a temporary solution
            // This doesn't cover the use case,
            // where different attributes with different data types, but the same name
            // exist on different branches.
            Map<String, Map<String, ClsfNodeAttrDTO>> classifierAttributes = new HashMap<>();
            List<String> classifiersNames = metaModelService.getClassifiersForEntity(exchangeEntity.getName());
            if (CollectionUtils.isNotEmpty(classifiersNames)) {

                for (String classifierName : classifiersNames) {
                    List<ClsfNodeAttrDTO> attributes = classifierService.getAllClsfAttr(classifierName);
                    if (CollectionUtils.isEmpty(attributes)) {
                        continue;
                    }

                    classifierAttributes.put(classifierName,
                            attributes.stream().collect(Collectors.toMap(ClsfNodeAttrDTO::getAttrName, dto -> dto)));
                }
            }

            super.setRowMapper(new EntityRowMapper(exchangeEntity, attrs, classifierAttributes));
            super.setSql(ImportDataJobUtils.getSql(exchangeEntity, offset, blockSize, databaseVendor, previousSuccessStartDate));

        } else if (parameters.exchangeObjectIsRelation()) {

            ExchangeRelation exchangeRelation = parameters.getExchangeObject();
            RelationDef def = metaModelService.getRelationById(exchangeRelation.getRelation());
            if (exchangeRelation instanceof ContainmentRelation) {

                ContainmentRelation containmentRelation = (ContainmentRelation) exchangeRelation;
                DbExchangeEntity containmentEntity = (DbExchangeEntity) containmentRelation.getEntity();

                attrs = metaModelService.getAttributesInfoMap(containmentEntity.getName());
                super.setRowMapper(new ContainsRelationRowMapper(containmentRelation, new EntityRowMapper(containmentEntity, attrs, null)));
            } else {

                DbRelatesToRelation relTo = (DbRelatesToRelation) exchangeRelation;

                attrs = metaModelService.getAttributesInfoMap(exchangeRelation.getRelation());
                super.setRowMapper(new ReferenceRelationRowMapper(relTo, attrs, def.getToEntity()));
            }

            super.setSql(ImportDataJobUtils.getSql(exchangeRelation, offset, blockSize, databaseVendor, previousSuccessStartDate));
        }

        super.setDataSource(initSingleDataSource(databaseUrl));
        super.setDriverSupportsAbsolute(true);
        super.afterPropertiesSet();
    }

    @Override
    protected ImportDataSet readCursor(ResultSet rs, int currentRow) throws SQLException {
        try {
            return super.readCursor(rs, currentRow);
        } catch (Exception e) {
            ResultSetMetaData metaData = rs.getMetaData();
            StringBuilder rowDescription = new StringBuilder("[");
            for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                rowDescription.append(rs.getObject(i));
                rowDescription.append(",");
            }
            rowDescription.append("]");
            Integer numberInSet = offset * blockSize + currentRow;
            LOGGER.error("Record can not be read.\n OperationId: {}.\n Row: {}.\n Sql: {}.\n Record index: {}.\n", operationId, rowDescription.toString(), getSql(), numberInSet, e);
            ImportErrorPO errorPO = new ImportErrorPO(null, rowDescription.toString(), operationId, numberInSet, getSql());
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_IMPORT, e, errorPO);

            ImportDataJobStepExecutionState state = ImportDataJobUtils.getStepState();
            state.incrementFailed(1);

            return ImportDataJobConstants.DUMMY_RECORD;
        }
    }

    @Override
    public int getCurrentItemCount() {
        return super.getCurrentItemCount();
    }
    /**
     * @param operationId the operation id
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    /**
     * @param databaseUrl the databaseUrl to set
     */
    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    /**
     * @param bulkSize the bulkSize to set
     */
    public void setBlockSize(Integer bulkSize) {
        this.blockSize = bulkSize;
    }
    /**
     * @param offset the offset to set
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
