package com.rhy.webmvc;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * @author: Herion Lemon
 * @date: 2021年05月12日 11:32:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class ExtendRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
	@Override
	protected void handlerMethodsInitialized(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
		super.handlerMethodsInitialized(handlerMethods);
		System.out.println("进入了自己的ExtendRequestMappingHandlerMapping");
	}
}
