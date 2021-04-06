/**
 *
 */
package com.unidata.mdm.backend.service.data.etalon;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;

/**
 * @author Mikhail Mikhailov
 * Upsert a single etalon period.
 */
public class EtalonUpsertRunnable implements Runnable {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EtalonUpsertRunnable.class);
    /**
     * The context.
     */
    private final UpsertRequestContext context;
    /**
     * The component.
     */
    private final EtalonRecordsComponent component;
    /**
     * The latch.
     */
    private final CountDownLatch latch;
    /**
     * MDC context map.
     */
    private final Map<String, String> mdc;
    /**
     * Security context.
     */
    private final Authentication authentication;
    /**
     * Constructor.
     */
    public EtalonUpsertRunnable(
            final UpsertRequestContext context,
            final EtalonRecordsComponent component,
            final CountDownLatch latch) {
        super();
        this.context = context;
        this.component = component;
        this.latch = latch;

        mdc = MDC.getCopyOfContextMap();
        authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (mdc != null) {
            MDC.setContextMap(mdc);
        }
        SecurityContext sctx = SecurityContextHolder.createEmptyContext();
        sctx.setAuthentication(authentication);
        SecurityContextHolder.setContext(sctx);

        try {
            component.upsertEtalonPeriod(context);
        } catch (Throwable e) {
            LOGGER.warn("Upsert etalon period caught throwable!", e);
        } finally {
            latch.countDown();
        }
    }
}
