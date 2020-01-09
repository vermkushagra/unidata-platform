package org.unidata.mdm.meta.context;

import java.io.Serializable;

import org.unidata.mdm.meta.service.segments.ModelPublishStartExecutor;
import org.unidata.mdm.system.context.AbstractCompositeRequestContext;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author maria.chistyakova
 * @since  18.12.2019
 */
public class PublishModelRequestContext
        extends AbstractCompositeRequestContext
        implements PipelineInput, Serializable {

    public static PublishModelRequestContext.PublishModelRequestContextBuilder builder() {
        return new PublishModelRequestContextBuilder();
    }
    /**
     * Constructor.
     *
     * @param b
     */
    public PublishModelRequestContext(PublishModelRequestContextBuilder b) {
        super(b);
    }

    @Override
    public String getStartTypeId() {
        return ModelPublishStartExecutor.SEGMENT_ID;
    }

    public static class PublishModelRequestContextBuilder extends AbstractCompositeRequestContextBuilder<PublishModelRequestContextBuilder> {

        @Override
        public PublishModelRequestContext build() {
            return new PublishModelRequestContext(this);
        }
    }
}