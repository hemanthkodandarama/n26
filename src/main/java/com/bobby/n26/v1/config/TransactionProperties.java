package com.bobby.n26.v1.config;

import com.bobby.n26.v1.common.ConfigListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@Configuration
@ConfigurationProperties("txn")
public class TransactionProperties {
    @Value("${txn.age-limit-millis.save:60000}")
    private int ageLimitForSaveByMillis;

    @Value("${txn.age-limit-millis.stats:60000}")
    private int ageLimitForStatsByMillis;

    @Value("${txn.time-discount-for-save-millis:10}")
    private int timeDiscountForSaveByMillis;

    @Value("${txn.delay-millis.save:0}")
    private int delayForSaveByMillis;

    @Value("${txn.delay-millis.stats:0}")
    private int delayForStatsByMillis;

    List<ConfigListener> configListeners = new ArrayList<>();

    public int getAgeLimitForSaveByMillis() {
        return ageLimitForSaveByMillis;
    }

    public TransactionProperties setAgeLimitForSaveByMillis(
        int ageLimitForSaveByMillis) {
        this.ageLimitForSaveByMillis = ageLimitForSaveByMillis;
        return this;
    }

    public int getAgeLimitForStatsByMillis() {
        return ageLimitForStatsByMillis;
    }

    public TransactionProperties setAgeLimitForStatsByMillis(
        int ageLimitForStatsByMillis) {
        if (ageLimitForStatsByMillis != this.ageLimitForStatsByMillis){
            this.ageLimitForStatsByMillis = ageLimitForStatsByMillis;
            // call observers
            for (ConfigListener listener : configListeners){
                listener.ageLimitForStatsChanged();
            }
        }
        return this;
    }

    public int getTimeDiscountForSaveByMillis() {
        return timeDiscountForSaveByMillis;
    }

    public TransactionProperties setTimeDiscountForSaveByMillis(
        int timeDiscountForSaveByMillis) {
        this.timeDiscountForSaveByMillis = timeDiscountForSaveByMillis;
        return this;
    }

    public int getDelayForSaveByMillis() {
        return delayForSaveByMillis;
    }

    public TransactionProperties setDelayForSaveByMillis(
        int delayForSaveByMillis) {
        this.delayForSaveByMillis = delayForSaveByMillis;
        return this;
    }

    public int getDelayForStatsByMillis() {
        return delayForStatsByMillis;
    }

    public TransactionProperties setDelayForStatsByMillis(
        int delayForStatsByMillis) {
        this.delayForStatsByMillis = delayForStatsByMillis;
        return this;
    }

    public void addListener(ConfigListener listener){
        if (!configListeners.contains(listener)){
            configListeners.add(listener);
        }
    }

    @Override public String toString() {
        return "TransactionProperties{" + "ageLimitForSaveByMillis="
            + ageLimitForSaveByMillis + ", ageLimitForStatsByMillis="
            + ageLimitForStatsByMillis + ", timeDiscountForSaveByMillis="
            + timeDiscountForSaveByMillis + ", delayForSaveByMillis="
            + delayForSaveByMillis + ", delayForStatsByMillis="
            + delayForStatsByMillis + '}';
    }
}
