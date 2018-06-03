package com.bobby.n26.v1.service;

import com.bobby.n26.v1.common.OutDatedTransactionException;
import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;
import com.bobby.n26.v1.service.store.TransactionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.bobby.n26.v1.common.Utils.format;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@Service
public class InMemoryTransactionService implements TransactionService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired private TransactionProperties prop;
    @Autowired @Qualifier("expiryMapStore") private TransactionStore store;

    @Override
    public void save(Transaction txn) throws OutDatedTransactionException
        , IllegalArgumentException {
        // validate
        validateTxn(txn);
        // save
        store.save(txn);
    }

    /**
     * Validates a transaction for save function. Checks if the input transaction is young enough to save.
     * @param txn The transaction object instance to save
     * @throws OutDatedTransactionException Thrown if the transaction is older than acceptable age
     * @throws IllegalArgumentException Thrown if either transaction or its time is NULL
     */
    private void validateTxn(Transaction txn) throws
        OutDatedTransactionException , IllegalArgumentException {
        if (txn == null || txn.getTime() == 0){
            IllegalArgumentException e = new IllegalArgumentException("Invalid Transaction");
            log.error("Either transaction or its time is NULL",e);
            throw e;
        }
        Instant receiveTime = Instant.now();
        long expirationTime = receiveTime
            .minusMillis(prop.getAgeLimitForSaveByMillis())
            .minusMillis(prop.getTimeDiscountForSaveByMillis())
            .toEpochMilli();
        long txnTime = txn.getTime();
        log.debug("Input txn to validate: {}",txn);
        log.debug("AgeLimitSaveMillis: {}",prop.getAgeLimitForSaveByMillis());
        log.debug("Request received at: {}",format(receiveTime));
        log.debug("Oldest acceptable time: {} , Txn time: {}",
            format(expirationTime), format(txnTime));
        if (txnTime < expirationTime){
            log.error("Transaction is out-dated");
            throw new OutDatedTransactionException("Transaction is out-dated");
        }
        log.debug("Validation passed!");
    }

    @Override
    public Stats getStatistics() {
        // prepare
        Instant calculationMoment = Instant.now();
        long oldestAcceptableTxnTime = calculationMoment
            .minusMillis(prop.getAgeLimitForStatsByMillis()).toEpochMilli();
        log.debug("Statistics age limit in milliseconds: {}",prop.getAgeLimitForStatsByMillis());
        log.debug("Transactions NOT older than {} are involved in statistics.",
            format(oldestAcceptableTxnTime));
        Stats stats = store.getStatistics();
        log.debug("Statistics: {}",stats);
        return stats;
    }

    @Override public void reset() {
        store.reset();
    }

}
