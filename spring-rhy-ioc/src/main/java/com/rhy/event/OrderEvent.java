package com.rhy.event;

import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * @author: Herion Lemon
 * @date: 2021年03月01日 16:18:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 订单事件对象
 */
public class OrderEvent extends ApplicationEvent implements Serializable {
	private static final long serialVersionUID=1L;
	private String name;

	public OrderEvent(Object source, String name) {
		super(source);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
