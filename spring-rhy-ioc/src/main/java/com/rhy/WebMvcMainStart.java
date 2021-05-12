package com.rhy;

import com.rhy.config.Config;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * @author: Herion Lemon
 * @date: 2021年05月10日 13:21:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class WebMvcMainStart {
	public static void main(String[] args) throws LifecycleException {
		//		System.out.println(System.getProperty("user.dir")+ File.separator+"spring-rhy-ioc"+File.separator+"src"+File.separator+"main"+File.separator+"webapp");
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(Config.class);
		String[] strings = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(annotationConfigApplicationContext, HandlerMapping.class);
	}
}
