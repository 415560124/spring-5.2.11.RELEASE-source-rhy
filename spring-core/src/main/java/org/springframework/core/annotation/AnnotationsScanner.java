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

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Scanner to search for relevant annotations in the annotation hierarchy of an
 * {@link AnnotatedElement}.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 5.2
 * @see AnnotationsProcessor
 */
abstract class AnnotationsScanner {

	private static final Annotation[] NO_ANNOTATIONS = {};

	private static final Method[] NO_METHODS = {};


	private static final Map<AnnotatedElement, Annotation[]> declaredAnnotationCache =
			new ConcurrentReferenceHashMap<>(256);

	private static final Map<Class<?>, Method[]> baseTypeMethodsCache =
			new ConcurrentReferenceHashMap<>(256);


	private AnnotationsScanner() {
	}


	/**
	 * Scan the hierarchy of the specified element for relevant annotations and
	 * call the processor as required.
	 * @param context an optional context object that will be passed back to the
	 * processor
	 * @param source the source element to scan
	 * @param searchStrategy the search strategy to use
	 * @param processor the processor that receives the annotations
	 * @return the result of {@link AnnotationsProcessor#finish(Object)}
	 */
	@Nullable
	static <C, R> R scan(C context, AnnotatedElement source, SearchStrategy searchStrategy,
			AnnotationsProcessor<C, R> processor) {

		R result = process(context, source, searchStrategy, processor);
		return processor.finish(result);
	}

	@Nullable
	private static <C, R> R process(C context, AnnotatedElement source,
			SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {
		//类型为Class
		if (source instanceof Class) {
			//执行处理Class的方法
			return processClass(context, (Class<?>) source, searchStrategy, processor);
		}
		//类型为Method
		if (source instanceof Method) {
			//执行处理Method的方法
			return processMethod(context, (Method) source, searchStrategy, processor);
		}
		return processElement(context, source, processor);
	}

	@Nullable
	private static <C, R> R processClass(C context, Class<?> source,
			SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {

		switch (searchStrategy) {
			case DIRECT:
				return processElement(context, source, processor);
			case INHERITED_ANNOTATIONS:
				return processClassInheritedAnnotations(context, source, searchStrategy, processor);
			case SUPERCLASS:
				return processClassHierarchy(context, source, processor, false, false);
			case TYPE_HIERARCHY:
				return processClassHierarchy(context, source, processor, true, false);
			case TYPE_HIERARCHY_AND_ENCLOSING_CLASSES:
				return processClassHierarchy(context, source, processor, true, true);
		}
		throw new IllegalStateException("Unsupported search strategy " + searchStrategy);
	}

	@Nullable
	private static <C, R> R processClassInheritedAnnotations(C context, Class<?> source,
			SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {

		try {
			if (isWithoutHierarchy(source, searchStrategy)) {
				return processElement(context, source, processor);
			}
			Annotation[] relevant = null;
			int remaining = Integer.MAX_VALUE;
			int aggregateIndex = 0;
			Class<?> root = source;
			while (source != null && source != Object.class && remaining > 0 &&
					!hasPlainJavaAnnotationsOnly(source)) {
				R result = processor.doWithAggregate(context, aggregateIndex);
				if (result != null) {
					return result;
				}
				Annotation[] declaredAnnotations = getDeclaredAnnotations(source, true);
				if (relevant == null && declaredAnnotations.length > 0) {
					relevant = root.getAnnotations();
					remaining = relevant.length;
				}
				for (int i = 0; i < declaredAnnotations.length; i++) {
					if (declaredAnnotations[i] != null) {
						boolean isRelevant = false;
						for (int relevantIndex = 0; relevantIndex < relevant.length; relevantIndex++) {
							if (relevant[relevantIndex] != null &&
									declaredAnnotations[i].annotationType() == relevant[relevantIndex].annotationType()) {
								isRelevant = true;
								relevant[relevantIndex] = null;
								remaining--;
								break;
							}
						}
						if (!isRelevant) {
							declaredAnnotations[i] = null;
						}
					}
				}
				result = processor.doWithAnnotations(context, aggregateIndex, source, declaredAnnotations);
				if (result != null) {
					return result;
				}
				source = source.getSuperclass();
				aggregateIndex++;
			}
		}
		catch (Throwable ex) {
			AnnotationUtils.handleIntrospectionFailure(source, ex);
		}
		return null;
	}

	@Nullable
	private static <C, R> R processClassHierarchy(C context, Class<?> source,
			AnnotationsProcessor<C, R> processor, boolean includeInterfaces, boolean includeEnclosing) {

		return processClassHierarchy(context, new int[] {0}, source, processor,
				includeInterfaces, includeEnclosing);
	}

	@Nullable
	private static <C, R> R processClassHierarchy(C context, int[] aggregateIndex, Class<?> source,
			AnnotationsProcessor<C, R> processor, boolean includeInterfaces, boolean includeEnclosing) {

		try {
			R result = processor.doWithAggregate(context, aggregateIndex[0]);
			if (result != null) {
				return result;
			}
			if (hasPlainJavaAnnotationsOnly(source)) {
				return null;
			}
			Annotation[] annotations = getDeclaredAnnotations(source, false);
			result = processor.doWithAnnotations(context, aggregateIndex[0], source, annotations);
			if (result != null) {
				return result;
			}
			aggregateIndex[0]++;
			if (includeInterfaces) {
				for (Class<?> interfaceType : source.getInterfaces()) {
					R interfacesResult = processClassHierarchy(context, aggregateIndex,
						interfaceType, processor, true, includeEnclosing);
					if (interfacesResult != null) {
						return interfacesResult;
					}
				}
			}
			Class<?> superclass = source.getSuperclass();
			if (superclass != Object.class && superclass != null) {
				R superclassResult = processClassHierarchy(context, aggregateIndex,
					superclass, processor, includeInterfaces, includeEnclosing);
				if (superclassResult != null) {
					return superclassResult;
				}
			}
			if (includeEnclosing) {
				// Since merely attempting to load the enclosing class may result in
				// automatic loading of sibling nested classes that in turn results
				// in an exception such as NoClassDefFoundError, we wrap the following
				// in its own dedicated try-catch block in order not to preemptively
				// halt the annotation scanning process.
				try {
					Class<?> enclosingClass = source.getEnclosingClass();
					if (enclosingClass != null) {
						R enclosingResult = processClassHierarchy(context, aggregateIndex,
							enclosingClass, processor, includeInterfaces, true);
						if (enclosingResult != null) {
							return enclosingResult;
						}
					}
				}
				catch (Throwable ex) {
					AnnotationUtils.handleIntrospectionFailure(source, ex);
				}
			}
		}
		catch (Throwable ex) {
			AnnotationUtils.handleIntrospectionFailure(source, ex);
		}
		return null;
	}

	@Nullable
	private static <C, R> R processMethod(C context, Method source,
			SearchStrategy searchStrategy, AnnotationsProcessor<C, R> processor) {

		switch (searchStrategy) {
			case DIRECT:
			case INHERITED_ANNOTATIONS:
				return processMethodInheritedAnnotations(context, source, processor);
			case SUPERCLASS:
				return processMethodHierarchy(context, new int[] {0}, source.getDeclaringClass(),
						processor, source, false);
			//Transactional查找注解采用这种策略，包含所有超类及接口
			case TYPE_HIERARCHY:
			case TYPE_HIERARCHY_AND_ENCLOSING_CLASSES:
				return processMethodHierarchy(context, new int[] {0}, source.getDeclaringClass(),
						processor, source, true);
		}
		throw new IllegalStateException("Unsupported search strategy " + searchStrategy);
	}

	@Nullable
	private static <C, R> R processMethodInheritedAnnotations(C context, Method source,
			AnnotationsProcessor<C, R> processor) {

		try {
			R result = processor.doWithAggregate(context, 0);
			return (result != null ? result :
				processMethodAnnotations(context, 0, source, processor));
		}
		catch (Throwable ex) {
			AnnotationUtils.handleIntrospectionFailure(source, ex);
		}
		return null;
	}

	@Nullable
	private static <C, R> R processMethodHierarchy(C context, int[] aggregateIndex,
			Class<?> sourceClass, AnnotationsProcessor<C, R> processor, Method rootMethod,
			boolean includeInterfaces) {

		try {
			R result = processor.doWithAggregate(context, aggregateIndex[0]);
			if (result != null) {
				return result;
			}
			/**
			 * 是否为java内部类或者类型是{@link Ordered}
			 */
			if (hasPlainJavaAnnotationsOnly(sourceClass)) {
				return null;
			}
			boolean calledProcessor = false;
			if (sourceClass == rootMethod.getDeclaringClass()) {
				result = processMethodAnnotations(context, aggregateIndex[0],
					rootMethod, processor);
				calledProcessor = true;
				if (result != null) {
					return result;
				}
			}
			else {
				for (Method candidateMethod : getBaseTypeMethods(context, sourceClass)) {
					if (candidateMethod != null && isOverride(rootMethod, candidateMethod)) {
						result = processMethodAnnotations(context, aggregateIndex[0],
							candidateMethod, processor);
						calledProcessor = true;
						if (result != null) {
							return result;
						}
					}
				}
			}
			if (Modifier.isPrivate(rootMethod.getModifiers())) {
				return null;
			}
			if (calledProcessor) {
				aggregateIndex[0]++;
			}
			if (includeInterfaces) {
				for (Class<?> interfaceType : sourceClass.getInterfaces()) {
					R interfacesResult = processMethodHierarchy(context, aggregateIndex,
						interfaceType, processor, rootMethod, true);
					if (interfacesResult != null) {
						return interfacesResult;
					}
				}
			}
			Class<?> superclass = sourceClass.getSuperclass();
			if (superclass != Object.class && superclass != null) {
				R superclassResult = processMethodHierarchy(context, aggregateIndex,
					superclass, processor, rootMethod, includeInterfaces);
				if (superclassResult != null) {
					return superclassResult;
				}
			}
		}
		catch (Throwable ex) {
			AnnotationUtils.handleIntrospectionFailure(rootMethod, ex);
		}
		return null;
	}

	private static <C> Method[] getBaseTypeMethods(C context, Class<?> baseType) {
		if (baseType == Object.class || hasPlainJavaAnnotationsOnly(baseType)) {
			return NO_METHODS;
		}

		Method[] methods = baseTypeMethodsCache.get(baseType);
		if (methods == null) {
			boolean isInterface = baseType.isInterface();
			methods = isInterface ? baseType.getMethods() : ReflectionUtils.getDeclaredMethods(baseType);
			int cleared = 0;
			for (int i = 0; i < methods.length; i++) {
				if ((!isInterface && Modifier.isPrivate(methods[i].getModifiers())) ||
						hasPlainJavaAnnotationsOnly(methods[i]) ||
						getDeclaredAnnotations(methods[i], false).length == 0) {
					methods[i] = null;
					cleared++;
				}
			}
			if (cleared == methods.length) {
				methods = NO_METHODS;
			}
			baseTypeMethodsCache.put(baseType, methods);
		}
		return methods;
	}

	private static boolean isOverride(Method rootMethod, Method candidateMethod) {
		return (!Modifier.isPrivate(candidateMethod.getModifiers()) &&
				candidateMethod.getName().equals(rootMethod.getName()) &&
				hasSameParameterTypes(rootMethod, candidateMethod));
	}

	private static boolean hasSameParameterTypes(Method rootMethod, Method candidateMethod) {
		if (candidateMethod.getParameterCount() != rootMethod.getParameterCount()) {
			return false;
		}
		Class<?>[] rootParameterTypes = rootMethod.getParameterTypes();
		Class<?>[] candidateParameterTypes = candidateMethod.getParameterTypes();
		if (Arrays.equals(candidateParameterTypes, rootParameterTypes)) {
			return true;
		}
		return hasSameGenericTypeParameters(rootMethod, candidateMethod,
				rootParameterTypes);
	}

	private static boolean hasSameGenericTypeParameters(
			Method rootMethod, Method candidateMethod, Class<?>[] rootParameterTypes) {

		Class<?> sourceDeclaringClass = rootMethod.getDeclaringClass();
		Class<?> candidateDeclaringClass = candidateMethod.getDeclaringClass();
		if (!candidateDeclaringClass.isAssignableFrom(sourceDeclaringClass)) {
			return false;
		}
		for (int i = 0; i < rootParameterTypes.length; i++) {
			Class<?> resolvedParameterType = ResolvableType.forMethodParameter(
					candidateMethod, i, sourceDeclaringClass).resolve();
			if (rootParameterTypes[i] != resolvedParameterType) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	private static <C, R> R processMethodAnnotations(C context, int aggregateIndex, Method source,
			AnnotationsProcessor<C, R> processor) {

		Annotation[] annotations = getDeclaredAnnotations(source, false);
		R result = processor.doWithAnnotations(context, aggregateIndex, source, annotations);
		if (result != null) {
			return result;
		}
		Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(source);
		if (bridgedMethod != source) {
			Annotation[] bridgedAnnotations = getDeclaredAnnotations(bridgedMethod, true);
			for (int i = 0; i < bridgedAnnotations.length; i++) {
				if (ObjectUtils.containsElement(annotations, bridgedAnnotations[i])) {
					bridgedAnnotations[i] = null;
				}
			}
			return processor.doWithAnnotations(context, aggregateIndex, source, bridgedAnnotations);
		}
		return null;
	}

	@Nullable
	private static <C, R> R processElement(C context, AnnotatedElement source,
			AnnotationsProcessor<C, R> processor) {

		try {
			R result = processor.doWithAggregate(context, 0);
			return (result != null ? result : processor.doWithAnnotations(
				context, 0, source, getDeclaredAnnotations(source, false)));
		}
		catch (Throwable ex) {
			AnnotationUtils.handleIntrospectionFailure(source, ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	static <A extends Annotation> A getDeclaredAnnotation(AnnotatedElement source, Class<A> annotationType) {
		Annotation[] annotations = getDeclaredAnnotations(source, false);
		for (Annotation annotation : annotations) {
			if (annotation != null && annotationType == annotation.annotationType()) {
				return (A) annotation;
			}
		}
		return null;
	}

	static Annotation[] getDeclaredAnnotations(AnnotatedElement source, boolean defensive) {
		boolean cached = false;
		//从缓存中读一次，在解析后会把source当成key，然后解析出来的annotations[]当成value
		Annotation[] annotations = declaredAnnotationCache.get(source);
		//返回不为空代表被缓存过
		if (annotations != null) {
			cached = true;
		}
		else {
			//获得source（类/方法）上的注解
			annotations = source.getDeclaredAnnotations();
			//source上有注解
			if (annotations.length != 0) {
				//检查获得的注解是不是忽略的注解 ，之后的判断中如果存在不是忽略的注解则设置false
				boolean allIgnored = true;
				for (int i = 0; i < annotations.length; i++) {
					Annotation annotation = annotations[i];
					//检查是不是忽略的注解
					if (isIgnorable(annotation.annotationType()) ||
							!AttributeMethods.forAnnotationType(annotation.annotationType()).isValid(annotation)) {
						annotations[i] = null;
					}
					else {
						allIgnored = false;
					}
				}
				//如果所有注解都是被忽略的注解则返回NO_ANNOTATIONS
				annotations = (allIgnored ? NO_ANNOTATIONS : annotations);
				if (source instanceof Class || source instanceof Member) {
					declaredAnnotationCache.put(source, annotations);
					cached = true;
				}
			}
		}
		/**
		 * 传进来的defensive（应该是否克隆的标志） = false或者annotations == 0，那么直接返回annotations
		 * 否则如果传进来的defensive = true , 并且annotation.length !=0 ，并且如果没有被缓存则返回annotations，否则返回annotations.clone()
		 */
		if (!defensive || annotations.length == 0 || !cached) {
			return annotations;
		}
		return annotations.clone();
	}

	private static boolean isIgnorable(Class<?> annotationType) {
		return AnnotationFilter.PLAIN.matches(annotationType);
	}

	static boolean isKnownEmpty(AnnotatedElement source, SearchStrategy searchStrategy) {
		//是否为java内部类 || 类型{@link Ordered}接口
		if (hasPlainJavaAnnotationsOnly(source)) {
			return true;
		}
		/**
		 * 查询策略为{@link SearchStrategy#DIRECT} || 没有上级类了
		 */
		if (searchStrategy == SearchStrategy.DIRECT || isWithoutHierarchy(source, searchStrategy)) {
			//如果是方法 && 是桥接
			if (source instanceof Method && ((Method) source).isBridge()) {
				//直接返回false代表不知道source里是否为空
				return false;
			}
			//获得source的注解
			return getDeclaredAnnotations(source, false).length == 0;
		}
		/**
		 * 上面条件不满足代表存在上级对象，所以不知道是否为空，所以返回false
		 */
		return false;
	}

	static boolean hasPlainJavaAnnotationsOnly(@Nullable Object annotatedElement) {
		//传入元素类型是Class
		if (annotatedElement instanceof Class) {
			return hasPlainJavaAnnotationsOnly((Class<?>) annotatedElement);
		}
		else if (annotatedElement instanceof Member) {
			return hasPlainJavaAnnotationsOnly(((Member) annotatedElement).getDeclaringClass());
		}
		else {
			return false;
		}
	}

	/**
	 * 是否为java内部类或者类型是{@link Ordered}
	 * @param type
	 * @return
	 */
	static boolean hasPlainJavaAnnotationsOnly(Class<?> type) {
		/**
		 * 类型名是java.开头，就是java内部的类 || 类型是{@link Ordered}
		 */
		return (type.getName().startsWith("java.") || type == Ordered.class);
	}

	private static boolean isWithoutHierarchy(AnnotatedElement source, SearchStrategy searchStrategy) {
		//传入对象类型是Object
		if (source == Object.class) {
			return true;
		}
		//类型是Class
		if (source instanceof Class) {
			Class<?> sourceClass = (Class<?>) source;
			//没有上级类 = 类型的父类为Object && 类型没有接口
			boolean noSuperTypes = (sourceClass.getSuperclass() == Object.class &&
					sourceClass.getInterfaces().length == 0);
			/**
			 * 如果搜索策略是{@link SearchStrategy#TYPE_HIERARCHY_AND_ENCLOSING_CLASSES} ? 没有上级类 && 没有匿名内部类 : 没有上级类
			 * 因为搜索策略TYPE_HIERARCHY_AND_ENCLOSING_CLASSES代表搜索所有包括匿名内部类
			 */
			return (searchStrategy == SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES ? noSuperTypes &&
					sourceClass.getEnclosingClass() == null : noSuperTypes);
		}
		//类型是Method
		if (source instanceof Method) {
			Method sourceMethod = (Method) source;
			//方法是否为私有的（私有的说明没有上级方法）
			return (Modifier.isPrivate(sourceMethod.getModifiers()) ||
					//再次递归解析class
					isWithoutHierarchy(sourceMethod.getDeclaringClass(), searchStrategy));
		}
		return true;
	}

	static void clearCache() {
		declaredAnnotationCache.clear();
		baseTypeMethodsCache.clear();
	}

}
