package org.unidata.mdm.system.type.module;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.unidata.mdm.system.type.batch.BatchSetPostProcessor;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.rendering.RenderingAction;
import org.unidata.mdm.system.type.rendering.RenderingResolver;

/**
 * @author Mikhail Mikhailov
 * Module contract.
 */
public interface Module {
    /**
     * Gets module ID, i. e. 'org.unidata.mdm.core'.
     * @return ID
     */
    String getId();
    /**
     * Gets module version, consisting of major.minor.rev, i. e. '5.4.3'.
     * @return version
     */
    String getVersion();
    /**
     * Gets module localized name, 'Unidata Core'.
     * @return name
     */
    String getName();
    /**
     * Gets module localized description, i. e. 'This outstanding module is for all the good things on earth...'.
     * @return description
     */
    String getDescription();
    /**
     * Gets module 'tag', i. e. 'super_fast_edition'.
     * @return module tag
     */
    @Nullable
    default String getTag() {
        return null;
    }

    default Collection<Dependency> getDependencies() {
        return Collections.emptyList();
    }

    /**
     * Gets the exported pipeline start types. May be empty.
     * @return collection of pipeline start types
     */
    default Collection<Start<PipelineInput>> getStartTypes() {
        return Collections.emptyList();
    }
    /**
     * Gets the exported pipeline point types. May be empty.
     * @return collection of pipeline point types
     */
    default Collection<Point<PipelineInput>> getPointTypes() {
        return Collections.emptyList();
    }
    /**
     * Gets the exported pipeline connector types. May be empty.
     * @return collection of pipeline connector types
     */
    default Collection<Connector<PipelineInput, PipelineOutput>> getConnectorTypes() {
        return Collections.emptyList();
    }
    /**
     * Get the modules fallbacks
     * @return collection of fallbacks
     */
    default Collection<Fallback<PipelineInput>> getFallbacks() {
        return Collections.emptyList();
    }
    /**
     * Gets the exported pipeline finish types. May be empty.
     * @return collection of pipeline finish types
     */
    default Collection<Finish<PipelineInput, PipelineOutput>> getFinishTypes() {
        return Collections.emptyList();
    }
    /**
     * Gets resource bundle basenames - i. e. 'my_resources' for localized resources
     * such as (my_resources_en, my_resources_fr, my_resources_fi).
     * Resources may be java or XML properties.
     * Files must be in classpath.
     * Property names should be prefixed with MODULE_ID, i. e. (for 'org.unidata.mdm.system'
     * org.unidata.mdm.system.information.message = The weather is nice today!)
     * @return array of resource bundle base names
     */
    default String[] getResourceBundleBasenames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    /**
     * Gets the rendering resolver for this module or null, if none defined.
     * @return rendering resolver or null
     */
    @Nullable
    default RenderingResolver getRenderingResolver() {
        return null;
    }
    /**
     * Gets the module's exported rendering actions.
     * @return collection of actions
     */
    default Collection<RenderingAction> getRenderingActions() {
        return Collections.emptyList();
    }
    /**
     * Gets a collection batch set post processors, which can be used in jobs.
     * @return collection of post-processors
     */
    @SuppressWarnings("rawtypes")
    default Collection<Class<? extends BatchSetPostProcessor>> getBatchSetPostProcessors() {
        return Collections.emptyList();
    }
    /**
     * Gets configuration properties, exported by this module.
     * @return configuration properties
     */
    default ApplicationConfigurationProperty[] getConfigurationProperties() {
        return new ApplicationConfigurationProperty[0];
    }
    /**
     * Runs module's install/upgrade procedure.
     * Can be used to init / mgirate DB schema or other similar tasks.
     */
    default void install() {
        // Override
    }
    /**
     * Runs module's uninstall procedure.
     * Can be used to drop schema or similar tasks.
     */
    default void uninstall() {
        // Override
    }

    /**
     * Runs module's start procedure. Happens upon each application startup.
     */
    default void start() {
        // Override
    }

    /**
     * Runs after all modules execute method start in same order as dependencies
     */
    default void ready() {
        // Override
    }

    /**
     * Runs module's stop procedure. Happens upon each application shutdown.
     */
    default void stop() {
        // Override
    }
}
