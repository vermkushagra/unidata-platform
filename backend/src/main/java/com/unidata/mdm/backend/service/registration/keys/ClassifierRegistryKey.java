package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;

/**
 * Unique key help identify classifier in system, as a classifier
 */
public class ClassifierRegistryKey implements UniqueRegistryKey {

    /**
     * Classifier name
     */
    @Nonnull
    private final String classifierName;

    /**
     * Constructor
     *
     * @param classifierName - classifier name
     */
    public ClassifierRegistryKey(@Nonnull String classifierName) {
        this.classifierName = classifierName;
    }

    /**
     * @return classifier name
     */
    @Nonnull
    public String getClassifierName() {
        return classifierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassifierRegistryKey)) return false;

        ClassifierRegistryKey that = (ClassifierRegistryKey) o;

        return classifierName.equals(that.classifierName);
    }

    @Override
    public int hashCode() {
        return classifierName.hashCode();
    }

    /**
     * @return type of key
     */
    @Override
    public Type keyType() {
        return Type.CLASSIFIER;
    }

    @Override
    public String toString() {
        return "{" +
                "classifierName='" + classifierName + '\'' +
                '}';
    }
}
