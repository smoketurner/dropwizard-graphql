/**
 * Copyright 2018 Smoke Turner, LLC.
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
package com.smoketurner.dropwizard.graphql;

import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLServlet;
import graphql.servlet.SimpleGraphQLServlet;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class GraphQLBundle<C extends Configuration>
        implements ConfiguredBundle<C>, GraphQLConfiguration<C> {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/"));
    }

    @Override
    public void run(final C configuration, final Environment environment)
            throws Exception {
        final GraphQLFactory factory = getGraphQLFactory(configuration);

        final GraphQLSchema schema = factory.build();
        final GraphQLServlet servlet = SimpleGraphQLServlet.builder(schema)
                .withInstrumentation(factory.getInstrumentations()).build();

        environment.servlets().addServlet("graphql", servlet)
                .addMapping("/graphql", "/schema.json");
    }
}
