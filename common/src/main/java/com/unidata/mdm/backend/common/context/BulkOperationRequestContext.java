/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.List;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;

/**
 * @author Mikhail Mikhailov
 *
 */
public class BulkOperationRequestContext extends CommonRequestContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5414491305127120715L;
    /**
     * Search by context.
     */
    private final transient ComplexSearchRequestContext applyBySearchContext;
    /**
     * Selected IDs.
     */
    private final List<String> applyBySelectedIds;

    /**
     * Entity name
     */
    private final String entityName;
    /**
     * Configuration.
     */
    private final transient BulkOperationConfiguration configuration;
    /**
     * Constructor.
     */
    private BulkOperationRequestContext(BulkOperationRequestContextBuilder b) {
        super();
        this.applyBySearchContext = b.applyBySearchContext;
        this.applyBySelectedIds = b.applyBySelectedIds;
        this.configuration = b.configuration;
        this.entityName = b.entityName;
    }

    /**
     * @return the applyBySearchContext
     */
    public ComplexSearchRequestContext getApplyBySearchContext() {
        return applyBySearchContext;
    }

    /**
     * @return the applyBySelectedIds
     */
    public List<String> getApplyBySelectedIds() {
        return applyBySelectedIds;
    }

    /**
     * @return the configuration
     */
    public BulkOperationConfiguration getConfiguration() {
        return configuration;
    }

    /**
     *
     * @return entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @author Mikhail Mikhailov
     * Bulk operation request context builder.
     */
    public static class BulkOperationRequestContextBuilder {
        /**
         * Search by context.
         */
        private ComplexSearchRequestContext applyBySearchContext;
        /**
         * Selected IDs.
         */
        private List<String> applyBySelectedIds;
        /**
         * Configuration.
         */
        private BulkOperationConfiguration configuration;

        /**
         * Entity name
         */
        private String entityName;
        /**
         * Constructor.
         */
        public BulkOperationRequestContextBuilder() {
            super();
        }
        /**
         * Sets search context.
         * @param ctx the context
         * @return self
         */
        public BulkOperationRequestContextBuilder applyBySearchContext(ComplexSearchRequestContext ctx) {
            this.applyBySearchContext = ctx;
            return this;
        }
        /**
         * Sets selected ids.
         * @param selectedIds the IDs
         * @return self
         */
        public BulkOperationRequestContextBuilder applyBySelectedIds(List<String> selectedIds) {
            this.applyBySelectedIds = selectedIds;
            return this;
        }
        /**
         * Sets selected ids.
         * @param selectedIds the IDs
         * @return self
         */
        public BulkOperationRequestContextBuilder configuration(BulkOperationConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * @param entityName entity name
         * @return self
         */
        public BulkOperationRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * Builder method.
         * @return context
         */
        public BulkOperationRequestContext build() {
            return new BulkOperationRequestContext(this);
        }
    }
}
