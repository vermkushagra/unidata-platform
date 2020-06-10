package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 * Root context for classifier data operations.
 */
public abstract class AbstractClassifierDataRequestContext extends CommonSendableContext
    implements ClassifierIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6132335587844967713L;
    /**
     * Constructor.
     */
    public AbstractClassifierDataRequestContext() {
        super();
    }
}
