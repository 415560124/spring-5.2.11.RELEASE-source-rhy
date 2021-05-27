package com.rhy.transaction;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;

/**
 * @author: Herion Lemon
 * @date: 2021年05月15日 11:27:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class CustomDataSourceTransactionManager extends DataSourceTransactionManager {
    public CustomDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }
    public void manualCommit(DefaultTransactionStatus status){
        doCommit(status);
    }
    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        super.doCommit(status);
    }
}
