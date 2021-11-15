package com.rhy.beanPostProcessor;

import com.rhy.service.impl.OrderServiceImpl;
import com.rhy.service.impl.UserServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * @author: Rhy
 * @date: 2021/11/12 16:49
 * @description:
 */
@Component
public class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor, DestructionAwareBeanPostProcessor {
	private static final Log logging = LogFactory.getLog(MyBeanPostProcessor.class);

	/**
	 * 初始化前执行
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessBeforeInitialization 初始化前执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
	}

	/**
	 * 初始化后执行
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
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

	/**
	 * 实例化后执行
	 * @param bean the bean instance created, with properties not having been set yet
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation 实例化后执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
	}

	/**
	 * 属性赋值时执行
	 * @param pvs the property values that the factory is about to apply (never {@code null})
	 * @param bean the bean instance created, but whose properties have not yet been set
	 * @param beanName the name of the bean
	 * @return
	 * @throws BeansException
	 */
	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("InstantiationAwareBeanPostProcessor#postProcessProperties 填充属性时执行");
		}
		return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
	}

	/**
	 * Bean创建后，实例化后置处理器之前执行
	 * @param beanDefinition the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 */
	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if(beanType.isAssignableFrom(UserServiceImpl.class)){
			System.out.println("MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition 最后一次修改RootBeanDefinition机会，属性赋值之前执行");
			//可以对BeanDefinition做一些修改
			beanDefinition.setInitMethodName("init");
			beanDefinition.setDestroyMethodName(AbstractBeanDefinition.INFER_METHOD);
//			beanDefinition.setDestroyMethodName("beanDefinitionDestroy");
			beanDefinition.getPropertyValues().add("orderService",new OrderServiceImpl());
		}
	}

	/**
	 * Bean销毁之前执行
	 * @param bean the bean instance to be destroyed
	 * @param beanName the name of the bean
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
		if(bean instanceof UserServiceImpl){
			System.out.println("DestructionAwareBeanPostProcessor#postProcessBeforeDestruction Bean销毁之前执行");
		}
	}

	/**
	 * 判断Bean是否需要销毁
	 * @param bean the bean instance to check
	 * @return
	 */
	@Override
	public boolean requiresDestruction(Object bean) {
		if(bean instanceof UserServiceImpl){
			System.out.println("DestructionAwareBeanPostProcessor#requiresDestruction 判断Bean是否需要销毁");
		}
		return DestructionAwareBeanPostProcessor.super.requiresDestruction(bean);
	}
}
