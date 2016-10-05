package com.github.ledoyen.collectj.input.sql;

import java.util.List;

import javax.sql.DataSource;

public interface SqlConfiguration {

	List<DataSource> dataSources();
}
