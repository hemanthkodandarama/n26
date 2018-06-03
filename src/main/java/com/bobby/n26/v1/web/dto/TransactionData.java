package com.bobby.n26.v1.web.dto;

import javax.validation.constraints.NotNull;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class TransactionData {

    @NotNull
    private long timestamp;
    @NotNull
    private double amount;

    public TransactionData() {
    }

    public TransactionData(long timestamp, double amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public TransactionData setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionData setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    @Override public String toString() {
        return "TransactionData{" + "timestamp=" + timestamp + ", amount=" + amount + '}';
    }
}
