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

package org.unidata.mdm.core.type.monitoring.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.HazelcastInstanceProxy;
import com.hazelcast.internal.metrics.LongGauge;
import com.hazelcast.internal.metrics.MetricsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class HazelcastInternalsExporters {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastInternalsExporters.class);

    public HazelcastInternalsExporters(final HazelcastInstance hazelcastInstance) {
        final HazelcastInstanceProxy hazelcastInstanceProxy = (HazelcastInstanceProxy) hazelcastInstance;
        try {
            final Field original = HazelcastInstanceProxy.class.getDeclaredField("original");
            original.setAccessible(true);
            final HazelcastInstanceImpl hazelcastInstanceImpl = (HazelcastInstanceImpl) original.get(hazelcastInstance);
            final MetricsRegistry metricsRegistry = hazelcastInstanceImpl.node.nodeEngine.getMetricsRegistry();
            initGauges(metricsRegistry);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("Error getting hazelcast internals", e);
        }
    }

    private void initGauges(final MetricsRegistry metricsRegistry) {
        metricsRegistry.getNames().forEach(m -> {
            final LongGauge longGauge = metricsRegistry.newLongGauge(m);
            System.out.println(m + " = " + longGauge.read());
        });
//        final LongGauge longGauge = metricsRegistry.newLongGauge("os.totalSwapSpaceSize");
//        System.out.println();longGauge.read()
    }
}
