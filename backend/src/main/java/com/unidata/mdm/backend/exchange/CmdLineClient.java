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
