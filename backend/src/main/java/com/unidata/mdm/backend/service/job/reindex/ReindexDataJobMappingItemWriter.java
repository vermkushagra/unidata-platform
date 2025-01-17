package com.unidata.mdm.backend.service.job.reindex;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Simple update data mapping writer.
 */
public class ReindexDataJobMappingItemWriter implements ItemWriter<String> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobMappingItemWriter.class);
    /**
     * Drop or not.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_CLEAN_INDEXES + "] ?: false}")
    private boolean forceCleanIndexes;
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
    public ReindexDataJobMappingItemWriter() {
        super();
    }

    @Override
    public void write(List<? extends String> items) throws Exception {

        for (String entityName : items) {

            LOGGER.info("Processing {}.", entityName);
            if (forceCleanIndexes && searchService.indexExists(entityName, SecurityUtils.getCurrentUserStorageId())) {

                LOGGER.info("Executing drop/create for {}.", entityName);
                try {
                    searchService.closeIndex(entityName, SecurityUtils.getCurrentUserStorageId());
                    searchService.dropIndex(entityName, SecurityUtils.getCurrentUserStorageId());
                } catch (Exception e) {
                    LOGGER.warn("Drop/create failed.", e);
                }
            }

            boolean success = false;
            if (metaModelService.isLookupEntity(entityName)) {

                success = searchService.updateLookupEntityMapping(
                        metaModelService.getLookupEntityById(entityName),
                        SecurityUtils.getCurrentUserStorageId());
            } else {

                GetEntityDTO entityById = metaModelService.getEntityById(entityName);
                success = searchService.updateEntityMapping(
                        entityById.getEntity(),
                        entityById.getRefs(),
                        SecurityUtils.getCurrentUserStorageId());
                if (CollectionUtils.isNotEmpty(entityById.getRelations())) {
                    for (RelationDef def : entityById.getRelations()) {
                        success &= searchService.updateRelationMapping(def, SecurityUtils.getCurrentUserStorageId());
                    }
                }
            }

            if (!success) {
                LOGGER.error("Cannot map type {} to index. Mapping failed. Skipping.", entityName);
            }
        }
    }
}
