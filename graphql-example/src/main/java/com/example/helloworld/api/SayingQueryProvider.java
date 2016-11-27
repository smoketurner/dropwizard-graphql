package com.example.helloworld.api;

import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLObjectType.newObject;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLQueryProvider;

public class SayingQueryProvider implements GraphQLQueryProvider {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter = new AtomicLong();

    public SayingQueryProvider(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
    }

    @Override
    public GraphQLObjectType getQuery() {
        return newObject().name("Saying").description("A friendly greeting")
                .field(field -> field.type(GraphQLLong).name("id")
                        .description("Unique ID").dataFetcher(env -> {
                            return counter.incrementAndGet();
                        }))
                .field(field -> field.type(GraphQLString).name("content")
                        .description("Greeting message")
                        .argument(a -> a.name("name")
                                .description("Name to greet")
                                .defaultValue(defaultName).type(GraphQLString))
                        .dataFetcher(env -> {
                            final Optional<String> name = Optional
                                    .ofNullable(env.getArgument("name"));
                            return String.format(template,
                                    name.orElse(defaultName));
                        }))
                .build();
    }

    @Override
    public Object context() {
        return new Saying(1, "Hello Stranger!");
    }
}
