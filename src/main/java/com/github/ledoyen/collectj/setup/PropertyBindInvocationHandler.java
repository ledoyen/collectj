package com.github.ledoyen.collectj.setup;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.cfg4j.provider.ConfigurationProvider;

public class PropertyBindInvocationHandler implements InvocationHandler {

	private final ConfigurationProvider simpleConfigurationProvider;
	private final String prefix;

	/**
	 * Create invocation handler which fetches property from given {@code configurationProvider} using call to
	 * {@link ConfigurationProvider#getProperty(String, Class)} method.
	 *
	 * @param configurationProvider configuration provider to use for fetching properties
	 * @param prefix prefix for calls to {@link ConfigurationProvider#getProperty(String, Class)}
	 */
	PropertyBindInvocationHandler(ConfigurationProvider configurationProvider, String prefix) {
		this.simpleConfigurationProvider = requireNonNull(configurationProvider);
		this.prefix = requireNonNull(prefix);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

}
