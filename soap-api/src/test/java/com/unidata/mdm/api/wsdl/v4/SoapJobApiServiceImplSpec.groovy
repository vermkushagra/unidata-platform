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

package com.unidata.mdm.api.wsdl.v4

import com.unidata.mdm.api.v4.*
import com.unidata.mdm.backend.common.dto.job.*
import com.unidata.mdm.backend.common.service.JobService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class SoapJobApiServiceImplSpec extends Specification {

    @Subject soapJobApiServiceImpl = new SoapJobApiServiceImpl()

    def jobService = Mock(JobService)

    @Shared longParameter = new JobParameterDTO(1, "test", 1L)

    @Shared savedJob = new JobDTO(1L, "savedJob", "someWork", [longParameter], true, false, "* * * * * ?", "description", true)

    def request = new UnidataRequestBody()
    def response = new UnidataResponseBody()

    def setup() {
        soapJobApiServiceImpl.jobService = jobService
    }

    def "handle find all jobs request when no jobs"() {
        given:
        request.withRequestFindAllJobs(new RequestFindAllJobs())

        when:
        soapJobApiServiceImpl.handleFindAllJobs(request, response)

        then:
        1 * jobService.findAll() >> []
        with(response) {
            responseFindAllJobs != null
            responseFindAllJobs.jobs.isEmpty()
        }
    }

    def "handle find all jobs request"() {
        given:
        request.withRequestFindAllJobs(new RequestFindAllJobs())

        when:
        soapJobApiServiceImpl.handleFindAllJobs(request, response)

        then:
        1 * jobService.findAll() >> [savedJob]
        with(response.responseFindAllJobs.jobs[0]) {
            id == savedJob.id
            name == savedJob.name
            parameters[0].id == longParameter.id
            parameters[0].name == longParameter.name
            parameters[0].type == JobParameterType.LONG
            parameters[0].value.longValue == longParameter.longValue
        }
    }

    def "handle save job request"() {
        given:
        def newJob = new Job()
                .withId(null)
                .withName("newJob")
                .withCronExpression("* * * * * ?")
                .withEnabled(true)
                .withError(false)
                .withSkipCronWarnings(true)
                .withJobNameReference("newWork")
                .withDescription("desc")
        request.withRequestSaveJob(new RequestSaveJob().withJob(newJob))

        when:
        soapJobApiServiceImpl.handleSaveJob(request, response)

        then:
        1 * jobService.saveJob({ it.name == newJob.name}) >>
                new JobDTO(2L, newJob.name, newJob.jobNameReference, [], newJob.enabled, newJob.error, newJob.cronExpression, newJob.description, newJob.skipCronWarnings)
        with (response.responseSaveJob.job) {
            id == 2L
            name == newJob.name
            jobNameReference == newJob.jobNameReference
        }
    }

    def "handle delete job request"() {
        given:
        request.withRequestRemoveJob(new RequestRemoveJob().withId(1L))

        when:
        soapJobApiServiceImpl.handleRemoveJob(request, response)

        then:
        1 * jobService.removeJob(1L)
        response.responseRemoveJob != null
    }

    def "handle find job request"() {
        given:
        request.withRequestFindJob(new RequestFindJob().withId(1L))

        when:
        soapJobApiServiceImpl.handleFindJob(request, response)

        then:
        1 * jobService.findJob(1L) >> savedJob
        with(response.responseFindJob.job) {
            id == savedJob.id
            name == savedJob.name
            parameters[0].id == longParameter.id
            parameters[0].name == longParameter.name
            parameters[0].type == JobParameterType.LONG
            parameters[0].value.longValue == longParameter.longValue
        }
    }

    def "handle run job request without parameters"() {
        given:
        request.withRequestRunJob(new RequestRunJob().withId(1L).withParameters([]))

        when:
        soapJobApiServiceImpl.handleRunJob(request, response)

        then:
        1 * jobService.runJob(1L, []) >>
                new JobExecutionDTO(
                        savedJob, [], null, null, null, null, JobExecutionBatchStatus.STARTING, new JobExecutionExitStatusDTO("UNKNOWN", null), []
                )
        with(response.responseRunJob.jobExecution) {
            job.id == savedJob.id
            startTime == null
            createTime == null
            endTime == null
            lastUpdated == null
            status == JobExecutionStatus.STARTING
            exitStatus.code == "UNKNOWN"
        }
    }

    def "handle job status request"() {
        given:
        request.withRequestJobStatus(new RequestJobStatus().withId(1L))

        when:
        soapJobApiServiceImpl.handleJobStatus(request, response)

        then:
        1 * jobService.jobStatus(1L) >>
                new JobExecutionDTO(
                        savedJob, [], null, null, null, null, JobExecutionBatchStatus.STARTED, new JobExecutionExitStatusDTO("UNKNOWN", null), []
                )
        with(response.responseJobStatus.jobExecution) {
            job.id == savedJob.id
            startTime == null
            createTime == null
            endTime == null
            lastUpdated == null
            status == JobExecutionStatus.STARTED
            exitStatus.code == "UNKNOWN"
        }
    }
}
