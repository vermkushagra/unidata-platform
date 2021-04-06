package com.unidata.mdm.backend.service.model.util.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.wrappers.SourceSystemWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.SourceSystemDef;

@Component
public class SourceSystemModelElementFacade extends AbstractModelElementFacade<SourceSystemWrapper, SourceSystemDef> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.SOURCE_SYSTEM;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull SourceSystemDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull SourceSystemDef modelElement) {
        return JaxbUtils.marshalSourceSystem(modelElement);
    }


    @Nonnull
    @Override
    public SourceSystemWrapper convertToWrapper(@Nonnull SourceSystemDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new SourceSystemWrapper(modelElement);
    }
}
