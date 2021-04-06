package com.unidata.mdm.backend.service.job.importJob.record;

import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initSingleDataSource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.actions.impl.data.ExportDataAuditAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.po.ImportErrorPO;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRecordSet;
import com.unidata.mdm.backend.service.security.utils.SecurityConstants;

@StepScope
public class ImportRecordReader extends JdbcCursorItemReader<ImportRecordSet> {

    /**
     * Dummy record
     */
    static final ImportRecordSet DUMMY_RECORD = new ImportRecordSet();

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportRecordReader.class);

    /**
     * database url
     */
    private String databaseUrl;

    /**
     * Operation id
     */
    private String operationId;

    /**
     * batch size
     */
    private Integer batchSize;

    /**
     * step number
     */
    private Integer step;

    /**
     * The name of the user, who started the job.
     */
    private String userName;

    /**
     * The token of the user, who started the job.
     */
    private String userToken;

    @Autowired
    private AuditEventsWriter auditEventsWriter;

    /**
     * The security service.
     */
    @Autowired
    private SecurityService securityService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getDataSource() == null) {
            setDataSource(initSingleDataSource(databaseUrl));
        }

        // Set authentication for all import actions, started by this thread/partition.
        if (Objects.nonNull(userName) && !SecurityConstants.SYSTEM_USER_NAME.equals(userName) && Objects.nonNull(userToken)) {

            boolean authenticated = securityService.authenticate(userToken, true);
            LOGGER.info("Initiator user [{}] {} authenticated.", userName, authenticated ? "successfully" : "could NOT be");
        }

        super.afterPropertiesSet();
    }

    @Override
    protected ImportRecordSet readCursor(ResultSet rs, int currentRow) throws SQLException {
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
            Integer numberInSet = step * batchSize + currentRow;
            LOGGER.error("Record can not be read.\n OperationId: {}.\n Row: {}.\n Sql: {}.\n Record index: {}.\n", operationId, rowDescription.toString(), getSql(), numberInSet, e);
            ImportErrorPO errorPO = new ImportErrorPO(null, rowDescription.toString(), operationId, numberInSet, getSql());
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_EXPORT, e, errorPO);
            return DUMMY_RECORD;
        }
    }

    @Override
    public int getCurrentItemCount() {
        return super.getCurrentItemCount();
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public void setAuditEventsWriter(AuditEventsWriter auditEventsWriter) {
        this.auditEventsWriter = auditEventsWriter;
    }

    @Required
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    @Required
    public void setStep(Integer step) {
        this.step = step;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param userToken the userToken to set
     */
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
