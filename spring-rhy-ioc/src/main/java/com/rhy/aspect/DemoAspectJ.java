package com.rhy.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author: Herion Lemon
 * @date: 2021年03月16日 16:48:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: AspectJ切面类示例
 */
@Aspect
@Component
public class DemoAspectJ {
	@Pointcut("execution(* com.rhy.service.impl.*.*(..))")
	public void pointcut() {};

	@Before(value = "pointcut()")
	public void methodBefore(JoinPoint joinPoint){
		String methodName = joinPoint.getSignature().getName();
		System.out.println("执行目标方法【"+methodName+"】的<前置通知>,入参"+ Arrays.asList(joinPoint.getArgs()));
	}

	@After(value = "pointcut()")
	public void methodAfter(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		System.out.println("执行目标方法【"+methodName+"】的<后置通知>,入参"+Arrays.asList(joinPoint.getArgs()));
	}

	@AfterReturning(value = "pointcut()",returning = "result")
	public void methodReturning(JoinPoint joinPoint, Object result) {
		String methodName = joinPoint.getSignature().getName();
		System.out.println("执行目标方法【"+methodName+"】的<返回通知>,入参"+Arrays.asList(joinPoint.getArgs()));
	}

	@AfterThrowing(value = "pointcut()")
	public void methodAfterThrowing(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		System.out.println("执行目标方法【"+methodName+"】的<异常通知>,入参"+Arrays.asList(joinPoint.getArgs()));
	}

	public void hello(){

	}
}
