/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.core.type.classreading;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.asm.ClassReader;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.lang.Nullable;

/**
 * {@link MetadataReader} implementation based on an ASM
 * {@link org.springframework.asm.ClassReader}.
 * SimpleMetadataReader 为MetadataReader的默认实现，在创建SimpleMetadataReader通过ASM字节码操控框架读取class读取class资源流生成classMetadata与annotationMetadata
 * @author Juergen Hoeller
 * @author Costin Leau
 * @since 2.5
 */
final class SimpleMetadataReader implements MetadataReader {

	private static final int PARSING_OPTIONS = ClassReader.SKIP_DEBUG
			| ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;
	/**
	 * class的IO流引用
	 */
	private final Resource resource;
	/**
	 * class类的注解元数据
	 */
	private final AnnotationMetadata annotationMetadata;

	/**
	 * 用于通过对ASM字节码操控框架读取class 读取class资源流
	 * @param resource
	 * @param classLoader
	 * @throws IOException
	 */
	SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
		//注解元数据读取访问者读取注解元数据
		SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
		//通过ASM字节码操控框架读取class
		getClassReader(resource).accept(visitor, PARSING_OPTIONS);
		this.resource = resource;
		//注解元数据
		this.annotationMetadata = visitor.getMetadata();
	}

	private static ClassReader getClassReader(Resource resource) throws IOException {
		// 获取class类IO流
		try (InputStream is = resource.getInputStream()) {
			try {
				return new ClassReader(is);
			}
			catch (IllegalArgumentException ex) {
				throw new NestedIOException("ASM ClassReader failed to parse class file - " +
						"probably due to a new Java class file version that isn't supported yet: " + resource, ex);
			}
		}
	}


	@Override
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public ClassMetadata getClassMetadata() {
		//返回当前类的注解元数据
		return this.annotationMetadata;
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		//返回当前类的注解元数据
		return this.annotationMetadata;
	}

}
