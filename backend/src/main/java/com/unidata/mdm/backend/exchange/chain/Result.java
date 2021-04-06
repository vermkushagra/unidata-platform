package com.unidata.mdm.backend.exchange.chain;

/**
 * Class responsible for collecting processing results.
 */
public class Result {

    private String entityName;
    private int total;
    private int processed;
    private int failed;
    private int reject;

    public Result() {
    }

    /**
     * @return total input items
     */
    public int getTotal() {
        return total;
    }

    public Result setTotal(int total) {
        this.total = total;
        return this;
    }

    /**
     * @return total processed items
     */
    public int getProcessed() {
        return processed;
    }

    public Result setProcessed(int processed) {
        this.processed = processed;
        return this;
    }

    /**
     * @return total failed items
     */
    public int getFailed() {
        return failed;
    }

    public Result setFailed(int failed) {
        this.failed = failed;
        return this;
    }

    /**
     * @return total reject items
     */
    public int getReject() {
        return reject;
    }

    public Result setReject(int reject) {
        this.reject = reject;
        return this;
    }

    /**
     * @return entity name of items
     */
    public String getEntityName() {
        return entityName;
    }

    public Result setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public void addTotal(int addition) {
        total += addition;
    }

    public void addRejected(int addition) {
        reject += addition;
    }

    public void addFailed(int addition) {
        failed += addition;
    }

    public void addProcessed(int addition) {
        processed += addition;
    }

    @Override
    public String toString() {
        return "Result{" +
                "total=" + total +
                ", processed=" + processed +
                ", failed=" + failed +
                ", reject=" + reject +
                '}';
    }
}
