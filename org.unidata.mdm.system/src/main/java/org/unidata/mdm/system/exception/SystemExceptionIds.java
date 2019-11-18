package org.unidata.mdm.system.exception;

/**
 * @author Mikhail Mikhailov
 * Exception IDs for this module.
 */
public class SystemExceptionIds {
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
     * Constructor.
     */
    private SystemExceptionIds() {
        super();
    }
}
