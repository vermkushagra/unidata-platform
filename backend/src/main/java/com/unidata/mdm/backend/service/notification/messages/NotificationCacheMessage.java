/**
 *
 */

package com.unidata.mdm.backend.service.notification.messages;

import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import java.io.Serializable;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class NotificationCacheMessage implements Serializable {
    private final static long serialVersionUID = 12345L;
    private NotificationConfig config;
    private String messageBody;

    public NotificationCacheMessage() {
        // No-op.
    }

    public NotificationCacheMessage(NotificationConfig config, String messageBody) {
        this.config = config;
        this.messageBody = messageBody;
    }

    public NotificationConfig getConfig() {
        return config;
    }

    public void setConfig(NotificationConfig config) {
        this.config = config;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
