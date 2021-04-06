package com.unidata.mdm.backend.util.reports;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.ReportInfoHolder;

/**
 * Extract element from info holder
 *
 * @param <R> return type of value (can be Object)
 * @param <I> generalized info holder
 */
public interface ElementExtractor<R, I extends ReportInfoHolder> {

    /**
     * @param infoHolder - info holder
     * @return element
     */
    @Nonnull
    R getElement(I infoHolder);
}
