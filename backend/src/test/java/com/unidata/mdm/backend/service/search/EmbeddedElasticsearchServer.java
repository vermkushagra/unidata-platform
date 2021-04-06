package com.unidata.mdm.backend.service.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

public class EmbeddedElasticsearchServer {
    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticsearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticsearchServer(String dataDirectory) {

        this.dataDirectory = dataDirectory;
        Path path = Paths.get(dataDirectory);
        boolean isExist = path.toFile().exists();
        if (!isExist) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Settings elasticsearchSettings = Settings.builder()
               .put("http.enabled", "false")
               .put("path.data", dataDirectory)
               .put("transport.type", "local")
               .build();

        node = new Node(elasticsearchSettings);
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            Files.deleteIfExists(Paths.get(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }
}
