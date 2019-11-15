package org.unidata.mdm.data.context;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Abstract from side request context.
 */
public abstract class AbstractRelationsFromRequestContext<T extends AbstractRelationToRequestContext>
    extends AbstractRecordIdentityContext
    implements RecordIdentityContext {
    /**
     * Constructor.
     * @param b the builder
     */
    protected AbstractRelationsFromRequestContext(AbstractRelationsFromRequestContextBuilder<?> b) {
        super(b);
    }
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8277274116336739520L;
    /**
     * Gets the To side relations.
     * @return map of relations
     */
    public abstract Map<String, List<T>> getRelations();
    /**
     * @author Mikhail Mikhailov
     *
     * @param <X> the concrete builder class
     */
    public abstract static class AbstractRelationsFromRequestContextBuilder<X extends AbstractRelationsFromRequestContextBuilder<X>>
        extends AbstractRecordIdentityContextBuilder<X> {
        /**
         * Constructor.
         */
        public AbstractRelationsFromRequestContextBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param other
         */
        public AbstractRelationsFromRequestContextBuilder(AbstractRelationsFromRequestContext<?> other) {
            super(other);
        }
    }
}
