package com.rhy.web;

import com.rhy.config.Config;
import com.rhy.config.WebMvcConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @author: Rhy
 * @date: 2022/1/14 17:40
 * @description:
 */
public class RhyWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected Class<?>[] getRootConfigClasses() {
		//service mapper的Spring容器配置
		return new Class[]{Config.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		//springMvc的Spring容器配置
		return new Class[]{WebMvcConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		//Servlet映射的资源路径
		return new String[]{"/"};
	}
}
