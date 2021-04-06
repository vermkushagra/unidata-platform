/**
 * Date: 19.02.2016
 */

package com.unidata.mdm.backend.service.job.sample;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SampleItemSubmission {
    private String value;

    public SampleItemSubmission() {
        // No-op.
    }

    public SampleItemSubmission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
