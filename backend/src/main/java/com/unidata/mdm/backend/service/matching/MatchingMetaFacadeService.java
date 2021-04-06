package com.unidata.mdm.backend.service.matching;

import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;

/**
 * Service.
 */
public interface MatchingMetaFacadeService {

    /**
     * Save user settings.
     *
     * @param matchingUserSettings - user defined rules and groups
     */
    void saveUserSettings(MatchingUserSettings matchingUserSettings);

	/**
	 * Import user settings.
	 *
	 * @param matchingUserSettings the matching user settings
	 */
	void importUserSettings(MatchingUserSettings matchingUserSettings);
}
