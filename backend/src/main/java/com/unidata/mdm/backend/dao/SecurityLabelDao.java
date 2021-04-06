package com.unidata.mdm.backend.dao;

import java.util.List;

import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelAttributeValuePO;

public interface SecurityLabelDao {
    void saveLabelsForObject(int objectId, List<SecurityLabel> securityLabels);

    List<LabelAttributeValuePO> findLabelsAttributesValuesForObject(int objectId);

    /**
     * Clean users' labels values where label don't assigned to role
     *
     * @param roleName role name
     */
    void cleanUsersLabels(String roleName);
}
