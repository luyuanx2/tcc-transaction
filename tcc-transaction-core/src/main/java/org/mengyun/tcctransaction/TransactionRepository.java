package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionXid;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 事务库接口.
 *
 * Created by changmingxie on 11/12/15.
 */
public interface TransactionRepository {

    /**
     * 创建事务日志记录.
     * @param transaction
     */
    int create(Transaction transaction);

    /**
     * 更新事务日志记录.
     * @param transaction
     */
    int update(Transaction transaction);

    /**
     * 删除事务日志记录.
     * @param transaction
     */
    int delete(Transaction transaction);

    /**
     * 根据xid查找事务日志记录.
     * @param xid
     */
    Transaction findByXid(TransactionXid xid);

    /**
     * 找出所有未处理事务日志（从某一时间点开始）.
     * @param date
     */
    List<Transaction> findAllUnmodifiedSince(Date date);
}
