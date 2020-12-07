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
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
//		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = applicationContext.getBean(UserService.class);
		userService.helloWorld();


		String[] dependsOn = new String[] { "A", "B", "C" };
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class);
		bdb.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bdb.addPropertyReference("age", "15");
		for (int i = 0; i < dependsOn.length; i++) {
			bdb.addDependsOn(dependsOn[i]);
		}

		bdb.applyCustomizers(new BeanDefinitionCustomizer()
		{

			@Override
			public void customize(BeanDefinition bd) {
				bdb.addPropertyReference("age", "16");
			}
		});

		RootBeanDefinition rbd = (RootBeanDefinition) bdb.getBeanDefinition();

	}
}
