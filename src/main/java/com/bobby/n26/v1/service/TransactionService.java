package com.bobby.n26.v1.service;

import com.bobby.n26.v1.common.OutDatedTransactionException;
import com.bobby.n26.v1.model.Stats;
import com.bobby.n26.v1.model.Transaction;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public interface TransactionService {

    void save(Transaction transaction) throws OutDatedTransactionException
        , IllegalArgumentException;
    Stats getStatistics();

    /**
     * Clears / empties the transaction store.
     * Called by tests to flush the store before every test method
     * Of course it can be used by others as well if needed
     */
    void reset();
}
