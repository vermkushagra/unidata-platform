package org.unidata.mdm.search.module;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.system.type.module.Module;

public class SearchModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchModule.class);
    /**
     * This module id.
     */
    public static final String MODULE_ID = "org.unidata.mdm.search";

    @Autowired
    private Client client;

    @Override
    public String getId() {
        return MODULE_ID;
    }

    @Override
    public String getVersion() {
        return "5.2";
    }

    @Override
    public String getName() {
        return "Unidata search";
    }

    @Override
    public String getDescription() {
        return "Unidata search module";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getResourceBundleBasenames() {
        return new String[]{ "search_messages" };
    }

    @Override
    public void install() {
        LOGGER.info("Install");

    }

    @Override
    public void uninstall() {
        LOGGER.info("Uninstall");

    }

    @Override
    public void start() {
        LOGGER.info("Starting...");


        LOGGER.info("Started");
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping...");
        client.close();
        client.threadPool().shutdownNow();
        LOGGER.info("Stopped.");
    }
}
