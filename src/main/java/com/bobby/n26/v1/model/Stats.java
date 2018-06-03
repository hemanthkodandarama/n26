package com.bobby.n26.v1.model;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class Stats {
    private double min = 0;
    private double max = 0;
    private double avg = 0;
    private double sum = 0;
    private long count = 0;

    public double getMin() {
        return min;
    }

    public Stats setMin(double min) {
        this.min = min;
        return this;
    }

    public double getMax() {
        return max;
    }

    public Stats setMax(double max) {
        this.max = max;
        return this;
    }

    public double getAvg() {
        return avg;
    }

    public Stats setAvg(double avg) {
        this.avg = avg;
        return this;
    }

    public double getSum() {
        return sum;
    }

    public Stats setSum(double sum) {
        this.sum = sum;
        return this;
    }

    public long getCount() {
        return count;
    }

    public Stats setCount(long count) {
        this.count = count;
        return this;
    }

    @Override public String toString() {
        return "Stats{" + "min=" + min + ", max=" + max + ", avg=" + avg
            + ", sum=" + sum + ", count=" + count + '}';
    }
}
