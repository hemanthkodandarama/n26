package com.bobby.n26.v1.common;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
public class OutDatedTransactionException extends Exception {
    public OutDatedTransactionException() {
    }

    public OutDatedTransactionException(String message) {
        super(message);
    }

    public OutDatedTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutDatedTransactionException(Throwable cause) {
        super(cause);
    }

    public OutDatedTransactionException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
