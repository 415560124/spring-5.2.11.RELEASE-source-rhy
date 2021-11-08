package com.rhy.event;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author: Herion Lemon
 * @date: 2021年03月01日 17:03:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 上下文加载完成监听器
 */
//@Component
public class ContextRefreshedEventListener {
	@EventListener(ContextRefreshedEvent.class)
	public void doContextStartedEventListener(){

		System.out.println("spring环境初始化完成");
	}
}
