package org.unidata.mdm.core.dto.job;

public class JobExecutionExitStatusDTO {

    private final String exitCode;

    private final String exitDescription;

    public JobExecutionExitStatusDTO(String exitCode, String exitDescription) {
        this.exitCode = exitCode;
        this.exitDescription = exitDescription;
    }

    public String getExitCode() {
        return exitCode;
    }

    public String getExitDescription() {
        return exitDescription;
    }
}
