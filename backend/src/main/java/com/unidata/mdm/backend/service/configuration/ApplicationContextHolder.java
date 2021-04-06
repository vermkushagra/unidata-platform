package com.unidata.mdm.backend.service.configuration;

import org.springframework.context.ApplicationContext;

/**
 * @author Mikhail Mikhailov
 * Just a simple wrapper, to hold {@link ApplicationContext} reference.
 */
public final class ApplicationContextHolder {
    /**
     * The context reference.
     */
    private ApplicationContext applicationContext;
    /**
     * Constructor.
     */
    public ApplicationContextHolder() {
        super();
    }
    /**
     * Gets the context.
     * @return the context
     */
    public ApplicationContext get() {
        return this.applicationContext;
    }
    /**
     * Sets the application context.
     * @param applicationContext the context
     */
    public void set(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
