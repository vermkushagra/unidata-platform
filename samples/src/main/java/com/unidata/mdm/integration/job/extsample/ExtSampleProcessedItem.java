/**
 */

package com.unidata.mdm.integration.job.extsample;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class ExtSampleProcessedItem {
    private String value;

    public ExtSampleProcessedItem() {
        // No-op.
    }

    public ExtSampleProcessedItem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
