package com.github.ledoyen.collectj.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R> {

	R apply(T t) throws Exception;

	static <T, R> Function<T, R> silence(ThrowingFunction<T, R> throwing) {
		return t -> {
			try {
				return throwing.apply(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
