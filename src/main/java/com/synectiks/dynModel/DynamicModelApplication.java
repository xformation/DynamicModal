package com.synectiks.dynModel;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
@EntityScan(basePackages = "com.synectiks")
@ComponentScan(basePackages = { "com.synectiks" })
public class DynamicModelApplication {

	private static ConfigurableApplicationContext ctx;
	private static String[] params;

	public static void main(String[] args) {
		params = args;
		ctx = SpringApplication.run(DynamicModelApplication.class, args);
	}

	/**
	 * Method to start loggin all request payloads.
	 * @return
	 */
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}

	/**
	 * Utility method to get bean from spring context.
	 * @param cls
	 * @return
	 */
	public static <T> T getBean(Class<T> cls) {
		return ctx.getBean(cls);
	}

	/**
	 * Utility method to register a bean dynamically
	 * @param cls
	 */
	public static void registerBean(Class<?> cls) {
		GenericBeanDefinition gbd = new GenericBeanDefinition();
		gbd.setBeanClass(cls);
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition(cls.getSimpleName(), gbd);
	}

//	@Bean
//	@Primary
//	public EntityMapper getEntityMapper() {
//		return new EntityMapper() {
//			
//			@Override
//			public String mapToString(Object object) throws IOException {
//				return IUtils.ELST_MAPPER.writeValueAsString(object);
//			}
//			
//			@Override
//			public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
//				return IUtils.ELST_MAPPER.readValue(source, clazz);
//			}
//
//			@Override
//			public Map<String, Object> mapObject(Object source) {
//				try {
//					return IUtils.getMapFromJson(new JSONObject(mapToString(source)));
//				} catch (JSONException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//			@Override
//			public <T> T readObject(Map<String, Object> source, Class<T> targetType) {
//				try {
//					return mapToObject(mapToString(source), targetType);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				return null;
//			}
//		};
//	}

	/**
	 * Method to restart the application server for new classes loading.s
	 */
	public static void restart() {
		try {
			ctx.close();
			Thread.sleep(1000);
			ctx = SpringApplication.run(DynamicModelApplication.class, params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
