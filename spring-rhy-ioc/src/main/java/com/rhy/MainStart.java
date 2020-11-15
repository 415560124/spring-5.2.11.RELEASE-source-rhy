package com.rhy;

import com.rhy.config.Config;
import com.rhy.service.UserService;
import com.rhy.service.impl.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainStart {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = applicationContext.getBean(UserService.class);
		userService.helloWorld();
	}
}
