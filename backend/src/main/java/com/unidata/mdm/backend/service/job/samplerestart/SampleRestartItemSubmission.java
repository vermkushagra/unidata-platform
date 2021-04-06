/**
 *
 */

package com.unidata.mdm.backend.service.job.samplerestart;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleRestartItemSubmission {
    private String value;

    public SampleRestartItemSubmission() {
        // No-op.
    }

    public SampleRestartItemSubmission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
