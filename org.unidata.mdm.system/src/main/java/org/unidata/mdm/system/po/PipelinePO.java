package org.unidata.mdm.system.po;

/**
 * @author Mikhail Mikhailov on Nov 26, 2019
 */
public class PipelinePO {
    /**
     * The start segment type id.
     */
    public static final String FIELD_START_ID = "start_id";
    /**
     * The subject. May be null.
     */
    public static final String FIELD_SUBJECT = "subject";
    /**
     * JSON content.
     */
    public static final String FIELD_CONTENT = "content";
    /**
     * The start id.
     */
    private String startId;
    /**
     * The subject.
     */
    private String subject;
    /**
     * JSON content.
     */
    private String content;
    /**
     * @return the startId
     */
    public String getStartId() {
        return startId;
    }
    /**
     * @param startId the startId to set
     */
    public void setStartId(String startId) {
        this.startId = startId;
    }
    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }
    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
}
