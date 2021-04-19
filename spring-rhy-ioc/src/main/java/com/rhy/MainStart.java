package com.rhy;

import com.rhy.bean.A;
import com.rhy.config.Config;
import com.rhy.event.OrderEvent;
import com.rhy.event.OrderEvent2;
import com.rhy.service.UserService;
import com.rhy.service.impl.UserServiceImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MainStart {
	public static void main(String[] args) {

		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = applicationContext.getBean(UserService.class);
		userService.helloWorld();

//		applicationContext.publishEvent(new OrderEvent(applicationContext,"ioc完成1"));
//		applicationContext.publishEvent(new OrderEvent2(applicationContext,"ioc完成3"));
//		applicationContext.publishEvent(new OrderEvent(applicationContext,"ioc完成2"));

	}
}

  