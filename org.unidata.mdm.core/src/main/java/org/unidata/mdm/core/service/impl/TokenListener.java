package org.unidata.mdm.core.service.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import org.unidata.mdm.core.dao.UserDao;
import org.unidata.mdm.core.notification.NotificationSystemConstants;
import org.unidata.mdm.core.notification.SecurityEventTypeConstants;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.util.Maps;

import java.util.function.BiConsumer;
// import com.unidata.mdm.backend.service.audit.AuditLocalizationConstants;

/**
 * The listener interface for receiving token events. The class that is
 * interested in processing a token event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addTokenListener<code> method. When the token event occurs,
 * that object's appropriate method is invoked.
 *
 * @author ilya.bykov
 */
public class TokenListener implements
    EntryRemovedListener<String, SecurityToken>,
    EntryExpiredListener<String, SecurityToken>,
    EntryEvictedListener<String, SecurityToken> {

    private final BiConsumer<String, Object> coreSender;

    /** The user dao. */
    private final UserDao userDao;

    /**
     * Instantiates a new token listener.
     *
     * @param coreSender the core notification sender
     * @param userDao           the user dao
     */
    public TokenListener(final BiConsumer<String, Object> coreSender, final UserDao userDao) {
        this.coreSender = coreSender;
        this.userDao = userDao;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hazelcast.map.listener.EntryRemovedListener#entryRemoved(com.
     * hazelcast.core.EntryEvent)
     */
    @Override
    public void entryRemoved(EntryEvent<String, SecurityToken> event) {
        userDao.deleteToken(event.getKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void entryEvicted(EntryEvent<String, SecurityToken> event) {
        userDao.deleteToken(event.getKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.hazelcast.map.listener.EntryEvictedListener#entryEvicted(com.
     * hazelcast.core.EntryEvent)
     */
    @Override
    public void entryExpired(EntryEvent<String, SecurityToken> event) {
        // No need to delete token. It is already done above in entryEvicted()
        final String userName = event.getOldValue().getUser().getLogin();
        coreSender.accept(
                SecurityEventTypeConstants.LOGOUT_TYPE,
                Maps.of(NotificationSystemConstants.LOGIN, userName, "reason", ""/*MessageUtils.getMessage(AuditLocalizationConstants.LOGOUT_BY_TIMEOUT)*/)
        );
    }
}
