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

package org.unidata.mdm.system.type.pipeline;

/**
 * @author Mikhail Mikhailov
 * The pipeline segment type.
 */
public enum SegmentType {
    /**
     * Pipeline's starting point, defining pipeline's input type.
     */
    START,
    /**
     * Pipeline's execution point. Contains code, processing input type.
     */
    POINT,
    /**
     * Connector type segment,
     * connecting another pipeline to this pipeline and returning intermediate result.
     */
    CONNECTOR,
    /**
     * The finalizer type, preparing the pipeline's result.
     */
    FINISH,
    /**
     * Marks an action, executed only if the pipeline fails.
     * Fallbacks are executed on the order of submission to pipeline.
     */
    FALLBACK;
}
