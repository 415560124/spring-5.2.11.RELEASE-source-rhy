package com.rhy.extend;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Rhy
 * @title Condition实现类测试
 * @description Condition实现类测试
 * @createTime 2021年01月06日 14:15:00
 * @modifier：Rhy
 * @modification_time：2021-01-06 14:15
 */
public class ConditionImpl implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return true;
	}
}
