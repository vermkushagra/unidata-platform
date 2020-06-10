package com.unidata.mdm.backend.common.context;

/**
 * Common sendable context for modifying operations.
 * @author Mikhail Mikhailov
 *
 */
public abstract class CommonSendableContext
    extends CommonRequestContext
    implements NotificationSendableContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7972675023238757045L;
    /**
     * Sendable destination support
     */
    private String notificationDestination;
    /**
     * Sendable destination support
     */
    private String notificationId;
    /**
     * Constructor.
     */
    protected CommonSendableContext() {
        super();
        flags.set(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendNotification() {
        return flags.get(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotificationDestination() {
        return notificationDestination;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Mark context as not sendable
     */
    public void skipNotification() {
        flags.clear(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
    }

    /**
     * Marks context as sendble and sets destination and notification id.
     * @param notificationDestination
     * @param notificationId
     */
    public void sendNotification(String notificationDestination, String notificationId) {
        flags.set(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
        this.notificationDestination = notificationDestination;
        this.notificationId = notificationId;
    }

    /**
     * Method copies behavior of argument context.
     * @param notificationSendableContext
     */
    public void repeatNotificationBehavior(NotificationSendableContext notificationSendableContext){
        flags.set(ContextUtils.CTX_FLAG_SEND_NOTIFICATION, notificationSendableContext.sendNotification());
        this.notificationDestination = notificationSendableContext.getNotificationDestination();
        this.notificationId = notificationSendableContext.getNotificationId();
    }
}
