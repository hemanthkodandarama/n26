package com.bobby.n26.v1.model;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class Transaction {

    private long time;
    private double amount;

    public Transaction() {
    }

    public Transaction(long time, double amount) {
        this.time = time;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public Transaction setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Transaction setTime(long time) {
        this.time = time;
        return this;
    }

    @Override public String toString() {
        return "Transaction{" + "time=" + time + ", amount=" + amount + '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Transaction that = (Transaction) o;

        if (time != that.time)
            return false;
        return Double.compare(that.amount, amount) == 0;
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = (int) (time ^ (time >>> 32));
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
