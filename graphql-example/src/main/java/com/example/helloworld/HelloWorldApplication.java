/*
 * Copyright © 2019 Smoke Turner, LLC (github@smoketurner.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.helloworld;

import com.example.helloworld.api.SayingDataFetcher;
import com.example.helloworld.resources.HelloWorldResource;
import com.smoketurner.dropwizard.graphql.GraphQLBundle;
import com.smoketurner.dropwizard.graphql.GraphQLFactory;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import java.util.EnumSet;
import org.eclipse.jetty.servlets.CrossOriginFilter;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

  public static void main(String[] args) throws Exception {
    new HelloWorldApplication().run(args);
  }

  @Override
  public String getName() {
    return "hello-world";
  }

  @Override
  public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    final GraphQLBundle<HelloWorldConfiguration> bundle =
        new GraphQLBundle<HelloWorldConfiguration>() {
          @Override
          public GraphQLFactory getGraphQLFactory(HelloWorldConfiguration configuration) {

            final GraphQLFactory factory = configuration.getGraphQLFactory();
            // the RuntimeWiring must be configured prior to the run()
            // methods being called so the schema is connected properly.
            factory.setRuntimeWiring(buildWiring(configuration));
            return factory;
          }
        };
    bootstrap.addBundle(bundle);
  }

  @Override
  public void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {

    // Enable CORS to allow GraphiQL on a separate port to reach the API
    final FilterRegistration.Dynamic cors =
        environment.servlets().addFilter("cors", CrossOriginFilter.class);
    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    final HelloWorldResource resource =
        new HelloWorldResource(configuration.getTemplate(), configuration.getDefaultName());
    environment.jersey().register(resource);
  }

  private static RuntimeWiring buildWiring(HelloWorldConfiguration configuration) {

    final SayingDataFetcher fetcher =
        new SayingDataFetcher(configuration.getTemplate(), configuration.getDefaultName());

    final RuntimeWiring wiring =
        RuntimeWiring.newRuntimeWiring()
            .type("Query", typeWiring -> typeWiring.dataFetcher("saying", fetcher))
            .scalar(ExtendedScalars.GraphQLLong)
            .build();

    return wiring;
  }
}
