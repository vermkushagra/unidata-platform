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

import com.unidata.mdm.backend.dao.UserDao;
import com.unidata.mdm.backend.service.security.po.UserPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/test-only-dao.xml",})
@ActiveProfiles("default")
@Transactional
public class UserDaoImplTest {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImplTest.class);

    @Autowired
    private UserDao dao;

    @Test
    public void testSaveAndLoadAll() {
        UserPropertyPO property = new UserPropertyPO();

        property.setName("test-name1");
        property.setDisplayName("Test name 1");
        property.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        property.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        property.setCreatedBy("testuser");
        property.setUpdatedBy("testuser");

        dao.saveProperty(property);

        Assert.assertNotNull("User property id can't be null", property.getId());

        List<UserPropertyPO> allProperties = dao.loadAllProperties();

        Assert.assertNotNull("All properties can't be null", allProperties);

        UserPropertyPO existProperty = allProperties.stream()
            .filter(p -> p.getId().equals(property.getId()))
            .findFirst().get();

        Assert.assertEquals(property.getName(), existProperty.getName());
        Assert.assertEquals(property.getDisplayName(), existProperty.getDisplayName());

        existProperty = dao.loadPropertyByName(property.getName());
        Assert.assertNotNull(existProperty);
        Assert.assertEquals(property.getName(), existProperty.getName());
        Assert.assertEquals(property.getDisplayName(), existProperty.getDisplayName());

        UserPO user = dao.getAll().get(0);

        UserPropertyValuePO propValue = new UserPropertyValuePO();

        propValue.setProperty(property);
        propValue.setValue("some value 1");
        propValue.setUserId(user.getId().longValue());
        propValue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        propValue.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        propValue.setCreatedBy("testuser");
        propValue.setUpdatedBy("testuser");

        dao.saveUserPropertyValues(Collections.singletonList(propValue));

        Assert.assertNotNull("User property value id can't be null", propValue.getId());

        int userId = user.getId();

        List<UserPropertyValuePO> existUserPropertyValues = dao.loadUserPropertyValuesByUserIds(
            Collections.singleton(userId)).get(userId);

        Assert.assertNotNull("User property values can't be null", existUserPropertyValues);
        Assert.assertTrue(existUserPropertyValues.size() > 0);

        UserPropertyValuePO existPropValue = existUserPropertyValues.stream()
            .filter(v -> propValue.getId().equals(v.getId()))
            .findFirst()
            .get();

        Assert.assertEquals(propValue.getValue(), existPropValue.getValue());
        Assert.assertEquals(propValue.getProperty().getId(), existPropValue.getProperty().getId());

        // Change value for existing user property value object.
        propValue.setValue("some value 1-1");

        dao.saveUserPropertyValues(Collections.singletonList(propValue));

        existUserPropertyValues = dao.loadUserPropertyValuesByUserIds(Collections.singleton(userId)).get(userId);
        existPropValue = existUserPropertyValues.stream()
            .filter(v -> propValue.getId().equals(v.getId()))
            .findFirst()
            .get();

        Assert.assertEquals(propValue.getValue(), existPropValue.getValue());
        Assert.assertEquals(propValue.getProperty().getId(), existPropValue.getProperty().getId());

        dao.deleteUserPropertyValuesByIds(Collections.singleton(propValue.getId()));
        dao.deleteUserPropertyValuesByUserId(user.getId().longValue());

        dao.deleteProperty(property.getId());

        log.info("Property deleted: {}", property.getId());
    }
}
