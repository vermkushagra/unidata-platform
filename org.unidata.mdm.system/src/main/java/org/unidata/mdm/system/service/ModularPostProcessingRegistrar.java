package org.unidata.mdm.system.service;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Mikhail Mikhailov on Dec 27, 2019
 */
public interface ModularPostProcessingRegistrar {
    /**
     * Registers a {@link BeanPostProcessor} to be used in child (modules) contexts.
     * @param beanPostProcessor the processor
     */
    void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    /**
     * Registers a {@link BeanFactoryPostProcessor} to be used in child (modules) contexts.
     * @param beanFactoryPostProcessor the processor
     */
    void registerBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor);
}
