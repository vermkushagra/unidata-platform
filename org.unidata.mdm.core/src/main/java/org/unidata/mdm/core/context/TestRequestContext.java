package org.unidata.mdm.core.context;

import java.util.Collection;

import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.context.CompositeRequestContext;
import org.unidata.mdm.system.context.RequestFragmentContext;
import org.unidata.mdm.system.context.RequestFragmentId;

/**
 * @author Mikhail Mikhailov
 *
 */
public class TestRequestContext extends CommonRequestContext
    implements RequestFragmentContext<TestRequestContext> {

    public static final RequestFragmentId<TestRequestContext> TFRID
        = new RequestFragmentId<>("TFRID", null);

    public static final int BOOL_FLD_ID = FLAG_ID_PROVIDER.getAndIncrement();

    /**
     *
     */
    private static final long serialVersionUID = 9214244381859027195L;

    /**
     * Constructor.
     * @param b
     */
    public TestRequestContext(CommonRequestContextBuilder<?> b) {
        super(b);

    }

    boolean testField() {
        return flags.get(BOOL_FLD_ID);
    }

    @Override
    public RequestFragmentId<TestRequestContext> getFragmentId() {
        return TFRID;
    }

    public TestRequestContext testExtract(CompositeRequestContext ctx) {
        return ctx.fragment(TFRID);
    }

    public Collection<TestRequestContext> testExtract2(CompositeRequestContext ctx) {
        return ctx.fragments(TFRID);
    }
}
