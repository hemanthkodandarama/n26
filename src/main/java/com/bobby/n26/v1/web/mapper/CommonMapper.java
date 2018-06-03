package com.bobby.n26.v1.web.mapper;

import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;
import com.bobby.n26.v1.web.dto.ConfigData;
import com.bobby.n26.v1.web.dto.StatsData;
import com.bobby.n26.v1.web.dto.TransactionData;
import org.springframework.stereotype.Component;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@Component
public class CommonMapper {

    // TXN

    public Transaction toModel(TransactionData source){
        if (source == null){
            return null;
        }
        return new Transaction(source.getTimestamp(), source.getAmount());
    }
    public TransactionData toData(Transaction source){
        if (source == null){
            return null;
        }
        return new TransactionData(source.getTime(), source.getAmount());
    }

    // STATS

    public StatsData toData(Stats source){
        if (source == null){
            return null;
        }
        return new StatsData()
            .setAvg(source.getAvg())
            .setCount(source.getCount())
            .setMax(source.getMax())
            .setMin(source.getMin())
            .setSum(source.getSum());
    }

    // CONFIG

    public ConfigData toData(TransactionProperties source){
        if (source == null){
            return null;
        }
        return new ConfigData()
        .setAgeLimitForSaveByMillis(source.getAgeLimitForSaveByMillis())
        .setAgeLimitForStatsByMillis(source.getAgeLimitForStatsByMillis())
        .setTimeDiscountForSaveByMillis(source.getTimeDiscountForSaveByMillis());
    }

}
