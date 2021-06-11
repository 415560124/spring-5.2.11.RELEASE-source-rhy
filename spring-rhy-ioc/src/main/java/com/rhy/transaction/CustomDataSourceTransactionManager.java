package com.rhy.transaction;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * @author: Herion Lemon
 * @date: 2021年05月15日 11:27:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class CustomDataSourceTransactionManager extends DataSourceTransactionManager implements Serializable {
	private static final long serialVersionUID=1L;

    public CustomDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 手动提交
     * @param status
     */
    public void manualCommit(DefaultTransactionStatus status){
        super.doCommit(status);
    }
    public void manualCommitForBatch(DefaultTransactionStatus status){
        triggerBeforeCommit(status);
        super.doCommit(status);
    }
}
