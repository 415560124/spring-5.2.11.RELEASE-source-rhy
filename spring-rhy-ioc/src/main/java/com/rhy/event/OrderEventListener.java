package com.rhy.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author: Herion Lemon
 * @date: 2021年03月01日 16:20:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 订单事件监听器
 */
@Component
public class OrderEventListener implements ApplicationListener<OrderEvent> {
//	@EventListener(OrderEvent.class)
	@Override
	public void onApplicationEvent(OrderEvent event) {
		System.out.println("发生订单事件：name="+event.getName());
	}
}
