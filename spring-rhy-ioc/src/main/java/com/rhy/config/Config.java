package com.rhy.config;

import com.rhy.bean.A;
import com.rhy.bean.B;
import com.rhy.bean.C;
import com.rhy.extend.ConditionImpl;
import com.rhy.importselect.MyImportBeanDefinitionRegistry;
import com.rhy.importselect.MyImportSelector;
import org.springframework.context.annotation.*;

import java.util.function.Predicate;

@Configuration
//@Conditional({ConditionImpl.class})
@ComponentScan("com.rhy,;com.wyy")
@Import({MyImportSelector.class, MyImportBeanDefinitionRegistry.class})
public class Config {
//	@Bean
//	public A a(){
//		A a = new A();
//		a.setB(b());
//		return a;
//	}
//	@Bean
//	public B b(){
//		return new B();
//	}
	/**
	 * 配置类中的嵌套类
	 * 源码：{@link ConfigurationClassParser#doProcessConfigurationClass(ConfigurationClass, ConfigurationClassParser.SourceClass, Predicate)}=>processMemberClasses
	 */
//	@ComponentScan("com.rhy")
//	class Config2{
//
//	}
}
