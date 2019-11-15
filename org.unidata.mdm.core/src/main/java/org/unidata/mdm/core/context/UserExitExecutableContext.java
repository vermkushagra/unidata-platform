package org.unidata.mdm.core.context;

/**
 * TODO: Remove this interface, after (short) migration period.
 * @author Dmitry Kopin
 *         Turn on/off user exit for operation
 */
@Deprecated
public interface UserExitExecutableContext {

    boolean isBypassExtensionPoints();
}
