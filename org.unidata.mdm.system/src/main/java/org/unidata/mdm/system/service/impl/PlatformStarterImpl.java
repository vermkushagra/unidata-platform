/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
