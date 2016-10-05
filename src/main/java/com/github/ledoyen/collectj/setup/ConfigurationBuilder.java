package com.github.ledoyen.collectj.setup;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.context.propertiesprovider.JsonBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProviderSelector;
import org.cfg4j.source.context.propertiesprovider.PropertyBasedPropertiesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;

import com.github.ledoyen.collectj.Options;

public class ConfigurationBuilder {

	public static ConfigurationProvider build(Options opts) {
		List<ConfigurationSource> sources = opts.getConfigurationSources().stream().map(s -> {
			String[] splitted = s.split(":", 2);
			if (splitted.length < 2) {
				throw new IllegalArgumentException("Unable to parse configuration [?:" + s + "]");
			}
			ConfigFilesProvider cfp = () -> Arrays.asList(splitted[1].split(",")).stream().map(Paths::get).collect(Collectors.toList());
			PropertiesProviderSelector pps = new PropertiesProviderSelector(new PropertyBasedPropertiesProvider(), new FixedYamlBasedPropertiesProvider(),
					new JsonBasedPropertiesProvider());
			ConfigurationSource cs = null;
			switch (splitted[0].toLowerCase()) {
			case "file":
				cs = new FilesConfigurationSource(cfp, pps);
				break;
			case "classpath":
				cs = new ClasspathConfigurationSource(cfp, pps);
				break;
			default:
				throw new IllegalArgumentException("Unrecognized prefix [" + splitted[0] + "]");
			}
			return cs;
		}).collect(Collectors.toList());

		ConfigurationSource source = new MergeConfigurationSource(sources.toArray(new ConfigurationSource[sources.size()]));

		return new SmartConfigurationProvider(source, new DefaultEnvironment());
		// return new ConfigurationProviderBuilder().withConfigurationSource(source).build();
	}
}
