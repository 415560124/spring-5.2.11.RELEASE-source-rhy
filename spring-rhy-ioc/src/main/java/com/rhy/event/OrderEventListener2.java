package com.rhy.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author: Herion Lemon
 * @date: 2021年03月01日 16:20:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 订单事件监听器
 */
//@Component
@Lazy
public class OrderEventListener2 implements ApplicationListener<OrderEvent2> {
//	@EventListener(OrderEvent.class)
	@Override
	public void onApplicationEvent(OrderEvent2 event) {
		System.out.println("发生订单事件：name="+event.getName());
	}
}
