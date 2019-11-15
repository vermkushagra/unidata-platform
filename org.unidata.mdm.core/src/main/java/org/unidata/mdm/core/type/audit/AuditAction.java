package org.unidata.mdm.core.type.audit;

/**
 * Audit action
 */
public interface AuditAction {
    /**
     * @param event - event for enriching
     * @param input -input
     */
    void enrichEvent(Event event, Object... input);

    /**
     * @return subsystem
     */
    SubSystem getSubsystem();

    /**
     * @param input- input
     * @return true if input valid otherwise false
     */
    boolean isValidInput(Object... input);

    /**
     * @return name
     */
    String name();

}
