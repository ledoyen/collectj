package com.github.ledoyen.collectj;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.cfg4j.provider.ConfigurationProvider;

import com.github.ledoyen.collectj.input.Input;
import com.github.ledoyen.collectj.input.sql.DataSourceConfiguration;
import com.github.ledoyen.collectj.input.sql.SqlInput;
import com.github.ledoyen.collectj.setup.ArgumentsParser;
import com.github.ledoyen.collectj.setup.Configuration;
import com.github.ledoyen.collectj.setup.ConfigurationBuilder;
import com.github.ledoyen.collectj.utils.Classes;
import com.github.ledoyen.collectj.utils.ThrowingFunction;

public class Batch {

	public static void main(String[] args) {
		Options opts = ArgumentsParser.parse(args);

		ConfigurationProvider provider = ConfigurationBuilder.build(opts);

		DataSourceConfiguration lol = provider.bind("dataSource", DataSourceConfiguration.class);
		System.out.println(lol.name() + " " + lol.url());

		List<Class<? extends Input>> inputClasses = Arrays.asList(SqlInput.class);

		List<Input> inputInstances = inputClasses.stream().map(ThrowingFunction.silence(c -> {
			Input input = c.newInstance();
			Classes.doWithFields(input, f -> {
				Object localConfiguration = provider.bind(c.getSimpleName(), f.getType());
				f.set(input, localConfiguration);
			}, f -> f.isAnnotationPresent(Configuration.class));
			return input;
		})).collect(Collectors.toList());

		inputInstances.forEach(Input::execute);
		// TODO instantiate and wire with binded configuration
	}
}
