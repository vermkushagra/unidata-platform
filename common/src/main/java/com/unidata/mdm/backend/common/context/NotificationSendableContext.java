/**
 *
 */
package com.unidata.mdm.backend.common.context;


/**
 * @author Mikhail Mikhailov
 * Is capable for notifications.
 */
public interface NotificationSendableContext {

    /**
     * Whether notification should be send or not.
     * @return true, if the context should be sent, false otherwise
     */
    boolean sendNotification();
    /**
     * Gets the destination. This may be JMS replyTo destination queue JNDI name.
     * @return destination name
     */
    String getNotificationDestination();
    /**
     * Gets the replay ID. This may be JMS correlationId.
     * @return id
     */
    String getNotificationId();
}
