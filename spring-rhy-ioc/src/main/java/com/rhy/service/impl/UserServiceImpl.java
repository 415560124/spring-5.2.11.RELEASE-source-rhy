package com.rhy.service.impl;

import com.rhy.service.OrderService;
import com.rhy.service.PayService;
import com.rhy.service.UserService;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
//	@Autowired
	@Resource
	private PayService payService;
	public UserServiceImpl() {
		System.out.println("UserServiceImpl constructor no param");
	}
	@Autowired
	public UserServiceImpl(PayService payService) {
		System.out.println("UserServiceImpl constructor have param");
	}

	@PostConstruct
	public void postConstruct(){
//		System.out.println("UserServiceImpl 构造之后，初始前调用");
		System.out.println(this.getClass().getName()+" postConstruct");
	}

	public void init(){
		System.out.println(this.getClass().getName()+" init");
	}
	@Override
	public void helloWorld() {
		System.out.println("Hello world!");
	}

	/**
	 * 通过{@link MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition}自定义填充属性值
	 */
	private OrderService orderService;
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
}
