Dropwizard GraphQL Bundle
========================
[![Build Status](https://travis-ci.org/smoketurner/dropwizard-graphql.svg?branch=master)](https://travis-ci.org/smoketurner/dropwizard-graphql)
[![Maven Central](https://img.shields.io/maven-central/v/com.smoketurner.dropwizard/dropwizard-graphql.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.smoketurner.dropwizard/dropwizard-graphql/)
[![GitHub license](https://img.shields.io/github/license/smoketurner/dropwizard-graphql.svg?style=flat-square)](https://github.com/smoketurner/dropwizard-graphql/tree/master)
[![Become a Patron](https://img.shields.io/badge/Patron-Patreon-red.svg)](https://www.patreon.com/bePatron?u=9567343)

A bundle for providing [GraphQL](http://graphql.org) API endpoints in Dropwizard applications.

Dependency Info
---------------
```xml
<dependency>
    <groupId>com.smoketurner.dropwizard</groupId>
    <artifactId>graphql-core</artifactId>
    <version>2.0.7-1</version>
</dependency>
```

Usage
-----
Add a `GraphQLBundle` to your [Application](https://javadoc.io/doc/io.dropwizard/dropwizard-project/latest/io/dropwizard/Application.html) class.

```java
@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    // ...
    final GraphQLBundle<HelloWorldConfiguration> bundle = new GraphQLBundle<HelloWorldConfiguration>() {
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
```
## Adding GraphQL along with REST API Endpoints in Dropwizard
To use GraphQL along with REST APIs in dropwizard you need to change the root path in the bundle which we add
in the main class of dropwizard. Otherwise the bundle may conflict with root path of REST API's.

You need to add the root path by overiding the ```initialize``` method in GraphQL bundle.
```
@Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.htm", "graphql-playground"));
  }
``` 
This is the default ```initialize``` method in GraphQL bundle.
If you want to expose your GraphQL endpoint at ``localhost:8080/graphql`` then you have to change
the path in the ```AssetBundle``` constructor.

Now the overriden method which we add while adding bundle is 

```
@Override
  public void initialize(Bootstrap<?> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/graphql", "index.htm", "graphql-playground"));
    //graphql is the endpoint which is concerned with graphql
  }
```  
This avoids conflict between REST API and GraphQL endpoints.

When we start the dropwizard server the GraphQL playground looks for GraphQL schema.GraphQL dropwizard creates a 
schema.json file after processing our GraphQL schema. The GraphQL playground looks out for this schema. It looks out 
at ```/graphql``` from the GraphQL endpoint.If you wish to change where the GraphQL playground looks for this schema file
then you may override the ```run``` method in GraphQL bundle class.

If we want our schema.json to be available at ```localhost:8080/graphql/query``` then the overridden method should like this.
```
@Override
  public void run(final C configuration, final Environment environment) throws Exception {
    final GraphQLFactory factory = getGraphQLFactory(configuration);

    final PreparsedDocumentProvider provider =
        new CachingPreparsedDocumentProvider(factory.getQueryCache(), environment.metrics());

    final GraphQLSchema schema = factory.build();

    final GraphQLQueryInvoker queryInvoker =
        GraphQLQueryInvoker.newBuilder()
            .withPreparsedDocumentProvider(provider)
            .withInstrumentation(factory.getInstrumentations())
            .build();

    final graphql.kickstart.servlet.GraphQLConfiguration config =
        graphql.kickstart.servlet.GraphQLConfiguration.with(schema).with(queryInvoker).build();

    final GraphQLHttpServlet servlet = GraphQLHttpServlet.with(config);

    environment.servlets().addServlet("graphql", servlet).addMapping("/query", "/schema.json");
  }
```

Example Application
-------------------
This bundle includes a modified version of the `HelloWorldApplication` from Dropwizard's [Getting Started](https://www.dropwizard.io/en/latest/getting-started.html) documentation.

You can execute this application on your local machine then running:

```
./mvnw clean package
java -jar graphql-example/target/graphql-example-2.0.7-2-SNAPSHOT.jar server graphql-example/hello-world.yml
```

This will start the application on port `8080` with a [GraphQL Playground](https://github.com/prisma-labs/graphql-playground) interface for exploring the API.

Support
-------
Please file bug reports and feature requests in [GitHub issues](https://github.com/smoketurner/dropwizard-graphql/issues).

License
-------
Copyright (c) 2020 Smoke Turner, LLC

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the [LICENSE](LICENSE) file in this repository for the full license text.
