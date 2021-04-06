/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.ContributorPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;


/**
 * @author Mikhail Mikhailov
 *
 */
public class TimeIntervalRowMapper implements RowMapper<TimeIntervalPO> {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(TimeIntervalRowMapper.class);

    /**
     * 'YYYY-MM-DD HH24:MI:SS.MS' in the DB.
     */
    private static final FastDateFormat CONTRIBUTOR_CREATE_DATE_FORMAT
    	= FastDateFormat.getInstance("\"yyyy-MM-dd HH:mm:ss.SSS\"");

    /**
     * Origin ID index.
     */
    private static final int IDX_ORIGIN_ID = 0;
    /**
     * Revision index.
     */
    private static final int IDX_REVISION = 1;
    /**
     * Source system index.
     */
    private static final int IDX_SOURCE_SYSTEM = 2;
    /**
     * Status index.
     */
    private static final int IDX_STATUS = 3;
    /**
     * Approval index.
     */
    private static final int IDX_APPROVAL = 4;
    /**
     * Owner index.
     */
    private static final int IDX_OWNER = 5;
    /**
     * Create date.
     */
    private static final int IDX_CREATE_DATE = 6;

    /**
     * Default row mapper.
     */
    public static final TimeIntervalRowMapper DEFAULT_TIME_INTERVAL_ROW_MAPPER
        = new TimeIntervalRowMapper();

    /**
     * Boundary row mapper.
     */
    public static final TimeIntervalRowMapper BOUNDARY_TIME_INTERVAL_ROW_MAPPER
        = new TimeIntervalRowMapper(true, false, false);

    /**
     * Default row mapper.
     */
    public static final TimeIntervalRowMapper RELATIONS_DEFAULT_TIME_INTERVAL_ROW_MAPPER
        = new TimeIntervalRowMapper(false, true, false);

    /**
     * Default row mapper.
     */
    public static final TimeIntervalRowMapper RECORDS_DEFAULT_TIME_INTERVAL_ROW_MAPPER
        = new TimeIntervalRowMapper(false, false, true);

    /**
     * Relation boundary extractor.
     */
    public static final ResultSetExtractor<TimeIntervalPO> DEFAULT_RELATIONS_BOUNDARY_EXTRACTOR
        = rs -> rs != null && rs.next() ? TimeIntervalRowMapper.BOUNDARY_TIME_INTERVAL_ROW_MAPPER.mapRow(rs, 0) : null;

    /**
     * Multiple relations by from etalon id result set extractor.
     */
    public static final ResultSetExtractor<Map<String, List<TimeIntervalPO>>> DEFAULT_RELATIONS_BY_FROM_ETALON_ID_EXTRACTOR
        = rs -> {

            Map<String, List<TimeIntervalPO>> map = new HashMap<>();
            int rowNum = 0;
            while (rs.next()) {
                TimeIntervalPO row = RELATIONS_DEFAULT_TIME_INTERVAL_ROW_MAPPER.mapRow(rs, ++rowNum);
                if (!map.containsKey(row.getRelationEtalonId())) {
                    map.put(row.getRelationEtalonId(), new ArrayList<>());
                }
                map.get(row.getRelationEtalonId()).add(row);
            }

            return map;
        };

    /**
     * Multiple relations by from etalon id result set extractor.
     */
    public static final ResultSetExtractor<Map<String, Map<String, List<TimeIntervalPO>>>> DEFAULT_COMPLETE_RELATIONS_EXTRACTOR
        = rs -> {

            Map<String, Map<String, List<TimeIntervalPO>>> map = new HashMap<>();
            int rowNum = 0;
            while (rs.next()) {

                TimeIntervalPO row = RELATIONS_DEFAULT_TIME_INTERVAL_ROW_MAPPER.mapRow(rs, ++rowNum);

                Map<String, List<TimeIntervalPO>> rel = map.computeIfAbsent(row.getName(), key -> new HashMap<>());
                rel.computeIfAbsent(row.getRelationEtalonId(), key -> new ArrayList<>()).add(row);
            }

            return map;
        };

        /**
         * Multiple relations by from etalon id result set extractor.
         */
        public static final ResultSetExtractor<Map<String, Map<String, List<TimeIntervalPO>>>> DEFAULT_COMPLETE_RECORDS_EXTRACTOR
            = rs -> {

                Map<String, Map<String, List<TimeIntervalPO>>> map = new HashMap<>();
                int rowNum = 0;
                while (rs.next()) {

                    TimeIntervalPO row = RECORDS_DEFAULT_TIME_INTERVAL_ROW_MAPPER.mapRow(rs, ++rowNum);

                    Map<String, List<TimeIntervalPO>> rel = map.computeIfAbsent(row.getName(), key -> new HashMap<>());
                    rel.computeIfAbsent(row.getRelationEtalonId(), key -> new ArrayList<>()).add(row);
                }

                return map;
            };

    /**
     * Boundary mapper.
     */
    private final boolean isBoundary;

    /**
     * Relations boundary mapper.
     */
    private final boolean isRelation;

    /**
     * Records timelene(s) mapper (multiple ids).
     */
    private final boolean isRecord;

    /**
     * Constructor.
     * @param isBoundary record or relation boundary
     * @param isRelation relation timeline
     * @param isRecord supports multiple record's timeline
     */
    public TimeIntervalRowMapper(boolean isBoundary, boolean isRelation, boolean isRecord) {
        super();
        this.isBoundary = isBoundary;
        this.isRelation = isRelation;
        this.isRecord = isRecord;
    }

    /**
     * Constructor.
     */
    public TimeIntervalRowMapper() {
        super();
        this.isBoundary = false;
        this.isRelation = false;
        this.isRecord = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeIntervalPO po = new TimeIntervalPO();

        po.setPeriodId(rs.getLong(TimeIntervalPO.FIELD_PERIOD_ID));
        po.setFrom(rs.getTimestamp(TimeIntervalPO.FIELD_VALID_FROM));
        po.setTo(rs.getTimestamp(TimeIntervalPO.FIELD_VALID_TO));
        po.setName(rs.getString(TimeIntervalPO.FIELD_NAME));

        ContributorPO[] contributors = null;
        Array versions = rs.getArray(TimeIntervalPO.FIELD_CONTRIBUTORS);
        if (versions != null) {
            contributors = extractContributors((String[]) versions.getArray());
            versions.free();
        }

        po.setContributors(contributors);

        if (isBoundary) {
            po.setCreateDate(rs.getTimestamp(TimeIntervalPO.FIELD_CREATE_DATE));
            po.setUpdateDate(rs.getTimestamp(TimeIntervalPO.FIELD_UPDATE_DATE));
            po.setCreatedBy(rs.getString(TimeIntervalPO.FIELD_CREATED_BY));
            po.setUpdatedBy(rs.getString(TimeIntervalPO.FIELD_UPDATED_BY));

            String status = rs.getString(TimeIntervalPO.FIELD_STATUS);
            po.setStatus(status == null ? null : RecordStatus.valueOf(status));

            String state = rs.getString(TimeIntervalPO.FIELD_STATE);
            po.setState(state == null ? null : ApprovalState.valueOf(state));

            po.setEtalonGsn(rs.getLong(TimeIntervalPO.FIELD_ETALON_GSN));
        }

        if (isRelation) {
            po.setRelationEtalonId(rs.getString(TimeIntervalPO.FIELD_RELATION_ETALON_ID));
        }

        if (isRecord) {
            po.setRecordEtalonId(rs.getString(TimeIntervalPO.FIELD_RECORD_ETALON_ID));
        }

        return po;
    }

    /**
     * Extracts contributors from serialized contributor strings.
     * @param lines contributor lines
     * @return arry of contributors or null
     */
    private ContributorPO[] extractContributors(String[] lines) {
        ContributorPO[] contributors = null;
        if (lines != null) {
            contributors = new ContributorPO[lines.length];
            for (int i = 0; i < lines.length; i++) {
                String l = lines[i];
                try {
                    String[] fields = l != null ? l.substring(1, l.length() - 1).split(",") : null;
                    if (fields != null) {
                        ContributorPO contributor = new ContributorPO();
                        contributor.setOriginId(fields[IDX_ORIGIN_ID].trim());
                        contributor.setRevision(Integer.valueOf(fields[IDX_REVISION].trim()));
                        contributor.setSourceSystem(fields[IDX_SOURCE_SYSTEM].trim());
                        contributor.setStatus(RecordStatus.valueOf(fields[IDX_STATUS].trim()));
                        contributor.setApproval(ApprovalState.valueOf(fields[IDX_APPROVAL].trim()));
                        contributor.setOwner(fields[IDX_OWNER].trim());
                        contributor.setLastUpdate(CONTRIBUTOR_CREATE_DATE_FORMAT.parse(fields[IDX_CREATE_DATE].trim()));

                        contributors[i] = contributor;
                    }
                } catch (Exception e) {
                    LOGGER.warn("Cannot deserialize contributor. Input: {}. Exception caught: {}.", l, e);
                }
            }
        }

        return contributors;
    }
}
