package com.rhy.circular.reference;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rhy
 * @title 循环依赖启动类
 * @description 循环依赖启动类
 * @createTime 2021年01月13日 17:03:00
 * @modifier：Rhy
 * @modification_time：2021-01-13 17:03
 */
public class MainStart {
	public static Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
	public static Map<String,Object> singletonObjects = new HashMap<>();
	public static void main(String[] args) throws Exception {
		loadBeanDefinitions();

		for (String beanName:beanDefinitionMap.keySet()) {
			getBean(beanName);
		}
	}

	/**
	 * 模拟getBean
	 * @param beanName bean名称
	 */
	private static Object getBean(String beanName) throws Exception {
		//实例化
		RootBeanDefinition rootBeanDefinition = (RootBeanDefinition) beanDefinitionMap.get(beanName);
		Class cls = rootBeanDefinition.getBeanClass();
		Object instance = cls.newInstance();
		//赋值属性
		Field[] fields = cls.getDeclaredFields();
		for(Field field : fields){
			field.setAccessible(true);
			Object bean = getBean(field.getName());
			field.set(instance,bean);
		}
		//初始化
		//加入到缓存中
		singletonObjects.put(beanName,instance);

		return instance;
	}

	/**
	 * 加载bean定义
	 */
	private static void loadBeanDefinitions() {
		beanDefinitionMap.put("instanceA",new RootBeanDefinition(InstanceA.class));
		beanDefinitionMap.put("instanceB",new RootBeanDefinition(InstanceB.class));
	}
}
