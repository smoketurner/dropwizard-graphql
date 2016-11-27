Dropwizard GraphQL Bundle
========================
[![Build Status](https://travis-ci.org/smoketurner/dropwizard-graphql.svg?branch=master)](https://travis-ci.org/smoketurner/dropwizard-graphql)
[![Coverage Status](https://coveralls.io/repos/smoketurner/dropwizard-graphql/badge.svg)](https://coveralls.io/r/smoketurner/dropwizard-graphql)
[![Maven Central](https://img.shields.io/maven-central/v/com.smoketurner.dropwizard/dropwizard-graphql.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.smoketurner.dropwizard/dropwizard-graphql/)
[![GitHub license](https://img.shields.io/github/license/smoketurner/dropwizard-graphql.svg?style=flat-square)](https://github.com/smoketurner/dropwizard-graphql/tree/master)

A bundle for providing [GraphQL](http://graphql.org) API endpoints in Dropwizard applications.

Dependency Info
---------------
```xml
<dependency>
    <groupId>com.smoketurner.dropwizard</groupId>
    <artifactId>graphql-core</artifactId>
    <version>1.0.5-1</version>
</dependency>
```

Usage
-----
Add a `GraphQLBundle` to your [Application](http://www.dropwizard.io/1.0.5/dropwizard-core/apidocs/io/dropwizard/Application.html) class.

```java
@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    // ...
    bootstrap.addBundle(new GraphQLBundle<MyConfiguration>() {
        @Override
        public GraphQLFactory getGraphQLFactory(MyConfiguration configuration) {
            return configuration.getGraphQLFactory();
        }
    });
}

@Override
public void run(MyConfiguration configuration, Environment environment) throws Exception {
    final GraphQLServlet servlet = configuration.getGraphQLFactory().build();
    servlet.bindQueryProvider(provider);
}
```

Example Application
-------------------
This bundle includes a modified version of the `HelloWorldApplication` from Dropwizard's [Getting Started](http://www.dropwizard.io/1.0.5/docs/getting-started.html) documentation.

```xml
<dependency>
    <groupId>com.smoketurner.dropwizard</groupId>
    <artifactId>graphql-example</artifactId>
    <version>1.0.5-1</version>
</dependency>
```

You can execute this application on your local machine then running:

```
mvn clean package
java -jar graphql-example/target/graphql-example-1.0.5-1.jar server graphql-example/hello-world.yml
```

This will start the application on port `8888`. You can then install [GraphiQL](https://github.com/graphql/graphiql/tree/master/example) and modify [line 113](https://github.com/graphql/graphiql/blob/master/example/index.html#L113) to point to `http://127.0.0.1:8888/graphql` to be able to test out your API.

Support
-------
Please file bug reports and feature requests in [GitHub issues](https://github.com/smoketurner/dropwizard-graphql/issues).

License
-------
Copyright (c) 2016 Justin Plock

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the [LICENSE](LICENSE) file in this repository for the full license text.
