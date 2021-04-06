package com.unidata.mdm.backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.dao.DQErrorsDao;
import com.unidata.mdm.backend.dao.rm.DQErrorsExtractor;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplateImpl;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplateImpl;
import com.unidata.mdm.backend.po.DQErrorPO;


/**
 * The Class DQErrorsDaoImpl.
 */
@Repository
public class DQErrorsDaoImpl implements DQErrorsDao {

	/** The Constant ID. */
	private static final String ID = "id";

	/** The Constant REQUEST_ID. */
	private static final String REQUEST_ID = "request_id";

	/** The Constant RECORD_ID. */
	private static final String RECORD_ID = "record_id";

	/** The Constant ENTITY_NAME. */
	private static final String ENTITY_NAME = "entity_name";

	/** The Constant RULE_NAME. */
	private static final String RULE_NAME = "rule_name";

	/** The Constant SEVERITY. */
	private static final String SEVERITY = "severity";

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant CATEGORY. */
	private static final String CATEGORY = "category";

	/** The Constant MESSAGE. */
	private static final String MESSAGE = "message";

	/** The Constant CREATED_AT. */
	private static final String CREATED_AT = "created_at";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "created_by";

	/** The Constant UPDATED_AT. */
	private static final String UPDATED_AT = "updated_at";

	/** The Constant UPDATED_BY. */
	private static final String UPDATED_BY = "updated_by";

	/** The Constant DQ_ERRORS. */
	private static final String DQ_RESULTS = "unidata_dq.dq_results";

	/** The Constant SELECT_BY_RECORD_ID. */
	private static final String SELECT_BY_RECORD_ID = "SELECT DISTINCT ON (BY_REC.record_id) * "
			+ "FROM "
			+ "("
			+ "select id, request_id, record_id, entity_name, rule_name, severity, status, category, message, created_at, created_by, updated_at, updated_by "
			+ "from unidata_dq.dq_results where record_id=:record_id"
			+ ") AS BY_REC ORDER  BY BY_REC.record_id, BY_REC.created_at DESC";

	/** The Constant DELETE_BY_RECORD_ID. */
	private static final String DELETE_BY_RECORD_ID = "delete from unidata_dq.dq_results where record_id=:record_id";

	/** The Constant INSERT_BATCH. */
	private static final String INSERT_BATCH = "INSERT INTO unidata_dq.dq_results "
			+ "(request_id, record_id, entity_name, rule_name, severity, status, category, created_at, created_by, updated_at, updated_by) "
			+ "VALUES(?, ?, ?, string_to_array(?,',')::varchar[], string_to_array(?,',')::unidata_dq.dq_severity[], string_to_array(?,',')::unidata_dq.dq_status[],  string_to_array(?,',')::varchar[], ?, ?, ?, ?)";
	/**
	 * JDBC template.
	 */
	protected UnidataJdbcTemplate jdbcTemplate;
	/**
	 * Named parameter template.
	 */
	protected UnidataNamedParameterJdbcTemplate namedJdbcTemplate;

	/**
	 * Instantiates a new DQ errors dao impl.
	 *
	 * @param dataSource
	 *            the data source
	 */
	@Autowired
	public DQErrorsDaoImpl(DataSource dataSource) {
		super();
		this.jdbcTemplate = new UnidataJdbcTemplateImpl(dataSource);
		this.namedJdbcTemplate = new UnidataNamedParameterJdbcTemplateImpl(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.dao.DQErrorsDao#saveErrors(java.util.List)
	 */
	@Override
	public int[] saveErrors(List<DQErrorPO> errors) {
		BatchPreparedStatementSetter batchSetter = new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				DQErrorPO error = errors.get(i);
				ps.setString(1, error.getRequestId());
				ps.setString(2, error.getRecordId());
				ps.setString(3, error.getEntityName());
				ps.setString(4, String.join(",",error.getRuleName()));
				ps.setString(5, String.join(",",error.getSeverity()));
				ps.setString(6, String.join(",",error.getStatus()));
				ps.setString(7, String.join(",",error.getCategory()));
//				ps.setString(8, String.join(",",error.getMessage()));
				ps.setTimestamp(8, new java.sql.Timestamp(error.getCreatedAt().getTime()));
				ps.setString(9, error.getCreatedBy());
				ps.setTimestamp(10, error.getUpdatedAt()==null?null:new java.sql.Timestamp(error.getUpdatedAt().getTime()));
				ps.setString(11, error.getUpdatedBy());

			}

			@Override
			public int getBatchSize() {
				return errors.size();
			}
		};
		return jdbcTemplate.batchUpdate(INSERT_BATCH, batchSetter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.DQErrorsDao#getErrorsByRecordID(java.lang.
	 * String)
	 */
	@Override
	public DQErrorPO getErrorsByRecordID(String recordID) {
		Map<String, Object> params = new HashMap<>();
		params.put(RECORD_ID, recordID);
		List<DQErrorPO> errors = namedJdbcTemplate.query(SELECT_BY_RECORD_ID, params, new DQErrorsExtractor());
		return errors.size()==0?null:errors.get(0);
	}

}
