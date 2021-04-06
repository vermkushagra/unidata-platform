package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.cleanse.common.CleanseFunctionWrapper;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.Model;

/**
 * Cleanse functions parser. 
 * Search for cleanse functions in metamodel.
 * Initialize them and store initialized functions in the metamodel cache.
 * @author ilya.bykov
 */
public class CleanseParser implements ModelParser<CleanseFunctionWrapper> {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanseParser.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.unidata.mdm.backend.service.cache.ModelParser#parse(com.unidata
     * .mdm.meta.Model)
     */
    @Override
    public Map<String, CleanseFunctionWrapper> parse(Model model)  {
        Map<String, CleanseFunctionWrapper> result = new ConcurrentHashMap<String, CleanseFunctionWrapper>();
        LOGGER.info("Cleanse functions initializing...");
        if (model != null && model.getCleanseFunctions() != null && model.getCleanseFunctions().getGroup() != null) {
            try {
				parse(model.getCleanseFunctions().getGroup(), null, result);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new SystemRuntimeException(e, ExceptionId.EX_SYSTEM_CLEANSE_BASIC_INIT_FAILED);
			}
        }
        LOGGER.info("Finished.  Loaded {} cleanse functions.", result.size());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.unidata.mdm.backend.service.model.util.parsers.ModelParser#getValueType()
     */
    @Override
    public Class<CleanseFunctionWrapper> getValueType() {
        return CleanseFunctionWrapper.class;
    }

    /**
     * Recursive traverse cleanse functions tree.
     * </br>
     * If basic or custom cleanse function found:
     *  <ul>
     *     <li>Load cleanse function from classpath</li>
     *     <li>Initialize it with reflection.</li>
     *     <li>Store cleanse function instance in the memory cache.</li>
     *  </ul
     *  </br>
     * If composite cleanse function found:
     *  <ul>
     *     <li>Build cleanse function graph in the memory.</li>
     *     <li>Topologically sort it.</li>
     *     <li>Store in the memory cache.</li>
     *  </ul
     *
     * @param node
     *            tree node contains information about cleanse functions.
     *            </br>
     *            Could be: 
     *            <ul>
     *            <li>{@see CleanseFunctionGroupDef} - cleanse function group.</li>
     *            <li>{@see CleanseFunctionDef} - 'BASIC' or 'CUSTOM' cleanse function.</li>
     *            <li>{@see CompositeCleanseFunctionDef} - 'COMPOSITE' cleanse function.</li>
     *            </ul>
     * @param path
     *            Relative path to cleanse function
     * @param result
     *            Map used as an in memory cache
     * @throws InstantiationException
     *             the instantiation exception. {@see InstantiationException}
     * @throws IllegalAccessException
     *             the illegal access exception. {@see IllegalAccessException}
     * @throws ClassNotFoundException
     *             the class not found exception. {@see ClassNotFoundException}
     */
    private void parse(CleanseFunctionGroupDef node, String path, Map<String, CleanseFunctionWrapper> result)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // first execution path will be null
        if (path == null) {
            path = "";
        }
        List<?> list = node.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
        // iterate over tree nodes
        for (Object object : list) {
            // if cleanse function group found just add group name to path
            if (object instanceof CleanseFunctionGroupDef) {
                CleanseFunctionGroupDef group = (CleanseFunctionGroupDef) object;
                parse(group, path + "." + group.getGroupName(), result);
                // if cleanse function definition found:
                // add cleanse function to path and
                // initialize cleanse function instance

                // Composite cleanse function.
                // Initialize cleanse function graph, topologically sort it and
                // cache in memory
            } else if (object instanceof CompositeCleanseFunctionDef) {
                CompositeCleanseFunctionDef def = (CompositeCleanseFunctionDef) object;
                if (def.getLogic() != null) {
                    String id = path + "." + def.getFunctionName();
                    CleanseFunctionWrapper wrapper = new CleanseFunctionWrapper(def);
                    result.put(StringUtils.substring(id, id.indexOf(".") + 1, id.length()), wrapper);
                }
                // Common or custom cleanse function
                // Initialize cleanse function with reflection, cache instance
                // in
                // memory
            } else if (object instanceof CleanseFunctionDef) {
                CleanseFunctionDef def = (CleanseFunctionDef) object;
                if (def.getJavaClass() != null) {
                    String id = path + "." + def.getFunctionName();
                    CleanseFunctionWrapper wrapper = new CleanseFunctionWrapper(id, def);
                    result.put(StringUtils.substring(id, id.indexOf(".") + 1, id.length()), wrapper);
                }
            }
        }
    }
}