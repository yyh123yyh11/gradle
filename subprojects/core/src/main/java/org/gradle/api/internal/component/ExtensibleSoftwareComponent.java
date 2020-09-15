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

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.component.SoftwareComponentVariantSpec;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.internal.Cast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract public class ExtensibleSoftwareComponent implements SoftwareComponentInternal {

    private final Map<String, SoftwareComponentVariantSpec> variants = new HashMap<>();

    private final ConfigurationContainer configurations;
    private final ObjectFactory objects;

    public ExtensibleSoftwareComponent(ConfigurationContainer configurations, ObjectFactory objects) {
        this.configurations = configurations;
        this.objects = objects;
    }

    @Override
    public void variant(String name, Action<? super SoftwareComponentVariantSpec> action) {
        if (!variants.containsKey(name)) {
            SoftwareComponentVariantSpec variant = objects.newInstance(DefaultSoftwareComponentVariantSpec.class, name);
            variants.put(name, variant);
        }
        action.execute(variants.get(name));
    }

    @Override
    public Set<? extends UsageContext> getUsages() {
        return realizeVariants();
    }

    private Set<? extends UsageContext> realizeVariants() {
        Set<UsageContext> realizedVariants = new HashSet<>();
        for (SoftwareComponentVariantSpec variant : variants.values()) {
            realizedVariants.add(new DefaultSoftwareComponentVariant(realizeVariant(variant)));
        }
        return realizedVariants;
    }

    private Configuration realizeVariant(SoftwareComponentVariantSpec variant) {
        Configuration configuration = configurations.findByName(variant.getName());
        if (configuration != null) {
            return configuration;
        }
        configuration = configurations.create(variant.getName());
        configuration.setCanBeResolved(false);
        configuration.setCanBeConsumed(true);
        configuration.setVisible(false);
        for (Object artifact : variant.getArtifacts()) {
            configuration.getOutgoing().artifact(artifact);
        }
        for (String dependencyBucket : variant.getDependencyBuckets()) {
            configuration.extendsFrom(bucket(dependencyBucket));
        }
        for (Map.Entry<Attribute<?>, Property<String>> entry : variant.getAttributes().entrySet()) {
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
