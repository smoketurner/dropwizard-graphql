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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.execution.batched.BatchedExecutionStrategy;
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

public class GraphQLFactory {

    @NotEmpty
    private String schemaFile = "";

    @NotEmpty
    @OneOf({ "async", "async_serial", "subscription", "batched" })
    private String executionStrategy = "async";

    @NotNull
    private List<String> blockedFields = Collections.emptyList();

    @NotNull
    private List<Instrumentation> instrumentations = Collections
            .singletonList(new TracingInstrumentation());

    @NotNull
    private RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
            .build();

    @JsonProperty
    public BufferedReader getSchemaFile() {
        return getResourceAsBufferedReader(schemaFile);
    }

    @JsonProperty
    public void setSchemaFile(final String file) {
        this.schemaFile = file;
    }

    @JsonProperty
    public ExecutionStrategy getExecutionStrategy() {
        switch (executionStrategy) {
        case "batched":
            return new BatchedExecutionStrategy();
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
        this.executionStrategy = strategy;
    }

    @JsonIgnore
    public RuntimeWiring getRuntimeWiring() {
        return runtimeWiring;
    }

    @JsonIgnore
    public void setRuntimeWiring(final RuntimeWiring wiring) {
        this.runtimeWiring = wiring;
    }

    @JsonProperty
    public List<String> getBlockedFields() {
        return blockedFields;
    }

    @JsonProperty
    public void setBlockedFields(final List<String> fields) {
        this.blockedFields = fields;
    }

    @JsonIgnore
    public ChainedInstrumentation getInstrumentations() {
        return new ChainedInstrumentation(instrumentations);
    }

    @JsonIgnore
    public void setInstrumentations(List<Instrumentation> instrumentations) {
        this.instrumentations = instrumentations;
    }

    public GraphQLSchema build() throws SchemaProblem {
        final SchemaParser parser = new SchemaParser();
        final TypeDefinitionRegistry registry = parser.parse(getSchemaFile());

        final SchemaGenerator generator = new SchemaGenerator();
        final GraphQLSchema schema = generator.makeExecutableSchema(registry,
                runtimeWiring);
        return schema;
    }

    private static BufferedReader getResourceAsBufferedReader(final String resourceName) {
        final ClassLoader loader = MoreObjects.firstNonNull(Thread.currentThread().getContextClassLoader(), GraphQLFactory.class.getClassLoader());
        final InputStream resourceAsStream = loader.getResourceAsStream(resourceName);
        Preconditions.checkArgument(resourceAsStream != null, "resource %s not found.", resourceName);
        return new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
    }
}
