package com.rhy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author: Herion Lemon
 * @date: 2021年03月10日 14:26:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 监听器配置，在源码里能看到，监听器的beanName为
 * {@link AbstractApplicationContext#APPLICATION_EVENT_MULTICASTER_BEAN_NAME}所以只需要替换bean里面的这个实例就行了
 */
//@Configuration
public class ListenerConfig {
	/**
	 * 设置事件多播器发布事件为异步
	 * @return
	 */
	@Bean("applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster(){
		SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
		simpleApplicationEventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return simpleApplicationEventMulticaster;
	}
}
