package com.github.ledoyen.collectj.utils;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> {

	void accept(T t) throws Exception;

	static <T> Consumer<T> silence(ThrowingConsumer<T> throwing) {
		return t -> {
			try {
				throwing.accept(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
