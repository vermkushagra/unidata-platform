package org.unidata.mdm.system.type.configuration;

import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Alexander Malyshev
 */
public interface ApplicationConfigurationProperty {
    String getKey();
    String getGroupKey();
    ConfigurationPropertyType getPropertyType();
    Predicate<Optional<String>> getValidator();
    Function<String, ? extends Serializable> getDeserializer();
    Optional<? extends Serializable> getDefaultValue();
    List<Pair<? extends Serializable, String>> getAvailableValues();
    boolean isRequired();
    boolean isReadonly();
}
