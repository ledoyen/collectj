package com.github.ledoyen.collectj.input.sql;

import com.github.ledoyen.collectj.Metric;
import com.github.ledoyen.collectj.input.Input;
import com.github.ledoyen.collectj.setup.Configuration;

public class SqlInput implements Input {

	@Configuration
	private SqlConfiguration conf;

	@Override
	public Iterable<Metric> execute() {
		System.out.println(conf.dataSources());
		return null;
	}
}
