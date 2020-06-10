/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.common.upath;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 * UPath object.
 */
public class UPath {
    /**
     * Empty path.
     */
    private static final String EMPTY = "";
    /**
     * Elements.
     */
    private final List<UPathElement> elements = new ArrayList<>();
    /**
     * Entity name.
     */
    private final String entity;
    /**
     * Constructor.
     * @param entity the entity name
     */
    public UPath(String entity) {
        super();
        this.entity = entity;
    }
    /**
     * @return the elements
     */
    public List<UPathElement> getElements() {
        return elements;
    }
    /**
     * @return the segments
     */
    public List<UPathElement> getSegments() {
        List<UPathElement> view = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {

            UPathElement e = elements.get(i);
            if (e.getType() != UPathElementType.COLLECTING) {
                continue;
            }

            view.add(e);
        }

        return view;
    }
    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }
    /**
     * Gets the size (i. e. number of path segments) of this UPath.
     * @return number of segments
     */
    public int getNumberOfSegments() {

        int result = 0;
        for (int i = 0; i < elements.size(); i++) {

            UPathElement e = elements.get(i);
            if (e.getType() != UPathElementType.COLLECTING) {
                continue;
            }

            result++;
        }

        return result;
    }
    /**
     * Gets the last {@link UPathElement} or null, if empty.
     * @return last element
     */
    public UPathElement getTail() {
        return elements.isEmpty() ? null : elements.get(elements.size() - 1);
    }
    /**
     * Gets the sub segments UPath starting from segment index 'from'.
     * @param from the segment index to start
     * @return UPath
     */
    public UPath getSubSegmentsUPath(int from) {

        if (from < 0) {
            throw new ArrayIndexOutOfBoundsException("'From' segments index for UPath subtraction out of bounds.");
        }

        int done = 0;
        for (int i = 0; i < elements.size(); i++) {

            UPathElement e = elements.get(i);
            if (e.getType() != UPathElementType.COLLECTING) {
                continue;
            }

            if (done++ == from) {
                UPath newPath = new UPath(this.entity);
                newPath.elements.addAll(this.elements.subList(i, this.elements.size()));
                return newPath;
            }
        }

        throw new ArrayIndexOutOfBoundsException("'From' segments index for UPath subtraction out of bounds.");
    }
    /**
     * Tells whether this upath denotes the root element.
     * @return true, if so, false otherwise
     */
    public boolean isRoot() {
        return elements.size() == 1 && UPathConstants.UPATH_ROOT_NAME.equals(elements.get(0).getElement());
    }
    /**
     * Gets canonical meta model path.
     * @return meta model path
     */
    public String toPath() {

        if (elements.isEmpty()) {
            return EMPTY;
        }

        StringBuilder pb = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {

            UPathElement e = elements.get(i);
            if (e.getType() != UPathElementType.COLLECTING) {
                continue;
            }

            pb.append(i > 0 ? '.' : EMPTY)
              .append(e.getElement());
        }

        return pb.toString();
    }
    /**
     * Gets UPath path.
     * @return UPath path
     */
    public String toUPath() {

        if (elements.isEmpty()) {
            return EMPTY;
        }

        StringBuilder pb = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {

            UPathElement e = elements.get(i);
            if (e.getType() == UPathElementType.COLLECTING) {
                pb.append(i > 0 ? '.' : EMPTY);
            }

            pb.append(e.getElement());
        }

        return pb.toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toUPath();
    }
}