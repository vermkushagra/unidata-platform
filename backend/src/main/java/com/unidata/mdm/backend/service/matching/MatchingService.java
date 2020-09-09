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

package com.unidata.mdm.backend.service.matching;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.types.EtalonRecord;

/**
 * Service which match records
 */
public interface MatchingService {

    /**
     * Does construct cluster/block info for given etalon record, according to metadata, defined via rules/groups.
     *
     * @param etalonRecord - record
     * @param date         - asOf
     * @return collection of clusters
     */
    @Nonnull
    Collection<Cluster> constructPreprocessing(@Nonnull EtalonRecord etalonRecord, @Nonnull Date date);

    @NotNull
    Collection<Cluster> construct(@Nonnull EtalonRecord etalon, @Nonnull Date date, Integer ruleId);

    @Nonnull
    Collection<Cluster> constructAutoMerge(@Nonnull EtalonRecord etalonRecord, @Nonnull Date date);

    @Nonnull
    Collection<Cluster> constructPreprocessing(@Nonnull String etalonId, @Nonnull Date date);

}
