package org.unidata.mdm.meta.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;


/**
 * @author Dmitry Kopin on 31.05.2019.
 */
public interface LookupService {
    /**
     * Get lookup display name by code
     * @param lookupName lookup name
     * @param codeAttrValue code attr value
     * @param validFrom valid from for filter
     * @param validTo valid to for filter
     * @param toBuildAttrs custom display attributes list
     * @param useAttributeNameForDisplay use  attribute names for build display name
     * @return return pair of linkedEtalonId, Display name
     */
    Pair<String, String> getLookupDisplayNameById(String lookupName, Object codeAttrValue, Date validFrom, Date validTo, List<String> toBuildAttrs, boolean useAttributeNameForDisplay);
}
