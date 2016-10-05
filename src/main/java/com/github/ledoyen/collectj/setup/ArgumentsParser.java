package com.github.ledoyen.collectj.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.github.ledoyen.collectj.Batch;
import com.github.ledoyen.collectj.Options;

public class ArgumentsParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentsParser.class);

	public static Options parse(String[] args) {
		Options options = new Options();
		JCommander parser = new JCommander(options);
		parser.setProgramName(Batch.class.getName());
		try {
			parser.parse(args);
			LOGGER.info("Launching with " + options);
		} catch (ParameterException e) {
			LOGGER.error(e.getMessage());
			System.err.println(e.getMessage());
			StringBuilder usage = new StringBuilder();
			parser.usage(usage);
			System.err.println(usage.toString());
			System.exit(-1);
		}
		return options;
	}
}
