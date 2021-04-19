package com.rhy.extend;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Rhy
 * @title 测试BeanDefinitionRegistryPostProcess实现类
 * @description 测试BeanDefinitionRegistryPostProcess实现类
 * @createTime 2021年01月05日 17:04:00
 * @modifier：Rhy
 * @modification_time：2021-01-05 17:04
 */
//@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("执行了：MyBeanDefinitionRegistryPostProcessor#postProcessBeanFactory");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("执行了：MyBeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry");
	}
}
