package com.github.ledoyen.collectj.input;

import com.github.ledoyen.collectj.Metric;

public interface Input {

	Iterable<Metric> execute();
}
