/*
 * Copyright © 2018 Smoke Turner, LLC (github@smoketurner.com)
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
package com.example.helloworld.api;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class SayingDataFetcher implements DataFetcher<Saying> {

  private final String template;
  private final String defaultName;
  private final AtomicLong counter = new AtomicLong();

  public SayingDataFetcher(String template, String defaultName) {
    this.template = template;
    this.defaultName = defaultName;
  }

  @Override
  public Saying get(DataFetchingEnvironment environment) {
    final Optional<String> name = Optional.ofNullable(environment.getArgument("name"));
    final String value = String.format(template, name.orElse(defaultName));
    return new Saying(counter.incrementAndGet(), value);
  }
}
