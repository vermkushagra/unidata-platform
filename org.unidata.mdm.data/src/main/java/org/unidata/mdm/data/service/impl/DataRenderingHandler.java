package org.unidata.mdm.data.service.impl;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.type.rendering.BulkDataRecalculationInputSource;
import org.unidata.mdm.data.type.rendering.DataRenderingAction;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentCollector;
import org.unidata.mdm.system.type.rendering.InputFragmentRenderer;
import org.unidata.mdm.system.type.rendering.InputRenderingAction;
import org.unidata.mdm.system.type.rendering.OutputFragmentRenderer;
import org.unidata.mdm.system.type.rendering.OutputRenderingAction;
import org.unidata.mdm.system.type.rendering.RenderingResolver;

/**
 * @author Mikhail Mikhailov on Jan 16, 2020
 */
@Component
public class DataRenderingHandler implements RenderingResolver {

    @Override
    public InputFragmentRenderer get(InputRenderingAction action) {

        if (DataRenderingAction.BULK_DATA_REINDEX_ACTION == action) {
            return (c, s) -> renderRecalculate(c, (BulkDataRecalculationInputSource) s);
        }

        return null;
    }

    @Override
    public OutputFragmentRenderer get(OutputRenderingAction action) {
        // TODO Auto-generated method stub
        return null;
    }
    /*
     * Normally, it is used by reindex job.
     * We add relations stuff here.
     * action is "RENDER_RECALCULATE" now
     */
    private void renderRecalculate(InputFragmentCollector<?> collector, BulkDataRecalculationInputSource source) {
        // collector.fragment();
    }
}
