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
