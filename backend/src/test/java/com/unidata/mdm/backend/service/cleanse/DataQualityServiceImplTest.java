package com.unidata.mdm.backend.service.cleanse;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.ByteStreams;
import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.DataQualityStatus;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SeverityType;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.dao.SystemElementsDao;
import com.unidata.mdm.backend.po.initializer.ActionTypePO;
import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;
import com.unidata.mdm.backend.service.cleanse.impl.CleanseFunctionServiceImpl;
import com.unidata.mdm.backend.service.cleanse.impl.MetaModelServiceDQMock;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * The Class DataQualityServiceImplTest.
 *
 * @author ilya.bykov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/test-dq-context.xml" })
@PrepareForTest({ CFAppContext.class, MessageUtils.class })
@Transactional
public class DataQualityServiceImplTest {
    /** The search service. */
    @Mock
    private SearchService searchService;

    /** The message source. */
    @Mock
    private MessageSource messageSource;

    /** The configuration service. */
    @Mock
    private ConfigurationService configurationService;

    /** The system elements dao. */
    @Mock
    private SystemElementsDao systemElementsDao;

    /** The cleanse function service. */
    @InjectMocks
    @Autowired
    private CleanseFunctionServiceImpl cleanseFunctionService;

    /** The dq service. */
    @InjectMocks
    @Autowired
    private DataQualityServiceImpl dqService;

    /** The metamodel service mock. */
    @InjectMocks
    @Autowired
    private MetaModelServiceDQMock metamodelServiceMock;

    /**
     * Test apply rules valid record.
     */
    @Test
    public void testApplyRulesValidRecord() {
        GetEntityDTO entityDTO = metamodelServiceMock.getEntityById("product");
        DataRecord record = new SerializableDataRecord();
        record.addAttribute(new StringSimpleAttributeImpl("name", "name"));
        record.addAttribute(new StringSimpleAttributeImpl("code", "code"));
        record.addAttribute(new NumberSimpleAttributeImpl("price", 12.12d));
        List<DQRuleDef> rules = entityDTO.getEntity().getDataQualities();
        DQContext<DataRecord> ctx = new DQContext<>().withEntityName("product").withRecordId("123")
                .withRecordValidFrom(new Date()).withRecordValidTo(new Date()).withRules(rules).withRecord(record);
        dqService.applyRules(ctx);
        assertEquals(0, ctx.getErrors().size());
        assertEquals("NAME", ((StringSimpleAttributeImpl) ctx.getRecord().getAttribute("name")).getValue());
        assertEquals("code", ((StringSimpleAttributeImpl) ctx.getRecord().getAttribute("code")).getValue());
        assertEquals(new Double(12.12d),
                ((NumberSimpleAttributeImpl) ctx.getRecord().getAttribute("price")).getValue());
    }

    /**
     * Test apply rules invalid record.
     */
    @Test
    public void testApplyRulesInvalidRecord() {
        GetEntityDTO entityDTO = metamodelServiceMock.getEntityById("product");
        DataRecord record = new SerializableDataRecord();
        record.addAttribute(new StringSimpleAttributeImpl("name", "n"));
        record.addAttribute(new StringSimpleAttributeImpl("code", "code"));
        record.addAttribute(new NumberSimpleAttributeImpl("price", -12.12d));
        List<DQRuleDef> rules = entityDTO.getEntity().getDataQualities();
        DQContext<DataRecord> ctx = new DQContext<>().withEntityName("product").withRecordId("123")
                .withRecordValidFrom(new Date()).withRecordValidTo(new Date()).withRules(rules).withRecord(record);
        dqService.applyRules(ctx);
        assertEquals(2, ctx.getErrors().size());
        assertEquals("Product checks", ctx.getErrors().get(0).getCategory());
        assertEquals("product_name_length", ctx.getErrors().get(0).getRuleName());
        assertEquals(SeverityType.HIGH, ctx.getErrors().get(0).getSeverity());
        assertEquals(DataQualityStatus.NEW, ctx.getErrors().get(0).getStatus());
        assertEquals("Product name should be more than 3 and less than 20 symbols",
                ctx.getErrors().get(0).getMessage());
        assertEquals("Product checks", ctx.getErrors().get(1).getCategory());
        assertEquals("product_price_size", ctx.getErrors().get(1).getRuleName());
        assertEquals(SeverityType.HIGH, ctx.getErrors().get(1).getSeverity());
        assertEquals(DataQualityStatus.NEW, ctx.getErrors().get(1).getStatus());
        assertEquals("Product price should be greater than 1 and less than million",
                ctx.getErrors().get(1).getMessage());
        assertEquals("N", ((StringSimpleAttributeImpl) ctx.getRecord().getAttribute("name")).getValue());
        assertEquals("code", ((StringSimpleAttributeImpl) ctx.getRecord().getAttribute("code")).getValue());
        assertEquals(new Double(-12.12d),
                ((NumberSimpleAttributeImpl) ctx.getRecord().getAttribute("price")).getValue());

    }

    /**
     * Inits the.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
//        PowerMockito.mockStatic(CFAppContext.class);
//        PowerMockito.mockStatic(MessageUtils.class);
        SystemElementPO elementPO = new SystemElementPO()
                .withFolder(ClassLoader.getSystemResource("model/dq/testCleanseFunc.jar").toString()
                        .replaceAll("file:/", "").replaceAll("/testCleanseFunc.jar", ""))
                .withAction(ActionTypePO.CREATE)
                .withContent(
                        ByteStreams.toByteArray(ClassLoader.getSystemResourceAsStream("model/dq/testCleanseFunc.jar")))
                .withType(ElementTypePO.CUSTOM_CF).withName("testCleanseFunc.jar");

        Mockito.when(systemElementsDao.getByNameAndPath(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(elementPO);
        cleanseFunctionService.afterContextRefresh();
    }

    @BeforeClass
    public static void initClass() {
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getMessage(anyString(), any(), any(Locale.class))).then(invocation -> invocation.getArgument(0));
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(anyString(), anyString())).then(invocation -> invocation.getArgument(1));
        when(applicationContext.getEnvironment()).thenReturn(environment);
        MessageUtils.init(applicationContext);
    }
}
