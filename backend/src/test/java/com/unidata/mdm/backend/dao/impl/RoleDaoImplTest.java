/**
 * Date: 06.07.2016
 */

package com.unidata.mdm.backend.dao.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.dao.RoleDao;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/test-only-dao.xml",})
@ActiveProfiles("default")
@Transactional
public class RoleDaoImplTest {
    private static final Logger log = LoggerFactory.getLogger(RoleDaoImplTest.class);

    @Autowired
    private RoleDao dao;

    @Test
    public void testSaveAndLoadAll() {
        RolePropertyPO property = new RolePropertyPO();

        property.setName("test-name1");
        property.setDisplayName("Test name 1");
        property.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        property.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        property.setCreatedBy("testuser");
        property.setUpdatedBy("testuser");

        dao.saveProperty(property);

        Assert.assertNotNull("Role property id can't be null", property.getId());

        List<RolePropertyPO> allProperties = dao.loadAllProperties();

        Assert.assertNotNull("All properties can't be null", allProperties);

        RolePropertyPO existProperty = allProperties.stream()
            .filter(p -> p.getId().equals(property.getId()))
            .findFirst().get();

        Assert.assertEquals(property.getName(), existProperty.getName());
        Assert.assertEquals(property.getDisplayName(), existProperty.getDisplayName());

        existProperty = dao.loadPropertyByName(property.getName());
        Assert.assertNotNull(existProperty);
        Assert.assertEquals(property.getName(), existProperty.getName());
        Assert.assertEquals(property.getDisplayName(), existProperty.getDisplayName());

        RolePO role = dao.getAll().stream().findFirst().get();

        RolePropertyValuePO propValue = new RolePropertyValuePO();

        propValue.setProperty(property);
        propValue.setValue("some value 1");
        propValue.setRoleId(role.getId().longValue());
        propValue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        propValue.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        propValue.setCreatedBy("testuser");
        propValue.setUpdatedBy("testuser");

        dao.saveRolePropertyValues(Collections.singletonList(propValue));

        Assert.assertNotNull("Role property value id can't be null", propValue.getId());

        int roleId = role.getId();

        List<RolePropertyValuePO> existRolePropertyValues = dao.loadRolePropertyValuesByRoleIds(
            Collections.singleton(roleId)).get(roleId);

        Assert.assertNotNull("Role property values can't be null", existRolePropertyValues);
        Assert.assertTrue(existRolePropertyValues.size() > 0);

        RolePropertyValuePO existPropValue = existRolePropertyValues.stream()
            .filter(v -> propValue.getId().equals(v.getId()))
            .findFirst()
            .get();

        Assert.assertEquals(propValue.getValue(), existPropValue.getValue());
        Assert.assertEquals(propValue.getProperty().getId(), existPropValue.getProperty().getId());

        // Change value for existing role property value object.
        propValue.setValue("some value 1-1");

        dao.saveRolePropertyValues(Collections.singletonList(propValue));

        existRolePropertyValues = dao.loadRolePropertyValuesByRoleIds(Collections.singleton(roleId)).get(roleId);
        existPropValue = existRolePropertyValues.stream()
            .filter(v -> propValue.getId().equals(v.getId()))
            .findFirst()
            .get();

        Assert.assertEquals(propValue.getValue(), existPropValue.getValue());
        Assert.assertEquals(propValue.getProperty().getId(), existPropValue.getProperty().getId());

        dao.deleteRolePropertyValuesByIds(Collections.singleton(propValue.getId()));
        dao.deleteRolePropertyValuesByRoleId(role.getId().longValue());

        dao.deleteProperty(property.getId());

        log.info("Property deleted: {}", property.getId());
    }
}
