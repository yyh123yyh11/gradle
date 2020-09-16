/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.component;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.component.DependencyResolverContainer;
import org.gradle.api.component.DependencyResolverSpec;
import org.gradle.api.model.ObjectFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultDependencyResolverContainer implements DependencyResolverContainer {

    private final Map<String, DependencyResolverSpec> resolvers = new HashMap<>();

    private final ConfigurationContainer configurations;
    private final ObjectFactory objects;

    public DefaultDependencyResolverContainer(ConfigurationContainer configurations, ObjectFactory objects) {
        this.configurations = configurations;
        this.objects = objects;
    }

    @Override
    public DependencyResolverSpec configure(String name, Action<? super DependencyResolverSpec> action) {
        DependencyResolverSpec resolver = resolvers.get(name);
        if (resolver == null) {
            resolver = new DefaultDependencyResolverSpec(name, configurations, objects);
            resolvers.put(name, resolver);
        }
        action.execute(resolver);
        return resolver;
    }

    @Override
    public DependencyResolverSpec get(String name) {
        if (!resolvers.containsKey(name)) {
            throw new GradleException("No resolver named " + name);
        }
        return resolvers.get(name);
    }
}
