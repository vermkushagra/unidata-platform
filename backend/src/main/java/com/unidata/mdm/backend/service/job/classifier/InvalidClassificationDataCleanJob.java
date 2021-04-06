package com.unidata.mdm.backend.service.job.classifier;

import java.util.List;

import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class InvalidClassificationDataCleanJob extends QuartzJobBean {

    private ClsfDao clsfDao;

    @Autowired
    public void setClsfDao(ClsfDao clsfDao) {
        this.clsfDao = clsfDao;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (jobDisabled(context)) {
            return;
        }
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final List<ClsfPO> allClassifiers = clsfDao.findAllClassifiers();
        allClassifiers.forEach(classifier -> {
            clsfDao.removeOriginsLinksToClassifierNotExistsNodes(classifier);
            clsfDao.removeEtalonLinksToClassifierNotExistsNodes(classifier);
        });
    }

    private boolean jobDisabled(JobExecutionContext context) {
        final JobDataMap jobDataMap = context.getMergedJobDataMap();
        return !jobDataMap.getBooleanValue("jobEnabled");
    }
}
