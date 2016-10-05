package com.github.ledoyen.collectj;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.beust.jcommander.Parameter;

public class Options {

	@Parameter(names = "-conf", description = "Directory where configuration can be found", required = true)
	private List<String> configurationSources;

	@Parameter(names = "-work", description = "Working directory where informations are passed from on execution to another", arity = 1)
	private File workingFolder = new File("work").getAbsoluteFile();

	public List<String> getConfigurationSources() {
		return configurationSources;
	}

	public File getWorkingFolder() {
		return workingFolder;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (configurationSources != null) {
			sb.append("-conf ").append(configurationSources.stream().collect(Collectors.joining(" "))).append(" ");
		}
		sb.append("-work ").append(workingFolder.getAbsolutePath());
		return sb.toString();
	}
}
