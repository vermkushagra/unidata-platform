package com.unidata.mdm.backend.dao.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.dao.SecurityLabelDao;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelAttributeValuePO;
import com.unidata.mdm.backend.service.security.po.LabelPO;
import org.springframework.util.CollectionUtils;

public class SecurityLabelDaoImpl extends AbstractDaoImpl implements SecurityLabelDao {

    private final String selectSecurityLabelsByObject;
    private final String deleteAllObjectSecurityLabelsAttributes;
    private final String addSecurityLabelsAttributeToObject;
    private final String cleanUsersLabelsHavingRole;

    public SecurityLabelDaoImpl(final String connectionTable, final DataSource dataSource, final Properties sql) {
        super(dataSource);


        selectSecurityLabelsByObject =
                String.format(sql.getProperty("SELECT_SECURITY_LABELS_BY_OBJECT"), connectionTable);
        deleteAllObjectSecurityLabelsAttributes =
                String.format(sql.getProperty("DELETE_ALL_OBJECT_S_LABEL_ATTRIBUTES"), connectionTable);
        addSecurityLabelsAttributeToObject =
                String.format(sql.getProperty("ADD_S_LABEL_ATTRIBUTE_TO_OBJECT"), connectionTable);
        cleanUsersLabelsHavingRole = sql.getProperty("CLEAN_USERS_LABELS_HAVING_ROLE");
    }

    public void saveLabelsForObject(final int objectId, final List<SecurityLabel> securityLabels) {
        jdbcTemplate.update(deleteAllObjectSecurityLabelsAttributes, objectId);
        if (CollectionUtils.isEmpty(securityLabels)) {
            return;
        }
        int group = 0;
        for (final SecurityLabel securityLabel : securityLabels) {
            group++;
            final List<SecurityLabelAttribute> slas = securityLabel.getAttributes();
            if (CollectionUtils.isEmpty(slas)) {
                continue;
            }
            for (final SecurityLabelAttribute sla : slas) {
                final Map<String, Object> params = new HashMap<>();
                params.put("id", objectId);
                params.put("value", sla.getValue());
                params.put("attributeName", sla.getName());
                params.put("labelName", securityLabel.getName());
                params.put("labelGroup", group);
                namedJdbcTemplate.update(addSecurityLabelsAttributeToObject, params);
            }
        }
    }

    public List<LabelAttributeValuePO> findLabelsAttributesValuesForObject(final int objectId) {
        final List<Map<String, Object>> rows = namedJdbcTemplate.query(
                selectSecurityLabelsByObject,
                Collections.singletonMap("objectId", objectId),
                (rs, rowNum) -> {
                    final Map<String, Object> row = new HashMap<>();
                    row.put("labelId", rs.getInt("labelId"));
                    row.put("labelName", rs.getString("labelName"));
                    row.put("labelDisplayName", rs.getString("labelDisplayName"));
                    row.put("labelDescription", rs.getString("labelDescription"));
                    row.put("labelAttributeId", rs.getInt("labelAttributeId"));
                    row.put("labelAttributeName", rs.getString("labelAttributeName"));
                    row.put("labelAttributePath", rs.getString("labelAttributePath"));
                    row.put("labelAttributeDescription", rs.getString("labelAttributeDescription"));
                    row.put("labelAttributeValue", new LabelAttributeValuePO(
                            rs.getInt("labelAttributeValueId"),
                            rs.getString("labelAttributeValueValue"),
                            rs.getInt("labelAttributeValueGroup"),
                            null
                    ));
                    return row;
                }
        );
        final Map<Integer, LabelAttributePO> las = new HashMap<>();
        final Map<Integer, LabelPO> ls = new HashMap<>();
        for (final Map<String, Object> row : rows) {
            final LabelAttributeValuePO labelAttributeValuePO = (LabelAttributeValuePO) row.get("labelAttributeValue");
            final Integer labelAttributeId = (Integer) row.get("labelAttributeId");
            if (!las.containsKey(labelAttributeId)) {
                final LabelAttributePO labelAttributePO = new LabelAttributePO(
                        labelAttributeId,
                        (String) row.get("labelAttributeName"),
                        (String) row.get("labelAttributePath"),
                        (String) row.get("labelAttributeDescription")
                );
                las.put(labelAttributeId, labelAttributePO);
                final Integer labelId = (Integer) row.get("labelId");
                if (!ls.containsKey(labelId)) {
                    final LabelPO labelPO = new LabelPO(labelId);
                    labelPO.setName((String) row.get("labelName"));
                    labelPO.setDisplayName((String) row.get("labelDisplayName"));
                    labelPO.setDescription((String) row.get("labelDescription"));
                    ls.put(labelId, labelPO);
                }
                final LabelPO label = ls.get(labelId);
                labelAttributePO.setLabel(label);
                label.addLabelAttributePO(labelAttributePO);
            }
            final LabelAttributePO labelAttributePO = las.get(labelAttributeId);
            labelAttributeValuePO.setLabelAttribute(labelAttributePO);
            labelAttributePO.addLabelAttributeValue(labelAttributeValuePO);
        }
        return rows.stream().map(r -> (LabelAttributeValuePO) r.get("labelAttributeValue")).collect(Collectors.toList());
    }

    @Override
    public void cleanUsersLabels(String roleName) {
        namedJdbcTemplate.update(cleanUsersLabelsHavingRole, Collections.singletonMap("roleName", roleName));
    }
}
