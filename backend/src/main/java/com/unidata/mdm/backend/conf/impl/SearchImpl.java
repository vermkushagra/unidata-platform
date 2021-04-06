package com.unidata.mdm.backend.conf.impl;

import com.unidata.mdm.backend.common.integration.exits.SearchListener;
import com.unidata.mdm.conf.Search;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Kopin on 19.09.2017.
 */
public class SearchImpl extends Search {

    private final Map<String, SearchListener> beforeSearchInstances = new HashMap<>();

    private final Map<String, SearchListener> afterSearchInstances = new HashMap<>();

    public Map<String, SearchListener> getBeforeSearchInstances() {
        return beforeSearchInstances;
    }

    public Map<String, SearchListener> getAfterSearchInstances() {
        return afterSearchInstances;
    }

}
