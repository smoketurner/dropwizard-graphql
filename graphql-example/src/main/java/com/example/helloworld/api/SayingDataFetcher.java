package com.example.helloworld.api;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class SayingDataFetcher implements DataFetcher<Saying> {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter = new AtomicLong();

    public SayingDataFetcher(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
    }

    @Override
    public Saying get(DataFetchingEnvironment environment) {
        final Optional<String> name = Optional
                .ofNullable(environment.getArgument("name"));
        final String value = String.format(template, name.orElse(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
}
