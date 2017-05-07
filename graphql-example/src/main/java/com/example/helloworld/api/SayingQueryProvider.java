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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLTypesProvider;

public class SayingQueryProvider implements GraphQLQueryProvider,
        GraphQLMutationProvider, GraphQLTypesProvider {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter = new AtomicLong();

    /**
     * Constructor
     *
     * @param template
     * @param defaultName
     */
    public SayingQueryProvider(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
    }

    @Override
    public Collection<GraphQLFieldDefinition> getQueries() {
        final List<GraphQLFieldDefinition> definitions = new ArrayList<>();
        definitions.add(GraphQLFieldDefinition.newFieldDefinition()
                .name("saying").type(getSayingType()).build());
        return definitions;
    }

    private GraphQLObjectType getSayingType() {
        return GraphQLObjectType.newObject().name("Saying")
                .description("A friendly greeting")
                .field(f -> f.type(GraphQLLong).name("id")
                        .description("Unique ID")
                        .dataFetcher(env -> counter.incrementAndGet()))
                .field(f -> f.type(GraphQLString).name("content")
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
    public Collection<GraphQLType> getTypes() {
        return Collections.emptyList();
    }

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return null;
    }
}
