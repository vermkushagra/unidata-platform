package org.unidata.mdm.meta.service.impl.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.SourceSystemDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.SourceSystemInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;

@Component
public class SourceSystemModelElementFacade extends AbstractModelElementFacade<SourceSystemInfoHolder, SourceSystemDef> {

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
        return MetaJaxbUtils.marshalSourceSystem(modelElement);
    }


    @Nonnull
    @Override
    public SourceSystemInfoHolder convertToWrapper(@Nonnull SourceSystemDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new SourceSystemInfoHolder(modelElement);
    }
}
