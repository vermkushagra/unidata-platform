/**
 * Date: 09.08.2016
 */

package com.unidata.mdm.integration.sink;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.Component;
import org.apache.camel.component.jms.JmsConfiguration;

import com.unidata.mdm.backend.common.integration.notification.SinkComponent;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SinkActiveMQComponent extends ActiveMQComponent implements SinkComponent, Component {

    private int jmsConcurrentConsumers;
    private int poolMaxConnections;
    private String connectionBrokerURL;
    private String connectionUserName;
    private String connectionPassword;

    private PooledConnectionFactory pooledConnectionFactory;

    /**
     *
     * @throws Exception
     */
    @Override
    public void init() {
        ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory();

        jmsConnectionFactory.setBrokerURL(connectionBrokerURL);
        jmsConnectionFactory.setUserName(connectionUserName);
        jmsConnectionFactory.setPassword(connectionPassword);

        pooledConnectionFactory = new PooledConnectionFactory();

        pooledConnectionFactory.setConnectionFactory(jmsConnectionFactory);
        pooledConnectionFactory.setMaxConnections(poolMaxConnections);

        pooledConnectionFactory.start();

        JmsConfiguration jmsConfig = new JmsConfiguration();

        jmsConfig.setConnectionFactory(pooledConnectionFactory);
        jmsConfig.setConcurrentConsumers(jmsConcurrentConsumers);

        setConfiguration(jmsConfig);
    }

    @Override
    public void destroy() {
        if (pooledConnectionFactory != null) {
            pooledConnectionFactory.stop();
        }
    }

    public void setJmsConcurrentConsumers(int jmsConcurrentConsumers) {
        this.jmsConcurrentConsumers = jmsConcurrentConsumers;
    }

    public void setPoolMaxConnections(int poolMaxConnections) {
        this.poolMaxConnections = poolMaxConnections;
    }

    public void setConnectionBrokerURL(String connectionBrokerURL) {
        this.connectionBrokerURL = connectionBrokerURL;
    }

    public void setConnectionUserName(String connectionUserName) {
        this.connectionUserName = connectionUserName;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }
}
