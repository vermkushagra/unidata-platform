package com.unidata.mdm.backend.service.job.reindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Mikhail Mikhailov
 * Update data mapping item reader.
 */
@StepScope
public class ReindexDataJobMappingItemReader implements ItemReader<String> {
    /**
     * Entity names.
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_ENTITY_NAME + "]}")
    private String entityNamesAsString;
    /**
     * Work of this reader.
     */
    private List<String> work = new ArrayList<>();
    /**
     * Constructor.
     */
    public ReindexDataJobMappingItemReader() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String read() throws Exception {

        if (work.isEmpty() && StringUtils.isNotBlank(entityNamesAsString)) {
            work.addAll(Arrays.asList(StringUtils.split(entityNamesAsString, "|")));
        }

        if (!work.isEmpty()) {

            String retval = work.remove(work.size() - 1);
            if (work.isEmpty()) {
                entityNamesAsString = null;
            }

            return retval;
        }

        return null;
    }

}
