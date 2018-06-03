package com.bobby.n26.v1.service.store;

import com.bobby.n26.v1.common.OutDatedTransactionException;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public interface TransactionStore {
    void save(Transaction transaction);
    Stats getStatistics();
    void reset();
}
