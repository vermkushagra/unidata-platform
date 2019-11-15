package org.unidata.mdm.search.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.ManagedIndexId;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Indexing request.
 */
public class IndexRequestContext extends CommonRequestContext implements TypedSearchContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7544779812424219448L;
    /**
     * The storage id to use. Overrides the system one.
     */
    private final String storageId;
    /**
     * Entity name (name of the index).
     */
    private final String entity;
    /**
     * Routing (usually etalon id).
     */
    private final String routing;
    /**
     * Drop and recreate or update.
     */
    private final boolean drop;
    /**
     * Refresh result of indexing or not.
     */
    private final boolean refresh;
    /**
     * Delete IDs collection
     */
    private final transient Map<IndexType, List<ManagedIndexId>> delete;
    /**
     * Indexing objects collection.
     */
    private final transient Map<IndexType, List<Indexing>> index;
    /**
     * Update objects collection.
     */
    private final transient Map<IndexType, List<Indexing>> update;
    /**
     * Constructor.
     */
    private IndexRequestContext(IndexRequestContextBuilder b) {
        super(b);
        this.storageId = b.storageId;
        this.entity = b.entity;
        this.routing = b.routing;
        this.drop = b.drop;
        this.refresh = b.refresh;
        this.delete = MapUtils.isNotEmpty(b.delete) ? b.delete : Collections.emptyMap();
        this.update = MapUtils.isNotEmpty(b.update) ? b.update : Collections.emptyMap();
        this.index = MapUtils.isNotEmpty(b.index) ? b.index : Collections.emptyMap();
    }

    /**
     * @return the storageId
     */
    @Override
    public String getStorageId() {
        return storageId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntity() {
        return entity;
    }

    /**
     * @return the routing
     */
    public String getRouting() {
        return routing;
    }
    /**
     * @return the drop
     */
    public boolean isDrop() {
        return drop;
    }
    /**
     * @return the refresh
     */
    public boolean isRefresh() {
        return refresh;
    }
    /**
     * Tells, whether this builder has collected some updates.
     * @return true if has some, false otherwise
     */
    public boolean hasUpdates() {
        return MapUtils.isNotEmpty(delete) || MapUtils.isNotEmpty(update) || MapUtils.isNotEmpty(index);
    }
    /**
     * Gets delete payload.
     * @return delete IDs
     */
    public Map<IndexType, List<ManagedIndexId>> getDeletes() {
        return delete;
    }
    /**
     * Gets updates.
     * @return update objects
     */
    public Map<IndexType, List<Indexing>> getUpdates() {
        return update;
    }
    /**
     * Gets index objects.
     * @return index objects
     */
    public Map<IndexType, List<Indexing>> getIndex() {
        return index;
    }
    /**
     * Builder object.
     * @return builder
     */
    public static IndexRequestContextBuilder builder() {
        return new IndexRequestContextBuilder();
    }
    /**
     * Copy builder.
     * @param idx the context to copy
     * @return builder
     */
    public static IndexRequestContextBuilder builder(IndexRequestContext idx) {
        return new IndexRequestContextBuilder(idx);
    }
    /**
     * Context builder.
     * @author Mikhail Mikhailov
     */
    public static class IndexRequestContextBuilder extends CommonRequestContextBuilder<IndexRequestContextBuilder> {
        /**
         * The storage id to use. Overrides the system one.
         */
        private String storageId;
        /**
         * Type to operate on.
         */
        private String entity;
        /**
         * Routing (usually etalon id).
         */
        private String routing;
        /**
         * Drop and recreate or update.
         */
        private boolean drop;
        /**
         * Refresh result of indexing or not.
         */
        private boolean refresh = true;
        /**
         * Delete IDs collection
         */
        private Map<IndexType, List<ManagedIndexId>> delete = new HashMap<>();
        /**
         * Indexing objects collection.
         */
        private Map<IndexType, List<Indexing>> index = new HashMap<>();
        /**
         * Update objects collection.
         */
        private Map<IndexType, List<Indexing>> update = new HashMap<>();
        /**
         * Constructor.
         */
        private IndexRequestContextBuilder() {
            super();
        }

        public IndexRequestContextBuilder(final IndexRequestContext idx) {
            super();
            storageId = idx.storageId;
            entity = idx.entity;
            routing = idx.routing;
            drop = idx.drop;
            refresh = idx.refresh;
            delete = idx.delete;
            index = idx.index;
            update = idx.update;
        }

        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public IndexRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }
        /**
         * @param entityName - entity name
         * @return self
         */
        public IndexRequestContextBuilder entity(String entityName){
            this.entity = entityName;
            return this;
        }
        /**
         * @param routing - routing
         * @return self
         */
        public IndexRequestContextBuilder routing(String routing){
            this.routing = routing;
            return this;
        }
        /**
         * Drop and recreate or update.
         * @param drop drop or update
         * @return self
         */
        public IndexRequestContextBuilder drop(boolean drop) {
            this.drop = drop;
            return this;
        }

        /**
         * Drop and recreate or update.
         * @param refresh drop or update
         * @return self
         */
        public IndexRequestContextBuilder refresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }

        /**
         * Adds ids to delete
         * @param ids the ids
         * @return self
         */
        public IndexRequestContextBuilder delete(ManagedIndexId... ids) {
            if (ArrayUtils.isNotEmpty(ids)) {
                for (int i = 0; i < ids.length; i++) {
                    if (Objects.isNull(ids[i]) || Objects.isNull(ids[i].getSearchType())) {
                        continue;
                    }

                    delete.computeIfAbsent(ids[i].getSearchType(), key -> new ArrayList<ManagedIndexId>()).add(ids[i]);
                }

            }
            return this;
        }
        /**
         * Adds ids to delete
         * @param ids the ids
         * @return self
         */
        public IndexRequestContextBuilder delete(Collection<ManagedIndexId> ids) {
            if (CollectionUtils.isNotEmpty(ids)) {
                for (ManagedIndexId id : ids) {
                    if (Objects.isNull(id) || Objects.isNull(id.getSearchType())) {
                        continue;
                    }

                    delete.computeIfAbsent(id.getSearchType(), key -> new ArrayList<ManagedIndexId>()).add(id);
                }

            }
            return this;
        }
        /**
         * Adds objects to index
         * @param ixs the objects
         * @return self
         */
        public IndexRequestContextBuilder index(Indexing... ixs) {
            if (ArrayUtils.isNotEmpty(ixs)) {
                for (int i = 0; i < ixs.length; i++) {
                    if (Objects.isNull(ixs[i]) || Objects.isNull(ixs[i].getIndexType())) {
                        continue;
                    }

                    index.computeIfAbsent(ixs[i].getIndexType(), key -> new ArrayList<Indexing>()).add(ixs[i]);
                }

            }
            return this;
        }
        /**
         * Adds objects to index
         * @param ixs the objects
         * @return self
         */
        public IndexRequestContextBuilder index(Collection<Indexing> ixs) {
            if (CollectionUtils.isNotEmpty(ixs)) {
                for (Indexing id : ixs) {
                    if (Objects.isNull(id) || Objects.isNull(id.getIndexType())) {
                        continue;
                    }

                    index.computeIfAbsent(id.getIndexType(), key -> new ArrayList<Indexing>()).add(id);
                }

            }
            return this;
        }
        /**
         * Adds objects to update.
         * @param ups the objects
         * @return self
         */
        public IndexRequestContextBuilder update(Indexing... ups) {
            if (ArrayUtils.isNotEmpty(ups)) {
                for (int i = 0; i < ups.length; i++) {
                    if (Objects.isNull(ups[i]) || Objects.isNull(ups[i].getIndexType())) {
                        continue;
                    }

                    index.computeIfAbsent(ups[i].getIndexType(), key -> new ArrayList<Indexing>()).add(ups[i]);
                }

            }
            return this;
        }
        /**
         * Adds objects to update.
         * @param ups the objects
         * @return self
         */
        public IndexRequestContextBuilder update(Collection<Indexing> ups) {
            if (CollectionUtils.isNotEmpty(ups)) {
                for (Indexing id : ups) {
                    if (Objects.isNull(id) || Objects.isNull(id.getIndexType())) {
                        continue;
                    }

                    index.computeIfAbsent(id.getIndexType(), key -> new ArrayList<Indexing>()).add(id);
                }

            }
            return this;
        }
        /**
         * Tells, whether this builder has collected some updates.
         * @return true if has some, false otherwise
         */
        public boolean hasUpdates() {
            return MapUtils.isNotEmpty(delete) || MapUtils.isNotEmpty(update) || MapUtils.isNotEmpty(index);
        }
        /**
         * Builds context.
         * @return context
         */
        @Override
        public IndexRequestContext build() {
            return new IndexRequestContext(this);
        }
    }
}
