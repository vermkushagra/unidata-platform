package com.unidata.mdm.backend.service.job.reindex;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * @author Denis Kostovarov
 *         Not really a 'reader', but rather a preprocessor.
 */
@Component
@StepScope
public class ReindexDataJobDataItemReader extends JdbcCursorItemReader<Pair<Long, String>> {
    /**
     * Run id.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_RUN_ID + "]}")
    private String runId;
    /**
     * Operation id.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * Entity name.
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_ENTITY_NAME + "]}")
    private String entityName;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_START_GSN + "]}")
    private Long startGsn;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_END_GSN + "]}")
    private Long endGsn;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_EXECUTION_MODE + "] ?: 'DEFAULT'}")
    private ReindexDataJobExecutionMode mode;
    /**
     * Data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * Row mapper.
     */
    private static final RowMapper<Pair<Long, String>> NEXT_GSN_ROW_MAPPER = (rs, rowNum) -> {
        Long gsn = rs.getLong(1);
        String name = rs.getString(2);
        return new ImmutablePair<Long, String>(gsn, name);
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        super.setDataSource(unidataDataSource);
        super.setSaveState(true);
        super.setDriverSupportsAbsolute(true);
        super.setRowMapper(NEXT_GSN_ROW_MAPPER);
        super.setSql(getQuery());
        super.afterPropertiesSet();
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @param startGsn the startGsn to set
     */
    public void setStartGsn(Long startGsn) {
        this.startGsn = startGsn;
    }

    /**
     * @param endGsn the endGsn to set
     */
    public void setEndGsn(Long endGsn) {
        this.endGsn = endGsn;
    }

    /**
     * @param unidataDataSource the unidataDataSource to set
     */
    public void setUnidataDataSource(DataSource unidataDataSource) {
        this.unidataDataSource = unidataDataSource;
    }
    /**
     * Gets the appropriate query.
     * @return query string
     */
    private String getQuery() {

        if (mode == ReindexDataJobExecutionMode.DEFAULT
         || mode == ReindexDataJobExecutionMode.IMPORT_RELATIONS_INITIAL_MULTIVERSIONS
         || mode == ReindexDataJobExecutionMode.IMPORT_RECORDS_INITIAL_MULTIVERSIONS) {
            return new StringBuilder()
                    .append("select gsn, name from etalons where ")
                    .append(Objects.nonNull(entityName) ? "name = '" + entityName + "' and " : "")
                    .append("gsn >= ")
                    .append(startGsn)
                    .append(" and gsn <= ")
                    .append(endGsn)
                    .append(" and status in ('ACTIVE', 'INACTIVE')")
                    .toString();
        } else if (mode == ReindexDataJobExecutionMode.IMPORT_RECORDS_UPDATE) {
            return new StringBuilder()
                    .append("select gsn, name from etalons where ")
                    .append(Objects.nonNull(entityName) ? "name = '" + entityName + "' and " : "")
                    .append("gsn >= ")
                    .append(startGsn)
                    .append(" and gsn <= ")
                    .append(endGsn)
                    .append(" and status in ('ACTIVE', 'INACTIVE')")
                    .append(" and ((etalons.operation_id = '")
                    .append(operationId)
                    .append("' or exists (select true from origins o, origins_vistory v where o.etalon_id = etalons.id and o.id = v.origin_id and v.operation_id = '")
                    .append(operationId)
                    .append("')) or (exists (select true from etalons_classifiers e where e.etalon_id_record = etalons.id and e.operation_id = '")
                    .append(operationId)
                    .append("') or exists (select true from etalons_classifiers e, origins_classifiers o, origins_classifiers_vistory v where e.etalon_id_record = etalons.id and o.etalon_id = e.id and o.id = v.origin_id and v.operation_id = '")
                    .append(operationId)
                    .append("')))")
                    .toString();
        } else if (mode == ReindexDataJobExecutionMode.IMPORT_RELATIONS_UPDATE) {
            return new StringBuilder()
                    .append("select gsn, name from etalons where ")
                    .append(Objects.nonNull(entityName) ? "name = '" + entityName + "' and " : "")
                    .append("(gsn >= ")
                    .append(startGsn)
                    .append(" and gsn <= ")
                    .append(endGsn)
                    .append(") and status in ('ACTIVE', 'INACTIVE')")
                    .append(" and (exists (select true from etalons_relations e where e.etalon_id_from = etalons.id and e.operation_id = '")
                    .append(operationId)
                    .append("') or exists (select true from etalons_relations e, origins_relations o, origins_relations_vistory v where e.etalon_id_from = etalons.id and o.etalon_id = e.id and o.id = v.origin_id and v.operation_id = '")
                    .append(operationId)
                    .append("'))")
                    .toString();
        }

        // Provoke NPE.
        return null;
    }
}
