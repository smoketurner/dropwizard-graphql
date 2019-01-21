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
package com.smoketurner.dropwizard.graphql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilderSpec;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaProblem;
import io.dropwizard.validation.OneOf;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQLFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLFactory.class);

  /** @deprecated Please use {@link schemaFiles} instead */
  @Deprecated() private String schemaFile = "";

  @NotNull private List<String> schemaFiles = new ArrayList<>();

  @NotEmpty
  @OneOf({"async", "async_serial", "subscription"})
  private String executionStrategy = "async";

  private boolean enableTracing = true;

  @NotNull private CacheBuilderSpec queryCache = CacheBuilderSpec.disableCaching();

  @NotNull private List<Instrumentation> instrumentations = new ArrayList<>();

  @NotNull private RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().build();

  @Deprecated
  @JsonProperty
  public String getSchemaFile() {
    return schemaFile;
  }

  @Deprecated
  @JsonProperty
  public void setSchemaFile(final String file) {
    schemaFile = file;
  }

  @JsonProperty
  public List<String> getSchemaFiles() {
    return schemaFiles;
  }

  @JsonProperty
  public void setSchemaFiles(List<String> files) {
    schemaFiles = Optional.ofNullable(files).orElseGet(ArrayList::new);
  }

  @JsonProperty
  public ExecutionStrategy getExecutionStrategy() {
    switch (executionStrategy) {
      case "async_serial":
        return new AsyncSerialExecutionStrategy();
      case "subscription":
        return new SubscriptionExecutionStrategy();
      case "async":
      default:
        return new AsyncExecutionStrategy();
    }
  }

  @JsonProperty
  public void setExecutionStrategy(final String strategy) {
    executionStrategy = strategy;
  }

  @JsonIgnore
  public RuntimeWiring getRuntimeWiring() {
    return runtimeWiring;
  }

  @JsonIgnore
  public void setRuntimeWiring(final RuntimeWiring wiring) {
    runtimeWiring = wiring;
  }

  @JsonProperty
  public boolean isEnableTracing() {
    return enableTracing;
  }

  @JsonProperty
  public void setEnableTracing(boolean enabled) {
    enableTracing = enabled;
    if (enabled) {
      instrumentations.add(new TracingInstrumentation());
    }
  }

  @JsonProperty
  public CacheBuilderSpec getQueryCache() {
    return queryCache;
  }

  @JsonProperty
  public void setQueryCache(String queryCache) {
    this.queryCache = CacheBuilderSpec.parse(queryCache);
  }

  @JsonIgnore
  public ChainedInstrumentation getInstrumentations() {
    return new ChainedInstrumentation(instrumentations);
  }

  @JsonIgnore
  public void setInstrumentations(List<Instrumentation> instrumentations) {
    this.instrumentations = Optional.ofNullable(instrumentations).orElseGet(ArrayList::new);
  }

  public GraphQLSchema build() throws SchemaProblem {
    final SchemaParser parser = new SchemaParser();
    final TypeDefinitionRegistry registry = new TypeDefinitionRegistry();

    if (!Strings.isNullOrEmpty(schemaFile)) {
      schemaFiles.add(schemaFile);
    }

    if (!schemaFiles.isEmpty()) {
      schemaFiles.stream()
          .filter(f -> !Strings.isNullOrEmpty(f))
          .map(f -> getResourceAsReader(f))
          .map(r -> parser.parse(r))
          .forEach(p -> registry.merge(p));
    }

    final SchemaGenerator generator = new SchemaGenerator();
    final GraphQLSchema schema = generator.makeExecutableSchema(registry, runtimeWiring);
    return schema;
  }

  /**
   * Return a resource file name as a BufferedReader
   *
   * @param name Resource file name
   * @return BufferedReader for the file
   */
  private static BufferedReader getResourceAsReader(final String name) {
    LOGGER.info("Loading GraphQL schema file: {}", name);

    final ClassLoader loader =
        MoreObjects.firstNonNull(
            Thread.currentThread().getContextClassLoader(), GraphQLFactory.class.getClassLoader());

    final InputStream in = loader.getResourceAsStream(name);

    Objects.requireNonNull(in, String.format("resource not found: %s", name));

    return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }
}
