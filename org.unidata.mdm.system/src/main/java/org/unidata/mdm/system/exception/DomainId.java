package org.unidata.mdm.system.exception;

/**
 * @author Mikhail Mikhailov on Sep 26, 2019
 * Exception domain indicator.
 */
@FunctionalInterface
public interface DomainId {
    /**
     * Domain name (generic failure, job, validation, meta, etc.)
     * @return domain name
     */
    String name();
}
