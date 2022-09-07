package com.smoketurner.dropwizard.graphql;

import com.apollographql.federation.graphqljava._Entity;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class EntitiesDataFetcher implements DataFetcher<Object> {
    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
        return dataFetchingEnvironment.<List<Map<String, Object>>>getArgument(_Entity.argumentName)
            .stream()
            .map(o -> resolveReference(o, dataFetchingEnvironment))
            .collect(Collectors.toList());
    }

    public abstract Object resolveReference(Map<String, Object> ref, DataFetchingEnvironment dataFetchingEnvironment);
}
