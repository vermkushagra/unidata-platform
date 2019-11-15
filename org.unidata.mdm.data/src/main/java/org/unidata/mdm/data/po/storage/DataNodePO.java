package org.unidata.mdm.data.po.storage;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.unidata.mdm.data.type.storage.PoolSetting;

/**
 * @author Mikhail Mikhailov
 * Node attrs.
 */
public class DataNodePO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "nodes_info";
    /**
     * Generated id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Node's name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * PostgreSQL host name.
     */
    public static final String FIELD_HOST = "host";
    /**
     * PostgreSQL port number.
     */
    public static final String FIELD_PORT = "port";
    /**
     * PostgreSQL database name.
     */
    public static final String FIELD_DATABASE = "dbname";
    /**
     * PostgreSQL database user.
     */
    public static final String FIELD_USER = "username";
    /**
     * PostgreSQL database password.
     */
    public static final String FIELD_PASSWORD = "password";
    /**
     * Bitronix settings.
     */
    public static final String FIELD_SETTINGS = "settings";
    /**
     * Create timestamp.
     */
    public static final String FIELD_CREATE_DATE = "create_date";
    /**
     * Update timestamp.
     */
    public static final String FIELD_UPDATE_DATE = "update_date";
    /**
     * Generated id.
     */
    private int id;
    /**
     * The node name.
     */
    private String name;
    /**
     * PostgreSQL host.
     */
    private String host;
    /**
     * Port.
     */
    private int port;
    /**
     * PostgreSQL database.
     */
    private String database;
    /**
     * PostgreSQL user.
     */
    private String user;
    /**
     * PostgreSQL password.
     */
    private String password;
    /**
     * Create date.
     */
    private Date createDate;
    /**
     * Update date.
     */
    private Date updateDate;
    /**
     * Pool settings.
     */
    private Map<PoolSetting, String> settings = Collections.emptyMap();
    /**
     * Constructor.
     */
    public DataNodePO() {
        super();
    }
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
    /**
     * @param host the host to set
     */
    public void setHost(String url) {
        this.host = url;
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
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }
    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the settings
     */
    public Map<PoolSetting, String> getSettings() {
        return settings;
    }
    /**
     * @param settings the settings to set
     */
    public void setSettings(Map<PoolSetting, String> settings) {
        this.settings = settings;
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
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
