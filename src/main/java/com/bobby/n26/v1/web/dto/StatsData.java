package com.bobby.n26.v1.web.dto;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class StatsData {
    private double min;
    private double max;
    private double avg;
    private double sum;
    private long count;

    public double getMin() {
        return min;
    }

    public StatsData setMin(double min) {
        this.min = min;
        return this;
    }

    public double getMax() {
        return max;
    }

    public StatsData setMax(double max) {
        this.max = max;
        return this;
    }

    public double getAvg() {
        return avg;
    }

    public StatsData setAvg(double avg) {
        this.avg = avg;
        return this;
    }

    public double getSum() {
        return sum;
    }

    public StatsData setSum(double sum) {
        this.sum = sum;
        return this;
    }

    public long getCount() {
        return count;
    }

    public StatsData setCount(long count) {
        this.count = count;
        return this;
    }

    @Override public String toString() {
        return "StatsData{" + "min=" + min + ", max=" + max + ", avg=" + avg
            + ", sum=" + sum + ", count=" + count + '}';
    }
}
