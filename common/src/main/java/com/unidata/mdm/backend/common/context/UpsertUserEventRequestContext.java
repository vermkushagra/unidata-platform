/**
 *
 */
package com.unidata.mdm.backend.common.context;


/**
 * @author Mikhail Mikhailov
 * User event.
 */
public class UpsertUserEventRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -4820373900025554339L;
    /**
     * Type.
     */
    private final String type;
    /**
     * Content.
     */
    private final String content;
    /**
     * User login.
     */
    private final String login;
    /**
     * User id.
     */
    private final Integer userId;
    /**
     * Constructor.
     */
    private UpsertUserEventRequestContext(UpsertUserEventRequestContextBuilder b) {
        super();
        this.content = b.content;
        this.login = b.login;
        this.type = b.type;
        this.userId = b.userId;
    }


    /**
     * @return the type
     */
    public String getType() {
        return type;
    }


    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }


    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }


    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     *
     * @return builder
     */
    public static UpsertUserEventRequestContextBuilder builder(){
        return new UpsertUserEventRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class UpsertUserEventRequestContextBuilder {
        /**
         * Type.
         */
        private String type;
        /**
         * Content.
         */
        private String content;
        /**
         * User login.
         */
        private String login;
        /**
         * User id.
         */
        private Integer userId;
        /**
         * Sets type.
         * @param type
         * @return
         */
        public UpsertUserEventRequestContextBuilder type(String type) {
            this.type = type;
            return this;
        }
        /**
         * Sets content.
         * @param content
         * @return
         */
        public UpsertUserEventRequestContextBuilder content(String content) {
            this.content = content;
            return this;
        }
        /**
         * Sets login.
         * @param login
         * @return
         */
        public UpsertUserEventRequestContextBuilder login(String login) {
            this.login = login;
            return this;
        }
        /**
         * Sets userId.
         * @param userId
         * @return
         */
        public UpsertUserEventRequestContextBuilder userId(Integer userId) {
            this.userId = userId;
            return this;
        }
        /**
         * Build method.
         * @return
         */
        public UpsertUserEventRequestContext build() {
            return new UpsertUserEventRequestContext(this);
        }
    }
}
