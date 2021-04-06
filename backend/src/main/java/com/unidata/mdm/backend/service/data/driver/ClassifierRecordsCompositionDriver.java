package com.unidata.mdm.backend.service.data.driver;

import java.util.List;

import com.unidata.mdm.backend.common.types.OriginClassifier;

/**
 * Classifier composer
 */
public class ClassifierRecordsCompositionDriver extends EtalonCompositionDriverBase<OriginClassifier> {

    @Override
    public boolean hasActiveBVR(List<CalculableHolder<OriginClassifier>> calculables) {
        //Not applicable
        return true;
    }

    @Override
    public boolean hasActiveBVT(List<CalculableHolder<OriginClassifier>> calculables) {
        return false;
    }

    @Override
    public OriginClassifier composeBVR(List<CalculableHolder<OriginClassifier>> calculables, boolean includeInactive, boolean includeWinners) {
        return super.composeDefaultBVR(calculables, includeInactive);
    }

    @Override
    public OriginClassifier composeBVT(List<CalculableHolder<OriginClassifier>> calculables, boolean includeInactive, boolean includeWinners) {
        throw new UnsupportedOperationException();
    }

}
