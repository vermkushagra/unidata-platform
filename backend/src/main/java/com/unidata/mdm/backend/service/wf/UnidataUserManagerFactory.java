/**
 *
 */
package com.unidata.mdm.backend.service.wf;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Mikhail Mikhailov
 * Unidata user manager factory.
 */
public class UnidataUserManagerFactory implements SessionFactory {

    /**
     * User manager.
     */
    @Autowired
    private UnidataUserIdentityManager unidataUserIdentityManager;
    /**
     * Constructor.
     */
    public UnidataUserManagerFactory() {
        super();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.interceptor.SessionFactory#getSessionType()
     */
    @Override
    public Class<?> getSessionType() {
        return UserIdentityManager.class;
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.interceptor.SessionFactory#openSession()
     */
    @Override
    public Session openSession() {
        return unidataUserIdentityManager;
    }

}
