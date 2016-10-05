package com.github.ledoyen.collectj;

import java.time.Instant;

public class Metric {

	private final String name;
	private final long value;
	private final Instant timestamp;

	public Metric(String name, long value, Instant timestamp) {
		this.name = name.intern();
		this.value = value;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public long getValue() {
		return value;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
}
