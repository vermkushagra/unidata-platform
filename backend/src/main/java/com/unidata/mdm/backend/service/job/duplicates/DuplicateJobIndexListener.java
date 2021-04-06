package com.unidata.mdm.backend.service.job.duplicates;


import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.Map;

/**
 * @author Dmitry Kopin on 16.03.2018.
 */
public class DuplicateJobIndexListener implements JobExecutionListener{

    @Autowired
    private SearchServiceExt searchService;

    private String entityName;

    @Override
    public void beforeJob(JobExecution jobExecution){
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "-1");
        searchService.setIndexSettings(entityName, SecurityUtils.getCurrentUserStorageId(), indexParams);
        searchService.refreshIndex(entityName, SecurityUtils.getCurrentUserStorageId(), false);
    }


    @Override
    public void afterJob(JobExecution jobExecution){
        Map<String, Object> indexParams = Collections.singletonMap("index.refresh_interval", "1s");
        searchService.setIndexSettings(entityName, SecurityUtils.getCurrentUserStorageId(), indexParams);
        searchService.refreshIndex(entityName, SecurityUtils.getCurrentUserStorageId(), true);
    }


    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
