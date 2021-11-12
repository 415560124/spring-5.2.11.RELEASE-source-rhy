package com.rhy.beanPostProcessor;

import com.rhy.service.impl.OrderServiceImpl;
import com.rhy.service.impl.UserServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * @author: Rhy
 * @date: 2021/11/12 16:49
 * @description:
 */
@Component
public class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {
	private static final Log logging = LogFactory.getLog(MyBeanPostProcessor.class);
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessBeforeInitialization 初始化前执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(beanName.equals("userServiceImpl")){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessAfterInitialization 初始化后执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	/**
	 * 初始化前执行
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		if(beanClass.isAssignableFrom(UserServiceImpl.class)){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation 实例化前执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation 实例化后执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessProperties 填充属性时执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
	}

	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if(beanType.isAssignableFrom(UserServiceImpl.class)){
			System.out.println("MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition 最后一次修改RootBeanDefinition机会，属性赋值之前执行");
			//可以对BeanDefinition做一些修改
			beanDefinition.setInitMethodName("init");
			beanDefinition.getPropertyValues().add("orderService",new OrderServiceImpl());
		}
	}

}
