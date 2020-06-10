package com.unidata.mdm.api.wsdl.v4;

import com.unidata.mdm.api.v4.UnidataRequestBody;
import com.unidata.mdm.api.v4.UnidataResponseBody;

public interface SoapJobApiService {
    void handleFindAllJobs(UnidataRequestBody request, UnidataResponseBody response);

    void handleSaveJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleRemoveJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleFindJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleRunJob(UnidataRequestBody request, UnidataResponseBody response);

    void handleJobStatus(UnidataRequestBody request, UnidataResponseBody response);
}
