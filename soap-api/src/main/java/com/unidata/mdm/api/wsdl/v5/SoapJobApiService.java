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

package com.unidata.mdm.api.wsdl.v5;

import com.unidata.mdm.api.v5.UnidataRequestBody;
import com.unidata.mdm.api.v5.UnidataResponseBody;

public interface SoapJobApiService {
    void handleFindAllJobs(UnidataRequestBody request, UnidataResponseBody response);

    void handleSaveJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleRemoveJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleFindJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleRunJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleJobStatus(UnidataRequestBody request, UnidataResponseBody response);

    void handleStopJob(UnidataRequestBody request, UnidataResponseBody response);
}
