package com.rhy.service.impl;

import com.rhy.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserServiceImpl implements UserService {

	@PostConstruct
	public void postConstruct(){
		System.out.println("UserServiceImpl 构造之后，初始前调用");
	}
	@Override
	public void helloWorld() {
		System.out.println("Hello world!");
	}
}
