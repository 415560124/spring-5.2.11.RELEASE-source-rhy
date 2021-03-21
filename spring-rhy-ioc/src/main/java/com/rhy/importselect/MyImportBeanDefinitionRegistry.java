package com.rhy.importselect;

import com.rhy.bean.B;
import com.rhy.bean.C;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: Herion Lemon
 * @date: 2021年03月17日 12:52:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: ImportBeanDefinitionRegistry实现类
 */
public class MyImportBeanDefinitionRegistry implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {

	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(B.class);
		registry.registerBeanDefinition("b",beanDefinition);
	}
}
