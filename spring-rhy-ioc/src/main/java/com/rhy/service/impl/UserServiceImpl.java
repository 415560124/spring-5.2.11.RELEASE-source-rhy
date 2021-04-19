package com.rhy.service.impl;

import com.rhy.service.UserService;
import org.springframework.stereotype.Service;

//@Service
public class UserServiceImpl implements UserService {

	@Override
	public void helloWorld() {
		System.out.println("Hello world!");
	}
}
