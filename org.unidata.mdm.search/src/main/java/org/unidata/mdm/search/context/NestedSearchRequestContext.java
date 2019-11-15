package org.unidata.mdm.search.context;

/**
 * @author Dmitry Kopin on 02.04.2018.
 */
public class NestedSearchRequestContext  {
    /**
     * @author Mikhail Mikhailov
     * Type of the nested search, supported by this context.
     */
    public enum NestedSearchType {
        /**
         * Has child joined queries.
         */
        HAS_CHILD,
        /**
         * Has parent joined queries.
         */
        HAS_PARENT,
        /**
         * Nested objects withing the same type.
         */
        NESTED_OBJECTS
    }

    private final SearchRequestContext nestedSearch;

    private final Integer minDocCount;

    private final NestedSearchType nestedSearchType;

    private final String nestedQueryName;

    private final boolean positive;

    public NestedSearchRequestContext(NestedSearchRequestContextBuilder builder){
        this.nestedSearch = builder.nestedSearch;
        this.minDocCount = builder.minDocCount;
        this.nestedSearchType = builder.nestedSearchType;
        this.nestedQueryName = builder.nestedQueryName;
        this.positive = builder.positive;
    }

    public static NestedSearchRequestContextBuilder builder(SearchRequestContext nestedSearch){
        NestedSearchRequestContextBuilder builder = new NestedSearchRequestContextBuilder();
        builder.nestedSearch = nestedSearch;
        return builder;
    }

    public SearchRequestContext getNestedSearch() {
        return nestedSearch;
    }

    /**
     * @return the nestedSearchType
     */
    public NestedSearchType getNestedSearchType() {
        return nestedSearchType;
    }

    /**
     * @return the nestedQueryName
     */
    public String getNestedQueryName() {
        return nestedQueryName;
    }

    public Integer getMinDocCount() {
        return minDocCount;
    }

    public boolean isPositive() {
        return positive;
    }

    public static class NestedSearchRequestContextBuilder{

        private SearchRequestContext nestedSearch;

        private Integer minDocCount;

        private NestedSearchType nestedSearchType;

        private String nestedQueryName;

        private boolean positive = true;

        private NestedSearchRequestContextBuilder(){
            super();
        }

        public NestedSearchRequestContext build(){
            return new NestedSearchRequestContext(this);
        }

        public NestedSearchRequestContextBuilder nestedSearchType(NestedSearchType nestedSearchType) {
            this.nestedSearchType = nestedSearchType;
            return this;
        }

        public NestedSearchRequestContextBuilder nestedQueryName(String nestedQueryName) {
            this.nestedQueryName = nestedQueryName;
            return this;
        }

        public NestedSearchRequestContextBuilder minDocCount(Integer minDocCount){
            this.minDocCount = minDocCount;
            return this;
        }

        public NestedSearchRequestContextBuilder positive(boolean positive){
            this.positive = positive;
            return this;
        }
    }
}
