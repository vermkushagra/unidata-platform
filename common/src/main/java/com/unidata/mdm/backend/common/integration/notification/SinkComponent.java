/**
 * Date: 09.08.2016
 */

package com.unidata.mdm.backend.common.integration.notification;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public interface SinkComponent {
    /**
     * Initialize component after all properties set.
     */
    void init();

    /**
     * Destroy method called before component removed.
     */
    void destroy();
}
