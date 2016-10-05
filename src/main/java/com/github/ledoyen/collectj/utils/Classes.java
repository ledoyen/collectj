package com.github.ledoyen.collectj.utils;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class Classes {

	public static void doWithFields(Object o, ThrowingConsumer<Field> fieldCallback, Predicate<Field> fieldFilter) {
		for (Field f : o.getClass().getDeclaredFields()) {
			if (fieldFilter.test(f)) {
				if (!f.isAccessible())
					f.setAccessible(true);
				ThrowingConsumer.silence(fieldCallback).accept(f);
			}
		}
	}
}
