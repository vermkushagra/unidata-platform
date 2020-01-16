package org.unidata.mdm.meta.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.MeasurementValueDef;
import org.unidata.mdm.meta.MeasurementValues;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelImportService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.util.MetaJaxbUtils;
import org.unidata.mdm.meta.util.ModelUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class MetaModelImportServiceImpl implements MetaModelImportService {

    private final MetaModelService metaModelService;

    private final MetaDraftService metaDraftService;

    private final MetaMeasurementService metaMeasurementService;

    public MetaModelImportServiceImpl(
            final MetaModelService metaModelService,
            final MetaDraftService metaDraftService,
            final MetaMeasurementService metaMeasurementService
    ) {
        this.metaModelService = metaModelService;
        this.metaDraftService = metaDraftService;
        this.metaMeasurementService = metaMeasurementService;
    }


    @Override
    public void importModel(final InputStream inputStream, final boolean recreate) {
        try {
            final Model model = MetaJaxbUtils.createModelFromInputStream(inputStream);
            if (recreate && CollectionUtils.isEmpty(model.getSourceSystems())) {
                model.getSourceSystems().add(ModelUtils.createDefaultSourceSystem());
            }
            final UpdateModelRequestContext updateModelRequestContext =
                    UpdateModelRequestContext.builder()
                            .enumerationsUpdate(model.getEnumerations() == null ? null : new ArrayList<>(model
                                    .getEnumerations()
                                    .stream()
                                    .collect(Collectors.toCollection(() ->
                                            new TreeSet<>(Comparator.comparing(EnumerationDataType::getName))))))
                            .sourceSystemsUpdate(model.getSourceSystems())
                            .nestedEntityUpdate(model.getNestedEntities())
                            .lookupEntityUpdate(model.getLookupEntities())
                            .entitiesGroupsUpdate(model.getEntitiesGroup())
                            .entityUpdate(model.getEntities())
                            .relationsUpdate(model.getRelations())
                            .isForceRecreate(
                                    recreate ?
                                            UpdateModelRequestContext.ModelUpsertType.FULLY_NEW :
                                            UpdateModelRequestContext.ModelUpsertType.ADDITION
                            )
                            .build();
            metaModelService.upsertModel(updateModelRequestContext);
//            metaDraftService.removeDraft();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importMeasureUnits(final InputStream measureUnitsInputStream) {
        try {
            MeasurementValues values = MetaJaxbUtils.createMeasurementValuesFromInputStream(measureUnitsInputStream);
            if (Objects.isNull(values)) {
                return;
            }

            for (MeasurementValueDef value : values.getValue()) {
                metaMeasurementService.saveValue(MeasurementValueXmlConverter.convert(value));
            }
        } catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
