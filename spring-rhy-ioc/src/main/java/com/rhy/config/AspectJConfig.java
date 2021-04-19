package com.rhy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: Herion Lemon
 * @date: 2021年03月16日 13:21:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: 代理配置
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
//(proxyTargetClass = true)//强制使用CGLIB
public class AspectJConfig {

}
