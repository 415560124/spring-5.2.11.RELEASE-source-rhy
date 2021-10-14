package com.rhy.service.impl;

import com.rhy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Rhy
 * @date: 2021/10/14 16:38
 * @description:
 */
@Component
public class TargetService {
	@Autowired
	public UserService userService;

	public void test(){
		System.out.println("UserService："+userService);
	}
}
