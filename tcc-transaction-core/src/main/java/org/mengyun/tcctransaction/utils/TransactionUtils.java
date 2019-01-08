package org.mengyun.tcctransaction.utils;

import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * Created by changming.xie on 2/23/17.
 */
public class TransactionUtils {

    /**
     * 是否是合法的事务上下文.
     * @param isTransactionActive
     * @param propagation
     * @param transactionContext
     * @return
     */
    public static boolean isLegalTransactionContext(boolean isTransactionActive, Propagation propagation, TransactionContext transactionContext) {

        if (propagation.equals(Propagation.MANDATORY) && !isTransactionActive && transactionContext == null) {
            return false;
        }

        return true;
    }
}
