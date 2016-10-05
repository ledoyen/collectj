package com.github.ledoyen.collectj.setup;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.provider.GenericTypeInterface;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.validator.BindingValidator;

import com.github.drapostolos.typeparser.NoSuchRegisteredParserException;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import com.github.ledoyen.collectj.utils.Classes;

public abstract class AbstractConfigurationProvider implements ConfigurationProvider {

	protected final ConfigurationSource configurationSource;
	protected final Environment environment;

	/**
	 * {@link ConfigurationProvider} backed by provided {@link ConfigurationSource} and using {@code environment} to select environment. To construct
	 * this provider use {@link ConfigurationProviderBuilder}.
	 *
	 * @param configurationSource source for configuration
	 * @param environment {@link Environment} to use
	 */
	protected AbstractConfigurationProvider(ConfigurationSource configurationSource, Environment environment) {
		this.configurationSource = requireNonNull(configurationSource);
		this.environment = requireNonNull(environment);
	}

	@Override
	public Properties allConfigurationAsProperties() {
		try {
			return configurationSource.getConfiguration(environment);
		} catch (IllegalStateException | MissingEnvironmentException e) {
			throw new IllegalStateException("Couldn't fetch configuration from configuration source", e);
		}
	}

	@Override
	public <T> T bind(String prefix, Class<T> type) {
		return bind(this, prefix, type);
	}

	/**
	 * Create an instance of a given {@code type} that will be bound to the {@code configurationProvider}. Each time configuration changes the bound
	 * object will be updated with the new values. Use {@code prefix} to specify the relative path to configuration values. Please note that each
	 * method of returned object can throw runtime exceptions. For details see javadoc for
	 * {@link PropertyBindInvocationHandler#invoke(Object, Method, Object[])}.
	 *
	 * @param <T> interface describing configuration object to bind
	 * @param prefix relative path to configuration values (e.g. "myContext" will map settings "myContext.someSetting", "myContext.someOtherSetting")
	 * @param type {@link Class} for {@code <T>}
	 * @return configuration object bound to this {@link ConfigurationProvider}
	 * @throws NoSuchElementException when the provided {@code key} doesn't have a corresponding config value
	 * @throws IllegalArgumentException when property can't be coverted to {@code type}
	 * @throws IllegalStateException when provider is unable to fetch configuration value for the given {@code key}
	 */
	<T> T bind(ConfigurationProvider configurationProvider, String prefix, Class<T> type) {
		@SuppressWarnings("unchecked")
		// yes, a bit hacky, but no way to copy/paste this too
		T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type },
				Classes.instanciate(Classes.<InvocationHandler> forName("org.cfg4j.provider.BindInvocationHandler"), configurationProvider, prefix));

		new BindingValidator().validate(proxy, type);

		return proxy;
	}

	/**
	 * To use on a POJO leaf (Map & Collection of simple types are considered leaves).
	 */
	protected <T> T getSimpleProperty(String key, Class<T> type) {
		String propertyStr = getProperty(key);

		try {
			TypeParser parser = TypeParser.newBuilder().build();
			return parser.parse(propertyStr, type);
		} catch (TypeParserException | NoSuchRegisteredParserException e) {
			throw new IllegalArgumentException("Unable to cast value \'" + propertyStr + "\' to " + type, e);
		}
	}

	/**
	 * @see #getSimpleProperty(String, Class)
	 */
	protected <T> T getSimpleProperty(String key, GenericTypeInterface genericType) {
		String propertyStr = getProperty(key);

		try {
			TypeParser parser = TypeParser.newBuilder().build();
			@SuppressWarnings("unchecked")
			T property = (T) parser.parseType(propertyStr, genericType.getType());
			return property;
		} catch (TypeParserException | NoSuchRegisteredParserException e) {
			throw new IllegalArgumentException("Unable to cast value \'" + propertyStr + "\' to " + genericType, e);
		}
	}

	private String getProperty(String key) {
		try {

			Object property = configurationSource.getConfiguration(environment).get(key);

			if (property == null) {
				throw new NoSuchElementException("No configuration with key: " + key);
			}

			return property.toString();

		} catch (IllegalStateException e) {
			throw new IllegalStateException("Couldn't fetch configuration from configuration source for key: " + key, e);
		}
	}
}
