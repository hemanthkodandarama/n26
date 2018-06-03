package com.bobby.n26.v1.service.store;

import com.bobby.n26.v1.common.ConfigListener;
import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@Component("expiryMapStore")
public class ExpiringConcurrentMapStore implements TransactionStore ,
    ConfigListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Cache<Long,Double> cache;
    private ConcurrentMap<Long,Double> store;
    private Stats stats;
    private AtomicLong atomicLong = new AtomicLong(1);

    @Autowired TransactionProperties prop;

    @PostConstruct public void init(){
        log.debug("Initializing transaction store with TTL: {} ms", TimeUnit.MILLISECONDS);
        stats = new Stats();
        cache = CacheBuilder.newBuilder().expireAfterWrite(
            prop.getAgeLimitForStatsByMillis(), TimeUnit.MILLISECONDS).build();
        store = cache.asMap();
    }

    @Override public void save(Transaction txn) {
        store.put(txn.getTime()+atomicLong.getAndIncrement(), txn.getAmount());
        new Thread(this::calculateStats).start();
    }

    @Override public Stats getStatistics() {
        return stats;
    }

    @Override public void reset() {
        store.clear();
    }

    @Override public void ageLimitForStatsChanged() {
        init();
    }

    @Scheduled(fixedDelay = 1000)
    public void calculateStats(){
        log.trace("calculateStats started.");
        // refresh the expiring cache connected to the txn store map
        cache.cleanUp();
        // assign the stats calculation result to the class level global variable
        stats = streamCalculate();
        log.trace("Stats calculation result: {}",stats);
        // just show the store content if any
        if (store.size() == 0){
            log.trace("store is empty.");
        }else{
            log.trace("store size: {}", store.size());
            for (Map.Entry<Long,Double> entry : store.entrySet()){
                log.trace("Txn - Time: {} , Amount: {}",entry.getKey(), entry.getValue());
            }
        }
    }

    private Stats streamCalculate(){
        // query the txn store without any filter, cause it only contains the young transactions
        DoubleSummaryStatistics summary = store.entrySet().stream()
            .mapToDouble(Map.Entry::getValue).summaryStatistics();
        return new Stats()
            .setAvg(Double.isInfinite(summary.getAverage()) ? 0 : summary.getAverage())
            .setCount(summary.getCount())
            .setMax(Double.isInfinite(summary.getMax()) ? 0 : summary.getMax())
            .setMin(Double.isInfinite(summary.getMin()) ? 0 : summary.getMin())
            .setSum(Double.isInfinite(summary.getSum()) ? 0 : summary.getSum());
    }

}
