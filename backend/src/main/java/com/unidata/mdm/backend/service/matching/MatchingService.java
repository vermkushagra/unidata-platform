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
    Collection<Cluster> construct(@Nonnull EtalonRecord etalonRecord, @Nonnull Date date);

    @NotNull
    Collection<Cluster> construct(@Nonnull EtalonRecord etalon, @Nonnull Date date, Integer groupId, Integer ruleId);

    @Nonnull
    Collection<Cluster> constructAutoMerge(@Nonnull EtalonRecord etalonRecord, @Nonnull Date date);

    @Nonnull
    Collection<Cluster> construct(@Nonnull String etalonId, @Nonnull Date date);
}
