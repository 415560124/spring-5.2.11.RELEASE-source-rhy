package com.rhy.service.impl;

import com.rhy.dao.AccountInfoDao;
import com.rhy.dao.ProductInfoDao;
import com.rhy.service.PayService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.math.BigDecimal;

@Component
//@Transactional(rollbackFor = Exception.class)
public class PayServiceImpl implements PayService {

    @Autowired
    private AccountInfoDao accountInfoDao;

    @Autowired
    private ProductInfoDao productInfoDao;

	@Override
	@Transactional(rollbackFor = Exception.class)
//	@Transactional(propagation = Propagation.NEVER)
    public void pay(String accountId, double money) {
        //查询余额
        double blance = accountInfoDao.qryBlanceByUserId(accountId);
//		((DataSourceTransactionManager.DataSourceTransactionObject)((DefaultTransactionStatus)TransactionAspectSupport.currentTransactionStatus()).getTransaction()).getConnectionHolder().getConnection().commit();
        //余额不足正常逻辑
        if(new BigDecimal(blance).compareTo(new BigDecimal(money))<0) {
            throw new RuntimeException("余额不足");
        }


        //更新余额
         int retVal = accountInfoDao.updateAccountBlance(accountId,money);

        //库存-1
        //updateProductStore(1);
//        ((PayService)AopContext.currentProxy()).updateProductStore(1);

//        System.out.println(1/0);
//		throw new RuntimeException("余额不足");
    }

    @Override
//	@Transactional(propagation = Propagation.NESTED)
//	@Transactional(propagation = Propagation.REQUIRED)
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Transactional(propagation = Propagation.NESTED)
//	@Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateProductStore(Integer productId) {
//		((DataSourceTransactionManager.DataSourceTransactionObject)((DefaultTransactionStatus)TransactionAspectSupport.currentTransactionStatus()).getTransaction()).getConnectionHolder().getConnection().commit();

		try{
            productInfoDao.updateProductInfo(productId);
//			throw new RuntimeException("余额不足");
        }
        catch (Exception e) {
            throw new RuntimeException("内部异常");
        }
    }


}
