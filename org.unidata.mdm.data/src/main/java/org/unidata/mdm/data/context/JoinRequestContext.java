package org.unidata.mdm.data.context;

/**
 * Join origin to an existing etalon id.
 * @author Mikhail Mikhailov
 */
public class JoinRequestContext
    extends AbstractRecordIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -9109988572739215092L;
    /**
     * Constructor.
     */
    private JoinRequestContext(JoinRequestContextBuilder b) {
        super(b);
    }
    /**
     * Builder shorthand.
     * @return builder
     */
    public static JoinRequestContextBuilder builder() {
        return new JoinRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class JoinRequestContextBuilder extends AbstractRecordIdentityContextBuilder<JoinRequestContextBuilder> {
        /**
         * Constructor.
         */
        public JoinRequestContextBuilder() {
            super();
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public JoinRequestContext build() {
            return new JoinRequestContext(this);
        }
    }
}
