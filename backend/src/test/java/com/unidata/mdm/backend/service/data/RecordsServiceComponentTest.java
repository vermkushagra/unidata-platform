package com.unidata.mdm.backend.service.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.security.RightDTO;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.UserEndpointDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemSecurityException;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceType;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractCodeAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.dto.storage.UserInfo;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.po.RecordKeysPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.security.utils.BearerToken;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * @author Mikhail Mikhailov
 * Tests, related to data management.
 */
@ActiveProfiles(profiles = { DefaultTestDataConfiguration.TEST_DATA_PROFILE_NAME })
@ContextConfiguration(classes = { DefaultTestDataConfiguration.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class RecordsServiceComponentTest {

    public static final String TEST_ENTITY_1 = "test_entity_1";

    public static final String TEST_ENUM_1 = "test_enum_1";

    public static final String TEST_LOOKUP_1 = "test_lookup_1";

    public static final String TEST_RELATION_1 = "test_relation_1";

    public static final String TEST_CLASSIFIER_1 = "test_classifier_1";

    public static final String TEST_ETALON_ID_1 = UUID.randomUUID().toString();

    public static final String TEST_SOURCE_SYSTEM_1 = "test_source_system_1";

    public static final String TEST_EXTERNAL_ID_1 = "test_external_id_1";

    public static final String TEST_ORIGIN_ID_1 = UUID.randomUUID().toString();

    public static final long TEST_GSN_1 = Long.MIN_VALUE;

    public static final String TEST_ADMIN_SOURCE_SYSTEM = "unidata";

    public static final String TEST_EXTERNAL_ID_2 = "test_external_id_2";

    public static final String TEST_ORIGIN_ID_2 = UUID.randomUUID().toString();

    public static final long TEST_GSN_2 = Long.MAX_VALUE;

    public static final String TEST_CLASSIFIER_ETALON_ID_1 = UUID.randomUUID().toString();

    public static final String TEST_CLASSIFIER_ORIGIN_ID_1 = UUID.randomUUID().toString();

    public static final String TEST_CLASSIFIER_ORIGIN_ID_2 = UUID.randomUUID().toString();

    public static final String TEST_CLASSIFIER_NODE_ID_1 = UUID.randomUUID().toString();

    public static final String TEST_CLASSIFIER_NODE_ID_2 = UUID.randomUUID().toString();

    public static final String TEST_ROLE_1 = "test_role_1";

    public static final String TEST_USER_NAME_1 = "user_1";

    public static final String TEST_USER_NAME_2 = "user_2";

    public static final String TEST_OPERATION_ID_1 = UUID.randomUUID().toString();

    public static final String TEST_OPERATION_ID_2 = UUID.randomUUID().toString();

    public static final List<SourceSystemDef> TEST_SOURCE_SYSTEMS_1 = Arrays.asList(
        new SourceSystemDef().withName(TEST_SOURCE_SYSTEM_1).withAdmin(false).withWeight(new BigInteger("100")),
        new SourceSystemDef().withName(TEST_ADMIN_SOURCE_SYSTEM).withAdmin(true).withWeight(new BigInteger("100"))
    );

    @Autowired
    @Qualifier("defaultTestDataConfiguration")
    private DefaultTestDataConfiguration configuration;

    // SUT/CUT
    @Autowired
    private RecordsServiceComponent recordServiceComponent;

    /**
     * Constructor.
     */
    public RecordsServiceComponentTest() {
        super();
    }

    /**
     * Setup security context.
     */
    @BeforeClass
    public static void classStartUp() {

        UserInfo testUser = new UserInfo();

        // Authorize
        testUser.setLabels(Collections.emptyList());
        testUser.setCustomProperties(Collections.emptyList());
        testUser.setEndpoints(Arrays.asList(new UserEndpointDTO [] {
                new UserEndpointDTO(Endpoint.REST.name(), "REST", "REST endpoint"),
                new UserEndpointDTO(Endpoint.SOAP.name(), "SOAP", "SOAP endpoint")
        }));

        List<Right> testRights = new ArrayList<>();

        RightDTO tr = new RightDTO();
        SecuredResourceDTO tsr = new SecuredResourceDTO();
        tsr.setName("ADMIN_SYSTEM_MANAGEMENT");
        tsr.setDisplayName("Администратор системы");
        tsr.setType(SecuredResourceType.SYSTEM);
        tsr.setCategory(SecuredResourceCategory.SYSTEM);
        tr.setSecuredResource(tsr);
        tr.setRead(true);
        tr.setCreate(true);
        tr.setUpdate(true);
        tr.setDelete(true);
        testRights.add(tr);

        tr = new RightDTO();
        tsr = new SecuredResourceDTO();
        tsr.setName("ADMIN_DATA_MANAGEMENT");
        tsr.setDisplayName("Администратор данных");
        tsr.setType(SecuredResourceType.SYSTEM);
        tsr.setCategory(SecuredResourceCategory.SYSTEM);
        tr.setSecuredResource(tsr);
        tr.setRead(true);
        tr.setCreate(true);
        tr.setUpdate(true);
        tr.setDelete(true);
        testRights.add(tr);

        RoleDTO testRole = new RoleDTO();
        testRole.setName(TEST_ROLE_1);
        testRole.setDisplayName("An test role.");

        RightDTO etr = new RightDTO();
        tsr = new SecuredResourceDTO();
        tsr.setName(TEST_ENTITY_1);
        tsr.setDisplayName("Test entity secured resource.");
        tsr.setType(SecuredResourceType.USER_DEFINED);
        tsr.setCategory(SecuredResourceCategory.META_MODEL);
        etr.setSecuredResource(tsr);
        etr.setUpdate(true);
        etr.setRead(true);
        etr.setCreate(true);
        etr.setDelete(true);

        RightDTO ltr = new RightDTO();
        tsr = new SecuredResourceDTO();
        tsr.setName(TEST_LOOKUP_1);
        tsr.setDisplayName("Test lookup secured resource.");
        tsr.setType(SecuredResourceType.USER_DEFINED);
        tsr.setCategory(SecuredResourceCategory.META_MODEL);
        ltr.setSecuredResource(tsr);
        ltr.setUpdate(true);
        ltr.setRead(true);
        ltr.setCreate(true);
        ltr.setDelete(true);

        testRole.setRights(Arrays.asList(etr, ltr));

        testUser.setRights(testRights);
        testUser.setRoles(Collections.singletonList(testRole));
        testUser.setHasAuthorization(true);

        // Profile
        testUser.setLogin(TEST_USER_NAME_1);
        testUser.setPassword("123");
        testUser.setPasswordUpdatedAt(new Date());
        testUser.setPasswordUpdatedBy(TEST_USER_NAME_1);
        testUser.setAdmin(true);
        testUser.setSecurityDataSource(SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE);
        testUser.setCreatedAt(new Date());
        testUser.setUpdatedBy(TEST_USER_NAME_1);
        testUser.setUpdatedAt(new Date());
        testUser.setExternal(false);
        testUser.setForcePasswordChangeFlag(false);
        testUser.setEmail(TEST_USER_NAME_1 + "@org.com");
        testUser.setLocale(new Locale("en"));
        testUser.setName("User One");
        testUser.setHasProfile(true);

        // ST
        // 1. Create token and set necessary fields.
        final SecurityToken token = new SecurityToken();

        // 2. Extract stuff, which may have been supplied by authorization
        List<Role> roles = testUser.getRoles();
        List<Right> rights = testUser.getRights();
        List<SecurityLabel> labels = testUser.getLabels();

        // 3. Create maps from roles
        final Map<String, Right> rightsMap = SecurityUtils.createRightsMap(roles);
        final Map<String, Role> rolesMap = SecurityUtils.createRolesMap(roles);
        final Map<String, List<SecurityLabel>> labelsMap = SecurityUtils.createLabelsMap(roles);

        // 4. Overwrite calculated rights with manually supplied one
        Map<String, Right> overwriteRights = SecurityUtils.extractRightsMap(rights);
        overwriteRights.forEach(rightsMap::put);

        // 5. Overwrite calculated labels with manually supplied ones
        Map<String, List<SecurityLabel>> overwriteLabels = SecurityUtils.extractLabelsMap(labels);
        overwriteLabels.forEach(labelsMap::put);

        token.getRightsMap().putAll(rightsMap);
        token.getRolesMap().putAll(rolesMap);
        token.getLabelsMap().putAll(labelsMap);
        token.setEndpoint(Endpoint.REST);
        token.setToken(UUID.randomUUID().toString());
        token.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        token.setUser(testUser);

        final List<GrantedAuthority> testAuthorities = new ArrayList<>();
        for (final Right right : token.getRightsMap().values()) {
            final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(right.getSecuredResource().getName());
            testAuthorities.add(grantedAuthority);
        }

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.getContext().setAuthentication(new BearerToken(token, testAuthorities));
    }

    /**
     * Reload mocks.
     */
    @Before
    public void startUp() {
        configuration.resetMocks();
    }

    @Test
    public void testLoadRecord_InvalidKeysNoExtId() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_ENTITY_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .build();

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No ext. id. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_INVALID_INPUT expected.", dpe.getId(), ExceptionId.EX_DATA_GET_INVALID_INPUT);
        }
    }

    @Test
    public void testLoadRecord_InvalidKeysNoEntityName() {

        GetRequestContext grc = GetRequestContext.builder()
                .externalId(TEST_EXTERNAL_ID_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .build();

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No entity name. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_INVALID_INPUT expected.", dpe.getId(), ExceptionId.EX_DATA_GET_INVALID_INPUT);
        }
    }

    @Test
    public void testLoadRecord_InvalidKeysNoSourceSystem() {

        GetRequestContext grc = GetRequestContext.builder()
                .externalId(TEST_EXTERNAL_ID_1)
                .entityName(TEST_ENTITY_1)
                .build();

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No source system. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_INVALID_INPUT expected.", dpe.getId(), ExceptionId.EX_DATA_GET_INVALID_INPUT);
        }
    }

    @Test
    public void testLoadRecord_IdentityNotFoundByExternalId() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_ENTITY_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .build();

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, TEST_ENTITY_1))
               .thenReturn(Collections.emptyList());

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No record found by external ID. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS expected.", dpe.getId(), ExceptionId.EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS);
            Mockito
                .verify(configuration.getDataRecordsDaoMock())
                .loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, TEST_ENTITY_1);
        }
    }

    @Test
    public void testLoadRecord_IdentityNotFoundByOriginId() {

        GetRequestContext grc = GetRequestContext.builder()
                .originKey(TEST_ORIGIN_ID_1)
                .build();

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByOriginId(TEST_ORIGIN_ID_1))
               .thenReturn(Collections.emptyList());

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No record found by origin ID. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS expected.", dpe.getId(), ExceptionId.EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS);
            Mockito
                .verify(configuration.getDataRecordsDaoMock())
                .loadRecordKeysByOriginId(TEST_ORIGIN_ID_1);
        }
    }

    @Test
    public void testLoadRecord_IdentityNotFoundByEtalonId() {

        GetRequestContext grc = GetRequestContext.builder()
                .etalonKey(TEST_ETALON_ID_1)
                .build();

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByEtalonId(TEST_ETALON_ID_1))
               .thenReturn(Collections.emptyList());
        Mockito.when(configuration.getMetaModelServiceMock().getAdminSourceSystem())
               .thenReturn(new SourceSystemDef().withName(TEST_ADMIN_SOURCE_SYSTEM));

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No record found by etalon ID. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS expected.", dpe.getId(), ExceptionId.EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS);
            Mockito
                .verify(configuration.getDataRecordsDaoMock())
                .loadRecordKeysByEtalonId(TEST_ETALON_ID_1);
            Mockito
                .verify(configuration.getMetaModelServiceMock())
                .getAdminSourceSystem();
        }
    }

    @Test
    public void testLoadRecord_IdentityNotFoundByGSN() {

        GetRequestContext grc = GetRequestContext.builder()
                .gsn(Long.MIN_VALUE)
                .build();

        Mockito.when(configuration.getMetaModelServiceMock().getAdminSourceSystem())
               .thenReturn(new SourceSystemDef().withName(TEST_ADMIN_SOURCE_SYSTEM));
        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByGSN(Long.MIN_VALUE))
                .thenReturn(Collections.emptyList());

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No record found by GSN. DataProcessingException was not thrown!");
        } catch (DataProcessingException dpe) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS expected.", dpe.getId(), ExceptionId.EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS);
            Mockito
                .verify(configuration.getDataRecordsDaoMock())
                .loadRecordKeysByGSN(Long.MIN_VALUE);
            Mockito
                .verify(configuration.getMetaModelServiceMock())
                .getAdminSourceSystem();
        }
    }

    @Test
    public void testLoadRecord_CheckRightsNotSuccessful() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName("no_rights_entity")
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .build();

        BearerToken tokenObject = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        SecurityToken securityToken = (SecurityToken) tokenObject.getDetails();

        // Temporary switch admin flag off.
        securityToken.getUser().setAdmin(false);

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, "no_rights_entity"))
               .thenReturn(BoolshitFactory.testKeys_1("no_rights_entity"));
        Mockito.when(configuration.getSecurityServiceExtMock().getTokenObjectByToken(tokenObject.getCurrentToken()))
               .thenReturn(securityToken);

        try {
            recordServiceComponent.loadRecord(grc);
            fail("No security rights. SystemSecurityException was not thrown!");
        } catch (SystemSecurityException sse) {
            assertEquals("Wrong exception ID thrown. EX_DATA_GET_NO_RIGHTS expected.", sse.getId(), ExceptionId.EX_DATA_GET_NO_RIGHTS);
            Mockito
                .verify(configuration.getDataRecordsDaoMock())
                .loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, "no_rights_entity");
            Mockito
                .verify(configuration.getSecurityServiceExtMock())
                .getTokenObjectByToken(tokenObject.getCurrentToken());

            Assert.assertNotNull(grc.getFromStorage(grc.keysId()));

            RecordKeys keys = grc.keys();

            Assert.assertEquals(keys.getEtalonKey().getId(), TEST_ETALON_ID_1);
            Assert.assertEquals(keys.getOriginKey().getId(), TEST_ORIGIN_ID_1);
            Assert.assertEquals(keys.getOriginKey().getExternalId(), TEST_EXTERNAL_ID_1);
            Assert.assertEquals(keys.getOriginKey().getSourceSystem(), TEST_SOURCE_SYSTEM_1);
            Assert.assertEquals(keys.getOriginKey().getEntityName(), "no_rights_entity");
            Assert.assertTrue(
                    CollectionUtils.isNotEmpty(keys.getSupplementaryKeys())
                 && keys.getSupplementaryKeys().stream()
                     .anyMatch(ok -> TEST_ORIGIN_ID_2.equals(ok.getId())
                         && TEST_EXTERNAL_ID_2.equals(ok.getExternalId())
                         && TEST_ADMIN_SOURCE_SYSTEM.equals(ok.getSourceSystem())
                         && TEST_GSN_2 == ok.getGsn()
                         && "no_rights_entity".equals(ok.getEntityName())));

        } finally {
            securityToken.getUser().setAdmin(true);
        }
    }

    @Test
    public void testLoadRecord_SimpleEntitySuccessful() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_ENTITY_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .build();

        BearerToken tokenObject = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        SecurityToken securityToken = (SecurityToken) tokenObject.getDetails();

        // Temporary switch admin flag off.
        securityToken.getUser().setAdmin(false);

        EntityWrapper ew = BoolshitFactory.testEntityWrapper_1(TEST_ENTITY_1, false, false, false);
        List<OriginsVistoryRecordPO> ov = BoolshitFactory.testVistoryPOs_1(TEST_ENTITY_1, false, false, false, false);

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, TEST_ENTITY_1))
               .thenReturn(BoolshitFactory.testKeys_1(TEST_ENTITY_1));
        Mockito.when(configuration.getSecurityServiceExtMock().getTokenObjectByToken(tokenObject.getCurrentToken()))
               .thenReturn(securityToken);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadVersions(TEST_ETALON_ID_1, null, false, TEST_USER_NAME_1))
               .thenReturn(ov);
        Mockito.when(configuration.getMetaModelServiceMock().getValueById(TEST_ENTITY_1, EntityWrapper.class))
               .thenReturn(ew);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadEtalonBoundary(TEST_ETALON_ID_1, null, false))
               .thenReturn(BoolshitFactory.testTimeIntervalPO_1(TEST_ENTITY_1));
        Mockito.when(configuration.getSearchServiceExtMock().search(Mockito.any(SearchRequestContext.class)))
               .thenReturn(new SearchResultDTO());
        Mockito.when(configuration.getMetaModelServiceMock().getAttributesInfoMap(TEST_ENTITY_1))
               .thenReturn(ew.getAttributes());

        try {

            GetRecordDTO result = recordServiceComponent.loadRecord(grc);

            Assert.assertNotNull(grc.getFromStorage(grc.keysId()));

            RecordKeys keys = grc.keys();

            Assert.assertEquals(keys.getEtalonKey().getId(), TEST_ETALON_ID_1);
            Assert.assertEquals(keys.getOriginKey().getId(), TEST_ORIGIN_ID_1);
            Assert.assertEquals(keys.getOriginKey().getExternalId(), TEST_EXTERNAL_ID_1);
            Assert.assertEquals(keys.getOriginKey().getSourceSystem(), TEST_SOURCE_SYSTEM_1);
            Assert.assertEquals(keys.getOriginKey().getEntityName(), TEST_ENTITY_1);
            Assert.assertTrue(
                    CollectionUtils.isNotEmpty(keys.getSupplementaryKeys())
                 && keys.getSupplementaryKeys().stream()
                     .anyMatch(ok -> TEST_ORIGIN_ID_2.equals(ok.getId())
                         && TEST_EXTERNAL_ID_2.equals(ok.getExternalId())
                         && TEST_ADMIN_SOURCE_SYSTEM.equals(ok.getSourceSystem())
                         && TEST_GSN_2 == ok.getGsn()
                         && TEST_ENTITY_1.equals(ok.getEntityName())));

            Assert.assertSame(keys, result.getRecordKeys());
            Assert.assertNotNull(result.getEtalon());
            Assert.assertNotNull((result.getEtalon().getAttribute("string_attr_1")));
            Assert.assertNotNull((result.getEtalon().getAttribute("string_attr_2")));
            Assert.assertNotNull((result.getEtalon().getAttribute("int_attr_3")));
            Assert.assertNotNull((result.getEtalon().getAttribute("int_attr_4")));
            Assert.assertArrayEquals(
                    new Object[] {
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_1")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_2")).getValue(), // Overwritten by TEST_SOURCE_SYSTEM_1
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_3")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_4")).getValue()
            },
                    new Object[] {
                    "string_val_1",
                    "string_val_2",
                    Long.valueOf(3),
                    Long.valueOf(4)
            });

        } finally {
            securityToken.getUser().setAdmin(true);
        }
    }

    @Test
    public void testLoadRecord_SimpleLookupSuccessful() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_LOOKUP_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .build();

        BearerToken tokenObject = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        SecurityToken securityToken = (SecurityToken) tokenObject.getDetails();

        // Temporary switch admin flag off.
        securityToken.getUser().setAdmin(false);

        LookupEntityWrapper lew = BoolshitFactory.testLookupWrapper_1(TEST_LOOKUP_1);
        List<OriginsVistoryRecordPO> ov = BoolshitFactory.testVistoryPOs_1(TEST_LOOKUP_1, true, false, false, false);

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, TEST_LOOKUP_1))
               .thenReturn(BoolshitFactory.testKeys_1(TEST_LOOKUP_1));
        Mockito.when(configuration.getSecurityServiceExtMock().getTokenObjectByToken(tokenObject.getCurrentToken()))
               .thenReturn(securityToken);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadVersions(TEST_ETALON_ID_1, null, false, TEST_USER_NAME_1))
               .thenReturn(ov);
        Mockito.when(configuration.getMetaModelServiceMock().getValueById(TEST_LOOKUP_1, LookupEntityWrapper.class))
               .thenReturn(lew);
        Mockito.when(configuration.getMetaModelServiceMock().isLookupEntity(TEST_LOOKUP_1))
               .thenReturn(true);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadEtalonBoundary(TEST_ETALON_ID_1, null, false))
               .thenReturn(BoolshitFactory.testTimeIntervalPO_1(TEST_LOOKUP_1));
        Mockito.when(configuration.getSearchServiceExtMock().search(Mockito.any(SearchRequestContext.class)))
               .thenReturn(new SearchResultDTO());
        Mockito.when(configuration.getMetaModelServiceMock().getAttributesInfoMap(TEST_LOOKUP_1))
               .thenReturn(lew.getAttributes());

        try {

            GetRecordDTO result = recordServiceComponent.loadRecord(grc);

            Assert.assertNotNull(grc.getFromStorage(grc.keysId()));

            RecordKeys keys = grc.keys();

            Assert.assertEquals(keys.getEtalonKey().getId(), TEST_ETALON_ID_1);
            Assert.assertEquals(keys.getOriginKey().getId(), TEST_ORIGIN_ID_1);
            Assert.assertEquals(keys.getOriginKey().getExternalId(), TEST_EXTERNAL_ID_1);
            Assert.assertEquals(keys.getOriginKey().getSourceSystem(), TEST_SOURCE_SYSTEM_1);
            Assert.assertEquals(keys.getOriginKey().getEntityName(), TEST_LOOKUP_1);
            Assert.assertTrue(
                    CollectionUtils.isNotEmpty(keys.getSupplementaryKeys())
                 && keys.getSupplementaryKeys().stream()
                     .anyMatch(ok -> TEST_ORIGIN_ID_2.equals(ok.getId())
                         && TEST_EXTERNAL_ID_2.equals(ok.getExternalId())
                         && TEST_ADMIN_SOURCE_SYSTEM.equals(ok.getSourceSystem())
                         && TEST_GSN_2 == ok.getGsn()
                         && TEST_LOOKUP_1.equals(ok.getEntityName())));

            Assert.assertSame(keys, result.getRecordKeys());
            Assert.assertNotNull(result.getEtalon());
            Assert.assertNotNull((result.getEtalon().getAttribute("code_attr_1")));
            Assert.assertNotNull((result.getEtalon().getAttribute("string_attr_1")));
            Assert.assertNotNull((result.getEtalon().getAttribute("string_attr_2")));
            Assert.assertNotNull((result.getEtalon().getAttribute("int_attr_3")));
            Assert.assertNotNull((result.getEtalon().getAttribute("int_attr_4")));
            Assert.assertArrayEquals(
                    new Object[] {
                    ((CodeAttribute<?>) result.getEtalon().getAttribute("code_attr_1")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_1")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_2")).getValue(), // Overwritten by TEST_SOURCE_SYSTEM_1
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_3")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_4")).getValue()
            },
                    new Object[] {
                    "winner_code_attr_value",
                    "string_val_1",
                    "string_val_2",
                    Long.valueOf(3),
                    Long.valueOf(4)
            });

            Assert.assertNotNull(((CodeAttribute<?>) result.getEtalon().getAttribute("code_attr_1")).getSupplementary());
            Assert.assertTrue(((CodeAttribute<?>) result.getEtalon().getAttribute("code_attr_1")).getSupplementary().contains("supplementary_code_attr_value"));

        } finally {
            securityToken.getUser().setAdmin(true);
        }
    }

    @Test
    public void testLoadRecord_LinksToLookupsEnumsAndLinkTemplatesEntitySuccessful() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_ENTITY_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .build();

        BearerToken tokenObject = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        SecurityToken securityToken = (SecurityToken) tokenObject.getDetails();

        // Temporary switch admin flag off.
        securityToken.getUser().setAdmin(false);

        EnumerationWrapper enw = BoolshitFactory.testEnumWrapper_1(TEST_ENUM_1);
        LookupEntityWrapper lew = BoolshitFactory.testLookupWrapper_1(TEST_LOOKUP_1);
        EntityWrapper ew = BoolshitFactory.testEntityWrapper_1(TEST_ENTITY_1, true, true, true);
        List<OriginsVistoryRecordPO> ov = BoolshitFactory.testVistoryPOs_1(TEST_ENTITY_1, false, true, true, true);

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, TEST_ENTITY_1))
               .thenReturn(BoolshitFactory.testKeys_1(TEST_ENTITY_1));
        Mockito.when(configuration.getSecurityServiceExtMock().getTokenObjectByToken(tokenObject.getCurrentToken()))
               .thenReturn(securityToken);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadVersions(TEST_ETALON_ID_1, null, false, TEST_USER_NAME_1))
               .thenReturn(ov);
        Mockito.when(configuration.getMetaModelServiceMock().getValueById(TEST_ENTITY_1, EntityWrapper.class))
               .thenReturn(ew);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadEtalonBoundary(TEST_ETALON_ID_1, null, false))
               .thenReturn(BoolshitFactory.testTimeIntervalPO_1(TEST_ENTITY_1));
        Mockito.when(configuration.getSearchServiceExtMock().search(Mockito.any(SearchRequestContext.class)))
               .thenReturn(new SearchResultDTO());
        Mockito.when(configuration.getMetaModelServiceMock().getAttributesInfoMap(TEST_ENTITY_1))
               .thenReturn(ew.getAttributes());

        // after executor part
        final Map<String, SearchResultHitFieldDTO> fields = new HashMap<>();
        fields.put(RecordHeaderField.FIELD_ETALON_ID.getField(),
                new SearchResultHitFieldDTO(RecordHeaderField.FIELD_ETALON_ID.getField(), Collections.singletonList(TEST_ETALON_ID_1)));

        // '1 Nice display value.'
        fields.put("code_attr_1", new SearchResultHitFieldDTO("code_attr_1", Collections.singletonList(Long.valueOf(1L))));
        fields.put("string_attr_1", new SearchResultHitFieldDTO("string_attr_1", Collections.singletonList("Nice display value.")));

        final SearchResultDTO response = new SearchResultDTO();
        response.setTotalCount(1);
        response.setMaxScore(1.1F);
        response.setFields(Arrays.asList(RecordHeaderField.FIELD_ETALON_ID.getField(), "code_attr_1", "string_attr_1"));
        response.setHits(Arrays.asList(new SearchResultHitDTO(TEST_ETALON_ID_1, UUID.randomUUID().toString(), 1.1F, fields, null)));

        Mockito.when(configuration.getMetaModelServiceMock().getValueById(TEST_ENUM_1, EnumerationWrapper.class))
               .thenReturn(enw);
        Mockito.when(configuration.getMetaModelServiceMock().getLookupEntityById(TEST_LOOKUP_1))
               .thenReturn(lew.getEntity());
        Mockito.when(configuration.getSearchServiceExtMock().search(Mockito.any(ComplexSearchRequestContext.class)))
               .thenAnswer(in -> {
                   ComplexSearchRequestContext csrc = (ComplexSearchRequestContext) in.getArgument(0);
                   return Collections.singletonMap(csrc.getAllInnerContexts().iterator().next(), response);
               });

        // EnumerationWrapper ew = metaModelService.;
        try {

            GetRecordDTO result = recordServiceComponent.loadRecord(grc);

            Assert.assertNotNull(grc.getFromStorage(grc.keysId()));

            RecordKeys keys = grc.keys();

            Assert.assertEquals(keys.getEtalonKey().getId(), TEST_ETALON_ID_1);
            Assert.assertEquals(keys.getOriginKey().getId(), TEST_ORIGIN_ID_1);
            Assert.assertEquals(keys.getOriginKey().getExternalId(), TEST_EXTERNAL_ID_1);
            Assert.assertEquals(keys.getOriginKey().getSourceSystem(), TEST_SOURCE_SYSTEM_1);
            Assert.assertEquals(keys.getOriginKey().getEntityName(), TEST_ENTITY_1);
            Assert.assertTrue(
                    CollectionUtils.isNotEmpty(keys.getSupplementaryKeys())
                 && keys.getSupplementaryKeys().stream()
                     .anyMatch(ok -> TEST_ORIGIN_ID_2.equals(ok.getId())
                         && TEST_EXTERNAL_ID_2.equals(ok.getExternalId())
                         && TEST_ADMIN_SOURCE_SYSTEM.equals(ok.getSourceSystem())
                         && TEST_GSN_2 == ok.getGsn()
                         && TEST_ENTITY_1.equals(ok.getEntityName())));

            Assert.assertSame(keys, result.getRecordKeys());
            Assert.assertNotNull(result.getEtalon());
            Assert.assertNotNull(result.getEtalon().getAttribute("string_attr_1"));
            Assert.assertNotNull(result.getEtalon().getAttribute("string_attr_2"));
            Assert.assertNotNull(result.getEtalon().getAttribute("int_attr_3"));
            Assert.assertNotNull(result.getEtalon().getAttribute("int_attr_4"));
            Assert.assertNotNull(result.getEtalon().getAttribute("enum_attr_5"));
            Assert.assertNotNull(result.getEtalon().getAttribute("lookup_link_attr_6"));
            Assert.assertNotNull(result.getEtalon().getAttribute("template_attr_7"));
            Assert.assertArrayEquals(
                    new Object[] {
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_1")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_2")).getValue(), // Overwritten by TEST_SOURCE_SYSTEM_1
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_3")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_4")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("enum_attr_5")).getValue(), // Overwritten by TEST_SOURCE_SYSTEM_1
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("lookup_link_attr_6")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("template_attr_7")).getValue()
            },
                    new Object[] {
                    "string_val_1",
                    "string_val_2",
                    Long.valueOf(3),
                    Long.valueOf(4),
                    "VAL1",
                    Long.valueOf(1L),
                    "Value for replacement: string_val_1."
            });

            // Display values
            Assert.assertEquals("Just a value ONE.", ((SimpleAttribute<?>) result.getEtalon().getAttribute("enum_attr_5")).getDisplayValue());
            Assert.assertEquals("1 Nice display value.", ((SimpleAttribute<?>) result.getEtalon().getAttribute("lookup_link_attr_6")).getDisplayValue());
            Assert.assertEquals(TEST_ETALON_ID_1,  ((CodeLinkValue) ((SimpleAttribute<?>) result.getEtalon().getAttribute("lookup_link_attr_6"))).getLinkEtalonId());

        } finally {
            securityToken.getUser().setAdmin(true);
        }
    }

    @Test
    public void testLoadRecord_FlagsEntitySuccessful_SoftDeletedAndRelations() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_ENTITY_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .fetchRelations(true)
                .fetchSoftDeleted(true)
                .build();

        BearerToken tokenObject = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        SecurityToken securityToken = (SecurityToken) tokenObject.getDetails();

        // Temporary switch admin flag off.
        securityToken.getUser().setAdmin(false);

    }
/*
 * .fetchClusters(true)
 * .fetchRelations(true)
 * .fetchSoftDeleted(true)
 */
    @Test
    public void testLoadRecord_FlagsEntitySuccessful_OriginsAndClassifiers() {

        GetRequestContext grc = GetRequestContext.builder()
                .entityName(TEST_ENTITY_1)
                .sourceSystem(TEST_SOURCE_SYSTEM_1)
                .externalId(TEST_EXTERNAL_ID_1)
                .fetchClassifiers(true)
                .fetchOrigins(true)
                .build();

        BearerToken tokenObject = (BearerToken) SecurityContextHolder.getContext().getAuthentication();
        SecurityToken securityToken = (SecurityToken) tokenObject.getDetails();

        // Temporary switch admin flag off.
        securityToken.getUser().setAdmin(false);

        EntityWrapper ew = BoolshitFactory.testEntityWrapper_1(TEST_ENTITY_1, false, false, false);
        List<OriginsVistoryRecordPO> ov = BoolshitFactory.testVistoryPOs_1(TEST_ENTITY_1, false, false, false, false);
        List<EtalonClassifierPO> ec = BoolshitFactory.testEtalonClassifiers_1(TEST_CLASSIFIER_1);
        List<OriginsVistoryClassifierPO> ovcPO = BoolshitFactory.testClassifierVistory_1();
        ClassifierKeysPO keyPO = BoolshitFactory.testClassifierKey_1(TEST_CLASSIFIER_1, TEST_ENTITY_1);

        Mockito.when(configuration.getDataRecordsDaoMock().loadRecordKeysByExternalId(TEST_EXTERNAL_ID_1, TEST_SOURCE_SYSTEM_1, TEST_ENTITY_1))
               .thenReturn(BoolshitFactory.testKeys_1(TEST_ENTITY_1));
        Mockito.when(configuration.getSecurityServiceExtMock().getTokenObjectByToken(tokenObject.getCurrentToken()))
               .thenReturn(securityToken);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadVersions(TEST_ETALON_ID_1, null, false, TEST_USER_NAME_1))
               .thenReturn(ov);
        Mockito.when(configuration.getMetaModelServiceMock().getValueById(TEST_ENTITY_1, EntityWrapper.class))
               .thenReturn(ew);
        Mockito.when(configuration.getOriginsVistoryDaoMock().loadEtalonBoundary(TEST_ETALON_ID_1, null, false))
               .thenReturn(BoolshitFactory.testTimeIntervalPO_1(TEST_ENTITY_1));
        Mockito.when(configuration.getSearchServiceExtMock().search(Mockito.any(SearchRequestContext.class)))
               .thenReturn(new SearchResultDTO());
        Mockito.when(configuration.getMetaModelServiceMock().getAttributesInfoMap(TEST_ENTITY_1))
               .thenReturn(ew.getAttributes());
        Mockito.when(configuration.getClassifiersDaoMock().loadClassifierEtalons(TEST_ETALON_ID_1, TEST_CLASSIFIER_1, null))
               .thenReturn(ec);
        Mockito.when(configuration.getMetaModelServiceMock().getAdminSourceSystem())
               .thenReturn(new SourceSystemDef().withName(TEST_ADMIN_SOURCE_SYSTEM));
        Mockito.when(configuration.getClassifiersDaoMock().loadClassifierKeysByClassifierEtalonId(TEST_ADMIN_SOURCE_SYSTEM, TEST_CLASSIFIER_ETALON_ID_1))
               .thenReturn(keyPO);
        Mockito.when(configuration.getClsfServiceMock().isNodeExist(TEST_CLASSIFIER_NODE_ID_1, TEST_CLASSIFIER_1))
               .thenReturn(Boolean.TRUE);
        Mockito.when(configuration.getClassifiersDaoMock().loadClassifierVersions(TEST_CLASSIFIER_ETALON_ID_1, null, false))
               .thenReturn(ovcPO);
        Mockito.when(configuration.getMetaModelServiceMock().getReversedSourceSystems())
               .thenReturn(ModelUtils.createSourceSystemsMap(TEST_SOURCE_SYSTEMS_1, true));

        try {

            GetRecordDTO result = recordServiceComponent.loadRecord(grc);

            Assert.assertNotNull(grc.getFromStorage(grc.keysId()));

            RecordKeys keys = grc.keys();

            Assert.assertEquals(keys.getEtalonKey().getId(), TEST_ETALON_ID_1);
            Assert.assertEquals(keys.getOriginKey().getId(), TEST_ORIGIN_ID_1);
            Assert.assertEquals(keys.getOriginKey().getExternalId(), TEST_EXTERNAL_ID_1);
            Assert.assertEquals(keys.getOriginKey().getSourceSystem(), TEST_SOURCE_SYSTEM_1);
            Assert.assertEquals(keys.getOriginKey().getEntityName(), TEST_ENTITY_1);
            Assert.assertTrue(
                    CollectionUtils.isNotEmpty(keys.getSupplementaryKeys())
                 && keys.getSupplementaryKeys().stream()
                     .anyMatch(ok -> TEST_ORIGIN_ID_2.equals(ok.getId())
                         && TEST_EXTERNAL_ID_2.equals(ok.getExternalId())
                         && TEST_ADMIN_SOURCE_SYSTEM.equals(ok.getSourceSystem())
                         && TEST_GSN_2 == ok.getGsn()
                         && TEST_ENTITY_1.equals(ok.getEntityName())));

            Assert.assertSame(keys, result.getRecordKeys());
            Assert.assertNotNull(result.getEtalon());
            Assert.assertNotNull((result.getEtalon().getAttribute("string_attr_1")));
            Assert.assertNotNull((result.getEtalon().getAttribute("string_attr_2")));
            Assert.assertNotNull((result.getEtalon().getAttribute("int_attr_3")));
            Assert.assertNotNull((result.getEtalon().getAttribute("int_attr_4")));
            Assert.assertArrayEquals(
                    new Object[] {
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_1")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("string_attr_2")).getValue(), // Overwritten by TEST_SOURCE_SYSTEM_1
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_3")).getValue(),
                    ((SimpleAttribute<?>) result.getEtalon().getAttribute("int_attr_4")).getValue()
            },
                    new Object[] {
                    "string_val_1",
                    "string_val_2",
                    Long.valueOf(3),
                    Long.valueOf(4)
            });

            Assert.assertNotNull(result.getOrigins());
            Assert.assertFalse(result.getOrigins().isEmpty());
            Assert.assertTrue(result.getOrigins().size() == ov.size());
            Assert.assertTrue(result.getOrigins().stream()
                    .allMatch(o -> TEST_SOURCE_SYSTEMS_1.stream()
                            .anyMatch(ss -> ss.getName().equals(o.getInfoSection().getOriginKey().getSourceSystem()))));

            Assert.assertNotNull(result.getClassifiers());
            Assert.assertTrue(MapUtils.isNotEmpty(result.getClassifiers()));
            Assert.assertNotNull(result.getClassifiers().get(TEST_CLASSIFIER_1));

            EtalonClassifier ecl = result.getClassifiers().get(TEST_CLASSIFIER_1).iterator().next().getEtalon();
            List<OriginClassifier> ocls = result.getClassifiers().get(TEST_CLASSIFIER_1).iterator().next().getOrigins();

            Assert.assertNotNull(ecl);
            // Check BVR winner
            Assert.assertArrayEquals(new Object []{
                    "string_val_2",
                    Long.valueOf(5L)
            }, new Object[] {
                    ((SimpleAttribute<?>) ecl.getAttribute("string_attr_1")).getValue(),
                    ((SimpleAttribute<?>) ecl.getAttribute("int_attr_2")).getValue(),
            });

            Assert.assertTrue(CollectionUtils.isNotEmpty(ocls));
            Assert.assertTrue(ocls.size() == 2);

        } finally {
            securityToken.getUser().setAdmin(true);
        }
    }

    public static class BoolshitFactory {

        public static List<RecordKeysPO> testKeys_1(String entityName) {

            RecordKeysPO key1 = new RecordKeysPO();
            key1.setEnrich(null);
            key1.setEtalonGsn(TEST_GSN_1);
            key1.setEtalonId(TEST_ETALON_ID_1);
            key1.setEtalonName(entityName);
            key1.setEtalonState(ApprovalState.APPROVED);
            key1.setEtalonStatus(RecordStatus.ACTIVE);
            key1.setEtalonVersion(0);
            key1.setOriginExternalId(TEST_EXTERNAL_ID_1);
            key1.setOriginGsn(TEST_GSN_1);
            key1.setOriginId(TEST_ORIGIN_ID_1);
            key1.setOriginName(entityName);
            key1.setOriginRevision(0);
            key1.setOriginSourceSystem(TEST_SOURCE_SYSTEM_1);
            key1.setOriginStatus(RecordStatus.ACTIVE);
            key1.setOriginVersion(0);

            RecordKeysPO key2 = new RecordKeysPO();
            key2.setEnrich(null);
            key2.setEtalonGsn(TEST_GSN_2);
            key2.setEtalonId(TEST_ETALON_ID_1);
            key2.setEtalonName(entityName);
            key2.setEtalonState(ApprovalState.APPROVED);
            key2.setEtalonStatus(RecordStatus.ACTIVE);
            key2.setEtalonVersion(0);
            key2.setOriginExternalId(TEST_EXTERNAL_ID_2);
            key2.setOriginGsn(TEST_GSN_2);
            key2.setOriginId(TEST_ORIGIN_ID_2);
            key2.setOriginName(entityName);
            key2.setOriginRevision(0);
            key2.setOriginSourceSystem(TEST_ADMIN_SOURCE_SYSTEM);
            key2.setOriginStatus(RecordStatus.ACTIVE);
            key2.setOriginVersion(0);

            return Arrays.asList(key1, key2);
        }

        public static List<OriginsVistoryRecordPO> testVistoryPOs_1(String entityName,
                boolean asLookup, boolean withLookupLinks, boolean withEnumValues, boolean withTemplates) {

            SerializableDataRecord r1 = new SerializableDataRecord(2);
            r1.addAll(Arrays.asList(
                new Attribute [] {
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "string_attr_1", "string_val_1"),
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.INTEGER, "int_attr_4", Long.valueOf(4L))
                }
            ));

            if (asLookup) {
                r1.addAttribute(AbstractCodeAttribute.of(CodeDataType.STRING, "code_attr_1", "winner_code_attr_value"));
            }

            if (withEnumValues) {
                r1.addAttribute(AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "enum_attr_5", "VAL1"));
            }

            if (withLookupLinks) {
                r1.addAttribute(AbstractSimpleAttribute.of(SimpleAttribute.DataType.INTEGER, "lookup_link_attr_6", Long.valueOf(1L)));
            }

            if (withTemplates) {
                AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "template_attr_7", "Should be replaced by postprocessor.");
            }

            Date ts = new Date(System.currentTimeMillis());
            OriginsVistoryRecordPO po1 = new OriginsVistoryRecordPO();
            po1.setData(r1);
            po1.setApproval(ApprovalState.APPROVED);
            po1.setCreateDate(ts);
            po1.setCreatedBy(TEST_USER_NAME_1);
            po1.setEnrichment(false);
            po1.setExternalId(TEST_EXTERNAL_ID_1);
            po1.setGsn(TEST_GSN_1);
            po1.setId(UUID.randomUUID().toString());
            po1.setMajor(4);
            po1.setMinor(7);
            po1.setName(entityName);
            po1.setOperationId(TEST_OPERATION_ID_1);
            po1.setOriginId(TEST_ORIGIN_ID_1);
            po1.setRevision(1);
            po1.setShift(DataShift.PRISTINE);
            po1.setSourceSystem(TEST_SOURCE_SYSTEM_1);
            po1.setStatus(RecordStatus.ACTIVE);
            po1.setUpdateDate(ts);
            po1.setUpdatedBy(TEST_USER_NAME_1);
            po1.setValidFrom(null);
            po1.setValidTo(null);

            SerializableDataRecord r2 = new SerializableDataRecord(2);
            r2.addAll(Arrays.asList(
                new Attribute [] {
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "string_attr_1", "should be overwritten"),
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "string_attr_2", "string_val_2"),
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.INTEGER, "int_attr_3", Long.valueOf(3L))
                }
            ));

            if (asLookup) {
                r2.addAttribute(AbstractCodeAttribute.of(CodeDataType.STRING, "code_attr_1", "supplementary_code_attr_value"));
            }

            Date ts2 = new Date(ts.getTime() - 7777);
            OriginsVistoryRecordPO po2 = new OriginsVistoryRecordPO();
            po2.setData(r2);
            po2.setApproval(ApprovalState.APPROVED);
            po2.setCreateDate(ts2);
            po2.setCreatedBy(TEST_USER_NAME_2);
            po2.setEnrichment(false);
            po2.setExternalId(TEST_EXTERNAL_ID_2);
            po2.setGsn(TEST_GSN_2);
            po2.setId(UUID.randomUUID().toString());
            po2.setMajor(4);
            po2.setMinor(7);
            po2.setName(entityName);
            po2.setOperationId(TEST_OPERATION_ID_2);
            po2.setOriginId(TEST_ORIGIN_ID_2);
            po2.setRevision(1);
            po2.setShift(DataShift.PRISTINE);
            po2.setSourceSystem(TEST_ADMIN_SOURCE_SYSTEM);
            po2.setStatus(RecordStatus.ACTIVE);
            po2.setUpdateDate(ts2);
            po2.setUpdatedBy(TEST_USER_NAME_2);
            po2.setValidFrom(null);
            po2.setValidTo(null);

            return Arrays.asList(po1, po2);
        }

        public static ClassifierKeysPO testClassifierKey_1(String classifierName, String recordName) {

            ClassifierKeysPO po = new ClassifierKeysPO();

            // CLSF etalon
            po.setEtalonId(TEST_CLASSIFIER_ETALON_ID_1);
            po.setEtalonIdRecord(TEST_ETALON_ID_1);
            po.setEtalonName(classifierName);
            // Etalon record
            po.setEtalonRecordName(recordName);
            po.setEtalonRecordState(ApprovalState.APPROVED);
            po.setEtalonRecordStatus(RecordStatus.ACTIVE);
            po.setEtalonState(ApprovalState.APPROVED);
            po.setEtalonStatus(RecordStatus.ACTIVE);
            // CLSF origin
            po.setOriginId(TEST_CLASSIFIER_ORIGIN_ID_1);
            po.setOriginName(classifierName);
            po.setOriginNodeId(TEST_CLASSIFIER_NODE_ID_1);
            po.setOriginRevision(1);
            po.setOriginSourceSystem(TEST_SOURCE_SYSTEM_1);
            po.setOriginStatus(RecordStatus.ACTIVE);
            // Origin record
            po.setOriginIdRecord(TEST_ORIGIN_ID_1);
            po.setOriginRecordExternalId(TEST_EXTERNAL_ID_1);
            po.setOriginRecordName(recordName);
            po.setOriginRecordSourceSystem(TEST_SOURCE_SYSTEM_1);
            po.setOriginRecordStatus(RecordStatus.ACTIVE);

            return po;
        }

        public static List<OriginsVistoryClassifierPO> testClassifierVistory_1() {

            Date ts = new Date(System.currentTimeMillis());
            OriginsVistoryClassifierPO po1 = new OriginsVistoryClassifierPO();

            SerializableDataRecord r1 = new SerializableDataRecord(2);
            r1.addAll(Arrays.asList(
                new Attribute [] {
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "string_attr_1", "string_val_1"),
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.INTEGER, "int_attr_2", Long.valueOf(4L))
                }
            ));

            po1.setApproval(ApprovalState.APPROVED);
            po1.setCreateDate(new Date(ts.getTime() - 100000));
            po1.setCreatedBy(TEST_USER_NAME_1);
            po1.setUpdateDate(new Date(ts.getTime() - 100000));
            po1.setUpdatedBy(TEST_USER_NAME_1);
            po1.setData(r1);
            po1.setEtalonId(TEST_CLASSIFIER_ETALON_ID_1);
            po1.setId(UUID.randomUUID().toString());
            po1.setMajor(4);
            po1.setMinor(7);
            po1.setName(TEST_CLASSIFIER_1);
            po1.setNodeId(TEST_CLASSIFIER_NODE_ID_1);
            po1.setOperationId(UUID.randomUUID().toString());
            po1.setOriginId(TEST_CLASSIFIER_ORIGIN_ID_1);
            po1.setOriginIdRecord(TEST_ORIGIN_ID_1);
            po1.setOriginRecordExternalId(TEST_EXTERNAL_ID_1);
            po1.setOriginRecordName(TEST_ENTITY_1);
            po1.setOriginRecordSourceSystem(TEST_SOURCE_SYSTEM_1);
            po1.setOriginRecordStatus(RecordStatus.ACTIVE);
            po1.setRevision(1);
            po1.setShift(DataShift.PRISTINE);
            po1.setSourceSystem(TEST_SOURCE_SYSTEM_1);
            po1.setStatus(RecordStatus.ACTIVE);

            OriginsVistoryClassifierPO po2 = new OriginsVistoryClassifierPO();

            SerializableDataRecord r2 = new SerializableDataRecord(2);
            r2.addAll(Arrays.asList(
                new Attribute [] {
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.STRING, "string_attr_1", "string_val_2"),
                      AbstractSimpleAttribute.of(SimpleAttribute.DataType.INTEGER, "int_attr_2", Long.valueOf(5L))
                }
            ));

            po2.setApproval(ApprovalState.APPROVED);
            po2.setCreateDate(ts);
            po2.setCreatedBy(TEST_USER_NAME_2);
            po2.setUpdateDate(ts);
            po2.setUpdatedBy(TEST_USER_NAME_2);
            po2.setData(r2);
            po2.setEtalonId(TEST_CLASSIFIER_ETALON_ID_1);
            po2.setId(UUID.randomUUID().toString());
            po2.setMajor(4);
            po2.setMinor(7);
            po2.setName(TEST_CLASSIFIER_1);
            po2.setNodeId(TEST_CLASSIFIER_NODE_ID_1);
            po2.setOperationId(UUID.randomUUID().toString());
            po2.setOriginId(TEST_CLASSIFIER_ORIGIN_ID_2);
            po2.setOriginIdRecord(TEST_ORIGIN_ID_2);
            po2.setOriginRecordExternalId(TEST_EXTERNAL_ID_2);
            po2.setOriginRecordName(TEST_ENTITY_1);
            po2.setOriginRecordSourceSystem(TEST_ADMIN_SOURCE_SYSTEM);
            po2.setOriginRecordStatus(RecordStatus.ACTIVE);
            po2.setRevision(1);
            po2.setShift(DataShift.PRISTINE);
            po2.setSourceSystem(TEST_ADMIN_SOURCE_SYSTEM);
            po2.setStatus(RecordStatus.ACTIVE);

            return Arrays.asList(po1, po2);
        }

        public static List<EtalonClassifierPO> testEtalonClassifiers_1(String name) {

            Date ts = new Date(System.currentTimeMillis());
            EtalonClassifierPO po1 = new EtalonClassifierPO();

            po1.setApproval(ApprovalState.APPROVED);
            po1.setCreateDate(ts);
            po1.setCreatedBy(TEST_USER_NAME_1);
            po1.setEtalonIdRecord(TEST_ETALON_ID_1);
            po1.setGsn(Long.MIN_VALUE);
            po1.setId(TEST_CLASSIFIER_ETALON_ID_1);
            po1.setName(name);
            po1.setOperationId(UUID.randomUUID().toString());
            po1.setStatus(RecordStatus.ACTIVE);

            return Arrays.asList(po1);
        }

        public static TimeIntervalPO testTimeIntervalPO_1(String entityName) {

            TimeIntervalPO po = new TimeIntervalPO();

            po.setName(entityName);
            po.setEtalonGsn(TEST_GSN_1);
            po.setState(ApprovalState.APPROVED);
            po.setStatus(RecordStatus.ACTIVE);
            po.setPeriodId(0L);

            return po;
        }

        public static EntityWrapper testEntityWrapper_1(String entityName, boolean generateLookupLinks, boolean generateEnumValues, boolean generateTemplates) {

            EntityDef def= JaxbUtils.getMetaObjectFactory().createEntityDef();
            def.withSimpleAttribute(
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.STRING)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("string_attr_1")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false),
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.STRING)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("string_attr_2")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false),
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.INTEGER)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("int_attr_3")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false),
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.INTEGER)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("int_attr_4")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false)
                )
               .withName(entityName == null ? TEST_ENTITY_1 : entityName)
               .withClassifiers(TEST_CLASSIFIER_1)
               .withVersion(1L);

            if (generateEnumValues) {
                def.withSimpleAttribute(JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withEnumDataType(TEST_ENUM_1)
                        .withName("enum_attr_5")
                        .withDisplayable(true)
                        .withHidden(false)
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false));
            }

            if (generateLookupLinks) {

                def.withSimpleAttribute(JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withLookupEntityType(TEST_LOOKUP_1)
                        .withLookupEntityCodeAttributeType(SimpleDataType.INTEGER)
                        .withLookupEntityDisplayAttributes("code_attr_1", "string_attr_1")
                        .withName("lookup_link_attr_6")
                        .withDisplayable(true)
                        .withHidden(false)
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false));
            }

            if (generateTemplates) {
                def.withSimpleAttribute(JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withLinkDataType("Value for replacement: {string_attr_1}.")
                        .withName("template_attr_7")
                        .withDisplayable(true)
                        .withHidden(false)
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false));
            }

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(def, null);
            Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(def, TEST_SOURCE_SYSTEMS_1, attrs);

            return new EntityWrapper(def, def.getName(), attrs, bvtMap);
        }

        public static EnumerationWrapper testEnumWrapper_1(String enumName) {

            EnumerationDataType enumeration = JaxbUtils.getMetaObjectFactory().createEnumerationDataType()
                .withName(enumName)
                .withDisplayName("Test enumeration.")
                .withEnumVal(
                    JaxbUtils.getMetaObjectFactory().createEnumerationValue()
                        .withName("VAL1")
                        .withDisplayName("Just a value ONE."),
                    JaxbUtils.getMetaObjectFactory().createEnumerationValue()
                        .withName("VAL2")
                        .withDisplayName("Just a value TWO."));

            return new EnumerationWrapper(enumeration);
        }

        public static LookupEntityWrapper testLookupWrapper_1(String entityName) {

            LookupEntityDef def= JaxbUtils.getMetaObjectFactory().createLookupEntityDef();
            def .withCodeAttribute(JaxbUtils.getMetaObjectFactory().createCodeAttributeDef()
                    .withName("code_attr_1")
                    .withSimpleDataType(SimpleDataType.STRING)
                    .withHidden(false)
                    .withMainDisplayable(true)
                    .withNullable(false)
                    .withSearchable(true)
                    .withUnique(false))
                .withSimpleAttribute(
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.STRING)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("string_attr_1")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false),
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.STRING)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("string_attr_2")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false),
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.INTEGER)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("int_attr_3")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false),
                    JaxbUtils.getMetaObjectFactory().createSimpleAttributeDef()
                        .withSimpleDataType(SimpleDataType.INTEGER)
                        .withDisplayable(true)
                        .withHidden(false)
                        .withMainDisplayable(true)
                        .withName("int_attr_4")
                        .withNullable(false)
                        .withReadOnly(false)
                        .withSearchable(true)
                        .withUnique(false)
                )
               .withName(entityName == null ? TEST_LOOKUP_1 : entityName)
               .withVersion(1L);


            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(def, null);
            Map<String, Map<String, Integer>> bvtMap = ModelUtils.createBvtMap(def, TEST_SOURCE_SYSTEMS_1, attrs);

            return new LookupEntityWrapper(def, def.getName(), attrs, bvtMap);
        }
    }
}
