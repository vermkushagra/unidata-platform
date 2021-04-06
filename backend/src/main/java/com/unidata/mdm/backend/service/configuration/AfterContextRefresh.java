package com.unidata.mdm.backend.service.configuration;


/**
 * @author Mikhail Mikhailov
 * Denotes code, what has to be run after application context refresh.
 */
@FunctionalInterface
public interface AfterContextRefresh {

    /**
     * Run some code after context refresh.
     */
    void afterContextRefresh();
}
