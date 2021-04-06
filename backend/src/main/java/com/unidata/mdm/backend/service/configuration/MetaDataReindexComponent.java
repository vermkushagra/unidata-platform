package com.unidata.mdm.backend.service.configuration;

import static java.util.Arrays.asList;

import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;

@Component
public class MetaDataReindexComponent implements AfterContextRefresh {

    @Autowired
    private JobServiceExt jobServiceExt;

    @Override
    public void afterContextRefresh() {
        JobParameterDTO reindexModel = new JobParameterDTO("reindexModelMeta", true);
        JobParameterDTO reindexClassifiers = new JobParameterDTO("reindexClassifiersMeta", true);
        JobDTO job = new JobDTO();
        job.setDescription("Reindex meta data if it need");
        job.setName("Reindex meta Job");
        job.setEnabled(true);
        job.setJobNameReference("reindexMetaJob");
        job.setParameters(asList(reindexModel, reindexClassifiers));
        JobExecution execution = jobServiceExt.startSystemJob(job);
    }
}
