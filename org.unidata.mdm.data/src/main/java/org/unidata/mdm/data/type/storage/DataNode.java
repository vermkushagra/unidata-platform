package org.unidata.mdm.data.type.storage;

import java.util.Date;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * A data node.
 */
public class DataNode {
    /**
     * Assigned number.
     */
    private final int number;
    /**
     * The node's name/alias.
     */
    private final String name;
    /**
     * The host name.
     */
    private final String host;
    /**
     * Database port.
     */
    private final int port;
    /**
     * The database name.
     */
    private final String database;
    /**
     * The database user.
     */
    private final String user;
    /**
     * The database password.
     */
    private final String password;
    /**
     * The pool settings.
     */
    private final Map<PoolSetting, String> settings;
    /**
     * Create mark.
     */
    private final Date createDate;
    /**
     * Update mark.
     */
    private final Date updateDate;
    /**
     * Constructor.
     */
    private DataNode(DataNodeBuilder b) {
        super();
        this.settings = b.settings;
        this.number = b.number;
        this.name = b.name;
        this.host = b.host;
        this.port = b.port;
        this.database = b.database;
        this.user = b.user;
        this.password = b.password;
        this.createDate = b.createDate;
        this.updateDate = b.updateDate;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the dataSource
     */
    public Map<PoolSetting, String> getSettings() {
        return settings;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @return new builder instance
     */
    public static DataNodeBuilder builder() {
        return new DataNodeBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder.
     */
    public static class DataNodeBuilder {
        /**
         * Assigned number.
         */
        private int number;
        /**
         * The node's name/alias.
         */
        private String name;
        /**
         * The database host.
         */
        private String host;
        /**
         * Database port.
         */
        private int port;
        /**
         * The database name.
         */
        private String database;
        /**
         * The database user.
         */
        private String user;
        /**
         * The database password.
         */
        private String password;
        /**
         * The data source.
         */
        private Map<PoolSetting, String> settings;
        /**
         * Create mark.
         */
        private Date createDate;
        /**
         * Update mark.
         */
        private Date updateDate;
        /**
         * Constructor.
         */
        private DataNodeBuilder() {
            super();
        }
        /**
         * @param number the number to set
         */
        public DataNodeBuilder number(int number) {
            this.number = number;
            return this;
        }
        /**
         * @param name the name to set
         */
        public DataNodeBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param host the host to set
         */
        public DataNodeBuilder host(String host) {
            this.host = host;
            return this;
        }
        /**
         * @param port the port to set
         */
        public DataNodeBuilder port(int port) {
            this.port = port;
            return this;
        }
        /**
         * @param database the database to set
         */
        public DataNodeBuilder database(String database) {
            this.database = database;
            return this;
        }
        /**
         * @param user the user to set
         */
        public DataNodeBuilder user(String user) {
            this.user = user;
            return this;
        }
        /**
         * @param password the password to set
         */
        public DataNodeBuilder password(String password) {
            this.password = password;
            return this;
        }
        /**
         * @param dataSource the dataSource to set
         */
        public DataNodeBuilder settings(Map<PoolSetting, String> settings) {
            this.settings = settings;
            return this;
        }
        /**
         * @param createDate the createDate to set
         */
        public DataNodeBuilder createDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }
        /**
         * @param updateDate the updateDate to set
         */
        public DataNodeBuilder updateDate(Date updateDate) {
            this.updateDate = updateDate;
            return this;
        }
        /**
         * Instance.
         * @return instance
         */
        public DataNode build() {
            return new DataNode(this);
        }
    }
}
