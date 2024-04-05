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

package com.unidata.mdm.backend.po;

import java.util.Date;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class MessagePO {
    private Long id;
    private String message;
    private MessageType type;
    private boolean delivered;
    private int failedSend;
    private Date createDate;
    private Date sendDate;
    private Date lastAttemptDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public int getFailedSend() {
        return failedSend;
    }

    public void setFailedSend(int failedSend) {
        this.failedSend = failedSend;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getLastAttemptDate() {
        return lastAttemptDate;
    }

    public void setLastAttemptDate(Date lastAttemptDate) {
        this.lastAttemptDate = lastAttemptDate;
    }

    public enum MessageType {
        NOTIFICATION(1),
        AUDIT(2),
        ;

        private int id;

        MessageType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static MessageType getMessageTypeById(int id) {
           for (MessageType type : MessageType.values()) {
               if (type.id == id) {
                   return type;
               }
           }

           return null;
       }
    }
}