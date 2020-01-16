package org.unidata.mdm.system.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.service.DbCleaner;
import org.unidata.mdm.system.service.ModuleService;
import org.unidata.mdm.system.service.PlatformStarter;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PlatformStarterImpl implements PlatformStarter, ApplicationListener<ContextRefreshedEvent> {

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Value("${unidata.db.clean:false}")
    private boolean cleanDb;

    private final DbCleaner dbCleaner;

    private final ModuleService moduleService;

    public PlatformStarterImpl(
            final DbCleaner dbCleaner,
            final ModuleService moduleService
    ) {
        this.dbCleaner = dbCleaner;
        this.moduleService = moduleService;
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        moduleService.setCurrentContext(event.getApplicationContext());
        if (!started.compareAndSet(false, true)) {
            return;
        }
        if (cleanDb) {
            dbCleaner.clean();
        }
        moduleService.init();
    }
}
