package org.unidata.mdm.system.dto;

/**
 * Boxing 'void' return method value for PipelineExecutionResult
 *
 * @author maria.chistyakova
 * @since  08.12.2019
 */
public class NullResultDto implements PipelineExecutionResult {

    private static final NullResultDto instance = new NullResultDto();

    private NullResultDto(){}

    public static NullResultDto getInstance(){
        return instance;
    }
}
