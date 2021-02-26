package com.rhy.bean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class A implements FactoryBean<A> {

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


}
