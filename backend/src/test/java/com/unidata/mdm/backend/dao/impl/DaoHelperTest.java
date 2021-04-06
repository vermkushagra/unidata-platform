/**
 * Date: 22.03.2016
 */

package com.unidata.mdm.backend.dao.impl;

import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplateImpl;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/test-only-dao.xml",})
@ActiveProfiles("default")
public class DaoHelperTest implements InitializingBean {
    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcTemplate = new UnidataJdbcTemplateImpl(dataSource);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertLongsToTemp() {
        List<Long> firstIds = Arrays.asList(1L, 2L, 3L, 4L);
        List<Long> secondIds = Arrays.asList(11L, 22L, 33L, 43L);

        long list1 = daoHelper.insertLongsToTemp(firstIds);
        long list2 = daoHelper.insertLongsToTemp(secondIds);

        assertNotEquals(list1, list2);

        List<Long> ids = jdbcTemplate.query("select id from t_tmp_id where list_id = ? ",
            new SingleColumnRowMapper<>(Long.class),
            list1);

        assertNotNull(ids);
        assertEquals(firstIds.size(), ids.size());
        assertTrue(firstIds.containsAll(ids));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertStringsToTemp() {
        List<String> firstIds = Arrays.asList("id-1", "id-2", "id-3", "id-4", "id-5");
        List<String> secondIds = Arrays.asList("id-11", "id-22", "id-33", "id-44", "id-55");

        long list1 = daoHelper.insertStringsToTemp(firstIds);
        long list2 = daoHelper.insertStringsToTemp(secondIds);

        assertNotEquals(list1, list2);

        List<String> ids = jdbcTemplate.query("select some_text from t_tmp_id where list_id = ? ",
            new SingleColumnRowMapper<>(String.class),
            list1);

        assertNotNull(ids);
        assertEquals(firstIds.size(), ids.size());
        assertTrue(firstIds.containsAll(ids));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testCreateIds() {
        long id = daoHelper.createId();
        long[] ids = daoHelper.createIds(4);

        assertNotNull(ids);

        Set<Long> set = new HashSet<>();

        for (long idVal : ids) {
            set.add(idVal);
        }

        set.add(id);

        assertEquals(5, set.size());
    }
}
