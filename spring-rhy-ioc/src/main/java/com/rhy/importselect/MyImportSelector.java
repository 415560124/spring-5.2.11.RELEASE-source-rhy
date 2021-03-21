package com.rhy.importselect;

import com.rhy.bean.A;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author: Herion Lemon
 * @date: 2021年03月17日 12:27:00
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description: ImportSelector实现类
 */
public class MyImportSelector implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{
				A.class.getName()
		};
	}
}
