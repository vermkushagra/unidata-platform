package org.unidata.mdm.system.service;

/**
 * @author Mikhail Mikhailov
 * Marks code, that has to be run after application context refresh event.
 */
@FunctionalInterface
public interface AfterContextRefresh {
    /**
     * Run some code after context refresh.
     */
    void afterContextRefresh();
}
