/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Date: 22.03.2016
 */

package org.unidata.mdm.core.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * FIXME: Kill this class brutally!
 *
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class CustomJobDaoSupport extends JdbcDaoSupport {
//    private static final String[] SQL_PATTERN_SYMBOLS = {"%", "_"};

    private final AtomicLong nextId = new AtomicLong(1);

//    private String commonSequenceName;
//    private String createIdsQuery;
    private String createIdQuery;

    private String createTmpIdTableQuery = "create temporary table if not exists t_tmp_id "
            + "( list_id bigint, id bigint, some_text text, some_number bigint ) "
            + "on commit drop";

    private String insertTmpIdQuery = "insert into t_tmp_id (list_id, id) values (?, ?)";

//    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
//    public void postgresBatchUpdate(String sql, BatchPreparedStatementSetter setter) {
//        getJdbcTemplate().batchUpdate(sql, setter);
//    }

    public long listId() {
        return nextId.getAndIncrement();
    }

    // TODO Remove this crap ASAP!
//    @Transactional(propagation = Propagation.MANDATORY)
    public long insertLongsToTemp(final Collection<Long> ids) {
        final long listId = listId();

        getJdbcTemplate().update(createTmpIdTableQuery);

        final Iterator<Long> iter = ids.iterator();

        getJdbcTemplate().batchUpdate(insertTmpIdQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, listId);
                ps.setLong(2, iter.next());
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });

        return listId;
    }



//    @Transactional(propagation = Propagation.SUPPORTS)
//    public long[] createIds(int count, String sequence) {
//        if (count == 0) {
//            return new long[0];
//        }
//
//        final long[] ids = new long[count];
//
//        getJdbcTemplate().query(createIdsQuery,
//                new ResultSetExtractor<Object>() {
//                    @Override
//                    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
//                        for (int i = 0; rs.next(); i++) {
//                            ids[i] = rs.getLong(1);
//                        }
//
//                        return null;
//                    }
//                }, sequence, count);
//
//        return ids;
//    }

//    @Transactional(propagation = Propagation.MANDATORY)
    public long createId(String sequence) {
        return getJdbcTemplate().queryForObject(createIdQuery, Long.class, sequence);
    }

    /**
     *
     * @return
     */
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public long[] createIds(int count) {
//        return createIds(count, commonSequenceName);
//    }

    /**
     *
     * @return
     */
//    @Transactional(propagation = Propagation.SUPPORTS)
//    public long createId() {
//        return createId(commonSequenceName);
//    }

//    @Required
//    public void setCreateTmpIdTableQuery(String createTmpIdTableQuery) {
//        this.createTmpIdTableQuery = createTmpIdTableQuery;
//    }
//
//    @Required
//    public void setInsertTmpIdQuery(String insertTmpIdQuery) {
//        this.insertTmpIdQuery = insertTmpIdQuery;
//    }

//    @Required
//    public void setCommonSequenceName(String commonSequenceName) {
//        this.commonSequenceName = commonSequenceName;
//    }

//    public void setCreateIdsQuery(String createIdsQuery) {
//        this.createIdsQuery = createIdsQuery;
//    }
//
//    public void setCreateIdQuery(String createIdQuery) {
//        this.createIdQuery = createIdQuery;
//    }
//
//    @Autowired
//    @Qualifier("coreDataSource")
//    public void setCoreDataSource(DataSource dataSource){
//        setDataSource(dataSource);
//    }
}
