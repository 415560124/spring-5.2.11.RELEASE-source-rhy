package com.rhy.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EnableTransactionManagement
@Configuration
public class TransactionConfig {


	@Bean
	@Lookup
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setUrl("jdbc:mysql://localhost:3306/spring_source?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		return dataSource;
	}

	/**
	 *  配置JdbcTemplate Bean组件
	 *     <bean class="org.springframework.jdbc.core.JdbcTemplate" id="jdbcTemplate">
	 *         <property name="dataSource" ref="dataSource" ></property>
	 *     </bean>
	 * @param dataSource
	 * @return
	 */
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 *  配置事务管理器
	 *     <bean class="org.springframework.jdbc.datasource.DataSourceTransactionManager" id="transactionManager">
	 *         <property name="dataSource" ref="dataSource"></property>
	 *     </bean>
	 * @param dataSource
	 * @return
	 */
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	/**
	 * 按名称匹配的TransactionAttributeSource
	 * @return
	 */
//	@Bean
	public TransactionAttributeSource transactionAttributeSource(){
		Map<String, TransactionAttribute> txMap = new HashMap<>();
		//增删改
		RuleBasedTransactionAttribute ruleBasedTransactionAttribute = new RuleBasedTransactionAttribute();
		ruleBasedTransactionAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		ruleBasedTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txMap.put("add*",ruleBasedTransactionAttribute);
		txMap.put("save*",ruleBasedTransactionAttribute);
		txMap.put("insert*",ruleBasedTransactionAttribute);
		txMap.put("update*",ruleBasedTransactionAttribute);
		txMap.put("delete*",ruleBasedTransactionAttribute);

		//查询用于只读事务
		RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
		readOnly.setReadOnly(true);
		readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txMap.put("get*",readOnly);
		txMap.put("query*",readOnly);

		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
		source.setNameMap(txMap);

		return source;
	}

}
