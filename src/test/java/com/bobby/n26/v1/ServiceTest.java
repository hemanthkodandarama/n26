package com.bobby.n26.v1;

import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;
import com.bobby.n26.v1.common.OutDatedTransactionException;
import com.bobby.n26.v1.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:application.yml")
public class ServiceTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired TransactionService service;
    @Autowired TransactionProperties prop;

    @Before
    public void before(){
        service.reset();
    }

    @Test
    public void printConfigValues(){
        log.info("AgeLimitForSaveByMillis: {}",prop.getAgeLimitForSaveByMillis());
        log.info("AgeLimitForStatsByMillis: {}",prop.getAgeLimitForStatsByMillis());
    }

    @Test
    public void whenTxnIsYoungEnoughToSave_ThenSuccess() throws
        OutDatedTransactionException {
        // given
        long fiveSecondsToBeTooOld = Instant.now()
            .minusMillis(prop.getAgeLimitForSaveByMillis() - 5000).toEpochMilli();
        Transaction txn = new Transaction(fiveSecondsToBeTooOld,5000);

        // when
        service.save(txn);
    }

    @Test
    public void whenTxnIsExactlyOnTheAgeLimitEdgeToSave_ThenStillSuccess()
        throws OutDatedTransactionException {
        // given
        long exactlyOnTheLimitEdge = Instant.now()
            .minusMillis(prop.getAgeLimitForSaveByMillis()).toEpochMilli();
        Transaction txn = new Transaction(exactlyOnTheLimitEdge,200);

        // when
        service.save(txn);
    }

    @Test(expected = OutDatedTransactionException.class)
    public void whenTxnIsOutDatedToSave_ThenError() throws
        OutDatedTransactionException {
        // given
        long twoMinutesAgo = Instant.now().minusSeconds(120).toEpochMilli();
        Transaction txn1 = new Transaction(twoMinutesAgo,1500);
        // when
        service.save(txn1);
    }

    @Test
    public void whenTxnsAreSaved_ThenStatsShouldBeCorrect()
        throws OutDatedTransactionException, InterruptedException {
        // prepare
        int ageLimit = prop.getAgeLimitForSaveByMillis();
        Instant now = Instant.now();
        // given
        Transaction txn1 = new Transaction(now.minusMillis(ageLimit - 1000)
            .toEpochMilli(),100);
        Transaction txn2 = new Transaction(now.minusMillis(ageLimit - 2000)
            .toEpochMilli(),50);

        service.save(txn1);
        service.save(txn2);
        // when
        Thread.sleep(500);
        Stats stats = service.getStatistics();
        // then
        assertEquals("error in count", 2, stats.getCount(), 0);
        assertEquals("error in min", 50, stats.getMin(), 0);
        assertEquals("error in max", 100, stats.getMax(), 0);
        assertEquals("error in sum", 150, stats.getSum(), 0);
        assertEquals("error in avg", 75, stats.getAvg(), 0);
    }

    @Test
    public void whenManyTxnsAreSaved_ThenStatsShouldBeCorrect()
        throws OutDatedTransactionException, InterruptedException {
        // prepare
        int ageLimit = prop.getAgeLimitForSaveByMillis();
        Instant now = Instant.now();
        // given
        List<Transaction> txnList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            txnList.add(new Transaction(now.minusMillis(ageLimit - 2000 - i)
                .toEpochMilli(),10+i));
        }
        for (int i = 0; i < txnList.size(); i++) {
            log.debug("saving txn no.{}",i+1);
            service.save(txnList.get(i));
        }

        // when
        Thread.sleep(500);
        Stats stats = service.getStatistics();
        // then
        DoubleSummaryStatistics summary = txnList.stream().
            mapToDouble(Transaction::getAmount).summaryStatistics();
        assertEquals("error in count", summary.getCount(), stats.getCount(), 0);
        assertEquals("error in min", summary.getMin(), stats.getMin(), 0);
        assertEquals("error in max", summary.getMax(), stats.getMax(), 0);
        assertEquals("error in sum", summary.getSum(), stats.getSum(), 0);
        assertEquals("error in avg", summary.getAverage(), stats.getAvg(), 0);
    }

    /**
     * reduces the time limits to 6 (instead of the config value) seconds only for test
     */
    @Test
    public void whenTimePasses_ThenStatsCalculationShouldIgnoreOlds_customTime()
        throws OutDatedTransactionException, InterruptedException {
        // prepare
        int ageLimitSave = prop.getAgeLimitForSaveByMillis();
        int ageLimitStats = prop.getAgeLimitForStatsByMillis();
        int ageLimitSaveOld = ageLimitSave;
        int ageLimitStatsOld = ageLimitStats;
        ageLimitSave = 6000; // milliseconds
        ageLimitStats = 6000; // milliseconds
        prop.setAgeLimitForSaveByMillis(ageLimitSave);
        prop.setAgeLimitForStatsByMillis(ageLimitStats);
        Instant now = Instant.now();
        // given
        List<Transaction> txnList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            txnList.add(new Transaction(now.minusMillis(ageLimitSave - 2000 - i).toEpochMilli(),10+i));
        }
        for (int i = 0; i < txnList.size(); i++) {
            log.debug("saving txn no.{}",i+1);
            service.save(txnList.get(i));
        }

        log.debug("waiting for {} milliseconds to start second part...",ageLimitStats);
        Thread.sleep(ageLimitStats+500);
        log.debug("second part started.");

        txnList.clear();
        now = Instant.now();
        for (int i = 0; i < 2; i++) {
            txnList.add(new Transaction(now.minusMillis(ageLimitSave - 2000 - i)
                .toEpochMilli(),10+i));
        }
        for (int i = 0; i < txnList.size(); i++) {
            log.debug("saving txn no.{}",i+1);
            service.save(txnList.get(i));
        }

        // when
        Thread.sleep(500);
        Stats stats = service.getStatistics();
        // then
        DoubleSummaryStatistics summary = txnList.stream().
            mapToDouble(Transaction::getAmount).summaryStatistics();
        assertEquals("error in count", summary.getCount(), stats.getCount());
        assertEquals("error in min", summary.getMin(), stats.getMin(), 0);
        assertEquals("error in max", summary.getMax(), stats.getMax(), 0);
        assertEquals("error in sum", summary.getSum(), stats.getSum(), 0);
        assertEquals("error in avg", summary.getAverage(), stats.getAvg(), 0);

        prop.setAgeLimitForSaveByMillis(ageLimitSaveOld);
        prop.setAgeLimitForStatsByMillis(ageLimitStatsOld);
    }

    // ---------------------------- concurrency support test -------------------------

    @Test
    public void whenSavingAndGettingStatsSimultanousely_thenNoError()
        throws InterruptedException {
        // prepare
        int ageLimit = prop.getAgeLimitForSaveByMillis();
        int saveRequestCount = 500;
        long floor = Instant.now().minusMillis(ageLimit/2).toEpochMilli();
        // given
        List<Transaction> txnList = new ArrayList<>();
        for (int i = 0; i < saveRequestCount; i++) {
            // different timestamps for each txn:
            long time = ThreadLocalRandom.current()
                .nextLong(floor, Instant.now().toEpochMilli() + 1);
            int amount = 10+i;
            txnList.add(new Transaction(time,amount));
            // one timestamp for all, because duplicates are also supported:
//            int halfMinuteBeforeGettingTooOld = ageLimit - 30000;
//            int amount = 10+i;
//            txnList.add(new Transaction(now.minusMillis(halfMinuteBeforeGettingTooOld)
//                .toEpochMilli(),amount));

        }

        ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(1);
        for (Transaction txn : txnList) {

            executorService.schedule((Callable) () -> {
                try {
                    service.save(txn);
                } catch (OutDatedTransactionException e) {
                    log.error("save failed", e);
                }
                return null;
            },100,TimeUnit.MILLISECONDS);

        }

        TimeUnit.MILLISECONDS.sleep(500);
        service.getStatistics();

        // shutdown the executorService
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(
                txnList.size() * prop.getDelayForSaveByMillis() + 2000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        log.info("finished all tasks");

    }

    @Test
    public void whenTxnsAreSaved_thenTheyShouldAutoExpire()
        throws OutDatedTransactionException, InterruptedException {
        // prepare
        int ageLimit = prop.getAgeLimitForSaveByMillis();
        Instant now = Instant.now();
        // given
        List<Transaction> txnList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            txnList.add(new Transaction(now.minusMillis(ageLimit - 2000 - i)
                .toEpochMilli(),10+i));
        }
        for (int i = 0; i < txnList.size(); i++) {
            log.debug("saving txn no.{}",i+1);
            service.save(txnList.get(i));
        }

        Thread.sleep(prop.getAgeLimitForStatsByMillis() + 1000);
        // when
        Stats stats = service.getStatistics();
        // then
        log.info("Stats: {}",stats);
        assertEquals(0, stats.getCount() );

    }

}
