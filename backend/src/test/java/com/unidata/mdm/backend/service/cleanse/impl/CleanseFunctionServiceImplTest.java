package com.unidata.mdm.backend.service.cleanse.impl;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.io.ByteStreams;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.dao.SystemElementsDao;
import com.unidata.mdm.backend.po.initializer.ActionTypePO;
import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;
import com.unidata.mdm.backend.service.cleanse.CFAppContext;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.Model;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * The Class CleanseFunctionServiceImplTest.
 * @author ilya.bykov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/test-dq-context.xml" })
@PrepareForTest({ CFAppContext.class, MessageUtils.class })
@Transactional
public class CleanseFunctionServiceImplTest {

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

    /**
     * Test get all.
     *
     * @throws CleanseFunctionExecutionException the cleanse function execution exception
     */
    @Test
    public void testGetAll() throws CleanseFunctionExecutionException {
        assertNotNull(cleanseFunctionService.getAll());

    }

    /**
     * Test get by ID.
     */
    @Test
    public void testGetByID() {
        assertNotNull(cleanseFunctionService.getByID("Строковые.Соединить"));
    }

    /**
     * Test execute basic.
     *
     * @throws CleanseFunctionExecutionException the cleanse function execution exception
     */
    @Test
    public void testExecuteBasic() throws CleanseFunctionExecutionException {
        assertNotNull(cleanseFunctionService.getByID("Строковые.Соединить"));
        Map<String, Object> input = new HashMap<>();
        StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", "1");
        input.put("port1", port1);
        StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "2");
        input.put("port2", port2);
        StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", "3");
        input.put("port3", port3);
        StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", "4");
        input.put("port4", port4);
        Map<String, Object> output = cleanseFunctionService.executeSingle(input, "Строковые.Соединить");
        assertNotNull(output);
        assertEquals(1, output.size());
        assertNotNull(output.get("port1"));
        assertEquals(true, output.get("port1") instanceof StringSimpleAttributeImpl);
        assertEquals("1234", ((StringSimpleAttributeImpl) output.get("port1")).getValue());
    }

    /**
     * Test composite cleanse function.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CleanseFunctionExecutionException the cleanse function execution exception
     */
    @Test
    public void testCompositeCleanseFunction() throws IOException, CleanseFunctionExecutionException {
        Model model = JaxbUtils.createModelFromInputStream(ClassLoader.getSystemResourceAsStream("model/dq/model.xml"));
        ListOfCleanseFunctions cleanseFunctions = model.getCleanseFunctions();
        List<Serializable> list = cleanseFunctions.getGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction();
        for (Serializable el : list) {
            if (el instanceof CompositeCleanseFunctionDef) {
                CompositeCleanseFunctionDef cleanseFunctionDef = (CompositeCleanseFunctionDef) el;
                cleanseFunctionService.upsertCompositeCleanseFunction("ПолноеИмя", cleanseFunctionDef);
            }
        }

        assertNotNull(cleanseFunctionService.getByID("ПолноеИмя"));
        Map<String, Object> input = new HashMap<>();
        StringSimpleAttributeImpl port0 = new StringSimpleAttributeImpl("port0", "  ivanov");
        input.put("port0", port0);
        StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("port1", " ");
        input.put("port1", port1);
        StringSimpleAttributeImpl port2 = new StringSimpleAttributeImpl("port2", "ivan");
        input.put("port2", port2);
        StringSimpleAttributeImpl port3 = new StringSimpleAttributeImpl("port3", " ");
        input.put("port3", port3);
        StringSimpleAttributeImpl port4 = new StringSimpleAttributeImpl("port4", "ivanovich");
        input.put("port4", port4);
        Map<String, Object> output = cleanseFunctionService.executeSingle(input, "ПолноеИмя");
        assertNotNull(output);
        assertEquals(1, output.size());
        assertNotNull(output.get("port0"));
        assertEquals(true, output.get("port0") instanceof StringSimpleAttributeImpl);
        assertEquals("IVANOV IVAN IVANOVICH", ((StringSimpleAttributeImpl) output.get("port0")).getValue());
    }

    /**
     * Test custom cleanse function.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws CleanseFunctionExecutionException the cleanse function execution exception
     */
    @Test
    @Ignore
    public void testCustomCleanseFunction() throws IOException, CleanseFunctionExecutionException {
        CFCustomUploaderResponse response = cleanseFunctionService.preloadAndValidateCustomFunction(Paths
                        .get(ClassLoader.getSystemResource("model/dq/testCleanseFunc.jar").toString().replaceAll("file:/", "")),
                true);
        assertNotNull(response);
        cleanseFunctionService.loadAndInit(response.getTemporaryId());
        Map<String, Object> input = new HashMap<>();
        StringSimpleAttributeImpl port1 = new StringSimpleAttributeImpl("INN", "7707083893");
        input.put("INN", port1);
        Map<String, Object> output = cleanseFunctionService.executeSingle(input, "CustomValidateINN");
        assertNotNull(output);
        assertEquals(1, output.size());
        assertNotNull(output.get("isValid"));
        assertEquals(true, output.get("isValid") instanceof BooleanSimpleAttributeImpl);
        assertEquals(true, ((BooleanSimpleAttributeImpl) output.get("isValid")).getValue());

    }

    /**
     * Inits the.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        SystemElementPO elementPO = new SystemElementPO()
                .withFolder(ClassLoader.getSystemResource("model/dq/testCleanseFunc.jar").toString()
                        .replaceAll("file:/", "").replaceAll("/testCleanseFunc.jar", ""))
                .withAction(ActionTypePO.CREATE)
                .withContent(
                        ByteStreams.toByteArray(ClassLoader.getSystemResourceAsStream("model/dq/testCleanseFunc.jar")))
                .withType(ElementTypePO.CUSTOM_CF).withName("testCleanseFunc.jar");

        when(systemElementsDao.getByNameAndPath(anyString(), anyString()))
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
