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

package com.unidata.mdm.backend.notification.notifiers;

import java.util.Collection;

import com.unidata.mdm.backend.notification.events.ClassifierChangeEvent.ClassifierChangesEventType;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * @author Mikhail Mikhailov
 * Classifier metam model changes notifier.
 */
public interface ClassifierChangeNotifier extends AfterContextRefresh {
    /**
     * Notifies about changes to classifiers.
     * @param changeType the type of changes
     * @param classifierNames the classifier names
     */
    void notifyClassifierChanged(ClassifierChangesEventType changeType, Collection<String> classifierNames);
}
