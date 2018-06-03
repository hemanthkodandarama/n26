package com.bobby.n26.v1.service.store;

import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bobby.n26.v1.common.Utils.format;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@Component("mapStore")
public class ConcurrentMapStore implements TransactionStore {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired TransactionProperties prop;

    private ConcurrentHashMap<Long,Double> store = new ConcurrentHashMap<>();


    @Override public void save(Transaction txn) {
        store.put(txn.getTime(), txn.getAmount());
    }

    @Override public Stats getStatistics() {
        // prepare
        Instant calculationMoment = Instant.now();
        long oldestAcceptableTxnTime = calculationMoment
            .minusMillis(prop.getAgeLimitForStatsByMillis()).toEpochMilli();
        log.debug("Calculation started at: {}",format(calculationMoment));
        log.debug("Calculation age limit: {}",prop.getAgeLimitForStatsByMillis());
        log.debug("Transactions NOT older than (before) {} are involved in calculation.",
            format(oldestAcceptableTxnTime));
        // call calculate
        Stats stats = manualCalculate(oldestAcceptableTxnTime);
        log.debug("Stats calculation result: {}",stats);
        return stats;
    }

    private Stats streamCalculate(long oldestAcceptableTxnTime){
        DoubleSummaryStatistics summary = store.entrySet().stream()
            .filter(p -> p.getKey() >= oldestAcceptableTxnTime)
            .mapToDouble(Map.Entry::getValue).summaryStatistics();
        return new Stats()
            .setAvg(summary.getAverage())
            .setCount(summary.getCount())
            .setMax(summary.getMax())
            .setMin(summary.getMin())
            .setSum(summary.getSum());
    }

    private Stats manualCalculate(long oldestAcceptableTxnTime){
        double min = 0 , max = 0 , sum = 0;
        long count = 0;
        for (Map.Entry<Long,Double> txn : store.entrySet()){
            if (txn.getValue() < oldestAcceptableTxnTime){
                continue;
            }
            double amount = txn.getValue();
            // min
            if (count == 0){
                min = amount;
            } else if (amount < min){
                min = amount;
            }
            // max
            if (amount > max){
                max = amount;
            }
            // sum
            sum += amount;
            // count
            count++;
        }

        return new Stats()
            .setAvg(count == 0 ? 0 : sum / count)
            .setCount(count)
            .setMax(max)
            .setMin(min)
            .setSum(sum);
    }

    @Override public void reset() {
        store.clear();
    }
}
