package com.rhy.service.impl;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author: Rhy
 * @date: 2021/11/29 20:13
 * @description:
 */
@Component
public class SmartLifecycleImpl implements SmartLifecycle {
	private boolean isRunning;
	@Override
	public void start() {
		System.out.println("SmartLifecycle#start");
		isRunning = true;
	}

	@Override
	public void stop() {
		System.out.println("SmartLifecycle#stop");
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}
}
