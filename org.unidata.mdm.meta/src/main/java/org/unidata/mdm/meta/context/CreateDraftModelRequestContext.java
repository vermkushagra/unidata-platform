package org.unidata.mdm.meta.context;

import java.io.Serializable;

import org.unidata.mdm.meta.service.segments.ModelPublishStartExecutor;
import org.unidata.mdm.system.context.AbstractCompositeRequestContext;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * context for create model draft pipeline
 *
 * @author maria.chistyakova
 * @since  13.01.2020
 */
public class CreateDraftModelRequestContext
        extends AbstractCompositeRequestContext
        implements PipelineInput, Serializable {

    private boolean changeActive;

    public static CreateDraftModelRequestContextBuilder builder() {
        return new CreateDraftModelRequestContextBuilder();
    }

    public CreateDraftModelRequestContext(CreateDraftModelRequestContextBuilder b) {
        super(b);
        this.changeActive = b.changeActive;
    }

    public boolean isChangeActive() {
        return changeActive;
    }

    @Override
    public String getStartTypeId() {
        return ModelPublishStartExecutor.SEGMENT_ID;
    }

    public static class CreateDraftModelRequestContextBuilder extends AbstractCompositeRequestContextBuilder<CreateDraftModelRequestContextBuilder> {

        private boolean changeActive;

        public CreateDraftModelRequestContextBuilder setChangeActive(boolean changeActive) {
            this.changeActive = changeActive;
            return this;
        }

        @Override
        public CreateDraftModelRequestContext build() {
            return new CreateDraftModelRequestContext(this);
        }


    }
}
