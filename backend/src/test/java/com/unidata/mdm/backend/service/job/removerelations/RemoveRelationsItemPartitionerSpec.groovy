package com.unidata.mdm.backend.service.job.removerelations

import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder
import spock.lang.Shared
import spock.lang.Specification

class RemoveRelationsItemPartitionerSpec extends Specification {

    def idsKey = "ids"
    def userKey = "user"

    @Shared removeRelationItemPartitioner = new RemoveRelationsItemPartitioner()

    def jobParameterHolder = Mock(ComplexJobParameterHolder)

    def setup() {
        jobParameterHolder.getComplexParameterAndRemove(idsKey) >> (1..28).toList()

        removeRelationItemPartitioner.setJobParameterHolder(jobParameterHolder)
        removeRelationItemPartitioner.setBatchSize(10)
        removeRelationItemPartitioner.setIdsKey(idsKey)
        removeRelationItemPartitioner.setUserKey(userKey)
    }

    def "Partition given ids"() {
        when:
        def partition = removeRelationItemPartitioner.partition(10)

        then:
        partition.size() == 3
    }
}
