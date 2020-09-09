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

package com.unidata.mdm.backend.util.constants;

/**
 * @author Dmitry Kopin on 22.09.2017.
 */
public interface UserMessageConstants {
    String STATISTIC_EXPORT_SUCCESS = "app.user.events.export.statistic.success";
    String STATISTIC_EXPORT_UNSUCCESS = "app.user.events.export.statistic.unsuccess";
    String CLASSIFIER_EXPORT_XLSX_SUCCESS = "app.user.events.export.classifier.xlsx.success";
    String CLASSIFIER_EXPORT_XLSX_UNSUCCESS = "app.user.events.export.classifier.xlsx.unsuccess";
    String CLASSIFIER_EXPORT_XML_SUCCESS = "app.user.events.export.classifier.xml.success";
    String CLASSIFIER_EXPORT_XML_UNSUCCESS = "app.user.events.export.classifier.xml.unsuccess";
    String CLASSIFIER_IMPORT_UPDATE_STEP1_SUCCESS = "app.user.events.import.update.classifier.step1.success";
    String CLASSIFIER_IMPORT_UPDATE_STEP2_SUCCESS = "app.user.events.import.update.classifier.step2.success";
    String CLASSIFIER_IMPORT_UPDATE_STEP2_FAIL = "app.user.events.import.update.classifier.step2.fail";
    String CLASSIFIER_IMPORT_NEW_SUCCESS = "app.user.events.import.new.classifier.success";
    String CLASSIFIER_IMPORT_UNSUCCESS = "app.user.events.import.classifier.unsuccess";
    String DATA_IMPORT_UNSUCCESS = "app.user.events.import.data.unsuccess";
    String DATA_EXPORT_ENTITY_RESULT = "app.user.events.export.data.entity.result";
    String DATA_EXPORT_LOOKUP_RESULT = "app.user.events.export.data.lookup.result";
    String DATA_EXPORT_METADATA_SUCCESS = "app.user.events.export.metadata.success";
    String JOBS_EXPORT_SUCCESS = "app.user.events.export.jobs.success";
    String JOBS_EXPORT_FAIL = "app.user.events.export.jobs.fail";
    String JOBS_IMPORT_SUCCESS = "app.user.events.import.jobs.success";
    String JOBS_IMPORT_FAIL = "app.user.events.import.jobs.fail";
}
