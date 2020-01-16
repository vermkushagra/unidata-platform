/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.data.type.calculables;

import java.util.Map;

import org.unidata.mdm.core.type.calculables.BvtCalculationInfo;
import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.model.AttributeModelElement;

/**
 * @author Mikhail Mikhailov
 * The BVT calculation info.
 */
public class DataBvtCaclulationInfo<T extends Calculable> extends AbstractDataCalculationInfo<T> implements BvtCalculationInfo<T> {
    /**
     * The BVT map.
     */
    private final Map<String, Map<String, Integer>> bvtMap;
    /**
     * The attributes map.
     */
    private final Map<String, AttributeModelElement> attrsMap;
    /**
     * If true, creates special extended record as a result and includes winner attributes into it.
     */
    private final boolean includeWinners;
    /**
     * Constructor.
     */
    public DataBvtCaclulationInfo(BvtCaclulationInfoBuilder<T> b) {
        super(b);
        this.bvtMap = b.bvtMap;
        this.attrsMap = b.attrsMap;
        this.includeWinners = b.includeWinners;
    }
    /**
     * @return the bvtMap
     */
    @Override
    public Map<String, Map<String, Integer>> getBvtMap() {
        return bvtMap;
    }
    /**
     * @return the attrsMap
     */
    public Map<String, AttributeModelElement> getAttrsMap() {
        return attrsMap;
    }
    /**
     * @return the includeWinners
     */
    public boolean includeWinners() {
        return includeWinners;
    }
    /**
     * Builder method.
     * @param <C> calculable type
     * @return builder
     */
    public static<C extends Calculable> BvtCaclulationInfoBuilder<C> builder() {
        return new BvtCaclulationInfoBuilder<>();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder.
     *
     * @param <T> calculable type
     */
    public static class BvtCaclulationInfoBuilder<T extends Calculable>
        extends AbstractDataCalculationInfoBuilder<T, BvtCaclulationInfoBuilder<T>> {
        /**
         * The BVT map.
         */
        private Map<String, Map<String, Integer>> bvtMap;
        /**
         * Attributes map.
         */
        private Map<String, AttributeModelElement> attrsMap;
        /**
         * If true, creates special extended record as a result and includes winner attributes into it.
         */
        private boolean includeWinners;
        /**
         * Constructor.
         */
        protected BvtCaclulationInfoBuilder() {
            super();
        }
        /**
         * Sets BVT map.
         * @param bvtMap the map
         * @return self
         */
        public BvtCaclulationInfoBuilder<T> bvtMap(Map<String, Map<String, Integer>> bvtMap) {
            this.bvtMap = bvtMap;
            return self();
        }
        /**
         * Sets attributes map.
         * @param attrsMap the map
         * @return self
         */
        public BvtCaclulationInfoBuilder<T> attrsMap(Map<String, AttributeModelElement> attrsMap) {
            this.attrsMap = attrsMap;
            return self();
        }
        /**
         * If true, creates special extended record as a result and includes winner attributes into it.
         * @param includeWinners the flag
         * @return self
         */
        public BvtCaclulationInfoBuilder<T> includeWinners(boolean includeWinners) {
            this.includeWinners = includeWinners;
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public DataBvtCaclulationInfo<T> build() {
            return new DataBvtCaclulationInfo<>(this);
        }
    }
}
