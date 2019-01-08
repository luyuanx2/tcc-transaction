package org.mengyun.tcctransaction.spring.repository;


import org.mengyun.tcctransaction.repository.JdbcTransactionRepository;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;

/**
 * spring jdbc 事务仓库
 * Created by changmingxie on 10/30/15.
 */
public class SpringJdbcTransactionRepository extends JdbcTransactionRepository {

    /**
     * 获取数据库连接
     * @return
     */
    @Override
    protected Connection getConnection() {
        return DataSourceUtils.getConnection(this.getDataSource());
    }

    /**
     * 释放连接
     * @param con
     */
    @Override
    protected void releaseConnection(Connection con) {
        DataSourceUtils.releaseConnection(con, this.getDataSource());
    }
}
