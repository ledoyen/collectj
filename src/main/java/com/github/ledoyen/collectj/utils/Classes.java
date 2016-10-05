package com.github.ledoyen.collectj.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Classes {

	/**
	 * Field are accessible inside the <b>fieldCallback</b>.
	 */
	public static void doWithFields(Object o, ThrowingConsumer<Field> fieldCallback, Predicate<Field> fieldFilter) {
		for (Field f : o.getClass().getDeclaredFields()) {
			if (fieldFilter.test(f)) {
				if (!f.isAccessible())
					f.setAccessible(true);
				ThrowingConsumer.silence(fieldCallback).accept(f);
			}
		}
	}

	/**
	 * Useful to reference package-protected classes.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Useful if constructor is not public.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instanciate(Class<? extends T> clazz, Object... constructorArgs) {
		Set<Constructor<?>> eligibleConstructors = new HashSet<>();
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (constructor.getParameterTypes().length == constructorArgs.length && argsMatches(constructor.getParameterTypes(), constructorArgs)) {
				eligibleConstructors.add(constructor);
			}
		}
		if (eligibleConstructors.size() == 0) {
			throw new IllegalArgumentException(String.format("No constructor of [%s] matches given arguments [%s]", clazz.getSimpleName(), Arrays.asList(constructorArgs)));
		} else if (eligibleConstructors.size() > 1) {
			throw new IllegalArgumentException(String.format("Too many constructors (%d) of [%s] matches given arguments [%s]", eligibleConstructors.size(), clazz.getSimpleName(),
					Arrays.asList(constructorArgs)));
		}

		Constructor<T> mathingConstructor = (Constructor<T>) eligibleConstructors.iterator().next();
		mathingConstructor.setAccessible(true);

		try {
			return mathingConstructor.newInstance(constructorArgs);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Assumes that the two arrays are of the same length.
	 */
	private static boolean argsMatches(Class<?>[] parameterTypes, Object[] constructorArgs) {
		boolean match = true;
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Object value = constructorArgs[i];
			if (value != null && !parameterType.isAssignableFrom(value.getClass())) {
				match = false;
				break;
			}
		}
		return match;
	}
}
