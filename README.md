# 1.Spring笔记链接

---
https://www.processon.com/view/link/604b1a1f7d9c08389fdc0f36

# 2.@Transactional注解解析流程代码

```java
private static <T> T searchWithFindSemantics(AnnotatedElement element,
			@Nullable Class<? extends Annotation> annotationType, @Nullable String annotationName,
			@Nullable Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth) {

		if (visited.add(element)) {
			try {
				// 拿到元素上所有的注解
				Annotation[] annotations = element.getDeclaredAnnotations();
				if (annotations.length > 0) {
					List<T> aggregatedResults = (processor.aggregates() ? new ArrayList<>() : null);

					// 循环所有的注解做匹配，找到则返回
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
						if (!AnnotationUtils.isInJavaLangAnnotationPackage(currentAnnotationType)) {
							if (currentAnnotationType == annotationType ||
									currentAnnotationType.getName().equals(annotationName) ||
									processor.alwaysProcesses()) {
								T result = processor.process(element, annotation, metaDepth);
								if (result != null) {
									if (aggregatedResults != null && metaDepth == 0) {
										aggregatedResults.add(result);
									}
									else {
										return result;
									}
								}
							}
							// Repeatable annotations in container?
							else if (currentAnnotationType == containerType) {
								for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
									T result = processor.process(element, contained, metaDepth);
									if (aggregatedResults != null && result != null) {
										// No need to post-process since repeatable annotations within a
										// container cannot be composed annotations.
										aggregatedResults.add(result);
									}
								}
							}
						}
					}

					// 再循环所有注解的源类型再匹配一次，可能注解里会包含匹配的注解
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
						if (hasSearchableMetaAnnotations(currentAnnotationType, annotationType, annotationName)) {
							T result = searchWithFindSemantics(currentAnnotationType, annotationType, annotationName,
									containerType, processor, visited, metaDepth + 1);
							if (result != null) {
								processor.postProcess(currentAnnotationType, annotation, result);
								if (aggregatedResults != null && metaDepth == 0) {
									aggregatedResults.add(result);
								}
								else {
									return result;
								}
							}
						}
					}

					if (!CollectionUtils.isEmpty(aggregatedResults)) {
						// Prepend to support top-down ordering within class hierarchies
						processor.getAggregatedResults().addAll(0, aggregatedResults);
					}
				}
				//1.如果查找的元素是方法
				if (element instanceof Method) {
					Method method = (Method) element;
					T result;

					// 1.1.在桥接方法中找
					Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
					if (resolvedMethod != method) {
						result = searchWithFindSemantics(resolvedMethod, annotationType, annotationName,
								containerType, processor, visited, metaDepth);
						if (result != null) {
							return result;
						}
					}

					// 1.2.在接口方法上找
					Class<?>[] ifcs = method.getDeclaringClass().getInterfaces();
					if (ifcs.length > 0) {
						result = searchOnInterfaces(method, annotationType, annotationName,
								containerType, processor, visited, metaDepth, ifcs);
						if (result != null) {
							return result;
						}
					}

					// 1.3.在父类方法上找
					Class<?> clazz = method.getDeclaringClass();
					while (true) {
						clazz = clazz.getSuperclass();
						if (clazz == null || clazz == Object.class) {
							break;
						}
						Set<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethodsInBaseType(clazz);
						if (!annotatedMethods.isEmpty()) {
							for (Method annotatedMethod : annotatedMethods) {
								if (AnnotationUtils.isOverride(method, annotatedMethod)) {
									Method resolvedSuperMethod = BridgeMethodResolver.findBridgedMethod(annotatedMethod);
									result = searchWithFindSemantics(resolvedSuperMethod, annotationType, annotationName,
											containerType, processor, visited, metaDepth);
									if (result != null) {
										return result;
									}
								}
							}
						}
						// 父类的接口方法上找
						result = searchOnInterfaces(method, annotationType, annotationName,
								containerType, processor, visited, metaDepth, clazz.getInterfaces());
						if (result != null) {
							return result;
						}
					}
				}
				//2.如果查找的元素是类
				else if (element instanceof Class) {
					Class<?> clazz = (Class<?>) element;
					if (!Annotation.class.isAssignableFrom(clazz)) {
						// 2.1.查找类的接口
						for (Class<?> ifc : clazz.getInterfaces()) {
							T result = searchWithFindSemantics(ifc, annotationType, annotationName,
									containerType, processor, visited, metaDepth);
							if (result != null) {
								return result;
							}
						}
						// 2.2.查找类的父类
						Class<?> superclass = clazz.getSuperclass();
						if (superclass != null && superclass != Object.class) {
							T result = searchWithFindSemantics(superclass, annotationType, annotationName,
									containerType, processor, visited, metaDepth);
							if (result != null) {
								return result;
							}
						}
					}
				}
			}
			catch (Throwable ex) {
				AnnotationUtils.handleIntrospectionFailure(element, ex);
			}
		}
		return null;
	}
```

+ 解析传进来元素的注解`element.getDeclaredAnnotations()`，循环比对
+ 循环元素的注解，获得注解里面的注解`annotation.annotationType()`，循环比对
+ 如果查找的元素是方法`element instanceof Method`
  + 递归在桥接方法上找
  + 递归在接口方法上找
  + 递归在父类方法上找
  + 递归在父类的接口方法上找
+ 如果查找的元素是类`element instanceof Class`
  + 递归在接口上找
  + 递归在父类上找