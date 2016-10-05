package com.github.ledoyen.collectj.setup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.cfg4j.provider.GenericTypeInterface;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.validator.BindingValidator;

import com.google.common.primitives.Primitives;

/**
 * Inspired by {@link org.cfg4j.provider.SimpleConfigurationProvider SimpleConfigurationProvider} with the add of recursive handling of types.
 */
public class SmartConfigurationProvider extends AbstractConfigurationProvider {

	public SmartConfigurationProvider(ConfigurationSource configurationSource, Environment environment) {
		super(configurationSource, environment);
	}

	@Override
	public <T> T getProperty(String key, Class<T> type) {
		return getProperty(key, () -> type);
	}

	@Override
	public <T> T getProperty(String key, GenericTypeInterface genericType) {
		Type type = genericType.getType();
		final T result;
		if (type instanceof Class && (String.class.equals(type) || Primitives.isWrapperType(Primitives.wrap((Class<?>) (type))))) {
			result = getSimpleProperty(key, genericType);
		} else {
			Class<T> propertyClazz = getClass(type);
			T proxy = (T) Proxy.newProxyInstance(SmartConfigurationProvider.class.getClassLoader(), new Class<?>[] { propertyClazz }, new PropertyBindInvocationHandler(this, key));

			if (!propertyClazz.getPackage().getName().startsWith("java.")) {
				new BindingValidator().validate(proxy, propertyClazz);
			}
			result = proxy;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClass(Type type) {
		final Class<T> clazz;
		if (type instanceof Class) {
			clazz = (Class<T>) type;
		} else if (type instanceof ParameterizedType) {
			clazz = (Class<T>) ((ParameterizedType) type).getRawType();
		} else {
			clazz = null;
		}
		return clazz;
	}
}
