/*
 * Copyright © 2018 Smoke Turner, LLC (contact@smoketurner.com)
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

import edu.umd.cs.findbugs.annotations.Nullable;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetcher;
import java.util.List;

/**
 * Generic class to return a validation error message to the user from a {@link DataFetcher} without
 * a stack trace.
 */
public class GraphQLValidationError extends RuntimeException implements GraphQLError {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   *
   * @param message Error message to return to the user
   */
  public GraphQLValidationError(String message) {
    super(message);
  }

  @Nullable
  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.ValidationError;
  }

  // We don't want stacktraces being returned in the GraphQL response, so always return null.
  @Nullable
  @Override
  public StackTraceElement[] getStackTrace() {
    return null;
  }
}
