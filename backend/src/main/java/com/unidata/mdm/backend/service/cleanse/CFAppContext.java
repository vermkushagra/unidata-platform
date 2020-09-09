/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.cleanse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

/**
 * Spring application context holder. 
 * Should be used inside any cleanse
 * function where access to the spring application context is needed.
 * @author ilya.bykov
 */
@Service("CF_APP_CTX")
public class CFAppContext implements BeanFactoryAware {

    /** The context. */
    public static BeanFactory CONTEXT;

    /**
     * Instantiates a new CF app context.
     */
    public CFAppContext() {
    }

    /**
     * Gets the bean by name.
     *
     * @param s the s
     * @return the bean
     * @throws BeansException the beans exception
     */
    public static Object getBean(String s) throws BeansException {
        return CONTEXT.getBean(s);
    }

    /**
     * Gets the bean.
     *
     * @param <T> the generic type
     * @param s the s
     * @param tClass the t class
     * @return the bean
     * @throws BeansException the beans exception
     */
    public static <T> T getBean(String s, Class<T> tClass) throws BeansException {
        return CONTEXT.getBean(s, tClass);
    }

    /**
     * Gets the bean.
     *
     * @param <T> the generic type
     * @param tClass the t class
     * @return the bean
     * @throws BeansException the beans exception
     */
    public static <T> T getBean(Class<T> tClass) throws BeansException {
        return CONTEXT.getBean(tClass);
    }

    /**
     * Gets the bean.
     *
     * @param s the s
     * @param objects the objects
     * @return the bean
     * @throws BeansException the beans exception
     */
    public static Object getBean(String s, Object... objects) throws BeansException {
        return CONTEXT.getBean(s, objects);
    }

    /**
     * Contains bean.
     *
     * @param s the s
     * @return true, if successful
     */
    public static boolean containsBean(String s) {
        return CONTEXT.containsBean(s);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }
}