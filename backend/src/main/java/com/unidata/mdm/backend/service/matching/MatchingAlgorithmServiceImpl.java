package com.unidata.mdm.backend.service.matching;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.service.matching.algorithms.Algorithm;

@Service
public class MatchingAlgorithmServiceImpl implements MatchingAlgorithmService {

    /**
     * Map contains base matching algorithms.
     */
    @Resource(name = "algorithmMap")
    private Map<Integer, Algorithm> algorithmMap;

    @Nullable
    @Override
    public Algorithm getAlgorithmById(Integer id) {
        return algorithmMap.get(id);
    }

    @Nonnull
    @Override
    public Collection<Algorithm> getAllAlgorithms() {
        return algorithmMap.values();
    }
}
