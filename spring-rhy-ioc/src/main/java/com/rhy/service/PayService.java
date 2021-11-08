package com.rhy.service;


import org.springframework.transaction.annotation.Transactional;

public interface PayService {


//	@Transactional(rollbackFor = Exception.class)
    void pay(String accountId, double money);

    void updateProductStore(Integer productId);
}
