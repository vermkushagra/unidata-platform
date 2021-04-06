/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.security.UserEventRO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UserEventConverter {

    /**
     * Constructor.
     */
    private UserEventConverter() {
        super();
    }

    /**
     * To REST from system.
     * @param source system
     * @return REST
     */
    public static UserEventRO to(UserEventDTO source) {
        if (source == null) {
            return null;
        }

        UserEventRO target = new UserEventRO();

        target.setId(source.getId());
        target.setBinaryDataId(source.getBinaryDataId());
        target.setCharacterDataId(source.getCharacterDataId());
        target.setContent(source.getContent());
        target.setCreateDate(source.getCreateDate());
        target.setCreatedBy(source.getCreatedBy());
        target.setType(source.getType());

        return target;
    }

    /**
     * To REST from system.
     * @param source system
     * @param target REST
     */
    public static void to (List<UserEventDTO> source, List<UserEventRO> target) {
        if (source == null || source.isEmpty()) {
            return;
        }

        for (UserEventDTO d : source) {
            target.add(to(d));
        }
    }
}
