package org.unidata.mdm.data.type.calculables;

import java.util.Map;

import org.unidata.mdm.core.type.calculables.BvrCalculationInfo;
import org.unidata.mdm.core.type.calculables.Calculable;

/**
 * @author Mikhail Mikhailov
 * The BVR calculation info.
 */
public class DataBvrCaclulationInfo<T extends Calculable> extends AbstractDataCalculationInfo<T> implements BvrCalculationInfo<T> {
    /**
     * The BVR map.
     */
    private final Map<String, Integer> bvrMap;
    /**
     * Constructor.
     */
    public DataBvrCaclulationInfo(BvrCaclulationInfoBuilder<T> b) {
        super(b);
        this.bvrMap = b.bvrMap;
    }
    /**
     * @return the bvrMap
     */
    @Override
    public Map<String, Integer> getBvrMap() {
        return bvrMap;
    }
    /**
     * Builder method.
     * @param <C> calculable type
     * @return builder
     */
    public static<C extends Calculable> BvrCaclulationInfoBuilder<C> builder() {
        return new BvrCaclulationInfoBuilder<>();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder.
     *
     * @param <T> calculable type
     */
    public static class BvrCaclulationInfoBuilder<T extends Calculable>
        extends AbstractDataCalculationInfoBuilder<T, BvrCaclulationInfoBuilder<T>> {
        /**
         * The BVR map.
         */
        private Map<String, Integer> bvrMap;
        /**
         * Constructor.
         */
        protected BvrCaclulationInfoBuilder() {
            super();
        }
        /**
         * Sets BVR map.
         * @param bvrMap the map
         * @return self
         */
        public BvrCaclulationInfoBuilder<T> bvrMap(Map<String, Integer> bvrMap) {
            this.bvrMap = bvrMap;
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public DataBvrCaclulationInfo<T> build() {
            return new DataBvrCaclulationInfo<>(this);
        }
    }
}
