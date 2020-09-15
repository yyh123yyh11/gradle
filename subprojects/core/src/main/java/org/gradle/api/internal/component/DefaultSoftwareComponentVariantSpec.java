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
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.DocsType;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.SoftwareComponentVariantSpec;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.internal.Cast;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DefaultSoftwareComponentVariantSpec implements SoftwareComponentVariantSpec {

    private final String name;
    private final Map<Attribute<?>, Property<String>> attributes = new HashMap<>();
    private final Set<Object> artifacts = new HashSet<>();
    private final Set<String> dependencyBuckets = new TreeSet<>();

    private final ObjectFactory objects;

    @Inject
    public DefaultSoftwareComponentVariantSpec(String name, ObjectFactory objects) {
        this.name = name;
        this.objects = objects;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Property<String> providesUsage() {
        return provides(Usage.USAGE_ATTRIBUTE);
    }

    @Override
    public Property<String> providesLibrary() {
        provides(Category.CATEGORY_ATTRIBUTE).convention(Category.LIBRARY);
        return provides(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE);
    }

    @Override
    public Property<String> providesDocumentation() {
        provides(Category.CATEGORY_ATTRIBUTE).convention(Category.DOCUMENTATION);
        return provides(DocsType.DOCS_TYPE_ATTRIBUTE);
    }

    @Override
    public Property<String> provides(Attribute<?> attributeType) {
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
    public void artifactFrom(Object artifactProvider) {
        artifacts.add(artifactProvider);
    }

    @Override
    public void artifactsFrom(Iterable<Object> artifactProviders) {
        for (Object artifact: artifactProviders) {
            artifactFrom(artifact);
        }
    }

    @Override
    public void removeArtifact(Object artifactProvider) {
        artifacts.add(artifactProvider);
    }

    @Override
    public List<Object> getArtifacts() {
        return ImmutableList.copyOf(artifacts);
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
}
