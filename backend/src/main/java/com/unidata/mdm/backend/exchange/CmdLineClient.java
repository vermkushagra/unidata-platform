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

package com.unidata.mdm.backend.exchange;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.exchange.util.ExchangeUtils;

/**
 * @author Mikhail Mikhailov
 *         Standalone (command line) client.
 */
public class CmdLineClient {

    /**
     * @param args
     */
    public static void main(String[] args) {
        boolean success = false;

        try (ExchangeContext ctx = ExchangeContext.getContext(args)) {
            if (ctx.isHelpRequest() || ctx.getActions() == null || ctx.getActions().length == 0) {
                System.err.println();
                System.err.println((!ctx.isHelpRequest() ? "No actions defined! " : "") + "Available actions and params:");
                ExchangeContext.getParamsHelp(System.err);
                System.exit(-1);
                return;
            }

            // Set profile and create context
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            beanFactory.registerSingleton(StandaloneConfiguration.EXCHANGE_CONTEXT_BEAN_NAME, ctx);
            AnnotationConfigApplicationContext aCtx = new AnnotationConfigApplicationContext(beanFactory);
            aCtx.getEnvironment().setActiveProfiles(StandaloneConfiguration.STANDALONE_PROFILE_NAME);
            aCtx.register(StandaloneConfiguration.class);
            aCtx.registerShutdownHook();
            aCtx.refresh();

            ctx.putToStorage(StorageId.COMMON_APPLICATION_CONTEXT, aCtx);

            for (Action a : ctx.getActions()) {
                if (a == Action.MIGRATION) {
                    success = ExchangeUtils.modelMigration(ctx);
                } else {
                    throw new IllegalArgumentException("Invalid action requested.");
                }
            }
            aCtx.close();
            System.out.println("FINISHED.");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            System.exit(success ? 0 : -1);
        }
    }
}
