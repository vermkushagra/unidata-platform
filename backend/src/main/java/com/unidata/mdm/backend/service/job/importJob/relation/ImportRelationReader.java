package com.unidata.mdm.backend.service.job.importJob.relation;

import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initSingleDataSource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.dao.impl.ImportErrorDao;
import com.unidata.mdm.backend.po.ImportErrorPO;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRelationSet;

@Component("importRelationReader")
@StepScope
public class ImportRelationReader extends JdbcCursorItemReader<ImportRelationSet> {

    static final ImportRelationSet DUMMY_RELATION = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportRelationReader.class);

    private String databaseUrl;

    private String operationId;

    @Autowired
    private ImportErrorDao importErrorDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getDataSource() == null) {
            setDataSource(initSingleDataSource(databaseUrl));
        }
        super.afterPropertiesSet();
    }

    @Override
    protected ImportRelationSet readCursor(ResultSet rs, int currentRow) throws SQLException {
        try {
            return super.readCursor(rs, currentRow);
        } catch (Exception e) {
            ResultSetMetaData metaData = rs.getMetaData();
            StringBuilder rowDescription = new StringBuilder("");
            for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                rowDescription.append(metaData.getColumnName(i));
                rowDescription.append("=");
                rowDescription.append(rs.getObject(i));
                rowDescription.append("\n");
            }
            LOGGER.error("Relation can not be read.\n OperationId: {}.\n Row: {}.\n Sql: {}.\n Record index: {}.\n", operationId, rowDescription.toString(), getSql(), currentRow, e);
            ImportErrorPO errorPO = new ImportErrorPO(e.toString(), rowDescription.toString(), operationId, currentRow, getSql());
            importErrorDao.logError(errorPO);
            return DUMMY_RELATION;
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

    public void setImportErrorDao(ImportErrorDao importErrorDao) {
        this.importErrorDao = importErrorDao;
    }
}
