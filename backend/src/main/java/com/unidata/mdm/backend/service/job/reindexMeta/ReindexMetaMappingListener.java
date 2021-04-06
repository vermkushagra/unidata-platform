package com.unidata.mdm.backend.service.job.reindexMeta;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;

public class ReindexMetaMappingListener implements JobExecutionListener {

    @Autowired
    private SearchServiceExt searchServiceExt;

    private boolean recreateAudit;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        final String storageId = SecurityUtils.getCurrentUserStorageId();
        boolean isModelIndexExist = searchServiceExt.modelIndexExists(storageId);
        if (!isModelIndexExist) {
            searchServiceExt.createModelIndex(storageId);
        }
        if (!searchServiceExt.classifierIndexExist(storageId)) {
            searchServiceExt.createClassifierIndex(storageId);
        }

        searchServiceExt.createAuditIndex(null, recreateAudit);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }

    @Required
    public void setRecreateAudit(boolean recreateAudit) {
        this.recreateAudit = recreateAudit;
    }
}
