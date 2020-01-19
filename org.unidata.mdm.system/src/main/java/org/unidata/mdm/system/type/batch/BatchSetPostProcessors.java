package org.unidata.mdm.system.type.batch;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Mikhail Mikhailov on Jan 18, 2020
 */
@SuppressWarnings("rawtypes")
public class BatchSetPostProcessors implements Iterable<BatchSetPostProcessor> {

    private final List<BatchSetPostProcessor> postProcessors;

    public BatchSetPostProcessors(List<BatchSetPostProcessor> postProcessors) {
        this.postProcessors = CollectionUtils.isEmpty(postProcessors) ? Collections.emptyList() : postProcessors;
    }

    @Override
    public Iterator<BatchSetPostProcessor> iterator() {
        return postProcessors.iterator();
    }
}
