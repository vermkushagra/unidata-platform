package org.unidata.mdm.system.exception;

/**
 * @author Mikhail Mikhailov
 * Exception IDs for this module.
 */
public class SystemExceptionIds {
    /**
     * JAXB data type factory init failure.
     */
    public static final ExceptionId EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE
        = new ExceptionId("EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE", "app.data.jaxbTypeFactoryInitFailure");
    /**
     * Node id undefined.
     */
    public static final ExceptionId EX_SYSTEM_NODE_ID_UNDEFINED
        = new ExceptionId("EX_SYSTEM_NODE_ID_UNDEFINED", "app.system.node.id.undefined");
    /**
     * Platform version undefined.
     */
    public static final ExceptionId EX_SYSTEM_PLATFORM_VERSION_UNDEFINED
        = new ExceptionId("EX_SYSTEM_PLATFORM_VERSION_UNDEFINED", "app.system.platform.version.undefined");
    /**
     * Platform version invalid.
     */
    public static final ExceptionId EX_SYSTEM_PLATFORM_VERSION_INVALID
        = new ExceptionId("EX_SYSTEM_PLATFORM_VERSION_INVALID", "app.system.platform.version.invalid");

    public static final ExceptionId EX_CONFIGURATION_PROPERTIES_INVALID =
            new ExceptionId("EX_CONFIGURATION_PROPERTIES_INVALID", "");

    public static final ExceptionId EX_MODULE_CANNOT_BE_INSTALLED = new ExceptionId("EX_MODULE_CANNOT_BE_INSTALLED", "app.module.cannot.be.installed");

    public static final ExceptionId EX_MODULE_CANNOT_BE_UNINSTALLED = new ExceptionId("EX_MODULE_CANNOT_BE_UNINSTALLED", "app.module.cannot.be.uninstalled");

    public static final ExceptionId EX_SYSTEM_CANNOT_INITIALIZE_NON_XA_FACTORY
        = new ExceptionId("EX_SYSTEM_CANNOT_INITIALIZE_NON_XA_FACTORY", "app.module.cannot.initialize.non.xa.factory");

    public static final ExceptionId EX_SYSTEM_CANNOT_INITIALIZE_XA_FACTORY
        = new ExceptionId("EX_SYSTEM_CANNOT_INITIALIZE_XA_FACTORY", "app.module.cannot.initialize.xa.factory");

    public static final ExceptionId EX_SYSTEM_CANNOT_CREATE_NON_XA_DATASOURCE
        = new ExceptionId("EX_SYSTEM_CANNOT_CREATE_NON_XA_DATASOURCE", "app.module.cannot.create.non.xa.datasource");

    public static final ExceptionId EX_SYSTEM_CANNOT_CREATE_XA_DATASOURCE
        = new ExceptionId("EX_SYSTEM_CANNOT_CREATE_XA_DATASOURCE", "app.module.cannot.create.xa.datasource");
    /**
     * Undefined error occurs.
     */
    public static final ExceptionId EX_SYSTEM_CONNECTION_GET
            = new ExceptionId("EX_SYSTEM_CONNECTION_GET", "app.system.connection.get");
    /**
     * The pipeline is already closed.
     */
    public static final ExceptionId EX_PIPELINE_ALREADY_FINISHED
        = new ExceptionId("EX_PIPELINE_ALREADY_FINISHED", "app.pipeline.already.finished");
    /**
     * The pipeline is not finished yet.
     */
    public static final ExceptionId EX_PIPELINE_IS_NOT_FINISHED
        = new ExceptionId("EX_PIPELINE_IS_NOT_FINISHED", "app.pipeline.is.not.finished");
    /**
     * Pipeline start type [{}] not found.
     */
    public static final ExceptionId EX_PIPELINE_START_TYPE_NOT_FOUND
        = new ExceptionId("EX_PIPELINE_START_TYPE_NOT_FOUND", "app.pipeline.start.type.not.found");
    /**
     * Pipeline not found by id [{}], subject [{}].
     */
    public static final ExceptionId EX_PIPELINE_NOT_FOUND_BY_ID_AND_SUBJECT
        = new ExceptionId("EX_PIPELINE_NOT_FOUND_BY_ID_AND_SUBJECT", "app.pipeline.not.found.by.id.and.subject");
    /**
     * Start segment not found by id [{}].
     */
    public static final ExceptionId EX_PIPELINE_START_SEGMENT_NOT_FOUND_BY_ID
        = new ExceptionId("EX_PIPELINE_START_SEGMENT_NOT_FOUND_BY_ID", "app.pipeline.start.segment.not.found.by.id");
    /**
     * Segment not found by id [{}].
     */
    public static final ExceptionId EX_PIPELINE_SEGMENT_NOT_FOUND_BY_ID
        = new ExceptionId("EX_PIPELINE_SEGMENT_NOT_FOUND_BY_ID", "app.pipeline.segment.not.found.by.id");
    /**
     * Segment found, but is of different type [{}].
     */
    public static final ExceptionId EX_PIPELINE_SEGMENT_OF_WRONG_TYPE
        = new ExceptionId("EX_PIPELINE_SEGMENT_OF_WRONG_TYPE", "app.pipeline.segment.of.wrong.type");
    /**
     * Invalid number of segments. A pipeline must contain at least 2 points of type 'start' and 'finish'.
     */
    public static final ExceptionId EX_PIPELINE_INVALID_NUMBER_OF_SEGMENTS
        = new ExceptionId("EX_PIPELINE_INVALID_NUMBER_OF_SEGMENTS", "app.pipeline.invalid.number.of.segments");
    /**
     * Invalid pipeline layout. A pipeline must start with a point of type 'start' and end with a point of type 'finish'.
     */
    public static final ExceptionId EX_PIPELINE_HAS_NO_START_OR_FINISH_OR_BOTH
        = new ExceptionId("EX_PIPELINE_HAS_NO_START_OR_FINISH_OR_BOTH", "app.pipeline.has.no.start.or.finish.or.both");
    /**
     * Pipeline execution failed.
     */
    public static final ExceptionId EX_PIPELINE_EXECUTION_FAILED
        = new ExceptionId("EX_PIPELINE_EXECUTION_FAILED", "app.pipeline.execution.failed");

    public static final ExceptionId EX_EVENT_NO_VALID_ID
        = new ExceptionId("EX_EVENT_NO_VALID_ID", "app.event.invalid.id");

    public static final ExceptionId EX_EVENT_ALREADY_WAITING
        = new ExceptionId("EX_EVENT_ALREADY_WAITING", "app.event.is.waiting.yet");

    /**
     * Constructor.
     */
    private SystemExceptionIds() {
        super();
    }
}
