package com.rhy;

import com.rhy.config.Config;
import com.rhy.service.impl.TargetService;
import com.rhy.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainStart {
	public static void main(String[] args) {

		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = applicationContext.getBean(UserService.class);
		userService.helloWorld();
		TargetService targetService = applicationContext.getBean(TargetService.class);
		//这里的userSerivce为null，因为代理对象虽然继承实现，但是他把原对象封装到了target中
		System.out.println(targetService.userService);
		targetService.test();

//		applicationContext.publishEvent(new OrderEvent(applicationContext,"ioc完成1"));
//		applicationContext.publishEvent(new OrderEvent2(applicationContext,"ioc完成3"));
//		applicationContext.publishEvent(new OrderEvent(applicationContext,"ioc完成2"));

	}
}

  