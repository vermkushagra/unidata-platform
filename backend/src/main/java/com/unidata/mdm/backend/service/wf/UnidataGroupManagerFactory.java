/**
 *
 */
package com.unidata.mdm.backend.service.wf;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Mikhail Mikhailov
 * Custom group management factory.
 */
public class UnidataGroupManagerFactory implements SessionFactory {

    /**
     * Group identity manager.
     */
    @Autowired
    private UnidataGroupIdentityManger unidataGroupIdentityManger;

    /**
     * Constructor.
     */
    public UnidataGroupManagerFactory() {
        super();
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.interceptor.SessionFactory#getSessionType()
     */
    @Override
    public Class<?> getSessionType() {
        return GroupIdentityManager.class;
    }

    /* (non-Javadoc)
     * @see org.activiti.engine.impl.interceptor.SessionFactory#openSession()
     */
    @Override
    public Session openSession() {
        return unidataGroupIdentityManger;
    }

}
