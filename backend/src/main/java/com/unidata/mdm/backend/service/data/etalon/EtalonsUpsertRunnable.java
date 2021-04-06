/**
 *
 */
package com.unidata.mdm.backend.service.data.etalon;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;

/**
 * @author Mikhail Mikhailov
 * Calculate etalons runnable.
 */
public class EtalonsUpsertRunnable implements Runnable {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EtalonsUpsertRunnable.class);
    /**
     * The context to process.
     */
    private final UpsertRequestContext context;
    /**
     * The processor.
     */
    private final EtalonRecordsComponent component;
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
    public EtalonsUpsertRunnable(final UpsertRequestContext context, EtalonRecordsComponent component) {
        super();
        this.context = context;
        this.component = component;

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
            component.upsertEtalon(context);
        } catch (Throwable t) {
            LOGGER.warn("Caught throwable while calculation etalon timeline.", t);
        }
    }
}
