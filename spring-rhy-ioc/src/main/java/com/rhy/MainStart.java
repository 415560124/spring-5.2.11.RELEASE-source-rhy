package com.rhy;

import com.rhy.bean.A;
import com.rhy.config.Config;
import com.rhy.service.UserService;
import com.rhy.service.impl.UserServiceImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainStart {
	public static void main(String[] args) {

		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = applicationContext.getBean(UserService.class);
		userService.helloWorld();


	}
}

  