package com.bobby.n26.v1.service.store;

import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.bobby.n26.v1.common.Utils.format;
import static com.bobby.n26.v1.common.Utils.toLocalDateTime;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@Component("customStore")
public class CustomConcurrentStore implements TransactionStore {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private List<Transaction> store = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    @Autowired private TransactionProperties prop;

    private void lock(){
        lock.lock();
    }
    private void unLock(){
        lock.unlock();
    }

    @Override public void save(Transaction txn) {
        lock();
        store.add(txn);
        // Only to simulate the delay in a long running process
        if (prop.getDelayForSaveByMillis() != 0){
            try {
                TimeUnit.MILLISECONDS.sleep(prop.getDelayForSaveByMillis());
            } catch (InterruptedException ignored) {
                log.error("thread sleep interrupted");
            }
        }
        //
        unLock();
    }

    @Override public Stats getStatistics() {
        lock();
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
        // Only to simulate the delay in a long running process
        if (prop.getDelayForStatsByMillis() != 0){
            try {
                TimeUnit.MILLISECONDS.sleep(prop.getDelayForStatsByMillis());
            } catch (InterruptedException ignored) {
                log.error("thread sleep interrupted");
            }
        }
        //
        unLock();
        return stats;
    }

    private Stats streamCalculate(long oldestAcceptableTxnTime){
        DoubleSummaryStatistics summary = store.stream()
            .filter(p -> p.getTime() >= oldestAcceptableTxnTime)
            .mapToDouble(Transaction::getAmount).summaryStatistics();
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
        for (Transaction txn : store){
            if (txn.getTime() < oldestAcceptableTxnTime){
                continue;
            }
            double amount = txn.getAmount();
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
        lock();
        store.clear();
        unLock();
    }
}
