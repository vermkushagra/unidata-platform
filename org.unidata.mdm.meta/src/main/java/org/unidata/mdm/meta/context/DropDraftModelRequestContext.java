package org.unidata.mdm.meta.context;

import java.io.Serializable;

import org.unidata.mdm.meta.service.segments.ModelPublishStartExecutor;
import org.unidata.mdm.system.context.AbstractCompositeRequestContext;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author maria.chistyakova
 * @since  13.01.2020
 */
public class DropDraftModelRequestContext
        extends AbstractCompositeRequestContext
        implements PipelineInput, Serializable {

    public static DropDraftModelRequestContextBuilder builder() {
        return new DropDraftModelRequestContextBuilder();
    }

    private boolean changeActive;

    /**
     * Constructor.
     *
     * @param b
     */
    public DropDraftModelRequestContext(DropDraftModelRequestContextBuilder b) {
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

    public static class DropDraftModelRequestContextBuilder extends AbstractCompositeRequestContextBuilder<DropDraftModelRequestContextBuilder> {

        private boolean changeActive;

        public DropDraftModelRequestContextBuilder setChangeActive(boolean changeActive) {
            this.changeActive = changeActive;
            return this;
        }

        @Override
        public DropDraftModelRequestContext build() {
            return new DropDraftModelRequestContext(this);
        }
    }
}
