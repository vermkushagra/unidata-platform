package com.unidata.mdm.backend.po.matching;
/**
 * Matching algorithm table.
 */
public class MatchingAlgorithmPO {
    /**
     * Id field.
     */
    public static final String FIELD_ID = "id";
    /**
     * Algorithm id field.
     */
    public static final String FIELD_ALGORITHM_ID = "algorithm_id";
    /**
     * Rule id field.
     */
    public static final String FIELD_RULE_ID = "rule_id";
    /**
     * Data field.
     */
    public static final String FIELD_DATA = "data";
    /**
     * Rule id.
     */
    private int ruleId;
    /**
     * Algorithm id.
     */
    private int algorithmId;
    /**
     * Data.
     */
    private String data;

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public int getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(int algorithmId) {
        this.algorithmId = algorithmId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
