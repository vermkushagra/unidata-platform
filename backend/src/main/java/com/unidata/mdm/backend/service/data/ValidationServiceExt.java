package com.unidata.mdm.backend.service.data;

import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.service.ValidationService;

import java.util.Date;

/**
 * @author Dmitry Kopin on 01.12.2017.
 */
public interface ValidationServiceExt extends ValidationService {


    /**
     * Calculate count of incomming links to the etalon.
     * @param etalonId - etalon id
     * @param asOf - as of date
     * @return count of incomming links to the etalon
     */
    Multimap<AttributeInfoHolder, Object> getMissedLinkedLookupEntities(String etalonId, Date asOf);
}
