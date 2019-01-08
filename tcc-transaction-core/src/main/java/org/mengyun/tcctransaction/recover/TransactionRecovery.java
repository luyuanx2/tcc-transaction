package org.mengyun.tcctransaction.recover;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.OptimisticLockException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.support.TransactionConfigurator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 事务恢复任务处理器
 *
 * Created by changmingxie on 11/10/15.
 */
public class TransactionRecovery {

    static final Logger logger = Logger.getLogger(TransactionRecovery.class.getSimpleName());

    /**
     * TCC事务配置器.
     */
    private TransactionConfigurator transactionConfigurator;

    /**
     * 开始事务恢复操作,被{@code RecoverScheduledJob#init()}调用
     */
    public void startRecover() {

        // 找出所有需要恢复的事务信息
        List<Transaction> transactions = loadErrorTransactions();

        recoverErrorTransactions(transactions);
    }

    private List<Transaction> loadErrorTransactions() {


        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

        TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
        RecoverConfig recoverConfig = transactionConfigurator.getRecoverConfig();

        return transactionRepository.findAllUnmodifiedSince(new Date(currentTimeInMillis - recoverConfig.getRecoverDuration() * 1000));
    }

    /**
     * 事务恢复逻辑
     *
     * @param transactions
     */
    private void recoverErrorTransactions(List<Transaction> transactions) {


        for (Transaction transaction : transactions) {

            // 重试的次数超过最大的重试次数，则跳过
            if (transaction.getRetriedCount() > transactionConfigurator.getRecoverConfig().getMaxRetryCount()) {

                logger.error(String.format("recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)));
                continue;
            }

            // 如果是分支事务 && (分支事务的创建时间+最大重试次数*需要执行事务恢复的时间间隔) > 大于当前时间，则跳过
            if (transaction.getTransactionType().equals(TransactionType.BRANCH)
                    && (transaction.getCreateTime().getTime() +
                    transactionConfigurator.getRecoverConfig().getMaxRetryCount() *
                            transactionConfigurator.getRecoverConfig().getRecoverDuration() * 1000
                    > System.currentTimeMillis())) {
                continue;
            }
            
            try {
                // 重试次数+1
                transaction.addRetriedCount();

                if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {
                    // 如果是CONFIRMING(2)状态，则将事务往前执行
                    transaction.changeStatus(TransactionStatus.CONFIRMING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    // 事务提交操作
                    transaction.commit();
                    // 执行成功删除
                    transactionConfigurator.getTransactionRepository().delete(transaction);

                } else if (transaction.getStatus().equals(TransactionStatus.CANCELLING)
                        || transaction.getTransactionType().equals(TransactionType.ROOT)) {
                    // 如果是 CANCELLING(3)状态，或者是主事务，则将事务回滚
                    // 回滚情况分为：
                    // 1、主事务为【TRYING】阶段
                    // 2、或者事务为【CANCELLING】阶段
                    transaction.changeStatus(TransactionStatus.CANCELLING);
                    transactionConfigurator.getTransactionRepository().update(transaction);
                    // 事务回滚操作
                    transaction.rollback();
                    // 执行成功删除
                    transactionConfigurator.getTransactionRepository().delete(transaction);
                }

            } catch (Throwable throwable) {

                if (throwable instanceof OptimisticLockException
                        || ExceptionUtils.getRootCause(throwable) instanceof OptimisticLockException) {
                    logger.warn(String.format("optimisticLockException happened while recover. txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)), throwable);
                } else {
                    logger.error(String.format("recover failed, txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)), throwable);
                }
            }
        }
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
