package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.Map;

import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;
import com.unidata.mdm.meta.Model;

/**
 * The Interface ModelParser.
 *
 * @param <V>
 *            the value type
 *            @author ilya.bykov
 */
public interface ModelParser<V extends ValueWrapper> {

    /**
     * Parse meta model to Map<String, V>.
     *
     * @param model
     *            meta model {@link Model}
     * @return result of parsing.
     * @throws Exception
     *             the exception
     */
    Map<String, V> parse(Model model);

    /**
     * Gets the value class.
     *
     * @return the value class
     */
    Class<V> getValueType();
}
