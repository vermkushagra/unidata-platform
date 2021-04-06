package com.unidata.mdm.backend.service.classifier;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * It is a wrapper over list, but with idea that elements of list are sequence elements of an abstract tree.
 *
 * @param <T>
 */
public class Branch<T> {

    /**
     * Sequence elements which represent branch of tree.
     */
    @Nonnull
    private final List<T> path;

    /**
     * Is from root branch, or from leaf
     */
    private boolean direction;

    public Branch(@Nonnull List<T> path, boolean fromRoot) {
        this.path = path;
        this.direction = fromRoot;
    }

    @Nonnull
    public List<T> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * Change branch direction
     */
    public Branch<T> reverse() {
        Collections.reverse(path);
        direction = !direction;
        return this;
    }

    /**
     * @return direction of branch. true if the first element of path is root element
     */
    public boolean isToRoot() {
        return !direction;
    }

    /**
     * @return direction of branch. true if the first element of path is leaf element
     */
    public boolean isFromRoot() {
        return direction;
    }
}
