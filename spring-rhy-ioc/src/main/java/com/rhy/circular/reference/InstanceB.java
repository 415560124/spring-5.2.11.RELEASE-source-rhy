package com.rhy.circular.reference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Rhy
 * @title 实例B
 * @description 实例B
 * @createTime 2021年01月13日 17:02:00
 * @modifier：Rhy
 * @modification_time：2021-01-13 17:02
 */
@Component
public class InstanceB {
	@Autowired
	private InstanceA instanceA;
}
