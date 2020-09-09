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
 *
 */

package com.unidata.mdm.backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.dao.MessageDao;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplateImpl;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplateImpl;
import com.unidata.mdm.backend.po.MessagePO;
import com.unidata.mdm.backend.po.MessagePO.MessageType;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@Repository
public class MessageDaoImpl implements MessageDao {
    private static final String MESSAGE_SEQ = "message_id_seq";
    private UnidataJdbcTemplate jdbcTemplate;
    private UnidataNamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    private DaoHelper daoHelper;

    private final String FIND_UNDELIVERED_MESSAGE_BY_ID;
    private final String FIND_UNDELIVERED_MESSAGES_BY_TYPEID;
    private final String INSERT_MESSAGE;
    private final String DELIVER_MESSAGE;
    private final String FAILED_SEND_MESSAGE;
    private final String DELETE_EXPIRED_MESSAGE;
    private final String getNextUndeliveredBlockSQL;
    private final String loadNextUndeliveredBlockSQL;

    @Autowired
    public MessageDaoImpl(final DataSource dataSource, @Qualifier("message-sql") final Properties sql) {
        FIND_UNDELIVERED_MESSAGE_BY_ID = sql.getProperty("FIND_UNDELIVERED_MESSAGE_BY_ID");
        FIND_UNDELIVERED_MESSAGES_BY_TYPEID = sql.getProperty("FIND_UNDELIVERED_MESSAGES_BY_TYPEID");
        INSERT_MESSAGE = sql.getProperty("INSERT_MESSAGE");
        DELIVER_MESSAGE = sql.getProperty("DELIVER_MESSAGE");
        FAILED_SEND_MESSAGE = sql.getProperty("FAILED_SEND_MESSAGE");
        DELETE_EXPIRED_MESSAGE = sql.getProperty("DELETE_EXPIRED_MESSAGE");
        getNextUndeliveredBlockSQL = sql.getProperty("getNextUndeliveredBlockSQL");
        loadNextUndeliveredBlockSQL = sql.getProperty("loadNextUndeliveredBlockSQL");

        jdbcTemplate = new UnidataJdbcTemplateImpl(dataSource);
        namedJdbcTemplate = new UnidataNamedParameterJdbcTemplateImpl(dataSource);
    }

    @Override
    public void insert(final List<MessagePO> msgs) {
        msgs.forEach(msg -> msg.setId(daoHelper.createId(MESSAGE_SEQ)));

        jdbcTemplate.batchUpdate(
                INSERT_MESSAGE,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        final MessagePO msg = msgs.get(0);
                        ps.setLong(1, msg.getId());
                        ps.setString(2, msg.getMessage());
                        ps.setBoolean(3, msg.isDelivered());
                        ps.setInt(4, msg.getType().getId());
                        ps.setInt(5, msg.getFailedSend());
                        if (msg.getSendDate() != null) {
                            ps.setDate(6, new java.sql.Date(msg.getSendDate().getTime()));
                        }
                        else {
                            ps.setNull(6, Types.DATE);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return msgs.size();
                    }
                }
        );
    }

    @Override
    public List<MessagePO> findUndeliveredMessages(MessageType msgType, int maxFailedSend) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("maxFailedSend", maxFailedSend);
        params.put("typeId", msgType.getId());

        return namedJdbcTemplate.query(FIND_UNDELIVERED_MESSAGES_BY_TYPEID, params,
            new MessageRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, Long> findNextUndeliveredBlock(Long start, MessageType msgType, int maxFailedSend,
            int blockSize) {

        Map<String, Object> params = new HashMap<>(4);
        params.put("start", start);
        params.put("maxFailedSend", maxFailedSend);
        params.put("typeId", msgType.getId());
        params.put("blockSize", blockSize);

        return namedJdbcTemplate.query(getNextUndeliveredBlockSQL, rs ->
            rs != null && rs.next() ? new ImmutablePair<>(rs.getLong("block_start"), rs.getLong("block_end")) : null
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> loadNextUndeliveredBlock(Long start, MessageType msgType, int maxFailedSend, int blockSize) {
        return jdbcTemplate.queryForList(loadNextUndeliveredBlockSQL, Long.class, start, maxFailedSend, msgType.getId(), blockSize);
    }

    @Override
    public MessagePO findUndeliveredMessage(long msgId) {
        List<MessagePO> messages = jdbcTemplate.query(FIND_UNDELIVERED_MESSAGE_BY_ID, new MessageRowMapper(), msgId);

        return CollectionUtils.isEmpty(messages) ? null : messages.get(0);
    }

    @Override
    public void deliverMessage(long msgId) {
        jdbcTemplate.update(DELIVER_MESSAGE, msgId);

    }

    @Override
    public void incrementFailedSendMessage(long msgId) {
        jdbcTemplate.update(FAILED_SEND_MESSAGE, new Date(), msgId);
    }

    @Override
    public long cleanOldMessages(MessagePO.MessageType messageType, long maxLifetime){
        return jdbcTemplate.update(DELETE_EXPIRED_MESSAGE, messageType.getId(),
                new Timestamp(Instant.now().minus(maxLifetime, ChronoUnit.MINUTES).toEpochMilli()));
    }

    /**
     *
     */
    private enum Columns {
        MESSAGE_ID,
        MESSAGE,
        DELIVERED,
        TYPE_ID,
        FAILED_SEND,
        CREATE_DATE,
        SEND_DATE,
        LAST_ATTEMPT_DATE
    }

    private static class MessageRowMapper implements RowMapper<MessagePO> {
        @Override
        public MessagePO mapRow(ResultSet rs, int rowNum) throws SQLException {
            MessagePO message = new MessagePO();

            message.setId(rs.getLong(Columns.MESSAGE_ID.name()));
            message.setMessage(rs.getString(Columns.MESSAGE.name()));
            message.setDelivered(rs.getBoolean(Columns.DELIVERED.name()));
            message.setType(MessageType.getMessageTypeById(rs.getInt(Columns.TYPE_ID.name())));
            message.setFailedSend(rs.getInt(Columns.FAILED_SEND.name()));
            message.setCreateDate(rs.getTimestamp(Columns.CREATE_DATE.name()));
            message.setSendDate(rs.getTimestamp(Columns.SEND_DATE.name()));
            message.setLastAttemptDate(rs.getTimestamp(Columns.LAST_ATTEMPT_DATE.name()));

            return message;
        }
    }
}
