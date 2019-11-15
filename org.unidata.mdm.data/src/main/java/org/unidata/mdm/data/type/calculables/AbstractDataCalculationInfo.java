package org.unidata.mdm.data.type.calculables;

import java.util.Collections;
import java.util.List;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.CalculationInfo;

/**
 * @author Mikhail Mikhailov
 * Base class for supplying calculation infos to the driver.
 */
public abstract class AbstractDataCalculationInfo<T extends Calculable> implements CalculationInfo<T> {
    /**
     * The versions to use for calculations.
     */
    private final List<CalculableHolder<T>> versions;
    /**
     * Include inactive versions into calculation or not.
     */
    private final boolean includeInactive;
    /**
     * Constructor.
     */
    public AbstractDataCalculationInfo(AbstractDataCalculationInfoBuilder<T, ?> b) {
        super();
        this.versions = b.versions;
        this.includeInactive = b.includeInactive;
    }
    /**
     * @return the versions
     */
    @Override
    public List<CalculableHolder<T>> getVersions() {
        return versions == null ? Collections.emptyList() : versions;
    }
    /**
     * @return the includeInactive
     */
    public boolean includeInactive() {
        return includeInactive;
    }
    /**
     * The builder for info.
     * @author Mikhail Mikhailov
     *
     * @param <T> calculable type
     * @param <X> self type
     */
    public abstract static class AbstractDataCalculationInfoBuilder<T extends Calculable, X extends AbstractDataCalculationInfoBuilder<T, X>> {
        /**
         * The versions to use for calculations.
         */
        private List<CalculableHolder<T>> versions;
        /**
         * Include inactive versions into calculation or not.
         */
        private boolean includeInactive;
        /**
         * Constructor.
         */
        protected AbstractDataCalculationInfoBuilder() {
            super();
        }
        /**
         * Sets the versions to the builder.
         * @param versions the versions to set
         * @return self
         */
        public X versions(List<CalculableHolder<T>> versions) {
            this.versions = versions;
            return self();
        }
        /**
         * Include inactive versions into calculation or not.
         * @param includeInactive the indicator
         * @return self
         */
        public X includeInactive(boolean includeInactive) {
            this.includeInactive = includeInactive;
            return self();
        }
        /**
         * 'Self' cast.
         * @return
         */
        @SuppressWarnings("unchecked")
        protected X self() {
            return (X) this;
        }
        /**
         * The build method.
         * @return info
         */
        public abstract AbstractDataCalculationInfo<T> build();
    }
}
