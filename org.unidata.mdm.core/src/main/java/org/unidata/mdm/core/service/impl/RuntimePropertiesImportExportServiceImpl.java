package org.unidata.mdm.core.service.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.context.SaveLargeObjectRequestContext;
import org.unidata.mdm.core.context.UpsertUserEventRequestContext;
import org.unidata.mdm.core.dto.UserEventDTO;
import org.unidata.mdm.core.service.LargeObjectsServiceComponent;
import org.unidata.mdm.core.service.RuntimePropertiesImportExportService;
import org.unidata.mdm.core.service.UserService;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.system.service.RuntimePropertiesService;
import org.unidata.mdm.system.service.impl.RuntimePropertiesServiceImpl;
import org.unidata.mdm.system.type.configuration.ConfigurationProperty;
import org.unidata.mdm.system.util.MessageUtils;

/**
 * @author Alexander Malyshev
 */
@Service
public class RuntimePropertiesImportExportServiceImpl implements RuntimePropertiesImportExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimePropertiesImportExportServiceImpl.class);

    private static final String CONFIG_EXPORT_SUCCESS = "app.user.events.export.config.success";
    private static final String CONFIG_EXPORT_FAIL = "app.user.events.export.config.fail";
    private static final String CONFIG_IMPORT_SUCCESS = "app.user.events.import.config.success";
    private static final String CONFIG_IMPORT_FAIL = "app.user.events.import.config.fail";

    private final RuntimePropertiesService runtimePropertiesService;

    private final UserService userService;

    private final LargeObjectsServiceComponent largeObjectsServiceComponent;

    private final MessageUtils messageUtils;

    public RuntimePropertiesImportExportServiceImpl(
            final RuntimePropertiesService runtimePropertiesService,
            final UserService userService,
            final LargeObjectsServiceComponent largeObjectsServiceComponent, MessageUtils messageUtils) {
        this.runtimePropertiesService = runtimePropertiesService;
        this.userService = userService;
        this.largeObjectsServiceComponent = largeObjectsServiceComponent;
        this.messageUtils = messageUtils;
    }

    @Override
    public void exportProperties() {
        final String data = runtimePropertiesService.availableProperties().stream()
                .collect(Collectors.groupingBy(p -> p.getProperty().getGroupKey()))
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(this::generateGroupString)
                .collect(Collectors.joining("\n"));
        sendExportDataToUser(data, SecurityUtils.getCurrentUserName());
    }

    private String generateGroupString(Map.Entry<String, List<ConfigurationProperty>> e) {
        return "# " + e.getKey() + "\n" + e.getValue().stream()
                .sorted(Comparator.comparing(p -> p.getProperty().getKey()))
                .map(this::generatePropertyString)
                .collect(Collectors.joining("\n"));
    }

    private String generatePropertyString(ConfigurationProperty p) {
        return "## " + messageUtils.getMessage(p.getProperty().getKey()) + "\n"
                + p.getProperty().getKey() + "="
                + (p.getValue() == null ? RuntimePropertiesServiceImpl.NULL_VALUE_PLACE_HOLDER : p.getValue());
    }

    private void sendExportDataToUser(
            final String data,
            final String currentUserName
    ) {
        final UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder configExportUserEvent =
                new UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder()
                        .login(currentUserName)
                        .type("CONFIG_EXPORT");
        try (final InputStream is = new ByteArrayInputStream(data.getBytes())) {
            final UpsertUserEventRequestContext upsertUserEventRequestContext =
                    configExportUserEvent
                            .content(messageUtils.getMessage(CONFIG_EXPORT_SUCCESS))
                            .build();
            final UserEventDTO userEventDTO = userService.upsert(upsertUserEventRequestContext);
            final SaveLargeObjectRequestContext saveLargeObjectRequestContext =
                    new SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder()
                            .eventKey(userEventDTO.getId())
                            .mimeType("text/plain")
                            .binary(false)
                            .inputStream(is)
                            .filename(fileName())
                            .build();
            largeObjectsServiceComponent.saveLargeObject(saveLargeObjectRequestContext);
        } catch (IOException e) {
            LOGGER.error("Can't export backend configuration file", e);
            final UpsertUserEventRequestContext upsertUserEventRequestContext =
                    configExportUserEvent
                            .content(messageUtils.getMessage(CONFIG_EXPORT_FAIL))
                            .build();
            userService.upsert(upsertUserEventRequestContext);
        }
    }

    private String fileName() {
        try {
            return URLEncoder.encode(
                    "config_"
                            + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss")
                            + ".properties",
                    StandardCharsets.UTF_8.name()
            );
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error generating properties file name", e);
        }
        return "backend.properties";
    }

    @Override
    public void importProperties(Path path) {
        final String currentUserName = SecurityUtils.getCurrentUserName();
        final UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder configImportUserEvent =
                new UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder()
                        .login(currentUserName)
                        .type("CONFIG_IMPORT");
        final UpsertUserEventRequestContext upsertUserEventRequestContext =
                configImportUserEvent
                        .content(messageUtils.getMessage(importFile(path)))
                        .build();
        userService.upsert(upsertUserEventRequestContext);
    }

    private String importFile(Path path) {
        final Properties properties = new Properties();
        try (final InputStream inputStream = new FileInputStream(path.toFile())) {
            properties.load(inputStream);
            runtimePropertiesService.updatePropertiesValues(
                    properties.entrySet().stream()
                            .collect(
                                    Collectors.toMap(
                                            e -> e.getKey().toString(),
                                            e -> e.getValue().toString()
                                    )
                            )
            );
            return CONFIG_IMPORT_SUCCESS;
        } catch (Exception e) {
            LOGGER.error("Can't import backend configuration file", e);
            return CONFIG_IMPORT_FAIL;
        }
    }
}
