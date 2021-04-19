package com.rhy;

import com.rhy.config.Config;
import com.rhy.service.PayService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author: Herion Lemon
 * @date: 2021年04月19日 15:29:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class TransactionalMainStart {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		PayService payService = (PayService) applicationContext.getBean("payServiceImpl");
		payService.pay("1",10);

	}
}
