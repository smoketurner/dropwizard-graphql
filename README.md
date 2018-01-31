Dropwizard GraphQL Bundle
========================
[![Build Status](https://travis-ci.org/smoketurner/dropwizard-graphql.svg?branch=master)](https://travis-ci.org/smoketurner/dropwizard-graphql)
[![Coverage Status](https://coveralls.io/repos/smoketurner/dropwizard-graphql/badge.svg)](https://coveralls.io/r/smoketurner/dropwizard-graphql)
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
    <version>1.2.3-2</version>
</dependency>
```

Usage
-----
Add a `GraphQLBundle` to your [Application](http://www.dropwizard.io/1.2.2/dropwizard-core/apidocs/io/dropwizard/Application.html) class.

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

Example Application
-------------------
This bundle includes a modified version of the `HelloWorldApplication` from Dropwizard's [Getting Started](http://www.dropwizard.io/1.2.2/docs/getting-started.html) documentation.

You can execute this application on your local machine then running:

```
./mvnw clean package
java -jar graphql-example/target/graphql-example-1.2.3-3-SNAPSHOT.jar server graphql-example/hello-world.yml
```

This will start the application on port `8080` with a [GraphiQL](https://github.com/graphql/graphiql) interface for exploring the API.

Support
-------
Please file bug reports and feature requests in [GitHub issues](https://github.com/smoketurner/dropwizard-graphql/issues).

License
-------
Copyright (c) 2018 Smoke Turner, LLC

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the [LICENSE](LICENSE) file in this repository for the full license text.
