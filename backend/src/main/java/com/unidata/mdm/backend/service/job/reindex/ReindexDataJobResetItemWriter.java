package com.unidata.mdm.backend.service.job.reindex;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 * Simple update data mapping writer.
 */
public class ReindexDataJobResetItemWriter implements ItemWriter<String> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobResetItemWriter.class);
    /**
     * Prepare per index params.
     * ("index.refresh_interval", "1s"); // Enable refresh
     * ("index.warmer.enabled", Boolean.TRUE); // Enable warmers
     */
    private static final Map<String, Object> RESET_INDEX_PARAMS = Collections.singletonMap("index.refresh_interval", "1s");
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Meta model service
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public ReindexDataJobResetItemWriter() {
        super();
    }

    @Override
    public void write(List<? extends String> items) throws Exception {

        for (String entityName : items) {

            boolean isEntity = metaModelService.isEntity(entityName);
            LOGGER.info(isEntity
                    ? "Re-Setting bulk-optimized options to index for {} (entity)."
                    : "Not re-setting bulk-optimized options to index for {} (lookup).", entityName);
            if (isEntity) {
                // searchService.closeIndex(entity, SecurityUtils.getCurrentUserStorageId());
                searchService.setIndexSettings(entityName, SecurityUtils.getCurrentUserStorageId(), RESET_INDEX_PARAMS);
                // searchService.openIndex(entity, SecurityUtils.getCurrentUserStorageId());
                searchService.refreshIndex(entityName, SecurityUtils.getCurrentUserStorageId(), true);
            }
        }
    }
}
