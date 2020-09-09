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

package com.unidata.mdm.backend.dao;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.po.MessagePO;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public interface MessageDao {

    void insert(List<MessagePO> msgs);

    MessagePO findUndeliveredMessage(long msgId);

    List<MessagePO> findUndeliveredMessages(MessagePO.MessageType msgType, int maxFailedSend);

    Pair<Long, Long> findNextUndeliveredBlock(Long start, MessagePO.MessageType msgType, int maxFailedSend, int blockSize);

    List<Long> loadNextUndeliveredBlock(Long start, MessagePO.MessageType msgType, int maxFailedSend, int blockSize);

    void deliverMessage(long msgId);

    void incrementFailedSendMessage(long msgId);

    /**
     * Remove old expired messages by type and maxLifetime
     * @param messageType message type
     * @param maxLifetime max lifetime for messages
     * @return count of removed messages
     */
    long cleanOldMessages(MessagePO.MessageType messageType, long maxLifetime);
}