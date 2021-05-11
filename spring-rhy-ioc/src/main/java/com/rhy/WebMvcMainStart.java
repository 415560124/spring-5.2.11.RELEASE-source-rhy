package com.rhy;

import com.rhy.config.Config;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

/**
 * @author: Herion Lemon
 * @date: 2021年05月10日 13:21:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class WebMvcMainStart {
	public static void main(String[] args) throws LifecycleException {
		//初始化Tomcat
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(9999);
		tomcat.addContext("/",System.getProperty("user.dir")+ File.separator+"spring-rhy-ioc"+File.separator+"src"+File.separator+"main"+File.separator+"webapp");
		//初始化spring
		tomcat.start();
		tomcat.getServer().await();
		//		System.out.println(System.getProperty("user.dir")+ File.separator+"spring-rhy-ioc"+File.separator+"src"+File.separator+"main"+File.separator+"webapp");
	}
}
