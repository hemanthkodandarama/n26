package com.bobby.n26.v1.web.dto;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class ConfigData {

    private int ageLimitForSaveByMillis;
    private int ageLimitForStatsByMillis;
    private int timeDiscountForSaveByMillis;

    public int getAgeLimitForSaveByMillis() {
        return ageLimitForSaveByMillis;
    }

    public ConfigData setAgeLimitForSaveByMillis(int ageLimitForSaveByMillis) {
        this.ageLimitForSaveByMillis = ageLimitForSaveByMillis;
        return this;
    }

    public int getAgeLimitForStatsByMillis() {
        return ageLimitForStatsByMillis;
    }

    public ConfigData setAgeLimitForStatsByMillis(
        int ageLimitForStatsByMillis) {
        this.ageLimitForStatsByMillis = ageLimitForStatsByMillis;
        return this;
    }

    public int getTimeDiscountForSaveByMillis() {
        return timeDiscountForSaveByMillis;
    }

    public ConfigData setTimeDiscountForSaveByMillis(
        int timeDiscountForSaveByMillis) {
        this.timeDiscountForSaveByMillis = timeDiscountForSaveByMillis;
        return this;
    }

}
