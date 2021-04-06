package com.unidata.mdm.backend.service.configuration;

import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Register new data source.
 * Use for correct shutdown
 * @author Dmitry Kopin on 12.04.2017.
 */
public class RegisterDataSource {
    private CloseContextListener contextListener;

    private DataSource dataSource;

    @PostConstruct
    public void register(){
        if(contextListener.getDataSources() == null){
            contextListener.setDataSources(new ArrayList<>());
        }
        contextListener.getDataSources().add(dataSource);
    }

    public CloseContextListener getContextListener() {
        return contextListener;
    }
    @Required
    public void setContextListener(CloseContextListener contextListener) {
        this.contextListener = contextListener;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
