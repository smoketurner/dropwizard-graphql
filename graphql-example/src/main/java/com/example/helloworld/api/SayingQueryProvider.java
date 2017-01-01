/**
 * Copyright 2017 Smoke Turner, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
