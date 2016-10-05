package com.github.ledoyen.collectj.setup;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.cfg4j.source.context.propertiesprovider.PropertiesProvider;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.scanner.ScannerException;

/**
 * Copy of {@link org.cfg4j.source.context.propertiesprovider.YamlBasedPropertiesProvider YamlBasedPropertiesProvider} without the buggy
 * {@link org.cfg4j.source.context.propertiesprovider.FormatBasedPropertiesProvider#flatten FormatBasedPropertiesProvider#flatten}.
 */
public class FixedYamlBasedPropertiesProvider implements PropertiesProvider {

	@Override
	public Properties getProperties(InputStream inputStream) {
		Yaml yaml = new Yaml();

		Properties properties = new Properties();

		try (Reader reader = new UnicodeReader(inputStream)) {

			Object object = yaml.load(reader);

			if (object != null) {
				Map<String, Object> yamlAsMap = convertToMap(object);
				properties.putAll(flatten(yamlAsMap));
			}

			return properties;

		} catch (IOException | ScannerException e) {
			throw new IllegalStateException("Unable to load yaml configuration from provided stream", e);
		}
	}

	/**
	 * Convert given Yaml document to a multi-level map.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> convertToMap(Object yamlDocument) {
		Map<String, Object> yamlMap = new LinkedHashMap<>();

		// Document is a text block
		if (!(yamlDocument instanceof Map)) {
			yamlMap.put("content", yamlDocument);
			return yamlMap;
		}

		for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) yamlDocument).entrySet()) {
			Object value = entry.getValue();

			if (value instanceof Map) {
				value = convertToMap(value);
			} else if (value instanceof Collection) {
				ArrayList<Map<String, Object>> collection = new ArrayList<>();

				for (Object element : ((Collection) value)) {
					collection.add(convertToMap(element));
				}

				value = collection;
			}

			yamlMap.put(entry.getKey().toString(), value);
		}
		return yamlMap;
	}

	private Map<String, Object> flatten(Map<String, Object> yamlAsMap) {
		Map<String, Object> acc = new LinkedHashMap<>();
		recFlatten(null, yamlAsMap, acc, false);
		return acc;
	}

	@SuppressWarnings("unchecked")
	private void recFlatten(String prefix, Object yamlAsMap, Map<String, Object> acc, boolean parentCollection) {
		String completePrefix = prefix != null ? prefix + "." : "";
		if (yamlAsMap instanceof Map) {
			for (Entry<String, Object> entry : ((Map<String, Object>) yamlAsMap).entrySet()) {
				if (parentCollection && "content".equals(entry.getKey())) {
					recFlatten(prefix, entry.getValue(), acc, false);
				} else {
					recFlatten(completePrefix + entry.getKey(), entry.getValue(), acc, false);
				}
			}
		} else if (yamlAsMap instanceof Iterable) {
			int i = 0;
			for (Object item : (Iterable<?>) yamlAsMap) {
				recFlatten(prefix + "[" + i + "]", item, acc, true);
				i++;
			}
			acc.put(prefix + ".size", i);
		} else {
			acc.put(prefix, yamlAsMap);
		}
	}
}
