package com.rhy.circular.reference;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rhy
 * @title 循环依赖启动类
 * @description 循环依赖启动类
 * @createTime 2021年01月13日 17:03:00
 * @modifier：Rhy
 * @modification_time：2021-01-13 17:03
 */
public class MainStart {
	public static Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
	/**
	 * 一级缓存
	 */
	public static Map<String,Object> singletonObjects = new ConcurrentHashMap<>();
	/**
	 * 二级缓存：将完整Bean和纯净Bean分离开来，避免读取到不完整的Bean
	 */
	public static Map<String,Object> earlySingletonObjects = new ConcurrentHashMap<>();
	/**
	 * 三级缓存：存函数式接口
	 */
	public static Map<String, ObjectFactory> singletonFactories = new ConcurrentHashMap<>();

	/**
	 * Bean正在创建标记
	 */
	public static Set<String> singletonsCurrentyInCreation = new HashSet<>();
	public static void main(String[] args) throws Exception {
		loadBeanDefinitions();

		for (String beanName:beanDefinitionMap.keySet()) {
			getBean(beanName);
		}
	}
	/**
	 * 加载bean定义
	 */
	private static void loadBeanDefinitions() {
		beanDefinitionMap.put("instanceA",new RootBeanDefinition(InstanceA.class));
		beanDefinitionMap.put("instanceB",new RootBeanDefinition(InstanceB.class));
	}
	/**
	 * 1.循环依赖问题的产生
	 * 模拟getBean
	 * @param beanName bean名称
	 */
//	private static Object getBean(String beanName) throws Exception {
//		//实例化
//		RootBeanDefinition rootBeanDefinition = (RootBeanDefinition) beanDefinitionMap.get(beanName);
//		Class<?> cls = rootBeanDefinition.getBeanClass();
//		Object instance = cls.newInstance();
//		//赋值属性
//		Field[] fields = cls.getDeclaredFields();
//		for(Field field : fields){
//			field.setAccessible(true);
//			Object bean = getBean(field.getName());
//			field.set(instance,bean);
//		}
//		//初始化
//		//加入到缓存中
//		singletonObjects.put(beanName,instance);
//
//		return instance;
//	}
//	/**
//	 * 2.用一级缓存解决循环依赖
//	 * <p>
//	 *     问题：
//	 *     （1）无法保证多线程下的一级缓存Bean完整性
//	 * </p>
//	 * 模拟getBean
//	 * @param beanName bean名称
//	 */
//	private static Object getBean(String beanName) throws Exception {
//		Object singleton = getSingleton(beanName);
//		if(singleton != null){
//			return singleton;
//		}
//		//实例化
//		RootBeanDefinition rootBeanDefinition = (RootBeanDefinition) beanDefinitionMap.get(beanName);
//		Class<?> cls = rootBeanDefinition.getBeanClass();
//		Object instance = cls.newInstance();
//		//加入到缓存中
//		singletonObjects.put(beanName,instance);
//		//赋值属性
//		Field[] fields = cls.getDeclaredFields();
//		for(Field field : fields){
//			field.setAccessible(true);
//			Object bean = getBean(field.getName());
//			field.set(instance,bean);
//		}
//		//初始化
//		return instance;
//	}

//	/**
//	 * 2.用一级缓存解决循环依赖
//	 * <p>
//	 *     问题：
//	 *     （1）无法保证多线程下的一级缓存Bean完整性
//	 * </p>
//	 * 从缓存池中获取对象
//	 * @param beanName
//	 * @return
//	 */
//	private static Object getSingleton(String beanName) {
//		if(singletonObjects.containsKey(beanName)){
//			return singletonObjects.get(beanName);
//		}
//		return null;
//	}
//	/**
//	 * 3.用二级缓存解决循环依赖
//	 * <p>
//	 *     问题：
//	 *     （1）耦合，多线程下的读取性能
//	 * </p>
//	 * 模拟getBean
//	 * @param beanName bean名称
//	 */
//	private static Object getBean(String beanName) throws Exception {
//		Object singleton = getSingleton(beanName);
//		if(singleton != null){
//			return singleton;
//		}
//		//实例化
//		RootBeanDefinition rootBeanDefinition = (RootBeanDefinition) beanDefinitionMap.get(beanName);
//		Class<?> cls = rootBeanDefinition.getBeanClass();
//		Object instance = cls.newInstance();
//		//加入到二级缓存中
//		earlySingletonObjects.put(beanName,instance);
//		//赋值属性
//		Field[] fields = cls.getDeclaredFields();
//		for(Field field : fields){
//			field.setAccessible(true);
//			Object bean = getBean(field.getName());
//			field.set(instance,bean);
//		}
//		//初始化
//		//加入到一级缓存中
//		singletonObjects.put(beanName,instance);
//		return instance;
//	}
//
//	/**
//	 * 3.用二级缓存解决循环依赖
//	 * <p>
//	 *     问题：
//	 *     （1）耦合，多线程下的读取性能
//	 * </p>
//	 * 从缓存池中获取对象
//	 * @param beanName
//	 * @return
//	 */
//	private static Object getSingleton(String beanName) {
//		if(singletonObjects.containsKey(beanName)){
//			return singletonObjects.get(beanName);
//		}else if(earlySingletonObjects.containsKey(beanName)){
//			/**
//			 * 创建循环依赖，耦合，应该通过BeanPostProcessor
//			 * Spring还是希望正常的Bean在初始化后创建代理，只有当前为循环依赖时，才在实例化时创建动态代理
//			 */
//			return new JdkProxyBeanPostProcessor().getEarlyBeanReference(earlySingletonObjects.get(beanName),beanName);
//		}
//		return null;
//	}
	/**
 	 * 4.用三级缓存解决循环依赖
	 * <p>
	 * 目的：解耦，处理AOP
	 * </p>
 	 * 模拟getBean
 	 * @param beanName bean名称
 	 */
	private static Object getBean(String beanName) throws Exception {
		Object singleton = getSingleton(beanName);
		if(singleton != null){
			return singleton;
		}
		//标记正在创建
		singletonsCurrentyInCreation.add(beanName);
		//createBean

		//实例化
		RootBeanDefinition rootBeanDefinition = (RootBeanDefinition) beanDefinitionMap.get(beanName);
		Class<?> cls = rootBeanDefinition.getBeanClass();
		Object instance = cls.newInstance();
		//加入到三级缓存 解耦，处理AOP
		singletonFactories.put(beanName,() -> {
			/**
			 * 解耦
			 * Spring还是希望正常的Bean在初始化后创建代理，只有当前为循环依赖时，才在实例化时创建动态代理
			 */
			return new JdkProxyBeanPostProcessor().getEarlyBeanReference(earlySingletonObjects.get(beanName),beanName);
		});
		//赋值属性
		Field[] fields = cls.getDeclaredFields();
		for(Field field : fields){
			field.setAccessible(true);
			Object bean = getBean(field.getName());
			field.set(instance,bean);
		}
		//初始化 -- 一般在此时创建AOP动态代理
		//如果二级缓存中有，就是存在循环依赖。
		//如果被代理了二级缓存中就是代理对象，否则就是源对象
		if(earlySingletonObjects.containsKey(beanName)){
			instance = earlySingletonObjects.get(beanName);
		}
		//加入到一级缓存中
		singletonObjects.put(beanName,instance);
		//remove 二级缓存和三级缓存
		return instance;
	}

	/**
	 * 4.用三级缓存解决循环依赖
	 * <p>
	 * 目的：解耦，处理AOP
	 * </p>
	 * 从缓存池中获取对象
	 * @param beanName
	 * @return
	 */
	private static Object getSingleton(String beanName) {
		Object bean = singletonObjects.get(beanName);
		//一级缓存中不存在 && Bean正在创建（循环依赖）
		if(bean == null && singletonsCurrentyInCreation.contains(beanName)){
			//则从二级缓存中拿
			bean = earlySingletonObjects.get(beanName);
			//二级缓存中没有则调用三级缓存创建代理对象
			if(bean == null){
				//调用三级缓存中的工厂方法创建代理对象，如果不被代理则返回源对象
				ObjectFactory objectFactory = singletonFactories.get(beanName);
				if(objectFactory != null){
					bean = objectFactory.getObject();
					earlySingletonObjects.put(beanName,bean);
					//remove三级缓存
				}
			}
		}
		return null;
	}
}
