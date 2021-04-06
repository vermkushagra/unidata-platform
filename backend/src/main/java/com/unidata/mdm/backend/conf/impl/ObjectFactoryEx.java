package com.unidata.mdm.backend.conf.impl;

import com.unidata.mdm.conf.Delete;
import com.unidata.mdm.conf.Join;
import com.unidata.mdm.conf.Merge;
import com.unidata.mdm.conf.ObjectFactory;
import com.unidata.mdm.conf.Search;
import com.unidata.mdm.conf.Split;
import com.unidata.mdm.conf.Upsert;
import com.unidata.mdm.conf.WorkflowProcessDefinition;


/**
 * @author Mikhail Mikhailov
 * Object factory for configuration objects.
 */
public class ObjectFactoryEx extends ObjectFactory {

    /**
     * Constructor.
     */
    public ObjectFactoryEx() {
        super();
    }

    /**
     * @see com.unidata.mdm.conf.ObjectFactory#createDelete()
     */
    @Override
    public Delete createDelete() {
        return new DeleteImpl();
    }

    /**
     * @see com.unidata.mdm.conf.ObjectFactory#createMerge()
     */
    @Override
    public Merge createMerge() {
        return new MergeImpl();
    }

    /**
     * @see com.unidata.mdm.conf.ObjectFactory#createUpsert()
     */
    @Override
    public Upsert createUpsert() {
        return new UpsertImpl();
    }

    /**
     * @see com.unidata.mdm.conf.ObjectFactory#createSearch()
     */
    @Override
    public Search createSearch() {
        return new SearchImpl();
    }

    @Override
    public Join createJoin() {
        return new JoinImpl();
    }

    @Override
    public Split createSplit() {
        return new SplitImpl();
    }

    /**
     * @see com.unidata.mdm.conf.ObjectFactory#createWorkflowProcessDefinition()
     */
    @Override
    public WorkflowProcessDefinition createWorkflowProcessDefinition() {
        return new WorkflowProcessDefinitionImpl();
    }
}
