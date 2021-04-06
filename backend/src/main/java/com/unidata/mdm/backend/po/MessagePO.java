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
