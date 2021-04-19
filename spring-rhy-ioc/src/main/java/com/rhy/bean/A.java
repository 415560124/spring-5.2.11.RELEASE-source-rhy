package com.rhy.bean;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class A implements FactoryBean<A> {
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	BeanFactory beanFactory;
	public A() {
		System.out.println("init A");
	}

	@Override
	public A getObject() throws Exception {
		return new A();
	}

	@Override
	public Class<A> getObjectType() {
		return A.class;
	}
	@Autowired
	B b;



	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
