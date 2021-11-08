package com.rhy.config;

import com.rhy.webmvc.ExtendRequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.PathMatcher;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.util.UrlPathHelper;

import java.util.Map;
import java.util.function.Predicate;

/**
 * @author: Herion Lemon
 * @date: 2021年05月12日 09:48:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
@EnableWebMvc
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	/**
	 * Return a {@link RequestMappingHandlerMapping} ordered at 0 for mapping
	 * requests to annotated controllers.
	 */
//	@Override
//	@Bean
//	@SuppressWarnings("deprecation")
//	public RequestMappingHandlerMapping requestMappingHandlerMapping(
//			@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
//			@Qualifier("mvcConversionService") FormattingConversionService conversionService,
//			@Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
//
//		RequestMappingHandlerMapping mapping = new ExtendRequestMappingHandlerMapping();
//		mapping.setOrder(0);
//		mapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider));
//		mapping.setContentNegotiationManager(contentNegotiationManager);
//		mapping.setCorsConfigurations(getCorsConfigurations());
//
//		PathMatchConfigurer configurer = getPathMatchConfigurer();
//
//		Boolean useSuffixPatternMatch = configurer.isUseSuffixPatternMatch();
//		if (useSuffixPatternMatch != null) {
//			mapping.setUseSuffixPatternMatch(useSuffixPatternMatch);
//		}
//		Boolean useRegisteredSuffixPatternMatch = configurer.isUseRegisteredSuffixPatternMatch();
//		if (useRegisteredSuffixPatternMatch != null) {
//			mapping.setUseRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch);
//		}
//		Boolean useTrailingSlashMatch = configurer.isUseTrailingSlashMatch();
//		if (useTrailingSlashMatch != null) {
//			mapping.setUseTrailingSlashMatch(useTrailingSlashMatch);
//		}
//
//		UrlPathHelper pathHelper = configurer.getUrlPathHelper();
//		if (pathHelper != null) {
//			mapping.setUrlPathHelper(pathHelper);
//		}
//		PathMatcher pathMatcher = configurer.getPathMatcher();
//		if (pathMatcher != null) {
//			mapping.setPathMatcher(pathMatcher);
//		}
////		Map<String, Predicate<Class<?>>> pathPrefixes = configurer.getPathPrefixes();
////		if (pathPrefixes != null) {
////			mapping.setPathPrefixes(pathPrefixes);
////		}
//
//		return mapping;
//	}
}
