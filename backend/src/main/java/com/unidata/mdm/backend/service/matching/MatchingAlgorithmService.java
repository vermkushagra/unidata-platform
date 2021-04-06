package com.unidata.mdm.backend.service.matching;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

import com.unidata.mdm.backend.service.matching.algorithms.Algorithm;

/**
 * Responsible for working with algorithms
 */
public interface MatchingAlgorithmService {

    /**
     * @param id - id of algorithm
     * @return algorithm
     */
    @Nullable
    Algorithm getAlgorithmById(Integer id);

    /**
     * @return collection of algorithms
     */
    @Nonnull
    Collection<Algorithm> getAllAlgorithms();

}
