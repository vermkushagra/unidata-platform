/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 *
 */
public class BulkUpsertRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1381962967125886493L;
    /**
     * The bulk set.
     */
    private final List<UpsertRequestContext> bulkSet;
    /**
     * Constructor.
     * @param b builder instance
     */
    private BulkUpsertRequestContext(BulkUpsertRequestContextBuilder b) {
        super();
        this.bulkSet = b.bulkSet;

    }
    /**
     * @return the bulkSet
     */
    public List<UpsertRequestContext> getBulkSet() {
        return bulkSet;
    }

    /**
     * Checks if the bulk set is a valid one.
     * @return true if the bulk set valid, false otherwise
     */
    public boolean isBulkSet() {
        return bulkSet != null && !bulkSet.isEmpty();
    }

    /**
     * @author Mikhail Mikhailov
     * The builder class.
     */
    public static class BulkUpsertRequestContextBuilder {

        /**
         * The bulk set.
         */
        private List<UpsertRequestContext> bulkSet;

        /**
         * Constructor.
         */
        public BulkUpsertRequestContextBuilder() {
            super();
        }
        /**
         * Sets the bulk set to use.
         * @param bulkSet the bulk set
         * @return self
         */
        public BulkUpsertRequestContextBuilder bulkSet(List<UpsertRequestContext> bulkSet) {
            this.bulkSet = bulkSet;
            return this;
        }
        /**
         * Builder method.
         * @return new context
         */
        public BulkUpsertRequestContext build() {
            return new BulkUpsertRequestContext(this);
        }
    }
}
