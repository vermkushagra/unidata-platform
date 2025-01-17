package com.unidata.mdm.backend.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.dao.RoleDao;
import com.unidata.mdm.backend.dao.SecurityLabelDao;
import com.unidata.mdm.backend.dao.UserDao;
import com.unidata.mdm.backend.dao.rm.APIRowMapper;
import com.unidata.mdm.backend.dao.rm.PasswordRowMapper;
import com.unidata.mdm.backend.dao.rm.TokenRowMapper;
import com.unidata.mdm.backend.dao.rm.UserEventRowMapper;
import com.unidata.mdm.backend.dao.rm.UserPropertyRowMapper;
import com.unidata.mdm.backend.dao.rm.UserPropertyValueRowMapper;
import com.unidata.mdm.backend.dao.rm.UserRowMapper;
import com.unidata.mdm.backend.service.security.po.ApiPO;
import com.unidata.mdm.backend.service.security.po.PasswordPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.TokenPO;
import com.unidata.mdm.backend.service.security.po.UserEventPO;
import com.unidata.mdm.backend.service.security.po.UserPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO.FieldColumns;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * The Class UserDAOImpl.
 */
@Repository
public class UserDaoImpl extends AbstractDaoImpl implements UserDao {

    private static final String CONNECTION_TABLE = "s_user_s_label_attribute_value";

    /** The password row mapper. */
    private PasswordRowMapper passwordRowMapper = new PasswordRowMapper();

    /** The token row mapper. */
    private TokenRowMapper tokenRowMapper = new TokenRowMapper();

    /** The role property row mapper. */
    private UserPropertyRowMapper userPropertyRowMapper = new UserPropertyRowMapper();

    /**
     * Loads events by login and possibly given date.
     */
    private final String LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL;
    /**
     * Loads events by login and possibly given date and limit and offset.
     */
    private final String LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL;
    /**
     * Counts event by login.
     */
    private final String COUNT_EVENT_BY_LOGIN;
    /**
     * Deletes event by id.
     */
    private final String DELETE_EVENT_BY_EVENT_ID_SQL;
    /**
     * Delete selected events.
     */
    private final String DELETE_EVENTS_BY_EVENT_IDS_SQL;
    /**
     * Deletes event by login and 'to' date.
     */
    private final String DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL;
    /**
     * Put an event by login.
     */
    private final String PUT_USER_EVENT_BY_LOGIN_SQL;
    /**
     * Put an event by user id.
     */
    private final String PUT_USER_EVENT_SQL;

    /** The load all user properties. */
    private final String LOAD_ALL_USER_PROPERTIES;

    /** The load user property by name. */
    private final String LOAD_USER_PROPERTY_BY_NAME;

    /** The load user property by display name. */
    private final String LOAD_USER_PROPERTY_BY_DISPLAY_NAME;

    /** The insert user property. */
    private final String INSERT_USER_PROPERTY;

    /** The update user property by id. */
    private final String UPDATE_USER_PROPERTY_BY_ID;

    /** The delete user property values by user property id. */
    private final String DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID;

    /** The delete user property by id. */
    private final String DELETE_USER_PROPERTY_BY_ID;

    /** The insert user property value. */
    private final String INSERT_USER_PROPERTY_VALUE;

    /** The update user property value by id. */
    private final String UPDATE_USER_PROPERTY_VALUE_BY_ID;

    /** The delete user property values by ids. */
    private final String DELETE_USER_PROPERTY_VALUES_BY_IDS;

    /** The delete user property values by user id. */
    private final String DELETE_USER_PROPERTY_VALUES_BY_USER_ID;

    /** The load user property values by user ids. */
    private final String LOAD_USER_PROPERTY_VALUES_BY_USER_IDS;
    /**
     * Checks the user exists (simple count).
     */
    private final String CHECK_USER_EXISTS_SQL;

    /** The dao helper. */
    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private RoleDao roleDao;

    private final SecurityLabelDao securityLabelDao;

    /**
     * Instantiates a new user dao.
     *
     * @param dataSource
     *            the data source
     * @param sql
     *            the sql
     */
    @Autowired
    public UserDaoImpl(DataSource dataSource, @Qualifier("security-sql") Properties sql) {
        super(dataSource);
        LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL = sql.getProperty("LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL");
        LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL = sql
                .getProperty("LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL");
        DELETE_EVENT_BY_EVENT_ID_SQL = sql.getProperty("DELETE_EVENT_BY_EVENT_ID_SQL");
        DELETE_EVENTS_BY_EVENT_IDS_SQL = sql.getProperty("DELETE_EVENTS_BY_EVENT_IDS_SQL");
        DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL = sql.getProperty("DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL");
        PUT_USER_EVENT_BY_LOGIN_SQL = sql.getProperty("PUT_USER_EVENT_BY_LOGIN_SQL");
        PUT_USER_EVENT_SQL = sql.getProperty("PUT_USER_EVENT_SQL");
        COUNT_EVENT_BY_LOGIN = sql.getProperty("COUNT_EVENT_BY_LOGIN");
        LOAD_ALL_USER_PROPERTIES = sql.getProperty("LOAD_ALL_USER_PROPERTIES");
        LOAD_USER_PROPERTY_BY_NAME = sql.getProperty("LOAD_USER_PROPERTY_BY_NAME");
        LOAD_USER_PROPERTY_BY_DISPLAY_NAME = sql.getProperty("LOAD_USER_PROPERTY_BY_DISPLAY_NAME");
        INSERT_USER_PROPERTY = sql.getProperty("INSERT_USER_PROPERTY");
        UPDATE_USER_PROPERTY_BY_ID = sql.getProperty("UPDATE_USER_PROPERTY_BY_ID");
        DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID = sql
                .getProperty("DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID");
        DELETE_USER_PROPERTY_BY_ID = sql.getProperty("DELETE_USER_PROPERTY_BY_ID");
        INSERT_USER_PROPERTY_VALUE = sql.getProperty("INSERT_USER_PROPERTY_VALUE");
        UPDATE_USER_PROPERTY_VALUE_BY_ID = sql.getProperty("UPDATE_USER_PROPERTY_VALUE_BY_ID");
        DELETE_USER_PROPERTY_VALUES_BY_IDS = sql.getProperty("DELETE_USER_PROPERTY_VALUES_BY_IDS");
        DELETE_USER_PROPERTY_VALUES_BY_USER_ID = sql.getProperty("DELETE_USER_PROPERTY_VALUES_BY_USER_ID");
        LOAD_USER_PROPERTY_VALUES_BY_USER_IDS = sql.getProperty("LOAD_USER_PROPERTY_VALUES_BY_USER_IDS");
        CHECK_USER_EXISTS_SQL = sql.getProperty("CHECK_USER_EXISTS_SQL");

        securityLabelDao = new SecurityLabelDaoImpl(CONNECTION_TABLE, dataSource, sql);
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.unidata.mdm.backend.dao.impl.UserDao#create(com.unidata.mdm.backend.
	 * service.security.po.UserPO, java.util.List)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public UserPO create(final UserPO user, final List<SecurityLabel> userLabels) {
		Map<String, Object> paramMap = new HashMap<>();
		final String createdBy = user.getCreatedBy() != null ? user.getCreatedBy() : SecurityUtils.getCurrentUserName();
		paramMap.put(UserPO.Fields.CREATED_AT, new Timestamp(new Date().getTime()));
		paramMap.put(UserPO.Fields.CREATED_BY, createdBy);
		paramMap.put(UserPO.Fields.ACTIVE, user.isActive());
		paramMap.put(UserPO.Fields.ADMIN, user.isAdmin());
		paramMap.put(UserPO.Fields.EMAIL, user.getEmail());
		paramMap.put(UserPO.Fields.LOCALE, user.getLocale());
		paramMap.put(UserPO.Fields.FIRST_NAME, user.getFirstName());
		paramMap.put(UserPO.Fields.LAST_NAME, user.getLastName());
		paramMap.put(UserPO.Fields.LOGIN, user.getLogin());
		paramMap.put(UserPO.Fields.NOTES, user.getNotes());
		paramMap.put(UserPO.Fields.SOURCE, user.getSource());
		paramMap.put(UserPO.Fields.EXTERNAL, user.isExternal());
		int rowsAffected = namedJdbcTemplate.update(UserPO.Queries.INSERT_NEW, paramMap);

        if (rowsAffected == 0) {
            throw new DataProcessingException("No record inserted while creating user",
                    ExceptionId.EX_SECURITY_CANNOT_CREATE_USER);
        }
        final UserPO userPO = findByLogin(user.getLogin());
        user.setId(userPO.getId());
        securityLabelDao.saveLabelsForObject(userPO.getId(), userLabels);

        final List<RolePO> roles = user.getRoles();
        final List<Map<String, Object>> params = new ArrayList<>();
        for (final RolePO role : roles) {
            final Map<String, Object> toAttach = new HashMap<>();
            toAttach.put(UserPO.Fields.S_USERS_ID, userPO.getId());
            toAttach.put(RolePO.Fields.NAME, role.getName());
            params.add(toAttach);
        }
        namedJdbcTemplate.batchUpdate(UserPO.Queries.ATTACH_TO_ROLE, params.toArray(new Map[params.size()]));
        paramMap.put(PasswordPO.Fields.S_USER_ID, user.getId());
        attachToAPI(user, paramMap);
        namedJdbcTemplate.update(PasswordPO.Queries.DEACTIVATE_BY_USER_ID, paramMap);
        if (!user.isExternal()) {
            paramMap = new HashMap<>();
            paramMap.put(PasswordPO.Fields.UPDATED_AT, user.getPassword().get(0).getUpdatedAt());
            paramMap.put(PasswordPO.Fields.UPDATED_BY, user.getPassword().get(0).getUpdatedBy());
            paramMap.put(PasswordPO.Fields.CREATED_AT, user.getPassword().get(0).getCreatedAt());
            paramMap.put(PasswordPO.Fields.CREATED_BY, user.getPassword().get(0).getCreatedBy());
            paramMap.put(PasswordPO.Fields.PASSWORD_TEXT, user.getPassword().get(0).getPasswordText());
            paramMap.put(PasswordPO.Fields.ACTIVE, user.getPassword().get(0).getActive());
            paramMap.put(PasswordPO.Fields.S_USER_ID, user.getId());
            namedJdbcTemplate.update(PasswordPO.Queries.INSERT_NEW, paramMap);
        }

        return user;
    }

    private void attachToAPI(final UserPO user, Map<String, Object> paramMap) {
        if (user.getApis() != null && user.getApis().size() != 0) {
            Set<String> apiNames = user.getApis().stream().map(ApiPO::getName).collect(Collectors.toSet());
            paramMap.put("api_names", apiNames);
            namedJdbcTemplate.update(UserPO.Queries.DETACH_FROM_API, paramMap);
            namedJdbcTemplate.update(UserPO.Queries.ATTACH_TO_API, paramMap);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.impl.UserDao#isExist(java.lang.String)
     */
    @Override
    @Transactional
    public boolean isExist(final String login) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.queryForObject(CHECK_USER_EXISTS_SQL, Long.class, login) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#findByEmail(java.lang.String,
     * java.lang.String)
     */
    @Override
    public UserPO findByEmail(String email, String source) {
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(UserPO.Fields.EMAIL, email);
        paramMap.put(UserPO.Fields.SOURCE, source);
        final List<UserPO> result = namedJdbcTemplate.query(UserPO.Queries.SELECT_BY_EMAIL_SOURCE, paramMap,
                UserRowMapper.DEFAULT_USER_ROW_MAPPER);
        if (result == null || result.size() == 0) {
            return null;
        }
        final UserPO user = result.get(0);

        fillUser(user);

        return user;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.impl.UserDao#update(java.lang.String,
     * com.unidata.mdm.backend.service.security.po.UserPO, java.util.List)
     */
    @Override
    @Transactional
    public UserPO update(final String login, final UserPO user, final List<SecurityLabel> userLabels) {

        final UserPO userPO = findByLogin(login);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(UserPO.Fields.ID, userPO.getId());
        paramMap.put(UserPO.Fields.S_USER_ID, userPO.getId());

		paramMap = new HashMap<>();
		paramMap.put(UserPO.Fields.ID, userPO.getId());
		paramMap.put(UserPO.Fields.UPDATED_AT, user.getUpdatedAt());
		paramMap.put(UserPO.Fields.UPDATED_BY, user.getUpdatedBy());
		paramMap.put(UserPO.Fields.ACTIVE, user.isActive());
		paramMap.put(UserPO.Fields.ADMIN, user.isAdmin());
		paramMap.put(UserPO.Fields.EMAIL, user.getEmail());
		paramMap.put(UserPO.Fields.LOCALE, user.getLocale());
		paramMap.put(UserPO.Fields.FIRST_NAME, user.getFirstName());
		paramMap.put(UserPO.Fields.LAST_NAME, user.getLastName());
		paramMap.put(UserPO.Fields.LOGIN, user.getLogin());
		paramMap.put(UserPO.Fields.NOTES, user.getNotes());
		paramMap.put(UserPO.Fields.SOURCE, user.getSource());
		paramMap.put(UserPO.Fields.EXTERNAL, user.isExternal());

        namedJdbcTemplate.update(UserPO.Queries.UPDATE_BY_ID, paramMap);
        attachToAPI(user, paramMap);

        securityLabelDao.saveLabelsForObject(userPO.getId(), userLabels);

        final List<RolePO> roles = user.getRoles();
        final List<Map<String, Object>> params = new ArrayList<>();
        for (final RolePO role : roles) {
            final Map<String, Object> toAttach = new HashMap<>();
            toAttach.put(UserPO.Fields.S_USERS_ID, userPO.getId());
            toAttach.put(RolePO.Fields.NAME, role.getName());
            params.add(toAttach);
        }
        paramMap.put(UserPO.Fields.S_USERS_ID, userPO.getId());
        namedJdbcTemplate.update(UserPO.Queries.CLEAN_USERS, paramMap);
        namedJdbcTemplate.batchUpdate(UserPO.Queries.ATTACH_TO_ROLE, (params.toArray(new Map[params.size()])));

        if (!CollectionUtils.isEmpty(user.getPassword())) {
            paramMap.put(PasswordPO.Fields.S_USER_ID, user.getId());
            namedJdbcTemplate.update(PasswordPO.Queries.DELETE_BY_USER_ID, paramMap);
            paramMap = new HashMap<>();
            paramMap.put(PasswordPO.Fields.UPDATED_AT, user.getPassword().get(0).getUpdatedAt());
            paramMap.put(PasswordPO.Fields.UPDATED_BY, user.getPassword().get(0).getUpdatedBy());
            paramMap.put(PasswordPO.Fields.CREATED_AT, user.getPassword().get(0).getCreatedAt());
            paramMap.put(PasswordPO.Fields.CREATED_BY, SecurityUtils.getCurrentUserName());
            paramMap.put(PasswordPO.Fields.PASSWORD_TEXT, user.getPassword().get(0).getPasswordText());
            paramMap.put(PasswordPO.Fields.ACTIVE, true);
            paramMap.put(PasswordPO.Fields.S_USER_ID, user.getId());
            namedJdbcTemplate.update(PasswordPO.Queries.INSERT_NEW, paramMap);
        }

        return user;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.impl.UserDao#getAll()
     */
    @Override
    @Transactional
    public List<UserPO> getAll() {
        List<UserPO> result = namedJdbcTemplate.query(UserPO.Queries.SELECT_ALL, UserRowMapper.DEFAULT_USER_ROW_MAPPER);
        return result;
    }

    /**
     * {@inheritDoc }.
     *
     * @return the list
     */
    @Override
    public List<UserPropertyPO> loadAllProperties() {
        return namedJdbcTemplate.query(LOAD_ALL_USER_PROPERTIES, userPropertyRowMapper);
    }

    /**
     * {@inheritDoc }.
     *
     * @param name
     *            the name
     * @return the user property po
     */
    @Override
    public UserPropertyPO loadPropertyByName(String name) {
        List<UserPropertyPO> list = namedJdbcTemplate.query(LOAD_USER_PROPERTY_BY_NAME,
                Collections.singletonMap("name", name), userPropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.UserDao#loadPropertyByDisplayName(java.lang.
     * String)
     */
    @Override
    public UserPropertyPO loadPropertyByDisplayName(final String displayName) {
        final List<UserPropertyPO> list = namedJdbcTemplate.query(LOAD_USER_PROPERTY_BY_DISPLAY_NAME,
                Collections.singletonMap("display_name", displayName), userPropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc }.
     *
     * @param property
     *            the property
     */
    @Override
    public void saveProperty(UserPropertyPO property) {
        if (property.getId() == null) {
            // Insert property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("created_at", property.getCreatedAt());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("created_by", property.getCreatedBy());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());

            KeyHolder keyHolder = new GeneratedKeyHolder();

            namedJdbcTemplate.update(INSERT_USER_PROPERTY, sqlParameterSource, keyHolder, new String[] { "id" });

            property.setId(keyHolder.getKey().longValue());

        } else {
            // Update property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());
            sqlParameterSource.addValue("id", property.getId());

            namedJdbcTemplate.update(UPDATE_USER_PROPERTY_BY_ID, sqlParameterSource);
        }
    }

    /**
     * {@inheritDoc }.
     *
     * @param id
     *            the id
     */
    @Override
    public void deleteProperty(long id) {
        // Delete all values first.
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_VALUES_BY_USER_PROPERTY_ID,
                Collections.singletonMap("userPropertyId", id));

        // Delete property.
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_BY_ID, Collections.singletonMap("userPropertyId", id));
    }

    /**
     * {@inheritDoc }.
     *
     * @param propertyValues
     *            the property values
     */
    @Override
    public void saveUserPropertyValues(Collection<UserPropertyValuePO> propertyValues) {

        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        List<UserPropertyValuePO> insertValues = new ArrayList<>();
        List<UserPropertyValuePO> updateValues = new ArrayList<>();

        for (UserPropertyValuePO propertyValue : propertyValues) {

            if (propertyValue.getId() == null) {
                insertValues.add(propertyValue);
            } else {
                updateValues.add(propertyValue);
            }
        }

        if (!CollectionUtils.isEmpty(insertValues)) {
            insertUserPropertyValues(insertValues);
        }

        if (!CollectionUtils.isEmpty(updateValues)) {
            Map<String, Object>[] map = createUserPropertyValueParams(updateValues);

            namedJdbcTemplate.batchUpdate(UPDATE_USER_PROPERTY_VALUE_BY_ID, map);
        }
    }

    /**
     * Insert user property values.
     *
     * @param propertyValues
     *            the property values
     */
    private void insertUserPropertyValues(List<UserPropertyValuePO> propertyValues) {
        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        long[] ids = daoHelper.createIds(propertyValues.size(), "s_user_property_value_id_seq");

        for (int i = 0; i < propertyValues.size(); i++) {
            UserPropertyValuePO propertyValue = propertyValues.get(i);

            propertyValue.setId(ids[i]);
        }

        Map<String, Object>[] map = createUserPropertyValueParams(propertyValues);

        namedJdbcTemplate.batchUpdate(INSERT_USER_PROPERTY_VALUE, map);
    }

    /**
     * {@inheritDoc }.
     *
     * @param ids
     *            the ids
     */
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteUserPropertyValuesByIds(Collection<Long> ids) {
        long listId = daoHelper.insertLongsToTemp(ids);

        namedJdbcTemplate.update(DELETE_USER_PROPERTY_VALUES_BY_IDS, Collections.singletonMap("listId", listId));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.UserDao#deleteUserPropertyValuesByUserId(
     * long)
     */
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteUserPropertyValuesByUserId(long userId) {
        namedJdbcTemplate.update(DELETE_USER_PROPERTY_VALUES_BY_USER_ID, Collections.singletonMap("userId", userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserPropertyValuePO> loadUserPropertyValuesByUserId(Integer userId) {
        return loadUserPropertyValuesByUserIds(Collections.singleton(userId)).get(userId);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.UserDao#loadUserPropertyValuesByUserIds(java.
     * util.Collection)
     */
    @Override
    public Map<Integer, List<UserPropertyValuePO>> loadUserPropertyValuesByUserIds(Collection<Integer> userIds) {

        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        return namedJdbcTemplate.query(LOAD_USER_PROPERTY_VALUES_BY_USER_IDS,
                Collections.singletonMap("listId", userIds), rs -> {
                    Map<Integer, List<UserPropertyValuePO>> result = new HashMap<>();

                    while (rs.next()) {
                        Integer userId = rs.getInt(FieldColumns.USER_ID.name());

                        List<UserPropertyValuePO> propValues = result.get(userId);
                        if (propValues == null) {
                            propValues = new ArrayList<>();
                            result.put(userId, propValues);
                        }

                        UserPropertyValuePO propValue = UserPropertyValueRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, 0);
                        propValues.add(propValue);
                    }

                    return result;
                });
    }

    /**
     * Fill user. TODO Reduce the number of queries performed in the loop
     *
     * @param user
     *            the user
     */
    private void fillUser(final UserPO user) {
        final Map<String, Object> paramMap = new HashMap<>();

        paramMap.put(PasswordPO.Fields.S_USER_ID, user.getId());
        user.setPassword(
                namedJdbcTemplate.query(PasswordPO.Queries.SELECT_BY_USER_ID_ACTIVE_ONLY, paramMap, passwordRowMapper)
        );

        user.setLabelAttributeValues(securityLabelDao.findLabelsAttributesValuesForObject(user.getId()));
        paramMap.put(UserPO.Fields.S_USERS_ID, user.getId());
        user.setRoles(roleDao.findRolesByUserLogin(user.getLogin()));
        user.setApis(namedJdbcTemplate.query(ApiPO.Queries.SELECT_BY_USER_ID, paramMap,
                APIRowMapper.DEFAULT_API_ROW_MAPPER));
        user.setTokens(namedJdbcTemplate.query(TokenPO.Queries.SELECT_BY_USER_ID, paramMap, tokenRowMapper));
        user.setProperties(loadUserPropertyValuesByUserId(user.getId()));
    }

    /**
     * Creates the user property value params.
     *
     * @param propertyValues
     *            the property values
     * @return the map[]
     */
    private Map<String, Object>[] createUserPropertyValueParams(List<UserPropertyValuePO> propertyValues) {
        @SuppressWarnings("unchecked")
        Map<String, Object>[] result = new Map[propertyValues.size()];

        for (int i = 0; i < propertyValues.size(); i++) {
            UserPropertyValuePO propertyValue = propertyValues.get(i);

            Map<String, Object> params = new HashMap<>();

            params.put("id", propertyValue.getId());
            params.put("user_id", propertyValue.getUserId());

            if (propertyValue.getProperty() != null) {
                params.put("property_id", propertyValue.getProperty().getId());
            }

            params.put("value", propertyValue.getValue());
            params.put("created_at", propertyValue.getCreatedAt());
            params.put("updated_at", propertyValue.getUpdatedAt());
            params.put("created_by", propertyValue.getCreatedBy());
            params.put("updated_by", propertyValue.getUpdatedBy());

            result[i] = params;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.impl.UserDao#saveProperties(java.util.List,
     * java.lang.String)
     */
    @Override
    @Transactional
    public void saveProperties(final List<UserPropertyDTO> properties, final String login) {
        jdbcTemplate.update("DELETE from s_user_property_value where user_id = (select id from s_user where login=?)",
                login);
        if (!CollectionUtils.isEmpty(properties)) {
            properties.stream().filter(userProperty -> userProperty.getValue() != null).forEach(userProperty -> {
                // TODO: warn! replace it with batch insert instead of loop.
                jdbcTemplate.update(
                        "insert into s_user_property_value(user_id, value, property_id, created_at, created_by) "
                                + "values((select id from s_user where login=?), ?, (select id from s_user_property where name=?),current_timestamp, ?)",
                        login, userProperty.getValue(), userProperty.getName(), SecurityUtils.getCurrentUserName());
            });
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.impl.UserDao#isLastAdmin()
     */
    @Override
    @Transactional
    public boolean isLastAdmin() {
        return jdbcTemplate.queryForObject("select count(id) from s_user where active=true and admin=true",
                Long.class) == 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.impl.UserDao#saveToken(com.unidata.mdm.
     * backend.service.security.po.TokenPO)
     */
    @Override
    @Transactional
    public void saveToken(TokenPO tokenPO) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TokenPO.Fields.UPDATED_AT, tokenPO.getUpdatedAt());
        paramMap.put(TokenPO.Fields.UPDATED_BY, tokenPO.getUpdatedBy());
        paramMap.put(TokenPO.Fields.CREATED_AT, tokenPO.getCreatedAt());
        paramMap.put(TokenPO.Fields.CREATED_BY, tokenPO.getCreatedBy());
        paramMap.put(TokenPO.Fields.TOKEN, tokenPO.getToken());
        paramMap.put(TokenPO.Fields.S_USER_ID, tokenPO.getUser().getId());
        namedJdbcTemplate.update(TokenPO.Queries.INSERT_NEW, paramMap);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#loadUserEvents(java.lang.String,
     * java.util.Date, int, int)
     */
    @Override
    public List<UserEventPO> loadUserEvents(String login, Date from, int page, int count) {

        MeasurementPoint.start();
        try {
            boolean returnAll = page <= 0 || count <= 0;
            Timestamp point = from == null ? null : new Timestamp(from.getTime());
            if (returnAll) {
                return jdbcTemplate.query(LOAD_USER_EVENTS_BY_LOGIN_AND_DATE_SQL, UserEventRowMapper.DEFAULT_ROW_MAPPER,
                        login, point);
            } else {
                return jdbcTemplate.query(LOAD_USER_EVENTS_BY_LOGIN_LIMIT_OFFSET_AND_DATE_SQL,
                        UserEventRowMapper.DEFAULT_ROW_MAPPER, login, point, (page - 1) * count, count);
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.UserDao#countUserEvents(java.lang.String)
     */
    @Override
    public Long countUserEvents(String login) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.queryForObject(COUNT_EVENT_BY_LOGIN, Long.class, login);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.UserDao#deleteUserEvent(java.lang.String)
     */
    @Override
    public boolean deleteUserEvent(String eventId) {

        MeasurementPoint.start();
        try {
            return jdbcTemplate.update(DELETE_EVENT_BY_EVENT_ID_SQL, eventId) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#deleteUserEvents(List)
     */
    @Override
    public boolean deleteUserEvents(List<String> eventIds) {

        MeasurementPoint.start();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put(UserEventPO.FIELD_ID, eventIds);
            return namedJdbcTemplate.update(DELETE_EVENTS_BY_EVENT_IDS_SQL, params) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.UserDao#deleteAllUserEvents(java.lang.String,
     * java.util.Date)
     */
    @Override
    public boolean deleteAllUserEvents(String login, Date to) {

        MeasurementPoint.start();
        try {
            Timestamp point = to == null ? null : new Timestamp(to.getTime());
            return jdbcTemplate.update(DELETE_EVENTS_BY_LOGIN_AND_DATE_SQL, login, point) > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#create(com.unidata.mdm.backend.
     * service.security.po.UserEventPO)
     */
    @Override
    public UserEventPO create(UserEventPO event) {
        MeasurementPoint.start();
        try {

            String newId = IdUtils.v1String();

            int result = jdbcTemplate.update(PUT_USER_EVENT_SQL, newId, event.getUserId(), event.getType(),
                    event.getContent(), event.getCreatedBy());

            if (result > 0) {
                event.setId(newId);
                return event;
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#create(com.unidata.mdm.backend.
     * service.security.po.UserEventPO, java.lang.String, java.lang.String)
     */
    @Override
    public UserEventPO create(UserEventPO event, String login) {

        MeasurementPoint.start();
        try {

            String newId = IdUtils.v1String();

            int result = jdbcTemplate.update(PUT_USER_EVENT_BY_LOGIN_SQL, newId, login, event.getType(),
                    event.getContent(), event.getCreatedBy());

            if (result > 0) {
                event.setId(newId);
                return event;
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#deleteToken(java.lang.String)
     */
    @Override
    public void deleteToken(String tokenString) {
        jdbcTemplate.update("delete from s_token cascade where token=?", tokenString);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.UserDao#findByLogin(java.lang.String)
     */
    @Override
    public UserPO findByLogin(String login) {
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(UserPO.Fields.LOGIN, login);
        final List<UserPO> result = namedJdbcTemplate.query(UserPO.Queries.SELECT_BY_LOGIN, paramMap,
                UserRowMapper.DEFAULT_USER_ROW_MAPPER);

        if (result == null || result.size() == 0) {
            return null;
        }
        final UserPO user = result.get(0);

        fillUser(user);

        return user;
    }

    @Override
    public List<ApiPO> getAPIList() {
        final List<ApiPO> result = namedJdbcTemplate.query(ApiPO.Queries.SELECT_ALL,
                APIRowMapper.DEFAULT_API_ROW_MAPPER);
        return result;
    }
}
