package org.unidata.mdm.system.type.module;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nullable;

import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

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

    default ApplicationConfigurationProperty[] configurationProperties() {
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
     * Runs module's stop procedure. Happens upon each application shutdown.
     */
    default void stop() {
        // Override
    }
    /**
     * Gets the exported pipeline start types. May be empty.
     * @return collection of pipeline start types
     */
    default Collection<Start<PipelineExecutionContext>> getStartTypes() {
        return Collections.emptyList();
    }
    /**
     * Gets the exported pipeline point types. May be empty.
     * @return collection of pipeline point types
     */
    default Collection<Point<PipelineExecutionContext>> getPointTypes() {
        return Collections.emptyList();
    }
    /**
     * Gets the exported pipeline connector types. May be empty.
     * @return collection of pipeline connector types
     */
    default Collection<Connector<PipelineExecutionContext, PipelineExecutionResult>> getConnectorTypes() {
        return Collections.emptyList();
    }

    /**
     * Get the modules fallbacks
     * @return collection of fallbacks
     */
    default Collection<Fallback<PipelineExecutionContext>> getFallbacks() {
        return Collections.emptyList();
    }

    /**
     * Gets the exported pipeline finish types. May be empty.
     * @return collection of pipeline finish types
     */
    default Collection<Finish<PipelineExecutionContext, PipelineExecutionResult>> getFinishTypes() {
        return Collections.emptyList();
    }
}
