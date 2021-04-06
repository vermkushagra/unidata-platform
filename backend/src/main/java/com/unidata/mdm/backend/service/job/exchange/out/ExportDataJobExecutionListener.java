package com.unidata.mdm.backend.service.job.exchange.out;

import java.nio.charset.StandardCharsets;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.job.reports.ReportGenerator;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * @author Mikhail Mikhailov
 * Job execution listener.
 */
public class ExportDataJobExecutionListener extends ReportGenerator {
    /**
     * Hazelcast instance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().putString("runId", IdUtils.v4String());
        super.beforeJob(jobExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {

        final String runId = jobExecution.getExecutionContext().getString("runId");
        final String keyPrefix = new StringBuilder()
                .append(ExportDataConstants.EXCHANGE_OBJECTS_PREFIX)
                .append("_")
                .append(runId)
                .append("_")
                .toString();

        IMap<Object, Object> map = hazelcastInstance.getMap(ExportDataConstants.EXCHANGE_OBJECTS_MAP_NAME);
        for (Object k : map.keySet()) {
            if (k.toString().startsWith(keyPrefix)) {
                map.remove(k);
            }
        }

        super.afterJob(jobExecution);
    }

    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {
        return "\n" + getStatisticMessage(jobExecution);
    }

    private String getStatisticMessage(JobExecution jobExecution) {

        final String runId = jobExecution.getExecutionContext().getString("runId");

        IAtomicLong fCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                .append(ExportDataConstants.EXPORT_JOB_FAIL_RESULT)
                .append("_")
                .append(runId)
                .toString());
        long failed = fCounter.get();

        IAtomicLong sCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                .append(ExportDataConstants.EXPORT_JOB_SKIP_RESULT)
                .append("_")
                .append(runId)
                .toString());
        long skept = sCounter.get();

        IAtomicLong iCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                .append(ExportDataConstants.EXPORT_JOB_INSERT_RESULT)
                .append("_")
                .append(runId)
                .toString());
        long inserted = iCounter.get();

        IAtomicLong uCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                .append(ExportDataConstants.EXPORT_JOB_UPDATE_RESULT)
                .append("_")
                .append(runId)
                .toString());
        long updated = uCounter.get();

        return "Всего записей:" + (failed + skept + inserted + updated)
            + ".\n Вставлено:" + inserted
            + ".\n Обновлено:" + updated
            + ".\n Пропущено:" + skept
            + ".\n Завершилось с ошибкой: " + failed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] getReport(JobExecution jobExecution) {

        final StringBuilder sb = new StringBuilder();
        for (StepExecution se : jobExecution.getStepExecutions()) {

            if (ExitStatus.FAILED.getExitCode().equals(se.getExitStatus().getExitCode())) {
                sb.append(se.getStepName())
                  .append(": ")
                  .append(se.getExitStatus().getExitCode())
                  .append("\n\n")
                  .append(se.getExitStatus().getExitDescription())
                  .append("\n\n");
            }
        }

        return sb.length() == 0 ? null : new StringBuilder()
                .append(MessageUtils.getMessage(JobUtil.MSG_COLLECTED_EXECUTION_FAILURES))
                .append("\n\n")
                .append(sb)
                .toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] getReportBytes(JobExecution jobExecution) {
        return this.getReport(jobExecution);
    }
}
