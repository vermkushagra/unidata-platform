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

    void insert(final MessagePO msg);

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