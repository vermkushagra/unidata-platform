package org.unidata.mdm.system.service;

/**
 * @author Mikhail Mikhailov on Nov 8, 2019
 * Marks code, that has to be run after platform startup.
 */
public interface AfterPlatformStartup {
    /**
     * Runs platform startup hooks.
     * No guarantees regarding the order of execution can be made.
     */
    void afterPlatformStartup();
}
