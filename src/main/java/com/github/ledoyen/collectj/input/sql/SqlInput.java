package com.github.ledoyen.collectj.input.sql;

import com.github.ledoyen.collectj.Metric;
import com.github.ledoyen.collectj.input.Input;
import com.github.ledoyen.collectj.setup.Configuration;

public class SqlInput implements Input {

	@Configuration
	private SqlConfiguration conf;

	@Override
	public Iterable<Metric> execute() {
		// TODO Auto-generated method stub
		return null;
	}
}
