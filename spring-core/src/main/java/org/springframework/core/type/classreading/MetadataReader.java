/*
 * Copyright 2002-2009 the original author or authors.
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

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * Simple facade for accessing class metadata,
 * as read by an ASM {@link org.springframework.asm.ClassReader}.
 * 通过ASM读取class IO流资源组装访问元数据的门面接口
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface MetadataReader {

	/**
	 * 返回class文件的IO资源引用
	 */
	Resource getResource();

	/**
	 * 为基础class读取基本类元数据，返回基础类的元数据
	 */
	ClassMetadata getClassMetadata();

	/**
	 * 为基础类读取完成的注解元数据，包括注解方法的元数据。返回基础类的完整注解元数据
	 */
	AnnotationMetadata getAnnotationMetadata();

}
