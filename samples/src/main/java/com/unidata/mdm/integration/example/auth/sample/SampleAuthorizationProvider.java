package com.unidata.mdm.integration.example.auth.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.integration.auth.AuthorizationProvider;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceType;
import com.unidata.mdm.backend.common.integration.auth.SecurityDataProviderException;
import com.unidata.mdm.backend.common.integration.auth.User;

/**
 * @author Denis Kostovarov
 */
public class SampleAuthorizationProvider implements AuthorizationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleAuthorizationProvider.class);

    private static final Map<String, String> RESOURCES;

    static {
        RESOURCES = new HashMap<>();
        RESOURCES.put("R_SIMPLE_3", "Сэмпл 3");
        RESOURCES.put("Sample1", "Сэмпл 1");
        /*
        RESOURCES.put("Z01T_PREDP_LOK", "Предприятия локомотивного хозяйства");
        RESOURCES.put("KGV", "Картотека грузовых вагонов");
        RESOURCES.put("H_STAN_OB", "Раздельные пункты");
        RESOURCES.put("ADMZHDOR", "Администрации железных дорог");
        RESOURCES.put("ZHDORMIR", "Железные дороги мира");
        */
    }

    @Override
    public void authorize(User user) throws SecurityDataProviderException {
        LOGGER.info("Getting sample authorization for external user");

        if ("lionell".equals(user.getLogin()) && user instanceof SampleUserInfo) {
            final SampleUserInfo sampleUserInfo = (SampleUserInfo) user;
            final List<Right> rights = new ArrayList<>();

            SampleRight r = new SampleRight();
            SampleSecuredResource sampleSecuredResource = new SampleSecuredResource();
            sampleSecuredResource.setName("ADMIN_SYSTEM_MANAGEMENT");
            sampleSecuredResource.setDisplayName("Администратор системы");
            sampleSecuredResource.setType(SecuredResourceType.SYSTEM);
            sampleSecuredResource.setCategory(SecuredResourceCategory.SYSTEM);
            r.setSecuredResource(sampleSecuredResource);
            r.setRead(true);
            r.setCreate(true);
            r.setUpdate(true);
            r.setDelete(true);
            rights.add(r);

            r = new SampleRight();
            sampleSecuredResource = new SampleSecuredResource();
            sampleSecuredResource.setName("ADMIN_DATA_MANAGEMENT");
            sampleSecuredResource.setDisplayName("Администратор данных");
            sampleSecuredResource.setType(SecuredResourceType.SYSTEM);
            sampleSecuredResource.setCategory(SecuredResourceCategory.SYSTEM);
            r.setSecuredResource(sampleSecuredResource);
            r.setRead(true);
            r.setCreate(true);
            r.setUpdate(true);
            r.setDelete(true);
            rights.add(r);

            final SampleRole sampleRole = new SampleRole();
            sampleRole.setName("testrole");
            sampleRole.setDisplayName("A testrole.");
            sampleRole.setRights(getRoleRights(user.getLogin()));

            sampleUserInfo.setRights(rights);
            sampleUserInfo.setRoles(Collections.singletonList(sampleRole));
            sampleUserInfo.setHasAuthorization(true);
        }
    }

    private static List<Right> getRoleRights(final String username) {
        final List<Right> rights = new ArrayList<>();
        final SampleRight r = new SampleRight();

        if ("lionell".equals(username)) {
            for (final String resourceName : RESOURCES.keySet()) {
                final SampleSecuredResource sampleSecuredResource = new SampleSecuredResource();
                sampleSecuredResource.setName(resourceName);
                sampleSecuredResource.setDisplayName(RESOURCES.get(resourceName));
                sampleSecuredResource.setType(SecuredResourceType.USER_DEFINED);
                sampleSecuredResource.setCategory(SecuredResourceCategory.META_MODEL);
                r.setSecuredResource(sampleSecuredResource);
                r.setUpdate(true);
                r.setRead(true);
                rights.add(r);
            }
        }

        return rights;
    }
}
