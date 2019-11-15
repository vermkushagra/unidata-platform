package org.unidata.mdm.meta.type.parse;

import java.util.Map;

import org.unidata.mdm.core.type.model.ModelElement;
import org.unidata.mdm.meta.Model;

/**
 * The Interface ModelParser.
 *
 * @param <V>
 *            the value type
 *            @author ilya.bykov
 */
public interface ModelParser<V extends ModelElement> {

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
