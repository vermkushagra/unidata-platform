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

package com.unidata.mdm.backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import javax.sql.DataSource;

import com.unidata.mdm.backend.common.context.CommonDependableContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class DaoHelper extends JdbcDaoSupport {
    private static final String[] SQL_PATTERN_SYMBOLS = {"%", "_"};

    private final AtomicLong nextId = new AtomicLong(1);

    private String commonSequenceName;
    private String createIdsQuery;
    private String createIdQuery;

    private String createTmpIdTableQuery;
    private String insertTmpIdQuery;

    private String insertStringsToTempQuery;

    private static ThreadLocal<Integer> txActionCount = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

//    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void postgresBatchUpdate(String sql, final Collection<Long> ids) {
        final Iterator<Long> iter = ids.iterator();

        postgresBatchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                assert iter.hasNext();

                ps.setLong(1, iter.next());
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });
    }

//    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public void postgresBatchUpdate(String sql, BatchPreparedStatementSetter setter) {
        getJdbcTemplate().batchUpdate(sql, setter);
    }

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

//    @Transactional(propagation = Propagation.MANDATORY)
    public long insertStringsToTemp(final Collection<String> ids) {
        final long listId = listId();

        getJdbcTemplate().update(createTmpIdTableQuery);

        final Iterator<String> iter = ids.iterator();

        postgresBatchUpdate(insertStringsToTempQuery, new BatchPreparedStatementSetter() {
            @Override
            public int getBatchSize() {
                return ids.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                assert iter.hasNext();

                ps.setLong(1, listId);
                ps.setString(2, iter.next());
            }
        });

        return listId;
    }

    /**
     *
     * @param r
     */
    public static void executeAfterCommitAction(final Runnable r) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Order is necessary to execute tx syncs in their add order.
            final int order = txActionCount.get();
            txActionCount.set(order < Ordered.LOWEST_PRECEDENCE ? order + 1 : Ordered.LOWEST_PRECEDENCE);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override public void afterCommit() {
                    r.run();
                }

                @Override public int getOrder() {
                    return order;
                }
            });
        } else {
            r.run();
        }
    }

    public static void executeAfterCommitAction(final List<Function<CommonDependableContext, Boolean>> finalizers, CommonDependableContext ctx) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Order is necessary to execute tx syncs in their add order.
            final int order = txActionCount.get();
            txActionCount.set(order < Ordered.LOWEST_PRECEDENCE ? order + 1 : Ordered.LOWEST_PRECEDENCE);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override public void afterCommit() {
                    finalizers.forEach(finalizer -> finalizer.apply(ctx));
                }

                @Override public int getOrder() {
                    return order;
                }
            });
        } else {
            finalizers.forEach(finalizer -> finalizer.apply(ctx));
        }
    }

    /**
     *
     * @param r
     */
    public static void executeAfterRollbackAction(final Runnable r) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Order is necessary to execute tx syncs in their add order.
            final int order = txActionCount.get();
            txActionCount.set(order < Ordered.LOWEST_PRECEDENCE ? order + 1 : Ordered.LOWEST_PRECEDENCE);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        r.run();
                    }
                }

                @Override public int getOrder() {
                    return order;
                }
            });
        } else {
            r.run();
        }
    }

//    @Transactional(propagation = Propagation.SUPPORTS)
    public long[] createIds(int count, String sequence) {
        if (count == 0) {
            return new long[0];
        }

        final long[] ids = new long[count];

        getJdbcTemplate().query(createIdsQuery,
                new ResultSetExtractor<Object>() {
                    @Override
                    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                        for (int i = 0; rs.next(); i++) {
                            ids[i] = rs.getLong(1);
                        }

                        return null;
                    }
                }, sequence, count);

        return ids;
    }

//    @Transactional(propagation = Propagation.MANDATORY)
    public long createId(String sequence) {
        return getJdbcTemplate().queryForObject(createIdQuery, Long.class, sequence);
    }

    public static String escapeSqlPattern(String param, String escapeSymbol) {
        String result = param.replaceAll(escapeSymbol, escapeSymbol + escapeSymbol);

        for (String specialSymbol : SQL_PATTERN_SYMBOLS) {
            result = result.replaceAll(specialSymbol, escapeSymbol + specialSymbol);
        }

        return result;
    }

    /**
     *
     * @return
     */
//    @Transactional(propagation = Propagation.SUPPORTS)
    public long[] createIds(int count) {
        return createIds(count, commonSequenceName);
    }

    /**
     *
     * @return
     */
//    @Transactional(propagation = Propagation.SUPPORTS)
    public long createId() {
        return createId(commonSequenceName);
    }

    @Required
    public void setInsertStringsToTempQuery(String insertStringsToTempQuery) {
        this.insertStringsToTempQuery = insertStringsToTempQuery;
    }

    @Required
    public void setCreateTmpIdTableQuery(String createTmpIdTableQuery) {
        this.createTmpIdTableQuery = createTmpIdTableQuery;
    }

    @Required
    public void setInsertTmpIdQuery(String insertTmpIdQuery) {
        this.insertTmpIdQuery = insertTmpIdQuery;
    }

    @Required
    public void setCommonSequenceName(String commonSequenceName) {
        this.commonSequenceName = commonSequenceName;
    }

    public void setCreateIdsQuery(String createIdsQuery) {
        this.createIdsQuery = createIdsQuery;
    }

    public void setCreateIdQuery(String createIdQuery) {
        this.createIdQuery = createIdQuery;
    }

    @Autowired
    public void setUnidataDataSource(DataSource dataSource){
        setDataSource(dataSource);
    }
}
