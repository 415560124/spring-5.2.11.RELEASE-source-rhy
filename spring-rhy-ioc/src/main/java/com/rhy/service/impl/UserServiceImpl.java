package com.rhy.service.impl;

import com.rhy.service.PayService;
import com.rhy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	public UserServiceImpl() {
		System.out.println("UserServiceImpl constructor no param");
	}
	@Autowired
	public UserServiceImpl(PayService payService) {
		System.out.println("UserServiceImpl constructor have param");
	}

	@Override
	public void helloWorld() {
		System.out.println("Hello world!");
	}
}
