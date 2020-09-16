/*
 * Copyright 2020 the original author or authors.
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

import com.google.common.collect.ImmutableList;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.DocsType;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.DependencyResolverSpec;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.internal.Cast;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DefaultDependencyResolverSpec implements DependencyResolverSpec, Iterable<File> {

    private final String name;
    private final Map<Attribute<?>, Property<String>> attributes = new HashMap<>();
    private final Set<String> dependencyBuckets = new TreeSet<>();

    private final ConfigurationContainer configurations;
    private final ObjectFactory objects;
    private final Property<Boolean> lenient;

    @Inject
    public DefaultDependencyResolverSpec(String name, ConfigurationContainer configurations, ObjectFactory objects) {
        this.name = name;
        this.configurations = configurations;
        this.objects = objects;
        this.lenient = objects.property(Boolean.class).convention(false);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Property<String> requiresUsage() {
        return requires(Usage.USAGE_ATTRIBUTE);
    }

    @Override
    public Property<String> requiresLibrary() {
        requires(Category.CATEGORY_ATTRIBUTE).convention(Category.LIBRARY);
        return requires(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE);
    }

    @Override
    public Property<String> requiresDocumentation() {
        requires(Category.CATEGORY_ATTRIBUTE).convention(Category.DOCUMENTATION);
        return requires(DocsType.DOCS_TYPE_ATTRIBUTE);
    }

    @Override
    public Property<String> requires(Attribute<?> attributeType) {
        if (attributes.containsKey(attributeType)) {
            return Cast.uncheckedNonnullCast(attributes.get(attributeType));
        }
        Property<String> property = objects.property(String.class);
        attributes.put(attributeType, property);
        return property;
    }

    @Override
    public Map<Attribute<?>, Property<String>> getAttributes() {
        return attributes;
    }

    @Override
    public void dependenciesFrom(String bucketName) {
        dependencyBuckets.add(bucketName);
    }

    @Override
    public void removedDependencyBucket(String bucketName) {
        dependencyBuckets.remove(bucketName);
    }

    @Override
    public List<String> getDependencyBuckets() {
        return ImmutableList.copyOf(dependencyBuckets);
    }

    @Override
    public Property<Boolean> allowEmptyMatches() {
        return lenient;
    }

    @Override
    public Iterator<File> iterator() {
        Configuration resolvableConfiguration = configurations.findByName(name);
        if (resolvableConfiguration == null) {
            resolvableConfiguration = realize();
        }
        if (lenient.get()) {
            return resolvableConfiguration.getIncoming().artifactView(v -> v.lenient(true)).getFiles().iterator();
        } else {
            return resolvableConfiguration.iterator();
        }
    }

    private Configuration realize() {
        Configuration configuration = configurations.create(name);
        configuration.setCanBeResolved(true);
        configuration.setCanBeConsumed(false);
        configuration.setVisible(false);
        for (String dependencyBucket : getDependencyBuckets()) {
            configuration.extendsFrom(bucket(dependencyBucket));
        }
        for (Map.Entry<Attribute<?>, Property<String>> entry : getAttributes().entrySet()) {
            Attribute<?> attribute = entry.getKey();
            Property<String> value = entry.getValue();
            if (value.isPresent()) {
                configuration.getAttributes().attribute(attribute, objects.named(Cast.uncheckedNonnullCast(attribute.getType()), value.get()));
            }
        }
        return configuration;
    }

    private Configuration bucket(String dependencyBucket) {
        Configuration configuration = configurations.findByName(dependencyBucket);
        if (configuration != null) {
            return configuration;
        }
        configuration = configurations.create(dependencyBucket);
        configuration.setCanBeResolved(false);
        configuration.setCanBeConsumed(false);
        configuration.setVisible(false);
        return configuration;
    }

}
