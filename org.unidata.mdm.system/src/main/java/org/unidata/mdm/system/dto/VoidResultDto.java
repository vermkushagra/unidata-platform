package org.unidata.mdm.system.dto;

/**
 * Boxing 'void' return method value for PipelineExecutionResult
 *
 * @author maria.chistyakova
 * @since  08.12.2019
 */
public class VoidResultDto implements PipelineExecutionResult {

    private static final VoidResultDto instance = new VoidResultDto();

    private VoidResultDto(){}

    public static VoidResultDto getInstance(){
        return instance;
    }
}
