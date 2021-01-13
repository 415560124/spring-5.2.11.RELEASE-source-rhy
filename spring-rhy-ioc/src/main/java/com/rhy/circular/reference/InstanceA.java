package com.rhy.circular.reference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Rhy
 * @title 实例A
 * @description 实例A
 * @createTime 2021年01月13日 17:01:00
 * @modifier：Rhy
 * @modification_time：2021-01-13 17:01
 */
@Component
public class InstanceA {
	@Autowired
	private InstanceB instanceB;

	public InstanceA() {
		System.out.println("实例化InstanceA");
	}
}
