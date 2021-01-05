/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
		/** --------------------------调用{@link BeanDefinitionRegistryPostProcessor}开始-------------------------- */
		/**
		 * 此处保存处理过的后置处理器名称集合
		 */
		Set<String> processedBeans = new HashSet<>();


		/**
		 * 判断我们的bean工厂是否实现了{@link BeanDefinitionRegistry}（实现了该接口就有注册和获取Bean定义的能力）
		 */
		if (beanFactory instanceof BeanDefinitionRegistry) {
			/**
			 * 强行将工厂转为{@link BeanDefinitionRegistry}，等会用来注册bean定义
			 */
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			//保存BeanFactoryPostProcessor类型的后置处理器
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			//保存BeanDefinitionRegistryPostProcessor类型的后置处理器
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
			//遍历每个BeanFactoryPostProcessor
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				//如果实现了 BeanDefinitionRegistryPostProcessor 则调用
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					/**
					 * 调用 {@link BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}后置方法
					 */
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					//添加到BeanDefinitionRegistryPostProcessor集合中
					registryProcessors.add(registryProcessor);
				}
				else {
					/**
					 * 如果没有实现BeanDefinitionRegistryPostProcessor接口，那么他就是BeanFactoryPostProcessor
					 * 把当前的后置处理器加入到regularPostProcessors中
					 */
					regularPostProcessors.add(postProcessor);
				}
			}

			// 定义一个集合保存当前准备创建的BeanDefinitionRegistryPostProcessor
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			/**
			 * 首先：去容器中获取类型为{@link BeanDefinitionRegistryPostProcessor}的bean
			 * 调用实现了{@link PriorityOrdered}的BeanDefinitionRegistryPostProcessor
			 */
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			// 循环上一步获取的BeanDefinitionRegistryPostProcessor的类型名称
			for (String ppName : postProcessorNames) {
				/**
				 * 判断是否实现了{@link PriorityOrdered}接口
 				 */
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					/**
					 * {@link org.springframework.beans.factory.BeanFactory#getBean(String, Class)}类型必须匹配，返回beanName下的此类型实例
					 * 实例化对象后放到当前注册后置处理器集合中去
					 */
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//同时也加入到处理过的集合中去processedBeans
					processedBeans.add(ppName);
				}
			}
			//对currentRegistryProcessors（当前执行的后置处理器集合）进行排序，数值从小到大
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			/**
			 * 加入到BeanDefinitionRegitryPostProcessor集合中
			 * 一开始的时候，spring只会执行BeanDefinitionRegistryPostProcessor独有的方法
			 * 而不会执行BeanDefinitionRegistryPostProcessor父类的方法，即BeanFactoryPostProcessor的方法
			 * 所以这里需要把处理器放入到一个集合中后续统一执行BeanFactoryPostProcessor的方法
			 */
			registryProcessors.addAll(currentRegistryProcessors);
			/**
			 * 执行currentRegistryProcessors中的BeanDefinitionRegistryPostProcessor
			 * 可以理解为执行ConfigurationClassPostProcessor的postProcessorBeanDefinitionRegistry方法
			 * spring插拔式设计的体现，像ConfigurationClassPostProcessor就相当于一个组件，Spring很多事情就是交给组件去管理
			 */
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			//清空当前执行的集合，为后面Order使用
			currentRegistryProcessors.clear();

			/**
			 * 下一步：去容器中获取类型为BeanDefinitionRegistryPostProcessor的bean
			 * 非执行过的BeanDefinitionRegistryPostProcessor && 调用实现了{@link Ordered}的BeanDefinitionRegistryPostProcessor
			 */
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			registryProcessors.addAll(currentRegistryProcessors);
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			/**
			 * 最后：去容器中获取类型为BeanDefinitionRegistryPostProcessor的bean
			 * 非执行过的BeanDefinitionRegistryPostProcessor && 调用没有实现任何优先级接口的BeanDefinitionRegistryPostProcessor
			 * 这里reiterate=true为了比如BeanDefinitionRegistryPostProcessor调用后新注册了一个BeanDefinitionRegistryPostProcessor这样就可以再次执行新注册的了
			 */
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}

			/**
			 * 调用实现了{@link BeanDefinitionRegistryPostProcessor}的{@link BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}
			 * 因为{@link BeanDefinitionRegistryPostProcessor}继承了{@link BeanFactoryPostProcessor}
			 * 所以这里相当于先调用实现了两者的bean
			 */
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			/**
			 * 然后执行 regularPostProcessors 中的{@link BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}
			 * 这里的regularPostProcessors中都是手动调用{@link AnnotationConfigApplicationContext#addBeanFactoryPostProcessor}方法加入的bean
			 */
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			/**
			 * 若当前beanFactory没有实现BeanDefinitionRegistry
			 * 则直接调用传入的bean的{@link BeanFactoryPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)}
			 */
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		/** --------------------------调用{@link BeanDefinitionRegistryPostProcessor}结束-------------------------- */

		/** --------------------------调用{@link BeanFactoryPostProcessor}开始-------------------------- */
		/**
		 * 第二步：调用{@link BeanFactoryPostProcessor}
		 */
		/**
		 * 获得实现了{@link BeanFactoryPostProcessor}的bean
		 */
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		/** 实现了{@link PriorityOrdered}的{@link BeanFactoryPostProcessor}集合 */
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		/** 实现了{@link Ordered}的{@link BeanFactoryPostProcessor} */
		List<String> orderedPostProcessorNames = new ArrayList<>();
		/** 没有任何顺序接口实现的{@link BeanFactoryPostProcessor} */
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		/** 循环所有实现了BeanFactoryPostProcessor的beanName */
		for (String ppName : postProcessorNames) {
			//如果执行过则跳过
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			/** 加入到实现了{@link PriorityOrdered}的{@link BeanFactoryPostProcessor}集合 */
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			/** 加入到实现了{@link Ordered}的{@link BeanFactoryPostProcessor}集合 */
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			/** 加入到没有任何顺序接口实现的{@link BeanFactoryPostProcessor}集合 */
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 首先：排序 => 执行实现了{@link PriorityOrdered}的{@link BeanFactoryPostProcessor}集合
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// 下一步：实例化 => 排序=> 执行实现了{@link Ordered}的{@link BeanFactoryPostProcessor}集合
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// 最后：实例化 => 执行没有任何顺序接口实现的{@link BeanFactoryPostProcessor}集合
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		// 清除缓存的合并bean定义，因为后处理器可能已经修改了原始元数据，例如替换值中的占位符...
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// Register BeanPostProcessorChecker that logs an info message when
		// a bean is created during BeanPostProcessor instantiation, i.e. when
		// a bean is not eligible for getting processed by all BeanPostProcessors.
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// Separate between BeanPostProcessors that implement PriorityOrdered,
		// Ordered, and the rest.
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// First, register the BeanPostProcessors that implement PriorityOrdered.
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// Next, register the BeanPostProcessors that implement Ordered.
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// Now, register all regular BeanPostProcessors.
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// Finally, re-register all internal BeanPostProcessors.
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// Re-register post-processor for detecting inner beans as ApplicationListeners,
		// moving it to the end of the processor chain (for picking up proxies etc).
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		// Nothing to sort?
		if (postProcessors.size() <= 1) {
			return;
		}
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
