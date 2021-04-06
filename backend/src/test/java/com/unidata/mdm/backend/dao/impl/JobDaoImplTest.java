/**
 * Date: 11.03.2016
 */

package com.unidata.mdm.backend.dao.impl;

import com.unidata.mdm.backend.dao.JobDao;
import com.unidata.mdm.backend.dto.job.JobFilter;
import com.unidata.mdm.backend.po.job.JobPO;
import com.unidata.mdm.backend.po.job.JobParameterPO;
import com.unidata.mdm.backend.po.job.JobTriggerPO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/test-only-dao.xml",})
@ActiveProfiles("default")
@Transactional
public class JobDaoImplTest {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JobDaoImplTest.class);

    @Autowired
    private JobDao jobDao;

    @Test
    public void testGetAllJobs() {
        List<JobPO> jobs = jobDao.getJobs();

        logger.info("Found job amount: " + jobs.size());
    }

    @Test
    public void testSaveAndFindJob() {
        final Random random = new Random();
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = RandomStringUtils.randomAlphabetic(10);
        final Date createdDate = truncateDate(new Date());
        final JobPO jobPo = new JobPO();

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setEnabled(true);

        final int paramsAmount = random.nextInt(30);
        final List<JobParameterPO> savedParams = new ArrayList<>();
        for (int i = 0; i < paramsAmount; ++i) {
            final String jobParam1 = RandomStringUtils.randomAlphabetic(10);
            final String jobParam1Value = RandomStringUtils.randomAlphabetic(100);
            final JobParameterPO param = new JobParameterPO(jobParam1, jobParam1Value);
            param.setCreateDate(createdDate);
            param.setCreatedBy(createdBy);
            jobPo.addParameter(param);
            savedParams.add(param);
        }
        final Long jobId = jobDao.insertJob(jobPo);

        JobPO foundJob = jobDao.findJob(jobName);

        Assert.assertNotNull("Job not found!", foundJob);
        Assert.assertEquals("Wrong job id", jobId, foundJob.getId());
        Assert.assertEquals("Wrong job name", jobName, foundJob.getName());
        Assert.assertEquals("Created date", createdDate, foundJob.getCreateDate());
        Assert.assertEquals("Created by", createdBy, foundJob.getCreatedBy());

        foundJob.setParameters(jobDao.getJobParameters(foundJob.getId()));

        Assert.assertNotNull("There should be parameters for this job", foundJob.getParameters());
        Assert.assertArrayEquals("Parameter arrays are not equal", savedParams.toArray(), foundJob.getParameters().toArray());

        jobDao.markJobEnabled(jobId, false);

        foundJob = jobDao.findJob(jobId);

        Assert.assertEquals(false, foundJob.isEnabled());

        jobDao.removeJob(foundJob.getId());
    }

    @Test
    public void testSaveAndUpdateJob() {
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String cronExpression = "0 0 0 1 2";
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = RandomStringUtils.randomAlphabetic(10);
        final Date createdDate = truncateDate(new Date());
        final String jobParam1 = RandomStringUtils.randomAlphabetic(10);
        final String jobParam1Value = RandomStringUtils.randomAlphabetic(100);
        final JobParameterPO param = new JobParameterPO(jobParam1, jobParam1Value);
        final JobPO jobPo = new JobPO();

        param.setCreateDate(createdDate);
        param.setCreatedBy(createdBy);

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setCronExpression(cronExpression);
        jobPo.addParameter(param);
        final Long jobId = jobDao.insertJob(jobPo);

        Assert.assertEquals("Returned id not match", jobId, jobPo.getId());

        JobPO foundJob = jobDao.findJob(jobName);

        Assert.assertNotNull("Job not found!", foundJob);
        Assert.assertEquals("Initial cron expression check failed", cronExpression, foundJob.getCronExpression());

        foundJob = jobDao.findJob(jobId);

        Assert.assertNotNull("Job not found!", foundJob);
        Assert.assertEquals("Initial cron expression check failed", cronExpression, foundJob.getCronExpression());

        foundJob.setParameters(jobDao.getJobParameters(foundJob.getId()));

        Assert.assertNotNull("There should be parameter for this job", foundJob.getParameters());
        Assert.assertEquals("There should be 1 parameter", 1, foundJob.getParameters().size());
        Assert.assertEquals("Parameter name check failed", jobParam1, foundJob.getParameters().get(0).getName());
        Assert.assertEquals("Parameter value check failed", jobParam1Value, foundJob.getParameters().get(0).getValueObject());

        final String changedCronExpression = "1 1 1 1 1";
        foundJob.setCronExpression(changedCronExpression);
        final String newParamValue = "newParamValue";

        final List<JobParameterPO> params = foundJob.getParameters();

        if (!CollectionUtils.isEmpty(params)) {
            final List<JobParameterPO> newParams = new ArrayList<>(params.size());
            for (final JobParameterPO p : params) {
                final JobParameterPO newParam = new JobParameterPO(p.getName(), newParamValue);
                newParam.setId(p.getId());
                newParam.setJobId(p.getJobId());
                newParam.setUpdatedBy("TEST");
                newParam.setUpdateDate(new Date());
                newParams.add(newParam);
            }

            foundJob.setParameters(newParams);
        }

        final String updatedBy = RandomStringUtils.randomAlphabetic(10);
        final Date updatedDate = truncateDate(new Date());
        foundJob.setUpdatedBy(updatedBy);
        foundJob.setUpdateDate(updatedDate);

        jobDao.updateJob(foundJob);

        final JobPO newFoundJob = jobDao.findJob(jobName);
        Assert.assertNotNull("Updated job not found!", newFoundJob);
        Assert.assertEquals("Changed cron expression check failed", changedCronExpression, newFoundJob.getCronExpression());
        Assert.assertEquals("Updated by check failed", updatedBy, newFoundJob.getUpdatedBy());
        Assert.assertEquals("Updated date check failed", updatedDate, newFoundJob.getUpdateDate());

        newFoundJob.setParameters(jobDao.getJobParameters(newFoundJob.getId()));

        Assert.assertNotNull("There should be changed parameter for this job", newFoundJob.getParameters());
        Assert.assertEquals("There should be 1 parameter", 1, newFoundJob.getParameters().size());
        Assert.assertEquals("Old parameter name check failed", jobParam1, newFoundJob.getParameters().get(0).getName());
        Assert.assertEquals("New parameter value check failed", newParamValue, newFoundJob.getParameters().get(0).getValueObject());

        JobFilter jobFilter = new JobFilter();
        jobFilter.setFromInd(0L);
        jobFilter.setItemCount(100);

        int jobCount = jobDao.getJobsCount(jobFilter);
        Assert.assertTrue(jobCount > 0);

        List<JobPO> jobs = jobDao.searchJobs(jobFilter);
        Assert.assertNotNull(jobs);
        Assert.assertTrue(jobs.size() > 0);

        jobDao.removeJob(foundJob.getId());
    }

    @Test
    public void testSaveBatchJobInstance() {
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String cronExpression = "3 3 3 2 3";
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = RandomStringUtils.randomAlphabetic(10);
        final Date createdDate = truncateDate(new Date());
        final String jobParam1 = RandomStringUtils.randomAlphabetic(10);
        final String jobParam1Value = RandomStringUtils.randomAlphabetic(100);
        final JobParameterPO param = new JobParameterPO(jobParam1, jobParam1Value);
        final JobPO jobPo = new JobPO();

        param.setCreateDate(createdDate);
        param.setCreatedBy(createdBy);

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setCronExpression(cronExpression);
        jobPo.addParameter(param);
        final Long jobId = jobDao.insertJob(jobPo);

        Assert.assertEquals("Returned id not match", jobId, jobPo.getId());

        final Long jobInstanceId = 1L;

        jobDao.saveBatchJobInstance(jobId, jobInstanceId, "TEST", new Date());

        final Map<Long, List<Long>> jobInstanceIds = jobDao.findAllBatchJobIds(Collections.singletonList(jobId));
        final Map<Long, Long> jobLastInstanceId = jobDao.findLastBatchJobIds(Collections.singletonList(jobId));
    }

    @Test
    public void testEmptyListBatchJobInstance() {
        final Map<Long, List<Long>> jobInstanceIds = jobDao.findAllBatchJobIds(Collections.emptyList());
        final Map<Long, Long> jobLastInstanceId = jobDao.findLastBatchJobIds(Collections.emptyList());
    }

    @Test
    public void testJobSaveDateParameter() {
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = "TEST";
        final Date createdDate = truncateDate(new Date());
        final JobPO jobPo = new JobPO();

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setEnabled(true);

        final String jobParam1 = RandomStringUtils.randomAlphabetic(10);
        final ZonedDateTime jobDateParam = ZonedDateTime.now();
        final JobParameterPO param = new JobParameterPO(jobParam1, jobDateParam);
        param.setCreateDate(createdDate);
        param.setCreatedBy(createdBy);
        jobPo.addParameter(param);

        final Long jobId = jobDao.insertJob(jobPo);
        final List<JobParameterPO> foundParams = jobDao.getJobParameters(jobId);

        Assert.assertNotNull("There should be parameters for this job", foundParams);
        Assert.assertEquals("There should be 1 parameter for this job", 1, foundParams.size());
        Assert.assertEquals("Saved and fetched date parameter not equal", jobDateParam.compareTo(foundParams.get(0).getDateValue()), 0);

        final JobPO foundJob = jobDao.findJob(jobId);
        final String updatedBy = "TEST";
        final Date updatedDate = truncateDate(new Date());
        foundJob.setUpdatedBy(updatedBy);
        foundJob.setUpdateDate(updatedDate);

        final ZonedDateTime newDate = ZonedDateTime.of(1900, 1, 13, 12, 33, 0, 0, ZoneId.systemDefault());
        final JobParameterPO newParam = new JobParameterPO(foundParams.get(0).getName(), newDate);
        newParam.setJobId(foundParams.get(0).getJobId());
        newParam.setId(foundParams.get(0).getId());
        newParam.setUpdateDate(truncateDate(new Date()));
        newParam.setUpdatedBy("TEST");

        foundJob.addParameter(newParam);

        jobDao.updateJob(foundJob);

        final JobPO newFoundJob = jobDao.findJob(jobName);

        newFoundJob.setParameters(jobDao.getJobParameters(newFoundJob.getId()));

        Assert.assertNotNull("There should be changed parameter for this job", newFoundJob.getParameters());
        Assert.assertEquals("There should be 1 parameter", 1, newFoundJob.getParameters().size());
        Assert.assertEquals("Old parameter name check failed", jobParam1, newFoundJob.getParameters().get(0).getName());
        Assert.assertEquals("New date parameter value check failed", newDate, newFoundJob.getParameters().get(0).getDateValue());
    }

    @Test
    public void testJobSaveLongParameter() {
        final Random random = new Random();
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = "TEST";
        final Date createdDate = truncateDate(new Date());
        final JobPO jobPo = new JobPO();

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setEnabled(true);

        final String jobParam1 = RandomStringUtils.randomAlphabetic(10);
        final Long jobLongParam = random.nextLong();
        final JobParameterPO param = new JobParameterPO(jobParam1, jobLongParam);
        param.setCreateDate(createdDate);
        param.setCreatedBy(createdBy);
        jobPo.addParameter(param);

        final Long jobId = jobDao.insertJob(jobPo);
        final List<JobParameterPO> foundParams = jobDao.getJobParameters(jobId);

        Assert.assertNotNull("There should be parameters for this job", foundParams);
        Assert.assertEquals("There should be 1 parameter for this job", 1, foundParams.size());
        Assert.assertEquals("Saved and fetched long parameter not equal", jobLongParam.compareTo(foundParams.get(0).getLongValue()), 0);

        final JobPO foundJob = jobDao.findJob(jobId);
        final String updatedBy = "TEST";
        final Date updatedDate = truncateDate(new Date());
        foundJob.setUpdatedBy(updatedBy);
        foundJob.setUpdateDate(updatedDate);

        final Long newLong = random.nextLong();
        final JobParameterPO newParam = new JobParameterPO(foundParams.get(0).getName(), newLong);
        newParam.setJobId(foundParams.get(0).getJobId());
        newParam.setId(foundParams.get(0).getId());
        newParam.setUpdateDate(truncateDate(new Date()));
        newParam.setUpdatedBy("TEST");

        foundJob.addParameter(newParam);

        jobDao.updateJob(foundJob);

        final JobPO newFoundJob = jobDao.findJob(jobName);

        newFoundJob.setParameters(jobDao.getJobParameters(newFoundJob.getId()));

        Assert.assertNotNull("There should be changed parameter for this job", newFoundJob.getParameters());
        Assert.assertEquals("There should be 1 parameter", 1, newFoundJob.getParameters().size());
        Assert.assertEquals("Old parameter name check failed", jobParam1, newFoundJob.getParameters().get(0).getName());
        Assert.assertEquals("New long parameter value check failed", newLong, newFoundJob.getParameters().get(0).getLongValue());
    }

    @Test
    public void testJobSaveDoubleParameter() {
        final Random random = new Random();
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = "TEST";
        final Date createdDate = truncateDate(new Date());
        final JobPO jobPo = new JobPO();

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setEnabled(true);

        final String jobParam1 = RandomStringUtils.randomAlphabetic(10);
        final Double jobDoubleParam = random.nextDouble();
        final JobParameterPO param = new JobParameterPO(jobParam1, jobDoubleParam);
        param.setCreateDate(createdDate);
        param.setCreatedBy(createdBy);
        jobPo.addParameter(param);

        final Long jobId = jobDao.insertJob(jobPo);
        final List<JobParameterPO> foundParams = jobDao.getJobParameters(jobId);

        Assert.assertNotNull("There should be parameters for this job", foundParams);
        Assert.assertEquals("There should be 1 parameter for this job", 1, foundParams.size());
        Assert.assertEquals("Saved and fetched double parameter not equal", jobDoubleParam.compareTo(foundParams.get(0).getDoubleValue()), 0);

        final JobPO foundJob = jobDao.findJob(jobId);
        final String updatedBy = "TEST";
        final Date updatedDate = truncateDate(new Date());
        foundJob.setUpdatedBy(updatedBy);
        foundJob.setUpdateDate(updatedDate);

        final Double newDouble = random.nextDouble();
        final JobParameterPO newParam = new JobParameterPO(foundParams.get(0).getName(), newDouble);
        newParam.setJobId(foundParams.get(0).getJobId());
        newParam.setId(foundParams.get(0).getId());
        newParam.setUpdateDate(truncateDate(new Date()));
        newParam.setUpdatedBy("TEST");

        foundJob.addParameter(newParam);

        jobDao.updateJob(foundJob);

        final JobPO newFoundJob = jobDao.findJob(jobName);

        newFoundJob.setParameters(jobDao.getJobParameters(newFoundJob.getId()));

        Assert.assertNotNull("There should be changed parameter for this job", newFoundJob.getParameters());
        Assert.assertEquals("There should be 1 parameter", 1, newFoundJob.getParameters().size());
        Assert.assertEquals("Old parameter name check failed", jobParam1, newFoundJob.getParameters().get(0).getName());
        Assert.assertEquals("New double parameter value check failed", newDouble, newFoundJob.getParameters().get(0).getDoubleValue());
    }

    @Test
    public void testSaveAndFindJobTrigger() {
        final JobTriggerPO jobTrigger = new JobTriggerPO();
        final Date createdDate = truncateDate(new Date());
        final String createdBy = RandomStringUtils.randomAlphabetic(10);
        final Long finishJobId = createJob();
        final Long startJobId = createJob();
        final String jobName = RandomStringUtils.randomAlphanumeric(20);
        final String description = "Test trigger";

        jobTrigger.setCreateDate(createdDate);
        jobTrigger.setCreatedBy(createdBy);
        jobTrigger.setName(jobName);
        jobTrigger.setStartJobId(startJobId);
        jobTrigger.setFinishJobId(finishJobId);
        jobTrigger.setSuccessRule(true);
        jobTrigger.setDescription(description);

        final Long jobTriggerId = jobDao.insertJobTrigger(jobTrigger);

        JobTriggerPO foundJobTrigger = jobDao.findJobTrigger(finishJobId, jobTriggerId);

        Assert.assertNotNull("JobTrigger not found!", foundJobTrigger);
        Assert.assertEquals("Wrong jobTrigger id", jobTriggerId, foundJobTrigger.getId());
        Assert.assertEquals("Wrong jobTrigger name", jobName, foundJobTrigger.getName());
        Assert.assertEquals("Wrong created date", createdDate, foundJobTrigger.getCreateDate());
        Assert.assertEquals("Wrong created by", createdBy, foundJobTrigger.getCreatedBy());
        Assert.assertEquals("Wrong finishJobId", finishJobId, foundJobTrigger.getFinishJobId());
        Assert.assertEquals("Wrong startJobId", startJobId, foundJobTrigger.getStartJobId());
        Assert.assertEquals("Wrong description", description, foundJobTrigger.getDescription());
    }


    @Test
    public void testSaveAndUpdateJobTrigger() {
        final JobTriggerPO jobTrigger = new JobTriggerPO();
        final Date createdDate = truncateDate(new Date());
        final String createdBy = RandomStringUtils.randomAlphabetic(10);
        final Long finishJobId = createJob();
        final Long startJobId = createJob();
        final Long nextJobId = createJob();
        final String jobName = RandomStringUtils.randomAlphanumeric(20);
        final String description = "Test trigger";

        jobTrigger.setCreateDate(createdDate);
        jobTrigger.setCreatedBy(createdBy);
        jobTrigger.setName(jobName);
        jobTrigger.setStartJobId(startJobId);
        jobTrigger.setFinishJobId(finishJobId);
        jobTrigger.setSuccessRule(true);
        jobTrigger.setDescription(description);

        final Long jobTriggerId = jobDao.insertJobTrigger(jobTrigger);

        final JobTriggerPO foundJobTrigger = jobDao.findJobTrigger(finishJobId, jobTriggerId);

        Assert.assertNotNull("JobTrigger not found!", foundJobTrigger);
        Assert.assertEquals("Wrong jobTrigger id", jobTriggerId, foundJobTrigger.getId());
        Assert.assertEquals("Wrong jobTrigger name", jobName, foundJobTrigger.getName());
        Assert.assertEquals("Wrong created date", createdDate, foundJobTrigger.getCreateDate());
        Assert.assertEquals("Wrong created by", createdBy, foundJobTrigger.getCreatedBy());
        Assert.assertEquals("Wrong finishJobId", finishJobId, foundJobTrigger.getFinishJobId());
        Assert.assertEquals("Wrong startJobId", startJobId, foundJobTrigger.getStartJobId());
        Assert.assertEquals("Wrong description", description, foundJobTrigger.getDescription());

        final String newJobName = RandomStringUtils.randomAlphanumeric(20);
        final Date updateDate = truncateDate(new Date());
        final String updateBy = RandomStringUtils.randomAlphabetic(10);

        foundJobTrigger.setStartJobId(nextJobId);
        foundJobTrigger.setName(newJobName);
        foundJobTrigger.setUpdateDate(updateDate);
        foundJobTrigger.setUpdatedBy(updateBy);

        jobDao.updateJobTrigger(foundJobTrigger);

        JobTriggerPO newJobTrigger = jobDao.findJobTrigger(finishJobId,jobTriggerId);

        Assert.assertNotNull("JobTrigger not found!", newJobTrigger);
        Assert.assertEquals("Wrong jobTrigger name", newJobName, newJobTrigger.getName());
        Assert.assertEquals("Wrong startJob id", nextJobId, newJobTrigger.getStartJobId());
        Assert.assertEquals("Wrong updateDate", updateDate, newJobTrigger.getUpdateDate());
        Assert.assertEquals("Wrong updateBy", updateBy, newJobTrigger.getUpdatedBy());
        Assert.assertEquals("Wrong startJobId", updateBy, newJobTrigger.getUpdatedBy());
    }

    private Date truncateDate(final Date dateObject) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dateObject);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    private long createJob(){
        final String jobName = RandomStringUtils.randomAlphanumeric(10);
        final String jobNameReference = RandomStringUtils.randomAlphanumeric(20);
        final String createdBy = RandomStringUtils.randomAlphabetic(10);
        final Date createdDate = truncateDate(new Date());
        final JobPO jobPo = new JobPO();

        jobPo.setCreateDate(createdDate);
        jobPo.setCreatedBy(createdBy);
        jobPo.setName(jobName);
        jobPo.setJobNameReference(jobNameReference);
        jobPo.setEnabled(true);

        return jobDao.insertJob(jobPo);
    }

}
